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
import widget.RoundTransform;

public class StoreComnoAdapter2 extends BaseAdapter {
    private Context context;
    private List<JSONObject> jsonObjects;

    public StoreComnoAdapter2(Context context, List<JSONObject> jsonObjects) {
        this.context = context;
        this.jsonObjects = jsonObjects;
    }

    @Override
    public int getCount() {
        return jsonObjects.size();
    }

    @Override
    public Object getItem(int i) {
        return jsonObjects.get(i);
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
            view = LayoutInflater.from(context).inflate(R.layout.combo_item2,null);
            viewHolder.tv_title = (TextView) view.findViewById(R.id.tv_title);
            viewHolder.tv_price = (TextView) view.findViewById(R.id.tv_price);
            viewHolder.tv_price2 = (TextView) view.findViewById(R.id.tv_price2);
            viewHolder.tv_baoxi = (TextView) view.findViewById(R.id.tv_baoxi);
            viewHolder.tv_unit = (TextView) view.findViewById(R.id.tv_unit);
            viewHolder.tv_peopleType = (TextView) view.findViewById(R.id.tv_peopleType);
            viewHolder.tv_storeName = (TextView) view.findViewById(R.id.tv_storeName);
            viewHolder.img_pic = (ImageView) view.findViewById(R.id.img_pic);
            viewHolder.tv_price3 = (TextView) view.findViewById(R.id.tv_price3);
            viewHolder.brand_img = (ImageView) view.findViewById(R.id.brand_img);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        try{
            if(!StringUtils.isEmpty(jsonObjects.get(i).getString("title"))){
                viewHolder.tv_title.setText(jsonObjects.get(i).getString("title"));
            }else{
                viewHolder.tv_title.setText("未编辑");
            }
            if(!StringUtils.isEmpty(jsonObjects.get(i).getString("stitle"))){
                viewHolder.tv_storeName.setText(jsonObjects.get(i).getString("stitle"));
            }else{
                viewHolder.tv_storeName.setText("未编辑");
            }
            viewHolder.tv_price.setText(jsonObjects.get(i).getDouble("money")/1000+"");
            if(jsonObjects.get(i).getInt("num")/100<999) {
                viewHolder.tv_price2.setVisibility(View.VISIBLE);
                viewHolder.tv_unit.setVisibility(View.VISIBLE);
                viewHolder.tv_baoxi.setText("包洗");
                viewHolder.tv_price2.setText(jsonObjects.get(i).getInt("num") / 100 + "");
            }else{
                viewHolder.tv_price2.setVisibility(View.GONE);
                viewHolder.tv_unit.setVisibility(View.GONE);
                viewHolder.tv_baoxi.setText("不限件数");
            }
            if(!StringUtils.isEmpty(jsonObjects.get(i).getString("suitable"))) {
                viewHolder.tv_peopleType.setText(""+jsonObjects.get(i).getString("suitable"));
            }else{
                viewHolder.tv_peopleType.setText("暂未编辑");
            }
            if(!StringUtils.isEmpty(jsonObjects.get(i).getString("pic"))){
                Picasso.with(context).load(RequestTag.BaseImageUrl+jsonObjects.get(i).getString("pic")).transform(new RoundTransform(12)).error(R.mipmap.default_iv).placeholder(R.mipmap.default_iv).into(viewHolder.img_pic);
            }else{
                viewHolder.img_pic.setImageDrawable(ContextCompat.getDrawable(context,R.mipmap.default_iv));
            }
            if(jsonObjects.get(i).getJSONArray("brand")!=null&&jsonObjects.get(i).getJSONArray("brand").length()>0){
                viewHolder.brand_img.setVisibility(View.VISIBLE);
                Picasso.with(context).load(RequestTag.BaseImageUrl+jsonObjects.get(i).getJSONArray("brand").getJSONObject(0).getString("pic")).error(R.mipmap.default_iv).placeholder(R.mipmap.default_iv).into(viewHolder.brand_img);
            }else{
                viewHolder.brand_img.setVisibility(View.GONE);
            }
            viewHolder.tv_price3.setText(jsonObjects.get(i).getInt("cell")+"");
        }catch (Exception e){

        }
        return view;
    }
    static class ViewHolder{
        private TextView tv_title;
        private TextView tv_price;
        private TextView tv_price2;//包洗
        private TextView tv_unit;//单位
        private TextView tv_baoxi;//包洗
        private TextView tv_price3;//已售
        private TextView tv_peopleType;//适合人群
        private TextView tv_storeName;//门店名称
        private ImageView img_pic;
        private ImageView brand_img;
    }
}
