# AlibabaSDK_push_rtmp (v4.4.0）
在使用Ali推流SDK，当你遇到这个问题：
···
No implementation found for void org.webrtc.utils.AlivcLog.nativeLog
···
没有初始化sdk导致的：
···
AlivcLivePusher.getSDKVersion();
···
没错，这就是初始化。
