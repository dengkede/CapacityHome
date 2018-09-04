package widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by 毛麒添 on 2017/2/23 0023.
 * 自定义侧滑菜单控件
 */

public class MySlideMenu extends FrameLayout {


    private View leftMenu;//左边栏对象
    private View mainMenu;//主界面对象

    private ViewDragHelper viewDragHelper;
    private int width;
    private float dragRange;//拖拽范围

    private FloatEvaluator floatEvaluator;//浮点数计算器
    private IntEvaluator intEvaluator;//整数计算器

    private DragState currentState=DragState.STATE_CLOSE;//默认是关闭状态

    /**
     * 枚举侧滑菜单的开关状态
     */
    public enum DragState{
        STATE_OPEN,STATE_CLOSE
    }

    public MySlideMenu(Context context) {
        super(context);
        init();
    }

    public MySlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MySlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }



    private void init(){
        viewDragHelper=ViewDragHelper.create(this,callback);
        floatEvaluator=new FloatEvaluator();
        intEvaluator=new IntEvaluator();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //让viewDragHelper帮助我们判断是否拦截
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //让触摸事件交给viewDragHelper来处理
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //检测控件异常
        if(getChildCount()!=2){
            throw new IllegalArgumentException("MySlideMenu only have two childView!");
        }
        leftMenu = getChildAt(0);
        mainMenu = getChildAt(1);
    }

    /**
     * 该方法在onMeasure执行完成后执行，可以在该方法中初始化自己和子View的宽高
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getMeasuredWidth();
        dragRange = width*0.8f;
    }

    private ViewDragHelper.Callback callback=new ViewDragHelper.Callback() {

        /**
         * 用于判断是否捕获当前child的触摸事件
         * @param child 当前触摸的子View
         * @param pointerId
         * @return true:就捕获并解析 false：不处理
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child==leftMenu||child==mainMenu;
        }

        /**
         * 获取view水平方向的拖拽范围,但是目前不能限制边界,返回的值目前用在手指抬起的时候view缓慢移动的动画世界的计算上面; 最好不要返回0
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return (int) dragRange;
        }
        /**
         * 控制child在水平方向的移动
         * @param child
         * @param left 表示ViewDragHelper认为你想让当前child的left改变的值,left=child.getLeft()+dx
         * @param dx 本次child水平方向移动的距离
         * @return 表示你真正想让child的left变成的值
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if(child==mainMenu){
                if(left<0) left=0;//限制左边界
                if(left>dragRange)left= (int) dragRange;//限制右边界
            }
            return left;
        }

        /**
         * 当child的位置改变的时候执行,一般用来做其他子View的伴随移动
         * changedView：位置改变的child
         * left：child当前最新的left
         * top: child当前最新的top
         * dx: 本次水平移动的距离
         * dy: 本次垂直移动的距离
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
           if(changedView==leftMenu){
               //固定侧滑菜单
               leftMenu.layout(0,0,mainMenu.getMeasuredWidth(),mainMenu.getMeasuredHeight());
               int newLeft=mainMenu.getLeft()+dx;
               if(newLeft<0) newLeft=0;//限制左边界
               if(newLeft>dragRange) newLeft= (int) dragRange;//限制右边界
               //两个菜单一起伴随滑动
               mainMenu.layout(newLeft,mainMenu.getTop()+dy,mainMenu.getRight()+dx,mainMenu.getBottom()+dy);
           }
            //计算滑动百分比
            float fraction=mainMenu.getLeft()/dragRange;
            //执行伴随动画
            executeAnim(fraction);
            //根据百分比来值来确定侧滑菜单是打开还是关闭
            if(fraction==0&&currentState!=DragState.STATE_CLOSE){//如果百分比是0，且当前状态不是关闭
                currentState=DragState.STATE_CLOSE;
                //调用回调方法
                listener.onClose();
            }else if(fraction==1&&currentState!=DragState.STATE_OPEN){
                currentState=DragState.STATE_OPEN;
                //调用回调方法
                listener.onOpen();
            }
            if(listener!=null){
                //只要listener存在，就将百分比暴露出去
                listener.Draging(fraction);
            }
        }

        /**
         * 手指抬起的执行该方法，
         * releasedChild：当前抬起的view
         * xvel:x方向的移动的速度 正：向右移动， 负：向左移动
         * yvel: y方向移动的速度  正：向上移动， 负：向下移动
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
           if(mainMenu.getLeft()<dragRange/2){//在拖拽范围的左边，关闭
               close();
           }else {//在拖拽范围的右边，打开
               open();
           }
            //当用户稍微滑动一下，根据X轴方向的速度来打开或者关闭侧滑菜单
            if(xvel>200&&currentState!=DragState.STATE_OPEN){
                open();
            }else if(xvel<-200&&currentState!=DragState.STATE_CLOSE){
                close();
            }
       }
    };
    //侧滑面板打开
    public void open() {
        viewDragHelper.smoothSlideViewTo(mainMenu, (int) dragRange,mainMenu.getTop());
        ViewCompat.postInvalidateOnAnimation(MySlideMenu.this);//刷新
    }
    //侧滑面板关闭
    public void close() {
        viewDragHelper.smoothSlideViewTo(mainMenu,0,mainMenu.getTop());
        ViewCompat.postInvalidateOnAnimation(MySlideMenu.this);//刷新
    }

    private void executeAnim(float fraction) {
        //移动侧边栏
        ViewHelper.setTranslationX(leftMenu,intEvaluator.evaluate(fraction,-leftMenu.getMeasuredWidth()/2,0));
        //放大侧边栏
        ViewHelper.setScaleX(leftMenu,floatEvaluator.evaluate(fraction,0.5f,1f));
        ViewHelper.setScaleY(leftMenu,floatEvaluator.evaluate(fraction,0.5f,1f));
        //改变侧边栏的透明度
        ViewHelper.setAlpha(leftMenu,floatEvaluator.evaluate(fraction,0.3f,1f));
        //给侧边栏背景添加黑色遮罩效果
        getBackground().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_OVER);
    }

    @Override
    public void computeScroll() {
        if(viewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(MySlideMenu.this);//刷新
        }
    }

    /**
     * 获取侧滑面板的状态
     */
    public DragState getDragState(){
        return currentState;
    }

    /**
     * 设置外部监听回调
     */
    private onDragStateChangeListener listener;

    public void setOnDragStateChangeListener(onDragStateChangeListener listener){
        this.listener=listener;
    }

    public interface onDragStateChangeListener{
        /**
         * 侧滑菜单打开
         */
        void onOpen();

        /**
         * 侧滑菜单处于关闭
         */
        void onClose();

        /**
         *正在拖拽,将此时的百分比随时暴露给调用者
         */
        void Draging(float fraction);
    }


}
