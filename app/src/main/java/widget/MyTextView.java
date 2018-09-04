package widget;

/**
 * Created by Administrator on 2018/4/8.
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import utils.StringUtils;

public class MyTextView extends AppCompatTextView
{

    public MyTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void setSpecifiedTextsColor(String text, String specifiedTexts, int color)
    {
        List<Integer> sTextsStartList = new ArrayList<>();

        int sTextLength = specifiedTexts.length();
        String temp = text;
        int lengthFront = 0;//记录被找出后前面的字段的长度
        int start = -1;
        do
        {
            start = temp.indexOf(specifiedTexts);

            if(start != -1)
            {
                start = start + lengthFront;
                sTextsStartList.add(start);
                lengthFront = start + sTextLength;
                temp = text.substring(lengthFront);
            }

        }while(start != -1);

        SpannableStringBuilder styledText = new SpannableStringBuilder(text);
        for(Integer i : sTextsStartList)
        {
            styledText.setSpan(
                    new ForegroundColorSpan(color),
                    i,
                    i + sTextLength,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        setText(styledText);
    }

    public static void setTextviewColorAndBold(TextView textView, String key, String value,int corlor) {
        if (StringUtils.isEmpty(value)) {
            return;
        }
        if (!StringUtils.isEmpty(key)) {
            SpannableStringBuilder style = new SpannableStringBuilder(value);
            int index = value.indexOf(key);
            if (index >= 0) {
                while (index < value.length() && index >= 0) {
                    style.setSpan(new ForegroundColorSpan(Color.rgb(0, 187, 33)), index, index + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    style.setSpan(new StyleSpan(Typeface.BOLD), index, index + key.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textView.setText(style);
                    index = value.indexOf(key, index + key.length());
                }
            } else {
                textView.setText(value);
            }

        } else {
            textView.setText(value);
        }
    }

}
