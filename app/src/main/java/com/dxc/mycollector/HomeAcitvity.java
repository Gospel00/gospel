/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.dxc.mycollector.fragment.NavigationFragment;
import com.dxc.mycollector.logs.Logger;
import com.dxc.mycollector.utils.DeviceUtil;

/**
 * Created by gospel on 2017/8/18.
 * About PersonWellcom
 */
public class HomeAcitvity extends BaseActivity {

    private NavigationFragment mNavigationFragment;
    boolean isOpen = false;
    //退出时的时间
    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //联网检查
        if (!DeviceUtil.isNetworkConnected(context)) {
            Logger.i(TAG, "设备未联网...");
            new AlertDialog.Builder(context)
                    .setTitle("系统提示")
                    .setCancelable(false)//按返回键不关闭窗口
                    .setIcon(R.drawable.danger_small)
                    .setMessage("系统检测到您的设备没有连接网络，任务将无法下载。")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            Logger.i(TAG, "已经提醒用户连接网络");
                        }
                    }).show();
        }

        setCurrentFragment();
//        init();

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_plus);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);  //根据字面意思是显示类型为显示自定义
        actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示
        ((TextView) findViewById(R.id.title_name)).setText("量测任务");
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

    //按返回键到最后一个activity推出侧滑菜单
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Object mHelperUtils;
                mExitTime = System.currentTimeMillis();
                drawerLayout.openDrawer(Gravity.LEFT);//侧滑菜单栏
                isOpen = true;//设置为打开状态
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

