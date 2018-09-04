package utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 字符串相关方法
 * Created by tsy on 16/8/15.
 */
public class StringUtils {

    /**
     * 是否为空
     * @param str 字符串
     * @return true 空 false 非空
     */
    public static Boolean isEmpty(String str) {
        if(str == null || str.equals("")) {
            return true;
        }

        return false;
    }
//http://www.scyoue.com/QrcodeArk/qrscan/staff.html?client=staffQrCan&type=2&ark_id=1&sign=0658F8DBD159962866AB3D4D0903A54F
//放件
    public static JSONObject submitString(String path){
        JSONObject jsonObject = new JSONObject();
        if(path.contains("?")) {
            String ss = path.split("[?]")[1];
            try {
                jsonObject.put("client", ss.split("&")[0].replace("client=",""));
                jsonObject.put("type", ss.split("&")[1].replace("type=",""));
                jsonObject.put("ark_id", ss.split("&")[2].replace("ark_id=",""));
            }catch (Exception e){

            }
        }
        return jsonObject;
    }

    /**
     * 获取充值
     * @param path
     * @return
     */
    public static JSONObject submitString_pay(String path){
        JSONObject jsonObject = new JSONObject();
        if(path.contains("?")) {
            String ss = path.split("[?]")[1];
            try {
                jsonObject.put("client", ss.split("&")[0].replace("client=",""));
                jsonObject.put("card_id", ss.split("&")[1].replace("card_id=",""));
                jsonObject.put("password", ss.split("&")[2].replace("password=",""));
                jsonObject.put("key", ss.split("&")[3].replace("key=",""));
            }catch (Exception e){

            }
        }
        return jsonObject;
    }
    /**
     * 获取人脸识别信息
     * @param path
     * @return
     */
    public static String submitString_face(String path){
        JSONObject jsonObject = new JSONObject();
        if(path.contains("?")) {
            String ss = path.split("[?]")[1];
            try {
                jsonObject.put("client", ss.split("&")[0].replace("client=",""));
                jsonObject.put("msg", ss.split("&")[1].replace("msg=",""));
            }catch (Exception e){

            }
        }
        try {
            return jsonObject.getString("msg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "正在录入人脸信息";
    }

    /**
     * 判断手机是否安装微信客户端
     *
     * @param context
     * @return
     */
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
