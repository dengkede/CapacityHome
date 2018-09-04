package utils;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

/**
 * Created by 81014 on 2018/8/31.
 * 设置指定textview内容点击
 */

public class MyClickText extends ClickableSpan {
    private Context context;
     private Click click;
    public MyClickText(Context context,Click click) {
        this.context = context;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        //设置文本的颜色
        //超链接形式的下划线，false 表示不显示下划线，true表示显示下划线
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(View widget) {
//        Toast.makeText(context,"发生了点击效果",Toast.LENGTH_SHORT).show();
        click.click(widget);
    }
    public interface Click{
        void click(View v);
    }

}
