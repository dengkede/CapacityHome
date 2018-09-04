package widget.show_html;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.x;


/**
 * 当前类注释: UrlImageGetter 加载网络图片
 * 公司：福泽通
 * ©版权所有，未经允许不得传播
 */
public class UrlImageGetter implements Html.ImageGetter {

    Context c;
    TextView container;
    int width ;

    /**
     *
     * @param t
     * @param c
     */
    public UrlImageGetter(TextView t, Context c) {
        this.c = c;
        this.container = t;
        width = c.getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public Drawable getDrawable(String source) {
        final UrlDrawable urlDrawable = new UrlDrawable();
        ImageView imageView = new ImageView(c);
     display(imageView, "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1496916120376&di=916bc033d397f67bd6b8432f11efcf7a&imgtype=0&src=http%3A%2F%2Fpassport.5857.com%2FUploads%2F77089%2F20170423%2Fdesk_20170423164423003.jpg", false,new Callback.CommonCallback<Bitmap>(){

            @Override
            public void onSuccess(Bitmap result) {
                float scaleWidth = ((float) width)/result.getWidth();
                // 取得想要缩放的matrix参数
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleWidth);
                result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix,
                        true);
                urlDrawable.bitmap = result;
                urlDrawable.setBounds(0, 0, result.getWidth(), result.getHeight());
                container.invalidate();
                container.setText(container.getText()); // 解决图文重叠
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(c,ex.toString(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(c,"".toString(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                Toast.makeText(c,"".toString(),1).show();
            }
        });

        return urlDrawable;
    }

    @SuppressWarnings("deprecation")
    public class UrlDrawable extends BitmapDrawable {
        protected Bitmap bitmap;
        @Override
        public void draw(Canvas canvas) {
            // override the draw to facilitate refresh function later
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, 0, 0, getPaint());
            }
        }
    }
    private void display(ImageView imageView, String iconUrl, boolean isCircluar,Callback.CommonCallback callback) {
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setCircular(isCircluar)
                .setCrop(true)
                .setUseMemCache(true)
//                .setLoadingDrawableId(R.mipmap.commodity_administration)
//                .setFailureDrawableId(R.mipmap.commodity_administration)
                .build();
        x.image().bind(imageView, iconUrl, imageOptions,callback);

    }

}
