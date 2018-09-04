package utils;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.administrator.capacityhome.MyApplication;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import http.RequestTag;

/**
 * 获取相关协议内容
 * Created by zhang on 2018/4/21.
 */

public class GetAgrement {
    private Context context;
    private Agreement_Dialog agreement_dialog;//协议弹框
    private Agreement_Dialog.Onclick agreement_onclick;
    private Agreement_Dialog_Sigle agreement_dialog_sigle;//协议弹框
    private Agreement_Dialog_Sigle.Onclick agreement_onclick_sigle;
    public GetAgrement(Context context) {
        this.context = context;
    }
    public GetAgrement(Context context,Agreement_Dialog.Onclick onclick) {
        this.context = context;
        this.agreement_onclick = onclick;
    }
    public GetAgrement(Context context,Agreement_Dialog_Sigle.Onclick onclick) {
        this.context = context;
        this.agreement_onclick_sigle = onclick;
    }
//    /**
//     * 获取相关协议内容
//     */
//    public void getAggrement(String marke, final String btn_name) {
//        if (agreement_dialog == null) {
//            Map<String, String> params = new HashMap<String, String>();
//            params.put("appid", RequestTag.APPID);
//            params.put("key", RequestTag.KEY);
//            String timestamp = System.currentTimeMillis() + "";
//            params.put("time", timestamp + "");
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("device_id", MyApplication.getDeviceId());
//                jsonObject.put("mark", marke);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            params.put("data", jsonObject.toString());
//            String sign = Md5.md5(jsonObject.toString(), timestamp);
//            params.put("sign", sign);
//            MyOkHttp.get().post(context, RequestTag.BaseUrl + RequestTag.ONE_ARTICAL, params, new JsonResponseHandler() {
//                @Override
//                public void onSuccess(int statusCode, JSONObject response) {
//                    try {
//                        if (response.getInt("status") != 0) {
//                            Toast.makeText(context, "暂时没有编辑", Toast.LENGTH_LONG).show();
//                        } else {
//                            if (response.getJSONObject("data") == null) {
//                                Toast.makeText(context, "暂时没有编辑", Toast.LENGTH_LONG).show();
//                            } else {
//                                popIsNull(response.getJSONObject("data").getString("content"), response.getJSONObject("data").getString("title"), btn_name);
//                            }
//                        }
//
//                    } catch (Exception e) {
//                    }
//
//                }
//
//                @Override
//                public void onFailure(int statusCode, String error_msg) {
//                    Toast.makeText(context, "请检查网络是否异常", Toast.LENGTH_LONG).show();
//                }
//            });
//        } else {
//            agreement_dialog.show();
//        }
//    }

    /**
     * 获取相关协议内容
     */
    public void getAggrement(String marke, final String btn_name) {
        if (agreement_dialog == null) {
            popIsNull("", "", btn_name,marke);
        } else {
            agreement_dialog.show();
        }
    }
    /**
     * 获取相关协议内容
     */
    public void getAggrement_detail(String marke, final String btn_name) {
        if (agreement_dialog == null) {
            popIsNull("detail", "", btn_name,marke);
        } else {
            agreement_dialog.show();
        }
    }

    /**
     * 获取相关协议内容
     */
    public void getAggrement_detail_size(final String btn_name,float width,float height) {
        if (agreement_dialog == null) {
            try {
                popIsNull_size("推送弹框", "", btn_name,"http://app.scyoue.com/mobile/message/systemMsg?id=12&app=1&uid="+MyApplication.getUid()+"&user="+MyApplication.getUserJson().getString("m_username")+"&pwd="+MyApplication.getPasswprd(),width,height);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            agreement_dialog.show();
        }
    }

    private void popIsNull(String content, String title, String btn_name,String mark) {
        if (agreement_dialog == null) {
            agreement_dialog = new Agreement_Dialog(context, new Agreement_Dialog.Onclick() {
                @Override
                public void OnClickLisener(View v) {
                    agreement_dialog.dismiss();
                }
            });
            agreement_dialog.show();
            WindowManager.LayoutParams lp = agreement_dialog.getWindow().getAttributes();
            lp.width = (int) (DisplayUtil.getScreenWidth(context) * 0.8); //设置宽度
            lp.height = (int) (DisplayUtil.getScreenHeight(context) * 0.8); //设置宽度
            agreement_dialog.getWindow().setAttributes(lp);
            agreement_dialog.setContent(content, title, btn_name,mark);
        } else {
            agreement_dialog.show();
        }
    }

    private void popIsNull_size(String content, String title, String btn_name,String mark,float width,float height) {
        if (agreement_dialog == null) {
            agreement_dialog = new Agreement_Dialog(context, new Agreement_Dialog.Onclick() {
                @Override
                public void OnClickLisener(View v) {
                    agreement_dialog.dismiss();
                }
            });
            agreement_dialog.show();
            WindowManager.LayoutParams lp = agreement_dialog.getWindow().getAttributes();
            lp.width = (int) (DisplayUtil.getScreenWidth(context) * width); //设置宽度
            lp.height = (int) (DisplayUtil.getScreenHeight(context) * height); //设置宽度
            agreement_dialog.getWindow().setAttributes(lp);
            agreement_dialog.setContent(content, title, btn_name,mark);
        } else {
            agreement_dialog.show();
        }
    }


    private void popIsNull2(String content, String title, String btn_name,String mark) {
        if (agreement_dialog == null) {
            agreement_dialog = new Agreement_Dialog(context, new Agreement_Dialog.Onclick() {
                @Override
                public void OnClickLisener(View v) {
                    agreement_dialog.dismiss();
                }
            });
            agreement_dialog.show();
            WindowManager.LayoutParams lp = agreement_dialog.getWindow().getAttributes();
            lp.width = (int) (DisplayUtil.getScreenWidth(context) * 0.8); //设置宽度
            lp.height = (int) (DisplayUtil.getScreenHeight(context) * 0.8); //设置宽度
            agreement_dialog.getWindow().setAttributes(lp);
            agreement_dialog.setContent(content, title, btn_name,mark);
        } else {
            agreement_dialog.show();
        }
    }



    private void popIsNull(String marke,String content, String title, String btn_name, final Agreement_Dialog.Onclick onclick) {
        if (agreement_dialog == null) {
            agreement_dialog = new Agreement_Dialog(context, new Agreement_Dialog.Onclick() {
                @Override
                public void OnClickLisener(View v) {
                    agreement_dialog.dismiss();
                    onclick.OnClickLisener(v);
                }
            });
            agreement_dialog.show();
            WindowManager.LayoutParams lp = agreement_dialog.getWindow().getAttributes();
            lp.width = (int) (DisplayUtil.getScreenWidth(context) * 0.8); //设置宽度
            lp.height = (int) (DisplayUtil.getScreenHeight(context) * 0.8); //设置宽度
            agreement_dialog.getWindow().setAttributes(lp);
            agreement_dialog.setContent(content, title, btn_name,marke);
        } else {
            agreement_dialog.show();
        }
    }

    public void popIsNull_sigle(String content, String title, String btn_name) {
        if (agreement_dialog_sigle == null) {
            agreement_dialog_sigle = new Agreement_Dialog_Sigle(context, new Agreement_Dialog_Sigle.Onclick() {
                @Override
                public void OnClickLisener(View v) {
                    agreement_dialog_sigle.dismiss();
                }
            });
            agreement_dialog_sigle.show();
            WindowManager.LayoutParams lp = agreement_dialog_sigle.getWindow().getAttributes();
            lp.width = (int) (DisplayUtil.getScreenWidth(context) * 0.8); //设置宽度
            lp.height = (int) (DisplayUtil.getScreenHeight(context) * 0.8); //设置宽度
            agreement_dialog_sigle.getWindow().setAttributes(lp);
            agreement_dialog_sigle.setContent(content, title, btn_name);
        } else {
            agreement_dialog_sigle.show();
        }
    }

    /**
     * 获取协议详情
     */
//
//    public void getAggrement_Detail(String id, String marke, final String btn_name, final Agreement_Dialog.Onclick onclick) {
//        if (agreement_dialog == null) {
//            Map<String, String> params = new HashMap<String, String>();
//            params.put("appid", RequestTag.APPID);
//            params.put("key", RequestTag.KEY);
//            String timestamp = System.currentTimeMillis() + "";
//            params.put("time", timestamp + "");
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("device_id", MyApplication.getDeviceId());
//                if (StringUtils.isEmpty(id)) {
//                    jsonObject.put("mark", marke);
//                } else {
//                    jsonObject.put("id", id);
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            params.put("data", jsonObject.toString());
//            String sign = Md5.md5(jsonObject.toString(), timestamp);
//            params.put("sign", sign);
//            MyOkHttp.get().post(context, RequestTag.BaseUrl + RequestTag.ARTICAL_DETAIL, params, new JsonResponseHandler() {
//                @Override
//                public void onSuccess(int statusCode, JSONObject response) {
//                    try {
//                        if (response.getInt("status") != 0) {
//                            Toast.makeText(context, "暂时没有编辑", Toast.LENGTH_LONG).show();
//                        } else {
//                            if (response.getJSONObject("data") == null) {
//                                Toast.makeText(context, "暂时没有编辑", Toast.LENGTH_LONG).show();
//                            } else {
//                                popIsNull(response.getJSONObject("data").getString("content"), response.getJSONObject("data").getString("title"), btn_name,onclick);
//                            }
//                        }
//
//                    } catch (Exception e) {
//                    }
//
//                }
//
//                @Override
//                public void onFailure(int statusCode, String error_msg) {
//                    Toast.makeText(context, "请检查网络是否异常", Toast.LENGTH_LONG).show();
//                }
//            });
//        } else {
//            agreement_dialog.show();
//        }
//    }

    public void getAggrement_Detail(String id, String marke, final String btn_name, final Agreement_Dialog.Onclick onclick) {
        if (agreement_dialog == null) {
            popIsNull(marke,"detail", "", btn_name,onclick);
        } else {
            agreement_dialog.show();
        }
    }

//    public void getAggrement_Detail(String id, String marke, final String btn_name) {
//        if (agreement_dialog == null) {
//            Map<String, String> params = new HashMap<String, String>();
//            params.put("appid", RequestTag.APPID);
//            params.put("key", RequestTag.KEY);
//            String timestamp = System.currentTimeMillis() + "";
//            params.put("time", timestamp + "");
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("device_id", MyApplication.getDeviceId());
//                if(StringUtils.isEmpty(id)) {
//                    jsonObject.put("mark", marke);
//                }else{
//                    jsonObject.put("id", id);
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            params.put("data", jsonObject.toString());
//            String sign = Md5.md5(jsonObject.toString(), timestamp);
//            params.put("sign", sign);
//            MyOkHttp.get().post(context, RequestTag.BaseUrl + RequestTag.ARTICAL_DETAIL, params, new JsonResponseHandler() {
//                @Override
//                public void onSuccess(int statusCode, JSONObject response) {
//                    try {
//                        if (response.getInt("status") != 0) {
//                            Toast.makeText(context, "暂时没有编辑", Toast.LENGTH_LONG).show();
//                        } else {
//                            if (response.getJSONObject("data") == null) {
//                                Toast.makeText(context, "暂时没有编辑", Toast.LENGTH_LONG).show();
//                            } else {
//                                popIsNull(response.getJSONObject("data").getString("content"), response.getJSONObject("data").getString("title"), btn_name);
//                            }
//                        }
//
//                    } catch (Exception e) {
//                    }
//
//                }
//
//                @Override
//                public void onFailure(int statusCode, String error_msg) {
//                    Toast.makeText(context, "请检查网络是否异常", Toast.LENGTH_LONG).show();
//                }
//            });
//        } else {
//            agreement_dialog.show();
//        }
//    }
public void getAggrement_Detail(String id, String marke, final String btn_name) {
    if (agreement_dialog == null) {
        popIsNull("detail","", btn_name,marke);
    } else {
        agreement_dialog.show();
    }
}

}
