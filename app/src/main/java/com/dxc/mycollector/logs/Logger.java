package com.dxc.mycollector.logs;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 * Created by Gospel on 2017/8/31 12:57
 * DXC technology
 */
@SuppressLint("SimpleDateFormat")
public class Logger {

    private static OutputStreamWriter writer;
    private static WriteThread writeThread = null;

    public static void i(String tag, String msg) {
        Log.i(tag, msg);
        write(tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
        write(tag, msg);
    }

    private static void write(String tag, String msg) {
        Date time = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
        String dsNow = sdf.format(time);
        String str = "mylog record is  " + dsNow + "    " + tag + "  " + msg;
        if (writeThread == null || !writeThread.isWriteThreadLive) {
            writeThread = new WriteThread();
            writeThread.start();
        }

        writeThread.linkedQueue.add(str);
    }

    public static void close() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (writeThread != null) {
            writeThread.isWriteThreadLive = false;
            writeThread = null;
        }

    }
}