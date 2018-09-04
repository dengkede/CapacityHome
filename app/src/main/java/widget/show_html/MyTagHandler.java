package widget.show_html;

import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;

import org.xml.sax.XMLReader;

import java.util.Locale;

/**
 * Created by TanBo on 2017/6/13.
 */

public class MyTagHandler implements Html.TagHandler {
    private Context mContext;

    public MyTagHandler(Context context) {
        mContext = context;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        // 处理标签<img>
        if (tag.toLowerCase(Locale.getDefault()).equals("img")) {            // 获取长度
            int len = output.length();
// 获取图片地址
            ImageSpan[] images = output.getSpans(len - 1, len, ImageSpan.class);
            String imgURL = images[0].getSource();
            // 使图片可点击并监听点击事件
            output.setSpan(new ClickableImage(mContext, imgURL), len - 1, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private class ClickableImage extends ClickableSpan {
        private String url;
        private Context context;

        public ClickableImage(Context context, String url) {
            this.context = context;
            this.url = url;
        }

        @Override
        public void onClick(View widget) {
//            // 进行图片点击之后的处理
//            ImageView imageView = new ImageView(mContext);
//            XUtil.display(imageView, MyUrl.urlPic+url,false);
//            ImageViewAware imageViewAware = new ImageViewAware(imageView);
//            Thumb(imageView,imageViewAware,MyUrl.urlPic+url,MyUrl.urlPic+url);
//            imageViewAware = null;
//            imageView = null;
        }
    }
    /**
     * 进入缩放界面
     */
//    public void Thumb (ImageView thumb, ImageViewAware thumbAware, String url, String url_max){
//        Intent intent = new Intent(mContext, PicViewActivity.class).putExtra("url",url_max);
//        intent.putExtra("image", new FengNiaoImageSource(url_max,0,0,MyUrl.urlPic+url,thumbAware.getWidth(),thumbAware.getHeight()));
//        ImageSize targetSize = new ImageSize(thumbAware.getWidth(), thumbAware.getHeight());
//        String memoryCacheKey = MemoryCacheUtils.generateKey(url_max, targetSize);
//        intent.putExtra("cache_key", memoryCacheKey);
//        Rect rect = new Rect();
//        thumb.getGlobalVisibleRect(rect);
//        intent.putExtra("rect", rect);
//        intent.putExtra("scaleType", ImageView.ScaleType.CENTER_CROP);
//        mContext.startActivity(intent);
//    }
}