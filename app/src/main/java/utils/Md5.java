package utils;

import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import http.RequestTag;

/**
 * Created by Administrator on 2018/3/30.
 */

public class Md5 {
    /**
     * md5加密
     *
     * @param data
     * @return
     */
    public static String md5(String data, String time) {
        //拼接appid+key+time+data   098f6bcd4621d373cade4e832627b4f6
        String content = "appid=" + RequestTag.APPID + "&key=" + RequestTag.KEY + "&time=" + time + "&data=" + data;
        //String content ="appid=20180601"+"&key=098f6bcd4621d373cade4e832627b4f6"+"&time="+time+"&data="+data;
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(content.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString().toUpperCase();
    }

    /**
     * md5加密
     *
     * @return
     */
    public static String md5_socket(String sendMessage) {
        String content = sendMessage;
        //String content ="appid=20180601"+"&key=098f6bcd4621d373cade4e832627b4f6"+"&time="+time+"&data="+data;
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(content.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString().toUpperCase();
    }
//app=1&appid=&device_id=pwd=&uid=key=

    /**men suo manage
     * @return
     */

    public static String md5_door(String pwd, String device_id,String uid,String key) {
        //拼接appid+key+time+data   098f6bcd4621d373cade4e832627b4f6
        String content = "app=1"+"&appid=" + RequestTag.APPID + "&device_id=" + device_id + "&pwd=" + pwd + "&uid=" + uid+ "&key=" + key;
        //String content ="appid=20180601"+"&key=098f6bcd4621d373cade4e832627b4f6"+"&time="+time+"&data="+data;
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(content.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString().toUpperCase();
    }

}
