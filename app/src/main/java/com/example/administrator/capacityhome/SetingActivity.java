package com.example.administrator.capacityhome;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.capacityhome.login.LoginActivity;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.DownloadResponseHandler;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import http.RequestTag;
import utils.DisplayUtil;
import utils.GetAgrement;
import utils.Md5;
import widget.MyTextView;
import widget.NumberProgressView;

/**
 * 设置
 */
public class SetingActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_loginOut;
    private RelativeLayout relayout_update;
    private RelativeLayout relayout_about;
    private PopupWindow popWindow;
    private PopupWindow popupWindow_down;
    private TextView tv_sure;
    private TextView tv_cancel;
    private TextView tv_content;
    private LinearLayout layout_parent;
    private GetAgrement getAgrement;
    private ProgressDialog progressDialog;
    private boolean isupdate = true;
    private String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final int PERMS_REQUEST_CODE = 200;
    private NumberProgressView numberProgress;
    private TextView tv_total;
    private TextView tv_current;
    private TextView tv_progress;
    private int number = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seting);
        initView();
        //Android 6.0以上版本需要临时获取权限
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 &&
                PackageManager.PERMISSION_GRANTED != checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermissions(perms, PERMS_REQUEST_CODE);
        } else {
            relayout_update.setOnClickListener(this);
        }

    }

    private void initView() {

        relayout_update = (RelativeLayout) findViewById(R.id.relayout_update);
        relayout_about = (RelativeLayout) findViewById(R.id.relayout_about);
        tv_loginOut = (TextView) findViewById(R.id.tv_loginOut);
        findViewById(R.id.relayout_imgBack).setOnClickListener(this);
        relayout_about.setOnClickListener(this);
        tv_loginOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(new AnimationUtils().loadAnimation(this, R.anim.bg_alpha));
        switch (v.getId()) {
            case R.id.relayout_update:
                //检查更新
                check_update("1.3.5");
//                showUpdaloadDialog("");
                break;
            case R.id.relayout_about:
                //关于我们
                if (getAgrement == null) {
                    getAgrement = new GetAgrement(this);
                }
                getAgrement.getAggrement("company", "我知道了");
                break;
            case R.id.tv_loginOut:
                //更换账号
                showpopWindow();
                break;
            case R.id.relayout_imgBack:
                finish();
                break;
        }
    }

    private void showpopWindow() {
        if (popWindow == null) {
            layout_parent = (LinearLayout) findViewById(R.id.layout_parent);
            int width = getResources().getDisplayMetrics().widthPixels / 10 * 8;
//            int height = getResources().getDisplayMetrics().widthPixels / 2.2;
            View popView = LayoutInflater.from(this).inflate(R.layout.delete_img_pop, null);
            popWindow = new PopupWindow(popView, width, LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView tv_title = (TextView) popView.findViewById(R.id.tv_title);
            tv_title.setText("是否退出当前账号");
            tv_cancel = (TextView) popView.findViewById(R.id.tv_cancel);
            tv_sure = (TextView) popView.findViewById(R.id.tv_sure);
            tv_content = (MyTextView) popView.findViewById(R.id.tv_address);
            tv_content.setVisibility(View.GONE);

            popWindow.setAnimationStyle(R.style.popwin_anim_style);
            popWindow.setFocusable(true);
            popWindow.setOutsideTouchable(false);// 设置同意在外点击消失
            ColorDrawable dw = new ColorDrawable(0x30000000);
            popWindow.setBackgroundDrawable(dw);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.tv_cancel:
                            popWindow.dismiss();
                            break;
                        case R.id.tv_sure:
                            popWindow.dismiss();
                            MyApplication.clearAll();
                            startActivity(SetingActivity.this, LoginActivity.class);
                            finish();
                            break;
                    }
                }
            };
            popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    DisplayUtil.backgroundAlpha(1.0f, SetingActivity.this);
                }
            });
            tv_sure.setOnClickListener(onClickListener);
            tv_cancel.setOnClickListener(onClickListener);
        }
        DisplayUtil.backgroundAlpha(0.3f, this);
        popWindow.showAtLocation(layout_parent, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 检查更新
     */
    private void check_update(final String client_ver) {
        showLoadingView();
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("client_ver", client_ver);//客户端当前版本号
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        http:
//211.149.154.203:85/download/android.apk
        params.put("sign", sign);
        MyOkHttp.get().post(this, RequestTag.BaseUrl + RequestTag.CHECK_UPDATE, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    Log.d("updatete",response.toString());
                    toastMessage(response.getJSONObject("data").getString("android_msg"));
                    if (response.getInt("status") == 0) {
                        if (response.getJSONObject("data").getString("android_ver").equals(client_ver)) {
                            //没有更新
                            dismissLoadingView();
                        } else {
                            //更新
                            dismissLoadingView();
                            showUpdaloadDialog(response.getJSONObject("data").getString("android"));
                        }
                    }else{
//                        if(response.getJSONObject("data").getString("android_ver").equals("1.3.1")){
//
//                        }
                    }
                } catch (Exception e) {
                    dismissLoadingView();
                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dismissLoadingView();
                toastMessage("请检查网络");
                startActivity(new Intent(SetingActivity.this, Main_NewActivity.class));
                finish();
            }
        });
    }

    private void showUpdaloadDialog(final String path) {
        // 这里的属性可以一直设置，因为每次设置后返回的是一个builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置提示框的标题
        builder.setTitle("版本升级").
                setIcon(R.mipmap.app_icons). // 设置提示框的图标
                setMessage("发现新版本！请及时更新").// 设置要显示的信息
                setPositiveButton("确定", new DialogInterface.OnClickListener() {// 设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startUpload(path);//下载最新的版本程序
            }
        }).setNegativeButton("取消", null);//设置取消按钮,null是什么都不做，并关闭对话框
        AlertDialog alertDialog = builder.create();
        // 显示对话框
        alertDialog.show();
    }

    private void startUpload(String path) {
        showLoadingView();
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("正在下载新版本");
        progressDialog.setCancelable(true);//不能手动取消下载进度对话框,fanhuijian
        progressDialog.setIndeterminate(false);
        String moduleName = getSaveFilePath();
        MyOkHttp.get().download(this, path, moduleName, "updata.apk", new DownloadResponseHandler() {
            @Override
            public void onFinish(final File download_file) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //  Toast.makeText(SetingActivity.this, "下载完成", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        installApk(download_file);
                    }
                });

            }

            @Override
            public void onProgress(final long currentBytes, final long totalBytes) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isupdate) {
//                                progressDialog.setMax((int) totalBytes);
//                                progressDialog.show();
//                                isupdate = false;
                            showpopupWindow_down(currentBytes / 1024 / 1024 + "m", totalBytes / 1024 / 1024 + "m");
                            isupdate = false;
                        } else {
                            dismissLoadingView();
                            // progressDialog.setProgress((int)currentBytes);
                            tv_current.setText(currentBytes / 1024 / 1024 + "m");
                            tv_progress.setText((int) (currentBytes * 100 / totalBytes) + "%");
                            numberProgress.setProgress((int) (currentBytes * 100 / totalBytes));
                        }
                    }
                });

            }

            @Override
            public void onFailure(String error_msg) {
                dismissLoadingView();
                toastMessage(error_msg);
            }
        });
    }

    /**
     * 安装apk
     */
    protected void installApk(File file) {
//        Intent intent = new Intent();
//        Uri data;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判断版本大于等于7.0
//            // "com.ansen.checkupdate.fileprovider"即是在清单文件中配置的authorities
//            // 通过FileProvider创建一个content类型的Uri
//            data = FileProvider.getUriForFile(this, "com.ansen.checkupdate.fileprovider", file);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);// 给目标应用一个临时授权
//        } else {
//            data = Uri.fromFile(file);
//        }
//
//
//        intent.setDataAndType(data, "application/vnd.android.package-archive");
//        startActivity(intent);

//        Intent intent = new Intent();
//        //执行动作
//        intent.setAction(Intent.ACTION_VIEW);
//        //执行的数据类型
//        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//        startActivity(intent);


        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 24) { //Android 7.0及以上
            // 参数2 清单文件中provider节点里面的authorities ; 参数3  共享的文件,即apk包的file类
            Uri apkUri = FileProvider.getUriForFile(this, "com.ansen.checkupdate.fileprovider", file);
            //对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        startActivity(intent);


    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        switch (permsRequestCode) {
            case PERMS_REQUEST_CODE:
                boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (storageAccepted) {
                    relayout_update.setOnClickListener(this);
                } else {
                    toastMessage("请打开相关权限，不然部分功能更能无法正常使用。");
                }
                break;

        }
    }

    /**
     * 获取文件保存路径 sdcard根目录/download/文件名称
     *
     * @return
     */
    public static String getSaveFilePath() {
        String newFilePath = Environment.getExternalStorageDirectory() + "/Download/";
        return newFilePath;
    }

    private void showpopupWindow_down(String cur, String total) {
        if (popupWindow_down == null) {
            layout_parent = (LinearLayout) findViewById(R.id.layout_parent);
            int width = getResources().getDisplayMetrics().widthPixels / 10 * 8;
//            int height = getResources().getDisplayMetrics().widthPixels / 2.2;
            View popView = LayoutInflater.from(this).inflate(R.layout.down_load, null);
            popupWindow_down = new PopupWindow(popView, width, LinearLayout.LayoutParams.WRAP_CONTENT);
            tv_current = (TextView) popView.findViewById(R.id.tv_current);
            tv_progress = (TextView) popView.findViewById(R.id.tv_progress);

            tv_total = (TextView) popView.findViewById(R.id.tv_total);
            numberProgress = (NumberProgressView) popView.findViewById(R.id.numberProgress);
            numberProgress.setProgress(0);
            tv_progress.setText("0%");
            popupWindow_down.setAnimationStyle(R.style.popwin_anim_style);
            tv_total.setText(total);
            tv_current.setText(cur);
            popupWindow_down.setFocusable(false);
            popupWindow_down.setOutsideTouchable(false);// 设置同意在外点击消失
            ColorDrawable dw = new ColorDrawable(0x30000000);
            popupWindow_down.setBackgroundDrawable(dw);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {

                    }
                }
            };
            popupWindow_down.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    DisplayUtil.backgroundAlpha(1.0f, SetingActivity.this);
                }
            });

        }
        dismissLoadingView();
        DisplayUtil.backgroundAlpha(0.3f, this);
        popupWindow_down.showAtLocation(layout_parent, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void onBackPressed() {
        if(popupWindow_down!=null&&popupWindow_down.isShowing()){
            if (number == 0) {
                Toast.makeText(SetingActivity.this, "正在更新，请等待,再次点击关闭", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            number = 0;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
            if (number == 1) {
                if(popupWindow_down!=null) {
                    popupWindow_down.dismiss();
                }
            } else {
                number++;
            }
        }else {
            super.onBackPressed();
        }
    }
}
