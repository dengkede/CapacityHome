package widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.administrator.capacityhome.R;


/**
 * Created by kedai on 2017/11/1.
 */

public class LoadingView2 extends ProgressDialog {
    private String content;
    private TextView tv_title;
    public LoadingView2(Context context) {
        super(context);
    }

    public LoadingView2(Context context, int theme,String content) {
        super(context, theme);
        this.content = content;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(getContext());
    }
    public void setContent(String title){
       // tv_title.setText(title);
    }

    private void init(Context context) {
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.loading_layout);//loading的xml文件
         tv_title = (TextView) findViewById(R.id.tv_load_dialog);
        tv_title.setText(content);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }
    @Override
    public void show() {//开启
        super.show();
    }
    @Override
    public void dismiss() {//关闭
        super.dismiss();
    }
}
