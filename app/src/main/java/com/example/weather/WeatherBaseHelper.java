package com.example.weather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WeatherBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "weatherBase.db";
    private static final int VERSION = 1;

    public  WeatherBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //存储各个城市七天的基本天气信息
        sqLiteDatabase.execSQL(WeatherDbSchema.BasicWeatherTable.CREATE);
        //存储各个城市每一天的每小时段天气信息
        sqLiteDatabase.execSQL(WeatherDbSchema.HourTable.CREATE);
        //存储各个城市每一天的生活指数信息
        sqLiteDatabase.execSQL(WeatherDbSchema.LifeTipsTable.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
