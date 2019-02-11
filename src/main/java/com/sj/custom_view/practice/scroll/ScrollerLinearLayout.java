package com.sj.custom_view.practice.scroll;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.BounceInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by SJ on 2019/1/8.
 */
public class ScrollerLinearLayout extends LinearLayout{

    private Scroller scroller;

    public ScrollerLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.scroller = new Scroller(context, new BounceInterpolator());
    }

    @Override
    public void computeScroll() {
        if(this.scroller.computeScrollOffset()){
            this.scrollTo(this.scroller.getCurrX(), this.scroller.getCurrY());
            this.postInvalidateOnAnimation();
        }
    }

    public void startScrollTo(){
        this.scroller.startScroll(this.getScrollX(), this.getScrollY(), 0, -500, 1000);
        this.postInvalidateOnAnimation();
    }
}
