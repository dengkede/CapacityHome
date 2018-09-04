package com.example.administrator.capacityhome.selfcenter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.PtrHandler;
import com.chanven.lib.cptr.header.MaterialHeader;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.example.administrator.capacityhome.BaseActivity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.AddressManageAdapter;
import http.RequestTag;
import utils.CityList;
import utils.Md5;
import utils.StringUtils;
import widget.DefaultLoadMoreViewFooter_normal;

/**
 * 地址管理
 */
public class AddressManageActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, OnLoadMoreListener, PtrHandler {
    ListView listView;
    private AddressManageAdapter adapter;
    private PtrClassicFrameLayout pcf_container;
    private List<JSONObject> list;
    private TextView layout_empty;
    private int index = -1;//选择的位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_manage);
        initView();
        initFrameLayout();
    }

    private void initView() {
        submitBtnVisible("添加", this);
        layout_empty = (TextView) findViewById(R.id.layout_empty);
        listView = (ListView) findViewById(R.id.addressManage_listView);
        list = new ArrayList<>();
        adapter = new AddressManageAdapter(list, this);
        listView.setAdapter(adapter);
        backActivity();
        setSubmitBtn("添加");
        listView.setOnItemClickListener(this);
    }

    private void initFrameLayout() {
        pcf_container = ((PtrClassicFrameLayout) findViewById(R.id.pcf_container));
        MaterialHeader materialHeader = new MaterialHeader(this);
        materialHeader.setPadding(0, 40, 0, 40);
        pcf_container.addPtrUIHandler(materialHeader);
        pcf_container.setHeaderView(materialHeader);
        // pcf_container.setHeaderView(view_head);
        pcf_container.setFooterView(new DefaultLoadMoreViewFooter_normal());
        pcf_container.setLoadMoreEnable(false);
        pcf_container.setOnLoadMoreListener(this);
        pcf_container.setPtrHandler(this);
        pcf_container.autoRefresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_submit_btn:
                Bundle bundle = new Bundle();
                bundle.putString("add", "add");
                startActivity_Bundle(this, AddAddreesActivity.class, bundle);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putString("add", "modify");
        try {
            if (!StringUtils.isEmpty(list.get(position).getString("name"))) {
                bundle.putString("name", list.get(position).getString("name"));
            } else {
                bundle.putString("name", "未设置");
            }
            if (!StringUtils.isEmpty(list.get(position).getString("tel"))) {
                bundle.putString("phone", list.get(position).getString("tel"));
            } else {
                bundle.putString("phone", "未设置");
            }
            if (!StringUtils.isEmpty(list.get(position).getString("address"))) {
                bundle.putString("address_detail", list.get(position).getString("address"));
            } else {
                bundle.putString("address_detail", "");
            }
            if (!StringUtils.isEmpty(list.get(position).getString("code"))) {
                bundle.putString("email_number", list.get(position).getString("code"));
            } else {
                bundle.putString("email_number", "未编辑");
            }
            if (!StringUtils.isEmpty(list.get(position).getString("country"))) {
                bundle.putString("address_city", list.get(position).getString("country"));
            } else {
                bundle.putString("address_city", "未编辑");
            }
            bundle.putString("id", list.get(position).getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        index = position;
        startActivity_Bundle(this, AddAddreesActivity.class, bundle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == 3) {
//            toastMessage("修改");
                Log.d("address",data.getStringExtra("json"));
                JSONObject jsonObject = new JSONObject(data.getStringExtra("json"));
                jsonObject.put("default",list.get(index).getString("default"));
                Log.d("address",jsonObject.toString());
                list.set(index, jsonObject);
                adapter.notifyDataSetChanged();
            } else if (resultCode == 4) {
//            toastMessage("添加");
                JSONObject jsonObject = new JSONObject(data.getStringExtra("json"));
                jsonObject.put("default","0");
                if(list.size()==0){
                    pcf_container.autoRefresh();
                }else{
                    list.add(jsonObject);
                    adapter.notifyDataSetChanged();
                }
            }
        }catch (Exception e){
            toastMessage("数据解析异常");
        }
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(pcf_container, listView, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        getAddressList();
    }

    @Override
    public void loadMore() {

    }

    /**
     * 获取列表
     */
    private void getAddressList() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid", MyApplication.getUid());
            //jsonObject.put("status",status);//非必传
            // jsonObject.put("additional",additional+"");//非必传
            jsonObject.put("username", MyApplication.getUserJson().getString("m_username"));
            jsonObject.put("pwd", MyApplication.getPasswprd());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.ADDRESS_MANAGE, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if (response.getInt("status") != 0) {
                        listView.setVisibility(View.GONE);
                        layout_empty.setVisibility(View.VISIBLE);
                        pcf_container.refreshComplete();
//                        pcf_container.loadMoreComplete(true);
                    } else {
                        list.clear();
                        if (response.getJSONArray("data") == null || response.getJSONArray("data").length() == 0) {
                            listView.setVisibility(View.GONE);
                            layout_empty.setVisibility(View.VISIBLE);
                        } else {
                            for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                                list.add(response.getJSONArray("data").getJSONObject(i));
                                final int finalI = i;
                            }
                            pcf_container.refreshComplete();
                            layout_empty.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    pcf_container.refreshComplete();
                   // pcf_container.loadMoreComplete(true);
                    layout_empty.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                pcf_container.refreshComplete();
               // pcf_container.loadMoreComplete(true);
                layout_empty.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        });
    }

}
