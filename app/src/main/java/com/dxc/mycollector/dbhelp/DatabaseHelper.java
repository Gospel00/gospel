package com.dxc.mycollector.dbhelp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gospel on 2017/8/18.
 * DatabaseHelper
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    static String name="user.db";
    static int dbVersion=1;
    private static final String mDatabasename = "filedownloader";
    private static SQLiteDatabase.CursorFactory mFactory = null;
    private static final int mVersion = 1;
    public DatabaseHelper(Context context) {
        super(context, mDatabasename, mFactory, mVersion);
    }
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
    }

    public static final String TABLE_NAME = "downloadinfo"; //文件下载信息数据表名称

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
    }

    /**
     * 创建任务下载表
     * @param db
     */
    public void createDownloadinfo(SQLiteDatabase db) {
        //创建任务下载信息数据表
        String downloadsql = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("
                + "id integer primary key autoincrement, "
                + "userID VARCHAR, "
                + "taskID VARCHAR, "
                + "url VARCHAR, "
                + "filePath VARCHAR, "
                + "fileName VARCHAR, "
                + "fileSize VARCHAR, "
                + "downLoadSize VARCHAR "
                + ")";
        db.execSQL(downloadsql);
    }

    /**
     * 创建用户表
     * @param db
     */
    public void createUser(SQLiteDatabase db) {
        //创建用户信息表
        String sql="create table user(id integer primary key autoincrement,username varchar(20),password varchar(20),age integer,sex varchar(2),phone varchar(20),address varchar(200))";
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
