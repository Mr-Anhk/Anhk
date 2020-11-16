package com.anhk.common.utils;

import com.anhk.common.utils.constants.RegexPatterns;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 虎哥
 */
public class RegexUtils {
    /**
     * 是否符合手机格式
     *
     * @param phone 要校验的手机号
     * @return true:符合，false：不符合
     */
    public static boolean isPhone(String phone) {
        return matches(phone, RegexPatterns.PHONE_REGEX);
    }

    /**
     * 是否符合邮箱格式
     *
     * @param email 要校验的邮箱
     * @return true:符合，false：不符合
     */
    public static boolean isEmail(String email) {
        return matches(email, RegexPatterns.EMAIL_REGEX);
    }

    private static boolean matches(String str, String regex) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        return str.matches(regex);
    }

    /**
     * 验证用户名，支持中英文（包括全角字符）、数字、下划线和减号 （全角及汉字算两位）,长度为4-20位,中文按二位计数
     *
     * @param userName
     * @return
     */
    public static boolean validateUserName(String userName) {
        String validateStr = "^[\\w\\-－＿[０-９]\u4e00-\u9fa5\uFF21-\uFF3A\uFF41-\uFF5A]+$";
        boolean rs = false;
        rs = matcher(validateStr, userName);
        if (rs) {
            int strLenth = getStrLength(userName);
            if (strLenth < 4 || strLenth > 20) {
                rs = false;
            }
        }
        return rs;
    }

    /**
     * 获取字符串的长度，对双字符（包括汉字）按两位计数
     *
     * @param value
     * @return
     */
    public static int getStrLength(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
        }
        return valueLength;
    }

    private static boolean matcher(String reg, String string) {
        boolean tem = false;
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(string);
        tem = matcher.matches();
        return tem;
    }

    public static void main(String[] args) {
        String str = "０－＿ｆ９ｚｄ中22";
        String st = "Ａ-ｄｑ_!!！！去符号标号！ノチセたのひちぬ！当然。!!..**半角";

        System.out.println(validateUserName(str));
        System.out.println(st.replaceAll("[\\pP&&[^-_]]", ""));
        System.out.println(st.replaceAll("[\\w\\-一-龥Ａ-Ｚａ-ｚ]", ""));
        System.out.println("===========================");
        System.out.println(checkPassWord("aaa_123&"));
        System.out.println(getStrLength("贾彭飞"));
    }

    /**
     * 密码复杂度校验（同时包含数字，字母，特殊符号）
     *
     * @param val
     * @return
     */
    public static boolean checkPassWord(String val) {
        String reg = "^^(?![a-zA-z]+$)(?!\\d+$)(?![!@#$%^&*_-]+$)(?![a-zA-z\\d]+$)(?![a-zA-z!@#$%^&*_-]+$)(?![\\d!@#$%^&*_-]+$)[a-zA-Z\\d!@#$%^&*_-]+$";
        return val.matches(reg);
    }

}
