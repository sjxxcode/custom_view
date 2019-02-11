package com.sj.custom_view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.FloatRange;
import android.text.TextUtils;

/**
 * Created by SJ on 2019/1/10.
 */
public class Util {

    public static Bitmap getBitmap(Context context, int resId, @FloatRange(from = 0.5, to = 5) float scale){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inDensity = context.getResources().getDisplayMetrics().densityDpi;
        options.inTargetDensity = (int) (options.inDensity * scale);

        return BitmapFactory.decodeResource(context.getResources(), resId, options);
    }

    public static <T> void checkNull(T target, String promptStr){
        if(target == null){
            throw new NullPointerException(!TextUtils.isEmpty(promptStr) ? promptStr : "The 'target' is null.");
        }
    }
}
