package com.sj.custom_view.practice.scable;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.sj.custom.R;

/**
 * Created by SJ on 2019/1/8.
 */
public class ScableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.act_scable_imgview_layout);
    }
}
