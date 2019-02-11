package com.sj.custom_view.practice.touch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.sj.custom.R;
import com.sj.custom_view.MyClick;

/**
 * Created by SJ on 2019/1/10.
 */
public class MultiTouchActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.act_mutil_touch_layout_1);

        this.findViewById(R.id.mutil_1).setOnClickListener(new MyClick(this, MultiTouchView1.class, R.id.layout));
        this.findViewById(R.id.mutil_2).setOnClickListener(new MyClick(this, MultiTouchView2.class, R.id.layout));
    }
}
