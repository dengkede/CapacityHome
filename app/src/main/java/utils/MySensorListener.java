package utils;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import google.zxing.camera.CameraManager;

/**
 * Created by Administrator on 2018/4/10.
 */

//传感器监听

public class MySensorListener implements SensorEventListener {

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    //获取光线的强度
    public void onSensorChanged(SensorEvent event) {

        float lux = event.values[0];//获取光线强度


        int retval = Float.compare(lux, (float) 10.0);
//        if(retval>0){//光线强度>10.0
//            CameraManager.get().closeLight();//关闪光灯
//        }
//        else {
//            CameraManager.get().openLight();//开闪光灯
//        }
    }
}