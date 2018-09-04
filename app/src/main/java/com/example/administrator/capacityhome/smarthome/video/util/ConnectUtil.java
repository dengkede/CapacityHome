package com.example.administrator.capacityhome.smarthome.video.util;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


/**
 * Created by Administrator on 2016/11/12.
 */
public class ConnectUtil {
    public final static int GET = 0x234;
    public final static int POST = 0x235;
    //    private static String baseUri = "http://www.138tg.com:8080";
    private static String baseUri = "http://120.76.250.104:8080";

    public static void request(final String uri, final Map<String, Object> requestParams, final OnRequestListener listener) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                return connect(uri, requestParams);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (!TextUtils.isEmpty(s)) {
                    listener.onRequestSuccess(s);
                } else {
                    Log.i("requestError", "error" + s);
                    listener.onFail();
                }
            }
        }.execute();
    }

    private static String connect(String uri, Map<String, Object> requestParams) {
        try {

            URL url = new URL(baseUri + uri);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            // 传递的数据
            JSONObject jsonObject = new JSONObject(requestParams);
            String data = jsonObject.toString();
            Log.i("requestParams", data);
//        String data = "username=" + URLEncoder.encode(userName, "UTF-8")
//                + "&userpass=" + URLEncoder.encode(userPass, "UTF-8");
            // 设置请求的头
            urlConnection.setRequestProperty("Connection", "keep-alive");
            // 设置请求的头
            urlConnection.setRequestProperty("Content-Type",
                    "application/json;charset=UTF-8");
            urlConnection.setRequestProperty("Charsert", "UTF-8");
            // 设置请求的头
            urlConnection.setRequestProperty("Content-Length",
                    String.valueOf(data.getBytes().length));
            // 设置请求的头
            urlConnection
                    .setRequestProperty("User-Agent",
                            "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");

            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            //setDoInput的默认值就是true
            //获取输出流
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                baos.close();
                // 返回字符串
                final String result = new String(baos.toByteArray());
                return result;
            }
            return "";

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
