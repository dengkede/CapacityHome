package adapter;

import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
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
 * Created by zhang on 2018/6/21.
 */

public class PopProspectAdapter extends BaseAdapter {
    private Context context;
    private List<JSONObject> list;
    private Handler handler;
    public PopProspectAdapter(Context context, List<JSONObject> list,Handler handler) {
        this.context = context;
        this.list = list;
        this.handler = handler;
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
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.pop_prospect_item,null);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tv_totalPrice = (TextView) convertView.findViewById(R.id.tv_totalPrice);
            viewHolder.img_pic = (ImageView) convertView.findViewById(R.id.img_pic);
            viewHolder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            viewHolder.tv_price2 = (TextView) convertView.findViewById(R.id.tv_price2);
            viewHolder.img_jia = (RelativeLayout) convertView.findViewById(R.id.relayout_jia);
            viewHolder.img_jian = (RelativeLayout) convertView.findViewById(R.id.relayout_jian);
            viewHolder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        try {
            Picasso.with(context).load(RequestTag.BaseImageUrl + list.get(position).getString("wap_pic")).error(R.mipmap.default_iv).into(viewHolder.img_pic);
            if (!StringUtils.isEmpty(list.get(position).getString("title"))) {
                viewHolder.tv_title.setText(list.get(position).getString("title"));
            } else {
                viewHolder.tv_title.setText("未编辑");
            }
            viewHolder.tv_price2.setText("原价" + (list.get(position).getDouble("money1") / 1000 + "").replace(".0", ""));
            viewHolder.tv_price.setText((list.get(position).getDouble("money") / 1000 + "").replace(".0", ""));

            //holder.tv_marketPrice.setFlags(Paint. STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
            viewHolder.tv_price2.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            viewHolder.tv_totalPrice.setText(list.get(position).getInt("count")*list.get(position).getDouble("money") / 1000+"");
            viewHolder.tv_count.setText(list.get(position).getInt("count")+"");
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                    switch (v.getId()) {
                            case R.id.relayout_jia:
                                list.get(position).put("count", list.get(position).getInt("count") + 1);
                                Message message = new Message();
                                message.obj = list.get(position);
                                message.arg2 =1;
                                handler.sendMessage(message);
                                message = null;
                                break;
                            case R.id.relayout_jian:
                                if(list.get(position).getInt("count")==1){
                                    list.get(position).put("count", 0);
                                    Message message2 = new Message();
                                    message2.arg2 =-1;
                                    message2.obj = list.get(position);
                                    handler.sendMessage(message2);
                                    message2 = null;
                                    list.remove(position);
                                }else{
                                    list.get(position).put("count", list.get(position).getInt("count") -1);
                                    Message message2 = new Message();
                                    message2.arg2 =-1;
                                    message2.obj = list.get(position);
                                    handler.sendMessage(message2);
                                    message2 = null;
                                }
                                break;
                        }
                        PopProspectAdapter.this.notifyDataSetChanged();
                    }catch (Exception e){

                    }
                }
            };
            viewHolder.img_jia.setOnClickListener(onClickListener);
            viewHolder.img_jian.setOnClickListener(onClickListener);
        } catch (Exception e) {
            Log.d("pinpaiItem", e.toString());
        }
        return convertView;
    }

    static class ViewHolder {
        private ImageView img_pic;
        private TextView tv_title;
        private TextView tv_price;
        private TextView tv_price2;
        private RelativeLayout img_jia;
        private RelativeLayout img_jian;
        private TextView tv_totalPrice;
        private TextView tv_count;
    }

}
