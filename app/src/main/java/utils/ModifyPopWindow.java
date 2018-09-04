package utils;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.capacityhome.R;

/**
 * Created by Administrator on 2018/4/8.
 * 修改类型popwinow公用
 */

public class ModifyPopWindow {
    public static PopupWindow showPopwindow(View parent, final Activity activity, PopupWindow popWindow, View popView, String title, final Click click,String content,boolean isHint) {
            int width = activity.getResources().getDisplayMetrics().widthPixels/10*8;
            popWindow = new PopupWindow(popView, width, LinearLayout.LayoutParams.WRAP_CONTENT);
            popWindow.setAnimationStyle(R.style.popwin_anim_style);
            popWindow.setFocusable(true);
            popWindow.setOutsideTouchable(false);// 设置同意在外点击消失
            ColorDrawable dw = new ColorDrawable(0x30000000);
            popWindow.setBackgroundDrawable(dw);
        TextView tv_title = (TextView) popView.findViewById(R.id.tv_title);
        final EditText et_content = (EditText) popView.findViewById(R.id.et_content);
        TextView tv_sure = (TextView) popView.findViewById(R.id.tv_sure);
        TextView tv_cancel = (TextView) popView.findViewById(R.id.tv_cancel);
        tv_title.setText(title);
        if(isHint){
            et_content.setHint(content);
        }else {
            et_content.setText(content);
            et_content.setSelection(content.length());
        }
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_content.getText().toString().isEmpty()){
                    Toast.makeText(activity,"请先输入修改内容",Toast.LENGTH_SHORT).show();
                }else {
                    click.click(v, et_content.getText().toString());
                }
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.click(v,et_content.getText().toString());
            }
        });
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                  DisplayUtil.backgroundAlpha(1.0f,activity);
            }
        });
        initPop(popWindow,parent,activity);
        // DisplayUtil.backgroundAlpha(0.2f, OrderDetailActivity.this);
        //popWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        return  popWindow;
    }

    public static void initPop(PopupWindow popWindow,View parent,Activity activity){
        DisplayUtil.backgroundAlpha(0.3f, activity);
        popWindow.showAtLocation(parent, Gravity.CENTER_VERTICAL| Gravity.CENTER_HORIZONTAL, 0, 0);

    }
public interface Click{
    void click(View view, String content);
}


}
