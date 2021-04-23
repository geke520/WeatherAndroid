package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends SingleFragmentActivity
        implements WeatherListFragment.Callbacks {

    private ImageView mWeatherTypeView;
    private TextView mDateTextView;
    private TextView mMaxTempTextView;
    private TextView mMinTempTextView;
    private TextView mWeatherTypeTextView;
    private SQLiteDatabase mDatabase;
    private Weather mToday;
    private MyUtil mMyUtil;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // 连接数据库
        mDatabase = new WeatherBaseHelper(MainActivity.this).getReadableDatabase();
        mMyUtil = (MyUtil)getApplication();

        if(findViewById(R.id.detail_fragment_container) == null){
            // 若是手机版面则上部视图应该被应用
            mDateTextView = findViewById(R.id.master_date_text_view);
            mMaxTempTextView = findViewById(R.id.master_maxtemp_text_view);
            mMinTempTextView = findViewById(R.id.master_mintemp_text_view);
            mWeatherTypeTextView = findViewById(R.id.master_weather_type_text_view);
            mWeatherTypeView = findViewById(R.id.master_weather_type_icon);
        }

    }


    @Override
    protected Fragment createFragment() {
        WeatherListFragment fragment = new WeatherListFragment();
        fragment.setInfoChangedListener(
                new WeatherListFragment.InfoChangedListener() {
            @Override
            public void onInfoChanged(boolean isCityChanged) {
                if(isCityChanged){
                    getTodayWeather();
                }
                updateUI();
            }
        });
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    private void getTodayWeather(){
        mToday = null;
        Cursor cursor = mDatabase.query(WeatherDbSchema.BasicWeatherTable.NAME,
                null, WeatherDbSchema.BasicWeatherTable.Cols.CITY_NAME + "=?",
                new String[]{mMyUtil.getCityName()}, null, null, null);
        WeatherCursorWrapper wrapper = new WeatherCursorWrapper(cursor);
        try{
            wrapper.moveToFirst();
            if(!wrapper.isAfterLast()) {
                mToday = wrapper.getWeather();
            }
        }finally {
            wrapper.close();
        }
        if(mToday == null){
            mToday = new Weather();
        }
    }

    public void updateUI(){
        // 当为手机版面时才去更新
        if(findViewById(R.id.detail_fragment_container) == null){
            MyUtil.setImage(mToday.getWeaImg(), mWeatherTypeView);
            mWeatherTypeTextView.setText(mToday.getWea());
            mMaxTempTextView.setText(MyUtil.handleTmeprature(mToday.getTem1(), mMyUtil.isFahrenheit()));
            mMinTempTextView.setText(MyUtil.handleTmeprature(mToday.getTem2(), mMyUtil.isFahrenheit()));
            mDateTextView.setText(MyUtil.handleDate(mToday.getDate()) + " 今天");
        }
    }

    @Override
    public void onWeatherSelected(String date) {
        if(findViewById(R.id.detail_fragment_container) == null){
            // 若是手机,启动另外一个Activity;
            Intent intent = DetailActivity.newIntent(MainActivity.this, date);
            startActivity(intent);
        }else{
            // 若是平板,detailFragment显示详细数据
            Fragment newDetail = DetailFragment.newInstance(date);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }
}
