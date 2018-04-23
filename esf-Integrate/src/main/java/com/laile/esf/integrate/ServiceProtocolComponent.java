package com.laile.esf.integrate;

import com.laile.esf.integrate.config.ServiceInfo;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public abstract interface ServiceProtocolComponent
{
  public abstract void registerConsumerService(BeanDefinitionRegistry paramBeanDefinitionRegistry, ServiceInfo paramServiceInfo);
  
  public abstract void registerProviderService(BeanDefinitionRegistry paramBeanDefinitionRegistry, ServiceInfo paramServiceInfo, boolean paramBoolean);
}