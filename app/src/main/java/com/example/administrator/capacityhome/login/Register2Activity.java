package com.example.administrator.capacityhome.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
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

import cn.jpush.android.api.JPushInterface;
import http.RequestTag;
import utils.Md5;
import utils.SPUtils;
import utils.StringUtils;
import utils.Timer;

public class Register2Activity extends BaseActivity implements View.OnClickListener {
    private EditText register2_etPassword;
    private EditText register2_etPassword2;
    private TextView tv_last;
    private TextView tv_sure;
    private String code;
    private String phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        initView();
        try {
            phoneNumber = getIntent().getStringExtra("phoneNumber");
            code = getIntent().getStringExtra("code");
        }catch (Exception e){

        }
    }

    private void initView() {
        backActivity();
        findViewById(R.id.top_submit_btn).setVisibility(View.GONE);
        register2_etPassword = (EditText) findViewById(R.id.register2_etPassword);
        register2_etPassword2 = (EditText) findViewById(R.id.register2_etPassword2);
        tv_last = (TextView) findViewById(R.id.tv_last);
        tv_sure = (TextView) findViewById(R.id.tv_sure);
        tv_last.setOnClickListener(this);
        tv_sure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(new AnimationUtils().loadAnimation(this, R.anim.bg_alpha));
        switch (v.getId()) {
            case R.id.tv_last:
                finish();
                break;
            case R.id.tv_sure:
                if(StringUtils.isEmpty(phoneNumber)){
                    toastMessage("手机号有误");
                }else if(StringUtils.isEmpty(code)){
                    toastMessage("验证码有误");
                }else if(!register2_etPassword.getText().toString().equals(register2_etPassword2.getText().toString())){
                    toastMessage("两次密码输入不一致");
                }else{
                    register();
                }
                break;
        }
    }
    private void setAlias(boolean isHaveId) {

        // 调用 Handler 来异步设置别名
        //mHandler_jpush.sendMessage(mHandler_jpush.obtainMessage(MSG_SET_ALIAS, "YOUE_"+MyApplication.getUid()));
        if(isHaveId) {
            JPushInterface.setAlias(this, 1, "YOUE_" + MyApplication.getUid());
        }else{
            JPushInterface.setAlias(this, 1, "R_" + MyApplication.getDeviceId());
        }
    }
    /**
     * 用户注册
     */
    private void register() {
        showLoadingView();
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("mobile", phoneNumber);
            jsonObject.put("m_password", register2_etPassword.getText().toString());
            Log.d("codecode",code);
            jsonObject.put("code", code);
            jsonObject.put("read",1+"");
            jsonObject.put("p_password",register2_etPassword.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.REGISTER, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    Log.d("LOGIN_INFO2",response.toString());
                    if(response.getInt("status")!=0){
                        toastMessage(response.getString("msg"));
                        dismissLoadingView();
                    }else{
                        SPUtils.setSharedStringData(MyApplication.getAppContext(),"user_json",response.getJSONObject("data").toString());
                        SPUtils.setSharedStringData(MyApplication.getAppContext(),"user_id",response.getJSONObject("data").getString("id"));
                        MyApplication.user_json = response.getJSONObject("data");
                        SPUtils.setSharedStringData(MyApplication.getAppContext(),"password",response.getString("password"));
                        MyApplication.passwprd = response.getString("password");
                        MyApplication.uid = response.getJSONObject("data").getString("id");
                        MyApplication.brand = response.getInt("brand");
                        SPUtils.setSharedIntData(MyApplication.getAppContext(),"brand",response.getInt("brand"));
                        setAlias(true);
                        dismissLoadingView();
                        setResult(3);
                        finish_rightToLeft();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    toastMessage(e.toString());
                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage(error_msg);
           dismissLoadingView();
            }
        });
    }

}
