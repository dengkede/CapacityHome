package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.administrator.capacityhome.R;

import org.json.JSONObject;

import java.util.List;

import utils.TimeUtils;


/**
 * Created by TanBo on 2017/3/6.
 */

public class WithdrawlRecord_Adapter extends BaseAdapter {
   /*  数据源 */
   private List<JSONObject> list;
   /*上下文   */
   private Context context;
   /* 把XML转化为View  */
   private LayoutInflater linearLayout;
   /* 选择默认位置  */
 private int choosePositio = -1;
    //1未审核 0以审核 2拒绝 3已转款
 private String[] state = new String[]{"待转款","待审","拒绝转款","已转款","重新审核"};//0=待转款 1=待审 2=拒绝转款 3=已转款 5=重新审核
    public WithdrawlRecord_Adapter(Context context, List<JSONObject> list) {
        this.list = list;
        this.context = context;
        linearLayout = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public JSONObject getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final  int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView==null){
            holder = new ViewHolder();
            convertView = linearLayout.inflate(R.layout.withdrawalrecord_item,null);
            holder.fzt_withdrawalRecord_item_tv_time1 = (TextView) convertView.findViewById(R.id.fzt_withdrawalRecord_item_tv_time1);
            holder.fzt_withdrawalRecord_item_tv_time2 = (TextView) convertView.findViewById(R.id.fzt_withdrawalRecord_item_tv_time2);
            holder.fzt_withdrawalRecord_item_tv_time3 = (TextView) convertView.findViewById(R.id.fzt_withdrawalRecord_item_tv_time3);
            holder.fzt_withdrawalRecord_item_tv_money = (TextView) convertView.findViewById(R.id.fzt_withdrawalRecord_item_tv_money);
            holder.fzt_withdrawalRecord_item_tv_state = (TextView) convertView.findViewById(R.id.fzt_withdrawalRecord_item_tv_state);
            holder.fzt_withdrawalRecord_item_tv_note = (TextView) convertView.findViewById(R.id.fzt_withdrawalRecord_item_tv_note);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            holder.fzt_withdrawalRecord_item_tv_time1.setText(TimeUtils.changeweekOne(list.get(position).getString("addtime")));
            holder.fzt_withdrawalRecord_item_tv_time2.setText(TimeUtils.times_simple(list.get(position).getString("addtime")));
            holder.fzt_withdrawalRecord_item_tv_money.setText(list.get(position).getDouble("money") / 1000 + "元");
            holder.fzt_withdrawalRecord_item_tv_note.setText("备注：" + list.get(position).getString("content"));
            holder.fzt_withdrawalRecord_item_tv_state.setText(state[list.get(position).getInt("show")]);
            holder.fzt_withdrawalRecord_item_tv_time3.setText(TimeUtils.time2(list.get(position).getLong("addtime")));
        }catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }
    class ViewHolder{
       private TextView fzt_withdrawalRecord_item_tv_time1;//星期几
       private TextView fzt_withdrawalRecord_item_tv_time2;//年月日
        private TextView fzt_withdrawalRecord_item_tv_time3;//几点
       private TextView fzt_withdrawalRecord_item_tv_money;//申请金额
       private TextView fzt_withdrawalRecord_item_tv_state;//状态
       private TextView fzt_withdrawalRecord_item_tv_note;//备注
    }

}
