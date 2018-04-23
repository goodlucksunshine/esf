package com.laile.esf.common.exception;

public class DatabaseException extends SystemException {
    private static final long serialVersionUID = -3348427634477409387L;

    public DatabaseException(ResultCode errCode) {
        super(errCode);
    }

    public DatabaseException(ResultCode errCode, Object... paramList) {
        super(errCode, paramList);
    }

    public DatabaseException(ResultCode errCode, Object param, Throwable cause) {
        super(errCode, param, cause);
    }

    public DatabaseException(ResultCode errCode, Object[] params, Throwable cause) {
        super(errCode, params, cause);
    }
}