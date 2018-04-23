package com.laile.esf.integrate;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.laile.esf.integrate.dubbo.container.LsfLogbackContainer;
import com.laile.esf.integrate.dubbo.container.LsfReportStatusContainer;

public class Main {
    static {
        LsfLogbackContainer.getInstance().start();
    }

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info('[' + new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]").format(new Date())
                + "] com.laile.esf.integration.Main start..");

        String reportFile = System.getProperty("esf.started.reportfile");
        LsfReportStatusContainer.getInstance().setReportFilePath(reportFile);

        init(args);

        logger.info('[' + new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]").format(new Date())
                + "] com.laile.esf.integration.Main stop..");

        LsfLogbackContainer.getInstance().stop();
    }

    private static void init(String[] args) {
        com.alibaba.dubbo.container.Main.main(args);
    }
}