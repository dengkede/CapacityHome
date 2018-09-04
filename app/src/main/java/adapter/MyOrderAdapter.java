package adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;

import org.json.JSONObject;

import java.util.List;

import utils.StringUtils;
import utils.TimeUtilsDate;

/**
 * Created by zhang on 2018/4/1.
 */

public class MyOrderAdapter extends BaseAdapter {
    private List<JSONObject> list;
    private Context context;
    private final int TIMETYPE = 0;//有日期显示的
    private final int NOTIMETYPE = 1;//没日期显示的

    public MyOrderAdapter(List<JSONObject> list, Context context) {
        this.list = list;
        this.context = context;
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
    public int getItemViewType(int position) {
//            try{
//                if(TimeUtilsDate.timedate4(list.get(position).getLong("addtime")).equals(TimeUtilsDate.timedate4(list.get(position-1).getLong("addtime")))){
//                    return NOTIMETYPE;
//                }else{
//                    return TIMETYPE;
//                }
//            }catch (Exception e){
//
//            }
        try {
            if (list.get(position).getInt("status") >= 0 && list.get(position).getInt("status") < 2 &&
                    list.get(position).getInt("additional") == 0) {
                return TIMETYPE;
            } else {
                return NOTIMETYPE;
            }
        } catch (Exception e) {

        }

        return NOTIMETYPE;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        ViewHolder1 holder1 = null;
        ViewHolder2 holder2 = null;
        if (convertView == null) {
            if (type == 0) {
                holder1 = new ViewHolder1();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_myorder, null);
                holder1.myorder_tv_orderState = (TextView) convertView.findViewById(R.id.myorder_tv_orderState);
                holder1.myorder_tv_content = (TextView) convertView.findViewById(R.id.myorder_tv_content);
                holder1.myorder_tv_note = (TextView) convertView.findViewById(R.id.myorder_tv_note);
                holder1.myorder_tv_time = (TextView) convertView.findViewById(R.id.myorder_tv_time);
                holder1.myorder_tv_state = (TextView) convertView.findViewById(R.id.myorder_tv_state);
                holder1.myorder_tv_money = (TextView) convertView.findViewById(R.id.myorder_tv_money);
                holder1.tv_yuan = (TextView) convertView.findViewById(R.id.tv_yuan);
                holder1.myorder_tv_orderState2 = (TextView) convertView.findViewById(R.id.myorder_tv_orderState2);
                holder1.tv_orderNumer = (TextView) convertView.findViewById(R.id.tv_orderNumer);
                holder1.tv_doubtNumber = (TextView) convertView.findViewById(R.id.tv_doubtNumber);
                holder1.tv_doubt = (TextView) convertView.findViewById(R.id.tv_doubt);
                convertView.setTag(holder1);
            } else {
                holder2 = new ViewHolder2();
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.item_myorder2, null);
                holder2.myorder_tv_orderState = (TextView) convertView.findViewById(R.id.myorder_tv_orderState);
                holder2.myorder_tv_content = (TextView) convertView.findViewById(R.id.myorder_tv_content);
                holder2.myorder_tv_note = (TextView) convertView.findViewById(R.id.myorder_tv_note);
                holder2.myorder_tv_time = (TextView) convertView.findViewById(R.id.myorder_tv_time);
                holder2.myorder_tv_state = (TextView) convertView.findViewById(R.id.myorder_tv_state);
                holder2.myorder_tv_money = (TextView) convertView.findViewById(R.id.myorder_tv_money);
                holder2.tv_yuan = (TextView) convertView.findViewById(R.id.tv_yuan);
                holder2.tv_orderNumer = (TextView) convertView.findViewById(R.id.tv_orderNumer);
                holder2.myorder_tv_orderState2 = (TextView) convertView.findViewById(R.id.myorder_tv_orderState2);
                holder2.tv_doubtNumber = (TextView) convertView.findViewById(R.id.tv_doubtNumber);
                holder2.tv_doubt = (TextView) convertView.findViewById(R.id.tv_doubt);
                convertView.setTag(holder2);
            }

        } else {
            if (type == 0) {
                holder1 = (ViewHolder1) convertView.getTag();
            } else {
                holder2 = (ViewHolder2) convertView.getTag();
            }
        }
        if (position % 2 == 0) {
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.fontcolor_f2));
        } else {
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }
        try {
            if (type == 0) {
                if (!StringUtils.isEmpty(MyApplication.getConfigrationJson().getJSONObject("xorderstatus").getString(list.get(position).getString("status")))) {
                    holder1.myorder_tv_orderState.setText(MyApplication.getConfigrationJson().getJSONObject("xorderstatus").getString(list.get(position).getString("status")));
                    holder1.myorder_tv_orderState.setTextColor(Color.parseColor(MyApplication.getConfigrationJson().getJSONObject("other").getJSONObject("xieyiorder_color").getString(list.get(position).getString("status"))));
                } else {
                    holder1.myorder_tv_orderState.setText("获取状态中");
                    holder1.myorder_tv_orderState.setTextColor(ContextCompat.getColor(context, R.color.fontcolor_f9));
                }
                if (list.get(position).getInt("status") == 6) {
                    holder1.myorder_tv_orderState2.setVisibility(View.VISIBLE);
                    if (list.get(position).getInt("open_box") == 1) {
                              //未取件
                        holder1.myorder_tv_orderState2.setText("未取件");
                        holder1.myorder_tv_orderState2.setTextColor(ContextCompat.getColor(context, R.color.fontcolor_yellow));
                    } else {
                        //已取件
                        holder1.myorder_tv_orderState2.setText("已取件");
                        holder1.myorder_tv_orderState2.setTextColor(Color.parseColor("#3686f7"));
                    }
                } else {
                    holder1.myorder_tv_orderState2.setVisibility(View.GONE);
                    //没取件
                    holder1.myorder_tv_orderState2.setText("未取件");
                    holder1.myorder_tv_orderState2.setTextColor(ContextCompat.getColor(context, R.color.fontcolor_yellow));
                }
                if(list.get(position).getInt("counts")==0){
                    holder1.tv_doubt.setVisibility(View.GONE);
                    holder1.tv_doubtNumber.setVisibility(View.GONE);
                }else{
                    holder1.tv_doubtNumber.setVisibility(View.VISIBLE);
                    holder1.tv_doubt.setVisibility(View.VISIBLE);
                    holder1.tv_doubtNumber.setText(Html.fromHtml("(<b>"+list.get(position).getInt("counts")+"</b>)"));
                }
                if (list.get(position).getInt("status") >= 0 && list.get(position).getInt("status") < 2 &&
                        list.get(position).getInt("additional") == 0) {
                    holder1.myorder_tv_state.setVisibility(View.GONE);
                    holder1.myorder_tv_state.setText("可加急");
                    holder1.myorder_tv_state.setTextColor(Color.parseColor("#666666"));
                    holder1.myorder_tv_state.setBackground(ContextCompat.getDrawable(context, R.drawable.home_rect_sloid_state));
                    //显示可加急
                } else if (list.get(position).getInt("additional") == 1) {
                    //处于加急状态
                    holder1.myorder_tv_state.setVisibility(View.GONE);
                    holder1.myorder_tv_state.setText("[ 已加急 ]");
                    //  holder1.myorder_tv_state.setText(MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("additional").getJSONArray(list.get(position).getInt("additional")).getString(0));
                    holder1.myorder_tv_state.setTextColor(Color.parseColor("#bf3000"));
                    GradientDrawable background = (GradientDrawable) holder1.myorder_tv_state.getBackground();
                    background.setColor(Color.parseColor("#00000000"));
                    // holder1.myorder_tv_state.setBackground(ContextCompat.getDrawable(context,R.drawable.home_rect_sloid_state2));
                } else {
                    //不显示
                    holder1.myorder_tv_state.setVisibility(View.GONE);
                }
                if (!StringUtils.isEmpty(list.get(position).getString("number"))) {
                    holder1.tv_orderNumer.setText("订单：" + list.get(position).getString("number"));
                } else {
                    holder1.tv_orderNumer.setText("订单：" + "数据异常");
                }
                if (!StringUtils.isEmpty(list.get(position).getString("addtime"))) {
                    holder1.myorder_tv_time.setText(TimeUtilsDate.timedate_week_hour(list.get(position).getLong("addtime")));
                } else {
                    holder1.myorder_tv_time.setText("未设置");
                }
                if (!StringUtils.isEmpty(list.get(position).getString("shop_remark"))) {
                    holder1.myorder_tv_note.setText("备注：" + list.get(position).getString("shop_remark"));
                } else {
                    if (list.get(position).getInt("status") == 6) {
                        //已完成
                        holder1.myorder_tv_note.setText("备注：" + "商家未备注");
                    } else {
                        if (list.get(position).getDouble("money") == 0) {
                            holder1.myorder_tv_note.setText("备注：" + "待取货后商家定价");
                        } else {
                            holder1.myorder_tv_note.setText("备注：" + "商家已定价未填写备注");
                        }
                    }
                }
                if (!StringUtils.isEmpty(list.get(position).getString("money")) && list.get(position).getLong("money") != 0) {
                    holder1.myorder_tv_money.setText(Html.fromHtml("<b>" + list.get(position).getLong("money") / 1000 + "</b>"));
                    holder1.myorder_tv_money.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.x32));
                    holder1.myorder_tv_money.setTextColor(ContextCompat.getColor(context, R.color.fontcolor_yellow));
                    holder1.tv_yuan.setVisibility(View.VISIBLE);
                } else {
                    holder1.myorder_tv_money.setText("待定价");
                    holder1.myorder_tv_money.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.x26));
                    holder1.tv_yuan.setVisibility(View.GONE);
                    holder1.myorder_tv_money.setTextColor(ContextCompat.getColor(context, R.color.fontcolor_f9));
                }
                if (!StringUtils.isEmpty(list.get(position).getString("ark_name"))) {
                    holder1.myorder_tv_content.setText("使用" + list.get(position).getString("ark_name") + list.get(position).getString("ark_box_number") + "号柜子");
                } else {
                    holder1.myorder_tv_content.setText("未命名");
                }
            } else {

                if (list.get(position).getInt("status") >= 0 && list.get(position).getInt("status") < 2 &&
                        list.get(position).getInt("additional") == 0) {
                    holder2.myorder_tv_state.setVisibility(View.GONE);
                    holder2.myorder_tv_state.setText("可加急");
                    holder2.myorder_tv_state.setTextColor(Color.parseColor("#666666"));
                    holder2.myorder_tv_state.setBackground(ContextCompat.getDrawable(context, R.drawable.home_rect_sloid_state));
                    //显示可加急
                } else if (list.get(position).getInt("additional") == 1) {
                    //处于加急状态
                    holder2.myorder_tv_state.setVisibility(View.GONE);
                    holder2.myorder_tv_state.setText("[ 已加急 ]");
                    //  holder1.myorder_tv_state.setText(MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("additional").getJSONArray(list.get(position).getInt("additional")).getString(0));
                    holder2.myorder_tv_state.setTextColor(Color.parseColor("#bf3000"));
//                    GradientDrawable background = (GradientDrawable) holder2.myorder_tv_state.getBackground();
//                    background.setColor(Color.parseColor("#00000000"));
                    holder2.myorder_tv_state.setBackground(null);
                    // holder1.myorder_tv_state.setBackground(ContextCompat.getDrawable(context,R.drawable.home_rect_sloid_state2));
                } else {
                    //不显示
                    holder2.myorder_tv_state.setVisibility(View.GONE);
                }
                if(list.get(position).getInt("counts")==0){
                    holder2.tv_doubt.setVisibility(View.GONE);
                    holder2.tv_doubtNumber.setVisibility(View.GONE);
                }else{
                    holder2.tv_doubtNumber.setVisibility(View.VISIBLE);
                    holder2.tv_doubt.setVisibility(View.VISIBLE);
                    holder2.tv_doubtNumber.setText(Html.fromHtml("(<b>"+list.get(position).getInt("counts")+"</b>)"));
                }
                if (list.get(position).getInt("status") == 6) {
                    holder2.myorder_tv_orderState2.setVisibility(View.VISIBLE);
                    if (list.get(position).getInt("open_box") == 1) {
                        //未取件
                        holder2.myorder_tv_orderState2.setText("未取件");
                        holder2.myorder_tv_orderState2.setTextColor(ContextCompat.getColor(context, R.color.fontcolor_yellow));
                    } else {
                        //已取件
                        holder2.myorder_tv_orderState2.setText("已取件");
                        holder2.myorder_tv_orderState2.setTextColor(Color.parseColor("#3686f7"));
                    }
                } else {
                    holder2.myorder_tv_orderState2.setVisibility(View.GONE);
                    //没取件
                    holder2.myorder_tv_orderState2.setText("未取件");
                    holder2.myorder_tv_orderState2.setTextColor(ContextCompat.getColor(context, R.color.fontcolor_yellow));
                }
                if (!StringUtils.isEmpty(MyApplication.getConfigrationJson().getJSONObject("xorderstatus").getString(list.get(position).getString("status")))) {
                    holder2.myorder_tv_orderState.setText(MyApplication.getConfigrationJson().getJSONObject("xorderstatus").getString(list.get(position).getString("status")));
                    holder2.myorder_tv_orderState.setTextColor(Color.parseColor(MyApplication.getConfigrationJson().getJSONObject("other").getJSONObject("xieyiorder_color").getString(list.get(position).getString("status"))));
                } else {
                    holder2.myorder_tv_orderState.setText("获取状态中");
                    holder2.myorder_tv_orderState.setTextColor(ContextCompat.getColor(context, R.color.fontcolor_f9));
                }

                if (!StringUtils.isEmpty(list.get(position).getString("shop_remark"))) {
                    holder2.myorder_tv_note.setText("备注：" + list.get(position).getString("shop_remark"));
                } else {
                    if (list.get(position).getInt("status") == 6) {
                        //已完成
                        holder2.myorder_tv_note.setText("备注：" + "商家未备注");
                    } else {
                        if (list.get(position).getDouble("money") == 0) {
                            holder2.myorder_tv_note.setText("备注：" + "待取货后商家定价");
                        } else {
                            holder2.myorder_tv_note.setText("备注：" + "商家已定价未填写备注");
                        }
                    }
                }
                if (!StringUtils.isEmpty(list.get(position).getString("number"))) {
                    holder2.tv_orderNumer.setText("订单：" + list.get(position).getString("number"));
                } else {
                    holder2.tv_orderNumer.setText("订单：" + "数据异常");
                }
                if (list.get(position).getDouble("money") != 0) {
                    holder2.tv_yuan.setVisibility(View.VISIBLE);
                    holder2.myorder_tv_money.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.x32));
                    holder2.myorder_tv_money.setText(Html.fromHtml("<b>" + list.get(position).getLong("money") / 1000 + "</b>"));
                    holder2.myorder_tv_money.setTextColor(ContextCompat.getColor(context, R.color.fontcolor_yellow));
                } else {
                    holder2.myorder_tv_money.setText(Html.fromHtml("待定价"));
                    holder2.tv_yuan.setVisibility(View.GONE);
                    holder2.myorder_tv_money.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.x26));
                    holder2.myorder_tv_money.setTextColor(ContextCompat.getColor(context, R.color.fontcolor_f9));
                }
                if (!StringUtils.isEmpty(list.get(position).getString("addtime"))) {
                    holder2.myorder_tv_time.setText(TimeUtilsDate.timedate_week_hour(list.get(position).getLong("addtime")));
                } else {
                    holder2.myorder_tv_time.setText("未设置");
                }
                if (!StringUtils.isEmpty(list.get(position).getString("ark_name"))) {
                    holder2.myorder_tv_content.setText("使用" + list.get(position).getString("ark_name") + list.get(position).getString("ark_box_number") + "号柜子");
                } else {
                    holder2.myorder_tv_content.setText("未命名");
                }


            }
        } catch (Exception e) {
            Log.d("e.e.e.e", e.toString());
        }
        return convertView;
    }

    class ViewHolder1 {
        TextView myorder_tv_state;//是否加急
        TextView myorder_tv_orderState;
        TextView myorder_tv_time;//时间
        TextView myorder_tv_content;//内容 使用。。。。衣柜
        TextView myorder_tv_note;//备注
        TextView myorder_tv_money;//钱
        TextView tv_yuan;
        TextView myorder_tv_orderState2;
        TextView tv_orderNumer;//订单编号
        TextView tv_doubtNumber;
        TextView tv_doubt;
    }

    class ViewHolder2 {
        TextView myorder_tv_time;//时间
        TextView myorder_tv_money;//钱
        TextView myorder_tv_orderState;
        TextView myorder_tv_content;
        TextView myorder_tv_state;
        TextView myorder_tv_note;
        TextView tv_yuan;//单位
        TextView tv_orderNumer;//订单编号
        TextView myorder_tv_orderState2;
        TextView tv_doubtNumber;
        TextView tv_doubt;
    }


}
