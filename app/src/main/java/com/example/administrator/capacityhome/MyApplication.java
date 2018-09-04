package com.example.administrator.capacityhome;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.navisdk.adapter.BNCommonSettingParam;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.example.administrator.capacityhome.login.LoginActivity;
import com.example.administrator.capacityhome.smarthome.video.activity.XMMainActivity;
import com.example.administrator.capacityhome.smarthome.video.appteam.DatabaseManager;
import com.example.administrator.capacityhome.smarthome.video.obj.MyCamera;
import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.zhy.autolayout.config.AutoLayoutConifg;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;
import utils.ACache;
import utils.DeviceUuidFactory;
import utils.SPUtils;
import utils.StringUtils;

/**
 * Created by Administrator on 2018/3/28.
 */

public class MyApplication extends Application {
    private static MyApplication myApplication;

    public static String getPasswprd() {
        if (passwprd == null) {
            if (SPUtils.getSharedStringData(myApplication, "password") == null) {
                return null;
            } else {
                passwprd = SPUtils.getSharedStringData(myApplication, "password");
            }
        }
        return passwprd;
    }

    public static JSONObject jsonObject_composite;//综合信息
    public static String deviceId = null;//设备唯一ID
    public static JSONObject user_json = null;//用户相关json数据
    public static String passwprd;
    public static int brand = 0;//喜欢的品牌id
    public static String brand_pics;//品牌图片
    public static boolean remeberPassword = true;//记住密码，
    public static String uid = null;//用户id
    public static JSONObject configration_json = null;//获取全局基本配置
    public static int pageSize = 10;//每页获取数目
    public static boolean can_detail = true;//可以跳转到详情页
    public static int no_rendMessage = 0;//未读消息数量

    public static boolean isRemeberPassword() {
        remeberPassword = SPUtils.getSharedBooleanData(myApplication, "remeberPasswprd");
        return remeberPassword;
    }

    public static boolean isupdate = false;//是否有数据变化，个人信息
    public static boolean isLogin = false;//是否是从登录界面过来

    //VIdio相关

    public static Stack<Activity> activityStack;
    private int count = 0;
    private final long TIME = 5 * 1000;

    public static Context getAppContext() {
        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        //默认使用的高度是设备的可用高度，也就是不包括状态栏和底部的操作栏的，如果你希望拿设备的物理高度进行百分比化用以下方法：
        AutoLayoutConifg.getInstance().useDeviceSize();
        SDKInitializer.initialize(getApplicationContext());
        deviceId = new DeviceUuidFactory(this).getDeviceUuid();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        // x.Ext.init(this);

        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityStopped(Activity activity) {

                boolean mIsForeground = isRunningForeground(MyApplication.this);
                Log.i("MyApplication", "切到后台  activity == " + activity.getLocalClassName()
                        + " mIsForeground == " + mIsForeground
                        + " count == " + count);

                if (count == 0) {
                    startDisconnect();
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
                DatabaseManager.n_mainActivity_Status = 1;
                count++;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                DatabaseManager.n_mainActivity_Status = 0;
                count--;
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                activityStack.remove(activity);
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                activityStack.add(activity);
            }
        });


    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static void clearAll() {
        ACache aCache = ACache.get(myApplication);
        aCache.clear();
        user_json = null;//用户相关json数据
        brand_pics = null;
        brand = 0;
        can_detail = true;
        passwprd = null;
        uid = null;//用户id
        remeberPassword = true;//记住密码，
        SPUtils.clear(myApplication);
        SPUtils.clear(MyApplication.getAppContext());
    }

    public static int getBrand() {
        if (brand == 0) {
            try {
                if (SPUtils.getSharedStringData(MyApplication.getAppContext(), "user_json") == null) {
                    return 0;
                } else {
                    brand = SPUtils.getSharedIntData(MyApplication.getAppContext(), "brand");
                }

            } catch (Exception e) {
                e.printStackTrace();
                //这里不用全局静态标识tag是因为怕被回收时，某些静态产生异常
            }
        }
        return brand;
    }

    public static String getBrand_pics() {
        if (brand_pics == null) {
            if ((StringUtils.isEmpty(SPUtils.getSharedStringData(MyApplication.getAppContext(), "brand_pics" + brand)))) {
                return null;
            } else {
                brand_pics = SPUtils.getSharedStringData(MyApplication.getAppContext(), "brand_pics" + brand);
            }
        }
        return brand_pics;
    }

    public static String getDeviceId() {
        if (deviceId == null) {
            deviceId = new DeviceUuidFactory(myApplication).getDeviceUuid();
        }
        return deviceId;
    }

    /**
     * 获取用户userjson
     *
     * @return
     */
    public static JSONObject getUserJson() {
        if (user_json == null) {
            try {
                if (SPUtils.getSharedStringData(myApplication, "user_json") == null) {
                    return null;
                } else {
                    user_json = new JSONObject(SPUtils.getSharedStringData(myApplication, "user_json"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
                //这里不用全局静态标识tag是因为怕被回收时，某些静态产生异常
            }
        }
        return user_json;
    }

    /**
     * 获取用户id
     *
     * @return
     */
    public static String getUid() {
        if (uid == null) {
            if (StringUtils.isEmpty(SPUtils.getSharedStringData(MyApplication.getAppContext(), "user_id"))) {
                return null;
            } else {
                uid = SPUtils.getSharedStringData(MyApplication.getAppContext(), "user_id");
            }
        }
        return uid;
    }

    /**
     * 获取全局基本配置
     *
     * @return
     */
    public static JSONObject getConfigrationJson() {
        if (configration_json == null) {
            try {
                if (SPUtils.getSharedStringData(myApplication, "configration_json") == null) {
                    return null;
                } else {
                    configration_json = new JSONObject(SPUtils.getSharedStringData(myApplication, "configration_json"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return configration_json;
    }

    public static int getPageSize() {
        if (pageSize == 0) {
            pageSize = 10;
        }
        return pageSize;
    }

    private Timer mTimer;
    private TimerTask mTimerTask;

    private void startDisconnect() {

        if (mTimer != null) {
            mTimerTask.cancel();
            mTimer.cancel();
            mTimerTask = null;
            mTimer = null;
        }

        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                for (MyCamera camera : XMMainActivity.CameraList) {
//					if (camera.isConnectedAlive()) {
                    camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                            AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVICESLEEP_REQ,
                            AVIOCTRLDEFs.SMsgAVIoctrlSetDeviceSleepReq.parseContent(Camera.DEFAULT_AV_CHANNEL));

                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException exception) {

                    }

                    //camera.disconnect();
                    //camera.isRequestReconnected = true;
//					}else {
//						camera.isRequestReconnected = false;
//					}
                }
            }
        };
        mTimer.schedule(mTimerTask, TIME);
    }

    private void startFinish() {
        if (mTimer != null) {
            mTimerTask.cancel();
            mTimer.cancel();
            mTimerTask = null;
            mTimer = null;
        }

        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (count == 0) {
                    Activity activity = null;
                    while (activityStack.size() > 0) {
                        activity = activityStack.lastElement();
                        activity.finish();
                        activityStack.remove(activity);
                        Log.i("Zed", " MyApplication  finish " + activity.getLocalClassName());
                    }
                    Log.i("MyApplication", " ------------FinishThread over-------------");
                }
            }
        };
        mTimer.schedule(mTimerTask, TIME);
    }

    /**
     * 程序是否在前台运行
     * 屏幕自动黑屏 此程序也会返回false,此时 APP仍处于前台
     *
     * @return
     */
    private boolean isRunningForeground(Context context) {

        boolean isForeground = false;

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean screen = pm.isScreenOn();

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    isForeground = false;
                } else {
                    isForeground = true;
                }
            }
            break;
        }

//        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//        String currentPackageName = cn.getPackageName();
//        if(!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(getPackageName())){
//            isForeground = true;
//        }

        if (isForeground && !screen) {
            return false;
        }

        return isForeground;
    }


    private void startBugly() {
//        Context context = getApplicationContext();
//        // 获取当前包名
//        String packageName = context.getPackageName();
//        // 获取当前进程名
//        String processName = getProcessName(android.os.Process.myPid());
//        // 设置是否为上报进程
//        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
//        strategy.setUploadProcess(processName == null || processName.equals(packageName));
//        // 初始化Bugly
//        CrashReport.initCrashReport(context, "196fec7519", false, strategy);
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
}
