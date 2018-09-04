package widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


import com.example.administrator.capacityhome.R;

import org.xutils.common.Callback;

import java.util.HashMap;
import java.util.Map;

import widget.show_html.HtmlTextView;
import widget.show_html.MyTagHandler;


/**
 * @Description:自定义对话框
 * 首页公告
 * @author http://blog.csdn.net/finddreams
 */
public class Agreement_Dialog extends AlertDialog implements View.OnClickListener{
	private Context mContext;
    private  WindowManager m;
	private Onclick onclick;
	private TextView tv_content;
	private TextView tv_agree;//同意
	private Map<String,Drawable> list_url = new HashMap<>();//网页图片
	private MyTagHandler tagHandler ;//为图片设置点击事件
	private TextView tv_title;
	private DisplayMetrics dm;
	private String url_android = null;//按钮链接地址
	public Agreement_Dialog(Context context, String content) {
		super(context);
		this.mContext = context;
		setCanceledOnTouchOutside(false);
	}

	public Agreement_Dialog(Context context, int theme, Onclick onclick) {
		super(context, theme);
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
		setContentView(R.layout.agreement_dialog);
		tv_title = (TextView) findViewById(R.id.tv_titles);
		tv_content = (TextView) findViewById(R.id.tv_content);
		tv_agree = (TextView) findViewById(R.id.tv_agree);
		tv_agree.setOnClickListener(this);
	}
public void setContent(String s,String title,String button_name,String url_android){
	//tv_content.setText(s);
//	String escapedUsername = TextUtils.htmlEncode(s);
//	String text = String.format(s, escapedUsername);
	if(title.isEmpty()){
		tv_title.setVisibility(View.GONE);
	}
	this.url_android = url_android;
	tv_title.setText(title);
	if(button_name.contains("查看详情")){
		setCanceledOnTouchOutside(true);
	}
	tv_agree.setText(button_name);
	tagHandler =  new MyTagHandler(getContext());
	getContent_url(htmlReplace(s),"");

//	tv_content.setText(Html.fromHtml(s));
}
	/**
	 *
	 *(特殊字符替换)
	 * @return String    返回类型
	 */
	public static String htmlReplace(String str){
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
		switch (v.getId()){
			case R.id.tv_agree:
				//确认付款
				onclick.OnClickLisener(v,url_android);
				Agreement_Dialog.this.dismiss();
				break;
		}

	}
  public interface Onclick{
       void OnClickLisener(View v, String url);
   }
	/**
	 * 图文混排，解析html
	 * @param json
	 * @return
	 */
	private void getContent(final String json){
		CharSequence text= Html.fromHtml(json, new Html.ImageGetter()
		{  public Drawable getDrawable(String source) {
			//根据图片资源ID获取图片
			Drawable draw = null;
			if(list_url.get(source)!=null){
				draw = list_url.get(source);
				if(dm.widthPixels/12*10<draw.getIntrinsicWidth()){
					draw.setBounds((dm.widthPixels/12*10-dm.widthPixels/12*10/5*4)/2, 0,dm.widthPixels/12*10/5*4, draw.getIntrinsicHeight()/3*2);
				}else {
					draw.setBounds((dm.widthPixels/12*10-draw.getIntrinsicWidth())/2, 0, draw.getIntrinsicWidth(), draw.getIntrinsicHeight());
				}
			}else{
				getContent_url(json,source);
			}

			return  draw;
//                return XUtil.display_drawble(null,MyUrl.urlPic+source,false);
		}
		}, tagHandler);
		tv_content.setText(text);
		tv_content.setMovementMethod(LinkMovementMethod.getInstance());
	}
	private void getContent_url(final String json, final String sources){
		CharSequence text= Html.fromHtml(json, new Html.ImageGetter()
		{  public Drawable getDrawable(final String source) {
			//根据图片资源ID获取图片
			if(!sources.isEmpty()){
				//用于异步时刚好图片加载完成，不进入回调（较少几率发生）
				list_url.put(sources,null);
			}
			if(list_url.get(source)==null) {
//				XUtil.display_drawble(MyUrl.urlPic + source, false, new Callback.CommonCallback<Drawable>() {
//					@Override
//					public void onSuccess(Drawable result) {
//						list_url.put(source, result);
//						getContent(json);
//					}
//
//					@Override
//					public void onError(Throwable ex, boolean isOnCallback) {
//						list_url.put(source, getContext().getResources().getDrawable(R.mipmap.commodity_administration));
//						getContent(json);
//					}
//
//					@Override
//					public void onCancelled(CancelledException cex) {
//
//					}
//
//					@Override
//					public void onFinished() {
//
//					}
//				});
			}
			return null;
//                return XUtil.display_drawble(null,MyUrl.urlPic+source,false);
		}
		}, null);
		tv_content.setText(text);
	}
}
