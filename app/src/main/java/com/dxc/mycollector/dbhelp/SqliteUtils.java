package com.dxc.mycollector.dbhelp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.dxc.mycollector.logs.Logger;
import com.dxc.mycollector.model.TaskInfo;
import com.dxc.mycollector.model.MeasureData;
import com.dxc.mycollector.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gospel on 2017/8/18.
 * DatabaseHelper
 */

public class SqliteUtils {
    static String TAG = SqliteUtils.class.getSimpleName();
    /**
     * 数据库名
     */
    public static final String DB_NAME = "tunnel_data_db";
    /**
     * 数据库版本
     */
    public static final int VERSION = 1;

    private static SqliteUtils sqliteDB;

    private SQLiteDatabase db;

    private SqliteUtils(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取SqliteDB实例
     *
     * @param context
     */
    public synchronized static SqliteUtils getInstance(Context context) {
        if (sqliteDB == null) {
            sqliteDB = new SqliteUtils(context);
            Logger.i(TAG, "SqliteUtils init success.");
        }
        return sqliteDB;
    }

    /**
     * 将User实例存储到数据库。
     */
    public int saveUser(User user) {
        if (user != null) {
            Cursor cursor = db.rawQuery("select * from tbl_users where username=?", new String[]{user.getuName().toString()});
            if (cursor.getCount() > 0) {
                return -1;
            } else {
                try {
                    db.execSQL("insert into tbl_users(username,password) values(?,?) ", new String[]{user.getuName().toString(), user.getuPwd().toString()});
                } catch (Exception e) {
                    Log.d("错误", e.getMessage().toString());
                }
                return 1;
            }
        } else {
            return 0;
        }
    }

    /**
     * 从数据库读取User信息。
     */
    public List<User> loadUser() {
        List<User> list = new ArrayList<User>();
        Cursor cursor = db
                .query("tbl_users", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndex("id")));
                user.setuName(cursor.getString(cursor
                        .getColumnIndex("username")));
                user.setuPwd(cursor.getString(cursor
                        .getColumnIndex("password")));
                user.setuAge(cursor.getString(cursor
                        .getColumnIndex("age")));
                user.setuPhone(cursor.getString(cursor
                        .getColumnIndex("phone")));
                user.setuSex(cursor.getString(cursor
                        .getColumnIndex("sex")));//0女1男
                user.setuAddress(cursor.getString(cursor
                        .getColumnIndex("address")));
                list.add(user);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 登录，根据用户名和密码查询
     *
     * @param pwd
     * @param name
     * @return
     */
    public int Quer(String pwd, String name) {
        HashMap<String, String> hashmap = new HashMap<String, String>();
        Cursor cursor = db.rawQuery("select * from tbl_users where username=?", new String[]{name});
        if (cursor.getCount() > 0) {
            Cursor pwdcursor = db.rawQuery("select * from tbl_users where password=? and username=?", new String[]{pwd, name});
            if (pwdcursor.getCount() > 0) {
                return 1;
            } else {
                return -1;
            }
        } else {
            return 0;
        }
    }

    /**
     * 将Measure存储到数据库。
     */
    public int saveMeasure(MeasureData measure) {
        if (measure != null) {
            try {
                db.execSQL("insert into tbl_measure(cllicheng,cldian,clren,cltime,gaocheng,shoulian,status,datatype,sources) values(?,?,?,?,?,?,?) ",
                        new String[]{measure.getSources()});
            } catch (Exception e) {
                Log.d("错误", e.getMessage().toString());
            }
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 将DownloadTask存储到数据库。
     */
    public int saveTaskInfo(TaskInfo downLoadData) {
        if (downLoadData != null) {
            try {
                db.execSQL("insert into tbl_task(taskId,userId,taskType,measureType," +
                                "startTime,endTime,proName,section,mileageLabel,mileageId," +
                                "pointLabel,pointId,initialValue) values(?,?,?,?,?,?,?,?,?,?,?,?,?) ",
                        new String[]{downLoadData.getTaskId(), downLoadData.getUserId(), downLoadData.getTaskType(),
                                downLoadData.getMeasureType(), downLoadData.getStartTime(), downLoadData.getEndTime(),
                                downLoadData.getDetailData().getProName(), downLoadData.getDetailData().getSection(),
                                downLoadData.getDetailData().getMileageLabel(), downLoadData.getDetailData().getMileageId(),
                                downLoadData.getDetailData().getPointLabel(), downLoadData.getDetailData().getPointId()
                                , downLoadData.getDetailData().getInitialValue()});
            } catch (Exception e) {
                Log.d("错误", e.getMessage().toString());
            }
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 从数据库读取任务信息。
     */
    public List<TaskInfo> loadTasks() {
        List<TaskInfo> list = new ArrayList<TaskInfo>();
        Cursor cursor = db
                .query("tbl_task", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                TaskInfo downLoadData = new TaskInfo();
                downLoadData.setId(cursor.getInt(cursor.getColumnIndex("id")));
                downLoadData.setTaskId(cursor.getString(cursor.getColumnIndex("taskId")));
                downLoadData.setUserId(cursor.getString(cursor.getColumnIndex("userId")));
                downLoadData.setTaskType(cursor.getString(cursor.getColumnIndex("taskType")));
                downLoadData.setMeasureType(cursor.getString(cursor.getColumnIndex("measureType")));
                downLoadData.setStartTime(cursor.getString(cursor.getColumnIndex("startTime")));
                downLoadData.setEndTime(cursor.getString(cursor.getColumnIndex("endTime")));
                downLoadData.getDetailData().setProName(cursor.getString(cursor.getColumnIndex("proName")));
                downLoadData.getDetailData().setSection(cursor.getString(cursor.getColumnIndex("section")));
                downLoadData.getDetailData().setMileageLabel(cursor.getString(cursor.getColumnIndex("mileageLabel")));
                downLoadData.getDetailData().setMileageId(cursor.getString(cursor.getColumnIndex("mileageId")));
                downLoadData.getDetailData().setPointLabel(cursor.getString(cursor.getColumnIndex("pointLabel")));
                downLoadData.getDetailData().setPointId(cursor.getString(cursor.getColumnIndex("pointId")));
                downLoadData.getDetailData().setInitialValue(cursor.getString(cursor.getColumnIndex("initialValue")));
                list.add(downLoadData);
            } while (cursor.moveToNext());
        }
        return list;
    }
}