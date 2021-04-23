package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity {
    private TextView mLocationTextView;
    private TextView mTempUnitsTextView;
    private TextView mWeatherNoteTextView;
    private CheckBox mWeatherNoteCheckBox;
    private LinearLayout mLocationLayout;
    private LinearLayout mTempUnitsLayout;
    private static final String SELECTED = "已开启";
    private static final String UNSELECTED = "未开启";
    private static final String FAHRENHEIT = "华氏温度";
    private static final String CELSIUS = "摄氏温度";
    private static final String OPEN_NOTE = "open_note";
    private static final String CITY_NAME = "city_name";
    private static final String IS_FRHRENHRIT = "temp_units";
    private boolean mNoteIsOpened;
    private String mCityName;
    private boolean mIsFahrenheit;
    private MyUtil mMyUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mMyUtil = (MyUtil) getApplication();
        // Intent intent = getIntent();
        // if(intent != null){
//            mNoteIsOpened = intent.getBooleanExtra(OPEN_NOTE, false);
//            mCityName = intent.getStringExtra(CITY_NAME);
//            mIsFahrenheit = intent.getBooleanExtra(IS_FRHRENHRIT, false);
        // }

        mNoteIsOpened = mMyUtil.isNoteIsOpened();
        mCityName = mMyUtil.getCityName();
        mIsFahrenheit = mMyUtil.isFahrenheit();
        mLocationTextView = (TextView)findViewById(R.id.setting_location_text_view);
        mLocationTextView.setText(mCityName);
        mTempUnitsTextView = (TextView)findViewById(R.id.setting_temp_unit_text_view);
        // 设置对应的温度类型
        String currentUnits = mIsFahrenheit ? FAHRENHEIT : CELSIUS;
        mTempUnitsTextView.setText(currentUnits);

        mWeatherNoteTextView = (TextView)findViewById(R.id.setting_weather_note_text_view);
        mWeatherNoteCheckBox = (CheckBox)findViewById(R.id.setting_wea_note_check_box);
        mWeatherNoteCheckBox.setChecked(mNoteIsOpened);      // 设置是否开启
        String noteText = mNoteIsOpened ? SELECTED : UNSELECTED;
        mWeatherNoteTextView.setText(noteText);                    // 设置对应文本

        // 监听事件,是否启用通知
        mWeatherNoteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                MyUtil.openNotification(SettingActivity.this);
                String text = b ? SELECTED : UNSELECTED;
                mNoteIsOpened = b;      // 更改通知状态
                mWeatherNoteTextView.setText(text);
                /** 修改 **/
                mMyUtil.setNoteIsOpened(mNoteIsOpened);

            }
        });

        // 监听点击事件,改变城市位置
        mLocationLayout = (LinearLayout)findViewById(R.id.setting_location_layout);
        mLocationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new InputDialog(SettingActivity.this).show();
            }
        });
        // 监听点击事件,改变温度单位
        mTempUnitsLayout = (LinearLayout)findViewById(R.id.setting_temp_unit_layout);
        mTempUnitsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mIsFahrenheit){
                    mTempUnitsTextView.setText(CELSIUS);
                    mIsFahrenheit = false;
                }else{
                    mTempUnitsTextView.setText(FAHRENHEIT);
                    mIsFahrenheit = true;
                }
                mMyUtil.setFahrenheit(mIsFahrenheit);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    // boolean openNote, String cityName, boolean isFahrenheit
    public static Intent newIntent(Context packageContext){
        Intent intent = new Intent(packageContext, SettingActivity.class);
        /*
        intent.putExtra(OPEN_NOTE, openNote);
        intent.putExtra(CITY_NAME, cityName);
        intent.putExtra(IS_FRHRENHRIT, isFahrenheit);
        */
        return intent;
    }


    /*
    public static String getCityName(Intent data){
        return data.getStringExtra(CITY_NAME);
    }

    public static boolean isFahrenheit(Intent data){
        return data.getBooleanExtra(IS_FRHRENHRIT, false);
    }

    public static boolean getNoteStatus(Intent data){
        return data.getBooleanExtra(OPEN_NOTE, false);
    }
    */

    private class InputDialog extends Dialog implements View.OnClickListener {
        private Activity mActivity;
        private EditText mInputEditText;
        private Button mCancelButton;
        private Button mSureButton;

        public InputDialog(Activity activity) {
            super(activity, R.style.DialogStyle);
            mActivity = activity;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.input_city);

            mInputEditText = (EditText)findViewById(R.id.new_city_edit);
            mCancelButton = (Button) findViewById(R.id.cancel_button);
            mSureButton = (Button)findViewById(R.id.sure_button);

            mCancelButton.setOnClickListener(this);
            mSureButton.setOnClickListener(this);

            setViewLocation();
            setCanceledOnTouchOutside(true);        // 外部点击取消

        }

        /**
         * 设置Dialog位宽度为屏幕宽度
         */
        private void setViewLocation(){
            DisplayMetrics dm = new DisplayMetrics();
            mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            int height = dm.heightPixels;

            Window window = this.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height  = WindowManager.LayoutParams.WRAP_CONTENT;
            window.getDecorView().setPadding(0, 0, 0, 0);
            // 设置显示位置
            // window.setAttributes(lp);
            // onWindowAttributesChanged(lp);
        }


        @Override
        public void onClick(View view) {
            int id = view.getId();
            if(id == R.id.sure_button){
                mCityName = mInputEditText.getText().toString();
                mLocationTextView.setText(mCityName);
            }
            mMyUtil.setCityName(mCityName);
            this.cancel();
        }
    }

}
