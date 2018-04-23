package com.laile.esf.integrate.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.namespace.QName;

import com.laile.esf.common.util.StringUtil;
import com.laile.esf.common.util.XmlUtil;
import com.laile.esf.integrate.exception.ServiceParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlServicesConfigurator extends AbstractServiceConfig implements InitializingBean {
    private Logger logger = LoggerFactory.getLogger(XmlServicesConfigurator.class);

    private static final String SERVICES_CONFIG_FILE_SUFFIX = "services.xml";

    private static final String SERVICES_ELEM_NAM = "services";

    private static final String SERVICE_ELEM_NAM = "service";

    private static final String OPERATION_ELEM_NAM = "operation";

    private Resource[] configResources;

    private String serviceFileSuffix;

    private boolean autoDiscover = true;

    protected void loadConfigFile(Resource configResource) throws Exception {
        File file;
        try {
            file = configResource.getFile();
        } catch (Exception e) {
            this.logger.warn("file path '{}' is not a single file.", configResource.getURL());
            file = null;
        }

        if ((file != null) && (file.isDirectory())) {
            if (this.autoDiscover) {
                this.logger.info(configResource.getURL() + " is directory, auto search config file in directory!");
                autoLoadConfig(file);
            } else {
                this.logger.info(configResource.getURL() + " is directory, skip it!");
            }
        } else {
            this.logger.info("loading config " + configResource.getDescription());
            loadConfig(configResource.getInputStream());
        }
    }

    protected void autoLoadConfig(File configDir) throws Exception {
        if (!configDir.isDirectory()) {
            return;
        }
        File[] childFiles = configDir.listFiles();
        String suffix = this.serviceFileSuffix == null ? "services.xml" : this.serviceFileSuffix;

        for (int i = 0; i < childFiles.length; i++) {
            File childFile = childFiles[i];

            if (childFile.isDirectory()) {
                autoLoadConfig(childFile);
            } else if (childFile.getName().endsWith(suffix)) {
                this.logger.info("auto loading config " + childFile.getAbsolutePath());
                try {
                    loadConfig(new FileInputStream(childFile));
                } catch (Exception e) {
                    this.logger.error("failed to load config file " + childFile.getPath(), e);
                }
            }
        }
    }

    protected void loadConfig(InputStream configStream) throws Exception {
        Document configDoc = XmlUtil.doc(configStream);
        try {
            Element servicesElem = configDoc.getDocumentElement();
            if (!servicesElem.getLocalName().equals("services")) {
                throw new ServiceParseException("unrecognized element " + servicesElem.getLocalName());
            }

            boolean usePkgNamespace = false;
            String defaultNamespace = servicesElem.getAttribute("namespace");
            String usePkgNamespaceAtt = servicesElem.getAttribute("usepkgnamespace");
            if ((usePkgNamespaceAtt != null) && ("true".equalsIgnoreCase(usePkgNamespaceAtt))) {
                usePkgNamespace = true;
            }

            NodeList elementList = servicesElem.getElementsByTagName("service");
            if (elementList != null) {
                for (int i = 0; i < elementList.getLength(); i++) {
                    parseServiceInfo(usePkgNamespace, defaultNamespace, (Element) elementList.item(i));
                }
            }
        } finally {
            configStream.close();
        }
    }

    private void parseServiceInfo(boolean usePkgNamespace, String defaultNamespace, Element serviceElem)
            throws ServiceParseException {
        String serviceName = serviceElem.getAttribute("name");
        String serviceInterfaceName = serviceElem.getAttribute("interface");
        String serviceImplementor = serviceElem.getAttribute("implementor");
        String version = serviceElem.getAttribute("version");
        String proxy = serviceElem.getAttribute("proxy");
        try {
            Class<?> serviceClass = getClass().getClassLoader().loadClass(serviceInterfaceName);
            ServiceInfo serviceInfo;
            if (usePkgNamespace) {
                serviceInfo = new ServiceInfo(serviceClass, new QName(null, serviceName), serviceImplementor);
            } else {
                serviceInfo = new ServiceInfo(serviceClass, new QName(defaultNamespace, serviceName),
                        serviceImplementor);
            }

            String timeout = serviceElem.getAttribute("timeout");
            if (!StringUtil.isEmpty(timeout)) {
                serviceInfo.setTimeout(Integer.parseInt(timeout));
            }

            String executes = serviceElem.getAttribute("executes");
            if (!StringUtil.isEmpty(executes)) {
                serviceInfo.setExecutes(Integer.parseInt(executes));
            }

            String actives = serviceElem.getAttribute("actives");
            if (!StringUtil.isEmpty(actives)) {
                serviceInfo.setActives(Integer.parseInt(actives));
            }

            String validation = serviceElem.getAttribute("validation");
            if (!StringUtil.isEmpty(validation)) {
                serviceInfo.setValidation(Boolean.valueOf(validation).booleanValue());
            }

            if (!StringUtil.isEmpty(version)) {
                serviceInfo.setVersion(version);
            }

            if (!StringUtil.isEmpty(proxy)) {
                serviceInfo.setProxy(proxy);
            }

            addService(serviceInfo);
            parseOperationInfos(serviceElem, serviceInfo);
        } catch (ClassNotFoundException e) {
            throw new ServiceParseException(e.getMessage());
        }
    }

    private void parseOperationInfos(Element serviceElem, ServiceInfo serviceInfo) {
        NodeList operationList = serviceElem.getElementsByTagName("operation");
        if (operationList != null) {
            for (int i = 0; i < operationList.getLength(); i++) {
                Element operationElem = (Element) operationList.item(i);
                String oprName = operationElem.getAttribute("name");
                OperationInfo oprInfo = serviceInfo.getOperationInfo(oprName);
                if (oprInfo == null) {
                    this.logger.error(
                            "服务配置错误,服务[" + serviceInfo.getServiceClass().getName() + "]中不存在方法名为" + oprName + "的操作");
                } else {
                    String timeout = operationElem.getAttribute("timeout");
                    if (!StringUtil.isEmpty(timeout)) {
                        oprInfo.setTimeout(Integer.parseInt(timeout));
                    }

                    String async = operationElem.getAttribute("async");
                    if (("true".equalsIgnoreCase(async)) || ("1".equals(async))) {
                        oprInfo.setAsync(true);
                    }

                    String oneway = operationElem.getAttribute("oneway");
                    if (("true".equalsIgnoreCase(oneway)) || ("1".equals(oneway)))
                        oprInfo.setOneWay(true);
                }
            }
        }
    }

    public void loadConfigs() {
        if (this.configResources == null) {
            throw new ServiceParseException("Must set property configResources before call method loadConfig()!");
        }

        for (int i = 0; i < this.configResources.length; i++) {
            try {
                loadConfigFile(this.configResources[i]);
            } catch (Exception e) {
                this.logger.error("failed to load config file " + this.configResources[i].getFilename(), e);
            }
        }
    }

    public void setConfigResources(Resource[] configResources) {
        this.configResources = configResources;
    }

    public void setAutoDiscover(boolean autoDiscover) {
        this.autoDiscover = autoDiscover;
    }

    public void setServiceFileSuffix(String value) {
        this.serviceFileSuffix = value;
    }

    public void afterPropertiesSet() throws Exception {
        loadConfigs();
    }
}