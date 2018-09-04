package com.example.administrator.capacityhome.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import utils.Agreement_Dialog;
import utils.DisplayUtil;
import utils.FormatUtil;
import utils.GetAgrement;
import utils.Md5;
import utils.StringUtils;
import utils.Timer;
import utils.serchcity.Agreement_Dialog2;

/**
 * 注册会员
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private EditText register_etPhoneNumber;
    private EditText register_etVertifyCode;
    private TextView register_getCode;
    private TextView register_tvReset;
    private LinearLayout layout_check;
    private CheckedTextView ct_check;
    private TextView tv_argument;
    private Agreement_Dialog2 agreement_dialog;
    private JSONObject json_xieyi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        getAggrement_Detail(false);
    }

    private void initView() {
        backActivity();
        findViewById(R.id.top_submit_btn).setVisibility(View.GONE);
        register_etPhoneNumber = (EditText) findViewById(R.id.register_etPhoneNumber);
        register_etVertifyCode = (EditText) findViewById(R.id.register_etVertifyCode);
        register_getCode = (TextView) findViewById(R.id.register_getCode);
        register_tvReset = (TextView) findViewById(R.id.register_tvReset);
        layout_check = (LinearLayout) findViewById(R.id.layout_check);
        ct_check = (CheckedTextView) findViewById(R.id.ct_check);
        tv_argument = (TextView) findViewById(R.id.tv_argument);
        tv_argument.setOnClickListener(this);
        register_getCode.setOnClickListener(this);
        register_tvReset.setOnClickListener(this);
        layout_check.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_getCode:
                //获取验证码
                Timer.timeCount(register_getCode);
                getNoteCode();
                break;
            case R.id.register_tvReset:
                //下一步
                if (!FormatUtil.isMobileNO(register_etPhoneNumber.getText().toString())) {
                    toastMessage("请输入正确的手机号");
                } else if (register_etVertifyCode.getText().toString().isEmpty()) {
                    toastMessage("请输入验证码");
                }
                else if (!ct_check.isChecked()) {
                    toastMessage("请先同意协议才能注册");
                } else {
                    startActivityForResult(new Intent(this, Register2Activity.class).putExtra("phoneNumber", register_etPhoneNumber.getText().toString()).putExtra("code", register_etVertifyCode.getText().toString()), 2);
                }
                break;
            case R.id.layout_check:
                if (ct_check.isChecked()) {
                    ct_check.setChecked(false);
                } else {
                    ct_check.setChecked(true);
                }
                break;
            case R.id.tv_argument:
                if (json_xieyi!=null) {
                    popIsNull(json_xieyi, "同意");
                } else {
                    getAggrement_Detail(true);
                }
                break;
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
            jsonObject.put("mobile", register_etPhoneNumber.getText().toString());
            jsonObject.put("code_type", "self_register");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 3) {
            setResult(3);
            finish();
        }
    }

    /**
     * 获取协议详情
     */

    public void getAggrement_Detail(final boolean isShows) {
        if (agreement_dialog == null) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("appid", RequestTag.APPID);
            params.put("key", RequestTag.KEY);
            String timestamp = System.currentTimeMillis() + "";
            params.put("time", timestamp + "");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("device_id", MyApplication.getDeviceId());
                jsonObject.put("mark", "member_regid");
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
                            toastMessage("暂时没有编辑");
                        } else {
                            if (response.getJSONObject("data") == null) {
                                toastMessage("暂时没有编辑");
                            } else {
                                json_xieyi = response.getJSONObject("data");
                                if(isShows){
                                    popIsNull(response.getJSONObject("data"), "同意");
                                }
                                tv_argument.setText("《"+response.getJSONObject("data").getString("title")+"》");
                            }
                        }

                    } catch (Exception e) {
                    }

                }

                @Override
                public void onFailure(int statusCode, String error_msg) {
                    toastMessage("请检查网络是否异常");
                }
            });
        } else {
            agreement_dialog.show();
        }
    }

    private void popIsNull(JSONObject jsonObject, String btn_name) {
        if (agreement_dialog == null) {
            agreement_dialog = new Agreement_Dialog2(this, new Agreement_Dialog2.Onclick() {
                @Override
                public void OnClickLisener(View v) {
                    if(v.getId() == R.id.tv_cancel){
                        ct_check.setChecked(false);
                    }else{
                        ct_check.setChecked(true);
                    }
                    agreement_dialog.dismiss();
                }
            });
//            try {
//                agreement_dialog.setContent(jsonObject.getString("content"), jsonObject.getString("title"), "同意");
//            }catch (Exception e){
//                e.printStackTrace();
//            }
        }
        try {
            agreement_dialog.show();
            WindowManager.LayoutParams lp = agreement_dialog.getWindow().getAttributes();
            lp.width = (int) (DisplayUtil.getScreenWidth(RegisterActivity.this) * 0.8); //设置宽度
            lp.height = (int) (DisplayUtil.getScreenHeight(RegisterActivity.this) * 0.8); //设置宽度
            agreement_dialog.getWindow().setAttributes(lp);
            agreement_dialog.setContent(jsonObject.getString("content"), jsonObject.getString("title"), "同意");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
