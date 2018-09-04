package com.example.administrator.capacityhome.smarthome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.administrator.capacityhome.BaseActivity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import http.RequestTag;
import utils.Md5;

/**
 * 门锁详
 */
public class DoorDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_detail);
    }
    /**
     * 获取门锁列表
     */
    private void getDoorLockList() {
        showLoadingView();
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis()+"";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());//转为设备id
            jsonObject.put("uid",MyApplication.getUid());
            jsonObject.put("id",MyApplication.getUid());//门锁设备id
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl+RequestTag.door_detail, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {

                    dismissLoadingView();
                }catch (Exception e){

                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dismissLoadingView();
                toastMessage("网络异常，请检查网络后重试");

            }
        });
    }
}
