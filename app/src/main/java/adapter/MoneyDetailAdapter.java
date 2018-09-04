package adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.capacityhome.R;

import org.json.JSONObject;

import java.util.List;

import utils.AdapterUtils;
import utils.StringUtils;
import utils.TimeUtilsDate;

/**
 * Created by zhang on 2018/3/31.
 */

public class MoneyDetailAdapter extends BaseAdapter {
    private List<JSONObject> list;
    private String type;
    public MoneyDetailAdapter(List<JSONObject> list, Context context) {
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

    public void notifyDataSetChanged(String type) {
        this.type = type;
        MoneyDetailAdapter.this.notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_laundy_moneydetail, null);
        }
        if(position%2==0){
            convertView.setBackgroundColor(ContextCompat.getColor(context,R.color.fontcolor_f2));
        }else{
            convertView.setBackgroundColor(ContextCompat.getColor(context,R.color.white));
        }
        TextView tv_title = AdapterUtils.getHolderItem(convertView,R.id.tv_title);
        TextView tv_money = AdapterUtils.getHolderItem(convertView,R.id.tv_money);
        TextView tv_time = AdapterUtils.getHolderItem(convertView,R.id.tv_time);
        TextView moneyDetail_tv_typeName = AdapterUtils.getHolderItem(convertView,R.id.moneyDetail_tv_typeName);
        try {
            if (!StringUtils.isEmpty(list.get(position).getString("content"))) {
                tv_title.setText(list.get(position).getString("content"));
            }else{
                tv_title.setText(list.get(position).getString("未设置"));
            }
            if(!StringUtils.isEmpty(list.get(position).getString("money"))){
                tv_money.setText(list.get(position).getDouble("money")/1000+"");
            }else{
                tv_money.setText("未编辑");
            }
            if(!StringUtils.isEmpty(type)){
                moneyDetail_tv_typeName.setText(type);
            }
            if(!StringUtils.isEmpty(list.get(position).getString("addtime"))){
                tv_time.setText(TimeUtilsDate.timedate_week_hour2(list.get(position).getLong("addtime")));
            }else{
                tv_time.setText("");
            }
        }catch (Exception e){

        }
        return convertView;
    }


}
