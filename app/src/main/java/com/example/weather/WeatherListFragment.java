package com.example.weather;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * WeatherListFragment托管MainActivity的天气列表
 */
public class WeatherListFragment extends Fragment {
    private RecyclerView mWeahterRecyclerView;
    private WeatherDownloader<String> mWeatherDownloader;
    private List<Weather> mWeathers;        // 与mWeahterRecyclerView绑定的模型数据
    private SQLiteDatabase mDatabase;
    private WeatherAdapter mWeatherAdapter;
//    private ImageView mWeatherTypeView;
//    private TextView mDateTextView;
//    private TextView mMaxTempTextView;
//    private TextView mMinTempTextView;
//    private TextView mWeatherTypeTextView;

    private boolean mIsFahrenheit;        // 标记温度类型
    private String mCityName = "长沙";    // 当前城市
    private boolean mNoteIsOpened;        // 是否开启通知
    private static final int SETTING = 0;
    private static final int MAP_LOCATION = 1;
    private static final String BAIDU_MAP = "com.baidu.BaiduMap";
    private static final String AUTONAVI_MAP = "com.autonavi.minimap";
    private static final String QQ_MAP = "com.tencent.map";
    private static final String AUTONAVI_KEY = "fbe553d1ce10a8b85b0a93391af78ef1";
    private double mLat;
    private double mLng;
    private InfoChangedListener mInfoChangedListener;
    private MyUtil mMyUtil;
    private Callbacks mCallbacks;

    public interface Callbacks{
        void onWeatherSelected(String date);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public interface InfoChangedListener{
        // 信息被改变之后让activity知道
        void onInfoChanged(boolean isCityChanged);
    }

    public void setInfoChangedListener(InfoChangedListener listener){
        mInfoChangedListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // 设备旋转时不随activity销毁而销毁
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        // 告诉FragmentManager其管理的fragment应该接收onCreateOptionMenu方法的调用指令
        setHasOptionsMenu(true);

        mMyUtil = (MyUtil) getActivity().getApplication();
        mCityName = mMyUtil.getCityName();
        mIsFahrenheit = mMyUtil.isFahrenheit();
        mNoteIsOpened = mMyUtil.isNoteIsOpened();

        mWeathers = new ArrayList<>();


        // 连接数据库
        mDatabase = new WeatherBaseHelper(getActivity()).getReadableDatabase();

        // 定义Handler默认会与主线程的Handler相关联
        Handler responseHandler = new Handler();

        mWeatherDownloader = new WeatherDownloader<>(responseHandler,getActivity());
        mWeatherDownloader.setWeatherDownloaderListener(
                new WeatherDownloader.WeatherDownloaderListener<String>() {
            @Override
            public void onWeatherDownloaded(boolean isOk) {
                String msg = isOk ? "刷新成功" : "刷新失败";
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                mInfoChangedListener.onInfoChanged(true);
                // 先从数据库取出数据
                getWeathers();
                // 更新数据显示部分
                updataUI();
            }
        });
        mWeatherDownloader.start();
        mWeatherDownloader.getLooper();     // 在start方法后执行可以保证线程就绪,避免潜在竞争
        Log.i("Test", "Background thread started");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // activity结束时关闭线程
        mWeatherDownloader.quit();
    }

    /**
     * 检查地图应用是否安装
     * @param packageName
     * @return
     */
    private boolean isIntalled(String packageName){
        final PackageManager packageManager = getContext().getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        if(packageInfos != null){
            for(PackageInfo info : packageInfos){
                if(info.packageName.equals(packageName)){
                    return true;
                }
            }
        }
        return false;
    }

    private void openAutoNaviMap(){
        if(isIntalled(AUTONAVI_MAP)){
            Toast.makeText(getActivity(),"抱歉,您还未安装高德地图", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i("Test", "启动高德地图3");
        // 启动高德地图
        LatLng latLng = new LatLng(mLat, mLng);
        Uri uri = Uri.parse("androidamap://viewMap")
                .buildUpon()
                .appendQueryParameter("sourceApplication", "WeatherForecast")
//                .appendQueryParameter("poiname", "abc")
                .appendQueryParameter("poiname", mCityName+"市")
                .appendQueryParameter("lat", String.valueOf(latLng.latitude))
                .appendQueryParameter("lon", String.valueOf(latLng.longitude))
//                .appendQueryParameter("poiname", mCityName)
                .appendQueryParameter("dev", "0")
//                .appendQueryParameter("style", "2")
                .build();
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        intent.setPackage(AUTONAVI_MAP);
        startActivity(intent);
    }

    private void updataUI(){
        if(mWeatherAdapter == null){
            setAdapter();
        }else{
            mWeatherAdapter.notifyDataSetChanged();
        }
    }



    private void getWeathers(){
        mWeathers.clear();
        // 更新数据
        Cursor cursor = mDatabase.query(WeatherDbSchema.BasicWeatherTable.NAME,
                null, WeatherDbSchema.BasicWeatherTable.Cols.CITY_NAME + "=?",
                new String[]{mCityName}, null, null, null);
        WeatherCursorWrapper wrapper = new WeatherCursorWrapper(cursor);
        try{
            wrapper.moveToFirst();
            while (!wrapper.isAfterLast()){
                Log.i("Test", "加入一个");
                Weather weather = wrapper.getWeather();
                Log.i("Test", "天气日期" + weather.getDate());
                mWeathers.add(weather);
                wrapper.moveToNext();
            }
        }finally {
            wrapper.close();
        }
        // 没有网络,数据库中也没有数据,默认添加初始数据
        if(mWeathers.size() == 0){
            // 预定义的数据
            Log.i("Test", "暂无数据");
            for(int i = 0; i < 7; i++){
                mWeathers.add(new Weather());
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 创建视图
        View view = inflater.inflate(R.layout.fragment_list_weather, container, false);
        // 设置LayoutManager,负责摆放列表项以及定义滚动屏幕的形为
        mWeahterRecyclerView = view.findViewById(R.id.weather_recycler_view);
        mWeahterRecyclerView.addItemDecoration(new DividerItemDecoration(
                getContext(), DividerItemDecoration.VERTICAL));
        mWeahterRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 获取数据
        mWeatherDownloader.queueDownloader(mCityName);

        // 设置对应Adapter
        setAdapter();
        return view;
    }



    // 设置对应的Adapter
    private void setAdapter(){
        if(isAdded()){
            mWeatherAdapter = new WeatherAdapter(mWeathers);
            mWeahterRecyclerView.setAdapter(mWeatherAdapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    // 创建菜单项
    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.selecte_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //判断点击的菜单项
        switch (item.getItemId()){
            case R.id.map_location_item:
                // 地图定位
                new FetchLongAndLat().execute();
                return true;
            case R.id.setting_item:
                Intent intent = SettingActivity.newIntent(getActivity());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 改变温度单位
     */
    @Override
    public void onResume() {
        super.onResume();
        boolean changed = false;
        if(mMyUtil.isFahrenheit() != mIsFahrenheit){
            // 温度类型被更改
            changed = true;
            mIsFahrenheit = mMyUtil.isFahrenheit();
        }
        if(!mCityName.equals(mMyUtil.getCityName())){
            // 若更改城市城市,重新获取即可
            mCityName = mMyUtil.getCityName();
            mWeatherDownloader.queueDownloader(mCityName);
        }else if(changed){
            // 若只是更改了温度类型,刷新界面
            updataUI();
            mInfoChangedListener.onInfoChanged(false);
        }
    }


    /**
     * 获取城市地区的经纬度
     */
    private class FetchLongAndLat extends AsyncTask<Void, Void, Void>{

        /**
         * 获取经纬度
         * @param voids
         * @return
         */
        @Override
        protected Void doInBackground(Void... voids) {
            try{
                String url = Uri.parse( "https://restapi.amap.com/v3/geocode/geo")
                        .buildUpon()
                        .appendQueryParameter("key", AUTONAVI_KEY)
                        .appendQueryParameter("address", mCityName)
                        .build().toString();
                String result = new WeatherFetcher(getActivity()).getUrl(url);
                mLat = mLng = -1024;        // 初始化
                JSONObject jsonBody = new JSONObject(result);
                Log.i("Test", result);
                if(jsonBody.getString("count").equals("0")) {
                    // 若没有对应经纬度信息
                    return null;
                }

                // 获取数据
                JSONArray data = jsonBody.getJSONArray("geocodes");
                // Log.i("Test", "长度为: " + data.length());
                if(data.length() > 0){
                    JSONObject locationObject = (JSONObject)data.get(0);
                    String location = locationObject.getString("location");
                    String[] latAndLng = location.split(",");
                    mLng = Double.parseDouble(latAndLng[0]);        // 经度
                    mLat = Double.parseDouble(latAndLng[1]);        // 维度
                    // Log.i("Test", mLng + " " + mLat);
                }
            } catch (IOException e) {
                Log.e("Test","发生异常", e);
            } catch (JSONException e) {
                Log.e("Test","发生异常", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //在这里启动高德地图
            Log.i("Test", "启动高德地图1");
            if(mLat == -1024 || mLng == -1024){
                Toast.makeText(getActivity(), "获取当前城市位置信息失败",
                        Toast.LENGTH_SHORT).show();
            }else{
                Log.i("Test", "启动高德地图2");
                openAutoNaviMap();

            }
        }
    }

    private class WeatherHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mWeatherTypeIcon;
        private TextView mDateTextView;
        private TextView mWeatherTypeTextView;
        private TextView mMaxTemperatureTextView;
        private TextView mMinTemperatureTextView;
        private Weather mWeather;

        public WeatherHolder(@NonNull View view) {
            super(view);
            // 控件引用
            mWeatherTypeIcon = itemView.findViewById(R.id.list_item_weather_type_icon);
            mDateTextView = itemView.findViewById(R.id.list_item_date_text_view);
            mWeatherTypeTextView = itemView.findViewById(R.id.list_item_weather_type_text_view);
            mMaxTemperatureTextView = itemView.findViewById(R.id.list_item_maxtemp_text_view);
            mMinTemperatureTextView = itemView.findViewById(R.id.list_item_mintemp_text_view);
            itemView.setOnClickListener(this);      // 设置事件监听器
        }

        // 绑定模型数据
        public void bind(Weather weather){
            mWeather = weather;
            MyUtil.setImage(weather.getWeaImg(), mWeatherTypeIcon);
            mDateTextView.setText(MyUtil.handleDay(weather.getDay()));
            mWeatherTypeTextView.setText(weather.getWea());
            mMaxTemperatureTextView.setText(MyUtil.handleTmeprature(weather.getTem1(), mIsFahrenheit));
            mMinTemperatureTextView.setText(MyUtil.handleTmeprature(weather.getTem2(), mIsFahrenheit));
        }

        // 监听点击事件

        @Override
        public void onClick(View view) {
            // Todo: 若是手机,启动另外一个Activity;若是平板,detailFragment显示详细数据
            mCallbacks.onWeatherSelected(mWeather.getDate());
        }
    }

    private class WeatherAdapter extends RecyclerView.Adapter<WeatherHolder>{
        private List<Weather> mWeathers;

        public WeatherAdapter(List<Weather> weathers){
            mWeathers = weathers;
        }

        @NonNull
        @Override
        public WeatherHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_weather, parent, false);
            return new WeatherHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WeatherHolder holder, int position) {
            Weather weather = mWeathers.get(position);
            holder.bind(weather);
        }

        @Override
        public int getItemCount() {
            return mWeathers.size();
        }
    }

}

