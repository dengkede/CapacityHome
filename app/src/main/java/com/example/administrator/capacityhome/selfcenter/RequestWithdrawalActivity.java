package com.example.administrator.capacityhome.selfcenter;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Timer;
import java.util.TimerTask;

import adapter.PopListAdapter;
import http.RequestTag;
import utils.DisplayUtil;
import utils.FormatUtil;
import utils.GetAgrement;
import utils.Md5;
import utils.StringUtils;
import utils.paydialog.IntetnetState;
import utils.paydialog.Pay_Dialog;

/**
 * 申请提现MyApplication.getUserJson().getLong("money")/1000+""
 */
public class RequestWithdrawalActivity extends BaseActivity implements View.OnClickListener {
    private EditText requestWithdrawal_tv_money;
    private TextView requestWithdrawal_tv_bankName;//银行名称
    private TextView requestWithdrawal_tv_userName;//银行用户名称
    private TextView requestWithdrawal_tv_bankNumber;//银行卡号
    private PopupWindow popWindow;
    private LinearLayout layout_parent;
    private ListView layout_pop_list;
    private PopListAdapter popListAdapter;
    private List<JSONObject> list_bank = new ArrayList<>();
    private String bank_id = null;
    private TextView requestWithdrawal_tvSubmit;
    private TextView requestWithdrawal_allDraw;//全部提现
    private RelativeLayout layout_top;
    /**
     * 支付弹框
     *
     * @param savedInstanceState
     */
    private Pay_Dialog.Onclick onclick;//支付按钮回调接口
    private IntetnetState pay_state;//支付弹框
    private boolean isAll = false;//是否选择的全部提现
    private GetAgrement getAgrement;//提现说名
    private RelativeLayout layout_bind;
    private RelativeLayout relayout_chooseBank;
    private boolean isUpdate = false;//判断是否前去绑定银行卡，重新刷新当前界面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_withdrawal);
        initView();
        getBankList();

    }

    private void initView() {
        layout_top = (RelativeLayout) findViewById(R.id.layout_top);
        findViewById(R.id.relayout_replain).setOnClickListener(this);
        layout_bind = (RelativeLayout) findViewById(R.id.layout_bind);
        findViewById(R.id.relayout_back).setOnClickListener(this);
        requestWithdrawal_tvSubmit = (TextView) findViewById(R.id.requestWithdrawal_tvSubmit);
        requestWithdrawal_allDraw = (TextView) findViewById(R.id.requestWithdrawal_allDraw);
        requestWithdrawal_tvSubmit.setOnClickListener(this);
        layout_bind.setOnClickListener(this);
        requestWithdrawal_allDraw.setOnClickListener(this);
        layout_parent = (LinearLayout) findViewById(R.id.layout_parent);
        relayout_chooseBank = (RelativeLayout) findViewById(R.id.relayout_chooseBank);
        relayout_chooseBank.setOnClickListener(this);
        requestWithdrawal_tv_money = (EditText) findViewById(R.id.requestWithdrawal_tv_money);
        requestWithdrawal_tv_bankNumber = (TextView) findViewById(R.id.requestWithdrawal_tv_bankNumber);
        requestWithdrawal_tv_bankName = (TextView) findViewById(R.id.requestWithdrawal_tv_bankName);
        requestWithdrawal_tv_userName = (TextView) findViewById(R.id.requestWithdrawal_tv_userName);
        try {
//            requestWithdrawal_tv_money.setHint(MyApplication.getUserJson().getLong("money") / 1000 + "");
            Spanned strC = Html.fromHtml("请输入金额，最小 <strong><font color=#ff6600>" + MyApplication.getConfigrationJson().getJSONObject("sysset").getLong("tixian_money") + "</font></strong>  元起提");
            requestWithdrawal_tv_money.setHint(strC);
        } catch (Exception e) {
            e.printStackTrace();
        }
        onclick = new Pay_Dialog.Onclick() {
            @Override
            public void OnClickLisener(View v) {
                if (isAll) {
                    requestWithdrawal_allDraw.setClickable(false);
                    try {
                        requestWithdraw(bank_id, MyApplication.getUserJson().getLong("money") + "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    requestWithdrawal_tvSubmit.setClickable(false);
                    requestWithdraw(bank_id, requestWithdrawal_tv_money.getText().toString());
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(new AnimationUtils().loadAnimation(this, R.anim.bg_alpha));
        switch (v.getId()) {
            case R.id.relayout_back:
                finish();
                break;
            case R.id.relayout_chooseBank:
                //选择银行卡
                showPopwindow();
                break;
            case R.id.requestWithdrawal_tvSubmit:
                //申请提现
                isAll = false;
                try {
                    if (MyApplication.getUserJson().getLong("money") / 1000 == 0) {
                        toastMessage("当前没有余额");
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (!requestWithdrawal_tv_money.getText().toString().isEmpty() && FormatUtil.isNumeric(requestWithdrawal_tv_money.getText().toString())) {
                    try {
                        if (Long.parseLong(requestWithdrawal_tv_money.getText().toString()) > MyApplication.getUserJson().getLong("money") / 1000) {
                            toastMessage("超出可提现余额");
                            break;
                        } else {
                            if (!StringUtils.isEmpty(bank_id)) {
                                if (pay_state == null) {
                                    pay_state = new IntetnetState();
                                }
                                pay_state.showGetData_pay(0, this, getResources().getDisplayMetrics(), onclick, layout_top, DisplayUtil.getStatusBarHeight(this));
                                pay_state.dismissLinsener(requestWithdrawal_tvSubmit);
                            } else {
                                requestWithdrawal_tvSubmit.setClickable(true);
                                toastMessage("请选择银行卡");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    toastMessage("请输入正确的金额");
                    requestWithdrawal_tvSubmit.setClickable(true);
                }
                break;
            case R.id.requestWithdrawal_allDraw:
                //全部提现
                isAll = true;
                try {
                    if (MyApplication.getUserJson().getLong("money") / 1000 == 0) {
                        toastMessage("当前没有余额");
                        break;
                    } else {
                        if (pay_state == null) {
                            pay_state = new IntetnetState();
                        }
                        pay_state.showGetData_pay(0, this, getResources().getDisplayMetrics(), onclick, layout_top, DisplayUtil.getStatusBarHeight(this));
                        pay_state.dismissLinsener(requestWithdrawal_allDraw);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.relayout_replain:
                if (getAgrement == null) {
                    getAgrement = new GetAgrement(this);
                }
                getAgrement.getAggrement_Detail(null, "withdrawal", "了解了");
                break;
            case R.id.layout_bind:
                isUpdate = true;
                Bundle bundle = new Bundle();
                bundle.putString("add", "add");
                startActivity_Bundle(this, AddBankActivity.class, bundle);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isUpdate) {
            getBankList();
        }
    }

    /**
     * 申请提现
     */
    private void requestWithdraw(String bank_id, String money) {
        showLoadingView();
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid", MyApplication.getUid());
            jsonObject.put("money", money);
            jsonObject.put("paypwd", pay_state.getPay_dialog().password());
            jsonObject.put("bank_id", bank_id);//银行卡列表id
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.WITHDRAW, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, final JSONObject response) {
                try {
                    if (response.getInt("status") != 0) {
                        Toast toast = Toast.makeText(RequestWithdrawalActivity.this, response.getString("msg"), Toast.LENGTH_LONG);
                        showMyToast(toast,response.getLong("time"));
                    } else {
                        requestWithdrawal_tv_money.setHint(response.getJSONObject("data").getString("money"));
                    }
                } catch (Exception e) {
                }
                if (pay_state != null) {
                    pay_state.dismiss_pay();
                }
                requestWithdrawal_tvSubmit.setClickable(true);
                requestWithdrawal_allDraw.setClickable(true);
                dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage("网络异常，请检查网络后重试");
                if (pay_state != null) {
                    pay_state.dismiss_pay();
                }
                dismissLoadingView();
                requestWithdrawal_tvSubmit.setClickable(true);
                requestWithdrawal_allDraw.setClickable(true);
            }
        });
    }

    /**
     * 获取银行卡列表
     */
    private void getBankList() {
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
            //jsonObject.put("status",status);//非必传
            // jsonObject.put("additional",additional+"");//非必传
            jsonObject.put("username", MyApplication.getUserJson().getString("m_username"));
            jsonObject.put("pwd", MyApplication.getPasswprd());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.BANK_LIST, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if (response.getInt("status") != 0) {

                    } else {
                        if (response.getJSONArray("data") == null || response.getJSONArray("data").length() == 0) {
                            layout_bind.setVisibility(View.VISIBLE);
                            relayout_chooseBank.setVisibility(View.GONE);
                        } else {
                            layout_bind.setVisibility(View.GONE);
                            relayout_chooseBank.setVisibility(View.VISIBLE);
                            for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                                //  list.add(response.getJSONArray("data").getJSONObject(i));
                                if (response.getJSONArray("data").getJSONObject(i).getString("default").equals("1")||response.getJSONArray("data").length()==1) {
                                    //是默认银行卡
                                    JSONObject jsonObject1 = response.getJSONArray("data").getJSONObject(i);
                                    bank_id = jsonObject1.getString("id");
                                    if (!StringUtils.isEmpty(jsonObject1.getString("name"))) {
                                        requestWithdrawal_tv_userName.setText(jsonObject1.getString("name"));
                                    } else {
                                        requestWithdrawal_tv_userName.setText("未设置");
                                    }
                                    if (!StringUtils.isEmpty(jsonObject1.getString("title"))) {
                                        requestWithdrawal_tv_bankName.setText(jsonObject1.getString("title"));
                                    } else {
                                        requestWithdrawal_tv_bankName.setText("未设置");
                                    }
                                    if (!StringUtils.isEmpty(jsonObject1.getString("number"))) {
                                        requestWithdrawal_tv_bankNumber.setText(setString(jsonObject1.getString("number")).replaceAll("\\d{4}(?!$)", "$0 "));
                                    } else {
                                        requestWithdrawal_tv_bankNumber.setText("未设置");
                                    }
                                }
                                list_bank.add(response.getJSONArray("data").getJSONObject(i));
                            }

                        }
                    }

                } catch (Exception e) {

                }
                dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dismissLoadingView();
            }
        });
    }

    /**
     * 把某几位变为不可见
     */
    private String setString(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (i >= 6 && i <= s.length() - 4) {
                sb.append('*');
            } else {
                sb.append(c);
            }
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <= sb.length(); i++) {
            if (i % 4 == 0) {
                builder.append(sb.charAt(i - 1) + " ");
            } else {
                builder.append(sb.charAt(i - 1));
            }
        }
        return builder.toString();
    }

    private void showPopwindow() {
        if (popWindow == null) {
            layout_parent = (LinearLayout) findViewById(R.id.layout_parent);
            View popView = View.inflate(this, R.layout.layout_pop_list, null);
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.6);
            layout_pop_list = (ListView) popView.findViewById(R.id.layout_pop_list);
            popListAdapter = new PopListAdapter(this, list_bank, false);
            layout_pop_list.setAdapter(popListAdapter);
            if (list_bank.size() >= 7) {
                popWindow = new PopupWindow(popView, width, height);
            } else {
                popWindow = new PopupWindow(popView, width, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
            popWindow.setAnimationStyle(R.style.popwin_anim_style);
            popWindow.setFocusable(true);
            popWindow.setOutsideTouchable(false);// 设置同意在外点击消失
            ColorDrawable dw = new ColorDrawable(0x30000000);
            popWindow.setBackgroundDrawable(dw);

        }

        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                DisplayUtil.backgroundAlpha(1.0f, RequestWithdrawalActivity.this);
            }
        });
        layout_pop_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    bank_id = list_bank.get(position).getString("id");
                    if (!StringUtils.isEmpty(list_bank.get(position).getString("name"))) {
                        requestWithdrawal_tv_userName.setText(list_bank.get(position).getString("name"));
                    } else {
                        requestWithdrawal_tv_userName.setText("未设置");
                    }
                    if (!StringUtils.isEmpty(list_bank.get(position).getString("title"))) {
                        requestWithdrawal_tv_bankName.setText(list_bank.get(position).getString("title"));
                    } else {
                        requestWithdrawal_tv_bankName.setText("未设置");
                    }
                    if (!StringUtils.isEmpty(list_bank.get(position).getString("number"))) {
                        requestWithdrawal_tv_bankNumber.setText(setString(list_bank.get(position).getString("number")).replaceAll("\\d{4}(?!$)", "$0 "));
                    } else {
                        requestWithdrawal_tv_bankNumber.setText("未设置");
                    }
                    popWindow.dismiss();
                } catch (Exception e) {
                    popWindow.dismiss();
                }
            }
        });
        DisplayUtil.backgroundAlpha(0.2f, RequestWithdrawalActivity.this);
        popWindow.showAtLocation(layout_parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    private void showMyToast(final Toast toast, final long cnt) {
        final Timer timer =new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        },0,3000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt );
    }

}
