package com.example.administrator.capacityhome;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.PtrHandler;
import com.chanven.lib.cptr.header.MaterialHeader;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.AllEvaluateListAdapter;
import http.RequestTag;
import utils.Md5;
import utils.StringUtils;
import widget.DefaultLoadMoreViewFooter_normal;

/**
 * 所有评价列表
 */
public class EvaluateListActivity extends BaseActivity implements View.OnClickListener, OnLoadMoreListener, PtrHandler {
    private ListView listview_evaluate;
    private List<JSONObject> list;
    private AllEvaluateListAdapter adapter;
    private PtrClassicFrameLayout pcf_container;
    private boolean isUpdate = false;
    private int page = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate_list);
        initView();
        initFrameLayout();
    }

    private void initView() {
        listview_evaluate = (ListView) findViewById(R.id.listview_evaluate);
        list = new ArrayList<>();
        adapter = new AllEvaluateListAdapter(list,this);
        listview_evaluate.setAdapter(adapter);
        findViewById(R.id.tv_evaluate).setOnClickListener(this);
        findViewById(R.id.relayout_imgBack).setOnClickListener(this);
        listview_evaluate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        if (!StringUtils.isEmpty(list.get(position).getString("pics"))&& !list.get(position).getString("pics").equals("null")) {
                            ArrayList lists = new ArrayList();
                            for (int i = 0; i < list.get(position).getString("pics").split(",").length; i++) {
                                lists.add(list.get(position).getString("pics").split(",")[i]);
                            }
                            startActivity_ImagrPager(EvaluateListActivity.this, 0, lists, false);
                        }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

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
        getAllEvaluateList();
        //pcf_container.autoRefresh();
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(pcf_container, listview_evaluate, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        isUpdate = true;
        page = 1;
        getAllEvaluateList();
    }

    @Override
    public void loadMore() {
        isUpdate = false;
        page = page+1;
        getAllEvaluateList();

    }
    /**
     * 所有评论列表
     */
    private void getAllEvaluateList() {
        if(list.size() == 0) {
            showLoadingView();
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            //jsonObject.put("uid", MyApplication.getUid());//用户ID（不传时hot不能为空）
            jsonObject.put("hot","2");
            jsonObject.put("page", page);
            jsonObject.put("pagesize", MyApplication.getPageSize() + "");
           // jsonObject.put("order_id", order_id);//用户订单ID（不传时hot不能为空）
            // jsonObject.put("id","");//当传入ID时表示取当前这条评价，但不能取子评价（不传时hot不能为空）
            if(MyApplication.getUserJson() != null) {
                jsonObject.put("username", MyApplication.getUserJson().getString("m_username"));
                jsonObject.put("pwd", MyApplication.getPasswprd());
            }else{

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.LAUNDY_ORDER_DOUBT, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    Log.d("response", response.toString());
                    if(isUpdate){
                        list.clear();
                        pcf_container.refreshComplete();
                        isUpdate = false;
                    }
                    if (response.getInt("status") != 0) {
                        pcf_container.loadMoreComplete(false);
                        toastMessage(response.getString("msg"));
                    } else {
                        if (response.getJSONArray("data") == null) {
                        } else {
                            for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                                list.add(response.getJSONArray("data").getJSONObject(i));
                            }
                            if(response.getInt("total") == list.size()){
                                pcf_container.loadMoreComplete(false);
                            }else{
                                pcf_container.loadMoreComplete(true);
                            }

                        }
                    }

                } catch (Exception e) {
                    pcf_container.loadMoreComplete(false);
                }
                adapter.notifyDataSetChanged();
                dismissLoadingView();

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                pcf_container.loadMoreComplete(false);
                pcf_container.refreshComplete();
                dismissLoadingView();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_evaluate:
                //我要评价
                toastMessage("下单后请在订单详情界面发表评价。");
                break;
            case R.id.relayout_imgBack:
                finish();
                break;
        }

    }
}
