package adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.capacityhome.BaseActivity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import http.RequestTag;
import utils.MyClickText;
import utils.StringUtils;
import utils.TimeUtilsDate;
import widget.CircleTransform;


public class RecommendAdv_evaluateAdapter extends PagerAdapter {
    LayoutInflater inflater;
    // 轮播图数据
    private List<JSONObject> imgUrl = new ArrayList<JSONObject>();
    private Context context;

    public RecommendAdv_evaluateAdapter(LayoutInflater inflater, List<JSONObject> imgUrl, Context context) {
        super();
        this.inflater = inflater;
        this.imgUrl = imgUrl;
        this.context = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return imgUrl.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View layout = inflater.inflate(R.layout.layout_allevaluate_item, null);
        final ImageView imageView = (ImageView) layout.findViewById(R.id.img_user);
        TextView tv_name = (TextView) layout.findViewById(R.id.tv_name);
        TextView tv_phone = (TextView) layout.findViewById(R.id.tv_phone);
        TextView tv_time = (TextView) layout.findViewById(R.id.tv_time);
        TextView tv_content = (TextView) layout.findViewById(R.id.tv_content);
//        if (position % 2 == 0) {
//            imageView.setImageResource(R.mipmap.img_user);
//        } else {
//            imageView.setImageResource(R.mipmap.img_user);
//        }
        try {
            if (!StringUtils.isEmpty(imgUrl.get(position).getString("name"))) {
                tv_name.setText(imgUrl.get(position).getString("name"));
            } else {
                tv_name.setText(imgUrl.get(position).getString("未设置"));
            }
            if (!StringUtils.isEmpty(imgUrl.get(position).getString("mobile"))) {
                tv_phone.setText(setString(imgUrl.get(position).getString("mobile")));
            } else {
                tv_content.setText("未设置");
            }
            tv_time.setText(TimeUtilsDate.timedate9(imgUrl.get(position).getLong("addtime")));
            if (!StringUtils.isEmpty(imgUrl.get(position).getString("pics"))&& !imgUrl.get(position).getString("pics").equals("null")) {
                tv_content.setText(imgUrl.get(position).getString("content") + "【" + imgUrl.get(position).getString("pics").split(",").length + "张图】- 轻触看图");
                setTextStyle(tv_content, imgUrl.get(position).getString("content"), imgUrl.get(position).getString("pics").split(",").length + "",position);
            } else {
                tv_content.setText(imgUrl.get(position).getString("content"));
            }
            Picasso.with(context).load(RequestTag.BaseImageUrl + imgUrl.get(position).getString("head")).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).transform(new CircleTransform()).into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    try {
                        Picasso.with(context).load(RequestTag.BaseImageUrl + MyApplication.getConfigrationJson().getJSONObject("other").getJSONArray("head").get(MyApplication.getUserJson().getInt("sex"))).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).transform(new CircleTransform()).into(imageView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {

        }

        ((ViewPager) container).addView(layout, 0);
//        try {
//            Picasso.with(MyApplication.getAppContext()).load(RequestTag.BaseImageUrl + imgUrl.get(position).getString("pic")).transform(new CircleTransform()).error(R.mipmap.img_user).placeholder(R.mipmap.default_iv).into(imageView);
//        }catch (Exception e){
//
//        }
        return layout;

    }

    /**
     * 设置部分内容颜色大小
     */
    private void setTextStyle(TextView tv, String s, String pics, final int position) {
        try {

            MyClickText.Click click = new MyClickText.Click() {
                @Override
                public void click(View v) {
                    ArrayList<String> lists = new ArrayList<>();
                    try {
                        for (int i = 0; i < imgUrl.get(position).getString("pics").split(",").length; i++) {
                            lists.add(imgUrl.get(position).getString("pics").split(",")[i]);
                        }
                        ((BaseActivity) context).startActivity_ImagrPager(context, 0, lists, false);
                    }catch (Exception e){

                    }
                }
            };
//        SpannableString string = new SpannableString(s+"风萧萧兮易水寒，壮士一去兮不复返！");
            SpannableString string = new SpannableString(s + "【" + pics + "张图】- 轻触看图");

//        string.setSpan(
//
//                new TextAppearanceSpan(this, R.style.style0),
//
//                0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            string.setSpan(new MyClickText(context, click), string.length() - s.length() + 1, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setMovementMethod(LinkMovementMethod.getInstance());//不设置 没有点击事件
            string.setSpan(

                    new TextAppearanceSpan(context, R.style.style1),

                    s.length(), s.length() + pics.length() + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            string.setSpan(

                    new TextAppearanceSpan(context, R.style.style0),

                    string.length() - 6, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(string, TextView.BufferType.SPANNABLE);
        }catch (Exception e){

        }
    }

    /**
     * 把某几位变为不可见
     */
    private String setString(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (i >= 3 && i <= s.length() - 4) {
                if (sb.toString().lastIndexOf('*') < 5) {
                    sb.append('*');
                }
            } else {
                sb.append(c);
            }
        }
//        StringBuilder builder = new StringBuilder();
//        for (int i = 1; i <= sb.length(); i++) {
//            if (i % 4 == 0) {
//                builder.append(sb.charAt(i - 1) + " ");
//            } else {
//                builder.append(sb.charAt(i - 1));
//            }
//        }
        return sb.toString();
    }
}