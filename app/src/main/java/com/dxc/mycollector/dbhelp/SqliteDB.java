package com.dxc.mycollector.dbhelp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Measure;
import android.util.Log;


import com.dxc.mycollector.model.MeasureData;
import com.dxc.mycollector.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gospel on 2017/8/18.
 * DatabaseHelper
 */

public class SqliteDB {
    /**
     * 数据库名
     */
    public static final String DB_NAME = "sqlite_dbname";
    /**
     * 数据库版本
     */
    public static final int VERSION = 1;

    private static SqliteDB sqliteDB;

    private SQLiteDatabase db;

    private SqliteDB(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取SqliteDB实例
     *
     * @param context
     */
    public synchronized static SqliteDB getInstance(Context context) {
        if (sqliteDB == null) {
            sqliteDB = new SqliteDB(context);
        }
        return sqliteDB;
    }

    /**
     * 将User实例存储到数据库。
     */
    public int saveUser(User user) {
        if (user != null) {
           /* ContentValues values = new ContentValues();
            values.put("username", user.getUsername());
            values.put("userpwd", user.getUserpwd());
            db.insert("User", null, values);*/

            Cursor cursor = db.rawQuery("select * from User where username=?", new String[]{user.getuName().toString()});
            if (cursor.getCount() > 0) {
                return -1;
            } else {
                try {
                    db.execSQL("insert into User(username,password) values(?,?) ", new String[]{user.getuName().toString(), user.getuPwd().toString()});
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
                .query("User", null, null, null, null, null, null);
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

    public int Quer(String pwd, String name) {
        HashMap<String, String> hashmap = new HashMap<String, String>();
        Cursor cursor = db.rawQuery("select * from User where username=?", new String[]{name});
        // hashmap.put("name",db.rawQuery("select * from User where name=?",new String[]{name}).toString());
        if (cursor.getCount() > 0) {
            Cursor pwdcursor = db.rawQuery("select * from User where password=? and username=?", new String[]{pwd, name});
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
                db.execSQL("insert into measuredata(clren,cltime,gaocheng,shoulian,dataType,status,) values(?,?,?,?,?,?,?) ",
                        new String[]{measure.getClren(),measure.getCltime(),measure.getGaocheng(),measure.getShoulian(),"1","1",measure.getSources()});
            } catch (Exception e) {
                Log.d("错误", e.getMessage().toString());
            }
            return 1;
        } else {
            return 0;
        }
    }
}