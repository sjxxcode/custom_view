package com.sj.custom_view.practice.scroll;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.BounceInterpolator;
import android.widget.Scroller;

/**
 * Created by SJ on 2019/1/8.
 */
public class ScrollerTextView extends android.support.v7.widget.AppCompatTextView{

    private Scroller scroller;

    public ScrollerTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.scroller = new Scroller(context, new BounceInterpolator());
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        if(this.scroller.computeScrollOffset()){
//            this.scrollTo(this.scroller.getCurrX(), this.scroller.getCurrY());
//            super.onDraw(canvas);
//            this.postInvalidateOnAnimation();
//            return;
//        }
//
//        super.onDraw(canvas);
//    }


    @Override
    public void computeScroll() {
        Log.e("===Scroller", "computeScroll()--1111111");

        if(this.scroller.computeScrollOffset()){
            Log.e("===Scroller", "computeScroll()--2222222");

            this.scrollTo(this.scroller.getCurrX(), this.scroller.getCurrY());
            this.postInvalidateOnAnimation();
        }
    }

    public void startScrollTo(){
        this.scroller.startScroll(this.getScrollX(), this.getScrollY(), 0, -200, 1000);
        this.postInvalidateOnAnimation();
    }
}
