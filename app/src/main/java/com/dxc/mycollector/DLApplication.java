/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector;

import android.app.Application;
import android.content.Intent;

import com.dxc.mycollector.logs.Logger;
import com.dxc.mycollector.taskDownload.DownLoadService;

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
        Logger.i(TAG, "Appliction init. ");
    }

//    public String getUserName() {
//        return userName;
//    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
}
