package adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import utils.AdapterUtils;
import utils.StringUtils;

/**
 * Created by zhang on 2018/3/31.
 */

public class LaundryAdapter extends BaseAdapter {
    private List<JSONObject> list;

    public LaundryAdapter(List<JSONObject> list, Context context) {
        this.list = list;
        this.context = context;
    }

    private Context context;

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
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_laundy, null);
        }
        TextView tv_laundryAddress = AdapterUtils.getHolderItem(convertView, R.id.tv_laundryAddress);
        TextView tv_laundyNumber = AdapterUtils.getHolderItem(convertView, R.id.tv_laundyNumber);
        TextView tv_laundyState = AdapterUtils.getHolderItem(convertView, R.id.tv_laundyState);
        TextView tv_laundyOrderState = AdapterUtils.getHolderItem(convertView, R.id.tv_laundyOrderState);
        try {
            if (!StringUtils.isEmpty(list.get(position).getString("ark_name"))) {
                tv_laundryAddress.setText(list.get(position).getString("ark_name"));
            } else {
                tv_laundryAddress.setText("未设置");
            }
            if (!StringUtils.isEmpty(list.get(position).getString("ark_box_number"))) {
                tv_laundyNumber.setText(list.get(position).getString("ark_box_number")+"号箱");
            } else {
                tv_laundyNumber.setText("未设置");
            }

            if (!StringUtils.isEmpty(MyApplication.getConfigrationJson().getJSONObject("xorderstatus").getString(list.get(position).getString("status")))) {
                tv_laundyOrderState.setText(MyApplication.getConfigrationJson().getJSONObject("xorderstatus").getString(list.get(position).getString("status")));
                tv_laundyOrderState.setTextColor(Color.parseColor(MyApplication.getConfigrationJson().getJSONObject("other").getJSONObject("xieyiorder_color").getString(list.get(position).getString("status"))));
            } else {
                tv_laundyOrderState.setText("获取状态中");
                tv_laundyOrderState.setTextColor(ContextCompat.getColor(context,R.color.fontcolor_f9));
            }

            if(!StringUtils.isEmpty(list.get(position).getString("additional"))){
                //处于加急状态
                tv_laundyState.setText(MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("additional").getJSONArray(list.get(position).getInt("additional")).getString(0));
                tv_laundyState.setTextColor(Color.WHITE);
                GradientDrawable background = (GradientDrawable) tv_laundyState.getBackground();
                background.setColor(Color.parseColor(MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("additional").getJSONArray(list.get(position).getInt("additional")).getString(1)));
                // tv_laundyState.setBackground(ContextCompat.getDrawable(context,R.drawable.home_rect_sloid_state2));
            }else{
                tv_laundyState.setText("普通");
                tv_laundyState.setTextColor(ContextCompat.getColor(context,R.color.fontcolor_f9));
                GradientDrawable background = (GradientDrawable) tv_laundyState.getBackground();
                background.setColor(Color.parseColor(MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("additional").getJSONArray(list.get(position).getInt("additional")).getString(1)));
            }

//            if (!StringUtils.isEmpty(list.get(position).getString("additional"))) {
//                if (list.get(position).getString("additional").equals("0")) {
//                    tv_laundyState.setBackground(ContextCompat.getDrawable(context,R.drawable.home_rect_sloid_state));
//                    tv_laundyState.setText("普通");
//                    tv_laundyState.setTextColor(ContextCompat.getColor(context,R.color.fontcolor_f9));
//                } else {
//                    tv_laundyState.setText("加急");
//                    tv_laundyState.setBackground(ContextCompat.getDrawable(context,R.drawable.home_rect_sloid_state2));
//                    tv_laundyState.setTextColor(ContextCompat.getColor(context,R.color.white));
//                }
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
