package adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.administrator.capacityhome.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import http.RequestTag;

/**
 * Created by zhang on 2018/6/2.
 */

public class MyBrandListAdapter extends BaseAdapter {
    private Click click;
    private Context context;
    private int index = 0;//记录上次点击位置
    private List<JSONObject> lists;//数据源
    private int brandId = 0;

    public MyBrandListAdapter(Context context, List<JSONObject> lists, Click click, int brandId) {
        this.context = context;
        this.lists = lists;
        this.click = click;
        this.brandId = brandId;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return lists.size();
    }

    @Override
    public JSONObject getItem(int arg0) {
        // TODO Auto-generated method stub
        return lists.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.brand_item, null);
            holder.img_brand = (ImageView) convertView.findViewById(R.id.img_brand);
            holder.tv_state = (RelativeLayout) convertView.findViewById(R.id.tv_state);
            holder.relayout_parent = (RelativeLayout) convertView.findViewById(R.id.relayout_parent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        GradientDrawable gradientDrawable = (GradientDrawable) holder.relayout_parent.getBackground();
        try {
            Picasso.with(context).load(RequestTag.BaseImageUrl + lists.get(position).getString("pic")).error(R.mipmap.default_iv).into(holder.img_brand);
            if(position==0){
                holder.tv_state.setVisibility(View.VISIBLE);
                gradientDrawable.setStroke((int) context.getResources().getDimension(R.dimen.x1), ContextCompat.getColor(context, R.color.fontcolor_yellow));
            }
            if(brandId == lists.get(position).getInt("id")){
                holder.tv_state.setVisibility(View.VISIBLE);
                gradientDrawable.setStroke((int) context.getResources().getDimension(R.dimen.x1), ContextCompat.getColor(context, R.color.fontcolor_yellow));
            }else{
                holder.tv_state.setVisibility(View.GONE);
                gradientDrawable.setStroke((int) context.getResources().getDimension(R.dimen.x1), ContextCompat.getColor(context, R.color.transparent));
            }
            if(getCount()==1){
                holder.tv_state.setVisibility(View.VISIBLE);
                gradientDrawable.setStroke((int) context.getResources().getDimension(R.dimen.x1), ContextCompat.getColor(context, R.color.fontcolor_yellow));
            }
        } catch (Exception e) {
            Log.d("eejsfkjksl", e.toString());
        }
        //假设mPagerSize=8，假如点击的是第二页（即mIndex=1）上的第二个位置item(position=1),那么这个item的实际位置就是pos=9
//        holder.tv_name.setText(lists.get(pos).getName() + "");
//        holder.iv_nul.setImageResource(lists.get(pos).getUrl());
        //添加item监听

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                try {
                    brandId = lists.get(position).getInt("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                click.onClick(position);
                MyBrandListAdapter.this.notifyDataSetChanged();
            }
        });
        return convertView;
    }

    static class ViewHolder {
        private RelativeLayout tv_state;
        private ImageView img_brand;
        private RelativeLayout relayout_parent;
    }

    public interface Click {
        void onClick(int position);
    }

}
