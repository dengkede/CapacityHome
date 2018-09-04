package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by fatchao
 * 日期  2017-07-11.
 * 邮箱  fat_chao@163.com
 */

public class CityBean implements Serializable {
    /**
     *  "regionAcount": 0,
     "regionId": "202",
     "regionName": "十堰",
     "parentId": "18"
     */

    private String title;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private boolean isTitle;//是标题还是title
    private String pinyinFirst;//悬浮栏
    private String matchPin = "";//首字母缩写,武汉则为WH
    private String namePinYin = "";//武汉,WUHAN
    private String mark;
    private ArrayList<String> namePinyinList = new ArrayList<>();//名字拼音集合，比如武汉，WU,HAN

    public boolean isTitle() {
        return isTitle;
    }

    public void setTitle(boolean title) {
        isTitle = title;
    }




    public void setPinyinFirst(String pinyinFirst) {
        this.pinyinFirst = pinyinFirst;
    }

    public String getPinyinFirst() {
        return pinyinFirst;
    }

    public String getMatchPin() {
        return matchPin;
    }

    public void setMatchPin(String matchPin) {
        this.matchPin = matchPin;
    }

    public String getNamePinYin() {
        return namePinYin;
    }

    public void setNamePinYin(String namePinYin) {
        this.namePinYin = namePinYin;
    }


    public ArrayList<String> getNamePinyinList() {
        return namePinyinList;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
