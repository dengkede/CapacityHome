package com.example.administrator.capacityhome.smarthome.video.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.smarthome.video.obj.LoginResult;
import com.example.administrator.capacityhome.smarthome.video.util.ConnectUtil;
import com.example.administrator.capacityhome.smarthome.video.util.InfoUtil;
import com.example.administrator.capacityhome.smarthome.video.util.OnRequestListener;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity_video extends Activity {
    private EditText phoneNumber;
    private EditText password;
    private TextView toRegist;
    private TextView forgetPassword;
    private Button btnLogin;
    private EditText debug;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_video);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        password = (EditText) findViewById(R.id.password);
        toRegist = (TextView) findViewById(R.id.toRegist);
        forgetPassword = (TextView) findViewById(R.id.forgetPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        debug = (EditText) findViewById(R.id.debug);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!Validator.isPhone(phoneNumber.getText().toString())) {
//                    Toast.makeText(LoginActivity_video.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if (TextUtils.isEmpty(password.getText().toString())) {
                    Toast.makeText(LoginActivity_video.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, Object> requestMap = new HashMap<String, Object>();
                requestMap.put("type", "UserHandler");
                requestMap.put("action", "editLoginPassword");
                Map<String, String> data = new HashMap<String, String>();
                data.put("loginName", phoneNumber.getText().toString());
                data.put("loginPassword", password.getText().toString());
                org.json.JSONObject jsonObject = new org.json.JSONObject(data);
                requestMap.put("data", jsonObject);
                debug.setText("参数：" + new JSONObject(requestMap).toString());
                ConnectUtil.request("/smart/user/login", requestMap, new OnRequestListener() {
                    @Override
                    public void onRequestSuccess(String result) {
                        LoginResult loginResult = (LoginResult) JSONObject.parseObject(result, LoginResult.class);
                        if (loginResult.getResult() == 1) {
                            Toast.makeText(LoginActivity_video.this, "登陆成功", Toast.LENGTH_SHORT).show();
                            InfoUtil.putString(LoginActivity_video.this, "pid", loginResult.getData().getPid());
                            InfoUtil.putString(LoginActivity_video.this, "token", loginResult.getData().getToken());
                            InfoUtil.putString(LoginActivity_video.this, "password", password.getText().toString());
                            InfoUtil.putString(LoginActivity_video.this, "phone", phoneNumber.getText().toString());
                            startActivity(new Intent(LoginActivity_video.this, MainActivity_video.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity_video.this, loginResult.getReason(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFail() {
                        super.onFail();
                        Toast.makeText(LoginActivity_video.this, "登录失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
//        toRegist.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivityForResult(new Intent(LoginActivity_video.this, RegisterActivity.class), 1);
//            }
//        });
//        forgetPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivityForResult(new Intent(LoginActivity_video.this, ForgetPwdActivity.class), 2);
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                phoneNumber.setText(data.getStringExtra("userPhone"));
                password.setText(data.getStringExtra("password"));
            }
        }
    }
}

