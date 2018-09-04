package utils;

import java.text.DecimalFormat;

/**
 * des:金钱
 * Created by xsf
 * on 2016.06.11:48
 */
public class MoneyUtil {
    public static String MoneyFomatWithTwoPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(d);
    }

    /**
     * DecimalFormat转换最简便
     * 保留两位小数
     */
    public static String m2(double f) {
        if(!(f+"").contains(".")){
            return f+"";
        }else {
            DecimalFormat df = new DecimalFormat("#.00");
            return df.format(f);
        }
    }

}
