package com.example.administrator.capacityhome.login;

import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
 * 重置密码
 */
public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener {
    private ImageView resetPassword_imgVisbility;
    private EditText reset_etPassword;
    private EditText reset_etPhoneNumber;
    private EditText reset_etVertifyCode;
    private TextView resetPassword_getCode;
    private TextView reset_tvReset;
    private boolean isVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initView();
    }

    private void initView() {
        backActivity();
        findViewById(R.id.top_submit_btn).setVisibility(View.GONE);
        resetPassword_getCode = (TextView) findViewById(R.id.resetPassword_getCode);
        resetPassword_imgVisbility = (ImageView) findViewById(R.id.resetPassword_imgVisbility);
        reset_etPhoneNumber = (EditText) findViewById(R.id.reset_etPhoneNumber);
        reset_etVertifyCode = (EditText) findViewById(R.id.reset_etVertifyCode);
        reset_etPassword = (EditText) findViewById(R.id.reset_etPassword);
        reset_tvReset = (TextView) findViewById(R.id.reset_tvReset);
        resetPassword_imgVisbility.setOnClickListener(this);
        reset_tvReset.setOnClickListener(this);
        resetPassword_getCode.setOnClickListener(this);
        reset_etPassword.addTextChangedListener(new MyEditTextChangeListener());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resetPassword_imgVisbility:
                //是否明文显示密码
                if (isVisible) {
                    setPasswordVisble(false);
                    isVisible = false;//不显示
                } else {
                    setPasswordVisble(true);
                    isVisible = true;//显示
                }
                break;
            case R.id.resetPassword_getCode:
                //获取验证码
                Timer.timeCount(resetPassword_getCode);
                getNoteCode();
                break;
            case R.id.reset_tvReset:
                //确认重置
                if (StringUtils.isEmpty(reset_etPhoneNumber.getText().toString())) {
                    toastMessage("请输入手机号");
                } else if (!FormatUtil.isMobileNO(reset_etPhoneNumber.getText().toString())) {
                    toastMessage("请输入正确的手机号");
                } else if (StringUtils.isEmpty(reset_etVertifyCode.getText().toString())) {
                    toastMessage("请输入验证码");
                } else if (StringUtils.isEmpty(reset_etPassword.getText().toString()) || reset_etPassword.getText().toString().length() < 6) {
                    toastMessage("请输入最少6位密码");
                } else {
                    setPassword();
                }
                break;
        }
    }

    /**
     * 动态设置是否显示密码
     */
    private void setPasswordVisble(boolean isChecked) {
        if (isChecked) { //选择状态 显示明文--设置为可见的密码
            reset_etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else { //默认状态显示密码--设置文本 要一起写才能起作用  InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
            reset_etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    public class MyEditTextChangeListener implements TextWatcher {
        /**
         * 编辑框的内容发生改变之前的回调方法
         */
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //LogUtils.showLog("MyEditTextChangeListener", "beforeTextChanged---" + charSequence.toString());
//            if(reset_etPhoneNumber.getText().toString().isEmpty()){
//                toastMessage("请先输入手机号");
//                login_etPassword.setText("");
//            }
        }

        /**
         * 编辑框的内容正在发生改变时的回调方法 >>用户正在输入
         * 我们可以在这里实时地 通过搜索匹配用户的输入
         */
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //LogUtils.showLog("MyEditTextChangeListener", "onTextChanged---" + charSequence.toString());
            if (!FormatUtil.isMobileNO(reset_etPhoneNumber.getText().toString())) {
                toastMessage("请先输入正确的手机号");
                reset_tvReset.setClickable(false);
                return;
            } else if (reset_etVertifyCode.getText().toString().isEmpty()) {
                toastMessage("请先输入正确的验证码");
                reset_tvReset.setClickable(false);
                return;
            } else {
                if (charSequence.length() >= 6) {
                    if (!FormatUtil.isMobileNO(reset_etPhoneNumber.getText().toString())) {
                        toastMessage("请输入正确的手机号码");
                        reset_tvReset.setClickable(false);
                    } else {
                        reset_tvReset.setBackgroundColor(ContextCompat.getColor(ResetPasswordActivity.this, R.color.fontcolor_yellow));
                        reset_tvReset.setTextColor(ContextCompat.getColor(ResetPasswordActivity.this, R.color.white));
                        reset_tvReset.setClickable(true);
                    }
                } else {
                    reset_tvReset.setClickable(false);
                    reset_tvReset.setBackgroundColor(ContextCompat.getColor(ResetPasswordActivity.this, R.color.fontcolor_f2));
                    reset_tvReset.setTextColor(ContextCompat.getColor(ResetPasswordActivity.this, R.color.fontcolor_e0));
                }
            }
        }

        /**
         * 编辑框的内容改变以后,用户没有继续输入时 的回调方法
         */
        @Override
        public void afterTextChanged(Editable editable) {
            //LogUtils.showLog("MyEditTextChangeListener", "afterTextChanged---");
        }
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
            jsonObject.put("mobile", reset_etPhoneNumber.getText().toString());
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
                    if (response.getInt("status") != 0) {
                        //失败
                        //获取验证码
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

    /**
     * 设置新密码
     */
    private void setPassword() {
        showLoadingView();
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("code", reset_etVertifyCode.getText().toString());
            jsonObject.put("mobile", reset_etPhoneNumber.getText().toString());
            jsonObject.put("m_password", reset_etPassword.getText().toString());
            jsonObject.put("repassword", reset_etPassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.SET_PASSWEOR, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    toastMessage(response.getString("msg"));
                    if (response.getInt("status") != 0) {
                        //失败
                        dismissLoadingView();
                    } else {
                        dismissLoadingView();
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    dismissLoadingView();
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dismissLoadingView();
                toastMessage("请求超时，请检查网络");
            }
        });
    }

}
