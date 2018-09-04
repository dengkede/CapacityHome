package com.example.administrator.capacityhome.selfcenter;

import android.os.Bundle;
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
import utils.StringUtils;
import utils.Timer;

/**
 * 支付密码
 */
public class PayPasswordActivity extends BaseActivity implements View.OnClickListener {
    private EditText payPassword_etPhone;
    private EditText payPassword_etCode;
    private EditText payPassword_etNewPay;
    private EditText payPassword_etNewPay2;
    private TextView payPassword_getCode;//获取验证码
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_password);
        initView();
    }

    private void initView() {
        findViewById(R.id.relayout_back).setOnClickListener(this);
        findViewById(R.id.payPassword_tvEdit).setOnClickListener(this);
        payPassword_getCode = (TextView) findViewById(R.id.payPassword_getCode);
        payPassword_etPhone = (EditText) findViewById(R.id.payPassword_etPhone);
        payPassword_etCode = (EditText) findViewById(R.id.payPassword_etCode);
        payPassword_etNewPay = (EditText) findViewById(R.id.payPassword_etNewPay);
        payPassword_etNewPay2 = (EditText) findViewById(R.id.payPassword_etNewPay2);
        payPassword_getCode.setOnClickListener(this);
        try {
            if (!StringUtils.isEmpty(MyApplication.getUserJson().getString("mobile"))) {
                payPassword_etPhone.setText(MyApplication.getUserJson().getString("mobile"));
                payPassword_etPhone.setSelection(payPassword_etPhone.getText().length());
            }
        }catch (Exception e){

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relayout_back:
                finish();
                break;
            case R.id.payPassword_getCode:
                if(payPassword_etPhone.getText().toString().isEmpty()||! FormatUtil.isMobileNO(payPassword_etPhone.getText().toString())){
                    toastMessage("请填写正确的手机号");
                    break;
                }
                //获取验证码
                Timer.timeCount(payPassword_getCode);
                getNoteCode();
                break;
            case R.id.payPassword_tvEdit:
                //提交
                if(payPassword_etPhone.getText().toString().isEmpty()||! FormatUtil.isMobileNO(payPassword_etPhone.getText().toString())){
                    toastMessage("请填写正确的手机号");
                    break;
                }
                if(payPassword_etCode.getText().toString().isEmpty()){
                    toastMessage("请填写正确的验证码");
                    break;
                }
                if(!isTrue(payPassword_etNewPay.getText().toString(),6,12)){
                    toastMessage("请编写6-12位支付密码");
                    break;
                }
                if(!isTrue(payPassword_etNewPay2.getText().toString(),6,12)){
                    toastMessage("请编写6-12位支付密码");
                    break;
                }
                if(!payPassword_etNewPay.getText().toString().equals(payPassword_etNewPay2.getText().toString())){
                    toastMessage("两次密码输入不一致");
                    break;
                }
                modifyPayPassword();
                break;
        }
    }

    /**
     * 判断数据位数是否正确
     */
    private boolean isTrue(String data,int lenght,int maxLenght){
        if(StringUtils.isEmpty(data)){
            return  false;
        }else{
            if(data.length()<lenght){
                return false;
            }else if(data.length()>maxLenght){
                return  false;
            }
        }
        return  true;
    }

    /**
     * 修改支付密码
     */
    private void modifyPayPassword() {
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
            json.put("username", MyApplication.getUserJson().getString("m_username"));
            json.put("pwd", MyApplication.getPasswprd());
            json.put("password", payPassword_etNewPay.getText().toString());//6-12位
            json.put("phone_code",payPassword_etCode.getText().toString());//短信验证码
            json.put("mobile",payPassword_etPhone.getText().toString());//用户注册时绑定的手机
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", json.toString());
        String sign = Md5.md5(json.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.SET_PAY_PASSWORD, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    toastMessage(response.getString("msg"));
                    if (response.getString("status").equals("0")) {
                        dismissLoadingView();
                             finish();
                    }else{
                        dismissLoadingView();
                        payPassword_getCode.setText("重新获取");
                        Timer.setViewClicble(payPassword_getCode,true);
                    }

                } catch (Exception e) {
                    dismissLoadingView();
                    toastMessage("数据解析异常");
                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dismissLoadingView();
                toastMessage("网络异常，请检查网络后重试");
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
            jsonObject.put("mobile", payPassword_etPhone.getText().toString());
            jsonObject.put("code_type", "pay_password");
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
                         payPassword_getCode.setText("重新获取");
                         Timer.setViewClicble(payPassword_getCode,true);
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
