package utils;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

/**
 * Created by TanBo on 2017/4/11.
 */

public class TimeUtilsDate {


    public static CharSequence matcherSearchText(int color, String string, String keyWord) {
        SpannableStringBuilder builder = new SpannableStringBuilder(string);
        int indexOf = string.indexOf(keyWord);
        if (indexOf != -1) {
            builder.setSpan(new ForegroundColorSpan(color), indexOf, indexOf + keyWord.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }
    /**
     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014-06-14  16:09:00"）
     *
     * @param time
     * @return
     */
    public static String timedate(long time) {

        Date nowTime = new Date(time*1000);
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String retStrFormatNowDate = sdFormatter.format(nowTime);
        return retStrFormatNowDate;

    }
    public static String timedate_week_hour(long time) {

        Date nowTime = new Date(time*1000);
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String retStrFormatNowDate = sdFormatter.format(nowTime);
        TimeUtils.changeweek(time+"");
        return retStrFormatNowDate+" "+TimeUtils.changeweek(time+"")+" "+Mill2(time);

    }
    public static String timedate_week_hour2(long time) {

        Date nowTime = new Date(time*1000);
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String retStrFormatNowDate = sdFormatter.format(nowTime);
        TimeUtils.changeweek(time+"");
        return retStrFormatNowDate+" "+Mill2(time)+" "+TimeUtils.changeweek(time+"");

    }
    /**
     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014-06-14  16:09:00"）
     *
     * @param time
     * @return
     */
    public static String timedate2(long time) {

        Date nowTime = new Date(time*1000);
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String retStrFormatNowDate = sdFormatter.format(nowTime);
        return retStrFormatNowDate;

    }
    public static String timedate3(long time) {

        Date nowTime = new Date(time*1000);
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String retStrFormatNowDate = sdFormatter.format(nowTime);
        return retStrFormatNowDate;

    }
    public static String timedate8(long time) {

        Date nowTime = new Date(time*1000);
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy/MM/dd");
        String retStrFormatNowDate = sdFormatter.format(nowTime);
        return retStrFormatNowDate;

    }
    public static String timedate4(long time) {

        Date nowTime = new Date(time*1000);
        SimpleDateFormat sdFormatter = new SimpleDateFormat("MM-dd");
        String retStrFormatNowDate = sdFormatter.format(nowTime);
        return retStrFormatNowDate;

    }
    public static String timedate5(long time) {

        Date nowTime = new Date(time*1000);
        SimpleDateFormat sdFormatter = new SimpleDateFormat("MM/dd");
        String retStrFormatNowDate = sdFormatter.format(nowTime);
        return retStrFormatNowDate;

    }

    public static String timedate9(long time) {

        Date nowTime = new Date(time*1000);
        SimpleDateFormat sdFormatter = new SimpleDateFormat("MM月dd日 HH:mm");
        String retStrFormatNowDate = sdFormatter.format(nowTime);
        return retStrFormatNowDate;

    }

    public static String timedate6(long time) {

        Date nowTime = new Date(time*1000);
        SimpleDateFormat sdFormatter = new SimpleDateFormat("HH点");
        String retStrFormatNowDate = sdFormatter.format(nowTime);
        return retStrFormatNowDate;

    }

    public static String timeday(String time) {
        long times = Long.parseLong(time);
        Date nowTime = new Date(times);
        SimpleDateFormat sdFormatter = new SimpleDateFormat("dd");
        String retStrFormatNowDate = sdFormatter.format(nowTime);
        return retStrFormatNowDate;

    }

    public static String timedate_year(String time) {
     long times = Long.parseLong(time);
        Date nowTime = new Date(times);
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy年MM月");
        String retStrFormatNowDate = sdFormatter.format(nowTime);
        return retStrFormatNowDate;

    }
    public static String timedate_year2(String time) {
        long times = Long.parseLong(time);
        Date nowTime = new Date(times);
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy年MM月dd日");
        String retStrFormatNowDate = sdFormatter.format(nowTime);
        return retStrFormatNowDate;

    }

    public static String timedateMill(long time) {
        Date nowTime = new Date(time*1000);
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String retStrFormatNowDate = sdFormatter.format(nowTime);
        return retStrFormatNowDate;

    }
    public static String Mill(long time) {

        Date nowTime = new Date(time*1000);
        SimpleDateFormat sdFormatter = new SimpleDateFormat("HH:mm:ss");
        String retStrFormatNowDate = sdFormatter.format(nowTime);
        return retStrFormatNowDate;

    }
    public static String Mill2(long time) {

        Date nowTime = new Date(time*1000);
        SimpleDateFormat sdFormatter = new SimpleDateFormat("HH:mm");
        String retStrFormatNowDate = sdFormatter.format(nowTime);
        return retStrFormatNowDate;

    }

    /**
     * 调此方法输入所要转换的时间输入例如（"2014-06-14-16-09-00"）返回时间戳
     *
     * @param time
     * @return
     */
    public static String dataOne(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd",
                Locale.CHINA);
        Date date;
        String times = time;
        try {
            date = sdr.parse(time);
            long l = date.getTime()/1000;
            return l+"";
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 选择时间
     */
    public static void initDatePicker(Context context, CustomDatePicker customDatePicker1, final TextView currentDate, final TimeLinsener timeLinsener) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        String s = currentDate.getText().toString();
        currentDate.setText(now.split(" ")[0]);
        customDatePicker1 = new CustomDatePicker(context, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                //currentDate.setText(time.split(" ")[0]);
                timeLinsener.getTime(time.split(" ")[0]);
            }
        }, "1940-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker1.showSpecificTime(false); // 不显示时和分
        customDatePicker1.setIsLoop(false); // 不允许循环滚动
        customDatePicker1.show(currentDate.getText().toString());
        currentDate.setText(s);
    }

    public interface TimeLinsener{
        void getTime(String s);
    }


    /**
     * 判断时间相隔多久
     * @param starTime
     */
    public static String time2(long starTime){
        long endTime = System.currentTimeMillis();
        String timeString = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //相隔多少分钟
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        long diff ;
        try {
            Date parse1 = dateFormat.parse(timestamp2Date(endTime+""));
            Date parse = dateFormat.parse(timestamp2Date(starTime+""));
            diff = parse1.getTime() - parse.getTime();
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
            if(day!=0)return day+"天"+"前";
            if(hour!=0)return hour+"小时"+"前";
            if(min!=0)return min+"分钟"+"前";
            return "刚刚";

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return timeString;

    }

    /**
     * 判断时间相隔多久
     * @param starTime
     */
    public static String time_evaluate(long starTime){
        long endTime = System.currentTimeMillis();
        String timeString = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //相隔多少分钟
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        long diff ;
        try {
            Date parse1 = dateFormat.parse(timestamp2Date(endTime+""));
            Date parse = dateFormat.parse(timestamp2Date(starTime+""));
            diff = parse1.getTime() - parse.getTime();
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
            if(day!=0) {
                return timedate9(starTime);
            }
            if(hour!=0)return hour+"小时"+"前";
            if(min!=0)return min+"分钟"+"前";
            return "刚刚";

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return timeString;

    }


    /**
     * 判断时间相隔多久
     * @param starTime
     */
    public static String time_day(long starTime){
        starTime = starTime*1000;
        long endTime = System.currentTimeMillis();
        String timeString = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //相隔多少分钟
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        long diff ;
        try {
            Date parse1 = dateFormat.parse(timestamp2Date(endTime+""));
            Date parse = dateFormat.parse(timestamp2Date(starTime+""));
            diff = parse1.getTime() - parse.getTime();
            day = diff / (24 * 60 * 60 * 1000);
            if(day!=0) {
                return day + "";
            }else{
                return 1 + "";
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return timeString;

    }


    public static String timestamp2Date(String str_num) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (str_num.length() == 13) {
            String date = sdf.format(new Date(toLong(str_num)));
            return date;
        } else {
            String date = sdf.format(new Date(toInt(str_num) * 1000L));
            return date;
        }
    }
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }
    /**
     * 对象转整
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(String obj) {
        if (obj == null)
            return 0;
        return Integer.parseInt(obj);
    }
    public static String getWeek(long timeStamp) {
        int mydate = 0;
        String week = null;
        Calendar cd = Calendar.getInstance();
        cd.setTime(new Date(timeStamp));
        mydate = cd.get(Calendar.DAY_OF_WEEK);
        // 获取指定日期转换成星期几
        if (mydate == 1) {
            week = "周日";
        } else if (mydate == 2) {
            week = "周一";
        } else if (mydate == 3) {
            week = "周二";
        } else if (mydate == 4) {
            week = "周三";
        } else if (mydate == 5) {
            week = "周四";
        } else if (mydate == 6) {
            week = "周五";
        } else if (mydate == 7) {
            week = "周六";
        }
        return week;
    }

    /**
     * 输入时间戳变星期
     *
     * @param time
     * @return
     */
    public static String changeweekOne(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        long lcc = Long.valueOf(time);
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        Date date = null;
        int mydate = 0;
        String week = null;
        try {
            date = sdr.parse(times);
            Calendar cd = Calendar.getInstance();
            cd.setTime(date);
            mydate = cd.get(Calendar.DAY_OF_WEEK);
            // 获取指定日期转换成星期几
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (mydate == 1) {
            week = "周日";
        } else if (mydate == 2) {
            week = "周一";
        } else if (mydate == 3) {
            week = "周二";
        } else if (mydate == 4) {
            week = "周三";
        } else if (mydate == 5) {
            week = "周四";
        } else if (mydate == 6) {
            week = "周五";
        } else if (mydate == 7) {
            week = "周六";
        }
        return week;

    }

}
