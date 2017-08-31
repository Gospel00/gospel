/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector.logs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.annotation.SuppressLint;

import com.google.gson.Gson;
/**
 * Created by Gospel on 2017/8/31 12:57
 * DXC technology
 */
@SuppressLint("SdCardPath")
public class WriteThread extends Thread {

    public boolean isWriteThreadLive = false;// 写日志线程是否已经在运行�?

    private static final String LOG_DIR = "/mnt/sdcard/mytunner/";
    private static final String LOG_SERVICE_LOG_PATH = LOG_DIR + "tunnerRecord.txt";

    public ConcurrentLinkedQueue<Object> linkedQueue = new ConcurrentLinkedQueue<Object>();

    public WriteThread() {
        File file = new File(LOG_SERVICE_LOG_PATH);
        if (file.exists()) {
            // 日志文件超过50M备份
            if (file.length() > 50 * 1024 * 1024) {
                // File file1 = new File(LOG_DIR + "xDeviceService_bak.txt");
                File file1 = new File(LOG_DIR + "tunnerRecord_bak.txt");
                if (file1.exists()) {
                    file1.delete();
                }
                file.renameTo(file1);
            }
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        isWriteThreadLive = true;
        Gson gson = new Gson();
        while (!linkedQueue.isEmpty()) {// 对列不空�?
            try {
                // 写日志到SD�?
                recordStringLog(gson.toJson(linkedQueue.poll()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        isWriteThreadLive = false;// 队列中的日志都写完了，关闭线程（也可以常�? 要测试下�?
    }

    /**
     * 打开日志文件并写入日志
     *
     * @return
     */
    private void recordStringLog(String text) {// 新建或打�?日志文件
        File file = new File(LOG_SERVICE_LOG_PATH);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(text);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}