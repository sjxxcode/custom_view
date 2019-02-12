package com.sj.custom_view.practice.layout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.sj.custom.R;

import java.util.Random;

/**
 * Created by SJ on 2019/2/12.
 */
public class TagActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.act_tag_layout);

        String[] strs = {"中国","俄罗斯","澳大利亚","阿尔巴尼亚","吉尔吉斯斯坦"};
        TagLayout tags = this.findViewById(R.id.tags);

        for(int i = 0; i < 30; i++){
            Button btn = new Button(this);
            btn.setText(strs[new Random().nextInt(strs.length)]);

            tags.addView(btn);
        }
    }
}
