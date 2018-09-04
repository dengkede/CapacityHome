package adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.smarthome.SmartHomeListActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import http.RequestTag;
import utils.AdapterUtils;

/**
 * Created by zhang on 2018/3/31.
 */

public class SmartHomeAdapter extends BaseAdapter {
    private List<JSONObject> list;

    public SmartHomeAdapter(List<JSONObject> list, Context context) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_smarthome, null);
        }
        final ImageView imageView = AdapterUtils.getHolderItem(convertView, R.id.img_view);
        TextView tv_time = AdapterUtils.getHolderItem(convertView, R.id.tv_time);
        TextView tv_address = AdapterUtils.getHolderItem(convertView, R.id.tv_address);
        try {
            //Picasso.with(context).load(RequestTag.BaseImageUrl + list.get(position).getString("pic")).into(imageView);
            Picasso.with(context).load(RequestTag.BaseImageUrl + list.get(position).getString("pic")).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    try {
                        Picasso.with(context).load(RequestTag.BaseImageUrl + MyApplication.getConfigrationJson().getJSONObject("other").getString("default_img")).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).into(imageView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            tv_address.setText(list.get(position).getString("title"));
            JSONObject jsonObject = MyApplication.getConfigrationJson();
            if(jsonObject!=null){
                JSONArray jsonArray = new JSONArray(jsonObject.getJSONObject("other").getJSONObject("door").getString("status"));
                JSONArray jsonArray2 = new JSONArray(jsonArray.getString(list.get(position).getInt("status")));
                tv_time.setText(jsonArray2.getString(0));
                tv_time.setBackgroundColor(Color.parseColor(jsonArray2.getString(1)));
                jsonObject = null;
                jsonArray = null;
                jsonArray2 = null;
//                if (list.get(position).getInt("status") == 0) {
//                    tv_time.setText("正常");
//                    tv_time.setBackgroundColor(ContextCompat.getColor(context, R.color.balck));
//                } else {
//                    tv_time.setText("异常");
//                    tv_time.setBackgroundColor(ContextCompat.getColor(context, R.color.color_ff00));
//                }
            }else{
                tv_time.setText("正常空");
                tv_time.setBackgroundColor(ContextCompat.getColor(context, R.color.balck));
            }

        } catch (Exception e) {

        }
        return convertView;
    }
}
