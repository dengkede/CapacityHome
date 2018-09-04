package com.example.administrator.capacityhome.smarthome;
/**
 * 小e管家
 */

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.capacityhome.BaseActivity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.smarthome.video.activity.MainActivity_video;
import com.example.administrator.capacityhome.smarthome.video.obj.LoginResult;
import com.example.administrator.capacityhome.smarthome.video.util.ConnectUtil;
import com.example.administrator.capacityhome.smarthome.video.util.InfoUtil;
import com.example.administrator.capacityhome.smarthome.video.util.OnRequestListener;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.SmartHomeAdapter;
import butterknife.BindView;
import http.RequestTag;
import utils.Md5;

public class SmartHomeActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    TextView tv_homeCount;//门锁数量
    @BindView(R.id.grid_smartHome)
    GridView grid_smartHome;
    private List<JSONObject> list;
    private SmartHomeAdapter adapter;
    private TextView layout_empty;
    private TextView tvAbnormal;//有异常
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_home);
        initView();
    }

    private void initView() {
        findViewById(R.id.top_submit_btn).setVisibility(View.GONE);
        tv_homeCount = (TextView) findViewById(R.id.tv_homeCount);
        grid_smartHome = (GridView) findViewById(R.id.grid_smartHome);
        layout_empty = (TextView) findViewById(R.id.layout_empty);
        tvAbnormal = (TextView) findViewById(R.id.tvAbnormal);
        setPageTitle("小e管家");
        backActivity();
        list = new ArrayList<>();
//        list.add(new JSONObject());
//        list.add(new JSONObject());
//        list.add(new JSONObject());
        adapter = new SmartHomeAdapter(list,this);
        grid_smartHome.setAdapter(adapter);
        grid_smartHome.setOnItemClickListener(this);
        getDoorLockList();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        try {
            bundle.putString("title",list.get(position).getString("title"));
            bundle.putString("door_id",list.get(position).getString("id"));
            bundle.putString("door_key",list.get(position).getString("key"));
            bundle.putLong("addtime",list.get(position).getLong("addtime"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //startActivity_Bundle(this,SmartHomeListActivity.class,bundle);
        startActivity_Bundle(this,MainActivity_video.class,bundle);
    }

    /**
     * 获取门锁列表
     */
    private void getDoorLockList() {
        tvAbnormal.setVisibility(View.GONE);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl+RequestTag.DOORLOCK_LIST, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try{
                    Log.d("doorlisls",response.toString());
                    if(response.getInt("status")!=0){
                        grid_smartHome.setVisibility(View.GONE);
                        layout_empty.setVisibility(View.VISIBLE);
                    }else{
                        if(response.getJSONArray("data")==null||response.getJSONArray("data").length()==0){
                            grid_smartHome.setVisibility(View.GONE);
                            layout_empty.setVisibility(View.VISIBLE);
                        }else {
                            for (int i = 0; i <response.getJSONArray("data").length();i++) {
                                if(response.getJSONArray("data").getJSONObject(i).getInt("status")==1) {
                                  tvAbnormal.setVisibility(View.VISIBLE);
                                }
                                list.add(response.getJSONArray("data").getJSONObject(i));
                            }
                            layout_empty.setVisibility(View.GONE);
                            grid_smartHome.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                        tv_homeCount.setText(response.getInt("total")+"");
                    }

                }catch (Exception e){
                    layout_empty.setVisibility(View.GONE);
                    grid_smartHome.setVisibility(View.VISIBLE);
                }
                dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dismissLoadingView();
                toastMessage("网络异常，请检查网络后重试");
                layout_empty.setVisibility(View.GONE);
                grid_smartHome.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 门锁视屏账号登录
     */
    private void login_video(){
        Map<String, Object> requestMap = new HashMap<String, Object>();
        requestMap.put("type", "UserHandler");
        requestMap.put("action", "editLoginPassword");
        Map<String, String> data = new HashMap<String, String>();
//        data.put("loginName", login_etPhoneNumber.getText().toString());
//        data.put("loginPassword", login_etPassword.getText().toString());
        org.json.JSONObject jsonObject = new org.json.JSONObject(data);
        requestMap.put("data", jsonObject);
        //debug.setText("参数：" + new com.alibaba.fastjson.JSONObject(requestMap).toString());
        ConnectUtil.request("/smart/user/login", requestMap, new OnRequestListener() {
            @Override
            public void onRequestSuccess(String result) {
                LoginResult loginResult = (LoginResult) com.alibaba.fastjson.JSONObject.parseObject(result, LoginResult.class);
                if (loginResult.getResult() == 1) {
                    Toast.makeText(SmartHomeActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                    InfoUtil.putString(SmartHomeActivity.this, "pid", loginResult.getData().getPid());
                    InfoUtil.putString(SmartHomeActivity.this, "token", loginResult.getData().getToken());
//                    InfoUtil.putString(SmartHomeActivity.this, "password", login_etPassword.getText().toString());
//                    InfoUtil.putString(SmartHomeActivity.this, "phone", login_etPhoneNumber.getText().toString());
                   
                } else {
                    Toast.makeText(SmartHomeActivity.this, loginResult.getReason(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                super.onFail();
                Toast.makeText(SmartHomeActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
}
