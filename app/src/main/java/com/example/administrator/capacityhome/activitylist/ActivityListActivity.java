package com.example.administrator.capacityhome.activitylist;

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
import com.example.administrator.capacityhome.WebViewUrlActivity;
import com.example.administrator.capacityhome.combo.ComboDetailActivity;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.ActivityListAdapter;
import adapter.StoreComnoAdapter;
import http.RequestTag;
import utils.Md5;
import widget.DefaultLoadMoreViewFooter_normal;

/**
 * 活动列表
 */
public class ActivityListActivity extends BaseActivity implements OnLoadMoreListener, PtrHandler, AdapterView.OnItemClickListener {
    private ListView listview_activity;
    private PtrClassicFrameLayout pcf_container;
    private TextView layout_empty;
    private ActivityListAdapter adapter;
    private List<JSONObject> list = new ArrayList<>();
    private boolean isUpdate = true;//判断是否是刷新
    private int page = 1;
    private String mark  = "activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        if(getIntent().getStringExtra("mark")!=null){
            mark = getIntent().getStringExtra("mark");
        }
        intView();
        initFrameLayout();
    }
    private void intView(){
        backActivity();
        findViewById(R.id.top_submit_btn).setVisibility(View.GONE);
        layout_empty = (TextView) findViewById(R.id.layout_empty);
        listview_activity = (ListView) findViewById(R.id.listview_activity);
        adapter = new ActivityListAdapter(this,list);
        listview_activity.setAdapter(adapter);
        listview_activity.setOnItemClickListener(this);
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
    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(pcf_container, listview_activity, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        isUpdate = true;
        page = 1;
        getActivityList();
    }

    @Override
    public void loadMore() {
        isUpdate  = false;
        page = page+1;
        getActivityList();
    }

    /**
     * 获取活动列表
     */
    private void getActivityList() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis()+"";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("mark",mark);
            jsonObject.put("page",page+"");
            //jsonObject.put("pagesize",MyApplication.getPageSize()+"");//非必传

            // jsonObject.put("additional",additional+"");//非必传
//            jsonObject.put("lat", MyApplication.getUserJson().getString("m_username"));
//            jsonObject.put("lon", MyApplication.getPasswprd());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl+RequestTag.ARTICAL_LIST, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try{

                    if(isUpdate){
                        pcf_container.refreshComplete();
                    }
                    if(response.getInt("status")!=0){
                        pcf_container.loadMoreComplete(false);
                        layout_empty.setVisibility(View.VISIBLE);
                        list.clear();
                        adapter.notifyDataSetChanged();
                    }else{
                        if(response.getJSONArray("data")==null||response.getJSONArray("data").length()==0){
                            listview_activity.setVisibility(View.GONE);
                            pcf_container.loadMoreComplete(false);
                            if(isUpdate){
                                isUpdate = false;
                                list.clear();
                            }
                            layout_empty.setVisibility(View.VISIBLE);
                        }else {
                            if(isUpdate){
                                isUpdate = false;
                                list.clear();
                            }
                            for (int i = 0; i <response.getJSONArray("data").length();i++) {
                                list.add(response.getJSONArray("data").getJSONObject(i));
                            }
                            if(list.size() >= response.getInt("total")){
                                //已加载完毕
                                pcf_container.loadMoreComplete(false);
                            }else{
                                pcf_container.loadMoreComplete(true);
                            }

                            layout_empty.setVisibility(View.GONE);
                            listview_activity.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                    }

                }catch (Exception e){
                    pcf_container.refreshComplete();
                    pcf_container.loadMoreComplete(false);
                    layout_empty.setVisibility(View.GONE);
                    listview_activity.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                pcf_container.refreshComplete();
                pcf_container.loadMoreComplete(false);
                layout_empty.setVisibility(View.GONE);
                listview_activity.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this,WebViewUrlActivity.class);
        try {
            intent.putExtra("mark", mark);
            intent.putExtra("live",true);
            intent.putExtra("id", list.get(i).getString("id"));
            intent.putExtra("title",list.get(i).getString("title"));
            startActivityForResult(intent,2);
        }catch (Exception e){
            toastMessage("没有相关数据");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 3){
            if(data.getStringExtra("mark")!=null) {
                mark = data.getStringExtra("mark");
                pcf_container.autoRefresh();
            }
        }
    }
}
