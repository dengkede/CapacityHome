package widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.example.administrator.capacityhome.R;

/**
 * Created by zhang on 2018/8/25.
 */


public class BannerRadiusImageView extends android.support.v7.widget.AppCompatImageView {

    private float mRadus = getContext().getResources().getDimensionPixelSize(R.dimen.x12);

    /*圆角的半径，依次为左上角xy半径，右上角，右下角，左下角*/
    private float[] rids = {mRadus, mRadus, mRadus, mRadus, mRadus, mRadus, mRadus, mRadus};

    public BannerRadiusImageView(Context context) {
        super(context);
    }

    public BannerRadiusImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BannerRadiusImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * 画图
     * @param canvas
     */
    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        int w = this.getWidth();
        int h = this.getHeight();
        /*向路径中添加圆角矩形。radii数组定义圆角矩形的四个圆角的x,y半径。radii长度必须为8*/
        path.addRoundRect(new RectF(0,0,w,h),rids, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }

    /**
     * 设置ImageView为正方形
     */
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
//        int childWidthSize = getMeasuredWidth();
//        // 高度和宽度一样
//        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }
}

