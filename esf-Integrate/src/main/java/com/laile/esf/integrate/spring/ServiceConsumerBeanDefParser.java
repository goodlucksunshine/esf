 package com.laile.esf.integrate.spring;

 import java.util.Iterator;

 import com.laile.esf.integrate.ServiceProtocolComponentFactory;
 import com.laile.esf.integrate.config.ServiceConfig;
 import com.laile.esf.integrate.config.ServiceInfo;
 import com.laile.esf.integrate.ServiceProtocolComponent;
 import org.springframework.beans.factory.config.BeanDefinition;
 import org.springframework.beans.factory.xml.ParserContext;
 import org.w3c.dom.Element;

 public class ServiceConsumerBeanDefParser
   extends AbstractServiceFactoryBeanDefParser
 {
   protected void doParse(Element element, ParserContext parserContext, BeanDefinition beanDef)
   {
     beanDef.setBeanClassName(ServiceConsumerFactoryBean.class.getName());
   }
   
   protected void registerServiceBeans(ParserContext parserContext, ServiceConfig serviceConfig, String protocol, boolean tokenFlag)
   {
     ServiceProtocolComponent svcProtocolComp = ServiceProtocolComponentFactory.getServiceProtocolComponent(protocol);
     
 
     if (svcProtocolComp == null) {
       throw new IllegalArgumentException("未知的服务协议:" + protocol);
     }
     
     if (serviceConfig != null) {
       Iterator<ServiceInfo> serviceIt = serviceConfig.getServiceInfos().iterator();
       while (serviceIt.hasNext()) {
         svcProtocolComp.registerConsumerService(parserContext.getRegistry(), (ServiceInfo)serviceIt.next());
       }
     }
   }
 }