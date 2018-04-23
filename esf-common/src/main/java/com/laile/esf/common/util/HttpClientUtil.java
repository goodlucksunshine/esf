package com.laile.esf.common.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.laile.esf.common.exception.BusinessException;
import com.laile.esf.common.exception.ResultCode;
import com.laile.esf.common.exception.SystemException;

public class HttpClientUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static PoolingHttpClientConnectionManager phccm;

    public static String EMPTY_STR_RESULT = "";

    /**
     * 初始化
     */
    private static void init() {
        if (phccm == null) {
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", trustAllHttpsCertificates())
                    .build();
            phccm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            phccm.setMaxTotal(HttpClientConfig.MAX_TOTAL);// 整个连接池最大连接数
            phccm.setDefaultMaxPerRoute(HttpClientConfig.MAX_PER_ROUTE);// 每路由最大连接数
            
            Runnable runnable = new Runnable() {  
                public void run() { 
                	
                	// 关闭过期的连接
                	phccm.closeExpiredConnections();
                	
                	// 关闭空闲时间超过1秒的连接
                	phccm.closeIdleConnections(5, TimeUnit.MINUTES);
                	//logger.debug("cmd=HttpClientUtil:init, *********************清理无效的链接****************");
                }  
            };  
            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();  
            // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间  
            service.scheduleAtFixedRate(runnable, 2, 30, TimeUnit.SECONDS);
        }
    }

    /**
     * 通过连接池获取HttpClient
     *
     * @return
     */
    private static CloseableHttpClient getHttpClient(boolean redirectsEnabled) {
        init();
        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if (executionCount >= HttpClientConfig.RETRY_NUM) {// 如果已经重试了8次，就放弃
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {// SSL握手异常
                    return false;
                }
                if (exception instanceof SocketException) {
                    return true;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };

        // 创建全局的requestConfig
        RequestConfig requestConfig = RequestConfig.custom()
        		//连接超时时间
        		.setConnectTimeout(HttpClientConfig.CONNECT_TIMEOUT)
                .setSocketTimeout(HttpClientConfig.SOCKET_TIMEOUT)
                //从池中获取连接超时时间
                //建议设置500ms即可，不要设置太大，这样可以使连接池连接不够时不用等待太久去获取连接，不要让大量请求堆积在获取连接处，尽快抛出异常，发现问题。
                .setConnectionRequestTimeout(500)
                .setRedirectsEnabled(redirectsEnabled)
                .setCookieSpec(CookieSpecs.DEFAULT).build();
        // 声明重定向策略对象
        LaxRedirectStrategy redirectStrategy = new LaxRedirectStrategy();
        return HttpClients.custom().setConnectionManager(phccm).setDefaultRequestConfig(requestConfig)
                .setRedirectStrategy(redirectStrategy).setRetryHandler(httpRequestRetryHandler).build();
    }

    /**
     * get请求
     *
     * @param requestUrl
     *
     * @return
     */
    public static String httpGetRequest(String requestUrl) {
        HttpGet httpGet = new HttpGet(requestUrl);
        return getResult(httpGet);
    }

    /**
     * 带参数的get请求
     *
     * @param requestUrl
     * @param params
     *
     * @return
     *
     * @throws URISyntaxException
     */
    public static String httpGetRequest(String requestUrl, Map<String, Object> params) throws URISyntaxException {
        URIBuilder ub = new URIBuilder();
        ub.setPath(requestUrl);

        ArrayList<NameValuePair> pairs = covertParamsToNVPList(params);
        ub.setParameters(pairs);

        HttpGet httpGet = new HttpGet(ub.build());
        return getResult(httpGet);
    }

    /**
     * 带header的get请求
     *
     * @param url
     * @param headers
     * @param params
     *
     * @return
     *
     * @throws URISyntaxException
     */
    public static String httpGetRequest(String url, Map<String, Object> headers, Map<String, Object> params)
            throws URISyntaxException {
        URIBuilder ub = new URIBuilder();
        ub.setPath(url);

        ArrayList<NameValuePair> pairs = covertParamsToNVPList(params);
        ub.setParameters(pairs);

        HttpGet httpGet = new HttpGet(ub.build());
        for (Map.Entry<String, Object> param : headers.entrySet()) {
            httpGet.addHeader(param.getKey(), String.valueOf(param.getValue()));
        }
        return getResult(httpGet);
    }

    /**
     * post请求
     *
     * @param url
     *
     * @return
     */
    public static String httpPostRequest(String url) {
        HttpPost httpPost = new HttpPost(url);
        return getResult(httpPost);
    }

    /**
     * 带参数的post请求
     *
     * @param url
     * @param params
     *
     * @return
     *
     * @throws UnsupportedEncodingException
     */
    public static String httpPostRequest(String url, Map<String, Object> params) throws UnsupportedEncodingException {
    	logger.info("cmd=HttpClientUtil:httpPostRequest 请求url：" + url);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setProtocolVersion(HttpVersion.HTTP_1_0);
        httpPost.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
        ArrayList<NameValuePair> pairs = covertParamsToNVPList(params);
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, HttpClientConfig.ENCODING));
        return getResult(httpPost);
    }

    public static String httpPostRequest(String url, Map<String, Object> params, String contentType) throws UnsupportedEncodingException {
    	logger.info("cmd=HttpClientUtil:httpPostRequest 请求url：" + url);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setProtocolVersion(HttpVersion.HTTP_1_0);
        httpPost.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
        httpPost.addHeader("Content-Type", contentType);
        ArrayList<NameValuePair> pairs = covertParamsToNVPList(params);
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, HttpClientConfig.ENCODING));
        return getResult(httpPost);
    }
    
    
    public static String invokeHttpPost(String requestUrl, String param) throws Exception {
        
    	logger.info("调用远程接口参数:{}", param);
        HttpClient client = new HttpClient();
        PostMethod postMethod = new PostMethod(requestUrl);
        StringRequestEntity entity = null;
        String response = "";
        try {
            entity = new StringRequestEntity(param, "application/json; charset=utf-8", "UTF-8");
            postMethod.setRequestEntity(entity);
            int statusCode = client.executeMethod(postMethod);
            if (statusCode == HttpStatus.SC_OK) {
                response = new String(postMethod.getResponseBody(), "utf-8");
                logger.info("远程调用接口:{},返回数据:{}", requestUrl, response);
            } else {
                logger.info("调用远程接口异常 httpStatus:{}", statusCode);
                throw new Exception("调用远程接口异常 httpStatus:" + statusCode);
            }
            postMethod.releaseConnection();
            logger.info(response);
            return response;
        } catch (Exception e) {
            logger.info("请求远程接口异常:{}", requestUrl, e);
            throw new Exception("请求接口异常");
        }
    }
    
    
   public static String invokeHttpPost(String requestUrl, String param, String env) throws Exception {
        
    	logger.info("调用远程接口参数:{}", param);
        HttpClient client = new HttpClient();
        PostMethod postMethod = new PostMethod(requestUrl);
        StringRequestEntity entity = null;
        String response = "";
        try {
            entity = new StringRequestEntity(param, "application/json; charset=" + env, env);
            postMethod.setRequestEntity(entity);
            int statusCode = client.executeMethod(postMethod);
            if (statusCode == HttpStatus.SC_OK) {
                response = new String(postMethod.getResponseBody(), env);
                logger.info("远程调用接口:{},返回数据:{}", requestUrl, response);
            } else {
                logger.info("调用远程接口异常 httpStatus:{}", statusCode);
                throw new Exception("调用远程接口异常 httpStatus:" + statusCode);
            }
            postMethod.releaseConnection();
            logger.info(response);
            return response;
        } catch (Exception e) {
            logger.info("请求远程接口异常:{}", requestUrl, e);
            throw new Exception("请求接口异常");
        }
    }
    
    /**
     * 带header的post请求
     *
     * @param url
     * @param headers
     * @param params
     *
     * @return
     *
     * @throws UnsupportedEncodingException
     */
    public static String httpPostRequest(String url, Map<String, Object> headers, Map<String, Object> params)
            throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(url);

        for (Map.Entry<String, Object> param : headers.entrySet()) {
            httpPost.addHeader(param.getKey(), String.valueOf(param.getValue()));
        }

        ArrayList<NameValuePair> pairs = covertParamsToNVPList(params);
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, HttpClientConfig.ENCODING));

        return getResult(httpPost);
    }

    /**
     * 参数转换
     *
     * @param params
     *
     * @return
     */
    public static ArrayList<NameValuePair> covertParamsToNVPList(Map<String, Object> params) {
        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));
        }
        return pairs;
    }

    /**
     * 处理Http请求
     *
     * @param request
     *
     * @return
     */
    private static String getResult(HttpRequestBase request) {
        CloseableHttpClient httpClient = getHttpClient(true);
        CloseableHttpResponse response = null;
        try {

            response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                logger.info(request.getRequestLine().getUri() + "请求异常,状态码:{},描述:{}", statusCode,
                        response.getStatusLine().getReasonPhrase());
                throw new SystemException(ResultCode.COMMON_ERROR,
                        "请求异常,状态码：" + statusCode + "  描述: " + response.getStatusLine().getReasonPhrase());
            }
            String url = request.getRequestLine().getUri();
            
            HttpEntity entity = response.getEntity();
            
            if (entity != null) {
                String result = EntityUtils.toString(entity, "utf-8");
                logger.info("cmd=HttpClientUtil:httpPostRequest 请求结果，result：" + result  + ", uri:" + url);
                return result;
            }
        } catch (ClientProtocolException e) {
            logger.error("http 请求异常！", e);
            throw new SystemException(ResultCode.COMMON_ERROR, "处理http请求异常");
        } catch (ConnectTimeoutException e) {
            logger.error("http 请求超时！", e);
            throw new BusinessException(ResultCode.HTTP_TIMEOUT, "http请求超时");
        } catch (SocketTimeoutException e) {
            logger.error("服务器请求超时");
            throw new BusinessException(ResultCode.SOCKET_TIMEOUT, "服务器处理请求超时");
        } catch (IOException e) {
            logger.error("http 请求异常！", e);
            throw new SystemException(ResultCode.COMMON_ERROR, "处理http请求异常");
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.info("http 请求异常！", e);
                    throw new SystemException(ResultCode.COMMON_ERROR, "处理http请求异常");
                }
            }

            
        }
        return EMPTY_STR_RESULT;
    }

    public static String httpGetRedirectRequest(String requestUrl, Map<String, Object> params) throws URISyntaxException {
       
    	URIBuilder ub = new URIBuilder();
        ub.setPath(requestUrl);

        ArrayList<NameValuePair> pairs = covertParamsToNVPList(params);
        ub.setParameters(pairs);

        HttpGet httpGet = new HttpGet(ub.build());
//        httpGet.setHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=ISO-8859-1"));
//        httpGet.setHeader(new BasicHeader("Accept", "text/plain;charset=ISO-8859-1"));
        return getRedirectContext(httpGet);
    }
    
    /**
     * 处理Http请求
     *
     * @param request
     *
     * @return
     */
    private static String getRedirectContext(HttpRequestBase request) {
    	
        CloseableHttpClient httpClient = getHttpClient(false);
        CloseableHttpResponse response = null;
        try {
        	
            response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_MOVED_TEMPORARILY) {
            	
            	if (statusCode == HttpStatus.SC_OK) {
            		String url = request.getRequestLine().getUri();
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String result = EntityUtils.toString(entity, "utf-8");
                        logger.info("cmd=HttpClientUtil:getRedirectContext 请求结果，result：" + result  + ", uri:" + url);
                        return "OK_200:" + result;
                    }
            	}
            	
            	logger.info(request.getRequestLine().getUri() + "请求异常,状态码:{},描述:{}", statusCode,
                        response.getStatusLine().getReasonPhrase());
                throw new SystemException(ResultCode.COMMON_ERROR,
                        "请求异常,状态码：" + statusCode + "  描述: " + response.getStatusLine().getReasonPhrase());
            }
           
            HttpEntity entity =response.getEntity();      
            String content = EntityUtils.toString(entity);
            return content;
            
        } catch (ClientProtocolException e) {
            logger.error("http 请求异常！", e);
            throw new SystemException(ResultCode.COMMON_ERROR, "处理http请求异常");
        } catch (ConnectTimeoutException e) {
            logger.error("http 请求超时！", e);
            throw new BusinessException(ResultCode.HTTP_TIMEOUT, "http请求超时");
        } catch (SocketTimeoutException e) {
            logger.error("服务器请求超时");
            throw new BusinessException(ResultCode.SOCKET_TIMEOUT, "服务器处理请求超时");
        } catch (IOException e) {
            logger.error("http 请求异常！", e);
            throw new SystemException(ResultCode.COMMON_ERROR, "处理http请求异常");
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.info("http 请求异常！", e);
                    throw new SystemException(ResultCode.COMMON_ERROR, "处理http请求异常");
                }
            }

            
        }
      //  return EMPTY_STR_RESULT;
    }

    /**
     * 绕过验证
     *
     * @return
     *
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static SSLConnectionSocketFactory trustAllHttpsCertificates() {
        SSLConnectionSocketFactory socketFactory = null;
        TrustManager[] trustAllCerts = new TrustManager[1];

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        trustAllCerts[0] = trustManager;
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");//sc = SSLContext.getInstance("TLS")
            sc.init(null, trustAllCerts, null);
            socketFactory = new SSLConnectionSocketFactory(sc, NoopHostnameVerifier.INSTANCE);
        } catch (Exception e) {
            logger.error("创建跳过证书ssl异常", e);
            throw new SystemException(ResultCode.COMMON_ERROR, "创建跳过证书ssl异常");
        }
        return socketFactory;
    }
    
    public static void main(String[] args) throws URISyntaxException {
    	JSONObject jsonObject = new JSONObject();
    	jsonObject.put("dd", "ds");
		System.err.println(httpGetRedirectRequest("http://47.52.152.93/gateway?bfaf7a2f715f9566ed4192b9f9053aad", jsonObject));
	}
}
