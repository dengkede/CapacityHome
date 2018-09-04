package com.example.administrator.capacityhome.smarthome.video.util;

import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.dabin.dpns.DpnsRequestManager;
import com.dabin.dpns.push.MqttInitManeger;

import com.example.administrator.capacityhome.MainActivity;
import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.smarthome.video.activity.Const;
import com.example.administrator.capacityhome.smarthome.video.activity.MainActivity_video;
import com.example.administrator.capacityhome.smarthome.video.activity.ShowPicActivity;
import com.example.administrator.capacityhome.smarthome.video.activity.ToneListActity;
import com.example.administrator.capacityhome.smarthome.video.activity.XMMainActivity;
import com.example.administrator.capacityhome.smarthome.video.appteam.DatabaseManager;
import com.example.administrator.capacityhome.smarthome.video.obj.MyCamera;
import com.example.administrator.capacityhome.smarthome.video.push.SharedPrefsStrListUtil;
import com.tutk.IOTC.AVIOCTRLDEFs;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by wand on 16/9/22.
 */
public class TPNSManager {


    private static DpnsRequestManager dpnsRequestManager;
    private static final String TAG = TPNSManager.class.getSimpleName();

    public static final String CHINA_PUSH = "https://pushcn.iotcplatform.com:7380/apns/apns.php?";
    public static final String FOREIGN_PUSH = "https://push.iotcplatform.com:7380/tpns?";
    public static final String PUSH_PRE_NAME = "push_setting";

    public static final int TPNS_TYPE_REGISTER = 0;
    public static final int TPNS_TYPE_MAPPING = 1;
    public static final int TPNS_TYPE_UNMAPPING = 2;
    public static final int TPNS_TYPE_SYNCMAPPING = 3;

    public static String URL = FOREIGN_PUSH;
    public static boolean mInland;
    public static String PUSH_UDID;

    static {
        dpnsRequestManager = new DpnsRequestManager();
    }

    public static String getUDID(Context context) {

        if (PUSH_UDID == null) {
            SharedPreferences setting = context.getSharedPreferences(PUSH_PRE_NAME, Context.MODE_PRIVATE);
            PUSH_UDID = setting.getString("device_imei", null);

            if (PUSH_UDID == null) {
                String serial_number = null;
                if (Build.VERSION.SDK_INT >= 9) {
                    serial_number = Build.SERIAL;
                }
                if (serial_number == null || serial_number.equals("")) {
                    int rendom_number = (int) (Math.random() * 1 + 1) + (int) (Math.random() * 10 + 1) + (int) (Math.random() * 100 + 1) + (int) (Math.random() * 1000 + 1) +
                            (int) (Math.random() * 10000 + 1) + (int) (Math.random() * 100000 + 1) + (int) (Math.random() * 1000000 + 1) + (int) (Math.random() * 10000000 + 1);
                    if (rendom_number < 10000000) {
                        rendom_number = rendom_number + 10000000;
                    }
                    serial_number = String.valueOf(rendom_number);
                }

                String mac_address = null;
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                if (wifiManager != null) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    if (wifiInfo != null) {
                        mac_address = wifiInfo.getMacAddress();
                    }
                }
                if (mac_address == null || mac_address.equals("")) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
                    Date curDate = new Date(System.currentTimeMillis());
                    mac_address = formatter.format(curDate);
                }

                String[] subSerialNum = new String[2];
                subSerialNum[0] = serial_number.substring(0, 4);
                subSerialNum[1] = serial_number.substring(4);
                mac_address = mac_address.replace(":", "");
                String[] subMacAddr = new String[2];
                subMacAddr[0] = mac_address.substring(0, 6);
                subMacAddr[1] = mac_address.substring(6);

                PUSH_UDID = "AN" + subMacAddr[0] + subSerialNum[1] + subMacAddr[1] + subSerialNum[0];
                setting.edit().putString("device_imei", PUSH_UDID).apply();
            }
        }

        return PUSH_UDID;
    }

    public static String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static String get_model() {
        return Build.MODEL.replace(" ", "%20");
    }


    //mapping all and unmapping remove
    public static void check_mapping_list(final Context context) {

        //首先mapping列表的
        for (final MyCamera camera : XMMainActivity.CameraList) {


            dpnsRequestManager.mappDevice(camera.getUID(), camera.getPassword(), new DpnsRequestManager.RequestListener() {
                @Override
                public void onSuccess() {
//                    Toast.makeText(context, "绑定成功", Toast.LENGTH_SHORT).show();
                    MqttInitManeger.getInstance().subscribeToTopic(camera.getUID());
                }

                @Override
                public void onFailed(int i, String error) {
//                    Toast.makeText(context, "绑定失败, result[" + i + "]" + error + "|" + camera.getUID(), Toast.LENGTH_SHORT).show();
                }
            });
//            if (mInland) {
//                JPushUtils.mapping(context, camera.getUID());
//            } else {
//                GooglePushUtils.mapping(context, camera.getUID());
//            }
        }

        //再unmapping列表没有的
        List<String> uidSet = SharedPrefsStrListUtil.initUIDList(context);
        for (final String setUID : uidSet) {
            boolean isMapping = false;
            for (MyCamera camera : XMMainActivity.CameraList) {
                if (setUID.equalsIgnoreCase(camera.getUID())) {
                    isMapping = true;
                    break;
                }
            }
            if (!isMapping) {//删除时候没有正常unmapping

                dpnsRequestManager.rmMappDevice(setUID, new DpnsRequestManager.RequestListener() {
                    @Override
                    public void onSuccess() {
                        try {
                            MqttInitManeger.getInstance().unsubscribeTopic(setUID);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                        Toast.makeText(context, "移除绑定成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int i, String error) {
//                        Toast.makeText(context, "移除绑定失败, result[" + i + "]", Toast.LENGTH_SHORT).show();
                    }
                });
//				if (mInland) {
//					JPushUtils.unmapping(context, setUID);
//				} else {
//					GooglePushUtils.unmapping(context, setUID);
//				}
            }
        }
    }


    public static void showNotification(Context ctx, String os, String dev_uid, String dev_alert, String event_type, String sound, String event_time, int loopnum, String picPath) {

        try {
            String notificationString = os + " " + dev_alert;
            int requestCode = (int) System.currentTimeMillis();
            NotificationManager manager = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
            Intent intent = new Intent(ctx, ShowPicActivity.class);
            Bundle extras = new Bundle();
            extras.putString("dev_uid", dev_uid);
            extras.putString("event_type", event_type);
            extras.putString("pic_path", picPath);
            intent.putExtras(extras);
            PendingIntent pendingIntent = PendingIntent.getActivity(ctx, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            Notification.Builder builder = new Notification.Builder(ctx);
            builder.setContentIntent(pendingIntent)
                    .setSmallIcon(Const.LOGO_RES)
                    .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(),Const.LOGO_RES))
                    .setAutoCancel(true)
//					.setDefaults(Notification.DEFAULT_ALL)
                    .setTicker(dev_alert)
                    .setContentTitle(ctx.getText(R.string.app_name))
                    .setContentText(notificationString);

            AudioManager audioManager = (AudioManager) ctx.getSystemService(AUDIO_SERVICE);
            int STREAM_NOTIFICATION = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

            boolean isOpen = ToneListActity.getPushToneSwitch(ctx);
            int pos = ToneListActity.getPushTonePos(ctx);

//            if (isOpen) {
            int cur = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (cur <= 0) {
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume / 2, 0);
            }
//            Uri uriSound = getUriSound(ctx, pos);
//            playRingtone(ctx, uriSound);
            long[] pattern = {0, 100, 1000};
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
            builder.setVibrate(pattern);
//            builder.setSound(uriSound);
//				builder.setSound(uriSound);
//            } else {
//                builder.setDefaults(Notification.DEFAULT_VIBRATE);
//            }
            MediaPlayerUtil.play(ctx, getResourceSound(ctx, pos), loopnum);
            Notification notification = builder.build();
            manager.notify(requestCode, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showNotification(Context ctx, String os, String dev_uid, String dev_alert, String event_type, int loopNum) {

        try {
            String notificationString = os + " " + dev_alert;
            int requestCode = (int) System.currentTimeMillis();
            NotificationManager manager = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
            Intent intent = new Intent(ctx, MainActivity_video.class);
            Bundle extras = new Bundle();
            extras.putString("dev_uid", dev_uid);
            extras.putString("event_type", event_type);
            intent.putExtras(extras);
            PendingIntent pendingIntent = PendingIntent.getActivity(ctx, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder builder = new Notification.Builder(ctx);
            builder.setContentIntent(pendingIntent)
                    .setSmallIcon(Const.LOGO_RES)
                    .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), Const.LOGO_RES))
                    .setAutoCancel(true)
//					.setDefaults(Notification.DEFAULT_ALL)
                    .setTicker(dev_alert)
                    .setContentTitle(ctx.getText(R.string.app_name))
                    .setContentText(notificationString);

            AudioManager audioManager = (AudioManager) ctx.getSystemService(AUDIO_SERVICE);
            int STREAM_NOTIFICATION = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

            boolean isOpen = ToneListActity.getPushToneSwitch(ctx);
            int pos = ToneListActity.getPushTonePos(ctx);

//            if (isOpen) {
            int cur = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (cur <= 0) {
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume / 2, 0);
            }
//            playRingtone(ctx, uriSound);
            long[] pattern = {0, 100, 1000};
//            builder.setDefaults(Notification.DEFAULT_VIBRATE);
            builder.setVibrate(pattern);
//            builder.setSound(uriSound);
            MediaPlayerUtil.play(ctx, getResourceSound(ctx, pos), loopNum);
//				builder.setSound(uriSound);
//            } else {
//                builder.setDefaults(Notification.DEFAULT_VIBRATE);
//            }
            Notification notification = builder.build();
            manager.notify(requestCode, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getResourceSound(Context ctx, int pos) {
        int resource = R.raw.alarm01;
        switch (pos) {
            case 0: {
                resource = R.raw.alarm01;
            }
            break;
            case 1: {
                resource = R.raw.alarm02;
            }
            break;
            case 2: {
                resource = R.raw.dingdong01;
            }
            break;
            case 3: {
                resource = R.raw.dingdong02;
            }
            break;
            case 4: {
                resource = R.raw.dingling;
            }
            break;
            case 5: {
                resource = R.raw.doorring01;
            }
            break;
            case 6: {
                resource = R.raw.ring01;
            }
            break;
            case 7: {
                resource = R.raw.ring02;
            }
            break;
            case 8: {
                resource = R.raw.ring03;
            }
            break;
            default: {
                resource = R.raw.dingdong01;
            }

        }
        return resource;
    }

    public static Uri getUriSound(Context ctx, int pos) {
        Uri uri = null;
        switch (pos) {
            case 0: {
                uri = Uri.parse("android.resource://" + ctx.getPackageName() + "/" + R.raw.alarm01);
            }
            break;
            case 1: {
                uri = Uri.parse("android.resource://" + ctx.getPackageName() + "/" + R.raw.alarm02);
            }
            break;
            case 2: {
                uri = Uri.parse("android.resource://" + ctx.getPackageName() + "/" + R.raw.dingdong01);
            }
            break;
            case 3: {
                uri = Uri.parse("android.resource://" + ctx.getPackageName() + "/" + R.raw.dingdong02);
            }
            break;
            case 4: {
                uri = Uri.parse("android.resource://" + ctx.getPackageName() + "/" + R.raw.dingling);
            }
            break;
            case 5: {
                uri = Uri.parse("android.resource://" + ctx.getPackageName() + "/" + R.raw.doorring01);
            }
            break;
            case 6: {
                uri = Uri.parse("android.resource://" + ctx.getPackageName() + "/" + R.raw.ring01);
            }
            break;
            case 7: {
                uri = Uri.parse("android.resource://" + ctx.getPackageName() + "/" + R.raw.ring02);
            }
            break;
            case 8: {
                uri = Uri.parse("android.resource://" + ctx.getPackageName() + "/" + R.raw.ring03);
            }
            break;
            default: {
                uri = Uri.parse("android.resource://" + ctx.getPackageName() + "/" + R.raw.dingdong01);
            }

        }
        return uri;
    }

    private static MediaPlayer mMediaPlayer;

    public static void playRingtone(Context context, Uri toneUri) {
        try {
            if (mMediaPlayer != null)
                mMediaPlayer.release();
            mMediaPlayer = MediaPlayer.create(context, toneUri);
            mMediaPlayer.setLooping(false);

            try {
                mMediaPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }

            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean foundInDatabase(Context context, String uid) {

        if (uid == null || context == null) {
            return false;
        }

        boolean found = false;
        DatabaseManager manager = new DatabaseManager(context);
        SQLiteDatabase db = manager.getReadableDatabase();
        Cursor cursor = db.query(DatabaseManager.TABLE_DEVICE,
                new String[]{"dev_uid"}, null,
                null, null, null, "_id LIMIT "
                        + MainActivity_video.CAMERA_MAX_LIMITS);

        while (cursor.moveToNext()) {
            String dev_uid = cursor.getString(0);
            if (uid.equals(dev_uid)) {
                found = true;
                break;
            }
        }
        cursor.close();
        db.close();
        return found;
    }

    public static boolean isNotificationEnabled(Context context) {

        if (Build.VERSION.SDK_INT >= 21) {
            final String CHECK_OP_NO_THROW = "checkOpNoThrow";
            final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

            AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = context.getApplicationInfo();
            String pkg = context.getApplicationContext().getPackageName();
            int uid = appInfo.uid;

            Class appOpsClass = null;
     /* Context.APP_OPS_MANAGER */
            try {
                appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
                int value = (Integer) opPostNotificationValue.get(Integer.class);
                boolean isAllow = (Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED;
                Log.i(TAG, "isNotificationEnabled: isAllow" + isAllow);
                return isAllow;

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoClassDefFoundError e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public final static String getEventType(Context context, int eventType, boolean isSearch) {

        String result = "";

        switch (eventType) {
            case AVIOCTRLDEFs.AVIOCTRL_EVENT_ALL:
                result = isSearch ? context.getText(R.string.evttype_all).toString() : context.getText(R.string.evttype_fulltime_recording).toString();
                break;

            case AVIOCTRLDEFs.AVIOCTRL_EVENT_MOTIONDECT:
                result = context.getText(R.string.evttype_motion_detection).toString();
                break;

            case AVIOCTRLDEFs.AVIOCTRL_EVENT_VIDEOLOST:
                result = context.getText(R.string.evttype_video_lost).toString();
                break;

            case AVIOCTRLDEFs.AVIOCTRL_EVENT_IOALARM:
                result = context.getText(R.string.evttype_io_alarm).toString();
                break;

            case AVIOCTRLDEFs.AVIOCTRL_EVENT_MOTIONPASS:
                result = context.getText(R.string.evttype_motion_pass).toString();
                break;

            case AVIOCTRLDEFs.AVIOCTRL_EVENT_VIDEORESUME:
                result = context.getText(R.string.evttype_video_resume).toString();
                break;

            case AVIOCTRLDEFs.AVIOCTRL_EVENT_IOALARMPASS:
                result = context.getText(R.string.evttype_io_alarm_pass).toString();
                break;

            case AVIOCTRLDEFs.AVIOCTRL_EVENT_EXPT_REBOOT:
                result = context.getText(R.string.evttype_expt_reboot).toString();
                break;

            case AVIOCTRLDEFs.AVIOCTRL_EVENT_SDFAULT:
                result = context.getText(R.string.evttype_sd_fault).toString();
                break;
            default:
                result = context.getText(R.string.evttype_motion_detection).toString();
                break;
        }

        return result;
    }
}
