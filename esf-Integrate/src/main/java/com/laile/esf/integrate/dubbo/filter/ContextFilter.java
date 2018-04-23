package com.laile.esf.integrate.dubbo.filter;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.laile.esf.common.context.ContextSlf4jUtil;
import com.laile.esf.common.context.ServiceContext;

@Activate(group = { "provider" }, order = -5000)
public class ContextFilter implements Filter {
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            ServiceContext context = ServiceContext.getContext();
            context.addHeaders(invocation.getAttachments());
            ContextSlf4jUtil.addLogKey2MDC(context);

            return invoker.invoke(invocation);
        } finally {
            ServiceContext.removeContext();
            ContextSlf4jUtil.rmvLogKeyFromMDC();
        }
    }
}