package com.example.administrator.capacityhome;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class WebViewActivity extends BaseActivity {
    private WebView webView;
    private String webViewData = null;
    private WebSettings settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        initView();
    }
    private void initView(){
        setPageTitle("");
        backActivity();
        webView = (WebView) findViewById(R.id.webView);
        initWebView("");
    }

    /**
     * 初始化WebView
     *
     * @param data
     */
    @SuppressLint("SetJavaScriptEnabled")
    @SuppressWarnings("deprecation")
    private void initWebView(final String data) {
        webViewData = "<style> body{word-wrap:break-word;font-family:Arial;text-align:justify;text-indent:0em;letter-spacing:1px;line-height:24px} img{max-width:100%; height:auto;} </style>"
                +data;
        settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView webView, String s) {
                webView.loadUrl("javascript:(" + readJS() + ")()");

            }

            /*
             * 此处能拦截超链接的url,即拦截href请求的内容。
             */
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                WebView.HitTestResult hit = view.getHitTestResult();
                if (hit != null) {
                    int hitType = hit.getType();
                    if (hitType == WebView.HitTestResult.SRC_ANCHOR_TYPE
                            || hitType == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {// 点击超链接
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    } else {
                        view.loadUrl(url);
                    }
                } else {
                    view.loadUrl(url);
                }
                return true;
            }

        });
        webView.addJavascriptInterface(new JavascriptInterface(), "mainActivity");
        webView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
        settings.setJavaScriptEnabled(true);
        settings.setBlockNetworkImage(false);// 解决图片不显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)

        {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

//        if (textSize == 1) {
//            settings.setTextSize(WebSettings.TextSize.SMALLER);
//        } else if (textSize == 3) {
//            settings.setTextSize(WebSettings.TextSize.LARGER);
//        } else {
            settings.setTextSize(WebSettings.TextSize.NORMAL);
        //}
    }
    /**
     * 设置WebView
     *
     * @author Administrator
     */
    public class JavascriptInterface {
        @android.webkit.JavascriptInterface
        public void startPhotoActivity(String imageUrl) {
//            Intent intent = new Intent(WebViewActivity.this, PhotoActivity.class);
//            Bundle b = new Bundle();
//            b.putString("imgUrl", imageUrl);
//            b.putString("allImgUrl", webViewData);
//            intent.putExtra("imgUrl", b);
//            startActivity(intent);
            finish();

        }
    }
    private String readJS() {
        try {
            InputStream inStream = getAssets().open("js.txt");
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = inStream.read(bytes)) > 0) {
                outStream.write(bytes, 0, len);
            }
            return outStream.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
