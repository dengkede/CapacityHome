package com.example.administrator.capacityhome.myorder;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import com.example.administrator.capacityhome.laundry.LaundryActivity;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import adapter.MyOrderAdapter;
import adapter.StringAdapter;
import http.RequestTag;
import utils.DisplayUtil;
import utils.KeyBordUtil;
import utils.Md5;
import utils.MoneyUtil;
import utils.StringUtils;
import widget.DefaultLoadMoreViewFooter_normal;

/**
 * 我的订单
 */
public class MyOrderActivity extends BaseActivity implements AdapterView.OnItemClickListener, OnLoadMoreListener, PtrHandler, View.OnClickListener {
    ListView listView_order;
    private List<JSONObject> list;
    private MyOrderAdapter adapter;
    private PtrClassicFrameLayout pcf_container;
    private View viewParentHead;
    private TextView tv_order_consumptionMoney;
    private TextView tv_myorder_totalCount;
    private TextView tv_type;
    private boolean isUpdate = true;
    private int page = 1;
    private PopupWindow popWindow;
    private List<JSONObject> list_orderType = new ArrayList<>();
    private TextView tv_popTitle;
    private ListView listView_type;
    private StringAdapter stringAdapter;
    private LinearLayout layout_parent;
    private String status;//订单状态
    private TextView tv_QuaryMoneyType;//查询金额类型，总金额
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        initView();
        initFrameLayout();
        initOrderType();
    }

    private void initView() {
        backActivity();
        findViewById(R.id.top_submit_btn).setVisibility(View.GONE);
        listView_order = (ListView) findViewById(R.id.listview_order);
        list = new ArrayList<>();
        adapter = new MyOrderAdapter(list, this);
        listView_order.setOnItemClickListener(this);
        viewParentHead = LayoutInflater.from(this).inflate(R.layout.layout_myorder_head, null);
        tv_type = (TextView) viewParentHead.findViewById(R.id.tv_type);
        tv_QuaryMoneyType = (TextView) viewParentHead.findViewById(R.id.tv_QuaryMoneyType);
        tv_type.setOnClickListener(this);
        tv_order_consumptionMoney = (TextView) viewParentHead.findViewById(R.id.tv_order_consumptionMoney);
        tv_myorder_totalCount = (TextView) viewParentHead.findViewById(R.id.tv_myorder_totalCount);
        try {
            tv_myorder_totalCount.setText(MyApplication.jsonObject_composite.getString("wardrobe_orders"));
        }catch (Exception e){

        }
        listView_order.addHeaderView(viewParentHead, null, true);
        //禁止头部出现分割线
        listView_order.setHeaderDividersEnabled(false);
        listView_order.setAdapter(adapter);
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
     * 洗衣订单类型
     */
    private void initOrderType(){
        if(MyApplication.getConfigrationJson()!=null){
            try {
                JSONObject jsonObject3 = new JSONObject();
                jsonObject3.put("key","");
                jsonObject3.put("value","全部订单");
                list_orderType.add(jsonObject3);

                JSONObject jsonObject = MyApplication.getConfigrationJson().getJSONObject("xorderstatus");
                Iterator<?> it = jsonObject.keys();
                while(it.hasNext()){//遍历JSONObject
                    String bb2 = (String) it.next().toString();//key
                    JSONObject jsonObject2 = new JSONObject();
                    jsonObject2.put("key",bb2);
                    jsonObject2.put("value",jsonObject.getString(bb2));
                    list_orderType.add(jsonObject2);
                    bb2 = null;
                    jsonObject2 = null;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0) {
            Bundle bundle = new Bundle();
            try {
                Log.d("orderDetail2",list.get(position-1).toString());
                bundle.putString("order_id", list.get(position-1).getString("id"));
                bundle.putString("number", list.get(position-1).getString("number"));
                bundle.putString("status", list.get(position-1).getString("status"));
                bundle.putString("additional", list.get(position-1).getString("additional"));
                bundle.putInt("type",list.get(position-1).getInt("status"));
                bundle.putString("ark_name", list.get(position-1).getString("ark_name") +" "+ list.get(position-1).getString("ark_box_number"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            startActivity_Bundle(this, OrderDetailActivity.class, bundle);
        }
        // startActivity(this,OrderDetailActivity.class);
    }


    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(pcf_container, listView_order, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        page = 1;
        isUpdate = true;
        getOrderList();
    }

    @Override
    public void loadMore() {
        page = page + 1;
        isUpdate = false;
        getOrderList();
    }

    /**
     * 获取订单列表
     */
    private void getOrderList() {
        KeyBordUtil.hintKeyboard(this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid", MyApplication.getUid());
            jsonObject.put("page", page + "");
            jsonObject.put("pagesize", MyApplication.getPageSize() + "");//非必传
            if(!StringUtils.isEmpty(status)) {
                jsonObject.put("status", status);//非必传
            }
            // jsonObject.put("additional",additional+"");//非必传
            jsonObject.put("username", MyApplication.getUserJson().getString("m_username"));
            jsonObject.put("pwd", MyApplication.getPasswprd());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.LAUNDY_ORDER, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    Log.d("myorderlist",response.toString());
                    if(StringUtils.isEmpty(status)){
                        //显示总金额
                        tv_QuaryMoneyType.setText("总消费");
                    }else{
                        tv_QuaryMoneyType.setText("查询金额");
                    }
                    if (isUpdate) {
                        pcf_container.refreshComplete();
                    }
                    if (response.getInt("status") != 0) {
                        tv_order_consumptionMoney.setText("0");
                        pcf_container.loadMoreComplete(false);
                        list.clear();
                        adapter.notifyDataSetChanged();
                    } else {
                        tv_order_consumptionMoney.setText(MoneyUtil.m2((response.getDouble("moneys")+response.getLong("addmoneys")+response.getDouble("timeoutmoneys"))/1000));
                        if (response.getJSONArray("data") == null || response.getJSONArray("data").length() == 0) {
                            listView_order.setVisibility(View.GONE);
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
                            for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                                list.add(response.getJSONArray("data").getJSONObject(i));
                            }
                            if (list.size() >= response.getInt("total")) {
                                //已加载完毕
                                pcf_container.loadMoreComplete(false);
                            } else {
                                pcf_container.loadMoreComplete(true);
                            }

                            listView_order.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    pcf_container.refreshComplete();
                    pcf_container.loadMoreComplete(false);
                    listView_order.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                pcf_container.refreshComplete();
                if(StringUtils.isEmpty(status)){
                    //显示总金额
                    tv_QuaryMoneyType.setText("总消费");
                }else{
                    tv_QuaryMoneyType.setText("查询金额");
                }
                pcf_container.loadMoreComplete(false);
                listView_order.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_type:
                showPopwindow("选择订单类型");
                break;
        }
    }
    private void showPopwindow(String title) {
        KeyBordUtil.hintKeyboard(this);
        if (popWindow == null) {
            layout_parent = (LinearLayout) findViewById(R.id.layout_parent);
            View popView = View.inflate(this, R.layout.layout_pop_list, null);
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.6);
            tv_popTitle = (TextView) popView.findViewById(R.id.tv_popTitle);
            tv_popTitle.setText(title);
            listView_type = (ListView) popView.findViewById(R.id.layout_pop_list);
            stringAdapter = new StringAdapter(this, list_orderType, "value");
            listView_type.setAdapter(stringAdapter);
            if (list_orderType.size() >= 7) {
                popWindow = new PopupWindow(popView, width, height);
            } else {
                popWindow = new PopupWindow(popView, width, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
            popWindow.setAnimationStyle(R.style.popwin_anim_style);
            popWindow.setFocusable(true);
            popWindow.setOutsideTouchable(false);// 设置同意在外点击消失
            ColorDrawable dw = new ColorDrawable(0x30000000);
            popWindow.setBackgroundDrawable(dw);
            listView_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
//                        type = list_bank.get(position).getString("id");
//                        addBank_tv_bankName.setText(list_bank.get(position).getString("title"));
                        tv_type.setText(list_orderType.get(position).getString("value"));
                        status = list_orderType.get(position).getString("key");
                        pcf_container.autoRefresh();
                        popWindow.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                DisplayUtil.backgroundAlpha(1.0f,MyOrderActivity.this);
            }
        });

        DisplayUtil.backgroundAlpha(0.2f, MyOrderActivity.this);
        popWindow.showAtLocation(layout_parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
}
