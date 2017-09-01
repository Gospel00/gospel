package com.dxc.mycollector.taskDownload;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.dxc.mycollector.dbhelp.DatabaseHelper;
import com.dxc.mycollector.logs.Logger;

/**
 * Created by gospel on 2017/8/18.
 * About 下载器后台服务
 */
public class DownLoadService extends Service {
    private static DownLoadManager downLoadManager;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    public static DownLoadManager getDownLoadManager() {
        return downLoadManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        downLoadManager = new DownLoadManager(DownLoadService.this);
        Logger.i("DownLoadService", "DownLoadService init .");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放downLoadManager
//        downLoadManager.stopAllTask();
        downLoadManager = null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (downLoadManager == null) {
            downLoadManager = new DownLoadManager(DownLoadService.this);
        }
    }
}
