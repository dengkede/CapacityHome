package com.example.administrator.capacityhome.laundry;

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
import com.example.administrator.capacityhome.myorder.OrderDetailActivity;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import adapter.LaundryAdapter;
import adapter.StringAdapter;
import http.RequestTag;
import utils.DisplayUtil;
import utils.KeyBordUtil;
import utils.Md5;
import utils.StringUtils;
import widget.DefaultLoadMoreViewFooter_normal;

/**
 * 小e洗衣
 */
public class LaundryActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener, OnLoadMoreListener, PtrHandler {
    ListView listview_laundy;
    private LaundryAdapter laundryAdapter;
    private PtrClassicFrameLayout pcf_container;
    private TextView layout_empty;
    private List<JSONObject> list;
    private PopupWindow popupWindow;
    private TextView tv_type;
    private TextView pop_tvWithdrawal;//提现记录
    private TextView pop_tvConsumption;//洗衣卷消费
    private TextView pop_tvRecharge;//余额充值
    private TextView pop_tvBalance;//余额消费
    private int page = 1;
    private int additional = 0;//0=普通1=加急
    private String status;//订单状态
    private LinearLayout layout_parent;
    private boolean isUpdate = true;//判断是否是刷新

    private List<JSONObject> list_orderType = new ArrayList<>();
    private PopupWindow popWindow;
    private TextView tv_popTitle;
    private ListView listView_type;
    private StringAdapter stringAdapter;
    private TextView tv_laundry_totalCount;
    private TextView tv_laundy_currentCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry);
        initView();
        initOrderType();
        initFrameLayout();
    }

    private void initView() {
        setPageTitle("小e管家");
        submitBtnVisible("全部订单", this);
        backActivity();
        layout_empty = (TextView) findViewById(R.id.layout_empty);
        listview_laundy = (ListView) findViewById(R.id.listview_laundy);
        View layout_head = LayoutInflater.from(this).inflate(R.layout.layout_laundry_head,null);
        tv_laundry_totalCount = (TextView) layout_head.findViewById(R.id.tv_laundry_totalCount);
        tv_laundy_currentCount = (TextView) layout_head.findViewById(R.id.tv_laundy_currentCount);
        try {
            tv_laundry_totalCount.setText(MyApplication.jsonObject_composite.getString("wardrobe_orders"));
            tv_laundy_currentCount.setText(MyApplication.jsonObject_composite.getString("wardrobe_orders_use"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        listview_laundy.addHeaderView(layout_head);
        tv_type = (TextView) layout_head.findViewById(R.id.tv_type);
        list = new ArrayList<>();
        laundryAdapter = new LaundryAdapter(list, this);
        listview_laundy.setAdapter(laundryAdapter);
        listview_laundy.setOnItemClickListener(this);
        tv_type.setOnClickListener(this);

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
        status = "0,1,2,3,4,5";
        tv_type.setText("未完成订单");
        pcf_container.autoRefresh();
    }

    /**
     * 洗衣订单类型
     */
    private void initOrderType(){
        if(MyApplication.getConfigrationJson()!=null){
            try {
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
        if(position!=0) {
            Bundle bundle = new Bundle();
            try {
                bundle.putString("order_id", list.get(position-1).getString("id"));
                bundle.putString("number", list.get(position-1).getString("number"));
                bundle.putString("status", list.get(position-1).getString("status"));
                bundle.putInt("type",list.get(position-1).getInt("status"));
                bundle.putString("additional", list.get(position-1).getString("additional"));
                bundle.putString("ark_name", list.get(position-1).getString("ark_name") +" "+ list.get(position-1).getString("ark_box_number"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity_Bundle(this, OrderDetailActivity.class, bundle);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_type:
                //showPopWindow(getView());
                showPopwindow("选择洗衣状态");
                break;
            case R.id.top_submit_btn:
                //全部订单
                status = "";
                tv_type.setText("全部订单");
                pcf_container.autoRefresh();
                break;
        }
    }

    //请求条件初始化
private void initDate(){
    page = 1;
    isUpdate = true;
    pcf_container.loadMoreComplete(true);
}

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(pcf_container, listview_laundy, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        initDate();
        getDoorLockList();
    }

    @Override
    public void loadMore() {
        isUpdate  = false;
        page = page+1;
        getDoorLockList();
    }

    /**
     * 获取洗衣列表
     */
    private void getDoorLockList() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis()+"";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid",MyApplication.getUid());
            jsonObject.put("page",page+"");
            jsonObject.put("pagesize",MyApplication.getPageSize()+"");//非必传
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
        MyOkHttp.get().post(this, RequestTag.BaseUrl+RequestTag.LAUNDY_ORDER, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try{
                    Log.d("responsesss",response.toString());
                    if(isUpdate){
                        pcf_container.refreshComplete();
                    }
                    if(response.getInt("status")!=0){
                        pcf_container.loadMoreComplete(false);
                        layout_empty.setVisibility(View.VISIBLE);
                        list.clear();
                        laundryAdapter.notifyDataSetChanged();
                    }else{
                        if(response.getJSONArray("data")==null||response.getJSONArray("data").length()==0){
                            listview_laundy.setVisibility(View.GONE);
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
                            listview_laundy.setVisibility(View.VISIBLE);
                        }
                        laundryAdapter.notifyDataSetChanged();
                    }

                }catch (Exception e){
                    pcf_container.refreshComplete();
                    pcf_container.loadMoreComplete(false);
                    layout_empty.setVisibility(View.GONE);
                    listview_laundy.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                pcf_container.refreshComplete();
                pcf_container.loadMoreComplete(false);
                layout_empty.setVisibility(View.GONE);
                listview_laundy.setVisibility(View.VISIBLE);
            }
        });
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
                DisplayUtil.backgroundAlpha(1.0f,LaundryActivity.this);
            }
        });

        DisplayUtil.backgroundAlpha(0.2f, LaundryActivity.this);
        popWindow.showAtLocation(layout_parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }




}
