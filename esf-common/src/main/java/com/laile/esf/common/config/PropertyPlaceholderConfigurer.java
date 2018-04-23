package com.laile.esf.common.config;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class PropertyPlaceholderConfigurer
        extends org.springframework.beans.factory.config.PropertyPlaceholderConfigurer {
    Logger logger= LoggerFactory.getLogger(PropertyPlaceholderConfigurer.class);
    private static Properties props;

    
    public Properties mergeProperties() throws IOException {
        props = super.mergeProperties();
        Property.init(props);
        logger.info("props string:{}", JSON.toJSONString(props));
        return props;
    }

    public static String getProperty(String key) {
        return props.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
}