package widget;

/**
 * Created by deng on 2018/4/23.
 */

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

//实现监听器OnGlobalLayoutListener，监听图片是否加载完成
public class MyImageView extends AppCompatImageView implements OnGlobalLayoutListener, OnScaleGestureListener,OnTouchListener{

    private boolean mOnce;//判断是否初始化
    private float mInitScale;//初始化时缩放的值
    private float mMidScale;//双击放大到达的值
    private float mMaxScale;//放大的最大值

    private ScaleGestureDetector mScaleGestureDetector;//捕获用户多指触控缩放的比例

    private Matrix mScaleMatrix;
    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //init
        mScaleMatrix = new Matrix();
        setScaleType(ScaleType.MATRIX);

        mScaleGestureDetector = new ScaleGestureDetector(context, this);

        setOnTouchListener(this);
        //当图片加载时，图片可能很大，也可能很小，需要让图片自适应屏幕大小，当图片太大时自动缩小到屏幕大小，当图片太小时放大到屏幕大小。

    }

    public MyImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
        // TODO Auto-generated constructor stub
    }

    public MyImageView(Context context) {
        this(context,null);
        // TODO Auto-generated constructor stub
    }
    @Override
    protected void onAttachedToWindow() {
        // TODO Auto-generated method stub
        super.onAttachedToWindow();//当View 显示在屏幕上时调用
        getViewTreeObserver().addOnGlobalLayoutListener(this);//注册接口
    }
    @SuppressWarnings("deprecation")
    @Override
    protected void onDetachedFromWindow() {
        // TODO Auto-generated method stub
        super.onDetachedFromWindow();//当View从屏幕上移除时调用
        getViewTreeObserver().removeGlobalOnLayoutListener(this);//移除接口
    }
    /**
     * 获取ImageView加载完成的图片
     */
    @Override
    public void onGlobalLayout() {
        // 全局的布局完成后调用
        if(!mOnce){
            //得到控件的宽和高
            int width = getWidth();
            int height = getHeight();
            //得到我们的图片以及宽和高
            Drawable d = getDrawable();
            if(d == null)
                return;

            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();
            float scale = 1.0f;//缩放值
            //如果图片的宽度大于控件高度，但是宽度小于控件的宽度，将其缩小
            if(dw > width && dh < height){
                scale = width*1.0f/dw;
            }
            else if(dh > height && dw < width){
                scale = height*1.0f /dh;
            }

            else if(dw > width && dh > height){
                scale = Math.min(width*1.0f/dw, height*1.0f/dh);
            }
            else if(dw < width && dh < height){
                scale = Math.min(width *1.0f/dw, height*1.0f/dh);
            }
   /*
    * 得到初始化时缩放的比例
    * */

            mInitScale = scale;
            mMaxScale = mInitScale * 4;
            mMidScale = mInitScale * 2;

            //将图片移动到当前控件的中心
            int dx = getWidth()/2 - dw /2;
            int dy = getHeight()/2 - dh/2;

            mScaleMatrix.postTranslate(dx, dy);//平移
            mScaleMatrix.postScale(mInitScale, mInitScale,width/2,height/2);//缩放,后面两个参数是缩放的中心点
            setImageMatrix(mScaleMatrix);

            mOnce = true;





        }
    }
    /**
     * 获取当前图片的缩放值
     * @return
     */
    public float getScale(){
        float[] values = new float[9];
        mScaleMatrix.getValues(values);
        return values[Matrix.MSCALE_X];

    }
    //缩放的区间，initScale maxScale
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        // TODO Auto-generated method stub
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();//得到缩放的值

        if(getDrawable() == null){
            return true;
        }
        //缩放范围的控制
        if((scale < mMaxScale && scaleFactor > 1.0f) || (scale > mInitScale && scaleFactor < 1.0f)){
            if(scale * scaleFactor < mInitScale){
                scaleFactor = mInitScale / scale;//当手指缩放小于最小值时 ，默认显示最小的比例
            }
            if(scale * scaleFactor > mMaxScale){//当手指缩放大于于最大值时 ，默认显示最大的比例
                scale = mMaxScale/scale;
            }
            //缩放,缩放中心是手指触控的地方
            mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(),detector.getFocusY());
            checkBorderAndCenterWhenScale();

            setImageMatrix(mScaleMatrix);
        }
        return true;//设置完成返回true保证事件能够进行
    }

    /**
     * 获得图片放大缩小以后的宽和高以及l r t b
     * @return
     */
    private RectF getMatrixRectF(){
        Matrix matrix = mScaleMatrix;
        RectF recF = new RectF();
        Drawable d = getDrawable();
        if(d != null){
            recF.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(recF);
        }
        return recF;
    }
    /**
     * 在缩放的时候进行边界控制范围位置控制
     */

    private void checkBorderAndCenterWhenScale() {
        // TODO Auto-generated method stub
        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        float width = getWidth();
        float height = getHeight();
        //缩放时进行边界检测，放在出现白边
        if(rect.width() >= width){
            if(rect.left > 0){//处理左边的空白
                deltaX = -rect.left;
            }
            if(rect.right < width){//处理右边的空白
                deltaX = (int) (width - rect.right);
            }
        }
        if(rect.height() >= height){
            if(rect.top > 0){
                deltaY = -rect.top;
            }
            if(rect.bottom < height){
                deltaY = height - rect.bottom;
            }
        }
        //如果宽度或高度小于控件的宽或高，则让其居中
        if(rect.width() < width){
            deltaX = width/2f -rect.right + rect.width()/2f;
        }
        if(rect.height() < height){
            deltaY = height /2f -rect.bottom + rect.height()/2f;
        }
        mScaleMatrix.postTranslate(deltaX, deltaY);

    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        // TODO Auto-generated method stub
        return true;//必须返回true
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        mScaleGestureDetector.onTouchEvent(event);//把event传递给mscaleGestureDetector处理
        return true;//必须返true
    }
}