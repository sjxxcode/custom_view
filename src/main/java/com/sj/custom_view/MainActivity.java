package com.sj.custom_view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sj.custom.R;
import com.sj.custom_view.practice.layout.MyViewPagerAct;
import com.sj.custom_view.practice.layout.TagActivity;
import com.sj.custom_view.practice.scable.ScableActivity;
import com.sj.custom_view.practice.touch.MultiTouchActivity;
import com.sj.custom_view.simulation.jk.JKSelectActivity;
import com.sj.custom_view.simulation.jk.JKRefreshActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.findViewById(R.id.taglayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TagActivity.class));
            }
        });

        this.findViewById(R.id.custom_viewpager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MyViewPagerAct.class));
            }
        });

        this.findViewById(R.id.scable_act).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ScableActivity.class));
            }
        });

        this.findViewById(R.id.multi_touch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MultiTouchActivity.class));
            }
        });

        this.findViewById(R.id.jk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, JKSelectActivity.class));
            }
        });

        this.findViewById(R.id.jk_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, JKRefreshActivity.class));
            }
        });
    }
}
