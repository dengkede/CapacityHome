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
import utils.StringUtils;
import utils.TimeUtilsDate;

public class ActivityListAdapter extends BaseAdapter {
    private Context context;
    private List<JSONObject> list;

    public ActivityListAdapter(Context context, List<JSONObject> list) {
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
        ViewHolder viewHolder;
        if(view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.activity_list_item,null);
            viewHolder.img_pic = (ImageView) view.findViewById(R.id.img_pic);
            viewHolder.tv_title = (TextView) view.findViewById(R.id.tv_title);
            viewHolder.tv_time = (TextView) view.findViewById(R.id.tv_time);
            viewHolder.tv_content = (TextView) view.findViewById(R.id.tv_content);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        try{
            if(!StringUtils.isEmpty(list.get(i).getString("wap_pic"))){
                Picasso.with(context).load(RequestTag.BaseImageUrl+list.get(i).getString("wap_pic")).error(R.mipmap.default_iv).placeholder(R.mipmap.default_iv).into(viewHolder.img_pic);
            }else{
                viewHolder.img_pic.setImageDrawable(ContextCompat.getDrawable(context,R.mipmap.default_iv));
            }
            if(!StringUtils.isEmpty(list.get(i).getString("title"))){
                viewHolder.tv_title.setText(list.get(i).getString("title"));
            }else{
                viewHolder.tv_title.setText("未编辑");
            }
            if(!StringUtils.isEmpty(list.get(i).getString("describe"))){
                viewHolder.tv_content.setText(list.get(i).getString("describe"));
            }else{
                viewHolder.tv_content.setText("未编辑");
            }
            viewHolder.tv_time.setText(TimeUtilsDate.timedate8(list.get(i).getLong("addtime")));
        }catch (Exception e){

        }
        if(i%2==0){
            view.setBackground(ContextCompat.getDrawable(context,R.color.white));
        }else{
            view.setBackground(ContextCompat.getDrawable(context,R.color.corlor_fa));
        }
        return view;
    }
    static class ViewHolder{
        private ImageView img_pic;
        private TextView tv_title;
        private TextView tv_time;
        private TextView tv_content;
    }

}
