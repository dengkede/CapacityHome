package adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import http.RequestTag;


public class RecommendAdvAdapter extends PagerAdapter {
    LayoutInflater inflater;
    // 轮播图数据
    private List<JSONObject> imgUrl = new ArrayList<JSONObject>();
    public RecommendAdvAdapter(LayoutInflater inflater, List<JSONObject> imgUrl) {
        super();
        this.inflater = inflater;
        this.imgUrl = imgUrl;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return imgUrl.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View layout = inflater.inflate(R.layout.layout_banner_item, null);
        ImageView imageView = (ImageView) layout.findViewById(R.id.iv_vp_picture);
        if(position%2==0){
            imageView.setImageResource(R.mipmap.banner1);
        }else{
            imageView.setImageResource(R.mipmap.banner2);
        }
        ((ViewPager) container).addView(layout, 0);
        try {
            Picasso.with(MyApplication.getAppContext()).load(RequestTag.BaseImageUrl + imgUrl.get(position).getString("pic")).error(R.mipmap.default_iv).placeholder(R.mipmap.default_iv).into(imageView);
        }catch (Exception e){

        }
        return layout;

    }
}