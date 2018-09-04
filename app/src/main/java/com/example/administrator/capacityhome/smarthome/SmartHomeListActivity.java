package com.example.administrator.capacityhome.smarthome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.PtrHandler;
import com.chanven.lib.cptr.header.MaterialHeader;
import com.chanven.lib.cptr.loadmore.GridViewWithHeaderAndFooter;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.example.administrator.capacityhome.BaseActivity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.smarthome.video.obj.MyCamera;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.SmartHomeListAdapter;
import http.RequestTag;
import utils.Md5;
import widget.DefaultLoadMoreViewFooter_normal;

/**
 * 小e管家某道门录像列表
 */
public class SmartHomeListActivity extends BaseActivity implements OnLoadMoreListener, PtrHandler {
    private GridViewWithHeaderAndFooter listView;
    private SmartHomeListAdapter adapter;
    private List<JSONObject> list;
    private PtrClassicFrameLayout pcf_container;
    private View viewParentHead;
    private Bundle bundle;
    private TextView smartList_tv_title;
   private TextView layout_empty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_home_list);
        bundle = getIntent().getBundleExtra("bundle");
        initView();
        initFrameLayout();
    }

    private void initFrameLayout() {
        pcf_container = ((PtrClassicFrameLayout) findViewById(R.id.pcf_container));
        layout_empty = (TextView) findViewById(R.id.layout_empty);
        MaterialHeader materialHeader = new MaterialHeader(this);
        materialHeader.setPadding(0, 40, 0, 40);
        pcf_container.addPtrUIHandler(materialHeader);
        pcf_container.setHeaderView(materialHeader);
        // pcf_container.setHeaderView(view_head);
        pcf_container.setFooterView(new DefaultLoadMoreViewFooter_normal());
        pcf_container.setLoadMoreEnable(false);
        pcf_container.setOnLoadMoreListener(this);
        pcf_container.setPtrHandler(this);
        //pcf_container.autoRefresh();
    }

    private void initView() {
        backActivity();
        findViewById(R.id.top_submit_btn).setVisibility(View.GONE);
        listView = (GridViewWithHeaderAndFooter) findViewById(R.id.grid_smartHomeList);
        list = new ArrayList<>();
//        for (int i = 0; i < 8; i++) {
//            list.add(new JSONObject());
//        }
        adapter = new SmartHomeListAdapter(list, this);
        viewParentHead = LayoutInflater.from(this).inflate(R.layout.layout_smarthome_head, null);
        smartList_tv_title = (TextView) viewParentHead.findViewById(R.id.smartList_tv_title);
        listView.addHeaderView(viewParentHead);
        listView.setAdapter(adapter);
        if(bundle!=null&&bundle.getString("title")!=null){
            smartList_tv_title.setText(bundle.getString("title"));
        }
        if(bundle!=null&&bundle.getString("door_id")!=null){
            getDoorLockList(bundle.getString("door_id"));
        }else{
            pcf_container.setVisibility(View.GONE);
            layout_empty.setVisibility(View.VISIBLE);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        MyCamera.init();
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(pcf_container, listView, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        pcf_container.refreshComplete();
    }

    @Override
    public void loadMore() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(800);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pcf_container.loadMoreComplete(false);
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 获取门锁详情列表
     */
    private void getDoorLockList(String door_id ) {
        showLoadingView();
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis()+"";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid",MyApplication.getUid());
            jsonObject.put("id",door_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl+RequestTag.DOOR_POLICE_RECORD, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try{
                    if(response.getInt("status")!=0){
                        pcf_container.setVisibility(View.GONE);
                        layout_empty.setVisibility(View.VISIBLE);
                    }else{
                        if(response.getJSONArray("data")==null||response.getJSONArray("data").length()==0){
                            pcf_container.setVisibility(View.GONE);
                            layout_empty.setVisibility(View.VISIBLE);
                        }else {
                            for (int i = 0; i <response.getJSONArray("data").length();i++) {
                                list.add(response.getJSONArray("data").getJSONObject(i));
                            }
                            layout_empty.setVisibility(View.GONE);
                            pcf_container.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                    }

                }catch (Exception e){
                    layout_empty.setVisibility(View.GONE);
                    pcf_container.setVisibility(View.VISIBLE);
                }
                dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("网络异常，请检查网络后重试");
                dismissLoadingView();
                layout_empty.setVisibility(View.GONE);
                pcf_container.setVisibility(View.VISIBLE);
            }
        });
    }
    /**
     * 打开h5门锁管理界面
     */
    private void oepn_doorManage_h5(String door_id ) {
        showLoadingView();
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis()+"";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid",MyApplication.getUid());
            jsonObject.put("id",door_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl+RequestTag.door_manage_h5, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try{
                    if(response.getInt("status")!=0){
                        pcf_container.setVisibility(View.GONE);
                        layout_empty.setVisibility(View.VISIBLE);
                    }else{
                        if(response.getJSONArray("data")==null||response.getJSONArray("data").length()==0){
                            pcf_container.setVisibility(View.GONE);
                            layout_empty.setVisibility(View.VISIBLE);
                        }else {
                            for (int i = 0; i <response.getJSONArray("data").length();i++) {
                                list.add(response.getJSONArray("data").getJSONObject(i));
                            }
                            layout_empty.setVisibility(View.GONE);
                            pcf_container.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                    }

                }catch (Exception e){
                    layout_empty.setVisibility(View.GONE);
                    pcf_container.setVisibility(View.VISIBLE);
                }
                dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("网络异常，请检查网络后重试");
                dismissLoadingView();
                layout_empty.setVisibility(View.GONE);
                pcf_container.setVisibility(View.VISIBLE);
            }
        });
    }

}
