package com.example.administrator.capacityhome.selfcenter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
import utils.FormatUtil;
import utils.Md5;
import utils.SPUtils;
import utils.StringUtils;
import utils.Timer;

/**
 * 修改密码
 */
public class ModifyPasswordActivity extends BaseActivity implements View.OnClickListener {
    private EditText password_etPhone;
    private EditText password_etCode;
    private EditText password_etNewPay;
    private EditText password_etNewPay2;
    private TextView password_getCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        initView();
    }

    private void initView() {
        findViewById(R.id.relayout_back).setOnClickListener(this);
        findViewById(R.id.password_tvEdit).setOnClickListener(this);
        password_etPhone = (EditText) findViewById(R.id.password_etPhone);
        password_etCode = (EditText) findViewById(R.id.password_etCode);
        password_etNewPay = (EditText) findViewById(R.id.password_etNewPay);
        password_etNewPay2 = (EditText) findViewById(R.id.password_etNewPay2);
        password_getCode = (TextView) findViewById(R.id.password_getCode);
        password_getCode.setOnClickListener(this);
        try {
            if (!StringUtils.isEmpty(MyApplication.getUserJson().getString("mobile"))) {
                password_etPhone.setText(MyApplication.getUserJson().getString("mobile"));
                password_etPhone.setSelection(password_etPhone.getText().length());
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relayout_back:
                finish();
                break;
            case R.id.password_getCode:
                if (password_etPhone.getText().toString().isEmpty() || !FormatUtil.isMobileNO(password_etPhone.getText().toString())) {
                    toastMessage("请填写正确的手机号");
                    break;
                }
                //获取验证码
                Timer.timeCount(password_getCode);
                getNoteCode();
                break;
            case R.id.password_tvEdit:
                //提交
                if (password_etPhone.getText().toString().isEmpty() || !FormatUtil.isMobileNO(password_etPhone.getText().toString())) {
                    toastMessage("请填写正确的手机号");
                    break;
                }
//                if(password_etCode.getText().toString().isEmpty()){
//                    toastMessage("请填写正确的验证码");
//                    break;
//                }
                if (!isTrue(password_etNewPay.getText().toString(), 6, 12)) {
                    toastMessage("请编写6-12位支付密码");
                    break;
                }
                if (!isTrue(password_etNewPay2.getText().toString(), 6, 12)) {
                    toastMessage("请编写6-12位支付密码");
                    break;
                }

                modifyPassword();
                break;
        }
    }

    /**
     * 判断数据位数是否正确
     */
    private boolean isTrue(String data, int lenght, int maxLenght) {
        if (StringUtils.isEmpty(data)) {
            return false;
        } else {
            if (data.length() < lenght) {
                return false;
            } else if (data.length() > maxLenght) {
                return false;
            }
        }
        return true;
    }

    /**
     * 修改登录密码
     */
    private void modifyPassword() {
        showLoadingView();
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject json = new JSONObject();
        try {
            json.put("device_id", MyApplication.getDeviceId());
            json.put("uid", MyApplication.getUid());
            json.put("password", password_etNewPay2.getText().toString());//新密码
            json.put("old", password_etNewPay.getText().toString());//原密码
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", json.toString());
        String sign = Md5.md5(json.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.SET_LOGIN_PASSWORD, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    Log.d("pasas",response.toString());
                    toastMessage(response.getString("msg"));
                    if (response.getString("status").equals("0")) {
                        SPUtils.setSharedStringData(MyApplication.getAppContext(),"password",response.getString("password"));
                        MyApplication.passwprd = response.getString("password");
                        dismissLoadingView();
                      finish();
                    }else{
                        dismissLoadingView();
                        password_getCode.setText("重新获取");
                        Timer.setViewClicble(password_getCode,true);
                    }

                } catch (Exception e) {
                    toastMessage("数据解析异常");
                    dismissLoadingView();
                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dismissLoadingView();
                toastMessage("网络异常，请检查网络后重试");
                Log.d("pasas",error_msg);

            }
        });
    }

    /**
     * 获取短信验证码
     */
    private void getNoteCode() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid", MyApplication.getUid());
            jsonObject.put("mobile", password_etPhone.getText().toString());
            jsonObject.put("code_type", "get_password");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.GETNOTECODE, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    toastMessage(response.getString("msg"));
                    if(response.getInt("status")!=0){
                        password_getCode.setText("重新获取");
                        Timer.setViewClicble(password_getCode,true);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {

            }
        });
    }
}
