package com.laile.esf.integrate.proxy;

import java.lang.reflect.Proxy;

public class ServiceProxyFactory<T> {
    public T createProxy(Class<T> serviceClass, ServiceInvokingProxy serviceInvokingProxy) {
        ServiceInvocationHandler serviceInvocationHandler = new ServiceInvocationHandler();
        serviceInvocationHandler.setServiceInvokingProxy(serviceInvokingProxy);
        serviceInvocationHandler.setInterfaceClass(serviceClass.getName());
        Class<?>[] interfaces = { serviceClass };
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces,
                serviceInvocationHandler);
    }
}
