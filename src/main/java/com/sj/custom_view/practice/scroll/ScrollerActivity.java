package com.sj.custom_view.practice.scroll;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sj.custom.R;

/**
 * Created by SJ on 2019/1/8.
 */
public class ScrollerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.act_scroller_layout);

        final ScrollerTextView text = this.findViewById(R.id.test);
        final ScrollerLinearLayout layout = this.findViewById(R.id.layout);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.startScrollTo();
            }
        });

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.startScrollTo();
            }
        });
    }
}
