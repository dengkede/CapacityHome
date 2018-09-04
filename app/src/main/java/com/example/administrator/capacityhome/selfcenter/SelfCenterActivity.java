package com.example.administrator.capacityhome.selfcenter;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.administrator.capacityhome.BaseActivity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.message.MessageActivity;

import org.json.JSONException;

import adapter.BankCardManageAdapter;

/**
 * 个人中心
 */
public class SelfCenterActivity extends BaseActivity implements View.OnClickListener {
    private int[] id = new int[]{R.id.relayout_basicInformation, R.id.relayout_addressmanage, R.id.relayout_bankCardmanage
            , R.id.relayout_payPassword, R.id.relayout_modifyPassword, R.id.relayout_requestWithdrawal, R.id.relayout_moneyDetail, R.id.relayout_messageCenter};//控件id
private TextView tv_selfCenter_recharge;
private TextView tv_selfCenter_balance;//余额
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_center);
        initView();
    }

    private void initView() {
        findViewById(R.id.top_submit_btn).setVisibility(View.GONE);
        tv_selfCenter_balance = (TextView) findViewById(R.id.tv_selfCenter_balance);
        try {
            if(MyApplication.getUserJson()!=null) {
                tv_selfCenter_balance.setText(MyApplication.getUserJson().getLong("money") / 1000 + "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setPageTitle("小e管家");
        backActivity();
        tv_selfCenter_recharge = (TextView) findViewById(R.id.tv_selfCenter_recharge);
        tv_selfCenter_recharge.setOnClickListener(this);
        for (int i = 0; i < id.length; i++) {
            findViewById(id[i]).setOnClickListener(this);
        }
        findViewById(R.id.tv_selfCenter_withdrawal).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relayout_basicInformation:
                //基本信息
                startActivity(this, BasicInfomationActivity.class);
                break;
            case R.id.relayout_addressmanage:
                //地址管理
                startActivity(this, AddressManageActivity.class);
                break;
            case R.id.relayout_bankCardmanage:
                //银行卡管理
                startActivity(this, BankCardManageActivity.class);
                break;
            case R.id.relayout_payPassword:
                //支付密码
                startActivity(this, PayPasswordActivity.class);
                break;
            case R.id.relayout_modifyPassword:
                startActivity(this, ModifyPasswordActivity.class);
                //修改密码
                break;
            case R.id.tv_selfCenter_withdrawal:
            case R.id.relayout_requestWithdrawal:
                //申请提现
                startActivity(this, RequestWithdrawalActivity.class);
                break;
            case R.id.relayout_moneyDetail:
                //资金明细
                Bundle bundle = new Bundle();
                bundle.putString("type", "money");
                bundle.putString("title","余额记录");
                startActivity_Bundle(this, MoneyDetailActivity.class, bundle);
                break;
            case R.id.relayout_messageCenter:
                //消息中心
                startActivity(this, MessageActivity.class);
                break;
            case R.id.tv_selfCenter_recharge:
                //充值
                startActivity(this,BalanceRechargeActivity.class);
                break;
        }
    }


}
