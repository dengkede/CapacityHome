package utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;


import com.example.administrator.capacityhome.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/29.
 * 选择器
 */

public class ChoosePop {
    static List<String> photoItems_photo;//相片选择
    static List<String> photoItems_sex;//性别选择
    static List<String> photoItems_grade;//年级选择
    public static void showPop( Context context,String type,String title, View view, RegisterPopupPicker popupPicker, RegisterPopupPicker.OnPopupItemSelectedListener listerner){
        if(popupPicker == null) {
            popupPicker = new RegisterPopupPicker((Activity)context);
            popupPicker.setHeadColor(ContextCompat.getColor(context, R.color.fontcolor_f6));
            popupPicker.showHeadText(true, title);
        }
        if(type.equals("sex")){
            //性别选择器
            if(photoItems_sex == null){
                photoItems_sex = new ArrayList<>();
                photoItems_sex.add("男");
                photoItems_sex.add("女");
            }
            popupPicker.startSelectPopupItem(view, photoItems_sex);
        }
        if(type.equals("photo")){
            if(photoItems_photo == null) {
                photoItems_photo = new ArrayList<>();
                photoItems_photo.add("拍照");
                photoItems_photo.add("相册");
            }
            popupPicker.startSelectPopupItem(view, photoItems_photo);
        }
        if(type.equals("grade")){
            if(photoItems_grade == null) {
                photoItems_grade = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    if(i==0){
                        photoItems_grade.add("幼儿级");
                    }else {
                        photoItems_grade.add(i + " 年级");
                    }
                }

            }
            popupPicker.startSelectPopupItem(view, photoItems_grade);
        }
        popupPicker.setPopupItemSelectedListener(listerner);

    }

    public static void showPop_list( Context context,String type,String title, View view, RegisterPopupPicker popupPicker, RegisterPopupPicker.OnPopupItemSelectedListener listerner,List<String> list){
        if(popupPicker == null) {
            popupPicker = new RegisterPopupPicker((Activity)context);
            popupPicker.setHeadColor(ContextCompat.getColor(context,R.color.fontcolor_f6));
            popupPicker.showHeadText(true, title);
        }

        popupPicker.startSelectPopupItem(view, list);
        popupPicker.setPopupItemSelectedListener(listerner);

    }

}
