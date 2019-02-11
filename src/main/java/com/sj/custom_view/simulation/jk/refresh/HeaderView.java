package com.sj.custom_view.simulation.jk.refresh;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;


/**
 * Created by eunice on 2019/1/17.
 */

public class HeaderView extends View implements IHeaderView{

    //---------------------画图相关-----------------//
    private Paint mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mArcPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mOtherPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mOtherPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);

    //上边矩形与底部圆环的高度差
    private final int DIFFLINE = 3;
    //上边矩形的高度
    private final int MAX_ABOVERECTH = 40;

    //上边那个矩形"尾巴"的高度
    private int mAboveRectH = 0;
    //底部圆弧的所在矩形的宽高
    private int mArcWH = 60;
    //底部与上部小圆的宽高差
    private int mArcDiff = 15;
    //角度
    private float mAngle = 90;

    private ValueAnimator mRefreshingAni;
    //---------------------画图相关-----------------//

    //---------------------移动相关-----------------//
    //可刷新临界值(不包含Header的Height)
    private final int REFRESH_OFFSET_Y = 300;

    //最大移动偏移量
    private int mMaxOffsetY = 0;
    //可刷新临界值(包含Header的Height)
    private int mRefreshOffsetY = 0;

    //状态
    private HeaderRefreshState mState = HeaderRefreshState.STATE_DONE;
    //---------------------移动相关-----------------//

    private float mRoteAngle;

    public HeaderView(Context context) {
        super(context);

        this.init();
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        this.mMaxOffsetY = REFRESH_OFFSET_Y + bottom- top;
        this.mRefreshOffsetY = bottom - top;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.drawOther(canvas);
        this.drawRing(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public int maxMoveOffsetY() {
        //int temp = Math.abs(this.getTop()) + this.getHeight() * 3;

        return this.mMaxOffsetY;
    }

    @Override
    public int refreshOffsetY() {
        return this.mRefreshOffsetY;
    }

    @Override
    public void move(float currentOffsetY, float offsetY) {
        if(currentOffsetY < this.mRefreshOffsetY){
            this.mState = HeaderRefreshState.STATE_REFRESH_BEFOR;

            //为了实现手指在把“四分之前圆环拉出来之后，再改变上部”效果
            float tempValue = this.getHeight() / 2 + DIFFLINE;

            int tempH = (int) (MAX_ABOVERECTH * ((currentOffsetY - tempValue) / (this.mRefreshOffsetY - tempValue)));
            if(tempH <= MAX_ABOVERECTH){
                this.mAboveRectH = tempH;
                this.postInvalidateOnAnimation();
            }

            return;
        }

        if(currentOffsetY >= this.mRefreshOffsetY){
            this.mState = HeaderRefreshState.STATE_REFRESH_CAN;
        }
    }

    @Override
    public void refresh() {
        if(this.mState == HeaderRefreshState.STATE_REFRESH_CAN){
            this.aniToRefreshState();
        }
    }

    @Override
    public void complate() {
        this.mState = HeaderRefreshState.STATE_DONE;

        //
        if(this.mRefreshingAni != null
                && (this.mRefreshingAni.isRunning() || this.mRefreshingAni.isStarted())){
            this.mRefreshingAni.cancel();
        }

        this.reset();
    }

    @Override
    public HeaderRefreshState refreshState() {
        return this.mState;
    }

    private void init() {
        this.mArcPaint.setColor(Color.BLACK);
        this.mArcPaint.setStyle(Paint.Style.FILL);

        this.mArcPaint2.setColor(Color.parseColor("#ffffff"));
        this.mArcPaint2.setStyle(Paint.Style.FILL);

        this.mOtherPaint.setColor(Color.GRAY);
        this.mOtherPaint.setStyle(Paint.Style.FILL);

        this.mOtherPaint2.setColor(Color.BLACK);
        this.mOtherPaint2.setStyle(Paint.Style.FILL);
    }

    private void drawOther(Canvas canvas) {
        if(this.mState == HeaderRefreshState.STATE_REFRESH_BEFOR
                || this.mState == HeaderRefreshState.STATE_REFRESH_CAN){
            //draw上部
            int aboveRectR = (this.getWidth() + this.mArcWH) / 2;
            int aboveRectT = this.getHeight() / 2 - MAX_ABOVERECTH;
            int aboveRectB = aboveRectT + this.MAX_ABOVERECTH - DIFFLINE;

            if(this.mState == HeaderRefreshState.STATE_REFRESH_BEFOR){
                canvas.drawRect(aboveRectR - this.mArcDiff,
                        aboveRectT,
                        aboveRectR,
                        aboveRectB,
                        this.mOtherPaint);
            }

            canvas.drawRect(aboveRectR - this.mArcDiff,
                    aboveRectB - this.mAboveRectH,
                    aboveRectR,
                    aboveRectB,
                    this.mOtherPaint2);
        }
    }

    private void drawRing(Canvas canvas){
        if(this.mState == HeaderRefreshState.STATE_REFREING){
            canvas.rotate(this.mRoteAngle, this.getWidth() / 2, this.getHeight() / 2);
        }

        //draw圆环
        int clipLeft = (this.getWidth() - this.mArcWH) / 2;
        int clicpTop = (this.getHeight() - this.mArcWH) / 2;

        //大弧
        canvas.drawArc(clipLeft,
                clicpTop,
                clipLeft + this.mArcWH,
                clicpTop + this.mArcWH,
                0, this.mAngle, true, this.mArcPaint);

        //小圆
        canvas.drawCircle(this.getWidth() / 2,
                this.getHeight() / 2,
                (this.mArcWH - this.mArcDiff * 2) / 2,
                this.mArcPaint2);
    }

    @SuppressWarnings("all")
    private void aniToRefreshState(){
        this.mAboveRectH = MAX_ABOVERECTH;

        PropertyValuesHolder holder1 = PropertyValuesHolder.ofFloat("angle", this.mAngle, 355);
        PropertyValuesHolder holder2 = PropertyValuesHolder.ofInt("height", this.mAboveRectH, 0);

        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(holder1, holder2);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAngle = (float) animation.getAnimatedValue("angle");
                mAboveRectH = (int) animation.getAnimatedValue("height");

                postInvalidateOnAnimation();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //
                mState = HeaderRefreshState.STATE_REFREING;
                aniToRefreshing();
            }
        });
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(300);
        animator.start();
    }

    @SuppressWarnings("all")
    private void aniToRefreshing(){
        if(this.mRefreshingAni != null
                && (this.mRefreshingAni.isStarted()
                || this.mRefreshingAni.isRunning())){
            return;
        }

        this.mRefreshingAni = ValueAnimator.ofFloat(0, 360 * 10000);
        this.mRefreshingAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRoteAngle = (float) animation.getAnimatedValue();
                postInvalidateOnAnimation();
            }
        });
        this.mRefreshingAni.setInterpolator(new LinearInterpolator());
        this.mRefreshingAni.setDuration(450 * 10000);
        this.mRefreshingAni.start();
    }

    private void reset() {
        this.mAboveRectH = 0;
        this.mAngle = 90;
    }
}
