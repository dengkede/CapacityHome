package widget.show_html;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 当前类注释: TextView 实现图文混排
 * 公司：福泽通
 * ©版权所有，未经允许不得传播
 */
public class HtmlTextView extends AppCompatTextView {


    public HtmlTextView(Context context) {
        super(context);
    }

    public HtmlTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HtmlTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Parses String containing HTML to Android's Spannable format and displays
     * it in this TextView.
     *
     * @param html String containing HTML, for example: "<b>Hello world!</b>"
     */
    public void setHtmlFromString(String html, boolean useLocalDrawables) {
        Html.ImageGetter imgGetter;
        if (useLocalDrawables) {
            imgGetter = new LocalImageGetter(getContext());
        } else {
            imgGetter = new UrlImageGetter(this, getContext());
        }
        // this uses Android's Html class for basic parsing, and HtmlTagHandler
        setText(Html.fromHtml(html, imgGetter, new HtmlTagHandler()));

        // make links work
        setMovementMethod(LinkMovementMethod.getInstance());

        // no flickering when clicking textview for Android < 4, but overriders
        // color...
        // text.setTextColor(getResources().getColor(android.R.color.secondary_text_dark_nodisable));
    }

}
