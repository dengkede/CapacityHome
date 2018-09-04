package adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.capacityhome.R;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

import http.RequestTag;
import utils.StringUtils;

/**
 * Created by zhang on 2018/6/3.
 */

public class BrandRuleAdapter extends BaseAdapter {
    private Context context;
    private List<JSONObject> list;

    public BrandRuleAdapter(Context context, List<JSONObject> list) {
        this.context = context;
        this.list = list;
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
        ViewHolder holder = null;
        if(convertView==null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.brand_rule_item,null);
            holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            holder.img_pic = (ImageView) convertView.findViewById(R.id.img_pic);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.relayout_img = (RelativeLayout) convertView.findViewById(R.id.relayout_img);
            holder.tv_marketPrice = (TextView) convertView.findViewById(R.id.tv_marketPrice);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        try{
            Picasso.with(context).load(RequestTag.BaseImageUrl+list.get(position).getString("wap_pic")).error(R.mipmap.default_iv).into(holder.img_pic);
            if(!StringUtils.isEmpty(list.get(position).getString("title"))) {
                holder.tv_title.setText(list.get(position).getString("title"));
            }else{
                holder.tv_title.setText("未编辑");
            }
            if(!StringUtils.isEmpty(list.get(position).getString("describe"))){
                holder.tv_content.setText(list.get(position).getString("describe"));
            }else{
                holder.tv_content.setText("未编辑");
            }
            holder.tv_marketPrice.setText(list.get(position).getDouble("money")/1000+"");
            holder.tv_price.setText(list.get(position).getDouble("money")/1000+"");
            if(position%2==0){
                convertView.setBackgroundColor(ContextCompat.getColor(context,R.color.fontcolor_f9f9));
                holder.relayout_img.setBackgroundColor(ContextCompat.getColor(context,R.color.fontcolor_f9f9));
            }else{
                convertView.setBackgroundColor(ContextCompat.getColor(context,R.color.white));
                holder.relayout_img.setBackgroundColor(ContextCompat.getColor(context,R.color.white));
            }
            //holder.tv_marketPrice.setFlags(Paint. STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
            holder.tv_marketPrice.setPaintFlags(Paint. STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
        }catch (Exception e){
            Log.d("pinpaiItem",e.toString());
        }
        return convertView;
    }
    static class ViewHolder{
        TextView tv_title;
        TextView tv_price;
        TextView tv_content;
        ImageView img_pic;
        TextView tv_marketPrice;//市场价
        RelativeLayout relayout_img;
    }

}
