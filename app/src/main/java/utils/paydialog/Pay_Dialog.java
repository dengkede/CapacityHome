package utils.paydialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.selfcenter.PayPasswordActivity;


/**
 * @Description:自定义对话框
 * @author http://blog.csdn.net/finddreams
 */
public class Pay_Dialog extends Dialog implements View.OnClickListener{

	private Context mContext;
    private TextView[] textViews;//数字键盘数组
	private int[] textViewIds = new int[]{R.id.tv_1,R.id.tv_2,R.id.tv_3,R.id.tv_4,R.id.tv_5,R.id.tv_6,
			R.id.tv_7,R.id.tv_8,R.id.tv_9,R.id.tv_10};
	private RelativeLayout pay_dialog_delete;//删除dialog
	private ImageView img_delete;//删除单个密码
	private TextView[] textViews_password;
	private int[] textViews_passwordIds = new int[]{R.id.tv_password1,R.id.tv_password2,R.id.tv_password3,
			R.id.tv_password4,R.id.tv_password5,R.id.tv_password6,R.id.tv_password7,R.id.tv_password8,R.id.tv_password9,
            R.id.tv_password10,R.id.tv_password11,R.id.tv_password12};
	private int postion = -1;//输入密码位置
    private LinearLayout layout_passwordAdd;//添加密码框
    private  WindowManager m;
    private TextView tv_confirmPay;
    private Onclick onclick;
    private StringBuffer buffer = new StringBuffer();
	public Pay_Dialog(Context context, String content) {
		super(context);
		this.mContext = context;
		setCanceledOnTouchOutside(false);
	}

	public Pay_Dialog(Context context, int theme,Onclick onclick) {
		super(context, theme);
        this.onclick = onclick;
		this.mContext = context;
		setCanceledOnTouchOutside(false);

	}

    /**
     * 已输入密码长度
     */
 public int inputLenth(){
     return  postion+1;
 }
public String password(){
    return buffer.toString();
}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
        m = getWindow().getWindowManager();
	}

	private void initView() {
		setContentView(R.layout.layout_pay);
		textViews = new TextView[textViewIds.length];
		textViews_password = new TextView[textViews_passwordIds.length];
		for(int i = 0;i<textViewIds.length;i++){
			textViews[i] = (TextView) findViewById(textViewIds[i]);
			textViews[i].setOnClickListener(this);
		}
		for (int i =0;i<textViews_passwordIds.length;i++){
			textViews_password[i] = (TextView) findViewById(textViews_passwordIds[i]);
		}
        tv_confirmPay = (TextView) findViewById(R.id.tv_confirmPay);
		img_delete = (ImageView) findViewById(R.id.img_delete);
        layout_passwordAdd = (LinearLayout) findViewById(R.id.layout_passwordAdd);
        pay_dialog_delete = (RelativeLayout) findViewById(R.id.pay_dialog_delete);
        findViewById(R.id.tv_modifyPassword).setOnClickListener(this);
        img_delete.setOnClickListener(this);
        pay_dialog_delete.setOnClickListener(this);
        tv_confirmPay.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
        v.startAnimation(new AnimationUtils().loadAnimation(getContext(), R.anim.bg_alpha));
     switch (v.getId()){
		 case R.id.tv_1:
             chooseNumber(1);
		 	break;
         case R.id.tv_2:
             chooseNumber(2);
             break;
         case R.id.tv_3:
             chooseNumber(3);
             break;
         case R.id.tv_4:
             chooseNumber(4);
             break;
         case R.id.tv_5:
             chooseNumber(5);
             break;
         case R.id.tv_6:
             chooseNumber(6);
             break;
         case R.id.tv_7:
             chooseNumber(7);
             break;
         case R.id.tv_8:
             chooseNumber(8);
             break;
         case R.id.tv_9:
             chooseNumber(9);
             break;
         case R.id.tv_10:
            chooseNumber(0);
             break;
         case R.id.img_delete:
             //删除密码
//             if(postion>0&&postion<6){
//                 textViews_password[postion].setText("");
//                 postion = postion -1;
//                 buffer.deleteCharAt(buffer.length()-1);
//             }else if(postion>=6){
//                 layout_passwordAdd.removeViewAt(postion-6);
//                 postion = postion -1;
//                 buffer.deleteCharAt(buffer.length()-1);
//             }else if(postion == 0){
//                 textViews_password[postion].setText("");
//                 postion = postion -1;
//                 buffer.deleteCharAt(buffer.length()-1);
//             }
             if(buffer.toString().length()>0) {
                 textViews_password[postion].setText("");
                 postion = postion - 1;
                 buffer.deleteCharAt(buffer.length() - 1);
             }
             break;
         case R.id.pay_dialog_delete:
             //删除dialog
             this.dismiss();
             break;
         case R.id.tv_confirmPay:
             //确认
             if(postion>=5) {
                 onclick.OnClickLisener(v);
             }else{
                 Toast.makeText(getContext(),"请输入最少6位密码",Toast.LENGTH_SHORT).show();
             }
             break;
         case R.id.tv_modifyPassword:
             getContext().startActivity(new Intent(getContext(), PayPasswordActivity.class));
             break;
	 }

	}
  public interface Onclick{
       void OnClickLisener(View v);
   }

    /**
     * 填写密码
     */
    private void chooseNumber(int index){
        if(postion<11) {
            postion = postion + 1;
            textViews_password[postion].setText("" + index);
            buffer.append(index + "");
        }else{
           Toast.makeText(mContext,"密码最多12位。", Toast.LENGTH_SHORT).show();
        }
//        }else if(postion<11){
//            postion = postion + 1;
//            addView(index);
////        }else{
//           Toast.makeText(mContext,"密码最多12位。", Toast.LENGTH_SHORT).show();
//        }
}
private void addView(int number){
    // 动态添加布局
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,  LinearLayout.LayoutParams.WRAP_CONTENT);
    LayoutInflater inflater3 = LayoutInflater.from(getContext());
    final View view = inflater3.inflate(R.layout.paypassword_tv, null);
    TextView tv = (TextView) view.findViewById(R.id.tv_password);
    Display d = m.getDefaultDisplay();
    WindowManager.LayoutParams p = getWindow().getAttributes();
    lp.width = (int) (d.getWidth()-getContext().getResources().getDimension(R.dimen.x52)*2-getContext().getResources().getDimension(R.dimen.x20)*5)/6;
    if(postion>6){
        lp.leftMargin = (int) getContext().getResources().getDimension(R.dimen.x19);
    }
    view.setLayoutParams(lp);
    tv.setText(number+"");
    buffer.append(number+"");
    layout_passwordAdd.addView(view);
}
}
