package receiver;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.capacityhome.GuidActivity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.combo.ComboDetailActivity;
import com.example.administrator.capacityhome.feedback.FeedBackDetailActivity;
import com.example.administrator.capacityhome.feedback.FeedBackDetailActivity2;
import com.example.administrator.capacityhome.login.LoginActivity;
import com.example.administrator.capacityhome.myorder.OrderDetailActivity;
import com.example.administrator.capacityhome.selfcenter.BalanceRechargeActivity;
import com.example.administrator.capacityhome.selfcenter.MoneyDetailActivity;
import com.example.administrator.capacityhome.smarthome.SmartHomeActivity;
import com.example.administrator.capacityhome.smarthome.SmartHomeListActivity;
import com.example.administrator.capacityhome.smarthome.push.JpuchNotifyActivity;
import com.example.administrator.capacityhome.smarthome.push.PushWidowActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;
import utils.ACache;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JIGUANG-Example";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();

            Logger.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                //Logger.d(TAG, "JPush用户注册成功");
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Logger.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                //send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                Logger.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                //zdynotify(context,bundle.getString(JPushInterface.EXTRA_MESSAGE));
                showNotification(context,bundle);
                //processCustomMessage(context, bundle);
                getPush(context, intent, bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                Logger.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
               // getPush(context, intent, bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                Logger.d(TAG, "[MyReceiver] 用户点击打开了通知"+JPushInterface.EXTRA_ALERT);

//                JSONObject jsonObject = new JSONObject(bundle.getString(JPushInterface.EXTRA_ALERT));
//                //Logger.d(TAG, "[MyReceiver] 接收到推送下来的通知"+jsonObject.toString());
//                Bundle bundle2 = new Bundle();
//
//                //打开自定义的Activity
//                Intent i = new Intent(context, JpuchNotifyActivity.class);
//                i.putExtras(bundle);
//                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                context.startActivity(i);

            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Logger.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                Logger.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
            } else {
                Logger.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {
            Logger.d("通知异常", "通知异常 " + e.toString());
        }

    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Logger.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Logger.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
//		if (MainActivity.isForeground) {
//			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//			if (!ExampleUtil.isEmpty(extras)) {
//				try {
//					JSONObject extraJson = new JSONObject(extras);
//					if (extraJson.length() > 0) {
//						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//					}
//				} catch (JSONException e) {
//
//				}
//
//			}
//			LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
//		}
    }

    /**
     * 收到推送消息
     */
    private void getPush(Context context, Intent intent, Bundle bundle) {
        Log.i(TAG, "onReceive:收到了锁屏消息 ");
        String action = intent.getAction();
        try {

            if (action.equals("cn.jpush.android.intent.MESSAGE_RECEIVED")) {
                //管理锁屏的一个服务
                KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                String text = km.inKeyguardRestrictedInputMode() ? "锁屏了" : "屏幕亮着的";
                Log.i(TAG, "text: " + text);
                if (km.inKeyguardRestrictedInputMode()) {
                    Log.i(TAG, "onReceive:锁屏了 ");
                    //判断是否锁屏
                    Intent alarmIntent = new Intent(context, PushWidowActivity.class).putExtra("message",bundle.getString(JPushInterface.EXTRA_MESSAGE));
                    //在广播中启动Activity的context可能不是Activity对象，所以需要添加NEW_TASK的标志，否则启动时可能会报错。
                    alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(alarmIntent); //启动显示锁屏消息的activity
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 在状态栏显示通知
     */
    private void showNotification(Context context,Bundle bundle) {
        try {
            JSONObject jsonObject = new JSONObject(bundle.getString(JPushInterface.EXTRA_MESSAGE));
            Log.e("tuisong",jsonObject.toString());
           // Intent intent = messageTepe(jsonObject);
            Intent intent =new Intent (MyApplication.getAppContext(),NotificationClickReceiver.class).putExtra("json",jsonObject.toString());
            //PendingIntent contentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
            NotificationManager barmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notice;
            Notification.Builder builder = new Notification.Builder(context).setTicker(jsonObject.getString("title"))
                    .setSmallIcon(R.mipmap.app_icons).setWhen(System.currentTimeMillis());
//            Intent appIntent = null;
//            appIntent = new Intent(context, LoginActivity.class);
//            appIntent.setAction(Intent.ACTION_MAIN);
//            appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//            appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//关键的一步，设置启动模式
            PendingIntent contentIntent =PendingIntent.getBroadcast(MyApplication.getAppContext(), (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                notice = builder.setContentIntent(contentIntent).setContentTitle(jsonObject.getString("title")).setContentText(jsonObject.getString("describe")).build();
                notice.flags = Notification.FLAG_AUTO_CANCEL;
                barmanager.notify((int) System.currentTimeMillis(), notice);
            }
        }catch (Exception e){
            Log.e("tuisong",e.toString()+","+bundle.getString(JPushInterface.EXTRA_MESSAGE));
        }

    }
//    /**
//     * 推送跳转判断
//     */
//    private Intent messageTepe(JSONObject jsonObject){
//        Intent intent = null;
//        try{
//            Bundle bundle2 = new Bundle();
//            if (jsonObject.getString("type").equals("orderMsg")) {
//                //订单消息
//                try {
//                    //Log.d("orderDetail2",list.get(position-1).toString());
//                    bundle2.putString("order_id", jsonObject.getString("order_id"));
//                    //bundle2.putString("status", list.get(position-1).getString("status"));
//                    bundle2.putInt("type",-1);
//                    intent = new Intent(MyApplication.getAppContext(), OrderDetailActivity.class).putExtra("bundle", bundle2);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } else if (jsonObject.getString("type").equals("OpenUrlWindow")) {
//                //OpenUrlWindow 打开H5窗口/全屏窗口
//            } else if (jsonObject.getString("type").equals("policeMsg")) {
//                //门锁警报 直接打开门锁详情窗口，也就是报警记录页面
//                try {
//                    bundle2.putString("title",jsonObject.getString("title"));
//                    bundle2.putString("door_id",jsonObject.getString("id"));
////                        bundle2.putString("door_key",list.get(position).getString("key"));
//                    //   bundle2.putLong("addtime",list.get(position).getLong("addtime"));
//                   intent = new Intent(MyApplication.getAppContext(), SmartHomeListActivity.class).putExtra("bundle", bundle2);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } else if (jsonObject.getString("type").equals("packageMsg")) {
//                //这里直接打开套餐详情窗口
//                try {
//                    MyApplication.getAppContext().startActivity(new Intent(MyApplication.getAppContext(), ComboDetailActivity.class).putExtra("id", jsonObject.getString("id")));
//                }catch (Exception e){
//                    Toast.makeText(MyApplication.getAppContext(),"没有相关数据",Toast.LENGTH_SHORT).show();
//                }
//            } else if (jsonObject.getString("type").equals("paymoneyMsg")) {
//                //这里直接打开购买洗衣券窗口（老的充值窗口）
//               intent = new Intent(MyApplication.getAppContext(), BalanceRechargeActivity.class);
//            } else if (jsonObject.getString("type").equals("opinionMsg")) {
//                //这里直接打开意见回复详情窗口
//
//                try {
//                    bundle2.putString("id", jsonObject.getString("id"));
//                    intent = new Intent(MyApplication.getAppContext(), FeedBackDetailActivity.class).putExtra("bundle", bundle2);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            } else if (jsonObject.getString("type").equals("orderQuestMsg")) {
//                //这里直接打开订单疑问回复详情窗口
//                try {
//                    bundle2.putString("order_id", jsonObject.getString("order_id"));
//                    bundle2.putString("parent_id", jsonObject.getString("id"));
//                    intent = new Intent(MyApplication.getAppContext(), FeedBackDetailActivity2.class).putExtra("bundle", bundle2);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }else if (jsonObject.getString("type").equals("moneyMsg")) {
//                //这里直接打开资金列表窗口
//                try {
//                    bundle2.putString("type", jsonObject.getString("moneyType"));
//                    intent = new Intent(MyApplication.getAppContext(), MoneyDetailActivity.class).putExtra("bundle", bundle2);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }catch (Exception e){
//            intent = new Intent(MyApplication.getAppContext(), GuidActivity.class);
//        }
//        return intent;
//    }

}
