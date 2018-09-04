package myview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
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
import utils.GetAgrement;
import utils.Md5;
import utils.StringUtils;
import widget.MyTextView;

/**
 * @author http://blog.csdn.net/finddreams
 * @Description:自定义对话框
 */
public class Type_choose extends Dialog implements View.OnClickListener {

    private Context mContext;
    private RelativeLayout[] layouViews;//布局数组
    private RelativeLayout relayout_priceSpread;
    private TextView tv_pay;//付款
    private int[] layoutIds = new int[]{R.id.layout1, R.id.layout2};
    private int[] ctId = new int[]{R.id.gouwuCar_balance, R.id.gouwuCar_addtional};
    private CheckedTextView[] ctViews;
    private WindowManager m;
    private Onclick onclick;
    private boolean isClickZFB = false;//是否选择支付宝
    private boolean isClickWX = false;//是否选择微信
    private String paytype = "";//本平台支付类型标识多个用英文豆号,分隔，如：money,interal,clothes
    private String payother = "";//选择的三方支付类型
    private JSONObject jsonObject;
    private RelativeLayout relayout_detail;//查看订单详情
    private TextView tv_priceSpread;//余额差价
    private TextView tv_integralSpread;//积分差价
    private TextView tv_priceSpread2;//支付价格
    private TextView tv_integral;//积分
    private TextView tv_balance;//余额
    private MyTextView tv_outTimePrice;//超时费用
    private MyTextView tv_addPrice;//加急费
    private Double orderPrice;
    private Double outTimePrice;//超时费
    private Double addTimePrice;//加急费
    private Double priceSpread = 0d;//还有多少未支付，给第三方支付剩余金额
    private ColumnUtils columnUtils;
    private List<JSONObject> list_type = new ArrayList<>();//支付方式
    private LinearLayout layout_payType;
    private LinearLayout layout_payType2;
    private int last_clickPositon = -1;//上次选择的第三方位置
    private boolean isClick = true;//第三方是否可以选择
    private boolean isChooseLaundry = false;//是否选择洗衣点
    private boolean isChooseBalance = false;//是否选择余额
    private boolean isChooseintegral = false;//是否选择积分
    private boolean isChooseCard = false;//是否选择体验卡
    private boolean isChoosMouth = false;//选择包月

    private GetAgrement getAgrement_PayHelp;
    private boolean isVisbilityMouth = true;//是否显示包月

    public void setArtical_type(String artical_type) {
        this.artical_type = artical_type;
    }

    private String artical_type = "PayHelp";

    public Type_choose(Context context, String content) {
        super(context);
        this.mContext = context;
        setCanceledOnTouchOutside(true);
    }

    /**
     * updata 6.25 追加一个是否显示包月 ziduan boolean isVisbilityMouth
     *
     * @param context
     * @param theme
     * @param onclick
     * @param jsonObject
     * @param orderPrice
     * @param outTimePrice
     * @param addTimePrice
     */
    public Type_choose(Context context, int theme, Onclick onclick, JSONObject jsonObject, Double orderPrice, Double outTimePrice, Double addTimePrice, boolean isVisbilityMouth) {
        super(context, theme);
        this.mContext = context;
        this.onclick = onclick;
        this.outTimePrice = outTimePrice;
        this.addTimePrice = addTimePrice;
        this.jsonObject = jsonObject;
        this.orderPrice = orderPrice;
        this.isVisbilityMouth = isVisbilityMouth;
        setCanceledOnTouchOutside(true);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        m = getWindow().getWindowManager();
    }

    private void initView() {
        setContentView(R.layout.pay_typechoose);
        relayout_detail = (RelativeLayout) findViewById(R.id.relayout_detail);
        layout_payType = (LinearLayout) findViewById(R.id.layout_payType);
        tv_outTimePrice = (MyTextView) findViewById(R.id.tv_outTimePrice);
        tv_addPrice = (MyTextView) findViewById(R.id.tv_addPrice);
        layout_payType2 = (LinearLayout) findViewById(R.id.layout_payType2);
        tv_priceSpread2 = (TextView) findViewById(R.id.tv_priceSpread2);
        tv_pay = (TextView) findViewById(R.id.tv_pay);
        tv_integral = (TextView) findViewById(R.id.tv_integral);
        tv_balance = (TextView) findViewById(R.id.tv_balance);
        tv_integralSpread = (TextView) findViewById(R.id.tv_integralSpread);
        tv_priceSpread = (TextView) findViewById(R.id.tv_priceSpread);
        layouViews = new RelativeLayout[layoutIds.length];
        ctViews = new CheckedTextView[ctId.length];
        relayout_priceSpread = (RelativeLayout) findViewById(R.id.relayout_priceSpread);
        for (int i = 0; i < layoutIds.length; i++) {
            layouViews[i] = (RelativeLayout) findViewById(layoutIds[i]);
            layouViews[i].setOnClickListener(this);
            ctViews[i] = (CheckedTextView) findViewById(ctId[i]);
        }
        tv_pay.setOnClickListener(this);
        relayout_detail.setOnClickListener(this);
        findViewById(R.id.relayout_close).setOnClickListener(this);
        findViewById(R.id.tv_help).setOnClickListener(this);
        try {
            if (outTimePrice != 0 && addTimePrice == 0) {
                tv_outTimePrice.setVisibility(View.VISIBLE);
                tv_outTimePrice.setSpecifiedTextsColor("（含超时费" + outTimePrice + "元）", outTimePrice + "", Color.parseColor("#ff6600"));
                tv_addPrice.setVisibility(View.GONE);
            } else if (addTimePrice != 0) {
                tv_outTimePrice.setVisibility(View.VISIBLE);
                tv_addPrice.setVisibility(View.VISIBLE);
                tv_outTimePrice.setSpecifiedTextsColor("（含超时费" + outTimePrice + "元、", outTimePrice + "", Color.parseColor("#ff6600"));
                tv_addPrice.setSpecifiedTextsColor("加急费" + addTimePrice + "元）", addTimePrice + "", Color.parseColor("#ff6600"));
            } else if (addTimePrice == 0) {
                tv_outTimePrice.setVisibility(View.GONE);
                tv_addPrice.setVisibility(View.GONE);
            } else {
                tv_outTimePrice.setVisibility(View.GONE);
                tv_addPrice.setVisibility(View.VISIBLE);
                tv_outTimePrice.setSpecifiedTextsColor("（含加急费" + outTimePrice + "元）", outTimePrice + "", Color.parseColor("#ff6600"));
            }
            tv_priceSpread2.setText(setMoney2(orderPrice) + "元");
            tv_priceSpread.setText(setMoney2(orderPrice) + "元");
            priceSpread = Double.parseDouble(setMoney2(orderPrice));
            tv_integralSpread.setText(setMoney2(orderPrice * MyApplication.getConfigrationJson().getJSONObject("other").getInt("inter_rate")) + "分");
            tv_integral.setText("（" + setMoney2(jsonObject.getDouble("interal") / 1000) + "分）");
            tv_balance.setText("（" + setMoney2(jsonObject.getDouble("money") / 1000) + "元）");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (columnUtils == null) {
            columnUtils = new ColumnUtils(getContext(), new ColumnUtils.Result() {
                @Override
                public void onSuccess(String title, JSONArray jsonArray) {
                    Log.d("jsontype",jsonArray.toString());
                    List<JSONObject> list_type2 = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            if (jsonArray.getJSONObject(i).getString("type").contains("alipay") ||
                                    jsonArray.getJSONObject(i).getString("type").contains("weixin")) {
                                list_type2.add(jsonArray.getJSONObject(i));
                            } else {
                                if (isVisbilityMouth) {
                                    list_type.add(jsonArray.getJSONObject(i));
                                    addPayTypeView(jsonArray.getJSONObject(i));
                                } else {
                                    if (!jsonArray.getJSONObject(i).getString("type").contains("monthly")&&!jsonArray.getJSONObject(i).getString("type").contains("clothes")) {
                                        list_type.add(jsonArray.getJSONObject(i));
                                        addPayTypeView(jsonArray.getJSONObject(i));
                                    }
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    for (int i = 0; i < list_type2.size(); i++) {
                        list_type.add(list_type2.get(i));
                        addPayTypeView2(list_type2.get(i), i);
                    }
//                    showPopwindow(title);
                }

                @Override
                public void onFailure(String msg) {
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (list_type.size() == 0) {
            columnUtils.getPayList("");
        }
    }

    /**
     * 四舍五入，两位小数
     */
    private String setMoney2(Double money) {
        String s = new java.text.DecimalFormat("#.00").format(money);
        if (s.contains(".00")) {
            s = s.substring(0, s.length() - 3);
        }
        return s;
    }

    /**
     * 根据选中状态获取差价
     */
    private void getPrice() {
        Double price = 0d;
        if(isChoosMouth){
            //包月支付已选
            paytype = "monthly" + ",";
        }else {
            paytype = "";
        }
        try {
            if (isChooseBalance) {
                price = price + jsonObject.getDouble("money") / 1000;
                paytype = paytype + "money" + ",";
            }
            if (isChooseintegral) {
                price = price + jsonObject.getDouble("interal") / 1000 / MyApplication.getConfigrationJson().getJSONObject("other").getInt("inter_rate");
                paytype = paytype + "interal" + ",";
            }
            if (isChooseLaundry) {
                //洗衣点
                price = price + jsonObject.getDouble("clothes") / 1000 / MyApplication.getConfigrationJson().getJSONObject("other").getInt("clothes_rate");
                paytype = paytype + "clothes" + ",";
            }
            if(isChooseCard){
                //体验卡
                price = price + jsonObject.getDouble("card") / 1000;
                paytype = paytype + "card" + ",";
            }
//            if(isChoosMouth){
//                //包月支付
//                price = orderPrice;
//                paytype = paytype+"clothes" + ",";
//            }
            if(isChoosMouth){
                tv_priceSpread.setText(0 + "元");
                if(price >= orderPrice){
                    setCheck(false);
                }else {
                    setCheck(true);
                }
                priceSpread = 0d;
            }else {
                if (price >= orderPrice) {
                    setCheck(false);
                    tv_priceSpread.setText(0 + "元");
                    priceSpread = 0d;
                } else {
                    setCheck(true);
                    tv_priceSpread.setText(setMoney2(orderPrice - price) + "元");
                    priceSpread = orderPrice - price;
                }
            }

        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout1:
                //余额支付
                ctViews[0].setChecked(!ctViews[0].isChecked());
                getPrice();

                break;
            case R.id.layout2:
                //积分支付
                ctViews[1].setChecked(!ctViews[1].isChecked());
                getPrice();
                break;
            case R.id.tv_pay:
                //支付
                v.startAnimation(new AnimationUtils().loadAnimation(getContext(), R.anim.bg_alpha));
                if (StringUtils.isEmpty(payother) && StringUtils.isEmpty(paytype)) {
                    Toast.makeText(getContext(), "请至少选择一种支付方式", Toast.LENGTH_SHORT).show();
                } else {
//                    if (isChoosMouth) {
//                        v.setClickable(false);
//                        onclick.OnClickLisener(v);
//                        if (!StringUtils.isEmpty(paytype)) {
//                            onclick.conten(paytype.substring(0, paytype.length() - 1), payother, v);
//                        } else {
//                            onclick.conten(paytype, payother, v);
//                        }
//                    } else {
                        if (priceSpread > 0 && StringUtils.isEmpty(payother)) {
                            Toast.makeText(getContext(), "请再选择一种第三方支付", Toast.LENGTH_SHORT).show();
                            v.setClickable(true);
                        } else {
                            v.setClickable(false);
                            onclick.OnClickLisener(v);
                            if (!StringUtils.isEmpty(paytype)) {
                                onclick.conten(paytype.substring(0, paytype.length() - 1), payother, v);
                            } else {
                                onclick.conten(paytype, payother, v);
                            }
                        }
                   // }
                }
                break;
            case R.id.relayout_close:
                v.startAnimation(new AnimationUtils().loadAnimation(getContext(), R.anim.bg_alpha));
                Type_choose.this.dismiss();
                break;
            case R.id.tv_help:
                v.startAnimation(new AnimationUtils().loadAnimation(getContext(), R.anim.bg_alpha));
                if (getAgrement_PayHelp == null) {
                    getAgrement_PayHelp = new GetAgrement(getContext());
                }
                getAgrement_PayHelp.getAggrement_Detail(null, artical_type, "我知道了");
                break;
            case R.id.relayout_detail:
                //查看订单详情
                onclick.OnClickLisener(v);
                onclick.conten(paytype, payother, v);
//                Bundle bundle = new Bundle();
//                try {
//                    bundle.putString("order_id",jsonObject.getString("id"));
//                    bundle.putString("number", jsonObject.getString("number"));
//                    bundle.putString("status", jsonObject.getString("status"));
//                    bundle.putString("additional",jsonObject.getString("additional"));
//                    bundle.putInt("type",jsonObject.getInt("status"));
//                    bundle.putString("ark_name", jsonObject.getString("ark_name") +" "+ jsonObject.getString("ark_box_number"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                Intent intent  = new Intent(getContext(), OrderDetailActivity.class);
//                getContext().startActivity(intent.putExtra("bundle",bundle));
                break;
        }
    }

    /**
     * 第三方选则改变,是否可选
     */
    private void setCheck(boolean isClick) {
        this.isClick = isClick;
        if (isClick) {

            for (int i = 0; i < layout_payType2.getChildCount(); i++) {
                try {
                    layout_payType2.getChildAt(i).setClickable(true);

                    layout_payType2.getChildAt(i).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            cancelClick();
            for (int i = 0; i < layout_payType2.getChildCount(); i++) {
                try {
                    layout_payType2.getChildAt(i).setClickable(false);
                    layout_payType2.getChildAt(i).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.fontcolor_f2));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        isClickZFB = false;
        isClickWX = false;

    }

    public interface Onclick {
        void OnClickLisener(View v);

        //内容
        void conten(String payType, String payOther, View view);
    }

    /**
     * 动态添加三方支付类型控件
     */
    private void addPayTypeView(final JSONObject json) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_paytype, null);
        final CheckedTextView ct_payType = (CheckedTextView) view.findViewById(R.id.ct_payType);
        final ImageView img_pay = (ImageView) view.findViewById(R.id.img_pay);
        TextView tv_money = (TextView) view.findViewById(R.id.tv_money);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        view.setLayoutParams(layoutParams);
        try {
            tv_title.setText(json.getString("name"));
            Picasso.with(getContext()).load(RequestTag.BaseImageUrl + json.getString("mobile_logo_url")).placeholder(R.mipmap.default_iv).into(img_pay, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    try {
                        Picasso.with(getContext()).load(RequestTag.BaseImageUrl + MyApplication.getConfigrationJson().getJSONObject("other").getString("default_img")).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).into(img_pay);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {

        }
        layout_payType.addView(view);
        try {
            if (json.getString("name").contains("洗衣")) {
                tv_money.setVisibility(View.VISIBLE);
                tv_money.setText("（" + jsonObject.getDouble("clothes") / 1000 + "点）");
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //洗衣点支付
                        if (ct_payType.isChecked()) {
                            isChooseLaundry = false;
                            ct_payType.setChecked(false);
                        } else {
                            ct_payType.setChecked(true);
                            isChooseLaundry = true;
                        }
                        getPrice();
                    }
                });
            } else if (json.getString("name").contains("余额")) {
                tv_money.setVisibility(View.VISIBLE);
                tv_money.setText("（" + jsonObject.getDouble("money") / 1000 + "元）");
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ct_payType.setChecked(!ct_payType.isChecked());
                        if (ct_payType.isChecked()) {
                            isChooseBalance = true;
                        } else {
                            isChooseBalance = false;
                        }
                        getPrice();
                    }
                });

            } else if (json.getString("name").contains("积分")) {
                tv_money.setVisibility(View.VISIBLE);
                tv_money.setText("（" + jsonObject.getDouble("interal") / 1000 + "分）");
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ct_payType.setChecked(!ct_payType.isChecked());
                        if (ct_payType.isChecked()) {
                            isChooseintegral = true;
                        } else {
                            isChooseintegral = false;
                        }
                        getPrice();
                    }
                });
            }else if(json.getString("type").equals("card")) {
                tv_money.setVisibility(View.VISIBLE);
                tv_money.setText("（" + jsonObject.getDouble("card") / 1000 + "元）");
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ct_payType.setChecked(!ct_payType.isChecked());
                        if (ct_payType.isChecked()) {
                            isChooseCard = true;
                        } else {
                            isChooseCard = false;
                        }
                        getPrice();
                    }
                });
            }
            else if (json.getString("type").equals("monthly")) {
                tv_money.setVisibility(View.VISIBLE);
                tv_money.setText("（" + json.getString("date") + "）");

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (json.getString("date").contains("无")) {
                                Toast.makeText(getContext(), "你没有充值包月套餐", Toast.LENGTH_SHORT).show();
                            } else {
                                ct_payType.setChecked(!ct_payType.isChecked());
//                                if (ct_payType.isChecked()) {
//                                    isChoosMouth = true;
//                                    priceSpread_last = priceSpread;
//                                    priceSpread = 0d;
//                                    tv_priceSpread.setText(0 + "元");
//                                    if(priceSpread_last==0){
//                                        setCheck(false);
//                                    }else {
//                                        setCheck(true);
//                                    }
//                                    paytype = paytype+"monthly" + ",";
//                                } else {
//                                    isChoosMouth = false;
//                                    priceSpread = priceSpread_last;
//                                    tv_priceSpread.setText(priceSpread + "元");
//                                    if(priceSpread>0){
//                                        setCheck(true);
//                                    }else{
//                                        setCheck(false);
//                                    }
//                                    paytype.replace("monthly,","");
//                                }
                                if (ct_payType.isChecked()) {
                                    isChoosMouth = true;
                                } else {
                                    isChoosMouth = false;
                                }
                                getPrice();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 动态添加三方支付类型控件,微信，支付宝
     */
    private void addPayTypeView2(final JSONObject json, final int position) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_paytype2, null);
        final CheckedTextView ct_payType = (CheckedTextView) view.findViewById(R.id.ct_payType);
        final ImageView img_pay = (ImageView) view.findViewById(R.id.img_pay);
        TextView tv_money = (TextView) view.findViewById(R.id.tv_money);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        view.setLayoutParams(layoutParams);
        try {
            tv_title.setText(json.getString("name"));
            Picasso.with(getContext()).load(RequestTag.BaseImageUrl + json.getString("mobile_logo_url")).placeholder(R.mipmap.default_iv).into(img_pay, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    try {
                        Picasso.with(getContext()).load(RequestTag.BaseImageUrl + MyApplication.getConfigrationJson().getJSONObject("other").getString("default_img")).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).into(img_pay);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {

        }
        layout_payType2.addView(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClick) {
                    try {
                        if (ct_payType.isChecked()) {
                            payother = "";
                            last_clickPositon = -1;
                            if (json.getString("name").contains("微信")) {
                                isClickWX = false;

                            } else if (json.getString("name").contains("支付宝")) {
                                isClickZFB = false;
                            }
                            ct_payType.setChecked(false);
                        } else {
                            payother = json.getString("type");
                            cancelClick();
                            last_clickPositon = position;
                            if (json.getString("name").contains("微信")) {
                                isClickWX = true;
                            } else if (json.getString("name").contains("支付宝")) {
                                isClickZFB = true;
                            }
                            ct_payType.setChecked(true);
                        }
                    } catch (Exception e) {

                    }
                }
            }
        });
    }

    /**
     * 取消上次选择的位置勾选
     */
    private void cancelClick() {
        if (last_clickPositon != -1) {
            ((CheckedTextView) layout_payType2.getChildAt(last_clickPositon).findViewById(R.id.ct_payType)).setChecked(false);
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
        MyOkHttp.get().post(getContext(), RequestTag.BaseUrl + RequestTag.QUARY_MONEY, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if (response.getString("status").equals("0")) {
                        MyApplication.getUserJson().put("money", response.getJSONObject("data").getLong("money"));
                        MyApplication.getUserJson().put("clothes", response.getJSONObject("data").getLong("clothes"));
                        MyApplication.getUserJson().put("interal", response.getJSONObject("data").getLong("interal"));
                        MyApplication.getUserJson().put("card", response.getJSONObject("card").getLong("card"));
                    } else {
                        // toastMessage(response.getString("msg"));
                        Toast.makeText(getContext(), response.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                Toast.makeText(getContext(), "网络异常，请检查网络后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
