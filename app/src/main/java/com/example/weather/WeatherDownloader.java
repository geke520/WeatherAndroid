package com.example.weather;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.contentcapture.ContentCaptureCondition;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;

public class WeatherDownloader<T> extends HandlerThread {
    private static final String TAG = "WeatherDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    private Handler mRequestHandler;    // 负责处理下载请求消息的队列
    private Handler mResponseHandler;   // 负责与主线程关联,更新UI
    private Context mContext;
    private WeatherDownloaderListener<T> mTWeatherDownloaderListener;
    private boolean mHasQuit;           // 标记该进程是否结束

    public interface  WeatherDownloaderListener<T>{
        // 天气信息下载完之后被调用,显示最新信息
        void onWeatherDownloaded(boolean isOk);
    }

    public void setWeatherDownloaderListener(WeatherDownloaderListener<T> listener){
        mTWeatherDownloaderListener = listener;
    }


    public WeatherDownloader(Handler responseHandler, Context context){
        super(TAG);
        mResponseHandler = responseHandler;
        mContext = context;
    }

    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }

    @Override
    protected void onLooperPrepared() {
        Log.i("Test", "初始化handle");
        mRequestHandler =  new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what == MESSAGE_DOWNLOAD){
                    T target = (T) msg.obj;     // 要处理的城市名称
                    handleRequest(target);
                }
            }
        };
        Log.i("Test", "初始化handle完成");
    }

    public void queueDownloader(T target){
        Log.i("Test", "处理城市: " + target);
        if(target == null){
            // Todo: 处理城市名字
            return;
        }else{
            // 发送信息给mRequest,让其处理下载请求
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
        }
    }

    private void handleRequest(final T target){
        Log.i("Test", "开始处理" + target);
        final boolean isOk = new WeatherFetcher(mContext).fetchItems((String) target);
        mResponseHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mHasQuit){
                    return;     // 若结束,则不处理
                }
                mTWeatherDownloaderListener.onWeatherDownloaded(isOk);
            }
        });
    }


}
