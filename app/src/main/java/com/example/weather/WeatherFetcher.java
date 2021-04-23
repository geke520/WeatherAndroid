package com.example.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WeatherFetcher {

    // appid：84311488    appsecret：7huPWCF2
    private static final String APPID = "84311488";
    private static final String APPSECRET = "7huPWCF2";
    private SQLiteDatabase mDatabase;
    private Context mContext;

    public WeatherFetcher(Context context){
        // 获取操作数据库
        mContext = context.getApplicationContext();
        mDatabase = new WeatherBaseHelper(mContext).getWritableDatabase();
    }

    byte[] getApiBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        // 创建需要访问的URL的连接对象
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            //此处真正连接到URL
            InputStream in = connection.getInputStream();
            // 判断是否连接成功
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                return null;
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0){
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            in.close();

            //返回数据
            return out.toByteArray();
        }finally {
            //关闭连接
            connection.disconnect();
        }
    }

    public String getUrl(String urlSec) throws IOException{
        return new String(getApiBytes(urlSec));
    }

    public boolean fetchItems(String cityName){
        // Todo: 写入数据到数据库
        try{
            String url = Uri.parse("https://www.tianqiapi.com/api")
                    .buildUpon()
                    .appendQueryParameter("version", "v1")
                    .appendQueryParameter("appid", APPID)
                    .appendQueryParameter("appsecret", APPSECRET)
                    .appendQueryParameter("city", cityName)
                    .build().toString();
            String jsonString = getUrl(url);
            Log.i("Test", jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            //解析Json数据
            parseItems(jsonBody);
            return true;
        }catch (IOException ie){
            Log.i("Test", "Get weather info failed");
            return false;
        }
        catch (JSONException e){
            Log.e("JSON","Failed to parse JSON",e);
            return false;
        }
    }


//    private List<Weather> parseItems(JSONObject jsonObject) throws JSONException{
//
//    }
//
//


    private void parseItems(JSONObject jsonBody) throws JSONException {

        //获取日期
        JSONArray weather_dataList = jsonBody.getJSONArray("data");
        Log.i("Test", String.valueOf(weather_dataList.length()));
        //获取城市名称
        String cityName = jsonBody.getString("city");
        Log.i("Test", cityName);

        // 先将原来的信息统一删除再重新插入
        mDatabase.delete(WeatherDbSchema.BasicWeatherTable.NAME,
                WeatherDbSchema.BasicWeatherTable.Cols.CITY_NAME + "=?",
                new String[]{cityName});
        for(int i = 0; i < weather_dataList.length(); i++){
            // 基本信息
            JSONObject weatherObject = (JSONObject) weather_dataList.get(i);
            String date = weatherObject.getString("date");
            insertWeatherValues(weatherObject, cityName);

            // 每小时段信息
            JSONArray hours = weatherObject.getJSONArray("hours");
            // 删除原来的小时段信息
            mDatabase.delete(WeatherDbSchema.HourTable.NAME,
                    WeatherDbSchema.HourTable.Cols.CITY_NAME + "=? "
                            + "and " + WeatherDbSchema.HourTable.Cols.DATE + "=?",
                    new String[]{cityName, date});
            for(int j = 0; j < hours.length(); j++){
                JSONObject hourObject = (JSONObject) hours.get(j);
                insertHourWeatherValues(hourObject, date, cityName);
            }

            // 生活指数信息
            JSONArray index = weatherObject.getJSONArray("index");
            if(index.length() == 6){
                // 删除原来的生活指数信息
                mDatabase.delete(WeatherDbSchema.LifeTipsTable.NAME,
                        WeatherDbSchema.LifeTipsTable.Cols.CITY_NAME + "=? "
                                + "and " + WeatherDbSchema.LifeTipsTable.Cols.DATE + "=?",
                        new String[]{cityName, date});
                insertLifeTipsValues(index.getJSONObject(0), date, cityName);
                insertLifeTipsValues(index.getJSONObject(3), date, cityName);
                insertLifeTipsValues(index.getJSONObject(5), date, cityName);
            }

            /**
            Weather weather = new Weather();
            weather.setDay(date);
            weather.setDate(weatherObject.getString("date"));
            weather.setWeek(weatherObject.getString("week"));
            weather.setWea(weatherObject.getString("wea"));
            weather.setWeaImg(weatherObject.getString("wea_img"));
            weather.setAirLevel(weatherObject.getString("air_level"));
            weather.setAirTips(weatherObject.getString("air_tips"));
            weather.setTem1(weatherObject.getString("tem1"));
            weather.setTem2(weatherObject.getString("tem2"));
            weather.setTem(weatherObject.getString("tem"));
            weather.setWinSpeed(weatherObject.getString("win_speed"));
            JSONArray hours = weatherObject.getJSONArray("hours");
            **/
            // 小时信息
            /*
            List<HourWeather> hourWeathers = new ArrayList<>();
            for(int j = 0; j < hours.length(); j++){
                JSONObject hourObject = (JSONObject) hours.get(i);

            }
            weather.setHours(hourWeathers);
            */

            // 生活指数信息
        }
    }

    private boolean insertWeatherValues(JSONObject jsonObject, String cityName){
        try {
            ContentValues values = new ContentValues();
            values.put(WeatherDbSchema.BasicWeatherTable.Cols.CITY_NAME, cityName);
            values.put(WeatherDbSchema.BasicWeatherTable.Cols.DAY, jsonObject.getString("day"));
            values.put(WeatherDbSchema.BasicWeatherTable.Cols.DATE, jsonObject.getString("date"));
            values.put(WeatherDbSchema.BasicWeatherTable.Cols.WEEK, jsonObject.getString("week"));
            values.put(WeatherDbSchema.BasicWeatherTable.Cols.WEA, jsonObject.getString("wea"));
            values.put(WeatherDbSchema.BasicWeatherTable.Cols.WEA_IMG, jsonObject.getString("wea_img"));
            //values.put(WeatherDbSchema.BasicWeatherTable.Cols.AIR_LEVEL, jsonObject.getString("air_level"));
            //values.put(WeatherDbSchema.BasicWeatherTable.Cols.AIR_TIPS, jsonObject.getString("air_tips"));
            values.put(WeatherDbSchema.BasicWeatherTable.Cols.TEM1, jsonObject.getString("tem1"));
            values.put(WeatherDbSchema.BasicWeatherTable.Cols.TEM2, jsonObject.getString("tem2"));
            values.put(WeatherDbSchema.BasicWeatherTable.Cols.TEM, jsonObject.getString("tem"));
            values.put(WeatherDbSchema.BasicWeatherTable.Cols.WIN_SPEED, jsonObject.getString("win_speed"));
            mDatabase.insert(WeatherDbSchema.BasicWeatherTable.NAME, null, values);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    private boolean insertHourWeatherValues(JSONObject jsonObject, String date, String cityName){
        try {
            ContentValues values = new ContentValues();
            values.put(WeatherDbSchema.HourTable.Cols.CITY_NAME, cityName);
            values.put(WeatherDbSchema.HourTable.Cols.DATE, date);
            Log.i("Test", "插入数据库小时天气日期" +cityName + date +  jsonObject.getString("day"));
            values.put(WeatherDbSchema.HourTable.Cols.DAY, jsonObject.getString("day"));
            values.put(WeatherDbSchema.HourTable.Cols.WEA, jsonObject.getString("wea"));
            values.put(WeatherDbSchema.HourTable.Cols.TEM, jsonObject.getString("tem"));
            values.put(WeatherDbSchema.HourTable.Cols.WIN, jsonObject.getString("win"));
            values.put(WeatherDbSchema.HourTable.Cols.WIN_SPEED, jsonObject.getString("win_speed"));
            mDatabase.insert(WeatherDbSchema.HourTable.NAME, null, values);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }


    private boolean insertLifeTipsValues(JSONObject jsonObject, String date, String cityName){
        try {
            // 插入新数据
            ContentValues values = new ContentValues();
            values.put(WeatherDbSchema.LifeTipsTable.Cols.CITY_NAME, cityName);
            values.put(WeatherDbSchema.LifeTipsTable.Cols.DATE, date);
            values.put(WeatherDbSchema.LifeTipsTable.Cols.TITLE, jsonObject.getString("title"));
            values.put(WeatherDbSchema.LifeTipsTable.Cols.LEVEL, jsonObject.getString("level"));
            values.put(WeatherDbSchema.LifeTipsTable.Cols.DESC, jsonObject.getString("desc"));
            mDatabase.insert(WeatherDbSchema.LifeTipsTable.NAME, null, values);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }






}
