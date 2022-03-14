package com.example.alibabasdk_push_rtmp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alivc.live.pusher.AlivcLivePushConfig;
import com.alivc.live.pusher.AlivcLivePushInfoListener;
import com.alivc.live.pusher.AlivcLivePushStatsInfo;
import com.alivc.live.pusher.AlivcLivePusher;
import com.alivc.live.pusher.AlivcPreviewOrientationEnum;
import com.alivc.live.pusher.AlivcResolutionEnum;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    public static final int CAPTURE_PERMISSION_REQUEST_CODE = 0x1123;

    Button button;

    private AlivcLivePushConfig mAlivcLivePushConfig;

    private AlivcLivePusher mAlivcLivePusher = null;
    private int mCaptureVolume = 50;

    private boolean mIsStartPushing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        AlivcLivePusher.getSDKVersion();
        mAlivcLivePushConfig = new AlivcLivePushConfig();
        if (mAlivcLivePushConfig.getPreviewOrientation() == AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_RIGHT.getOrientation() || mAlivcLivePushConfig.getPreviewOrientation() == AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT.getOrientation()) {
            mAlivcLivePushConfig.setNetworkPoorPushImage(getFilesDir().getPath() + File.separator + "alivc_resource/poor_network_land.png");
            mAlivcLivePushConfig.setPausePushImage(getFilesDir().getPath() + File.separator + "alivc_resource/background_push_land.png");
        } else {
            mAlivcLivePushConfig.setNetworkPoorPushImage(getFilesDir().getPath() + File.separator + "alivc_resource/poor_network.png");
            mAlivcLivePushConfig.setPausePushImage(getFilesDir().getPath() + File.separator + "alivc_resource/background_push.png");
        }
        AlivcLivePushConfig.setMediaProjectionPermissionResultData(null);
        initView();
        setClick();
        getSystemService(Context.CLIPBOARD_SERVICE);
    }

    private void initView() {
        button = (Button)findViewById(R.id.btnPush);
    }

    private void setClick() {
        button.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.btnPush:
                    if (mIsStartPushing) {
                        return;
                    }
                    mIsStartPushing = true;
                    if (getPushConfig() != null) {
                        if (mAlivcLivePusher == null) {
                            Intent intent = new Intent(MainActivity.this, ForegroundService.class);
                            startService(intent);
                            startScreenCapture();
                        } else {
                            view.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mIsStartPushing = false;
                                }
                            },1000);
                            Intent intent = new Intent(MainActivity.this, ForegroundService.class);
                            stopService(intent);
                            stopPushWithoutSurface();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private AlivcLivePushConfig getPushConfig() {
        mAlivcLivePushConfig.setResolution(AlivcResolutionEnum.RESOLUTION_540P);
        return mAlivcLivePushConfig;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAPTURE_PERMISSION_REQUEST_CODE: {
                if (resultCode == Activity.RESULT_OK) {
                    mAlivcLivePushConfig.setMediaProjectionPermissionResultData(data);
                    if (mAlivcLivePushConfig.getMediaProjectionPermissionResultData() != null) {
                        if (mAlivcLivePusher == null) {
                            startPushWithoutSurface("rtmp://106.52.180.133:1935/live/10");
                        } else {
                            stopPushWithoutSurface();
                        }
                    }
                }
            }
            break;
            default:
                break;
        }
    }

    @TargetApi(21)
    private void startScreenCapture() {
        if (Build.VERSION.SDK_INT >= 21) {
            MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)
                    getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            try {
                this.startActivityForResult(
                        mediaProjectionManager.createScreenCaptureIntent(), CAPTURE_PERMISSION_REQUEST_CODE);
            } catch (ActivityNotFoundException ex) {
                ex.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext()
                                , "Start ScreenRecording failed, current device is NOT suuported!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Toast.makeText(this, "录屏需要5.0版本以上", Toast.LENGTH_LONG).show();
        }
    }

    private void stopPushWithoutSurface() {
        if (mAlivcLivePusher != null) {
            try {
                mAlivcLivePusher.stopCamera();
            } catch (Exception e) {
            }
            try {
                mAlivcLivePusher.stopCameraMix();
            } catch (Exception e) {
            }
            try {
                mAlivcLivePusher.stopPush();
            } catch (Exception e) {
            }
            try {
                mAlivcLivePusher.stopPreview();
            } catch (Exception e) {
            }
            try {
                mAlivcLivePusher.destroy();
            } catch (Exception e) {
            }

            mAlivcLivePusher.setLivePushInfoListener(null);
            mAlivcLivePusher = null;
        }
    }

    private void startPushWithoutSurface(String url) {
        mAlivcLivePusher = new AlivcLivePusher();
        try {
            mAlivcLivePusher.init(getApplicationContext(), mAlivcLivePushConfig);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        mAlivcLivePusher.setLivePushInfoListener(new AlivcLivePushInfoListener() {
            @Override
            public void onPreviewStarted(AlivcLivePusher pusher) {

            }

            @Override
            public void onPreviewStoped(AlivcLivePusher pusher) {

            }

            @Override
            public void onPushStarted(AlivcLivePusher pusher) {
            }

            @Override
            public void onFirstAVFramePushed(AlivcLivePusher pusher) {
            }

            @Override
            public void onPushPauesed(AlivcLivePusher pusher) {

            }

            @Override
            public void onPushResumed(AlivcLivePusher pusher) {

            }

            @Override
            public void onPushStoped(AlivcLivePusher pusher) {

            }

            @Override
            public void onPushRestarted(AlivcLivePusher pusher) {
            }

            @Override
            public void onFirstFramePreviewed(AlivcLivePusher pusher) {
                mIsStartPushing = false;
            }

            @Override
            public void onDropFrame(AlivcLivePusher pusher, int countBef, int countAft) {

            }

            @Override
            public void onAdjustBitRate(AlivcLivePusher pusher, int curBr, int targetBr) {

            }

            @Override
            public void onAdjustFps(AlivcLivePusher pusher, int curFps, int targetFps) {

            }

            @Override
            public void onPushStatistics(AlivcLivePusher pusher, AlivcLivePushStatsInfo statistics) {

            }
        });

        try {
            mAlivcLivePusher.startPreview(null);
        } catch (Exception e) {
            Log.i("TAG","StartPreview failed");
            return;
        }
        try {
            mAlivcLivePusher.startPush(url);
        } catch (Exception e) {
            Log.i("TAG","startPush failed");
            return;
        }
        mAlivcLivePusher.setPreviewOrientation(AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT);
        mAlivcLivePusher.setCaptureVolume(mCaptureVolume);
    }
}