# AlibabaSDK_push_rtmp (v4.4.0）[推流SDK](https://help.aliyun.com/document_detail/267433.html)
由于低版本适配不了android 10的手机，所以需要更新，在使用Ali推流SDK，当你遇到这个问题：
```
    No implementation found for void org.webrtc.utils.AlivcLog.nativeLog
```
其原因是没有初始化sdk导致的：
```
    AlivcLivePusher.getSDKVersion();
```
没错，这就是初始化。

# 使用时请在 AndroidManifest.xml 加入以下权限
```
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.REORDER_TASKS" />
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
```

# 请在proguard-rules.pro中加入混淆
```
   -keep class com.alivc.** { *;}
   -keep class com.aliyun.rts.network.* { *;}
```
