package adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import http.RequestTag;
import utils.AdapterUtils;
import utils.DisplayUtil;
import utils.Md5;
import utils.StringUtils;

/**
 * Created by zhang on 2018/3/31.
 */

public class BankCardManageAdapter extends BaseAdapter {
    private List<JSONObject> list;
    private int index = -1;
    private PopupWindow popWindow;
    private TextView tv_cancel;
    private TextView tv_sure;
    private TextView tv_title;
    private TextView tv_address;

    public BankCardManageAdapter(List<JSONObject> list, Context context) {
        this.list = list;
        this.context = context;
    }

    private Context context;

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_bankcardmanage
                    , null);
        }
        TextView bankManage_tv_userName = AdapterUtils.getHolderItem(convertView, R.id.bankManage_tv_userName);
        TextView bankManage_tvBankName = AdapterUtils.getHolderItem(convertView, R.id.bankManage_tvBankName);
        final TextView bankManage_tvBankNumber = AdapterUtils.getHolderItem(convertView, R.id.bankManage_tvBankNumber);
        TextView bankManage_tv_set = AdapterUtils.getHolderItem(convertView, R.id.bankManage_tv_set);
        RelativeLayout relayout_delete = AdapterUtils.getHolderItem(convertView, R.id.relayout_delete);
        try {

            if (list.get(position).getString("default").equals("1")) {
                index = position;
                bankManage_tv_set.setTextColor(ContextCompat.getColor(context, R.color.fontcolor_yellow));
                bankManage_tv_set.setText("默认银行卡");
            } else {
                bankManage_tv_set.setTextColor(ContextCompat.getColor(context, R.color.fontcolor_f9));
                bankManage_tv_set.setText("设为默认");
            }

            if (!StringUtils.isEmpty(list.get(position).getString("name"))) {
                bankManage_tv_userName.setText(list.get(position).getString("name"));
            } else {
                bankManage_tv_userName.setText("未编辑");
            }
            if (!StringUtils.isEmpty(list.get(position).getString("title"))) {
                bankManage_tvBankName.setText(list.get(position).getString("title"));
            } else {
                bankManage_tvBankName.setText("未编辑");
            }
            if (!StringUtils.isEmpty(list.get(position).getString("number"))) {
                bankManage_tvBankNumber.setText(list.get(position).getString("number").replaceAll("\\d{4}(?!$)", "$0 "));
            } else {
                bankManage_tvBankNumber.setText("未编辑");
            }
            bankManage_tv_set.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //设为默认地址
                    if (position != index) {
                        try {
                            if(index == -1){
                                index = 0;
                            }
                            setDefault(list.get(position).getString("id"), position);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(context, "当前已是默认地址了", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            relayout_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopwindow(parent,position,bankManage_tvBankNumber.getText().toString());
                }
            });
        } catch (Exception e) {
            Log.d("exception", "数据解析异常");
        }
        return convertView;
    }

    /**
     * 删除银行卡
     */

    private void showPopwindow(View view, final int position, String content) {
        if (popWindow == null) {
            int width = context.getResources().getDisplayMetrics().widthPixels / 10 * 8;
//            int height = context.getResources().getDisplayMetrics().widthPixels / 2;
            View popView = LayoutInflater.from(context).inflate(R.layout.delete_img_pop, null);
            popWindow = new PopupWindow(popView, width, LinearLayout.LayoutParams.WRAP_CONTENT);
            tv_cancel = (TextView) popView.findViewById(R.id.tv_cancel);
            tv_sure = (TextView) popView.findViewById(R.id.tv_sure);
            tv_address = (TextView) popView.findViewById(R.id.tv_address);
            tv_title = (TextView) popView.findViewById(R.id.tv_title);
            popWindow.setAnimationStyle(R.style.popwin_anim_style);
            popWindow.setFocusable(true);
            popWindow.setOutsideTouchable(false);// 设置同意在外点击消失
            ColorDrawable dw = new ColorDrawable(0x30000000);
            popWindow.setBackgroundDrawable(dw);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.tv_cancel:
                            popWindow.dismiss();
                            break;
                        case R.id.tv_sure:
                            popWindow.dismiss();
                            try {
                                deleteBankCard(list.get(position).getString("id"), position);
                            }catch (Exception e){
                                Toast.makeText(context, "数据解析异常", Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }
                }
            };
            popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    DisplayUtil.backgroundAlpha(1.0f, (Activity) context);
                }
            });
            tv_title.setText("是否删除当前银行卡？");
            tv_address.setVisibility(View.VISIBLE);
            tv_sure.setOnClickListener(onClickListener);
            tv_cancel.setOnClickListener(onClickListener);
        }
        try {
            if (!StringUtils.isEmpty(list.get(position).getString("name"))) {
                content = content +" "+ list.get(position).getString("name");
            }
        }catch (Exception e){

        }
        tv_address.setText(content);
        tv_address.setGravity(Gravity.CENTER);
        DisplayUtil.backgroundAlpha(0.3f, (Activity) context);
        popWindow.showAtLocation(view, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 设置默认银行卡
     */
    private void setDefault(String bank_id, final int position) {
        ((BaseActivity)context).showLoadingView();
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
            jsonObject.put("id", bank_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(context, RequestTag.BaseUrl + RequestTag.BANK_DEFAULT, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if (response.getInt("status") != 0) {
                        Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "设置成功", Toast.LENGTH_SHORT).show();
                        list.get(index).put("default", 0 + "");
                        index = position;
                        list.get(position).put("default", 1 + "");
                        notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    Toast.makeText(context, "数据解析异常", Toast.LENGTH_SHORT).show();
                }
                ((BaseActivity)context).dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                Toast.makeText(context, "网络异常，请检查网络后重试", Toast.LENGTH_SHORT).show();
                ((BaseActivity)context).dismissLoadingView();
            }
        });
    }

    /**
     * 删除银行卡
     */
    private void deleteBankCard(String address_id, final int position) {
        ((BaseActivity)context).showLoadingView();
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
            jsonObject.put("id", address_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(context, RequestTag.BaseUrl + RequestTag.DELETE_BANK, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                    if (response.getInt("status") != 0) {
                        //Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                    } else {
                        index = -1;
                        list.remove(position);
                       BankCardManageAdapter.this.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    Toast.makeText(context, "数据解析异常", Toast.LENGTH_SHORT).show();
                }
                ((BaseActivity)context).dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                Toast.makeText(context, "网络异常，请检查网络后重试", Toast.LENGTH_SHORT).show();
                ((BaseActivity)context).dismissLoadingView();
            }
        });
    }


}
