package adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.capacityhome.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import utils.AdapterUtils;
import utils.StringUtils;

/**
 * Created by Administrator on 2018/4/18.
 */

public class StringAdapter extends BaseAdapter {
    private Context context;
    private List<JSONObject> list;
    private String key;//取值关键字
    public StringAdapter(Context context, List<JSONObject> list,String key) {
        this.context = context;
        this.list = list;
        this.key = key;
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
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.layout_pop_choose_text, null);
        }
        TextView tv_popTitle = AdapterUtils.getHolderItem(convertView,R.id.tv_popTitle);
        try {
            if(!StringUtils.isEmpty(list.get(position).getString(key))){
                tv_popTitle.setText(list.get(position).getString(key));
            }else{
                tv_popTitle.setText("未设置");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
