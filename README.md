# AlibabaSDK_push_rtmp (v4.4.0）
由于低版本适配不了android 10的手机，所以需要更新，在使用Ali推流SDK，当你遇到这个问题：
```
No implementation found for void org.webrtc.utils.AlivcLog.nativeLog
```
其原因是没有初始化sdk导致的：
```
AlivcLivePusher.getSDKVersion();
```
没错，这就是初始化。
