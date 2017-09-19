package com.dxc.mycollector.dbhelp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.dxc.mycollector.logs.Logger;

import java.io.File;

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
//        DatabaseContext dbContext = new DatabaseContext(context);
        super(context, getMyDatabaseName(name), factory, version);
        Logger.i(TAG, "DatabaseHelper init success.");
    }

    private static String getMyDatabaseName(String name) {
        String databasename = name;
        boolean isSdcardEnable = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {//SDCard是否插入
            isSdcardEnable = true;
        }
        String dbPath = null;
        if (isSdcardEnable) {
            dbPath = Environment.getExternalStorageDirectory().getPath() + "/Tunnel/database/";
        } else {//未插入SDCard，建在内存中
            Logger.i(TAG, "没有内存卡，数据库无法创建");
        }
        File dbp = new File(dbPath);
        if (!dbp.exists()) {
            dbp.mkdirs();
            Logger.i(TAG, "创建数据库存放目录：" + dbPath);
        }
        databasename = dbPath + databasename;
        return databasename;
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

//        formatTable(db);
    }

    public void formatTable(SQLiteDatabase db) {
        try {
            db.execSQL("DELETE FROM tbl_users");
            Logger.i(TAG, "tbl_users format success.");
            db.execSQL("DELETE FROM tbl_task");
            Logger.i(TAG, "tbl_task format success.");
            db.execSQL("DELETE FROM tbl_measure");
            Logger.i(TAG, "tbl_measure format success.");
        } catch (Exception e) {
            Logger.e(TAG, "format table failed." + e.getMessage());
        }
    }

    /**
     * 创建用户表
     *
     * @param db
     */
    public void createUser(SQLiteDatabase db) {

        //创建用户信息表

        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME1 +
                "(id integer primary key autoincrement," +
                "username varchar(20)," +
                "password varchar(20)," +
                "repassword varchar(20)," +
                "phone varchar(20)," +
                "realname varchar(20)," +
                "idcard varchar(20)," +
                "address varchar(20)," +
                "gongdian varchar(20))";
        try {
            db.execSQL(sql);
            db.execSQL("insert into tbl_users(username,password,repassword,phone,realname,idcard, address,gongdian) values(?,?,?,?,?,?,?,?) ",
                    new String[]{"gospel", "gospel5200","gospel5200","11","gospel","1111","beijing","1"});
        } catch (Exception e) {
            Logger.e(TAG, "tbl_users create failed." + e.getMessage());
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
                + "initialValue VARCHAR,"
                + "status VARCHAR,"
                + "sjz VARCHAR,"
                + "cz VARCHAR)";
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
                "taskId  varchar(100)," +
                "cllicheng varchar(2000)," +
                "cldian varchar(2000)," +
                "cllichengId varchar(2000)," +
                "cldianId varchar(2000)," +
                "clren varchar(100)," +
                "cltime varchar(50)," +
                "gaocheng varchar(100)," +
                "shoulian varchar(100)," +
                "status varchar(100)," +
                "datatype varchar(100)," +
                "createtime varchar(50)," +
                "updatetime varchar(50)," +
                "sources varchar(1000)," +
                "chushizhi varchar(50)," +
                "chazhi varchar(50))";
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
