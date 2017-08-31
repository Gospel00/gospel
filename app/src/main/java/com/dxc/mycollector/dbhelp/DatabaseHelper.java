package com.dxc.mycollector.dbhelp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gospel on 2017/8/18.
 * DatabaseHelper
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    static String name = "user.db";
    static int dbVersion = 1;
    private static final String mDatabasename = "tunnel_data";
    private static SQLiteDatabase.CursorFactory mFactory = null;
    private static final int mVersion = 1;

    public DatabaseHelper(Context context) {
        super(context, mDatabasename, mFactory, mVersion);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
    }

    public static final String TABLE_NAME1 = "userinfo"; //用户信息
    public static final String TABLE_NAME2 = "downloadinfo"; //文件下载信息
    public static final String TABLE_NAME3 = "measuredata"; //解析后的测量信息

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
        createDownloadinfo(db);
        createMeasure(db);
    }

    /**
     * 创建用户表
     *
     * @param db
     */
    public void createUser(SQLiteDatabase db) {
        //创建用户信息表
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME1 + " (id integer primary key autoincrement,username varchar(20),password varchar(20),age integer,sex varchar(2),phone varchar(20),address varchar(200))";
        db.execSQL(sql);
    }

    /**
     * 创建任务下载表
     * @param db
     */
    public void createDownloadinfo(SQLiteDatabase db) {
        //创建任务下载信息数据表
        String downloadsql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME2 + " ("
                + "id integer primary key autoincrement, "
                + "cllicheng VARCHAR, "
                + "cldian VARCHAR, "
                + "clren VARCHAR, "
                + "cltime VARCHAR, "
                + "gaocheng VARCHAR, "
                + "shoulian VARCHAR, "
                + "status VARCHAR"
                + ")";
        db.execSQL(downloadsql);
    }


    /**
     * 解析测量数据
     * @param db
     */
    public void createMeasure(SQLiteDatabase db) {
        //创建用户信息表
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME3 + " (id integer primary key autoincrement,sources varchar(2000),author varchar(100),datatime varchar(100),gaocheng varchar(100),shoulian varchar(100))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
