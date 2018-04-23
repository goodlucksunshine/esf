package com.laile.esf.integrate.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceInvocationHandler implements InvocationHandler {
    private ServiceInvokingProxy serviceInvokingProxy = null;

    private String interfaceClass = "";

    public ServiceInvokingProxy getServiceInvokingProxy() {
        return this.serviceInvokingProxy;
    }

    public void setServiceInvokingProxy(ServiceInvokingProxy serviceInvokingProxy) {
        this.serviceInvokingProxy = serviceInvokingProxy;
    }

    public String getInterfaceClass() {
        return this.interfaceClass;
    }

    public void setInterfaceClass(String interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?>[] argTypes = method.getParameterTypes();
        Object result = this.serviceInvokingProxy.invoke(this.interfaceClass, method.getName(), argTypes, args);
        return result;
    }
}