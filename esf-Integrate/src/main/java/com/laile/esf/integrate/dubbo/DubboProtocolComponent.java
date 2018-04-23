package com.laile.esf.integrate.dubbo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.laile.esf.common.util.StringUtil;
import com.laile.esf.integrate.dubbo.container.LsfLogbackContainer;
import com.laile.esf.integrate.config.OperationInfo;
import com.laile.esf.integrate.exception.ServiceCreateException;
import com.laile.esf.integrate.proxy.ServiceProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import com.alibaba.dubbo.config.MethodConfig;
import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.alibaba.dubbo.config.spring.ServiceBean;
import com.laile.esf.integrate.ServiceProtocolComponent;
import com.laile.esf.integrate.config.ServiceInfo;

public class DubboProtocolComponent implements ServiceProtocolComponent {
    private static Logger logger;

    private static final String PROXY_FACTORY_BEAN_ID = "ESF_PROXY_FACTORY_BEAN";

    private static final String PROXY_FACTORY_METHOD = "createProxy";

    private static Random random = new Random();

    public DubboProtocolComponent() {
        LsfLogbackContainer.getInstance().start();
        logger = LoggerFactory.getLogger(DubboProtocolComponent.class);
    }

    public void registerConsumerService(BeanDefinitionRegistry registry, ServiceInfo serviceInfo) {
        logger.debug("register consumer service:" + serviceInfo.getServiceName() + " to dubbo");

        if (StringUtil.isEmpty(serviceInfo.getProxy())) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ReferenceBean.class);
            BeanDefinition beanDef = builder.getRawBeanDefinition();
            String beanId = serviceInfo.getServiceName();

            beanDef.setLazyInit(false);
            beanDef.setScope("singleton");
            beanDef.getPropertyValues().add("interfaceClass", serviceInfo.getServiceClass());

            beanDef.getPropertyValues().add("cluster", "failfast");

            beanDef.getPropertyValues().add("check", "false");
            if (serviceInfo.getTimeout() > 0) {
                beanDef.getPropertyValues().add("timeout", Integer.valueOf(serviceInfo.getTimeout() * 1000));
            }

            if (serviceInfo.getVersion() != null) {
                beanDef.getPropertyValues().add("version", serviceInfo.getVersion());
            }
            List<MethodConfig> methodConfigList = generateMethodConfig(serviceInfo);
            beanDef.getPropertyValues().add("methods", methodConfigList);

            if (serviceInfo.getActives() > 0) {
                beanDef.getPropertyValues().add("actives", Integer.valueOf(serviceInfo.getActives()));
            }

            registry.registerBeanDefinition(beanId, beanDef);
        } else {
            try {
                if (!registry.containsBeanDefinition("ESF_PROXY_FACTORY_BEAN")) {
                    GenericBeanDefinition proxyBeanFactoryDef = new GenericBeanDefinition();
                    proxyBeanFactoryDef.setBeanClass(ServiceProxyFactory.class);
                    proxyBeanFactoryDef.setLazyInit(false);
                    proxyBeanFactoryDef.setScope("singleton");
                    registry.registerBeanDefinition("ESF_PROXY_FACTORY_BEAN", proxyBeanFactoryDef);
                }

                BeanDefinitionBuilder builder = BeanDefinitionBuilder
                        .genericBeanDefinition(serviceInfo.getServiceClass());

                AbstractBeanDefinition beanDef = builder.getRawBeanDefinition();
                String beanId = serviceInfo.getServiceName();
                beanDef.setLazyInit(false);
                beanDef.setScope("singleton");
                beanDef.setFactoryBeanName("ESF_PROXY_FACTORY_BEAN");
                beanDef.setFactoryMethodName("createProxy");

                ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
                constructorArgumentValues.addGenericArgumentValue(serviceInfo.getServiceClass());
                constructorArgumentValues.addGenericArgumentValue(new RuntimeBeanReference(serviceInfo.getProxy()));
                beanDef.setConstructorArgumentValues(constructorArgumentValues);

                registry.registerBeanDefinition(beanId, beanDef);
            } catch (Exception e) {
                throw new ServiceCreateException("找不到代理对象，BeanId=" + serviceInfo.getProxy(), e);
            }
        }
    }

    private List<MethodConfig> generateMethodConfig(ServiceInfo serviceInfo) {
        Iterator<OperationInfo> operIt = serviceInfo.getOperationIterator();
        ArrayList<MethodConfig> methodConfigList = new ArrayList();
        while (operIt.hasNext()) {
            OperationInfo operInfo = (OperationInfo) operIt.next();
            MethodConfig methodConfig = new MethodConfig();

            methodConfig.setName(operInfo.getOperationName());
            if (operInfo.isAsync()) {
                methodConfig.setAsync(Boolean.TRUE);
            }

            if (operInfo.isOneWay()) {
                methodConfig.setReturn(Boolean.valueOf(false));
            } else {
                methodConfig.setReturn(Boolean.valueOf(true));
            }

            if (operInfo.getTimeout() > 0) {
                methodConfig.setTimeout(Integer.valueOf(operInfo.getTimeout()));
            }

            methodConfigList.add(methodConfig);
        }

        return methodConfigList;
    }

    public void registerProviderService(BeanDefinitionRegistry registry, ServiceInfo serviceInfo, boolean tokenFlag) {
        logger.debug("register provider service:" + serviceInfo.getServiceName() + " to dubbo");

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ServiceBean.class);
        BeanDefinition beanDef = builder.getRawBeanDefinition();
        String beanId = serviceInfo.getServiceName();

        beanDef.setLazyInit(false);
        beanDef.setScope("singleton");
        beanDef.getPropertyValues().add("interfaceClass", serviceInfo.getServiceClass());

        beanDef.getPropertyValues().add("cluster", "failfast");

        if (serviceInfo.getVersion() != null)
            beanDef.getPropertyValues().add("version", serviceInfo.getVersion());
        beanDef.getPropertyValues().add("ref", new RuntimeBeanReference(serviceInfo.getImplementor()));
        if (tokenFlag) {
            beanDef.getPropertyValues().add("token", newToken());
        }
        List<MethodConfig> methodConfigList = generateMethodConfig(serviceInfo);
        beanDef.getPropertyValues().add("methods", methodConfigList);
        if (serviceInfo.isValidation()) {
            beanDef.getPropertyValues().add("validation", "true");
        }

        if (serviceInfo.getActives() > 0) {
            beanDef.getPropertyValues().add("actives", Integer.valueOf(serviceInfo.getActives()));
        }

        if (serviceInfo.getExecutes() > 0) {
            beanDef.getPropertyValues().add("executes", Integer.valueOf(serviceInfo.getExecutes()));
        }

        registry.registerBeanDefinition(beanId, beanDef);
    }

    private String newToken() {
        return String.valueOf(Math.abs(random.nextInt()));
    }
}