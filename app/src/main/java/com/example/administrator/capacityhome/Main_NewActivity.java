package com.example.administrator.capacityhome;

import android.Manifest;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.baidu.navisdk.adapter.BNCommonSettingParam;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.dabin.dpns.utils.Utils;
import com.example.administrator.capacityhome.feedback.FeedBackActivity;
import com.example.administrator.capacityhome.login.LoginActivity;
import com.example.administrator.capacityhome.message.MessageActivity;
import com.example.administrator.capacityhome.myorder.MyOrderActivity;
import com.example.administrator.capacityhome.myorder.OrderDetailActivity;
import com.example.administrator.capacityhome.mywallet.MyWalletActivity;
import com.example.administrator.capacityhome.navigation.BNDemoMainActivity;
import com.example.administrator.capacityhome.navigation.WNaviGuideActivity;
import com.example.administrator.capacityhome.selfcenter.MoneyDetailActivity;
import com.example.administrator.capacityhome.selfcenter.SelfCenterActivity;
import com.example.administrator.capacityhome.smarthome.SmartHomeActivity;
import com.example.administrator.capacityhome.wxapi.Constants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.DownloadResponseHandler;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.apache.commons.net.io.Util;
import org.feng.sockettest.server.IBackService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import adapter.BrandRuleAdapter;
import adapter.MyBrandListAdapter;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import fragment.HomeFragment;
import fragment.MyOrderFragment;
import fragment.RechargeCenterFragment;
import fragment.StorePriceFragment;
import google.zxing.activity.CaptureActivity;
import http.RequestTag;
import io.reactivex.annotations.NonNull;
import okhttp3.internal.platform.Platform;
import sever.BackService;
import utils.ACache;
import utils.Agreement_Dialog;
import utils.ColumnUtils;
import utils.Common_brandRule;
import utils.Constant;
import utils.DisplayUtil;
import utils.DrivingRouteOverlay;
import utils.GetAgrement;
import utils.KeyBordUtil;
import utils.Md5;
import utils.MoneyUtil;
import utils.OverlayManager;
import utils.SPUtils;
import utils.StringUtils;
import utils.TimeUtils;
import utils.TimeUtilsDate;
import utils.WalkingRouteOverlay;
import widget.CircleTransform;
import widget.MyTextView;
import widget.NumberProgressView;

/**
 * 改版主界面
 */
public class Main_NewActivity extends BaseActivity implements View.OnClickListener, BaiduMap.OnMapStatusChangeListener {

    private Fragment[] fragments;
    private int index = 0;
    private int currentIndex = 0;
    private FragmentManager fragmentManager;
    private final int FRAGMENT_COUNT = 4;
    private static final int HOME_FRAGMENT_INDEX = 0;
    private static final int STORE_PRICE_INDEX = 1;
    private static final int MYORDER_INDEX = 2;
    private static final int RECHARGE_INDEX = 3;
    private LinearLayout[] linearLayouts_tab;
    private int[] linearLayoutIds = new int[]{R.id.layout_tab1, R.id.layout_tab2, R.id.layout_tab3, R.id.layout_tab4};
    private ImageView[] img_tabs;
    private int[] img_tabsIds = new int[]{R.id.img_tab1, R.id.img_tab2, R.id.img_tab3, R.id.img_tab4};
    private TextView[] tv_tabs;
    private int[] tv_tabsIds = new int[]{R.id.tv_tab1, R.id.tv_tab2, R.id.tv_tab3, R.id.tv_tab4};
    private int[] img_resourse = new int[]{R.mipmap.foot1, R.mipmap.foot1_choose, R.mipmap.foot2, R.mipmap.foot2_choose, R.mipmap.foot3, R.mipmap.foot3_choose, R.mipmap.foot4, R.mipmap.foot4_choose};

    //主界面相关
    private MapView map_view;

    /**
     * 定位的监听器
     */
    public MyLocationListener mMyLocationListener;
    /***
     * 是否是第一次定位
     */
    private volatile boolean isFristLocation = true;

    /**
     * 最新一次的经纬度
     */
    private double mCurrentLantitude;
    private double mCurrentLongitude;
    /**
     * 当前的精度
     */
    private float mCurrentAccracy;


    private MapView mMapView;
    private BaiduMap mBaiduMap;
    public LocationClient mLocationClient;
    public BDLocationListener myListener = new MyLocationListener();
    private int[] id = new int[]{R.id.relayout_smartHome, R.id.relayout_laundy, R.id.relayout_selfCenter,
            R.id.relayout_myWallet, R.id.relayout_myOrder, R.id.relayout_feedback, R.id.relayout_laundyCard, R.id.relayout_likeBrand};
    private int[] layout_abId = new int[]{R.id.layout_tab1, R.id.layout_tab2, R.id.layout_tab3, R.id.layout_tab4, R.id.layout_tab5};
    private LinearLayout[] layout_tabViews;
    private DrawerLayout drawerLayout;
    private RelativeLayout layout_menu;
    private RelativeLayout main_imgUser;
    private ImageView home_imgUser;
    private TextView home_tvUserName;
    private TextView tv_userType;//会员类型
    private LinearLayout layout_orderList;//衣柜列表
    private TextView home_tvCredit;//信用
    private TextView home_tvBalance;//余额
    private TextView home_tvClothes;//洗衣点
    private TextView menu_smartCount;//已经添加几道门锁
    private TextView menu_tvClosetCount;//正在使用几个衣柜
    private MyTextView menu_tvOrderCount;//订单总数
    private MyTextView menu_tvOrderObligation;//待付款
    private PopupWindow popupWindow_location;//定位导航弹框
    private LinearLayout layout_car;//驾车
    private LinearLayout layout_walk;//步行
    private TextView tv_locationStart;//开始导航
    private GetAgrement getAgrement_price;//计费
    private GetAgrement getAgrement_down;//计费
    private Common_brandRule common_brandRule;
    private GetAgrement getAgrement_crdit;//信用值
    private ImageView img_car;
    private ImageView img_brand;
    private TextView tv_chooseBrand;
    private ImageView img_walk;
    private TextView tv_car;
    private TextView tv_walk;
    private ImageView location_imgType;//驾驶类型
    private TextView location_tvType;//需要...
    private TextView tv_distance;//距离
    private TextView tv_time;//时长
    private View view_zhezhao;
    private int number = 0;
    private RoutePlanSearch mSerch;
    private OnGetRoutePlanResultListener listener;
    private List<LatLng> list_locationMark;
    private LatLng my_lat;//我的定位信息
    private LatLng move_lat;//移动地图后的定位信息
    private LatLng end_lat;//终点定位信息
    private WalkNavigateHelper mNaviHelper;
    private float distance = 0;//距离
    private int time;//大概需要时长
    private boolean hasInitSuccess = false;
    private boolean hasRequestComAuth = false;
    private String mSDCardPath = null;
    private static final String APP_FOLDER_NAME = "BNSDKSimpleDemo";
    private static boolean isPermissionRequested = false;
    private final static int authBaseRequestCode = 1;
    private final static int authComRequestCode = 2;
    private String authinfo = null;
    private GetAgrement getAgrement;
    private GetAgrement getAgrement_laundry;//洗衣券
    private Agreement_Dialog.Onclick agreement_onclick;
    private final static String authBaseArr[] =
            {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_FINE_LOCATION};
    private final static String authComArr[] = {android.Manifest.permission.READ_PHONE_STATE};
    private int drive_type = 1;//导航类型 1步行
    private List<LatLng> list_location = new ArrayList<>();
    // 要申请的权限
    private String[] permissions = {android.Manifest.permission.DISABLE_KEYGUARD, Manifest.permission.WAKE_LOCK};
    private AlertDialog dialog;
    private PopupWindow popWindow_boxType;
    private PopupWindow popWindow_call;
    private TextView tv_callNumber;
    private TextView tv_callCancel;
    private TextView tv_callSure;
    private TextView tv_type1;
    private TextView tv_type2;
    private boolean is_put = true;
    private RelativeLayout img_update;
    private ACache aCache;
    private boolean isMove = false;//是否移动地图
    private LinearLayout layout_credit;//信用度父容器
    private boolean isGuihua = false;//是否规划成功能
    private ImageView line2;//阴影线，在订单出现是不显示
    private Float currentDistance = 0f;
    private TextView tv_sure_gps;
    private TextView tv_cancle_gps;
    private PopupWindow popupWindow_gps;
    private Handler mHandler;
    private boolean isFirst = true;
    private TextView tv_comboTime;//包月截止时间
    /**
     * 品牌相关
     */

    private GridView listview_Brand;
    private MyBrandListAdapter myBrandListAdapter;
    private int brandId;//品牌id
    private String brandName;//品牌名
    private List<JSONObject> list_brand;//总的数据源
    public String typeId = "";
    //private int currentPage;//当前页

    //结束
    /**
     * 喜欢的品牌
     */
    private ColumnUtils columnUtils;
    private PopupWindow pop_like;
    private TextView tv_agree;
    private View view_zhezhao_menu;
    private GridView listview_likeBrand;
    private int brandID_like = 0;
    private String brandPic_like = "";
    private MyBrandListAdapter myBrandGridAdapter;
    private List<JSONObject> list_brand_like;//总的数据源
//结束

    /**
     * 计费相关
     */
    private ScrollView scrollView_agreement;
    private TextView tv_priceRule;
    private TextView tv_userRule;
    private TextView tv_contentBrand;
    private TextView tv_titleBrand;
    private View tv_line1;
    private View tv_line2;
    private ListView listview_brand;
    private BrandRuleAdapter brandRuleAdapter;
    private ViewPager viewPager;
    private LinearLayout group;//圆点指示器
    private ImageView[] ivPoints;//小圆点图片的集合
    private int totalPage; //总的页数
    private int mPageSize = 3; //每页显示的最大的数量,品牌
    private List<JSONObject> list_brandItem = new ArrayList<>();//当前品牌下物品价格
    private List<View> viewPagerList;//GridView作为一个View对象添加到ViewPager集合中
    private RelativeLayout relayout_top;
    private ColumnUtils columnUtils_tab;//底部tab
    private List<JSONObject> list_tab = new ArrayList<>();
    private Tencent mTencent;
    private MyIUiListener mIUiListener;
    /**
     * gengxin
     */
    private PopupWindow popWindow;
    private PopupWindow popupWindow_down;
    private TextView tv_sure;
    private TextView tv_cancel_update;
    private TextView tv_content;

    private ProgressDialog progressDialog;
    private boolean isupdate = true;
    private String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final int PERMS_REQUEST_CODE = 200;
    private NumberProgressView numberProgress;
    private TextView tv_total;
    private TextView tv_current;
    private TextView tv_progress;

    /**
     * 长连接相关
     *
     * @param savedInstanceState
     */
    public static IBackService iBackService;
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iBackService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iBackService = IBackService.Stub.asInterface(service);
        }
    };
    private Intent mServiceIntent;
    private Dialog shareDialog;
    private TextView tv_wechat;
    private TextView tv_qq;
    private TextView tv_friends;
    private TextView tv_cancel;
    private IWXAPI api;

    public void requestCenterPos(MapStatus status) {
        //获取地图中心点
        move_lat = status.target;
        update2(move_lat);

    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        //地图操作的中心点
        requestCenterPos(mapStatus);
    }

    class MessageBackReciver extends BroadcastReceiver {

        public MessageBackReciver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BackService.HEART_BEAT_ACTION)) {
                //心跳
            } else {
                String message = intent.getStringExtra("message");
                Log.d("huifu", message);
                // ;UserOpenBox_REP 开箱  ACCEPT_OpenBoxMsg 衣柜发的回复消息 1-1

                if (!utils.StringUtils.isEmpty(message) && message.contains("UserOpenBox_REP")) {
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        if (jsonObject.getInt("status") == 1) {
                            if (OrderDetailActivity.handler != null) {
                                OrderDetailActivity.handler.sendEmptyMessage(1);
                            }
                            dismissLoadingView2();
                            Toast.makeText(MyApplication.getAppContext(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        if (OrderDetailActivity.handler != null) {
                            OrderDetailActivity.handler.sendEmptyMessage(1);
                        }
                        Toast.makeText(MyApplication.getAppContext(), "开箱异常，请去订单列表查看", Toast.LENGTH_SHORT).show();
                        dismissLoadingView2();
                    }
                }

                if (!utils.StringUtils.isEmpty(message) && message.contains("ACCEPT_OpenBoxMsg")) {
                    //证明是来自服务器开箱结果的消息
                    try {
                        if (OrderDetailActivity.handler != null) {
                            OrderDetailActivity.handler.sendEmptyMessage(1);
                        }
                        dismissLoadingView2();
                        JSONObject jsonObject = new JSONObject(message);
                        if (jsonObject.getInt("status") == 0) {
                            //开箱成功，关闭当前扫描界面
                            //toastMessage(jsonObject.getString("msg"));
                            if (OrderDetailActivity.handler != null) {
                                OrderDetailActivity.handler.sendEmptyMessage(2);
                            }
                            Toast.makeText(MyApplication.getAppContext(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                            if (jsonObject.getString("order_id") != null) {
                                if (is_put) {
                                    //放件,跳转到订单详情界面
                                    if (MyApplication.can_detail) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("order_id", jsonObject.getString("order_id"));
                                        MyApplication.can_detail = false;
                                        startActivity_Bundle(Main_NewActivity.this, OrderDetailActivity.class, bundle);
                                    }
                                } else {
                                    //取件
                                    if (MyApplication.can_detail) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("order_id", jsonObject.getString("order_id"));
                                        MyApplication.can_detail = false;
                                        startActivity_Bundle(Main_NewActivity.this, OrderDetailActivity.class, bundle);
                                    }
                                }
                            } else {
                                //开箱成功订单错误
                                Toast.makeText(MyApplication.getAppContext(), "开箱成功订单错误", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MyApplication.getAppContext(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
//                            toastMessage(jsonObject.getString("msg"));
//                            if (handler != null) {
//                                //重新开启扫描，解决不能连续扫描的问题
//                                toastMessage(jsonObject.getString("二次扫描"));
//                                handler.restartPreviewAndDecode();
//                            }
                        }
                    } catch (Exception e) {
                        dismissLoadingView2();
                    }
                } else {
                    //其余结果
                }
            }
        }

        ;
    }

    private MessageBackReciver mReciver;

    private IntentFilter mIntentFilter;

    private LocalBroadcastManager mLocalBroadcastManager;
    private boolean firstUpdate = true;
    private View tv_Noread;//未读消息提示

    /**
     * 结束
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);
        initView2();
        initView();
        //Android 6.0以上版本需要临时获取权限
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 &&
                PackageManager.PERMISSION_GRANTED != checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermissions(perms, PERMS_REQUEST_CODE);
        } else {
            check_update("1.3.6");
        }
        if (MyApplication.getUid() != null) {
            getRepleyList();
        }
    }

    // 这是来自 JPush Example 的设置别名的 Activity 里的代码。一般 App 的设置的调用入口，在任何方便的地方调用都可以。
    private void setAlias(boolean isHaveId) {

        // 调用 Handler 来异步设置别名
        //mHandler_jpush.sendMessage(mHandler_jpush.obtainMessage(MSG_SET_ALIAS, "YOUE_"+MyApplication.getUid()));
        if (isHaveId) {
            JPushInterface.setAlias(Main_NewActivity.this, 1, "YOUE_" + MyApplication.getUid());
        } else {
            JPushInterface.setAlias(Main_NewActivity.this, 1, "R_" + MyApplication.getDeviceId());
        }
    }

    private void initView() {
        tv_Noread = findViewById(R.id.tv_Noread);
        relayout_top = (RelativeLayout) findViewById(R.id.relayout_top);
        linearLayouts_tab = new LinearLayout[linearLayoutIds.length];
        tv_comboTime = (TextView) findViewById(R.id.tv_comboTime);
        findViewById(R.id.layout_combo).setOnClickListener(this);
        findViewById(R.id.tv_share).setOnClickListener(this);
        for (int i = 0; i < linearLayoutIds.length; i++) {
            linearLayouts_tab[i] = (LinearLayout) findViewById(linearLayoutIds[i]);
            linearLayouts_tab[i].setOnClickListener(this);
        }
        img_tabs = new ImageView[img_tabsIds.length];
        tv_tabs = new TextView[tv_tabsIds.length];
        for (int i = 0; i < img_tabsIds.length; i++) {
            img_tabs[i] = (ImageView) findViewById(img_tabsIds[i]);
        }
        for (int i = 0; i < tv_tabsIds.length; i++) {
            tv_tabs[i] = (TextView) findViewById(tv_tabsIds[i]);
        }
        fragmentManager = getSupportFragmentManager();
        findViewById(R.id.img_message).setOnClickListener(this);
        if (MyApplication.getUserJson() == null) {
//            //表示用户从没有登录
//            startActivity_leftToRight(this, LoginActivity.class);
            setAlias(false);

        }
        removeAllFragment();
        initFragment();
        setItem();
//        //调用相机权限判定
//        if (CameraCanUseUtils.isCameraCanUse()) {
//            Log.i("", "相机");
//        } else {
//           toastMessage("没相机权限，请到应用程序权限管理开启权限");
//            //跳转至app设置
//          //  getAppDetailSettingIntent();
//            return;
//        }
        //isLogin();
    }

    private void initFragment() {
        initFragmentTab();
        FragmentTransaction begin = fragmentManager.beginTransaction();
        begin.add(R.id.fragment_container, fragments[HOME_FRAGMENT_INDEX], HOME_FRAGMENT_INDEX + "")
                .add(R.id.fragment_container, fragments[STORE_PRICE_INDEX], "" + STORE_PRICE_INDEX)
                .hide(fragments[STORE_PRICE_INDEX])
                .show(fragments[HOME_FRAGMENT_INDEX]).commit();
    }

    /**
     * 判断登录态
     */
    private void isLogin() {
        if (MyApplication.getUid() == null) {
            //表示用户从没有登录
            startActivity_leftToRight(this, LoginActivity.class);
        } else if (MyApplication.user_json == null) {
            //打开侧滑
            //标识用户以前登录过
            getUserInfo("cehua", null);
        } else {
            //当前处于登录态
            initFragment();

        }
    }


    private void initFragmentTab() {
        HomeFragment homeFragment = new HomeFragment();
        StorePriceFragment storePriceFragment = new StorePriceFragment();
        MyOrderFragment myOrderFragment = new MyOrderFragment();
        RechargeCenterFragment rechargeCenterFragment = new RechargeCenterFragment();
        fragments = new Fragment[]{homeFragment, storePriceFragment, myOrderFragment, rechargeCenterFragment};
    }

    private void removeAllFragment() {
        FragmentTransaction begin = fragmentManager.beginTransaction();
        for (int i = 0; i < FRAGMENT_COUNT; i++) {
            Fragment f = fragmentManager.findFragmentByTag(i + "");
            if (f != null && f.isAdded()) {
                begin.remove(f);
            }
        }
        begin.commit();
    }

    public void setGoneTop(boolean vis) {
        //隐藏或者显示头部
        if (vis) {
            relayout_top.setVisibility(View.VISIBLE);
        } else {
            relayout_top.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_tab1:
                index = HOME_FRAGMENT_INDEX;
                changeTab();
                break;
            case R.id.layout_tab2:
                index = STORE_PRICE_INDEX;
                changeTab();
                break;
            case R.id.layout_tab3:
                index = MYORDER_INDEX;
                if (MyApplication.getUid() == null) {
                    //表示用户从没有登录
                    startActivity_leftToRight(this, LoginActivity.class);
                } else if (MyApplication.user_json == null) {
                    //打开侧滑
                    //标识用户以前登录过
                    getUserInfo2("cehua", null);
                } else {
                    changeTab();
                }
                break;
            case R.id.layout_tab4:
                index = RECHARGE_INDEX;
                if (MyApplication.getUid() == null) {
                    //表示用户从没有登录
                    startActivity_leftToRight(this, LoginActivity.class);
                } else if (MyApplication.user_json == null) {
                    //打开侧滑
                    //标识用户以前登录过
                    getUserInfo2("cehua", null);
                } else {
                    changeTab();
                }
                break;
            case R.id.relayout_smartHome:
                startActivity(this, SmartHomeActivity.class);
                break;
            case R.id.relayout_laundy:
//                startActivity(this, LaundryActivity.class);
                startActivity(this, MyOrderActivity.class);
                break;
            case R.id.relayout_selfCenter:
                startActivity(this, SelfCenterActivity.class);
                break;
            case R.id.relayout_myWallet:
                startActivity(this, MyWalletActivity.class);
                break;
            case R.id.relayout_myOrder:
                startActivity(this, MyOrderActivity.class);
                break;
            case R.id.relayout_feedback:
                startActivity(this, FeedBackActivity.class);
                break;
            case R.id.main_imgUser:
                if (MyApplication.getUid() == null) {
                    //表示用户从没有登录
                    startActivity_leftToRight(this, LoginActivity.class);
//                    drawerLayout.closeDrawer(layout_menu);
                } else if (MyApplication.user_json == null) {
                    //打开侧滑
                    //标识用户以前登录过
                    getUserInfo("cehua", null);
                } else {
                    //当前处于登录态
                    getDoorLockList();
                    setUserDate();
                    quary_time();
                    quary_money();
                    drawerLayout.openDrawer(layout_menu);
                    getRepleyList();
                    // getBrand();
                }
                break;
            case R.id.layout_saoma:
                //扫码

                break;
            case R.id.view_zhezhao2:
                if (popupWindow_location != null) {
                    popupWindow_location.dismiss();
                }
                break;
            case R.id.relayout_laundyCard:

                if (getAgrement_laundry == null) {
                    agreement_onclick = new Agreement_Dialog.Onclick() {
                        @Override
                        public void OnClickLisener(View v) {
                            //  洗衣卷
                            Bundle bundle = new Bundle();
                            bundle.putString("type", "xieyid");
                            bundle.putString("title", "洗衣点记录");
                            bundle.putString("xieyid", "xieyid");
                            startActivity_Bundle(Main_NewActivity.this, MoneyDetailActivity.class, bundle);
                        }
                    };
                    getAgrement_laundry = new GetAgrement(this, agreement_onclick);
                }
                getAgrement_laundry.getAggrement_Detail(null, "Vouchers", "我知道了", agreement_onclick);

                break;
            case R.id.img_message:
                //消息
                if (MyApplication.getUid() == null) {
                    startActivity(this, LoginActivity.class);
                } else {
                    startActivity(this, MessageActivity.class);
                }
                break;
            case R.id.main_tvAbout:
                //关于我们
                if (getAgrement == null) {
                    getAgrement = new GetAgrement(this);
                }
                getAgrement.getAggrement("company", "我知道了");
                //     getAgrement.popIsNull_sigle("","关于我们","我知道了");
                break;
            case R.id.tv_setting:
                //设置
                startActivity(this, SetingActivity.class);
                break;
            case R.id.tv_share:
                showShareDialog();
                break;
            case R.id.img_service:
                //拨打客服，动态获取拨打电话权限

                try {
                    show_callPop(MyApplication.getConfigrationJson().getJSONObject("sysset").getString("web_tel").split(",")[0]);

                } catch (JSONException e) {
                    e.printStackTrace();
                    toastMessage("客服不在线");
                }
                break;
            case R.id.img_update:
                //重新定位，并获取衣柜列表
//                MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.0f);
//                mBaiduMap.setMapStatus(msu);
//                MyLocationData locData = new MyLocationData.Builder()
//                        .accuracy(0)
//                        // 此处设置开发者获取到的方向信息，顺时针0-360
//                        .direction(0).latitude(my_lat.latitude)
//                        .longitude(my_lat.longitude).build();
//                // 设置定位数据
//                mBaiduMap.setMyLocationData(locData);

                //设定中心点坐标
                //定义地图状态
                if (firstUpdate) {
                    update(my_lat);
                    firstUpdate = false;
                } else {
                    if (!StringUtils.isEmpty(aCache.getAsString("click_update"))) {
                        toastMessage("操作太频繁了");
                    } else {
                        update(my_lat);//回到你当前定位位置
                    }
                }
                break;
//            case R.id.layout_tab1:
//                //钱包
//                isLogin(MyWalletActivity.class);
//                break;
//            case R.id.layout_tab2:
//                //计费
//                if (common_brandRule == null) {
//
//                    common_brandRule = new Common_brandRule(this, drawerLayout,true,view_zhezhao);
//                }
//                common_brandRule.showRulePop();
////                if (getAgrement_price == null) {
////                    getAgrement_price = new GetAgrement(this);
////                }
////                getAgrement_price.getAggrement_Detail(null, "PricingRules", "我知道了");
//                break;
//            case R.id.layout_tab3:
//                //app下载
//                if (getAgrement_down == null) {
//                    getAgrement_down = new GetAgrement(this);
//                }
//                getAgrement_down.getAggrement_Detail(null, "AppDownload", "我知道了");
//                break;
//            case R.id.layout_tab4:
//                //订单
//                isLogin(MyOrderActivity.class);
//                break;
            case R.id.layout_tab5:
                //个人
                isLogin(SelfCenterActivity.class);

                break;
            case R.id.layout_credit:
                //信用值
                try {
                    if (getAgrement_crdit == null) {
                        getAgrement_crdit = new GetAgrement(this);
                    }
                    getAgrement_crdit.getAggrement_Detail(null, "CreditRule", "我知道了");
                } catch (Exception e) {

                }
                break;
            case R.id.relayout_likeBrand:
                //选择喜欢的品牌
                if (list_brand_like == null) {
                    list_brand_like = new ArrayList<>();
                }
                if (columnUtils == null) {
                    columnUtils = new ColumnUtils(this, new ColumnUtils.Result() {
                        @Override
                        public void onSuccess(String title, JSONArray jsonArray) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    list_brand_like.add(jsonArray.getJSONObject(i));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            brand_likePop();
                        }

                        @Override
                        public void onFailure(String msg) {

                            toastMessage("网络异常，请检查网络后重试1");
                        }
                    });
                }

                if (list_brand_like.size() == 0) {
                    columnUtils.getColumn("", "Brand");
                } else {
                    brand_likePop();
                }
                break;
            case R.id.layout_combo:
                //点击套餐
                Bundle bundle = new Bundle();
                bundle.putString("type", "package");
                bundle.putString("title", "购买套餐");
                startActivity_Bundle(Main_NewActivity.this, MoneyDetailActivity.class, bundle);
                break;
            case R.id.tv_wechat:
                shareDialog.dismiss();
                if (StringUtils.isWeixinAvilible(this)) {
                    WXWebpageObject webObject = new WXWebpageObject();
                    webObject.webpageUrl = "" +
                            "http://down.scyoue.com/download/";
                    final WXMediaMessage msg = new WXMediaMessage(webObject);
                    try {
                        msg.title = MyApplication.getConfigrationJson().getJSONObject("share").getJSONObject("weixin").getString("name");
                        msg.description = MyApplication.getConfigrationJson().getJSONObject("share").getJSONObject("weixin").getString("describe");
                        Picasso.with(this).load(MyApplication.getConfigrationJson().getJSONObject("share").getJSONObject("weixin").getString("logo")).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                                msg.setThumbImage(bitmap);
//                                SendMessageToWX.Req req = new SendMessageToWX.Req();
//                                req.transaction = StringUtils.buildTransaction("webpage");
//                                req.message = msg;
//                                req.scene = SendMessageToWX.Req.WXSceneTimeline;
//                                api.sendReq(req);
                                msg.setThumbImage(bitmap);
                                SendMessageToWX.Req req = new SendMessageToWX.Req();
                                req.transaction = StringUtils.buildTransaction("webpage");
                                req.message = msg;
                                api.sendReq(req);
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });


                    } catch (Exception e) {

                    }


//                    Bitmap thumb  = BitmapFactory.decodeResource(getResources(),R.mipmap.app_icons);
//                    msg.thumbData = utils.Util.bmpToByteArray(thumb,true);

                } else {
                    toastMessage("您尚未安装微信客户端，无法进行微信分享");
                }
                break;
            case R.id.tv_friends:
                shareDialog.dismiss();
//                if (StringUtils.isWeixinAvilible(this)) {
//                    WXWebpageObject webObject = new WXWebpageObject();
//                    webObject.webpageUrl = "" +
//                            "http://down.scyoue.com/download/";
//                    WXMediaMessage msg = new WXMediaMessage(webObject);
//                    msg.title = "优E管家";
//                    msg.description = "优E管家下载";
////                    Bitmap thumb  = BitmapFactory.decodeResource(getResources(),R.mipmap.app_icons);
////                    msg.thumbData = utils.Util.bmpToByteArray(thumb,true);
//                    SendMessageToWX.Req req = new SendMessageToWX.Req();
//                    req.transaction = MyApplication.getUid()+""+ System.currentTimeMillis();
//                    req.transaction = StringUtils.buildTransaction("webpage");
//                    req.message = msg;
//                    req.scene = SendMessageToWX.Req.WXSceneTimeline;
//                    api.sendReq(req);
//                } else {
//                    toastMessage("您尚未安装微信客户端，无法进行微信分享");
//                }
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = "http://down.scyoue.com/download/";
                final WXMediaMessage msg = new WXMediaMessage(webpage);
                try {
                    msg.title = MyApplication.getConfigrationJson().getJSONObject("share").getJSONObject("weixin").getString("describe");
                    msg.description = MyApplication.getConfigrationJson().getJSONObject("share").getJSONObject("weixin").getString("describe");
                    Picasso.with(this).load(MyApplication.getConfigrationJson().getJSONObject("share").getJSONObject("weixin").getString("logo")).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            msg.setThumbImage(bitmap);
                            SendMessageToWX.Req req = new SendMessageToWX.Req();
                            req.transaction = StringUtils.buildTransaction("webpage");
                            req.message = msg;
                            req.scene = SendMessageToWX.Req.WXSceneTimeline;
                            api.sendReq(req);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });


                } catch (Exception e) {

                }

                break;
            case R.id.tv_qq:
                shareDialog.dismiss();
                if (mTencent == null) {
                    mTencent = Tencent.createInstance("1106984447", getApplicationContext());
                }
                qqShare();
                break;
            case R.id.tv_cancel:
                shareDialog.dismiss();
                break;
        }

    }

    /**
     * 判断用户登录态
     */
    public void isLoginOther(Class baseActivity) {
        //扫码
        if (MyApplication.getUid() == null) {
            //表示用户从没有登录
            startActivity_leftToRight(this, LoginActivity.class);
        } else if (MyApplication.user_json == null) {
            //可以打开扫描
            //标识用户以前登录过，
            getUserInfo("activity", baseActivity);
        } else {
            startActivity(this, baseActivity);
        }
    }

    public void saoma() {
        if (MyApplication.getUid() == null) {
            //表示用户从没有登录
            startActivity_leftToRight(this, LoginActivity.class);
        } else if (MyApplication.user_json == null) {
            //可以打开扫描
            //标识用户以前登录过，
            getUserInfo("saoma", null);
        } else {
            //当前处于登录态
//                    getDoorLockList();
//                    setUserDate();
//                    drawerLayout.openDrawer(layout_menu);
//                    getRepleyList();
            startQrCode();
        }
    }


    public void isLogin2() {
        if (MyApplication.getUid() == null) {
            //表示用户从没有登录
            startActivity_leftToRight(this, LoginActivity.class);
        } else if (MyApplication.user_json == null) {
            //打开侧滑
            //标识用户以前登录过
            getUserInfo("cehua", null);
        } else {
            //当前处于登录态
            getDoorLockList();
            setUserDate();
            drawerLayout.openDrawer(layout_menu);
            getRepleyList();
        }
    }

    /**
     * 底部栏颜色转换
     */
    private void changeTab() {
        if (currentIndex != index) {
            tv_tabs[index].setTextColor(ContextCompat.getColor(this, R.color.color_yellow));
            tv_tabs[currentIndex].setTextColor(ContextCompat.getColor(this, R.color.color_f5));
            img_tabs[index].setImageDrawable(ContextCompat.getDrawable(this, img_resourse[index * 2 + 1]));
            img_tabs[currentIndex].setImageDrawable(ContextCompat.getDrawable(this, img_resourse[currentIndex * 2]));
        }
        if (currentIndex != index) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (!fragments[currentIndex].isHidden()) {
                transaction.hide(fragments[currentIndex]);
            }
            if (!fragments[index].isAdded()) {
                transaction.add(R.id.fragment_container, fragments[index], "" + index);
            }
            transaction.show(fragments[index]).commit();
        }
        currentIndex = index;
    }

    public void setPage2(String typeId) {
        this.typeId = typeId;
        index = STORE_PRICE_INDEX;
        changeTab();
    }

    public void setPage2() {
        index = STORE_PRICE_INDEX;
        changeTab();
    }

    public void setPage3() {
        index = MYORDER_INDEX;
        if (MyApplication.getUid() == null) {
            //表示用户从没有登录
            startActivity_leftToRight(this, LoginActivity.class);
        } else if (MyApplication.user_json == null) {
            //打开侧滑
            //标识用户以前登录过
            getUserInfo2("cehua", null);
        } else {
            changeTab();
        }
    }

    public void setPage4() {
        index = RECHARGE_INDEX;
        if (MyApplication.getUid() == null) {
            //表示用户从没有登录
            startActivity_leftToRight(this, LoginActivity.class);
        } else if (MyApplication.user_json == null) {
            //打开侧滑
            //标识用户以前登录过
            getUserInfo2("cehua", null);
        } else {
            changeTab();
        }
    }
//    /**
//     * 获取用户基本信息
//     */
//    private void getUserInfo(final String type, final Class activity) {
//        showLoadingView();
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("appid", RequestTag.APPID);
//        params.put("key", RequestTag.KEY);
//        String timestamp = System.currentTimeMillis() + "";
//        params.put("time", timestamp + "");
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("device_id", MyApplication.getDeviceId());
//            jsonObject.put("uid", MyApplication.getUid());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        params.put("data", jsonObject.toString());
//        String sign = Md5.md5(jsonObject.toString(), timestamp);
//        params.put("sign", sign);
//        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.USERINFO, params, new JsonResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, JSONObject response) {
//                try {
//                    Log.d("userResult", response.toString());
//                    if (response.getInt("status") != 0) {
//                        toastMessage(response.getString("msg"));
//                    } else {
//                        MyApplication.user_json = response.getJSONObject("data");
//                        MyApplication.brand = response.getJSONObject("data").getInt("brand");
//                        SPUtils.setSharedIntData(MyApplication.getAppContext(), "brand", response.getJSONObject("data").getInt("brand"));
//                        SPUtils.setSharedStringData(MyApplication.getAppContext(), "user_json", response.getJSONObject("data").toString());
//                        initFragment();
////                        if (type.equals("saoma")) {
////                            //进入扫码
////                            startQrCode();
////                        } else if (type.equals("activity")) {
////                            //进入其它界面
////                            startActivity(Main_NewActivity.this, activity);
////                        } else {
////                            //进入侧滑
////                            setUserDate();
////                            getDoorLockList();
////                            drawerLayout.openDrawer(layout_menu);
////                            getRepleyList();
////                        }
//
//                    }
//                } catch (Exception e) {
//                    toastMessage("数据解析异常，请稍后重试");
//                    dismissLoadingView();
//                }
//                if (type.equals("saoma") || type.equals("activity")) {
//                    //进入扫码
//                    dismissLoadingView();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, String error_msg) {
//                dismissLoadingView();
//
//            }
//        });
//    }

    private void initView2() {
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);
        //将应用的appid注册到微信
        api.registerApp(Constants.APP_ID);
        aCache = ACache.get(this);
        layout_credit = (LinearLayout) findViewById(R.id.layout_credit);
        menu_tvOrderCount = (MyTextView) findViewById(R.id.menu_tvOrderCount);
        menu_tvOrderObligation = (MyTextView) findViewById(R.id.menu_tvOrderObligation);
        home_imgUser = (ImageView) findViewById(R.id.home_imgUser);
        view_zhezhao_menu = findViewById(R.id.view_zhezhao_menu);
        img_brand = (ImageView) findViewById(R.id.img_brand);
        tv_chooseBrand = (TextView) findViewById(R.id.tv_chooseBrand);
        line2 = (ImageView) findViewById(R.id.line2);
        home_tvUserName = (TextView) findViewById(R.id.home_tvUserName);
        findViewById(R.id.main_tvAbout).setOnClickListener(this);
        layout_credit.setOnClickListener(this);
        findViewById(R.id.tv_setting).setOnClickListener(this);
//        findViewById(R.id.img_service).setOnClickListener(this);//客服电话
        //   img_update = (RelativeLayout) findViewById(R.id.img_update);//点击重新读取定位同时读取衣柜列表，在图上把自己的位置设置为中心点，点击频率：30秒可点击一次，在频繁限制时间内给提示：操作太频繁了（黑底白字提示）
        tv_userType = (TextView) findViewById(R.id.tv_userType);
        home_tvCredit = (TextView) findViewById(R.id.home_tvCredit);
        home_tvBalance = (TextView) findViewById(R.id.home_tvBalance);
        home_tvClothes = (TextView) findViewById(R.id.home_tvClothes);
        mMapView = (MapView) findViewById(R.id.map_view);
        menu_smartCount = (TextView) findViewById(R.id.menu_smartCount);
        menu_tvClosetCount = (TextView) findViewById(R.id.menu_tvClosetCount);
        layout_orderList = (LinearLayout) findViewById(R.id.layout_orderList);
        view_zhezhao = findViewById(R.id.view_zhezhao2);
//        view_zhezhao = new View(this);
        view_zhezhao.setOnClickListener(this);
        // img_update.setOnClickListener(this);
        // findViewById(R.id.img_message).setOnClickListener(this);
//        findViewById(R.id.layout_saoma).setOnClickListener(this);
        layout_menu = (RelativeLayout) findViewById(R.id.layout_menu);
        main_imgUser = (RelativeLayout) findViewById(R.id.main_imgUser);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        layout_tabViews = new LinearLayout[layout_abId.length];
        for (int i = 0; i < layout_abId.length; i++) {
            layout_tabViews[i] = (LinearLayout) findViewById(layout_abId[i]);
            layout_tabViews[i].setOnClickListener(this);
        }
        for (int i = 0; i < id.length; i++) {
            findViewById(id[i]).setOnClickListener(this);
        }
        layout_menu.getLayoutParams().width = DisplayUtil.getScreenWidth(this) / 10 * 9;
        main_imgUser.setOnClickListener(this);
        //Picasso.with(this).load(R.mipmap.img_user).error(R.mipmap.img_user).transform(new CircleTransform()).into(home_imgUser);
        setBaidu();
        if (!TimeUtils.isOPen(this)) {
            show_gps();

        }
        isFristLocation = true;
//        drawerLayout.closeDrawer();
        //注册监听函数
        initMap();
        SocketInit();
        if (OrderDetailActivity.handler != null) {
            OrderDetailActivity.handler.sendEmptyMessage(2);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                showDialogTipUserRequestPermission();
            }
        }
        // setBrand();
        // setDrawerRightEdgeSize(drawerLayout,this,0.1f);
    }

    /**
     * 长连接相关
     */
    private void SocketInit() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mReciver = new MessageBackReciver();
        mServiceIntent = new Intent(this, BackService.class);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BackService.HEART_BEAT_ACTION);
        mIntentFilter.addAction(BackService.MESSAGE_ACTION);
    }

    private void initMap() {
        //获取地图控件引用
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.0f);
        mBaiduMap.setMapStatus(msu);
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setOnMarkerClickListener(new MyMarkerClickListener());
        //空白地图, 基础地图瓦片将不会被渲染。在地图类型中设置为NONE，将不会使用流量下载基础地图瓦片图层。使用场景：与瓦片图层一起使用，节省流量，提升自定义瓦片图下载速度。
        //mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
        //开启交通图
        // mBaiduMap.setTrafficEnabled(true);
        //开启热力图
        // mBaiduMap.setBaiduHeatMapEnabled(true);

//定位
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        //配置定位SDK参数
        initLocation();
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        //开启定位
        mLocationClient.start();
        //图片点击事件，回到定位点
        mLocationClient.requestLocation();
        mBaiduMap.setOnMapStatusChangeListener(this);
    }

    //配置定位SDK参数
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 10000;//十秒请求一次
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation
        // .getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);
        option.setOpenGps(true); // 打开gps

        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    //实现BDLocationListener接口,BDLocationListener为结果监听接口，异步获取定位结果
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (my_lat != null) {
                Log.d("weizhiwz", my_lat.latitude + "," + my_lat.longitude);
                my_lat = new LatLng(location.getLatitude(), location.getLongitude());

            }
            if (isFristLocation) {
                my_lat = new LatLng(location.getLatitude(), location.getLongitude());
                if (move_lat == null) {
                    move_lat = my_lat;
                }
                // 构造定位数据
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(0)
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(0).latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();
                // 设置定位数据
                mBaiduMap.setMyLocationData(locData);
                mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.NORMAL, true, BitmapDescriptorFactory.fromResource(R.mipmap.location_icon2),
                        0xAAFFFF88, 0xAA00FF00));

                // 当不需要定位图层时关闭定位图层
                //mBaiduMap.setMyLocationEnabled(false);
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                sb.append(location.getTime());
                sb.append("\nerror code : ");
                sb.append(location.getLocType());
                sb.append("\nlatitude : ");
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");
                sb.append(location.getLongitude());
                sb.append("\nradius : ");
                sb.append(location.getRadius());
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 单位：公里每小时
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 单位：米
                    sb.append("\ndirection : ");
                    sb.append(location.getDirection());// 单位度
                    sb.append("\naddr : ");
                    sb.append(location.getAddrStr());
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");

                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    sb.append("\naddr : ");
                    sb.append(location.getAddrStr());
                    //运营商信息
                    sb.append("\noperationers : ");
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                sb.append("\nlocationdescribe : ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
                List<Poi> list = location.getPoiList();// POI数据
                if (list != null) {
                    sb.append("\npoilist size = : ");
                    sb.append(list.size());
                    for (Poi p : list) {
                        sb.append("\npoi= : ");
                        sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                    }
                }

                isFristLocation = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
                //定义Maker坐标点

                LatLng point = new LatLng(location.getLatitude(), location.getLongitude() + 0.02);
                //showLineMarker(location);
                getArkList(location, my_lat);
            }


        }

    }

    private void setBaidu() {
        // 隐藏百度的LOGO
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }

        // 不显示地图上比例尺
        mMapView.showScaleControl(false);

        // 不显示地图缩放控件（按钮控制栏）
        mMapView.showZoomControls(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        if (mSerch != null) {
            mSerch.destroy();
        }
        if (mNaviHelper != null) {
            mNaviHelper.quit();
        }
        if (!MyApplication.isRemeberPassword()) {
//            SPUtils.getSharedStringData(myApplication,"password")
            SPUtils.clear(MyApplication.getAppContext());

        }
        Log.d("destroyyy", "退出首页");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        Log.d("onResum", "true");
//        if(relayout_top!=null){
//            relayout_top.setVisibility(View.VISIBLE);
//        }
        if (MyApplication.isupdate) {
            setUserDate();
            MyApplication.isupdate = false;
        }
        mMapView.onResume();
        if (MyApplication.getUserJson() != null && aCache != null && aCache.getAsString("quary_money") == null) {
            quary_money();
        }
        if (drawerLayout != null && MyApplication.getUserJson() == null) {
            drawerLayout.closeDrawer(layout_menu);
        }
        if (!isFirst && !TimeUtils.isOPen(this)) {
            isFirst = false;
            Log.d("onResum", "true2");
            show_gps();
        }
        isFirst = false;
        if (MyApplication.getUserJson() != null && aCache != null && aCache.getAsString("doorlist") == null) {
            getDoorLockList();
        }
        if (MyApplication.isLogin) {
            //从登陆界面过来，重新设置当前值
            if (MyApplication.getUserJson() != null) {
                getDoorLockList();
                setUserDate();
                getRepleyList();
            }
        }
        if (mNaviHelper != null) {
            mNaviHelper.resume();
        }
        if (tv_Noread != null) {
            if (MyApplication.no_rendMessage < 1) {
                tv_Noread.setVisibility(View.GONE);
            } else {
                tv_Noread.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocalBroadcastManager.registerReceiver(mReciver, mIntentFilter);
        try {
            bindService(mServiceIntent, conn, BIND_AUTO_CREATE);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
        if (mNaviHelper != null) {
            mNaviHelper.pause();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
//        unbindService(conn);
//        mLocalBroadcastManager.unregisterReceiver(mReciver);
    }

    /**
     * 根据坐标点绘制Marker
     */
    private void showLineMarker(BDLocation location) {

        List<LatLng> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            LatLng point;
            if (i == 0) {
                point = new LatLng(location.getLatitude() + 0.0005 + i / 100f, location.getLongitude());
                setView(point);
            } else if (i == 1) {
                point = new LatLng(location.getLatitude(), location.getLongitude() + 0.005);
            } else {
                if (i < 5) {
                    point = new LatLng(location.getLatitude(), location.getLongitude() + 0.005);
                } else {
                    point = new LatLng(location.getLatitude() + 0.005, location.getLongitude());
                }
            }
            list.add(point);
        }
        //获取后台柜子坐标

        list_locationMark = list;
        //构建marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.location_picture);
        MarkerOptions option;
        for (int i = 0; i < list.size(); i++) {
            //构建MarkerOption，用于在地图上添加Marker
            option = new MarkerOptions().icon(bitmap).position(list.get(i));
            //生长动画
            option.animateType(MarkerOptions.MarkerAnimateType.grow);
            //在地图上添加Marker，并显示
            mBaiduMap.addOverlay(option);
            //设置Marker覆盖物的ZIndex
            option.zIndex(i);
        }
        getArkList(location, my_lat);
    }
    //创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量

    /**
     * 设置marke悬浮框
     */
    private void setView(LatLng pt) {
        //创建InfoWindow展示的view
        View view_marke = LayoutInflater.from(this).inflate(R.layout.layout_marker, null);
//定义用于显示该InfoWindow的坐标点
//        LatLng pt = new LatLng(39.86923, 116.397428);

//创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
        InfoWindow mInfoWindow = new InfoWindow(view_marke, pt, (int) -(getResources().getDimension(R.dimen.x62)));

//显示InfoWindow
        mBaiduMap.showInfoWindow(mInfoWindow);
    }

    /**
     * 判断用户登录态
     */
    private void isLogin(Class baseActivity) {
        //扫码
        if (MyApplication.getUid() == null) {
            //表示用户从没有登录
            startActivity_leftToRight(this, LoginActivity.class);
        } else if (MyApplication.user_json == null) {
            //可以打开扫描
            //标识用户以前登录过，
            getUserInfo("activity", baseActivity);
        } else {
            startActivity(this, baseActivity);
        }
    }

    private void update(LatLng latLng) {
        showLoadingView();
        isMove = false;
        MapStatus mMapStatus = new MapStatus.Builder()
                //要移动的点
                .target(latLng)
                //放大地图到20倍
                .zoom(16)
                .build();

        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.clear();
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        BDLocation bdLocation = new BDLocation();
        getArkList(null, latLng);
        aCache.put("click_update", "click", ACache.TIME_MILL * 30);

    }

    private void update2(LatLng latLng) {
        showLoadingView();
        isMove = true;
        MapStatus mMapStatus = new MapStatus.Builder()
                //要移动的点
                .target(latLng)
                .build();

        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.clear();
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        BDLocation bdLocation = new BDLocation();
        getArkList(null, latLng);
        aCache.put("click_update", "click", ACache.TIME_MILL * 30);

    }

    /**
     * 设置用户相关数据
     */
    private void setUserDate() {
        try {

            String pic = MyApplication.getUserJson().getString("head");
            if (pic.contains("http://") || pic.contains("https://")) {
                pic = MyApplication.getUserJson().getString("head");
            } else {
                pic = RequestTag.BaseImageUrl + MyApplication.getUserJson().getString("head");
            }
            Picasso.with(this).load(pic).placeholder(R.mipmap.default_iv).error(R.mipmap.default_iv).transform(new CircleTransform()).into(home_imgUser, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    try {
                        Picasso.with(Main_NewActivity.this).load(RequestTag.BaseImageUrl + MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("head").get(MyApplication.getUserJson().getInt("sex"))).placeholder(R.mipmap.default_iv).error(R.mipmap.default_iv).transform(new CircleTransform()).into(home_imgUser);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            if (StringUtils.isEmpty(MyApplication.getUserJson().getString("name"))) {
                home_tvUserName.setText(MyApplication.getUserJson().getString("name"));
            } else {
                if (StringUtils.isEmpty(MyApplication.getUserJson().getString("mobile"))) {
                    home_tvUserName.setText(MyApplication.getUserJson().getString("nickname"));
                } else {
                    String s = MyApplication.getUserJson().getString("mobile");
                    home_tvUserName.setText(MyApplication.getUserJson().getString("nickname") + " • " + s.substring(0, 3) + "***" + s.substring(s.length() - 2, s.length()));
                }
            }
            tv_userType.setText(MyApplication.getUserJson().getString("level"));
            home_tvBalance.setText(MoneyUtil.m2(MyApplication.getUserJson().getDouble("money") / 1000));
            home_tvClothes.setText(MoneyUtil.m2(MyApplication.getUserJson().getDouble("clothes") / 1000));
            home_tvCredit.setText(MyApplication.getUserJson().getLong("credit") + "");
        } catch (Exception e) {

        }
    }

    /**
     * 获取用户基本信息
     */
    private void getUserInfo(final String type, final Class activity) {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.USERINFO, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    Log.d("userResult", response.toString());
                    if (response.getInt("status") != 0) {
                        toastMessage(response.getString("msg"));
                    } else {
                        MyApplication.user_json = response.getJSONObject("data");
                        MyApplication.brand = response.getJSONObject("data").getInt("brand");
                        SPUtils.setSharedIntData(MyApplication.getAppContext(), "brand", response.getJSONObject("data").getInt("brand"));
                        SPUtils.setSharedStringData(MyApplication.getAppContext(), "user_json", response.getJSONObject("data").toString());
                        setAlias(true);
                        if (type.equals("saoma")) {
                            //进入扫码
                            startQrCode();
                        } else if (type.equals("activity")) {
                            //进入其它界面
                            startActivity(Main_NewActivity.this, activity);
                        } else {
                            //进入侧滑
                            setUserDate();
                            quary_time();
                            quary_money();
                            getDoorLockList();
                            drawerLayout.openDrawer(layout_menu);
                            getRepleyList();
                        }

                    }
                } catch (Exception e) {
                    toastMessage("数据解析异常，请稍后重试");
                    dismissLoadingView();
                }
                if (type.equals("saoma") || type.equals("activity")) {
                    //进入扫码
                    dismissLoadingView();
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dismissLoadingView();
            }
        });
    }

    /**
     * 获取用户基本信息
     */
    public void getUserInfo2(final String type, final Class activity) {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.USERINFO, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    Log.d("userResult", response.toString());
                    if (response.getInt("status") != 0) {
                        toastMessage(response.getString("msg"));
                    } else {
                        MyApplication.user_json = response.getJSONObject("data");
                        MyApplication.brand = response.getJSONObject("data").getInt("brand");
                        SPUtils.setSharedIntData(MyApplication.getAppContext(), "brand", response.getJSONObject("data").getInt("brand"));
                        SPUtils.setSharedStringData(MyApplication.getAppContext(), "user_json", response.getJSONObject("data").toString());
                        if (type.equals("saoma")) {
                            //进入扫码
                            startQrCode();
                        } else if (type.equals("activity")) {
                            //进入其它界面
                            startActivity(Main_NewActivity.this, activity);
                        } else {
                            //进入侧滑
                            setUserDate();
                            getRepleyList();
                            changeTab();
                        }

                    }
                } catch (Exception e) {
                    toastMessage("数据解析异常，请稍后重试");
                    dismissLoadingView();
                }
                if (type.equals("saoma") || type.equals("activity")) {
                    //进入扫码
                    dismissLoadingView();
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dismissLoadingView();

            }
        });
    }

    /**
     * 获取用户基本信息
     */
    private void getUserInfo() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid", MyApplication.getUid());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.USERINFO, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    Log.d("ingogog", response.toString());
                    if (response.getInt("status") != 0) {
                        toastMessage(response.getString("msg"));
                    } else {
                        SPUtils.setSharedStringData(MyApplication.getAppContext(), "user_json", response.getJSONObject("data").toString());
                        MyApplication.user_json = response.getJSONObject("data");

                    }

                } catch (Exception e) {
                    toastMessage("数据解析异常，请稍后重试");
                    Log.d("info", e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {

            }
        });
    }


    /**
     * 获取用户喜欢的品牌
     */
    private void getBrand() {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.likeBrand, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    Log.d("bsbsbsb", response.getString("msg"));
                    if (response.getInt("status") != 0) {
                        toastMessage(response.getString("msg"));
                        tv_chooseBrand.setVisibility(View.VISIBLE);
                        img_brand.setVisibility(View.GONE);
                    } else {
                        MyApplication.brand = response.getJSONObject("data").getInt("id");
                        SPUtils.setSharedIntData(MyApplication.getAppContext(), "brand", response.getJSONObject("data").getInt("id"));
                        MyApplication.brand_pics = response.getJSONObject("data").getString("pic");
                        SPUtils.setSharedStringData(MyApplication.getAppContext(), "brand_pics" + MyApplication.brand, response.getJSONObject("data").getString("pic"));
                        if (MyApplication.getBrand_pics() != null) {
                            Picasso.with(Main_NewActivity.this).load(RequestTag.BaseImageUrl + MyApplication.getBrand_pics()).error(R.mipmap.default_iv).placeholder(R.mipmap.default_iv).into(img_brand);
                        }
                    }
                } catch (Exception e) {
                    toastMessage("数据解析异常，请稍后重试");

                }
                dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dismissLoadingView();
                toastMessage("网络异常，请检查网络后重试2");
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (popupWindow_down != null && popupWindow_down.isShowing()) {
            if (number == 0) {
                Toast.makeText(Main_NewActivity.this, "正在更新，请等待,再次点击关闭", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            number = 0;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
            if (number == 1) {
                if (popupWindow_down != null) {
                    popupWindow_down.dismiss();
                }
            } else {
                number++;
            }
        } else {
            if (number == 0) {
                Toast.makeText(Main_NewActivity.this, "再次点击退出小e管家", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            number = 0;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
            if (number == 1) {
                super.onBackPressed();
            } else {
                number++;
            }
        }

    }

    // 开始扫码
    private void startQrCode() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(Main_NewActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.REQ_PERM_CAMERA);
            return;
        }
        // 二维码扫码
        Intent intent = new Intent(Main_NewActivity.this, CaptureActivity.class);
        startActivityForResult(intent, Constant.REQ_QR_CODE);
    }

    // 开始拨打电话
    private void startCallPhone(String phone) {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(Main_NewActivity.this,
                    Manifest.permission.CALL_PHONE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Main_NewActivity.this, new String[]{
                        Manifest.permission.CALL_PHONE
                }, Constant.REQUEST_CODE_ASK_CALL_PHONE);
                return;
            } else {
                call(phone);
            }
        } else {
            call(phone);
        }

    }

    private void call(String phone) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.CALL");
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调
        if (requestCode == Constant.REQ_QR_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);
            if (!scanResult.contains("client=userQrCan") && !scanResult.contains("client=userPaycard")) {
                toastMessage("扫描有误，请重试");
            } else if (scanResult.contains("client=userPaycard")) {
                //充值
                try {
                    recharge(new JSONObject(bundle.getString("jsonObject")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("asdasda", bundle.getString("jsonObject"));
                showPopwindow_boxType(bundle.getBoolean("is_put"), bundle.getBoolean("is_additional"), bundle.getString("jsonObject"));
            }
            //将扫描出的信息显示出来
            //tvResult.setText(scanResult);

        }
        if (requestCode == 123) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 检查该权限是否已经获取
                int i = ContextCompat.checkSelfPermission(this, permissions[0]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (i != PackageManager.PERMISSION_GRANTED) {
                    // 提示用户应该去应用设置界面手动开启权限
                    showDialogTipUserGoToAppSettting();
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
        //QQ分享
        Tencent.onActivityResultData(requestCode, resultCode, data, mIUiListener);
        if (requestCode == com.tencent.connect.common.Constants.REQUEST_API) {
            if (resultCode == com.tencent.connect.common.Constants.REQUEST_QQ_SHARE || resultCode == com.tencent.connect.common.Constants.REQUEST_QZONE_SHARE || resultCode == com.tencent.connect.common.Constants.REQUEST_OLD_SHARE) {
                Tencent.handleResultData(data, mIUiListener);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.REQ_PERM_CAMERA:
                // 摄像头权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    startQrCode();
                } else {
                    // 被禁止授权
                    Toast.makeText(Main_NewActivity.this, "请至权限中心打开本应用的相机访问权限", Toast.LENGTH_LONG).show();
                }
                break;
            case 321:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                        boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                        if (!b) {
                            // 用户还是想用我的 APP 的
                            // 提示用户去应用设置界面手动开启权限
                            showDialogTipUserGoToAppSettting();
                        } else
                            finish();
                    } else {
                        Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case Constant.REQUEST_CODE_ASK_CALL_PHONE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted callDirectly(mobile);
                    try {
                        call(MyApplication.getConfigrationJson().getJSONObject("sysset").getString("web_tel").split(",")[0]);
                    } catch (Exception e) {

                    }
                } else {
                    Toast.makeText(Main_NewActivity.this, "请至权限中心打开本应用的电话拨打访问权限", Toast.LENGTH_LONG).show();
                }
                break;
            case PERMS_REQUEST_CODE:
                boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (storageAccepted) {
                    check_update("1.3.6");
                } else {
                    toastMessage("请打开相关权限，不然部分功能更能无法正常使用。");
                }
                break;
        }
    }

    private void showLocationPop() {
        View popView = View.inflate(this, R.layout.layout_main_location_pop, null);
        int width = getResources().getDisplayMetrics().widthPixels / 10 * 8;
        int height = (int) (getResources().getDisplayMetrics().heightPixels / 3) + 40;
        if (popupWindow_location == null) {
            popupWindow_location = new PopupWindow(popView, width, height);
            popupWindow_location.setAnimationStyle(R.style.popwin_anim_style);
            popupWindow_location.setFocusable(true);
            popupWindow_location.setOutsideTouchable(false);// 设置同意在外点击消失
            ColorDrawable dw = new ColorDrawable(0x30000000);
            popupWindow_location.setBackgroundDrawable(dw);
            layout_car = (LinearLayout) popView.findViewById(R.id.layout_car);
            layout_walk = (LinearLayout) popView.findViewById(R.id.layout_walk);
            tv_locationStart = (TextView) popView.findViewById(R.id.tv_locationStart);
            img_car = (ImageView) popView.findViewById(R.id.img_car);
            img_walk = (ImageView) popView.findViewById(R.id.img_walk);
            location_imgType = (ImageView) popView.findViewById(R.id.location_imgType);
            location_tvType = (TextView) popView.findViewById(R.id.location_tvType);
            tv_car = (TextView) popView.findViewById(R.id.tv_car);
            tv_walk = (TextView) popView.findViewById(R.id.tv_walk);
            tv_distance = (TextView) popView.findViewById(R.id.tv_distance);
            tv_time = (TextView) popView.findViewById(R.id.tv_time);
            View.OnClickListener onClickListenernew = new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.tv_locationStart:
                            //开始导航
                            //initWalk();
                            if (drive_type == 2) {
                                startDriving();
                            } else {
                                if (currentDistance > 20000f) {
                                    toastMessage("距离太远，不支持步行");
                                } else {
                                    initWalk();
                                }
                            }
                            break;
                        case R.id.layout_car:
                            //驾车
                            drive_type = 2;
                            setBackGround(layout_car, layout_walk, tv_car, tv_walk, img_car, img_walk, 1);
                            break;
                        case R.id.layout_walk:
                            //步行
                            drive_type = 1;
                            setBackGround(layout_walk, layout_car, tv_walk, tv_car, img_walk, img_car, 2);
                            break;
                    }
                }
            };
            if (currentDistance > 1000) {
                tv_distance.setText(distance + "KM");
            } else {
                tv_distance.setText(distance + "M");
            }
            tv_time.setText(time + "分钟");
            layout_car.setOnClickListener(onClickListenernew);
            layout_walk.setOnClickListener(onClickListenernew);
            tv_locationStart.setOnClickListener(onClickListenernew);

        }

        popupWindow_location.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                view_zhezhao.setVisibility(View.GONE);
//                MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.0f);
//                mBaiduMap.setMapStatus(msu);
                //普通地图
//                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
//                mBaiduMap.setMyLocationEnabled(true);
            }
        });
        view_zhezhao.setVisibility(View.VISIBLE);
        if (!popupWindow_location.isShowing()) {
            popupWindow_location.showAtLocation(drawerLayout, Gravity.BOTTOM, 0, getResources().getDisplayMetrics().widthPixels / 10);
        }
    }

    //覆盖物点击监听器
    public class MyMarkerClickListener implements BaiduMap.OnMarkerClickListener {

        @Override
        public boolean onMarkerClick(Marker marker) {
            if (end_lat != null && marker.getPosition() != null && marker.getPosition().latitude == end_lat.latitude && marker.getPosition().longitude == end_lat.longitude) {
                if (isGuihua) {
                    showLocationPop();
                } else {
                    end_lat = marker.getPosition();
                    showLoadingView();
                    aCache.put("time_guihua", "hava");//规划时间
                    walkPlanInit(marker.getPosition());
                }
            } else {
                end_lat = marker.getPosition();
                showLoadingView();
                aCache.put("time_guihua", "hava");//规划时间
                walkPlanInit(marker.getPosition());
            }
            return false;
        }
    }

    /**
     * 改变弧形效果，驾车，步行
     */
    private void setBackGround(LinearLayout layout1, LinearLayout layout2, TextView tv1, TextView tv2, ImageView img1, ImageView img2, int type) {
        layout1.setBackground(ContextCompat.getDrawable(this, R.drawable.home_rect_sloid_yellow3));
        layout2.setBackground(ContextCompat.getDrawable(this, R.drawable.home_rect_sloid_translate));
        tv1.setTextColor(ContextCompat.getColor(this, R.color.white));
        tv2.setTextColor(ContextCompat.getColor(this, R.color.fontcolor_f9));
        PlanNode stNode;
        PlanNode enNode;
        if (isMove) {
            stNode = PlanNode.withLocation(new LatLng(move_lat.latitude, move_lat.longitude));
            enNode = PlanNode.withLocation(new LatLng(end_lat.latitude, end_lat.longitude));
        } else {
            stNode = PlanNode.withLocation(new LatLng(my_lat.latitude, my_lat.longitude));
            enNode = PlanNode.withLocation(new LatLng(end_lat.latitude, end_lat.longitude));
        }
        if (type == 1) {
            showLoadingView();
            startDrivePlan(stNode, enNode);
            location_tvType.setText("驾车需要");
            location_imgType.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.car_choose));
            img1.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.car_white));
            img2.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.walk_grey));
        } else {
            //步行
            showLoadingView();
            startWalkPlan(stNode, enNode);
            location_tvType.setText("步行需要");
            location_imgType.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.walk_choose));
            img2.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.car_grey));
            img1.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.walk_white));
        }
    }

    /**
     * 初始化步行路径规划
     */
    private void walkPlanInit(LatLng lat) {
        if (mSerch == null) {
            mSerch = RoutePlanSearch.newInstance();
        }
        if (listener == null) {
            listener = new OnGetRoutePlanResultListener() {

                public void onGetWalkingRouteResult(WalkingRouteResult result) {
                    dismissLoadingView();
                    //获取步行线路规划结果
                    if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                        Toast.makeText(Main_NewActivity.this, "抱歉，太远了不支持步行导航", Toast.LENGTH_SHORT).show();
                        isGuihua = false;
                        return;
                    }
                    if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                        //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                        //result.getSuggestAddrInfo()
                        Toast.makeText(Main_NewActivity.this, "抱歉，太远了不支持步行导航", Toast.LENGTH_SHORT).show();
                        isGuihua = false;
                        return;
                    }
                    isGuihua = true;
                    if (result.error == SearchResult.ERRORNO.NO_ERROR) {
//                          mBaiduMap.clear();
                        // mroute = result.getRouteLines().get(0);
                        WalkingRouteOverlay overlay = new WalkingRouteOverlay(mBaiduMap);
                        //mrouteOverlay = overlay;
                        mBaiduMap.setOnMarkerClickListener(overlay);
                        WalkingRouteLine routeLine = result.getRouteLines().get(0);
                        overlay.setData(routeLine);
                        distance = routeLine.getDistance();
                        currentDistance = distance;
                        Log.d("tv_distance", distance + "");
                        time = routeLine.getDuration() / 60;
                        if (tv_distance != null) {
                            Log.d("tv_distance", distance + "");
                            if (distance >= 1000) {
                                tv_distance.setText(Math.round(distance / 1000 * 10) / 10 + "KM");
                            } else {
                                tv_distance.setText(distance + "M");
                            }
                            tv_time.setText(time + "分钟");
                        } else {
                            if (distance >= 1000) {
                                distance = Math.round(distance / 1000 * 10) / 10;
                            }
                        }
                        overlay.addToMap();
                        overlay.zoomToSpan();
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showLocationPop();
                                    }
                                });

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

                @Override
                public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

                }

                @Override
                public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

                }

                @Override
                public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
                    aCache.remove("time_guihua");
                    dismissLoadingView();
                    if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                        Toast.makeText(Main_NewActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                        isGuihua = false;
                        return;
                    }
                    if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                        //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                        //result.getSuggestAddrInfo()
                        Toast.makeText(Main_NewActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                        isGuihua = false;
                        return;
                    }
                    isGuihua = true;
                    if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                        DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
                        //mrouteOverlay = overlay;
                        mBaiduMap.setOnMarkerClickListener(overlay);
//                        map_view.removeOverlay(line);
                        DrivingRouteLine routeLine = drivingRouteResult.getRouteLines().get(0);

                        overlay.setData(routeLine);
                        distance = routeLine.getDistance();
                        currentDistance = distance;
                        time = routeLine.getDuration() / 60;
                        if (tv_distance != null) {
                            if (distance >= 1000) {
                                tv_distance.setText((Math.round(distance / 1000 * 10) / 10) + "KM");
                            } else {
                                tv_distance.setText(distance + "M");
                            }
                            tv_time.setText(time + "分钟");
                        }
                        overlay.addToMap();
                        overlay.zoomToSpan();
//
//                        DrivingRouteLine routeLine = drivingRouteResult.getRouteLines().get(0);
//                        distance = routeLine.getDistance();
//                        time = routeLine.getDuration() / 60;
//                        if (tv_distance != null) {
//                            if (distance >= 1000) {
//                                tv_distance.setText((Math.round(distance / 1000 * 10) / 10) + "KM");
//                            } else {
//                                tv_distance.setText(distance + "M");
//                            }
//                            tv_time.setText(time + "分钟");
//                        }
//                        test(drivingRouteResult.getRouteLines().get(0));

                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showLocationPop();
                                    }
                                });

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

                @Override
                public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

                }

                @Override
                public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

                }
            };
        }
        mSerch.setOnGetRoutePlanResultListener(listener);
        PlanNode stNode;
        PlanNode enNode;
        if (isMove) {
            stNode = PlanNode.withLocation(new LatLng(move_lat.latitude, move_lat.longitude));
            enNode = PlanNode.withLocation(new LatLng(end_lat.latitude, end_lat.longitude));
        } else {
            stNode = PlanNode.withLocation(new LatLng(my_lat.latitude, my_lat.longitude));
            enNode = PlanNode.withLocation(new LatLng(end_lat.latitude, end_lat.longitude));
        }
        startWalkPlan(stNode, enNode);
        //5秒获取一次状态看是否规划成功
        CountDownTimer timer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                //倒计时结束
                if (getShow2State()) {
                    if (aCache != null && aCache.getAsString("time_guihua") != null) {
                        aCache.remove("time_guihua");
                        toastMessage("距离有点远，请稍后");
                    }
                }

            }
        };
        timer.start();
    }

    //路径规划不要节点图标
    private void test(DrivingRouteLine drivingRouteLine) {
        final ArrayList<OverlayOptions> list = new ArrayList<OverlayOptions>();
        PolylineOptions object = new PolylineOptions();
        List<LatLng> arg0 = new ArrayList<LatLng>();
        List<DrivingRouteLine.DrivingStep> allStep = drivingRouteLine.getAllStep();
        for (int i = 0; i < allStep.size(); i++) {
            DrivingRouteLine.DrivingStep drivingStep = allStep.get(i);
            List<LatLng> wayPoints = drivingStep.getWayPoints();
            arg0.addAll(wayPoints);
        }
        object.color(Color.RED).width(10).points(arg0);

        list.add(object);
        OverlayManager overlayManager = new OverlayManager(mBaiduMap) {

            @Override
            public boolean onPolylineClick(Polyline arg0) {
                return false;
            }

            @Override
            public boolean onMarkerClick(Marker arg0) {
                return false;
            }

            @Override
            public List<OverlayOptions> getOverlayOptions() {
                return list;
            }
        };
        overlayManager.addToMap();

    }


    //路径规划不要节点图标
    private void test_walk(WalkingRouteLine walkRouteLine) {
        final ArrayList<OverlayOptions> list = new ArrayList<OverlayOptions>();
        PolylineOptions object = new PolylineOptions();
        List<LatLng> arg0 = new ArrayList<LatLng>();
        List<WalkingRouteLine.WalkingStep> allStep = walkRouteLine.getAllStep();
        for (int i = 0; i < allStep.size(); i++) {
            WalkingRouteLine.WalkingStep walkStep = allStep.get(i);
            List<LatLng> wayPoints = walkStep.getWayPoints();
            arg0.addAll(wayPoints);
        }
        object.color(R.color.fontcolor_yellow).width(10).points(arg0);

        list.add(object);
        OverlayManager overlayManager = new OverlayManager(mBaiduMap) {

            @Override
            public boolean onPolylineClick(Polyline arg0) {
                return false;
            }

            @Override
            public boolean onMarkerClick(Marker arg0) {
                return false;
            }

            @Override
            public List<OverlayOptions> getOverlayOptions() {
                return list;
            }
        };
        overlayManager.addToMap();

    }


    /**
     * 开始步行路径规划
     * //设置起终点、途经点信息，对于tranist search 来说，城市名无意义
     * PlanNode stNode = PlanNode.withLocation(new LatLng(StationsList.getStationsList().get(0).getLatitude(), StationsList.getStationsList().get(0).getLongitude()));
     * PlanNode enNode = PlanNode.withLocation(new LatLng(StationsList.getStationsList().get(StationsList.getStationsList().size()-1).getLatitude(), StationsList.getStationsList().get(StationsList.getStationsList().size()-1).getLongitude()));
     */
    private void startWalkPlan(PlanNode stNode, PlanNode enNode) {
        mSerch.walkingSearch((new WalkingRoutePlanOption()).from(stNode).to(enNode));
    }

    private void startDrivePlan(PlanNode stNode, PlanNode enNode) {
        mSerch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(enNode));
    }


    // 引擎初始化
    private void initWalk() {
        // BNRouteGuideManager.getInstance().setVoiceModeInNavi(BNaviSettingManager.VoiceMode.Quite);
//        Bundle bundle = new Bundle();
//        // 必须设置APPID，否则会静音
//        bundle.putString(BNCommonSettingParam.TTS_APP_ID, "11014997");
//        BNaviSettingManager.setNaviSdkParam(bundle);

        // 必须设置APPID，否则会静音
        Bundle bundle = new Bundle();
        // 必须设置APPID，否则会静音
        bundle.putString(BNCommonSettingParam.TTS_APP_ID, "11014997");
        BNaviSettingManager.setNaviSdkParam(bundle);
        mNaviHelper = WalkNavigateHelper.getInstance();
        initSetting();
        final WalkNaviLaunchParam param = new WalkNaviLaunchParam().stPt(move_lat).endPt(end_lat);
        mNaviHelper.initNaviEngine(this, new IWEngineInitListener() {
            @Override
            public void engineInitSuccess() {
                Log.d("walk", "引擎初始化成功");
                //initSetting();
                routePlanWithParam(param);
            }

            @Override
            public void engineInitFail() {
                Log.d("walk", "引擎初始化失败");
            }
        });
    }

    /**
     * /**
     * 开始算路
     */
    private void routePlanWithParam(WalkNaviLaunchParam param) {
        mNaviHelper.routePlanWithParams(param, new IWRoutePlanListener() {
            @Override
            public void onRoutePlanStart() {
                Log.d("walk", "开始算路");
            }

            @Override
            public void onRoutePlanSuccess() {
                Log.d("walk", "算路成功,跳转至诱导页面");
                Intent intent = new Intent();
                intent.setClass(Main_NewActivity.this, WNaviGuideActivity.class);
                startActivity(intent);
            }

            @Override
            public void onRoutePlanFail(WalkRoutePlanError error) {
                Log.d("walk", "算路失败");
            }

        });
    }

    /**
     * 启动百度地图导航(Native)
     */
    public void startNavi() {


        // 构建 导航参数
        NaviParaOption para = new NaviParaOption()
                .startPoint(move_lat).endPoint(end_lat);

        try {
            BaiduMapNavigation.openBaiduMapNavi(para, this);
        } catch (BaiduMapAppNotSupportNaviException e) {
            e.printStackTrace();
            //showDialog();
        }

    }

    private BNRoutePlanNode.CoordinateType mCoordinateType = null;

    private void routeplanToNavi(BNRoutePlanNode.CoordinateType coType) {
        mCoordinateType = coType;
        if (!hasInitSuccess) {
            Toast.makeText(Main_NewActivity.this, "还未初始化!", Toast.LENGTH_SHORT).show();
        }
        // 权限申请
        if (Build.VERSION.SDK_INT >= 23) {
            // 保证导航功能完备
            if (!hasCompletePhoneAuth()) {
                if (!hasRequestComAuth) {
                    hasRequestComAuth = true;
                    this.requestPermissions(authComArr, authComRequestCode);
                    return;
                } else {
                    Toast.makeText(Main_NewActivity.this, "没有完备的权限!", Toast.LENGTH_SHORT).show();
                }
            }

        }
        BNRoutePlanNode sNode = null;
        BNRoutePlanNode eNode = null;

        switch (coType) {
            case BD09LL: {
                sNode = new BNRoutePlanNode(move_lat.longitude, move_lat.latitude,
                        "起点", null, coType);
                eNode = new BNRoutePlanNode(end_lat.longitude, end_lat.latitude, "", null, coType);
                break;
            }
            default:
                ;
        }
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode));
        }
    }

    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {

            Intent intent = new Intent(Main_NewActivity.this, BNDemoMainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("routePlanNode", (BNRoutePlanNode) mBNRoutePlanNode);
            intent.putExtras(bundle);
            startActivity(intent);

        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
            Toast.makeText(Main_NewActivity.this, "算路失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void initNavi() {
        BNOuterTTSPlayerCallback ttsCallback = null;
        // 申请权限
        if (Build.VERSION.SDK_INT >= 23) {

            if (!hasBasePhoneAuth()) {

                this.requestPermissions(authBaseArr, authBaseRequestCode);
                return;

            }
        }

        BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
                    authinfo = "key校验成功!";
                } else {
                    authinfo = "key校验失败, " + msg;
                }
//                Main_NewActivity.this.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        Toast.makeText(Main_NewActivity.this, authinfo, Toast.LENGTH_LONG).show();
//                    }
//                });
            }

            public void initSuccess() {
                //  Toast.makeText(Main_NewActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                hasInitSuccess = true;
                initSetting();
            }

            public void initStart() {
                //  Toast.makeText(Main_NewActivity.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
            }

            public void initFailed() {
                Toast.makeText(Main_NewActivity.this, "请检查网络设置和GPS是否打开", Toast.LENGTH_SHORT).show();
            }

        }, null, ttsHandler, ttsPlayStateListener);

    }

    private boolean hasBasePhoneAuth() {
        // TODO Auto-generated method stub

        PackageManager pm = this.getPackageManager();
        for (String auth : authBaseArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private boolean hasCompletePhoneAuth() {
        // TODO Auto-generated method stub

        PackageManager pm = this.getPackageManager();
        for (String auth : authComArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    //    private void initSetting() {
//        // BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
//        BNaviSettingManager
//                .setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
//        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
//        // BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
//        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
//        if (BaiduNaviManager.isNaviInited()) {
//            routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09LL);
//        }
//    }
    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionRequested) {

            isPermissionRequested = true;

            ArrayList<String> permissions = new ArrayList<>();
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            if (permissions.size() == 0) {
                return;
            } else {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 0);
            }
        }
    }

    /**
     * 内部TTS播报状态回传handler
     */
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
                    //showToastMsg("Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
                    //showToastMsg("Handler : TTS play end");
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * 内部TTS播报状态回调接口
     */
    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

        @Override
        public void playEnd() {
            //showToastMsg("TTSPlayStateListener : TTS play end");
            Log.d("TTS", " TTS play end");
        }

        @Override
        public void playStart() {
            //showToastMsg("TTSPlayStateListener : TTS play start");
            Log.d("TTS", "TTS play start");
        }
    };

    private void startDriving() {
        if (initDirs()) {
            initNavi();
        }
        requestPermission();
    }

    private void initSetting() {

        // BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
        BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        // BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
        if (BaiduNaviManager.isNaviInited()) {
            routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09LL);
        }
        Bundle bundle = new Bundle();
        // 必须设置APPID，否则会静音
        bundle.putString(BNCommonSettingParam.TTS_APP_ID, "11014997");
        BNaviSettingManager.setNaviSdkParam(bundle);
    }

    /**
     * 用户综合信息
     */
    private void getRepleyList() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid", MyApplication.getUid());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.MENBER_STATISTICS, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if (response.getInt("status") != 0) {
                        toastMessage(response.getString("msg"));
                    } else {
                        if (response.getJSONObject("data") != null) {
                            MyApplication.jsonObject_composite = response.getJSONObject("data");
                            if (!StringUtils.isEmpty(response.getJSONObject("data").getString("doors"))) {
                                menu_smartCount.setText(response.getJSONObject("data").getString("doors"));
                            }
                            if (!StringUtils.isEmpty(response.getJSONObject("data").getString("wardrobe_orders_use"))) {
                                menu_tvClosetCount.setText(response.getJSONObject("data").getString("wardrobe_orders_use"));
                            }
                            if (!StringUtils.isEmpty(response.getJSONObject("data").getString("wardrobe_orders_use"))) {
                                //2543笔订单 3笔待付款
                                menu_tvOrderCount.setSpecifiedTextsColor(response.getJSONObject("data").getString("wardrobe_orders") + "笔订单 ", response.getJSONObject("data").getString("wardrobe_orders"), Color.parseColor("#FF6600"));
                                menu_tvOrderObligation.setSpecifiedTextsColor(response.getJSONObject("data").getString("wardrobe_orders_waitpay") + "待付款", response.getJSONObject("data").getString("wardrobe_orders_waitpay"), Color.parseColor("#FF6600"));
                            }
                            MyApplication.no_rendMessage = response.getJSONObject("data").getInt("no_read_message");
                            if (MyApplication.no_rendMessage > 0) {
                                tv_Noread.setVisibility(View.VISIBLE);
                            } else {
                                tv_Noread.setVisibility(View.GONE);
                            }
                        } else {

                        }
                    }

                } catch (Exception e) {
                    toastMessage("数据异常");
                }
                try {
                    dismissLoadingView();
                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                try {
                    dismissLoadingView();
                } catch (Exception e) {

                }
            }
        });
    }

    /**
     * 获取正在进行的洗衣列表，最多1条
     */
    private void getDoorLockList() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid", MyApplication.getUid());
            jsonObject.put("page", 1 + "");
            jsonObject.put("pagesize", 1 + "");//非必传
            jsonObject.put("status", "0,1,2,3,4,5");//非必传
            jsonObject.put("username", MyApplication.getUserJson().getString("m_username"));
            jsonObject.put("pwd", MyApplication.getPasswprd());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.LAUNDY_ORDER, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if (response.getInt("status") != 0) {
                        layout_orderList.removeAllViews();
                        line2.setVisibility(View.VISIBLE);
                    } else {
                        if (response.getJSONArray("data") == null || response.getJSONArray("data").length() == 0) {
                            layout_orderList.removeAllViews();
                            line2.setVisibility(View.VISIBLE);
                        } else {
                            if (aCache.getAsString("doorlist") == null) {
                                aCache.put("doorlist", "have", ACache.TIME_MILL * 30);//存储一个值让其判断回到当前界面是否刷新一次
                            }
                            line2.setVisibility(View.GONE);
                            layout_orderList.removeAllViews();
//                            line2.setVisibility(View.VISIBLE);
                            for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                                // list.add(response.getJSONArray("data").getJSONObject(i));
                                addOrderView(response.getJSONArray("data").getJSONObject(i));
                            }

                        }

                    }

                } catch (Exception e) {


                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {

              //  toastMessage("网络异常，请检查网络后重试");
            }
        });
    }


    /**
     * 获取洗衣品牌列表
     */
    private void getBrandList(String ark_id) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid", MyApplication.getUid());
            jsonObject.put("ark_id", ark_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.laundy_brand, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if (list_brand == null) {
                        list_brand = new ArrayList<>();
                    }
                    if (response.getInt("status") != 0) {
                        list_brand.clear();
                    } else {
                        if (response.getJSONArray("data") == null || response.getJSONArray("data").length() == 0) {
                            list_brand.clear();
                        } else {
                            list_brand.clear();
                            for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                                // list.add(response.getJSONArray("data").getJSONObject(i));
                                if (i == 0) {
                                    brandId = response.getJSONArray("data").getJSONObject(0).getInt("id");
                                    brandName = response.getJSONArray("data").getJSONObject(0).getString("title");
                                }
                                if (response.getJSONArray("data").getJSONObject(i).getInt("id") == MyApplication.brand) {
                                    brandId = response.getJSONArray("data").getJSONObject(i).getInt("id");
                                    brandName = response.getJSONArray("data").getJSONObject(i).getString("title");
                                }

                                list_brand.add(response.getJSONArray("data").getJSONObject(i));
                            }

                        }

                    }
                    initData();
                } catch (Exception e) {


                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("网络异常，请检查网络后重试4");
            }
        });
    }


    /**
     * 动态添加列表数据
     */
    private void addOrderView(final JSONObject jsonObject) {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_main_list, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        TextView tv_time = (TextView) view.findViewById(R.id.tv_time);
        TextView tv_hours = (TextView) view.findViewById(R.id.tv_hours);
        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        params.height = (int) (DisplayUtil.getScreenHeight(this) / 11.4);

        try {
            if (!StringUtils.isEmpty(jsonObject.getString("addtime"))) {
                tv_time.setText(TimeUtilsDate.timedate5(jsonObject.getLong("addtime")));
                tv_hours.setText(TimeUtilsDate.timedate6(jsonObject.getLong("addtime")));
            } else {
                tv_time.setText("");
                tv_hours.setText("");
            }
            if (!StringUtils.isEmpty(jsonObject.getString("ark_name"))) {
                tv_content.setText(jsonObject.getString("ark_name") + jsonObject.getString("ark_box_number") + "号箱");
            } else {
                tv_content.setText("未编辑");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AnimationUtils().loadAnimation(Main_NewActivity.this, R.anim.bg_alpha));
                Bundle bundle = new Bundle();
                try {
                    bundle.putString("order_id", jsonObject.getString("id"));
                    bundle.putString("number", jsonObject.getString("number"));
                    bundle.putString("status", jsonObject.getString("status"));
                    bundle.putString("additional", jsonObject.getString("additional"));
                    bundle.putInt("type", jsonObject.getInt("status"));
                    bundle.putString("ark_name", jsonObject.getString("ark_name") + " " + jsonObject.getString("ark_box_number"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                startActivity_Bundle(Main_NewActivity.this, OrderDetailActivity.class, bundle);
            }
        });
        view.setLayoutParams(params);
        layout_orderList.addView(view);
    }

    /**
     * 衣柜详情列表
     */
    private void getArkList(final BDLocation location, LatLng latLng) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("page", "1");
            jsonObject.put("pagesize", MyApplication.getPageSize() + "");
            jsonObject.put("lat", latLng.latitude + "");
            jsonObject.put("lon", latLng.longitude + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        Log.d("response_eero2", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.ARK_LIST, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    Log.d("response", response.toString());
                    if (response.getInt("status") != 0) {
                        toastMessage(response.getString("msg"));
                    } else {
//                        Iterator<?> it = response.getJSONObject("data").keys();
//                        while(it.hasNext()){//遍历JSONObject
//                            list_location.add(new LatLng( response.getJSONObject("data").getJSONObject((String) it.next().toString()).getDouble("lat"), response.getJSONObject("data").getJSONObject((String) it.next().toString()).getDouble("lon") ));
//                        }
                        list_location.clear();
                        if (response.getJSONArray("data") != null && response.getJSONArray("data").length() > 0) {
                            for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                                list_location.add(new LatLng(response.getJSONArray("data").getJSONObject(i).getDouble("lat") / 1000000, response.getJSONArray("data").getJSONObject(i).getDouble("lon") / 1000000));
                            }
                            //构建marker图标
                            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.location_picture);
                            MarkerOptions option;
                            for (int i = 0; i < list_location.size(); i++) {
                                //构建MarkerOption，用于在地图上添加Marker
                                option = new MarkerOptions().icon(bitmap).position(list_location.get(i));
                                //生长动画
                                option.animateType(MarkerOptions.MarkerAnimateType.grow);
                                //在地图上添加Marker，并显示
                                mBaiduMap.addOverlay(option);
                                //设置Marker覆盖物的ZIndex
                                option.zIndex(i);
                            }
                        } else {
                            toastMessage(response.getString("msg"));
                        }
                    }

                } catch (Exception e) {

                }
                if (location == null) {
                    dismissLoadingView();
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                Log.d("response_eero", error_msg);
                if (location == null) {
                    dismissLoadingView();
                }
            }
        });
    }


    // 提示用户该请求权限的弹出框
    private void showDialogTipUserRequestPermission() {

        new AlertDialog.Builder(this)
                .setTitle("推送权限不可用")
                .setMessage("由于程序需要相关推送权限，为你推送相关信息；\n否则，您将无法正常及时收到相关推送信息")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startRequestPermission();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    // 开始提交请求权限
    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 321);
    }


    // 提示用户去应用设置界面手动开启权限

    private void showDialogTipUserGoToAppSettting() {

        dialog = new AlertDialog.Builder(this)
                .setTitle("推送权限不可用")
                .setMessage("请在-应用设置-权限-中，允许小管家使用相关推送权限")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        startActivityForResult(intent, 123);
    }

    private void showPopwindow_boxType(final boolean is_put, final boolean is_add, final String json) {
        this.is_put = is_put;
        if (popWindow_boxType == null) {
            int width = getResources().getDisplayMetrics().widthPixels / 10 * 8;
//            int height = getResources().getDisplayMetrics().widthPixels / 2.2;
            View popView = LayoutInflater.from(this).inflate(R.layout.choose_box_type, null);
            popWindow_boxType = new PopupWindow(popView, width, LinearLayout.LayoutParams.WRAP_CONTENT);
            listview_Brand = (GridView) popView.findViewById(R.id.listview_Brand);
            tv_type1 = (TextView) popView.findViewById(R.id.tv_type1);
            tv_type2 = (TextView) popView.findViewById(R.id.tv_type2);
            popWindow_boxType.setAnimationStyle(R.style.popwin_anim_style);
            popWindow_boxType.setFocusable(true);
            popWindow_boxType.setOutsideTouchable(true);// 设置同意在外点击消失
            ColorDrawable dw = new ColorDrawable(0x30000000);
            popWindow_boxType.setBackgroundDrawable(dw);
            popWindow_boxType.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    view_zhezhao.setVisibility(View.GONE);
                }
            });
        }
        JSONObject jsonObject_open = null;
        try {
            jsonObject_open = new JSONObject(json);
            getBrandList(jsonObject_open.getString("ark_id"));
            jsonObject_open = null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_type1:
                        popWindow_boxType.dismiss();
                        try {
                            if (is_put) {
                                //放货开箱
                                SocketMessage(new JSONObject(json), is_add, 1 + "");
                            } else {
                                //取货开箱
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.tv_type2:
                        popWindow_boxType.dismiss();
                        try {
                            if (is_put) {
                                //放货开箱
                                SocketMessage(new JSONObject(json), is_add, 2 + "");
                            } else {
                                //取货开箱
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };
        view_zhezhao.setVisibility(View.VISIBLE);
        tv_type1.setOnClickListener(onClickListener);
        tv_type2.setOnClickListener(onClickListener);
        view_zhezhao.setVisibility(View.VISIBLE);
        popWindow_boxType.showAtLocation(drawerLayout, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 是否拨打电话
     */
    private void show_callPop(final String number) {
        if (popWindow_call == null) {
            int width = getResources().getDisplayMetrics().widthPixels / 10 * 8;
//            int height = getResources().getDisplayMetrics().widthPixels / 2.2;
            View popView = LayoutInflater.from(this).inflate(R.layout.layout_call_pop, null);
            popWindow_call = new PopupWindow(popView, width, LinearLayout.LayoutParams.WRAP_CONTENT);
            tv_callCancel = (TextView) popView.findViewById(R.id.tv_callCancel);
            tv_callSure = (TextView) popView.findViewById(R.id.tv_callSure);
            tv_callNumber = (TextView) popView.findViewById(R.id.tv_callNumber);
            tv_callNumber.setText(number);
            popWindow_call.setAnimationStyle(R.style.popwin_anim_style);
            popWindow_call.setFocusable(true);
            popWindow_call.setOutsideTouchable(true);// 设置同意在外点击消失
            ColorDrawable dw = new ColorDrawable(0x30000000);
            popWindow_call.setBackgroundDrawable(dw);

            popWindow_call.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    view_zhezhao.setVisibility(View.GONE);
                }
            });
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow_call.dismiss();
                switch (v.getId()) {
                    case R.id.tv_callCancel:
                        break;
                    case R.id.tv_callSure:
                        startCallPhone(number);
                        break;
                }
            }
        };
        tv_callCancel.setOnClickListener(onClickListener);
        tv_callSure.setOnClickListener(onClickListener);
        view_zhezhao.setVisibility(View.VISIBLE);
        popWindow_call.showAtLocation(drawerLayout, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
    }


    /**
     * 发送开箱信息，放入衣物
     */
    private void SocketMessage(JSONObject json, boolean orderaddmoney, String box_type) {
        try {
            JSONObject jsonObject = new JSONObject();
            try {
                Log.d("asdasda2", json.toString());
                jsonObject.put("apiType", RequestTag.OPEN_ARK);
                jsonObject.put("ark_id", json.getString("ark_id"));
                Log.d("ark_idd", json.getString("ark_id"));
                jsonObject.put("client_id", "APP_" + MyApplication.getUid());//客户端ID（APP_+用户UID）
                jsonObject.put("type", json.getString("type"));//1=存件码开箱 2=取件码开箱
                jsonObject.put("uid", MyApplication.getUid());
                jsonObject.put("box_type", box_type);//打开大小箱，1=衣柜箱子（大） 2=鞋柜箱子（小）
                jsonObject.put("brand", brandId + "");
                jsonObject.put("brand_name", brandName);
                jsonObject.put("mobile", MyApplication.getUserJson().getString("mobile"));
                jsonObject.put("name", MyApplication.getUserJson().getString("name"));
                jsonObject.put("nickname", MyApplication.getUserJson().getString("nickname"));
                if (json.getString("type").equals("1")) {
                    if (orderaddmoney) {
                        //加急
                        jsonObject.put("additional", 1 + "");//type=1时，开type=1时必传 1=加急 0=正常
                    } else {
                        jsonObject.put("additional", 0 + "");//type=1时，开type=1时必传 1=加急 0=正常
                    }
                }

                jsonObject.put("key", RequestTag.SOCKET_KEY);
                jsonObject.put("sign", Md5.md5_socket(jsonObject.toString()));

            } catch (Exception e) {

            }
//有空指针的可能
            Log.d("qwr", jsonObject.toString());
            showLoadingView2("正在开箱中");
            boolean isSend = iBackService.sendMessage(jsonObject.toString());//Send Content by socket
            CountDownTimer timer = new CountDownTimer(10000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    //倒计时结束
                    if (getShow2State()) {
                        //未收到开箱信息
                        setShow2Tile("开箱超时，请重试");
                    }

                }
            };
            timer.start();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送开箱信息,获取衣物
     */
    private void SocketMessage_getBox(JSONObject json2) {
        try {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("apiType", RequestTag.OPEN_ARK);
                jsonObject.put("ark_id", json2.getString("ark_id"));
                jsonObject.put("client_id", "APP_" + MyApplication.getUid());//客户端ID（APP_+用户UID）
                jsonObject.put("type", 2 + "");//1=存件码开箱 2=取件码开箱
                jsonObject.put("uid", MyApplication.getUid());
                jsonObject.put("mobile", MyApplication.getUserJson().getString("mobile"));
                jsonObject.put("name", MyApplication.getUserJson().getString("name"));
                jsonObject.put("nickname", MyApplication.getUserJson().getString("nickname"));
                jsonObject.put("box_number", json2.getString("box_number"));//type=2时，开指定箱子编号（查询订单时会返回箱子ID和箱子编号）
                jsonObject.put("order_id", json2.getString("id"));
                jsonObject.put("key", RequestTag.SOCKET_KEY);
                jsonObject.put("sign", Md5.md5_socket(jsonObject.toString()));

            } catch (Exception e) {

            }
//有空指针的可能
            boolean isSend = iBackService.sendMessage(jsonObject.toString());//Send Content by socket
            showLoadingView2("正在开箱中");
            CountDownTimer timer = new CountDownTimer(10000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    //倒计时结束
                    if (getShow2State()) {
                        //未收到开箱信息
                        setShow2Tile("开箱超时，请重试");
                    }

                }
            };
            timer.start();
//            Toast.makeText(this, isSend ? "success" : "fail",
//                    Toast.LENGTH_SHORT).show();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取最新的资金状态
     */
    private void quary_money() {
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
                    if (response.getString("status").equals("0")) {
                        aCache.put("quary_money", "quary", ACache.TIME_MILL * 30);
                        MyApplication.getUserJson().put("money", response.getJSONObject("data").getLong("money"));
                        MyApplication.getUserJson().put("clothes", response.getJSONObject("data").getLong("clothes"));
                        MyApplication.getUserJson().put("interal", response.getJSONObject("data").getLong("interal"));
                        MyApplication.getUserJson().put("card", response.getJSONObject("data").getLong("card"));
                        home_tvBalance.setText(response.getJSONObject("data").getLong("money") / 1000 + "");
                        home_tvClothes.setText(response.getJSONObject("data").getLong("clothes") / 1000 + "");
                    } else {
                        // toastMessage(response.getString("msg"));
                        toastMessage(response.getString("msg"));
                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {

            }
        });
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
                    view_zhezhao.setVisibility(View.GONE);
                }
            });

        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_cancel:
                        popupWindow_gps.dismiss();
                        break;
                    case R.id.tv_sure:
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 1315);
                        popupWindow_gps.dismiss();
                        break;
                }
            }
        };
        tv_cancle_gps.setOnClickListener(onClickListener);
        tv_sure_gps.setOnClickListener(onClickListener);
        view_zhezhao.setVisibility(View.VISIBLE);
        Runnable showPopWindowRunnable = new Runnable() {

            @Override
            public void run() {
                // 得到activity中的根元素
                View view = findViewById(R.id.drawerLayout);
                // 如何根元素的width和height大于0说明activity已经初始化完毕
                if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {
                    // 显示popwindow
                    popupWindow_gps.showAtLocation(drawerLayout, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
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

    private void initData() {
        // TODO Auto-generated method stub
        //总的页数向上取整

        if (list_brand.size() > 0) {
            try {
                brandId = list_brand.get(0).getInt("id");
                brandName = list_brand.get(0).getString("title");
            } catch (Exception e) {

            }
        }
        for (int i = 0; i < list_brand.size(); i++) {
            try {
                if (MyApplication.getBrand() == list_brand.get(0).getInt("id")) {
                    brandId = list_brand.get(i).getInt("id");
                    brandName = list_brand.get(i).getString("title");
                    break;
                }
            } catch (Exception e) {

            }
        }
        myBrandListAdapter = new MyBrandListAdapter(this, list_brand, new MyBrandListAdapter.Click() {
            @Override
            public void onClick(int position) {
                try {
                    brandId = list_brand.get(position).getInt("id");
                    brandName = list_brand.get(position).getString("title");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, MyApplication.getBrand());
        listview_Brand.setAdapter(myBrandListAdapter);
        if (list_brand.size() > 6) {
            //最多显示四行，超出滚动显示
            ViewGroup.LayoutParams params = listview_Brand.getLayoutParams();
            params.height = (int) (getResources().getDimension(R.dimen.x68) * 3);
            listview_Brand.setLayoutParams(params);
        }
    }

    /**
     * 喜欢的品牌弹框
     */
    private void brand_likePop() {
        if (pop_like == null) {
            int width = getResources().getDisplayMetrics().widthPixels / 10 * 7;
            int height = getResources().getDisplayMetrics().heightPixels / 2;
            View popView = LayoutInflater.from(this).inflate(R.layout.likebrand_pop, null);
            pop_like = new PopupWindow(popView, width, LinearLayout.LayoutParams.WRAP_CONTENT);
            listview_likeBrand = (GridView) popView.findViewById(R.id.listview_likeBrand);
            tv_agree = (TextView) popView.findViewById(R.id.tv_agree);
            pop_like.setAnimationStyle(R.style.popwin_anim_style);
            pop_like.setFocusable(true);
            pop_like.setOutsideTouchable(true);// 设置同意在外点击消失
            ColorDrawable dw = new ColorDrawable(0x30000000);
            pop_like.setBackgroundDrawable(dw);
            pop_like.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    view_zhezhao_menu.setVisibility(View.GONE);
                }
            });
            if (list_brand_like.size() > 8) {
                //最多显示四行，超出滚动显示
                ViewGroup.LayoutParams params = listview_likeBrand.getLayoutParams();
                params.height = (int) (getResources().getDimension(R.dimen.x68) * 4);
                listview_likeBrand.setLayoutParams(params);
            }
            initData_like();
        }
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_agree:
                        //确认选择默认喜欢的品牌
                        view_zhezhao_menu.setVisibility(View.GONE);
                        saveBrand(brandID_like, brandPic_like);
                        pop_like.dismiss();
                        break;
                }
            }
        };
        tv_agree.setOnClickListener(onClickListener);
        view_zhezhao_menu.setVisibility(View.VISIBLE);
        pop_like.showAtLocation(drawerLayout, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void initData_like() {
        // TODO Auto-generated method stub
        //总的页数向上取整
        if (list_brand_like.size() > 0) {
            try {
                brandID_like = list_brand_like.get(0).getInt("id");
                brandPic_like = list_brand_like.get(0).getString("pic");
            } catch (Exception e) {

            }
        }
        for (int i = 0; i < list_brand_like.size(); i++) {
            try {
                if (MyApplication.getBrand() == list_brand_like.get(0).getInt("id")) {
                    brandID_like = list_brand_like.get(i).getInt("id");
                    brandPic_like = list_brand_like.get(i).getString("pic");
                    break;
                }
            } catch (Exception e) {

            }
        }
        myBrandGridAdapter = new MyBrandListAdapter(this, list_brand_like, new MyBrandListAdapter.Click() {
            @Override
            public void onClick(int position) {
                try {
                    brandID_like = list_brand_like.get(position).getInt("id");
                    brandPic_like = list_brand_like.get(position).getString("pic");
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage(e.toString());
                }
            }
        }, MyApplication.getBrand());
        //设置喜欢的品牌适配器
        listview_likeBrand.setAdapter(myBrandGridAdapter);

    }

    /**
     * 保存用户喜欢的品牌
     */
    private void saveBrand(final int brand, final String pic) {
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
            jsonObject.put("brand", brand + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.saveLikeBrand, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    toastMessage(response.getString("msg"));
                    Log.d("urlll2", response.toString());
                    if (response.getInt("status") != 0) {

                    } else {
                        tv_chooseBrand.setVisibility(View.GONE);
                        img_brand.setVisibility(View.VISIBLE);
                        MyApplication.brand = brand;
                        SPUtils.setSharedIntData(MyApplication.getAppContext(), "brand", brand);
                        MyApplication.brand_pics = pic;
                        SPUtils.setSharedStringData(MyApplication.getAppContext(), "brand_pics" + MyApplication.brand, pic);
                        if (MyApplication.getBrand_pics() != null) {
                            Picasso.with(Main_NewActivity.this).load(RequestTag.BaseImageUrl + MyApplication.brand_pics).error(R.mipmap.default_iv).placeholder(R.mipmap.default_iv).into(img_brand);
                        }
                        //   Log.d("urlll",pic);
                    }
                } catch (Exception e) {
                    toastMessage("数据解析异常，请稍后重试");
                    Log.d("urlll2", e.toString());
                }
                dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dismissLoadingView();
                toastMessage("网络异常，请检查网络后重试5");
            }
        });
    }

    /**
     * 充值
     */

    private void recharge(final JSONObject jsonObject) {
        showLoadingView_pay();
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
            json.put("card_id", jsonObject.getString("card_id"));
            json.put("password", jsonObject.getString("password"));
            json.put("key", jsonObject.getString("key"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        params.put("data", json.toString());
        Log.d("daadada", json.toString());
        String sign = Md5.md5(json.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.recharge_card, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    dismiss_pay();
                    Log.d("recharge", response.toString());
                    toastMessage(response.getString("msg"));
                    if (response.getString("status").equals("0")) {
                        //充值成功
                        if (response.getString("type").equals("0")) {
                            //0=充值成功后跳转 资金明细 > 余额记录里
                            Bundle bundle = new Bundle();
                            bundle.putString("type", "money");
                            bundle.putString("title", "余额记录");
                            startActivity_Bundle(Main_NewActivity.this, MoneyDetailActivity.class, bundle);
                        } else if (response.getString("type").equals("1")) {
                            //1=充值成功后跳转 资金明细 > 洗衣券记录里
                            Bundle bundle = new Bundle();
                            bundle.putString("type", "xieyid");
                            bundle.putString("title", "洗衣点记录");
                            bundle.putString("xieyid", "xieyid");
                            startActivity_Bundle(Main_NewActivity.this, MoneyDetailActivity.class, bundle);
                        } else if (response.getString("type").equals("2")) {
                            //2=充值成功后跳转 资金明细 > 体验卡记录里
                            Bundle bundle = new Bundle();
                            bundle.putString("type", "card");
                            bundle.putString("title", "体验卡记录");
                            bundle.putString("card", "card");
                            startActivity_Bundle(Main_NewActivity.this, MoneyDetailActivity.class, bundle);
                        }
                    }
                } catch (Exception e) {
                    //toastMessage("数据解析异常");
                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("网络异常，请检查网络后重试6");

            }
        });
    }

    /**
     * 查询包月时间
     */
    private void quary_time() {

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
        Log.d("daadada", json.toString());
        String sign = Md5.md5(json.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.MOUTH_COMBO_TIME, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {

                    if (response.getString("status").equals("0")) {
                        tv_comboTime.setText("包月洗至" + TimeUtilsDate.timedate9(response.getLong("end_time")));
                    } else {
                        toastMessage(response.getString("msg"));
                    }
                } catch (Exception e) {
                    //toastMessage("数据解析异常");
                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("网络异常，请检查网络后重试7");

            }
        });
    }

    /**
     * 设置底部tab按钮
     */
    private void setItem() {
        if (columnUtils_tab == null) {
            columnUtils_tab = new ColumnUtils(this, new ColumnUtils.Result() {
                @Override
                public void onSuccess(String title, JSONArray jsonArray) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            list_tab.add(jsonArray.getJSONObject(i));
                            tv_tabs[i].setText(jsonArray.getJSONObject(i).getString("title"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    // myGridAdapter_home.notifyDataSetChanged();
                }

                @Override
                public void onFailure(String msg) {
                    // Log.d("weixinxin2",msg);
                    Log.d("请检查网络8", msg);
                   // toastMessage("网络异常，请检查网络后重试8");
                }
            });
        }
        columnUtils_tab.getColumn("", "AppBottomButton");
    }

    private void showShareDialog() {

        shareDialog = new Dialog(this, R.style.shareDialogStyle);
        shareDialog.setCanceledOnTouchOutside(true);
        // 填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_share_pop, null);
        // 初始化控件
        tv_wechat = (TextView) inflate.findViewById(R.id.tv_wechat);
        tv_friends = (TextView) inflate.findViewById(R.id.tv_friends);
        tv_qq = (TextView) inflate.findViewById(R.id.tv_qq);
        tv_wechat.setOnClickListener(this);
        tv_friends.setOnClickListener(this);
        tv_qq.setOnClickListener(this);
        tv_cancel = (TextView) inflate.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(this);
        // 将布局设置给Dialog
        shareDialog.setContentView(inflate);
        // 获取当前Activity所在的窗体
        Window dialogWindow = shareDialog.getWindow();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = shareDialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); // 设置宽度
        shareDialog.getWindow().setAttributes(lp);
        // 设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        // 获得窗体的属性
        shareDialog.show();// 显示对话框

    }

    static class MyIUiListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
// 操作成功

            Toast.makeText(MyApplication.getAppContext(), "分享成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(UiError uiError) {
// 分享异常
            Toast.makeText(MyApplication.getAppContext(), "分享失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
// 取消分享
            Toast.makeText(MyApplication.getAppContext(), "取消分享", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分享到QQ
     */
    public void qqShare() {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);//分享的类型
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://down.scyoue.com/download/");//内容地址
        try {
            params.putString(QQShare.SHARE_TO_QQ_TITLE, MyApplication.getConfigrationJson().getJSONObject("share").getJSONObject("qq").getString("name"));//分享标题
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, MyApplication.getConfigrationJson().getJSONObject("share").getJSONObject("qq").getString("describe"));//要分享的内容摘要
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, MyApplication.getConfigrationJson().getJSONObject("share").getJSONObject("qq").getString("logo"));//分享的图片URL
        } catch (Exception e) {

        }
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "优e管家");//应用名称
        //params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,R.mipmap.app_icons);
        mTencent.shareToQQ(Main_NewActivity.this, params, mIUiListener);
    }

    private void showpopWindow() {
        if (popWindow == null) {

            int width = getResources().getDisplayMetrics().widthPixels / 10 * 8;
//            int height = getResources().getDisplayMetrics().widthPixels / 2.2;
            View popView = LayoutInflater.from(this).inflate(R.layout.delete_img_pop, null);
            popWindow = new PopupWindow(popView, width, LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView tv_title = (TextView) popView.findViewById(R.id.tv_title);
            tv_title.setText("是否退出当前账号");
            tv_cancel = (TextView) popView.findViewById(R.id.tv_cancel);
            tv_sure = (TextView) popView.findViewById(R.id.tv_sure);
            tv_content = (MyTextView) popView.findViewById(R.id.tv_address);
            tv_content.setVisibility(View.GONE);

            popWindow.setAnimationStyle(R.style.popwin_anim_style);
            popWindow.setFocusable(true);
            popWindow.setOutsideTouchable(false);// 设置同意在外点击消失
            ColorDrawable dw = new ColorDrawable(0x30000000);
            popWindow.setBackgroundDrawable(dw);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.tv_cancel:
                            popWindow.dismiss();
                            break;
                        case R.id.tv_sure:
                            popWindow.dismiss();
                            MyApplication.clearAll();
                            startActivity(Main_NewActivity.this, LoginActivity.class);
                            finish();
                            break;
                    }
                }
            };
            popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    DisplayUtil.backgroundAlpha(1.0f, Main_NewActivity.this);
                }
            });
            tv_sure.setOnClickListener(onClickListener);
            tv_cancel.setOnClickListener(onClickListener);
        }
        DisplayUtil.backgroundAlpha(0.3f, this);
        popWindow.showAtLocation(drawerLayout, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 检查更新
     */
    private void check_update(final String client_ver) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("client_ver", client_ver);//客户端当前版本号
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        http:
//211.149.154.203:85/download/android.apk
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.CHECK_UPDATE, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if (response.getInt("status") == 0) {
                        if (response.getJSONObject("data").getString("android_ver").equals(client_ver)) {
                            //没有更新

                        } else {
                            //更新

                            showUpdaloadDialog(response.getJSONObject("data").getString("android"));
                        }
                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                Log.d("请检查网络", error_msg);
               // toastMessage("请检查网络");

            }
        });
    }

    private void showUpdaloadDialog(final String path) {
        // 这里的属性可以一直设置，因为每次设置后返回的是一个builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置提示框的标题
        builder.setTitle("版本升级").
                setIcon(R.mipmap.app_icons). // 设置提示框的图标
                setMessage("发现新版本！请及时更新").// 设置要显示的信息
                setPositiveButton("确定", new DialogInterface.OnClickListener() {// 设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startUpload(path);//下载最新的版本程序
            }
        }).setNegativeButton("取消", null);//设置取消按钮,null是什么都不做，并关闭对话框
        AlertDialog alertDialog = builder.create();
        // 显示对话框
        alertDialog.show();
    }

    private void startUpload(String path) {
        showLoadingView();
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("正在下载新版本");
        progressDialog.setCancelable(true);//不能手动取消下载进度对话框,fanhuijian
        progressDialog.setIndeterminate(false);
        String moduleName = getSaveFilePath();
        MyOkHttp.get().download(this, path, moduleName, "updata.apk", new DownloadResponseHandler() {
            @Override
            public void onFinish(final File download_file) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //  Toast.makeText(SetingActivity.this, "下载完成", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        installApk(download_file);
                    }
                });

            }

            @Override
            public void onProgress(final long currentBytes, final long totalBytes) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isupdate) {
//                                progressDialog.setMax((int) totalBytes);
//                                progressDialog.show();
//                                isupdate = false;
                            showpopupWindow_down(currentBytes / 1024 / 1024 + "m", totalBytes / 1024 / 1024 + "m");
                            isupdate = false;
                        } else {
                            dismissLoadingView();
                            // progressDialog.setProgress((int)currentBytes);
                            tv_current.setText(currentBytes / 1024 / 1024 + "m");
                            tv_progress.setText((int) (currentBytes * 100 / totalBytes) + "%");
                            numberProgress.setProgress((int) (currentBytes * 100 / totalBytes));
                        }
                    }
                });

            }

            @Override
            public void onFailure(String error_msg) {
                dismissLoadingView();
                toastMessage("网络异常");
            }
        });
    }

    /**
     * 安装apk
     */
    protected void installApk(File file) {
//        Intent intent = new Intent();
//        Uri data;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判断版本大于等于7.0
//            // "com.ansen.checkupdate.fileprovider"即是在清单文件中配置的authorities
//            // 通过FileProvider创建一个content类型的Uri
//            data = FileProvider.getUriForFile(this, "com.ansen.checkupdate.fileprovider", file);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);// 给目标应用一个临时授权
//        } else {
//            data = Uri.fromFile(file);
//        }
//
//
//        intent.setDataAndType(data, "application/vnd.android.package-archive");
//        startActivity(intent);

//        Intent intent = new Intent();
//        //执行动作
//        intent.setAction(Intent.ACTION_VIEW);
//        //执行的数据类型
//        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//        startActivity(intent);


        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 24) { //Android 7.0及以上
            // 参数2 清单文件中provider节点里面的authorities ; 参数3  共享的文件,即apk包的file类
            Uri apkUri = FileProvider.getUriForFile(this, "com.ansen.checkupdate.fileprovider", file);
            //对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        startActivity(intent);


    }


    /**
     * 获取文件保存路径 sdcard根目录/download/文件名称
     *
     * @return
     */
    public static String getSaveFilePath() {
        String newFilePath = Environment.getExternalStorageDirectory() + "/Download/";
        return newFilePath;
    }

    private void showpopupWindow_down(String cur, String total) {
        if (popupWindow_down == null) {
            int width = getResources().getDisplayMetrics().widthPixels / 10 * 8;
//            int height = getResources().getDisplayMetrics().widthPixels / 2.2;
            View popView = LayoutInflater.from(this).inflate(R.layout.down_load, null);
            popupWindow_down = new PopupWindow(popView, width, LinearLayout.LayoutParams.WRAP_CONTENT);
            tv_current = (TextView) popView.findViewById(R.id.tv_current);
            tv_progress = (TextView) popView.findViewById(R.id.tv_progress);

            tv_total = (TextView) popView.findViewById(R.id.tv_total);
            numberProgress = (NumberProgressView) popView.findViewById(R.id.numberProgress);
            numberProgress.setProgress(0);
            tv_progress.setText("0%");
            popupWindow_down.setAnimationStyle(R.style.popwin_anim_style);
            tv_total.setText(total);
            tv_current.setText(cur);
            popupWindow_down.setFocusable(false);
            popupWindow_down.setOutsideTouchable(false);// 设置同意在外点击消失
            ColorDrawable dw = new ColorDrawable(0x30000000);
            popupWindow_down.setBackgroundDrawable(dw);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {

                    }
                }
            };
            popupWindow_down.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    DisplayUtil.backgroundAlpha(1.0f, Main_NewActivity.this);
                }
            });

        }
        dismissLoadingView();
        DisplayUtil.backgroundAlpha(0.3f, this);
        popupWindow_down.showAtLocation(drawerLayout, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

}
