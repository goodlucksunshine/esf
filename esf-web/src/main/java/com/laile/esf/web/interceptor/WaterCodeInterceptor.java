package com.laile.esf.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.laile.esf.common.util.TrackUtil;
import com.laile.esf.web.environment.Environment;
import com.laile.esf.web.util.WebFrameConst;
import com.laile.esf.web.util.WebUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.mobile.device.site.SitePreferenceUtils;
import org.springframework.mobile.device.util.ResolverUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

public class WaterCodeInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(WaterCodeInterceptor.class);

    private String sysCode;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String requestEnvParams = null;
        String userAgent = null;
        String channel = null;
        // 获取cookie JSESSIONID
        String jessionId = WebUtil.getCookie(request, "JSESSIONID");
        // 获取访问路径
        String requestUrl = request.getServletPath().trim();
        // 获取session
        HttpSession session = WebUtil.getSession(request, Boolean.valueOf(false));
        // 拿到sessionId
        String sessionId = session == null ? null : session.getId();
        // 组装访问路径信息
        logger.info("==========Access Url:" + requestUrl + ",JsessionId:" + jessionId + ",sessionId:" + sessionId + ",channel:" + sysCode);
        // 获取http Header
        Enumeration headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String name = (String) headers.nextElement();
            if ("channel".equalsIgnoreCase(name)) {
                channel = request.getHeader(name);
            }
            if ("EnvParams".equalsIgnoreCase(name)) {
                requestEnvParams = request.getHeader(name);
            }
            if ("User-Agent".equalsIgnoreCase(name)) {
                userAgent = request.getHeader(name);
            }
        }
        if (channel == null) {
            Device device = DeviceUtils.getCurrentDevice(request);
            SitePreference sitePreference = SitePreferenceUtils.getCurrentSitePreference(request);
            if (ResolverUtils.isMobile(device, sitePreference)) {
                MDC.put("channel", "CHANNEL_WAP");
            } else {
                MDC.put("channel", "CHANNEL_WEB");
            }
        } else if (channel.equalsIgnoreCase("APP")) {
            MDC.put("channel", "CHANNEL_APP");
        } else if (channel.equalsIgnoreCase("PC")) {
            MDC.put("channel", "CHANNEL_WEB");
        } else if ((channel.equalsIgnoreCase("WAP")) || (channel.equalsIgnoreCase("WECHAT"))) {
            MDC.put("channel", "CHANNEL_WAP");
        } else {
            MDC.put("channel", "CHANNEL_WEB");
        }
        Environment params = null;
        try {
            if (!StringUtils.isEmpty(requestEnvParams)) {
                params = JSON.parseObject(requestEnvParams, Environment.class);
                if (logger.isDebugEnabled()) {
                    logger.debug("环境参数RequestEnvParams:{}", params);
                }
            }
        } catch (Exception e) {
            logger.warn("环境参数解析错误", e);
        }
        if (params == null) {
            params = new Environment();
        }
        params.setClientIp(params.getIp());
        params.setIp(WebUtil.getIpAddr(request));
        if (!StringUtils.isEmpty(userAgent)) {
            params.setBrowser(userAgent);
        }
        logger.info("用户访问环境:{}", params);
        MDC.put("consumerIp", params.getIp());
        request.setAttribute("channel", channel);
        request.setAttribute(WebFrameConst.channel, sysCode);
        String requestNo = TrackUtil.generateChannelFlowNo(this.sysCode);
        request.setAttribute("requestNo", requestNo);
        request.setAttribute("sessionId", jessionId);
        MDC.put("requestNo", requestNo);
        return true;
    }

    public String getSysCode() {
        return sysCode;
    }

    public void setSysCode(String sysCode) {
        this.sysCode = sysCode;
    }
}
