package adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import utils.AdapterUtils;
import utils.DisplayUtil;
import utils.StringUtils;
import utils.TimeUtilsDate;

/**
 * Created by zhang on 2018/3/31.
 */

public class FeedBackAdapter extends BaseAdapter {
    private List<JSONObject> list;
    private JSONArray types; //["系统问题", "你提我改", "投诉举报", "资金安全", "账户安全", "其它问题", "预约摄影师"]
    private JSONArray show;

    public FeedBackAdapter(List<JSONObject> list, Context context) {
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

    /**
     * 设置颜色值和type值
     */
    public void setDateType(JSONArray types, JSONArray show) {
        this.types = types;
        this.show = show;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_feedback, null);
        }
        TextView tv_feedback_questionState = AdapterUtils.getHolderItem(convertView,R.id.tv_feedback_questionState);
        TextView tv_feedback_state = AdapterUtils.getHolderItem(convertView, R.id.tv_feedback_state);
        TextView tv_feedback_week = AdapterUtils.getHolderItem(convertView, R.id.tv_feedback_week);
        TextView tv_feedback_data = AdapterUtils.getHolderItem(convertView, R.id.tv_feedback_data);
        TextView tv_feedbak_state2 = AdapterUtils.getHolderItem(convertView, R.id.tv_feedbak_state2);
        TextView tv_feedback_questionType = AdapterUtils.getHolderItem(convertView, R.id.tv_feedback_questionType);
        TextView tv_feedback_questionContent = AdapterUtils.getHolderItem(convertView, R.id.tv_feedback_questionContent);
        TextView tv_file = AdapterUtils.getHolderItem(convertView, R.id.tv_file);
        try {

            if(!StringUtils.isEmpty(list.get(position).getString("refstatus"))){
                if(list.get(position).getString("refstatus").equals("0")){
                    tv_feedback_questionState.setText("[未回复]");
                    tv_feedback_questionState.setTextColor(ContextCompat.getColor(context,R.color.color_ff00));
                }else{
                    tv_feedback_questionState.setText("[已回]");
                    tv_feedback_questionState.setTextColor(ContextCompat.getColor(context,R.color.fontcolor_f6));
                }
            }else{
                tv_feedback_questionState.setText("[未回复]");
                tv_feedback_questionState.setTextColor(ContextCompat.getColor(context,R.color.color_ff00));
            }

            if (!StringUtils.isEmpty(list.get(position).getString("rid"))) {
                if (list.get(position).getString("rid").equals("0")) {
                    tv_feedback_state.setText("[未读]");
                    tv_feedback_state.setTextColor(ContextCompat.getColor(context, R.color.color_ff00));
                } else {
                    tv_feedback_state.setText("[已读]");
                    tv_feedback_state.setTextColor(ContextCompat.getColor(context, R.color.color_yidu));
                }
            } else {
                tv_feedback_state.setText("");
                tv_feedback_state.setTextColor(ContextCompat.getColor(context, R.color.color_ff00));
            }
            
            if (!StringUtils.isEmpty(list.get(position).getLong("addtime") + "")) {
               tv_feedback_week.setText(TimeUtilsDate.getWeek(list.get(position).getLong("addtime") * 100));
                tv_feedback_data.setText(TimeUtilsDate.timedate3(list.get(position).getLong("addtime")));
            } else {
                tv_feedback_data.setText("");
                tv_feedback_week.setText("");
            }
            if(!StringUtils.isEmpty(list.get(position).getString("show"))){
               tv_feedbak_state2.setText((String)show.getJSONArray(list.get(position).getInt("show")).get(0));
                tv_feedbak_state2.setTextColor(Color.parseColor((String)show.getJSONArray(list.get(position).getInt("show")).get(1)));
            }else{
                tv_feedbak_state2.setText("");
                tv_feedbak_state2.setTextColor(ContextCompat.getColor(context,R.color.color_ff00));
            }
//            if(!StringUtils.isEmpty(list.get(position).getString("type"))){
//               tv_feedback_questionType.setText("["+(String)types.get(list.get(position).getInt("type"))+"]");
//            }else{
//                tv_feedback_questionType.setText("[未知]");
//            }

            if(!StringUtils.isEmpty(list.get(position).getString("type"))){
                tv_feedback_questionType.setText("["+ MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("message_type").getString(list.get(position).getInt("type"))+"]");
            }else{
                tv_feedback_questionType.setText("[未知]");
            }

            if (!StringUtils.isEmpty(list.get(position).getString("content"))) {
                tv_feedback_questionContent.setText(list.get(position).getString("content"));
            } else {
                tv_feedback_questionContent.setText("未编辑");
            }
            if(!StringUtils.isEmpty(list.get(position).getString("pics"))){
                tv_file.setVisibility(View.VISIBLE);
            }else{
                tv_file.setVisibility(View.GONE);
            }
        }catch (Exception e){

        }
            return convertView;
    }
}
