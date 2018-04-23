package com.laile.esf.common.exception;

import com.laile.esf.common.javaenum.CheckStyle;

import java.util.Map;

/**
 * Created by sunshine on 16/7/25.
 */
public class
TipsBusinessException extends ServiceException {
    CheckStyle checkStyle;


    Map<String, String> exExtends;

    public TipsBusinessException(ResultCode errCode, CheckStyle checkStyle) {
        super(errCode);
        this.checkStyle = checkStyle;
    }

    public TipsBusinessException(ResultCode errCode, CheckStyle checkStyle, Map<String, String> exExtends) {
        super(errCode);
        this.checkStyle = checkStyle;
        this.exExtends = exExtends;
    }


    public TipsBusinessException(String errCode, String errMsg, CheckStyle checkStyle, Map<String, String> exExtends) {
        super(errCode,errMsg);
        this.checkStyle = checkStyle;
        this.exExtends = exExtends;
    }

    public TipsBusinessException(String errCode, String errMsg, CheckStyle checkStyle) {
        super(errCode,errMsg);
        this.checkStyle = checkStyle;
    }


    public Map<String, String> returnExvalue() {
        return exExtends;
    }

    public CheckStyle returnStyle() {
        return checkStyle;
    }

}
