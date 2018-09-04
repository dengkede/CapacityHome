package google.zxing.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.capacityhome.BaseActivity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.combo.ComboDetailActivity;
import com.example.administrator.capacityhome.myorder.OrderDetailActivity;
import com.example.administrator.capacityhome.wxapi.Constants;
import com.example.administrator.capacityhome.wxapi.WXPayEntryActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import google.zxing.decoding.CaptureActivityHandler;
import google.zxing.decoding.InactivityTimer;
import google.zxing.decoding.RGBLuminanceSource;
import google.zxing.view.ViewfinderView;
import http.RequestTag;
import myview.Type_choose;
import utils.CameraCanUseUtils;
import utils.Common_brandRule;
import utils.Constant;
import utils.DisplayUtil;
import utils.GetAgrement;
import utils.KeyBordUtil;
import utils.Md5;
import utils.PermissionPageUtils;
import widget.MyTextView;
import zfb_pay.PayType;


/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CaptureActivity extends BaseActivity implements Callback, View.OnClickListener {
    private static final int REQUEST_CODE_SCAN_GALLERY = 100;
    private TextView saomiao_tvUrgent;//加急
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private ImageButton back;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private ProgressDialog mProgress;
    private String photo_path;
    private Bitmap scanBitmap;
    private Common_brandRule common_brandRule;
    private LinearLayout layout_handWar;//手电
    /**
     * Camera相机硬件操作类
     */
    private Camera camera = null;

    /**
     * Camera2相机硬件操作类
     */
    private CameraManager manager = null;
    private CameraDevice cameraDevice;
    private CameraCaptureSession captureSession = null;
    private CaptureRequest request = null;
    private SurfaceTexture surfaceTexture;
    private Surface surface;
    private String cameraId = null;
    private boolean isSupportFlashCamera2 = false;
    private Camera.Parameters params;
    private LinearLayout layout_createRandomCode;//创建随机码
    private TextView tv_randomCode;//随机码
    private boolean orderaddmoney = false;//是否加急
    private String order_id;//订单id
    private PopupWindow popWindow_additional;//加急弹框提示
    private TextView tv_sure;
    private TextView tv_cancel;
    private MyTextView tv_content;
    private RelativeLayout layout_parent;
    private GetAgrement getAgrement;
    private boolean is_put = true;//是放入衣物还是取出,true,放入
    private boolean isResultSuccess = false;
    private TextView tv_sure_gps;
    private TextView tv_cancle_gps;
    private PopupWindow popupWindow_gps;
    private TextView tv_content_gps;
    private PermissionPageUtils permissionPageUtils;
    private Handler mHandler;
    private boolean isCan = true;//判断相关权限是否开启
    /**
     * 支付弹框相关
     */
    private Type_choose dialog;//支付选择
    private Type_choose.Onclick onclick;

//    private IBackService iBackService;
//    private ServiceConnection conn = new ServiceConnection() {
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            iBackService = null;
//        }
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            iBackService = IBackService.Stub.asInterface(service);
//        }
//    };
//    private Intent mServiceIntent;
//
//    class MessageBackReciver extends BroadcastReceiver {
//
//        public MessageBackReciver() {
//
//        }
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals(BackService.HEART_BEAT_ACTION)) {
//                //心跳
//            } else {
//                String message = intent.getStringExtra("message");
//                Log.d("huifu", message);
//                if (!utils.StringUtils.isEmpty(message) && message.contains("ACCEPT_OpenBoxMsg")) {
//                    //证明是来自开箱结果的消息
//                    try {
//                        JSONObject jsonObject = new JSONObject(message);
//                        if (jsonObject.getInt("status") == 0) {
//                            //开箱成功，关闭当前扫描界面
//                            toastMessage(jsonObject.getString("msg"));
//                            startActivity_topToBottom();
//                        } else {
//                            toastMessage(jsonObject.getString("msg"));
//                            if (handler != null) {
//                                //重新开启扫描，解决不能连续扫描的问题
//                                toastMessage(jsonObject.getString("二次扫描"));
//                                handler.restartPreviewAndDecode();
//                            }
//                        }
//                    } catch (Exception e) {
//
//                    }
//                } else {
//                    //其余结果
//                }
//            }
//        }
//
//        ;
//    }
//
//    private MessageBackReciver mReciver;
//
//    private IntentFilter mIntentFilter;
//
//    private LocalBroadcastManager mLocalBroadcastManager;

    private final CameraCaptureSession.StateCallback stateCallback = new CameraCaptureSession.StateCallback() {

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public void onConfigured(CameraCaptureSession arg0) {
            captureSession = arg0;
            CaptureRequest.Builder builder;
            try {
                builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
                builder.addTarget(surface);
                request = builder.build();
                captureSession.capture(request, null, null);
            } catch (CameraAccessException e) {
                // Log.e(TAG, e.getMessage());
            }
        }

        ;

        public void onConfigureFailed(CameraCaptureSession arg0) {
        }

        ;
    };

    private boolean isOpen = false;

    //	private Button cancelScanButton;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        //ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
        google.zxing.camera.CameraManager.init(getApplication());
        layout_createRandomCode = (LinearLayout) findViewById(R.id.layout_createRandomCode);
        tv_randomCode = (TextView) findViewById(R.id.tv_randomCode);
        saomiao_tvUrgent = (TextView) findViewById(R.id.saomiao_tvUrgent);
        layout_handWar = (LinearLayout) findViewById(R.id.layout_handWar);
        layout_handWar.setOnClickListener(this);
        layout_createRandomCode.setOnClickListener(this);
        saomiao_tvUrgent.setOnClickListener(this);
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_content);
        findViewById(R.id.btn_question).setOnClickListener(this);
        back = (ImageButton) findViewById(R.id.btn_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//		cancelScanButton = (Button) this.findViewById(R.id.btn_cancel_scan);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        // 初始化Camera硬件
        this.manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if (CameraCanUseUtils.isCameraCanUse()) {
            Log.i("", "相机");
        } else {
            // toastMessage("没相机权限，请到应用程序权限管理开启权限");
            show_gps();
            //getAppDetailSettingIntent();
            //跳转至app设置
            //  getAppDetailSettingIntent();
            return;
        }
        // SocketInit();
        //添加toolbar
//        addToolbar();
        //   pay(CaptureActivity.this, order, null, null);
    }

//    /**
//     * 长连接相关
//     */
//    private void SocketInit() {
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
//        mReciver = new MessageBackReciver();
//        mServiceIntent = new Intent(this, BackService.class);
//        mIntentFilter = new IntentFilter();
//        mIntentFilter.addAction(BackService.HEART_BEAT_ACTION);
//        mIntentFilter.addAction(BackService.MESSAGE_ACTION);
//    }

    //    /**
//     * 相机权限设置
//     * 跳转至设置页面
//     */
//    private void getAppDetailSettingIntent() {
//        Intent localIntent = new Intent();
//        if (Build.VERSION.SDK_INT >= 9) {
//            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//            localIntent.setData(Uri.fromParts("package",getPackageName(), null));
//        } else if (Build.VERSION.SDK_INT <= 8) {
//            localIntent.setAction(Intent.ACTION_VIEW);
//            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
//            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
//        }
//        startActivity(localIntent);
//    }
    private void addToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        ImageView more = (ImageView) findViewById(R.id.scanner_toolbar_more);
//        assert more != null;
//        more.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.scanner_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.scan_local:
//                //打开手机中的相册
//                Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); //"android.intent.action.GET_CONTENT"
//                innerIntent.setType("image/*");
//                Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
//                this.startActivityForResult(wrapperIntent, REQUEST_CODE_SCAN_GALLERY);
//                return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SCAN_GALLERY:
                    //获取选中图片的路径
                    Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                    if (cursor.moveToFirst()) {
                        photo_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    }
                    cursor.close();
                    mProgress = new ProgressDialog(CaptureActivity.this);
                    mProgress.setMessage("正在扫描...");
                    mProgress.setCancelable(false);
                    mProgress.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Result result = scanningImage(photo_path);
                            if (result != null) {
                                //获取扫码结果
//                                Message m = handler.obtainMessage();
//                                m.what = R.id.decode_succeeded;
//                                m.obj = result.getText();
//                                handler.sendMessage(m);
                                //返回主界面处理
                                /**
                                 Intent resultIntent = new Intent();
                                 Bundle bundle = new Bundle();
                                 bundle.putString(Constant.INTENT_EXTRA_KEY_QR_SCAN, result.getText());
                                 //                                Logger.d("saomiao",result.getText());
                                 //                                bundle.putParcelable("bitmap",result.get);
                                 resultIntent.putExtras(bundle);
                                 CaptureActivity.this.setResult(RESULT_OK, resultIntent);
                                 */
                                //当前界面处理

                            } else {
                                Message m = handler.obtainMessage();
                                m.what = R.id.decode_failed;
                                m.obj = "Scan failed!";
                                handler.sendMessage(m);
                            }
                        }
                    }).start();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 扫描二维码图片的方法
     *
     * @param path
     * @return
     */
    public Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); //设置二维码内容的编码

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(layout_createRandomCode!=null) {
            if (CameraCanUseUtils.isCameraCanUse()) {
                isCan = true;
            } else {
                isCan = false;
                show_gps();
            }
        }
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.scanner_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

        //quit the scan view
//		cancelScanButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				CaptureActivity.this.finish();
//			}
//		});
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        google.zxing.camera.CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        Log.d("resultString", resultString);
        //Toast.makeText(CaptureActivity.this, resultString, Toast.LENGTH_SHORT).show();
        //FIXME
        if (!isResultSuccess) {
            if (TextUtils.isEmpty(resultString)) {
                Toast.makeText(CaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
                isResultSuccess = false;
            } else {
                isResultSuccess = true;
                Intent resultIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString(Constant.INTENT_EXTRA_KEY_QR_SCAN, resultString);
                //Log.d("erweima", resultString);
                // 不能使用Intent传递大于40kb的bitmap，可以使用一个单例对象存储这个bitmap
//            bundle.putParcelable("bitmap", barcode);
//            Logger.d("saomiao",resultString);
                // resultIntent.putExtras(bundle);
                // this.setResult(RESULT_OK, resultIntent);

                if (!resultString.contains("client=userQrCan") && !resultString.contains("client=userPaycard")&&!resultString.contains("client=userFace")) {
                    toastMessage("不是本应用的相关地址");
                    isResultSuccess = false;
                    handler.restartPreviewAndDecode();
                } else if (resultString.contains("client=userPaycard")) {
                    //充值
                    bundle.putString("jsonObject", utils.StringUtils.submitString_pay(resultString).toString());
                    Log.d("resultString2", utils.StringUtils.submitString_pay(resultString).toString());
                    resultIntent.putExtras(bundle);
                    this.setResult(RESULT_OK, resultIntent);
                    finish();
                }else if(resultString.contains("client=userFace")) {
                    //用户扫描的是人脸识别二维码
                    Toast toast=Toast.makeText(CaptureActivity.this, utils.StringUtils.submitString_face(resultString), Toast.LENGTH_LONG);
                    showMyToast(toast, 3*1000);

                }
                else if (resultString.contains("type=2")) {
                    //用户扫描的是取件码,要查询订单状态
                    is_put = false;
                    query_NoOrder(utils.StringUtils.submitString(resultString));
                } else {
                    is_put = true;

                    //            bundle.putParcelable("bitmap", barcode);
                    bundle.putBoolean("is_put", is_put);
                    bundle.putBoolean("is_additional", orderaddmoney);
                    bundle.putString("jsonObject", utils.StringUtils.submitString(resultString).toString());
                    Log.d("resultString2", utils.StringUtils.submitString(resultString).toString());
                    resultIntent.putExtras(bundle);
                    this.setResult(RESULT_OK, resultIntent);
                    finish();
                    //SocketMessage(utils.StringUtils.submitString(resultString));
                }
            }
        }
        // CaptureActivity.this.finish();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            google.zxing.camera.CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saomiao_tvUrgent:
                // show_gps();
                //加急
                if (orderaddmoney) {
                    //取消加急
                    saomiao_tvUrgent.setText("点击加急");
                    saomiao_tvUrgent.setTextColor(ContextCompat.getColor(this, R.color.fontcolor_f3));
                    saomiao_tvUrgent.setBackground(ContextCompat.getDrawable(this, R.drawable.white_rect));
                    orderaddmoney = false;
                } else {
                    //加急
                    showPopwindow_additional();
                }
                break;
            case R.id.layout_handWar:
                if (isOpen) {
                    closeLight();
                } else {
                    openLight();
                }
                break;
            case R.id.layout_createRandomCode:
                //创建随机码
                layout_createRandomCode.setClickable(false);
                create_randomCode();
                // SocketMessage(null);
                break;
            case R.id.btn_question:
//                if (getAgrement == null) {
//                    getAgrement = new GetAgrement(this);
//                }
//                getAgrement.getAggrement_Detail(null,"PricingRules", "我知道了");
                if (common_brandRule == null) {
                    if (layout_parent == null) {
                        layout_parent = (RelativeLayout) findViewById(R.id.layout_parent);
                    }
                    common_brandRule = new Common_brandRule(this, layout_parent, false, null);
                }
                common_brandRule.showRulePop();
                break;
        }
    }


    private void openLight() //开闪光灯
    {
        camera = google.zxing.camera.CameraManager.getCamera(); //我们先前在CameraManager类中添加的静态方法
        params = camera.getParameters();
        params.setFlashMode(camera.getParameters().FLASH_MODE_TORCH);
        camera.setParameters(params);
        camera.startPreview();
        isOpen = true;
    }

    private void closeLight() //关闪光灯
    {
        params.setFlashMode(camera.getParameters().FLASH_MODE_OFF);
        camera.setParameters(params);
        isOpen = false;
    }

    /**
     * 创建随机码
     */
    private void create_randomCode() {
        KeyBordUtil.hintKeyboard(this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject json = new JSONObject();
        try {
            json.put("device_id", MyApplication.getDeviceId());
            json.put("uid", MyApplication.getUid());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", json.toString());
        String sign = Md5.md5(json.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.CREATE_RANDOM_CODE, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    toastMessage(response.getString("msg"));
                    if (response.getString("status").equals("0")) {
                        tv_randomCode.setText(response.getJSONObject("data").getString("code"));
                    } else {
                        // toastMessage(response.getString("msg"));
                    }
                } catch (Exception e) {
                    toastMessage("数据解析异常");
                }
                layout_createRandomCode.setClickable(true);
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("网络异常，请检查网络后重试");
                layout_createRandomCode.setClickable(true);
            }
        });
    }


    /**
     * 获取最新的资金状态
     */
    private void quary_money() {
        KeyBordUtil.hintKeyboard(this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject json = new JSONObject();
        try {
            json.put("device_id", MyApplication.getDeviceId());
            json.put("uid", MyApplication.getUid());
            json.put("username", MyApplication.getUserJson().getString("m_username"));
            json.put("pwd", MyApplication.getPasswprd());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", json.toString());
        String sign = Md5.md5(json.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.QUARY_MONEY, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    toastMessage(response.getString("msg"));
                    if (response.getString("status").equals("0")) {
                        MyApplication.getUserJson().put("money", response.getJSONObject("data").getLong("money"));
                        MyApplication.getUserJson().put("clothes", response.getJSONObject("data").getLong("clothes"));
                        MyApplication.getUserJson().put("interal", response.getJSONObject("data").getLong("interal"));
                        MyApplication.getUserJson().put("card", response.getJSONObject("card").getLong("card"));
                    } else {
                        // toastMessage(response.getString("msg"));
                    }
                } catch (Exception e) {
                    toastMessage("数据解析异常");
                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("网络异常，请检查网络后重试");

            }
        });
    }

    /**
     * 获取品牌
     */


//    /**
//     * 发送开箱信息，放入衣物
//     */
//    private void SocketMessage(JSONObject json) {
//        try {
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("apiType", RequestTag.OPEN_ARK);
//                jsonObject.put("ark_id", json.getString("ark_id"));
//                jsonObject.put("client_id", "APP_" + MyApplication.getUid());//客户端ID（APP_+用户UID）
//                jsonObject.put("type", json.getString("type"));//1=存件码开箱 2=取件码开箱
//                jsonObject.put("uid", MyApplication.getUid());
////                jsonObject.put("box_type","");//打开大小箱，1=衣柜箱子（大） 2=鞋柜箱子（小）
//                jsonObject.put("mobile", MyApplication.getUserJson().getString("mobile"));
//                jsonObject.put("name", MyApplication.getUserJson().getString("name"));
//                jsonObject.put("nickname", MyApplication.getUserJson().getString("nickname"));
//                if (json.getString("type").equals("1")) {
//                    if (orderaddmoney) {
//                        //加急
//                        jsonObject.put("additional", 1 + "");//type=1时，开type=1时必传 1=加急 0=正常
//                    } else {
//                        jsonObject.put("additional", 0 + "");//type=1时，开type=1时必传 1=加急 0=正常
//                    }
//                }
//
//                jsonObject.put("key", RequestTag.SOCKET_KEY);
//                jsonObject.put("sign", Md5.md5_socket(jsonObject.toString()));
//
//            } catch (Exception e) {
//
//            }
////有空指针的可能
//            Log.d("qwr", jsonObject.toString());
//            boolean isSend = iBackService.sendMessage(jsonObject.toString());//Send Content by socket
////            Toast.makeText(this, isSend ? "success" : "fail",
////                    Toast.LENGTH_SHORT).show();
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }

//    /**
//     * 发送开箱信息,获取衣物
//     */
//    private void SocketMessage_getBox(JSONObject json2) {
//        try {
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("apiType", RequestTag.OPEN_ARK);
//                jsonObject.put("ark_id", json2.getString("ark_id"));
//                jsonObject.put("client_id", "APP_" + MyApplication.getUid());//客户端ID（APP_+用户UID）
//                jsonObject.put("type", 2 + "");//1=存件码开箱 2=取件码开箱
//                jsonObject.put("uid", MyApplication.getUid());
//                jsonObject.put("mobile", MyApplication.getUserJson().getString("mobile"));
//                jsonObject.put("name", MyApplication.getUserJson().getString("name"));
//                jsonObject.put("nickname", MyApplication.getUserJson().getString("nickname"));
//                jsonObject.put("box_number", json2.getString("box_number"));//type=2时，开指定箱子编号（查询订单时会返回箱子ID和箱子编号）
//                jsonObject.put("order_id", json2.getString("id"));
//                jsonObject.put("key", RequestTag.SOCKET_KEY);
//                jsonObject.put("sign", Md5.md5_socket(jsonObject.toString()));
//
//            } catch (Exception e) {
//
//            }
////有空指针的可能
//            boolean isSend = iBackService.sendMessage(jsonObject.toString());//Send Content by socket
////            Toast.makeText(this, isSend ? "success" : "fail",
////                    Toast.LENGTH_SHORT).show();
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }


    /**
     * 查询待支付订单
     */
    private void query_NoOrder(final JSONObject jsonObject) {
        KeyBordUtil.hintKeyboard(this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject json = new JSONObject();
        try {
            json.put("device_id", MyApplication.getDeviceId());
            json.put("uid", MyApplication.getUid());
            json.put("ark_id", jsonObject.getString("ark_id"));
            json.put("username", MyApplication.getUserJson().getString("m_username"));
            json.put("pwd", MyApplication.getPasswprd());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", json.toString());
        String sign = Md5.md5(json.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.QUERY_ORDER_NOPAY, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    Log.d("daizhifu", response.toString());
                    if (response.getString("status").equals("0")) {
                        // tv_randomCode.setText(response.getJSONObject("data").getString("code"));
                        if (response.getJSONObject("data") != null) {
                            //data存在则证明当前有未支付订单，则跳转支付窗口支付费用后再调用开箱请求接口。
                            order_id = response.getJSONObject("data").getString("id");
                            //showGetData(response.getJSONObject("data"));
                            if (!utils.StringUtils.isEmpty(order_id)) {
                                //跳转到订单详情界面
                                Bundle bundle = new Bundle();
                                try {
                                    bundle.putString("order_id", order_id);
                                    bundle.putString("status", response.getJSONObject("data").getString("status"));
                                    bundle.putInt("type", response.getJSONObject("data").getInt("status"));
                                    bundle.putBoolean("isPay", true);
                                    bundle.putString("qrcode", 1 + "");
                                    bundle.putString("json_pay", response.getJSONObject("data").toString());
                                    if (orderaddmoney) {
                                        bundle.putString("additional", 1 + "");
                                    } else {
                                        bundle.putString("additional", 0 + "");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                startActivity_Bundle(CaptureActivity.this, OrderDetailActivity.class, bundle);
                                finish();
                            } else {
                                toastMessage("未获取到订单id,请退出后重试");
                            }
                        } else {
                            //不存在未支付的订单
                            // SocketMessage(jsonObject);
                        }
                    } else {
                        toastMessage(response.getString("msg"));
                        handler.restartPreviewAndDecode();
                    }
                } catch (Exception e) {
                    //toastMessage("数据解析异常");
                }
                layout_createRandomCode.setClickable(true);
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("网络异常，请检查网络后重试");
                layout_createRandomCode.setClickable(true);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        //  mLocalBroadcastManager.registerReceiver(mReciver, mIntentFilter);
        //  bindService(mServiceIntent, conn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        unbindService(conn);
//        mLocalBroadcastManager.unregisterReceiver(mReciver);
    }

    public void showGetData(final JSONObject jsonObject) {
        if (onclick == null) {
            onclick = new Type_choose.Onclick() {
                @Override
                public void OnClickLisener(View v) {
                    if (v.getId() == R.id.relayout_detail) {
                        if (!utils.StringUtils.isEmpty(order_id)) {
                            //跳转到订单详情界面
                            Bundle bundle = new Bundle();
                            try {
                                bundle.putString("order_id", order_id);
                                bundle.putString("status", jsonObject.getString("status"));
                                bundle.putInt("type", jsonObject.getInt("status"));
//                           bundle.putString("number", jsonObject.getString("number"));
//                           bundle.putString("status", jsonObject.getString("status"));
                                if (orderaddmoney) {
                                    bundle.putString("additional", 1 + "");
                                } else {
                                    bundle.putString("additional", 0 + "");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            startActivity_Bundle(CaptureActivity.this, OrderDetailActivity.class, bundle);
                        } else {
                            toastMessage("未获取到订单id,请退出后重试");
                        }

                    }
                }

                @Override
                public void conten(String payTyp, String payOther, View v) {
                    if (v.getId() == R.id.tv_pay) {
                        //支付
                        if (!utils.StringUtils.isEmpty(order_id)) {
                            createPayOrder(payTyp, payOther, jsonObject, v);
                        } else {
                            toastMessage("未获取到订单id,请退出后重试");
                            v.setClickable(true);
                            handler.restartPreviewAndDecode();
                        }
                    }

                }
            };
        }
        if (dialog == null) {
            dialog = new Type_choose(this, R.style.loading_dialog, onclick, MyApplication.getUserJson(), 1000d, 0d, 0d, true);//100d价格
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉黑色头部
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);//只有这样才能去掉黑色背景
        }
        dialog.show();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = DisplayUtil.getScreenWidth(this); //设置宽度
        dialog.getWindow().setAttributes(lp);
    }

    public void dismiss() {
        dialog.dismiss();
    }

    //支付
    private void pay(final Context context, String order, final JSONObject jsonObject, final View view) {
        PayType payType = new PayType();
        PayType.SucceedPay succeedPay = new PayType.SucceedPay() {
            @Override
            public void callBack(Boolean back, String s) {
                if (s.contains("支付成功")) {
                    //二次验证
                    //paySecondesSure();
                    toastMessage("成功");
                    //  SocketMessage_getBox(jsonObject);
                    quary_money();

                } else {
                    Toast.makeText(context, s.toString(), Toast.LENGTH_LONG).show();
                    try {
                        view.setClickable(true);
                        handler.restartPreviewAndDecode();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        order = order.replace("\"", "");
        payType.pay_zfb(context, order.replace("\"", "").replace("\"", ""), succeedPay);
    }

    /**
     * 创建支付订单
     */
    private void createPayOrder(String payType, final String payOther, final JSONObject jsonObject, final View view) {
        KeyBordUtil.hintKeyboard(this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject json = new JSONObject();
        try {
            json.put("device_id", MyApplication.getDeviceId());
            json.put("uid", MyApplication.getUid());
            json.put("paytype", payType);
            json.put("payother", payOther);
            Log.d("zhifubao", payType + "," + payOther);
            json.put("order_id", order_id);
            json.put("order_type", "user_write");
            json.put("username", MyApplication.getUserJson().getString("m_username"));
            json.put("pwd", MyApplication.getPasswprd());
            json.put("request_type", "app");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", json.toString());
        String sign = Md5.md5(json.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.POST_PAY, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    view.setClickable(true);
                    toastMessage(response.getString("msg"));
                    Log.d("pay", response.toString());
                    if (response.getString("status").equals("0")) {
                        if (utils.StringUtils.isEmpty(response.getString("openbox"))) {
                            if (!utils.StringUtils.isEmpty(response.getString("data"))) {
                                if(payOther.contains("weixin")){
                                    //微信支付
                                    startActivity(new Intent(CaptureActivity.this, WXPayEntryActivity.class).putExtra("wxDate",response.getJSONObject("data").toString()));
                                    Constants.APP_ID = response.getJSONObject("data").getString("appid");
                                }else{
                                    //支付宝支付
                                    pay(CaptureActivity.this, response.getString("data"), jsonObject, view);
                                }

                                dialog.dismiss();
                            }
                        } else {
                            if (!utils.StringUtils.isEmpty(response.getString("openbox"))) {
                                jsonObject.put("box_number", response.getString("box_number"));
                                //   SocketMessage_getBox(jsonObject);
                                quary_money();
                                dialog.dismiss();
                            }
                        }

                    } else {
                        // toastMessage(response.getString("msg"));
                        handler.restartPreviewAndDecode();
                    }
                } catch (Exception e) {
                    toastMessage("数据解析异常");
                    view.setClickable(true);
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("网络异常，请检查网络后重试");
                view.setClickable(true);
            }
        });
    }


    private void showPopwindow_additional() {
        if (popWindow_additional == null) {
            layout_parent = (RelativeLayout) findViewById(R.id.layout_parent);
            int width = getResources().getDisplayMetrics().widthPixels / 10 * 8;
//            int height = getResources().getDisplayMetrics().widthPixels / 2.2;
            View popView = LayoutInflater.from(this).inflate(R.layout.delete_img_pop, null);
            popWindow_additional = new PopupWindow(popView, width, LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView tv_title = (TextView) popView.findViewById(R.id.tv_title);
            tv_title.setText("是否加急");
            tv_cancel = (TextView) popView.findViewById(R.id.tv_cancel);
            tv_sure = (TextView) popView.findViewById(R.id.tv_sure);
            tv_content = (MyTextView) popView.findViewById(R.id.tv_address);
            tv_content.setVisibility(View.VISIBLE);
            try {
                tv_content.setSpecifiedTextsColor("订单加急加收" + MyApplication.getConfigrationJson().getJSONObject("other").getString("orderaddmoney") + "元，确定要加急吗？", MyApplication.getConfigrationJson().getJSONObject("other").getString("orderaddmoney"), Color.parseColor("#FF6600"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            popWindow_additional.setAnimationStyle(R.style.popwin_anim_style);
            popWindow_additional.setFocusable(true);
            popWindow_additional.setOutsideTouchable(false);// 设置同意在外点击消失
            ColorDrawable dw = new ColorDrawable(0x30000000);
            popWindow_additional.setBackgroundDrawable(dw);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.tv_cancel:
                            popWindow_additional.dismiss();
                            break;
                        case R.id.tv_sure:
                            popWindow_additional.dismiss();
                            //加急
                            try {
                                saomiao_tvUrgent.setText("本次加急费" + MyApplication.getConfigrationJson().getJSONObject("other").getString("orderaddmoney") + "元");
                            } catch (Exception e) {
                                e.printStackTrace();
                                saomiao_tvUrgent.setText("本次使用将加急");
                            }
                            saomiao_tvUrgent.setTextColor(ContextCompat.getColor(CaptureActivity.this, R.color.white));
                            saomiao_tvUrgent.setBackground(ContextCompat.getDrawable(CaptureActivity.this, R.drawable.fe_rect));
                            orderaddmoney = true;
                            break;
                    }
                }
            };
            popWindow_additional.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    DisplayUtil.backgroundAlpha(1.0f, CaptureActivity.this);
                }
            });
            tv_sure.setOnClickListener(onClickListener);
            tv_cancel.setOnClickListener(onClickListener);
        }
        DisplayUtil.backgroundAlpha(0.3f, this);
        popWindow_additional.showAtLocation(layout_parent, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
    }



    /**
     * 是否开启GPS
     */
    private void show_gps() {
        if (popupWindow_gps == null) {
            int width = getResources().getDisplayMetrics().widthPixels / 10 * 8;
            int height = getResources().getDisplayMetrics().heightPixels / 4;
            View popView = LayoutInflater.from(this).inflate(R.layout.gps_pop_item, null);
            popupWindow_gps = new PopupWindow(popView, width, height);
            tv_content_gps = (TextView) popView.findViewById(R.id.tv_content);
            tv_cancle_gps = (TextView) popView.findViewById(R.id.tv_cancel);
            tv_sure_gps = (TextView) popView.findViewById(R.id.tv_sure);
            popupWindow_gps.setAnimationStyle(R.style.popwin_anim_style);
            popupWindow_gps.setFocusable(false);
            popupWindow_gps.setOutsideTouchable(false);// 设置同意在外点击消失
            ColorDrawable dw = new ColorDrawable(0x30000000);
            popupWindow_gps.setBackgroundDrawable(dw);
            popupWindow_gps.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    //.setVisibility(View.GONE);
                }
            });
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.tv_cancel:
                            popupWindow_gps.dismiss();
                            break;
                        case R.id.tv_sure:
                            if (permissionPageUtils == null) {
                                permissionPageUtils = new PermissionPageUtils(CaptureActivity.this);
                            }
                            permissionPageUtils.jumpPermissionPage();
//                            Intent intent = new Intent();
//                            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                            startActivityForResult(intent, 1315);
                            popupWindow_gps.dismiss();
                            break;
                    }
                }
            };
            tv_content_gps.setText("请开启拍照权限,否则扫码功能将不能正常使用。");
            tv_cancle_gps.setOnClickListener(onClickListener);
            tv_sure_gps.setOnClickListener(onClickListener);
        }
        if (layout_parent == null) {
            layout_parent = (RelativeLayout) findViewById(R.id.layout_parent);
        }
        //popupWindow_gps.showAtLocation(layout_parent, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);

        Runnable showPopWindowRunnable = new Runnable() {

            @Override
            public void run() {
                // 得到activity中的根元素

                // 如何根元素的width和height大于0说明activity已经初始化完毕
                if (layout_parent != null && layout_parent.getWidth() > 0 && layout_parent.getHeight() > 0) {
                    // 显示popwindow
                    popupWindow_gps.showAtLocation(layout_parent, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                    // 停止检测
                    mHandler.removeCallbacks(this);
                } else {
                    // 如果activity没有初始化完毕则等待5毫秒再次检测
                    mHandler.postDelayed(this, 5);
                }
            }
        };
        if (popupWindow_gps != null && !popupWindow_gps.isShowing()) {
            // 开始检测
            if (mHandler == null) {
                mHandler = new Handler();
            }
            mHandler.post(showPopWindowRunnable);
        }

    }
    public void showMyToast(final Toast toast, final int cnt) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 3000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
                finish();
            }
        }, cnt );
    }
}