package com.dxc.mycollector.taskDownload;

import android.app.Application;
import android.content.Intent;

import com.dxc.mycollector.model.User;

/**
 * Created by gospel on 2017/8/18.
 * About Application startService
 */
public class DLApplication extends Application {
    public static final String amdin = "gospel";
    public static User userSession = new User();

    @Override
    public void onCreate() {
        super.onCreate();
        this.startService(new Intent(this, DownLoadService.class));
    }

}
