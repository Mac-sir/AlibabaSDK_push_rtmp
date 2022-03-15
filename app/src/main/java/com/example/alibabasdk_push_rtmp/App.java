package com.example.alibabasdk_push_rtmp;

import android.app.Application;

import com.alivc.live.pusher.AlivcLivePusher;

/**
 * Created by xwf on 2022/3/14.
 **/
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AlivcLivePusher.getSDKVersion();
    }
}
