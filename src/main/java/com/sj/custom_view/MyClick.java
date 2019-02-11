package com.sj.custom_view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

/**
 * Created by SJ on 2019/1/11.
 */
public class MyClick implements View.OnClickListener{

    private FragmentActivity context;
    private Class tragetClass;
    private int layoutId;

    public MyClick(FragmentActivity context, Class tragetClass, int layoutId) {
        this.context = context;
        this.tragetClass = tragetClass;
        this.layoutId = layoutId;
    }

    @Override
    public void onClick(View v) {
        if(this.tragetClass == null && this.context != null){
            Toast.makeText(this.context, "目标-Class-为null,无法加载", Toast.LENGTH_LONG).show();
            return;
        }

        FragmentManager fm = this.context.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putSerializable("view_class", this.tragetClass);

        Fragment fragment = MyFragment.instantiate(this.context,
                MyFragment.class.getName(), bundle);
        ft.replace(this.layoutId, fragment);

        ft.commit();
    }
}
