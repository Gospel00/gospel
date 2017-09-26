/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector;

import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.dxc.mycollector.fragment.NavigationFragment;

/**
 * Created by gospel on 2017/8/18.
 * About PersonWellcom
 */
public class HomeAcitvity extends BaseActivity {

    private NavigationFragment mNavigationFragment;
    boolean isOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setCurrentFragment();
//        init();

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_plus);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);  //根据字面意思是显示类型为显示自定义
        actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示
        ((TextView) findViewById(R.id.title_name)).setText("欢迎光临");
        //以下代码用于去除阴影
        if (Build.VERSION.SDK_INT >= 21) {
            getSupportActionBar().setElevation(0);
        }

        findViewById(R.id.title_personal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (isOpen) {
                    drawerLayout.closeDrawers();
                    isOpen = false;
                } else {
                    drawerLayout.openDrawer(Gravity.LEFT);
                    isOpen = true;
                }
            }
        });
    }

    private void setCurrentFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mNavigationFragment = NavigationFragment.newInstance(getString(R.string.navigation_navigation_bar));
        transaction.replace(R.id.frame_content, mNavigationFragment).commit();
    }
}

