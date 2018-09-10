package com.example.administrator.capacityhome.selfcenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.capacityhome.BaseActivity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.login.LoginActivity;
import com.example.administrator.capacityhome.message.MessageActivity;
import com.example.administrator.capacityhome.wxapi.Constants;
import com.example.administrator.capacityhome.wxapi.WXPayEntryActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import http.RequestTag;
import utils.ColumnUtils;
import utils.KeyBordUtil;
import utils.Md5;
import utils.SPUtils;
import utils.StringUtils;
import zfb_pay.PayType;

/**
 * 余额充值
 */
public class BalanceRechargeActivity extends BaseActivity implements View.OnClickListener {
    private int[] ids = new int[]{R.id.tv_choose1, R.id.tv_choose2, R.id.tv_choose3, R.id.tv_choose4};
    private int[] ids_ct = new int[]{R.id.ct_circle1, R.id.ct_circle2, R.id.ct_circle3, R.id.ct_circle4};
    private TextView[] tvViews;
    private CheckedTextView[] tvViews_ct;
    private EditText et_money;
    private ColumnUtils columnUtils;
    private int index = 0;//j记录上次变化位置
    private List<JSONObject> list_type = new ArrayList<>();
    private LinearLayout layout_payType;
    private TextView tv_sendClothes;//赠送洗衣点
    private LinearLayout layout_clothes;
    private String type;
    private String payid;
    private String money;
    private boolean isCanPay = false;
    private String name = "";
    private TextView tv_notice;
    private View tv_Noread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_recharge);
        initView();
        post();
        getPayType();
        getAggrement();
    }

    private void initView() {
        tv_Noread = findViewById(R.id.tv_Noread);
        if(MyApplication.no_rendMessage>0){
            tv_Noread.setVisibility(View.VISIBLE);
        }else{
            tv_Noread.setVisibility(View.GONE);
        }
        tv_notice = (TextView) findViewById(R.id.tv_notice);
        findViewById(R.id.tv_sure_right).setOnClickListener(this);
        layout_payType = (LinearLayout) findViewById(R.id.layout_payType);
        layout_clothes = (LinearLayout) findViewById(R.id.layout_clothes);
        tv_sendClothes = (TextView) findViewById(R.id.tv_sendClothes);
        tvViews = new TextView[ids.length];
        tvViews_ct = new CheckedTextView[ids_ct.length];
        for (int i = 0; i < ids.length; i++) {
            tvViews[i] = (TextView) findViewById(ids[i]);
            tvViews_ct[i] = (CheckedTextView) findViewById(ids_ct[i]);
            tvViews[i].setOnClickListener(this);
            tvViews_ct[i].setOnClickListener(this);
        }
        et_money = (EditText) findViewById(R.id.et_money);
        findViewById(R.id.relayout_back).setOnClickListener(this);
        findViewById(R.id.img_message).setOnClickListener(this);
        et_money.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 输入的内容变化的监听
                try {
                    if (MyApplication.getConfigrationJson().getJSONObject("other").getInt("PaymoneyStatus") == 1) {
                        layout_clothes.setVisibility(View.GONE);
                        for (int i = 0; i < MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("PaymoneySet").length(); i++) {
                            if (s.toString().equals(MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("PaymoneySet").getJSONArray(i).getString(0))) {
                                layout_clothes.setVisibility(View.VISIBLE);
                                tv_sendClothes.setText(MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("PaymoneySet").getJSONArray(i).getString(1) + "");
                                setTextChange(i);
                            } else {
                                tvViews_ct[i].setChecked(false);
                                tvViews[i].setTextColor(ContextCompat.getColor(BalanceRechargeActivity.this, R.color.fontcolor_f9));
                            }
                            if (Double.parseDouble(s.toString()) >= Double.parseDouble((MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("PaymoneySet").getJSONArray(0).getString(0)))) {
                                //价格只要处于某一区间都赠送
                                layout_clothes.setVisibility(View.VISIBLE);
                                if (Double.parseDouble(s.toString()) >= Double.parseDouble((MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("PaymoneySet").getJSONArray(i).getString(0)))) {
                                    tv_sendClothes.setText(MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("PaymoneySet").getJSONArray(i).getString(1) + "");
                                }
                            } else {
                                layout_clothes.setVisibility(View.GONE);
                            }
                        }
                        if (StringUtils.isEmpty(s.toString())) {
                            setTextChange(0);
                        }
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // 输入前的监听
                Log.e("输入前确认执行该方法", "开始输入");

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 输入后的监听
                Log.e("输入结束执行该方法", "输入结束");

            }
        });
        if(MyApplication.no_rendMessage>0){
            tv_Noread.setVisibility(View.VISIBLE);
        }else{
            tv_Noread.setVisibility(View.GONE);
        }
    }

    private void getPayType() {
        if (columnUtils == null) {
            columnUtils = new ColumnUtils(this, new ColumnUtils.Result() {
                @Override
                public void onSuccess(String title, JSONArray jsonArray) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            if (jsonArray.getJSONObject(i).getString("name").contains("支付宝") ||
                                    jsonArray.getJSONObject(i).getString("name").contains("微信")) {
                                list_type.add(jsonArray.getJSONObject(i));
                                addPayTypeView2(jsonArray.getJSONObject(i), i - 1);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(String msg) {
                    toastMessage(msg);
                }
            });
        }
        if (list_type.size() == 0) {
            columnUtils.getPayList("");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ct_circle1:
            case R.id.tv_choose1:
                setTextChange(0);
                break;
            case R.id.ct_circle2:
            case R.id.tv_choose2:
                setTextChange(1);
                break;
            case R.id.ct_circle3:
            case R.id.tv_choose3:
                setTextChange(2);
                break;
            case R.id.ct_circle4:
            case R.id.tv_choose4:
                setTextChange(3);
                break;
            case R.id.relayout_back:
                finish();
                break;
            case R.id.tv_sure_right:
                //充值
                if (et_money.getText().toString().isEmpty()) {
                    toastMessage("请输入充值金额");
                } else {
                    for (int i = 0; i < layout_payType.getChildCount(); i++) {
                        if (((CheckedTextView) layout_payType.getChildAt(i).findViewById(R.id.pay_ct_circle)).isChecked()) {
                            isCanPay = true;
                            break;
                        } else {
                            isCanPay = false;
                        }
                    }
                    if (name.contains("微信")) {
                        toastMessage("暂请选择支付宝");
                    } else {
                        if (isCanPay) {
                            createPayOrder();
                        } else {
                            toastMessage("请选择支付方式");
                        }
                    }
                }
                break;
            case R.id.img_message:
                //消息
                if (MyApplication.getUid() == null) {
                    startActivity(new Intent(this, LoginActivity.class));
                } else {
                    startActivity(new Intent(this, MessageActivity.class));
                }
                break;
        }
    }

    private void setTextChange(int position) {
        if (index != position) {
            tvViews[position].setTextColor(ContextCompat.getColor(this, R.color.fontcolor_yellow));
            tvViews_ct[position].setChecked(true);
            tvViews[index].setTextColor(ContextCompat.getColor(this, R.color.fontcolor_f9));
            tvViews_ct[index].setChecked(false);
            index = position;
            try {
                money = MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("PaymoneySet").getJSONArray(position).get(0) + "";
                et_money.setText(money + "");
                et_money.setSelection((money + "").length());
                layout_clothes.setVisibility(View.VISIBLE);
                tv_sendClothes.setText(MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("PaymoneySet").getJSONArray(position).get(1) + "");
            } catch (Exception e) {
                e.printStackTrace();
                toastMessage(e.toString());
            }
        }
    }

    //支付
    private void pay(final Context context, String order) {
        PayType payType = new PayType();
        PayType.SucceedPay succeedPay = new PayType.SucceedPay() {
            @Override
            public void callBack(Boolean back, String s) {
                if (s.contains("支付成功")) {
                    //二次验证
                    //paySecondesSure();
                    toastMessage("充值成功");
                    quary_money();
                } else {
                    Toast.makeText(context, s.toString(), Toast.LENGTH_LONG).show();
                }
            }
        };
        order = order.replace("\"", "");
        payType.pay_zfb(context, order.replace("\"", "").replace("\"", ""), succeedPay);
    }

    /**
     * 创建支付订单
     */
    private void createPayOrder() {
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
            json.put("paytype", type);
            json.put("payid", payid);
            if (!et_money.getText().toString().isEmpty()) {
                json.put("money", et_money.getText().toString());
            } else {
                json.put("money", money + "");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", json.toString());
        String sign = Md5.md5(json.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.BALANCE_RECHARGE, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {

                    Log.d("pay", response.toString());
                    if (response.getString("status").equals("0")) {
                        if (!StringUtils.isEmpty(response.getJSONObject("data").getString("callback"))) {
                            if (type.contains("weixin")) {
                                startActivityForResult(new Intent(BalanceRechargeActivity.this, WXPayEntryActivity.class).putExtra("wxDate", response.getJSONObject("data").getJSONObject("callback").toString()),4);
                                Constants.APP_ID = response.getJSONObject("data").getJSONObject("callback").getString("appid");
                            } else {
                                pay(BalanceRechargeActivity.this, response.getJSONObject("data").getString("callback"));
                            }
                        }


                    }
//                    if (response.getString("status").equals("0")) {
//                        if (!StringUtils.isEmpty(response.getJSONObject("data").getString("callback"))) {
//                            pay(BalanceRechargeActivity.this, response.getJSONObject("data").getString("callback"));
//                        }
//
//                    }
                    else {
                        toastMessage(response.getString("msg"));
                    }
                } catch (Exception e) {
                    toastMessage("数据解析异常");
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

    /**
     * 动态添加三方支付类型控件,微信，支付宝
     */
    private void addPayTypeView2(final JSONObject json, final int position) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(BalanceRechargeActivity.this).inflate(R.layout.balance_recharge, null);
        final CheckedTextView pay_ct_circle = (CheckedTextView) view.findViewById(R.id.pay_ct_circle);
        final ImageView img_pay = (ImageView) view.findViewById(R.id.img_pic);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        view.setLayoutParams(layoutParams);
        try {
            tv_title.setText(json.getString("name"));
            Picasso.with(BalanceRechargeActivity.this).load(RequestTag.BaseImageUrl + json.getString("mobile_logo_url")).into(img_pay, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    try {
                        Picasso.with(BalanceRechargeActivity.this).load(RequestTag.BaseImageUrl + MyApplication.getConfigrationJson().getJSONObject("other").getString("default_img")).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).into(img_pay);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {

        }
        layout_payType.addView(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AnimationUtils().loadAnimation(BalanceRechargeActivity.this, R.anim.bg_alpha));
                try {
                    type = json.getString("type");
                    payid = json.getString("id");
                    name = json.getString("name");
                    if (json.getString("name").contains("支付宝")) {
                        for (int i = 0; i < layout_payType.getChildCount(); i++) {
                            if (position != i) {
                                ((CheckedTextView) layout_payType.getChildAt(i).findViewById(R.id.pay_ct_circle)).setChecked(false);
                            }
                        }
                        if (pay_ct_circle.isChecked()) {
                            pay_ct_circle.setChecked(false);
                        } else {
                            pay_ct_circle.setChecked(true);
                        }
                    } else if (json.getString("name").contains("微信")) {
                        for (int i = 0; i < layout_payType.getChildCount(); i++) {
                            if (position != i) {
                                ((CheckedTextView) layout_payType.getChildAt(i).findViewById(R.id.pay_ct_circle)).setChecked(false);
                            }
                        }
                        if (pay_ct_circle.isChecked()) {
                            pay_ct_circle.setChecked(false);
                        } else {
                            pay_ct_circle.setChecked(true);
                        }
                    }
                } catch (Exception e) {

                }
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
                    if (response.getString("status").equals("0")) {
                        MyApplication.isupdate = true;
                        MyApplication.getUserJson().put("money", response.getJSONObject("data").getLong("money"));
                        MyApplication.getUserJson().put("clothes", response.getJSONObject("data").getLong("clothes"));
                        MyApplication.getUserJson().put("interal", response.getJSONObject("data").getLong("interal"));
                        MyApplication.getUserJson().put("card", response.getJSONObject("data").getLong("card"));
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "pay_money");
                        bundle.putString("title", "余额充值");
                        startActivity_Bundle(BalanceRechargeActivity.this, MoneyDetailActivity.class, bundle);
                        finish();
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
     * //     * 获取相关协议内容
     * //
     */
    public void getAggrement() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("mark", "PaySee");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.ARTICAL_DETAIL, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if (response.getInt("status") != 0) {
                        Toast.makeText(BalanceRechargeActivity.this, "暂时没有编辑", Toast.LENGTH_LONG).show();
                    } else {
                        if (response.getJSONObject("data") == null) {
                            Toast.makeText(BalanceRechargeActivity.this, "暂时没有编辑", Toast.LENGTH_LONG).show();
                        } else {
                            tv_notice.setText("温馨提示：" + Html.fromHtml(response.getJSONObject("data").getString("content")));
                            // popIsNull(response.getJSONObject("data").getString("content"), response.getJSONObject("data").getString("title"), btn_name);
                        }
                    }

                } catch (Exception e) {
                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                Toast.makeText(BalanceRechargeActivity.this, "请检查网络是否异常", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void post() {
        showLoadingView();
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.CONFIGURATION, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    Log.d("configration_json", response.toString());
                    if (response.getInt("status") != 0) {
                        toastMessage(response.getString("msg"));
                    } else {
                        SPUtils.setSharedStringData(BalanceRechargeActivity.this, "configration_json", response.getJSONObject("data").toString());
                        RequestTag.BaseUrl = response.getJSONObject("data").getString("apiurl");
                        RequestTag.BaseImageUrl = response.getJSONObject("data").getString("imgurl");
                        RequestTag.BaseVieoUrl = response.getJSONObject("data").getString("videourl");
                        RequestTag.ArticaleUrl = response.getJSONObject("data").getString("articleurl");
                    }
                } catch (Exception e) {

                }
                try {
                    if (MyApplication.getConfigrationJson().getJSONObject("other").getInt("PaymoneyStatus") == 1) {
                        if (MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("PaymoneySet") != null) {
                            for (int i = 0; i < tvViews.length; i++) {
                                tvViews[i].setText("充值" + MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("PaymoneySet").getJSONArray(i).get(0) + "送" + MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("PaymoneySet").getJSONArray(i).get(1) + "点");
                                if (i == 0) {
                                    et_money.setText(MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("PaymoneySet").getJSONArray(i).get(0) + "");
                                    et_money.setCursorVisible(true);
                                    money = MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("PaymoneySet").getJSONArray(i).get(0) + "";
                                    et_money.setSelection(money.length());
                                    layout_clothes.setVisibility(View.VISIBLE);
                                    tv_sendClothes.setText(MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("PaymoneySet").getJSONArray(i).get(1) + "");
                                }
                            }
                        }
                    } else {
                        findViewById(R.id.layout_1).setVisibility(View.GONE);
                        findViewById(R.id.layout_2).setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 4) {
            quary_money();
        }
//        else if (requestCode == 2) {
//            quary_money();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tv_Noread != null) {
            if (MyApplication.no_rendMessage < 1) {
                tv_Noread.setVisibility(View.GONE);
            }else{
                tv_Noread.setVisibility(View.VISIBLE);
            }
        }
    }
}
