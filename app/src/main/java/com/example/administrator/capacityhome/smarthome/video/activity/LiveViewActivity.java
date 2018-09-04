package com.example.administrator.capacityhome.smarthome.video.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.capacityhome.*;
import com.example.administrator.capacityhome.smarthome.video.appteam.DatabaseManager;
import com.example.administrator.capacityhome.smarthome.video.appteam.XM_AVIOCTRLDEFs;
import com.example.administrator.capacityhome.smarthome.video.obj.DeviceInfo;
import com.example.administrator.capacityhome.smarthome.video.obj.MyCamera;
import com.example.administrator.capacityhome.smarthome.video.ui.VerticalScrollView;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;
import com.tutk.IOTC.AVFrame;
import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.CameraListener;
import com.tutk.IOTC.IAVConnectListener;
import com.tutk.IOTC.IMonitor;
import com.tutk.IOTC.IOTCAPIs;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.MediaCodecMonitor;
import com.tutk.IOTC.MediaCodecMonitor_MPEG4;
import com.tutk.IOTC.Packet;
import com.tutk.IOTC.St_SInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import http.RequestTag;
import utils.ImageBase64;
import utils.Md5;
import utils.SPUtils;


public class LiveViewActivity extends com.example.administrator.capacityhome.BaseActivity implements IRegisterIOTCListener, CameraListener, View.OnClickListener, IAVConnectListener {
    public static boolean isInLiveViewActivity = false;
    private static final boolean AEC_ON = false;
    private static final int STS_CHANGE_CHANNEL_STREAMINFO = 99;
    private static final int STS_SNAPSHOT_SCANED = 98;
    private static final int SPEAK_OPEN_SUCCESS = 100;
    private static final int SPEAK_OPEN_FAIL = 101;
    private static final int SPEAK_START_LOCALRECORD_SUCCESS = 102;
    private static final int REQUEST_CODE_ALBUM = 99;
    private static final int REQUEST_CODE_SWITCH = 11;
    private static final int TIMER_COUNT_60 = 60;
    private RelativeLayout.LayoutParams btnLayoutParams;
    private static final int OPT_MENU_ITEM_ALBUM = Menu.FIRST;
    private static final int OPT_MENU_ITEM_SNAPSHOT = Menu.FIRST + 1;
    private static final int OPT_MENU_ITEM_AUDIOCTRL = Menu.FIRST + 2;
    private static final int OPT_MENU_ITEM_AUDIO_IN = Menu.FIRST + 3;
    private static final int OPT_MENU_ITEM_AUDIO_OUT = Menu.FIRST + 4;

    private final int THUMBNAIL_LIMIT_HEIGHT = 720;
    private final int THUMBNAIL_LIMIT_WIDTH = 1280;

    private String mDevUID;
    private String mDevUUID;
    private String mConnStatus = "";
    private String mFilePath = "";
    private int mVideoFPS;
    private long mVideoBPS;
    private int mOnlineNm;
    private int mFrameCount;
    private int mIncompleteFrameCount;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mMiniVideoWidth;
    private int mMiniVideoHeight;
    private int mSelectedChannel;
    private int mKeepFPScnt = 0;
    private int mCnt = 0;
    private int mFPScnt = 0;
    private int batWidth;
    private int nDoorCount = 2;
    private byte door_index = 0;

    private int lastBat = 100;

    private boolean mIsListening = true;
    private boolean mIsSpeaking = false;
    private boolean mIsRecording = false;
    private boolean mIsIncoming = false;
    private boolean mIsCallResp = false;

    private LinearLayout linPnlCameraInfo;
    private LinearLayout layoutRecording;
    private TextView tvRecording;
    private TextView txtConnectionSlash;
    private TextView txtResolutionSlash;
    private TextView txtShowFPS;
    private TextView txtFPSSlash;
    private TextView txtShowBPS;
    private TextView txtShowOnlineNumber;
    private TextView txtOnlineNumberSlash;
    private TextView txtShowFrameRatio;
    private TextView txtFrameCountSlash;
    private TextView txtQuality;
    private ViewGroup.LayoutParams batParams;
    private TextView txtRecvFrmPreSec;
    private TextView txtRecvFrmSlash;
    private TextView txtDispFrmPreSeco;
    private TextView txtConnectionStatus;
    private TextView txtConnectionMode;
    private TextView txtResolution;
    private TextView txtFrameRate;
    private TextView txtBitRate;
    private TextView txtOnlineNumber;
    private TextView txtFrameCount;
    private TextView txtIncompleteFrameCount;
    private TextView txtPerformance;
    private TextView tv_title;
    private View batProcess;
    private TextView tv_time;
    private TextView tv_battery;

    private ImageButton ibtn_ylk_listen;
    private ImageView ibtn_ylk_speak;
    private ImageButton ibtn_ylk_door;
    private ImageView ibtn_ylk_snapshot;
    private RelativeLayout ic_bat_bg;
    private ImageButton ibtn_ylk_back;
    private ImageButton ibtn_ylk_recording;
    private ImageButton ibtn_ylk_switch_device;
    private ImageButton btn_call;
    private ImageButton btn_hangup;

    private Button btnBigDoor;
    private Button btnSmallDoor;
    private Button btnCancel;
    private TextView tv_homeCount;//智能保护天

    private VerticalScrollView myScrollView;
    private ImageView imTpnsreg;
    private RelativeLayout pnlopen_layout;
    private LinearLayout pnltoolbar;
    private FrameLayout layout_loading;

    private AlertDialog.Builder builder;
    private AlertDialog dlg;
    private IMonitor monitor;
    private MyCamera mCamera;
    private DeviceInfo mDevice;
    private ThreadTimer mThreadShowRecodTime;
    //private custom_Dialog_prompt mProcessbar;
    private IncomingTimeThread mIncomingTimeThread;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private boolean try_connect = true;
    private boolean isOn = false;
    private View unlock_btn;
    public static boolean isJietu = false;//是否在当前界面截图
    private RelativeLayout relayout_voice_backGround;
    private String time;
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        actionBar.setCustomView(R.layout.titlebar);
        // tv_title = (TextView) this.findViewById(R.id.bar_text);
//        imTpnsreg = (ImageView) findViewById(R.id.imTpnsreg);
//        imTpnsreg.setVisibility(View.GONE);
//        ibtn_ylk_switch_device = (ImageButton) findViewById(R.id.bar_right_btn);
//        ibtn_ylk_switch_device.setBackgroundResource(R.drawable.btn_switch_deivce);
//        ibtn_ylk_switch_device.setOnClickListener(LiveViewActivity.this);
//        if (XMMainActivity.CameraList.size() > 1) {
//            ibtn_ylk_switch_device.setVisibility(View.VISIBLE);
//        }
        btnLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        ibtn_ylk_back = (ImageButton) findViewById(R.id.bar_left_btn);
//        ibtn_ylk_back.setOnClickListener(LiveViewActivity.this);

        Bundle bundle = this.getIntent().getExtras();
        mDevUID = bundle.getString("dev_uid");
        mDevUUID = bundle.getString("dev_uuid");
        mConnStatus = bundle.getString("conn_status");
        mSelectedChannel = bundle.getInt("camera_channel");
        mIsIncoming = bundle.getBoolean("mIsIncoming", false);
        time = bundle.getString("time");
//		mIsListening = !mIsIncoming;//default open
        mIsSpeaking = !mIsIncoming;
        // mProcessbar = new custom_Dialog_prompt(LiveViewActivity.this, getText(R.string.txt_processing), 0, true);
        //  mProcessbar.setCancelable(false);
        for (MyCamera camera : XMMainActivity.CameraList) {
            if (mDevUID.equalsIgnoreCase(camera.getUID()) && mDevUUID.equalsIgnoreCase(camera.getUUID())) {
                mCamera = camera;
                break;
            }
        }
        for (DeviceInfo dev : XMMainActivity.DeviceList) {
            if (mDevUID.equalsIgnoreCase(dev.UID) && mDevUUID.equalsIgnoreCase(dev.UUID)) {
                mDevice = dev;
                break;
            }
        }
        if (mCamera != null) {
//			if (mCamera.mSID == IOTCAPIs.IOTC_ER_DEVICE_IS_SLEEP) {
//				IOTCAPIs.IOTC_WakeUp_WakeDevice(mCamera.getUID());
//				Toast.makeText(LiveViewActivity.this,
//						getString(R.string.tips_wakeup_device), Toast.LENGTH_SHORT).show();
//			} else if (!mCamera.isChannelConnected(Camera.DEFAULT_AV_CHANNEL)) {
//				mCamera.disconnect();
//				mCamera.connect(mDevUID);
//				mCamera.start(Camera.DEFAULT_AV_CHANNEL, mDevice.View_Account, mDevice.View_Password);
//			}

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            if (metrics.widthPixels < metrics.heightPixels) {
                setupViewInPortraitLayout();
            } else {
                setupViewInLandscapeLayout();
            }


            String status = bundle.getString("conn_status", null);
            if (status == null) {
                txtConnectionStatus.setText(MainActivity_video.getCheck_Device_Result(LiveViewActivity.this, mCamera.Check_Device_Result));
            } else {
                txtConnectionStatus.setText(status);

                Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                                XM_AVIOCTRLDEFs.IOTYPE_USER_NOVATEK_CUSTOM_REQ,
                                XM_AVIOCTRLDEFs.SmsgAVIoctrlQuerBatteryReq.parseConent(1));
                    }
                }, 0, 10, TimeUnit.MINUTES);
            }

        } else {
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCamera != null) {
            if (!mCamera.isChannelConnected(Camera.DEFAULT_AV_CHANNEL)) {
//			mCamera.disconnect();
                mCamera.connect(mDevUID);
                mCamera.start(Camera.DEFAULT_AV_CHANNEL, mDevice.View_Account, mDevice.View_Password);
            }

            mCamera.isInLiveView = true;
//            if ((tv_title != null) && (mDevice != null)) {
//                tv_title.setText(mDevice.NickName);
//            }

            if (AEC_ON) {
                mCamera.startAcousticEchoCanceler();
            }

            mCamera.registerIOTCListener(this);
            mCamera.SetCameraListener(this);
            mCamera.registerAVListener(this);
            mCamera.startShow(mSelectedChannel, true, true);

            if (monitor != null) {
                monitor.enableDither(mCamera.mEnableDither);
                monitor.attachCamera(mCamera, mSelectedChannel);
            }
            if (mCamera.isChannelConnected(Camera.DEFAULT_AV_CHANNEL) && getPermission(Manifest.permission.RECORD_AUDIO, REQUEST_CODE_PERMISSION_RESUME)) {
                if (mIsListening) {
                    mCamera.startListening(mSelectedChannel, mIsListening);
                }

                if (mIsSpeaking) {
                    mCamera.startSpeaking(mSelectedChannel);
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("LiveView", " onStop ");
        if (mCamera != null) {
            mCamera.isInLiveView = false;
            if (mIsSpeaking) {
                mCamera.stopSpeaking(mSelectedChannel);
//				mIsSpeaking = false;
            }

            if (mIsRecording) {
                mCamera.stopRecording();
                mIsRecording = false;
                ibtn_ylk_recording.setBackgroundResource(R.drawable.background_ylk_record);
                layoutRecording.setVisibility(View.GONE);
                if (mThreadShowRecodTime != null) {
                    mThreadShowRecodTime.stopThread();
                    mThreadShowRecodTime = null;
                }
            }

            if (mIsListening) {
                mCamera.stopListening(mSelectedChannel);
//				mIsListening = false;
            }
            mCamera.stopShow(mSelectedChannel);
            mCamera.unregisterIOTCListener(LiveViewActivity.this);
            mCamera.unRegisterAVListener();

            if (AEC_ON && mCamera != null) {
                mCamera.stopAcousticEchoCanceler();
            }
        }

        if (monitor != null) {
            monitor.deattachCamera();
        }

        if (mTimer != null) {
            mTimer.cancel();
            mTimerTask.cancel();
            mTimer = null;
            mTimerTask = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
		if(mCamera != null){
			mCamera.disconnect();
		}
    }

    private void quit() {
        byte[] snapshot = null;
        Bitmap bmp = mCamera.Snapshot(mSelectedChannel);

        if (bmp != null) {
            bmp = compressImage(mCamera.Snapshot(mSelectedChannel));
            if (bmp.getWidth() * bmp.getHeight() > THUMBNAIL_LIMIT_WIDTH * THUMBNAIL_LIMIT_HEIGHT) {
                bmp = Bitmap.createScaledBitmap(bmp, THUMBNAIL_LIMIT_WIDTH, THUMBNAIL_LIMIT_HEIGHT, false);
            }
            snapshot = DatabaseManager.getByteArrayFromBitmap(bmp);
        }

        DatabaseManager manager = new DatabaseManager(this);
        manager.updateDeviceChannelByUID(mDevUID, mSelectedChannel);
        if (snapshot != null) {
            manager.updateDeviceSnapshotByUID(mDevUID, snapshot);
        }

        if (mIncomingTimeThread != null) {
            mIncomingTimeThread.stopThread();
            mIncomingTimeThread = null;
        }

        /* return values to main page */
        Bundle extras = new Bundle();
        extras.putString("dev_uuid", mDevUUID);
        extras.putString("dev_uid", mDevUID);
        extras.putByteArray("snapshot", snapshot);
        extras.putInt("camera_channel", mSelectedChannel);
        extras.putBoolean("isJietu",isJietu);
        Intent intent = new Intent();
        intent.putExtras(extras);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Log.i("IOTCamViewer", "" + metrics.widthPixels + " X " + metrics.heightPixels);
        if (metrics.widthPixels < metrics.heightPixels) {
            setupViewInPortraitLayout();
        } else {
            setupViewInLandscapeLayout();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

		/*if(mCamera != null){
            if(mCamera.isConnectedAlive() && mDevice!=null && mCamera.Check_Device_Result != -1){
				txtConnectionStatus.setText(mDevice.Status);
			}else {
				txtConnectionStatus.setText(MainActivity.getCheck_Device_Result(LiveViewActivity.this,mCamera.Check_Device_Result));
			}
		}*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SWITCH) {
            if (resultCode == RESULT_OK) {
                String switch_uid = data.getStringExtra("dev_uid");
                String switch_uuid = data.getStringExtra("dev_uuid");
                mSelectedChannel = data.getIntExtra("camera_channel", 0);

                for (MyCamera camera : XMMainActivity.CameraList) {

                    if (switch_uid.equalsIgnoreCase(camera.getUID())
                            && switch_uuid.equalsIgnoreCase(camera.getUUID())) {
                        mCamera = camera;
                        mDevUID = switch_uid;
                        break;
                    }
                }
                for (DeviceInfo dev : XMMainActivity.DeviceList) {
                    if (switch_uid.equalsIgnoreCase(dev.UID) && switch_uuid.equalsIgnoreCase(dev.UUID)) {
                        mDevice = dev;
                        mDevUUID = switch_uuid;
                        break;
                    }
                }
                mIsListening = true;
                mIsSpeaking = true;
            }
        }
    }

    private void setupViewInLandscapeLayout() {
        setContentView(R.layout.monitor_live_view_landscape);
        txtConnectionStatus = null;
        txtConnectionMode = null;
        txtResolution = null;
        txtFrameRate = null;
        txtBitRate = null;
        txtOnlineNumber = null;
        txtFrameCount = null;
        txtIncompleteFrameCount = null;
        txtRecvFrmPreSec = null;
        txtDispFrmPreSeco = null;
        txtPerformance = null;
        if (monitor != null) {
            monitor.deattachCamera();
            monitor = null;
        }
        monitor = (IMonitor) findViewById(R.id.monitor);
        monitor.cleanFrameQueue();
        monitor.setMaxZoom(3.0f);
//		monitor.enableDither(mCamera.mEnableDither);
//		monitor.attachCamera(mCamera, mSelectedChannel);
        //reScaleMonitor();
    }

    private void setupViewInPortraitLayout() {
        setContentView(R.layout.live_view_portrait);
        findViewById(R.id.top_submit_btn).setVisibility(View.GONE);
        tv_homeCount = (TextView) findViewById(R.id.tv_homeCount);
        if(time!=null){
            tv_homeCount.setText(time);
        }
        backActivity();
        Button button = (Button) findViewById(R.id.test);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                        XM_AVIOCTRLDEFs.IOTYPE_USER_NOVATEK_CUSTOM_REQ,
                        XM_AVIOCTRLDEFs.SmsgAVIoctrlPrepareFpListReq.parseConent());
            }
        });

        unlock_btn = findViewById(R.id.unlock_btn);
        //Camera info
        linPnlCameraInfo = (LinearLayout) findViewById(R.id.pnlCameraInfo);
        linPnlCameraInfo.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                MainActivity_video.nShowMessageCount++;
                showMessage();
            }
        });
        relayout_voice_backGround = (RelativeLayout) findViewById(R.id.relayout_voice_backGround);
        ic_bat_bg = (RelativeLayout) findViewById(R.id.ic_bat_bg);
        txtConnectionSlash = (TextView) findViewById(R.id.txtConnectionSlash);
        txtResolutionSlash = (TextView) findViewById(R.id.txtResolutionSlash);
        txtShowFPS = (TextView) findViewById(R.id.txtShowFPS);
        batProcess = findViewById(R.id.batProcess);
        txtFPSSlash = (TextView) findViewById(R.id.txtFPSSlash);
        txtShowBPS = (TextView) findViewById(R.id.txtShowBPS);
        txtShowOnlineNumber = (TextView) findViewById(R.id.txtShowOnlineNumber);
        txtOnlineNumberSlash = (TextView) findViewById(R.id.txtOnlineNumberSlash);
        txtShowFrameRatio = (TextView) findViewById(R.id.txtShowFrameRatio);
        txtFrameCountSlash = (TextView) findViewById(R.id.txtFrameCountSlash);
        txtQuality = (TextView) findViewById(R.id.txtQuality);
//		txtQuality.setVisibility(View.INVISIBLE);
        txtDispFrmPreSeco = (TextView) findViewById(R.id.txtDispFrmPreSeco);
//		txtDispFrmPreSeco.setVisibility(View.INVISIBLE);
        txtRecvFrmSlash = (TextView) findViewById(R.id.txtRecvFrmSlash);
//		txtRecvFrmSlash.setVisibility(View.INVISIBLE);
        txtRecvFrmPreSec = (TextView) findViewById(R.id.txtRecvFrmPreSec);
//		txtRecvFrmPreSec.setVisibility(View.INVISIBLE);
        txtPerformance = (TextView) findViewById(R.id.txtPerformance);
        txtConnectionStatus = (TextView) findViewById(R.id.txtConnectionStatus);
        txtConnectionMode = (TextView) findViewById(R.id.txtConnectionMode);
        txtResolution = (TextView) findViewById(R.id.txtResolution);
        txtFrameRate = (TextView) findViewById(R.id.txtFrameRate);
        txtBitRate = (TextView) findViewById(R.id.txtBitRate);
        txtOnlineNumber = (TextView) findViewById(R.id.txtOnlineNumber);
        txtFrameCount = (TextView) findViewById(R.id.txtFrameCount);
        txtIncompleteFrameCount = (TextView) findViewById(R.id.txtIncompleteFrameCount);
        tv_battery = (TextView) findViewById(R.id.tv_battery);

        txtConnectionStatus.setText(mConnStatus);
        txtConnectionSlash.setText("");
        txtResolutionSlash.setText("");
        txtResolution.setText("0" + "x" + "0 ");
        txtShowFPS.setText("");
        txtFPSSlash.setText("");
        txtShowBPS.setText("");
        txtOnlineNumberSlash.setText("");
        txtShowFrameRatio.setText("");
        txtFrameCountSlash.setText("");
        txtRecvFrmSlash.setText("");
        txtPerformance.setText(getPerformance((int) (((float) mCamera
                .getDispFrmPreSec() / (float) mCamera
                .getRecvFrmPreSec()) * 100)));
        batParams = batProcess.getLayoutParams();

        txtConnectionMode.setVisibility(View.GONE);
        txtFrameRate.setVisibility(View.GONE);
        txtBitRate.setVisibility(View.GONE);
        txtFrameCount.setVisibility(View.GONE);
        txtIncompleteFrameCount.setVisibility(View.GONE);
        txtRecvFrmPreSec.setVisibility(View.GONE);
        txtDispFrmPreSeco.setVisibility(View.GONE);

        //toolbar
        pnltoolbar = (LinearLayout) findViewById(R.id.pnltoolbar);
        ibtn_ylk_speak = (ImageView) findViewById(R.id.ibtn_ylk_speak);
        ibtn_ylk_speak.setOnClickListener(this);
        ibtn_ylk_listen = (ImageButton) findViewById(R.id.ibtn_ylk_listen);
        ibtn_ylk_listen.setOnClickListener(this);
        ibtn_ylk_door = (ImageButton) findViewById(R.id.ibtn_ylk_door);
        ibtn_ylk_door.setOnClickListener(this);
//		ibtn_ylk_door.setVisibility(View.GONE);
        ibtn_ylk_snapshot = (ImageView) findViewById(R.id.ibtn_ylk_snapshot);
        ibtn_ylk_snapshot.setOnClickListener(this);
        ibtn_ylk_recording = (ImageButton) findViewById(R.id.ibtn_ylk_record_video);
        ibtn_ylk_recording.setOnClickListener(this);

        //door
        pnlopen_layout = (RelativeLayout) findViewById(R.id.pnlopen_layout);
        pnlopen_layout.setVisibility(View.GONE);
        btnBigDoor = (Button) findViewById(R.id.btn_big);
        btnSmallDoor = (Button) findViewById(R.id.btn_small);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnBigDoor.setOnClickListener(this);
        btnSmallDoor.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        layoutRecording = (LinearLayout) findViewById(R.id.layoutRecording);
        layoutRecording.setVisibility(View.GONE);
        tvRecording = (TextView) findViewById(R.id.tvRecording);
        monitor = (IMonitor) this.findViewById(R.id.monitor);
        monitor.cleanFrameQueue();
        monitor.setMaxZoom(3.0f);

        layout_loading = (FrameLayout) findViewById(R.id.layout_loading);

        //来电处理
        btn_call = (ImageButton) findViewById(R.id.btn_call);
        btn_call.setOnClickListener(this);
        btn_hangup = (ImageButton) findViewById(R.id.btn_hangup);
        btn_hangup.setOnClickListener(this);
        tv_time = (TextView) findViewById(R.id.tv_time);
        if (mIsIncoming) {
            pnltoolbar.setVisibility(View.GONE);
            unlock_btn.setVisibility(View.GONE);
            if (mIncomingTimeThread == null) {
                mIncomingTimeThread = new IncomingTimeThread();
                mIncomingTimeThread.start();
            }

        } else {
            btn_call.setVisibility(View.GONE);
            btn_hangup.setVisibility(View.GONE);
            tv_time.setVisibility(View.GONE);
        }

        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        layout_loading.setVisibility(View.GONE);
                        if (mIsIncoming) {
                            Toast.makeText(LiveViewActivity.this, getString(R.string.tips_incoming_view), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        mTimer.schedule(mTimerTask, 7 * 1000);
//		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
//			@Override
//			public void run() {
//				mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
//						XM_AVIOCTRLDEFs.IOTYPE_USER_NOVATEK_CUSTOM_REQ,
//						XM_AVIOCTRLDEFs.SmsgAVIoctrlQuerBatteryReq.parseConent(1));
//			}
//		}, 0, 10, TimeUnit.MINUTES);
//		reScaleMonitor();
    }

    private void showMessage() {
        St_SInfo stSInfo = new St_SInfo();
        IOTCAPIs.IOTC_Session_Check(mCamera.getMSID(), stSInfo);
//		if (MainActivity.nShowMessageCount >= 10) {
        linPnlCameraInfo.setVisibility(View.VISIBLE);
        txtConnectionStatus.setText(mConnStatus);
        txtConnectionMode.setText(getSessionMode(mCamera != null ? mCamera
                .getSessionMode() : -1)
                + " C: "
                + IOTCAPIs.IOTC_Get_Nat_Type()
                + ", D: "
                + stSInfo.NatType
                + ",R" + mCamera.getbResend());
        txtConnectionSlash.setText(" / ");
        txtResolutionSlash.setText(" / ");
        txtShowFPS.setText(getText(R.string.txtFPS));
        txtFPSSlash.setText(" / ");
        txtShowBPS.setText(getText(R.string.txtBPS));
        // txtShowOnlineNumber.setText(getText(R.string.txtOnlineNumber));
        txtOnlineNumberSlash.setText(" / ");
        txtShowFrameRatio.setText(getText(R.string.txtFrameRatio));
        txtFrameCountSlash.setText(" / ");
        txtQuality.setText(getText(R.string.txtQuality));
        txtRecvFrmSlash.setText(" / ");
        // mCamera.getDispFrmPreSec()
        txtConnectionMode.setVisibility(View.VISIBLE);
        txtResolution.setVisibility(View.VISIBLE);
        txtFrameRate.setVisibility(View.VISIBLE);
        txtBitRate.setVisibility(View.VISIBLE);
        txtOnlineNumber.setVisibility(View.VISIBLE);
        txtFrameCount.setVisibility(View.VISIBLE);
        txtIncompleteFrameCount.setVisibility(View.VISIBLE);
        txtRecvFrmPreSec.setVisibility(View.INVISIBLE);
        txtDispFrmPreSeco.setVisibility(View.INVISIBLE);
//		}
    }

    // filename: such as,M20101023_181010.jpg
    public static String getFileNameWithTime() {
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH) + 1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        int mSec = c.get(Calendar.SECOND);
        int mMilliSec = c.get(Calendar.MILLISECOND);

        StringBuffer sb = new StringBuffer();
        sb.append("IMG_");
        sb.append(mYear);
        if (mMonth < 10)
            sb.append('0');
        sb.append(mMonth);
        if (mDay < 10)
            sb.append('0');
        sb.append(mDay);
        sb.append('_');
        if (mHour < 10)
            sb.append('0');
        sb.append(mHour);
        if (mMinute < 10)
            sb.append('0');
        sb.append(mMinute);
        if (mSec < 10)
            sb.append('0');
        sb.append(mSec);
        sb.append(".jpg");

        return sb.toString();
    }

    private String getSessionMode(int mode) {
        String result = "";
        if (mode == 0) {
            result = getText(R.string.connmode_p2p).toString();
        } else if (mode == 1) {
            result = getText(R.string.connmode_relay).toString();
        } else if (mode == 2) {
            result = getText(R.string.connmode_lan).toString();
        } else {
            result = getText(R.string.connmode_none).toString();
        }
        return result;
    }

    public String getPerformance(int mode) {
        String result = "";
        if (mode < 30) {
            result = getText(R.string.txtBad).toString();
        } else if (mode < 60) {
            result = getText(R.string.txtNormal).toString();
        } else {
            result = getText(R.string.txtGood).toString();
        }
        return result;
    }

    public static boolean isSDCardValid() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public Bitmap compressImage(Bitmap image) {
        Bitmap tempBitmap = image;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 5, baos);
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
            Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tempBitmap;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == OPT_MENU_ITEM_ALBUM) {
            File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Snapshot/" + mDevUID);
            String[] allFiles = folder.list();
            if (allFiles != null && monitor != null) {
                monitor.deattachCamera();
                Intent intent = new Intent(LiveViewActivity.this, GridViewGalleryActivity.class);
                intent.putExtra("snap", mDevUID);
                intent.putExtra("images_path", folder.getAbsolutePath());
                startActivity(intent);
            } else {
                String msg = getString(R.string.tips_no_snapshot_found);
                Toast.makeText(LiveViewActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        } else if (id == OPT_MENU_ITEM_SNAPSHOT) {
        } else if (id == OPT_MENU_ITEM_AUDIOCTRL) {
            ArrayList<String> s = new ArrayList<String>();
            s.add(getText(R.string.txtMute).toString());
            if (mCamera.getAudioInSupported(0))
                s.add(getText(R.string.txtListen).toString());
            if (mCamera.getAudioOutSupported(0))
                s.add(getText(R.string.txtSpeak).toString());

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, s);

            final AlertDialog dlg = new AlertDialog.Builder(this).create();
            dlg.setTitle(null);
            dlg.setIcon(null);

            ListView view = new ListView(this);
            view.setAdapter(adapter);
            view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View view,
                                        int position, long id) {
                    if (mCamera == null)
                        return;
                    if (position == 1) { // Listening
//						mCamera.stopSpeaking(mSelectedChannel);
                        mIsListening = true;
                        mIsSpeaking = true;
                        mCamera.startSpeaking(mSelectedChannel);
                        mCamera.startListening(mSelectedChannel, mIsListening);
                    } else if (position == 2) { // Speaking
//						mCamera.stopListening(mSelectedChannel);
//						mIsListening = false;
                        mIsListening = true;
                        mIsSpeaking = true;
                        mCamera.startListening(mSelectedChannel, mIsListening);
                        mCamera.startSpeaking(mSelectedChannel);
//						mCamera.bLastListening = false;
                    } else if (position == 0) { // Mute
                        mCamera.stopListening(mSelectedChannel);
                        mCamera.stopSpeaking(mSelectedChannel);
                        mIsListening = mIsSpeaking = false;
                    }
                    dlg.dismiss();
                    LiveViewActivity.this.invalidateOptionsMenu();
                }
            });
            dlg.setView(view);
            dlg.setCanceledOnTouchOutside(true);
            dlg.show();
        } else if (id == OPT_MENU_ITEM_AUDIO_IN) {
            if (!mIsListening) {
                mCamera.startListening(mSelectedChannel, true);
            } else {
                mCamera.stopListening(mSelectedChannel);
            }
            mIsListening = !mIsListening;
            this.invalidateOptionsMenu();
        } else if (id == OPT_MENU_ITEM_AUDIO_OUT) {
            if (!mIsSpeaking) {
                mCamera.startSpeaking(mSelectedChannel);
            } else {
                mCamera.stopSpeaking(mSelectedChannel);
            }
            mIsSpeaking = !mIsSpeaking;
            this.invalidateOptionsMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                quit();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void receiveFrameData(final Camera camera, int avChannel, Bitmap bmp) {
        if (mCamera == camera && avChannel == mSelectedChannel) {
            if (bmp != null && (bmp.getWidth() != mVideoWidth || bmp.getHeight() != mVideoHeight)) {
                mVideoWidth = bmp.getWidth();
                mVideoHeight = bmp.getHeight();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mTimer != null) {
                            layout_loading.setVisibility(View.GONE);
                            mTimer.cancel();
                            mTimerTask.cancel();
                            mTimer = null;
                            mTimerTask = null;
                        }
                    }
                });
            }
        }
    }

    @Override
    public void receiveFrameInfo(final Camera camera, int avChannel,
                                 long bitRate, int frameRate, int onlineNm, int frameCount,
                                 int incompleteFrameCount) {
        if (mCamera == camera && avChannel == mSelectedChannel) {
            mVideoFPS = frameRate;
            mVideoBPS = bitRate;
            mOnlineNm = onlineNm;
            mFrameCount = frameCount;
            mIncompleteFrameCount = incompleteFrameCount;
            Bundle bundle = new Bundle();
            bundle.putInt("avChannel", avChannel);
            Message msg = handler.obtainMessage();
            msg.what = STS_CHANGE_CHANNEL_STREAMINFO;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    @Override
    public void receiveChannelInfo(final Camera camera, int avChannel, int resultCode) {
        if (mCamera == camera && avChannel == mSelectedChannel) {
            Bundle bundle = new Bundle();
            bundle.putInt("avChannel", avChannel);
            Message msg = handler.obtainMessage();
            msg.what = resultCode;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    @Override
    public void receiveSessionInfo(final Camera camera, int resultCode) {
        if (mCamera == camera) {
            Bundle bundle = new Bundle();
            Message msg = handler.obtainMessage();
            msg.what = resultCode;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    @Override
    public void receiveIOCtrlData(final Camera camera, int avChannel, int avIOCtrlMsgType, byte[] data) {
        // if ((mCamera == camera) && (mSelectedChannel == avChannel))
        if ((mCamera == camera) && (mSelectedChannel == avChannel)) {
            Bundle bundle = new Bundle();
            bundle.putInt("avChannel", avChannel);
            bundle.putByteArray("data", data);

            Message msg = handler.obtainMessage();
            msg.what = avIOCtrlMsgType;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    @Override
    public void receiveFrameDataForMediaCodec(
            Camera arg0, int arg1, byte[] arg2, int arg3,
            int arg4, byte[] arg5, boolean arg6, int arg7) {
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            int avChannel = bundle.getInt("avChannel");
            byte[] data = bundle.getByteArray("data");
            St_SInfo stSInfo = new St_SInfo();
            IOTCAPIs.IOTC_Session_Check(mCamera.getMSID(), stSInfo);
            switch (msg.what) {
//				case SPEAK_OPEN_SUCCESS:
//					if (ibtn_ylk_speak != null) {
//						ibtn_ylk_speak.setBackgroundResource(R.drawable.background_ylk_speak_on);
//						mIsSpeaking = true;
//					}
//					break;
//
//				case SPEAK_OPEN_FAIL:
//					if (ibtn_ylk_speak != null) {
//						ibtn_ylk_speak.setBackgroundResource(R.drawable.background_ylk_speak_off);
////					mCamera.stopSpeaking(mSelectedChannel);
//
////						mIsSpeaking = true;
//						mIsListening = true;
//						mCamera.startListening(mSelectedChannel, mIsListening);
//						mIsSpeaking = false;
//					}
//					break;
                case TIMER_COUNT_60:
                    int incomingTime = (int) msg.obj;
                    if (incomingTime < 0) {
                        if (mCamera != null && mCamera.isSessionConnected()) {
                            mCamera.sendIOCtrl(mSelectedChannel,
                                    XM_AVIOCTRLDEFs.IOTYPE_XM_CALL_RESP,
                                    XM_AVIOCTRLDEFs.SMsgAVIoctrlCallResp.paraseContent(door_index, 0));
                        }
                        if (mIncomingTimeThread != null) {
                            mIncomingTimeThread.stopThread();
                            mIncomingTimeThread = null;
                        }
                        quit();
                    } else {
                        if (tv_time != null) {
                            tv_time.setText(String.valueOf(incomingTime));
                        }
                    }
                    break;

                case SPEAK_START_LOCALRECORD_SUCCESS:
                    if (ibtn_ylk_recording != null) {
                        layoutRecording.setVisibility(View.VISIBLE);
                        mThreadShowRecodTime = new ThreadTimer();
                        mThreadShowRecodTime.start();
                        ibtn_ylk_recording.setEnabled(true);
                        mIsRecording = true;
                        //  mProcessbar.dismiss();
                        ibtn_ylk_recording.setBackgroundResource(R.mipmap.record_on);
                        Toast.makeText(LiveViewActivity.this, R.string.tips_recording_start, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case STS_CHANGE_CHANNEL_STREAMINFO:
                    if (txtResolution != null) {
                        txtResolution.setText(String.valueOf(mVideoWidth) + "x" + String.valueOf(mVideoHeight));
                    }
                    if (txtFrameRate != null) {
                        if (Camera.nCodecId_temp == AVFrame.MEDIA_CODEC_VIDEO_MPEG4) {
                            txtFrameRate.setText(String.valueOf(MediaCodecMonitor_MPEG4.keepFPS));
                        } else if (Camera.nCodecId_temp == AVFrame.MEDIA_CODEC_VIDEO_H264) {
                            txtFrameRate.setText(String.valueOf(MediaCodecMonitor.keepFPS));

                            Log.i("eddie", "mKeepFPScnt");
                            if (MediaCodecMonitor.keepFPS == 0) {

                                if (mKeepFPScnt == 10) {
                                    mKeepFPScnt = 0;
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            mCamera.disconnect();
                                            SystemClock.sleep(1000);
                                            mCamera.connect(mDevUID);
                                            mCamera.start(Camera.DEFAULT_AV_CHANNEL, mDevice.View_Account,
                                                    mDevice.View_Password);
                                        }
                                    }.start();
                                    Toast.makeText(LiveViewActivity.this, R.string.Liveview_networkbad, Toast.LENGTH_SHORT).show();

                                }

                                mKeepFPScnt++;
                                Log.i("eddie", "mKeepFPScnt" + mKeepFPScnt);

                            } else {
                                mCnt = 0;
                                Log.i("eddie", "mKeepFPScnt" + mKeepFPScnt);
                            }

                        } else {
                            txtFrameRate.setText(String.valueOf(mVideoFPS));
                            Log.i("eddie", "mVideoFPS");
                            if (mVideoFPS == 0) {

                                if (mFPScnt == 10) {
                                    mFPScnt = 0;
                                    new Thread() {
                                        @Override
                                        public void run() {

                                            mCamera.disconnect();
                                            SystemClock.sleep(1000);
                                            mCamera.connect(mDevUID);
                                            mCamera.start(Camera.DEFAULT_AV_CHANNEL, mDevice.View_Account,
                                                    mDevice.View_Password);
                                        }
                                    }.start();

                                    Toast.makeText(LiveViewActivity.this, R.string.Liveview_networkbad, Toast.LENGTH_SHORT).show();

                                    if (mCnt == 5) {
                                        mIsListening = false;
                                        mCamera.stopListening(mSelectedChannel);
                                        mCamera.stopSpeaking(mSelectedChannel);
                                        mIsSpeaking = false;
                                    }
                                    mCnt++;
                                }
                                mFPScnt++;
                                Log.i("eddie", "mFPScnt" + mFPScnt + "mCnt" + mCnt);

                            } else {
                                mFPScnt = 0;
                                mCnt = 0;
                                Log.i("eddie", "mFPScnt" + mFPScnt);
                            }
                        }
                    }

                    if (txtBitRate != null) {
                        txtBitRate.setText(String.valueOf(mVideoBPS) + "Kbps");
                    }

                    if (txtOnlineNumber != null) {
                        txtOnlineNumber.setText(String.valueOf(mOnlineNm));
                    }

                    if (txtFrameCount != null) {
                        txtFrameCount.setText(String.valueOf(mFrameCount));
                    }

                    if (txtIncompleteFrameCount != null) {
                        txtIncompleteFrameCount.setText(String.valueOf(mIncompleteFrameCount));
                    }

                    if (txtConnectionMode != null) {
                        txtConnectionMode.setText(getSessionMode(mCamera != null ? mCamera
                                .getSessionMode() : -1)
                                + " C: "
                                + IOTCAPIs.IOTC_Get_Nat_Type()
                                + ", D: "
                                + stSInfo.NatType
                                + ",R"
                                + mCamera.getbResend());
                    }

                    if (txtRecvFrmPreSec != null) {
                        txtRecvFrmPreSec.setText(String.valueOf(mCamera.getRecvFrmPreSec()));
                    }

                    if (txtDispFrmPreSeco != null) {
                        // txtDispFrmPreSeco.setText(String.valueOf(mCamera.getDispFrmPreSec()));
                        txtDispFrmPreSeco.setText(String.valueOf(MediaCodecMonitor.mDecodeCount_temp));
                    }
                    if (txtPerformance != null) {
                        txtPerformance.setText(getPerformance((int) (((float) mCamera
                                .getDispFrmPreSec() / (float) mCamera
                                .getRecvFrmPreSec()) * 100)));
                    }
                    break;
                case STS_SNAPSHOT_SCANED:
                    isJietu = true;
                    post_event((String) msg.obj,1+"",getIntent().getExtras().getString("door_id"));
                  //  Toast.makeText(LiveViewActivity.this, getString(R.string.tips_snapshot_ok), Toast.LENGTH_SHORT).show();
                    break;
                case Camera.CONNECTION_STATE_CONNECTING:
                    if (!mCamera.isSessionConnected() || !mCamera.isChannelConnected(mSelectedChannel)) {
                        mConnStatus = getString(R.string.connstus_connecting);
                        if (txtConnectionStatus != null) {
                            txtConnectionStatus.setText(mConnStatus);
                        }
                        toastMessage("正在唤醒设备");
                    }
                    break;
                case Camera.CONNECTION_STATE_CONNECTED:
                    if (mCamera.isSessionConnected()
                            && avChannel == mSelectedChannel
                            && mCamera.isChannelConnected(mSelectedChannel)) {
                        mConnStatus = getString(R.string.connstus_connected);
                        if (txtConnectionStatus != null) {
                            txtConnectionStatus.setText(mConnStatus);
                        }
                        mCamera.startShow(mSelectedChannel, true, true);
                        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                                XM_AVIOCTRLDEFs.IOTYPE_USER_NOVATEK_CUSTOM_REQ,
                                XM_AVIOCTRLDEFs.SmsgAVIoctrlQuerBatteryReq.parseConent(1));

                        if (getPermission(Manifest.permission.RECORD_AUDIO, REQUEST_CODE_PERMISSION_RESUME)) {
                            if (mIsListening) {
                                mCamera.startListening(mSelectedChannel, mIsListening);
                            }
                            if (mIsSpeaking) {
                                mCamera.startSpeaking(mSelectedChannel);
                            }
                        }
                        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ,
                                AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq.parseContent());
                        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
                                AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
                        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ,
                                AVIOCTRLDEFs.SMsgAVIoctrlTimeZone.parseContent());
                        mCamera.sendIOCtrl(mSelectedChannel,
                                XM_AVIOCTRLDEFs.IOTYPE_XM_GETUNLOCKPASSWDSWITCH_REQ,
                                XM_AVIOCTRLDEFs.SMsgAVIoctrlGetUnlockSwitchReq.parseContent());
                        LiveViewActivity.this.invalidateOptionsMenu();
                    }
                    break;
                case Camera.CONNECTION_STATE_DISCONNECTED:
                    mConnStatus = getString(R.string.connstus_disconnect);
                    if (txtConnectionStatus != null) {
                        txtConnectionStatus.setText(mConnStatus);
                    }
                    LiveViewActivity.this.invalidateOptionsMenu();
                    break;
                case Camera.CONNECTION_STATE_UNKNOWN_DEVICE:
                    mConnStatus = getString(R.string.connstus_unknown_device);
                    if (txtConnectionStatus != null) {
                        txtConnectionStatus.setText(mConnStatus);
                    }
                    LiveViewActivity.this.invalidateOptionsMenu();
                    break;
                case Camera.CONNECTION_STATE_TIMEOUT:
                    break;
                case Camera.CONNECTION_STATE_CONNECT_FAILED:
                    mConnStatus = getString(R.string.connstus_connection_failed);
                    if (txtConnectionStatus != null) {
                        txtConnectionStatus.setText(mConnStatus);
                    }
                    if (try_connect) {
                        mCamera.disconnect();
                        SystemClock.sleep(500);
                        mCamera.connect(mDevice.UID);
                        mCamera.start(Camera.DEFAULT_AV_CHANNEL, mDevice.View_Account, mDevice.View_Password);
                        try_connect = false;
                    }
                    LiveViewActivity.this.invalidateOptionsMenu();
                    break;
                case Camera.CONNECTION_STATE_SLEEP:
//					IOTCAPIs.IOTC_WakeUp_WakeDevice(mCamera.getUID());
//					Toast.makeText(LiveViewActivity.this,
//							getString(R.string.tips_wakeup_device), Toast.LENGTH_SHORT).show();
                    break;
                case Camera.CONNECTION_STATE_WRONG_PASSWORD:
                    mConnStatus = getString(R.string.connstus_wrong_password);
                    if (txtConnectionStatus != null) {
                        txtConnectionStatus.setText(mConnStatus);
                    }
                    toastMessage("密码错误");
                    break;
                case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_RESP:
                    LiveViewActivity.this.invalidateOptionsMenu();
                    break;
                case XM_AVIOCTRLDEFs.IOTYPE_XM_GETUNLOCKPASSWDSWITCH_RESP:
                    mDevice.nEnable = Packet.byteArrayToInt_Little(data, 0);
                    break;
                case XM_AVIOCTRLDEFs.IOTYPE_XM_CALL_IND:
                    if (mIsCallResp) {
                        break;
                    }
                    Toast.makeText(LiveViewActivity.this, getString(R.string.tips_someone_responded), Toast.LENGTH_LONG).show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            quit();
                        }
                    }, 5000);
                    break;
                case XM_AVIOCTRLDEFs.IOTYPE_XM_UNLOCK_RESP:
                    int index = Packet.byteArrayToInt_Little(data, 0);
                    break;
                case XM_AVIOCTRLDEFs.IOTYPE_XM_GETDOORLIST_RESP:
                    nDoorCount = Packet.byteArrayToInt_Little(data, 0);
                    if (nDoorCount > 0) {
                        if (ibtn_ylk_door != null) {
                            ibtn_ylk_door.setEnabled(true);
                            ibtn_ylk_door.setVisibility(View.VISIBLE);
                        }
                        mDevice.count = nDoorCount;
                        mDevice.bEnable1 = data[4];
                        mDevice.nTime1 = Packet.byteArrayToInt_Little(data, 5);
                        if (nDoorCount == 2) {
                            mDevice.bEnable2 = data[12];
                            mDevice.nTime2 = Packet.byteArrayToInt_Little(data, 13);
                        }
                    }
                    break;
                case XM_AVIOCTRLDEFs.IOTYPE_USER_NOVATEK_CUSTOM_RESP:
                    //前面的４字节就是回应resp　ｃｍｄ
                    int cmd = Packet.byteArrayToInt_Little(data, 0);
                    switch (cmd) {
                        case XM_AVIOCTRLDEFs.IOTYPE_XM_QUERYBATTERY_RESP:
                            byte[] buf = new byte[data.length - 4];
                            System.arraycopy(data, 4, buf, 0, data.length - 4);
                            XM_AVIOCTRLDEFs.SmsgAVIoctrlQuerBatteryResp batteryResp = new XM_AVIOCTRLDEFs.SmsgAVIoctrlQuerBatteryResp(buf);
                            final int batteryCount = batteryResp.getResult();

//                            mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
//                                    XM_AVIOCTRLDEFs.IOTYPE_USER_NOVATEK_CUSTOM_REQ,
//                                    XM_AVIOCTRLDEFs.SmsgAVIoctrlQuerBatteryReq.parseConent(1));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv_battery.setText(batteryCount + "%");
                                    batParams = batProcess.getLayoutParams();
//                                    batWidth=dip2px(LiveViewActivity.this, 25) ;
                                    batWidth = dip2px(LiveViewActivity.this, 25) * batteryCount / 100;
                                    batParams.width = batWidth;
                                    batProcess.setLayoutParams(batParams);
//                                    if (batteryCount > lastBat) {
//                                        batProcess.setVisibility(View.GONE);
//                                        ic_bat_bg.setBackgroundResource(R.drawable.ic_power);
//                                    } else {
//                                        ic_bat_bg.setBackgroundResource(R.drawable.batbg);
//                                        batProcess.setVisibility(View.VISIBLE);
//                                    }
//                                    lastBat = batteryCount;
                                }
                            });
                            break;
                        case XM_AVIOCTRLDEFs.IOTYPE_XM_PREPAREFPLIST_RSP:
                            XM_AVIOCTRLDEFs.SmsgAVIoctrlPrepareFpListResp resp = new XM_AVIOCTRLDEFs.SmsgAVIoctrlPrepareFpListResp(data);
                            total = resp.getTotal();
                            if (total >= current) {
                                mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                                        XM_AVIOCTRLDEFs.IOTYPE_USER_NOVATEK_CUSTOM_REQ,
                                        XM_AVIOCTRLDEFs.SmsgAVIoctrlGetFpListReq.parseConent(current));
                            }
                            break;
                        case XM_AVIOCTRLDEFs.IOTYPE_XM_GETFPLIST_RSP:
                            XM_AVIOCTRLDEFs.SmsgAVIoctrlGetFpListResp addResp = new XM_AVIOCTRLDEFs.SmsgAVIoctrlGetFpListResp(data);
                            if (addResp.getResult() == 0) {
//								Log.e("liveViewActivity",new String(addResp.getFp()));
                                ++current;
                                fingerId += 1;
                            } else if (addResp.getResult() == -2) {
                                Log.e("liveViewActivity", "已经存在");
                            } else {
                                Log.e("liveViewActivity", "添加失败");
                            }
                            break;
                        case XM_AVIOCTRLDEFs.IOTYPE_XM_UNLOCK_RESP:
                            byte[] buff = new byte[data.length - 4];
                            System.arraycopy(data, 4, buff, 0, data.length - 4);
                            XM_AVIOCTRLDEFs.SmsgAVIoctrlUnlockResp unLockResp = new XM_AVIOCTRLDEFs.SmsgAVIoctrlUnlockResp(buff);
                            final int unLockResult = unLockResp.getRestlt();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (unLockResult == 0) {
                                        isOn = !isOn;
                                        Toast.makeText(LiveViewActivity.this, "成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LiveViewActivity.this, "失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            break;
                    }
                    break;
            }
        }
    };

    private int total;
    private int current;
    private int fingerId = 0;

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void OnSnapshotComplete() {
        MediaScannerConnection.scanFile(LiveViewActivity.this,
                new String[]{mFilePath.toString()},
                new String[]{"image/*"},
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                        Message msg = handler.obtainMessage();
                        msg.what = STS_SNAPSHOT_SCANED;
                        msg.obj = path;
                        handler.sendMessage(msg);
                    }
                });
    }

    private boolean firstComing = true;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && firstComing && batWidth != 0) {
            Log.i("progressWidth", batWidth + "windowFocus");
            firstComing = false;
            batParams = batProcess.getLayoutParams();
            batParams.width = batWidth;
            batProcess.setLayoutParams(batParams);
        }
    }

    //调用camera.snapshot方法的回调接口
    @Override
    public void getSnapshotBitmap(byte[] bitmap) {
    }

    @Override
    public void getAVconnectStatus(final int type, final int resp) {
        Log.i("Zed", "LiveView type == " + type + "  resp == " + resp);
//		LiveView type == 1  resp == 2
//		LiveView type == 2  resp == 2
//		LiveView type == 2  resp == 3
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // mProcessbar.dismiss();
                        switch (type) {
                            case IAVConnectListener.AV_CONNECT_TYPE_AUDIO:
                                if (resp == IAVConnectListener.AUDIO_READY_SUCESS) {
                                    ibtn_ylk_listen.setBackgroundResource(R.drawable.backgound_ylk_listen_on);
                                } else if (resp == IAVConnectListener.AUDIO_READY_STOP) {
                                    ibtn_ylk_listen.setBackgroundResource(R.drawable.backgound_ylk_listen_off);
                                } else {
                                    //mCamera.stopListening(mSelectedChannel);
                                    //mIsListening = false;
                                }
                                break;

                            case IAVConnectListener.AV_CONNECT_TYPE_SPEAK:
                                if (resp == IAVConnectListener.SPEAK_READY_SUCESS) {
                                    relayout_voice_backGround.setBackgroundResource(R.drawable.background_ylk_speak_on);
                                } else if (resp == IAVConnectListener.SPEAK_READY_STOP) {
                                    relayout_voice_backGround.setBackgroundResource(R.drawable.background_ylk_speak_off);
                                } else {
                                    mCamera.stopSpeaking(mSelectedChannel);
                                    mIsSpeaking = false;
                                }
                                break;
                        }
                    }
                }, 500);
            }
        });
    }

    private class ThreadTimer extends Thread {
        long mCurrentTime = 0;
        long mLastTime = 0;
        long mTime = 0;
        String mShow;
        public boolean mIsRunning = false;
        Time time = new Time();

        public void stopThread() {
            mIsRunning = false;
        }

        @Override
        public void run() {
            mIsRunning = true;
            while (mIsRunning) {
                mCurrentTime = System.currentTimeMillis();
                if ((mCurrentTime - mLastTime) >= 1000) {
                    if ((mCurrentTime - mLastTime) >= 2000) {
                        mTime += 0;
                    } else {
                        mTime += (mCurrentTime - mLastTime);
                    }
                    time.set(mTime);
                    mShow = time.format("%M:%S");
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            tvRecording.setText(mShow);
                            if (mTime >= 180000 && mIsRecording) {
                                layoutRecording.setVisibility(View.GONE);
                                ibtn_ylk_recording.setBackgroundResource(R.drawable.background_ylk_record);
                                mCamera.stopRecording();
                                mIsRecording = false;
                                mIsRunning = false;
                                Toast.makeText(LiveViewActivity.this, getString(R.string.tips_record_ok), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    mLastTime = mCurrentTime;
                }
            }
        }
    }

    private class IncomingTimeThread extends Thread {
        private int time = 60;
        private boolean isRunning = true;

        private void stopThread() {
            isRunning = false;
        }

        @Override
        public void run() {
            while (isRunning) {
                time--;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = handler.obtainMessage(TIMER_COUNT_60);
                message.obj = time;
                handler.sendMessage(message);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_ylk_speak:
                //说话
                if (getPermission(Manifest.permission.RECORD_AUDIO, REQUEST_CODE_PERMISSION_SPEAKING)) {
                    startCameraSpeaking();
                }
                break;
            case R.id.ibtn_ylk_listen:
                //静音
                if (getPermission(Manifest.permission.RECORD_AUDIO, REQUEST_CODE_PERMISSION_LISTENING)) {
                    startCameraListening();
                }
                break;
            case R.id.ibtn_ylk_door:
                //开锁
                /*if (mDevice.count == 1) {
                    if (mDevice.bEnable1 == 0) { //不允许
						Toast.makeText(LiveViewActivity.this,
								getString(R.string.txt_forbid_open),
								Toast.LENGTH_SHORT).show();
					} else {
						door_index = 0;
						if (mDevice.nEnable == 0) {
							mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
									XM_AVIOCTRLDEFs.IOTYPE_XM_UNLOCK_REQ,
									XM_AVIOCTRLDEFs.SMsgAVIoctrlUnlockReq.paraseContent(0, door_index, null));
						} else {
							requstInputPasswd();
						}
					}
				} else if (mDevice.count == 2) {
					if ((mDevice.bEnable1 == 1) && (mDevice.bEnable2 == 1)) {
						new ActionSheetDialog(LiveViewActivity.this)
								.builder()
								.setCancelable(false)
								.setCanceledOnTouchOutside(false)
								.addSheetItem(LiveViewActivity.this.getString(R.string.txtBigDoor),
										SheetItemColor.Blue,
										new OnSheetItemClickListener() {
											@Override
											public void onClick(int which) {
												door_index = 1;
												if (mDevice.nEnable == 0) {
													mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
															XM_AVIOCTRLDEFs.IOTYPE_XM_UNLOCK_REQ,
															XM_AVIOCTRLDEFs.SMsgAVIoctrlUnlockReq.paraseContent(0, door_index, null));
												} else {
													requstInputPasswd();
												}
											}
										})
								.addSheetItem(LiveViewActivity.this.getString(R.string.txtSmallDoor),
										SheetItemColor.Blue,
										new OnSheetItemClickListener() {
											@Override
											public void onClick(int which) {
												door_index = 0;
												if (mDevice.nEnable == 0) {
													mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
															XM_AVIOCTRLDEFs.IOTYPE_XM_UNLOCK_REQ,
															XM_AVIOCTRLDEFs.SMsgAVIoctrlUnlockReq.paraseContent(0, (byte) 0, null));
												} else {
													requstInputPasswd();
												}
											}
										}).show();
					} else if ((mDevice.bEnable1 == 1) && (mDevice.bEnable2 == 0)) {
						door_index = 0;
						if (mDevice.nEnable == 0) {
							mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
									XM_AVIOCTRLDEFs.IOTYPE_XM_UNLOCK_REQ,
									XM_AVIOCTRLDEFs.SMsgAVIoctrlUnlockReq.paraseContent(0, door_index, null));
						} else {
							requstInputPasswd();
						}
					} else if ((mDevice.bEnable1 == 0) && (mDevice.bEnable2 == 1)) {

						door_index = 1;
						if (mDevice.nEnable == 0) {
							mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
									XM_AVIOCTRLDEFs.IOTYPE_XM_UNLOCK_REQ,
									XM_AVIOCTRLDEFs.SMsgAVIoctrlUnlockReq.paraseContent(0, door_index, null));
						} else {
							requstInputPasswd();
						}
					} else {
						Toast.makeText(LiveViewActivity.this,
								getString(R.string.txt_forbid_open),
								Toast.LENGTH_SHORT).show();
					}
				}*/
//				mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
//						XM_AVIOCTRLDEFs.IOTYPE_XM_UNLOCK_REQ,
//						XM_AVIOCTRLDEFs.SMsgAVIoctrlUnlockReq.paraseContent(0, door_index=1, null));
                mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, XM_AVIOCTRLDEFs.IOTYPE_USER_NOVATEK_CUSTOM_REQ, XM_AVIOCTRLDEFs.SMsgAVIoctrlUnlockReq.parseConent(isOn ? 0 : 1));
                break;
            case R.id.ibtn_ylk_snapshot:
                //截图
                showLoadingView();
                if (getPermission(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_CODE_PERMISSION_SNAPSHOT)) {
                    startCameraSnapshot();
                }
                break;
            case R.id.ibtn_ylk_record_video:
                // inittoolbar();
                if (mIsRecording) {
                    mIsRecording = false;
                    mCamera.stopRecording();
                    Toast.makeText(LiveViewActivity.this,
                            R.string.tips_recording_saved_ablumn,
                            Toast.LENGTH_SHORT).show();
                    ibtn_ylk_recording.setBackgroundResource(R.drawable.background_ylk_record);
                    layoutRecording.setVisibility(View.GONE);
                    if (!mIsListening) {
                        mCamera.stopListening(mSelectedChannel);
                    }
                    if (mThreadShowRecodTime != null) {
                        mThreadShowRecodTime.stopThread();
                        mThreadShowRecodTime = null;
                    }
                } else {
                    if (getPermission(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_CODE_PERMISSION_STORAGE)) {
                        startCameraRecording();
                    }
                }
                break;
            case R.id.btn_call:
                // mProcessbar.show();
//				if (!mCamera.isChannelConnected(mSelectedChannel)) {
//					mCamera.disconnect();
//					mCamera.connect(mDevUID);
//					mCamera.start(Camera.DEFAULT_AV_CHANNEL, mDevice.View_Account, mDevice.View_Password);
//				}
                mIsCallResp = true;
                unlock_btn.setVisibility(View.VISIBLE);
                mCamera.sendIOCtrl(mSelectedChannel,
                        XM_AVIOCTRLDEFs.IOTYPE_XM_CALL_RESP,
                        XM_AVIOCTRLDEFs.SMsgAVIoctrlCallResp.paraseContent(door_index, 1));
                if (getPermission(Manifest.permission.RECORD_AUDIO, REQUEST_CODE_PERMISSION_INCAMING)) {
                    if (!mIsListening) {
                        mIsListening = true;
                        mCamera.startListening(mSelectedChannel, mIsListening);
                    }

                    if (!mIsSpeaking) {
                        mIsSpeaking = true;
                        mCamera.startSpeaking(mSelectedChannel);
                    }
                }

                if (mIncomingTimeThread != null) {
                    mIncomingTimeThread.stopThread();
                    mIncomingTimeThread = null;
                }
                pnltoolbar.setVisibility(View.VISIBLE);
                pnltoolbar.setVisibility(View.VISIBLE);
                tv_time.setVisibility(View.GONE);
                btn_call.setVisibility(View.GONE);
                btn_hangup.setVisibility(View.GONE);
                break;
            case R.id.btn_hangup:
                mCamera.sendIOCtrl(mSelectedChannel,
                        XM_AVIOCTRLDEFs.IOTYPE_XM_CALL_RESP,
                        XM_AVIOCTRLDEFs.SMsgAVIoctrlCallResp.paraseContent(door_index, 0));
                quit();
                break;
            case R.id.bar_left_btn:
                quit();
                break;
            case R.id.bar_right_btn:
                if (mIsRecording) {
                    Toast.makeText(LiveViewActivity.this, getString(R.string.tips_close_recording), Toast.LENGTH_SHORT).show();
                    break;
                }
                Intent intent = new Intent();
                Bundle extras = new Bundle();
                extras.putInt("nType", 8);
                extras.putString("devUID", mDevUID);
                extras.putString("devUUID", mDevUUID);
                intent.putExtras(extras);
                intent.setClass(LiveViewActivity.this, ViewCameraListActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SWITCH);
                break;
            case R.id.btn_big:
                door_index = 1;
                if (mDevice.nEnable == 0) {
                    mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                            XM_AVIOCTRLDEFs.IOTYPE_XM_UNLOCK_REQ,
                            XM_AVIOCTRLDEFs.SMsgAVIoctrlUnlockReq.paraseContent(0, door_index, null));
                } else {
                    requstInputPasswd();
                }
                pnlopen_layout.setVisibility(View.GONE);
                break;
            case R.id.btn_small:
                door_index = 0;
                if (mDevice.nEnable == 0) {
                    mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                            XM_AVIOCTRLDEFs.IOTYPE_XM_UNLOCK_REQ,
                            XM_AVIOCTRLDEFs.SMsgAVIoctrlUnlockReq.paraseContent(0, (byte) 0, null));
                } else {
                    requstInputPasswd();
                }
                pnlopen_layout.setVisibility(View.GONE);
                break;
            case R.id.btn_cancel:
                pnlopen_layout.setVisibility(View.GONE);
                break;
        }
    }

    private static final int REQUEST_CODE_PERMISSION_STORAGE = 100;
    private static final int REQUEST_CODE_PERMISSION_INCAMING = 101;
    private static final int REQUEST_CODE_PERMISSION_LISTENING = 102;
    private static final int REQUEST_CODE_PERMISSION_SPEAKING = 103;
    private static final int REQUEST_CODE_PERMISSION_SNAPSHOT = 104;
    private static final int REQUEST_CODE_PERMISSION_RESUME = 105;

    public boolean getPermission(String permission, int result) {
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(LiveViewActivity.this, permission)) {
            return true;
        } else {
            ActivityCompat.requestPermissions(LiveViewActivity.this, new String[]{permission}, result);
            return false;
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i("LiveView", " onRequestPermissionsResult " + requestCode);
        if (grantResults.length > 0) {
            switch (requestCode) {
                case REQUEST_CODE_PERMISSION_LISTENING:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startCameraListening();
                    }
                    break;
                case REQUEST_CODE_PERMISSION_SPEAKING:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startCameraSpeaking();
                    }
                    break;
                case REQUEST_CODE_PERMISSION_SNAPSHOT:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startCameraSnapshot();
                    }
                    break;

                case REQUEST_CODE_PERMISSION_STORAGE:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startCameraRecording();
                    }
                    break;
                case REQUEST_CODE_PERMISSION_INCAMING:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (!mIsListening) {
                            mIsListening = true;
                            mCamera.startListening(mSelectedChannel, mIsListening);
                        }
                        if (!mIsSpeaking) {
                            mIsSpeaking = true;
                            mCamera.startSpeaking(mSelectedChannel);
                        }
                    }
                    break;
                case REQUEST_CODE_PERMISSION_RESUME:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (mIsListening) {
                            mCamera.startListening(mSelectedChannel, mIsListening);
                        }
                        if (mIsSpeaking) {
                            mCamera.startSpeaking(mSelectedChannel);
                        }
                    } else {
                        mIsListening = false;
                        mIsSpeaking = false;
                    }
                    break;
            }
        }
    }

    public void startCameraRecording() {
        if (getAvailaleSize() <= 300) {
            Toast.makeText(LiveViewActivity.this, R.string.recording_tips_size,
                    Toast.LENGTH_SHORT).show();
        }
        if (mCamera.codec_ID_for_recording == AVFrame.MEDIA_CODEC_VIDEO_H264 || mCamera.codec_ID_for_recording == AVFrame.MEDIA_CODEC_VIDEO_HEVC) {
            ibtn_ylk_recording.setEnabled(false);
            mCamera.startListening(mSelectedChannel, mIsListening);
            File rootFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Record/");
            if (!rootFolder.exists()) {
                try {
                    rootFolder.mkdir();
                } catch (SecurityException se) {
                }
            }
            File targetFolder = new File(rootFolder.getAbsolutePath() + "/" + mDevUID);
            if (!targetFolder.exists()) {
                try {
                    targetFolder.mkdir();
                } catch (SecurityException se) {
                }
            }

            String name = getFileNameWithTime2();
            mFilePath = targetFolder.getAbsoluteFile() + File.separator + name.replace("mp4", "jpg");
            final String path = targetFolder.getAbsoluteFile() + File.separator + name;

            mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, XM_AVIOCTRLDEFs
                    .IOTYPE_XM_USER_INSERT_I_FRAME, XM_AVIOCTRLDEFs
                    .SmsgAVIoctrlGetDoorListReq.parseContent());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (mCamera.startRecording(path, true)) {
                        mCamera.SetCameraListener(null);
                        mCamera.setSnapshot(LiveViewActivity.this, mSelectedChannel, mFilePath);
                        handler.sendEmptyMessage(SPEAK_START_LOCALRECORD_SUCCESS);
                    }
                }
            }).start();
            mIsRecording = true;
            // mProcessbar.show();
        } else {
            Toast.makeText(LiveViewActivity.this, R.string.recording_tips_format, Toast.LENGTH_SHORT).show();
        }
    }

    public void startCameraListening() {
        //mProcessbar.show();
        if (mIsListening) {
            mIsListening = false;
            if (mIsRecording) {
                mCamera.startListening(mSelectedChannel, false);
                // mProcessbar.dismiss();
                ibtn_ylk_listen.setBackgroundResource(R.drawable.backgound_ylk_listen_off);
            } else {
                mCamera.stopListening(mSelectedChannel);
            }
        } else {
            mIsListening = true;
            mCamera.startListening(mSelectedChannel, mIsListening);
        }
    }

    public void startCameraSpeaking() {
        // mProcessbar.show();
        if (mIsSpeaking) {
            mCamera.stopSpeaking(mSelectedChannel);
            mIsSpeaking = false;
        } else {
            mCamera.startSpeaking(mSelectedChannel);
            mIsSpeaking = true;
        }
    }

    public void startCameraSnapshot() {
        if (mCamera != null && mCamera.isChannelConnected(mSelectedChannel)) {
            if (isSDCardValid()) {
                File rootFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Snapshot/");
                File targetFolder = new File(rootFolder.getAbsolutePath() + "/" + mDevUID);
                if (!rootFolder.exists()) {
                    try {
                        rootFolder.mkdir();
                    } catch (SecurityException se) {
                    }
                }
                if (!targetFolder.exists()) {
                    try {
                        targetFolder.mkdir();
                    } catch (SecurityException se) {
                    }
                }
                mFilePath = targetFolder.getAbsoluteFile() + "/" + getFileNameWithTime();
                if (mCamera != null) {
                    mCamera.SetCameraListener(LiveViewActivity.this);
                    mCamera.setSnapshot(LiveViewActivity.this, mSelectedChannel, mFilePath);

                } else {
                    Toast.makeText(LiveViewActivity.this,
                            getString(R.string.tips_snapshot_failed),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LiveViewActivity.this,
                        getString(R.string.tips_no_sdcard),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void requstInputPasswd() {
        builder = new AlertDialog.Builder(LiveViewActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View inputView = inflater.inflate(R.layout.input_ulock_passwd, null);
        builder.setTitle(getString(R.string.tips_input_password));
        builder.setView(inputView);
        final EditText input_number = (EditText) inputView.findViewById(R.id.edtPasswd);
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mCamera != null && mCamera.isSessionConnected()) {
                            String strPasswd = input_number.getText().toString().trim();
                            mCamera.sendIOCtrl(mSelectedChannel,
                                    XM_AVIOCTRLDEFs.IOTYPE_XM_UNLOCK_REQ,
                                    XM_AVIOCTRLDEFs.SMsgAVIoctrlUnlockReq.paraseContent(0, door_index, strPasswd));
                        }
                    }

                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pnlopen_layout.setVisibility(View.GONE);
                    }
                });
        dlg = builder.create();
        dlg.show();
    }

    public static String getTimeStamp() {
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH) + 1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        int mSec = c.get(Calendar.SECOND);
        int mMilliSec = c.get(Calendar.MILLISECOND);

        StringBuffer sb = new StringBuffer();

        sb.append(mYear);
        sb.append("-");
        if (mMonth < 10)
            sb.append('0');
        sb.append(mMonth);
        sb.append("-");
        if (mDay < 10)
            sb.append('0');
        sb.append(mDay);
        sb.append(" ");
        if (mHour < 10)
            sb.append('0');
        sb.append(mHour);
        sb.append(":");
        if (mMinute < 10)
            sb.append('0');
        sb.append(mMinute);
        sb.append(":");
        if (mSec < 10)
            sb.append('0');
        sb.append(mSec);
        return sb.toString();
    }

    public static String getFileNameWithTime2() {
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH) + 1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        int mSec = c.get(Calendar.SECOND);
        int mMilliSec = c.get(Calendar.MILLISECOND);

        StringBuffer sb = new StringBuffer();
        // sb.append("MP4_");
        sb.append(mYear);
        if (mMonth < 10)
            sb.append('0');
        sb.append(mMonth);
        if (mDay < 10)
            sb.append('0');
        sb.append(mDay);
        sb.append('_');
        if (mHour < 10)
            sb.append('0');
        sb.append(mHour);
        if (mMinute < 10)
            sb.append('0');
        sb.append(mMinute);
        if (mSec < 10)
            sb.append('0');
        sb.append(mSec);
        sb.append(".mp4");

        return sb.toString();
    }

    public static long getAvailaleSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return (availableBlocks * blockSize) / 1024 / 1024;
    }

    private void reScaleMonitor() {
        if (mVideoHeight == 0 || mVideoWidth == 0) {
            return;
        }
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int screenWidth = size.x;
        final int screenHeight = size.y;
        final SurfaceView surfaceView;
        surfaceView = (SurfaceView) monitor;
        if (surfaceView == null)
            return;

        if (screenHeight >= screenWidth) {
            /**
             * portrait mode
             */
            surfaceView.getLayoutParams().width = screenWidth;
            surfaceView.getLayoutParams().height = (int) (screenWidth
                    * mVideoHeight / (float) mVideoWidth);
            /*if (mMiniVideoHeight < surfaceView.getLayoutParams().height) {
                surfaceView.getLayoutParams().height = mMiniVideoHeight;
			}*/
        } else {
            if (surfaceView.getLayoutParams().width > screenWidth) {
                surfaceView.getLayoutParams().width = screenWidth;
                surfaceView.getLayoutParams().height = (int) (screenWidth * mVideoHeight / (float) mVideoWidth);
                final int scrollViewHeight = surfaceView.getLayoutParams().height;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (myScrollView != null) {
                            myScrollView.setPadding(0, (screenHeight - scrollViewHeight) / 2, 0, 0);
                        }
                    }
                });
            } else {
                surfaceView.getLayoutParams().height = screenHeight;
                surfaceView.getLayoutParams().width = (int) (screenHeight * mVideoWidth / (float) mVideoHeight);
                final int scrollViewWidth = surfaceView.getLayoutParams().width;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (myScrollView != null) {
                            myScrollView.setPadding((screenWidth - scrollViewWidth) / 2, 0, 0, 0);
                        }
                    }
                });
            }
        }
        mMiniVideoHeight = surfaceView.getLayoutParams().height;
        mMiniVideoWidth = surfaceView.getLayoutParams().width;
        surfaceView.setLayoutParams(surfaceView.getLayoutParams());
    }

    public void zoomSurface(float scale) {
        SurfaceView surfaceView = (SurfaceView) monitor;
        android.view.ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();

        lp.width = (int) (mMiniVideoWidth * scale);
        lp.height = lp.width * 3 / 4;
        surfaceView.setLayoutParams(lp);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        int scrollViewHeight = height * 3 / 4;
        int scrollViewWidth = width;
        int recalPadding;

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        if (metrics.widthPixels < metrics.heightPixels) {
            recalPadding = (scrollViewHeight - lp.height) / 2;
            if (recalPadding < 0)
                recalPadding = 0;
        } else {
            recalPadding = (scrollViewWidth - lp.width) / 2;
            if (recalPadding < 0)
                recalPadding = 0;
        }
    }

    public void receiveSnapshot(Bitmap bmp) {
        try {
            FileOutputStream fos = new FileOutputStream(mFilePath);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
            addImageGallery(new File(mFilePath));

            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(LiveViewActivity.this,
                            getText(R.string.tips_snapshot_ok),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(LiveViewActivity.this,
                            getText(R.string.tips_snapshot_failed),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean saveImage(String fileName, Bitmap frame) {
        if (fileName == null || fileName.length() <= 0)
            return false;
        boolean bErr = false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName, false);
            frame.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            bErr = true;
            System.out.println("saveImage(.): " + e.getMessage());
        } finally {
            if (bErr) {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        }
        addImageGallery(new File(fileName));
        return true;
    }

    private void addImageGallery(File file) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg"); // setar
        // isso
        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    /**
     * 上报
     */
    private void post_event(final String path, String type, String id) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid", MyApplication.getUid());
            jsonObject.put("type", type);//0=门铃 1=抓拍 2=报警
            jsonObject.put("addtime", System.currentTimeMillis() / 1000 + "");
            jsonObject.put("pics", ImageBase64.imageToBase64(path));
            jsonObject.put("id",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.door_event_upService, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    Log.d("asdklhkhsl",response.toString()+"，"+jsonObject.toString()+","+path);
                    toastMessage(response.getString("msg"));
                } catch (Exception e) {

                }
                dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dismissLoadingView();
                toastMessage("网络异常，请检查后重试");
            }
        });
    }
}
