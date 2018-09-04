package com.example.administrator.capacityhome.selfcenter;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.capacityhome.BaseActivity;
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

import adapter.PopListAdapter;
import http.RequestTag;
import utils.ACache;
import utils.CityList;
import utils.CityPop_show;
import utils.ColumnUtils;
import utils.DisplayUtil;
import utils.FormatUtil;
import utils.KeyBordUtil;
import utils.Md5;
import utils.StringUtils;
import widget.CircleTransform;

/**
 * 添加银行卡
 */
public class AddBankActivity extends BaseActivity implements View.OnClickListener {
    private EditText addBank_et_name;
    private TextView addBank_tv_bankName;
    private TextView addBank_tv_city;
    private ImageView img_bank;//银行卡图标
    private EditText addBank_et_shop;
    private EditText addBank_et_bankNumber;
    private TextView pop_tvWithdrawal;//提现记录
    private TextView pop_tvConsumption;//洗衣卷消费
    private TextView pop_tvRecharge;//余额充值
    private TextView pop_tvBalance;//余额消费
    private PopupWindow popupWindow;
    private RelativeLayout relayout_bankType;
    private JSONObject json = new JSONObject();
    private PopupWindow popWindow;
    private PopListAdapter popListAdapter;
    private ListView layout_pop_list;
    private List<JSONObject> list_bank = new ArrayList<>();
    private LinearLayout layout_parent;
    private ColumnUtils columnUtils;
    private TextView tv_popTitle;//选择器标题
    private String type;//银行卡类型
    private String area_id;//城市id
    private PopupWindow popWindow_city;
    private ACache aCache;
    private CityPop_show cityPop_show;//选择城市
    private String type_add = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank);
        initView();
    }

    private void initView() {
        aCache = ACache.get(this);
        findViewById(R.id.relayout_back).setOnClickListener(this);
        findViewById(R.id.relayout_bankCity).setOnClickListener(this);
        img_bank = (ImageView) findViewById(R.id.img_bank);
        layout_parent = (LinearLayout) findViewById(R.id.layout_parent);
        addBank_tv_city = (TextView) findViewById(R.id.addBank_tv_city);
        addBank_et_name = (EditText) findViewById(R.id.addBank_et_name);
        addBank_tv_bankName = (TextView) findViewById(R.id.addBank_tv_bankName);
        addBank_et_shop = (EditText) findViewById(R.id.addBank_et_shop);
        addBank_et_bankNumber = (EditText) findViewById(R.id.addBank_et_bankNumber);
        relayout_bankType = (RelativeLayout) findViewById(R.id.relayout_bankType);
        relayout_bankType.setOnClickListener(this);
        type_add = getIntent().getBundleExtra("bundle").getString("add");
        if (type_add != null && !type_add.equals("add")) {
            //修改
            setPageTitle("修改银行卡");
            submitBtnVisible("保存", this);
            if (!StringUtils.isEmpty(getIntent().getBundleExtra("bundle").getString("id"))) {
                getBankDetail(getIntent().getBundleExtra("bundle").getString("id"));
            }

        } else {
            submitBtnVisible("提交", this);
            setPageTitle("添加银行卡");
            //添加
        }
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(new AnimationUtils().loadAnimation(this, R.anim.bg_alpha));
        switch (v.getId()) {
            case R.id.relayout_back:
                finish();
                break;
            case R.id.top_submit_btn:
                if (type_add != null && !type_add.equals("add")) {
                    //修改
                    if (isUpdate()) {
                        addded("modify");
                    }
                } else {
                    //添加
                    if (isUpdate()) {
                        addded("add");
                    }
                }
                break;
            case R.id.relayout_bankCity:
                //选择开户城市
                if (cityPop_show == null) {
                    cityPop_show = new CityPop_show(this, layout_parent, aCache, new CityPop_show.Result_areaId() {
                        @Override
                        public void resultSuccess(String area_id2, String title) {
                            if (!StringUtils.isEmpty(title)) {
                                addBank_tv_city.setText(title);
                            }
                            area_id = area_id2;
                        }

                        @Override
                        public void resultFail(String msg) {
                            toastMessage(msg);
                        }
                    });
                }
                KeyBordUtil.hintKeyboard(this);
                cityPop_show.showPopwindow();
                break;
            case R.id.relayout_bankType:
                //选择银行类型
                //showPopWindow(getView());
                if (columnUtils == null) {
                    columnUtils = new ColumnUtils(this, new ColumnUtils.Result() {
                        @Override
                        public void onSuccess(String title, JSONArray jsonArray) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    list_bank.add(jsonArray.getJSONObject(i));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            showPopwindow(title);
                        }

                        @Override
                        public void onFailure(String msg) {
                            toastMessage(msg);
                        }
                    });
                }
                if (list_bank.size() == 0) {
                    columnUtils.getColumn("", "bank");
                } else {
                    showPopwindow(tv_popTitle.getText().toString());
                }
                break;
            case R.id.pop_tvWithdrawal:
                //提现记录
                addBank_tv_bankName.setText("中国工商银行");
                popupWindow.dismiss();
                break;
            case R.id.pop_tvRecharge:
                //余额充值
                addBank_tv_bankName.setText("中国招商银行");
                popupWindow.dismiss();
                break;
            case R.id.pop_tvConsumption:
                //洗衣卷消费
                addBank_tv_bankName.setText("中国建设银行");
                popupWindow.dismiss();
                break;
            case R.id.pop_tvBalance:
                //余额消费
                addBank_tv_bankName.setText("中国农业银行");
                popupWindow.dismiss();
                break;
        }
    }

    /**
     * 判断数据的输入格式是否争取
     */
    private boolean isUpdate() {
        if (StringUtils.isEmpty(addBank_et_name.getText().toString().trim())) {
            toastMessage("请输入用户名");
            return false;
        }
        if (StringUtils.isEmpty(type)) {
            toastMessage("请选择银行卡类型");
            return false;
        }
        if (StringUtils.isEmpty(addBank_et_shop.getText().toString().trim())) {
            toastMessage("请填写开户网点");
            return false;
        }
        if (StringUtils.isEmpty(area_id)) {
            toastMessage("请选择开户城市");
            return false;
        }
        if (StringUtils.isEmpty(addBank_et_bankNumber.getText().toString().trim()) && FormatUtil.checkBankCard(addBank_et_bankNumber.getText().toString())) {
            toastMessage("请输入正确的银行卡号");
            return false;
        }
        return true;
    }

    private void showPopWindow(View view) {
        try {
            int width = DisplayUtil.getScreenWidth(this);
            popupWindow = new PopupWindow(view, width,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
            ColorDrawable dw = new ColorDrawable(0x30000000);// 背景颜色全透明
            popupWindow.setBackgroundDrawable(dw);
            int[] location = new int[2];
            relayout_bankType.getLocationOnScreen(location);
            popupWindow.setAnimationStyle(R.style.style_pop_animation);// 动画效果必须放在showAsDropDown()方法上边，否则无效
            backgroundAlpha(0.7f);// 设置背景半透明
            popupWindow.showAsDropDown(relayout_bankType);
            //popupWindow.showAtLocation(tv_pop, Gravity.NO_GRAVITY, location[0]+tv_pop.getWidth(),location[1]);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

                @Override
                public void onDismiss() {
                    popupWindow = null;// 当点击屏幕时，使popupWindow消失
                    backgroundAlpha(1.0f);// 当点击屏幕时，使半透明效果取消
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 设置popupWindow背景半透明
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;// 0.0-1.0
        getWindow().setAttributes(lp);
    }

    /*
     * 得到popupwindow的View
     */
    private View getView() {
        View view = LayoutInflater.from(this).inflate(
                R.layout.moneydetail_pop, null);
        pop_tvWithdrawal = (TextView) view.findViewById(R.id.pop_tvWithdrawal);
        pop_tvConsumption = (TextView) view.findViewById(R.id.pop_tvConsumption);
        pop_tvBalance = (TextView) view.findViewById(R.id.pop_tvBalance);
        pop_tvRecharge = (TextView) view.findViewById(R.id.pop_tvRecharge);
        pop_tvWithdrawal.setText("中国工商银行");
        pop_tvConsumption.setText("中国建设银行");
        pop_tvBalance.setText("中国农业银行");
        pop_tvRecharge.setText("中国招商银行");
        pop_tvRecharge.setOnClickListener(this);
        pop_tvBalance.setOnClickListener(this);
        pop_tvConsumption.setOnClickListener(this);
        pop_tvWithdrawal.setOnClickListener(this);
        return view;
    }

    /**
     * 根据id获取银行卡详情
     */
    private void getBankDetail(String bank_id) {
        showLoadingView();
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
            jsonObject.put("id", bank_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.Bank_DETAIL, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, final JSONObject response) {
                try {
                    if (response.getInt("status") != 0) {
                        toastMessage(response.getString("msg"));
                    } else {
                        json = response.getJSONObject("data");
                        area_id = json.getString("city");
                        type = json.getString("type");
                        if (StringUtils.isEmpty(response.getJSONObject("data").getString("name"))) {
                            addBank_et_name.setText("未编辑");
                        } else {
                            addBank_et_name.setText(response.getJSONObject("data").getString("name"));
                        }
                        if (StringUtils.isEmpty(response.getJSONObject("data").getString("title"))) {
                            addBank_tv_bankName.setText("未编辑");
                        } else {
                            addBank_tv_bankName.setText(response.getJSONObject("data").getString("title"));
                        }
                        if (StringUtils.isEmpty(response.getJSONObject("data").getString("country"))) {
                            addBank_tv_city.setText("未编辑");
                        } else {
                            CityList cityList = new CityList(AddBankActivity.this, new CityList.ResultAddress() {
                                @Override
                                public void onSuccess(String address) {
                                    addBank_tv_city.setText(address);
                                    dismissLoadingView();
                                }

                                @Override
                                public void onFailure() {
                                    dismissLoadingView();
                                }
                            });
                            cityList.getCityList_detail(response.getJSONObject("data").getString("country"));
                        }
                        if (!StringUtils.isEmpty(response.getJSONObject("data").getString("address"))) {
                            addBank_et_shop.setText(response.getJSONObject("data").getString("address"));
                        } else {
                            addBank_et_shop.setHint("未编辑");
                        }
                        if (StringUtils.isEmpty(response.getJSONObject("data").getString("number"))) {
                            addBank_et_bankNumber.setText("未编辑");
                        } else {
                            addBank_et_bankNumber.setText(response.getJSONObject("data").getString("number").replaceAll("\\d{4}(?!$)", "$0 "));
                        }
                    }
                    if (!StringUtils.isEmpty(response.getJSONObject("data").getString("pic"))) {
                        img_bank.setVisibility(View.VISIBLE);
                        //  Picasso.with(context).load(RequestTag.BaseImageUrl+list.get(position).getString("pic")).error(R.mipmap.img_user).placeholder(R.mipmap.default_iv).transform(new CircleTransform()).into(img_bank);
                        Picasso.with(AddBankActivity.this).load(RequestTag.BaseImageUrl + response.getJSONObject("data").getString("pic")).placeholder(R.mipmap.default_iv).transform(new CircleTransform()).into(img_bank, new Callback() {
                            @Override
                            public void onSuccess() {
                                img_bank.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError() {
                                try {
                                    img_bank.setVisibility(View.VISIBLE);
                                    Picasso.with(AddBankActivity.this).load(RequestTag.BaseImageUrl + MyApplication.getConfigrationJson().getJSONObject("other").getString("default_img")).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).into(img_bank);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        Picasso.with(AddBankActivity.this).load(R.mipmap.default_iv).into(img_bank);
                        // img_bank.setImageResource(R.mipmap.default_iv);
                    }
                } catch (Exception e) {
                    dismissLoadingView();
                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("网络异常，请检查网络后重试");
                dismissLoadingView();
            }
        });
    }

    private void setData() {
        try {
            json.put("type", type);
            json.put("name", addBank_et_name.getText().toString());
            json.put("number", addBank_et_bankNumber.getText().toString().trim().replaceAll("\\s*", ""));
            json.put("area_id", area_id);
            json.put("address", addBank_et_shop.getText().toString());
            json.put("default", "0");//默认银行卡传1
        } catch (Exception e) {

        }
    }

    /**
     * 添加修改银行卡
     */
    private void addded(final String added) {
        showLoadingView();
        KeyBordUtil.hintKeyboard(this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        try {
            json.put("device_id", MyApplication.getDeviceId());
            json.put("uid", MyApplication.getUid());
            json.put("username", MyApplication.getUserJson().getString("m_username"));
            json.put("pwd", MyApplication.getPasswprd());
            setData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", json.toString());
        String sign = Md5.md5(json.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.Bank_ADDED, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    dismissLoadingView();
                    toastMessage(response.getString("msg"));
                    if (response.getString("status").equals("0")) {
                        json = response.getJSONObject("data");
                        Intent intent = new Intent();
                        json.put("title", addBank_tv_bankName.getText().toString());
                        intent.putExtra("json", json.toString());
                        if (added.equals("modify")) {
                            setResult(2, intent);
                            finish();
                        } else {
                            setResult(3, intent);
                            finish();
                        }
                    }

                } catch (Exception e) {
                    toastMessage("数据解析异常");
                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("网络异常，请检查网络后重试");
                dismissLoadingView();
            }
        });
    }

    private void showPopwindow(String title) {
        KeyBordUtil.hintKeyboard(this);
        if (popWindow == null) {
            layout_parent = (LinearLayout) findViewById(R.id.layout_parent);
            View popView = View.inflate(this, R.layout.layout_pop_list, null);
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.6);
            tv_popTitle = (TextView) popView.findViewById(R.id.tv_popTitle);
            layout_pop_list = (ListView) popView.findViewById(R.id.layout_pop_list);
            popListAdapter = new PopListAdapter(this, list_bank, true);
            layout_pop_list.setAdapter(popListAdapter);
            popWindow = new PopupWindow(popView, width, LinearLayout.LayoutParams.WRAP_CONTENT);
            popWindow.setAnimationStyle(R.style.popwin_anim_style);
            popWindow.setFocusable(true);
            popWindow.setOutsideTouchable(false);// 设置同意在外点击消失
            ColorDrawable dw = new ColorDrawable(0x30000000);
            popWindow.setBackgroundDrawable(dw);
            layout_pop_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        type = list_bank.get(position).getString("id");
                        addBank_tv_bankName.setText(list_bank.get(position).getString("title"));
                        if (!StringUtils.isEmpty(list_bank.get(position).getString("pic"))) {
                            //  Picasso.with(context).load(RequestTag.BaseImageUrl+list.get(position).getString("pic")).error(R.mipmap.img_user).placeholder(R.mipmap.default_iv).transform(new CircleTransform()).into(img_bank);
                            Picasso.with(AddBankActivity.this).load(RequestTag.BaseImageUrl + list_bank.get(position).getString("pic")).placeholder(R.mipmap.default_iv).transform(new CircleTransform()).into(img_bank, new Callback() {
                                @Override
                                public void onSuccess() {
                                    img_bank.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onError() {
                                    try {
                                        img_bank.setVisibility(View.VISIBLE);
                                        Picasso.with(AddBankActivity.this).load(RequestTag.BaseImageUrl + MyApplication.getConfigrationJson().getJSONObject("other").getString("default_img")).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).into(img_bank);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            Picasso.with(AddBankActivity.this).load(R.mipmap.default_iv).into(img_bank);
                            // img_bank.setImageResource(R.mipmap.default_iv);
                        }
                        popWindow.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                DisplayUtil.backgroundAlpha(1.0f, AddBankActivity.this);
            }
        });

        DisplayUtil.backgroundAlpha(0.2f, AddBankActivity.this);
        popWindow.showAtLocation(layout_parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }


}
