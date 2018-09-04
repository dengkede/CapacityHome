package com.example.administrator.capacityhome.smarthome.video.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.administrator.capacityhome.smarthome.video.activity.XMMainActivity;
import com.example.administrator.capacityhome.smarthome.video.obj.MyCamera;

import IOTC.IOTCAPIs;


public class IntentServiceActivity extends Service {

    private String TAG = "AAA";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind is called");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate is called");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(TAG, "onStart");
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy is called");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
//        XMMainActivity.initCameraList(this, true);
        for (MyCamera camera : XMMainActivity.CameraList) {
//            camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
//                    AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVICESLEEP_REQ,
//                    AVIOCTRLDEFs.SMsgAVIoctrlSetDeviceSleepReq.parseContent(Camera.DEFAULT_AV_CHANNEL));
//            Log.d(TAG,"2.onTaskRemoved IOTYPE_USER_IPCAM_DEVICESLEEP_REQ");
            int mSID = camera.getMSID();
            if (mSID >= 0) {
                IOTCAPIs.IOTC_Session_Close(mSID);
                Log.d(TAG,"2.IOTC_Session_Close");
            }
        }
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onTrimMemory(int level) {
        Log.d(TAG, "onTrimMemory is called,the level is " + level);
        super.onTrimMemory(level);
    }
}
