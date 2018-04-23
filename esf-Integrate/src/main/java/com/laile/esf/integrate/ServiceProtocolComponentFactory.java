package com.laile.esf.integrate;


import com.laile.esf.integrate.dubbo.DubboProtocolComponent;

public class ServiceProtocolComponentFactory {
    public static final String DUBBO_PROTOCOL = "dubbo";

    public static final String EBOX_PROTOCOL = "ebox";

    private static DubboProtocolComponent dubboProtocolComp = new DubboProtocolComponent();


    public static ServiceProtocolComponent getServiceProtocolComponent(String protocol) {
        if ("dubbo".equalsIgnoreCase(protocol))
            return dubboProtocolComp;
        return null;
    }
}
