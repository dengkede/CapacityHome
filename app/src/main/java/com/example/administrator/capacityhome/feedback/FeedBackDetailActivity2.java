package com.example.administrator.capacityhome.feedback;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.FeedDetailAdapter2;
import adapter.OrderDoubtAdapter;
import http.RequestTag;
import utils.ImageBase64;
import utils.ImagePost;
import utils.Md5;
import utils.StringUtils;
import utils.TakePhotoHelper;
import widget.DefaultLoadMoreViewFooter_normal;

public class FeedBackDetailActivity2 extends BaseTakePhotoActivity implements View.OnClickListener, OnLoadMoreListener, PtrHandler {
    private ListView feedDetail_listView;
    private List<JSONObject> list;
    private FeedDetailAdapter2 adapter;
    private TakePhotoHelper takePhotoHelper;
    private OrderDoubtAdapter adapter_photo;
    private String order_id = null;//订单id
    private String parent_id = null;//评论回复的父级id
    private TextView tv_submit;
    private PtrClassicFrameLayout pcf_container;
    private boolean isUpdate = true;//判断是否是刷新
    private FeedDetailAdapter2.Reply reply;
    private List<String> list_pic = new ArrayList<>();
    private ArrayList<String> list_choose = new ArrayList<>();
    private ImagePost imagePost;
    private GridView gridView;
    private EditText doubtPop_etContent;
    private TextView top_submit_btn;
    private TextView tv_cancl;
    private PopupWindow popWindow;
    private RelativeLayout layout_parent;
    private View view_zhezhao;
    private RelativeLayout layout_pop_parent;
    private int page = 1;
    private LinearLayout layout_empty;//当前是否有数据
   private String id = null;
    private TextView tv_note;
    private boolean isRepleySelf = false;//是否是恢复自己
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back_detail2);
        initView();
        initFrameLayout();
    }

    private void initView() {
        tv_submit = (TextView) findViewById(R.id.tv_submit);
        layout_parent = (RelativeLayout) findViewById(R.id.layout_parent);
        view_zhezhao = findViewById(R.id.view_zhezhao);
        layout_empty = (LinearLayout) findViewById(R.id.layout_empty);
        feedDetail_listView = (ListView) findViewById(R.id.feedDetail_listView);
        findViewById(R.id.relayout_imgBack).setOnClickListener(this);
        tv_submit.setOnClickListener(this);
        list_pic.add(null);
        list = new ArrayList<>();
        if (!StringUtils.isEmpty(getIntent().getBundleExtra("bundle").getString("json"))) {
            try {
                list.add(new JSONObject(getIntent().getBundleExtra("bundle").getString("json")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

        }
        reply = new FeedDetailAdapter2.Reply() {
            @Override
            public void onclick(View view) {
                isRepleySelf = false;
                showPopwindow();
            }
        };
        adapter = new FeedDetailAdapter2(list, this, reply);
        feedDetail_listView.setAdapter(adapter);
        if (getIntent().getBundleExtra("bundle") != null) {
            if (!StringUtils.isEmpty(getIntent().getBundleExtra("bundle").getString("order_id"))) {
                order_id = getIntent().getBundleExtra("bundle").getString("order_id");
                parent_id = getIntent().getBundleExtra("bundle").getString("parent_id");
                getRepleyList();
            }
        }
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
        //pcf_container.autoRefresh();
    }

    private void showPopwindow() {
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = (int) (getResources().getDisplayMetrics().heightPixels / 2.2);
        //int height = (int) (getResources().getDisplayMetrics().heightPixels-getResources().getDimension(R.dimen.x152));
        if (popWindow == null) {
            View popView = View.inflate(this, R.layout.layout_order_doubt_pop, null);
            gridView = (GridView) popView.findViewById(R.id.doubtPop_grid);
            layout_pop_parent = (RelativeLayout) popView.findViewById(R.id.layout_pop_parent);
            doubtPop_etContent = (EditText) popView.findViewById(R.id.doubtPop_etContent);
            top_submit_btn = (TextView) popView.findViewById(R.id.top_submit_btn);
            top_submit_btn.setOnClickListener(this);
            tv_cancl = (TextView) popView.findViewById(R.id.tv_cancl);
            tv_note = (TextView) popView.findViewById(R.id.tv_note);
            popWindow = new PopupWindow(popView, width, height);
            popWindow.setAnimationStyle(R.style.popwin_anim_style);
            popWindow.setFocusable(true);
            popWindow.setOutsideTouchable(false);
            ColorDrawable dw = new ColorDrawable(0x00000000);
            popWindow.setBackgroundDrawable(dw);
            adapter_photo = new OrderDoubtAdapter(list_pic, this, (int) ((width - getResources().getDimension(R.dimen.x92)) / 4));
            gridView.setAdapter(adapter_photo);
            try {
                tv_note.setText("如果有照片请上传，单张最大" + MyApplication.getConfigrationJson().getJSONObject("upset").getLong("imgmax") / 1024 + "M"+"，"+"最多8张图");
            }catch (Exception e){

            }
            // initPopLinsterner();
        }
        tv_cancl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
            }
        });
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
                    startActivity_ImagrPager(FeedBackDetailActivity2.this, position,list_choose,true);
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
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(pcf_container, feedDetail_listView, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        isUpdate = true;
        page = 1;
        getRepleyList();
    }

    @Override
    public void loadMore() {
        page = page + 1;
        isUpdate = false;
        getRepleyList();

    }


    /**
     * 订单回复详情列表
     */
    private void getRepleyList() {
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
            jsonObject.put("page", page);
            jsonObject.put("pagesize", MyApplication.getPageSize() + "");
            jsonObject.put("order_id", order_id);
            jsonObject.put("parent_id", parent_id);
            // jsonObject.put("id","");//当传入ID时表示取当前这条评价，但不能取子评价
            jsonObject.put("username", MyApplication.getUserJson().getString("m_username"));
            jsonObject.put("pwd", MyApplication.getPasswprd());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.ORDER_REPLY, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if(layout_empty.getVisibility()==View.VISIBLE) {
                        layout_empty.setVisibility(View.GONE);
                    }
                    Log.d("response", response.toString());
                    if (isUpdate) {
                        pcf_container.refreshComplete();
                    }
                    if (response.getInt("status") != 0) {
                        toastMessage(response.getString("msg"));
                        if (!isUpdate) {
                            pcf_container.loadMoreComplete(false);
                        }
                    } else {
                        if (isUpdate) {
                            list.clear();
                            list.add(new JSONObject(getIntent().getBundleExtra("bundle").getString("json")));
                        }
                        if (response.getJSONArray("data") != null) {
                            for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                                list.add(response.getJSONArray("data").getJSONObject(i));
                            }
                            if (list.size()< response.getInt("total")) {
                                pcf_container.loadMoreComplete(true);
                            } else {
                                pcf_container.loadMoreComplete(false);
                            }
                            adapter.notifyDataSetChanged();
                            feedDetail_listView.setSelection(0);
                        }
                        if(list.size()==0){
                            layout_empty.setVisibility(View.VISIBLE);
                        }
                    }
                    if (isUpdate) {
                        isUpdate = false;
                    }

                } catch (Exception e) {
                    pcf_container.loadMoreComplete(false);
                }
                dismissLoadingView();

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                if (isUpdate) {
                    pcf_container.refreshComplete();
                }
                pcf_container.loadMoreComplete(false);
                dismissLoadingView();
            }
        });
    }

    /**
     * 回复
     */
    private void postDoubt(String content, Object pics) {
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
            jsonObject.put("username", MyApplication.getUserJson().getString("m_username"));
            jsonObject.put("pwd", MyApplication.getPasswprd());
            if(!isRepleySelf) {
                jsonObject.put("parent_id", list.get(0).getString("parent_id"));
            }else{
                jsonObject.put("parent_id", list.get(0).getString("id"));
            }
            jsonObject.put("order_id", order_id);
            if (pics!=null) {
                jsonObject.put("pics",pics);
            }
            jsonObject.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.REPLY_DOUBT, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if (response.getInt("status") != 0) {
                        dismissLoadingView();
                        toastMessage(response.getString("msg"));
                    } else {
                            toastMessage(response.getString("msg"));
                            dismissLoadingView();
                            pcf_container.autoRefresh();

                    }

                } catch (Exception e) {
                    try {
                        toastMessage(response.getString("msg"));
                    }catch (Exception e2){

                    }
                    dismissLoadingView();
                }
                if (popWindow != null && popWindow.isShowing()) {
                    popWindow.dismiss();
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dismissLoadingView();
            }
        });
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
                            Log.d("img5",jsonObject.toString());
                            postDoubt(doubtPop_etContent.getText().toString(), jsonObject.get("url"));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_submit_btn:
                //提交回复
                if (doubtPop_etContent.getText().toString().isEmpty()) {
                    toastMessage("请输入内容");
                } else {

                    if (list_choose.size() == 0) {
                        postDoubt(doubtPop_etContent.getText().toString(), "");
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
                break;
            case R.id.relayout_imgBack:
                finish();
                break;
            case R.id.tv_submit:
                //回复点击后同样执行疑问发表界面，用户可对自己的疑问主题补充回复
                isRepleySelf = true;
                showPopwindow();
                break;
        }
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
                        list_pic.addAll(data.getStringArrayListExtra("list_pic"));
                        list_pic.add(null);
                        adapter_photo.notifyDataSetChanged();
                    }

                }
                break;
        }
    }

}
