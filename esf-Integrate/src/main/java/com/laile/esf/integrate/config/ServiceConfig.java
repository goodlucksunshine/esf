package com.laile.esf.integrate.config;

import java.util.Collection;

import javax.xml.namespace.QName;

public abstract interface ServiceConfig
{
  public abstract Collection<ServiceInfo> getServiceInfos();
  
  public abstract ServiceInfo getServiceInfo(QName paramQName);
  
  public abstract ServiceInfo getServiceInfo(Class<?> paramClass);
}