package fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.capacityhome.R;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.tsy.sdk.myokhttp.MyOkHttp;

import butterknife.ButterKnife;
import widget.LoadingView;

/**
 * Created by Administrator on 2018/3/28.
 */

public class BaseFragment extends RxFragment {

    private ProgressDialog progressDialog;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
//            if (UserToken.getInstance().getUserModel() == null) {
//                UserToken.getInstance().setUserModel((UserModel) savedInstanceState.getSerializable("static_user"));
//            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
       // outState.putSerializable("static_user", UserToken.getInstance().getUserModel());
    }

    protected void showLoadingView(){

        if(progressDialog == null){
            progressDialog = new LoadingView(getActivity(), R.style.loading_dialog_style);
        }
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//默认
        //progressDialog.setCancelable(true);
        progressDialog.show();
    }

    protected void dismissLoadingView(){
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    public <T extends View>T findViewById(int resId) {

        try {
            View view = getView().findViewById(resId);
            if(view == null)
                view = getActivity().findViewById(resId);
            return (T)view;
        } catch(Exception ex) {
            return null;
        }
    }

    protected void setText(int resId, String text) {
        TextView textView = findViewById(resId);
        textView.setText(text);
    }

    protected void setText(int resId, int textId) {
        TextView textView = findViewById(resId);
        textView.setText(textId);
    }

    protected void toastMessage(String msg) {
        if(getActivity() ==null){
            return ;
        }
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    protected void toastMessage(int msg) {
        if(getActivity() ==null){
            return ;
        }
        Toast.makeText(getActivity(), getActivity().getResources().getString(msg), Toast.LENGTH_SHORT).show();
    }

    protected void setPageTitle(String title) {
        setText(R.id.page_title, title);
    }

    protected void setPageTitle(int resId) {
        setText(R.id.page_title, resId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyOkHttp.get().cancel(getActivity());

    }
}