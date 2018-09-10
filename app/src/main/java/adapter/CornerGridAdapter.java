package adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.administrator.capacityhome.BaseActivity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.VideoPlayActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import http.RequestTag;
import utils.StringUtils;
import widget.RoundTransform;

/**
 * Created by zhang on 2018/4/4.
 * 圆角图片
 */

public class CornerGridAdapter extends BaseAdapter {
    private ArrayList<String> list;
    private Context context;
    private List<String> list_video;

    public CornerGridAdapter(ArrayList<String> list, Context context, List<String> list_video) {
        this.list = list;
        this.context = context;
        this.list_video = list_video;
    }

    @Override
    public int getCount() {
        if (list_video != null && list_video.size() > 0) {
            return list.size() + list_video.size();
        } else {
            return list.size();
        }
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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout, null);
            holder = new ViewHolder();
            holder.myImage = (ImageView) convertView.findViewById(R.id.myImage);
            holder.img_video = (ImageView) convertView.findViewById(R.id.img_video);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            if (position > list.size() - 1) {
                holder.myImage.setImageResource(R.mipmap.video_de);
                holder.img_video.setVisibility(View.GONE);
                //视屏tupian
                if (list_video.get(position-list.size()) != null) {
                    String path = RequestTag.BaseVieoUrl+list_video.get(position - list.size());
                    String s = path.substring(path.length()-7,path.length()-4)+".mp4";
                    if(fileIsExists(Environment.getExternalStorageDirectory()+ "/Download/video/"+s)){
                       holder.myImage.setImageDrawable(getNetVideoBitmap(Environment.getExternalStorageDirectory()+ "/Download/video/"+s));
                       // holder.myImage.setImageResource(R.mipmap.default_iv);
                        holder.img_video.setVisibility(View.VISIBLE);
                    }else {
                        Picasso.with(context).load(R.mipmap.video_de).transform(new RoundTransform(12)).into(holder.myImage);
                        //holder.myImage.setImageResource(R.mipmap.video_de);
                    }
                } else {
                    holder.myImage.setImageResource(R.mipmap.video_de);
                }

            } else {
                holder.img_video.setVisibility(View.GONE);
                if (!StringUtils.isEmpty(list.get(position))) {
                    // Picasso.with(context).load(RequestTag.BaseImageUrl + list.get(position)).into(holder.myImage);
                    final ViewHolder finalHolder = holder;
                    Picasso.with(context).load(RequestTag.BaseImageUrl + list.get(position)).placeholder(R.mipmap.default_iv).transform(new RoundTransform(12)).error(R.mipmap.img_user).into(holder.myImage, new Callback() {
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
                } else {
                    // Picasso.with(context).load("http://img3.imgtn.bdimg.com/it/u=1853920800,3896119207&fm=27&gp=0.jpg").into(holder.myImage);
                    Picasso.with(context).load(RequestTag.BaseImageUrl + MyApplication.getConfigrationJson().getJSONObject("other").getString("default_img")).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).into(holder.myImage);
                }
            }
        } catch (Exception e) {
            e.toString();
        }
        if (position > list.size() - 1) {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    ((BaseActivity) context).startActivity_ImagrPager(context, position, list, false);
                    Log.d("uoqowoertoiw",RequestTag.BaseVieoUrl+list_video.get(position - list.size()));
                    context.startActivity(new Intent(context, VideoPlayActivity.class).putExtra("path_video",RequestTag.BaseVieoUrl+list_video.get(position - list.size())));
                }
            });
        } else {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((BaseActivity) context).startActivity_ImagrPager(context, position, list, false);
                }
            });
        }
        return convertView;
    }

    class ViewHolder {
        ImageView myImage;
        ImageView img_video;
    }
    //判断文件是否存在

    public boolean fileIsExists(String strFile)

    {

        try

        {

            File f=new File(strFile);

            if(!f.exists())

            {

                return false;

            }



        }

        catch (Exception e)

        {

            return false;

        }



        return true;

    }
    public Drawable getNetVideoBitmap(String videoUrl) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //根据url获取缩略图
            retriever.setDataSource(videoUrl);
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        Drawable drawable = new BitmapDrawable(context.getResources(),bitmap);
        return drawable;
    }
}
