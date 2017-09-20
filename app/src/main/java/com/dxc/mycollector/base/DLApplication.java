/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector.base;

import android.app.Application;
import android.content.Intent;

import com.dxc.mycollector.taskDownload.DownLoadService;
import com.gstar.android.GstarSDK;

/**
 * Created by gospel on 2017/8/18.
 * About Application startService
 */
public class DLApplication extends Application {
    public static final String amdin = "gospel";
    public static String userName = null;
    String TAG = DLApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        this.startService(new Intent(this, DownLoadService.class));
        //使用默认包名，默认水印图标进行初始化
//        GstarSDK.getInstance(this);
//		//使用默认包名，用户定制水印图标进行初始化
        GstarSDK.getInstance(this, mcKey.getMcKey());
        //使用用户给定的包名，用户定制水印图标进行初始化
//		GstarSDK.getInstance(this,this.getApplicationContext().getPackageName() ,mcKey.getMcKey());
    }
}
