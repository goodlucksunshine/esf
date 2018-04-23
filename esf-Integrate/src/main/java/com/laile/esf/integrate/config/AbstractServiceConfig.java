package com.laile.esf.integrate.config;

import java.util.Collection;
import java.util.HashMap;

import javax.xml.namespace.QName;

import com.laile.esf.integrate.exception.ServiceCreateException;

public abstract class AbstractServiceConfig implements ServiceConfig {
    private HashMap<QName, ServiceInfo> name2Services = new HashMap();

    private HashMap<Class<?>, ServiceInfo> cls2Services = new HashMap();

    public ServiceInfo getServiceInfo(QName serviceName) {
        return (ServiceInfo) this.name2Services.get(serviceName);
    }

    public ServiceInfo getServiceInfo(Class<?> serviceClass) {
        return (ServiceInfo) this.cls2Services.get(serviceClass);
    }

    public Collection<ServiceInfo> getServiceInfos() {
        return this.name2Services.values();
    }

    public void addService(ServiceInfo serviceInfo) {
        if (this.name2Services.containsKey(serviceInfo.getServiceQName())) {
            throw new ServiceCreateException("service " + serviceInfo.getServiceName().toString() + " has existed!");
        }

        if (this.cls2Services.containsKey(serviceInfo.getServiceClass())) {
            throw new ServiceCreateException(
                    "service class: " + serviceInfo.getServiceClass().toString() + " has existed!");
        }

        this.name2Services.put(serviceInfo.getServiceQName(), serviceInfo);
        this.cls2Services.put(serviceInfo.getServiceClass(), serviceInfo);
    }

    public void addService(Class<?> serviceClass) {
        addService(new ServiceInfo(serviceClass, null, null));
    }
}
