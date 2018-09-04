package com.example.administrator.capacityhome.wxapi;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.administrator.capacityhome.BaseActivity;
import com.example.administrator.capacityhome.R;
import com.google.gson.JsonObject;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {
    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
    private IWXAPI api;
    private String openbox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_wxpay_entry);
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        showLoadingView();
        api.handleIntent(getIntent(), this);
        if(getIntent().getStringExtra("openbox")!=null){
            openbox = getIntent().getStringExtra("openbox");
        }
        if (getIntent().getStringExtra("wxDate") != null) {
            try {
                sendPayRequest(new JSONObject(getIntent().getStringExtra("wxDate")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            toastMessage("订单异常,请重试");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.d("EX_ERRO", "onPayFinish, errCode = " + resp.errCode);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            switch (resp.errCode) {
                case 0:
                    //成功
                    Intent inten = new Intent();
                    inten.putExtra("openbox",openbox);
                    inten.putExtra("json",getIntent().getStringExtra("json"));
                    setResult(3,inten);
                    finish();
                    toastMessage("支付成功");
                    break;
                case -1:
                    //失败
                    //失败
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.app_tip);
                    builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
                    builder.show();
                    toastMessage("支付失败");
                    break;
                case -2:
                    //取消
                    toastMessage("支付取消");
                    finish();
                    break;
                default:
                    //失败
                    //失败
                    toastMessage("支付失败");
                    AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
                    builder3.setTitle(R.string.app_tip);
                    builder3.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
                    builder3.show();
                    break;
            }

        }
    }

    /**
     * 调用微信支付
     */
    public void sendPayRequest(JSONObject jsonObject) {
        if (jsonObject == null) {
            //返回空数据
            return;
        }
        try {
            PayReq req = new PayReq();
            req.appId = jsonObject.getString("appid");
            req.partnerId = jsonObject.getString("partnerid");
            req.prepayId = jsonObject.getString("prepayid");
            req.nonceStr = jsonObject.getString("noncestr");
            req.timeStamp = jsonObject.getString("timestamp");
            req.packageValue = jsonObject.getString("package");
            req.sign = jsonObject.getString("sign");
            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
            //3.调用微信支付sdk支付方法
            api.sendReq(req);

        } catch (Exception e) {
         toastMessage("订单异常,请重试");
        }
        dismissLoadingView();
    }

}
