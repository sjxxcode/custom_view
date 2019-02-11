package com.sj.custom_view.practice.touch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sj.custom.R;
import com.sj.custom_view.Util;
import com.sj.custom_view.practice.PrintMarkHelper;

/**
 * 多点触控第2种使用场景：多个手指配合情形
 * <p>
 * Created by SJ on 2019/1/10.
 */
public class MultiTouchView2 extends View {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Bitmap bitmap = null;

    //初始偏移
    private float originalX;
    private float originalY;

    //移动偏移
    private float moveOffsetX;
    private float moveoffsetY;

    private float lastX;
    private float lastY;

    public MultiTouchView2(Context context) {
        super(context);

        this.initBitmap();
    }

    public MultiTouchView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.initBitmap();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        this.originalX = (w - this.bitmap.getWidth()) / 2;
        this.originalY = (h - this.bitmap.getHeight()) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.BLUE);

        canvas.drawBitmap(this.bitmap,
                this.originalX + this.moveOffsetX,
                this.originalY + this.moveoffsetY,
                this.paint);

        PrintMarkHelper.printMark(this, canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float[] xy = this.getFocusPointXY(event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                this.lastX = xy[0];
                this.lastY = xy[1];

                break;

            case MotionEvent.ACTION_MOVE:
                this.moveOffsetX += xy[0] - lastX;
                this.moveoffsetY += xy[1] - lastY;

                this.lastX = xy[0];
                this.lastY = xy[1];

                this.invalidate();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                this.lastX = xy[0];
                this.lastY = xy[1];
                break;

            case MotionEvent.ACTION_POINTER_UP:
                xy = this.getFocusPointXYWhenPointUp(event, event.getActionIndex());
                this.lastX = xy[0];
                this.lastY = xy[1];
                break;
        }

        return true;
    }

    private void initBitmap() {
        this.bitmap = Util.getBitmap(this.getContext(), R.mipmap.batman, 2);
    }

    private float[] getFocusPointXY(MotionEvent event){
        float xSum = 0;
        float ySum = 0;
        int pointCount = event.getPointerCount();

        for(int index = 0; index < pointCount; index++){
            xSum += event.getX(index);
            ySum += event.getY(index);
        }

        return new float[]{xSum / pointCount, ySum / pointCount};
    }

    private float[] getFocusPointXYWhenPointUp(MotionEvent event, int upPointIndex){
        float xSum = 0;
        float ySum = 0;
        int pointCount = event.getPointerCount();

        for(int index = 0; index < pointCount; index++){
            if(index != upPointIndex){
                xSum += event.getX(index);
                ySum += event.getY(index);
            }
        }

        pointCount--;

        return new float[]{xSum / pointCount, ySum / pointCount};
    }
}
