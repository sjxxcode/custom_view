package com.sj.custom_view.practice.touch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.sj.custom.R;
import com.sj.custom_view.Util;
import com.sj.custom_view.practice.PrintMarkHelper;

/**
 * 多点触控第1种使用场景：多个手指接力情形
 * <p>
 * Created by SJ on 2019/1/10.
 */
public class MultiTouchView1 extends View {

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

    //对于手指来说，它的 index 是会变得，而id 是不会变的。
    //所以，只能通过追踪id 来找到手指对应的index，从而通过index获取追踪手指的坐标信息。
    private int trackingPointId;

    public MultiTouchView1(Context context) {
        super(context);

        this.initBitmap();
    }

    public MultiTouchView1(Context context, @Nullable AttributeSet attrs) {
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

        canvas.drawColor(Color.RED);

        canvas.drawBitmap(this.bitmap,
                this.originalX + this.moveOffsetX,
                this.originalY + this.moveoffsetY,
                this.paint);

        PrintMarkHelper.printMark(this, canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                this.trackingPointId = event.getPointerId(0);
                this.lastX = event.getX();
                this.lastY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                int tempIndex = event.findPointerIndex(this.trackingPointId);
                this.moveOffsetX += event.getX(tempIndex) - lastX;
                this.moveoffsetY += event.getY(tempIndex) - lastY;

                this.lastX = event.getX(tempIndex);
                this.lastY = event.getY(tempIndex);

                this.invalidate();
                break;

            case MotionEvent.ACTION_UP:
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                //FIXME 这下边两句代码的意思如下解释
                //当另一个手指按下的时候，需要让新按下的这根手指"抢夺焦点"，就是说：让之后的move 事件从这个手指中拿坐标数据。
                int index = event.getActionIndex();
                this.trackingPointId = event.getPointerId(index);

                this.lastX = event.getX(index);
                this.lastY = event.getY(index);

                break;

            case MotionEvent.ACTION_POINTER_UP:
                int upPointIndex = event.getActionIndex();
                int upPointId = event.getPointerId(upPointIndex);

                if(upPointId == this.trackingPointId){
                    int newPointIndex = event.getPointerCount() - 1;
                    newPointIndex += upPointIndex == newPointIndex ? -1 : 0;

                    this.trackingPointId = event.getPointerId(newPointIndex);

                    this.lastX = event.getX(newPointIndex);
                    this.lastY = event.getY(newPointIndex);
                }
                break;
        }

        return true;
    }

    private void initBitmap() {
        this.bitmap = Util.getBitmap(this.getContext(), R.mipmap.batman, 2);
    }

    private void print(MotionEvent event) {
        int actionIndex = event.getActionIndex();
        int pointCount = event.getPointerCount();
        int pointId = event.getPointerId(actionIndex);

        Log.e("===MultiTouch","--ActionMasked:" + event.getActionMasked()
                                        + "--PointCount:" + pointCount
                                        + "--ActionIndex:" + actionIndex
                                        + "--PointId:" + pointId);
    }
}
