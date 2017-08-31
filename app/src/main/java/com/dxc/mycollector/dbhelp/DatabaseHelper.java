package com.dxc.mycollector.dbhelp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dxc.mycollector.ExamineRecord;
import com.dxc.mycollector.logs.Logger;

/**
 * Created by gospel on 2017/8/18.
 * DatabaseHelper
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    static String TAG = DatabaseHelper.class.getSimpleName();
    static String name = "user.db";
    static int dbVersion = 1;
    private static final String mDatabasename = "tunnel_data_db";
    private static SQLiteDatabase.CursorFactory mFactory = null;
    private static final int mVersion = 1;

    public DatabaseHelper(Context context) {
        super(context, mDatabasename, mFactory, mVersion);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
        Logger.i(TAG, "DatabaseHelper init success.");
    }

    public static final String TABLE_NAME1 = "tbl_users"; //用户信息
    public static final String TABLE_NAME2 = "tbl_task"; //任务下载信息
    public static final String TABLE_NAME3 = "tbl_measure"; //解析后的测量信息

//    public SQLiteHelper(Context context) {
//        super(context, mDatabasename, mFactory, mVersion);
//    }
//
//    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
//                        int version) {
//        super(context, name, factory, version);
//    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createUser(db);
        Logger.i(TAG, "tbl_users create success.");
        createDownloadinfo(db);
        Logger.i(TAG, "tbl_task create success.");
        createMeasure(db);
        Logger.i(TAG, "tbl_measure create success.");
    }

    /**
     * 创建用户表
     *
     * @param db
     */
    public void createUser(SQLiteDatabase db) {

        //创建用户信息表
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME1 +
                " (id integer primary key autoincrement," +
                "username varchar(20)," +
                "password varchar(20)," +
                "age integer," +
                "sex varchar(2)," +
                "phone varchar(20)," +
                "address varchar(200))";
        try {
            db.execSQL(sql);
        } catch (Exception e) {
            Logger.i(TAG, "tbl_users create failed." + e.getMessage());
        }
    }

    /**
     * 创建任务下载表
     *
     * @param db
     */
    public void createDownloadinfo(SQLiteDatabase db) {
        //创建任务下载信息数据表
        String downloadsql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME2 + " ("
                + "id integer primary key autoincrement, "
                + "taskId VARCHAR, "
                + "userId VARCHAR, "
                + "taskType VARCHAR, "
                + "measureType VARCHAR, "
                + "startTime VARCHAR, "
                + "endTime VARCHAR, "
                + "proName VARCHAR,"
                + "section VARCHAR,"
                + "mileageLabel VARCHAR,"
                + "mileageId VARCHAR,"
                + "pointLabel VARCHAR,"
                + "pointId VARCHAR,"
                + "initialValue VARCHAR"
                + ")";
        try {
            db.execSQL(downloadsql);
        } catch (Exception e) {
            Logger.i(TAG, "tbl_task create failed." + e.getMessage());
        }
    }


    /**
     * 解析测量数据
     *
     * @param db
     */
    public void createMeasure(SQLiteDatabase db) {
        //创建用户信息表
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME3
                + " (id integer primary key autoincrement," +
                "cllicheng varchar(2000)," +
                "cldian varchar(2000)," +
                "clren varchar(2000)," +
                "cltime varchar(2000)," +
                "gaocheng varchar(2000)," +
                "shoulian varchar(2000)," +
                "status varchar(100)," +
                "datatype varchar(100)," +
                "sources varchar(100))";
        try {
            db.execSQL(sql);
        } catch (Exception e) {
            Logger.i(TAG, "tbl_measure create failed." + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
