package com.example.weather;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment {

    private SQLiteDatabase mDatabase;
    private static final String mDatabaseFile =
            "data/data/com.example.weather/databases/" + WeatherBaseHelper.DATABASE_NAME;
    private static final String CITY_NAME = "city_name";
    private static final String DATE = "date";
    private static final String IS_FAHRENHEIT = "temp_unit";
    private String cityName;
    private String date;
    private Weather mWeather;
    private List<HourWeather> mHourWeathers;
    private List<LifeTip> mLifeTips;
    private TextView mDayTextView;
    private TextView mDateTextView;
    private ImageView mWeatherTypeIcon;
    private TextView mTempTextView;
    private TextView mMaxTempTextView;
    private TextView mMinTempTextView;
    private RecyclerView mHourRecyclerView;
    private RecyclerView mLifeTipRecyclerView;
    private MyUtil mMyUtil;
    private HourAdapter mHourAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);        // 设备旋转时不随activity销毁而销毁
        // 告诉FragmentManager其管理的fragment应该接收onCreateOptionMenu方法的调用指令
        setHasOptionsMenu(true);
        // 获取原来的数据库
        mDatabase = SQLiteDatabase.openOrCreateDatabase(mDatabaseFile, null);
        mMyUtil = (MyUtil)getActivity().getApplication();
        cityName = mMyUtil.getCityName();
        date = getArguments().getString(DATE);

        // 读取数据库中的数据
        mWeather = getWeather();
        mHourWeathers = getHourWeathers();
        mLifeTips = getLifeTips();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        if(mWeather == null){
            Toast.makeText(getActivity(), "暂无数据", Toast.LENGTH_SHORT).show();
            return null;
        }
        View view = inflater.inflate(R.layout.fragment_detail, container,false);
        mDayTextView = view.findViewById(R.id.detail_day_text_view);
        mDateTextView = view.findViewById(R.id.detail_date_text_view);
        mWeatherTypeIcon = view.findViewById(R.id.detail_weather_type_icon);
        mTempTextView = view.findViewById(R.id.detail_temp_text_view);
        mMaxTempTextView = view.findViewById(R.id.detail_max_temp_text_view);
        mMinTempTextView = view.findViewById(R.id.detail_min_temp_text_view);
        mHourRecyclerView = view.findViewById(R.id.detail_hour_recycler_view);
        mLifeTipRecyclerView = view.findViewById(R.id.detail_life_tip_recycler_view);
        // 设置为横向排列
        mLifeTipRecyclerView.addItemDecoration(new DividerItemDecoration(
                getContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation( LinearLayoutManager.HORIZONTAL);
        mHourRecyclerView.setLayoutManager(linearLayoutManager);
        // 生活指数信息设置为纵向排列即可
        mLifeTipRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Todo: 设置Adapter
        setAdapter();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mDayTextView.setText(MyUtil.handleDay(mWeather.getDay()));
        mDateTextView.setText(MyUtil.handleDate(mWeather.getDate()));
        MyUtil.setImage(mWeather.getWeaImg(), mWeatherTypeIcon);
        mTempTextView.setText(MyUtil.handleTmeprature(mWeather.getTem(), mMyUtil.isFahrenheit()));
        mMaxTempTextView.setText(MyUtil.handleTmeprature(mWeather.getTem1(), mMyUtil.isFahrenheit()));
        mMinTempTextView.setText(MyUtil.handleTmeprature(mWeather.getTem2(), mMyUtil.isFahrenheit()));
        mHourAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu( Menu menu,  MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.share_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.share_menu:
//                shareWeather();
                gotoShare();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String captureScreen(){
        // 获取截图
        getActivity().getWindow().getDecorView().setDrawingCacheEnabled(true);
        Bitmap bmp = getActivity().getWindow().getDecorView().getDrawingCache();
        // 内置SD卡路径
        String sdCardPath = Environment.getExternalStorageDirectory().getPath();
        String imgPath = sdCardPath + File.separator + "share.png";
        // 保存截图
        try {
            File imgFile = new File(imgPath);
            FileOutputStream fos = new FileOutputStream(imgFile);
            if(fos != null){
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            }else {
                return null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imgPath;
    }

    private void shareWeather(){
        String imgPath = captureScreen();
        if(imgPath == null){
            Toast.makeText(getActivity(), "保存截图失败", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imgPath)));
        shareIntent.setType("image/jpeg");
        // shareIntent.putExtra(Intent.EXTRA_TEXT, "试一试");
        // shareIntent.setType("text/plain");
        getContext().startActivity(Intent.
                createChooser(shareIntent, "天气分享"));
    }


    private void gotoShare(){
        new AlertDialog.Builder(getActivity())
                .setTitle("权限确认")
                .setMessage("我们将截取当前屏幕进行分享,您是否同意?")
                .setNegativeButton("取消", null)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    private void setAdapter() {
        if(isAdded()){
            mHourAdapter = new HourAdapter(mHourWeathers);
            mHourRecyclerView.setAdapter(mHourAdapter);
            LifeTipAdapter lifeTipAdapter = new LifeTipAdapter(mLifeTips);
            mLifeTipRecyclerView.setAdapter(lifeTipAdapter);
        }
    }


    private Weather getWeather(){
        // 获取当天的基本信息
        Cursor cursor = mDatabase.query(WeatherDbSchema.BasicWeatherTable.NAME,
                null, WeatherDbSchema.BasicWeatherTable.Cols.CITY_NAME + "=? and "
                        + WeatherDbSchema.BasicWeatherTable.Cols.DATE + "=?",
                new String[]{cityName, date}, null, null, null);
        Weather weather = null;
        WeatherCursorWrapper wrapper = new WeatherCursorWrapper(cursor);
        try {
            wrapper.moveToFirst();
            weather = wrapper.getWeather();
        }finally {
            wrapper.close();
            return weather;
        }
    }

    private List<HourWeather> getHourWeathers(){
        // 从数据库中获取当前各小时段天气
        Cursor cursor = mDatabase.query(WeatherDbSchema.HourTable.NAME,
                null, WeatherDbSchema.HourTable.Cols.CITY_NAME + "=? and "
                + WeatherDbSchema.HourTable.Cols.DATE + "=?",
                new String[]{cityName, date}, null, null, null);
        List<HourWeather> hourWeathers = new ArrayList<>();
        WeatherCursorWrapper wrapper = new WeatherCursorWrapper(cursor);
        try {
            // 读取所有信息
            wrapper.moveToFirst();
            while (!wrapper.isAfterLast()){
                HourWeather hw = wrapper.getHourWeather();
                Log.i("Test", hw.toString());
                hourWeathers.add(hw);
                wrapper.moveToNext();
            }
        }finally {
            wrapper.close();
        }
        return hourWeathers;
    }

    private List<LifeTip> getLifeTips(){
        // 从数据库中获取当填所有生活指数信息
        Cursor cursor = mDatabase.query(WeatherDbSchema.LifeTipsTable.NAME,
                null, WeatherDbSchema.LifeTipsTable.Cols.CITY_NAME + "=? and "
                        + WeatherDbSchema.LifeTipsTable.Cols.DATE + "=?",
                new String[]{cityName, date}, null, null, null);
        List<LifeTip> lifeTips = new ArrayList<>();
        WeatherCursorWrapper wrapper = new WeatherCursorWrapper(cursor);
        try {
            // 读取所有信息
            wrapper.moveToFirst();
            while (!wrapper.isAfterLast()){
                lifeTips.add(wrapper.getLifeTip());
                wrapper.moveToNext();
            }
        }finally {
            wrapper.close();
        }
        return lifeTips;
    }

    // 当天小时段内recyclerview中的列表项
    private class HourHolder extends RecyclerView.ViewHolder{
        private TextView mTimeTextView;
        private TextView mWeatherTextView;
        private TextView mTempTextView;

        public HourHolder(@NonNull View view) {
            super(view);
            // 控件引用
            mTimeTextView = itemView.findViewById(R.id.hour_item_time_text_view);
            mWeatherTextView = itemView.findViewById(R.id.hour_item_weather_text_view);
            mTempTextView = itemView.findViewById(R.id.hour_item_temp_text_view);
        }

        // 绑定模型数据
        public void bind(HourWeather hourWeather){
            // 绑定数据
            mTimeTextView.setText(hourWeather.getDay());
            mTempTextView.setText(MyUtil.handleTmeprature(hourWeather.getTem(), mMyUtil.isFahrenheit()));
            mWeatherTextView.setText(hourWeather.getWea());
        }
    }

    // 显示小时段信息列表对应的Adapter
    private class HourAdapter extends RecyclerView.Adapter<HourHolder>{
        private List<HourWeather> hourWeathers;

        public HourAdapter(List<HourWeather> hourWeathers){
            this.hourWeathers = hourWeathers;
        }

        // 创建对应列表项视图的HourHolder
        @NonNull
        @Override
        public HourHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.hourlist_item_detail, parent, false);
            return new HourHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HourHolder holder, int position) {
            HourWeather hourWeather = hourWeathers.get(position);
            holder.bind(hourWeather);
        }

        @Override
        public int getItemCount() {
            return hourWeathers.size();
        }
    }

    private class LifeTipHolder extends RecyclerView.ViewHolder{
        private TextView mTitleTextView;
        private TextView mDescTextView;

        public LifeTipHolder(@NonNull View view) {
            super(view);
            // 获取控件引用
            mTitleTextView = itemView.findViewById(R.id.index_item_title);
            mDescTextView = itemView.findViewById(R.id.index_item_desc);
        }

        public void bind(LifeTip lifeTip){
            mTitleTextView.setText(lifeTip.getTitle());
            mDescTextView.setText(lifeTip.getDesc());
        }
    }

    // 与生活指数相关的Adapter
    private class LifeTipAdapter extends RecyclerView.Adapter<LifeTipHolder>{
        private List<LifeTip> lifeTips;

        public LifeTipAdapter(List<LifeTip> lifeTips){
            this.lifeTips = lifeTips;
        }

        @NonNull
        @Override
        public LifeTipHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.index_item_detail, parent, false);
            return new LifeTipHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LifeTipHolder holder, int position) {
            LifeTip lifeTip = lifeTips.get(position);
            holder.bind(lifeTip);
        }

        @Override
        public int getItemCount() {
            return lifeTips.size();
        }
    }

    /**
     * 方便传递数据,产生一个新的DetailFragment
     * @param date
     * @return
     */
    public static DetailFragment newInstance(String date){
        Bundle args = new Bundle();
        args.putString(DATE, date);
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(args);
        return detailFragment;
    }

}
