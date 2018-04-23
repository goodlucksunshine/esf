package com.laile.esf.integrate.dubbo.container;

import java.io.File;
import java.io.IOException;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.container.Container;

public class LsfReportStatusContainer implements Container {
    private static LsfReportStatusContainer instance = new LsfReportStatusContainer();

    private static final Logger LOGGER = LoggerFactory.getLogger(LsfReportStatusContainer.class);

    private volatile boolean hasStart = false;

    private String reportFilePath = "";

    public static LsfReportStatusContainer getInstance() {
        return instance;
    }

    public void setReportFilePath(String strpath) {
        instance.reportFilePath = strpath;
    }

    public synchronized void start() {
        if (instance.hasStart) {
            LOGGER.info("This container has started.");
            return;
        }

        instance.hasStart = true;

        if ((instance.reportFilePath == null) || (instance.reportFilePath.isEmpty())) {
            LOGGER.info("Report file path is empty.");
            return;
        }

        File f = new File(instance.reportFilePath);
        try {
            f.createNewFile();
        } catch (IOException e) {
            LOGGER.warn("Create report file failed: " + f.getAbsolutePath(), e);
        }

        LOGGER.warn("Container[" + getClass().getSimpleName() + "] started.");
    }

    public synchronized void stop() {
        instance.hasStart = false;
        LOGGER.warn("Container[" + getClass().getSimpleName() + "] stopped.");
    }
}