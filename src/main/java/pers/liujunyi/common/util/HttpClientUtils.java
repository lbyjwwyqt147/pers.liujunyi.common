package pers.liujunyi.common.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/***
 *
 * @FileName: HttpClientUtils
 * @Company:
 * @author    ljy
 * @Date      2018年05月15日
 * @version   1.0.0
 * @remark:   http 工具类
 * @explain
 *
 *
 */
@Log4j2
public final class HttpClientUtils {

    private static final  Integer STATUS = 200;
    /** 建立连接的timeout时间 */
    private static final Integer CONNECT_TIME_OUT = 3000;
    /** 从连接池中后去连接的timeout时间 */
    private static final Integer CONNECTIONREQUEST_TIME_OUT = 1000;
    /** 数据传输处理时间 */
    private static final Integer SOCKET_TIME_OUT = 4000;
    /** 连接池最大并发连接数 */
    private static final Integer MAX_TOTAL = 300;
    /** 单路由最大并发数 */
    private static final Integer DEFAULT_MAX_PERROUTE = 50;
    /** 重试次数 */
    private static final Integer EXECUTION_COUNT = 5;

    private static RequestConfig requestConfig = null;
    private static PoolingHttpClientConnectionManager pccm = null;
    private static HttpRequestRetryHandler retryHandler = null;
    static {
        // 初始化线程池
        requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIME_OUT).setConnectionRequestTimeout(CONNECTIONREQUEST_TIME_OUT).setSocketTimeout(SOCKET_TIME_OUT)
                .setExpectContinueEnabled(true).build();
        pccm = new PoolingHttpClientConnectionManager();
        // 连接池最大并发连接数
        pccm.setMaxTotal(MAX_TOTAL);
        // 单路由最大并发数
        pccm.setDefaultMaxPerRoute(DEFAULT_MAX_PERROUTE);
        retryHandler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception , int executionCount , HttpContext context) {
                // 重试5次,从1开始
                if (executionCount > EXECUTION_COUNT) {
                    return false;
                }
                if (exception instanceof UnknownHostException || exception instanceof ConnectTimeoutException || exception instanceof SocketException || exception instanceof SocketTimeoutException
                        || !(exception instanceof SSLException) || exception instanceof NoHttpResponseException) {
                    log.info("http 请求可能出现如下异常：UnknownHostException | ConnectTimeoutException | SocketException | SocketTimeoutException | SSLException | NoHttpResponseException 需要重新执行请求 .....");
                    log.info("http 请求重试信息：重试请求 : " + context.toString() + "  重试执行次数: " + executionCount);
                    return true;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                if (idempotent) {
                    // 如果请求被认为是幂等的，那么就重试。即重复执行不影响程序其他效果的
                    return true;
                }
                return false;
            }
        };
    }


    private HttpClientUtils() {}

    /**
     * 从request获取登录的IP
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }


    /**
     *  判断是否为ajax请求
     * @param request
     * @return
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        if (request.getHeader("accept").indexOf("application/json") > -1
                || (request.getHeader("X-Requested-With") != null && request.getHeader("X-Requested-With").equals(
                "XMLHttpRequest"))) {
            return true;
        }
        return false;
    }


    /**
     * get请求，参数拼接在地址上
     *
     * @param url 请求地址加参数
     * @param headMap  请求参数
     * @return
     */
 /*   public static String httpGet(String url, Map<String, String> headMap) {
        String responseContent = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpGet = new HttpGet(url);
            setGetHead(httpGet, headMap);
            CloseableHttpResponse response = httpclient.execute(httpGet);
            responseContent = getResult(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseContent;
    }*/

    /**
     * get请求，参数放在map里
     *
     * @param url 请求url
     * @param paramMap  请求参数
     * @return
     */
    public static String httpGet(String url, Map<String, Object> paramMap) {
        return httpGet(url, paramMap, null);
    }

    /**
     * get请求，参数放在map里
     *
     * @param url 请求url
     * @param paramMap  请求参数
     * @param headMap
     * @return
     */
    public static String httpGet(String url, Map<String, Object> paramMap, Map<String, String> headMap) {
        String responseContent = null;
        CloseableHttpClient httpclient = gethttpclient(url);
        try {
            log.info("http get 请求地址: " + url.trim());
            log.info("http get 请求参数: " + JSON.toJSONString(paramMap));
            HttpClients.custom().setConnectionManager(pccm).setDefaultRequestConfig(requestConfig).setRetryHandler(retryHandler)
                    .build();
            List<NameValuePair> pairs = new LinkedList<>();
            for(Map.Entry<String,Object> entry : paramMap.entrySet()) {
                Object object = entry.getValue();
                if (object != null) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), String.valueOf(object).trim()));
                }
            }
            URIBuilder builder = new URIBuilder(url.trim());
            builder.setParameters(pairs);
            HttpGet httpGet = new HttpGet(builder.build());
            setGetHead(httpGet, headMap);
            CloseableHttpResponse response = httpclient.execute(httpGet);
            responseContent = getResult(response);
        } catch (SocketTimeoutException | ConnectTimeoutException ex) {
            log.info("http请求连接超时......");
            log.error("http请求连接超时异常信息:" + ex.getMessage());
        } catch (Exception e) {
            log.info("http请求异常..... ");
            log.error("http请求异常信息: " + e.getMessage());
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseContent;
    }

    /**
     * http post 请求
     * @param url　请求url
     * @param paramsMap  请求参数
     * @return
     */
    public static String httpPost(String url, Map<String, Object> paramsMap) {
        return httpPost(url, paramsMap, null);
    }

    /**
     * http的post请求
     *
     * @param url　请求url
     * @param paramsMap　请求参数
     * @param headMap  头部信息
     * @return
     */
    public static String httpPost(String url, Map<String, Object> paramsMap, Map<String, String> headMap) {
        String responseContent = null;
        CloseableHttpClient httpclient = gethttpclient(url);
        try {
            log.info("http post 请求地址: " + url.trim());
            log.info("http post 请求参数: " + JSON.toJSONString(paramsMap));
            HttpClients.custom().setConnectionManager(pccm).setDefaultRequestConfig(requestConfig).setRetryHandler(retryHandler)
                    .build();
            HttpPost httpPost = new HttpPost(url.trim());
            setPostHead(httpPost, headMap);
            setPostParams(httpPost, paramsMap);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            responseContent = getResult(response);
        } catch (SocketTimeoutException | ConnectTimeoutException ex) {
            log.info("http请求连接超时......");
            log.error("http请求连接超时异常信息:" + ex.getMessage());
        } catch (Exception e) {
            log.info("http请求异常..... ");
            log.error("http请求异常信息: " + e.getMessage());
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseContent;
    }

    /**
     * post请求，参数为json字符串
     *
     * @param url  请求url
     * @param jsonParam 请求参数  json字符串
     * @return
     */
    public static String postJson(String url, String jsonParam) {
        return postJson(url, jsonParam, null);
    }

    /**
     * post请求，参数为json字符串
     *
     * @param url  请求url
     * @param jsonParam 请求参数  json字符串
     * @return
     */
    public static String postJson(String url, String jsonParam, Map<String, String> headMap) {
        String responseContent = null;
        CloseableHttpClient httpclient = gethttpclient(url);
        try {
            log.info("http post 请求地址: " + url.trim());
            log.info("http post 请求参数: " + jsonParam);
            HttpClients.custom().setConnectionManager(pccm).setDefaultRequestConfig(requestConfig).setRetryHandler(retryHandler)
                    .build();
            HttpPost httpPost = new HttpPost(url.trim());
            setPostHead(httpPost, headMap);
            if (StringUtils.isNotBlank(jsonParam)) {
                //解决中文乱码问题
                StringEntity entity = new StringEntity(jsonParam.trim(), "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
            }
            CloseableHttpResponse response = httpclient.execute(httpPost);
            responseContent = getResult(response);
        } catch (SocketTimeoutException | ConnectTimeoutException ex) {
            log.info("http请求连接超时......");
            log.error("http请求连接超时异常信息:" + ex.getMessage());
        } catch (Exception e) {
            log.info("http请求异常..... ");
            log.error("http请求异常信息: " + e.getMessage());
        }  finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseContent;
    }

    /**
     * 设置http的HEAD
     *
     * @param httpGet
     * @param headMap
     */
    private static void setGetHead(HttpGet httpGet, Map<String, String> headMap) {
        if (headMap != null && headMap.size() > 0) {
            Set<String> keySet = headMap.keySet();
            for (String key : keySet) {
                httpGet.addHeader(key.trim(), headMap.get(key).trim());
            }
        }
    }

    /**
     * 设置http的HEAD
     *
     * @param httpPost
     * @param headMap
     */
    private static void setPostHead(HttpPost httpPost, Map<String, String> headMap) {
        if (headMap != null && headMap.size() > 0) {
            Set<String> keySet = headMap.keySet();
            for (String key : keySet) {
                httpPost.addHeader(key.trim(), headMap.get(key).trim());
            }
        }
    }

    /**
     * 设置POST的参数
     *
     * @param httpPost
     * @param paramsMap
     * @throws Exception
     */
    private static void setPostParams(HttpPost httpPost, Map<String, Object> paramsMap) throws Exception {
        if (paramsMap != null && paramsMap.size() > 0) {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            Set<String> keySet = paramsMap.keySet();
            for (String key : keySet) {
                Object object = paramsMap.get(key);
                if (object != null) {
                    nvps.add(new BasicNameValuePair(key.trim(), String.valueOf(object).trim()));
                }

            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
        }
    }


    /**
     *
     * @param url
     * @param paramsMap
     * @param headMap
     * @return
     */
    public static String httpPostIgnoreResponseStatus(String url, Map<String, Object> paramsMap, Map<String, String> headMap) {
        String responseContent = null;
        CloseableHttpClient httpclient = gethttpclient(url);
        try {
            log.info("http post 请求地址: " + url.trim());
            log.info("http post 请求参数: " + JSON.toJSONString(paramsMap));
            HttpClients.custom().setConnectionManager(pccm).setDefaultRequestConfig(requestConfig).setRetryHandler(retryHandler)
                    .build();
            HttpPost httpPost = new HttpPost(url.trim());
            setPostHead(httpPost, headMap);
            setPostParams(httpPost, paramsMap);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();
                responseContent = entityToString(entity);
                EntityUtils.consume(entity);
            } catch (SocketTimeoutException | ConnectTimeoutException ex) {
                log.info("http请求连接超时......");
                log.error("http请求连接超时异常信息:" + ex.getMessage());
            } catch (Exception e) {
                log.info("http请求异常..... ");
                log.error("http请求异常信息: " + e.getMessage());
            } finally {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseContent;
    }

    private static String getResult(CloseableHttpResponse response) {
        String result = null;
        try {
            if (response != null ) {
                int currentStatus = response.getStatusLine().getStatusCode();
                if (currentStatus == STATUS.intValue()) {
                    try {
                        HttpEntity entity = response.getEntity();
                        result = entityToString(entity);
                        EntityUtils.consume(entity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("http 请求返回结果: " + result);
        return result;
    }

    /**
     * 将返回结果转化为String
     *
     * @param entity
     * @return
     * @throws Exception
     */
    private static String entityToString(HttpEntity entity) throws Exception {
        String result = null;
        if(entity != null) {
            long lenth = entity.getContentLength();
            if (lenth != -1 && lenth < 4098) {
                result = EntityUtils.toString(entity,"UTF-8");
            } else {
                InputStreamReader reader1 = new InputStreamReader(entity.getContent(), "UTF-8");
                CharArrayBuffer buffer = new CharArrayBuffer(4098);
                char[] tmp = new char[2048];
                int l;
                while((l = reader1.read(tmp)) != -1) {
                    buffer.append(tmp, 0, l);
                }
                result = buffer.toString();
            }
        }
        return result;
    }

    /**
     * 设置  httpclient
     * @param url
     * @return
     */
    private static CloseableHttpClient gethttpclient(String url) {
        CloseableHttpClient httpclient = null;
        try {
            if (url.trim().indexOf("https") != -1) {
                SSLContext sslContext =  new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                    @Override
                    public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                        return true;
                    }
                }).build();
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
                httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            } else {
                httpclient = HttpClients.createDefault();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return httpclient;
    }
}
