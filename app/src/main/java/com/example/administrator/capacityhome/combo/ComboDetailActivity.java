package com.example.administrator.capacityhome.combo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.administrator.capacityhome.BaseActivity;
import com.example.administrator.capacityhome.Main_NewActivity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.login.LoginActivity;
import com.example.administrator.capacityhome.wxapi.Constants;
import com.example.administrator.capacityhome.wxapi.WXPayEntryActivity;
import com.squareup.picasso.Picasso;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import http.RequestTag;
import myview.Type_choose;
import utils.DisplayUtil;
import utils.MImageGetter;
import utils.Md5;
import utils.SPUtils;
import utils.StringUtils;
import utils.paydialog.IntetnetState;
import utils.paydialog.Pay_Dialog;
import widget.RoundTransform;
import zfb_pay.PayType;
/**
 * 套餐详情
 */
public class ComboDetailActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_content;
    private TextView tv_title;
    private TextView tv_price2;//数量
    private TextView tv_price;//包洗
    private TextView tv_r;//单位
    private TextView price_tv;//价格
    private TextView tv_price3;//已售
    private TextView tv_peopelType;//适合人群
    private TextView tv_storeName;
    private ImageView brand_img;
    private ImageView img_pic;
    private TextView tv_titles;
    private TextView tv_buy;//立即购买
    private RelativeLayout layout_top;
/**
 * 用于填充布局，无实际效果
 */
private TextView tv_price2_y;//价格
private TextView tv_price_y;
private TextView tv_r_y;//单位
    /**
     * 支付弹框相关
     */
    private Pay_Dialog.Onclick onclick_pwd;//支付按钮回调接口
    private IntetnetState pay_state;//支付弹框
    private Type_choose dialog;//支付选择
    private Type_choose.Onclick onclick;
    private String payType;
    private String payOther;
    private JSONObject jsonObject_main;
    private RelativeLayout title_top;
    private WebView webView;
    /**
     * 定位
     *
     * @param savedInstanceState
     */
    private LocationClient mLocationClient;
    private BDLocation bdLoca;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combo_detail);
        initView();
        initLocation();
    }

    private void initView() {
        backActivity();
        tv_price3 = (TextView) findViewById(R.id.tv_price3);
        tv_titles = (TextView) findViewById(R.id.tv_title);
        tv_titles.setText("套餐详情");
        webView = (WebView) findViewById(R.id.webView);
        tv_buy = (TextView) findViewById(R.id.tv_buy);
        layout_top = (RelativeLayout) findViewById(R.id.relayout_parent);
        title_top = (RelativeLayout) findViewById(R.id.title_top);
        findViewById(R.id.top_submit_btn).setVisibility(View.GONE);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_title = (TextView) findViewById(R.id.tv_title2);
        price_tv = (TextView) findViewById(R.id.price_tv);
        tv_r = (TextView) findViewById(R.id.tv_r);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_price2 = (TextView) findViewById(R.id.tv_price2);
        tv_peopelType = (TextView) findViewById(R.id.tv_peopelType);
        tv_storeName = (TextView) findViewById(R.id.tv_storeName);
        img_pic = (ImageView) findViewById(R.id.img_pic);
        brand_img = (ImageView) findViewById(R.id.brand_img);
        if (getIntent().getStringExtra("id") != null) {
            getDetail(getIntent().getStringExtra("id"));
        } else {
            toastMessage("暂时没有相关数据");
        }
        tv_buy.setOnClickListener(this);
        onclick_pwd = new Pay_Dialog.Onclick() {
            @Override
            public void OnClickLisener(View v) {
                    try {
                        buyCombo(payType,payOther,pay_state.getPay_dialog().password());
                       // requestWithdraw(bank_id, MyApplication.getUserJson().getLong("money") + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        };
        WebSettings settings = webView.getSettings();
        settings.setSupportZoom(true);
        settings.setDefaultFontSize(14);
        webView.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//        settings.
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

    /**
     * (特殊字符替换)
     *
     * @return String    返回类型
     */
    public static String htmlReplace(String str) {
        str = str.replace("&ldquo;", "“");
        str = str.replace("&quot;", "“");
        str = str.replace("&amp;", "&");
        str = str.replace("&lt;", "<");
        str = str.replace("&gt;", ">");
        str = str.replace("&rdquo;", "”");
        str = str.replace("&nbsp;", " ");
        str = str.replace("&", "&amp;");
        str = str.replace("&#39;", "'");
        str = str.replace("&rsquo;", "’");
        str = str.replace("&mdash;", "—");
        str = str.replace("&ndash;", "–");
        return str;
    }

    private void getDetail(String id) {
        showLoadingView();
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.COMBO_DETAIL, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if (response.getInt("status") != 0) {
                        toastMessage(response.getString("msg"));
                    } else {
                        jsonObject_main = response.getJSONObject("data");
                        if (!StringUtils.isEmpty(response.getJSONObject("data").getString("title"))) {
                            tv_title.setText(response.getJSONObject("data").getString("title"));
                        } else {
                            tv_title.setText("未编辑");
                        }
                        if (!StringUtils.isEmpty(response.getJSONObject("data").getString("stitle"))) {
                            tv_storeName.setText(response.getJSONObject("data").getString("stitle"));
                        } else {
                            tv_storeName.setText("未编辑");
                        }
                        price_tv.setText(response.getJSONObject("data").getDouble("money") / 1000 + "");
                        tv_price3.setText(response.getJSONObject("data").getInt("cell")+"");
                        if(response.getJSONObject("data").getInt("num") / 100<999) {
                            tv_r.setVisibility(View.VISIBLE);
                            tv_price2.setVisibility(View.VISIBLE);
                            tv_price2.setText(response.getJSONObject("data").getInt("num") / 100 + "");
                        }else{
                            tv_r.setText("不限件");
                           // tv_price.setText("不限件");
                            tv_price.setVisibility(View.GONE);
                            tv_price2.setVisibility(View.GONE);
                            //tv_r.setVisibility(View.INVISIBLE);
                        }
                        if (!StringUtils.isEmpty(response.getJSONObject("data").getString("suitable"))) {
                            tv_peopelType.setText("" + response.getJSONObject("data").getString("suitable"));
                        } else {
                            tv_peopelType.setText("暂未编辑");
                        }
                        webView.loadDataWithBaseURL(null, response.getJSONObject("data").getString("content"), "text/html" , "utf-8", null);
                        tv_content.setText(Html.fromHtml(htmlReplace(response.getJSONObject("data").getString("content")), new MImageGetter(tv_content, ComboDetailActivity.this), null));
                        if (!StringUtils.isEmpty(response.getJSONObject("data").getString("pic"))) {
                            Picasso.with(ComboDetailActivity.this).load(RequestTag.BaseImageUrl + response.getJSONObject("data").getString("pic")).transform(new RoundTransform(20)).error(R.mipmap.default_iv).placeholder(R.mipmap.default_iv).into(img_pic);
                        } else {
                            img_pic.setImageDrawable(ContextCompat.getDrawable(ComboDetailActivity.this, R.mipmap.default_iv));
                        }
                        if (response.getJSONObject("data").getJSONArray("brand") != null && response.getJSONObject("data").getJSONArray("brand").length() > 0) {
                            brand_img.setVisibility(View.VISIBLE);
                            Picasso.with(ComboDetailActivity.this).load(RequestTag.BaseImageUrl + response.getJSONObject("data").getJSONArray("brand").getJSONObject(0).getString("pic")).error(R.mipmap.default_iv).placeholder(R.mipmap.default_iv).into(brand_img);
                        } else {
                            brand_img.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.toString();
                }
                dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("请检查网络");
                dismissLoadingView();

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_buy:
                //购买
               isLoginOther();
                break;
        }
    }
    /**
     * 判断用户登录态
     */
    public void isLoginOther() {
        //扫码
        if (MyApplication.getUid() == null) {
            //表示用户从没有登录
            startActivity_leftToRight(this, LoginActivity.class);
        } else if (MyApplication.user_json == null) {
            //可以打开扫描
            //标识用户以前登录过，
            getUserInfo();
        } else {
            tv_buy.setClickable(false);
            showGetData(jsonObject_main);
        }
    }
    /**
     * 获取用户基本信息
     */
    private void getUserInfo() {
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
                        tv_buy.setClickable(false);
                        showGetData(jsonObject_main);
                    }
                } catch (Exception e) {
                    toastMessage("数据解析异常，请稍后重试");

                }
                dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dismissLoadingView();

            }
        });
    }
    private void buyCombo(String payTyp, final String payOther, String paypwd) {
        showLoadingView();
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("paytype", payTyp);
            jsonObject.put("payother", payOther);
            jsonObject.put("num", "1");//购买份数默认1
            jsonObject.put("request_type","app");
            if (bdLoca != null) {
                jsonObject.put("lon", bdLoca.getLongitude() + "");
                jsonObject.put("lat", bdLoca.getLatitude() + "");
            }
            jsonObject.put("id", getIntent().getStringExtra("id"));
            jsonObject.put("uid", MyApplication.getUid());
            if(!StringUtils.isEmpty(payTyp)) {
                jsonObject.put("paypwd", paypwd);
            }//（当paytype不为空时请用户输入后传入）
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.BUY_COMBO, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if (response.getInt("status") != 0) {
                        toastMessage(response.getString("msg"));
                    } else {
                        if (!utils.StringUtils.isEmpty(response.getString("data"))) {
                            if(payOther.contains("weixin")){
                                startActivity(new Intent(ComboDetailActivity.this, WXPayEntryActivity.class).putExtra("wxDate",response.getJSONObject("data").toString()));
                                Constants.APP_ID = response.getJSONObject("data").getString("appid");
                            }else {
                                pay(ComboDetailActivity.this, response.getString("data"), tv_buy);
                                dialog.dismiss();
                                pay_state.dismiss_pay();
                            }
                        }
                        else {
                            toastMessage(response.getString("msg"));
                            dialog.dismiss();
                            pay_state.dismiss_pay();

                        }
                    }
                } catch (Exception e) {
                    e.toString();
                    if(e.toString().contains("No value for data")){

                        try {
                            toastMessage(response.getString("msg"));
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        dialog.dismiss();
                        pay_state.dismiss_pay();
                    }
                }
                tv_buy.setClickable(true);
                dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("请检查网络");
                dismissLoadingView();
                tv_buy.setClickable(true);

            }
        });
    }

    public void showGetData(final JSONObject jsonObject) {
        if (onclick == null) {
            onclick = new Type_choose.Onclick() {
                @Override
                public void OnClickLisener(View v) {
                    if (v.getId() == R.id.relayout_detail) {

                    }
                }

                @Override
                public void conten(String payTyp, String payOther, View v) {
                    if (v.getId() == R.id.tv_pay) {
                        ComboDetailActivity.this.payType = payTyp;
                        ComboDetailActivity.this.payOther = payOther;
                        //支付
                           // createPayOrder(payTyp, payOther, jsonObject, v);
                        if(payTyp==null||payTyp.isEmpty()){
                            buyCombo(payTyp,payOther,null);
                        }else{
                            if (pay_state == null) {
                                pay_state = new IntetnetState();
                            }

                            pay_state.showGetData_pay(0, ComboDetailActivity.this, getResources().getDisplayMetrics(), onclick_pwd, title_top, DisplayUtil.getStatusBarHeight(ComboDetailActivity.this));
                            pay_state.dismissLinsener(tv_buy);

                        }
                          v.setClickable(true);
                            dialog.dismiss();
                        }

                }
            };
        }
        if (dialog == null) {
            try {
                Double price = jsonObject.getDouble("money") / 1000;
                dialog = new Type_choose(this, R.style.loading_dialog, onclick, MyApplication.getUserJson(), price, 0d, 0d,false);//100d价格
            } catch (Exception e) {

            }
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉黑色头部
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);//只有这样才能去掉黑色背景
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    tv_buy.setClickable(true);
                }
            });
        }
        dialog.setArtical_type("PayHelp");
        dialog.show();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = DisplayUtil.getScreenWidth(this); //设置宽度
        dialog.getWindow().setAttributes(lp);
    }
    //支付
    private void pay(final Context context, String order, final View view) {
        PayType payType = new PayType();
        PayType.SucceedPay succeedPay = new PayType.SucceedPay() {
            @Override
            public void callBack(Boolean back, String s) {
                if (s.contains("支付成功")) {
                    //二次验证
                    //paySecondesSure();
                    toastMessage("成功");
                    view.setClickable(true);
                    //  SocketMessage_getBox(jsonObject);

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

//    /**
//     * 创建支付订单
//     */
//    private void createPayOrder(String payType, String payOther, final JSONObject jsonObject, final View view) {
//        KeyBordUtil.hintKeyboard(this);
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("appid", RequestTag.APPID);
//        params.put("key", RequestTag.KEY);
//        String timestamp = System.currentTimeMillis() + "";
//        params.put("time", timestamp + "");
//        JSONObject json = new JSONObject();
//        try {
//            json.put("device_id", MyApplication.getDeviceId());
//            json.put("uid", MyApplication.getUid());
//            json.put("paytype", payType);
//            json.put("payother", payOther);
//            Log.d("zhifubao", payType + "," + payOther);
//            json.put("order_id", order_id);
//            json.put("order_type", "user_write");
//            jsonObject.put("username", MyApplication.getUserJson().getString("m_username"));
//            jsonObject.put("pwd", MyApplication.getPasswprd());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        params.put("data", json.toString());
//        String sign = Md5.md5(json.toString(), timestamp);
//        params.put("sign", sign);
//        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.POST_PAY, params, new JsonResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, JSONObject response) {
//                try {
//                    view.setClickable(true);
//                    toastMessage(response.getString("msg"));
//                    Log.d("pay", response.toString());
//                    if (response.getString("status").equals("0")) {
//                            if (!utils.StringUtils.isEmpty(response.getString("data"))) {
//                                pay(ComboDetailActivity.this, response.getString("data"), jsonObject,tv_buy);
//                                dialog.dismiss();
//                            }
//                         else {
//                           dialog.dismiss();
//                        }
//
//                    } else {
//                        // toastMessage(response.getString("msg"));
//
//                    }
//                } catch (Exception e) {
//                    toastMessage("数据解析异常");
//                    view.setClickable(true);
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, String error_msg) {
//                toastMessage("网络异常，请检查网络后重试");
//                view.setClickable(true);
//            }
//        });
//    }
}
