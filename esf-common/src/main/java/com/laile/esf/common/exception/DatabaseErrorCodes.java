package com.laile.esf.common.exception;

public enum DatabaseErrorCodes implements ResultCode {
    DUPLICATE_KEY("SDBE001"), UNKNOWN_ERROR("SDBE002"), CANT_GET_CONNECTION("SDBE003"), CONNECTION_TIMEOUT("SDBE004");

    private String code;

    private DatabaseErrorCodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}