package com.example.weather;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


public class DetailActivity extends SingleFragmentActivity {

    private static final String CITY_NAME = "com.example.weather.city_name";
    private static final String DATE = "com.example.weather.date";
    private static final String IS_FAHRENHEIT = "com.example.weather.temp_unit";
    private MyUtil mMyUtil;

    @Override
    protected Fragment createFragment() {
        mMyUtil = (MyUtil)getApplication();
        Intent intent = getIntent();
        String date = intent.getStringExtra(DATE);
        return DetailFragment.newInstance(date);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_detail;
    }

    /**
     * 返回一个DetailAcitivity的intent以启动该activity
     * @param packageContext
     * @param date 日期
     * @return
     */
    public static Intent newIntent(Context packageContext, String date){
        Intent intent = new Intent(packageContext, DetailActivity.class);
        intent.putExtra(DATE, date);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_activity_setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting_item:
                Intent intent = SettingActivity.newIntent(DetailActivity.this);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
