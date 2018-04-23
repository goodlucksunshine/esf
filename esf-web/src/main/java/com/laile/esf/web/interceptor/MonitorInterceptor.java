package com.laile.esf.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.laile.esf.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


/**
 * Class Describe
 * <p/>
 * User: yangguang Date: 16/10/9 Time: 下午5:56
 */

public class MonitorInterceptor extends HandlerInterceptorAdapter {
    private static final Logger MONITOR = LoggerFactory.getLogger("monitor");

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorInterceptor.class);

    private static final String MONITOR_START_TIME = "MONITOR_START_TIME";

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        request.setAttribute("MONITOR_START_TIME", Long.valueOf(System.currentTimeMillis()));
        return true;
    }

    public void postHandle(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse,
                           Object handler, ModelAndView modelandview) throws Exception {
        super.postHandle(httpservletrequest, httpservletresponse, handler, modelandview);
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        try {
            String path = request.getServletPath();
            if (path.contains(".")) {
                path = path.substring(0, path.indexOf("."));
            }
            Long startTime = (Long) request.getAttribute("MONITOR_START_TIME");
            Long endTime = Long.valueOf(System.currentTimeMillis());
            String tradeResult = "success";
            if (ex != null) {
                if ((ex instanceof BusinessException)) {
                    tradeResult = "success";
                } else {
                    tradeResult = "failure";
                }
            }
            MONITOR.info("interface={}|result={}|time={} ms",
                    new Object[] { path, tradeResult, Long.valueOf(endTime.longValue() - startTime.longValue()) });
        } catch (Exception e) {
            LOGGER.warn("监控日志打印异常:{}", e);
        }
        super.afterCompletion(request, response, handler, ex);
    }
}
