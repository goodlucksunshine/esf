package com.laile.esf.integrate.dubbo;

import java.util.Properties;

import com.laile.esf.common.exception.SystemErrorCodes;
import com.laile.esf.common.exception.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.utils.ConfigUtils;

public final class PropertiesConfigurer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesConfigurer.class);

    private String file;

    private boolean ignoreNoResource = false;

    public void init() {
        LOGGER.warn("PropertiesLoader init start.");
        Properties p = ConfigUtils.loadProperties(this.file);
        if ((!this.ignoreNoResource) && (p.isEmpty())) {
            throw new SystemException(SystemErrorCodes.INVALID_PARAM_VALUE2,
                    new Object[] { "dubbo.properties文件不存在，或属性文件内容为空。", "文件路径：" + this.file });
        }

        ConfigUtils.setProperties(p);
        LOGGER.warn("PropertiesLoader init OK.");
    }

    public boolean isIgnoreNoResource() {
        return this.ignoreNoResource;
    }

    public void setIgnoreNoResource(boolean ignoreNoResource) {
        this.ignoreNoResource = ignoreNoResource;
    }

    public String getFile() {
        return this.file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}