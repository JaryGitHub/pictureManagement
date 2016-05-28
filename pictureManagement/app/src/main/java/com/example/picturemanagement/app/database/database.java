package com.example.picturemanagement.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by mycomputer on 2016/5/28.
 */
public class database extends SQLiteOpenHelper {

    private Context context;
    public database(Context context)
    {
        super(context,"my.db",null,1);//初始化数据库  名字为my
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table P_classification(P_url varchar(1000) primary key,P_class varchar(1000))";//创建表P_classification图片的url为主键
        String sql_1 = "create table classification(class varchar(100) primary key)";//创建表classification分类为主键
        try{
            db.execSQL(sql);
            db.execSQL(sql_1);//创建表
        }catch (Exception e)
        {
            Toast.makeText(context,"数据库创建失败",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
