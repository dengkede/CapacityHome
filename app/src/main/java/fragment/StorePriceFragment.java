package fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.capacityhome.BaseActivity;
import com.example.administrator.capacityhome.Main_NewActivity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.google.gson.JsonObject;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.BrandRuleAdapter2;
import adapter.MyBrandGridAdapter2;
import adapter.PopProspectAdapter;
import http.RequestTag;
import utils.ColumnUtils;
import utils.GetAgrement;
import utils.Md5;
import utils.StringUtils;

/**
 * Created by Administrator on 2018/6/11.
 */

public class StorePriceFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private GridView gridView;
    private GridView grid_price;
    private List<JSONObject> list_price = new ArrayList<>();
    private BrandRuleAdapter2 brandRuleAdapter2;
    private MyBrandGridAdapter2 adapter2;
    private MyBrandGridAdapter2.Click click;
    private ColumnUtils columnUtils;
    private ColumnUtils columnUtils_type;//洗衣分类
    private String class_id = "";//分类id
    private List<JSONObject> list_type = new ArrayList<>();
    private List<JSONObject> list_brand = new ArrayList<>();//总的品牌数据源
    private int brand_index = 0;//默认喜欢品牌的索引
    //    private int[] tvIds = new int[]{R.id.tv_1, R.id.tv_2, R.id.tv_3, R.id.tv_4};
//    private TextView[] textViews = new TextView[tvIds.length];
//    private int[] Id = new int[]{R.id.line_1, R.id.line_2, R.id.line_3, R.id.line_4};
//    private View[] view_lines = new View[Id.length];
    private LinearLayout layout_parent_tv;
    private int index = 0;
    private int currentIndex = 0;
    private PopupWindow popupWindow;
    private TextView remove_prospect;
    private ImageView img_remove;
    private RelativeLayout relayou_close;
    private TextView tv_totalPrice;//合计总价
    private ListView listview_pop;
    private PopProspectAdapter popProspectAdapter;
    private RelativeLayout layout_parent;
    private List<JSONObject> list_prospect = new ArrayList<>();
    private TextView tv_prospectPrice;//预估费用
    private Handler handler;
    private RelativeLayout relayout_lan;
    private View view_zhezhao;
    private GetAgrement getAgrement;
    private TextView tv_count;//选中总数量
    private int toatal_count = 0;//选中总量

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stroreprice_fragment_layout, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {

        if (hidden) {

            //相当于Fragment的onPause
            //((Main_NewActivity)getActivity()).setGoneTop(true);
        } else {

            // 相当于Fragment的onResume
            ((Main_NewActivity) getActivity()).setGoneTop(true);
            try {
                if (!StringUtils.isEmpty(((Main_NewActivity) getActivity()).typeId)) {
                    if (list_type != null && list_type.size() > 0) {
                        for (int i = 0; i < list_type.size(); i++) {
                            if (list_type.get(i).getString("id").equals(((Main_NewActivity) getActivity()).typeId)) {
                                index = i;
                                changeTab();
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {

            }

        }

    }

    private void initView() {
        layout_parent_tv = findViewById(R.id.layout_parent_tv);
        findViewById(R.id.tv_artical).setOnClickListener(this);
        tv_count = findViewById(R.id.tv_count);
        layout_parent = findViewById(R.id.layout_parent);
        view_zhezhao = findViewById(R.id.view_zhezhao);
        relayout_lan = findViewById(R.id.relayout_lan);
        gridView = (GridView) findViewById(R.id.grid);
        tv_prospectPrice = findViewById(R.id.tv_prospectPrice);
        grid_price = findViewById(R.id.grid_price);
        click = new MyBrandGridAdapter2.Click() {
            @Override
            public void onClick(int i) {
                try {
                    if (brand_index != i) {
                        brand_index = i;
                        getBrandList(list_brand.get(i).getString("id"));
                        list_prospect.clear();
                        tv_prospectPrice.setText("0");
                        if (tv_totalPrice != null) {
                            tv_totalPrice.setText("0");
                        }
                        toatal_count = 0;
                        tv_count.setVisibility(View.INVISIBLE);
                        tv_count.setText(toatal_count + "");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        adapter2 = new MyBrandGridAdapter2(getActivity(), list_brand, click);
        brandRuleAdapter2 = new BrandRuleAdapter2(getActivity(), list_price);
//        for (int i = 0; i < tvIds.length; i++) {
//            textViews[i] = (TextView) findViewById(tvIds[i]);
//            textViews[i].setOnClickListener(this);
//        }
//        for (int i = 0; i < Id.length; i++) {
//            view_lines[i] = findViewById(Id[i]);
//        }
        getBrandType();
        gridView.setAdapter(adapter2);
        grid_price.setAdapter(brandRuleAdapter2);
        grid_price.setOnItemClickListener(this);
        // gridView.setOnItemClickListener(this);
        getBrand();
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.obj != null) {
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    for (int i = 0; i < list_price.size(); i++) {
                        try {
                            if (jsonObject.getString("id").equals(list_price.get(i).getString("id"))) {
                                Double price;
                                if (msg.arg2 < 0) {
                                    //减
                                    toatal_count = toatal_count - 1;
                                    price = Double.parseDouble(tv_prospectPrice.getText().toString()) - list_price.get(i).getDouble("money") / 1000 * 1;
                                } else {
                                    //加上
                                    toatal_count = toatal_count + 1;
                                    price = Double.parseDouble(tv_prospectPrice.getText().toString()) + list_price.get(i).getDouble("money") / 1000 * 1;
                                }
                                if (toatal_count > 0) {
                                    tv_count.setVisibility(View.VISIBLE);
                                } else {
                                    tv_count.setVisibility(View.INVISIBLE);
                                }
                                tv_count.setText(toatal_count + "");
                                list_price.get(i).put("count", jsonObject.getInt("count"));
                                tv_prospectPrice.setText((price + ""));
                                tv_totalPrice.setText(tv_prospectPrice.getText().toString());
                                brandRuleAdapter2.notifyDataSetChanged();
                                jsonObject = null;
                                break;
                            }
                        } catch (Exception e) {

                        }
                    }
                }
                return false;
            }
        });
        relayout_lan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (Double.parseDouble(tv_prospectPrice.getText().toString()) > 0) {
//                    showPop();
//                } else {
//                    toastMessage("请选择要洗的衣物类型");
//                }
            }
        });
    }

    /**
     * 获取品牌
     */
    private void getBrand() {
        if (columnUtils == null) {
            columnUtils = new ColumnUtils(getActivity(), new ColumnUtils.Result() {
                @Override
                public void onSuccess(String title, JSONArray jsonArray) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            if (i == 0) {
                                brand_index = 0;
                            }
                            if (jsonArray.getJSONObject(i).getInt("id") == MyApplication.brand) {
                                brand_index = i;
                            }
                            list_brand.add(jsonArray.getJSONObject(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (list_brand.size() < 4) {
                        gridView.setNumColumns(4);
                    }
                    if (list_brand.size() > 0) {
                        try {
                            getBrandList(list_brand.get(0).getString("id"));
                        } catch (Exception e) {
                            Log.d("brand_exception:", e.toString());
                        }
                    }
                    adapter2.notifyDataSetChanged();
                }

                @Override
                public void onFailure(String msg) {
                    toastMessage("网络异常");
                }
            });
        }
        if (list_brand.size() == 0) {
            columnUtils.getColumn("", "Brand");
        }
    }

    /**
     * 获取洗衣分类
     */
    private void getBrandType() {
        if (columnUtils_type == null) {
            columnUtils_type = new ColumnUtils(getActivity(), new ColumnUtils.Result() {
                @Override
                public void onSuccess(String title, JSONArray jsonArray) {
                    layout_parent_tv.removeAllViews();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            list_type.add(jsonArray.getJSONObject(i));

//                            if (i < 4) {
//                                textViews[i].setVisibility(View.VISIBLE);
//                                textViews[i].setText(list_type.get(i).getString("title"));
//                            }
                            addTv(list_type.get(i).getString("title"), i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onFailure(String msg) {
                    toastMessage("网络异常");
                }
            });
        }
        if (list_type.size() == 0) {
            columnUtils_type.getColumn("", "Offline");
        }
    }

    /**
     * 动态添加分类
     */
    private void addTv(String content, final int position) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_store_price_tv_item, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_item);
        View line = view.findViewById(R.id.line);
        if (currentIndex == position) {
            tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_yellow));
            line.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_yellow));
        } else {
            tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_f5));
            line.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
        tv.setText(content);
//        view_lines[index].setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_yellow));
//        view_lines[currentIndex].setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = position;
                if (currentIndex != index) {
                    changeTab();
                }
            }
        });
        //添加控件
        layout_parent_tv.addView(view);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_artical) {
            if (getAgrement == null) {
                getAgrement = new GetAgrement(getActivity());
            }
            getAgrement.getAggrement_detail("Budget", "我知道了");
        } else {
            switch (view.getId()) {
                case R.id.tv_1:
                    index = 0;
                    break;
                case R.id.tv_2:
                    index = 1;
                    break;
                case R.id.tv_3:
                    index = 2;
                    break;
                case R.id.tv_4:
                    index = 3;
                    break;
            }
            try {
                ((Main_NewActivity) getActivity()).typeId = list_type.get(index).getString("id");
            } catch (Exception e) {

            }
            changeTab();
        }
    }

    /**
     * 底部栏颜色转换
     */
    private void changeTab() {
        if (currentIndex != index) {
            try {
                class_id = list_type.get(index).getString("id");
                getBrandList(list_brand.get(brand_index).getString("id"));
            } catch (Exception e) {

            }
            View view = layout_parent_tv.getChildAt(index);
            TextView tv = (TextView) view.findViewById(R.id.tv_item);
            View line = view.findViewById(R.id.line);
            tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_yellow));
            line.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_yellow));
            View view2 = layout_parent_tv.getChildAt(currentIndex);
            TextView tv2 = (TextView) view2.findViewById(R.id.tv_item);
            View line2 = view2.findViewById(R.id.line);
            tv2.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_f5));
            line2.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
            currentIndex = index;
        }
    }

    /**
     * 用于根据品牌读取洗衣商品列表
     */
    private void getBrandList(final String brand) {
        ((BaseActivity) getActivity()).showLoadingView();
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("brand", brand);
            if (StringUtils.isEmpty(class_id)) {
                if (list_type != null & list_type.size() > 0)
                    class_id = list_type.get(0).getString("id");
            }
            jsonObject.put("class_id", class_id);//分类id
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(getActivity(), RequestTag.BaseUrl + RequestTag.getBrandGoodsList, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    Log.d("pinpaiItem", response.toString());
                    if (response.getInt("status") != 0) {
                        list_price.clear();
                    } else {
                        if (response.getJSONArray("data") == null || response.getJSONArray("data").length() == 0) {
                            list_price.clear();
                        } else {
                            list_price.clear();
                            for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                                JSONObject jsonObject1 = response.getJSONArray("data").getJSONObject(i);
                                jsonObject1.put("count", 0);
                                for (int j = 0; j < list_prospect.size(); j++) {
                                    if (list_prospect.get(j).getString("id").equals(response.getJSONArray("data").getJSONObject(i).getString("id"))) {
                                        jsonObject1.put("count", list_prospect.get(j).getInt("count"));
                                        break;
                                    }
                                }
                                list_price.add(jsonObject1);
                                jsonObject1 = null;
                            }

                        }

                    }
                    brandRuleAdapter2.notifyDataSetChanged();
                } catch (Exception e) {
                    //toastMessage(e.toString());

                }
                ((BaseActivity) getActivity()).dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("网络异常");
                ((BaseActivity) getActivity()).dismissLoadingView();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        try {
//            list_price.get(i).put("count", list_price.get(i).getInt("count") + 1);
//            boolean isadd = true;
//            for (int j = 0; j < list_prospect.size(); j++) {
//                if (list_prospect.get(j).getString("id").equals(list_price.get(i).getString("id"))) {
//                    isadd = false;
//                    break;
//                }
//            }
//            if (isadd) {
//                list_prospect.add(list_price.get(i));
//            }
//            toatal_count = toatal_count + 1;
//            if (tv_count.getVisibility() == View.INVISIBLE) {
//                tv_count.setVisibility(View.VISIBLE);
//            }
//            tv_count.setText(toatal_count + "");
//            brandRuleAdapter2.notifyDataSetChanged();
//            tv_prospectPrice.setText((Double.parseDouble(tv_prospectPrice.getText().toString()) + list_price.get(i).getDouble("money") / 1000) + "");
//            tv_totalPrice.setText(tv_prospectPrice.getText().toString());
//        } catch (Exception e) {
//
//        }
    }

    private void showPop() {
        if (popupWindow == null) {
            int width = getResources().getDisplayMetrics().widthPixels / 10 * 8;
            int height = getResources().getDisplayMetrics().heightPixels;
            View popView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_prospect, null);
            popupWindow = new PopupWindow(popView, width, ViewGroup.LayoutParams.MATCH_PARENT);
            relayou_close = (RelativeLayout) popView.findViewById(R.id.relayou_close);
            listview_pop = (ListView) popView.findViewById(R.id.listview_pop);
            remove_prospect = (TextView) popView.findViewById(R.id.remove_prospect);
            img_remove = (ImageView) popView.findViewById(R.id.img_remove);
            tv_totalPrice = (TextView) popView.findViewById(R.id.tv_totalPrice);
            popupWindow.setAnimationStyle(R.style.popwin_anim_style);
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);// 设置同意在外点击消失
            ColorDrawable dw = new ColorDrawable(0x30000000);
            popupWindow.setBackgroundDrawable(dw);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    view_zhezhao.setVisibility(View.GONE);
                }
            });
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.remove_prospect:
                        case R.id.img_remove:
                            //清空
                            list_prospect.clear();
                            tv_prospectPrice.setText("0");
                            tv_totalPrice.setText("0");
                            for (int i = 0; i < list_price.size(); i++) {
                                try {
                                    list_price.get(i).put("count", 0);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            adapter2.notifyDataSetChanged();
                            toatal_count = 0;
                            tv_count.setVisibility(View.INVISIBLE);
                            tv_count.setText(toatal_count + "");
                            brandRuleAdapter2.notifyDataSetChanged();
                            popupWindow.dismiss();
                            break;
                        case R.id.relayou_close:
                            popupWindow.dismiss();
                            break;
                    }
                }
            };
            remove_prospect.setOnClickListener(onClickListener);
            img_remove.setOnClickListener(onClickListener);
            relayou_close.setOnClickListener(onClickListener);
            popProspectAdapter = new PopProspectAdapter(getActivity(), list_prospect, handler);
            listview_pop.setAdapter(popProspectAdapter);
            tv_totalPrice.setText(tv_prospectPrice.getText().toString());
        } else {
            popProspectAdapter.notifyDataSetChanged();
        }
        view_zhezhao.setVisibility(View.VISIBLE);
        popupWindow.showAtLocation(layout_parent, Gravity.RIGHT, 0, 0);
    }

}
