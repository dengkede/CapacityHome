//package com.example.administrator.capacityhome.navigation;
//
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.content.ContextCompat;
//import android.util.Log;
//import android.view.View;
//import android.view.animation.AnimationUtils;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.baidu.location.BDLocation;
//import com.baidu.location.LocationClient;
//import com.baidu.location.LocationClientOption;
//import com.baidu.mapapi.bikenavi.BikeNavigateHelper;
//import com.baidu.mapapi.bikenavi.adapter.IBEngineInitListener;
//import com.baidu.mapapi.bikenavi.adapter.IBRoutePlanListener;
//import com.baidu.mapapi.bikenavi.model.BikeRoutePlanError;
//import com.baidu.mapapi.bikenavi.params.BikeNaviLauchParam;
//import com.baidu.mapapi.map.BaiduMap;
//import com.baidu.mapapi.map.MapStatus;
//import com.baidu.mapapi.map.MapStatusUpdateFactory;
//import com.baidu.mapapi.map.MapView;
//import com.baidu.mapapi.map.MyLocationData;
//import com.baidu.mapapi.model.LatLng;
//import com.baidu.navisdk.adapter.BNOuterLogUtil;
//import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
//import com.baidu.navisdk.adapter.BNRoutePlanNode;
//import com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType;
//import com.baidu.navisdk.adapter.BNaviSettingManager;
//import com.baidu.navisdk.adapter.BaiduNaviManager;
//import com.baidu.navisdk.adapter.BaiduNaviManager.NaviInitListener;
//import com.baidu.navisdk.adapter.BaiduNaviManager.RoutePlanListener;
//import com.example.tanbo.activity.*;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//
//import MyView.MyToast;
//
//public class MapActivity extends com.example.tanbo.activity.BaseActivity {
//    /**
//     * 骑行导航
//     */
//    private BikeNavigateHelper mNaviHelper;
//    BikeNaviLauchParam param;
//    private static boolean isPermissionRequested = false;
//    //骑行导航相关结束
//    public static List<Activity> activityList = new LinkedList<Activity>();
//
//    private static final String APP_FOLDER_NAME = "BNSDKSimpleDemo";
//    private int[] locationTypeId = new int[]{R.id.walk,R.id.driver,R.id.bike};//导航方式选择ID
//    private TextView[] locationTypeViews;//导航方式
//    private RelativeLayout location_back;//返回
//    private String mSDCardPath = null;
//    // 定位相关
//    LocationClient mLocClient;
//    //定位监听
//    public MyLocationListenner myListener = new MyLocationListenner();
//    MapView mMapView = null;
//    BaiduMap mBaiduMap;
//    boolean isFirstLoc = true; // 是否首次定位
//    BDLocation mlocation;
//    public static final String ROUTE_PLAN_NODE = "routePlanNode";
//    public static final String SHOW_CUSTOM_ITEM = "showCustomItem";
//    public static final String RESET_END_NODE = "resetEndNode";
//    public static final String VOID_MODE = "voidMode";
//
//    private final static String authBaseArr[] =
//            { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION };
//    private final static String authComArr[] = { Manifest.permission.READ_PHONE_STATE };
//    private final static int authBaseRequestCode = 1;
//    private final static int authComRequestCode = 2;
//    private int changePosition = 1;//记录上次选择位置,0步行，1驾车，2骑车
//    private boolean hasInitSuccess = false;
//    private boolean hasRequestComAuth = false;
//    private Double longitude;
//    private Double latitude;
//    private TextView tv_daohang;//开始导航
//    private Boolean isInit  =  false;//初始化是否成功
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        // TODO Auto-generated method stub
//        super.onCreate(savedInstanceState);
//
//        activityList.add(this);
//        setContentView(R.layout.activity_map);
//        init();
//        tv_daohang = (TextView) findViewById(R.id.tv_daohang);
//        locationTypeViews = new TextView[locationTypeId.length];
//        location_back = (RelativeLayout) findViewById(R.id.location_back);
//        BNOuterLogUtil.setLogSwitcher(true);
//        View.OnClickListener onClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                v.startAnimation(new AnimationUtils().loadAnimation(MapActivity.this, R.anim.bg_alpha));
//                switch (v.getId()){
//
//                    case R.id.driver:
//                        //驾车
//                        changeFont(locationTypeViews[1],locationTypeViews[changePosition],1);
//                        if (BaiduNaviManager.isNaviInited()) {
//                            routeplanToNavi(CoordinateType.BD09LL);
//                        }
//                        break;
//
//                }
//            }
//        };
//        tv_daohang.setOnClickListener(onClickListener);
//        for (int i = 0;i<locationTypeId.length;i++){
//            locationTypeViews[i] = (TextView) findViewById(locationTypeId[i]);
//            locationTypeViews[i].setOnClickListener(onClickListener);
//        }
//        location_back.setOnClickListener(onClickListener);
//        if (initDirs()) {
//            initNavi();
//        }
//        requestPermission();
//        mNaviHelper = BikeNavigateHelper.getInstance();
//        LatLng startPt = new LatLng(MyApplication.latitude, MyApplication.longitude);
//        LatLng endPt = new LatLng(latitude, longitude);
//        param = new BikeNaviLauchParam().stPt(startPt).endPt(endPt);
//        // BNOuterLogUtil.setLogSwitcher(true);
//
//    }
//    //初始化地图
//    private void init(){
//        try {
//            JSONObject jsonObject = new JSONObject(getIntent().getExtras().getString("endAddress"));
//            longitude = Double.parseDouble(jsonObject.getString("longitude"));
//            latitude  = Double.parseDouble(jsonObject.getString("latitude"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        //获取地图控件引用
//        mMapView = (MapView) findViewById(R.id.bmapView);
//        mBaiduMap = mMapView.getMap();
//        // 开启定位图层
//        mBaiduMap.setMyLocationEnabled(true);
//        // 定位初始化
//        mLocClient = new LocationClient(this);
//        mLocClient.registerLocationListener(myListener);
//        LocationClientOption option = new LocationClientOption();
//        option.setOpenGps(true); // 打开gps
//        option.setCoorType("bd09ll"); // 设置坐标类型
//        option.setScanSpan(1000);
//        option.setAddrType("all");
//        mLocClient.setLocOption(option);
//        mLocClient.start();
//    }
//
//    private boolean initDirs() {
//        mSDCardPath = getSdcardDir();
//        if (mSDCardPath == null) {
//            return false;
//        }
//        File f = new File(mSDCardPath, APP_FOLDER_NAME);
//        if (!f.exists()) {
//            try {
//                f.mkdir();
//            } catch (Exception e) {
//                e.printStackTrace();
//                return false;
//            }
//        }
//        return true;
//    }
//
//    String authinfo = null;
//
//    /**
//     * 内部TTS播报状态回传handler
//     */
//    private Handler ttsHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            int type = msg.what;
//            switch (type) {
//                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
//                    //showToastMsg("Handler : TTS play start");
//                    break;
//                }
//                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
//                    //showToastMsg("Handler : TTS play end");
//                    break;
//                }
//                default:
//                    break;
//            }
//        }
//    };
//
//    /**
//     * 内部TTS播报状态回调接口
//     */
//    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {
//
//        @Override
//        public void playEnd() {
//            //showToastMsg("TTSPlayStateListener : TTS play end");
//        }
//
//        @Override
//        public void playStart() {
//            //showToastMsg("TTSPlayStateListener : TTS play start");
//        }
//    };
//
//    public void showToastMsg(final String msg) {
//        MapActivity.this.runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//                Toast.makeText(MapActivity.this, msg, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private boolean hasBasePhoneAuth() {
//        // TODO Auto-generated method stub
//
//        PackageManager pm = this.getPackageManager();
//        for (String auth : authBaseArr) {
//            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    private boolean hasCompletePhoneAuth() {
//        // TODO Auto-generated method stub
//
//        PackageManager pm = this.getPackageManager();
//        for (String auth : authComArr) {
//            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    private void initNavi() {
//
//        BNOuterTTSPlayerCallback ttsCallback = null;
//        // 申请权限
//        if (android.os.Build.VERSION.SDK_INT >= 23) {
//
//            if (!hasBasePhoneAuth()) {
//
//                this.requestPermissions(authBaseArr, authBaseRequestCode);
//                return;
//
//            }
//        }
//
//        BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME, new NaviInitListener() {
//            @Override
//            public void onAuthResult(int status, String msg) {
//                if (0 == status) {
//                    authinfo = "key校验成功!";
//                } else {
//                    authinfo = "key校验失败, " + msg;
//                }
//                MapActivity.this.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        Toast.makeText(MapActivity.this, authinfo, Toast.LENGTH_LONG).show();
//                    }
//                });
//            }
//
//            public void initSuccess() {
//                Toast.makeText(MapActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
//                hasInitSuccess = true;
//                initSetting();
//            }
//
//            public void initStart() {
//                Toast.makeText(MapActivity.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
//            }
//
//            public void initFailed() {
//                //Toast.makeText(MapActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
//            }
//
//        }, null, ttsHandler, ttsPlayStateListener);
//
//    }
//
//    private String getSdcardDir() {
//        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
//            return Environment.getExternalStorageDirectory().toString();
//        }
//        return null;
//    }
//
//    private CoordinateType mCoordinateType = null;
//
//    private void routeplanToNavi(CoordinateType coType) {
//        mCoordinateType = coType;
//        if (!hasInitSuccess) {
//            Toast.makeText(MapActivity.this, "还未初始化!", Toast.LENGTH_SHORT).show();
//        }
//        // 权限申请
//        if (android.os.Build.VERSION.SDK_INT >= 23) {
//            // 保证导航功能完备
//            if (!hasCompletePhoneAuth()) {
//                if (!hasRequestComAuth) {
//                    hasRequestComAuth = true;
//                    this.requestPermissions(authComArr, authComRequestCode);
//                    return;
//                } else {
//                    Toast.makeText(MapActivity.this, "没有完备的权限!", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//        }
//        BNRoutePlanNode sNode = null;
//        BNRoutePlanNode eNode = null;
//
//        switch (coType) {
//            case BD09LL: {
//                sNode = new BNRoutePlanNode(mlocation.getLongitude(),mlocation.getLatitude(),
//                        getIntent().getStringExtra("getAddress"), null, coType);
//                eNode = new BNRoutePlanNode(longitude, latitude, "", null, coType);
//                break;
//            }
//            default:
//                ;
//        }
//        if (sNode != null && eNode != null) {
//            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
//            list.add(sNode);
//            list.add(eNode);
//            BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode));
//        }
//    }
//
//    public class DemoRoutePlanListener implements RoutePlanListener {
//
//        private BNRoutePlanNode mBNRoutePlanNode = null;
//
//        public DemoRoutePlanListener(BNRoutePlanNode node) {
//            mBNRoutePlanNode = node;
//        }
//
//        @Override
//        public void onJumpToNavigator() {
//            /*
//             * 设置途径点以及resetEndNode会回调该接口
//             */
//
//            for (Activity ac : activityList) {
//
//                if (ac.getClass().getName().endsWith("BNDemoMainActivity")) {
//
//                    return;
//                }
//            }
//            Intent intent = new Intent(MapActivity.this,BNDemoMainActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
//            intent.putExtras(bundle);
//            startActivity(intent);
//
//        }
//
//        @Override
//        public void onRoutePlanFailed() {
//            // TODO Auto-generated method stub
//            Toast.makeText(MapActivity.this, "算路失败", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void initSetting() {
//        // BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
//        BNaviSettingManager
//                .setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
//        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
//        // BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
//        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
//    }
//
//    @Override
//    protected void onDestroy() {
//        // 退出时销毁定位
//        mLocClient.stop();
//        // 关闭定位图层
//        mBaiduMap.setMyLocationEnabled(false);
//        mMapView.onDestroy();
//        mMapView = null;
//        super.onDestroy();
//    }
//
//    @Override
//    protected void onPause() {
//        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
//        mMapView.onPause();
//        super.onPause();
//    }
//
//    @Override
//    protected void onResume() {
//        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
//        mMapView.onResume();
//        super.onResume();
//    }
//    private BNOuterTTSPlayerCallback mTTSCallback = new BNOuterTTSPlayerCallback() {
//
//        @Override
//        public void stopTTS() {
//            // TODO Auto-generated method stub
//            Log.e("test_TTS", "stopTTS");
//        }
//
//        @Override
//        public void resumeTTS() {
//            // TODO Auto-generated method stub
//            Log.e("test_TTS", "resumeTTS");
//        }
//
//        @Override
//        public void releaseTTSPlayer() {
//            // TODO Auto-generated method stub
//            Log.e("test_TTS", "releaseTTSPlayer");
//        }
//
//        @Override
//        public int playTTSText(String speech, int bPreempt) {
//            // TODO Auto-generated method stub
//            Log.e("test_TTS", "playTTSText" + "_" + speech + "_" + bPreempt);
//
//            return 1;
//        }
//
//        @Override
//        public void phoneHangUp() {
//            // TODO Auto-generated method stub
//            Log.e("test_TTS", "phoneHangUp");
//        }
//
//        @Override
//        public void phoneCalling() {
//            // TODO Auto-generated method stub
//            Log.e("test_TTS", "phoneCalling");
//        }
//
//        @Override
//        public void pauseTTS() {
//            // TODO Auto-generated method stub
//            Log.e("test_TTS", "pauseTTS");
//        }
//
//        @Override
//        public void initTTSPlayer() {
//            // TODO Auto-generated method stub
//            Log.e("test_TTS", "initTTSPlayer");
//        }
//
//        @Override
//        public int getTTSState() {
//            // TODO Auto-generated method stub
//            Log.e("test_TTS", "getTTSState");
//            return 1;
//        }
//    };
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        // TODO Auto-generated method stub
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == authBaseRequestCode) {
//            for (int ret : grantResults) {
//                if (ret == 0) {
//                    continue;
//                } else {
//                    Toast.makeText(MapActivity.this, "缺少导航基本的权限!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }
//            initNavi();
//        } else if (requestCode == authComRequestCode) {
//            for (int ret : grantResults) {
//                if (ret == 0) {
//                    continue;
//                }
//            }
//            routeplanToNavi(mCoordinateType);
//        }
//    }
//    /**
//     * 骑行导航相关方法
//     */
//    private void startBikeNavi() {
//        Log.d("View", "startBikeNavi");
//        mNaviHelper.initNaviEngine(this, new IBEngineInitListener() {
//            @Override
//            public void engineInitSuccess() {
//                Log.d("View", "engineInitSuccess");
//                routePlanWithParam();
//            }
//
//            @Override
//            public void engineInitFail() {
//                Log.d("View", "engineInitFail");
//                // routePlanWithParam();
//            }
//        });
//    }
//
//    private void routePlanWithParam() {
//        mNaviHelper.routePlanWithParams(param, new IBRoutePlanListener() {
//            @Override
//            public void onRoutePlanStart() {
//                Log.d("View", "onRoutePlanStart");
//            }
//
//            @Override
//            public void onRoutePlanSuccess() {
//                Log.d("View", "onRoutePlanSuccess");
//                Intent intent = new Intent();
//                intent.setClass(MapActivity.this, BNaviGuideActivity.class);
//                startActivity(intent);
//            }
//
//            @Override
//            public void onRoutePlanFail(BikeRoutePlanError error) {
//                Log.d("View", "onRoutePlanFail");
//            }
//
//        });
//    }
//
//
//    private void requestPermission() {
//        if (Build.VERSION.SDK_INT >= 23 && !isPermissionRequested) {
//
//            isPermissionRequested = true;
//
//            ArrayList<String> permissions = new ArrayList<>();
//            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
//            }
//
//            if (permissions.size() == 0) {
//                return;
//            } else {
//                requestPermissions(permissions.toArray(new String[permissions.size()]), 0);
//            }
//        }
//    }
//    //结束
//    /**
//     * 改变字体透明度
//     * position当前选择位置
//     */
//    private void changeFont(TextView tv1,TextView tv2,int position){
//        if(changePosition!=position) {
//            tv1.setTextColor(ContextCompat.getColor(this,R.color.white));
//            tv2.setTextColor(ContextCompat.getColor(this,R.color.touming_white));
//            changePosition = position;
//        }
//    }
//    /**
//     * 定位SDK监听函数
//     */
//
//    public class MyLocationListenner implements com.baidu.location.BDLocationListener {
//        @Override
//        public void onReceiveLocation(BDLocation location) {
//            // map view 销毁后不在处理新接收的位置
//            if (location == null || mMapView == null) {
//                return;
//            }
//            mlocation = location;
//
//            MyLocationData locData = new MyLocationData.Builder()
//                    .accuracy(mlocation.getRadius())
//                    // 此处设置开发者获取到的方向信息，顺时针0-360
//                    .direction(100).latitude(mlocation.getLatitude())
//                    .longitude(mlocation.getLongitude()).build();
//            mBaiduMap.setMyLocationData(locData);
//            if (isFirstLoc) {
//                isFirstLoc = false;
//                LatLng ll = new LatLng(location.getLatitude(),
//                        location.getLongitude());
//                MapStatus.Builder builder = new MapStatus.Builder();
//                builder.target(ll).zoom(18.0f);
//                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
//            }
//        }
//
//        @Override
//        public void onConnectHotSpotMessage(String s, int i) {
//
//        }
//
//        public void onReceivePoi(BDLocation poiLocation) {
//        }
//    }
//}
