package com.example.administrator.capacityhome.selfcenter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.capacityhome.BaseTakePhotoActivity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.StringAdapter;
import http.RequestTag;
import utils.ChoosePop;
import utils.ColumnUtils;
import utils.DisplayUtil;
import utils.ImageBase64;
import utils.ImagePost;
import utils.KeyBordUtil;
import utils.Md5;
import utils.RegisterPopupPicker;
import utils.StringUtils;
import utils.TakePhotoHelper;

/**
 * 实名认证
 */
public class ApproveActivity extends BaseTakePhotoActivity implements View.OnClickListener {
    private TextView tv_type;//证件类型
    private EditText et_cardNumber;//证件号码
    private RelativeLayout relayout_chooseType;//选择证件类型
    private ImageView img_front;//正面
    private ImageView img_verso;//反面
    private RegisterPopupPicker popupPicker_photo;//头像选择
    private TakePhotoHelper takePhotoHelper;
    private String direction = "front";//选择正反 front正
    private ImagePost imagePost;
    private String ptc1 = null;
    private String ptc2 = null;
    private TextView tv_submit;
    private EditText et_cardName;
    private String type_id = "56";//证件类型
    private ColumnUtils columnUtils;
    private ColumnUtils columnUtils2;
    private List<JSONObject> lis_type = new ArrayList<>();
    private PopupWindow popWindow;
    private TextView tv_popTitle;
    private ListView listView_type;
    private StringAdapter stringAdapter;
    private LinearLayout layout_parent;
    private TextView tv_reason;
    private TextView tv_state;
    private RelativeLayout relayout_state2;
    private RelativeLayout relayout_state;
    private TextView tv_state_title;
    private int approve_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_approve3);
        initView();
        approve_info();
    }

    private void initView() {
        relayout_state2 = (RelativeLayout) findViewById(R.id.relayout_state2);
        tv_state = (TextView) findViewById(R.id.tv_state);
        relayout_state = (RelativeLayout) findViewById(R.id.relayout_state);
        tv_type = (TextView) findViewById(R.id.tv_type);
        findViewById(R.id.relayout_back).setOnClickListener(this);
        tv_reason = (TextView) findViewById(R.id.tv_reason);
        tv_state_title = (TextView) findViewById(R.id.tv_state_title);
        relayout_chooseType = (RelativeLayout) findViewById(R.id.relayout_chooseType);
        relayout_chooseType.setOnClickListener(this);
        tv_submit = (TextView) findViewById(R.id.tv_submit);
        et_cardName = (EditText) findViewById(R.id.et_cardName);
        tv_submit.setOnClickListener(this);
        findViewById(R.id.relayout_front).setOnClickListener(this);
        findViewById(R.id.relayout_verso).setOnClickListener(this);
        et_cardNumber = (EditText) findViewById(R.id.et_cardNumber);
        img_front = (ImageView) findViewById(R.id.img_front);
        tv_type = (TextView) findViewById(R.id.tv_type);
        img_verso = (ImageView) findViewById(R.id.img_verso);
        takePhotoHelper = new TakePhotoHelper(null);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relayout_chooseType:
                //选择证件类型
                if (columnUtils2 == null) {
                    columnUtils2 = new ColumnUtils(ApproveActivity.this, new ColumnUtils.Result() {
                        @Override
                        public void onSuccess(String title, JSONArray jsonArray) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    lis_type.add(jsonArray.getJSONObject(i));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            showPopwindow(title);
                        }

                        @Override
                        public void onFailure(String msg) {
                            toastMessage(msg);
                        }
                    });
                }
                if (lis_type.size() == 0) {
                    columnUtils2.getColumn("", "id_type");
                } else {
                    showPopwindow(tv_popTitle.getText().toString());
                    //  showPopWindow(getView());
                }
                break;
            case R.id.relayout_front:
                //选择正面
                direction = "front";
                ChoosePop.showPop(this, "photo", "选择证件正面", v, popupPicker_photo, listener_photo);
                break;
            case R.id.relayout_verso:
                //选择反面
                direction = "verso";
                ChoosePop.showPop(this, "photo", "选择证件反面", v, popupPicker_photo, listener_photo);
                break;

            case R.id.tv_submit:
                if (StringUtils.isEmpty(ptc1)) {
                    toastMessage("请上传身份证正面照片");
                    break;
                }
                if (StringUtils.isEmpty(ptc2)) {
                    toastMessage("请上传身份证背面照片");
                    break;
                }
                if (StringUtils.isEmpty(type_id)) {
                    toastMessage("请选择证件类型");
                    break;
                }
                if (StringUtils.isEmpty(et_cardName.getText().toString())) {
                    toastMessage("请输入证件名称");
                    break;
                }
                if (StringUtils.isEmpty(et_cardNumber.getText().toString())) {
                    toastMessage("请输入证件号码");
                    break;
                }
                approve(ptc1, ptc2, et_cardName.getText().toString());
                break;
            case R.id.relayout_back:
               back();
                break;
        }
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        TImage image = result.getImage();
        final String path = image.getOriginalPath();
//        String fileName = file.getName();
//        //得到图片格式
//        String typeText = fileName.substring(fileName.lastIndexOf("."),fileName.length());
        runOnUiThread(new Runnable() {
            File file = new File(path);

            @Override
            public void run() {
                if (direction.equals("front")) {
                    ptc1 = ImageBase64.imageToBase64(path);
                    postImage(ptc1, null);
                    Picasso.with(ApproveActivity.this).load(file).error(R.mipmap.add_default).into(img_front);
                } else {
                    ptc2 = ImageBase64.imageToBase64(path);
                    postImage(ptc2, null);
                    Picasso.with(ApproveActivity.this).load(file).error(R.mipmap.add_default).into(img_verso);
                }
            }
        });

      /*  RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file",file.getName() , requestFile);*/


    }

    /**
     * 上传图片
     */
    private void postImage(final String pics, JSONArray pics2) {
        if (imagePost == null) {
            imagePost = new ImagePost(new ImagePost.Result() {
                @Override
                public void success(JSONObject jsonObject) {
                    try {
                        if (jsonObject.getInt("status") != 0) {
                            toastMessage(jsonObject.getString("msg"));
                        } else {
                            // postFeedBack(doubtPop_etContent.getText().toString(), jsonObject.getString("url"));
                            if (direction.equals("front")) {
                                ptc1 = jsonObject.getString("url");
                            } else {
                                ptc2 = jsonObject.getString("url");
                            }
                        }

                    } catch (Exception e) {
                        if (direction.equals("front")) {
                            ptc1 = null;
                        } else {
                            ptc2 = null;
                        }
                    }

                    dismissLoadingView();
                }

                @Override
                public void failure(String msg) {
                    if (direction.equals("front")) {
                        ptc1 = null;
                    } else {
                        ptc2 = null;
                    }
                    dismissLoadingView();
                }
            }, this);
        }
        imagePost.psotDoubt(pics, pics2);
        showLoadingView();
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


    // 设置popupWindow背景半透明
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;// 0.0-1.0
        getWindow().setAttributes(lp);
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
            stringAdapter = new StringAdapter(this, lis_type, "title");
            listView_type.setAdapter(stringAdapter);
            if (lis_type.size() >= 7) {
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
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                        type = list_bank.get(position).getString("id");
//                        addBank_tv_bankName.setText(list_bank.get(position).getString("title"));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                tv_type.setText(lis_type.get(position).getString("title"));
                                type_id = lis_type.get(position).getString("id");
                                popWindow.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                }
            });
        }

        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                DisplayUtil.backgroundAlpha(1.0f, ApproveActivity.this);
            }
        });

        DisplayUtil.backgroundAlpha(0.2f, ApproveActivity.this);
        popWindow.showAtLocation(layout_parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }


    /**
     * 实名认证
     */
    private void approve(String pic1, String pic2, String name) {
        showLoadingView();
        KeyBordUtil.hintKeyboard(this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject json = new JSONObject();
        try {
            json.put("device_id", MyApplication.getDeviceId());
            json.put("uid", MyApplication.getUid());
            json.put("id_type", type_id);
            json.put("name", name);
            json.put("id_code", et_cardNumber.getText().toString());
            json.put("cert_pic1", pic1);//正面
            json.put("cert_pic2", pic2);//反面
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", json.toString());
        String sign = Md5.md5(json.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.REAL_authentication, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    toastMessage(response.getString("msg"));
                    approve_info();

                } catch (Exception e) {
                    toastMessage("数据解析异常");
                }
                dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("网络异常，请检查网络后重试");
                dismissLoadingView();
            }
        });
    }

    /**
     * 读取实名认证资料
     */
    private void approve_info() {
        showLoadingView();
        KeyBordUtil.hintKeyboard(this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject json = new JSONObject();
        try {
            json.put("device_id", MyApplication.getDeviceId());
            json.put("uid", MyApplication.getUid());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", json.toString());
        String sign = Md5.md5(json.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.GET_authentication, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, final JSONObject response) {
                try {
                    Log.d("shiming", response.toString());
                    if (!StringUtils.isEmpty(response.getJSONObject("data").getString("name"))) {
                        et_cardName.setText(response.getJSONObject("data").getString("name"));
                        //et_cardNumber.setSelection(et_cardName.getText().toString().length());
                    }
                    if (!StringUtils.isEmpty(response.getJSONObject("data").getString("id_code"))) {
                        et_cardNumber.setText(response.getJSONObject("data").getString("id_code"));
                    }
                    approve_status = response.getJSONObject("data").getInt("id_state");
                    if (!StringUtils.isEmpty(response.getJSONObject("data").getString("reason"))) {
                        if (response.getJSONObject("data").getString("reason").equals("null")) {
                            tv_reason.setText("");
                        } else {
                            tv_reason.setText(response.getJSONObject("data").getString("reason"));
                        }
                        tv_reason.setTextColor(Color.parseColor((String) MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("member_state_color").get(response.getJSONObject("data").getInt("id_state"))));
                        tv_state.setText((String) MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("member_state").get(response.getJSONObject("data").getInt("id_state")) + "");
                        tv_state.setTextColor(Color.parseColor((String) MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("member_state_color").get(response.getJSONObject("data").getInt("id_state"))));
                    } else {
                        tv_state.setText((String) MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("member_state").get(response.getJSONObject("data").getInt("id_state")) + "");
                        tv_state.setTextColor(Color.parseColor((String) MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("member_state_color").get(response.getJSONObject("data").getInt("id_state"))));
                    }
                    if (!StringUtils.isEmpty(response.getJSONObject("data").getString("cert_pic1"))) {
                        Picasso.with(ApproveActivity.this).load(RequestTag.BaseImageUrl + response.getJSONObject("data").getString("cert_pic1")).placeholder(R.mipmap.default_iv).error(R.mipmap.default_iv).into(img_front, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                try {
                                    Picasso.with(ApproveActivity.this).load(RequestTag.BaseImageUrl + MyApplication.getConfigrationJson().getJSONObject("other").getString("default_img")).placeholder(R.mipmap.default_iv).error(R.mipmap.default_iv).into(img_front);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (!StringUtils.isEmpty(response.getJSONObject("data").getString("cert_pic2"))) {
                        Picasso.with(ApproveActivity.this).load(RequestTag.BaseImageUrl + response.getJSONObject("data").getString("cert_pic2")).placeholder(R.mipmap.default_iv).error(R.mipmap.default_iv).into(img_verso, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                try {
                                    Picasso.with(ApproveActivity.this).load(RequestTag.BaseImageUrl + MyApplication.getConfigrationJson().getJSONObject("other").getString("default_img")).placeholder(R.mipmap.default_iv).error(R.mipmap.default_iv).into(img_verso);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
//                         if(response.getInt("status")!=0) {
//                             toastMessage(response.getString("msg"));
//                         }

                    if (columnUtils == null) {
                        columnUtils = new ColumnUtils(ApproveActivity.this, new ColumnUtils.Result() {
                            @Override
                            public void onSuccess(String title, JSONArray jsonArray) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        if (jsonArray.getJSONObject(i).getString("id").equals(response.getJSONObject("data").getString("id_type")))
                                            tv_type.setText(jsonArray.getJSONObject(i).getString("title"));
                                        break;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
//                                showPopwindow(title);
                            }

                            @Override
                            public void onFailure(String msg) {
                                toastMessage(msg);
                            }
                        });
                    }
                    columnUtils.getColumn("", "id_type");
                    if (response.getJSONObject("data").getString("id_state").equals("0")) {
                        //通过
                        tv_submit.setVisibility(View.GONE);
                        relayout_chooseType.setClickable(false);
                        editTextable(et_cardName, false);
                        editTextable(et_cardNumber, false);
                        findViewById(R.id.relayout_front).setVisibility(View.GONE);
                        findViewById(R.id.relayout_verso).setVisibility(View.GONE);
                        tv_state_title.setText("认证说明：");
                    }
                } catch (Exception e) {
                    Log.d("shiming", e.toString());
//                    try {
////                        if (!StringUtils.isEmpty(response.getJSONObject("data").getString("id_state"))) {
////                            tv_state2.setText((String) MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("member_state").get(response.getJSONObject("data").getInt("id_state")));
////                            tv_state2.setTextColor(Color.parseColor((String) MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("member_state_color").get(response.getJSONObject("data").getInt("id_state"))));
////                        }
//                    }catch (Exception e2){
//
//                    }
                }
                dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("网络异常，请检查网络后重试");
                dismissLoadingView();
            }
        });
    }

    //设置EditText可输入和不可输入状态
    private void editTextable(EditText editText, boolean editable) {
        if (!editable) { // disable editing password
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
            editText.setClickable(false); // user navigates with wheel and selects widget
        } else { // enable editing of password
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.setClickable(true);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            // System.out.println("按下了back键   onKeyDown()");
            back();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 返回数据传输处理
     */
    private void back() {
        Intent data = new Intent();
        data.putExtra("approve_status", approve_status);
        setResult(3, data);
        finish();
    }

}
