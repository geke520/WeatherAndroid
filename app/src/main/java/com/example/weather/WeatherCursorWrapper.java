package com.example.weather;

import android.database.Cursor;
import android.database.CursorWrapper;

public class WeatherCursorWrapper extends CursorWrapper {
    public WeatherCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    // 从数据库中获取一条基本天气信息
    public Weather getWeather(){
        if(getWrappedCursor() == null)    return null;
        String day = getString(getColumnIndex(WeatherDbSchema.BasicWeatherTable.Cols.DAY));
        String date = getString(getColumnIndex(WeatherDbSchema.BasicWeatherTable.Cols.DATE));
        String week = getString(getColumnIndex(WeatherDbSchema.BasicWeatherTable.Cols.WEEK));
        String wea = getString(getColumnIndex(WeatherDbSchema.BasicWeatherTable.Cols.WEA));
        String weaImg = getString(getColumnIndex(WeatherDbSchema.BasicWeatherTable.Cols.WEA_IMG));
        String airLevel = getString(getColumnIndex(WeatherDbSchema.BasicWeatherTable.Cols.AIR_LEVEL));
        String tem1 = getString(getColumnIndex(WeatherDbSchema.BasicWeatherTable.Cols.TEM1));
        String tem2 = getString(getColumnIndex(WeatherDbSchema.BasicWeatherTable.Cols.TEM2));
        String tem = getString(getColumnIndex(WeatherDbSchema.BasicWeatherTable.Cols.TEM));

        Weather weather = new Weather(day, date, week, wea);
        weather.setAirLevel(airLevel);
        weather.setWeaImg(weaImg);
        weather.setTem1(tem1);
        weather.setTem2(tem2);
        weather.setTem(tem);

        return weather;
    }

    // 从数据库中获取一天内一个小时段的天气
    public HourWeather getHourWeather(){
        if(getWrappedCursor() == null)    return null;
        String temp = getString(getColumnIndex(WeatherDbSchema.HourTable.Cols.TEM));
        String day = getString(getColumnIndex(WeatherDbSchema.HourTable.Cols.DAY));
        String wea = getString(getColumnIndex(WeatherDbSchema.HourTable.Cols.WEA));

        return new HourWeather(day, wea, temp);
    }

    // 从数据库中获取一天的几条生活指数信息
    public LifeTip getLifeTip(){
        if(getWrappedCursor() == null)    return null;
        String title = getString(getColumnIndex(WeatherDbSchema.LifeTipsTable.Cols.TITLE));
        String desc = getString(getColumnIndex(WeatherDbSchema.LifeTipsTable.Cols.DESC));
        return new LifeTip(title, desc);
    }

}
