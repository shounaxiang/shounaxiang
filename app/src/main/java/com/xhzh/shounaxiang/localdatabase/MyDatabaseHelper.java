package com.xhzh.shounaxiang.localdatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by wjsay on 2018/10/24
 * Describe:
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context mContext;

    public static final String CREATE_GOODS = "create table Goods ("
            + "Goods_id int primary key,"
            + "User_id text,"
            + "Space_name text,"
            + "Goods_name text,"
            + "Goods_img text,"
            + "Goods_path text,"
            + "Space_id text,"
            + "Goods_price text,"
            + "Goods_stars text,"
            + "Goods_num text,"
            + "Goods_color text,"
            + "Goods_assort text,"
            + "Goods_season text"
            + ")";
    public static final String CREATE_SPACE = "create table Space ("
            + "Space_id int primary key,"
            + "Space_name text,"
            + "Space_img text,"
            + "Space_belong text,"
            + "Space_level text,"
            + "User_id"
            + ")";


    public MyDatabaseHelper (Context context, String name,
                             SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists Goods");
        sqLiteDatabase.execSQL("drop table if exists Space");
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_GOODS);
        db.execSQL(CREATE_SPACE);
        Toast.makeText(mContext, "本地数据库创建成功", Toast.LENGTH_SHORT).show();
    }
}
