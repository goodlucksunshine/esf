package com.laile.esf.common.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class ServiceContext {
    private static final ThreadLocal<ServiceContext> contexts = new ThreadLocal() {
        protected ServiceContext initialValue() {
            return new ServiceContext();
        }
    };

    private static final String esfPrefix = "k.";

    private Map<String, String> headers = new HashMap();

    public static ServiceContext getContext() {
        return (ServiceContext) contexts.get();
    }

    public static ServiceContext getContext(boolean initFlowNo) {
        ServiceContext c = (ServiceContext) contexts.get();
        if (!initFlowNo) {
            return c;
        }

        c.setRequestNo(getRandomFlowNo());
        return c;
    }

    public static ServiceContext getContext(String prefix) {
        ServiceContext c = (ServiceContext) contexts.get();
        if (prefix == null) {
            return c;
        }

        c.setRequestNo(prefix + getRandomFlowNo());
        return c;
    }

    public static String getRandomFlowNo() {
        int index = (int) (System.currentTimeMillis() % 13L);
        return UUID.randomUUID().toString().replaceAll("-", "").substring(index, index + 16);
    }

    public void initBy(ServiceContext parentContext) {
        if ((parentContext == null) || (parentContext == this)) {
            return;
        }

        this.headers.clear();
        this.headers.putAll(parentContext.headers);
    }

    public static void removeContext() {
        contexts.remove();
    }

    public String getHeader(String key) {
        return (String) this.headers.get(key);
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public Map<String, String> getCloneHeaders() {
        Map<String, String> m = new HashMap(this.headers.size());
        for (Entry<String, String> e : this.headers.entrySet()) {
            m.put("k." + (String) e.getKey(), e.getValue());
        }
        return m;
    }

    public void removeHeader(String key) {
        this.headers.remove(key);
    }

    public void addHeaders(Map<String, String> h) {
        for (Entry<String, String> e : h.entrySet()) {
            String key = (String) e.getKey();
            if ((key != null) && (key.startsWith("k."))) {
                addHeader(key.substring("k.".length()), (String) e.getValue());
            }
        }
    }

    public String getUserId() {
        return getHeader("esf.userId");
    }

    public String getUserIP() {
        return getHeader("esf.userIp");
    }

    public String getUserMac() {
        return getHeader("esf.userMac");
    }

    public String getUserImei() {
        return getHeader("esf.userImei");
    }

    public String getRequestNo() {
        return getHeader("esf.requestNo");
    }

    public String getConsumerIp() {
        return getHeader("esf.consumerIp");
    }

    public String getChannelCode() {
        return getHeader("esf.channelCode");
    }

    public String getSessionId() {
        return getHeader("esf.sessionId");
    }

    public void setUserId(String str) {
        this.headers.put("esf.userId", str);
    }

    public void setUserIP(String str) {
        this.headers.put("esf.userIp", str);
    }

    public void setUserMac(String str) {
        this.headers.put("esf.userMac", str);
    }

    public void setUserImei(String str) {
        this.headers.put("esf.userImei", str);
    }

    public void setRequestNo(String str) {
        this.headers.put("esf.requestNo", str);
    }

    public void setConsumerIp(String str) {
        this.headers.put("esf.consumerIp", str);
    }

    public void setChannelCode(String str) {
        this.headers.put("esf.channelCode", str);
    }

    public void setSessionId(String str) {
        this.headers.put("esf.sessionId", str);
    }
}
