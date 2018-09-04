package com.example.administrator.capacityhome.selfcenter;

import android.graphics.drawable.ColorDrawable;
import android.icu.math.BigDecimal;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import adapter.MoneyDetailAdapter;
import adapter.PopListAdapter;
import adapter.StringAdapter;
import http.RequestTag;
import utils.DisplayUtil;
import utils.KeyBordUtil;
import utils.Md5;
import utils.StringUtils;
import widget.DefaultLoadMoreViewFooter_normal;

/**
 * 资金明细
 */
public class MoneyDetailActivity extends BaseActivity implements View.OnClickListener,OnLoadMoreListener, PtrHandler {
    ListView listView;
    private PtrClassicFrameLayout pcf_container;
    private LinearLayout layout_parent;
    private TextView layout_empty;
    private MoneyDetailAdapter adapter;
    private List<JSONObject> list;
    private TextView moneyDetail_tvType;//
    private LinearLayout layout_chooseType;//选择类型
    private PopupWindow popWindow;
    private List<JSONObject> list_type = new ArrayList<>();
    private ListView listView_type;
    private TextView tv_popTitle;
    private StringAdapter stringAdapter;
    private int page = 1;
    private String money_type = "money";//默认余额记录
    private boolean isUpdate = true;//是否处于刷新状态
    private TextView tv_totalEarning;//总收入
    private TextView tv_totalExpenditure;//总支出
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_detail);
        initView();
        initType();
        initFrameLayout();
    }

    /**
     * 选择type
     */
    private void initType(){
        if(list_type.size()==0&&MyApplication.getConfigrationJson()!=null){
            try {
                JSONObject jsonObject = MyApplication.getConfigrationJson().getJSONObject("moneytype");
                Iterator<?> it = jsonObject.keys();
                while(it.hasNext()){//遍历JSONObject
                    String bb2 = (String) it.next().toString();//key
                    JSONObject jsonObject2 = new JSONObject();
                    jsonObject2.put("key",bb2);
                    jsonObject2.put("value",jsonObject.getString(bb2));
                    boolean isAdd = true;
                    for (int i = 0;i<MyApplication.getConfigrationJson().getJSONArray("moneynotype").length();i++){
                        if(bb2.equals(MyApplication.getConfigrationJson().getJSONArray("moneynotype").getString(i))){
                            isAdd = false;
                            break;
                        }
                    }
                    if(isAdd){
                        list_type.add(jsonObject2);
                    }
                    bb2 = null;
                    jsonObject2 = null;
                }
                if(getIntent().getBundleExtra("bundle")!=null&&!StringUtils.isEmpty(getIntent().getBundleExtra("bundle").getString("type"))){
                    money_type = getIntent().getBundleExtra("bundle").getString("type");
                    if(getIntent().getBundleExtra("bundle").getString("title")==null){
                        for (int i = 0;i<list_type.size();i++){
                            if(list_type.get(i).getString("key").equals(money_type)){
                                moneyDetail_tvType.setText(list_type.get(i).getString("value"));
                                break;
                            }
                        }

                    }else {
                        moneyDetail_tvType.setText(getIntent().getBundleExtra("bundle").getString("title"));
                    }
                }else {
                    if (list_type.size() > 0) {
                        money_type = list_type.get(0).getString("key");
                        moneyDetail_tvType.setText(list_type.get(0).getString("value"));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void initView() {
        backActivity();
        layout_parent = (LinearLayout) findViewById(R.id.layout_parent);
        findViewById(R.id.top_submit_btn).setVisibility(View.GONE);
        tv_totalEarning = (TextView) findViewById(R.id.tv_totalEarning);
        tv_totalExpenditure = (TextView) findViewById(R.id.tv_totalExpenditure);
        moneyDetail_tvType = (TextView) findViewById(R.id.moneyDetail_tvType);
        layout_chooseType = (LinearLayout) findViewById(R.id.layout_chooseType);
        list = new ArrayList<>();
        listView = (ListView)findViewById(R.id.listview_moneyDetail);

        layout_chooseType.setOnClickListener(this);
        adapter = new MoneyDetailAdapter(list,this);
        listView.setAdapter(adapter);
//        Bundle bundle = getIntent().getBundleExtra("bundle");
//        if(bundle.get("main_laundy")!=null&&bundle.get("main_laundy").equals("main_laundy")){
//            moneyDetail_tvType.setText("洗衣卷消费");
//            adapter.notifyDataSetChanged();
//        }
        if(getIntent().getBundleExtra("bundle")!=null&&!StringUtils.isEmpty(getIntent().getBundleExtra("bundle").getString("type"))){
            money_type = getIntent().getBundleExtra("bundle").getString("type");
            moneyDetail_tvType.setText(getIntent().getBundleExtra("bundle").getString("title"));
        }
    }

    /***
     * 查询条件的初始化
     */
    private void initDate(){
        page = 1;
//        pcf_container.loadMoreComplete(true);
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(pcf_container, listView, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame)
    {
        isUpdate = true;
        initDate();
        getMoneyDetail();
    }
    @Override
    public void loadMore() {
        isUpdate = false;
        page = page+1;
        getMoneyDetail();
    }

    private void initFrameLayout() {
        pcf_container = ((PtrClassicFrameLayout) findViewById(R.id.pcf_container));
        layout_empty = (TextView) findViewById(R.id.layout_empty);
        layout_empty.setBackgroundColor(ContextCompat.getColor(this,R.color.fontcolor_f2));
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_chooseType:
              //  showPopWindow(getView());
                showPopwindow("请选择类型");
                break;
        }
    }

    /**
     * 资金明细
     */
    private void getMoneyDetail() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid", MyApplication.getUid());
            jsonObject.put("page",page+"");
            jsonObject.put("username", MyApplication.getUserJson().getString("m_username"));
            jsonObject.put("pwd", MyApplication.getPasswprd());
            jsonObject.put("money_type", money_type);
//            jsonObject.put("keyword", bank_id);//"非必须"
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.MONEY_DETAIL, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, final JSONObject response) {
                try{
                    if(isUpdate){
                        pcf_container.refreshComplete();
                        list.clear();
                        isUpdate = false;
                    }
                    Log.d("moneyDetail",response.toString());
                    if(response.getJSONObject("data").getJSONObject("search_sum").getDouble("s")==0){
                        tv_totalEarning.setText("0");
                    }else {
                        tv_totalEarning.setText(setMoney2(response.getJSONObject("data").getJSONObject("search_sum").getDouble("s") / 1000));
                    }
                    if(response.getJSONObject("data").getJSONObject("search_sum").getDouble("z")==0){
                        tv_totalExpenditure.setText("0");
                    }else {
                        tv_totalExpenditure.setText(setMoney2(response.getJSONObject("data").getJSONObject("search_sum").getDouble("z") / 1000));
                    }
                    if(response.getInt("status")!=0){
                        pcf_container.setVisibility(View.GONE);
                        layout_empty.setVisibility(View.VISIBLE);
                        pcf_container.loadMoreComplete(false);
                    }else{
                        if(response.getJSONObject("data").getJSONArray("list").length()<MyApplication.getPageSize()){
                            pcf_container.loadMoreComplete(false);
                        }else{
                            pcf_container.loadMoreComplete(true);
                        }

                        if(response.getJSONObject("data").getJSONArray("list")==null||response.getJSONObject("data").getJSONArray("list").length()==0){
                            pcf_container.setVisibility(View.GONE);
                            layout_empty.setVisibility(View.VISIBLE);
                        }else {
                            for (int i = 0; i <response.getJSONObject("data").getJSONArray("list").length();i++) {
                                list.add(response.getJSONObject("data").getJSONArray("list").getJSONObject(i));
                            }
                            layout_empty.setVisibility(View.GONE);
                            pcf_container.setVisibility(View.VISIBLE);
                        }
                    }

                }catch (Exception e){
                    layout_empty.setVisibility(View.GONE);
                    pcf_container.setVisibility(View.VISIBLE);
                    if(isUpdate){
                        isUpdate = false;
                        pcf_container.refreshComplete();
                    }else{
                        pcf_container.loadMoreComplete(false);
                    }
                }
                try {
                    adapter.notifyDataSetChanged(response.getJSONObject("data").getJSONObject("now").getString("v"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("网络异常，请检查网络后重试");
                if(isUpdate){
                    isUpdate = false;
                    pcf_container.refreshComplete();
                    pcf_container.loadMoreComplete(false);
                }
                layout_empty.setVisibility(View.GONE);
                pcf_container.setVisibility(View.VISIBLE);
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
            stringAdapter = new StringAdapter(this, list_type, "value");
            listView_type.setAdapter(stringAdapter);
            if (list_type.size() >= 7) {
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
                        moneyDetail_tvType.setText(list_type.get(position).getString("value"));
                        money_type = list_type.get(position).getString("key");
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
                DisplayUtil.backgroundAlpha(1.0f, MoneyDetailActivity.this);
            }
        });
        popWindow.showAtLocation(layout_parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        DisplayUtil.backgroundAlpha(0.3f, MoneyDetailActivity.this);

    }

    /**
     * 四舍五入，两位小数
     */
    private String setMoney2(Double money){
        String s = new java.text.DecimalFormat("#.00").format(money);
        if(s.contains(".00")){
           s = s.substring(0,s.length()-3);
        }
      return s;
}

}
