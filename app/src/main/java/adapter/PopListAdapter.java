package adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import http.RequestTag;
import utils.AdapterUtils;
import utils.StringUtils;
import widget.CircleTransform;

/**
 * Created by Administrator on 2018/4/17.
 */

public class PopListAdapter extends BaseAdapter {
    private Context context;
    private List<JSONObject> list;
    private boolean isGone;
    public PopListAdapter(Context context, List<JSONObject> list,boolean isGone) {
        this.context = context;
        this.list = list;
        this.isGone = isGone;
    }

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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if(!isGone) {
                convertView = View.inflate(context, R.layout.layout_pop_list_item, null);
            }else{
                convertView = View.inflate(context, R.layout.layout_pop_list_item2, null);
            }
        }
        final ImageView img_bank = AdapterUtils.getHolderItem(convertView,R.id.img_bank);
        TextView tv_bankName = AdapterUtils.getHolderItem(convertView,R.id.tv_bankName);
        TextView tv_bankNumber = AdapterUtils.getHolderItem(convertView,R.id.tv_bankNumber);
        if(isGone){
            tv_bankNumber.setVisibility(View.GONE);
        }else{
            tv_bankNumber.setVisibility(View.VISIBLE);

        }
        try {
            if(!StringUtils.isEmpty(list.get(position).getString("pic"))){
              //  Picasso.with(context).load(RequestTag.BaseImageUrl+list.get(position).getString("pic")).error(R.mipmap.img_user).placeholder(R.mipmap.default_iv).transform(new CircleTransform()).into(img_bank);
                Picasso.with(context).load(RequestTag.BaseImageUrl+list.get(position).getString("pic")).placeholder(R.mipmap.default_iv).transform(new CircleTransform()).into(img_bank, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        try {
                            Picasso.with(context).load(RequestTag.BaseImageUrl+MyApplication.getConfigrationJson().getJSONObject("other").getString("default_img")).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).into(img_bank);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }else{
                Picasso.with(context).load(R.mipmap.default_iv).into(img_bank);
               // img_bank.setImageResource(R.mipmap.default_iv);
            }
            if(!StringUtils.isEmpty(list.get(position).getString("title"))){
                tv_bankName.setText(list.get(position).getString("title"));
            }else{
                tv_bankName.setText("未编辑");
            }
            if(!isGone) {
                String name = "";
                if (!StringUtils.isEmpty(list.get(position).getString("name"))) {
                    name = list.get(position).getString("name") + " ";
                } else {
                    name = " ";
                }
                if (!StringUtils.isEmpty(list.get(position).getString("number"))) {
                    tv_bankNumber.setText(name + setString(list.get(position).getString("number")).replaceAll("\\d{4}(?!$)", "$0 "));
                } else {
                    tv_bankNumber.setText(name + "未设置");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
    /**
     * 把某几位变为不可见
     */
    private String setString(String s){
        StringBuilder sb  =new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (i >= 6 && i <= s.length()-4) {
                sb.append('*');
            } else {
                sb.append(c);
            }
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 1;i<=sb.length();i++){
            if(i%4==0) {
                builder.append(sb.charAt(i - 1)+" ");
            }else{
                builder.append(sb.charAt(i - 1));
            }
        }
        return  builder.toString();
    }
}
