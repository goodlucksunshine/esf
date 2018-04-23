package com.laile.esf.common.exception;

/**
 * Created by sunshine on 16/7/22.
 */
public class BusinessException extends ServiceException {
    private static final long serialVersionUID = 1L;

    public BusinessException(ResultCode errCode) {
        super(errCode);
    }




    public BusinessException(String errCode, String errMsg, Throwable e) {
        super(errCode, errMsg, e);
    }

    public BusinessException(String errCode, String errMsg) {
        super(errCode, errMsg);
    }

    public BusinessException(ResultCode errCode, Object... paramList) {
        super(errCode, paramList);
    }

    public BusinessException(ResultCode errCode, Object param, Throwable cause) {
        super(errCode, param, cause);
    }

    public BusinessException(ResultCode errCode, Object[] params, Throwable cause) {
        super(errCode, params, cause);
    }

    public BusinessException(String thirdErrCode, ResultCode innerCode, Object... paramList) {
        super(thirdErrCode, innerCode, paramList);
    }

    public BusinessException(String thirdErrCode, String innerCode, Object... paramList) {
        super(thirdErrCode, innerCode, paramList);
    }
}
