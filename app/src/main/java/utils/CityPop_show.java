package utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.selfcenter.BasicInfomationActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import adapter.PopCityChooseAdapter;
import model.CityBean;
import utils.serchcity.MatchUtils;
import utils.serchcity.PinYinUtil;

/**
 * Created by Administrator on 2018/4/18.
 * 城市选择弹框公用
 */

public class CityPop_show {
    private Context context;
    private LinearLayout layout_parent;
    private PopupWindow popWindow_city;
    private GridView gridView;
    private EditText popWindow_et_serch;
    private TextView tv_title;
    private PopCityChooseAdapter cityAdapter;
    private Result_areaId result_areaId;
    private ArrayList<CityBean> searchResult = new ArrayList<>();//搜索结果
    private ArrayList<CityBean> mBodyDatas = new ArrayList<>();//城市列表
    private ACache aCache;

    private LinearLayout layout_privence;//选择的省
    private LinearLayout layout_city;//选择的市
    private LinearLayout layout_country;//选择的区
    private TextView tv_privence;
    private TextView tv_city;
    private TextView tv_country;
    private String privence_id;
    private String city_id ;
    private String country_id ;
    private TextView tv_citySure;


    public CityPop_show(Context context, LinearLayout layout_parent, ACache aCache,Result_areaId result_areaId) {
        this.context = context;
        this.layout_parent = layout_parent;
        this.aCache = aCache;
        this.result_areaId = result_areaId;
    }

    public  void showPopwindow() {
        if (popWindow_city == null) {
            final int[] count = {1};
            View popView = View.inflate(context, R.layout.popwindow_city, null);
            int width = context.getResources().getDisplayMetrics().widthPixels;
            int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.6);
            tv_title = (TextView) popView.findViewById(R.id.tv_title);
            gridView = (GridView) popView.findViewById(R.id.pop_chooseCity_grid);
            popWindow_et_serch = (EditText) popView.findViewById(R.id.popWindow_et_serch);
            layout_privence = (LinearLayout) popView.findViewById(R.id.layout_privence);
            layout_city = (LinearLayout) popView.findViewById(R.id.layout_city);
            layout_country = (LinearLayout) popView.findViewById(R.id.layout_country);
            tv_privence = (TextView) popView.findViewById(R.id.tv_privence);
            tv_city = (TextView) popView.findViewById(R.id.tv_city);
            tv_country = (TextView) popView.findViewById(R.id.tv_country);
            tv_citySure = (TextView) popView.findViewById(R.id.tv_citySure);
            popWindow_city = new PopupWindow(popView, width, height);
            popWindow_city.setAnimationStyle(R.style.popwin_anim_style);
            popWindow_city.setFocusable(true);
            popWindow_city.setOutsideTouchable(false);// 设置同意在外点击消失
            ColorDrawable dw = new ColorDrawable(0x30000000);
            popWindow_city.setBackgroundDrawable(dw);
            initListener();
            cityAdapter = new PopCityChooseAdapter(searchResult, context);
            gridView.setAdapter(cityAdapter);
            popView.findViewById(R.id.relayout_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popWindow_city.dismiss();
                }
            });
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.layout_privence:
                            //点击省名
                            initData("province");
                            layout_city.setVisibility(View.GONE);
                            layout_country.setVisibility(View.GONE);
                            tv_privence.setText("请选择省");
                            privence_id = null;
                            city_id = null;
                            country_id = null;
                            count[0] =1;
                            break;
                        case R.id.layout_city:
                            //点击市名
                            layout_country.setVisibility(View.GONE);
                            initData(privence_id);
                            count[0] =2;
                            city_id = null;
                            country_id = null;
                            //initData(city_id);
                            break;
                        case R.id.tv_country:
                            //点击区名
                            break;
                        case R.id.tv_citySure:
                            //提交选择的城市
                            if(!StringUtils.isEmpty(country_id)) {
                                result_areaId.resultSuccess(country_id,tv_privence.getText().toString() + tv_city.getText().toString() + tv_country.getText().toString());
                                popWindow_city.dismiss();
                            }else{
                                if(StringUtils.isEmpty(privence_id)||StringUtils.isEmpty(city_id)){
                                    Toast.makeText(context,"请选择所在省市",Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(context,"请选择所在区",Toast.LENGTH_SHORT).show();
                                }
                            }

                            break;
                    }
                }
            };
            tv_citySure.setOnClickListener(onClickListener);
            layout_privence.setOnClickListener(onClickListener);
            layout_city.setOnClickListener(onClickListener);
            initData("province");
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    view.startAnimation(new AnimationUtils().loadAnimation(context, R.anim.bg_alpha));
                    if(count[0]==1){
                        tv_privence.setText(searchResult.get(position).getTitle());
                        privence_id = searchResult.get(position).getId();
                        popWindow_et_serch.setHint("请输入市汉语拼音首字母");
                        //tv_title.setText(searchResult.get(position).getTitle());
                    }else if(count[0]==2){
                        if(layout_city.getVisibility() == View.GONE){
                            layout_city.setVisibility(View.VISIBLE);
                        }
                        city_id = searchResult.get(position).getId();
                        popWindow_et_serch.setHint("请输入区县汉语拼音首字母");
                        tv_city.setText(searchResult.get(position).getTitle());
                        //  tv_title.setText(tv_title.getText().toString()+"-"+searchResult.get(position).getTitle());
                    }
                    if (count[0] < 3) {
                        //没有选到最后一级
                        initData(searchResult.get(position).getId());
                    } else {
                        country_id = searchResult.get(position).getId();
                        if(layout_country.getVisibility() == View.GONE){
                            layout_country.setVisibility(View.VISIBLE);
                        }
                        tv_country.setText(searchResult.get(position).getTitle());
                        //已经选到第三极
                        //postUserQQ_WX_EMAIL(basicInfomation_tv_address, searchResult.get(position).getId(), "area_id", builder.toString());
                        //popWindow_city.dismiss();
                    }
                    count[0] = count[0] + 1;
                }
            });
        }
//        tv_title.setText("请选择省份");
//        popWindow_et_serch.setHint("请输入省份汉语拼音首字母");
//        initData("province");

        popWindow_city.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                DisplayUtil.backgroundAlpha(1.0f, (Activity) context);
            }
        });
        DisplayUtil.backgroundAlpha(0.2f, (Activity) context);
        popWindow_city.showAtLocation(layout_parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    private void initData(final String key) {
        final Type type = new TypeToken<CityBean>() {
        }.getType();
        final List<CityBean> list = new ArrayList<>();
        // List<CityBean> list = new Gson().fromJson(getAssetsData("address.json"), listType);//解析asset目录下的数据
        if (aCache == null) {
            aCache = ACache.get(MyApplication.getAppContext());
        }
        if (aCache.getAsJSONArray(key) == null) {
            CityList.Result result = new CityList.Result() {
                @Override
                public JSONArray onSuccess(JSONArray jsonArray) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            list.add((CityBean) new Gson().fromJson(aCache.getAsJSONArray(key).getJSONObject(i).toString(), type));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            result_areaId.resultFail("数据解析异常");
                        }
                    }
                    PinYinUtil.getOrderCity(list);//将数据进行排序
                    mBodyDatas.clear();
                    mBodyDatas.addAll(list);
                    initDatas(list);
                    return null;
                }

                @Override
                public void onFailure() {

                }
            };
            CityList cityList = new CityList(context, result);
            cityList.getCityList(key);
        } else {
            for (int i = 0; i < aCache.getAsJSONArray(key).length(); i++) {
                try {
                    list.add((CityBean) new Gson().fromJson(aCache.getAsJSONArray(key).getJSONObject(i).toString(), type));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            PinYinUtil.getOrderCity(list);//将数据进行排序
            mBodyDatas.clear();
            mBodyDatas.addAll(list);
            initDatas(list);
        }

    }


    //搜索数据
    private void filterCity(Editable s) {
        searchResult.clear();
        String inputStr = s.toString();
        MatchUtils.find(inputStr, mBodyDatas, searchResult);
        //因为每次搜索的结果不同，所以匹配类型不同，但是数据源都是同一个数据源，所以每次搜索前，要重置数据
        int size = searchResult.size();
        //  Log.d("size-->", String.valueOf(size));
        cityAdapter.notifyDataSetChanged();
    }

    //进行排序
    private void getContacts() {
        searchResult.clear();
        // PinYinUtil.getOrderCity(mBodyDatas);
        searchResult.addAll(mBodyDatas);
        cityAdapter.notifyDataSetChanged();
    }


    //加载城市列表
    private void initDatas(List<CityBean> data) {
        String first = data.get(0).getPinyinFirst();
        CityBean cityBean = new CityBean();
        cityBean.setPinyinFirst(first);
        cityBean.setTitle(true);
        data.add(0, cityBean);
        for (int i = 0; i < data.size(); i++) {
            if (!TextUtils.equals(data.get(i).getPinyinFirst(), first)) {
                first = data.get(i).getPinyinFirst();
                CityBean bean = new CityBean();
                bean.setPinyinFirst(first);
                bean.setTitle(true);
                data.add(i, bean);
            }
        }

        getContacts();
    }
    protected void initListener() {
        popWindow_et_serch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    filterCity(s);
                } else {
                    //空的时候其实应该返回当前类型全部
                    searchResult.clear();
                    searchResult.addAll(mBodyDatas);
                    cityAdapter.notifyDataSetChanged();
                }
            }
        });
    }
    public interface Result_areaId{
        void resultSuccess(String area_id,String title);
        void resultFail(String msg);
    }

}
