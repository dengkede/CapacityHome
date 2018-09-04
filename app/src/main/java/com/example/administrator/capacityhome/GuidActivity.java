package com.example.administrator.capacityhome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.baidu.mapapi.map.TextureMapView;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import http.RequestTag;
import utils.Md5;
import utils.SPUtils;

public class GuidActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guid);
        File f = new File(MyApplication.getAppContext().getFilesDir().getAbsolutePath() + "/" + "map_style.txt");
        if (f.exists()) {
            TextureMapView.setMapCustomEnable(true);
            TextureMapView.setCustomMapStylePath(MyApplication.getAppContext().getFilesDir().getAbsolutePath() + "/map_style.txt");
            post();
        } else {
            setMapCustomFile();
        }
       // post();
    }

    public void setMapCustomFile() {
        TextureMapView.setMapCustomEnable(true);
        FileOutputStream out = null;
        InputStream inputStream = null;
        try {
            inputStream = getResources().getAssets().open("custom_config_0323.txt");
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);

            String moduleName = MyApplication.getAppContext().getFilesDir().getAbsolutePath();
            File f = new File(moduleName + "/" + "map_style.txt");
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            out = new FileOutputStream(f);
            out.write(b);
            TextureMapView.setCustomMapStylePath(moduleName + "/map_style.txt");

        } catch (IOException e) {

        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (out != null) {
                    out.close();
                }
                post();
            } catch (IOException e) {
                post();
            }
        }
    }

    private void post() {
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
                    Log.d("configration_json",response.toString());
                    if (response.getInt("status") != 0) {
                        toastMessage(response.getString("msg"));
                    } else {
                        SPUtils.setSharedStringData(GuidActivity.this, "configration_json", response.getJSONObject("data").toString());
                        RequestTag.BaseUrl = response.getJSONObject("data").getString("apiurl");
                        RequestTag.BaseImageUrl = response.getJSONObject("data").getString("imgurl");
                        RequestTag.BaseVieoUrl = response.getJSONObject("data").getString("videourl");
                        RequestTag.ArticaleUrl = response.getJSONObject("data").getString("articleurl");
                    }
                } catch (Exception e) {

                }
                startActivity(new Intent(GuidActivity.this, Main_NewActivity.class));
                finish();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("请检查网络");
                startActivity(new Intent(GuidActivity.this, Main_NewActivity.class));
                finish();
            }
        });
    }

//    /**
//     * 自定义通知栏
//     */
//    private void setNotificationView(){
//        CustomPushNotificationBuilder builder = new
//                CustomPushNotificationBuilder(GuidActivity.this,
//                R.layout.customer_notitfication_layout,
//                R.id.icon,
//                R.id.title,
//                R.id.text);
//        // 指定定制的 Notification Layout
//        builder.statusBarDrawable = R.drawable.your_notification_icon;
//        // 指定最顶层状态栏小图标
//        builder.layoutIconDrawable = R.drawable.your_2_notification_icon;
//        // 指定下拉状态栏时显示的通知图标
//        JPushInterface.setPushNotificationBuilder(2, builder);
//}




}
