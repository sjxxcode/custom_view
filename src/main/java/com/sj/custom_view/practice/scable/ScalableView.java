package com.sj.custom_view.practice.scable;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.OverScroller;

import com.sj.custom.R;

/**
 * 缩放图片
 *
 * Created by eunice on 2019/1/7.
 */

public class ScalableView extends View {

    //大图模式下的一个 缩放系数
    private final float SCALE_FACTOR = 1.2F;
    //初始偏移量
    private final float ORIGNAL_OFFSET = 0F;

    private Bitmap mBitmap;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    //手势
    //GestureDetector与GestureDetectorCompat
    private GestureDetectorCompat mGestureDetector;
    //缩放手势
    private ScaleGestureDetector mScaleDetector;
    //Scroller
    OverScroller mScroller;

    //最大缩放倍数
    private float mMaxScale = 1F;
    //最小缩放倍数
    private float mMinScale = 1F;

    private int mBitmapW = 0;
    private int mBitmapH = 0;
    //图片的起始位置 X
    private float mDrawLeft = 0;
    //图片的起始位置 Y
    private float mDrawTop = 0;
    //大图模式下，横向滑动图片的偏移量
    private float mOffsetX = ORIGNAL_OFFSET;
    //大图模式下，纵向滑动图片的偏移量
    private float mOffsetY = ORIGNAL_OFFSET;

    //当前缩放倍数
    private float mCurrentScale = mMinScale;
    //是否是大图模式
    private boolean isLargeMode = false;

    private RefreshRunable mRefreshTask = new RefreshRunable();

    public ScalableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        this.mDrawLeft = (this.getWidth() - this.mBitmapW) / 2F;
        this.mDrawTop = (this.getHeight() - this.mBitmapH) / 2F;

        float scale1 = this.getWidth() / (this.mBitmapW + 0F);
        float scale2 = this.getHeight() / (this.mBitmapH + 0F);
        this.mMinScale = scale1 > scale2 ? scale2 : scale1;
        this.mMaxScale = (scale1 > scale2 ? scale1 : scale2) * SCALE_FACTOR;
        this.mCurrentScale = this.mMinScale;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.scale(this.mCurrentScale, this.mCurrentScale, this.getWidth() / 2, this.getHeight() / 2);
        canvas.drawBitmap(this.mBitmap,
                this.mDrawLeft + this.mOffsetX,
                this.mDrawTop + this.mOffsetY,
                this.mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = this.mScaleDetector.onTouchEvent(event);

        if(!this.mScaleDetector.isInProgress()){
            result = this.mGestureDetector.onTouchEvent(event);
        }

        return result;
    }

    private void init() {
        this.initBitmap();
        this.initGesture();
        this.initScroller();
    }

    private void initBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inDensity = 160;
        options.inTargetDensity = 520;

        this.mBitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.batman, options);

        this.mBitmapW = this.mBitmap.getWidth();
        this.mBitmapH = this.mBitmap.getHeight();
    }

    private void initGesture() {
        this.mGestureDetector = new GestureDetectorCompat(this.getContext(), new GestureImpl());
        this.mGestureDetector.setOnDoubleTapListener(new DoubleTapGestureImpl());
        this.mScaleDetector = new ScaleGestureDetector(this.getContext(), new ScaleGestureImpl());
    }

    private void initScroller() {
        this.mScroller = new OverScroller(this.getContext());
    }

    private ValueAnimator getAni(float scaleFrom, float scaleTo,
                                 float offsetXFrom, float offsetXTo,
                                 float offsetYFrom, float offsetYTo) {
        PropertyValuesHolder holder1 = PropertyValuesHolder.ofFloat("imgScale", scaleFrom, scaleTo);
        PropertyValuesHolder holder2 = PropertyValuesHolder.ofFloat("offsetX", offsetXFrom, offsetXTo);
        PropertyValuesHolder holder3 = PropertyValuesHolder.ofFloat("offsetY", offsetYFrom, offsetYTo);

        ValueAnimator ani = ObjectAnimator.ofPropertyValuesHolder(holder1, holder2, holder3);
        ani.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float scale = (float) animation.getAnimatedValue("imgScale");
                float offsetX = (float) animation.getAnimatedValue("offsetX");
                float offsetY = (float) animation.getAnimatedValue("offsetY");

                mCurrentScale = scale;
                mOffsetX = offsetX;
                mOffsetY = offsetY;

                invalidate();
            }
        });

        ani.setDuration(300);

        return ani;
    }

    private void refreshWhenDrage(float distanceX, float distanceY) {
        if (isLargeMode) {
            float absOffsetX = Math.abs((this.getWidth() - this.mBitmapW * this.mMaxScale) / 2F);
            float absOffsetY = Math.abs((this.getHeight() - this.mBitmapH * this.mMaxScale) / 2F);

            this.mOffsetX -= distanceX;
            if (this.mOffsetX < -absOffsetX) {
                this.mOffsetX = -absOffsetX;
            } else if (this.mOffsetX > absOffsetX) {
                this.mOffsetX = absOffsetX;
            }

            this.mOffsetY -= distanceY;
            if (this.mOffsetY < -absOffsetY) {
                this.mOffsetY = -absOffsetY;
            } else if (this.mOffsetY > absOffsetY) {
                this.mOffsetY = absOffsetY;
            }

            this.invalidate();
        }
    }

//    private float getImgScale() {
//        return this.mCurrentScale;
//    }
//
//    private void setImgScale(float scale) {
//        this.mCurrentScale = scale;
//        this.invalidate();
//    }

    private class RefreshRunable implements Runnable{

        @Override
        public void run() {
            if (mScroller.computeScrollOffset()) {
                mOffsetX = mScroller.getCurrX();
                mOffsetY = mScroller.getCurrY();
                invalidate();

                postOnAnimation(this);
            }
        }
    }

    private class GestureImpl implements GestureDetector.OnGestureListener{

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {}

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent down, MotionEvent move, float distanceX, float distanceY) {
            refreshWhenDrage(distanceX, distanceY);

            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {}

        @Override
        public boolean onFling(MotionEvent down, MotionEvent move, float velocityX, float velocityY) {
            if(isLargeMode){
                int absX = (int) Math.abs(((mBitmapW * mMaxScale - getWidth()) / 2F));
                int absY = (int) Math.abs(((mBitmapH * mMaxScale - getHeight()) / 2F));

                mScroller.fling(
                        (int) mOffsetX,
                        (int) mOffsetY,
                        (int) velocityX,
                        (int) velocityY,
                        -absX,
                        absX,
                        -absY,
                        absY,
                        100,
                        100);

                postOnAnimation(mRefreshTask);
            }

            return false;
        }
    }

    private class DoubleTapGestureImpl implements GestureDetector.OnDoubleTapListener{

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            isLargeMode = !isLargeMode;

            if (isLargeMode) { //大图模式
                getAni(mCurrentScale, mMaxScale,
                        ORIGNAL_OFFSET, mOffsetX,
                        ORIGNAL_OFFSET, mOffsetY).start();
            } else { //小图模式
                getAni(mCurrentScale, mMinScale,
                        mOffsetX, ORIGNAL_OFFSET,
                        mOffsetY, ORIGNAL_OFFSET).start();
            }

            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }
    }

    private class ScaleGestureImpl implements ScaleGestureDetector.OnScaleGestureListener {

        private float orignalScale = 0F;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float tempScale = this.orignalScale * detector.getScaleFactor();
            if(tempScale > mMaxScale){
                tempScale = mMaxScale;
            } else if(tempScale < mMinScale){
                tempScale = mMinScale;
            }

            mCurrentScale = tempScale;

            isLargeMode = mCurrentScale > mMinScale ? true : false;

            postInvalidateOnAnimation();

            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            this.orignalScale = mCurrentScale;

            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }
    }
}
