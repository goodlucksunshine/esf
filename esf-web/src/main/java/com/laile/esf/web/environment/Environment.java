package com.laile.esf.web.environment;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Class Describe
 * <p/>
 * User: yangguang
 * Date: 16/10/15
 * Time: 下午2:11
 */
public class Environment implements Serializable {
    /**
     * IP地址
     */
    private String ip;
    /**
     * 终端IP地址
     */
    private String clientIp;
    /**
     * mac地址
     */
    private String mac;
    /**
     * imei
     */
    private String imei;
    /**
     * 运营商
     */
    private String operators;
    /**
     * 制造商
     */
    private String manufacturer;
    /**
     * 手机类型
     */
    private String phoneModel;
    /**
     * 终端名称
     */
    private String computerName;
    /**
     * 操作系统
     */
    private String os;
    /**
     * 操作系统版本
     */
    private String osVersion;
    /**
     * 分辨率
     */
    private String resolution;
    /**
     * 字体
     */
    private String font;
    /**
     * 字体大小
     */
    private String fontSize;
    /**
     * 浏览器
     */
    private String browser;
    /**
     * 浏览器版本
     */
    private String browserVersion;
    /**
     * APP名称
     */
    private String appName;
    /**
     * APP类型
     */
    private String appType;
    /**
     * APP版本
     */
    private String appVersion;
    /**
     * 经度
     */
    private String longitude;
    /**
     * 纬度
     */
    private String latitude;
    /**
     * 网络类型
     */
    private String internetType;
    /**
     * 是否模拟器
     */
    private String isEmulator;
    /**
     * 设备ID
     */
    private String deviceId;

    public String getImei()
    {
        return this.imei;
    }

    public void setImei(String imei)
    {
        this.imei = imei;
    }

    public String getOperators()
    {
        return this.operators;
    }

    public void setOperators(String operators)
    {
        this.operators = operators;
    }

    public String getManufacturer()
    {
        return this.manufacturer;
    }

    public void setManufacturer(String manufacturer)
    {
        this.manufacturer = manufacturer;
    }

    public String getPhoneModel()
    {
        return this.phoneModel;
    }

    public void setPhoneModel(String phoneModel)
    {
        this.phoneModel = phoneModel;
    }

    public String getComputerName()
    {
        return this.computerName;
    }

    public void setComputerName(String computerName)
    {
        this.computerName = computerName;
    }

    public String getOs()
    {
        return this.os;
    }

    public void setOs(String os)
    {
        this.os = os;
    }

    public String getOsVersion()
    {
        return this.osVersion;
    }

    public void setOsVersion(String osVersion)
    {
        this.osVersion = osVersion;
    }

    public String getResolution()
    {
        return this.resolution;
    }

    public void setResolution(String resolution)
    {
        this.resolution = resolution;
    }

    public String getFont()
    {
        return this.font;
    }

    public void setFont(String font)
    {
        this.font = font;
    }

    public String getFontSize()
    {
        return this.fontSize;
    }

    public void setFontSize(String fontSize)
    {
        this.fontSize = fontSize;
    }

    public String getBrowser()
    {
        return this.browser;
    }

    public void setBrowser(String browser)
    {
        this.browser = browser;
    }

    public String getBrowserVersion()
    {
        return this.browserVersion;
    }

    public void setBrowserVersion(String browserVersion)
    {
        this.browserVersion = browserVersion;
    }

    public String getAppName()
    {
        return this.appName;
    }

    public void setAppName(String appName)
    {
        this.appName = appName;
    }

    public String getAppType()
    {
        return this.appType;
    }

    public void setAppType(String appType)
    {
        this.appType = appType;
    }

    public String getAppVersion()
    {
        return this.appVersion;
    }

    public void setAppVersion(String appVersion)
    {
        this.appVersion = appVersion;
    }

    public String getLongitude()
    {
        return this.longitude;
    }

    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }

    public String getLatitude()
    {
        return this.latitude;
    }

    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    public String getIp()
    {
        return this.ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getMac()
    {
        return this.mac;
    }

    public void setMac(String mac)
    {
        this.mac = mac;
    }

    public String getClientIp()
    {
        return this.clientIp;
    }

    public void setClientIp(String clientIp)
    {
        this.clientIp = clientIp;
    }

    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }

    public String getInternetType()
    {
        return this.internetType;
    }

    public void setInternetType(String internetType)
    {
        this.internetType = internetType;
    }

    public String getIsEmulator()
    {
        return this.isEmulator;
    }

    public void setIsEmulator(String isEmulator)
    {
        this.isEmulator = isEmulator;
    }

    public String getDeviceId()
    {
        return this.deviceId;
    }

    public void setDeviceId(String deviceId)
    {
        this.deviceId = deviceId;
    }

}
