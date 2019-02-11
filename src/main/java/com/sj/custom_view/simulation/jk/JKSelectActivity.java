package com.sj.custom_view.simulation.jk;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.sj.custom.R;
import com.sj.custom_view.MyClick;
import com.sj.custom_view.simulation.jk.select.SelectView;

/**
 * Created by SJ on 2019/1/11.
 */
public class JKSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.act_jk_main_layout);

        this.findViewById(R.id.select).setOnClickListener(new MyClick(this, SelectView.class, R.id.layout));
    }
}
