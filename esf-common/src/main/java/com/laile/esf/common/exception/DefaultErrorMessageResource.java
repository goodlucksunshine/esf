package com.laile.esf.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by sunshine on 16/7/22.
 */
public class DefaultErrorMessageResource extends AbstractErrorMessageResource {
    private static Logger logger = LoggerFactory.getLogger(DefaultErrorMessageResource.class);

    private static final String DEFAULT_ERROR_MESSAGE_FILE = "/errorcodes/message.properties";

    private String resourceFileName;

    private Properties properties = new Properties();

    public DefaultErrorMessageResource() {
        this("/errorcodes/message.properties");
    }

    public DefaultErrorMessageResource(String errorMessageFile) {
        this.resourceFileName = errorMessageFile;
        loadProperties();
    }

    private void loadProperties() {
        ClassLoader classLoader = getClass().getClassLoader();
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        this.properties.clear();
        try {
            if (this.resourceFileName != null) {
                if (this.resourceFileName.startsWith("/")) {
                    this.resourceFileName = this.resourceFileName.substring(1);
                }
                Enumeration<URL> ps = Thread.currentThread().getContextClassLoader().getResources(resourceFileName);
                while (ps.hasMoreElements()) {
                    InputStream in = null;
                    try {
                        in = ps.nextElement().openStream();
                        properties.load(in);
                    } finally {
                        closeIO(in);
                    }
                }
                this.properties.load(classLoader.getResourceAsStream(this.resourceFileName));
            }
        } catch (Exception e) {
            logger.error("加载异常资源文件失败" + this.resourceFileName, e);
        }
    }

    private static void closeIO(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected String doGetMessage(String errorCode) {
        return this.properties.getProperty(errorCode);
    }
}
