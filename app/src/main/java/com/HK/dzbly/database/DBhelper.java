package com.HK.dzbly.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/8/30$
 * 描述： 数据库的创建和定义增删改查操作
 * 修订历史：
 */
public class DBhelper extends SQLiteOpenHelper {

    public static String DZBLY_TABLE = "DZBLY";//地质编录仪数据存储表
    public static String DATA_TABLE = "DATA";//照片和视频数据存储的表
    public static final String db_name = "cqhk.db"; //数据库名称
    public static final String DB_NAME = "/data/data/com.HK.dzbly/databases/cqhk.db"; //数据库的存储地址

    //带全部参数的构造函数，此构造函数必不可少
    public DBhelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, db_name, factory, 1);
    }

    //带两个参数的构造函数，调用的其实是带三个参数的构造函数
    public DBhelper(Context context, String name) {
        this(context, name, 1);
    }

    //带三个参数的构造函数，调用的是带所有参数的构造函数
    public DBhelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    //创建数据库
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w("create a Database", "create a Database");
        //创建数据库表sql语句 并 执行
        String sql = "CREATE TABLE IF NOT EXISTS DZBLY(Did INTEGER PRIMARY KEY AUTOINCREMENT," +
                "[CreatedTime] TimeStamp NOT NULL DEFAULT (datetime('now','localtime')),name text not null,val text ,rollAngle text,elevation text,type text not null,result text)";
        db.execSQL(sql);
    }

    //根据传参创建表
    public void CreateTable(Context context, String table) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_NAME, null);
        String sql = "CREATE TABLE " + table + " (type text not null,[CreatedTime] TimeStamp NOT NULL DEFAULT (datetime('now','localtime')),name text PRIMARY KEY" + ");";
        try {
            db.execSQL(sql);
        } catch (Exception e) {
            e.getStackTrace();
        }
        db.close();
    }

    //插入数据
    public void Insert(Context context, String table, ContentValues values) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                DB_NAME, null);
        try {
            db.insert(table, null, values);
        } catch (Exception e) {
            e.getStackTrace();
        }
        Log.i("插入数据", "数据插入成功");
        db.close();
    }

    //删除表
    public void DeleteTable(Context context, String table) {

        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                DB_NAME, null);
        String sql = "drop table " + table;
        try {
            db.execSQL(sql);
        } catch (Exception e) {
            e.getStackTrace();
        }
        db.close();
    }

    //判断表是否存在
    public boolean IsTableExist(String table) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                DB_NAME, null);
        boolean isTableExist = true;
        Cursor cursor = db.rawQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='" + table + "'", null);
        int learned = 0;
        if (cursor.moveToFirst()) {
            learned = cursor.getInt(0);
        }
        if (learned == 0) {
            isTableExist = false;
        }
        cursor.close();
        db.close();
        return isTableExist;
    }

    //查询表数据
    public Cursor Query(Context context, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        //columns 列名称  selection where后面跟的字符串 selectionArgs 就是对应selection中的问号所代表的变量，使得where条件可以动态赋值
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                DB_NAME, null);
        //context.getDatabasePath(DATABASE_NAME).getPath()
        Cursor cursor = null;
        try {
            cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        } catch (Exception e) {
            e.getStackTrace();
        }
        //db.close();
        return cursor;
    }

    //删除表数据
    public void Delete(Context context, String table, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                DB_NAME, null);
        try {
            db.delete(table, whereClause, whereArgs);
        } catch (Exception e) {
            e.getStackTrace();
        }
        db.close();
    }

    //修改表数据
    public void Update(Context context, String table, ContentValues values, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                DB_NAME, null);
        try {
            db.update(table, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.getStackTrace();
        }
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
