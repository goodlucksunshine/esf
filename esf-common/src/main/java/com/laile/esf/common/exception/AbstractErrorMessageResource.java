package com.laile.esf.common.exception;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by sunshine on 16/7/22.
 */
public abstract class AbstractErrorMessageResource
        extends ErrorMessageResource
        implements InitializingBean, DisposableBean {
    private ErrorMessageResource oldInstance;
    private boolean autoRegister = false;
    private boolean registered = false;
    private ErrorMessageResource parent;

    public ErrorMessageResource getOldInstance() {
        return this.oldInstance;
    }

    public void setOldInstance(ErrorMessageResource oldInstance) {
        this.oldInstance = oldInstance;
    }

    public boolean isAutoRegister() {
        return this.autoRegister;
    }

    public void setAutoRegister(boolean autoRegister) {
        this.autoRegister = autoRegister;
    }

    public ErrorMessageResource getParent() {
        return this.parent;
    }

    public void setParent(ErrorMessageResource parent) {
        this.parent = parent;
    }

    protected abstract String doGetMessage(String paramString);

    public String getMessage(String errorCode) {
        String message = doGetMessage(errorCode);
        if ((message == null) && (this.parent != null)) {
            message = this.parent.getMessage(errorCode);
        }
        return message;
    }

    public String getMessage(ResultCode errorCode) {
        return getMessage(errorCode.getCode());
    }

    public void destroy()
            throws Exception {
        if (this.registered) {
            setGlobalInstance(this.oldInstance);
            this.registered = false;
        }
    }

    public void afterPropertiesSet()
            throws Exception {
        if (this.autoRegister) {
            this.oldInstance = setGlobalInstance(this);
            this.registered = true;
        }
    }
}