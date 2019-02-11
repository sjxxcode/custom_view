package com.sj.custom_view.simulation.jk.refresh.obsolete_code;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.sj.custom.R;
import com.sj.custom_view.simulation.jk.refresh.HeaderRefreshState;
import com.sj.custom_view.simulation.jk.refresh.IHeaderView;

import java.lang.ref.WeakReference;

/**
 * 滑动:
 * 1:需要研究下scrollBy()与scrollTo()
 *
 * 布局
 * 1:因为整体采用scrollBy()移动，所以为了使Header能够隐藏的话，是在初始化时候设置Header的绘制位置，使top变小。
 *   但是这种方式会引起RefreshLayout下部会多出一块区域，为了解决这个问题，需要在onMeasure()等测量完毕后，手动更改RefreshLayout的宽高
 *
 * Created by SJ on 2019/1/18.
 */
public class RefreshLayout extends LinearLayout {

    private static final String TAG = "===RefreshLayout";

    //滑动处理
    private Scroller mScroller;
    //手势处理器
    private TouchHelper mTouchHelper;

    //用于通知Header刷新完毕了
    private IComplate mComplate;
    //HeaderView
    private IHeaderView mHeader;

    //滑动阻尼系数
    private float mDampingValue = 0.5f;

    private int mHeaderHeight;

    public RefreshLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.preInit();
    }

    //<editor-fold desc="系统回调">
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        this.setMeasuredDimension(this.getMeasuredWidth(),
                this.getMeasuredHeight() - this.mHeaderHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        this.setViewDisplayOrder();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        this.initView();
    }

    @Override
    public void computeScroll() {
        if (this.mScroller.computeScrollOffset()) {
            this.scrollTo(this.mScroller.getCurrX(), this.mScroller.getCurrY());
            this.postInvalidateOnAnimation();

        } else if(this.mComplate != null){ //如果“执行了刷新完毕操作”需要在RefreshLayout复位之后通知Header
            this.mComplate.complate();
            this.mComplate = null;
        }
    }
    //</editor-fold>

    private void preInit() {
        this.setOrientation(LinearLayout.VERTICAL);

        this.mScroller = new Scroller(this.getContext(), new DecelerateInterpolator());
        this.mTouchHelper = new TouchHelper(this, new MoveListener(this), new TouchHelper.IProxyView() {
            @Override
            public boolean dispatchTouchEvent(MotionEvent event) {
                return false;
            }

            @Override
            public boolean isTop() {
                return false;
            }
        });
    }

    private void initView() {
        this.mHeader = this.findViewById(R.id.header);
    }

    private void setViewDisplayOrder() {
        //child.bringToFront();
        //this.bringChildToFront(child);
        //这两个方法是等价的.第一种是在其方法内部会找到Parent，然后再调用Parent的bringChildToFront()

        //在布局子view之后,更改所有子View的绘制位置
        this.mHeaderHeight = 0;
        //标志HeaderView在子View序列中的位置是否为第一个子View
        boolean isFirst = false;

        //找到HeaderView并记录下HeaderView的高度并把HeaderView添加到子View序列的尾部
        for(int i = 0; i < this.getChildCount(); i++){
            View child = this.getChildAt(i);
            if(child instanceof IHeaderView){
                this.mHeaderHeight = child.getHeight();
                isFirst = i == 0;

                if(!isFirst){
                    this.bringChildToFront(child);
                }

                break;
            }
        }

        //更改子View的绘制位置
        //更改子View绘制的top值,手动把每个子View的绘制位置向上移动HeaderView的高度,这样就会把HeaderView隐藏到屏幕外部,并且其他View也会跟着移动相同距离
        for(int i = 0; i < this.getChildCount(); i++){
            View child = this.getChildAt(i);

            if(isFirst){
                child.layout(child.getLeft(), child.getTop() - this.mHeaderHeight, child.getRight(), child.getBottom() - this.mHeaderHeight);
            } else {
                if(!(child instanceof IHeaderView)){
                    this.bringChildToFront(child);
                }
            }
        }
    }

    private void smoothMove(int fromY, int toY){
        this.mScroller.startScroll(this.getScrollX(), fromY,
                0, toY, 200);
        this.postInvalidateOnAnimation();
    }

    private void initIComplate(){
        this.mComplate = new IComplate() {
            @Override
            public void complate() {
                callBackHeaderViewComplate();
            }
        };
    }

    //<editor-fold desc="回调Header">
    private boolean hasCallBack() {
        return this.mHeader != null;
    }

    private int getHeaderViewMaxOffsetY() {
        return this.hasCallBack() ? this.mHeader.maxMoveOffsetY() : 0;
    }

    private int getHeaderViewRefreshOffsetY() {
        return this.hasCallBack() ? this.mHeader.refreshOffsetY() : 0;
    }

    private HeaderRefreshState getHeaderViewRefreshState(){
        return this.hasCallBack() ? this.mHeader.refreshState() : HeaderRefreshState.STATE_DONE;
    }

    private void callBackHeaderViewToMove(float offset) {
        if (this.hasCallBack() && this.getScrollY() <= 0) {
            this.mHeader.move(Math.abs(this.getScrollY()), offset);
        }
    }

    private void callBackHeaderViewToRefresh() {
        if (this.hasCallBack()) {
            this.mHeader.refresh();
        }
    }

    private void callBackHeaderViewComplate(){
        if (this.hasCallBack()) {
            this.mHeader.complate();
        }
    }
    //</editor-fold>

    //<editor-fold desc="对外提供的api">
    public void refreshComplate(){
        if (this.mScroller != null && this.hasCallBack()){
            if(!this.mScroller.isFinished()){
                this.mScroller.abortAnimation();
            }

            this.initIComplate();

            int currentScrollY = this.getScrollY();
            this.smoothMove(currentScrollY, -currentScrollY);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Touch事件处理">
    private static class MoveListener implements TouchHelper.IMove {

        private WeakReference<RefreshLayout> mTarget;

        public MoveListener(RefreshLayout refreshLayout) {
            this.mTarget = new WeakReference<>(refreshLayout);
        }

        private RefreshLayout getTargetContent() {
            if (this.mTarget == null) {
                return null;
            }

            return this.mTarget.get();
        }

        private void moveActual(RefreshLayout refreshLayout, int offsetY){
            refreshLayout.callBackHeaderViewToMove(offsetY);
            refreshLayout.scrollBy(0, offsetY);
        }

        private void move(RefreshLayout refreshLayout, int offsetY){
            if(refreshLayout != null && offsetY != 0){
                //当前HeaderView的state
                HeaderRefreshState state = refreshLayout.getHeaderViewRefreshState();

                //刷新中不允许移动
                if(state == HeaderRefreshState.STATE_REFREING){
                    return;
                }

                //向上滑动最大偏移量
                int upOffsetY = 0;
                //向下滑动最大偏移量
                int downOffsetY = -refreshLayout.getHeaderViewMaxOffsetY();
                //移动的偏移量
                int moveToOffsetY = refreshLayout.getScrollY() + offsetY;
                //移动方向
                boolean moveDirection = offsetY < 0 ? true : false;

                if(moveDirection && moveToOffsetY >= downOffsetY){ //下
                    this.moveActual(refreshLayout, offsetY);

                } else if(!moveDirection && moveToOffsetY <= upOffsetY){ //上
                    this.moveActual(refreshLayout, offsetY);
                }
            }
        }

        @Override
        public void move(float offset) {
            RefreshLayout refreshLayout = this.getTargetContent();

            if (refreshLayout != null){
                //计算后的偏移量
                int intValue = (int) -(offset * refreshLayout.mDampingValue);

                this.move(refreshLayout, intValue);
            }
        }

        @Override
        public void up() {
            RefreshLayout refreshLayout = this.getTargetContent();
            if (refreshLayout != null) {
                refreshLayout.callBackHeaderViewToRefresh();

                int currentScrollY = refreshLayout.getScrollY();
                int refreshOffsetY = refreshLayout.getHeaderViewRefreshOffsetY();

                int toScrollY = -currentScrollY;
                if (Math.abs(currentScrollY) >= refreshOffsetY) {
                    toScrollY = Math.abs(currentScrollY)- refreshOffsetY;
                }

                refreshLayout.smoothMove(currentScrollY, toScrollY);
            }
        }
    }
    //</editor-fold>

    /**
     * 当手动调用complate之后,用于等RefreshLayout复位之后通知Header刷新完毕了
     */
    private interface IComplate{
        void complate();
    }
}
