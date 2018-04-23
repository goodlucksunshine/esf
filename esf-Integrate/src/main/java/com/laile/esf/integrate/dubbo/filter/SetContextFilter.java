package com.laile.esf.integrate.dubbo.filter;

import com.laile.esf.common.context.ServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;

@Activate(group = { "consumer" }, order = -5000)
public class SetContextFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetContextFilter.class);

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        ServiceContext context = ServiceContext.getContext();

        boolean genFlowFlag = false;
        long startTime = System.currentTimeMillis();
        String flowNo = null;
        try {
            flowNo = context.getRequestNo();
            if ((flowNo == null) || (flowNo.isEmpty())) {
                flowNo = ServiceContext.getRandomFlowNo();
                context.addHeader("esf.requestNo", flowNo);
                genFlowFlag = true;
            }

            String localIp = RpcContext.getContext().getLocalHost();
            String remoteIp = RpcContext.getContext().getRemoteHost();
            int port = RpcContext.getContext().getRemotePort();
            LOGGER.info("[RQ-BEIGN][服务:{}.{}][流水号:{}][本地IP:{}][远端IP:{}:{}].",
                    new Object[] { invoker.getInterface().getName(), invocation.getMethodName(), flowNo, localIp,
                            remoteIp, Integer.valueOf(port) });

            context.addHeader("esf.consumerIp", localIp);

            invocation.getAttachments().putAll(context.getCloneHeaders());
            long endTime;
            return invoker.invoke(invocation);
        } finally {
            if (genFlowFlag) {
                context.removeHeader("esf.requestNo");
            }

            long endTime = System.currentTimeMillis();
            LOGGER.info("[RQ-END][流水号:{}][耗时: time={} ms].", flowNo, Long.valueOf(endTime - startTime));
        }
    }
}