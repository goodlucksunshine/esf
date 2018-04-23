package com.laile.esf.common.util;

import com.laile.esf.common.exception.ResultCode;
import com.laile.esf.common.exception.BusinessException;
import com.laile.esf.common.exception.SystemException;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class Describe
 * <p/>
 * User: yangguang Date: 16/10/9 Time: 下午3:50
 */
public class ValidatorUtil {

    /**
     * 正整数或者小数(小数点后不大于2位)
     */
    public static final String REG_INTEGER_OR_DECIMAL = "^[0-9]+\\.{0,1}[0-9]{0,2}$";

    /**
     * 小数 小数点后不能大于3位
     */
    public static final String REG_DECIMAL = "^[0]+\\.{0,1}[0-9]{0,3}$";

    /**
     * 正整数
     */
    public static final String REG_INTEGER = "^[0-9]*$";

    /**
     * 非零正整数
     */
    public static final String REG_INTEGER_GT_ZERO = "^\\+?[1-9][0-9]*$";

    /**
     * 验证是否是汉字
     */
    public static final String REG_CHINESE = "^[\u4e00-\u9fa5]{0,}$";

    /**
     * 固定电话
     * 正确格式为："XXX-XXXXXXX"、"XXXX-XXXXXXXX"、"XXX-XXXXXXX"、"XXX-XXXXXXXX"、"XXXXXXX"和"XXXXXXXX"。
     */
    public static final String REG_TELEPTHONE = "^(\\(\\d{3,4}-)|\\d{3.4}-)?\\d{7,8}$";

    /**
     * 身份证号码校验
     *
     * @param IDStr
     *
     * @return
     *
     * @throws ParseException
     */
    public static void IDCardValidate(String IDStr) throws ParseException {
        String errorInfo = "";// 记录错误信息
        String[] ValCodeArr = {"1", "0", "x", "9", "8", "7", "6", "5", "4", "3", "2"};
        String[] Wi = {"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2"};
        String Ai = "";
        // ================ 号码的长度 15位或18位 ================
        if (IDStr.length() != 15 && IDStr.length() != 18) {
            throw new BusinessException(ResultCode.COMMON_ERROR, "请输入正确的身份证号码");
        }
        // =======================(end)========================

        // ================ 数字 除最后以为都为数字 ================
        if (IDStr.length() == 18) {
            Ai = IDStr.substring(0, 17);
        } else if (IDStr.length() == 15) {
            Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
        }
        if (isNumeric(Ai) == false) {
            throw new BusinessException(ResultCode.COMMON_ERROR, "请输入正确的身份证号码");
        }
        // =======================(end)========================

        // ================ 出生年月是否有效 ================
        String strYear = Ai.substring(6, 10);// 年份
        String strMonth = Ai.substring(10, 12);// 月份
        String strDay = Ai.substring(12, 14);// 月份
        if (isDate(strYear + "-" + strMonth + "-" + strDay) == false) {
            throw new BusinessException(ResultCode.COMMON_ERROR, "请输入正确的身份证号码");
        }
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
                    || (gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
                throw new BusinessException(ResultCode.COMMON_ERROR, "请输入正确的身份证号码");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            throw new BusinessException(ResultCode.COMMON_ERROR, "请输入正确的身份证号码");
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            throw new BusinessException(ResultCode.COMMON_ERROR, "请输入正确的身份证号码");
        }
        // =====================(end)=====================

        // ================ 地区码时候有效 ================
        Hashtable h = GetAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
            throw new BusinessException(ResultCode.COMMON_ERROR, "请输入正确的身份证号码");
        }
        // ==============================================

        // ================ 判断最后一位的值 ================
        int TotalmulAiWi = 0;
        for (int i = 0; i < 17; i++) {
            TotalmulAiWi = TotalmulAiWi + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);
        }
        int modValue = TotalmulAiWi % 11;
        String strVerifyCode = ValCodeArr[modValue];
        Ai = Ai + strVerifyCode;

        if (IDStr.length() == 18) {
            if (Ai.equals(IDStr) == false) {
                throw new BusinessException(ResultCode.COMMON_ERROR, "请输入正确的身份证号码");
            }
        } else {
            throw new BusinessException(ResultCode.COMMON_ERROR, "请输入正确的身份证号码");
        }
        // =====================(end)=====================
    }

    /**
     * 功能：设置地区编码
     *
     * @return Hashtable 对象
     */
    @SuppressWarnings("unchecked")
    private static Hashtable GetAreaCode() {
        Hashtable hashtable = new Hashtable();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }

    /**
     * 功能：判断字符串是否为日期格式
     *
     * @param strDate
     *
     * @return
     */
    public static boolean isDate(String strDate) {
        Pattern pattern = Pattern.compile(
                "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        Matcher m = pattern.matcher(strDate);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /*********************************** 身份证验证结束 ****************************************/

    /**
     * 正则匹配
     *
     * @param str   校验值
     * @param regex 校验正则表达式
     *
     * @return 校验结果
     */
    public static boolean matcher(String str, String regex) {
        if (StringUtil.isEmpty(str) || StringUtil.isEmpty(regex)) {
            throw new SystemException(ResultCode.COMMON_ERROR, "parpm str and regex can't be empty");
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否为浮点数或者整数
     *
     * @param str
     *
     * @return true Or false
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("^(-?\\d+)(\\.\\d+)?$");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println(matcher("0.00",REG_INTEGER_OR_DECIMAL));
    }

    /**
     * 判断是否为正确的邮件格式
     *
     * @param str
     *
     * @return boolean
     */
    public static boolean isEmail(String str) {
        if (StringUtils.isEmpty(str))
            return false;
        return str.matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$");
    }

    /**
     * 判断字符串是否为合法手机号 11位 13 14 15 18开头
     *
     * @param str
     *
     * @return boolean
     */
    public static boolean isMobile(String str) {
        if (StringUtils.isEmpty(str))
            return false;
        return str.matches("^(13|14|15|18|17)\\d{9}$");
    }
}
