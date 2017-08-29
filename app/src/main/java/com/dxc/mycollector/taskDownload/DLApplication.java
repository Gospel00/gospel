package com.dxc.mycollector.taskDownload;

import android.app.Application;
import android.content.Intent;
/**
 * Created by gospel on 2017/8/18.
 * About Application startService
 */
public class DLApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        this.startService(new Intent(this, DownLoadService.class));
    }

}
