package com.example.administrator.capacityhome.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.administrator.capacityhome.BaseActivity;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import http.RequestTag;
import utils.KeyBordUtil;

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    /**
     * 微信登录相关
     */
    private IWXAPI api;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //通过WXAPIFactory工厂获取IWXApI的示例
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);
        //将应用的appid注册到微信
        api.registerApp(Constants.APP_ID);
        //ViseLog.d("------------------------------------");
        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        try {
            boolean result = api.handleIntent(getIntent(), this);
            if (!result) {
                //ViseLog.d("参数不合法，未被SDK处理，退出");
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        api.handleIntent(data, this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {
        //   ViseLog.d("baseReq:"+ JSON.toJSONString(baseReq));
    }

    @Override
    public void onResp(BaseResp baseResp) {
        // ViseLog.d("baseResp:"+JSON.toJSONString(baseResp));
        //  ViseLog.d("baseResp:"+baseResp.errStr+","+baseResp.openId+","+baseResp.transaction+","+baseResp.errCode);
        String result = "";
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "发送成功";
                //showMsg(1,result);
                Bundle bundle = new Bundle();
//                getAccess_token(baseResp.+"");
                baseResp.toBundle(bundle);
                SendAuth.Resp sp = new SendAuth.Resp(bundle);
                String code = sp.code;
                //getAccess_token(code);
                toastMessage("分享成功");
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "发送取消";
                //showMsg(2,result);
                toastMessage("发送取消");
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "发送被拒绝";
                //showMsg(1,result);
                toastMessage("发送被拒绝");
                finish();
                break;
            default:
                result = "发送返回";
                //showMsg(0,result);
                finish();
                break;
        }

    }
//    {
//        "code":"0712DRee0kzwiz13fUce0900feRet",
//            "country":"CN",
//            "errCode":0,
//            "lang":"zh_CN",
//            "state":"wechat_sdk_微信登录",
//            "type":1,
//            "url":"wxb363a9ff53731258://oauth?code=0712DRee0kzwiz13fUce0900fe02DRet&ate=wechat_sdk_%E5%BE%AE%E4%BF%A1%E7%99%BB%E5%BD%95"
//    }

    // https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code获取token
//    解释:
//    errCode:
//    //ERR_OK = 0(用户同意) ERR_AUTH_DENIED = -4（用户拒绝授权 ERR_USER_CANCEL = -2（用户取消）
//    code:用户换取access_token的code，仅在ErrCode为0时有效
//    state:第三方程序发送时用来标识其请求的唯一性的标志，由第三方程序调用sendReq时传入，由微信终端回传，state字符串长度不能超过1K
//    lang:微信客户端当前语言
//    country:微信用户当前国家信息

    /**
     * 通过code获取access_token
     */
    private void getAccess_token(String code) {

        showLoadingView();
        KeyBordUtil.hintKeyboard(this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", Constants.APP_ID);
        params.put("secret", "17fdb19aef779080a5dd9d6c7e26f1ac");
        params.put("code", code);//
        params.put("grant_type", "authorization_code");

        MyOkHttp.get().get(this, "https://api.weixin.qq.com/sns/oauth2/access_token", params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    getWXUserInfo(response.getString("openid"), response.getString("access_token"));
                } catch (Exception e) {
                    //toastMessage("数据解析异常");
                    try {
                        toastMessage(response.getString("errmsg"));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
                dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("网络异常，请检查网络后重试");
                Log.d("weixinxin",error_msg);
                dismissLoadingView();

            }
        });
    }

    /**
     * 通过code获取access_token
     */
    private void getWXUserInfo(String openid, String token) {
        showLoadingView();
        KeyBordUtil.hintKeyboard(this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject json = new JSONObject();
        try {
            json.put("openid", openid);
            json.put("access_token", token);


        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("paye", e.toString());
        }

        MyOkHttp.get().get(this, "https://api.weixin.qq.com/sns/userinfo?", params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    toastMessage(response.toString());
                } catch (Exception e) {
                    toastMessage("数据解析异常");
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
