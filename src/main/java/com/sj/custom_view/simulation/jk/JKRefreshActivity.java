package com.sj.custom_view.simulation.jk;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sj.custom.R;
import com.sj.custom_view.simulation.jk.refresh.RefreshLayout3;

/**
 * Created by SJ on 2019/1/18.
 */
public class JKRefreshActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.act_refresh_layout);

        View img1 = this.findViewById(R.id.img_1);
        View text1 = this.findViewById(R.id.text_1);

//        img1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(v.getContext(), "Click--Img", Toast.LENGTH_SHORT).show();
//            }
//        });
//        img1.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if(event.getActionMasked() == MotionEvent.ACTION_UP){
//                    Toast.makeText(v.getContext(), "Touch--Img", Toast.LENGTH_SHORT).show();
//                }
//                return true;
//            }
//        });
//        text1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(v.getContext(), "Click--Text", Toast.LENGTH_SHORT).show();
//            }
//        });

        final RefreshLayout3 refreshLayout = this.findViewById(R.id.refresh_layout);

        this.findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.refreshComplate();
            }
        });
    }
}
