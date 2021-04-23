package com.example.weather;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Field;

public class MyUtil extends Application {

    private boolean mIsFahrenheit;        // 标记温度类型
    private String mCityName = "长沙";    // 当前城市
    private boolean mNoteIsOpened;        // 是否开启通知


    public boolean isFahrenheit() {
        return mIsFahrenheit;
    }

    public void setFahrenheit(boolean fahrenheit) {
        mIsFahrenheit = fahrenheit;
    }

    public String getCityName() {
        return mCityName;
    }

    public void setCityName(String cityName) {
        mCityName = cityName;
    }

    public boolean isNoteIsOpened() {
        return mNoteIsOpened;
    }

    public void setNoteIsOpened(boolean noteIsOpened) {
        mNoteIsOpened = noteIsOpened;
    }

    public static void setImage(String imgName, ImageView imageView){
        Class drawable = R.drawable.class;
        try {
            Field mField = drawable.getField(imgName);
            int id = mField.getInt(mField.getName());
            imageView.setImageResource(id);
        } catch (Exception e) {
            Log.e("Test", "获取天气图片出错", e);
        }
    }

    /**
     * 将日期格式为XX日（周XX）转化为周XX
     * @param day 初始日期
     * @return 周XX
     */
    public static String handleDay(String day){
        int len = day.length();
        return day.substring(len - 3, len - 1);
    }

    /**
     * 将日期格式为XXXX-XX-XX转换为XX月XX日
     * @param date: XXXX-XX-XX日期格式
     * @return: XX月XX日格式
     */
    public static String handleDate(String date){
        String[] ds = date.split("-");
        return ds[1] + "月" + ds[2] + "日";
    }

    /**
     * 若isFahrenheit为true,返回华氏温度;否则,返回摄氏温度
     * @param temp
     * @return
     */
    public static String handleTmeprature(String temp, boolean isFahrenheit){
        if(temp == null)    return null;
        if(!isFahrenheit)   return temp;
        int c = Integer.parseInt(temp.substring(0, temp.length() - 1));
        // 转换为华氏温度℉
        int f = (int)(c * 1.8 + 32);
        return String.valueOf(f) + "℉";
    }

    /**
     * BD-09坐标转换为GCJ-02坐标
     * @return
     */
    public static LatLng BD2GCJ(LatLng bd){
        double x = bd.longitude - 0.0065, y = bd.latitude - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * Math.PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * Math.PI);
        double lng = z * Math.cos(theta);
        double lat = z * Math.sin(theta);
        return new LatLng(lat, lng);
    }

    public static LatLng GCJ2BD(LatLng bd){
        double x = bd.longitude, y = bd.latitude;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * Math.PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * Math.PI);
        double tempLon = z * Math.cos(theta) + 0.0065;
        double tempLat = z * Math.sin(theta) + 0.006;
        return new LatLng(tempLat, tempLon);
    }

    // 开启通知权限
    public static boolean isNotificationEnabled(Context context){
        return NotificationManagerCompat.from(context.getApplicationContext())
                .areNotificationsEnabled();
    }

    public static void openNotification(Activity activity){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.getPackageName());
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, activity.getApplicationInfo().uid);
            activity.startActivity(intent);
        }else{

        }
    }

    public void sendNotification(String tempHigh, String tempLow, String wea){
        // 获取对应图片资源
        int resId = getResources().getIdentifier(wea + ".png",
                "drawable", getPackageName());
        NotificationManager manager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        Notification notification =  new NotificationCompat.Builder(this,
                "notification")
                .setContentTitle("天气预报")
                .setContentText("预报: " + wea + " 最高: " + tempHigh + "最低: " + tempLow)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(resId).build();

    }

}
