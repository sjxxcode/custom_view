package com.sj.custom_view.simulation.jk.select;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import com.sj.custom.R;
import com.sj.custom_view.Util;

import java.util.Arrays;

/**
 * 知识点汇总：
 * 1:属性动画
 * 1.1:如何自定义属性动画
 * 2:绘制
 * 2.1:文字绘制
 * 2.1.1:如何确定文字绘制的y坐标(采用Paint.getTextBounds()/TextPaint.getFontMetrics())
 * <p>
 * <p>
 * Created by SJ on 2019/1/11.
 */
public class SelectView extends View {

    private final float DEFAULT_TRANSLATE = 50;
    private final int DEFAULT_CHAR_SPACING = 20;
    private final int DEFAULT_TEXT_SIZE = 35;
    private final int DEFAULT_COLOR = Color.parseColor("#77787b");

    private Paint mOtherPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mOrignalTextPaint = null;
    private Paint mAddTextPaint = null;

    private Bitmap mSelectBit;
    private Bitmap mUnSelectBit;
    private Bitmap mShiningBit;
    private char[] mOrignalNums;
    private char[] mAddNums;
    private int mAddIndex;

    private float mOffsetX = 0;
    private float mOffsetY = 0;
    private float mScale = 1.0F;
    private float mShingScale = 1.0F;

    private float mTextTranslateOrginalX;
    private float mTextTranslateOrginalY;
    private float mTextTranslateOffsetY;

    private int mCircleRadius;
    private int mCircleAlpha;

    private int mTextAlpha = 255;
    private float mTextOffsetY;

    private boolean isInitFlag = true;
    //是否是选择状态
    private boolean isSelectFlag;
    //是否是抬起动作
    private boolean isUpFlag;
    //是否移动到了顶部
    private boolean isTextTranslateTop;


    public SelectView(Context context) {
        super(context);

        this.init();
    }

    public SelectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        this.mOffsetX = (this.getWidth() - this.mUnSelectBit.getWidth()) / 2F;
        this.mOffsetY = (this.getHeight() - this.mUnSelectBit.getHeight()) / 2F;

        this.mTextTranslateOrginalX = (this.getWidth() + this.mSelectBit.getWidth()) / 2F + 10;
        this.mTextTranslateOrginalY = this.getWidth() / 2F + this.mTextOffsetY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //circle
        if(!this.isInitFlag && this.isUpFlag && this.mCircleRadius > 0){
            //this.mCirclePaint.setAlpha(this.mCircleAlpha);
            //canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2, this.mCircleRadius, this.mCirclePaint);
        }

        //img
        canvas.save();
        Bitmap imgBit = this.isSelectFlag ? this.mSelectBit : this.mUnSelectBit;
        canvas.scale(this.mScale, this.mScale, this.getWidth() / 2, this.getHeight() / 2);
        canvas.drawBitmap(imgBit, this.mOffsetX, this.mOffsetY, this.mOtherPaint);
        canvas.restore();

        //shing
        if (this.isSelectFlag) {
            canvas.save();
            float shingOffsetX = this.mOffsetX + this.mSelectBit.getHeight() * 0.09F;
            float shingOffsetY = this.mOffsetY - this.mSelectBit.getHeight() * 0.4F;
            canvas.scale(this.mShingScale, this.mShingScale, this.getWidth() / 2, this.getHeight() / 2);
            canvas.drawBitmap(this.mShiningBit, shingOffsetX, shingOffsetY, this.mOtherPaint);
            canvas.restore();
        }

        //draw text
        drawSelctText(canvas);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                this.isUpFlag = false;
                this.startAni(1.0F, 0.7F,
                        1.0f, 0.7F,
                        new LinearInterpolator());
                break;

            case MotionEvent.ACTION_UP:
                this.isUpFlag = true;
                this.isSelectFlag = !this.isSelectFlag;

                float shingFromScale = this.isSelectFlag ? 0 : 1.0F;
                float shingToScale = this.isSelectFlag ? 1.0F : 0;

                this.startAni(0.7F, 1.0F,
                        shingFromScale, shingToScale,
                        new OvershootInterpolator());
                break;
        }

        return true;
    }

    private void init() {
        this.initTextChars();

        this.mSelectBit = Util.getBitmap(this.getContext(), R.mipmap.jk_ic_messages_like_selected, 1.5F);
        this.mUnSelectBit = Util.getBitmap(this.getContext(), R.mipmap.jk_ic_messages_like_unselected, 1.5F);
        this.mShiningBit = Util.getBitmap(this.getContext(), R.mipmap.jk_ic_messages_like_selected_shining, 1.5F);

        this.mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mCirclePaint.setColor(Color.RED);
        this.mCirclePaint.setStrokeWidth(10);
        this.mCirclePaint.setStyle(Paint.Style.STROKE);

        this.mAddTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mAddTextPaint.setTextSize(DEFAULT_TEXT_SIZE);
        this.mAddTextPaint.setColor(DEFAULT_COLOR);

        Rect bounds = new Rect();
        this.mOrignalTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mOrignalTextPaint.setTextSize(DEFAULT_TEXT_SIZE);
        this.mOrignalTextPaint.setColor(DEFAULT_COLOR);
        this.mOrignalTextPaint.getTextBounds(this.mOrignalNums, 0, this.mOrignalNums.length, bounds);
        this.mTextOffsetY = -(bounds.top + bounds.bottom) / 2;
    }

    private void initTextChars() {
        this.mOrignalNums = new char[]{'1', '2', '9'};
        int origanlNum = Integer.valueOf(new String(this.mOrignalNums));
        char[] tempChars = String.valueOf(origanlNum + 1).toCharArray();

        int fistIndex = 0;
        int twoIndex = 0;
        while (fistIndex < this.mOrignalNums.length && twoIndex < tempChars.length) {
            if (this.mOrignalNums[fistIndex] != tempChars[twoIndex]) {
                break;
            }
            fistIndex++;
            twoIndex++;
        }
        this.mAddIndex = twoIndex;
        this.mAddNums = Arrays.copyOfRange(tempChars, twoIndex, tempChars.length);
    }

    private void drawSelctText(Canvas canvas) {
        //text
        this.drawOrignalText(canvas);
        this.drawAddText(canvas);
        this.isInitFlag = false;
    }

    private void drawOrignalText(Canvas canvas) {
        float startX = this.mTextTranslateOrginalX;
        for (int index = 0; index < this.mOrignalNums.length; index++) {
            if (index >= this.mAddIndex) {
                this.drawMoveText(canvas, this.mOrignalTextPaint, startX, index);
            } else {
                this.mOrignalTextPaint.setAlpha(255);
                canvas.drawText(this.mOrignalNums, index, 1, startX, this.mTextTranslateOrginalY, this.mOrignalTextPaint);
            }

            startX += DEFAULT_CHAR_SPACING;
        }
    }

    private void drawAddText(Canvas canvas) {
        if (!this.isInitFlag) {
            float startY = this.mTextTranslateOrginalY + DEFAULT_TRANSLATE + this.mTextTranslateOffsetY;
            float startX = this.mTextTranslateOrginalX;
            startX += DEFAULT_CHAR_SPACING * this.mAddIndex;

            this.mAddTextPaint.setAlpha(255 - this.mTextAlpha);
            canvas.drawText(this.mAddNums, 0, this.mAddNums.length, startX, startY, this.mAddTextPaint);
        }
    }

    private void drawMoveText(Canvas canvas, Paint paint, float startX, int index) {
        paint.setAlpha(this.mTextAlpha);
        canvas.drawText(this.mOrignalNums, index, 1, startX, this.mTextTranslateOrginalY + this.mTextTranslateOffsetY, paint);
    }

    private void startAni(@FloatRange(from = 0.5F, to = 1.0F) float fromScale,
                          @FloatRange(from = 0.5F, to = 1.0F) float toScale,
                          @FloatRange(from = 0F, to = 1.0F) float shingTromScale,
                          @FloatRange(from = 0F, to = 1.0F) float shingToScale,
                          TimeInterpolator interpolator) {

        AnimatorSet aniSet = new AnimatorSet();
        AnimatorSet.Builder builder = aniSet.play(this.getImgAni(fromScale, toScale, shingTromScale, shingToScale, interpolator));

        if (this.isUpFlag) {
            builder.with(this.getTextChangeAni());
            builder.with(this.getCircleAni(interpolator));
        }

        aniSet.setDuration(300);
        aniSet.start();
    }

    @SuppressWarnings("all")
    private Animator getImgAni(float fromScale, float toScale,
                               float shingTromScale, float shingToScale,
                               TimeInterpolator interpolator) {

        PropertyValuesHolder holder1 = PropertyValuesHolder.ofFloat("scaleXY", fromScale, toScale);
        PropertyValuesHolder holder2 = PropertyValuesHolder.ofFloat("shingScaleXY", shingTromScale, shingToScale);

        ValueAnimator aniImg = ValueAnimator.ofPropertyValuesHolder(holder1, holder2);
        aniImg.setInterpolator(interpolator);
        aniImg.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mScale = (float) animation.getAnimatedValue("scaleXY");
                mShingScale = (float) animation.getAnimatedValue("shingScaleXY");

                postInvalidateOnAnimation();
            }
        });

        return aniImg;
    }

    @SuppressWarnings("all")
    private Animator getTextChangeAni() {
        float trslateFrom = this.isTextTranslateTop ? -DEFAULT_TRANSLATE : 0;
        float trslateTo = this.isTextTranslateTop ? 0 : -DEFAULT_TRANSLATE;
        int alphaFrom = this.isTextTranslateTop ? 0 : 255;
        int alphaTo = this.isTextTranslateTop ? 255 : 0;

        PropertyValuesHolder holder1 = PropertyValuesHolder.ofFloat("textTranslate", trslateFrom, trslateTo);
        PropertyValuesHolder holder2 = PropertyValuesHolder.ofInt("textAlpha", alphaFrom, alphaTo);

        ValueAnimator ani = ValueAnimator.ofPropertyValuesHolder(holder1, holder2);
        ani.setInterpolator(new DecelerateInterpolator());
        ani.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mTextTranslateOffsetY = (float) animation.getAnimatedValue("textTranslate");
                mTextAlpha = (int) animation.getAnimatedValue("textAlpha");
            }
        });
        ani.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isTextTranslateTop = !isTextTranslateTop;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        return ani;
    }

    @SuppressWarnings("all")
    private Animator getCircleAni(TimeInterpolator interpolator){
        PropertyValuesHolder holder1 = PropertyValuesHolder.ofInt("circieRadius", 10, 37);
        PropertyValuesHolder holder2 = PropertyValuesHolder.ofInt("circieAlpha", 0, 255, 0);

        ValueAnimator ani = ValueAnimator.ofPropertyValuesHolder(holder1);
        ani.setInterpolator(interpolator);
        ani.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCircleRadius = (int) animation.getAnimatedValue("circieRadius");
                //mCircleAlpha = (int) animation.getAnimatedValue("circieAlpha");
            }
        });

        return ani;
    }
}
