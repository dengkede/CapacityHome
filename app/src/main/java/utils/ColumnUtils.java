package utils;

import android.content.Context;
import android.widget.Toast;

import com.example.administrator.capacityhome.MyApplication;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import http.RequestTag;

/**
 * Created by Administrator on 2018/4/18.
 * 公用获取栏目相关接口
 */

public class ColumnUtils {
    private Context context;
    private Result result;

    public ColumnUtils(Context context, Result result) {
        this.context = context;
        this.result = result;
    }

    public void getColumn(final String parentId, String mark) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            if (!StringUtils.isEmpty(parentId)) {
                jsonObject.put("parent_id", parentId);//非必选 栏目ID（必须大于零，当parent_id为真时，则输入该ID下的子栏目
            }
            jsonObject.put("mark", mark);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(context, RequestTag.BaseUrl + RequestTag.COLUMN, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if (response.getInt("status") != 0) {
                        result.onFailure(response.getString("msg"));
                    } else {
                        if (StringUtils.isEmpty(parentId)) {
                            result.onSuccess(response.getJSONObject("data").getString("title"), response.getJSONObject("data").getJSONArray("data"));
                        } else {
                            result.onSuccess("", response.getJSONArray("data"));
                        }

                    }
                } catch (Exception e) {
                    Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                result.onFailure(error_msg);
            }
        });
    }

    public void getPayList(String type) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("uid",MyApplication.getUid());
            if (!StringUtils.isEmpty(type)) {
                jsonObject.put("type", type);//可选
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(context, RequestTag.BaseUrl + RequestTag.PAY_LIST, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if (response.getInt("status") != 0) {
                        result.onFailure(response.getString("msg"));
                    } else {

                        result.onSuccess("", response.getJSONArray("data"));

                    }
                } catch (Exception e) {
                     e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                result.onFailure(error_msg);
            }
        });
    }

    public interface Result {
        void onSuccess(String title, JSONArray jsonArray);

        void onFailure(String msg);
    }


}
