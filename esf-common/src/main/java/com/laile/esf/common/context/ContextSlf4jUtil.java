package com.laile.esf.common.context;

import org.slf4j.MDC;

public final class ContextSlf4jUtil {
    public static void addLogKey2MDC(ServiceContext context) {
        if (context == null) {
            return;
        }

        MDC.put("esf.requestNo", context.getRequestNo());
        MDC.put("esf.consumerIp", context.getConsumerIp());
        MDC.put("esf.channelCode", context.getChannelCode());
    }

    public static void rmvLogKeyFromMDC() {
        MDC.remove("esf.requestNo");
        MDC.remove("esf.consumerIp");
        MDC.remove("esf.channelCode");
    }
}