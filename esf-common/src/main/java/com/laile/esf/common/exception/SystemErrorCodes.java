package com.laile.esf.common.exception;

public enum SystemErrorCodes implements ResultCode {
    SYSTEM_UNKOWN_ERROR("SSYS001"),
    RESOURCE_NOT_EXIST("SSYS002"),
    CONCURRENT_REQUEST_OVERLIMIT("SSYS003"),
    SEQUENCE_NOT_EXIST("SSYS004"),
    ILLEGAL_ENUM_VALUE("SSYS005"),
    INVALID_PARAM_VALUE("SSYS006"),
    INVALID_PARAM_VALUE2("SSYS007");

    private String errorCode;

    private SystemErrorCodes(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getCode() {
        return this.errorCode;
    }
}