 package com.laile.esf.integrate.spring;

 import com.laile.esf.integrate.config.ServiceConfig;
 import org.springframework.context.ApplicationContext;
 import org.springframework.context.ApplicationListener;
 import org.springframework.context.event.ContextRefreshedEvent;


 @Deprecated
 public class ServiceConsumerFactoryBean
   implements ApplicationListener<ContextRefreshedEvent>
 {
   private ApplicationContext appCtx;
   private ServiceConfig serviceConfig;
   private String protocol;
   
   public String getProtocol()
   {
     return this.protocol;
   }
   
   public void setProtocol(String protocol) {
     this.protocol = protocol;
   }
   
   public ApplicationContext getApplicationContext() {
     return this.appCtx;
   }
   
   public ServiceConfig getServiceConfig() {
     return this.serviceConfig;
   }
   
   public void setServiceConfig(ServiceConfig serviceConfig) {
     this.serviceConfig = serviceConfig;
   }
   
   public void onApplicationEvent(ContextRefreshedEvent event) {}
 }