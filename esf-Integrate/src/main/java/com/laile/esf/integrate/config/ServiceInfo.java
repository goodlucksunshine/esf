package com.laile.esf.integrate.config;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.namespace.QName;


public class ServiceInfo {
    private QName serviceQName;
    private Class<?> serviceClass;
    private String implementor;
    private HashMap<String, OperationInfo> operations = new HashMap();

    private HashMap<Method, OperationInfo> methodMap = new HashMap();

    private int timeout;

    private boolean validation = false;

    private String version;

    private String proxy;

    private int executes;
    private int actives;

    public ServiceInfo(Class<?> serviceClass, QName serviceQName, String implementor) {
        if (serviceClass == null) {
            throw new IllegalArgumentException("Service class could't be null!");
        }

        if (serviceQName == null) {
            serviceQName = new QName(getNamespaceURI(serviceClass), getServiceName(serviceClass));
        }

        String localName = serviceQName.getLocalPart();
        String nameSpaceURI = serviceQName.getNamespaceURI();
        boolean changeFlag = false;
        if ((localName == null) || (localName.isEmpty())) {
            localName = getServiceName(serviceClass);
            changeFlag = true;
        }

        if ((nameSpaceURI == null) || (nameSpaceURI.equals(""))) {
            nameSpaceURI = getNamespaceURI(serviceClass);
            changeFlag = true;
        }

        if (changeFlag) {
            serviceQName = new QName(nameSpaceURI, localName);
        }

        if ((implementor == null) || (implementor.isEmpty())) {
            this.implementor = (localName + "Impl");
        } else {
            this.implementor = implementor;
        }

        this.serviceClass = serviceClass;
        this.serviceQName = serviceQName;

        generateOperationInfos(serviceClass);
    }

    private void generateOperationInfos(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();

        for (int i = 0; i < methods.length; i++) {
            int mod = methods[i].getModifiers();
            if ((Modifier.isPublic(mod)) && (!Modifier.isStatic(mod)) && (!methods[i].isSynthetic())) {
                WebMethod publishAnnotation = (WebMethod) methods[i].getAnnotation(WebMethod.class);
                if ((publishAnnotation == null) || (!publishAnnotation.exclude())) {

                    OperationInfo oprInfo = generateOperationInfo(methods[i]);

                    if (!this.operations.containsKey(oprInfo.getOperationName())) {


                        this.operations.put(oprInfo.getOperationName(), oprInfo);
                        this.methodMap.put(methods[i], oprInfo);
                    }
                }
            }
        }
        if (clazz.isInterface()) {
            Class<?>[] superIntfs = clazz.getInterfaces();
            for (int i = 0; i < superIntfs.length; i++) {
                generateOperationInfos(superIntfs[i]);
            }
        } else {
            Class<?> superClass = clazz.getSuperclass();
            if ((superClass != null) && (superClass != Object.class)) {
                generateOperationInfos(superClass);
            }
        }
    }

    private OperationInfo generateOperationInfo(Method m) {
        return new OperationInfo(this, m);
    }

    public String getNamespaceURI() {
        return this.serviceQName.getNamespaceURI();
    }

    public String getServiceName() {
        return this.serviceQName.getLocalPart();
    }

    public QName getServiceQName() {
        return this.serviceQName;
    }

    public Class<?> getServiceClass() {
        return this.serviceClass;
    }

    public OperationInfo getOperationInfo(String operationName) {
        return (OperationInfo) this.operations.get(operationName);
    }

    public OperationInfo getOperationInfo(Method m) {
        return (OperationInfo) this.methodMap.get(m);
    }


    public String getImplementor() {
        return this.implementor;
    }


    public void setImplementor(String implementor) {
        this.implementor = implementor;
    }

    public Iterator<OperationInfo> getOperationIterator() {
        return this.operations.values().iterator();
    }

    protected String getNamespaceURI(Class<?> clazz) {
        WebService webService = (WebService) clazz.getAnnotation(WebService.class);
        if (webService != null) {
            return webService.targetNamespace();
        }

        String className = clazz.getName();
        String[] parts = className.split("\\.");
        StringBuffer namespaceURI = new StringBuffer();

        namespaceURI.append("http://");
        for (int i = parts.length - 2; i >= 0; i--) {
            namespaceURI.append(parts[i]);
            if (i != 0) {
                namespaceURI.append(".");
            }
        }

        namespaceURI.append("/");

        return namespaceURI.toString();
    }

    protected String getServiceName(Class<?> clazz) {
        WebService webService = (WebService) clazz.getAnnotation(WebService.class);
        if (webService != null) {
            return webService.name();
        }

        String className = clazz.getSimpleName();
        return className.substring(0, 1).toLowerCase() + className.substring(1);
    }

    public int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isValidation() {
        return this.validation;
    }

    public void setValidation(boolean validation) {
        this.validation = validation;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProxy() {
        return this.proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }


    public int getExecutes() {
        return this.executes;
    }


    public void setExecutes(int executes) {
        this.executes = executes;
    }


    public int getActives() {
        return this.actives;
    }


    public void setActives(int actives) {
        this.actives = actives;
    }
}
