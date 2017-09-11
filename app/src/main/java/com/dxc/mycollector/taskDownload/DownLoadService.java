package com.dxc.mycollector.taskDownload;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.dxc.mycollector.WeiboDialogUtils;
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

    //消息处理线程
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    downLoadManager = new DownLoadManager(DownLoadService.this);
                    mHandler.sendEmptyMessageDelayed(1, 10000);//处理消息
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler.sendEmptyMessageDelayed(1, 10000);//处理消息
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
