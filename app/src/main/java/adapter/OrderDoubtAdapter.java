package adapter;

import android.content.Context;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.administrator.capacityhome.R;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.util.List;

/**
 * Created by zhang on 2018/4/4.
 * 订单疑问照片
 */

public class OrderDoubtAdapter extends BaseAdapter {
    private List<String> list;
    private Context context;
    private int width;

    public OrderDoubtAdapter(List<String> list, Context context, int width) {
        this.list = list;
        this.context = context;
        this.width = width;
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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout, null);
            holder = new ViewHolder();
            holder.myImage = (ImageView) convertView.findViewById(R.id.myImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.myImage.getLayoutParams();
        params.width = width;
        params.height = width;
        holder.myImage.setLayoutParams(params);
        if (list.get(position) == null) {
            Picasso.with(context).load(R.mipmap.add_default).into(holder.myImage);
        } else {
//            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"zhc.png");
            Picasso.with(context).load(new File(list.get(position))).error(R.mipmap.add_default).into(holder.myImage);
            //  Picasso.with(context).load("http://img3.imgtn.bdimg.com/it/u=1853920800,3896119207&fm=27&gp=0.jpg").into(holder.myImage);
        }
        return convertView;
    }

    class ViewHolder {
        ImageView myImage;
    }
}
