package com.example.administrator.capacityhome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * 透明过渡页
 */
public class TranslateActivity extends Activity {
    private TextView tv_title;
    private TextView tv_content;
    private TextView tv_cancel;
    private TextView tv_sure;
    private JSONObject json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        this.setFinishOnTouchOutside(true);
        if (getIntent().getStringExtra("json") == null) {
            startActivity(new Intent(this, GuidActivity.class));
            finish();
        } else {
            try {
                json = new JSONObject(getIntent().getStringExtra("json"));

            } catch (Exception e) {
                startActivity(new Intent(this, GuidActivity.class));
                finish();
            }
        }

        initView();
        DisplayMetrics d = getResources().getDisplayMetrics();
        android.view.WindowManager.LayoutParams p =
                getWindow().getAttributes();
        try {
            // JSONObject jsonObject_main = new JSONObject(getIntent().getStringExtra("json"));
            //webView.loadUrl(jsonObject_main.getString("url").split("app=1")[0]+"app=1"+"&uid="+MyApplication.getUid()+"&user="+MyApplication.getUserJson().getString("m_username")+"&pwd="+MyApplication.getPasswprd());
            p.width = (int) (d.widthPixels * 0.9);
            //p.height = (int) (d.heightPixels*0.4);
        } catch (Exception e) {
            //p.height = (int) (d.heightPixels*0.8);
            p.width = (int) (d.widthPixels * 0.4);
        }
        // 宽度设置为屏幕的0.7
        getWindow().setAttributes(p);
        if (getIntent().getStringExtra("json") != null) {
            try {
                json = new JSONObject(getIntent().getStringExtra("json"));
                tv_title.setText(json.getString("title"));
                tv_content.setText(json.getString("describe"));
            } catch (Exception e) {
           Log.d("eeeasdaf",e.toString());
            }
        }
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_sure = (TextView) findViewById(R.id.tv_sure);
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deal(json);
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 跳转处理
     */
    private void deal(JSONObject json) {
        if (MyApplication.getUid() == null) {
            //表示用户从没有登录
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (MyApplication.user_json == null) {
            //打开侧滑
            //标识用户以前登录过
            try {
                getUserInfo(messageTepe(json), this, json);
            } catch (Exception e) {
            }
        } else {
            try {

                Intent intent1 = messageTepe(json);
                if (intent1 != null) {
                    startActivity(intent1);
                } else {
                    h5(this, json);
                    // context.startActivity(new Intent(context, WebViewUrlActivity.class).putExtra("url","https://www.cnblogs.com/sunzn/p/3584003.html"));
                    //h5(new JSONObject(intent.getStringExtra("json")));
                }
                finish();
            } catch (Exception e) {
                finish();
            }

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
                        startActivity(new Intent(context, GuidActivity.class));
                    } else {
                        SPUtils.setSharedStringData(MyApplication.getAppContext(), "user_json", response.getJSONObject("data").toString());
                        MyApplication.user_json = response.getJSONObject("data");
                        if (intent != null) {
                            startActivity(intent);
                        } else {
                            h5(context, jsonObject_main);
                        }

                    }

                } catch (Exception e) {
                    Log.d("info", e.toString());
                    startActivity(new Intent(context, GuidActivity.class));
                }
                finish();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                startActivity(new Intent(context, GuidActivity.class));
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
    private void h5(Context context, JSONObject jsonObject) {
        try {

            if (jsonObject.get("windowSize") instanceof String) {

                context.startActivity(new Intent(context, WebViewUrlActivity.class).putExtra("url", jsonObject.getString("url").split("app=1")[0] + "app=1" + "&uid=" + MyApplication.getUid() + "&user=" + MyApplication.getUserJson().getString("m_username") + "&pwd=" + MyApplication.getPasswprd()).putExtra("tuisong", "tuisong"));
            } else {
                context.startActivity(new Intent(context, H5Activity.class).putExtra("json", jsonObject.toString()));
            }

            // context.startActivity(new Intent(context, WebViewUrlActivity.class).putExtra("url","https://www.cnblogs.com/sunzn/p/3584003.html"));
            //h5(new JSONObject(intent.getStringExtra("json")));
        } catch (Exception e) {

        }
    }


}
