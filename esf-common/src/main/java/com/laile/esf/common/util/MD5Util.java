package com.laile.esf.common.util;

import java.security.MessageDigest;

import org.apache.commons.lang3.StringUtils;

/**
 * @DateTime 2014年11月28日 下午2:11:10
 * @Company 华视传媒
 * @Author 刘兴密
 * @QQ 63972012
 * @Desc
 */
public class MD5Util {

    private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
            "e", "f" };

    /**
     * 转换字节数组为16进制字串
     * 
     * @param b
     *            字节数组
     * @return 16进制字串
     */
    public static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (byte aB : b) {
            resultSb.append(byteToHexString(aB));
        }
        return resultSb.toString();
    }

    /**
     * 转换byte到16进制
     * 
     * @param b
     *            要转换的byte
     * @return 16进制格式
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * MD5编码
     * 
     * @param origin
     *            原始字符串
     * @return 经过MD5加密之后的结果
     */
    public static String MD5(String origin) {
        String resultString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(origin.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;
    }

    public static String MD5(String origin, String encoding) {
        String resultString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(origin.getBytes(encoding)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;
    }

    /**
     * 密码加密生成
     * 
     * @param id
     * @param password
     * @param
     * @return
     * @throws Exception
     */
    public static String MD5Salf(String id, String password, String salt) throws Exception {
        try {

            if (StringUtils.isEmpty(salt) || StringUtils.isEmpty(password)) {
                throw new Exception("[getPassword][salf=" + salt + "][id=" + id + "][password=" + password + "]");
            }

            // 取Id的第一个数字
            int beginLen = getFirstNum(id);
            if (beginLen == 0) {
                // 取密码的长度
                beginLen = password.length();
            }
            if (beginLen > 10) {
                beginLen = 5;
            } else {
                beginLen = 10 - beginLen;
            }

            // 取Id的最后一个数字
            int endLen = getLastNum(id);
            if (endLen == 0) {
                // 取密码的长度
                endLen = password.length();
            }
            if (endLen > 10) {
                endLen = 5;
            } else {
                endLen = endLen / 2;
            }

            // 密码加密规则( 反转(xxx+md5(反转(随机码)+反转(pwd)+随机码)+base64(xxx)))
            password = MD5(StringUtil.reverse(salt) + StringUtil.reverse(password) + salt)
                    + Base64.byteArrayToBase64(salt.substring(endLen).getBytes("utf-8"));
            // 去除base64标识
            password = StringUtil.reverse(password.replaceAll("=", "").replace("\n", ""));

            // 对加密字符串所有数字减10
            String str = "";
            char[] charArray = password.toCharArray();
            for (char charStr : charArray) {
                if (Character.isDigit(charStr)) {
                    str += (10 - Integer.valueOf(String.valueOf(charStr)));
                } else {
                    str += charStr;
                }
            }
            password = str;

            // 将随机数插入到加密字符中
            String tmpStr = salt.substring(beginLen);
            StringBuffer buffer = new StringBuffer(password);
            // 是否奇数
            boolean isOddFlag = true;
            if (password.length() % 2 == 0) {
                isOddFlag = false;
            }
            char[] charArrayStr = tmpStr.toCharArray();
            int index = 0;
            for (int i = 0; i < charArrayStr.length; i++) {
                if (isOddFlag) {
                    index += 3;
                } else {
                    index += 2;
                }
                buffer.insert(index, charArrayStr[i]);
            }
            password = buffer.toString();
        } catch (Exception e) {
            // logger.error(e.getMessage(), e);
        }
        return password;
    }

    public static int getLastNum(String str) {
        try {
            char[] charArray = str.toCharArray();
            for (int i = charArray.length - 1; i >= 0; i--) {
                if (Character.isDigit(charArray[i]) && Integer.valueOf(String.valueOf(charArray[i])) > 0) {
                    return Integer.valueOf(String.valueOf(charArray[i]));
                }
            }
        } catch (Exception e) {
            // logger.error(e.getMessage(), e);
        }
        return 0;
    }

    public static int getFirstNum(String str) {
        try {
            char[] charArray = str.toCharArray();
            for (char c : charArray) {
                if (Character.isDigit(c) && Integer.valueOf(String.valueOf(c)) > 0) {
                    return Integer.valueOf(String.valueOf(c));
                }
            }
        } catch (Exception e) {
            // logger.error(e.getMessage(), e);
        }
        return 0;
    }
}
