package adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.TimeUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.selfcenter.Approve_completeActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import http.RequestTag;
import utils.AdapterUtils;
import utils.StringUtils;
import utils.TimeUtilsDate;

/**
 * Created by zhang on 2018/3/31.
 */

public class SmartHomeListAdapter extends BaseAdapter {
    private List<JSONObject> list;

    public SmartHomeListAdapter(List<JSONObject> list, Context context) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
//        if(convertView == null){
//            convertView = View.inflate(context, R.layout.item_smarthomelist, null);
//        }
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.layout_mainvideo_item, null);
        }
        final ImageView img_pic = AdapterUtils.getHolderItem(convertView, R.id.img_pic);
        TextView tv_title = AdapterUtils.getHolderItem(convertView, R.id.tv_title);
        TextView tv_type = AdapterUtils.getHolderItem(convertView, R.id.tv_type);
        TextView tv_time = AdapterUtils.getHolderItem(convertView, R.id.tv_time);
        TextView tv_name = AdapterUtils.getHolderItem(convertView, R.id.tv_name);
        try {
            if (list.get(position).getJSONArray("pics") != null) {

//                Iterator iterator = list.get(position).getJSONObject("pics").keys();
//                String key = (String) iterator.next();
                Picasso.with(context).load(RequestTag.BaseImageUrl + list.get(position).getJSONArray("pics").getString(0)).error(R.mipmap.img_user).into(img_pic, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        try {
                            Picasso.with(context).load(RequestTag.BaseImageUrl + MyApplication.getConfigrationJson().getJSONObject("other").getString("default_img")).error(R.mipmap.img_user).into(img_pic);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            } else {
                Picasso.with(context).load(R.mipmap.img_user).into(img_pic);
            }
            if (!StringUtils.isEmpty(list.get(position).getString("content"))) {
                tv_title.setText(list.get(position).getString("content"));
            } else {
                tv_title.setText("未编辑");
            }
            String s_name = "";
            if(list.get(position).getString("type").equals("0")){
                s_name = "提醒源：";
            }else if(list.get(position).getString("type").equals("1")){
                s_name = "抓拍者：";
            }else{
                s_name = "报警源：";
            }
            if (!StringUtils.isEmpty(list.get(position).getString("name"))) {
                tv_name.setText(s_name+list.get(position).getString("mobile")+"（"+list.get(position).getString("name")+"）");
            } else {
                tv_title.setText(s_name+"：未编辑");
            }
            String s = "";
            if (!StringUtils.isEmpty(list.get(position).getString("addtime"))) {
              if(list.get(position).getString("type").equals("0")){
                  s = "提醒于：";
              }else if(list.get(position).getString("type").equals("1")){
                  s = "抓拍于：";
              }else{
                  s = "报警于：";
              }
                tv_time.setText(s+ utils.TimeUtils.timeslash(list.get(position).getString("addtime")));
            } else {
                tv_time.setText(s+"未编辑");
            }
            if(!StringUtils.isEmpty(list.get(position).getString("type"))){
                tv_type.setText(MyApplication.getConfigrationJson().getJSONObject("door").getJSONArray("type").getJSONArray(Integer.parseInt(list.get(position).getString("type"))).getString(0));
                tv_type.setTextColor(Color.parseColor(MyApplication.getConfigrationJson().getJSONObject("door").getJSONArray("type").getJSONArray(list.get(position).getInt("type")).getString(1)));
            }else{
               tv_type.setText("");
            }

        } catch (Exception e) {
            Log.d("asd",e.toString());
        }
//        final ImageView smart_img = AdapterUtils.getHolderItem(convertView,R.id.smart_img);
//        TextView tv_time = AdapterUtils.getHolderItem(convertView,R.id.tv_time);
//        TextView smart_address = AdapterUtils.getHolderItem(convertView,R.id.smart_address);
//        try{
//            if(!StringUtils.isEmpty(list.get(position).getString("pics"))){
//                JSONArray jsonArray = new JSONArray(list.get(position).getString("pics"));
//                //Picasso.with(context).load(RequestTag.BaseImageUrl+jsonArray.getString(0)).into(smart_img);
//
//                Picasso.with(context).load(RequestTag.BaseImageUrl+jsonArray.getString(0)).error(R.mipmap.img_user).into(smart_img, new Callback() {
//                    @Override
//                    public void onSuccess() {
//
//                    }
//
//                    @Override
//                    public void onError() {
//                        try {
//                            Picasso.with(context).load(RequestTag.BaseImageUrl + MyApplication.getConfigrationJson().getJSONObject("other").getString("default_img")).error(R.mipmap.img_user).into(smart_img);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//
//            }else {
//                Picasso.with(context).load(R.mipmap.img_user).into(smart_img);
//            }
//            smart_address.setText(list.get(position).getString("content"));
//            long time = System.currentTimeMillis()/1000 - list.get(position).getLong("addtime");
//            //tv_time.setText(TimeUtilsDate.time2(time));
//        }catch (Exception e){
//
//        }
        return convertView;
    }
}
