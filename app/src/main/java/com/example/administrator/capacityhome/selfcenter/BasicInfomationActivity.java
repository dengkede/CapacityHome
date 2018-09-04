package com.example.administrator.capacityhome.selfcenter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.capacityhome.BaseTakePhotoActivity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.PopCityChooseAdapter;
import http.RequestTag;
import model.CityBean;
import utils.ACache;
import utils.ChoosePop;
import utils.CityList;
import utils.CustomDatePicker;
import utils.DisplayUtil;
import utils.FormatUtil;
import utils.GetAgrement;
import utils.ImageBase64;
import utils.Md5;
import utils.ModifyPopWindow;
import utils.RegisterPopupPicker;
import utils.SPUtils;
import utils.StringUtils;
import utils.TakePhotoHelper;
import utils.TimeUtilsDate;
import utils.Timer;
import utils.serchcity.MatchUtils;
import utils.serchcity.PinYinUtil;
import widget.CircleTransform;

/**
 * 基本信息
 */
public class BasicInfomationActivity extends BaseTakePhotoActivity implements View.OnClickListener {
    private PopupWindow popWindow_city;
    private LinearLayout layout_parent;
    private ImageView basic_imgUser;
    private RelativeLayout relayout_chooseCity;
    private TextView basicInfomation_tv_birthday;//显示生日
    private RegisterPopupPicker popupPicker_photo;//头像选择
    private TextView basicInfomation_tv_address;//地区
    private RelativeLayout relayout_close;
    private TakePhotoHelper takePhotoHelper;
    private PopCityChooseAdapter cityAdapter;
    private PopupWindow popupWindow_modify;
    private PopupWindow popupWindow_phone;//绑定手机
    private TextView tv_sure;
    private TextView tv_cancel;
    private LinearLayout layout_phone;//手机号旧
    private TextView tv_oldPhone;//原手机号
    private EditText et_phoneNumebr;
    private TextView tv_getCode;
    private ModifyPopWindow.Click click;
    private TextView[] modifyTv;
    private TextView basicInfomation_tv_state;
    private ACache aCache;
    private int[] tvId = new int[]{R.id.basicInfomation_tv_nickName, R.id.basicInfomation_tv_userName, R.id.basicInfomation_tv_phoneNumber,
            R.id.basicInfomation_tv_email, R.id.basicInfomation_tv_QQnumber, R.id.basicInfomation_tv_numberWX};//修改控件id
    private String type;//修改的类型
    private String[] type_modify = new String[]{"nickName", "name", "mobile", "email", "qq", "weixin"};
    private int[] ids = new int[]{R.id.relayout_chooseCity, R.id.relayoutchooseBirthday, R.id.relayout_chooseImage, R.id.relayout_modifyNickName,
            R.id.relayout_modifyName, R.id.relayout_modifyPhoneNumber, R.id.relayout_modifyEmail, R.id.relayout_modifyQQ, R.id.relayout_modifyWX, R.id.relayout_approve};//控件id

    /**
     * 城市搜索相关
     */
    private ArrayList<CityBean> mBodyDatas = new ArrayList<>();//城市列表
    private EditText popWindow_et_serch;
    private GridView gridView;
    private ArrayList<CityBean> searchResult = new ArrayList<>();//搜索结果
    //结束

    /**
     * 时间选择相关
     *
     * @param savedInstanceState
     */
    private CustomDatePicker customDatePicker;//时间选择
    private Handler handler;
    private TextView tv_title;
    private LinearLayout layout_privence;//选择的省
    private LinearLayout layout_city;//选择的市
    private LinearLayout layout_country;//选择的区
    private TextView tv_privence;
    private TextView tv_city;
    private TextView tv_country;
    private String privence_id;
    private String city_id;
    private String country_id;
    private TextView tv_citySure;
    private EditText et_code;
    private GetAgrement getAgrement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_infomation);
        initView();
        MyApplication.isupdate = true;
        getUserInfo();

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.obj != null) {
                    postUserImage((String) msg.obj);
                }
                return false;
            }
        });
        // postUserNickName("");
        //postUserImage("");
    }

    private void initView() {
        backActivity();
        basicInfomation_tv_state = (TextView) findViewById(R.id.basicInfomation_tv_state);
        basic_imgUser = (ImageView) findViewById(R.id.basic_imgUser);
        basicInfomation_tv_address = (TextView) findViewById(R.id.basicInfomation_tv_address);
        takePhotoHelper = new TakePhotoHelper(null);
        basicInfomation_tv_birthday = (TextView) findViewById(R.id.basicInfomation_tv_birthday);
        submitBtnVisible("帮助", this);
        for (int i = 0; i < ids.length; i++) {
            findViewById(ids[i]).setOnClickListener(this);
        }
        click = new ModifyPopWindow.Click() {
            @Override
            public void click(View view, String content) {
                if (view.getId() == R.id.tv_sure) {
                    //提交
                    for (int i = 0; i < type_modify.length; i++) {
                        if (type.equals(type_modify[i])) {
                            if (type.equals("nickName")) {
                                postUserNickName2(modifyTv[i], content);
                            } else {
                                postUserQQ_WX_EMAIL(modifyTv[i], content, type, "");
                            }
                            break;
                        }
                    }
                    popupWindow_modify.dismiss();
                } else {
                    //取消 popupWindow_modify
                    popupWindow_modify.dismiss();
                }
            }
        };
        modifyTv = new TextView[tvId.length];
        for (int i = 0; i < tvId.length; i++) {
            modifyTv[i] = (TextView) findViewById(tvId[i]);
        }
        try {
            String pic = MyApplication.getUserJson().getString("head");
            if (pic.contains("http://") || pic.contains("https://")) {
                pic = MyApplication.getUserJson().getString("head");
            } else {
                pic = RequestTag.BaseImageUrl + MyApplication.getUserJson().getString("head");
            }
            Picasso.with(this).load(pic).placeholder(R.mipmap.default_iv).error(R.mipmap.default_iv).transform(new CircleTransform()).into(basic_imgUser, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    try {
                        Picasso.with(BasicInfomationActivity.this).load(RequestTag.BaseImageUrl + MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("head").get(MyApplication.getUserJson().getInt("sex"))).placeholder(R.mipmap.default_iv).error(R.mipmap.default_iv).transform(new CircleTransform()).into(basic_imgUser);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {

        }
    }

    private void showPopwindow() {
        if (popWindow_city == null) {
            final int[] count = {1};
            layout_parent = (LinearLayout) findViewById(R.id.layout_parent);
            View popView = View.inflate(this, R.layout.popwindow_city, null);
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.6);
            gridView = (GridView) popView.findViewById(R.id.pop_chooseCity_grid);
            tv_citySure = (TextView) popView.findViewById(R.id.tv_citySure);
            popWindow_et_serch = (EditText) popView.findViewById(R.id.popWindow_et_serch);
            relayout_close = (RelativeLayout) popView.findViewById(R.id.relayout_close);
            layout_privence = (LinearLayout) popView.findViewById(R.id.layout_privence);
            layout_city = (LinearLayout) popView.findViewById(R.id.layout_city);
            layout_country = (LinearLayout) popView.findViewById(R.id.layout_country);
            tv_privence = (TextView) popView.findViewById(R.id.tv_privence);
            tv_city = (TextView) popView.findViewById(R.id.tv_city);
            tv_country = (TextView) popView.findViewById(R.id.tv_country);
            tv_title = (TextView) popView.findViewById(R.id.tv_title);
            popWindow_city = new PopupWindow(popView, width, height);
            popWindow_city.setAnimationStyle(R.style.popwin_anim_style);
            popWindow_city.setFocusable(true);
            popWindow_city.setOutsideTouchable(false);// 设置同意在外点击消失
            ColorDrawable dw = new ColorDrawable(0x30000000);
            popWindow_city.setBackgroundDrawable(dw);
            initListener();
            cityAdapter = new PopCityChooseAdapter(searchResult, this);
            gridView.setAdapter(cityAdapter);
            relayout_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popWindow_city.dismiss();
                }
            });
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.layout_privence:
                            //点击省名
                            initData("province");
                            layout_city.setVisibility(View.GONE);
                            layout_country.setVisibility(View.GONE);
                            tv_privence.setText("请选择省");
                            privence_id = null;
                            city_id = null;
                            country_id = null;
                            count[0] = 1;
                            break;
                        case R.id.layout_city:
                            //点击市名
                            layout_country.setVisibility(View.GONE);
                            initData(privence_id);
                            count[0] = 2;
                            city_id = null;
                            country_id = null;
                            //initData(city_id);
                            break;
                        case R.id.tv_country:
                            //点击区名
                            break;
                        case R.id.tv_citySure:
                            //提交选择的城市
                            if (!StringUtils.isEmpty(country_id)) {
                                postUserQQ_WX_EMAIL(basicInfomation_tv_address, country_id, "area_id", tv_privence.getText().toString() + tv_city.getText().toString() + tv_country.getText().toString());
                                popWindow_city.dismiss();
                            } else {
                                if (StringUtils.isEmpty(privence_id) || StringUtils.isEmpty(city_id)) {
                                    toastMessage("请选择所在省市");
                                } else {
                                    toastMessage("请选择所在区");
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
                    view.startAnimation(new AnimationUtils().loadAnimation(BasicInfomationActivity.this, R.anim.bg_alpha));
//                builder.append(searchResult.get(position).getTitle());
                    if (count[0] == 1) {
                        tv_privence.setText(searchResult.get(position).getTitle());
                        privence_id = searchResult.get(position).getId();
                        popWindow_et_serch.setHint("请输入市汉语拼音首字母");
                        //tv_title.setText(searchResult.get(position).getTitle());
                    } else if (count[0] == 2) {
                        if (layout_city.getVisibility() == View.GONE) {
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
                        if (layout_country.getVisibility() == View.GONE) {
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


        popWindow_city.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                DisplayUtil.backgroundAlpha(1.0f, BasicInfomationActivity.this);
            }
        });
        DisplayUtil.backgroundAlpha(0.2f, BasicInfomationActivity.this);
        popWindow_city.showAtLocation(layout_parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relayout_chooseCity:
                //所在地区
                showPopwindow();
                break;
            case R.id.relayoutchooseBirthday:
                TimeUtilsDate.initDatePicker(this, customDatePicker, basicInfomation_tv_birthday, new TimeUtilsDate.TimeLinsener() {
                    @Override
                    public void getTime(String s) {
                        showLoadingView();
                        postUserBirthday(basicInfomation_tv_birthday, s);
                    }
                });

                break;
            case R.id.relayout_chooseImage:
                //选择头像
                ChoosePop.showPop(this, "photo", "上传头像", v, popupPicker_photo, listener_photo);
                break;
            case R.id.relayout_modifyName:
                //修改姓名
                toastMessage("真实姓名认证后，不能修改");
                break;
            case R.id.relayout_modifyNickName:
//            case R.id.relayout_modifyName:
            case R.id.relayout_modifyPhoneNumber:
            case R.id.relayout_modifyEmail:
            case R.id.relayout_modifyQQ:
            case R.id.relayout_modifyWX:
                String title = null;
                String content = null;
                switch (v.getId()) {
                    case R.id.relayout_modifyNickName:
                        type = type_modify[0];
                        content = modifyTv[0].getText().toString();
                        title = "修改昵称";
                        break;
                    case R.id.relayout_modifyName:
                        type = type_modify[1];
                        content = modifyTv[1].getText().toString();
                        title = "修改姓名";
                        break;
                    case R.id.relayout_modifyPhoneNumber:
                        type = type_modify[2];
                        content = modifyTv[2].getText().toString();
                        title = "修改电话号码";
                        break;
                    case R.id.relayout_modifyEmail:
                        content = modifyTv[3].getText().toString();
                        type = type_modify[3];
                        title = "修改邮箱";
                        break;
                    case R.id.relayout_modifyQQ:
                        type = type_modify[4];
                        title = "修改QQ号";
                        content = modifyTv[4].getText().toString();
                        break;
                    case R.id.relayout_modifyWX:
                        type = type_modify[5];
                        title = "修改微信号";
                        content = modifyTv[5].getText().toString();
                        break;
                }
                try {
                    if (v.getId() == R.id.relayout_modifyPhoneNumber && StringUtils.isEmpty(MyApplication.getUserJson().getString("mobile"))) {
                        showPopwindow_bindPhone("绑定手机号码", "");
                    } else if (v.getId() == R.id.relayout_modifyPhoneNumber) {
                        showPopwindow_bindPhone("修改手机号码", MyApplication.getUserJson().getString("mobile"));
                    } else {
                        View view = LayoutInflater.from(this).inflate(R.layout.layout_modify_pop, null);
                        if (layout_parent == null) {
                            layout_parent = (LinearLayout) findViewById(R.id.layout_parent);
                        }
                        if (content.contains("请")) {
                            popupWindow_modify = ModifyPopWindow.showPopwindow(layout_parent, BasicInfomationActivity.this, popupWindow_modify, view, title, click, content, true);
                        } else {
                            popupWindow_modify = ModifyPopWindow.showPopwindow(layout_parent, BasicInfomationActivity.this, popupWindow_modify, view, title, click, content, false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.relayout_approve:
                try {
                    Intent intent = new Intent(this, ApproveActivity.class);
                    startActivityForResult(intent, 2);
//                    if (!MyApplication.getUserJson().getString("id_state").equals("0")) {
//                        startActivity(this, ApproveActivity.class);
//                    } else {
//                        startActivity(this, Approve_completeActivity.class);
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.top_submit_btn:
                //帮助
                if (getAgrement == null) {
                    getAgrement = new GetAgrement(this);
                }
                getAgrement.getAggrement_Detail(null, "MemberHelp", "我知道了");
                break;
        }
    }

    /**
     * 认证返回
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 3) {
            if (data != null) {
                try {
                    basicInfomation_tv_state.setText((String) MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("member_state").get(data.getIntExtra("approve_status", 0)));
                    basicInfomation_tv_state.setTextColor(Color.parseColor((String) MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("member_state_color").get(data.getIntExtra("approve_status", 0))));
                } catch (Exception e) {

                }

            }
        }
    }

    private RegisterPopupPicker.OnPopupItemSelectedListener listener_photo = new RegisterPopupPicker.OnPopupItemSelectedListener() {
        @Override
        public void onItemSelected(View locationView, int position, List<String> item) {
            switch (position) {
                case 0:
                    takePhotoHelper.PickPhotoFromCapture(getTakePhoto(), true);
                    break;
                case 1:
                    takePhotoHelper.PickPhotoFromGallery(getTakePhoto(), true);
                    break;
            }
        }
    };

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
            CityList cityList = new CityList(BasicInfomationActivity.this, result);
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

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        TImage image = result.getImage();
        final String path = image.getCompressPath();
//        String fileName = file.getName();
//        //得到图片格式
//        String typeText = fileName.substring(fileName.lastIndexOf("."),fileName.length());
        //postUserImage(path);
        Message message = new Message();
        message.obj = path;
        handler.sendMessage(message);
//        runOnUiThread(new Runnable() {
//            File file = new File(path);
//            postUserImage(path);
//            @Override
//            public void run() {
//                Picasso.with(BasicInfomationActivity.this).load(file).error(R.mipmap.add_default).transform(new CircleTransform()).into(new Target() {
//                    @Override
//                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
////                        basic_imgUser.setImageBitmap(bitmap);
//                        postUserImage(bitmap);
//                    }
//
//                    @Override
//                    public void onBitmapFailed(Drawable errorDrawable) {
//                        dismissLoadingView();
//                    }
//
//                    @Override
//                    public void onPrepareLoad(Drawable placeHolderDrawable) {
//                        showLoadingView();
//                    }
//                });
//
//            }
//        });

      /*  RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file",file.getName() , requestFile);*/


    }


    @Override
    public void takeCancel() {
        super.takeCancel();
        //       toastMessage("您已取消");
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
        //     toastMessage("发生错误");
    }

    /**
     * 获取用户基本信息
     */
    private void getUserInfo() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid", MyApplication.getUid());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.USERINFO, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    Log.d("ingogog", response.toString());
                    if (response.getInt("status") != 0) {
                        toastMessage(response.getString("msg"));
                    } else {
                        SPUtils.setSharedStringData(MyApplication.getAppContext(), "user_json", response.getJSONObject("data").toString());
                        MyApplication.user_json = response.getJSONObject("data");
                        modifyTv[0].setText(MyApplication.getUserJson().getString("nickname"));
                        modifyTv[1].setText(MyApplication.getUserJson().getString("name"));

                        if (!StringUtils.isEmpty(MyApplication.getUserJson().getString("mobile"))) {
                            modifyTv[2].setText(MyApplication.getUserJson().getString("mobile"));
                        } else {
                            modifyTv[2].setText("未设置");
                        }
                        if (!StringUtils.isEmpty(MyApplication.getUserJson().getString("email")) & !MyApplication.getUserJson().getString("email").equals("0")) {
                            modifyTv[3].setText(MyApplication.getUserJson().getString("email"));
                        } else {
                            modifyTv[3].setText("请填写你常用的邮箱");
                        }
                        if (!StringUtils.isEmpty(MyApplication.getUserJson().getString("qq")) & !MyApplication.getUserJson().getString("qq").equals("0")) {
                            modifyTv[4].setText(MyApplication.getUserJson().getString("qq"));
                        }
                        if (!StringUtils.isEmpty(MyApplication.getUserJson().getString("weixin")) & !MyApplication.getUserJson().getString("weixin").equals("0")) {
                            modifyTv[5].setText(MyApplication.getUserJson().getString("weixin"));
                        }
                        if (!StringUtils.isEmpty(MyApplication.getUserJson().getString("birthday")) && MyApplication.getUserJson().getLong("birthday") != 0) {
                            basicInfomation_tv_birthday.setText(TimeUtilsDate.timedate(MyApplication.getUserJson().getLong("birthday")));
                        }
                        if (!StringUtils.isEmpty(MyApplication.getUserJson().getString("area_id"))) {
                            if (aCache == null) {
                                aCache = ACache.get(MyApplication.getAppContext());
                                if (!StringUtils.isEmpty(aCache.getAsString("area_id"))) {
                                    basicInfomation_tv_address.setText(aCache.getAsString("area_id"));
                                } else {
                                    CityList.ResultAddress address = new CityList.ResultAddress() {
                                        @Override
                                        public void onSuccess(String content) {
                                            basicInfomation_tv_address.setText(content);
                                        }

                                        @Override
                                        public void onFailure() {

                                        }
                                    };
                                    CityList cityList = new CityList(BasicInfomationActivity.this, address);
                                    cityList.getCityList_detail(MyApplication.getUserJson().getString("area_id"));
                                }
                            }
                        }
                    }
                    if (!StringUtils.isEmpty(response.getJSONObject("data").getString("id_state"))) {
                        basicInfomation_tv_state.setText((String) MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("member_state").get(response.getJSONObject("data").getInt("id_state")));
                        basicInfomation_tv_state.setTextColor(Color.parseColor((String) MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("member_state_color").get(response.getJSONObject("data").getInt("id_state"))));
                    }
                } catch (Exception e) {
                    toastMessage("数据解析异常，请稍后重试");
                    Log.d("info", e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {

            }
        });
    }


    /**
     * 修改用户昵称
     */

    private void postUserNickName2(final TextView tv, final String nickName) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid", MyApplication.getUid());
            jsonObject.put("nickname", nickName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.MODIFY_USER_NICKNAME, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    toastMessage(response.getString("msg"));
                    if (response.getInt("status") != 0) {
                        // toastMessage(response.getString("msg"));
                    } else {
                        tv.setText(nickName);
                        MyApplication.getUserJson().put("nickname", response.getString("nickname"));
                    }
                } catch (Exception e) {
                    toastMessage("数据解析异常，请稍后重试");
                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage(error_msg);
            }
        });
    }


    private void postUserImage(final String path) {
        showLoadingView();
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid", MyApplication.getUid());
            jsonObject.put("img", ImageBase64.imageToBase64(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(BasicInfomationActivity.this, RequestTag.BaseUrl + RequestTag.MODIFY_USEIMAGE, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    dismissLoadingView();
                    toastMessage(response.getString("msg"));
                    if (response.getInt("status") != 0) {
                        //toastMessage(response.getString("msg"));
                    } else {
//                        tv.setText(nickname);
                        Picasso.with(BasicInfomationActivity.this).load(new File(path)).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).transform(new CircleTransform()).into(basic_imgUser, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                try {
                                    Picasso.with(BasicInfomationActivity.this).load(RequestTag.BaseImageUrl + MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("head").get(MyApplication.getUserJson().getInt("sex"))).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).transform(new CircleTransform()).into(basic_imgUser);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        getUserInfo();
                    }
                } catch (Exception e) {
                    toastMessage("数据解析异常，请稍后重试");
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage(error_msg);
                dismissLoadingView();
            }
        });
    }

    /**
     * 修改用户qq,邮箱，微信
     */
    private void postUserQQ_WX_EMAIL(final TextView tv, final String content, final String key, final String content2) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid", MyApplication.getUid());
            jsonObject.put("type", key);
            jsonObject.put("value", content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.MODIFY_QQ_WX_EMAIL, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    toastMessage(response.getString("msg"));
                    if (response.getInt("status") != 0) {
                        //  toastMessage(response.getString("msg"));
                    } else {
                        MyApplication.getUserJson().put(key, content);
                        if (key.equals("area_id")) {
                            aCache.put("area_id", content2);
                            tv.setText(content2);
                        } else {
                            tv.setText(content);
                        }
                    }
                } catch (Exception e) {
                    toastMessage("数据解析异常，请稍后重试");
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {

                toastMessage(error_msg);
            }
        });
    }


    /**
     * 修改生日
     */
    private void postUserBirthday(final TextView tv, final String content) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid", MyApplication.getUid());
            jsonObject.put("birthday", TimeUtilsDate.dataOne(content));
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.MODIFY_BIRTHDAY, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    toastMessage(response.getString("msg"));
                    if (response.getInt("status") != 0) {
                        // toastMessage(response.getString("msg"));
                    } else {
                        MyApplication.getUserJson().put("birthday", TimeUtilsDate.dataOne(content));
                        basicInfomation_tv_birthday.setText(content);
                    }
                } catch (Exception e) {
                    toastMessage("数据解析异常，请稍后重试");
                }
                dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dismissLoadingView();
                toastMessage(error_msg);
            }
        });
    }

    /**
     * 修改手机
     */
    private void modify_moile(final TextView tv, final String code) {
        showLoadingView();
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid", MyApplication.getUid());
            jsonObject.put("mobile", et_phoneNumebr.getText().toString());
            jsonObject.put("code", code);//验证码（如果old为空，则是新手机的验证码，否则为旧手机验证码）
            if (!StringUtils.isEmpty(MyApplication.getUserJson().getString("mobile"))) {
                jsonObject.put("old", MyApplication.getUserJson().getString("mobile"));//原手机号码(没有请传空，不能不传)
            } else {
                jsonObject.put("old", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.MODIFY_MOBILE, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    toastMessage(response.getString("msg"));
                    if (response.getInt("status") != 0) {
                        // toastMessage(response.getString("msg"));
                    } else {
                        MyApplication.getUserJson().put("mobile", et_phoneNumebr.getText().toString());
                        tv.setText(et_phoneNumebr.getText().toString());
                    }
                } catch (Exception e) {
                    toastMessage("数据解析异常，请稍后重试");
                }
                dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dismissLoadingView();
                toastMessage(error_msg);
            }
        });
    }


    private void showPopwindow_bindPhone(String title, String phone) {
        if (popupWindow_phone == null) {
            layout_parent = (LinearLayout) findViewById(R.id.layout_parent);
            int width = getResources().getDisplayMetrics().widthPixels / 10 * 8;
//            int height = getResources().getDisplayMetrics().widthPixels / 2.2;
            View popView = LayoutInflater.from(this).inflate(R.layout.bind_phone_pop, null);
            popupWindow_phone = new PopupWindow(popView, width, LinearLayout.LayoutParams.WRAP_CONTENT);
            tv_oldPhone = (TextView) popView.findViewById(R.id.tv_oldPhone);
            layout_phone = (LinearLayout) popView.findViewById(R.id.layout_phone);
            tv_title = (TextView) popView.findViewById(R.id.tv_title);
            et_code = (EditText) popView.findViewById(R.id.et_code);
            tv_title.setText(title);
            tv_cancel = (TextView) popView.findViewById(R.id.tv_cancel);
            tv_sure = (TextView) popView.findViewById(R.id.tv_sure);
            et_phoneNumebr = (EditText) popView.findViewById(R.id.et_phoneNumebr);
            tv_getCode = (TextView) popView.findViewById(R.id.tv_getCode);
            popupWindow_phone.setAnimationStyle(R.style.popwin_anim_style);
            popupWindow_phone.setFocusable(true);
            popupWindow_phone.setOutsideTouchable(false);// 设置同意在外点击消失
            ColorDrawable dw = new ColorDrawable(0x30000000);
            popupWindow_phone.setBackgroundDrawable(dw);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.tv_cancel:
                            popupWindow_phone.dismiss();
                            break;
                        case R.id.tv_sure:
                            popupWindow_phone.dismiss();
                            if (et_code.getText().toString().isEmpty()) {
                                toastMessage("请输入验证码");
                            } else {
                                modify_moile(modifyTv[2], et_code.getText().toString());
                            }
                            break;
                        case R.id.tv_getCode:
                            //获取短信验证码
                            if (FormatUtil.isMobileNO(et_phoneNumebr.getText().toString())) {
                                Timer.timeCount(tv_getCode);
                                getNoteCode(et_phoneNumebr.getText().toString());
                            } else {
                                toastMessage("请输入正确的手机格式");
                            }
                            break;
                    }
                }
            };
            popupWindow_phone.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    DisplayUtil.backgroundAlpha(1.0f, BasicInfomationActivity.this);
                }
            });
            tv_sure.setOnClickListener(onClickListener);
            tv_cancel.setOnClickListener(onClickListener);
            tv_getCode.setOnClickListener(onClickListener);
        }
        if (!StringUtils.isEmpty(phone)) {
            layout_phone.setVisibility(View.VISIBLE);
            tv_oldPhone.setText(phone);
            TextPaint tp = tv_title.getPaint();
            tp.setFakeBoldText(true);
        } else {
            layout_phone.setVisibility(View.GONE);
            TextPaint tp = tv_title.getPaint();
            tp.setFakeBoldText(false);
        }
        DisplayUtil.backgroundAlpha(0.3f, this);
        popupWindow_phone.showAtLocation(layout_parent, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 获取短信验证码
     */
    private void getNoteCode(String number) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("mobile", number);
            jsonObject.put("code_type", "edit_mobile");
            jsonObject.put("uid", MyApplication.getUid());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.GETNOTECODE, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {

                try {
                    toastMessage(response.getString("msg"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {

            }
        });
    }
}
