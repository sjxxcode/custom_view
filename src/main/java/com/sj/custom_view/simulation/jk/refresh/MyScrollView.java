package com.sj.custom_view.simulation.jk.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by SJ on 2019/1/28.
 */
public class MyScrollView extends ScrollView{
    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.e("===MyScrollView", "dispatchTouchEvent()--Action:" + ev.getActionMasked());

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //Log.e("===MyScrollView", "onInterceptTouchEvent()--Action:" + ev.getActionMasked());

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.e("===MyScrollView", "onTouchEvent()--Action:" + ev.getActionMasked());

        return super.onTouchEvent(ev);
    }
}
