package adapter;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import http.RequestTag;
import utils.StringUtils;
import utils.TimeUtilsDate;
import widget.CircleTransform;

/**
 * Created by zhang on 2018/4/1.
 */

public class FeedDetailAdapter2 extends BaseAdapter {
    private List<JSONObject> list;
    private Context context;
    private final int TIMETYPE = 0;//有日期显示的
    private final int NOTIMETYPE = 1;//没日期显示的
    private Reply reply;

    public FeedDetailAdapter2(List<JSONObject> list, Context context, Reply reply) {
        this.list = list;
        this.context = context;
        this.reply = reply;
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
        try {
            if (list.get(position).getString("uid").equals(MyApplication.getUid())) {
                return TIMETYPE;
            } else {
                return NOTIMETYPE;
            }
        } catch (Exception e) {

        }
        ;
        return TIMETYPE;
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
                convertView = LayoutInflater.from(context).inflate(R.layout.item_replydetail_1, null);
                holder1.tv_feedback_gridFile = (GridView) convertView.findViewById(R.id.tv_feedback_gridFile);
                holder1.tv_feedback_questionContent = (TextView) convertView.findViewById(R.id.tv_feedback_questionContent);
                holder1.tv_feedback_week = (TextView) convertView.findViewById(R.id.tv_feedback_week);
                holder1.tv_feedback_data = (TextView) convertView.findViewById(R.id.tv_feedback_data);
                holder1.tv_file = (TextView) convertView.findViewById(R.id.tv_file);
                holder1.tv_feedback_userName = (TextView) convertView.findViewById(R.id.tv_feedback_userName);
                holder1.img_managePic = (ImageView) convertView.findViewById(R.id.img_managePic);
                holder1.tv_feedback_questionState = (TextView) convertView.findViewById(R.id.tv_feedback_questionState);
                holder1.layout_them = (RelativeLayout) convertView.findViewById(R.id.layout_them);
                holder1.tv_feedback_questionType = (TextView) convertView.findViewById(R.id.tv_feedback_questionType);
                convertView.setTag(holder1);
            } else {
                holder2 = new ViewHolder2();
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.item_replydetail_2, null);
                holder2.tv_feedback_gridFile = (GridView) convertView.findViewById(R.id.tv_feedback_gridFile);
                holder2.feedBakeDetail_tvContent = (TextView) convertView.findViewById(R.id.feedBakeDetail_tvContent);
                holder2.relayout_reply = (RelativeLayout) convertView.findViewById(R.id.relayout_reply);
                holder2.tv_feedback_data = (TextView) convertView.findViewById(R.id.tv_feedback_data);
                holder2.tv_feedback_week = (TextView) convertView.findViewById(R.id.tv_feedback_week);
                holder2.tv_file = (TextView) convertView.findViewById(R.id.tv_file);
                holder2.tv_feedback_userName = (TextView) convertView.findViewById(R.id.tv_feedback_userName);
                holder2.img_managePic = (ImageView) convertView.findViewById(R.id.img_managePic);
                convertView.setTag(holder2);
            }

        } else {
            if (type == 0) {
                holder1 = (ViewHolder1) convertView.getTag();
            } else {
                holder2 = (ViewHolder2) convertView.getTag();
            }
        }
        try {
            if (type == 0) {
//                if(position==0){
//                    holder1.layout_them.setVisibility(View.VISIBLE);
//                }else{
//                    holder1.layout_them.setVisibility(View.GONE);
//                }
                if(position==0){
                    String s = "";
                    if (!StringUtils.isEmpty(list.get(position).getString("refstatus"))) {
                        if (!StringUtils.isEmpty(list.get(position).getString("refstatus")) && !list.get(position).getString("refstatus").equals("0")) {
                            s = " <font color='#0C9C6D'>[已回复]</font>";

                        } else {
                            s = " <font color='#bf3000'>[未回复]</font>";
                        }
                    }
                    if (!StringUtils.isEmpty(list.get(position).getString("content"))) {
                        holder1.tv_feedback_questionContent.setText(Html.fromHtml(list.get(position).getString("content")+s));
                    } else {
                        holder1.tv_feedback_questionContent.setText(Html.fromHtml("未编辑"+s));
                    }
                }else{
                    if (!StringUtils.isEmpty(list.get(position).getString("content"))) {
                        holder1.tv_feedback_questionContent.setText(list.get(position).getString("content"));
                    } else {
                        holder1.tv_feedback_questionContent.setText("未编辑");
                    }
                }
                final ViewHolder1 finalHolder = holder1;
                Picasso.with(context).load(RequestTag.BaseImageUrl + MyApplication.getUserJson().getString("head")).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).transform(new CircleTransform()).into(holder1.img_managePic, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        try {
                            Picasso.with(context).load(RequestTag.BaseImageUrl + MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("head").get(MyApplication.getUserJson().getInt("sex"))).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).transform(new CircleTransform()).into(finalHolder.img_managePic);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                if (!StringUtils.isEmpty(MyApplication.getUserJson().getString("name"))) {
                    holder1.tv_feedback_userName.setText("我");
                } else {
                    holder1.tv_feedback_userName.setText("未设置");
                }
                if (!StringUtils.isEmpty(list.get(position).getString("pics")) && !list.get(position).getString("pics").equals("null")) {
                    holder1.tv_file.setVisibility(View.VISIBLE);
                    //Toast.makeText(context,list.get(position).getString("pics"),Toast.LENGTH_LONG).show();
                    ArrayList<String> lists = new ArrayList<>();
                    for (int i = 0; i < list.get(position).getString("pics").split(",").length; i++) {
                        lists.add(list.get(position).getString("pics").split(",")[i]);
                    }
                    if(!StringUtils.isEmpty(list.get(position).getString("videos"))){
                        for (int i = 0;i<list.get(position).getString("videos").length();i++){
                            lists.add(list.get(position).getString("videos").split(",")[i]);
                        }
                    }
                    holder1.tv_feedback_gridFile.setVisibility(View.VISIBLE);
                    holder1.tv_feedback_gridFile.setAdapter(new FeedDetailGridAdapter(lists, context));

                } else {
                    if(!StringUtils.isEmpty(list.get(position).getString("videos")) && !list.get(position).getString("videos").equals("null")){
                        holder1.tv_file.setVisibility(View.VISIBLE);
                        holder1.tv_feedback_gridFile.setVisibility(View.VISIBLE);
                    }else {
                        holder1.tv_file.setVisibility(View.GONE);
                        holder1.tv_feedback_gridFile.setVisibility(View.GONE);
                    }
                }

//                if (!StringUtils.isEmpty(list.get(position).getString("refstatus"))) {
//                    if (!StringUtils.isEmpty(list.get(position).getString("refstatus")) && !list.get(position).getString("refstatus").equals("0")) {
//                        holder1.tv_feedback_questionState.setText("[已回复]");
//                        holder1.tv_feedback_questionState.setTextColor(Color.parseColor("#0C9C6D"));
//                    } else {
//                        holder1.tv_feedback_questionState.setText("[未回复]");
//                    }
//                }
                if(!StringUtils.isEmpty(list.get(position).getString("type"))){
                    holder1.tv_feedback_questionType.setText("["+MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("message_type").getString(list.get(position).getInt("type"))+"]");
                }else{
                    holder1.tv_feedback_questionType.setText("[其它问题]");
                }
                if (!StringUtils.isEmpty(list.get(position).getLong("addtime") + "")) {
                    holder1.tv_feedback_week.setText(TimeUtilsDate.getWeek(list.get(position).getLong("addtime") * 1000));
                    holder1.tv_feedback_data.setText(TimeUtilsDate.timedate3(list.get(position).getLong("addtime")));
                } else {
                    holder1.tv_feedback_data.setText("");
                    holder1.tv_feedback_week.setText("");
                }
            } else {
                if (!StringUtils.isEmpty(MyApplication.getUserJson().getString("name"))) {
                    holder2.tv_feedback_userName.setText(MyApplication.getUserJson().getString("name"));
                } else {
                    holder2.tv_feedback_userName.setText("未设置");
                }
                if (!StringUtils.isEmpty(list.get(position).getString("pics")) && !list.get(position).getString("pics").equals("null")) {
                    holder2.tv_file.setVisibility(View.VISIBLE);
                    ArrayList<String> lists = new ArrayList<>();
                    for (int i = 0; i < list.get(position).getString("pics").split(",").length; i++) {
                        lists.add(list.get(position).getString("pics").split(",")[i]);
                    }
                    if(!StringUtils.isEmpty(list.get(position).getString("videos"))){
                        for (int i = 0;i<list.get(position).getString("videos").length();i++){
                            lists.add(list.get(position).getString("videos").split(",")[i]);
                        }
                    }
                    holder2.tv_feedback_gridFile.setVisibility(View.VISIBLE);
                    holder2.tv_feedback_gridFile.setAdapter(new FeedDetailGridAdapter(lists, context));

                } else {
                    if(!StringUtils.isEmpty(list.get(position).getString("videos")) && !list.get(position).getString("videos").equals("null")){
                        holder1.tv_file.setVisibility(View.VISIBLE);
                        holder1.tv_feedback_gridFile.setVisibility(View.VISIBLE);
                    }else {
                        holder1.tv_file.setVisibility(View.GONE);
                        holder1.tv_feedback_gridFile.setVisibility(View.GONE);
                    }
//                    holder2.tv_file.setVisibility(View.GONE);
//                    holder2.tv_feedback_gridFile.setVisibility(View.GONE);
                }
//                if (!StringUtils.isEmpty(list.get(position).getString("content"))) {
//                    holder2.feedBakeDetail_tvContent.setText(list.get(position).getString("content"));
//                } else {
//                    holder2.feedBakeDetail_tvContent.setText("未编辑");
//                }

                if (!StringUtils.isEmpty(list.get(position).getString("content"))) {
                    holder2.feedBakeDetail_tvContent.setText(list.get(position).getString("content"));
                } else {
                    holder2.feedBakeDetail_tvContent.setText(Html.fromHtml("未编辑"));
                }
                if (!StringUtils.isEmpty(list.get(position).getLong("addtime") + "")) {
                    holder2.tv_feedback_week.setText(TimeUtilsDate.getWeek(list.get(position).getLong("addtime") * 1000));
                    holder2.tv_feedback_data.setText(TimeUtilsDate.timedate3(list.get(position).getLong("addtime")));
                } else {
                    holder2.tv_feedback_data.setText("");
                    holder2.tv_feedback_week.setText("");
                }
                holder2.relayout_reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setClickable(false);
                        reply.onclick(v);
                    }
                });
            }
            final ViewHolder2 finalHolder1 = holder2;
            Picasso.with(context).load(RequestTag.BaseImageUrl + list.get(position).getString("head")).error(R.mipmap.img_user).transform(new CircleTransform()).into(holder2.img_managePic, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    try {
                        Picasso.with(context).load(RequestTag.BaseImageUrl + MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("head").get(MyApplication.getUserJson().getInt("sex"))).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).transform(new CircleTransform()).into(finalHolder1.img_managePic);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            Log.d("sdsdfeee",e.toString());
        }
        return convertView;
    }

    class ViewHolder1 {
        RelativeLayout layout_them;
        ImageView img_managePic;
        TextView tv_file;//附件
        TextView tv_feedback_userName;
        TextView tv_feedback_state;//读取状态
        TextView tv_feedback_week;//星期几
        TextView tv_feedback_data;//年月日
        TextView tv_feedback_type;//问题类型
        TextView tv_feedbak_state2;//审核状态
        TextView tv_feedback_questionType;//问题类型
        TextView tv_feedback_questionState;//问题回复状态
        TextView tv_feedback_questionTitle;//问题标题
        TextView tv_feedback_questionContent;//问题类型
        GridView tv_feedback_gridFile;//附件
    }

    class ViewHolder2 {
        ImageView img_managePic;
        TextView tv_feedback_userName;
        TextView tv_file;//附件
        TextView myorder_tv_orderState;
        TextView tv_feedback_week;
        TextView tv_feedback_data;//年月日 时分
        TextView feedBakeDetail_tvContent;//回复内容
        RelativeLayout relayout_reply;//回复
        GridView tv_feedback_gridFile;//附件
    }

    public interface Reply {
        void onclick(View view);
    }

}
