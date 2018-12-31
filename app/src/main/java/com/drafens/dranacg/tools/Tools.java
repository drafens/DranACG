package com.drafens.dranacg.tools;

import java.util.Calendar;

public class Tools {
    //获取当前时间
    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour_int = calendar.get(Calendar.HOUR_OF_DAY);
        String hour;
        if(hour_int<10)hour ="0"+hour_int;
        else hour =""+hour_int;
        int minute = calendar.get(Calendar.MINUTE);
        return "阅读于："+year+"-"+month+"-"+day+" "+hour+":"+minute;
    }
}
