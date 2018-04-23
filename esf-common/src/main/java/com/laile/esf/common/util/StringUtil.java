package com.laile.esf.common.util;

import java.util.Arrays;

public class StringUtil {
    public static String fixStringLen(String str, char fillChar, int length) {
        int strLen = str.length();
        if (strLen < length) {
            char[] chars = new char[length];

            Arrays.fill(chars, 0, length - strLen, fillChar);
            System.arraycopy(str.toCharArray(), 0, chars, length - strLen, strLen);

            return new String(chars);
        }
        if (strLen > length) {
            str = str.substring(strLen - length);
        }

        return str;
    }

    public static String trim(String str) {
        if ((str == null) || (str.length() == 0)) {
            return null;
        }
        str = str.trim();
        if (str.length() == 0) {
            return null;
        }
        return str;
    }

    public static boolean isEmpty(String str) {
        if ((str == null) || (str.trim().equals(""))) {
            return true;
        }

        return false;
    }


    /**
     * 适应CJK（中日韩）字符集，部分中日韩的字是一样的
     */
    public static boolean isChinese2(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }


    /**
     * 判断是否含有特殊字符
     *
     * @param text
     * @return boolean true,通过，false，没通过
     */
    public static boolean hasSpecialChar(String text) {
        if (null == text || "".equals(text))
            return false;
        if (text.replaceAll("[a-z]*[A-Z]*\\d*-*_*\\s*", "").length() == 0) {
            // 如果不包含特殊字符
            return true;
        }
        return false;
    }

    public static String reverse(String str){
        return new StringBuilder(str).reverse().toString();
    }
}