package com.example.administrator.capacityhome;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.capacityhome.activitylist.ActivityListActivity;

import http.RequestTag;

public class WebViewUrlActivity extends BaseActivity {
    WebView web;
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private String path = RequestTag.ArticaleUrl + "mobile/Article/detail?";
    private String id;
    private String mark;
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            Uri result = intent == null || resultCode != WebViewUrlActivity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }
    private ProgressBar pg1;
    private TextView delete_dialog_tvTitle;
    private long exitTime = 0;
    private TextView tv_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_url);
        tv_title = (TextView)findViewById(R.id.tv_title);
//        if(getIntent().getStringExtra("title")==null) {
//          tv_title.setText("活动详情");
//        }else{
//            tv_title.setText(getIntent().getStringExtra("title"));
//        }
        backActivity();
        submitBtnVisible("更多", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(WebViewUrlActivity.this, ActivityListActivity.class);
                if(getIntent().getStringExtra("mark")!=null){
                    Intent intent = new Intent();
                    intent.putExtra("mark",getIntent().getStringExtra("mark"));
                    if(getIntent().getBooleanExtra("live",false)){
                        setResult(3,intent);
                    }else{
                        startActivity(new Intent(WebViewUrlActivity.this,ActivityListActivity.class).putExtra("mark",getIntent().getStringExtra("mark")));
                    }
                }
                finish();
            }
        });
       // findViewById(R.id.top_submit_btn).setVisibility(View.GONE);
        web = (WebView) findViewById(R.id.webview1);
        pg1=(ProgressBar) findViewById(R.id.progressBar1);
        if(getIntent().getStringExtra("name")!=null) {
            setPageTitle(getIntent().getStringExtra("name"));
        }
        web.setWebViewClient(new WebViewClient(){
            //覆写shouldOverrideUrlLoading实现内部显示网页
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO 自动生成的方法存根
                view.loadUrl(url);
                return true;
            }
        });
        WebSettings settings = web.getSettings();
        // 如果访问的页面中有JavaScript，则WebView必须设置支持JavaScript，否则显示空白页面
        web.getSettings().setJavaScriptEnabled(true);
        // 获取到UserAgentString
        String userAgent = settings.getUserAgentString();
        // 打印结果
        String ua = web.getSettings().getUserAgentString();
        web.getSettings().setUserAgentString(ua+"; xegj");
        web.setWebChromeClient(new WebChromeClient() {
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
                WebViewUrlActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);

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
        mark = getIntent().getStringExtra("mark");
        id = getIntent().getStringExtra("id");
        if(getIntent().getStringExtra("url")!=null){
            path = getIntent().getStringExtra("url");
        }else {
            if (id != null) {
                path = path + "id=" + id + "&app=1";
            } else {
                path = path + "mark=" + mark + "&app=1";
            }
        }
        //文章列表 显示更多，单页不显示更多
        if(!path.contains("mobile/Article/single")){
            //不显示更多
             findViewById(R.id.top_submit_btn).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.top_submit_btn).setVisibility(View.GONE);
        }
        if(path.contains("&sign=")){
            findViewById(R.id.top_submit_btn).setVisibility(View.GONE);
        }
        if(getIntent().getStringExtra("tuisong")!=null){
            findViewById(R.id.top_submit_btn).setVisibility(View.GONE);
        }
        web.loadUrl(path);

    }

    //flipscreen not loading again
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    //设置返回键动作（防止按返回键直接退出程序)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO 自动生成的方法存根
        if(keyCode==KeyEvent.KEYCODE_BACK) {
            if(web.canGoBack()) {//当webview不是处于第一页面时，返回上一个页面
                web.goBack();
                return true;
            }
            else {//当webview处于第一页面时,直接退出webView
                finish();
            }


        }
        return super.onKeyDown(keyCode, event);
    }
}
