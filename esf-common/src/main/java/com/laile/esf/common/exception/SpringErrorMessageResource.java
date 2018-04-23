package com.laile.esf.common.exception;

/**
 * Created by sunshine on 16/7/22.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

public class SpringErrorMessageResource
        extends AbstractErrorMessageResource {
    private static Logger logger = LoggerFactory.getLogger(SpringErrorMessageResource.class);
    private Properties allProps = new Properties();
    private Resource[] messageProperties = null;

    public Resource[] getMessageProperties() {
        return this.messageProperties;
    }

    public void setMessageProperties(Resource[] messageProperties) {
        this.messageProperties = messageProperties;

        this.allProps.clear();
        if (messageProperties != null) {
            for (Resource propResource : messageProperties) {
                loadProperties(propResource);
            }
        }
    }

    private void loadProperties(Resource propResource) {
        try {
            String propFileName = propResource.getURL().toString();
            logger.info("" + propFileName);
            Properties props = new Properties();
            try {
                props.load(propResource.getInputStream());
                for (Object errorCode : props.keySet()) {
                    if (this.allProps.containsKey(errorCode)) {
                        logger.warn("文件{}中的错误码[{}]消息配置重复，已有消息配置[{}],", new Object[]{propFileName, errorCode, this.allProps.get(errorCode)});
                    } else {
                        this.allProps.put(errorCode, props.get(errorCode));
                    }
                }
            } catch (Exception e) {
                logger.error("加载错误消息文件失败" + propResource.getFilename(), e);
            }
            logger.info("错误码消息配置文件" + propFileName + "加载成功");
        } catch (IOException e1) {
        }
    }

    protected String doGetMessage(String errorCode) {
        return this.allProps.getProperty(errorCode);
    }
}
