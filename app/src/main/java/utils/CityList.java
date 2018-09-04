package utils;

import android.content.Context;

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
 * Created by zhang on 2018/4/12.
 * 获取城市列表
 */

public class CityList {

    private Context context;
    private Result result;
    private ResultAddress resultAddress;
    private ACache aCache;

    public CityList(Context context, Result result) {
        this.context = context;
        this.result = result;
        aCache = ACache.get(MyApplication.getAppContext());
    }

    public CityList(Context context, ResultAddress resultAddress) {
        this.context = context;
        this.resultAddress = resultAddress;
        aCache = ACache.get(MyApplication.getAppContext());
    }

    public void getCityList(final String parentId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            if (!StringUtils.isEmpty(parentId)) {
                jsonObject.put("parent_id", parentId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(context, RequestTag.BaseUrl + RequestTag.GETCITYLIST, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if (response.getInt("status") != 0) {
                        result.onFailure();
                    } else {
                        if (StringUtils.isEmpty(parentId)) {
                            //保存省的数据再本地
                            if (aCache.getAsJSONArray("province") == null) {
                                aCache.put("province", response.getJSONArray("data"), ACache.TIME_DAY * 30);
                            }
                        } else {
                            //保存市区的数据,当parentid有值的时候，当前市区都已返回
                            if (aCache.getAsJSONArray(parentId) == null) {
                                aCache.put(parentId, response.getJSONArray("data"), ACache.TIME_DAY * 3);
                            }
                        }
                        result.onSuccess(response.getJSONArray("data"));

                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                result.onFailure();
            }
        });
    }


    public void getCityList_detail(final String parentId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            if (!StringUtils.isEmpty(parentId)) {
                jsonObject.put("id", parentId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(context, RequestTag.BaseUrl + RequestTag.GETCITY_ADDRESS, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    if (response.getInt("status") != 0) {
                        resultAddress.onFailure();
                    } else {
                        StringBuilder address = new StringBuilder();
                        for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                            address.append(response.getJSONArray("data").getJSONObject(i).getString("title"));
                        }
                        if (!StringUtils.isEmpty(address.toString())) {
                            SPUtils.setSharedStringData(context, parentId, address.toString());
                            resultAddress.onSuccess(address.toString());
                        } else {
                            resultAddress.onSuccess("请选择地区");
                        }


                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
         resultAddress.onFailure();
            }
        });
    }


    public interface Result {
        JSONArray onSuccess(JSONArray jsonArray);

        void onFailure();
    }

    public interface ResultAddress {
        void onSuccess(String address);

        void onFailure();
    }

}
