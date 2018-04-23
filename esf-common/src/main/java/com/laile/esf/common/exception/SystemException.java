package com.laile.esf.common.exception;

/**
 * Created by sunshine on 16/7/22.
 */
public class SystemException
        extends ServiceException {
    private static final long serialVersionUID = 8728570417116620956L;

    public SystemException(ResultCode errCode) {
        super(errCode);
    }

    public SystemException(String errCode, String errMsg, Throwable e) {
        super(errCode, errMsg, e);
    }

    public SystemException(String errCode, String errMsg) {
        super(errCode, errMsg);
    }

    public SystemException(String thirdErrCode, ResultCode innerCode, Object... paramList) {
        super(thirdErrCode, innerCode, paramList);
    }

    public SystemException(ResultCode errCode, Object param, Throwable cause) {
        super(errCode, param, cause);
    }

    public SystemException(ResultCode errCode, Object... paramList) {
        super(errCode, paramList);
    }

    public SystemException(ResultCode errCode, Object[] params, Throwable cause) {
        super(errCode, params, cause);
    }

    public SystemException(String thirdErrCode, String innerCode, Object... paramList) {
        super(thirdErrCode, innerCode, paramList);
    }
}