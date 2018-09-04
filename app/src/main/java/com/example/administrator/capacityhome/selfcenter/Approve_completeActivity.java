package com.example.administrator.capacityhome.selfcenter;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.capacityhome.BaseActivity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import http.RequestTag;
import utils.ColumnUtils;
import utils.KeyBordUtil;
import utils.Md5;
import utils.StringUtils;

public class Approve_completeActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_type;
    private TextView et_cardName;
    private TextView et_cardNumber;
    private ImageView img_front;
    private ImageView img_verso;
    private ColumnUtils columnUtils;
    private List<JSONObject> lis_type = new ArrayList<>();
    private TextView tv_reason;
    private TextView tv_state;
    private TextView tv_state2;
    private RelativeLayout relayout_state2;
    private RelativeLayout relayout_state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_complete);
        initView();
        approve_info();
    }

    private void initView() {
        relayout_state2 = (RelativeLayout) findViewById(R.id.relayout_state2);
        relayout_state = (RelativeLayout) findViewById(R.id.relayout_state);
        tv_type = (TextView) findViewById(R.id.tv_type);
        tv_reason = (TextView) findViewById(R.id.tv_reason);
        et_cardName = (TextView) findViewById(R.id.et_cardName);
        et_cardNumber = (TextView) findViewById(R.id.et_cardNumber);
        img_front = (ImageView) findViewById(R.id.img_front);
        img_verso = (ImageView) findViewById(R.id.img_verso);
        tv_reason = (TextView) findViewById(R.id.tv_reason);
        tv_state = (TextView) findViewById(R.id.tv_state);
        tv_state2 = (TextView) findViewById(R.id.tv_state2);
        findViewById(R.id.tv_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                finish();
                break;
        }
    }

    /**
     * 读取实名认证资料
     */
    private void approve_info() {
        showLoadingView();
        KeyBordUtil.hintKeyboard(this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject json = new JSONObject();
        try {
            json.put("device_id", MyApplication.getDeviceId());
            json.put("uid", MyApplication.getUid());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", json.toString());
        String sign = Md5.md5(json.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.GET_authentication, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, final JSONObject response) {
                try {
                    if (response.getJSONObject("data").getInt("id_state") == 0) {
                        if (!StringUtils.isEmpty(response.getJSONObject("data").getString("name"))) {
                            et_cardName.setText(response.getJSONObject("data").getString("name"));
                        }
                        if (!StringUtils.isEmpty(response.getJSONObject("data").getString("id_code"))) {
                            et_cardNumber.setText(response.getJSONObject("data").getString("id_code"));
                        }

                        //已认证
                        Picasso.with(Approve_completeActivity.this).load(RequestTag.BaseImageUrl + response.getJSONObject("data").getString("cert_pic1")).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).into(img_front, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                try {
                                    Picasso.with(Approve_completeActivity.this).load(RequestTag.BaseImageUrl + MyApplication.getConfigrationJson().getJSONObject("other").getString("default_img")).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).into(img_front);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        Picasso.with(Approve_completeActivity.this).load(RequestTag.BaseImageUrl + response.getJSONObject("data").getString("cert_pic2")).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).into(img_verso, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                try {
                                    Picasso.with(Approve_completeActivity.this).load(RequestTag.BaseImageUrl + MyApplication.getConfigrationJson().getJSONObject("other").getString("default_img")).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).into(img_verso);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (columnUtils == null) {
                        columnUtils = new ColumnUtils(Approve_completeActivity.this, new ColumnUtils.Result() {
                            @Override
                            public void onSuccess(String title, JSONArray jsonArray) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        if (jsonArray.getJSONObject(i).getString("id").equals(response.getJSONObject("data").getString("id_type")))
                                            tv_type.setText(jsonArray.getJSONObject(i).getString("title"));
                                            break;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
//                                showPopwindow(title);
                            }

                            @Override
                            public void onFailure(String msg) {
                                toastMessage(msg);
                            }
                        });
                    }
                    columnUtils.getColumn("","id_type");
                    if (!StringUtils.isEmpty(response.getJSONObject("data").getString("id_state"))) {
                        tv_state.setText((String) MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("member_state").get(response.getJSONObject("data").getInt("id_state")) + "：");
                        tv_state.setTextColor(Color.parseColor((String) MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("member_state_color").get(response.getJSONObject("data").getInt("id_state"))));
                    }else{
                        tv_state.setText("未设置");
                    }
                    if (!StringUtils.isEmpty(response.getJSONObject("data").getString("reason"))) {
                        tv_reason.setText((String) MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("member_state").get(response.getJSONObject("data").getInt("reason")));
                        tv_reason.setTextColor(Color.parseColor((String) MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("member_state_color").get(response.getJSONObject("data").getInt("id_state"))));
                    }

                } catch (Exception e) {
                  //  toastMessage("数据解析异常");
                    relayout_state.setVisibility(View.GONE);
                    relayout_state2.setVisibility(View.VISIBLE);
                    //不存在reason
                    try {
                        if (!StringUtils.isEmpty(response.getJSONObject("data").getString("id_state"))) {
                            tv_state2.setText((String) MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("member_state").get(response.getJSONObject("data").getInt("id_state")));
                            tv_state2.setTextColor(Color.parseColor((String) MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("member_state_color").get(response.getJSONObject("data").getInt("id_state"))));
                        }
                    }catch (Exception e2){

                    }
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
