package com.laile.esf.integrate.config;

import java.lang.reflect.Method;

import javax.xml.namespace.QName;

public class OperationInfo {
    private ServiceInfo serviceInfo;

    private String operationName;

    private Method oprMethod;

    private int timeout;

    private boolean async;

    private boolean oneWay;

    public OperationInfo(ServiceInfo serviceInfo, Method m) {
        if (m == null) {
            throw new IllegalArgumentException("method could't be null!");
        }

        this.serviceInfo = serviceInfo;
        this.oprMethod = m;
        this.operationName = m.getName();
    }

    public int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isAsync() {
        return this.async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public void setOneWay(boolean oneWay) {
        this.oneWay = oneWay;
    }

    public ServiceInfo getServiceInfo() {
        return this.serviceInfo;
    }

    public String getOperationName() {
        return this.operationName;
    }

    public QName getOperationQName() {
        return new QName(this.serviceInfo.getNamespaceURI(), this.operationName);
    }

    public boolean isOneWay() {
        return this.oneWay;
    }

    public Method getMethod() {
        return this.oprMethod;
    }
}