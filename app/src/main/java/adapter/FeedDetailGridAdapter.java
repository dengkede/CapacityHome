package adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.administrator.capacityhome.BaseActivity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.VideoPlayActivity;
import com.example.administrator.capacityhome.selfcenter.Approve_completeActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import http.RequestTag;
import utils.StringUtils;

/**
 * Created by zhang on 2018/4/4.
 */

public class FeedDetailGridAdapter extends BaseAdapter {
    private ArrayList<String> list;
    private Context context;

    public FeedDetailGridAdapter(ArrayList<String> list, Context context) {
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
        ViewHolder holder = null;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.layout,null);
            holder = new ViewHolder();
            holder.myImage = (ImageView) convertView.findViewById(R.id.myImage);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            if (!StringUtils.isEmpty(list.get(position))) {
               // Picasso.with(context).load(RequestTag.BaseImageUrl + list.get(position)).into(holder.myImage);
                if(list.get(position).contains(".mp4")){
                    holder.myImage.setImageDrawable(ContextCompat.getDrawable(context,R.mipmap.video_de));
                }else{
                    final ViewHolder finalHolder = holder;
                    Picasso.with(context).load(RequestTag.BaseImageUrl + list.get(position)).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).into(holder.myImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }
                        @Override
                        public void onError() {
                            try {
                                Picasso.with(context).load(RequestTag.BaseImageUrl + MyApplication.getConfigrationJson().getJSONObject("other").getString("default_img")).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).into(finalHolder.myImage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

            } else {
               // Picasso.with(context).load("http://img3.imgtn.bdimg.com/it/u=1853920800,3896119207&fm=27&gp=0.jpg").into(holder.myImage);
                Picasso.with(context).load(RequestTag.BaseImageUrl + MyApplication.getConfigrationJson().getJSONObject("other").getString("default_img")).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).into(holder.myImage);
            }
        }catch (Exception e){

        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!list.get(position).contains(".mp4")) {
                    ((BaseActivity) context).startActivity_ImagrPager(context, position, list, false);
                }else{
                    context.startActivity(new Intent(context, VideoPlayActivity.class).putExtra("path_video",RequestTag.BaseVieoUrl+list.get(position)));
                }
            }
        });
        return convertView;
    }
    class ViewHolder{
        ImageView myImage;
    }
}
