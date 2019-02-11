package com.sj.custom_view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.sj.custom.R;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by SJ on 2019/1/11.
 */
public class MyFragment extends Fragment {

    private RelativeLayout layout = null;
    private Class targetClass = null;

    public MyFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.layout = (RelativeLayout) inflater.inflate(R.layout.fragemt_mutile_layout, container, false);

        return this.layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.addTargetView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden){
            this.addTargetView();
        }
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);

        this.targetClass = (Class) args.getSerializable("view_class");
    }

    private void addTargetView() {
        if(this.layout != null && this.targetClass != null){
            this.layout.removeAllViewsInLayout();

            try {
                Constructor ct = this.targetClass.getDeclaredConstructor(Context.class);
                if(ct != null){
                    View child = (View) ct.newInstance(this.getActivity());
                    if(child != null){
                        this.layout.addView(child);
                    }
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}
