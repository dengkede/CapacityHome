package adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.administrator.capacityhome.R;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

import http.RequestTag;

public class MyGridAdapter_home extends BaseAdapter {
    private Context context;
    private List<JSONObject> list;
    private int[] imgId = new int[]{R.mipmap.but1,R.mipmap.but2,R.mipmap.but3,R.mipmap.but4,R.mipmap.but5,R.mipmap.but6,R.mipmap.but7,R.mipmap.but8};
    private String[] titles = new String[]{"洗衣服","洗裤子","洗鞋子","演示视屏","活动展示","我的订单","我的钱包","关于我们"};
    public MyGridAdapter_home(Context context, List<JSONObject> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(view==null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.layout_home_griditem, null);
            holder.img = (ImageView) view.findViewById(R.id.img);
            holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        try {
           // holder.img.setImageDrawable(ContextCompat.getDrawable(context, imgId[i]));
            Picasso.with(context).load(RequestTag.BaseImageUrl+list.get(i).getString("pic")).error(R.mipmap.default_iv).placeholder(R.mipmap.default_iv).into(holder.img);
            holder.tv_title.setText(list.get(i).getString("title"));
        }catch (Exception e){

        }
        return view;
    }
    static class ViewHolder{
        private ImageView img;
        private TextView tv_title;
    }

}
