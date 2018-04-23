package com.laile.esf.integrate.dubbo.container;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.container.Container;

public class LsfLogbackContainer implements Container {
    public static final String DEFAULT_ADAPTER = "slf4j";

    private static LsfLogbackContainer instance = new LsfLogbackContainer();

    private volatile boolean hasStart = false;

    private volatile Logger logger = null;

    public static LsfLogbackContainer getInstance() {
        return instance;
    }

    public synchronized void start() {
        if (instance.hasStart) {
            return;
        }

        instance.hasStart = true;
        System.setProperty("dubbo.application.logger", "slf4j");

        this.logger = LoggerFactory.getLogger(LsfLogbackContainer.class);
        this.logger.warn("Container[" + getClass().getSimpleName() + "] started.");
    }

    public synchronized void stop() {
        if (!instance.hasStart) {
            return;
        }

        instance.hasStart = false;
        this.logger.warn("Container[" + getClass().getSimpleName() + "] stopped.");
    }
}
