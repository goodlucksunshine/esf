package com.laile.esf.common.util;

public class HttpClientConfig {
    /**
     * 编码格式
     */
    public static String ENCODING = "UTF-8";
    /**
     * 整个连接池最大连接数
     */
    public static final int MAX_TOTAL = 1000;
    /**
     * 每路由最大连接数，默认值是2
     */
    public static final int MAX_PER_ROUTE = 40;
    /**
     * 链接时长
     */
    public static final int CONNECT_TIMEOUT = 20 * 1000;
    /**
     * socket链接时长
     */
    public static final int SOCKET_TIMEOUT = 40 * 1000;
    /**
     * 重连次数
     */
    public static final int RETRY_NUM = 3;
}
