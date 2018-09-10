package adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.capacityhome.BaseActivity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import http.RequestTag;
import utils.StringUtils;
import utils.TimeUtilsDate;
import widget.CircleTransform;

/**
 * Created by 81014 on 2018/8/31.
 */

public class AllEvaluateListAdapter extends BaseAdapter {
    private List<JSONObject> list;
    private Context context;

    public AllEvaluateListAdapter(List<JSONObject> list, Context context) {
        this.list = list;
        this.context = context;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_home_evaluate_item,null);
            holder.tv_picConunt = (TextView) convertView.findViewById(R.id.tv_picConunt);
            holder.img_user = (ImageView) convertView.findViewById(R.id.img_user);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.layout_pic = (LinearLayout) convertView.findViewById(R.id.layout_pic);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            if (!StringUtils.isEmpty(list.get(position).getString("nickname"))) {
                holder.tv_name.setText(list.get(position).getString("nickname"));
            } else {
                holder.tv_name.setText(list.get(position).getString("未设置"));
            }
            holder.tv_time.setText(TimeUtilsDate.time_evaluate(list.get(position).getLong("addtime")));
            if (!StringUtils.isEmpty(list.get(position).getString("pics"))&& !list.get(position).getString("pics").equals("null")) {
                holder.tv_content.setText(list.get(position).getString("content"));
                holder.layout_pic.setVisibility(View.VISIBLE);
                holder.tv_picConunt.setText("【"+list.get(position).getString("pics").split(",").length+"张图】- ");
              //  setTextStyle(holder.tv_content, list.get(position).getString("content"), list.get(position).getString("pics").split(",").length + "",position);
            } else {
                holder.layout_pic.setVisibility(View.GONE);
                holder.tv_content.setText(list.get(position).getString("content"));
            }
            Picasso.with(context).load(RequestTag.BaseImageUrl + list.get(position).getString("head")).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).transform(new CircleTransform()).into(holder.img_user, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    try {
                        Picasso.with(context).load(RequestTag.BaseImageUrl + MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("head").get(MyApplication.getUserJson().getInt("sex"))).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).transform(new CircleTransform()).into(holder.img_user);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            holder.layout_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList lists = new ArrayList();
                    try {
                        for (int i = 0; i < list.get(position).getString("pics").split(",").length; i++) {
                            lists.add(list.get(position).getString("pics").split(",")[i]);
                        }
                        ((BaseActivity) context).startActivity_ImagrPager(context, 0, lists, false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (Exception e) {

        }

        return convertView;
    }
    static class ViewHolder{
        TextView tv_picConunt;//图片数量
        ImageView img_user;
        TextView tv_name;
        TextView tv_content;
        TextView tv_time;
        LinearLayout layout_pic;
    }
}
