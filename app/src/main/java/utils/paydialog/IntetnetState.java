package utils.paydialog;

import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.example.administrator.capacityhome.R;


/**
 * Created by TanBo on 2017/4/5.
 */

public class IntetnetState  {
    /**
     * 读取数据的显示
     * @param v
     * @param context
     */
    private Pay_Dialog pay_dialog;

    /**
     * 支付密码dialog
     * @param id
     * @param context
     * @param dm
     * @param onclick
     */
    public void showGetData_pay(int id, Context context, DisplayMetrics dm,Pay_Dialog.Onclick onclick,View view,int height){
        pay_dialog = new Pay_Dialog(context, R.style.loading_dialog,onclick);
        pay_dialog.show();
        WindowManager.LayoutParams lp = pay_dialog.getWindow().getAttributes();
        lp.width = (int)(dm.widthPixels); //设置宽度
        lp.gravity = Gravity.BOTTOM;
        lp.height = (int) (dm.heightPixels-view.getHeight()+context.getResources().getDimension(R.dimen.x24)-height);
       pay_dialog.getWindow().setAttributes(lp);
    }
    public void dismiss_pay(){
        pay_dialog.dismiss();
    }

    public Pay_Dialog getPay_dialog() {
        return pay_dialog;
    }

    public void dismissLinsener(final View view){
    pay_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            view.setClickable(true);
        }
    });
}

}
