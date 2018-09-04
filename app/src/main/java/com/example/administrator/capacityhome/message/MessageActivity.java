package com.example.administrator.capacityhome.message;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.MessageAdapter;
import http.RequestTag;
import utils.ACache;
import utils.ColumnUtils;
import utils.Md5;
import widget.DefaultLoadMoreViewFooter_normal;

/**
 * 消息
 */
public class MessageActivity extends BaseActivity implements View.OnClickListener, OnLoadMoreListener, PtrHandler {
    private PtrClassicFrameLayout pcf_container;
    private ListView listView_message;
    private boolean isUpdate = true;
    private List<JSONObject> list = new ArrayList<>();
    private MessageAdapter adapter;
    private int page = 1;
    private ColumnUtils columnUtils;
    private List<JSONObject> list_messageType = new ArrayList<>();
    private ACache aCache;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        aCache = ACache.get(this);
        aCache.remove("messageType");
        initView();
        if(aCache.getAsJSONArray("messageType")!=null){
            for (int i = 0; i < aCache.getAsJSONArray("messageType").length(); i++) {
                try {
                    list_messageType.add(aCache.getAsJSONArray("messageType").getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            adapter = new MessageAdapter(MessageActivity.this,list,list_messageType,aCache);
            listView_message.setAdapter(adapter);
            initFrameLayout();
        }else {
            getType();
        }
    }

    private void initView() {
        listView_message = (ListView) findViewById(R.id.listview_message);
        findViewById(R.id.relayout_back).setOnClickListener(this);

    }

    private void initFrameLayout() {
        pcf_container = ((PtrClassicFrameLayout) findViewById(R.id.pcf_container));
        MaterialHeader materialHeader = new MaterialHeader(this);
        materialHeader.setPadding(0, 40, 0, 40);
        pcf_container.addPtrUIHandler(materialHeader);
        pcf_container.setHeaderView(materialHeader);
        // pcf_container.setHeaderView(view_head);
        pcf_container.setFooterView(new DefaultLoadMoreViewFooter_normal());
        pcf_container.setLoadMoreEnable(true);
        pcf_container.setOnLoadMoreListener(this);
        pcf_container.setPtrHandler(this);
        pcf_container.autoRefresh();
    }

    /**
     * 获取消息类型
     */
    private void getType(){
        showLoadingView();
        if (columnUtils == null) {
            columnUtils = new ColumnUtils(this, new ColumnUtils.Result() {
                @Override
                public void onSuccess(String title, JSONArray jsonArray) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            list_messageType.add(jsonArray.getJSONObject(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    aCache.put("messageType",jsonArray,ACache.TIME_DAY*3);
                    adapter = new MessageAdapter(MessageActivity.this,list,list_messageType,aCache);
                    listView_message.setAdapter(adapter);
                     dismissLoadingView();
                     initFrameLayout();
//                    showPopwindow(title);
                }

                @Override
                public void onFailure(String msg) {
                    toastMessage(msg);
                }
            });
        }
        if(list_messageType.size()==0) {
            columnUtils.getColumn("", "memberMessage");
        }
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(pcf_container, listView_message, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        isUpdate = true;
        page = 1;
        getMessageList();
    }

    @Override
    public void loadMore() {
        page = page++;
        isUpdate = false;
        getMessageList();
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.relayout_back:
               finish();
               break;
       }
    }
    /**
     * 获取消息列表
     */
    private void getMessageList() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid", MyApplication.getUid());
            jsonObject.put("page",page+"");
            jsonObject.put("pagesize",MyApplication.getPageSize()+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.MESSAGE_LIST, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if (isUpdate) {
                        pcf_container.refreshComplete();
                    }
                    Log.d("messageas",response.toString());

                    if (response.getInt("status") != 0) {
                        toastMessage(response.getString("msg"));
                        pcf_container.loadMoreComplete(false);
                        list.clear();
                        adapter.notifyDataSetChanged();
                    } else {
                        if (response.getJSONObject("data").getJSONArray("list") == null || response.getJSONObject("data").getJSONArray("list").length() == 0) {

                            pcf_container.loadMoreComplete(false);
                            if (isUpdate) {
                                isUpdate = false;
                                list.clear();
                            }
                        } else {
                            if (isUpdate) {
                                isUpdate = false;
                                list.clear();
                            }
                            for (int i = 0; i < response.getJSONObject("data").getJSONArray("list").length(); i++) {
                                list.add(response.getJSONObject("data").getJSONArray("list").getJSONObject(i));
                            }
                            if(list.size()>=response.getJSONObject("data").getJSONObject("sum").getInt("s")){
                                //已加载完毕
                                pcf_container.loadMoreComplete(false);
                            }else{
                                pcf_container.loadMoreComplete(true);
                            }
//                            if (response.getJSONArray("data").length() < MyApplication.getPageSize()) {
//                                //已加载完毕
//                                pcf_container.loadMoreComplete(false);
//                            } else {
//                                pcf_container.loadMoreComplete(true);
//                            }


                        }
                        adapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    pcf_container.refreshComplete();
                   


                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                pcf_container.refreshComplete();
                pcf_container.loadMoreComplete(false);
                toastMessage(error_msg);
            }
        });
    }
}
