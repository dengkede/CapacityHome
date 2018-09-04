package utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.capacityhome.BaseActivity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.BrandRuleAdapter;
import adapter.MyBrandGridAdapter;
import adapter.MyViewPagerAdapter;
import google.zxing.activity.CaptureActivity;
import http.RequestTag;

/**
 * Created by zhang on 2018/6/10.
 *公用计费弹框
 */

public class Common_brandRule {
    /**
     * 品牌相关
     */
    private PopupWindow popupWindow_brand;//品牌
    private int brand_index = 0;//默认喜欢品牌的索引
    private ColumnUtils columnUtils;
    private LinearLayout layout_webView;
    private WebView webView;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private View view;
    private ValueCallback<Uri> mUploadMessage;
    private ProgressBar pg1;
    private TextView tv_priceRule;
    private TextView tv_userRule;
    private TextView tv_agree;
    private TextView tv_titleBrand;
    private View tv_line1;
    private View tv_line2;
    private ListView listview_brand;
    private BrandRuleAdapter brandRuleAdapter;
    private ViewPager viewPager;
    private LinearLayout group;//圆点指示器
    private ImageView[] ivPoints;//小圆点图片的集合
    private int totalPage; //总的页数
    private int mPageSize = 3; //每页显示的最大的数量,品牌
    private List<JSONObject> list_brand = new ArrayList<>();//总的品牌数据源
    private List<JSONObject> list_brandItem = new ArrayList<>();//当前品牌下物品价格
    private List<View> viewPagerList;//GridView作为一个View对象添加到ViewPager集合中
    private Context context;
    private View layout_parent;
    private boolean isZhezhao = false;//是否使用遮罩,false 不使用
    private View view_zhezhao;
    public Common_brandRule(Context context, View layout_parent,boolean isZhezhao,View view_zhezhao) {
        this.context = context;
        this.layout_parent = layout_parent;
        this.view_zhezhao = view_zhezhao;
        this.isZhezhao  = isZhezhao;
    }

    public void showRulePop(){
        if (columnUtils == null) {
            columnUtils = new ColumnUtils(context, new ColumnUtils.Result() {
                @Override
                public void onSuccess(String title, JSONArray jsonArray) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            if(i == 0){
                                brand_index = 0;
                            }
                            if (jsonArray.getJSONObject(i).getInt("id") == MyApplication.brand) {
                                brand_index = i;
                            }
                            list_brand.add(jsonArray.getJSONObject(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    showPopwindow_brand();
                }

                @Override
                public void onFailure(String msg) {
                    toastMessage(msg);
                }
            });
        }
        if (list_brand.size() == 0) {
            columnUtils.getColumn("", "Brand");
        } else {
            showPopwindow_brand();
        }
    }
    private void toastMessage(String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
    private void showPopwindow_brand() {
        if (popupWindow_brand == null) {
            int width = context.getResources().getDisplayMetrics().widthPixels / 10 * 9;
            int height = context.getResources().getDisplayMetrics().heightPixels / 10 * 9;
            View popView = LayoutInflater.from(context).inflate(R.layout.layout_brand_rule_pop, null);
            popupWindow_brand = new PopupWindow(popView, width, height);
            viewPager = (ViewPager) popView.findViewById(R.id.viewpager);
            group = (LinearLayout) popView.findViewById(R.id.points);
            tv_agree = (TextView) popView.findViewById(R.id.tv_agree);
            tv_priceRule = (TextView) popView.findViewById(R.id.tv_priceRule);
            tv_userRule = (TextView) popView.findViewById(R.id.tv_useRule);
            tv_line1 = popView.findViewById(R.id.tv_line1);
            tv_titleBrand = (TextView) popView.findViewById(R.id.tv_title);
            tv_line2 = popView.findViewById(R.id.tv_line2);
            listview_brand = (ListView) popView.findViewById(R.id.listview_brand);
            brandRuleAdapter = new BrandRuleAdapter(context, list_brandItem);
            listview_brand.setAdapter(brandRuleAdapter);
            layout_webView = (LinearLayout) popView.findViewById(R.id.layout_webView);
            webView = (WebView) popView.findViewById(R.id.webView);
            pg1=(ProgressBar) popView.findViewById(R.id.progressBar1);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
//                    String title = view.getTitle();
//                    if (!TextUtils.isEmpty(title)) {
//                        tv_title.setText(title);
//                    }
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
                    ((Activity)context).startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);

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
                        ((Activity)context).startActivityForResult(intent, REQUEST_SELECT_FILE);
                    } catch (ActivityNotFoundException e) {
                        uploadMessage = null;
                        return false;
                    }
                    return true;
                }


            });


            try {
            } catch (Exception e) {
                e.printStackTrace();
            }
            popupWindow_brand.setAnimationStyle(R.style.popwin_anim_style);
            popupWindow_brand.setFocusable(true);
            popupWindow_brand.setOutsideTouchable(false);// 设置同意在外点击消失
            ColorDrawable dw = new ColorDrawable(0x30000000);
            popupWindow_brand.setBackgroundDrawable(dw);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.tv_useRule:
                            choosehange(2);
                            break;
                        case R.id.tv_priceRule:
                            choosehange(1);
                            break;
                        case R.id.tv_agree:
                            popupWindow_brand.dismiss();
                            break;
                    }
                }
            };
            popupWindow_brand.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if(isZhezhao){
                        view_zhezhao.setVisibility(View.GONE);
                    }else {
                        DisplayUtil.backgroundAlpha(1.0f, (Activity) context);
                    }
                }
            });
            tv_userRule.setOnClickListener(onClickListener);
            tv_priceRule.setOnClickListener(onClickListener);
            tv_agree.setOnClickListener(onClickListener);
            initData();
            webView.loadUrl(RequestTag.ArticaleUrl + RequestTag.Artical_DETAIL + "&mark=" + "PricingRules");
            try {
                if (list_brand.size() != 0) {
                    tv_titleBrand.setText("以下是《" + list_brand.get(brand_index).getString("title") + "》的定价");
                    getBrandList(list_brand.get(brand_index).getString("id"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            listview_brand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    try {
//                        toastMessage(position+"");
//                        getBrandList(list_brand.get(position).getString("id"));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
        }
        if(isZhezhao){
            view_zhezhao.setVisibility(View.VISIBLE);
        }else {
            DisplayUtil.backgroundAlpha(0.3f, (Activity) context);
        }
        popupWindow_brand.showAtLocation(layout_parent, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 选择下划线的改变
     * type =1;选择计费规则
     */
    private void choosehange(int type) {
        if (type == 1) {
            LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) tv_line1.getLayoutParams();
            params1.height = (int) context.getResources().getDimension(R.dimen.x3);
            tv_line1.setLayoutParams(params1);
            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) tv_line2.getLayoutParams();
            params2.height = (int) context.getResources().getDimension(R.dimen.x1);
            tv_line2.setLayoutParams(params2);
            tv_line1.setBackgroundColor(ContextCompat.getColor(context, R.color.fontcolor_yellow));
            tv_line2.setBackgroundColor(ContextCompat.getColor(context, R.color.fontcolor_e2));
            tv_priceRule.setTextColor(ContextCompat.getColor(context, R.color.fontcolor_yellow));
            tv_userRule.setTextColor(ContextCompat.getColor(context, R.color.fontcolor_f3));
            layout_webView.setVisibility(View.GONE);
        } else {
            LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) tv_line1.getLayoutParams();
            params1.height = (int) context.getResources().getDimension(R.dimen.x1);
            tv_line1.setLayoutParams(params1);
            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) tv_line2.getLayoutParams();
            params2.height = (int) context.getResources().getDimension(R.dimen.x3);
            tv_line2.setLayoutParams(params2);
            tv_line1.setBackgroundColor(ContextCompat.getColor(context, R.color.fontcolor_e2));
            tv_line2.setBackgroundColor(ContextCompat.getColor(context, R.color.fontcolor_yellow));
            tv_priceRule.setTextColor(ContextCompat.getColor(context, R.color.fontcolor_f3));
            tv_userRule.setTextColor(ContextCompat.getColor(context, R.color.fontcolor_yellow));
            layout_webView.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        // TODO Auto-generated method stub
        //总的页数向上取整
        totalPage = (int) Math.ceil(list_brand.size() * 1.0 / mPageSize);
        if (viewPagerList == null) {
            viewPagerList = new ArrayList<View>();
        } else {
            viewPagerList.clear();
        }
        for (int i = 0; i < totalPage; i++) {
            //每个页面都是inflate出一个新实例
            final GridView gridView = (GridView) View.inflate(context, R.layout.item_gridview, null);
            gridView.setNumColumns(3);
            gridView.setHorizontalSpacing((int) context.getResources().getDimension(R.dimen.x12));
            gridView.setAdapter(new MyBrandGridAdapter(context, list_brand, i, mPageSize, new MyBrandGridAdapter.Click() {
                @Override
                public void onClick(int position) {
                    try {
                        tv_titleBrand.setText("以下是《" + list_brand.get(position).getString("title") + "》的定价");
                        getBrandList(list_brand.get(position).getString("id"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            },MyApplication.getBrand()));
//            //添加item点击监听
//            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                @Override
//                public void onItemClick(AdapterView<?> arg0, View arg1,
//                                        int position, long arg3) {
//                    // TODO Auto-generated method stub
//                    try {
//                        tv_titleBrand.setText("以下是《" + list_brand.get(position).getString("title") + "》的定价");
//                        getBrandList(list_brand.get(position).getString("id"));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
            //每一个GridView作为一个View对象添加到ViewPager集合中
            viewPagerList.add(gridView);
        }
        //设置ViewPager适配器
        viewPager.setAdapter(new MyViewPagerAdapter(viewPagerList));

        //添加小圆点
        group.removeAllViews();
        ivPoints = new ImageView[totalPage];
        for (int i = 0; i < totalPage; i++) {
            //循坏加入点点图片组
            ivPoints[i] = new ImageView(context);
            if (i == 0) {
                ivPoints[i].setImageResource(R.drawable.page_focuese);
            } else {
                ivPoints[i].setImageResource(R.drawable.page_unfocused);
            }
            ivPoints[i].setPadding(8, 0, 8, 0);
            group.addView(ivPoints[i]);

        }
        //设置ViewPager的滑动监听，主要是设置点点的背景颜色的改变
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                //currentPage = position;
                for (int i = 0; i < totalPage; i++) {
                    if (i == position) {
                        ivPoints[i].setImageResource(R.drawable.page_focuese);
                    } else {
                        ivPoints[i].setImageResource(R.drawable.page_unfocused);
                    }
                }
            }
        });
    }

    //**
//        * (特殊字符替换)
//            *
//            * @return String    返回类型
//     */
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

//    /**
//     * 获取使用细则
//     */
//    private void getUseRule() {
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("appid", RequestTag.APPID);
//        params.put("key", RequestTag.KEY);
//        String timestamp = System.currentTimeMillis() + "";
//        params.put("time", timestamp + "");
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("device_id", MyApplication.getDeviceId());
//            jsonObject.put("mark", "PricingRules");
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        params.put("data", jsonObject.toString());
//        String sign = Md5.md5(jsonObject.toString(), timestamp);
//        params.put("sign", sign);
//        MyOkHttp.get().post(context, RequestTag.BaseUrl + RequestTag.ARTICAL_DETAIL, params, new JsonResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, JSONObject response) {
//                try {
//                    if (response.getInt("status") != 0) {
//                        toastMessage("暂时没有编辑");
//                    } else {
//                        if (response.getJSONObject("data") == null) {
//                            toastMessage("暂时没有编辑");
//                        } else {
//                            tv_contentBrand.setText(Html.fromHtml(htmlReplace(response.getJSONObject("data").getString("content")), new MImageGetter(tv_contentBrand, context), null));
//                        }
//                    }
//
//                } catch (Exception e) {
//                }
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, String error_msg) {
//                toastMessage("请检查网络是否异常");
//            }
//        });
//    }
    /**
     * 用于根据品牌读取洗衣商品列表
     */
    private void getBrandList(final String brand) {
        ((BaseActivity)context).showLoadingView();
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", RequestTag.APPID);
        params.put("key", RequestTag.KEY);
        String timestamp = System.currentTimeMillis() + "";
        params.put("time", timestamp + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", MyApplication.getDeviceId());
            jsonObject.put("brand", brand);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("data", jsonObject.toString());
        String sign = Md5.md5(jsonObject.toString(), timestamp);
        params.put("sign", sign);
        MyOkHttp.get().post(context, RequestTag.BaseUrl + RequestTag.getBrandGoodsList, params, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    Log.d("pinpaiItem",response.toString());
                    if (response.getInt("status") != 0) {
                        list_brandItem.clear();
                    } else {
                        if (response.getJSONArray("data") == null || response.getJSONArray("data").length() == 0) {
                            list_brandItem.clear();
                        } else {
                            list_brandItem.clear();
                            for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                                list_brandItem.add(response.getJSONArray("data").getJSONObject(i));
                            }

                        }

                    }
                    brandRuleAdapter.notifyDataSetChanged();
                } catch (Exception e) {


                }
                ((BaseActivity)context).dismissLoadingView();
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                toastMessage(error_msg);
                ((BaseActivity)context).dismissLoadingView();
            }
        });
    }
}
