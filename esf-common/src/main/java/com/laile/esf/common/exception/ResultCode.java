package com.laile.esf.common.exception;

/**
 * Created by sunshine on 16/7/22.
 */
public interface ResultCode {
    /**
     * 失败编码
     */
    public static final String COMMON_ERROR = "999999";
    public static final String COMMON_SUCCESS = "000000";
    public static final String QUICK_NOT_BIND = "000001";
    public static final String HTTP_TIMEOUT = "HTTP001";
    public static final String SOCKET_TIMEOUT = "SOCKET001";
    public String getCode();
}
