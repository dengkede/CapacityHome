package zfb_pay;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.AuthTask;

import java.util.Map;


/**
 * Created by TanBo on 2017/6/30.
 */

public class PayType {
    String authInfo = "";
    Runnable authRunnable;
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    private  Handler mHandler;
    private SucceedPay succeedPay;


    /**
     * 支付宝支付,已经有订单信息
     */
    public void pay_zfb(final Context context,String order, final SucceedPay succeedPay){

        this.succeedPay = succeedPay;

        mHandler = new Handler() {
            @SuppressWarnings("unused")
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SDK_PAY_FLAG: {
                        @SuppressWarnings("unchecked")
                        PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                        /**
                         对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                         */
                        String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                        String resultStatus = payResult.getResultStatus();
                        // 判断resultStatus 为9000则代表支付成功
                        if (TextUtils.equals(resultStatus, "9000")) {
                            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                            succeedPay.callBack(true,"支付成功");
                        } else {
                            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                            Toast.makeText(context, "支付失败", Toast.LENGTH_SHORT).show();
                            succeedPay.callBack(true,"支付失败");
                        }
                        break;
                    }
                    case SDK_AUTH_FLAG: {
                        @SuppressWarnings("unchecked")
                        AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                        String resultStatus = authResult.getResultStatus();

                        // 判断resultStatus 为“9000”且result_code
                        // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                        if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                            // 获取alipay_open_id，调支付时作为参数extern_token 的value
                            // 传入，则支付账户为该授权账户

                        } else {
                            // 其他状态值则为授权失败
                            succeedPay.callBack(true,"授权失败");

                        }
                        break;
                    }
                    default:
                        break;
                }
            };
        };
        authRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造AuthTask 对象
                AuthTask authTask = new AuthTask((Activity) context);
                // 调用授权接口，获取授权结果
                Map<String, String> result = authTask.authV2(authInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                Log.d("ash1234",result.toString());
                mHandler.sendMessage(msg);
            }
        };
        authInfo = order;
        Thread authThread = new Thread(authRunnable);
        authThread.start();
    }



//    /**
//     *xutils3网络请求,充值
//     */
//    private void recharge(final Context context,String money){
//        final IntetnetState state = new IntetnetState();
//        state.showGetData(null,context);
//        long time = System.currentTimeMillis();
//        String url= MyUrl.url+ MyUrl.recharge;
//        Map<String,Object> map = new HashMap<String,Object>();
//        JSONObject json = new JSONObject();
//        try {
//            json.put("type",1);//支付方式，1支付宝
//            json.put("money",money);//充值金额
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        map.put("data",json.toString());
//        map.put("sign", XUtil.md5(json.toString(),time)+"");
//        map.put("appid",MyUrl.appId);
//        map.put("uid",MyApplication.getInstance().uid+"");
//        map.put("time",time+"");
//        json = null;
//        XUtil.Post(url,map, new MyCallBack<JSONObject>(){
//            @Override
//            public void onSuccess(JSONObject result) {
//                super.onSuccess(result);
//                try {
//                    if(result.getInt("status")==0) {
//                        //调用成功
//                        authInfo = result.getString("data");
//                        Thread authThread = new Thread(authRunnable);
//                        authThread.start();
//                        //MyToast.makeText(context,result.getString("msg"), Toast.LENGTH_SHORT,1).show();
//
//                    }else{
//                        //失败
//                        MyToast.makeText(context,result.getString("msg"),Toast.LENGTH_SHORT,2).show();
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    MyToast.makeText(context,"数据格式解析异常，请稍后重试。.",Toast.LENGTH_SHORT,2).show();
//                }
//                state.dismiss();
//            }
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                super.onError(ex, isOnCallback);
//                state.dismiss();
//                MyToast.makeText(context,"网络连接异常，请检查网络后重试.",Toast.LENGTH_SHORT,2).show();
//            }
//
//        });
//    }

    /**
     * 支付回调
     */
    public interface SucceedPay{
        void callBack(Boolean back, String s);
    }

}
