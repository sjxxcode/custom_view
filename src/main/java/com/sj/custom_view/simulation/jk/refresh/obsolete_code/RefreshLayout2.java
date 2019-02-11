package com.sj.custom_view.simulation.jk.refresh.obsolete_code;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.sj.custom.R;
import com.sj.custom_view.simulation.jk.refresh.HeaderRefreshState;
import com.sj.custom_view.simulation.jk.refresh.IHeaderView;

import java.lang.ref.WeakReference;


/**
 * 滑动:
 * 1:需要研究下scrollBy()与scrollTo()
 * <p>
 * 布局
 * 1:因为整体采用scrollBy()移动，所以为了使Header能够隐藏的话，是在初始化时候设置Header的绘制位置，使top变小。
 * 但是这种方式会引起RefreshLayout下部会多出一块区域，为了解决这个问题，采用自定义Layout来解决
 * <p>
 * Created by SJ on 2019/1/18.
 */
public class RefreshLayout2 extends ViewGroup {

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

    public RefreshLayout2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.preInit();
    }

    //<editor-fold desc="系统回调">
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        this.initView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //1:先measure每个子View
        //2:根据子View的LayoutParams信息调用measureChildWhithState()该方法会根据开发者的建议去meausre子View
        //3:根据自己的可以空间+此Layout的特性+子View已经使用完的空间，3者共同决定Layout的宽高信息
        this.judgeSubviewDisplayOrder();
        this.measureAndSetChildWH(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childTop = 0;

        if (this.getChildCount() > 0) {
            View child0 = this.getChildAt(0);
            MarginLayoutParams lp = (MarginLayoutParams) child0.getLayoutParams();
            if (child0 instanceof IHeaderView) {
                childTop = -(child0.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
            }
        }

        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childLeft = lp.leftMargin;
            int childBottom = childTop + child.getMeasuredHeight() + lp.bottomMargin;
            childTop += lp.topMargin;

            child.layout(childLeft,
                         childTop,
                      childLeft + child.getMeasuredWidth() + lp.rightMargin,
                         childBottom);

            childTop = childBottom;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(this.getContext(), attrs);
    }

    @Override
    public void computeScroll() {
        if (this.mScroller.computeScrollOffset()) {
            this.scrollTo(this.mScroller.getCurrX(), this.mScroller.getCurrY());
            this.postInvalidateOnAnimation();

        } else if (this.mComplate != null) { //如果“执行了刷新完毕操作”需要在RefreshLayout复位之后通知Header
            this.mComplate.complate();
            this.mComplate = null;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean flag = this.mTouchHelper.dispatchTouchEvent(ev);

        if(!flag){
            this.dispatchTouchEventActual(ev);
            flag = true;
        }

        Log.e(TAG, "dispatchTouchEvent()---" + flag);

        return flag;
    }

    private boolean dispatchTouchEventActual(MotionEvent ev){
        boolean flag = super.dispatchTouchEvent(ev);

        Log.e(TAG, "dispatchTouchEventActual()---" + flag);

        return flag;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.e(TAG, "onInterceptTouchEvent()---");

        return this.mTouchHelper.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG, "onTouchEvent()---");

        return this.mTouchHelper.onTouchEvent(event);
    }
    //</editor-fold>

    //<editor-fold desc="内部使用的方法">
    private void preInit() {
        this.mScroller = new Scroller(this.getContext(), new DecelerateInterpolator());
        this.mTouchHelper = new TouchHelper(this,
                new MoveListener(this),
                new ProxyViewListener(this));
    }

    private void initView() {
        this.mHeader = this.findViewById(R.id.header);
    }

    private void judgeSubviewDisplayOrder() {
        //child.bringToFront();
        //this.bringChildToFront(child);
        //这两个方法是等价的.第一种是在其方法内部会找到Parent，然后再调用Parent的bringChildToFront()

        //标志HeaderView在子View序列中的位置是否为第一个
        boolean isFirst = false;

        //找到HeaderView并记录下HeaderView的高度并把HeaderView添加到子View序列的尾部
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            if (child instanceof IHeaderView) {
                isFirst = i == 0;

                if (!isFirst) {
                    this.bringChildToFront(child);
                }

                break;
            }
        }

        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);

            if (!isFirst && !(child instanceof IHeaderView)) {
                this.bringChildToFront(child);
            }
        }
    }

    private void measureAndSetChildWH(int widthMeasureSpec, int heightMeasureSpec) {
        int heightUsed = 0;

        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            this.measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, heightUsed);
            //解决底部View实现不出来的问题,只有子View是HeaderView的情况下,才往heightUsed增加当前子View占用的高度
            //在该方法之前已经调用了"setViewDisplayOrder()"把HeaderView调整到了第一位,所以这里只需要判断index就可以了
            if(i > 0){
                heightUsed += child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            }
        }

        this.setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), resolveSize(heightUsed, heightMeasureSpec));
    }

    private void smoothMove(int fromY, int toY) {
        this.mScroller.startScroll(this.getScrollX(), fromY,
                0, toY, 200);
        this.postInvalidateOnAnimation();
    }

    private boolean hasTop(){
        return true;
    }

    private void initIComplate() {
        this.mComplate = new IComplate() {
            @Override
            public void complate() {
                callBackHeaderViewComplate();
            }
        };
    }
    //</editor-fold>

    //<editor-fold desc="IHeaderView回调方法">
    private boolean hasCallBack() {
        return this.mHeader != null;
    }

    private int getHeaderViewMaxOffsetY() {
        return this.hasCallBack() ? this.mHeader.maxMoveOffsetY() : 0;
    }

    private int getHeaderViewRefreshOffsetY() {
        return this.hasCallBack() ? this.mHeader.refreshOffsetY() : 0;
    }

    private HeaderRefreshState getHeaderViewRefreshState() {
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

    private void callBackHeaderViewComplate() {
        if (this.hasCallBack()) {
            this.mHeader.complate();
        }
    }
    //</editor-fold>

    //<editor-fold desc="对外提供的api">
    public void refreshComplate() {
        if (this.mScroller != null && this.hasCallBack()) {
            if (!this.mScroller.isFinished()) {
                this.mScroller.abortAnimation();
            }

            this.initIComplate();

            int currentScrollY = this.getScrollY();
            this.smoothMove(currentScrollY, -currentScrollY);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Touch事件处理">
    private static class ProxyViewListener implements TouchHelper.IProxyView{
        private WeakReference<RefreshLayout2> mTarget;

        public ProxyViewListener(RefreshLayout2 target) {
            this.mTarget = new WeakReference<>(target);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            RefreshLayout2 target = null;

            if(this.mTarget != null && (target = this.mTarget.get()) != null){
                return target.dispatchTouchEventActual(event);
            }

            return false;
        }

        @Override
        public boolean isTop() {
            RefreshLayout2 target = null;

            if(this.mTarget != null && (target = this.mTarget.get()) != null){
                return target.hasTop();
            }

            return false;
        }
    }

    private static class MoveListener implements TouchHelper.IMove {

        private WeakReference<RefreshLayout2> mTarget;

        public MoveListener(RefreshLayout2 refreshLayout) {
            this.mTarget = new WeakReference<>(refreshLayout);
        }

        private RefreshLayout2 getTargetContent() {
            if (this.mTarget == null) {
                return null;
            }

            return this.mTarget.get();
        }

        private void moveActual(RefreshLayout2 refreshLayout, int offsetY) {
            refreshLayout.callBackHeaderViewToMove(offsetY);
            refreshLayout.scrollBy(0, offsetY);
        }

        private void move(RefreshLayout2 refreshLayout, int offsetY) {
            if (refreshLayout != null && offsetY != 0) {
                //当前HeaderView的state
                HeaderRefreshState state = refreshLayout.getHeaderViewRefreshState();

                //刷新中不允许移动
                if (state == HeaderRefreshState.STATE_REFREING) {
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

                if (moveDirection && moveToOffsetY >= downOffsetY) { //下
                    this.moveActual(refreshLayout, offsetY);

                } else if (!moveDirection && moveToOffsetY <= upOffsetY) { //上
                    this.moveActual(refreshLayout, offsetY);
                }
            }
        }

        @Override
        public void move(float offset) {
            RefreshLayout2 refreshLayout = this.getTargetContent();

            Log.e(TAG, "move()...");

            if (refreshLayout != null) {
                //计算后的偏移量
                int intValue = (int) -(offset * refreshLayout.mDampingValue);

                this.move(refreshLayout, intValue);
            }
        }

        @Override
        public void up() {
            RefreshLayout2 refreshLayout = this.getTargetContent();
            if (refreshLayout != null) {
                refreshLayout.callBackHeaderViewToRefresh();

                int currentScrollY = refreshLayout.getScrollY();
                int refreshOffsetY = refreshLayout.getHeaderViewRefreshOffsetY();

                int toScrollY = -currentScrollY;
                if (Math.abs(currentScrollY) >= refreshOffsetY) {
                    toScrollY = Math.abs(currentScrollY) - refreshOffsetY;
                }

                refreshLayout.smoothMove(currentScrollY, toScrollY);
            }
        }
    }
    //</editor-fold>

    /**
     * 当手动调用complate之后,用于等RefreshLayout复位之后通知Header刷新完毕了
     */
    private interface IComplate {
        void complate();
    }
}
