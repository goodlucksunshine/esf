package com.laile.esf.common.annotation.validator;


import static com.laile.esf.common.javaenum.RegexType.NONE;

import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.laile.esf.common.exception.BusinessException;
import com.laile.esf.common.exception.ResultCode;
import com.laile.esf.common.exception.SystemException;
import com.laile.esf.common.util.StringUtil;
import com.laile.esf.common.util.Tools;

/**
 * 注解解析
 *
 * @author Goofy
 */
public class Validator {
    private static final Logger logger = LoggerFactory.getLogger(Validator.class);

    public Validator() {
        super();
    }

    //解析的入口
    public static void valid(Object object) throws SystemException {
        //获取object的类型
        Class<? extends Object> clazz = object.getClass();
        //获取该类型声明的成员
        Field[] fields = clazz.getDeclaredFields();
        //遍历属性
        for (Field field : fields) {
            //对于private私有化的成员变量，通过setAccessible来修改器访问权限
            field.setAccessible(true);
            validate(field, object);
            //重新设置会私有权限
            field.setAccessible(false);
        }
    }


    private static void validate(Field field, Object object) {

        String description;
        Object value;

        //获取对象的成员的注解信息
        ColumnCheck columnCheck = field.getAnnotation(ColumnCheck.class);
        try {
            value = field.get(object);
        } catch (Exception e) {
            logger.error("valid object by annotation error", e);
            throw new SystemException(ResultCode.COMMON_ERROR, "系统异常,请稍后重试");
        }
        if (columnCheck == null) return;

        description = columnCheck.description().equals("") ? field.getName() : columnCheck.description();

        /*************注解解析工作开始******************/
        if (!columnCheck.nullable()) {
            if (value == null || StringUtils.isBlank(value.toString())) {
                throw new BusinessException(ResultCode.COMMON_ERROR, description + "不能为空");
            }
            checkMaxLength(columnCheck, value, description);
            checkMinLength(columnCheck, value, description);
            checkRegexType(columnCheck, value, description);
            checkExpression(columnCheck, value, description);
        } else {
            if (!checkNull(value)) {
                checkMaxLength(columnCheck, value, description);
                checkMinLength(columnCheck, value, description);
                checkRegexType(columnCheck, value, description);
                checkExpression(columnCheck, value, description);
            }
        }


        /*************注解解析工作结束******************/
    }


    private static void checkMaxLength(ColumnCheck columnCheck, Object value, String description) {
        if (value.toString().length() > columnCheck.maxLength() && columnCheck.maxLength() != 0) {
            throw new BusinessException(ResultCode.COMMON_ERROR, description + "长度不能超过" + columnCheck.maxLength());
        }
    }

    private static void checkMinLength(ColumnCheck columnCheck, Object value, String description) {
        if (value.toString().length() < columnCheck.minLength() && columnCheck.minLength() != 0) {
            throw new BusinessException(ResultCode.COMMON_ERROR, description + "长度不能小于" + columnCheck.minLength());
        }
    }

    private static void checkMinValue(ColumnCheck columnCheck, Object value, String description) {
        if (columnCheck.minValue() != 0) {
            try {
                Integer covert = (Integer) value;
                if (covert < columnCheck.minValue()) {
                    throw new BusinessException(ResultCode.COMMON_ERROR, description + "值不能小于" + columnCheck.minValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException(ResultCode.COMMON_ERROR, description + "值不能小于" + columnCheck.minValue());
            }
        }
    }

    private static void checkMaxValue(ColumnCheck columnCheck, Object value, String description) {
        if (columnCheck.maxValue() != 0) {
            try {
                Integer covert = (Integer) value;
                if (covert > columnCheck.minValue()) {
                    throw new BusinessException(ResultCode.COMMON_ERROR, description + "值不能小于" + columnCheck.minValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException(ResultCode.COMMON_ERROR, description + "值不能小于" + columnCheck.minValue());
            }
        }
    }

    private static void checkRegexType(ColumnCheck columnCheck, Object value, String description) {
        if (columnCheck.regexType() != NONE) {
            switch (columnCheck.regexType()) {
                case NONE:
                    break;
                case SPECIALCHAR:
                    if (StringUtil.hasSpecialChar(value.toString())) {
                        throw new BusinessException(ResultCode.COMMON_ERROR, description + "不能含有特殊字符");
                    }
                    break;
                case CHINESE:
                    if (StringUtil.isChinese2(value.toString())) {
                        throw new BusinessException(ResultCode.COMMON_ERROR, description + "不能含有中文字符");
                    }
                    break;
                case EMAIL:
                    if (!Tools.isEmail(value.toString())) {
                        throw new BusinessException(ResultCode.COMMON_ERROR, description + "地址格式不正确");
                    }
                    break;
                case IP:
                    if (!Tools.isIp(value.toString())) {
                        throw new BusinessException(ResultCode.COMMON_ERROR, description + "地址格式不正确");
                    }
                    break;
                case NUMBER:
                    if (!Tools.isNumber(value.toString())) {
                        throw new BusinessException(ResultCode.COMMON_ERROR, description + "不是数字");
                    }
                    break;
                case MOBILE:
                    if (!Tools.isMobile(value.toString())) {
                        throw new BusinessException(ResultCode.COMMON_ERROR, description + "格式不正确");
                    }
                    break;
                case TELPHONE:
                    if (!Tools.isTelPhone(value.toString())) {
                        throw new BusinessException(ResultCode.COMMON_ERROR, description + "格式不正确");
                    }
                case DECIMAL:
                    if (!Tools.isDecimal(value.toString())) {
                        throw new BusinessException(ResultCode.COMMON_ERROR, description + "格式不正确");
                    }
                default:
                    break;
            }
        }
    }

    private static void checkExpression(ColumnCheck columnCheck, Object value, String description) {
        if (StringUtils.isNotEmpty(columnCheck.regexExpression())) {
            if (!value.toString().matches(columnCheck.regexExpression())) {
                throw new BusinessException(ResultCode.COMMON_ERROR, description + "格式不正确");
            }
        }
    }


    private static boolean checkNull(Object o) {
        if (o == null || StringUtils.isBlank(o.toString())) {
            return true;
        } else {
            return false;
        }
    }
}
