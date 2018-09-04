package com.example.administrator.capacityhome;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import utils.GetAgrement;

public class H5Activity extends Activity implements View.OnClickListener {
    private TextView tv_content;
    private TextView tv_agree;//同意
    private TextView tv_title;
    private DisplayMetrics dm;
    private WebView webView;
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private ProgressBar pg1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h5);
        this.setFinishOnTouchOutside(true);
        tv_title = (TextView) findViewById(R.id.tv_titles);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_agree = (TextView) findViewById(R.id.tv_agree);
        tv_agree.setOnClickListener(this);
        webView = (WebView) findViewById(R.id.webView);
        pg1=(ProgressBar) findViewById(R.id.progressBar1);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String title = view.getTitle();
                if (!TextUtils.isEmpty(title)) {
                    tv_title.setText(title);
                }
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO 自动生成的方法存根
                view.loadUrl(url);
                return true;
            }
        });
        WebSettings settings =webView.getSettings();
        // 如果访问的页面中有JavaScript，则WebView必须设置支持JavaScript，否则显示空白页面
        webView.getSettings().setJavaScriptEnabled(true);
        // 获取到UserAgentString
        String userAgent = settings.getUserAgentString();
        // 打印结果
        String ua = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(ua+"; xegj");
        webView.setWebChromeClient(new WebChromeClient() {
            //The undocumented magic method override
            //Eclipse will swear at you if you try to put @Override here
            // For Android 3.0+

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(newProgress==100){
                    pg1.setVisibility(View.GONE);//加载完网页进度条消失
                }
                else{
                    pg1.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    pg1.setProgress(newProgress);//设置进度值
                }
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {

                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);

            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                openFileChooser(uploadMsg);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg);
            }

            // For Lollipop 5.0+ Devices
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;

                Intent intent = fileChooserParams.createIntent();
                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e) {
                    uploadMessage = null;
                    return false;
                }
                return true;
            }

        });

        DisplayMetrics d = getResources().getDisplayMetrics();
        android.view.WindowManager.LayoutParams p =
                getWindow().getAttributes();
        try {
            JSONObject jsonObject_main = new JSONObject(getIntent().getStringExtra("json"));
            webView.loadUrl(jsonObject_main.getString("url").split("app=1")[0]+"app=1"+"&uid="+MyApplication.getUid()+"&user="+MyApplication.getUserJson().getString("m_username")+"&pwd="+MyApplication.getPasswprd());
            p.width = (int) (d.widthPixels * java.lang.Float.parseFloat(jsonObject_main.getJSONArray("windowSize").getString(0)));
            p.height = (int) (d.heightPixels*java.lang.Float.parseFloat(jsonObject_main.getJSONArray("windowSize").getString(1)));
        }catch (Exception e){
            p.height = (int) (d.heightPixels*0.8);
            p.width = (int) (d.widthPixels*0.8);
        }
        // 宽度设置为屏幕的0.7
        getWindow().setAttributes(p);
    }
//    /**
//     * 跳转h5
//     */
//    private void h5(JSONObject jsonObject_main){
//        GetAgrement getAgrement = null;
//        if (getAgrement == null) {
//            getAgrement = new GetAgrement(this);
//        }
//        try {
//            if (jsonObject_main.getString("windowSize").equals("full")) {
//                getAgrement.getAggrement_detail_size("我知道了", 1f, 1f);
//            }
//        }catch (Exception e){
//            //可能是数组
//            try {
//                getAgrement.getAggrement_detail_size("我知道了", java.lang.Float.parseFloat(jsonObject_main.getJSONArray("windowSize").getString(0)), java.lang.Float.parseFloat(jsonObject_main.getJSONArray("windowSize").getString(1)));
//            }catch (Exception e2){
//
//            }
//        }
//    }

    @Override
    public void onClick(View view) {
        finish();
    }
}
