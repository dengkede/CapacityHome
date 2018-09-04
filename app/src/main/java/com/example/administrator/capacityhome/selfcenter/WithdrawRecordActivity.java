package com.example.administrator.capacityhome.selfcenter;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import adapter.WithdrawlRecord_Adapter;
import http.RequestTag;
import utils.CustomDatePicker;
import utils.Md5;
import utils.StringUtils;
import utils.TimeUtils;
import widget.DefaultLoadMoreViewFooter_normal;

import static android.R.attr.end;
import static android.R.attr.key;

/**
 * 提现记录
 */
public class WithdrawRecordActivity extends BaseActivity implements View.OnClickListener, OnLoadMoreListener, PtrHandler {
    ListView listView;
    private PtrClassicFrameLayout pcf_container;
    private WithdrawlRecord_Adapter adapter;
    private boolean isUpdate = true;//是否处于刷新状态
    private List<JSONObject> list = new ArrayList<>();
    private int page;
    private View view_head;
    private TextView tv_timeBegin;//开始时间
    private TextView tv_timeEnd;//结束时间
    private EditText fzt_moneyetail_et_serch;//输入框内容
    private ImageView fzt_moneydetail_item_img_serch;
    private CustomDatePicker customDatePicker;//时间选择
    private String start_time;
    private String end_time;
    private TextView fzt_moneyetail_tv_money;//可提现总额
    private TextView fzt_moneyetail_tv_serch_s;//当前查询条件下的总额
    private TextView fzt_moneyetail_tv_sum_s;//总额
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_record);
        initView();
        initFrameLayout();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.listview_withdrawDetail);
        adapter = new WithdrawlRecord_Adapter(this, list);
        findViewById(R.id.layout_top).setOnClickListener(this);
        view_head = LayoutInflater.from(this).inflate(R.layout.withdraw_moneydetail_header, null);
        fzt_moneydetail_item_img_serch = (ImageView) view_head.findViewById(R.id.fzt_moneydetail_item_img_serch);
        fzt_moneyetail_et_serch = (EditText) view_head.findViewById(R.id.fzt_moneyetail_et_serch);
        tv_timeBegin = (TextView) view_head.findViewById(R.id.tv_timeBegin);
        tv_timeEnd = (TextView) view_head.findViewById(R.id.tv_timeEnd);
        fzt_moneyetail_tv_money = (TextView) view_head.findViewById(R.id.fzt_moneyetail_tv_money);
        fzt_moneyetail_tv_serch_s = (TextView) view_head.findViewById(R.id.fzt_moneyetail_tv_serch_s);
        fzt_moneyetail_tv_sum_s = (TextView) view_head.findViewById(R.id.fzt_moneyetail_tv_sum_s);
        tv_timeBegin.setOnClickListener(this);
        tv_timeEnd.setOnClickListener(this);
        fzt_moneydetail_item_img_serch.setOnClickListener(this);
        listView.addHeaderView(view_head, null, true);
        //禁止头部出现分割线
        listView.setHeaderDividersEnabled(false);
        listView.setAdapter(adapter);
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
        return PtrDefaultHandler.checkContentCanBePulledDown(pcf_container, listView, header);
    }

    /***
     * 查询条件的初始化
     */
    private void initDate() {
        page = 1;
        pcf_container.loadMoreComplete(true);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        isUpdate = true;
        initDate();
        getWithdrawRecord();
    }

    @Override
    public void loadMore() {
        isUpdate = false;
        page = page + 1;
        getWithdrawRecord();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_top:
                finish();
                break;
            case R.id.fzt_moneydetail_item_img_serch:
                //搜索按钮
                try{
                    if(Long.parseLong(TimeUtils.getTimestamp(tv_timeBegin.getText().toString(),"yyyy-MM-dd"))>
                            Long.parseLong(TimeUtils.getTimestamp(tv_timeEnd.getText().toString(),"yyyy-MM-dd"))){
                        Toast.makeText(this,"开始时间应该小于结束时间。", Toast.LENGTH_SHORT).show();
                    }else if(fzt_moneyetail_et_serch.getText().toString().contains(" ")){
                        Toast.makeText(this,"搜索内容不应含有空格，请检查后重新输入。",Toast.LENGTH_SHORT).show();
                    }else{
                        start_time = Long.parseLong(TimeUtils.getTimestamp(tv_timeBegin.getText().toString(),"yyyy-MM-dd"))+"";
                        end_time = Long.parseLong(TimeUtils.getTimestamp(tv_timeEnd.getText().toString(),"yyyy-MM-dd"))+"";
                        getWithdrawRecord();
                    }
                }catch (Exception e){
                    if(fzt_moneyetail_et_serch.getText().toString().contains(" ")){
                        Toast.makeText(this,"搜索内容不应含有空格，请检查后重新输入。",Toast.LENGTH_SHORT).show();
                    }else{
                        getWithdrawRecord();
                    }
                }
//                getWithdrawRecord();
                break;
            case R.id.tv_timeBegin:
                TimeUtils.initDatePicker(this,customDatePicker,tv_timeBegin,"请选择开始日期");
                break;
            case R.id.tv_timeEnd:
                if(tv_timeBegin.getText().toString().contains("开始")){
                    Toast.makeText(this,"请先选择开始时间。",Toast.LENGTH_SHORT).show();
                }else {
                    TimeUtils.initDatePicker(this, customDatePicker, tv_timeEnd,"请选择结束日期");
                }
                break;
        }
    }

    /**
     * 提现记录
     */
    private void getWithdrawRecord() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid", MyApplication.getUid());
            jsonObject.put("page", page + "");
            if(!StringUtils.isEmpty(start_time)&&!StringUtils.isEmpty(end_time)) {
                jsonObject.put("start_time", start_time);
                jsonObject.put("end_time", end_time);
            }
            if(!fzt_moneyetail_et_serch.getText().toString().isEmpty()) {
                jsonObject.put("keyword", fzt_moneyetail_et_serch.getText().toString());
            }

//            jsonObject.put("keyword", bank_id);//"非必须"
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.WITHDRAW_RECORD, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, final JSONObject response) {
                try {
                    if (isUpdate) {
                        list.clear();
                        pcf_container.refreshComplete();
                        isUpdate = false;
                    }
                    Log.d("reques_order",response.toString());
                    if (response.getInt("status") != 0) {
                        toastMessage(response.getString("msg"));
                        pcf_container.loadMoreComplete(false);
                        list.clear();
                        adapter.notifyDataSetChanged();
                    } else {

                        if (response.getJSONObject("data").getJSONArray("list").length() < 10) {
                            pcf_container.loadMoreComplete(false);
                        } else {
                            pcf_container.loadMoreComplete(true);
                        }

                        if (response.getJSONObject("data").getJSONArray("list") == null || response.getJSONObject("data").getJSONArray("list").length() == 0) {
                        } else {
                            for (int i = 0; i < response.getJSONObject("data").getJSONArray("list").length(); i++) {
                                list.add(response.getJSONObject("data").getJSONArray("list").getJSONObject(i));
                            }
                            pcf_container.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                        fzt_moneyetail_tv_sum_s.setText(response.getJSONObject("data").getJSONObject("total").getDouble("z")/1000+"");
                        fzt_moneyetail_tv_money.setText(response.getJSONObject("data").getDouble("money")/1000+"");
                        fzt_moneyetail_tv_serch_s.setText(response.getJSONObject("data").getJSONObject("search_sum").getDouble("z")/1000+"");
                    }


                } catch (Exception e) {
                    pcf_container.setVisibility(View.VISIBLE);
                    if (isUpdate) {
                        isUpdate = false;
                        pcf_container.refreshComplete();
                    } else {
                        pcf_container.loadMoreComplete(false);
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("网络异常，请检查网络后重试");
                if (isUpdate) {
                    isUpdate = false;
                    pcf_container.refreshComplete();
                    pcf_container.loadMoreComplete(false);
                }
                pcf_container.setVisibility(View.VISIBLE);
            }
        });
    }
}
