package com.sj.custom_view.practice.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.OverScroller;

/**
 * 自定义ViewPager
 *
 * 知识点汇总:
 * 1:自定义Layout
 * 1.1:理解自定义layout的流程(先测量子View, 保存所有子View 在布局阶段的位置信息，然后再根据要实现layout的特性与所有子View的宽高信息设定自己的宽高信息)
 * 1.2:meausre()与onMeasure()的区别
 * 1.3:measureChild()的工作原理
 * 1.4:理解getMeasureChild()的原理
 * 1.5:理解resolveize()的作用
 * 1.4:setMeasuredDimension()作用
 * 1.5:理解layout()与onLayout()的区别
 *
 * 2:需要理解什么是"弹性滑动"
 * 2.1:理解Scroller的工作原理
 * 2.2:为什么结合Scroller可以滑动View,Scroller滑动的View本身还是它包含的内容
 *
 * 3:需要理解Touch事件
 * 3.1:Touch事件分发流程(Activity->DecordView->RootView->...->View)
 * 3.2:理解dispatchTouchEvnet()方法内部实现细节
 * 3.3:onInterceptTouchEvnet()方法在何时调用，作用
 * 3.3:理解onTouchEvent()作用，返回值意义
 * 3.4:理解什么是 事件序列
 * 3.5:理解requestDisallowIntercepte()方法作用
 * 3.6:理解什么是TouchTarget,以及它的作用
 *
 * Created by eunice on 2019/1/12.
 */

public class MyViewPager extends ViewGroup {

    //速度检测器
    private VelocityTracker mVelocityTracker;
    //
    private OverScroller mScroller;
    //
    private ViewConfiguration mViewConfiguration;
    //
    private int mTouchSloup = 0;
    //获取设备先关最小的滑动速度，用判断是否是快滑动作
    private int mMinVelocity;
    private int mUpScrollX;

    private long mFistMoveTime;


    private float mLastX;

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.init();
    }

    //--------------------------布局相关----------------------//
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int myWidth = MeasureSpec.getSize(widthMeasureSpec);
        int myHeight = MeasureSpec.getSize(heightMeasureSpec);

        //子View的宽高不能超过父控件的宽高
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(myWidth, MeasureSpec.EXACTLY);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(myHeight, MeasureSpec.EXACTLY);

        int childCount = this.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = this.getChildAt(i);
            this.measureChild(child, childWidthMeasureSpec, childHeightMeasureSpec);
        }

        this.setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = this.getChildCount();
        int width = r - l;
        int height = b - t;

        int childLeft = l;
        int childTop = t;

        for (int i = 0; i < childCount; i++) {
            View child = this.getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            child.layout(childLeft + lp.leftMargin,
                    childTop + lp.topMargin,
                    childLeft + width - lp.rightMargin,
                    childTop + height - lp.bottomMargin);

            childLeft += width;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(this.getContext(), attrs);
    }
    //--------------------------布局相关----------------------//

    //--------------------------触摸反馈相关----------------------//
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = false;

        switch (ev.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                this.mLastX = ev.getX();
                break;

            case MotionEvent.ACTION_MOVE:
                if(Math.abs(ev.getX() - this.mLastX) > this.mTouchSloup){ //拦截
                    this.mLastX = ev.getX();
                    this.mVelocityTracker.clear();

                    result = true;
                }
                break;
        }

        return result;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointX = (int) event.getX();
        int scrollX = this.getScrollX();
        int maxScrollX = (this.getChildCount() - 1) * this.getWidth();

        int offsetX = (int) (this.mLastX - pointX);
        int temp = scrollX + offsetX;
        boolean moveFlag = temp >= 0 && temp <= maxScrollX;

        boolean touchFlag = false;

        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                this.mVelocityTracker.addMovement(event);
                this.mFistMoveTime = event.getEventTime();
                touchFlag = true;
                break;

            case MotionEvent.ACTION_MOVE:
                if(moveFlag){
                    this.scrollBy(offsetX, 0);

                    this.mLastX = pointX;
                    this.mVelocityTracker.addMovement(event);
                }
                break;

            case MotionEvent.ACTION_UP:
                if(moveFlag){
                    //花费的时间应该这样计算，不能随便指定一个值
                    int time = (int) (event.getEventTime() - this.mFistMoveTime);
                    this.mVelocityTracker.addMovement(event);
                    this.mVelocityTracker.computeCurrentVelocity(time);
                    float xVelocity = this.mVelocityTracker.getXVelocity();
                    boolean isFlip = Math.abs(xVelocity) > this.mMinVelocity;

                    int sume = scrollX % this.getWidth();

                    if(isFlip){
                        sume = xVelocity < 0 ? this.getWidth() - sume : -sume;
                    } else {
                        sume = sume <= this.getWidth() / 2 ? -sume : this.getWidth() - sume;
                    }

                    //this.scrollBy(sume, 0);
                    this.mUpScrollX = scrollX;
                    this.smoothScroll(sume);
                }
                break;
        }

        //判断MyViewPager是否设置了以下事件，如果没设置则不需要执行super.onTouch()。
        //1:如果没设置还要执行super.onTouch()则会返回false,那么此次Touch事件序列是不会交给MyViewPager去处理的。
        //2:如果MyViewPager设置了以下事件的话，则交给super.onTouch()处理，此时系统返回true。
        //3:之所以没有直接写"retturn true"是因为，如果我在MyViewPager外部设置了以下事件，虽然MyViewPager能滑动，但是是无法响应设置的事件。
        final boolean clickable = this.isClickable() || this.isLongClickable() || this.isContextClickable();
        return clickable ? super.onTouchEvent(event) : touchFlag;
    }
    //--------------------------触摸反馈相关----------------------//

    @Override
    public void computeScroll() {
        if(this.mScroller.computeScrollOffset()){
            this.scrollTo(this.mUpScrollX + this.mScroller.getCurrX(), this.mScroller.getCurrY());
            this.postInvalidateOnAnimation();
        }
    }

    private void init(){
        this.mVelocityTracker = VelocityTracker.obtain();

        this.mViewConfiguration = ViewConfiguration.get(this.getContext());
        this.mTouchSloup = this.mViewConfiguration.getScaledTouchSlop();
        this.mMinVelocity = this.mViewConfiguration.getScaledMinimumFlingVelocity();

        this.mScroller = new OverScroller(this.getContext(), new DecelerateInterpolator());
    }

    private void smoothScroll(int distanceX){
        this.mScroller.startScroll(0, 0, distanceX, 0, 300);
        this.postInvalidateOnAnimation();
    }
}
