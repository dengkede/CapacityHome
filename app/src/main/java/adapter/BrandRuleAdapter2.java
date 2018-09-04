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

public class BrandRuleAdapter2 extends BaseAdapter {
    private Context context;
    private List<JSONObject> list;

    public BrandRuleAdapter2(Context context, List<JSONObject> list) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_gridprice_item,null);
            holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            holder.img_pic = (ImageView) convertView.findViewById(R.id.img_pic);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_marketPrice = (TextView) convertView.findViewById(R.id.tv_marketPrice);
            holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
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
            holder.tv_marketPrice.setText("原价"+(list.get(position).getDouble("money1")/1000+"").replace(".0",""));
            holder.tv_price.setText((list.get(position).getDouble("money")/1000+"").replace(".0",""));

            //holder.tv_marketPrice.setFlags(Paint. STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
            holder.tv_marketPrice.setPaintFlags(Paint. STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
            if(list.get(position).getInt("count")==0){
                holder.tv_count.setVisibility(View.GONE);
            }else{
                holder.tv_count.setVisibility(View.VISIBLE);
                holder.tv_count.setText(list.get(position).getInt("count")+"");
            }
        }catch (Exception e){
            Log.d("pinpaiItem",e.toString());
        }
        return convertView;
    }
    static class ViewHolder{
        TextView tv_title;
        TextView tv_price;
        ImageView img_pic;
        TextView tv_marketPrice;//市场价
        TextView tv_count;//数量
    }

}
