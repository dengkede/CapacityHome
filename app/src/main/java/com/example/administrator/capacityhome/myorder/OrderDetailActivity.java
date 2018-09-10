package com.example.administrator.capacityhome.myorder;
/**
 * 订单详细
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.PtrHandler;
import com.chanven.lib.cptr.header.MaterialHeader;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.example.administrator.capacityhome.BaseTakePhotoActivity;
import com.example.administrator.capacityhome.Main_NewActivity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.combo.ComboDetailActivity;
import com.example.administrator.capacityhome.feedback.FeedBackDetailActivity2;
import com.example.administrator.capacityhome.wxapi.Constants;
import com.example.administrator.capacityhome.wxapi.WXPayEntryActivity;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.CornerGridAdapter;
import adapter.OrderDoubtAdapter;
import http.RequestTag;
import model.VideoModel;
import myview.Type_choose;
import utils.DisplayUtil;
import utils.GetAgrement;
import utils.ImageBase64;
import utils.ImagePost;
import utils.KeyBordUtil;
import utils.Md5;
import utils.StringUtils;
import utils.TakePhotoHelper;
import utils.TimeUtils;
import utils.TimeUtilsDate;
import utils.paydialog.IntetnetState;
import utils.paydialog.Pay_Dialog;
import widget.DefaultLoadMoreViewFooter_normal;
import widget.NoScrollGridView;
import zfb_pay.PayType;

/**
 * 订单回复详情
 */
public class OrderDetailActivity extends BaseTakePhotoActivity implements View.OnClickListener, OnLoadMoreListener, PtrHandler {
    private LinearLayout layout_addType;//添加订单相关状态数据
    private TextView orderDetail_tv_price;//价格
    int type;
    private PopupWindow popWindow;
    private RelativeLayout layout_parent;
    private View view_zhezhao;
    private RelativeLayout layout_pop_parent;
    private TakePhotoHelper takePhotoHelper;
    private OrderDoubtAdapter adapter;
    private TextView orderDetail_tv_number;//订单编号
    private TextView orderDetail_arkName;//柜子位置名称
    private TextView orderDetail_remarke;//收货员备注
    private TextView orderDetail_tvTime;//订单时间
    private String order_id = null;
    private List<String> list_pic = new ArrayList<>();
    private ArrayList<String> list_choose = new ArrayList<>();
    private LinearLayout layout_doubt_parent;//添加疑问布局
    private ImagePost imagePost;
    private GridView gridView;
    private EditText doubtPop_etContent;
    private TextView top_submit_btn;
    private TextView tv_cancl;
    private boolean outside = true;//判断是点击外部还是pop内部
    private GetAgrement getAgrement;
    private GetAgrement getAgrement_PricingQuestion;//定价协议
    private GetAgrement getAgrement_OvertimeRule;//什么是超时费
    private TextView tv_isBusy;//是否加急
    private TextView tv_userRemarke;//点击备注
    private TextView tv_userRemarkContent;//提示备注
    private EditText et_userRemarkeContent;//备注输入框
    private LinearLayout layout_submitRemarke;//提交备注
    private TextView tv_submitRemark;//提交备注
    private NoScrollGridView grid_remarkPic;//商家备注图片
    private CornerGridAdapter grid_adapter;
    private ArrayList<String> list_gridPic = new ArrayList<>();
    private ArrayList<String> list_bitmap;
    private TextView tv_payOrder;
    private TextView tv_remark2;
    private TextView tv_payOrder2;//手工安全开箱
    private TextView orderDetail_state;
    private TextView orderDetail_tv_addPrice;//加急费用
    private LinearLayout layout_addPrice;
    public static Handler handler;
    /**
     * 支付弹框相关
     */
    private Type_choose dialog;//支付选择
    private Type_choose.Onclick onclick;
    private JSONObject jsonObject_order;
    private String additional;
    private boolean isModify = false;//是否是修改备注
    private LinearLayout layout_getGoodsCode;//取件码
    private TextView orderDetail_getGoodsCode;
    private String qrcode = "0";//判断属于什么发起支付的开箱 1二维码
    private PtrClassicFrameLayout pcf_container;
    private Boolean isUpdate = false;
    private TextView tv_sure_gps;
    private TextView tv_cancle_gps;
    private PopupWindow popupWindow_gps;
    private Handler mHandler;
    private boolean isFirst = true;
    /**
     * 定位
     *
     * @param savedInstanceState
     */
    private LocationClient mLocationClient;
    private BDLocation bdLoca;
    private LinearLayout layout_outTime;
    private TextView orderDetail_tv_outTimeprice;
    private ScrollView scrollView;
    private String payTyp_p;
    private String payOther_p;
    private JSONObject jsonObject_p;
    private String paypwd;
    /**
     * 支付密码框
     * @param savedInstanceState
     */
    private Pay_Dialog.Onclick onclick_pwd;//支付按钮回调接口
    private IntetnetState pay_state;//支付弹框
    private RelativeLayout layout_top;
    private TextView orderDetail_overtimeRule;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        initLocation();
        initView();
        getDoubtList();
        initFrameLayout();
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 1) {
                    dismissLoadingView2();
                } else if (msg.what == 2) {
                    try {
                        if (pcf_container != null && order_id != null) {
                            pcf_container.autoRefresh();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
    }

    private void initView() {
        backActivity();
        layout_top = (RelativeLayout) findViewById(R.id.layout_top);
        orderDetail_overtimeRule = (TextView) findViewById(R.id.orderDetail_overtimeRule);
        findViewById(R.id.top_submit_btn).setVisibility(View.GONE);
        findViewById(R.id.orderDetail_priceRule).setOnClickListener(this);
        orderDetail_state = (TextView) findViewById(R.id.orderDetail_state);
        findViewById(R.id.tv_doubt).setOnClickListener(this);
        tv_payOrder2 = (TextView) findViewById(R.id.tv_payOrder2);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        layout_outTime = (LinearLayout) findViewById(R.id.layout_outTime);
        orderDetail_tv_outTimeprice = (TextView) findViewById(R.id.orderDetail_tv_outTimeprice);
        orderDetail_tv_addPrice = (TextView) findViewById(R.id.orderDetail_tv_addPrice);
        layout_addPrice = (LinearLayout) findViewById(R.id.layout_addPrice);
        tv_userRemarke = (TextView) findViewById(R.id.tv_userRemarke);
        tv_remark2 = (TextView) findViewById(R.id.tv_remark2);
        tv_payOrder = (TextView) findViewById(R.id.tv_payOrder);
        orderDetail_getGoodsCode = (TextView) findViewById(R.id.orderDetail_getGoodsCode);
        layout_getGoodsCode = (LinearLayout) findViewById(R.id.layout_getGoodsCode);
        grid_remarkPic = (NoScrollGridView) findViewById(R.id.grid_remarkPic);
        tv_userRemarkContent = (TextView) findViewById(R.id.tv_userRemarkContent);
        et_userRemarkeContent = (EditText) findViewById(R.id.et_userRemarkeContent);
        layout_submitRemarke = (LinearLayout) findViewById(R.id.layout_submitRemarke);
        tv_submitRemark = (TextView) findViewById(R.id.tv_submitRemark);
        tv_payOrder2 = (TextView) findViewById(R.id.tv_payOrder2);
        tv_submitRemark.setOnClickListener(this);
        tv_userRemarke.setOnClickListener(this);
        tv_payOrder.setOnClickListener(this);
        tv_payOrder2.setOnClickListener(this);
        list_pic.add(null);
        tv_isBusy = (TextView) findViewById(R.id.tv_isBusy);
        orderDetail_tv_number = (TextView) findViewById(R.id.orderDetail_tv_number);
        orderDetail_arkName = (TextView) findViewById(R.id.orderDetail_arkName);
        orderDetail_remarke = (TextView) findViewById(R.id.orderDetail_remarke);
        orderDetail_tvTime = (TextView) findViewById(R.id.orderDetail_tvTime);
        layout_parent = (RelativeLayout) findViewById(R.id.layout_parent);
        view_zhezhao = findViewById(R.id.view_zhezhao);
        layout_addType = (LinearLayout) findViewById(R.id.layout_addType);
        orderDetail_tv_price = (TextView) findViewById(R.id.orderDetail_tv_price);
        layout_doubt_parent = (LinearLayout) findViewById(R.id.layout_doubt_parent);
        findViewById(R.id.orderDetail_priceDoubt).setOnClickListener(this);
        view_zhezhao.setOnClickListener(this);
        tv_isBusy.setOnClickListener(this);
        tv_isBusy.setClickable(false);
        orderDetail_tv_price.setText("");
        orderDetail_tv_price.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.x28));
        orderDetail_tv_price.setTextColor(ContextCompat.getColor(this, R.color.fontcolor_yellow));
        findViewById(R.id.orderDetail_orderDoubt).setOnClickListener(this);
        findViewById(R.id.orderDetail_priceDoubt).setOnClickListener(this);
        orderDetail_overtimeRule.setOnClickListener(this);
        takePhotoHelper = new TakePhotoHelper(null);
        if (getIntent().getBundleExtra("bundle") != null && getIntent().getBundleExtra("bundle").getInt("type") != -1) {
            type = getIntent().getBundleExtra("bundle").getInt("type");
        }
        if (getIntent().getBundleExtra("bundle") != null) {
            if (getIntent().getBundleExtra("bundle").getString("json_pay") != null) {
                try {
//                    toastMessage("有待支付");
                    if (getIntent().getBundleExtra("bundle").getString("status").equals("5")) {
                        if (!StringUtils.isEmpty(getIntent().getBundleExtra("bundle").getString("qrcode"))) {
                            qrcode = getIntent().getBundleExtra("bundle").getString("qrcode");
                        }
                        showGetData(new JSONObject(getIntent().getBundleExtra("bundle").getString("json_pay")));
                    } else if (getIntent().getBundleExtra("bundle").getString("status").equals("6")) {
                        tv_payOrder2.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!StringUtils.isEmpty(getIntent().getBundleExtra("bundle").getString("additional"))) {
            tv_isBusy.setText(getIntent().getBundleExtra("bundle").getString("additional"));
            try {
                Log.d("orderDetail3", getIntent().getBundleExtra("bundle").getString("additional"));
                if (!StringUtils.isEmpty(getIntent().getBundleExtra("bundle").getString("additional"))) {
                    //处于加急状态
                    tv_isBusy.setText(MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("additional").getJSONArray(Integer.parseInt(getIntent().getBundleExtra("bundle").getString("additional"))).getString(0));
                    tv_isBusy.setTextColor(Color.WHITE);
                    GradientDrawable background = (GradientDrawable) tv_isBusy.getBackground();
                    background.setColor(Color.parseColor(MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("additional").getJSONArray(Integer.parseInt(getIntent().getBundleExtra("bundle").getString("additional"))).getString(1)));
                    // tv_isBusy.setBackground(ContextCompat.getDrawable(context,R.drawable.home_rect_sloid_state2));
                } else {
                    tv_isBusy.setText("普通");
                    tv_isBusy.setTextColor(ContextCompat.getColor(this, R.color.fontcolor_f9));
                    GradientDrawable background = (GradientDrawable) tv_isBusy.getBackground();
                    background.setColor(Color.parseColor(MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("additional").getJSONArray(Integer.parseInt(getIntent().getBundleExtra("bundle").getString("additional"))).getString(1)));
                }
                additional = getIntent().getBundleExtra("bundle").getString("additional");
            } catch (Exception e) {

            }

        }
        if (!StringUtils.isEmpty(getIntent().getBundleExtra("bundle").getString("ark_name"))) {
            orderDetail_arkName.setText(getIntent().getBundleExtra("bundle").getString("ark_name"));
        }
        if (!StringUtils.isEmpty(getIntent().getBundleExtra("bundle").getString("order_id"))) {
            order_id = getIntent().getBundleExtra("bundle").getString("order_id");
          //  getOrderDetail(order_id);
        } else {
            toastMessage("订单错误，请前往订单列表查看");
        }

    }

    private void initFrameLayout() {
        pcf_container = ((PtrClassicFrameLayout) findViewById(R.id.pcf_container));
        MaterialHeader materialHeader = new MaterialHeader(this);
        materialHeader.setPadding(0, 40, 0, 40);
        pcf_container.addPtrUIHandler(materialHeader);
        pcf_container.setHeaderView(materialHeader);
        // pcf_container.setHeaderView(view_head);
        pcf_container.setFooterView(new DefaultLoadMoreViewFooter_normal());
        pcf_container.setLoadMoreEnable(false);
        pcf_container.setOnLoadMoreListener(this);
        pcf_container.setPtrHandler(this);
        pcf_container.autoRefresh();
        if (!TimeUtils.isOPen(this)) {
            show_gps();
        }
        onclick_pwd = new Pay_Dialog.Onclick() {
            @Override
            public void OnClickLisener(View v) {
                try {
                    paypwd = pay_state.getPay_dialog().password();
                    createPayOrder(payTyp_p, payOther_p, jsonObject_p, v);
                   // buyCombo(payType,payOther,pay_state.getPay_dialog().password());
                    // requestWithdraw(bank_id, MyApplication.getUserJson().getLong("money") + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(this);
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true);                                //打开gps
            option.setCoorType("bd09ll");                           //设置坐标类型为bd09ll 百度需要的坐标，也可以返回其他type类型，大家可以查看下
            option.setPriority(LocationClientOption.NetWorkFirst);
            option.setScanSpan(60000);//定时定位，每隔60秒钟定位一次。
            mLocationClient.setLocOption(option);
            mLocationClient.start();
        }//设置网络优先
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (bdLocation == null)
                    return;
                else {
                    bdLoca = bdLocation;
                    Log.d("bdLoca", bdLocation.getLatitude() + "," + bdLocation.getLongitude() + "");
                }
                //这里可以获取经纬度，这是回调方法哦，怎么执行大家可以查看资料，


            }

        });
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(pcf_container, scrollView, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        isUpdate = true;
        getOrderDetail(order_id);
    }

    @Override
    public void loadMore() {

    }


    /**
     * 根据状态类型动态添加相关数据布局
     */
    private void addView_typeOne() {
        View view;
        //等待收货员取货状态
        view = LayoutInflater.from(this).inflate(R.layout.order_detail_type1, null);
        TextView orderDetail_state = (TextView) view.findViewById(R.id.orderDetail_state);
        try {
            if (!StringUtils.isEmpty(MyApplication.getConfigrationJson().getJSONObject("xorderstatus").getString(type + ""))) {
                orderDetail_state.setText(MyApplication.getConfigrationJson().getJSONObject("xorderstatus").getString(type + ""));
                orderDetail_state.setTextColor(Color.parseColor(MyApplication.getConfigrationJson().getJSONObject("other").getJSONObject("xieyiorder_color").getString(type + "")));
            } else {
                orderDetail_state.setText("获取状态中");
                orderDetail_state.setTextColor(ContextCompat.getColor(this, R.color.fontcolor_f9));
            }
        } catch (Exception e) {

        }
        layout_addType.addView(view);
    }

    private void addView_typeTwo(JSONObject jsonObject) {
        View view;
        //正在洗衣
        view = LayoutInflater.from(this).inflate(R.layout.order_detail_type2, null);
        TextView orderDetail_state = (TextView) view.findViewById(R.id.orderDetail_state);
        TextView tv_time = (TextView) view.findViewById(R.id.tv_time);
        try {
            if (!StringUtils.isEmpty(jsonObject.getString("take_time"))) {
                tv_time.setText(TimeUtilsDate.timedate2(jsonObject.getLong("take_time")));
            } else {
                tv_time.setText("未填写");
            }

            if (!StringUtils.isEmpty(MyApplication.getConfigrationJson().getJSONObject("xorderstatus").getString(type + ""))) {
                orderDetail_state.setText(MyApplication.getConfigrationJson().getJSONObject("xorderstatus").getString(type + ""));
                orderDetail_state.setTextColor(Color.parseColor(MyApplication.getConfigrationJson().getJSONObject("other").getJSONObject("xieyiorder_color").getString(type + "")));
            } else {
                orderDetail_state.setText("获取状态中");
                orderDetail_state.setTextColor(ContextCompat.getColor(this, R.color.fontcolor_f9));
            }
        } catch (Exception e) {

        }
        layout_addType.addView(view);
    }

    private void addView_typeThree(JSONObject jsonObject) {
        View view;
        //可领取衣物
        view = LayoutInflater.from(this).inflate(R.layout.order_detail_type3, null);
        TextView orderDetail_state = (TextView) view.findViewById(R.id.orderDetail_state);
        TextView tv_time = (TextView) view.findViewById(R.id.tv_time);
        TextView tv_time_into = (TextView) view.findViewById(R.id.tv_time_into);
        try {
            if (!StringUtils.isEmpty(jsonObject.getString("take_time"))) {
                tv_time.setText(TimeUtilsDate.timedate2(jsonObject.getLong("take_time")));
            } else {
                tv_time.setText("未填写");
            }
            if (!StringUtils.isEmpty(jsonObject.getString("into_time"))) {
                tv_time_into.setText(TimeUtilsDate.timedate2(jsonObject.getLong("into_time")));
            } else {
                tv_time_into.setText("未填写");
            }
            if (!StringUtils.isEmpty(MyApplication.getConfigrationJson().getJSONObject("xorderstatus").getString(type + ""))) {
                orderDetail_state.setText(MyApplication.getConfigrationJson().getJSONObject("xorderstatus").getString(type + ""));
                orderDetail_state.setTextColor(Color.parseColor(MyApplication.getConfigrationJson().getJSONObject("other").getJSONObject("xieyiorder_color").getString(type + "")));
            } else {
                orderDetail_state.setText("获取状态中");
                orderDetail_state.setTextColor(ContextCompat.getColor(this, R.color.fontcolor_f9));
            }
        } catch (Exception e) {

        }
        layout_addType.addView(view);
    }

    private void addView_typeFour(JSONObject jsonObject) {
        View view;
        //已结束
        view = LayoutInflater.from(this).inflate(R.layout.order_detail_type4, null);
        TextView orderDetail_state = (TextView) view.findViewById(R.id.orderDetail_state);
        LinearLayout layout_getGoods = (LinearLayout) view.findViewById(R.id.layout_getGoods);
        TextView tv_time_IntoCargo = (TextView) view.findViewById(R.id.tv_time_IntoCargo);//入货
        TextView tv_time = (TextView) view.findViewById(R.id.tv_time_get);//收货
        TextView tv_time_into = (TextView) view.findViewById(R.id.tv_time_into);//下单
        TextView tv_time_complete = (TextView) view.findViewById(R.id.tv_time_complete);//完成
        try {
//            if(!StringUtils.isEmpty(jsonObject.getString("take_time"))){
//                tv_time.setText(TimeUtilsDate.timedate2(jsonObject.getLong("take_time")));
//            }else{
//                tv_time.setText("未填写");
//            }
            if (!StringUtils.isEmpty(jsonObject.getString("addtime")) && jsonObject.getLong("addtime") != 0) {
                //下单时间
                tv_time_into.setText(TimeUtilsDate.timedateMill(jsonObject.getLong("addtime")));
            } else {
                tv_time_into.setText("未填写");
            }
            if (!StringUtils.isEmpty(jsonObject.getString("take_time")) && jsonObject.getLong("take_time") != 0) {
                //收货时间
                tv_time.setText(TimeUtilsDate.timedateMill(jsonObject.getLong("take_time")));
            } else {
                tv_time.setText("待收货");
            }
            if (!StringUtils.isEmpty(jsonObject.getString("into_time")) && jsonObject.getLong("into_time") != 0) {
                //入货时间
                tv_time_IntoCargo.setText(TimeUtilsDate.timedateMill(jsonObject.getLong("into_time")));
            } else {
                tv_time_IntoCargo.setText("待清洗");
            }
            if (!StringUtils.isEmpty(jsonObject.getString("status_time")) && jsonObject.getLong("status_time") != 0) {
                //完成时间
                tv_time_complete.setText(TimeUtilsDate.timedateMill(jsonObject.getLong("status_time")));
            } else {
                tv_time_complete.setText("待支付");
            }
//            if(!StringUtils.isEmpty(jsonObject.getString("take_time"))){
//                tv_time.setText(TimeUtilsDate.timedate2(jsonObject.getLong("take_time")));
//            }else{
//                tv_time.setText("未填写");
//            }

//            if (!StringUtils.isEmpty(MyApplication.getConfigrationJson().getJSONObject("xorderstatus").getString(type + ""))) {
//                orderDetail_state.setText(MyApplication.getConfigrationJson().getJSONObject("xorderstatus").getString(type + ""));
//                orderDetail_state.setTextColor(Color.parseColor(MyApplication.getConfigrationJson().getJSONObject("other").getJSONObject("xieyiorder_color").getString(type + "")));
//            } else {
//                orderDetail_state.setText("获取状态中");
//                orderDetail_state.setTextColor(ContextCompat.getColor(this, R.color.fontcolor_f9));
//            }
            if (!StringUtils.isEmpty(jsonObject.getString("utake_time")) && jsonObject.getLong("utake_time") != 0) {
                layout_getGoods.setVisibility(View.VISIBLE);
                orderDetail_state.setText(TimeUtilsDate.timedateMill(jsonObject.getLong("utake_time")));
            } else {
                layout_getGoods.setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }
        if (layout_addType.getChildCount() == 1) {
            layout_addType.removeAllViews();
        }
        layout_addType.addView(view);
    }

    private void showPopwindow() {
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = (int) (getResources().getDisplayMetrics().heightPixels / 2.2);
//        int height = (int) (getResources().getDisplayMetrics().heightPixels-getResources().getDimension(R.dimen.x152));
        if (popWindow == null) {
            View popView = View.inflate(this, R.layout.layout_order_doubt_pop, null);
            gridView = (GridView) popView.findViewById(R.id.doubtPop_grid);
            top_submit_btn = (TextView) popView.findViewById(R.id.top_submit_btn);
            tv_cancl = (TextView) popView.findViewById(R.id.tv_cancl);
            layout_pop_parent = (RelativeLayout) popView.findViewById(R.id.layout_pop_parent);
            doubtPop_etContent = (EditText) popView.findViewById(R.id.doubtPop_etContent);
            popWindow = new PopupWindow(popView, width, height);
            popWindow.setAnimationStyle(R.style.popwin_anim_style);
            top_submit_btn.setOnClickListener(this);
            popWindow.setFocusable(true);
            popWindow.setOutsideTouchable(false);
            ColorDrawable dw = new ColorDrawable(0x00000000);
            popWindow.setBackgroundDrawable(dw);
            // initPopLinsterner();
        }
        adapter = new OrderDoubtAdapter(list_pic, this, (int) ((width - getResources().getDimension(R.dimen.x92)) / 4));
        gridView.setAdapter(adapter);
        tv_cancl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list_pic.get(position) == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            takePhotoHelper.PickMultiplePhoto(getTakePhoto(), 20, false);
                        }
                    });
                } else {
                    startActivity_ImagrPager(OrderDetailActivity.this, position, list_choose, true);
                }
            }
        });
        layout_pop_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outside = false;//内部
                popWindow.dismiss();
            }
        });
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //  DisplayUtil.backgroundAlpha(1.0f, OrderDetailActivity.this);
                // top_submit_btn.setVisibility(View.GONE);
                view_zhezhao.setVisibility(View.GONE);

            }
        });
        // DisplayUtil.backgroundAlpha(0.2f, OrderDetailActivity.this);
        popWindow.showAtLocation(layout_parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        view_zhezhao.setVisibility(View.VISIBLE);

    }

    private void initPopLinsterner() {
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE && popWindow.isFocusable()) {
                    toastMessage(event.getX() + "," + event.getY());
                    toastMessage(top_submit_btn.getX() + "," + top_submit_btn.getY());
                    //如果焦点不在popupWindow上，且点击了外面，不再往下dispatch事件：
                    //不做任何响应,不 dismiss popupWindow
                    return true;
                }
                //否则default，往下dispatch事件:关掉popupWindow，
                toastMessage(event.getX() + "," + event.getY());
                toastMessage(top_submit_btn.getX() + "," + top_submit_btn.getY());
                return false;
            }
        });
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        final List<TImage> images = result.getImages();
        //  List<File> list = new ArrayList<>();
        if (list_pic == null) {
            list_pic = new ArrayList<>();
        }
        list_pic.remove(null);
        for (int i = 0; i < images.size(); i++) {
            // list.add(new File(images.get(i).getCompressPath()));
            list_pic.add(images.get(i).getCompressPath());
            list_choose.add(images.get(i).getCompressPath());
        }
        list_pic.add(null);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });


    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.orderDetail_orderDoubt:
                //对订单有疑问
                showPopwindow();
                break;
            case R.id.orderDetail_priceRule:
                //定价规则 PricingRules rules_ann
                if (getAgrement == null) {
                    getAgrement = new GetAgrement(this);
                }
                getAgrement.getAggrement_Detail(null, "PricingRules", "知道了");
                break;
            case R.id.orderDetail_overtimeRule:
                //超时费
                if(getAgrement_OvertimeRule == null){
                    getAgrement_OvertimeRule = new GetAgrement(this);
                }
                getAgrement_OvertimeRule.getAggrement_Detail(null,"TimeOutMoney","知道了");
                break;
            case R.id.orderDetail_priceDoubt:
                //对定价有疑问
                //showPopwindow();
                if (getAgrement_PricingQuestion == null) {
                    getAgrement_PricingQuestion = new GetAgrement(this);
                }
                getAgrement_PricingQuestion.getAggrement_Detail(null, "rules_ann", "知道了");
                break;
            case R.id.top_submit_btn:
                //提交
                if (doubtPop_etContent.getText().toString().isEmpty()) {
                    toastMessage("请输入内容");
                } else {

                    if (list_choose.size() == 0) {
                        postDoubt(doubtPop_etContent.getText().toString(), null);
                    } else {
                        if (list_choose.size() == 1) {
                            postImage(ImageBase64.imageToBase64(list_choose.get(0)), null);

                        } else {
                            JSONArray jsonArray = new JSONArray();
                            for (int i = 0; i < list_choose.size(); i++) {
                                jsonArray.put(ImageBase64.imageToBase64(list_choose.get(i)));
                            }
                            postImage("", jsonArray);
                        }
                    }

                }
                break;
            case R.id.view_zhezhao:
                if (popWindow != null && popWindow.isShowing()) {
                    popWindow.dismiss();
                }
                break;
            case R.id.tv_submitRemark:
                //提交用户备注
                if (StringUtils.isEmpty(et_userRemarkeContent.getText().toString())) {
                    toastMessage("请输入备注内容");
                } else {
                    postRemark(et_userRemarkeContent.getText().toString());
                }
                break;
            case R.id.tv_userRemarke:
                //展开备注输入框
                if (tv_userRemarke.getText().toString().contains("修改备注")) {
                    et_userRemarkeContent.setBackground(ContextCompat.getDrawable(this, R.drawable.rect_fangf2));
                } else {
                    if (et_userRemarkeContent.getVisibility() == View.GONE) {
                        et_userRemarkeContent.setVisibility(View.VISIBLE);
                        if (isModify) {
                            tv_userRemarke.setText("[ 修改备注 ]");
                        } else {
                            tv_userRemarke.setText("[ 收起备注 ]");
                        }
                        layout_submitRemarke.setVisibility(View.VISIBLE);
                    } else {
                        et_userRemarkeContent.setVisibility(View.GONE);
                        tv_userRemarke.setText("[ 展开备注 ]");
                        layout_submitRemarke.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.tv_doubt:
                //发表疑问
                showPopwindow();
                break;
            case R.id.tv_isBusy:
                //加急，取消加急
                tv_isBusy.setClickable(false);
                if (additional.equals("0")) {
                    addItional(1 + "");
                } else {
                    addItional(0 + "");
                }
                break;
            case R.id.tv_payOrder:
                //支付
                showGetData(jsonObject_order);
                break;
            case R.id.tv_payOrder2:
                //支付
//                tv_payOrder2.setVisibility(View.VISIBLE);
//                tv_payOrder.setVisibility(View.GONE);
                try {
                    jsonObject_order.put("ark_id", jsonObject_order.getString("ark_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SocketMessage_getBox(jsonObject_order);
//                showGetData(jsonObject_order);
                break;
        }
    }

    /**
     * 上传图片
     */
    private void postImage(String pics, JSONArray pics2) {
        if (imagePost == null) {
            imagePost = new ImagePost(new ImagePost.Result() {
                @Override
                public void success(JSONObject jsonObject) {
                    try {
                        if (jsonObject.getInt("status") != 0) {
                            toastMessage(jsonObject.getString("msg"));
                        } else {
                            postDoubt(doubtPop_etContent.getText().toString(), jsonObject.get("url"));
                        }

                    } catch (Exception e) {

                    }

                    dismissLoadingView();
                }

                @Override
                public void failure(String msg) {
                    dismissLoadingView();
                }
            }, this);
        }
        imagePost.psotDoubt(pics, pics2);
        showLoadingView();
    }

    /**
     * 获取洗衣订单详情
     */
    private void getOrderDetail(String order_id) {
        if (!isUpdate) {
            showLoadingView();
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid", MyApplication.getUid());
            jsonObject.put("username", MyApplication.getUserJson().getString("m_username"));
            jsonObject.put("pwd", MyApplication.getPasswprd());
            jsonObject.put("order_id", order_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.LAUNDY_ORDER_DETAIL, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    pcf_container.refreshComplete();
                    Log.d("orderDetail5", response.toString());
                    tv_payOrder2.setVisibility(View.GONE);
                    tv_userRemarke.setClickable(true);
                    tv_userRemarkContent.setVisibility(View.VISIBLE);
                    if (response.getInt("status") != 0) {
                        toastMessage(response.getString("msg"));
                    } else {
                        if (response.getJSONObject("data") == null) {

                        } else {
                            jsonObject_order = response.getJSONObject("data");

                            StringBuilder stringBuilder = new StringBuilder();
                            if (response.getJSONObject("data").getJSONArray("goods") != null && response.getJSONObject("data").getJSONArray("goods").length() > 0) {
                                for (int i = 0; i < response.getJSONObject("data").getJSONArray("goods").length(); i++) {
                                    stringBuilder.append(response.getJSONObject("data").getJSONArray("goods").getJSONObject(i).getString("title") +
                                            "(" + response.getJSONObject("data").getJSONArray("goods").getJSONObject(i).getString("num") + ")<b>￥" +
                                            "" + response.getJSONObject("data").getJSONArray("goods").getJSONObject(i).getLong("money") / 1000 + "</b>，");
                                }
                            }

                            if (StringUtils.isEmpty(stringBuilder.toString()) && StringUtils.isEmpty(response.getJSONObject("data").getString("shop_remark"))) {
                                orderDetail_remarke.setText("待商家备注和定价");
                                orderDetail_remarke.setTextColor(ContextCompat.getColor(OrderDetailActivity.this, R.color.fontcolor_d8));
                            } else {
                                orderDetail_remarke.setText(Html.fromHtml(stringBuilder.toString() + response.getJSONObject("data").getString("shop_remark")));
                                orderDetail_remarke.setTextColor(ContextCompat.getColor(OrderDetailActivity.this, R.color.fontcolor_yellow));
                            }
                            orderDetail_tv_price.setText(response.getJSONObject("data").getLong("money") / 1000 + "");
                            orderDetail_tvTime.setText(TimeUtilsDate.timedate2(response.getJSONObject("data").getLong("addtime")));
                            orderDetail_arkName.setText(response.getJSONObject("data").getString("ark_name") + " " + response.getJSONObject("data").getString("box_number") + "号箱");
                            orderDetail_tv_number.setText(response.getJSONObject("data").getString("number"));
                            type = response.getJSONObject("data").getInt("status");
                            if (!StringUtils.isEmpty(MyApplication.getConfigrationJson().getJSONObject("xorderstatus").getString(type + ""))) {
                                orderDetail_state.setText(MyApplication.getConfigrationJson().getJSONObject("xorderstatus").getString(type + ""));
                                orderDetail_state.setTextColor(Color.parseColor(MyApplication.getConfigrationJson().getJSONObject("other").getJSONObject("xieyiorder_color").getString(type + "")));
                            } else {
                                orderDetail_state.setText("获取状态中");
                                orderDetail_state.setTextColor(ContextCompat.getColor(OrderDetailActivity.this, R.color.fontcolor_f9));
                            }
                            addView_typeFour(response.getJSONObject("data"));

                        }
                    }
                    if (response.getJSONObject("data").getString("status").equals("6") && response.getJSONObject("data").getString("open_box").equals("1") && !StringUtils.isEmpty(response.getJSONObject("data").getString("rand_number"))) {
                        layout_getGoodsCode.setVisibility(View.VISIBLE);
                        orderDetail_getGoodsCode.setText(response.getJSONObject("data").getString("rand_number"));
                        orderDetail_getGoodsCode.setTextColor(ContextCompat.getColor(OrderDetailActivity.this, R.color.fontcolor_yellow));
                    } else {
                        if (response.getJSONObject("data").getString("status").equals("6") && response.getJSONObject("data").getString("open_box").equals("2")) {
                            layout_getGoodsCode.setVisibility(View.VISIBLE);
                            orderDetail_getGoodsCode.setText("已取件");
                            orderDetail_getGoodsCode.setTextColor(Color.parseColor("#3686f7"));
                        } else {
                            layout_getGoodsCode.setVisibility(View.GONE);
                        }
                    }
                    if (response.getJSONObject("data").getInt("status") == 0 || response.getJSONObject("data").getInt("status") == 1) {
                        //可以编辑备注
                        if (!StringUtils.isEmpty(response.getJSONObject("data").getString("remark")) && !response.getJSONObject("data").getString("remark").equals("null")) {
                            et_userRemarkeContent.setText(response.getJSONObject("data").getString("remark"));
                            et_userRemarkeContent.setSelection(response.getJSONObject("data").getString("remark").length());
                            layout_submitRemarke.setVisibility(View.VISIBLE);
                        } else {
                            et_userRemarkeContent.setText("本次放入 件物品");
                            et_userRemarkeContent.setSelection(et_userRemarkeContent.getText().toString().length());
                            layout_submitRemarke.setVisibility(View.GONE);
                            et_userRemarkeContent.setVisibility(View.GONE);
                        }
                        editTextable(et_userRemarkeContent, true);
                    } else {
                        tv_userRemarke.setVisibility(View.GONE);
                        tv_userRemarkContent.setVisibility(View.GONE);
                        tv_remark2.setVisibility(View.VISIBLE);
                        //不可以编辑备注
                        if (!StringUtils.isEmpty(response.getJSONObject("data").getString("remark")) && !response.getJSONObject("data").getString("remark").equals("null")) {
                            //有用户备注内容
                            et_userRemarkeContent.setText(response.getJSONObject("data").getString("remark"));
                        } else {
                            //暂无备注内容
                            et_userRemarkeContent.setText("未备注");
                        }
                        layout_submitRemarke.setVisibility(View.GONE);
                        editTextable(et_userRemarkeContent, false);
                    }
                    if (response.getJSONObject("data").getInt("status") == 6) {
                        //已完成
                        tv_userRemarke.setClickable(false);
                        tv_userRemarkContent.setVisibility(View.GONE);
                        if (!StringUtils.isEmpty(response.getJSONObject("data").getString("remark")) && !response.getJSONObject("data").getString("remark").equals("null")) {
                            //有用户备注内容
                            tv_userRemarke.setText("以下是你下单时的备注");
                        } else {
                            //暂无备注内容
                            tv_userRemarke.setText("当前订单你没有填写备注");
                        }
                        tv_userRemarke.setText("[ 以下是你下单时的备注 ]");
                        tv_userRemarkContent.setVisibility(View.GONE);
                    }
                    additional = response.getJSONObject("data").getString("additional");
                    tv_isBusy.setClickable(true);
                    if (response.getJSONObject("data").getInt("status") >= 0 && response.getJSONObject("data").getInt("status") < 2 &&
                            additional.equals("0")) {
                        tv_isBusy.setVisibility(View.GONE);
                        layout_addPrice.setVisibility(View.GONE);
                        tv_isBusy.setText("点击加急");
                        tv_isBusy.setTextColor(Color.parseColor("#999999"));
                        GradientDrawable background = (GradientDrawable) tv_isBusy.getBackground();
                        background.setColor(ContextCompat.getColor(OrderDetailActivity.this, R.color.fontcolor_e0));
                        tv_isBusy.setClickable(true);
//                        tv_isBusy.setBackground(ContextCompat.getDrawable(OrderDetailActivity.this,R.drawable.home_rect_sloid_state));
                        //显示可加急
                    } else if (additional.equals("1")) {
                        //处于加急状态
                        tv_isBusy.setVisibility(View.GONE);
                        layout_addPrice.setVisibility(View.VISIBLE);
                        orderDetail_tv_addPrice.setText(response.getJSONObject("data").getLong("addmoney") / 1000 + "");
                        tv_isBusy.setText("已加急");
                        tv_isBusy.setClickable(true);
                        tv_isBusy.setTextColor(Color.parseColor("#ffffff"));
                        GradientDrawable background = (GradientDrawable) tv_isBusy.getBackground();
                        background.setColor(ContextCompat.getColor(OrderDetailActivity.this, R.color.fontcolor_d8));
//                    GradientDrawable background = (GradientDrawable) holder2.myorder_tv_state.getBackground();
//                    background.setColor(Color.parseColor("#00000000"));
                        // holder1.myorder_tv_state.setBackground(ContextCompat.getDrawable(context,R.drawable.home_rect_sloid_state2));
                    } else {
                        //不显示
                        if (response.getJSONObject("data").getInt("status") == 6) {
                            tv_isBusy.setVisibility(View.GONE);
                            tv_isBusy.setClickable(false);
                        } else {
                            tv_isBusy.setVisibility(View.GONE);
                        }
                    }
                    if (response.getJSONObject("data").getInt("status") == 5) {
                        //已入柜待收货
                        tv_payOrder.setVisibility(View.VISIBLE);
                    } else {
                        tv_payOrder.setVisibility(View.GONE);
                    }
                    if (response.getJSONObject("data").getString("status").equals("6") && response.getJSONObject("data").getString("open_box").equals("1")) {
                        tv_payOrder2.setVisibility(View.VISIBLE);
                    } else {
                        tv_payOrder2.setVisibility(View.GONE);
                    }
                    jsonObject_order.put("box_id", jsonObject_order.getString("ark_box_id"));
                    jsonObject_order.put("box_number", jsonObject_order.getString("ark_box_id"));
                    if (getIntent().getBundleExtra("bundle").getBoolean("isPay", false)) {
                        //扫码进入
                        if (getIntent().getBundleExtra("bundle").getString("status").equals("6")) {
                            //调用直接开箱接口
                            tv_payOrder2.setVisibility(View.VISIBLE);
                            tv_payOrder.setVisibility(View.GONE);
                            jsonObject_order.put("ark_id", jsonObject_order.getString("ark_id"));
                            SocketMessage_getBox(jsonObject_order);
                        } else if (getIntent().getBundleExtra("bundle").getString("status").equals("5")) {
                            //调起支付
                            showGetData(jsonObject_order);
                        }

                    }
                    if (response.getJSONObject("data").getLong("timeoutmoney") != 0&&response.getJSONObject("data").getInt("status") == 5) {
                        layout_outTime.setVisibility(View.VISIBLE);
                        orderDetail_tv_outTimeprice.setText(response.getJSONObject("data").getLong("timeoutmoney") / 1000 + "");
                    } else {
                        layout_outTime.setVisibility(View.GONE);
                        orderDetail_tv_outTimeprice.setText("");
                    }
                     list_gridPic.clear();
                    if(list_bitmap!=null) {
                        list_bitmap.clear();
                    }
                    if((response.getJSONObject("data").get("pics") instanceof JSONArray)&&(response.getJSONObject("data").get("videos") instanceof JSONArray)){
                        //图片视屏都有
                        grid_remarkPic.setVisibility(View.VISIBLE);
                        for (int i = 0; i < response.getJSONObject("data").getJSONArray("pics").length(); i++) {
                            list_gridPic.add(response.getJSONObject("data").getJSONArray("pics").getString(i));
                        }
                        if(list_bitmap == null){
                            list_bitmap = new ArrayList<String>();
                        }
                        for (int i = 0; i < response.getJSONObject("data").getJSONArray("videos").length(); i++) {
                            list_bitmap.add(response.getJSONObject("data").getJSONArray("videos").getString(i));
                        }
                        grid_adapter = new CornerGridAdapter(list_gridPic, OrderDetailActivity.this, list_bitmap);
                        grid_remarkPic.setAdapter(grid_adapter);
                    }else if(response.getJSONObject("data").get("pics") instanceof JSONArray){
                        //图片有
                        grid_remarkPic.setVisibility(View.VISIBLE);
                        for (int i = 0; i < response.getJSONObject("data").getJSONArray("pics").length(); i++) {
                            list_gridPic.add(response.getJSONObject("data").getJSONArray("pics").getString(i));
                        }
                        grid_adapter = new CornerGridAdapter(list_gridPic, OrderDetailActivity.this, list_bitmap);
                        grid_remarkPic.setAdapter(grid_adapter);
                    }else if(response.getJSONObject("data").get("videos") instanceof JSONArray){
                        //视屏有
                        grid_remarkPic.setVisibility(View.VISIBLE);
                        if(list_bitmap == null){
                            list_bitmap = new ArrayList<String>();
                        }
                        for (int i = 0; i < response.getJSONObject("data").getJSONArray("videos").length(); i++) {
                            list_bitmap.add(response.getJSONObject("data").getJSONArray("videos").getString(i));
                        }
                        grid_adapter = new CornerGridAdapter(list_gridPic, OrderDetailActivity.this, list_bitmap);
                        grid_remarkPic.setAdapter(grid_adapter);
                    }else{
                        //都没有
                        grid_remarkPic.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //图片是空 判断视屏
//                    try {
//                        if (response.getJSONObject("data").getJSONArray("videos") != null) {
//                            for (int i = 0; i < response.getJSONObject("data").getJSONArray("videos").length(); i++) {
//                                if (list_bitmap == null) {
//                                    list_bitmap = new ArrayList<VideoModel>();
//                                }
//                                list_bitmap.add(new VideoModel(CreateVideoThumbnail.createVideoThumbnail(RequestTag.BaseVieoUrl + response.getJSONObject("data").getJSONArray("videos").getString(i), MediaStore.Images.Thumbnails.MINI_KIND), response.getJSONObject("data").getJSONArray("videos").getString(i)));
//
//                            }
//
//                        }
//                        adapter.notifyDataSetChanged();
//                    } catch (Exception e2) {
//
//                    }
                }
                dismissLoadingView();

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                pcf_container.refreshComplete();
                dismissLoadingView();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFirst && !TimeUtils.isOPen(this)) {
            isFirst = false;
            show_gps();
        }
        isFirst = false;
    }

    //设置EditText可输入和不可输入状态
    private void editTextable(EditText editText, boolean editable) {
        if (!editable) { // disable editing password
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
            editText.setClickable(false); // user navigates with wheel and selects widget
        } else { // enable editing of password
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.setClickable(true);
        }
    }

    /**
     * 疑问订单列表
     */
    private void getDoubtList() {
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
            jsonObject.put("page", "1");
            jsonObject.put("pagesize", MyApplication.getPageSize() + "");
            jsonObject.put("order_id", order_id);
            // jsonObject.put("id","");//当传入ID时表示取当前这条评价，但不能取子评价
            jsonObject.put("username", MyApplication.getUserJson().getString("m_username"));
            jsonObject.put("pwd", MyApplication.getPasswprd());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.LAUNDY_ORDER_DOUBT, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    Log.d("response", response.toString());
                    if (response.getInt("status") != 0) {
                        toastMessage(response.getString("msg"));
                    } else {
                        if (response.getJSONArray("data") == null) {
                        } else {
                            for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                                addDoubtView(response.getJSONArray("data").getJSONObject(i));
                            }

                        }
                    }

                } catch (Exception e) {

                }
                dismissLoadingView();

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dismissLoadingView();
            }
        });
    }

    /**
     * 动态添加疑问数据
     */
    private void addDoubtView(final JSONObject jsonObject) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_laundry_detail, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.height = (int) (DisplayUtil.getScreenHeight(this) / 11.4);
        TextView tv_doubt_state = (TextView) view.findViewById(R.id.tv_doubt_state);
        TextView tv_doubt_content = (TextView) view.findViewById(R.id.tv_doubt_content);
        TextView tv_doubt_file = (TextView) view.findViewById(R.id.tv_doubt_file);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              sta
                Bundle bundle = new Bundle();
                bundle.putString("order_id", order_id);
                try {
                    bundle.putString("parent_id", jsonObject.getString("id"));
                    bundle.putString("json", jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity_Bundle(OrderDetailActivity.this, FeedBackDetailActivity2.class, bundle);
            }
        };
        view.setOnClickListener(onClickListener);
        try {
            if (!StringUtils.isEmpty(jsonObject.getString("content"))) {
                tv_doubt_content.setText(jsonObject.getString("content"));
            } else {
                tv_doubt_content.setText("未编写");
            }
            if (!StringUtils.isEmpty(jsonObject.getString("pics"))&&!jsonObject.getString("pics").equals("null")) {
                tv_doubt_file.setVisibility(View.VISIBLE);
                tv_doubt_file.setText("[附件]");
            } else {
                if(!StringUtils.isEmpty(jsonObject.getString("videos"))&&!jsonObject.getString("videos").equals("null")){
                    tv_doubt_file.setVisibility(View.VISIBLE);
                    tv_doubt_file.setText("[附件]");
                }else {
                    tv_doubt_file.setVisibility(View.GONE);
                }
            }
            if (!StringUtils.isEmpty(jsonObject.getString("refstatus"))) {
                if (!StringUtils.isEmpty(jsonObject.getString("refstatus")) && !jsonObject.getString("refstatus").equals("0")) {
                    tv_doubt_state.setText("[已回复]");
                    tv_doubt_state.setTextColor(Color.parseColor("#0C9C6D"));
                } else {
                    tv_doubt_state.setText("[未回复]");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        view.setLayoutParams(params);
        layout_doubt_parent.addView(view);
    }

    /**
     * 提交疑问
     */
    private void postDoubt(String content, Object pics) {
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
        }
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
            jsonObject.put("username", MyApplication.getUserJson().getString("m_username"));
            jsonObject.put("pwd", MyApplication.getPasswprd());
            jsonObject.put("order_id", order_id);
            if (pics != null) {
                jsonObject.put("pics", pics);
            }
            jsonObject.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.POST_DOUBT, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if (response.getInt("status") != 0) {
                        toastMessage(response.getString("msg"));
                    } else {
                        if (response.getJSONObject("data") == null) {
                            dismissLoadingView();
                        } else {
                            toastMessage(response.getString("msg"));
                            layout_doubt_parent.removeAllViews();
                            getDoubtList();
                        }
                    }

                } catch (Exception e) {
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
     * 提交备注
     */
    private void postRemark(String content) {
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
        }
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
            jsonObject.put("username", MyApplication.getUserJson().getString("m_username"));
            jsonObject.put("pwd", MyApplication.getPasswprd());
            jsonObject.put("order_id", order_id);
            jsonObject.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.USER_REMARK_ORDER, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    toastMessage(response.getString("msg"));
                    if (response.getInt("status") == 0) {
                        isModify = true;
                        layout_submitRemarke.setVisibility(View.GONE);
                        tv_userRemarke.setText("修改备注");
                    }
                } catch (Exception e) {
                }
                dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dismissLoadingView();
            }
        });
    }

    /**
     * 加急
     */
    private void addItional(final String additionall) {
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
        }
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
            jsonObject.put("username", MyApplication.getUserJson().getString("m_username"));
            jsonObject.put("pwd", MyApplication.getPasswprd());
            jsonObject.put("order_id", order_id);
            jsonObject.put("additional", additionall);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.ORDER_ADDITIONAL, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    tv_isBusy.setClickable(true);
                    toastMessage(response.getString("msg"));
                    if (response.getInt("status") == 0) {
                        additional = response.getJSONObject("data").getString("additional");
                        if (response.getJSONObject("data").getString("additional").equals("0")) {
                            tv_isBusy.setVisibility(View.GONE);
                            layout_addPrice.setVisibility(View.GONE);
                            tv_isBusy.setText("点击加急");
                            tv_isBusy.setTextColor(Color.parseColor("#999999"));
                            GradientDrawable background = (GradientDrawable) tv_isBusy.getBackground();
                            background.setColor(ContextCompat.getColor(OrderDetailActivity.this, R.color.fontcolor_e0));
                            tv_isBusy.setClickable(true);
//                        tv_isBusy.setBackground(ContextCompat.getDrawable(OrderDetailActivity.this,R.drawable.home_rect_sloid_state));
                            //显示可加急
                        } else if (response.getJSONObject("data").getString("additional").equals("1")) {
                            //处于加急状态
                            tv_isBusy.setVisibility(View.GONE);
                            layout_addPrice.setVisibility(View.VISIBLE);
                            orderDetail_tv_addPrice.setText(MyApplication.getConfigrationJson().getJSONObject("other").getString("orderaddmoney"));
                            tv_isBusy.setText("已加急");
                            tv_isBusy.setClickable(true);
                            tv_isBusy.setTextColor(Color.parseColor("#ffffff"));
                            GradientDrawable background = (GradientDrawable) tv_isBusy.getBackground();
                            background.setColor(ContextCompat.getColor(OrderDetailActivity.this, R.color.fontcolor_d8));
//                    GradientDrawable background = (GradientDrawable) holder2.myorder_tv_state.getBackground();
//                    background.setColor(Color.parseColor("#00000000"));
                            // holder1.myorder_tv_state.setBackground(ContextCompat.getDrawable(context,R.drawable.home_rect_sloid_state2));
                        }
                    }

                } catch (Exception e) {
                }
                dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dismissLoadingView();
                tv_isBusy.setClickable(true);
            }
        });
    }

    public void showGetData(final JSONObject jsonObject) {
        if (onclick == null) {
            onclick = new Type_choose.Onclick() {
                @Override
                public void OnClickLisener(View v) {
                    if (v.getId() == R.id.relayout_detail) {
                        if (!utils.StringUtils.isEmpty(order_id)) {

                        } else {
                            toastMessage("未获取到订单id,请退出后重试");
                        }

                    }
                }

                @Override
                public void conten(String payTyp, String payOther, View v) {
                    if (v.getId() == R.id.tv_pay) {
                        //支付
                        payOther_p = payOther;
                        payTyp_p = payTyp;
                        jsonObject_p = jsonObject;
                        if (!utils.StringUtils.isEmpty(order_id)) {
                           // createPayOrder(payTyp, payOther, jsonObject, v);
                            if(payTyp==null||payTyp.isEmpty()){
                                createPayOrder(payTyp, payOther, jsonObject, v);
                            }else{
                                if (pay_state == null) {
                                    pay_state = new IntetnetState();
                                }
                                pay_state.showGetData_pay(0, OrderDetailActivity.this, getResources().getDisplayMetrics(), onclick_pwd, layout_top, DisplayUtil.getStatusBarHeight(OrderDetailActivity.this));
                                pay_state.dismissLinsener(tv_payOrder);

                            }

                            dialog.dismiss();
                            v.setClickable(true);
                        } else {
                            toastMessage("未获取到订单id,请退出后重试");
                            v.setClickable(true);
                        }
                    }

                }
            };
        }
        if (dialog == null) {
            try {
                Double price = jsonObject.getDouble("money") / 1000 + jsonObject.getDouble("timeoutmoney") / 1000 + jsonObject.getDouble("addmoney") / 1000;
                dialog = new Type_choose(this, R.style.loading_dialog, onclick, MyApplication.getUserJson(), price, jsonObject.getDouble("timeoutmoney") / 1000, jsonObject.getDouble("addmoney") / 1000, true);//100d价格
            } catch (Exception e) {

            }
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉黑色头部
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);//只有这样才能去掉黑色背景
        }
        dialog.setArtical_type("PayHelp");
        dialog.show();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = DisplayUtil.getScreenWidth(this); //设置宽度
        dialog.getWindow().setAttributes(lp);
    }

    public void dismiss() {
        dialog.dismiss();
    }

    //支付
    private void pay(final Context context, String order, final JSONObject jsonObject, final View view, final String openbox) {
        PayType payType = new PayType();
        PayType.SucceedPay succeedPay = new PayType.SucceedPay() {
            @Override
            public void callBack(Boolean back, String s) {
                if (s.contains("支付成功")) {
                    toastMessage("成功");
                    //唤起开箱操作
                    if (openbox.equals("1")) {
                        SocketMessage_getBox(jsonObject);
                    }
                    if (!StringUtils.isEmpty(getIntent().getBundleExtra("bundle").getString("order_id"))) {
                        order_id = getIntent().getBundleExtra("bundle").getString("order_id");
                        getOrderDetail(order_id);
                    } else {
                        toastMessage("支付成功，欢迎继续使用，请注意接收短信或查看订单详情取件码");
                    }
                    quary_money();

                } else {
                    Toast.makeText(context, s.toString(), Toast.LENGTH_LONG).show();
                    try {
                        view.setClickable(true);
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
        showLoadingView();
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
            json.put("paytype", payType);//
            json.put("payother", payOther);
            json.put("order_id", order_id);
            json.put("order_type", "user_write");
            if (qrcode.equals("1")) {
                json.put("qrcode", qrcode);//1表示是从扫二维码发起的支付，其它为订单详情等地方发起的支付
            }
            if (bdLoca != null) {
                json.put("lon", bdLoca.getLongitude() + "");
                json.put("lat", bdLoca.getLatitude() + "");
            }
            json.put("request_type", "app");
            json.put("username", MyApplication.getUserJson().getString("m_username"));
            json.put("pwd", MyApplication.getPasswprd());
            if(!StringUtils.isEmpty(payTyp_p)) {
                json.put("paypwd", paypwd);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("paye", e.toString());
        }
        params.put("data", json.toString());
        Log.d("pay", json.toString());
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

                        if (!StringUtils.isEmpty(response.getString("data"))) {
                            //存在三方支付
                            jsonObject.put("box_number", response.getString("box_number"));
                            jsonObject.put("box_id", response.getString("box_id"));

                            if (payOther.contains("weixin")) {
                                //微信支付
                                startActivityForResult(new Intent(OrderDetailActivity.this, WXPayEntryActivity.class).putExtra("json",jsonObject.toString()).putExtra("openbox",response.getString("openbox")).putExtra("wxDate", response.getJSONObject("data").toString()), 2);
                                Constants.APP_ID = response.getJSONObject("data").getString("appid");
                            } else {
                                pay(OrderDetailActivity.this, response.getString("data"), jsonObject, view, response.getString("openbox"));
                            }
                            dialog.dismiss();
                            if(pay_state!=null) {
                                pay_state.dismiss_pay();
                            }
                        } else {
                            //不存在三方支付
                            if (response.getString("openbox").equals("1")) {
                                //开箱
                                jsonObject.put("box_number", response.getString("box_number"));
                                jsonObject.put("box_id", response.getString("box_id"));
                                //开箱
                                SocketMessage_getBox(jsonObject);

                            }
                            if (!StringUtils.isEmpty(getIntent().getBundleExtra("bundle").getString("order_id"))) {
                                order_id = getIntent().getBundleExtra("bundle").getString("order_id");
                                getOrderDetail(order_id);
                                quary_money();
                            }
                            dialog.dismiss();
                            if(pay_state!=null) {
                                pay_state.dismiss_pay();
                            }
                        }

                    } else {
                        // toastMessage(response.getString("msg"));
                        if(pay_state!=null) {
                            pay_state.dismiss_pay();
                        }
                    }
                } catch (Exception e) {
                    toastMessage("数据解析异常");
                    view.setClickable(true);
                    if(pay_state!=null) {
                        pay_state.dismiss_pay();
                    }
                }
                dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("网络异常，请检查网络后重试");
                Log.d("  ",error_msg);
                dismissLoadingView();
                view.setClickable(true);
            }
        });
    }


    /**
     * 发送开箱信息,获取衣物
     */
    private void SocketMessage_getBox(JSONObject json2) {
        if (bdLoca == null) {
            toastMessage("未获取到当前位置，为了您的物品安全，请检查您手机的GPS定位功能");
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("apiType", RequestTag.OPEN_ARK);
                    jsonObject.put("ark_id", json2.getString("ark_id"));
                    jsonObject.put("box_id", json2.getString("box_id"));
                    jsonObject.put("client_id", "APP_" + MyApplication.getUid());//客户端ID（APP_+用户UID）
                    jsonObject.put("type", 2 + "");//1=存件码开箱 2=取件码开箱
                    jsonObject.put("lon", bdLoca.getLongitude() + "");
                    jsonObject.put("lat", bdLoca.getLatitude() + "");
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
//有空指针的可能
                boolean isSend = Main_NewActivity.iBackService.sendMessage(jsonObject.toString());//Send Content by socket
                Log.d("isSend", jsonObject.toString());
//            Toast.makeText(this, isSend ? "success" : "fail",
//                    Toast.LENGTH_SHORT).show();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 2:
                //图片浏览界面删除返回
                if (data.getStringArrayListExtra("list_pic") != null) {
                    if (data.getStringArrayListExtra("list_pic").size() == list_choose.size()) {
                        break;
                    } else {
                        list_choose.clear();
                        list_choose.addAll(data.getStringArrayListExtra("list_pic"));
                        list_pic.clear();
                        list_pic.addAll(data.getStringArrayListExtra("list_pic"));
                        list_pic.add(null);
                        adapter.notifyDataSetChanged();
                    }

                }
                break;
            case 3:
                //微信成功界面返回
                toastMessage("成功");
                //唤起开箱操作
                if(data!=null&&data.getStringExtra("openbox")!=null) {
                    if (data.getStringExtra("openbox").equals("1")&&data.getStringExtra("json")!=null)
                        try {
                            SocketMessage_getBox(new JSONObject(data.getStringExtra("json")));
                        }catch (Exception e){

                        }
                    if (!StringUtils.isEmpty(getIntent().getBundleExtra("bundle").getString("order_id"))) {
                        order_id = getIntent().getBundleExtra("bundle").getString("order_id");
                        getOrderDetail(order_id);
                    } else {
                        toastMessage("支付成功，欢迎继续使用，请注意接收短信或查看订单详情取件码");
                    }
                    quary_money();
                }else{
                    toastMessage("订单异常，请登录后查看订单状态");
                }
                break;
        }
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
                    // toastMessage(response.getString("msg"));
                    if (response.getString("status").equals("0")) {
                        MyApplication.getUserJson().put("money", response.getJSONObject("data").getLong("money"));
                        MyApplication.getUserJson().put("clothes", response.getJSONObject("data").getLong("clothes"));
                        MyApplication.getUserJson().put("interal", response.getJSONObject("data").getLong("interal"));
                        MyApplication.getUserJson().put("card", response.getJSONObject("data").getLong("card"));
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

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.can_detail = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.can_detail = true;
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
            popupWindow_gps.setOutsideTouchable(true);// 设置同意在外点击消失
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
        Runnable showPopWindowRunnable = new Runnable() {

            @Override
            public void run() {
                // 得到activity中的根元素
                // 如何根元素的width和height大于0说明activity已经初始化完毕
                if (layout_parent != null && layout_parent.getWidth() > 0 && layout_parent.getHeight() > 0) {
                    // 显示popwindow
                    view_zhezhao.setVisibility(View.VISIBLE);
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

}
