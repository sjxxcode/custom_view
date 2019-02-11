package com.sj.custom_view.practice.touch;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by SJ on 2019/1/7.
 */
public class TouchView extends View{

    public TouchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if(action == MotionEvent.ACTION_UP){
            this.performClick();
        }

        return super.onTouchEvent(event);
    }
}
