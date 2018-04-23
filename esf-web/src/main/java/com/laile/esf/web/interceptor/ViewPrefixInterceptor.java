package com.laile.esf.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.mobile.device.site.SitePreferenceUtils;
import org.springframework.mobile.device.util.ResolverUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ViewPrefixInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ViewPrefixInterceptor.class);

    private String normalPrefix = "";

    private String mobilePrefix = "";

    private String tabletPrefix = "";

    private Boolean enableFallback = Boolean.valueOf(true);

    public static final String REDIRECT_URL_PREFIX = "redirect:";

    public static final String FORWARD_URL_PREFIX = "forward:";

    ViewPrefixInterceptor() {
        logger.info("ViewPrefixInterceptor init");
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object obj,
            ModelAndView modelandview) throws Exception {
        if (modelandview != null) {
            String viewName = modelandview.getViewName();

            String deviceViewName = getDeviceViewName(viewName, request);
            if ((this.enableFallback.booleanValue()) && (deviceViewName == null)) {
                deviceViewName = viewName;
            }
            logger.debug("Resolved View: " + deviceViewName);

            modelandview.setViewName(deviceViewName);
        }
    }

    private String getDeviceViewName(String viewName, HttpServletRequest request) {
        if (viewName.startsWith(REDIRECT_URL_PREFIX)) {
            return viewName;
        }
        if (viewName.startsWith(FORWARD_URL_PREFIX)) {
            return viewName;
        }
        Device device = DeviceUtils.getCurrentDevice(request);
        SitePreference sitePreference = SitePreferenceUtils.getCurrentSitePreference(request);
        String resolvedViewName = "/" + viewName;
        if (ResolverUtils.isNormal(device, sitePreference)) {
            resolvedViewName = getNormalPrefix() + resolvedViewName;
        } else if (ResolverUtils.isMobile(device, sitePreference)) {
            resolvedViewName = getMobilePrefix() + resolvedViewName;
        } else if (ResolverUtils.isTablet(device, sitePreference)) {
            resolvedViewName = getTabletPrefix() + resolvedViewName;
        }
        return resolvedViewName;
    }

    public String getNormalPrefix() {
        return this.normalPrefix;
    }

    public void setNormalPrefix(String normalPrefix) {
        this.normalPrefix = normalPrefix;
    }

    public String getMobilePrefix() {
        return this.mobilePrefix;
    }

    public void setMobilePrefix(String mobilePrefix) {
        this.mobilePrefix = mobilePrefix;
    }

    public String getTabletPrefix() {
        return this.tabletPrefix;
    }

    public void setTabletPrefix(String tabletPrefix) {
        this.tabletPrefix = tabletPrefix;
    }
}
