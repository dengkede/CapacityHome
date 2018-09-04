package com.example.administrator.capacityhome.smarthome.video.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.PtrHandler;
import com.chanven.lib.cptr.header.MaterialHeader;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.dabin.dpns.DpnsRequestManager;
import com.dabin.dpns.push.MqttInitManeger;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.WebViewUrlActivity;
import com.example.administrator.capacityhome.selfcenter.BasicInfomationActivity;
import com.example.administrator.capacityhome.smarthome.video.appteam.DatabaseManager;
import com.example.administrator.capacityhome.smarthome.video.appteam.XM_AVIOCTRLDEFs;
import com.example.administrator.capacityhome.smarthome.video.obj.DeviceInfo;
import com.example.administrator.capacityhome.smarthome.video.obj.MyCamera;
import com.example.administrator.capacityhome.smarthome.video.push.SharedPrefsStrListUtil;
import com.example.administrator.capacityhome.smarthome.video.util.ConnectUtil;
import com.example.administrator.capacityhome.smarthome.video.util.IPUtils;
import com.example.administrator.capacityhome.smarthome.video.util.InfoUtil;
import com.example.administrator.capacityhome.smarthome.video.util.IntentServiceActivity;
import com.example.administrator.capacityhome.smarthome.video.util.OnRequestListener;
import com.example.administrator.capacityhome.smarthome.video.util.TPNSManager;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;
import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IOTCAPIs;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.OnLineResultCBListener;
import com.tutk.IOTC.Packet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import adapter.SmartHomeListAdapter;
import http.RequestTag;
import utils.ACache;
import utils.DisplayUtil;
import utils.FormatUtil;
import utils.Md5;
import utils.StringUtils;
import utils.TimeUtilsDate;
import widget.DefaultLoadMoreViewFooter_normal;


public class MainActivity_video extends BaseActivity implements IRegisterIOTCListener, OnLineResultCBListener, OnLoadMoreListener, PtrHandler {
    public static final String TAG = "MainActivity";
    public static final String MAPPING = "mappReceiver";
    public static final int REQUEST_CODE_CAMERA_ADD = 4;
    public static final int REQUEST_CODE_CAMERA_VIEW = 1;
    public static final int REQUEST_CODE_CAMERA_EDIT = 2;
    public static final int CAMERA_MAX_LIMITS = 128;
    public static int nShowMessageCount = 0;
    private int jj = 1;
    //    private DeviceListAdapter adapter;
//    private ListView listView;
    private TextView tv_addDevice;
    private Button logout;
    private ImageView imTpnsreg;
    private ACTIVITY mACTIVITY = ACTIVITY.NORMAL;
    private Timer mCheckDeviceOnLineTimer;
    private TimerTask mCheckDeviceOnLineTimerTask;
    private final long CHECK_TIME = 5 * 1000;
    private List<DeviceResult.DataBean> dataList;
    private IPUtils mIPUtils;
    private String door_id;
    private long addtime;
    private DpnsRequestManager dpnsRequestManager;

    /**
     * 新加
     *
     * @param result
     * @param userData
     */
    private ListView listView_new;
    private SmartHomeListAdapter adapter_new;
    private TextView tv_homeCount;//保护天数
    private List<JSONObject> list;
    private LinearLayout layout_video;
    private PtrClassicFrameLayout pcf_container;
    private View viewParentHead;
    private Bundle bundle;
    private TextView smartList_tv_title;
    private TextView layout_empty;
    private int page = 1;
    private boolean isUpdate = true;
    private String door_key = null;
    private ACache aCache;
    private boolean tishi = true;//true给一次提示
    private PopupWindow popupWindow_connnect;
    private RelativeLayout relayout_parent;
    private TextView tv_phoneNumber;
    private EditText et_password;
    private TextView tv_connnect;
    private String uid;
    private boolean isConnect = true;//继续自动重连
    @Override
    public void onLineResultCB(int result, byte[] userData) {
        for (MyCamera camera : XMMainActivity.CameraList) {
            if (camera.getUID().equals(new String(userData))) {
                camera.Check_Device_Result = result;
                break;
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // adapter.notifyDataSetChanged();
            }
        });
    }

    private enum ACTIVITY {
        TO_LIVEVIEW,
        TO_EDIT,
        NORMAL;
    }

    public BroadcastReceiver tpnsReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MAPPING)) {
                Bundle bundle = intent.getExtras();
                String uid = bundle.getString("uid", null);
                int type = bundle.getInt("type", -1);

                Log.i("TPNSTask", " tpnsReceiver onReceive " + uid + " " + type);
                switch (type) {
                    case TPNSManager.TPNS_TYPE_REGISTER: {
                        imTpnsreg.setImageResource(R.mipmap.ic_light_tpns_n);
                    }
                    break;
                    case TPNSManager.TPNS_TYPE_MAPPING: {
                        for (MyCamera camera : XMMainActivity.CameraList) {
                            if (camera.getUID().equals(uid)) {
                                camera.isMapping = true;
                                // adapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                    break;
                    case TPNSManager.TPNS_TYPE_UNMAPPING: {
                        SharedPrefsStrListUtil.deleteUID(MainActivity_video.this, uid);
                    }
                    break;
                    case TPNSManager.TPNS_TYPE_SYNCMAPPING: {

                    }
                    break;
                }
            }
        }
    };

    @Override
    public void errorState(String dev_uid, MyCamera camera) {
        if (connect_count < 3&&isConnect) {
            connectCamera(dev_uid, ACTIVITY.TO_LIVEVIEW, false);
            connect_count++;
            Log.i("AAA", "重新連線 次數： " + connect_count);
        } else {
            dismissLoadingView2();
            camera.disconnect();
            stopTimeOutTask();
            hideProgressDlg();
            try {
                Toast.makeText(MainActivity_video.this, "连接超时请稍后重试", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // showToast(getString(R.string.txt_communication_failed));
        }
    }

    @Override
    public void finish_TIMEOUT() {
        for (MyCamera camera : XMMainActivity.CameraList) {
            if (camera.getMSID() >= 0) {
                camera.unregisterIOTCListener(MainActivity_video.this);
                camera.disconnect();
            }
        }
        uninit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jj = 2;
        setContentView(R.layout.activity_main_video);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
        MyCamera.init();
        //初始化数据,取数据库数据
        //   XMMainActivity.initCameraList(this, true);
        //  监听最近任务建
        HomeReceiver homeReceiver = new HomeReceiver();
        IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(homeReceiver, homeFilter);
        //打开喇叭
        OpenSpeaker();
//        //网络请求状态检测
        IntentFilter filter = new IntentFilter();
        filter.addAction(MAPPING);
        registerReceiver(tpnsReceiver, filter);
        //获取ip地址 开启push
        mIPUtils = new IPUtils(this);
//        mIPUtils.startGetIP();
        //check设备状态
        startCheckTask();
//        //检查是否开启通知功能
//        if (!TPNSManager.isNotificationEnabled(this)) {
//            AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(this);
//            dlgBuilder.setIcon(android.R.drawable.ic_dialog_alert);
//            dlgBuilder.setTitle(this.getString(R.string.message));
//            dlgBuilder.setMessage(this.getString(R.string.ntf_no_open_notification));
//            dlgBuilder.setPositiveButton(this.getString(R.string.ok),
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent();
//                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                            intent.setData(Uri.parse("package:" + MainActivity_video.this.getPackageName()));
//                            MainActivity_video.this.startActivity(intent);
//                        }
//                    });
//            dlgBuilder.show();
//        }
//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        actionBar.setCustomView(R.layout.titlebar);
//        TextView tv = (TextView) this.findViewById(R.id.bar_text);
//        tv.setText(getText(R.string.txt_ylk_device_list));
//        ImageButton btnEdit = (ImageButton) findViewById(R.id.bar_right_btn);
//        btnEdit.setVisibility(View.GONE);
//        ImageButton ibtn_back = (ImageButton) findViewById(R.id.bar_left_btn);
//        ibtn_back.setVisibility(View.GONE);
//        imTpnsreg = (ImageView) findViewById(R.id.imTpnsreg);
//        imTpnsreg.setVisibility(View.VISIBLE);
        setupView();
//        //push跳转
//        Bundle extras = this.getIntent().getExtras();
//        Log.i("Zed", "XMMainActivity extras = null is " + (extras == null));
//        if (extras != null) {
//            eventToActivity(this.getIntent());
//        }


        Map<String, Object> map = new HashMap<>();
        map.put("type", "UserHandler");
        map.put("action", "getBindDevices");
        final Map<String, String> data = new HashMap<>();
        data.put("userPid", InfoUtil.getString(this, "pid"));
        data.put("maxResult", "20");
        data.put("firstResult", "0");
        org.json.JSONObject jsonObject = new org.json.JSONObject(data);
        map.put("data", jsonObject);

        ConnectUtil.request("/smart/device/select", map, new OnRequestListener() {
            @Override
            public void onRequestSuccess(String result) {
                Log.i("getBindDevices", "result:" + result);
                DeviceResult deviceResult = (DeviceResult) FastJsonUtil.get(result, DeviceResult.class);
                dataList = deviceResult.getData();
                /* add value to data base */
                DatabaseManager manager = new DatabaseManager(MainActivity_video.this);
                for (final DeviceResult.DataBean bean :
                        dataList) {
                    boolean duplicated = false;
                    for (MyCamera camera : XMMainActivity.CameraList) {
                        if (bean.getDevicePid().equalsIgnoreCase(camera.getUID())) {
                            duplicated = true;
                            break;
                        }
                    }
                    if (!duplicated) {
                        long db_id = manager.addDevice(bean.getName(), bean.getDevicePid(), "", "", "admin", "888888", 3, 0, 1);
                        addDevice(bean.getName(), bean.getDevicePid(), "admin", "888888", db_id, 0, 0);

                    }
                }
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onFail() {
                super.onFail();
            }
        });
//        dpnsRequestManager = new DpnsRequestManager();
//        DpnsMqttManager.getInstance().setUserName(InfoUtil.getString(this, "phone"));
//        dpnsRequestManager.appRegister(null, new DpnsRequestManager.AppRegisterListener() {
//            @Override
//            public void onSuccess(String s) {
////                Toast.makeText(MainActivity_video.this, "设备注册成功", Toast.LENGTH_SHORT).show();
//                mIPUtils.startGetIP();
//                try {
//                    DpnsMqttManager.getInstance().mqttConnect();
//                } catch (Exception e) {
//                    LogUtils.e(TAG, "connect mqtt server error");
//                }
//            }
//
//            @Override
//            public void onFailed(int i, String error) {
////                LogUtils.e(TAG, "app register error[" + error + "]");
//                Log.i("registError", "app register error[" + error + "]");
//                Toast.makeText(MainActivity_video.this, "设备注册失败, result[" + i + "], error[" + error + "]", Toast.LENGTH_SHORT).show();
//            }
//        });


        Intent intent = new Intent(MainActivity_video.this, IntentServiceActivity.class);
        startService(intent);
        //新加
        bundle = getIntent().getBundleExtra("bundle");
        initView();
        initFrameLayout();

    }

    private DeviceInfo toEditDev = null;

    private void addDevice() {
        Intent startIntent = new Intent();
        Bundle extras = new Bundle();
        startIntent.putExtras(extras);
        startIntent.setClass(MainActivity_video.this, AddDeviceActivity.class);
        startActivityForResult(startIntent, REQUEST_CODE_CAMERA_ADD);
    }

    private void eventToActivity(Intent mIntent) {

        Bundle extras = mIntent.getExtras();
        if (extras == null) {
            return;
        }
        String dev_uid = extras.getString("dev_uid");
        String event_type = extras.getString("event_type");
        Intent intent = null;

        Log.i("Zed", "BaiduPush dev_uid =  " + dev_uid + "  event_type = " + event_type);

        for (DeviceInfo mDev : XMMainActivity.DeviceList) {
            if (mDev.UID.equalsIgnoreCase(dev_uid)) {
                if ("2000".equalsIgnoreCase(event_type)) {
                    intent = new Intent(MainActivity_video.this, LiveViewActivity.class);
                    extras.putString("dev_uid", mDev.UID);
                    extras.putString("dev_uuid", mDev.UUID);
                    extras.putString("dev_nickname", mDev.NickName);
                    extras.putString("conn_status", mDev.Status);
                    extras.putString("view_acc", mDev.View_Account);
                    extras.putString("view_pwd", mDev.View_Password);
                    extras.putInt("camera_channel", mDev.ChannelIndex);
                    extras.putBoolean("mIsIncoming", true);
                    intent.putExtras(extras);
                    MainActivity_video.this.startActivityForResult(intent, REQUEST_CODE_CAMERA_VIEW);
                }
                break;
            }
        }

    }

    private void addDevice(String dev_nickname, String dev_uid, String view_acc, String view_pwd, long db_id, int channel, int dev_type) {

        MyCamera camera = new MyCamera(MainActivity_video.this, dev_nickname, dev_uid, view_acc, view_pwd);
        DeviceInfo dev = new DeviceInfo(db_id, camera.getUUID(),
                dev_nickname, dev_uid, view_acc, view_pwd, "",
                3, channel, null, dev_type, (int) 1, 0);
        XMMainActivity.DeviceList.add(dev);

        camera.registerIOTCListener(this);

        XMMainActivity.CameraList.add(camera);
//        Bundle extras = new Bundle();
//        extras.putString("dev_uid", dev.UID);
//        extras.putString("dev_uuid", dev.UUID);
//        extras.putString("dev_nickname", dev.NickName);
//        extras.putString("view_acc", dev.View_Account);
//        extras.putString("view_pwd", dev.View_Password);
//        extras.putInt("camera_channel", dev.ChannelIndex);
//        extras.putString("conn_status", dev.Status);
//        Intent intent = new Intent();
//        intent.putExtras(extras);
//        intent.setClass(MainActivity_video.this, LiveViewActivity.class);
//        startActivity(intent);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAMERA_ADD) {
            switch (resultCode) {
                case RESULT_OK:
                    Bundle extras = data.getExtras();
                    long db_id = extras.getLong("db_id");
                    String dev_nickname = extras.getString("dev_nickname");
                    final String dev_uid = extras.getString("dev_uid");
                    String view_acc = extras.getString("view_acc");
                    String view_pwd = extras.getString("view_pwd");
                    int dev_type = extras.getInt("dev_type");
                    int event_notification = 3;
                    int channel = extras.getInt("camera_channel");
                    addDevice(dev_nickname, dev_uid, view_acc, view_pwd, db_id, channel, dev_type);
//                    MyCamera camera = new MyCamera(MainActivity_video.this, dev_nickname, dev_uid, view_acc, view_pwd);
//                    DeviceInfo dev = new DeviceInfo(db_id, camera.getUUID(),
//                            dev_nickname, dev_uid, view_acc, view_pwd, "",
//                            event_notification, channel, null, dev_type, (int) 1, 0);
//                    XMMainActivity.DeviceList.add(dev);
//
//                    camera.registerIOTCListener(this);
//
//                    XMMainActivity.CameraList.add(camera);

                    //  adapter.notifyDataSetChanged();

//                    if (TPNSManager.mInland) {
//                        JPushUtils.mapping(MainActivity_video.this, dev_uid);
//                    } else {
//                        GooglePushUtils.mapping(MainActivity_video.this, dev_uid);
//                    }

//                    dpnsRequestManager.mappDevice(dev_uid, "", new DpnsRequestManager.RequestListener() {
//                        @Override
//                        public void onSuccess() {
////                            Toast.makeText(MainActivity_video.this, "绑定成功", Toast.LENGTH_SHORT).show();
//                            MqttInitManeger.getInstance().subscribeToTopic(dev_uid);
//                        }
//
//                        @Override
//                        public void onFailed(int i, String error) {
////                            Toast.makeText(MainActivity_video.this, "绑定失败, result[" + i + "]", Toast.LENGTH_SHORT).show();
//                        }
//                    });

                    break;
            }
        } else if (requestCode == REQUEST_CODE_CAMERA_VIEW) {
            //从视屏界面返回
            switch (resultCode) {
                case RESULT_OK:
                    Bundle extras = data.getExtras();
                    String dev_uuid = extras.getString("dev_uuid");
                    String dev_uid = extras.getString("dev_uid");
                    byte[] byts = extras.getByteArray("snapshot");
                    int channelIndex = extras.getInt("camera_channel");
                    if (extras.getBoolean("isJietu")) {
                        pcf_container.autoRefresh();
                    }
                    Bitmap snapshot = null;
                    if (byts != null && byts.length > 0)
                        snapshot = DatabaseManager.getBitmapFromByteArray(byts);

                    for (int i = 0; i < XMMainActivity.DeviceList.size(); i++) {

                        if (dev_uuid.equalsIgnoreCase(XMMainActivity.DeviceList.get(i).UUID)
                                && dev_uid.equalsIgnoreCase(XMMainActivity.DeviceList.get(i).UID)) {

                            XMMainActivity.DeviceList.get(i).ChannelIndex = channelIndex;

                            if (snapshot != null)
                                XMMainActivity.DeviceList.get(i).Snapshot = snapshot;

                            // adapter.notifyDataSetChanged();
                            break;
                        }
                    }

                    break;
            }
        } else if (requestCode == REQUEST_CODE_CAMERA_EDIT) {
            switch (resultCode) {
                case RESULT_OK:
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        String devUID = extras.getString("devUID");
                        removeCamera(devUID);
                    }
                    MainActivity_video.showAlert(this,
                            getText(R.string.tips_warning),
                            getText(R.string.tips_remove_camera_ok),
                            getText(R.string.ok));
                    break;

                case RESULT_CANCELED:
                    break;

                case RESULT_FIRST_USER:
                    if (toEditDev.UID == null)
                        return;

                    mACTIVITY = ACTIVITY.NORMAL;

                    for (MyCamera cam : XMMainActivity.CameraList) {
                        if (cam.getUID().equals(toEditDev.UID)) {
                            cam.disconnect();
                            break;
                        }
                    }
                    for (DeviceInfo info : XMMainActivity.DeviceList) {
                        if (info.UID.equals(toEditDev.UID)) {
                            info.Status = getText(R.string.connstus_disconnect).toString();
                            info.Online = false;
                            break;
                        }
                    }
                    // adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    private void removeCamera(final String uid) {

        MyCamera camera = null;
        DeviceInfo deviceInfo = null;

        for (MyCamera myCamera : XMMainActivity.CameraList) {
            if (myCamera.getUID().equals(uid)) {
                camera = myCamera;
                break;
            }
        }

        for (DeviceInfo info : XMMainActivity.DeviceList) {
            if (info.UID.equals(uid)) {
                deviceInfo = info;
                break;
            }
        }

        if (camera == null || deviceInfo == null) {
            return;
        }

//        if (TPNSManager.mInland) {
//            JPushUtils.unmapping(MainActivity_video.this, camera.getUID());
//        } else {
//            GooglePushUtils.unmapping(MainActivity_video.this, camera.getUID());
//        }

        String pid = null;
        if (dataList != null) {
            for (int i = 0; i < dataList.size(); i++) {
                if (dataList.get(i).getDevicePid().equalsIgnoreCase(camera.getUID())) {
                    pid = dataList.get(i).getPid();
                    break;
                }
            }
        }

        final String finalPid = pid;
        dpnsRequestManager.rmMappDevice(uid, new DpnsRequestManager.RequestListener() {
            @Override
            public void onSuccess() {
                try {
                    MqttInitManeger.getInstance().unsubscribeTopic(uid);

                    final Map<String, Object> map = new HashMap<>();
                    map.put("type", "DeviceHandler");
                    map.put("action", "del");
                    Map<String, String> innerData = new HashMap<>();
                    innerData.put("pid", finalPid);
                    JSONObject jsonObject = new JSONObject(innerData);
                    map.put("data", jsonObject);
                    DatabaseManager manager = new DatabaseManager(MainActivity_video.this);
                    manager.removeDeviceByUID(uid);
                    ConnectUtil.request("/smart/device/del", map, new OnRequestListener() {
                        @Override
                        public void onRequestSuccess(String result) {
//                            showToast(result);
                        }

                        @Override
                        public void onFail() {
                            super.onFail();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int i, String error) {
            }
        });
        camera.unregisterIOTCListener(MainActivity_video.this);
        camera.disconnect();//must below unregisterIOTCListener


        DatabaseManager manager = new DatabaseManager(MainActivity_video.this);
        SQLiteDatabase db = manager.getReadableDatabase();
        Cursor cursor = db.query(DatabaseManager.TABLE_SNAPSHOT,
                new String[]{"_id", "dev_uid", "file_path", "time"},
                "dev_uid = '" + camera.getUID() + "'",
                null, null, null,
                "_id LIMIT " + MainActivity_video.CAMERA_MAX_LIMITS);

        while (cursor.moveToNext()) {
            String file_path = cursor.getString(2);
            File file = new File(file_path);
            if (file.exists())
                file.delete();
        }
        cursor.close();
        db.close();

        manager.removeSnapshotByUID(camera.getUID());
        manager.removeEalinkRecordByUID(camera.getUID());
        manager.removeDeviceByUID(camera.getUID());
        XMMainActivity.DeviceList.remove(deviceInfo);
        XMMainActivity.CameraList.remove(camera);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //adapter.notifyDataSetChanged();
            }
        });
    }


    public static String getCheck_Device_Result(Context context, int Check_Device_Result) {
        String result = null;
        if (Check_Device_Result == -1) {
            result = context.getText(R.string.connstus_connecting).toString();//正在联机
        } else if (Check_Device_Result >= 0) {
            result = context.getText(R.string.connstus_connected).toString();//已联机
        } else if (Check_Device_Result == IOTCAPIs.IOTC_ER_UNKNOWN_DEVICE || Check_Device_Result == IOTCAPIs.IOTC_ER_UNLICENSE
                || Check_Device_Result == IOTCAPIs.IOTC_ER_CAN_NOT_FIND_DEVICE
                || Check_Device_Result == IOTCAPIs.IOTC_ER_FAIL_SETUP_RELAY || Check_Device_Result == IOTCAPIs.IOTC_ER_DEVICE_OFFLINE
                ) {
            result = context.getText(R.string.connstus_unknown_device).toString();//未知设备
        } else if (Check_Device_Result == IOTCAPIs.IOTC_ER_TIMEOUT || Check_Device_Result == IOTCAPIs.IOTC_ER_REMOTE_TIMEOUT_DISCONNECT) {
            result = context.getText(R.string.connstus_disconnect).toString();//未联机
        } else if (Check_Device_Result == IOTCAPIs.IOTC_ER_DEVICE_IS_SLEEP) {
            result = context.getText(R.string.connstus_sleep).toString();//睡眠中
        } else {
            result = context.getText(R.string.connstus_connection_failed).toString();//联机失败
        }
        return result;
    }

    public static void showAlert(Context context, CharSequence title,
                                 CharSequence message, CharSequence btnTitle) {

        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(context);
        dlgBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        dlgBuilder.setTitle(title);
        dlgBuilder.setMessage(message);
        dlgBuilder.setPositiveButton(btnTitle,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    private void uninit() {
        Intent intent = new Intent(MainActivity_video.this, IntentServiceActivity.class);
        stopService(intent);

        stopTimeOutTask();
        hideProgressDlg();
        stopCheckTask();

        try {
            unregisterReceiver(tpnsReceiver);
        } catch (Exception e) {
        }

        if (mIPUtils != null) {
            mIPUtils.onDestroy();
        }

        MyCamera.uninit();
        android.os.Process.killProcess(android.os.Process.myPid());
        finish();
    }

    private void stopCheckTask() {
        if (mCheckDeviceOnLineTimer != null) {
            mCheckDeviceOnLineTimerTask.cancel();
            mCheckDeviceOnLineTimer.cancel();
            mCheckDeviceOnLineTimerTask = null;
            mCheckDeviceOnLineTimer = null;
        }
    }

    private String Tag = "AAA";
    private int send_SleepCount = 0;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle bundle = msg.getData();
            String requestDevice = bundle.getString("requestDevice");

            byte[] data = bundle.getByteArray("data");

            DeviceInfo device = null;
            MyCamera camera = null;

            for (int i = 0; i < XMMainActivity.DeviceList.size(); i++) {
                if (XMMainActivity.DeviceList.get(i).UUID.equalsIgnoreCase(requestDevice)) {
                    device = XMMainActivity.DeviceList.get(i);
                    break;
                }
            }

            for (int i = 0; i < XMMainActivity.CameraList.size(); i++) {
                if (XMMainActivity.CameraList.get(i).getUUID().equalsIgnoreCase(requestDevice)) {
                    camera = XMMainActivity.CameraList.get(i);
                    break;
                }
            }

            switch (msg.what) {

                case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVICESLEEP_RESP:
                    if (send_SleepCount > 0) {
                        send_SleepCount--;
                    }

                    if (send_SleepCount == 0) {
                        uninit();
                    }

                    break;

                case Camera.CONNECTION_STATE_CONNECTING:

                    if (camera != null && camera.mSID != IOTCAPIs.IOTC_ER_DEVICE_IS_SLEEP) {//睡眠check时不更新UI
                        if (!camera.isChannelConnected(0)) {
                            if (device != null) {
                                device.Status = getText(R.string.connstus_connecting).toString();
                                device.Online = false;
                            }
                        }
                    }
                    Log.i(Tag, getText(R.string.connstus_connecting).toString());
                    break;
                case Camera.CONNECTION_STATE_ＭAX_SESSION:
                    if (device != null) {
                        device.Status = getText(R.string.connstus_max_session).toString();
                        device.Online = true;
                        stopTimeOutTask();
                        hideProgressDlg();
                        Toast.makeText(MainActivity_video.this, getResources().getString(R.string.connstus_max_session), Toast.LENGTH_SHORT).show();
                    }
                    Log.i(Tag, getText(R.string.connstus_max_session).toString());
                    break;

                case Camera.CONNECTION_STATE_CONNECTED:

                    if (camera != null) {
                        if (camera.isSessionConnected() && camera.isChannelConnected(0)) {
                            if (device != null) {
                                device.Status = getText(R.string.connstus_connected).toString();
                                device.Online = true;
                            }

                            stopTimeOutTask();
                            hideProgressDlg();
                            if (mACTIVITY == ACTIVITY.TO_LIVEVIEW) {
                                toLiveViewActivity(device);
                            } else if (mACTIVITY == ACTIVITY.TO_EDIT) {
                                toEditXMDeviceActivity(device);
                            }

                            //获取音频格式
                            camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ, AVIOCTRLDEFs
                                    .SMsgAVIoctrlGetAudioOutFormatReq.parseContent());
                        }
                    }
                    Log.i(Tag, getText(R.string.connstus_connected).toString());
                    break;

                case Camera.CONNECTION_STATE_UNKNOWN_DEVICE:

                    if (device != null) {
                        device.Status = getText(R.string.connstus_unknown_device).toString();
                        device.Online = false;
                    }
                    Log.i(Tag, getText(R.string.connstus_unknown_device).toString());
                    //toastMessage("未知设备");
//					isReconnectDlg(device.UID, mSID);
                    break;

                case Camera.CONNECTION_STATE_DISCONNECTED:
                    // no Use
                    if (device != null) {
                        device.Status = getText(R.string.connstus_disconnect).toString();
                        device.Online = false;
                    }
                    Log.i(Tag, "CONNECTION_STATE_DISCONNECTED");
                    break;

                case Camera.CONNECTION_STATE_TIMEOUT:
                    if (device != null) {
                        device.Status = getText(R.string.connstus_disconnect).toString();
                        device.Online = false;
                    }
                    Log.i(Tag, "CONNECTION_STATE_TIMEOUT");
                    break;

                case Camera.CONNECTION_STATE_WRONG_PASSWORD:
                    if (device != null) {
                        device.Status = getText(R.string.connstus_wrong_password).toString();
                        device.Online = false;
                    }
                    if (mACTIVITY == ACTIVITY.TO_EDIT) {
                        toEditXMDeviceActivity(device);
                    }
                    if (mACTIVITY == ACTIVITY.TO_LIVEVIEW) {
                        camera.disconnect();

                    }
                    Log.i(Tag, getText(R.string.connstus_wrong_password).toString());
                    isConnect = false;
                    Toast.makeText(MainActivity_video.this, "密码错误", Toast.LENGTH_SHORT).show();
                    dismissLoadingView2();
                    stopTimeOutTask();
                    hideProgressDlg();

                    // dismissLoadingView();
                    break;
                case Camera.CONNECTION_STATE_SLEEP:
                    Log.i(Tag, "Zed CONNECTION_STATE_SLEEP   " + device.UID);
                    if (device != null) {
                        device.Status = getText(R.string.connstus_sleep).toString();
                        device.Online = false;
                    }
                    Log.i(Tag, getText(R.string.connstus_sleep).toString());
                    break;
                case Camera.CONNECTION_STATE_CONNECT_FAILED:
                    if (device != null) {
                        device.Status = getText(R.string.connstus_connection_failed).toString();
                        device.Online = false;
                        Log.i("wake", "reconnect" + camera + "," + device);
                        if (tishi) {
                            // toastMessage("正在连接。。");
                            tishi = false;
                        }
                    }
                    Log.i(Tag, getText(R.string.connstus_connection_failed).toString());

//					message = getString(R.string.connstus_connection_failed)  + ","
//							+ getString(R.string.ctxReconnect) + "?";
//					isReconnectDlg(device.UID, message);
                    break;
                case XM_AVIOCTRLDEFs.IOTYPE_XM_CALL_REQ:

                    byte door = data[0];
                    byte[] t = new byte[8];
                    System.arraycopy(data, 1, t, 0, 8);
                    AVIOCTRLDEFs.STimeDay time_l = new AVIOCTRLDEFs.STimeDay(t);
                    DatabaseManager manager = new DatabaseManager(MainActivity_video.this);
                    Resources res = MainActivity_video.this.getResources();
                    Bitmap bmp = BitmapFactory.decodeResource(res, R.mipmap.view_event);
                    if (bmp != null) {
                        manager.addEalinkVisitRecord(camera.getUID(), bmp, LiveViewActivity.getTimeStamp());
                    }
                    Intent intent = new Intent();
                    Bundle extras = new Bundle();
                    extras.putString("dev_uid", camera.getUID());
                    extras.putString("event_type", "2000");
                    extras.putString("event_time", String.valueOf(time_l.getTimeInMillis()));
                    intent.putExtras(extras);
                    intent.setAction("com.tutk.gcm");
                    MainActivity_video.this.sendBroadcast(intent);
                    Log.i(Tag, "IOTYPE_XM_CALL_REQ");
                    break;

                case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_RESP:

                    int total = Packet.byteArrayToInt_Little(data, 40);
                    if (total == -1 && camera != null
                            && camera.getSDCardFormatSupported(0) && device != null
                            && device.ShowTipsForFormatSDCard)
                        showSDCardFormatDialog(camera, device);
                    break;
                case XM_AVIOCTRLDEFs.IOTYPE_XM_GETDOORLIST_RESP:

                    int cnt = Packet.byteArrayToInt_Little(data, 0);
                    if (cnt > 0) {
                        device.bEnable1 = data[4];
                        device.nTime1 = Packet.byteArrayToInt_Little(data, 5);
                        if (cnt == 2) {
                            device.bEnable2 = data[12];
                            device.nTime2 = Packet.byteArrayToInt_Little(data, 13);
                        }
                        device.count = cnt;
                    }
                    break;

                case XM_AVIOCTRLDEFs.IOTYPE_XM_INQUIRE_READYSTATE_RESP:
                    int nReady = Packet.byteArrayToInt_Little(data, 0);
                    device.isUpgradeStatus = nReady == 1 ? true : false;
                    break;

                case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_EVENT_REPORT:

                    byte[] t2 = new byte[8];
                    System.arraycopy(data, 0, t2, 0, 8);
                    AVIOCTRLDEFs.STimeDay evtTime = new AVIOCTRLDEFs.STimeDay(t2);

                    int camChannel = Packet.byteArrayToInt_Little(data, 12);
                    int evtType = Packet.byteArrayToInt_Little(data, 16);

                    if (evtType != AVIOCTRLDEFs.AVIOCTRL_EVENT_MOTIONPASS && evtType != AVIOCTRLDEFs.AVIOCTRL_EVENT_IOALARMPASS) {
//					showNotification(device, camChannel, evtType, evtTime.getTimeInMillis());
                    }
                    break;
            }
            //  adapter.notifyDataSetChanged();
        }

    };

    private void toEditXMDeviceActivity(DeviceInfo dev) {
        Bundle extras = new Bundle();
        Intent intent = new Intent();
        extras.putString("dev_uid", dev.UID);
        extras.putString("dev_uuid", dev.UUID);
        extras.putString("dev_nickname", dev.NickName);
        extras.putString("view_acc", dev.View_Account);
        extras.putString("view_pwd", dev.View_Password);
        extras.putInt("camera_channel", dev.ChannelIndex);

        extras.putString("conn_status", dev.Status);
        intent.putExtras(extras);
        intent.setClass(MainActivity_video.this, EditXMDeviceActivity.class);
        startActivityForResult(intent, REQUEST_CODE_CAMERA_EDIT);
        toEditDev = dev;
    }

    private void toLiveViewActivity(DeviceInfo dev) {
        Bundle extras = new Bundle();
        extras.putString("dev_uid", dev.UID);
        extras.putString("dev_uuid", dev.UUID);
        extras.putString("dev_nickname", dev.NickName);
        extras.putString("view_acc", dev.View_Account);
        extras.putString("view_pwd", dev.View_Password);
        extras.putInt("camera_channel", dev.ChannelIndex);
        extras.putString("conn_status", dev.Status);
        extras.putString("door_id", getIntent().getBundleExtra("bundle").getString("door_id"));
        try {
            extras.putString("time", TimeUtilsDate.time_day(addtime));
        } catch (Exception e) {

        }
        Intent intent = new Intent();
        intent.putExtras(extras);
        intent.setClass(MainActivity_video.this, LiveViewActivity.class);
        startActivityForResult(intent, REQUEST_CODE_CAMERA_VIEW);
        dismissLoadingView();
        dismissLoadingView2();
    }

    @Override
    public void receiveFrameData(final Camera camera, int sessionChannel,
                                 Bitmap bmp) {

    }

    @Override
    public void receiveFrameDataForMediaCodec(Camera camera, int i, byte[] bytes, int i1, int i2, byte[] bytes1, boolean b, int i3) {

    }

    @Override
    public void receiveFrameInfo(final Camera camera, int sessionChannel,
                                 long bitRate, int frameRate, int onlineNm, int frameCount,
                                 int incompleteFrameCount) {

    }

    @Override
    public void receiveSessionInfo(final Camera camera, int resultCode) {

        Bundle bundle = new Bundle();
        bundle.putString("requestDevice", ((MyCamera) camera).getUUID());
        Message msg = handler.obtainMessage();
        msg.what = resultCode;
        msg.setData(bundle);
        handler.sendMessage(msg);

    }

    @Override
    public void receiveChannelInfo(final Camera camera, int sessionChannel,
                                   int resultCode) {

        Bundle bundle = new Bundle();
        bundle.putString("requestDevice", ((MyCamera) camera).getUUID());
        bundle.putInt("sessionChannel", sessionChannel);
        Message msg = handler.obtainMessage();
        msg.what = resultCode;
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    @Override
    public void receiveIOCtrlData(final Camera camera, int sessionChannel,
                                  int avIOCtrlMsgType, byte[] data) {

        Bundle bundle = new Bundle();
        bundle.putString("requestDevice", ((MyCamera) camera).getUUID());
        bundle.putInt("sessionChannel", sessionChannel);
        bundle.putByteArray("data", data);

        Message msg = handler.obtainMessage();
        msg.what = avIOCtrlMsgType;
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

//    public class DeviceListAdapter extends BaseAdapter {
//
//        private LayoutInflater mInflater;
//
//
//        public DeviceListAdapter(Context context) {
//
//            this.mInflater = LayoutInflater.from(context);
//        }
//
//        public int getCount() {
//
//            return XMMainActivity.DeviceList.size();
//        }
//
//        public Object getItem(int position) {
//
//            return XMMainActivity.DeviceList.get(position);
//        }
//
//        public long getItemId(int position) {
//
//            return position;
//        }
//
//        public View getView(final int position, View convertView,
//                            ViewGroup parent) {
//
//            final DeviceInfo dev = XMMainActivity.DeviceList.get(position);
//            final MyCamera cam = XMMainActivity.CameraList.get(position);
//            if (dev == null || cam == null)
//                return null;
//
//            ViewHolder holder = null;
//
//            if (convertView == null) {
//                convertView = mInflater.inflate(R.layout.device_list, null);
//                holder = new ViewHolder();
//                holder.img = (ImageView) convertView.findViewById(R.id.img);
//                holder.title = (TextView) convertView.findViewById(R.id.title);
//                holder.info = (TextView) convertView.findViewById(R.id.info);
//                holder.status = (TextView) convertView.findViewById(R.id.status);
//                holder.eventLayout = (FrameLayout) convertView.findViewById(R.id.eventLayout);
//                holder.GCM_Prompt = (TextView) convertView.findViewById(R.id.GCM_Prompt);
//                holder.wakeLayout = (RelativeLayout) convertView.findViewById(R.id.wakeLayout);
//                holder.wake = (ImageView) convertView.findViewById(R.id.imageView2);
//                holder.wakesleepLayout = (RelativeLayout) convertView.findViewById(R.id.wakesleepLayout);
//                holder.wakesleep = (ImageView) convertView.findViewById(R.id.imagesleep);
//                holder.img_tpns = (ImageView) convertView.findViewById(R.id.img_tpns);
//                holder.battery = (TextView) convertView.findViewById(R.id.tv_battery);
//                convertView.setTag(holder);
//
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//
//            if (holder != null) {
//                //holder.img.setImageBitmap(dev.Snapshot);//tank
//                if (dev.Snapshot == null) {
//                    holder.img.setBackground(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.default_snapshot)));
//                } else {
//                    holder.img.setBackground(new BitmapDrawable(dev.Snapshot));
//                }
//                holder.title.setText(dev.NickName);
//                holder.info.setText(dev.UID);
////				holder.battery.setText(cam.b);
//
//                if (cam.mIOTCAPIs != null) {
//                    dev.Status = getCheck_Device_Result(MainActivity_video.this, cam.Check_Device_Result);
//                    holder.status.setText(dev.Status);
//                    if (dev.Status.equals(getString(R.string.connstus_connected))) {
//                        dev.Online = true;
//                    } else {
//                        dev.Online = false;
//                    }
//
//                }
//
//                holder.GCM_Prompt.setVisibility(View.GONE);
//
//                holder.eventLayout.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (dev.Status.equals(getText(R.string.connstus_connected))) {
//                            toEditXMDeviceActivity(dev);
//                        } else {
//                            connectCamera(dev.UID, ACTIVITY.TO_EDIT, true);
//                        }
//                    }
//                });
//
//                if (cam.isMapping) {
//                    holder.img_tpns.setImageResource(R.mipmap.ic_light_tpns_n);
//                } else {
//                    holder.img_tpns.setImageResource(R.mipmap.ic_light_tpns_h);
//                }
//
//                holder.wakeLayout.setVisibility(View.GONE);
//                holder.wakesleepLayout.setVisibility(View.GONE);
//            }
//
//            return convertView;
//
//        }
//
//        public final class ViewHolder {
//            public ImageView img;
//            public TextView title;
//            public TextView info;
//            public TextView status;
//            public TextView GCM_Prompt;
//            public FrameLayout eventLayout;
//            public RelativeLayout wakeLayout;
//            public ImageView wake;
//            public RelativeLayout wakesleepLayout;
//            public ImageView wakesleep;
//            public ImageView img_tpns;
//            public TextView battery;
//        }
//    }

    private void connectCamera(String dev_uid, ACTIVITY activity, boolean hide) {
        MyCamera camera = null;
        DeviceInfo deviceInfo = null;
        for (MyCamera cam : XMMainActivity.CameraList) {
            if (cam.getUID().equals(dev_uid)) {
                camera = cam;
                break;
            }
        }
        for (DeviceInfo info : XMMainActivity.DeviceList) {
            if (info.UID.equals(dev_uid)) {
                deviceInfo = info;
                break;
            }
        }
        if (camera == null || deviceInfo == null) {
            return;
        }

        mACTIVITY = activity;

        camera.disconnect();
        camera.connect(deviceInfo.UID);
        camera.start(Camera.DEFAULT_AV_CHANNEL, deviceInfo.View_Account, deviceInfo.View_Password);
        camera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL,
                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
                AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
        camera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL,
                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ,
                AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq
                        .parseContent());
        camera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL,
                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ,
                AVIOCTRLDEFs.SMsgAVIoctrlTimeZone.parseContent());

//		startTimeOutTask(dev_uid);
        controlTimeOutTask(dev_uid, hide);
        tishi = true;
    }

    private void showSDCardFormatDialog(final Camera camera,
                                        final DeviceInfo device) {

        final AlertDialog dlg = new AlertDialog.Builder(MainActivity_video.this)
                .create();
        dlg.setTitle(R.string.dialog_FormatSDCard);
        dlg.setIcon(android.R.drawable.ic_menu_more);

        LayoutInflater inflater = dlg.getLayoutInflater();
        View view = inflater.inflate(R.layout.format_sdcard, null);
        dlg.setView(view);

        final CheckBox chbShowTipsFormatSDCard = (CheckBox) view
                .findViewById(R.id.chbShowTipsFormatSDCard);
        final Button btnFormat = (Button) view
                .findViewById(R.id.btnFormatSDCard);
        final Button btnClose = (Button) view.findViewById(R.id.btnClose);

        btnFormat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                        AVIOCTRLDEFs.IOTYPE_USER_IPCAM_FORMATEXTSTORAGE_REQ,
                        AVIOCTRLDEFs.SMsgAVIoctrlFormatExtStorageReq
                                .parseContent(0));
                device.ShowTipsForFormatSDCard = chbShowTipsFormatSDCard
                        .isChecked();

                DatabaseManager db = new DatabaseManager(MainActivity_video.this);
                db.updateDeviceAskFormatSDCardByUID(device.UID,
                        device.ShowTipsForFormatSDCard);

                dlg.dismiss();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                device.ShowTipsForFormatSDCard = chbShowTipsFormatSDCard
                        .isChecked();

                DatabaseManager db = new DatabaseManager(MainActivity_video.this);
                db.updateDeviceAskFormatSDCardByUID(device.UID,
                        device.ShowTipsForFormatSDCard);

                dlg.dismiss();
            }
        });
    }

    private class HomeReceiver extends BroadcastReceiver {

        private final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        private final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);

                if (reason == null)
                    return;
                switch (reason) {
//					case SYSTEM_DIALOG_REASON_HOME_KEY:// Home键
//						onHomeQuit();
//						break;
                    case SYSTEM_DIALOG_REASON_RECENT_APPS:// 最近任务列表键
                        onHomeQuit();
                        break;
                }
            }
        }

        private void onHomeQuit() {
            //Log.i(TAG, " ----onHomeQuit----" + " MyApplication.activityStack.size() == " + MyApplication.activityStack.size());
        }

    }

    public void OpenSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startCheckTask() {
        stopCheckTask();
        mCheckDeviceOnLineTimer = new Timer();
        mCheckDeviceOnLineTimerTask = new TimerTask() {
            @Override
            public void run() {
                for (MyCamera camera : XMMainActivity.CameraList) {
                    if (camera.mIOTCAPIs == null) {
                        camera.mIOTCAPIs = new IOTCAPIs();
                        camera.mIOTCAPIs.setOnLineResultCBListener(MainActivity_video.this);
                    }
                    IOTCAPIs.IOTC_Check_Device_On_Line(camera.getUID(), 5 * 1000, camera.getUID().getBytes(), camera.mIOTCAPIs);
                }
            }
        };
        mCheckDeviceOnLineTimer.schedule(mCheckDeviceOnLineTimerTask, 0, CHECK_TIME);
    }

    private void setupView() {
        setContentView(R.layout.activity_main_video);
//        startActivity(new Intent(this, TestActivity.class));
        //adapter = new DeviceListAdapter(MainActivity_video.this);
        logout = (Button) findViewById(R.id.btnLogou);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InfoUtil.logout(MainActivity_video.this);
                //   startActivity(new Intent(MainActivity_video.this, LoginActivity.class));
                finish();
            }
        });
        tv_addDevice = (TextView) findViewById(R.id.title);
        tv_addDevice.setOnClickListener(addDeviceOnclickListener);
//        listView = (ListView) findViewById(R.id.lstCameraList);
//        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(listViewOnItemClickListener);
//        listView.setOnItemLongClickListener(listViewOnItemLongClickListener);
//        findViewById(R.id.toShowPic).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity_video.this, ShowPicActivity.class);
//                Bundle extras = new Bundle();
//                extras.putString("dev_uid", "H5XG24PCXMGC45DU111A");
//                extras.putString("event_type", "204");
//                extras.putString("pic_path", "123.jpg");
//                intent.putExtras(extras);
//                startActivity(intent);
//            }
//        });
    }

    private View.OnClickListener addDeviceOnclickListener = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
//            new ActionSheetDialog(MainActivity_video.this)
//                    .builder()
//                    .setCancelable(false)
//                    .setCanceledOnTouchOutside(false)
//                    .addSheetItem((String) MainActivity_video.this.getText(R.string.txt_add_dev_online), SheetItemColor.Blue,
//                            new OnSheetItemClickListener() {
//                                @Override
//                                public void onClick(int which) {
            Intent startIntent = new Intent();
            Bundle extras = new Bundle();
            startIntent.putExtras(extras);
            startIntent.setClass(MainActivity_video.this, AddDeviceActivity.class);
            startActivityForResult(startIntent, REQUEST_CODE_CAMERA_ADD);
//
//                                }
//                            })
//                    .addSheetItem((String) MainActivity_video.this.getText(R.string.txt_shortcut_add), SheetItemColor.Blue,
//                            new OnSheetItemClickListener() {
//                                @Override
//                                public void onClick(int which) {
//                                    Intent startIntent = new Intent();
//                                    startIntent.setClass(MainActivity_video.this, SmartLinkActivity.class);
//                                    startActivityForResult(startIntent, REQUEST_CODE_CAMERA_ADD);
//
//                                }
//                            }).show();
        }
    };
//
//    private AdapterView.OnItemClickListener listViewOnItemClickListener = new AdapterView.OnItemClickListener() {
//
//        @Override
//        public void onItemClick(AdapterView<?> arg0, View v, int position,
//                                long id) {
//            if (position < XMMainActivity.DeviceList.size()) {
//                DeviceInfo dvinfo = XMMainActivity.DeviceList.get(position);
//                if (dvinfo.isUpgradeStatus) {
//                    Toast mToast = Toast.makeText(MainActivity_video.this, MainActivity_video.this.getText(R.string.txt_fw_upgrade_process), Toast.LENGTH_LONG);
//                    mToast.setGravity(Gravity.CENTER, 0, 0);
//                    mToast.show();
//                } else if (dvinfo.Status.equals(getText(R.string.connstus_connected))) {
//                    toLiveViewActivity(dvinfo);
//                } else {
//                    connectCamera(dvinfo.UID, ACTIVITY.TO_LIVEVIEW, true);
//                }
//            }
//        }
//    };

    private AdapterView.OnItemLongClickListener listViewOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            showRemoveDialog(XMMainActivity.CameraList.get(position).getUID());
            return true;
        }
    };

    private void showRemoveDialog(final String uid) {

        new AlertDialog.Builder(MainActivity_video.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getText(R.string.tips_warning))
                .setMessage(getText(R.string.tips_remove_camera_confirm))
                .setPositiveButton(getText(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                removeCamera(uid);
                            }
                        })
                .setNegativeButton(getText(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                            }
                        }).show();
    }

    private void initFrameLayout() {
        pcf_container = ((PtrClassicFrameLayout) findViewById(R.id.pcf_container));
        layout_empty = (TextView) findViewById(R.id.layout_empty);
        MaterialHeader materialHeader = new MaterialHeader(this);
        materialHeader.setPadding(0, 40, 0, 40);
        pcf_container.addPtrUIHandler(materialHeader);
        pcf_container.setHeaderView(materialHeader);
        // pcf_container.setHeaderView(view_head);
        pcf_container.setFooterView(new DefaultLoadMoreViewFooter_normal());
        pcf_container.setLoadMoreEnable(true);
        pcf_container.setOnLoadMoreListener(this);
        pcf_container.setPtrHandler(this);
        pcf_container.autoRefresh();
    }

    private void initView() {
        backActivity();
        aCache = ACache.get(this);
        submitBtnVisible("管理", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //管理进入h5界面
                if (bundle != null && bundle.getString("door_id") != null) {
                    //  oepn_doorManage_h5(bundle.getString("door_id"));
//
//                    params.put("appid", RequestTag.APPID);
//                    params.put("key", RequestTag.KEY);
//                    params.put("device_id", door_id);//锁数字
//                    params.put("uid",MyApplication.getUid());
//                    params.put("pwd",MyApplication.getPasswprd());
//                    params.put("app",1+"");
                    String sign = Md5.md5_door(MyApplication.getPasswprd(), bundle.getString("door_id"), MyApplication.getUid(), RequestTag.KEY);
                    String url = RequestTag.ArticaleUrl + RequestTag.door_manage_h5 + "app=1" + "&appid=" + RequestTag.APPID + "&device_id=" + bundle.getString("door_id") + "&pwd=" + MyApplication.getPasswprd() + "&uid=" + MyApplication.getUid() + "&key=" + RequestTag.KEY + "&sign=" + sign;
                    Intent intent = new Intent(MainActivity_video.this, WebViewUrlActivity.class);
                    intent.putExtra("mark", "activity");
                    intent.putExtra("url", url);
                    startActivity(intent);

                }
            }
        });
        listView_new = (ListView) findViewById(R.id.grid_smartHomeList);

        list = new ArrayList<>();
//        for (int i = 0; i < 8; i++) {
//            list.add(new JSONObject());
//        }
        adapter_new = new SmartHomeListAdapter(list, this);
        viewParentHead = LayoutInflater.from(this).inflate(R.layout.layout_smarthome_head, null);
        layout_video = (LinearLayout) viewParentHead.findViewById(R.id.layout_video);
        tv_homeCount = (TextView) viewParentHead.findViewById(R.id.tv_homeCount);
        smartList_tv_title = (TextView) viewParentHead.findViewById(R.id.smartList_tv_title);
        listView_new.addHeaderView(viewParentHead);
        listView_new.setAdapter(adapter_new);
        if (bundle != null && bundle.getString("title") != null) {
            smartList_tv_title.setText(bundle.getString("title"));
        }
        if (bundle != null && bundle.getString("door_id") != null) {
            door_key = bundle.getString("door_key");
            door_id = bundle.getString("door_id");
            addtime = bundle.getLong("addtime");
            getDoorLockDetail(bundle.getString("door_id"), false);
            if (addtime != 0) {
                tv_homeCount.setText(TimeUtilsDate.time_day(addtime));
            }
            // post_event("","1",bundle.getString("door_id"));
//            DatabaseManager manager = new DatabaseManager(MainActivity_video.this);
//                boolean duplicated = false;
//                for (MyCamera camera : XMMainActivity.CameraList) {
//                    if (bundle.getString("door_id").equalsIgnoreCase(camera.getUID())) {
//                        duplicated = true;
//                        break;
//                    }
//                }
//                if (!duplicated) {
//                    long db_id = manager.addDevice("门", bundle.getString("door_id"), "", "", "admin", "888888", 3, 0, 1);
//                    addDevice("门",bundle.getString("door_id"), "admin", "888888", db_id, 0, 0);
//
//                }


        } else {
            pcf_container.setVisibility(View.GONE);
            layout_empty.setVisibility(View.VISIBLE);
        }

        layout_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopwindow_connect();
            }

        });
        listView_new.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                try {
                    ArrayList<String> list_path = new ArrayList<>();
                    for (int i = 0; i < list.get(position).getJSONArray("pics").length(); i++) {
                        list_path.add(list.get(position).getJSONArray("pics").getString(i));

                    }
                    if (list_path.size() > 0) {
                        startActivity_ImagrPager(MainActivity_video.this, 0, list_path, false);
                    } else {
                        toastMessage("没有图片");
                    }
                } catch (Exception e) {

                }
            }

        });

    }

    /**
     * 进入视屏
     *
     * @return
     */
    private void intoVideo(String door_key, String password) {
        if (list.size() > 0 && 0 < XMMainActivity.DeviceList.size()) {
            for (int i = 0; i < XMMainActivity.DeviceList.size(); i++) {
                if (XMMainActivity.DeviceList.get(i).UID.equals(door_key)) {
                    DeviceInfo dvinfo = XMMainActivity.DeviceList.get(i);
                    if (dvinfo.isUpgradeStatus) {
                        Toast mToast = Toast.makeText(MainActivity_video.this, MainActivity_video.this.getText(R.string.txt_fw_upgrade_process), Toast.LENGTH_LONG);
                        mToast.setGravity(Gravity.CENTER, 0, 0);
                        mToast.show();
                    } else if (dvinfo.Status.equals(getText(R.string.connstus_connected))) {
                        dvinfo.setView_Password(password);
                        tishi = true;
                        toLiveViewActivity(dvinfo);
                    } else {
                        dvinfo.setView_Password(password);
                        connectCamera(dvinfo.UID, ACTIVITY.TO_LIVEVIEW, true);
                    }
                    break;
                }

            }

        } else {
            dismissLoadingView2();
            dismissLoadingView();
            toastMessage("没有绑定设备");
        }
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(pcf_container, listView_new, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        page = 1;
        isUpdate = true;
        getDoorLockList(door_id);
    }

    @Override
    public void loadMore() {
        page = page + 1;
        isUpdate = false;
        getDoorLockList(door_id);
    }

    /**
     * 获取门锁详情列表
     */
    private void getDoorLockList(String door_id) {
        showLoadingView();
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid", MyApplication.getUid());
            jsonObject.put("id", door_id);
            jsonObject.put("page", page + "");
            jsonObject.put("pagesize", 20);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.DOOR_POLICE_RECORD, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if (response.getInt("status") != 0) {
                        pcf_container.refreshComplete();
                        pcf_container.loadMoreComplete(false);
                        pcf_container.setVisibility(View.GONE);
                        layout_empty.setVisibility(View.VISIBLE);
                    } else {
                        if (response.getJSONArray("data") == null || response.getJSONArray("data").length() == 0) {
                            pcf_container.setVisibility(View.GONE);
                            layout_empty.setVisibility(View.VISIBLE);
                            list.clear();
                            adapter_new.notifyDataSetChanged();
                        } else {
                            if (isUpdate) {
                                list.clear();
                                pcf_container.refreshComplete();
                                isUpdate = false;
                            }
                            for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                                list.add(response.getJSONArray("data").getJSONObject(i));
                            }
                            if (list.size() >= response.getInt("total")) {
                                //已加载完毕
                                pcf_container.loadMoreComplete(false);
                            } else {
                                pcf_container.loadMoreComplete(true);
                            }
                            layout_empty.setVisibility(View.GONE);
                            pcf_container.setVisibility(View.VISIBLE);
                            adapter_new.notifyDataSetChanged();
                        }
                    }

                } catch (Exception e) {
                    layout_empty.setVisibility(View.GONE);
                    pcf_container.setVisibility(View.VISIBLE);
                }
                dismissLoadingView();

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("网络异常，请检查网络后重试");
                dismissLoadingView();
                pcf_container.refreshComplete();
                pcf_container.loadMoreComplete(false);
                layout_empty.setVisibility(View.GONE);
                pcf_container.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 打开h5门锁管理界面
     */
    private void oepn_doorManage_h5(String door_id) {
        showLoadingView();
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        params.put("device_id", door_id);//锁数字
        params.put("uid", MyApplication.getUid());
        params.put("pwd", MyApplication.getPasswprd());
        params.put("app", 1 + "");
        String sign = Md5.md5_door(MyApplication.getPasswprd(), door_id, MyApplication.getUid(), RequestTag.KEY);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.ArticaleUrl + RequestTag.door_manage_h5, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if (response.getInt("status") != 0) {

                    } else {

                    }

                } catch (Exception e) {

                }
                dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("网络异常，请检查网络后重试");
                dismissLoadingView();

            }
        });
    }

//    /**
//     * 上报
//     */
//    private void post_event(final String path, String type, String id) {
//        showLoadingView();
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("appid", RequestTag.APPID);
//        params.put("key", RequestTag.KEY);
//        String timestamp = System.currentTimeMillis() + "";
//        params.put("time", timestamp + "");
//        final JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("device_id", MyApplication.getDeviceId());
//            jsonObject.put("uid", MyApplication.getUid());
//            jsonObject.put("type", type);//0=门铃 1=抓拍 2=报警
//            jsonObject.put("addtime", System.currentTimeMillis() / 1000 + "");
//            jsonObject.put("pics", ImageBase64.imageToBase64("/data/user/0/com.example.administrator.capacityhome/cache/takephoto_cache/1531539476607.jpg"));
//            jsonObject.put("id",id);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        params.put("data", jsonObject.toString());
//        String sign = Md5.md5(jsonObject.toString(), timestamp);
//        params.put("sign", sign);
//        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.door_event_upService, params, new JsonResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, JSONObject response) {
//                try {
//                    Log.d("asdklhkhsl",response.toString()+"，"+jsonObject.toString()+","+path);
//                    toastMessage(response.getString("msg"));
//                } catch (Exception e) {
//
//                }
//                dismissLoadingView();
//            }
//
//            @Override
//            public void onFailure(int statusCode, String error_msg) {
//                dismissLoadingView();
//                toastMessage("网络异常，请检查后重试");
//            }
//        });
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LiveViewActivity.isJietu) {
            pcf_container.autoRefresh();
            LiveViewActivity.isJietu = false;
        }
    }

    /**
     * 获取门锁详情
     */
    private void getDoorLockDetail(String door_id, final boolean connect) {
        showLoadingView();
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());//转为设备id
            jsonObject.put("uid", MyApplication.getUid());
            jsonObject.put("id", door_id);//门锁设备id
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.door_detail, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if (response.getInt("status") == 1) {
                        toastMessage(response.getString("msg"));
                    } else {
                        door_key = response.getJSONObject("data").getString("key");
                        if (connect) {
                            tishi = true;
                            showPopwindow_connect();
                        }
                        if (addtime == 0) {
                            addtime = response.getJSONObject("data").getLong("addtime");
                            tv_homeCount.setText(TimeUtilsDate.time_day(addtime));
                        }
                    }
                } catch (Exception e) {
                    dismissLoadingView();
                    toastMessage("门锁异常，请退出后重试。");
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dismissLoadingView();
                toastMessage("网络异常，请检查网络后重试");

            }
        });
    }

    /**
     * 链接弹出框
     */
    private void showPopwindow_connect() {
        if (popupWindow_connnect == null) {
            relayout_parent = (RelativeLayout) findViewById(R.id.relayout_parent);
            int width = getResources().getDisplayMetrics().widthPixels / 10 * 8;
//            int height = getResources().getDisplayMetrics().widthPixels / 2.2;
            View popView = LayoutInflater.from(this).inflate(R.layout.connect_video_pop, null);
            popupWindow_connnect = new PopupWindow(popView, width, LinearLayout.LayoutParams.WRAP_CONTENT);
            tv_connnect = (TextView) popView.findViewById(R.id.tv_connnect);
            et_password = (EditText) popView.findViewById(R.id.et_password);
            tv_phoneNumber = (TextView) popView.findViewById(R.id.tv_phoneNumber);
            popupWindow_connnect.setAnimationStyle(R.style.popwin_anim_style);
            popupWindow_connnect.setFocusable(true);
            popupWindow_connnect.setOutsideTouchable(false);// 设置同意在外点击消失
            ColorDrawable dw = new ColorDrawable(0x30000000);
            popupWindow_connnect.setBackgroundDrawable(dw);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(new AnimationUtils().loadAnimation(MainActivity_video.this, R.anim.bg_alpha));
                    isConnect = true;
                    switch (v.getId()) {
                        case R.id.tv_connnect:
                            if (et_password.getText().toString().isEmpty()) {
                                toastMessage("请输入密码");
                            } else {
                                //链接设备
                                if (aCache.getAsString("click") != null) {
                                    //3秒内点击无效
                                } else {
                                    hideKeyboard();
                                    aCache.put("click", "true", ACache.TIME_MILL * 3);
                                    if (bundle != null && bundle.getString("door_id") != null) {

                                        if (door_key == null) {
                                            getDoorLockDetail(bundle.getString("door_id"), true);
                                        } else {
                                            showLoadingView2("正在连接中...");
                                            tishi = true;
                                            intoVideo(door_key, et_password.getText().toString());
                                        }

                                    } else {
                                        dismissLoadingView();
                                        toastMessage("没有绑定设备id");
                                    }
                                }
                            }
                            break;
                    }
                }
            };
            popupWindow_connnect.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    DisplayUtil.backgroundAlpha(1.0f, MainActivity_video.this);
                }
            });
            tv_connnect.setOnClickListener(onClickListener);
            try {
                if (!StringUtils.isEmpty(MyApplication.getUserJson().getString("mobile"))) {
                    tv_phoneNumber.setText(MyApplication.getUserJson().getString("mobile"));
                }
            } catch (Exception e) {

            }
        }

        DisplayUtil.backgroundAlpha(0.3f, this);
        popupWindow_connnect.showAtLocation(relayout_parent, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        try {
//            removeCamera(door_key);
//        } catch (Exception e) {
//
//        }
    }

    /**
     * 关闭软键盘
     */
    private void hideKeyboard() {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(et_password.getWindowToken(), 0);

    }
}
