package com.laile.esf.integrate.spring;

import java.io.IOException;

import com.laile.esf.integrate.config.ServiceConfig;
import com.laile.esf.integrate.config.XmlServicesConfigurator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.w3c.dom.*;

public abstract class AbstractServiceFactoryBeanDefParser implements BeanDefinitionParser {
    protected void parseChildElement(Element element, ParserContext parserContext, BeanDefinition beanDef) {
        String elemName = element.getLocalName();
        if (elemName.equals("property")) {
            parserContext.getDelegate().parsePropertyElement(element, beanDef);
        }
    }

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        GenericBeanDefinition beanDef = new GenericBeanDefinition();
        NodeList nodes = element.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node childNode = nodes.item(i);
            if (childNode.getNodeType() == 1) {
                parseChildElement((Element) childNode, parserContext, beanDef);
            }
        }

        XmlServicesConfigurator xmlServiceConfig = new XmlServicesConfigurator();
        String protocol = "dubbo";

        boolean tokenFlag = false;

        NamedNodeMap atts = element.getAttributes();
        Attr protocolAttr = (Attr) atts.getNamedItem("protocol");
        if (protocolAttr != null) {
            protocol = protocolAttr.getValue();
        }

        Attr tokenAttr = (Attr) atts.getNamedItem("tokenflag");
        if (tokenAttr != null) {
            tokenFlag = Boolean.parseBoolean(tokenAttr.getValue());
        }
        Attr configAttr = (Attr) atts.getNamedItem("config");
        if (configAttr != null) {
            PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(
                    parserContext.getReaderContext().getResourceLoader());
            try {
                Resource[] configResources = resourcePatternResolver.getResources(configAttr.getValue());
                xmlServiceConfig.setConfigResources(configResources);
                xmlServiceConfig.loadConfigs();
            } catch (IOException e) {
                throw new IllegalArgumentException("解析服务配置异常", e);
            }
        }

        beanDef.getPropertyValues().addPropertyValue("protocol", protocol);
        beanDef.getPropertyValues().addPropertyValue("serviceConfig", xmlServiceConfig);

        doParse(element, parserContext, beanDef);

        beanDef.setLazyInit(false);
        beanDef.setScope("singleton");

        String id = parserContext.getReaderContext().generateBeanName(beanDef);
        parserContext.getRegistry().registerBeanDefinition(id, beanDef);

        registerServiceBeans(parserContext, xmlServiceConfig, protocol, tokenFlag);

        return beanDef;
    }

    protected abstract void doParse(Element paramElement, ParserContext paramParserContext,
            BeanDefinition paramBeanDefinition);

    protected abstract void registerServiceBeans(ParserContext paramParserContext, ServiceConfig paramServiceConfig,
            String paramString, boolean paramBoolean);
}