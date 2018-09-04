package adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.message.MessageActivity;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import http.RequestTag;
import utils.ACache;
import utils.AdapterUtils;
import utils.ColumnUtils;
import utils.Md5;
import utils.StringUtils;
import utils.TimeUtilsDate;

/**
 * Created by Administrator on 2018/4/18.
 */

public class MessageAdapter extends BaseAdapter {
    private Context context;
    private List<JSONObject> list;
    private int index = -1;//当前展开项目
    private List<JSONObject> list_meeageType;
    private ColumnUtils columnUtils;
    private ACache aCache;

    public MessageAdapter(Context context, List<JSONObject> list, List<JSONObject> list_meeageType, ACache aCache) {
        this.context = context;
        this.list = list;
        this.list_meeageType = list_meeageType;
        this.aCache = aCache;
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
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.layout_message_type2, null);
        }
        final WebView tv_content = AdapterUtils.getHolderItem(convertView, R.id.tv_content);
        TextView message_tvMessageType = AdapterUtils.getHolderItem(convertView, R.id.message_tvMessageType);
        View tv_Noread = AdapterUtils.getHolderItem(convertView, R.id.tv_Noread);
        TextView message_tvMessageContent = AdapterUtils.getHolderItem(convertView, R.id.message_tvMessageContent);
        TextView message_tvMessageTime = AdapterUtils.getHolderItem(convertView, R.id.message_tvMessageTime);
        try {
            if (list.get(position).getInt("read") == 0) {
                // 0=未读 大于0表示已读
                tv_Noread.setVisibility(View.VISIBLE);
            } else {
                tv_Noread.setVisibility(View.GONE);
            }
            if (!StringUtils.isEmpty(list.get(position).getString("title"))) {
                message_tvMessageContent.setText(list.get(position).getString("title"));
            } else {
                message_tvMessageContent.setText("暂未编辑");
            }
            if (!StringUtils.isEmpty(list.get(position).getString("addtime"))) {
                message_tvMessageTime.setText(TimeUtilsDate.timedate3(list.get(position).getLong("addtime")));
            } else {

            }
            WebSettings settings = tv_content.getSettings();
            settings.setSupportZoom(true);
            settings.setDefaultFontSize(13);
            if (!StringUtils.isEmpty(list.get(position).getString("content"))) {
//                SpannableStringBuilder span = new SpannableStringBuilder("\t\t\t\t" + Html.fromHtml(htmlReplace(list.get(position).getString("content"))));
//                span.setSpan(new ForegroundColorSpan(Color.parseColor("#00000000")), 0, 2,
//                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                //tv_content.setText(Html.fromHtml(span.toString()));
                 String CSS_STYLE ="<style>* {line-height:24px;} p {color:#777777;} img {max-width:100%; height:auto;}}</style>";
                tv_content.loadDataWithBaseURL(null, CSS_STYLE+list.get(position).getString("content"), "text/html" , "utf-8", null);

            } else {
                tv_content.loadDataWithBaseURL(null, "未编辑", "text/html" , "utf-8", null);
            }
            if (index == position) {
                tv_content.setVisibility(View.VISIBLE);
            } else {
                tv_content.setVisibility(View.GONE);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tv_content.getVisibility() == View.GONE) {
                        tv_content.setVisibility(View.VISIBLE);
                        index = position;
                        try {
                            if (list.get(position).getInt("read") == 0) {
                                messageRead(position, list.get(position).getString("id"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        tv_content.setVisibility(View.GONE);
                        index = -1;
                    }
                }
            });
            if (list_meeageType.size() > 0 && !StringUtils.isEmpty(list.get(position).getString("type"))) {
               for (int i = 0;i<list_meeageType.size();i++){
                       if (list.get(position).getInt("type") == list_meeageType.get(i).getInt("id")) {
                           message_tvMessageType.setText("[ "+list_meeageType.get(i).getString("title")+" ]");
                           break;
                       }
               }
//                getType(message_tvMessageType, list.get(position).getInt("type") + "");
//                message_tvMessageType.setText(list_meeageType.get(list.get(position).getInt("type")).getString("title"));
            } else {
                message_tvMessageType.setText("[ 其它问题 ]");
            }

        } catch (Exception e) {
            // Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
        }
        return convertView;
    }

    /**
     * (特殊字符替换)
     *
     * @return String    返回类型
     */
    private String htmlReplace(String str) {
        str = str.replace("&ldquo;", "“");
        str = str.replace("&quot;", "“");
        str = str.replace("&amp;", "&");
        str = str.replace("&lt;", "<");
        str = str.replace("&gt;", ">");
        str = str.replace("&rdquo;", "”");
        str = str.replace("&nbsp;", " ");
        str = str.replace("&", "&amp;");
        str = str.replace("&#39;", "'");
        str = str.replace("&rsquo;", "’");
        str = str.replace("&mdash;", "—");
        str = str.replace("&ndash;", "–");
        return str;
    }

    /**
     * 消息已读
     */
    private void messageRead(final int position, String id) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid", MyApplication.getUid());
            jsonObject.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(context, RequestTag.BaseUrl + RequestTag.MESSAGE_READ, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if (response.getInt("status") == 0) {
                        list.get(position).put("read", 1);
                        MessageAdapter.this.notifyDataSetChanged();
                        MyApplication.no_rendMessage = MyApplication.no_rendMessage-1;
                    }
                    Toast.makeText(context, response.getString("msg").toString(), Toast.LENGTH_SHORT).show();

                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {

            }
        });
    }

    /**
     * 获取消息类型
     */
    private void getType(final TextView tv, final String parentId) {
        if (columnUtils == null) {
            columnUtils = new ColumnUtils(context, new ColumnUtils.Result() {
                @Override
                public void onSuccess(String title, JSONArray jsonArray) {

                    if (jsonArray != null) {
                        aCache.put("messageType_child" + parentId, jsonArray, ACache.TIME_DAY * 3);
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                tv.setText(jsonArray.getJSONObject(0).getString("title"));
                                break;

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(String msg) {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
        aCache.remove("messageType_child");
        if (aCache.getAsJSONArray("messageType_child" + parentId) == null) {
            columnUtils.getColumn(parentId, "memberMessage");
        } else {
            try {
                for (int i = 0; i < aCache.getAsJSONArray("messageType_child" + parentId).length(); i++) {

                    tv.setText(aCache.getAsJSONArray("messageType_child" + parentId).getJSONObject(0).getString("title"));
                    break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
