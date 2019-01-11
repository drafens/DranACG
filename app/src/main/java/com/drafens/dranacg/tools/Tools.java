package com.drafens.dranacg.tools;

import java.util.Calendar;
import java.util.List;

public class Tools {
    //获取当前时间
    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        int month_int = calendar.get(Calendar.MONTH) + 1;
        String month;
        if(month_int<10)month ="0"+month_int;
        else month =""+month_int;

        int day_int = calendar.get(Calendar.DAY_OF_MONTH);
        String day;
        if(day_int<10)day ="0"+day_int;
        else day =""+day_int;

        int hour_int = calendar.get(Calendar.HOUR_OF_DAY);
        String hour;
        if(hour_int<10)hour ="0"+hour_int;
        else hour =""+hour_int;

        int minute_int = calendar.get(Calendar.MINUTE);
        String minute;
        if (minute_int<10)minute ="0"+minute_int;
        else minute =""+minute_int;
        return "阅读于："+year+"-"+month+"-"+day+" "+hour+":"+minute;
    }

    static long strToInt(String string){
        string = string.trim();
        StringBuilder builder= new StringBuilder();
        if(!"".equals(string)) {
            for (int i = 0; i < string.length(); i++) {
                if (string.charAt(i) >= 48 && string.charAt(i) <= 57) {
                    builder.append(string.charAt(i));
                }
            }
        }
        return Long.parseLong(new String(builder));
    }

    public static String listToString(List<String> list) {
        if (list == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (String string : list) {
            if (first) {
                first = false;
            } else {
                result.append(",");
            }
            result.append(string);
        }
        return result.toString();
    }
}
