package com.baizhitong.spider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static String getPattern(String str, String regex,Integer index) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group(index);
        } else {
            return "";
        }
    }
    public static String getPattern(String str, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group(0);
        } else {
            return "";
        }
    }
    public static String formatter(String str, Object... objects) {
        Pattern pattern = Pattern.compile("\\{\\d\\}");
        Matcher matcher = pattern.matcher(str);
        int index = 0;
        while (matcher.find()) {
            String matchStr = matcher.group(0);
            str = str.replace(matchStr, objects[index].toString());
            index++;
        }
        return str;
    }
}
