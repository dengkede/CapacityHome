package com.example.administrator.capacityhome.smarthome.push;

import android.app.KeyguardManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.capacityhome.GuidActivity;
import com.example.administrator.capacityhome.H5Activity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.WebViewUrlActivity;
import com.example.administrator.capacityhome.combo.ComboDetailActivity;
import com.example.administrator.capacityhome.feedback.FeedBackDetailActivity;
import com.example.administrator.capacityhome.feedback.FeedBackDetailActivity2;
import com.example.administrator.capacityhome.login.LoginActivity;
import com.example.administrator.capacityhome.myorder.OrderDetailActivity;
import com.example.administrator.capacityhome.selfcenter.BalanceRechargeActivity;
import com.example.administrator.capacityhome.selfcenter.MoneyDetailActivity;
import com.example.administrator.capacityhome.smarthome.video.activity.MainActivity_video;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import http.RequestTag;
import utils.Md5;
import utils.SPUtils;
import utils.StringUtils;

public class PushWidowActivity extends AppCompatActivity {
    private Context mContext;
    private TextView textView;
    private TextView tv_title;
    private JSONObject jsonObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //四个标志位顾名思义，分别是锁屏状态下显示，解锁，保持屏幕长亮，打开屏幕。这样当Activity启动的时候，它会解锁并亮屏显示。
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED //锁屏状态下显示
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD //解锁
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON //保持屏幕长亮
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON); //打开屏幕
        //使用手机的背景
        Drawable wallPaper = WallpaperManager.getInstance(this).getDrawable();
        win.setBackgroundDrawable(wallPaper);
        setContentView(R.layout.activity_push_widow);
        mContext = this;
        initView();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //获取电源管理器对象
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        if (!pm.isScreenOn()) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire();  //点亮屏幕
            wl.release();  //任务结束后释放
        }
    }

    private void initView() {
        textView = (TextView) findViewById(R.id.tv_message);
        tv_title = (TextView) findViewById(R.id.tv_title);
        if(!StringUtils.isEmpty(getIntent().getStringExtra("message"))){
            try {
                jsonObject = new JSONObject(getIntent().getStringExtra("message"));
                textView.setText(jsonObject.getString("describe"));
                tv_title.setText(jsonObject.getString("title"));
            }catch (Exception e){

            }
        }else{
            textView.setText("去查看相关信息吧");
        }
        findViewById(R.id.message_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //先解锁系统自带锁屏服务，放在锁屏界面里面
                KeyguardManager keyguardManager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
                keyguardManager.newKeyguardLock("").disableKeyguard(); //解锁
                //点击进入消息对应的页面
                if (MyApplication.getUid() == null) {
                    //表示用户从没有登录
                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                } else if (MyApplication.user_json == null) {
                    //打开侧滑
                    //标识用户以前登录过
                    try {
                        getUserInfo(messageTepe(new JSONObject(getIntent().getStringExtra("message"))),mContext, new JSONObject(getIntent().getStringExtra("message")));
                    } catch (Exception e) {

                    }
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("message"));
                        Intent intent1 = messageTepe(jsonObject);
                        if (intent1 != null) {
                            mContext.startActivity(intent1);
                        } else {
                            h5(mContext,jsonObject);
                            // context.startActivity(new Intent(context, WebViewUrlActivity.class).putExtra("url","https://www.cnblogs.com/sunzn/p/3584003.html"));
                            //h5(new JSONObject(getIntent().getStringExtra("message")));
                        }
                    } catch (Exception e) {

                    }

                }
                finish();
            }
        });
        findViewById(R.id.close_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 推送跳转判断
     */
    private Intent messageTepe(JSONObject jsonObject) {
        Intent intent = null;
        try {
            Bundle bundle2 = new Bundle();
            if (jsonObject.getString("type").equals("orderMsg")) {
                //订单消息
                try {
                    //Log.d("orderDetail2",list.get(position-1).toString());
                    bundle2.putString("order_id", jsonObject.getString("order_id"));
                    //bundle2.putString("status", list.get(position-1).getString("status"));
                    bundle2.putInt("type", -1);
                    intent = new Intent(MyApplication.getAppContext(), OrderDetailActivity.class).putExtra("bundle", bundle2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (jsonObject.getString("type").equals("OpenUrlWindow")) {
                //OpenUrlWindow 打开H5窗口/全屏窗口
                intent = null;
            } else if (jsonObject.getString("type").equals("policeMsg")) {
                //门锁警报 直接打开门锁详情窗口，也就是报警记录页面
                try {
                    bundle2.putString("title", jsonObject.getString("title"));
                    bundle2.putString("door_id", jsonObject.getString("id"));
//                        bundle2.putString("door_key",list.get(position).getString("key"));
                    //   bundle2.putLong("addtime",list.get(position).getLong("addtime"));
                    intent = new Intent(MyApplication.getAppContext(), MainActivity_video.class).putExtra("bundle", bundle2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (jsonObject.getString("type").equals("packageMsg")) {
                //这里直接打开套餐详情窗口
                try {
                    intent = new Intent(MyApplication.getAppContext(), ComboDetailActivity.class).putExtra("id", jsonObject.getString("id"));
                } catch (Exception e) {
                    Toast.makeText(MyApplication.getAppContext(), "没有相关数据", Toast.LENGTH_SHORT).show();
                }
            } else if (jsonObject.getString("type").equals("paymoneyMsg")) {
                //这里直接打开购买洗衣券窗口（老的充值窗口）
                intent = new Intent(MyApplication.getAppContext(), BalanceRechargeActivity.class);
            } else if (jsonObject.getString("type").equals("opinionMsg")) {
                //这里直接打开意见回复详情窗口

                try {
                    bundle2.putString("id", jsonObject.getString("id"));
                    intent = new Intent(MyApplication.getAppContext(), FeedBackDetailActivity.class).putExtra("bundle", bundle2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (jsonObject.getString("type").equals("orderQuestMsg")) {
                //这里直接打开订单疑问回复详情窗口
                try {
                    bundle2.putString("order_id", jsonObject.getString("order_id"));
                    bundle2.putString("parent_id", jsonObject.getString("id"));
                    intent = new Intent(MyApplication.getAppContext(), FeedBackDetailActivity2.class).putExtra("bundle", bundle2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (jsonObject.getString("type").equals("moneyMsg")) {
                //这里直接打开资金列表窗口
                try {
                    bundle2.putString("type", jsonObject.getString("moneyType"));
                    intent = new Intent(MyApplication.getAppContext(), MoneyDetailActivity.class).putExtra("bundle", bundle2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            intent = new Intent(MyApplication.getAppContext(), GuidActivity.class);
        }
        return intent;
    }

    /**
     * 跳转h5
     */
    private void h5(Context context,JSONObject jsonObject) {
        try {

            if (jsonObject.get("windowSize") instanceof String) {

                context.startActivity(new Intent(context, WebViewUrlActivity.class).putExtra("url",jsonObject.getString("url").split("app=1")[0]+"app=1"+"&uid="+MyApplication.getUid()+"&user="+MyApplication.getUserJson().getString("m_username")+"&pwd="+MyApplication.getPasswprd()).putExtra("tuisong","tuisong"));
            } else {
                context.startActivity(new Intent(context, H5Activity.class).putExtra("json", jsonObject.toString()));
            }

            // context.startActivity(new Intent(context, WebViewUrlActivity.class).putExtra("url","https://www.cnblogs.com/sunzn/p/3584003.html"));
            //h5(new JSONObject(getIntent().getStringExtra("message")));
        } catch (Exception e) {

        }
    }
    /**
     * 获取用户基本信息
     */
    private void getUserInfo(final Intent intent, final Context context, final JSONObject jsonObject_main) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid", MyApplication.getUid());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(context, RequestTag.BaseUrl + RequestTag.USERINFO, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    Log.d("ingogog", response.toString());
                    if (response.getInt("status") != 0) {
                        context.startActivity(new Intent(context, GuidActivity.class));
                    } else {
                        SPUtils.setSharedStringData(MyApplication.getAppContext(), "user_json", response.getJSONObject("data").toString());
                        MyApplication.user_json = response.getJSONObject("data");
                        if (intent != null) {
                            context.startActivity(intent);
                        } else {
                            h5(context,jsonObject_main);
                        }
                    }

                } catch (Exception e) {
                    Log.d("info", e.toString());
                    context.startActivity(new Intent(context, GuidActivity.class));
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                context.startActivity(new Intent(context, GuidActivity.class));
            }
        });
    }
}
