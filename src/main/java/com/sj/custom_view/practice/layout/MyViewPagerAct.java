package com.sj.custom_view.practice.layout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.sj.custom.R;

/**
 * Created by SJ on 2018/12/29.
 */
public class MyViewPagerAct extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.act_custom_viewpager);
    }
}
