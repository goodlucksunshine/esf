package com.laile.esf.common.exception;

/**
 * Created by sunshine on 16/7/22.
 */
public abstract class ErrorMessageResource {
    private static ErrorMessageResource instance = null;

    public static ErrorMessageResource getInstance()
    {
        if (instance == null) {
            instance = new DefaultErrorMessageResource();
        }
        return instance;
    }

    public static ErrorMessageResource setGlobalInstance(ErrorMessageResource resource)
    {
        ErrorMessageResource oldInstance = instance;
        instance = resource;

        return oldInstance;
    }

    public abstract String getMessage(ResultCode paramErrorCode);

    public abstract String getMessage(String paramString);
}
