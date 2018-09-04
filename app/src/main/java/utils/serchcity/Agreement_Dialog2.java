package utils.serchcity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.capacityhome.R;

import http.RequestTag;
import utils.MImageGetter;


/**
 * @author http://blog.csdn.net/finddreams
 * @Description:自定义对话框 首页公告
 */
public class Agreement_Dialog2 extends AlertDialog implements View.OnClickListener {
    private Context mContext;
    private WindowManager m;
    private Onclick onclick;
    private TextView tv_content;
    private TextView tv_agree;//同意
    private TextView tv_title;
    private DisplayMetrics dm;
    private TextView tv_cancel;

    private WebView webView;
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private View view;
    private ProgressBar pg1;

    public Agreement_Dialog2(Context context, String content) {
        super(context);
        this.mContext = context;
        setCanceledOnTouchOutside(false);
    }

    public Agreement_Dialog2(Context context, Onclick onclick) {
        super(context, R.style.loading_dialog);
        dm = context.getResources().getDisplayMetrics();
        this.mContext = context;
        this.onclick = onclick;
        setCanceledOnTouchOutside(false);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        m = getWindow().getWindowManager();
    }

    private void initView() {
//        setContentView(R.layout.agreement_dialog2);
        view = getLayoutInflater().inflate(R.layout.agreement_dialog2, null);
        webView = (WebView) view.findViewById(R.id.webView);
        tv_title = (TextView) view.findViewById(R.id.tv_titles);
        tv_content = (TextView) view.findViewById(R.id.tv_content);
        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        pg1 = (ProgressBar) view.findViewById(R.id.progressBar1);
        GradientDrawable p = (GradientDrawable) tv_cancel.getBackground();
        p.setColor(ContextCompat.getColor(getContext(), R.color.fontcolor_f2));
        tv_agree = (TextView) view.findViewById(R.id.tv_agree);
        tv_agree.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
              //  String title = view.getTitle();
                tv_title.setVisibility(View.GONE);
//                if (!TextUtils.isEmpty(title)) {
//                    tv_title.setText(title);
//                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO 自动生成的方法存根
                view.loadUrl(url);
                return true;
            }
        });
        WebSettings settings = webView.getSettings();
        // 如果访问的页面中有JavaScript，则WebView必须设置支持JavaScript，否则显示空白页面
        webView.getSettings().setJavaScriptEnabled(true);
        // 获取到UserAgentString
        String userAgent = settings.getUserAgentString();
        // 打印结果
        String ua = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(ua + "; xegj");
        webView.setWebChromeClient(new WebChromeClient() {
            //The undocumented magic method override
            //Eclipse will swear at you if you try to put @Override here
            // For Android 3.0+

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    pg1.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    pg1.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    pg1.setProgress(newProgress);//设置进度值
                }
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {

                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                ((Activity) getContext()).startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);

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
                    ((Activity) getContext()).startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e) {
                    uploadMessage = null;
                    return false;
                }
                return true;
            }


        });
    }

    public void setContent(String s, String title, String button_name) {
        //tv_content.setText(s);
//	String escapedUsername = TextUtils.htmlEncode(s);
//	String text = String.format(s, escapedUsername);
//        if (title.isEmpty()) {
//            tv_title.setVisibility(View.GONE);
//        }

        if (button_name.contains("查看详情")) {
            setCanceledOnTouchOutside(true);
        }
        tv_agree.setText(button_name);
        //tv_content.setText(Html.fromHtml(htmlReplace(s), new MImageGetter(tv_content, getContext()), null));
        webView.loadUrl(RequestTag.ArticaleUrl + RequestTag.Artical_DETAIL+ "&mark=" + "member_regid");
        setContentView(view);
//	tv_content.setText(Html.fromHtml(s));
    }

    /**
     * (特殊字符替换)
     *
     * @return String    返回类型
     */
    public static String htmlReplace(String str) {
        str = str.replace("&ldquo;", "“");
        str = str.replace("&quot;", "“");
        str = str.replace("&amp;", "&");
        str = str.replace("&lt;", "<");
        str = str.replace("&gt;", ">");
        str = str.replace("&rdquo;", "”");
        str = str.replace("&nbsp;", " ");
        str = str.replace("&", "&amp;");
        str = str.replace("&#39;", "'");
        str = str.replace("&rsquo;", "’");
        str = str.replace("&mdash;", "—");
        str = str.replace("&ndash;", "–");
        return str;
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(new AnimationUtils().loadAnimation(getContext(), R.anim.bg_alpha));
        switch (v.getId()) {
            case R.id.tv_agree:
                //确认付款
                onclick.OnClickLisener(v);
                Agreement_Dialog2.this.dismiss();
                break;
            case R.id.tv_cancel:
                onclick.OnClickLisener(v);
                Agreement_Dialog2.this.dismiss();
                break;
        }

    }

    public interface Onclick {
        void OnClickLisener(View v);
    }

}

