package com.example.administrator.capacityhome.feedback;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.PtrHandler;
import com.chanven.lib.cptr.header.MaterialHeader;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.example.administrator.capacityhome.BaseTakePhotoActivity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.FeedBackAdapter;
import adapter.OrderDoubtAdapter;
import adapter.StringAdapter;
import http.RequestTag;
import utils.DisplayUtil;
import utils.ImageBase64;
import utils.ImagePost;
import utils.KeyBordUtil;
import utils.Md5;
import utils.StringUtils;
import utils.TakePhotoHelper;
import widget.DefaultLoadMoreViewFooter_normal;

/**
 * 问题反馈
 */
public class FeedBackActivity extends BaseTakePhotoActivity implements AdapterView.OnItemClickListener, OnLoadMoreListener, PtrHandler, View.OnClickListener {
    ListView feedback_listview;
    private FeedBackAdapter adapter;
    private List<JSONObject> list;
    private PtrClassicFrameLayout pcf_container;
    private View viewParentHead;
    private TakePhotoHelper takePhotoHelper;
    private List<String> list_pic = new ArrayList<>();
    private ArrayList<String> list_choose = new ArrayList<>();
    private boolean isUpdate = true;
    private int page = 1;
    private String keyword = "";
    private String question_type;
    private ImagePost imagePost;
    private GridView gridView;
    private OrderDoubtAdapter adapter_photo;
    private EditText doubtPop_etContent;
    private TextView tv_submit;
    private PopupWindow popWindow;
    private PopupWindow popupWindow_questionType;
    private ListView listView_type;
    private TextView tv_popTitle;
    private StringAdapter stringAdapter;
    private RelativeLayout layout_parent;
    private View view_zhezhao;
    private RelativeLayout layout_pop_parent;
    private TextView tv_note;
    private EditText feedback_tv_searchKey;
    private TextView top_submit_btn2;
    private List<JSONObject> list_type = new ArrayList<>();//问题类型

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        initView();
        initFrameLayout();
    }

    private void initView() {
        backActivity();
        setPageTitle("发表意见");
        tv_submit = (TextView) findViewById(R.id.top_submit_btn);
        tv_submit.setText("发表");
        tv_submit.setOnClickListener(this);
        feedback_listview = (ListView) findViewById(R.id.feedback_listview);
        layout_parent = (RelativeLayout) findViewById(R.id.layout_parent);
        view_zhezhao = findViewById(R.id.view_zhezhao);
        list = new ArrayList<>();
        adapter = new FeedBackAdapter(list, this);
        viewParentHead = LayoutInflater.from(this).inflate(R.layout.layout_feedback_head, null);
        feedback_tv_searchKey = (EditText) viewParentHead.findViewById(R.id.feedback_tv_searchKey);
        viewParentHead.findViewById(R.id.feedback_img_serch).setOnClickListener(this);
        feedback_listview.addHeaderView(viewParentHead, null, true);
        //禁止头部出现分割线
        feedback_listview.setHeaderDividersEnabled(false);
        feedback_listview.setAdapter(adapter);
        feedback_listview.setOnItemClickListener(this);
        list_pic.add(null);
        takePhotoHelper = new TakePhotoHelper(null);
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
     * 选择type
     */
    private void initType(){
        if(list_type.size()==0&&MyApplication.getConfigrationJson()!=null){
            try {
                JSONArray jsonArray = MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("message_type");
                for (int i = 0;i<jsonArray.length();i++){
                    JSONObject jsonObject2 = new JSONObject();
                    jsonObject2.put("key",""+i);
                    jsonObject2.put("value",jsonArray.getString(i));
                    list_type.add(jsonObject2);
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
                bundle.putString("id", list.get(position - 1).getString("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity_Bundle(this, FeedBackDetailActivity.class, bundle);
        }
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(pcf_container, feedback_listview, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        isUpdate = true;
        page = 1;
        getFeedList();
    }

    @Override
    public void loadMore() {
        isUpdate = false;
        page = page + 1;
        getFeedList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(list_type.size()==0) {
            initType();
        }
    }

    /**
     * 获取订单列表
     */
    private void getFeedList() {
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
            if (!StringUtils.isEmpty(feedback_tv_searchKey.getText().toString())) {
                jsonObject.put("keyword", feedback_tv_searchKey.getText().toString());//非必传
            }
//            if (!StringUtils.isEmpty(type)) {
//                jsonObject.put("type", type);//非必传
//            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.FEEDBACK_LIST, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if (isUpdate) {
                        pcf_container.refreshComplete();
                    }
                    if (response.getInt("status") != 0) {
                        pcf_container.loadMoreComplete(false);
                        list.clear();
                        adapter.notifyDataSetChanged();
                    } else {
                        if (response.getJSONArray("data") == null || response.getJSONArray("data").length() == 0) {
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
                            adapter.setDateType(response.getJSONArray("type"), response.getJSONArray("show"));
                            if (response.getJSONArray("data").length() < MyApplication.getPageSize()) {
                                //已加载完毕
                                pcf_container.loadMoreComplete(false);
                            } else {
                                pcf_container.loadMoreComplete(true);
                            }

                        }
                        adapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    pcf_container.refreshComplete();
                    pcf_container.loadMoreComplete(false);

                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                pcf_container.refreshComplete();
                pcf_container.loadMoreComplete(false);
            }
        });
    }

    private void showPopwindow() {
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = (int) (getResources().getDisplayMetrics().heightPixels / 2);
        //int height = (int) (getResources().getDisplayMetrics().heightPixels-getResources().getDimension(R.dimen.x152));
        if (popWindow == null) {
            View popView = View.inflate(this, R.layout.layout_order_doubt_pop, null);
            gridView = (GridView) popView.findViewById(R.id.doubtPop_grid);
            layout_pop_parent = (RelativeLayout) popView.findViewById(R.id.layout_pop_parent);
            doubtPop_etContent = (EditText) popView.findViewById(R.id.doubtPop_etContent);
            tv_note = (TextView) popView.findViewById(R.id.tv_note);
            doubtPop_etContent.setHint("请输入意见");
            top_submit_btn2 = (TextView) popView.findViewById(R.id.top_submit_btn);
            popWindow = new PopupWindow(popView, width, height);
            popWindow.setAnimationStyle(R.style.popwin_anim_style);
            popWindow.setFocusable(true);
            popWindow.setOutsideTouchable(false);
            ColorDrawable dw = new ColorDrawable(0x00000000);
            popWindow.setBackgroundDrawable(dw);
            adapter_photo = new OrderDoubtAdapter(list_pic, this, (int) ((width - getResources().getDimension(R.dimen.x92)) / 4));
            gridView.setAdapter(adapter_photo);
            top_submit_btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list_choose.size() <= 0) {
                        postFeedBack(doubtPop_etContent.getText().toString(), null);
                    } else {
                        if (list_choose.size() == 0) {
                            postImage(null, null);
                        } else {
                            if (list_choose.size() == 1) {
                                postImage(ImageBase64.imageToBase64(list_choose.get(0)), null);

                            } else {
                                JSONArray jsonArray = new JSONArray();
                                for (int i = 0;i<list_choose.size();i++){
                                    jsonArray.put(ImageBase64.imageToBase64(list_choose.get(i)));
                                }
                                postImage("", jsonArray);
                            }
                        }

                    }
                }
            });
            // initPopLinsterner();
            try {
                tv_note.setText("如果有照片请上传，单张最大" + MyApplication.getConfigrationJson().getJSONObject("upset").getLong("imgmax") / 1024 + "M"+"，"+"最多8张图");
            }catch (Exception e){

            }
        }
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list_pic.get(position) == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            takePhotoHelper.PickMultiplePhoto(getTakePhoto(), 20, false);
                        }
                    });
                }else{
                    startActivity_ImagrPager(FeedBackActivity.this, position,list_choose,true);
                }
            }
        });
        layout_pop_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
            }
        });
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                view_zhezhao.setVisibility(View.GONE);
            }
        });

        // DisplayUtil.backgroundAlpha(0.2f, OrderDetailActivity.this);
        popWindow.showAtLocation(layout_parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        view_zhezhao.setVisibility(View.VISIBLE);
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        final List<TImage> images = result.getImages();
        //  List<File> list = new ArrayList<>();
        if (list_pic == null) {
            list_pic = new ArrayList<>();
        }
        list_pic.remove(null);
        for (int i = 0; i < images.size(); i++) {
            // list.add(new File(images.get(i).getCompressPath()));
            list_pic.add(images.get(i).getCompressPath());
            list_choose.add(images.get(i).getCompressPath());
        }
        list_pic.add(null);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter_photo.notifyDataSetChanged();
            }
        });


    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_submit_btn:
                //发
                showPopQuestion("请选择问题类型");
                break;
            case R.id.feedback_img_serch:
                pcf_container.autoRefresh();
                break;
        }
    }

    /**
     * 上传图片
     */
    private void postImage(String pics, JSONArray pics2) {
        if (imagePost == null) {
            imagePost = new ImagePost(new ImagePost.Result() {
                @Override
                public void success(JSONObject jsonObject) {
                    try {
                        if (jsonObject.getInt("status") != 0) {
                            toastMessage(jsonObject.getString("msg"));
                        } else {
                            postFeedBack(doubtPop_etContent.getText().toString(), jsonObject.get("url"));
                        }

                    } catch (Exception e) {

                    }

                    dismissLoadingView();
                }

                @Override
                public void failure(String msg) {
                    dismissLoadingView();
                }
            }, this);
        }
        imagePost.psotDoubt(pics, pics2);
        showLoadingView();
    }

    /**
     * 发表
     */
    private void postFeedBack(String content, Object pics) {
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
            jsonObject.put("name", MyApplication.getUserJson().getString("name"));

            jsonObject.put("mobile", MyApplication.getUserJson().getString("mobile"));
            jsonObject.put("title", "问题反馈");
            jsonObject.put("type", question_type);
            if (pics!=null) {
                jsonObject.put("pics", pics);
            }
            jsonObject.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.FEEDBACK_SUBMIT, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if (response.getInt("status") != 0) {
                        toastMessage(response.getString("msg"));
                    } else {
                        toastMessage(response.getString("msg"));
                        pcf_container.autoRefresh();

                    }

                } catch (Exception e) {
                    dismissLoadingView();
                }
                if (popWindow != null && popWindow.isShowing()) {
                    popWindow.dismiss();
                }
                dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dismissLoadingView();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case 2:
                //图片浏览界面删除返回
                if(data.getStringArrayListExtra("list_pic")!=null){
                    if(data.getStringArrayListExtra("list_pic").size() == list_choose.size()){
                        break;
                    }else{
                        list_choose.clear();
                        list_choose.addAll(data.getStringArrayListExtra("list_pic"));
                        list_pic.clear();
                        list_pic.add(null);
                        adapter_photo.notifyDataSetChanged();
                    }

                }
                break;
        }
    }

    /**
     * 选择提问类型
     * @param title
     */
    private void showPopQuestion(String title) {
        KeyBordUtil.hintKeyboard(this);
        if (popupWindow_questionType == null) {
            View popView = View.inflate(this, R.layout.layout_pop_list, null);
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.6);
            tv_popTitle = (TextView) popView.findViewById(R.id.tv_popTitle);
            tv_popTitle.setText(title);
            listView_type = (ListView) popView.findViewById(R.id.layout_pop_list);
            stringAdapter = new StringAdapter(this, list_type, "value");
            listView_type.setAdapter(stringAdapter);
            if (list_type.size() >= 7) {
                popupWindow_questionType = new PopupWindow(popView, width, height);
            } else {
                popupWindow_questionType = new PopupWindow(popView, width, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
            popupWindow_questionType.setAnimationStyle(R.style.popwin_anim_style);
            popupWindow_questionType.setFocusable(true);
            popupWindow_questionType.setOutsideTouchable(false);// 设置同意在外点击消失
            ColorDrawable dw = new ColorDrawable(0x30000000);
            popupWindow_questionType.setBackgroundDrawable(dw);
            listView_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
//                        type = list_bank.get(position).getString("id");
//                        addBank_tv_bankName.setText(list_bank.get(position).getString("title"));
                        //moneyDetail_tvType.setText(list_type.get(position).getString("value"));
                        question_type = position+"";
                        popupWindow_questionType.dismiss();
                        showPopwindow();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        popupWindow_questionType.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                DisplayUtil.backgroundAlpha(1.0f, FeedBackActivity.this);
            }
        });

        DisplayUtil.backgroundAlpha(0.2f, FeedBackActivity.this);
        popupWindow_questionType.showAtLocation(layout_parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }


}
