package com.laile.esf.integrate.dubbo.filter;

import java.lang.reflect.Method;

import com.alibaba.dubbo.rpc.service.GenericService;
import com.laile.esf.common.context.ServiceContext;
import com.laile.esf.common.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.QueryTimeoutException;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;

@Activate(group = { "provider" })
public class ExceptionFilter implements Filter {
    public ExceptionFilter() {
        logger.info("创建ExceptionFilter");
    }

    public Result invoke(Invoker invoker, Invocation invocation) throws RpcException {
        long startTime = System.currentTimeMillis();
        ServiceContext context = ServiceContext.getContext();
        String sessionId = context.getSessionId();
        String methodName = invocation.getMethodName();
        Class interfaceCls = invoker.getInterface();
        String sysErrCode = "0";
        String busiErrCode = "-";
        String thirdErrCode = "-";
        if (sessionId == null)
            sessionId = "";
        logger.info("[TX-BEGIN][{}.{}][{}]", new Object[] { interfaceCls.getName(), methodName, sessionId });
        Result result = null;
        try {
            result = invoker.invoke(invocation);
            if (result.hasException() && GenericService.class != invoker.getInterface()) {
                Throwable exception = result.getException();
                if ((exception instanceof RuntimeException) || !(exception instanceof Exception)) {
                    sysErrCode = "6";
                    long endTime = System.currentTimeMillis();
                    logger.info("[TX-END][{}.{}][{}][错误码:{}][业务错误码:{}/{}][耗时: time={} ms]",
                            new Object[] { interfaceCls.getName(), methodName, sessionId, sysErrCode, busiErrCode,
                                    thirdErrCode, Long.valueOf(endTime - startTime) });
                    return result;
                }
                try {
                    Method method = interfaceCls.getMethod(methodName, invocation.getParameterTypes());
                    String msg = exception.getMessage();
                    Object obj;
                    if (exception instanceof BusinessException) {
                        logger.info("{}.{}() 调用异常:{} ",
                                new Object[] { method.getDeclaringClass().getName(), method.getName(), msg });
                        logger.debug("异常栈信息：", exception);
                    } else {
                        logger.error((new StringBuilder()).append(method.getDeclaringClass().getName()).append(".")
                                .append(method.getName()).append("() 调用异常:").toString(), exception);
                    }
                    if (!(exception instanceof ServiceException)) {
                        sysErrCode = "1";
                        busiErrCode = ((ServiceException) exception).getInnerErrorCode();
                        thirdErrCode = ((ServiceException) exception).getThirdErrCode();
                        if (busiErrCode == null)
                            busiErrCode = "-";
                        if (thirdErrCode == null)
                            thirdErrCode = "-";
                        obj = result;
                        long endTime = System.currentTimeMillis();
                        logger.info("[TX-END][{}.{}][{}][错误码:{}][业务错误码:{}/{}][耗时: time={} ms]",
                                new Object[] { interfaceCls.getName(), methodName, sessionId, sysErrCode, busiErrCode,
                                        thirdErrCode, Long.valueOf(endTime - startTime) });
                        return (Result) obj;
                    }
                    if (!(exception instanceof ServiceException)) {
                        sysErrCode = "1";
                        busiErrCode = ((ServiceException) exception).getInnerErrorCode();
                        thirdErrCode = ((ServiceException) exception).getThirdErrCode();
                        if (busiErrCode == null)
                            busiErrCode = "-";
                        if (thirdErrCode == null)
                            thirdErrCode = "-";
                        obj = result;
                        long endTime = System.currentTimeMillis();
                        logger.info("[TX-END][{}.{}][{}][错误码:{}][业务错误码:{}/{}][耗时: time={} ms]",
                                new Object[] { interfaceCls.getName(), methodName, sessionId, sysErrCode, busiErrCode,
                                        thirdErrCode, Long.valueOf(endTime - startTime) });
                        return ((Result) (obj));
                    }
                    if (!(exception instanceof RpcException)) {
                        sysErrCode = "6";
                        if (!(exception.getCause() instanceof ServiceException)) {
                            obj = new RpcResult(exception.getCause());
                            long endTime = System.currentTimeMillis();
                            logger.info("[TX-END][{}.{}][{}][错误码:{}][业务错误码:{}/{}][耗时: time={} ms]",
                                    new Object[] { interfaceCls.getName(), methodName, sessionId, sysErrCode,
                                            busiErrCode, thirdErrCode, Long.valueOf(endTime - startTime) });
                            return ((Result) (obj));
                        } else {
                            obj = result;
                            long endTime = System.currentTimeMillis();
                            logger.info("[TX-END][{}.{}][{}][错误码:{}][业务错误码:{}/{}][耗时: time={} ms]",
                                    new Object[] { interfaceCls.getName(), methodName, sessionId, sysErrCode,
                                            busiErrCode, thirdErrCode, Long.valueOf(endTime - startTime) });
                            return ((Result) (obj));
                        }
                    }
                    if (exception instanceof DataIntegrityViolationException) {
                        sysErrCode = "4";
                        exception = new DatabaseException(DatabaseErrorCodes.DUPLICATE_KEY,
                                new Object[] { exception.getMessage() });
                    } else if (exception instanceof QueryTimeoutException) {
                        sysErrCode = "4";
                        exception = new DatabaseException(DatabaseErrorCodes.CONNECTION_TIMEOUT);
                    } else if (exception instanceof DataAccessResourceFailureException) {
                        sysErrCode = "4";
                        exception = new DatabaseException(DatabaseErrorCodes.CANT_GET_CONNECTION);
                    } else if (exception instanceof DataAccessException) {
                        sysErrCode = "4";
                        exception = new DatabaseException(DatabaseErrorCodes.UNKNOWN_ERROR);
                    } else {
                        sysErrCode = "6";
                        exception = new SystemException(SystemErrorCodes.SYSTEM_UNKOWN_ERROR,
                                new Object[] { exception.getMessage() });
                    }
                    logger.info((new StringBuilder()).append("框架统一捕获转换异常为:").append(exception.getMessage()).toString());
                    obj = new RpcResult(exception);
                    long endTime = System.currentTimeMillis();
                    logger.info("[TX-END][{}.{}][{}][错误码:{}][业务错误码:{}/{}][耗时: time={} ms]",
                            new Object[] { interfaceCls.getName(), methodName, sessionId, sysErrCode, busiErrCode,
                                    thirdErrCode, Long.valueOf(endTime - startTime) });
                    return ((Result) (obj));

                } catch (NoSuchMethodException e) {
                    Result result3;
                    sysErrCode = "6";
                    StringBuilder errMsg = new StringBuilder(200);
                    errMsg.append("Fail to ExceptionFilter when called by ")
                            .append(RpcContext.getContext().getRemoteHost()).append(". service: ")
                            .append(interfaceCls.getName()).append(", method: ").append(methodName)
                            .append(", exception: ").append(e.getClass().getName()).append(": ").append(e.getMessage());
                    logger.warn(errMsg.toString(), e);
                    result3 = result;
                    long endTime = System.currentTimeMillis();
                    logger.info("[TX-END][{}.{}][{}][错误码:{}][业务错误码:{}/{}][耗时: time={} ms]",
                            new Object[] { interfaceCls.getName(), methodName, sessionId, sysErrCode, busiErrCode,
                                    thirdErrCode, Long.valueOf(endTime - startTime) });
                    return result3;
                }
            }
            long endTime = System.currentTimeMillis();
            logger.info("[TX-END][{}.{}][{}][错误码:{}][业务错误码:{}/{}][耗时: time={} ms]",
                    new Object[] { interfaceCls.getName(), methodName, sessionId, sysErrCode, busiErrCode, thirdErrCode,
                            Long.valueOf(endTime - startTime) });
            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            logger.info("[TX-END][{}.{}][{}][错误码:{}][业务错误码:{}/{}][耗时: time={} ms]",
                    new Object[] { interfaceCls.getName(), methodName, sessionId, sysErrCode, busiErrCode, thirdErrCode,
                            Long.valueOf(endTime - startTime) });
            throw e;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(ExceptionFilter.class);
}