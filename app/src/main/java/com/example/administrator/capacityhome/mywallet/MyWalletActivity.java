package com.example.administrator.capacityhome.mywallet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.capacityhome.BaseActivity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.selfcenter.BalanceRechargeActivity;
import com.example.administrator.capacityhome.selfcenter.MoneyDetailActivity;
import com.example.administrator.capacityhome.selfcenter.RequestWithdrawalActivity;
import com.example.administrator.capacityhome.selfcenter.WithdrawRecordActivity;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import adapter.WithdrawlRecord_Adapter;
import http.RequestTag;
import utils.ACache;
import utils.Md5;

/**
 * 我的钱包
 */
public class MyWalletActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_selfCenter_balance;
    private TextView tv_laundry_count;
    private TextView tv_integration;
    private TextView tv_textCard;//体验卡
    private RelativeLayout relayout_moneyDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);
        initView();
    }

    private void initView() {
        backActivity();
        findViewById(R.id.top_submit_btn).setVisibility(View.GONE);
        findViewById(R.id.relayout_selfCenter_withdrawal).setOnClickListener(this);
        tv_integration = (TextView) findViewById(R.id.tv_integration);
        tv_textCard = (TextView) findViewById(R.id.tv_textCard);
        tv_selfCenter_balance = (TextView) findViewById(R.id.tv_selfCenter_balance);
        tv_laundry_count = (TextView) findViewById(R.id.tv_laundry_count);
        relayout_moneyDetail = (RelativeLayout) findViewById(R.id.relayout_moneyDetail);
        relayout_moneyDetail.setOnClickListener(this);
        findViewById(R.id.relayout_withdrawRecord).setOnClickListener(this);
        findViewById(R.id.tv_selfCenter_recharge).setOnClickListener(this);
        findViewById(R.id.tv_selfCenter_withdrawal).setOnClickListener(this);
        setData();
        quary_money();
    }

    private void setData() {
        try {
            tv_selfCenter_balance.setText(MyApplication.getUserJson().getDouble("money") / 1000 + "");
            tv_laundry_count.setText(MyApplication.getUserJson().getDouble("clothes") / 1000 + "");
            tv_integration.setText(MyApplication.getUserJson().getDouble("interal") / 1000 + "");
            tv_textCard.setText(MyApplication.getUserJson().getDouble("card") / 1000 + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApplication.isupdate) {
            setData();
            MyApplication.isupdate = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_selfCenter_recharge:
                //充值
                startActivity(this, BalanceRechargeActivity.class);
                break;
            case R.id.relayout_withdrawRecord:
                //提现记录
                v.startAnimation(new AnimationUtils().loadAnimation(this, R.anim.bg_alpha));
                startActivity(this, WithdrawRecordActivity.class);
                break;
            case R.id.relayout_selfCenter_withdrawal:
            case R.id.tv_selfCenter_withdrawal:
                //申请提现
                v.startAnimation(new AnimationUtils().loadAnimation(this, R.anim.bg_alpha));
                startActivity(this, RequestWithdrawalActivity.class);
                break;
            case R.id.relayout_moneyDetail:
                v.startAnimation(new AnimationUtils().loadAnimation(this, R.anim.bg_alpha));
                //资金明细
                Bundle bundle = new Bundle();
                bundle.putString("type", "money");
                bundle.putString("title","余额记录");
                startActivity_Bundle(this, MoneyDetailActivity.class, bundle);
                break;
        }
    }
    /**
     * 获取最新的资金状态
     */
    private void quary_money() {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", json.toString());
        String sign = Md5.md5(json.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.QUARY_MONEY, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if (response.getString("status").equals("0")) {
                        MyApplication.getUserJson().put("money", response.getJSONObject("data").getLong("money"));
                        MyApplication.getUserJson().put("clothes", response.getJSONObject("data").getLong("clothes"));
                        MyApplication.getUserJson().put("interal", response.getJSONObject("data").getLong("interal"));
                        MyApplication.getUserJson().put("card", response.getJSONObject("data").getLong("card"));
                        setData();
                    } else {
                        // toastMessage(response.getString("msg"));
                        toastMessage(response.getString("msg"));
                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {

            }
        });
    }
}
