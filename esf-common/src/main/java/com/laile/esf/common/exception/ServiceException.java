package com.laile.esf.common.exception;

import com.laile.esf.common.util.DateUtil;

import java.text.MessageFormat;
import java.util.Date;

/**
 * Created by sunshine on 16/7/22.
 */
public abstract class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String errMsg;
    private String plainErrMsg;
    private String thirdErrCode;
    private String innerErrCode;
    private String innerErrCodeCls;
    private transient ResultCode errCode;
    private Object[] params;

    public ServiceException(String thirdErrCode, String errMsg) {
        this(thirdErrCode, errMsg, (Throwable) null);
    }

    public ServiceException(String thirdErrCode, String errMsg, Throwable e) {
        this.plainErrMsg = errMsg;
        this.thirdErrCode = thirdErrCode;
        this.errMsg = ('[' + thirdErrCode + ']' + errMsg);
        super.initCause(e);
    }


    public ServiceException(String thirdErrCode, ResultCode innerCode, Object... paramList) {
        this.thirdErrCode = thirdErrCode;
        initInnerErrorCode(innerCode);
        initParamList(paramList);
    }

    public ServiceException(String thirdErrCode, String innerCode, Object... paramList) {
        this.thirdErrCode = thirdErrCode;
        this.innerErrCode = innerCode;
        initParamList(paramList);
    }

    public ServiceException(ResultCode errCode) {
        this(errCode, null, null);
    }

    public ServiceException(ResultCode errCode, Object... paramList) {
        initInnerErrorCode(errCode);
        initParamList(paramList);
    }

    public ServiceException(ResultCode errCode, Object param, Throwable cause) {
        this(errCode, new Object[]{param}, cause);
    }

    public ServiceException(ResultCode errCode, Object[] params, Throwable cause) {
        initInnerErrorCode(errCode);
        this.params = params;
        super.initCause(cause);
    }

    private void initInnerErrorCode(ResultCode errCode) {
        this.errCode = errCode;
        if (errCode != null) {
            this.innerErrCode = errCode.getCode();
            this.innerErrCodeCls = errCode.getClass().getCanonicalName();
        }
    }

    private void initParamList(Object... paramList) {
        if ((paramList != null) && (paramList.length > 0) && ((paramList[(paramList.length - 1)] instanceof Throwable))) {
            Object[] newParam = new Object[paramList.length - 1];
            System.arraycopy(paramList, 0, newParam, 0, newParam.length);
            this.params = newParam;
            super.initCause((Throwable) paramList[(paramList.length - 1)]);
        } else {
            this.params = paramList;
            super.initCause(null);
        }
    }

    public String getMessage() {
        if (this.errMsg == null) {
            this.errMsg = translateMessage(getCode(), getInnerErrorCode());
        }
        if (this.errMsg != null) {
            return this.errMsg;
        }
        return "异常，错误码＝" + getInnerErrorCode();
    }

    private String formatMsg(String msgPattern, Object[] params) {
        if (params == null) {
            return MessageFormat.format(msgPattern, params);
        }
        Object[] formatedParams = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            if ((params[i] instanceof Date)) {
                formatedParams[i] = DateUtil.format((Date) params[i], "yyyy-MM-dd");
            } else {
                formatedParams[i] = (params[i] == null ? "null" : params[i].toString());
            }
        }
        return MessageFormat.format(msgPattern, formatedParams);
    }

    private String translateMessage(String showErrCode, String msgErrCode) {
        if (msgErrCode == null) {
            if ((this.params != null) && (this.params.length > 0)) {
                StringBuilder sb = new StringBuilder(8);
                for (int i = 0; i < this.params.length; i++) {
                    sb.append("param").append(i).append("={},");
                }
                sb.deleteCharAt(sb.length() - 1).append('.');
                this.plainErrMsg = formatMsg(sb.toString(), this.params);
            } else {
                this.plainErrMsg = "未知错误";
                this.params = null;
            }
            return "[" + showErrCode + "]" + this.plainErrMsg;
        }
        ErrorMessageResource excepResource = ErrorMessageResource.getInstance();
        String msgPattern = excepResource.getMessage(msgErrCode);
        if (msgPattern != null) {
            try {
                this.plainErrMsg = formatMsg(msgPattern, this.params);
                return "[" + showErrCode + "]" + this.plainErrMsg;
            } catch (Exception e) {
                this.plainErrMsg = ("未知错误" + e.getMessage());
                return "错误码" + msgErrCode + "解析错误，错误消息格式配置为：" + msgPattern + "报错信息：" + e.getMessage();
            }
        }
        this.plainErrMsg = ("错误码" + msgErrCode + "]未识别的错误。");
        return null;
    }

    public String getPlainErrMsg() {
        if (this.plainErrMsg == null) {
            translateMessage(getCode(), getInnerErrorCode());
        }
        return this.plainErrMsg;
    }

    public ResultCode getErrorCode() {
        if (this.errCode != null) {
            return this.errCode;
        }
        if ((this.innerErrCodeCls == null) || (this.innerErrCode == null)) {
            return null;
        }
        ResultCode[] codes = null;
        try {
            Class<?> o = Class.forName(this.innerErrCodeCls);
            Object[] t = o.getEnumConstants();
            if ((t instanceof ResultCode[])) {
                codes = (ResultCode[]) t;
            }
        } catch (Throwable e) {
            return null;
        }
        if (codes == null) {
            return null;
        }
        for (ResultCode e : codes) {
            if (e.getCode().equals(this.innerErrCode)) {
                this.errCode = e;
                break;
            }
        }
        return this.errCode;
    }

    public String getInnerErrorCode() {
        return this.innerErrCode;
    }

    public String getThirdErrCode() {
        return this.thirdErrCode;
    }

    public String getCode() {
        return this.thirdErrCode != null ? this.thirdErrCode : this.innerErrCode;
    }
}
