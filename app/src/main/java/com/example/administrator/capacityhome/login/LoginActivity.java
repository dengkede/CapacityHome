package com.example.administrator.capacityhome.login;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.administrator.capacityhome.BaseActivity;
import com.example.administrator.capacityhome.MainActivity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.smarthome.video.obj.LoginResult;
import com.example.administrator.capacityhome.smarthome.video.util.ConnectUtil;
import com.example.administrator.capacityhome.smarthome.video.util.InfoUtil;
import com.example.administrator.capacityhome.smarthome.video.util.OnRequestListener;
import com.example.administrator.capacityhome.wxapi.Constants;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;
import com.tsy.sdk.myutil.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cn.jpush.android.api.JPushInterface;
import http.RequestTag;
import utils.FormatUtil;
import utils.Md5;
import utils.SPUtils;
import utils.Timer;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private TextView login_tvLogin;
    private EditText login_etPassword;//密码
    private EditText login_etPhoneNumber;//手机号
    private TextView login_tvRemember;//记住密码
    private TextView login_tvForget;//忘记密码
    private TextView login_tvVertifyCode;//使用验证码登录
    private LinearLayout main;
    private ImageView login_imgCode;//图形验证码
    private EditText et_code;
    private Random random;//生成随机整数
    private TextView login_getCode;//获取图片验证码
   private LinearLayout layout_rememberPassword;//记住密码
    private CheckedTextView ct_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        getImageCode();
       // login_other();
    }

    private void initView() {
       // submitBtnVisible("注册会员", this);
       // backActivity();
        findViewById(R.id.top_submit_btn).setVisibility(View.GONE);
        findViewById(R.id.relayout_imgBack).setOnClickListener(this);
        main = (LinearLayout) findViewById(R.id.main);
        ct_check = (CheckedTextView) findViewById(R.id.ct_check);
        layout_rememberPassword = (LinearLayout) findViewById(R.id.layout_rememberPassword);
        login_getCode = (TextView) findViewById(R.id.login_getCode);
        login_tvLogin = (TextView) findViewById(R.id.login_tvLogin);
        login_imgCode = (ImageView) findViewById(R.id.login_imgCode);
        et_code = (EditText) findViewById(R.id.et_code);
        login_tvLogin.setOnClickListener(this);
        layout_rememberPassword.setOnClickListener(this);
        login_imgCode.setOnClickListener(this);
        findViewById(R.id.login_tvRegister).setOnClickListener(this);
        login_etPhoneNumber = (EditText) findViewById(R.id.login_etPhoneNumber);
        login_etPassword = (EditText) findViewById(R.id.login_etPassword);
        login_tvRemember = (TextView) findViewById(R.id.login_tvRemember);
        login_tvRemember.setOnClickListener(this);
        login_tvForget = (TextView) findViewById(R.id.login_tvForget);
        login_tvForget.setOnClickListener(this);
        login_tvVertifyCode = (TextView) findViewById(R.id.login_tvVertifyCode);
        login_tvVertifyCode.setOnClickListener(this);
        login_getCode.setOnClickListener(this);
        login_etPassword.addTextChangedListener(new MyEditTextChangeListener());
      //  addLayoutListener(main, login_tvLogin);
        random = new Random();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_tvForget:
                //忘记密码
                startActivity(this, ResetPasswordActivity.class);
                break;
            case R.id.layout_rememberPassword:
                //记住密码
                ct_check.setChecked(!ct_check.isChecked());
                break;
            case R.id.login_tvLogin:
                //登录
                Timer.setViewClicble(login_tvLogin,false);
                if (FormatUtil.isMobileNO(login_etPhoneNumber.getText().toString())) {
                    if (!et_code.getText().toString().isEmpty() && et_code.getText().toString().length() == 4) {
                        login(et_code.getText().toString());
                    } else {
                        Timer.setViewClicble(login_tvLogin,true);
                        toastMessage("请输入正确的验证码");
                    }
                }else{
                    toastMessage("请输入正确的手机号");
                }
                break;
            case R.id.login_tvVertifyCode:
                //获取验证码登录
                break;
            case R.id.login_imgCode:
                //获取图片验证码
               // Timer.timeCount(login_getCode);
                login_imgCode.setClickable(false);
                getImageCode();
                break;
            case R.id.relayout_imgBack:
                //关闭当前界面
                finish();
                break;
            case R.id.login_tvRegister:
                //注册
                startActivityForResult(new Intent(this,RegisterActivity.class),2);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 3){
            finish_rightToLeft();
        }
    }

    public class MyEditTextChangeListener implements TextWatcher {
        /**
         * 编辑框的内容发生改变之前的回调方法
         */
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //LogUtils.showLog("MyEditTextChangeListener", "beforeTextChanged---" + charSequence.toString());
//            if(login_etPhoneNumber.getText().toString().isEmpty()){
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
            if (!FormatUtil.isMobileNO(login_etPhoneNumber.getText().toString())) {
                toastMessage("请先输入正确的手机号");
                login_tvLogin.setClickable(false);
                return;
            } else {
                if (charSequence.length() >= 6) {
                    if (!FormatUtil.isMobileNO(login_etPhoneNumber.getText().toString())) {
                        toastMessage("请输入正确的手机号码");
                        login_tvLogin.setClickable(false);
                        return;
                    } else {
                        login_tvLogin.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.fontcolor_yellow));
                        login_tvLogin.setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.white));
                        login_tvLogin.setClickable(true);
                    }
                } else {
                    login_tvLogin.setClickable(false);
                    login_tvLogin.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.fontcolor_f2));
                    login_tvLogin.setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.fontcolor_e0));
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
     * 1、获取main在窗体的可视区域
     * 2、获取main在窗体的不可视区域高度
     * 3、判断不可视区域高度
     * 1、大于100：键盘显示  获取Scroll的窗体坐标
     * 算出main需要滚动的高度，使scroll显示。
     * 2、小于100：键盘隐藏
     *
     * @param main   根布局
     * @param scroll 需要显示的最下方View
     */
    public void addLayoutListener(final View main, final View scroll) {
        main.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                main.getWindowVisibleDisplayFrame(rect);
                int mainInvisibleHeight = main.getRootView().getHeight() - rect.bottom;
                if (mainInvisibleHeight > 100) {
                    int[] location = new int[2];
                    scroll.getLocationInWindow(location);
                    int srollHeight = (location[1] + scroll.getHeight()) - rect.bottom;
                    main.scrollTo(0, srollHeight);
                } else {
                    main.scrollTo(0, 0);
                }
            }
        });
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
     * 用户登录
     */
    private void login(String code) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("mobile", login_etPhoneNumber.getText().toString());
            jsonObject.put("m_password", login_etPassword.getText().toString());
            jsonObject.put("code", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.LOGIN, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    Log.d("LOGIN_INFO",response.toString());
                    if(response.getInt("status")!=0){
                        toastMessage(response.getString("msg"));
                    }else{
                        SPUtils.setSharedStringData(MyApplication.getAppContext(),"user_json",response.getJSONObject("data").toString());
                        SPUtils.setSharedStringData(MyApplication.getAppContext(),"user_id",response.getJSONObject("data").getString("id"));
                        MyApplication.user_json = response.getJSONObject("data");
                        SPUtils.setSharedStringData(MyApplication.getAppContext(),"password",response.getString("password"));
                        MyApplication.passwprd = response.getString("password");
                        MyApplication.uid = response.getJSONObject("data").getString("id");
                        MyApplication.brand = response.getJSONObject("data").getInt("brand");
                        SPUtils.setSharedIntData(MyApplication.getAppContext(),"brand",response.getJSONObject("data").getInt("brand"));
                        setAlias(true);
                        if(ct_check.isChecked()){
                            MyApplication.remeberPassword  = true;
                            SPUtils.setSharedBooleanData(LoginActivity.this,"remeberPasswprd",true);
                        }else{
                            MyApplication.remeberPassword  = false;
                            SPUtils.setSharedBooleanData(LoginActivity.this,"remeberPasswprd",false);
                        }
                        MyApplication.uid = response.getJSONObject("data").getString("id");
                        MyApplication.isLogin = true;//代表从登录界面回到首页
//                        if(MyApplication.getConfigrationJson()!=null) {
//                            finish_rightToLeft();
//                        }else{
//                            get_config();
//                        }
                        login_video();
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
                Timer.setViewClicble(login_tvLogin,true);
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                Timer.setViewClicble(login_tvLogin,true);
            }
        });
    }

    /**
     * 获取图片验证码
     */
    private void getImageCode() {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            String t = random.nextInt(10000) + "";
            jsonObject.put("t", t);
            Log.d("pathss",t);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.GETVERFICATIONCODE, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {

                try {
                    if (response.getInt("status") != 0) {
                        toastMessage(response.getString("msg"));
                    } else {
                        String path = response.getJSONObject("data").getString("path")+"&"+System.currentTimeMillis();
                        if (!StringUtils.isEmpty(path)) {
//                            Picasso.with(LoginActivity.this).load(RequestTag.BaseImageUrl + path).memoryPolicy(MemoryPolicy.NO_CACHE)
//                                    .networkPolicy(NetworkPolicy.NO_CACHE).placeholder(R.mipmap.default_iv).error(R.mipmap.ic_launcher).into(login_imgCode);
                            Glide.with(LoginActivity.this).load(RequestTag.BaseImageUrl + path).skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE).into(login_imgCode);
                            Log.d("ininini",RequestTag.BaseImageUrl + path);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                login_imgCode.setClickable(true);
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                login_imgCode.setClickable(true);
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
            jsonObject.put("mobile", "18380442243");
            jsonObject.put("code_type", random.nextInt(100));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.GETNOTECODE, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                toastMessage(response.toString());
                try {
                    String path = response.getJSONObject("data").getString("path")+"&"+System.currentTimeMillis();
                    if (!StringUtils.isEmpty(path)) {
                        Picasso.with(LoginActivity.this).load(RequestTag.BaseUrl + path).memoryPolicy(MemoryPolicy.NO_CACHE)
                                .networkPolicy(NetworkPolicy.NO_CACHE).error(R.mipmap.default_iv).into(login_imgCode);
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
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_right);
    }

    /**
     * 读取配置
     */
    private void get_config() {
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
                    if (response.getInt("status") != 0) {
                        toastMessage(response.getString("msg"));
                    } else {
                        SPUtils.setSharedStringData(LoginActivity.this, "configration_json", response.getJSONObject("data").toString());
                        RequestTag.BaseUrl = response.getJSONObject("data").getString("apiurl");
                        RequestTag.BaseImageUrl = response.getJSONObject("data").getString("imgurl");
                        RequestTag.BaseVieoUrl = response.getJSONObject("data").getString("videourl");
                    }
                } catch (Exception e) {

                }
                finish_rightToLeft();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("请检查网络");
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    /**
     * 第三方登录
     */
    private void login_other(){
        //先判断是否安装微信APP
//         if (!WXUtils.isWXAppInstalled()) {
//             ToastUtils.showToast("您还未安装微信客户端"); return;
//         }else{
             //微信登录
        IWXAPI api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
              SendAuth.Req req = new SendAuth.Req();
              req.scope = "snsapi_userinfo";
              req.state = "diandi_wx_login";
             // 像微信发送请求
           api.sendReq(req);
        // }

    }

    /**
     * 门锁视屏账号登录
     */
    private void login_video(){
        Map<String, Object> requestMap = new HashMap<String, Object>();
        requestMap.put("type", "UserHandler");
        requestMap.put("action", "editLoginPassword");
        Map<String, String> data = new HashMap<String, String>();
        data.put("loginName", login_etPhoneNumber.getText().toString());
        data.put("loginPassword", login_etPassword.getText().toString());
        org.json.JSONObject jsonObject = new org.json.JSONObject(data);
        requestMap.put("data", jsonObject);
        //debug.setText("参数：" + new com.alibaba.fastjson.JSONObject(requestMap).toString());
        ConnectUtil.request("/smart/user/login", requestMap, new OnRequestListener() {
            @Override
            public void onRequestSuccess(String result) {
                LoginResult loginResult = (LoginResult) com.alibaba.fastjson.JSONObject.parseObject(result, LoginResult.class);
                if (loginResult.getResult() == 1) {
                    Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                    InfoUtil.putString(LoginActivity.this, "pid", loginResult.getData().getPid());
                    InfoUtil.putString(LoginActivity.this, "token", loginResult.getData().getToken());
                    InfoUtil.putString(LoginActivity.this, "password", login_etPassword.getText().toString());
                    InfoUtil.putString(LoginActivity.this, "phone", login_etPhoneNumber.getText().toString());
                    if(MyApplication.getConfigrationJson()!=null) {
                        finish_rightToLeft();
                    }else{
                        get_config();
                    }
                } else {
                    if(MyApplication.getConfigrationJson()!=null) {
                        finish_rightToLeft();
                    }else{
                        get_config();
                    }
                 //   Toast.makeText(LoginActivity.this, loginResult.getReason(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                super.onFail();
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

