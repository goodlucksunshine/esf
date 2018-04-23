 package com.laile.esf.integrate.spring;

 import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

 public class NamespaceHandler extends NamespaceHandlerSupport
 {
   public void init() {
     registerBeanDefinitionParser("serviceprovider", new SeviceProviderBeanDefParser());
     registerBeanDefinitionParser("serviceconsumer", new ServiceConsumerBeanDefParser());
   }
 }