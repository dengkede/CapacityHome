package utils;

import android.content.Context;
import android.widget.Toast;

import com.example.administrator.capacityhome.MyApplication;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import http.RequestTag;

/**
 * Created by Administrator on 2018/4/16.
 * 图片上传
 */

public class ImagePost {
    private Result result;
    private Context context;


    public ImagePost(Result result, Context context) {
        this.result = result;
        this.context = context;
    }


    /**
     * 上传图片
     */
    public void psotDoubt(String img, JSONArray img2) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());

            if (img != null) {
                if (!StringUtils.isEmpty(img)) {
                    jsonObject.put("img", img);
                } else {
                    jsonObject.put("img", img2);
                }
            } else {
                jsonObject.put("img", "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(context, RequestTag.BaseUrl + RequestTag.POSTIMAGE, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
//                try {
//                    String[] pics = (String[]) response.get("url");
//                    for (int i = 0;i<pics.length;i++){
//                        pics[i] = pics[i].replace("\\","");
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                result.success(response);
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                result.failure(error_msg);
            }
        });
    }

    public interface Result {
        void success(JSONObject jsonObject);

        void failure(String msg);
    }


}
