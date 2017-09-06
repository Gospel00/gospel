package com.dxc.mycollector.dbhelp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.dxc.mycollector.logs.Logger;
import com.dxc.mycollector.model.TaskDetails;
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
    public static final String DB_NAME = "tunnel_data.db";
    /**
     * 数据库版本
     */
    public static final int VERSION = 1;

    private static SqliteUtils sqliteDB;

    private SQLiteDatabase db;

    public SqliteUtils(Context context) {
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
                    Log.d("保存用户信息错误", e.getMessage().toString());
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
                Cursor cursor = db.rawQuery("select * from tbl_measure where cldian=?", new String[]{measure.getCldian().toString()});
                if (cursor.getCount() <= 0) {
                    db.execSQL("insert into tbl_measure(cllicheng,cldian,clren,cltime,gaocheng," +
                                    "shoulian,status,datatype,sources,taskId,cllichengId,cldianId," +
                                    "createtime,updatetime) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?) ",
                            new String[]{measure.getCllicheng(), measure.getCldian(), measure.getClren(), measure.getCltime(), measure.getGaocheng(), measure.getShoulian(), measure.getStatus(), measure.getDataType(), measure.getSources(),"","","","",""});
                    return 1;
                }
                if(cursor.getCount() >0)
                {
                    return 1;
                }
                return 0;
            } catch (Exception e) {
                Log.d("保存测量信息异常：", e.getMessage().toString());
                return 0;
            }
        } else {
            return 0;
        }
    }
    /**
     * 将Measure存储到数据库。
     */
    public  List<MeasureData> queryMeasure() {
        List<MeasureData> list = new ArrayList<MeasureData>();
        try {
            Cursor cursor = db.rawQuery("select * from tbl_measure where status =?", new String[]{"1"});
            if (cursor.moveToFirst()) {
                do {
                    MeasureData downLoadData = new MeasureData();
                    downLoadData.setCllicheng(cursor.getString(cursor.getColumnIndex("cllicheng")));
                    downLoadData.setCldian(cursor.getString(cursor.getColumnIndex("cldian")));
                    downLoadData.setClren(cursor.getString(cursor.getColumnIndex("clren")));
                    downLoadData.setCltime(cursor.getString(cursor.getColumnIndex("cltime")));
                    downLoadData.setGaocheng(cursor.getString(cursor.getColumnIndex("gaocheng")));
                    downLoadData.setShoulian(cursor.getString(cursor.getColumnIndex("shoulian")));
                    downLoadData.setDataType(cursor.getString(cursor.getColumnIndex("datatype")));
                    downLoadData.setSources(cursor.getString(cursor.getColumnIndex("sources")));
                    list.add(downLoadData);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("保存测量信息异常：", e.getMessage().toString());
            return null;
        }
        return list;
    }

    /**
     * 将DownloadTask存储到数据库。
     */
    public int saveTaskInfo(TaskInfo downLoadData, TaskDetails taskDetails) {
        if (downLoadData != null) {
            try {
                Cursor cursor = db.rawQuery("select * from tbl_task where pointId=?", new String[]{taskDetails.getPointId().toString()});
                if (cursor.getCount() <= 0) {
                    db.execSQL("insert into tbl_task(taskId,userId,taskType,measureType," +
                                    "startTime,endTime,proName,section,mileageLabel,mileageId," +
                                    "pointLabel,pointId,initialValue) values(?,?,?,?,?,?,?,?,?,?,?,?,?) ",
                            new String[]{downLoadData.getTaskId(), downLoadData.getUserId(), downLoadData.getTaskType(),
                                    downLoadData.getMeasureType(), downLoadData.getStartTime(), downLoadData.getEndTime(),
                                    taskDetails.getProName(), taskDetails.getSection(),
                                    taskDetails.getMileageLabel(), taskDetails.getMileageId(),
                                    taskDetails.getPointLabel(), taskDetails.getPointId()
                                    , taskDetails.getInitialValue()});
                    return 1;
                }
            } catch (Exception e) {
                Logger.i(TAG, "保存任务信息异常：" + e.getMessage().toString());
            }
        }
        return 0;
    }

    /**
     * 从数据库读取任务信息。
     */
    public List<TaskInfo> loadTasks() {
        List<TaskInfo> list = new ArrayList<TaskInfo>();
        List<TaskDetails> listd = new ArrayList<TaskDetails>();
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

                //任务详情
                TaskDetails td = new TaskDetails();
                td.setProName(cursor.getString(cursor.getColumnIndex("proName")));
                td.setSection(cursor.getString(cursor.getColumnIndex("section")));
                td.setMileageLabel(cursor.getString(cursor.getColumnIndex("mileageLabel")));
                td.setMileageId(cursor.getString(cursor.getColumnIndex("mileageId")));
                td.setPointLabel(cursor.getString(cursor.getColumnIndex("pointLabel")));
                td.setPointId(cursor.getString(cursor.getColumnIndex("pointId")));
                td.setInitialValue(cursor.getString(cursor.getColumnIndex("initialValue")));
                listd.add(td);
                //任务详情
                downLoadData.setTaskDetail(td);
                list.add(downLoadData);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public int UpdateState(String taskIdS,TaskDetails td,String taskname,String nowtime,String gc,String sl)
    {
        if (td != null) {
            try {
                Cursor cursor = db.rawQuery("select * from tbl_measure where taskId=?", new String[]{taskIdS});
                if (cursor.getCount() <= 0) {
                    db.execSQL("insert into tbl_measure(cllicheng,cldian,clren,cltime,gaocheng," +
                                    "shoulian,status,datatype,sources,taskId,cllichengId,cldianId," +
                                    "createtime,updatetime) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?) ",
                            new String[]{td.getMileageLabel(), td.getPointLabel(),
                                    taskname, nowtime,gc.trim(), sl.trim(),"1", "0",
                                    "",taskIdS,nowtime,""});
                    return 1;
                }
                if(cursor.getCount() >0)
                {
                    return 1;
                }
                return 0;
            } catch (Exception e) {
                Log.d("保存信息异常：", e.getMessage().toString());
                return 0;
            }
        } else {
            return 0;
        }

        }


}