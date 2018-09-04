package adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.example.administrator.capacityhome.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import model.CityBean;
import utils.AdapterUtils;

/**
 * Created by zhang on 2018/3/31.
 */

public class PopCityChooseAdapter extends BaseAdapter {
    private ArrayList<CityBean> list;
    private int index =-1;//记录上次点击位置
    public PopCityChooseAdapter(ArrayList<CityBean>list, Context context) {
        this.list = list;
        this.context = context;
    }

    private Context context;

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CityBean getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_pop_choosecity, null);
        }

        final TextView tv_city = AdapterUtils.getHolderItem(convertView, R.id.tv_city);
        tv_city.setText(list.get(position).getMark()+" "+list.get(position).getTitle());
//        if(index!=position){
            tv_city.setTextColor(ContextCompat.getColor(context,R.color.fontcolor_f3));
            tv_city.setBackground(ContextCompat.getDrawable(context,R.color.white));
//        }else{
//            tv_city.setTextColor(ContextCompat.getColor(context, R.color.white));
//            tv_city.setBackground(ContextCompat.getDrawable(context,R.color.fontcolor_yellow));
//        }
//        tv_city.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                if(index!=position) {
////                    tv_city.setTextColor(ContextCompat.getColor(context, R.color.white));
////                    tv_city.setBackground(ContextCompat.getDrawable(context,R.color.fontcolor_yellow));
////                    index = position;
////                    PopCityChooseAdapter.this.notifyDataSetChanged();
////                }
//            }
//        });
        return convertView;
    }
}
