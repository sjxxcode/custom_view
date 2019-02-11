package com.sj.custom_view.practice;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by SJ on 2019/1/11.
 */
public class PrintMarkHelper {

    public static <T extends View> void printMark(T view, Canvas canvas){
        if(view != null && canvas != null){
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.BLACK);
            paint.setTextSize(30);

            String mark = view.getClass().getSimpleName();

            canvas.drawText(mark, 10, 30, paint);
        }
    }
}
