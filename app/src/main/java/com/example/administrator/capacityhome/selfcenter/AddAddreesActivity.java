package com.example.administrator.capacityhome.selfcenter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.example.administrator.capacityhome.BaseActivity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import http.RequestTag;
import utils.ACache;
import utils.CityList;
import utils.CityPop_show;
import utils.FormatUtil;
import utils.KeyBordUtil;
import utils.Md5;
import utils.SPUtils;
import utils.StringUtils;

/**
 * 添加地址
 */
public class AddAddreesActivity extends BaseActivity implements View.OnClickListener {
    private TextView addAddress_tv_right;//头部右边按钮
    private RelativeLayout relayout_back;//头部左边按钮
    private TextView addAddress_tv_title;//名称
    private LinearLayout layout_parent;
    private EditText addAddress_et_userName;//用户名
    private EditText addAddress_et_phoneNumber;//手机号
    private EditText addAddress_et_emailNumber;//邮编
    private TextView addAddress_tv_address;//地址
    private EditText addAddress_et_addressDetail;//详细地址
    private String type;//来源类型
    private RoutePlanSearch mSerch;
    private OnGetRoutePlanResultListener listener;
    private JSONObject json = new JSONObject();
    private String area_id;//城市id
    private CityPop_show cityPop_show;//选择城市
    private ACache aCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_addrees);
        initView();
    }

    private void initView() {
        aCache = ACache.get(this);
        layout_parent = (LinearLayout) findViewById(R.id.layout_parent);
        addAddress_tv_title = (TextView) findViewById(R.id.addAddress_tv_title);
        addAddress_tv_right = (TextView) findViewById(R.id.addAddress_tv_right);
        relayout_back = (RelativeLayout) findViewById(R.id.relayout_back);
        addAddress_tv_right.setOnClickListener(this);
        relayout_back.setOnClickListener(this);
        findViewById(R.id.relayout_chooseAddress).setOnClickListener(this);
        addAddress_et_userName = (EditText) findViewById(R.id.addAddress_et_userName);
        addAddress_et_phoneNumber = (EditText) findViewById(R.id.addAddress_et_phoneNumber);
        addAddress_et_emailNumber = (EditText) findViewById(R.id.addAddress_et_emailNumber);
        addAddress_tv_address = (TextView) findViewById(R.id.addAddress_tv_address);
        addAddress_et_addressDetail = (EditText) findViewById(R.id.addAddress_et_addressDetail);
        type = getIntent().getBundleExtra("bundle").getString("add");
        if (type != null && !type.equals("add")) {
            //修改
            addAddress_tv_right.setText("保存");
            addAddress_tv_title.setText("修改地址");
            try {

                if (!StringUtils.isEmpty(getIntent().getBundleExtra("bundle").getString("name"))) {
                    addAddress_et_userName.setText(getIntent().getBundleExtra("bundle").getString("name"));
                    json.put("name", getIntent().getBundleExtra("bundle").getString("name"));
                } else {
                    json.put("name", "未设置");
                    addAddress_et_userName.setText("未设置");
                }
                if (StringUtils.isEmpty(getIntent().getBundleExtra("bundle").getString("phone"))) {
                    addAddress_et_phoneNumber.setText("未设置");
                    json.put("tel", "未设置");
                } else {
                    addAddress_et_phoneNumber.setText(getIntent().getBundleExtra("bundle").getString("phone"));
                    json.put("tel", getIntent().getBundleExtra("bundle").getString("phone"));
                }
                if (StringUtils.isEmpty(getIntent().getBundleExtra("bundle").getString("email_number"))) {
                    addAddress_et_emailNumber.setText("未设置");
                    json.put("code", "未设置");
                } else {
                    addAddress_et_emailNumber.setText(getIntent().getBundleExtra("bundle").getString("email_number"));
                    json.put("code", getIntent().getBundleExtra("bundle").getString("email_number"));
                }
                if (StringUtils.isEmpty(getIntent().getBundleExtra("bundle").getString("address_city"))) {
                    addAddress_tv_address.setText("未设置");
                } else {
                    area_id = getIntent().getBundleExtra("bundle").getString("address_city");
                    if (SPUtils.getSharedStringData(this, getIntent().getBundleExtra("bundle").getString("address_city")) == null) {
                        CityList cityList = new CityList(this, new CityList.ResultAddress() {
                            @Override
                            public void onSuccess(String address) {
                                addAddress_tv_address.setText(address);

                            }

                            @Override
                            public void onFailure() {

                            }
                        });
                        cityList.getCityList_detail(getIntent().getBundleExtra("bundle").getString("address_city"));
                    } else {
                        addAddress_tv_address.setText(SPUtils.getSharedStringData(this, getIntent().getBundleExtra("bundle").getString("address_city")));
                    }
                }
                if (StringUtils.isEmpty(getIntent().getBundleExtra("bundle").getString("address_detail"))) {
                    addAddress_et_addressDetail.setText("未设置");
                } else {
                    addAddress_et_addressDetail.setText(getIntent().getBundleExtra("bundle").getString("address_detail"));
                }
            } catch (Exception e) {

            }

        } else {
            //添加
            addAddress_tv_right.setText("添加");
            addAddress_tv_title.setText("添加地址");
        }
    }

    private void setData() {
        try {
            json.put("name", addAddress_et_userName.getText().toString());
            json.put("tel", addAddress_et_phoneNumber.getText().toString());
            json.put("code", addAddress_et_emailNumber.getText().toString());
            json.put("address", addAddress_et_addressDetail.getText().toString());
            json.put("area_id", area_id);
            if (getIntent().getBundleExtra("bundle").getString("id") != null) {
                json.put("id", getIntent().getBundleExtra("bundle").getString("id"));
            } else {
                json.put("id", "");//等待后台返回
            }
        } catch (Exception e) {
            toastMessage("数据解析异常");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addAddress_tv_right:
                if(isUpdate()) {
                    if (type != null && !type.equals("add")) {
                        //修改
                        addded("modify");
                    } else {
                        //添加
                        addded("add");
                    }
                }
                break;
            case R.id.relayout_back:
                finish();
                break;
            case R.id.relayout_chooseAddress:
                //城市选择
                if (cityPop_show == null) {
                    cityPop_show = new CityPop_show(this, layout_parent, aCache, new CityPop_show.Result_areaId() {
                        @Override
                        public void resultSuccess(String area_id2, String title) {
                            if (!StringUtils.isEmpty(title)) {
                                addAddress_tv_address.setText(title);
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

        }
    }

    /**
     * 判断数据的输入格式是否争取
     */
    private boolean isUpdate() {
        if (StringUtils.isEmpty(addAddress_et_userName.getText().toString().trim())) {
            toastMessage("请输入用户名");
            return false;
        }
        if (!FormatUtil.isMobileNO(addAddress_et_phoneNumber.getText().toString().trim())) {
            toastMessage("请填写正确的手机号");
            return false;
        }
        if (StringUtils.isEmpty(area_id)) {
            toastMessage("请选择地区");
            return false;
        }

        if (StringUtils.isEmpty(addAddress_et_addressDetail.getText().toString().trim())) {
            toastMessage("请填写详细地址");
            return false;
        }
        return true;
    }

    /**
     * 添加修改地址
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
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.ADDED_ADDRESS, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    toastMessage(response.getString("msg"));
                    if (response.getString("status").equals("0")) {
                        json = response.getJSONObject("data");
                        Intent intent = new Intent();
                        intent.putExtra("json", json.toString());
                        dismissLoadingView();
                        if (added.equals("modify")) {
                            setResult(3, intent);
                            finish();
                        } else {
                            setResult(4, intent);
                            finish();
                        }
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
}
