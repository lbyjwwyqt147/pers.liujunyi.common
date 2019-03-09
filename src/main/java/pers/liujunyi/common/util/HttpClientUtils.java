package pers.liujunyi.common.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
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
     * http get请求，参数拼接在地址上
     *
     * @param url 请求地址加参数
     * @return
     */
    public static String httpGet(String url) {
        return httpGetRequest(url, null);
    }


    /**
     * http get请求，参数拼接在地址上
     *
     * @param url 请求地址加参数
     * @param headersMap  header
     * @return
     */
    public static String httpGetRequest(String url, Map<String, String> headersMap) {
        String responseContent = null;
        CloseableHttpClient httpclient = gethttpclient(url);
        try {
            log.info("http get 请求地址: " + url.trim());
            HttpClients.custom().setConnectionManager(pccm).setDefaultRequestConfig(requestConfig).setRetryHandler(retryHandler)
                    .build();
            HttpGet httpGet = new HttpGet(url.trim());
            setGetHead(httpGet, headersMap);
            CloseableHttpResponse response = httpclient.execute(httpGet);
            responseContent = getResult(response);
        }  catch (SocketTimeoutException | ConnectTimeoutException ex) {
            log.info("http get 请求连接超时......");
            log.error("http get 请求连接超时异常信息:" + ex.getMessage());
        } catch (Exception e) {
            log.info("http get 请求异常..... ");
            log.error("http get 请求异常信息: " + e.getMessage());
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
     * http get请求，参数放在map里
     *
     * @param url 请求url
     * @param paramMap  请求参数
     * @return
     */
    public static String httpGet(String url, Map<String, Object> paramMap) {
        return httpGet(url, paramMap, null);
    }

    /**
     * http get请求，参数放在map里
     *
     * @param url 请求url
     * @param paramMap  请求参数
     * @param headersMap
     * @return
     */
    public static String httpGet(String url, Map<String, Object> paramMap, Map<String, String> headersMap) {
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
            setGetHead(httpGet, headersMap);
            CloseableHttpResponse response = httpclient.execute(httpGet);
            responseContent = getResult(response);
        } catch (SocketTimeoutException | ConnectTimeoutException ex) {
            log.info("http get 请求连接超时......");
            log.error("http get 请求连接超时异常信息:" + ex.getMessage());
        } catch (Exception e) {
            log.info("http get 请求异常..... ");
            log.error("http get 请求异常信息: " + e.getMessage());
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
     * @param headersMap  头部信息
     * @return
     */
    public static String httpPost(String url, Map<String, Object> paramsMap, Map<String, String> headersMap) {
        String responseContent = null;
        CloseableHttpClient httpclient = gethttpclient(url);
        try {
            log.info("http post 请求地址: " + url.trim());
            log.info("http post 请求参数: " + JSON.toJSONString(paramsMap));
            HttpClients.custom().setConnectionManager(pccm).setDefaultRequestConfig(requestConfig).setRetryHandler(retryHandler)
                    .build();
            HttpPost httpPost = new HttpPost(url.trim());
            setPostHead(httpPost, headersMap);
            setPostParams(httpPost, paramsMap);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            responseContent = getResult(response);
        } catch (SocketTimeoutException | ConnectTimeoutException ex) {
            log.info("http post 请求连接超时......");
            log.error("http post 请求连接超时异常信息:" + ex.getMessage());
        } catch (Exception e) {
            log.info("http post 请求异常..... ");
            log.error("http post 请求异常信息: " + e.getMessage());
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
    public static String postJson(String url, String jsonParam, Map<String, String> headersMap) {
        String responseContent = null;
        CloseableHttpClient httpclient = gethttpclient(url);
        try {
            log.info("http post 请求地址: " + url.trim());
            log.info("http post 请求参数: " + jsonParam);
            HttpClients.custom().setConnectionManager(pccm).setDefaultRequestConfig(requestConfig).setRetryHandler(retryHandler)
                    .build();
            HttpPost httpPost = new HttpPost(url.trim());
            setPostHead(httpPost, headersMap);
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
            log.info("http post 请求连接超时......");
            log.error("http post 请求连接超时异常信息:" + ex.getMessage());
        } catch (Exception e) {
            log.info("http post 请求异常..... ");
            log.error("http post 请求异常信息: " + e.getMessage());
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
     * 发送 http put 请求，参数以map方式传递
     * @param url
     * @param  paramsMap  map 参数
     * @param  headers
     * @return
     */
    public static String httpPut(String url, Map<String,Object> paramsMap, Map<String,String> headers){
        String responseContent = null;
        log.info("http put 请求地址: " + url.trim());
        log.info("http put 参数: " + JSON.toJSONString(paramsMap));
        CloseableHttpClient httpclient = gethttpclient(url);
        //设置参数
        List<NameValuePair> nvps = new ArrayList<>();
        for (Iterator iter = paramsMap.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String value = String.valueOf(paramsMap.get(name));
            nvps.add(new BasicNameValuePair(name.trim(), value.trim()));
        }
        try {
            HttpClients.custom().setConnectionManager(pccm).setDefaultRequestConfig(requestConfig).setRetryHandler(retryHandler)
                    .build();
            URIBuilder builder = new URIBuilder(url.trim());
            builder.setParameters(nvps);
            HttpPut httpPut = new HttpPut(builder.build());
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpPut.setHeader(entry.getKey().trim(), entry.getValue().trim());
                }
            }
            CloseableHttpResponse response = httpclient.execute(httpPut);
            responseContent = getResult(response);
        } catch (SocketTimeoutException | ConnectTimeoutException ex) {
            log.info("http put 请求连接超时......");
            log.error("http put 请求连接超时异常信息:" + ex.getMessage());
        } catch (Exception e) {
            log.info("http put 请求异常..... ");
            log.error("http put 请求异常信息: " + e.getMessage());
        }  finally{
            try {
                //关闭连接、释放资源
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseContent;
    }

    /**
     * 发送 http put 请求，参数json符串进行提交
     * @param url
     * @param  stringJson  json字符串参数
     * @param  headers
     * @return
     */
    public static String httpPutJson(String url, String stringJson, Map<String,String> headers){
        String responseContent = null;
        log.info("http put 请求地址: " + url.trim());
        log.info("http put 参数: " + stringJson);
        String  encode = "utf-8";
        CloseableHttpClient httpclient = gethttpclient(url);
        HttpPut httpPut = new HttpPut(url.trim());
        //设置header
        httpPut.setHeader("Content-type", "application/json");
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPut.setHeader(entry.getKey().trim(), entry.getValue().trim());
            }
        }
        //组织请求参数
        StringEntity stringEntity = new StringEntity(stringJson.trim(), encode);
        stringEntity.setContentEncoding("UTF-8");
        stringEntity.setContentType("application/json");
        httpPut.setEntity(stringEntity);
        try {
            HttpClients.custom().setConnectionManager(pccm).setDefaultRequestConfig(requestConfig).setRetryHandler(retryHandler)
                    .build();
            CloseableHttpResponse response = httpclient.execute(httpPut);
            responseContent = getResult(response);
        } catch (SocketTimeoutException | ConnectTimeoutException ex) {
            log.info("http put 请求连接超时......");
            log.error("http put 请求连接超时异常信息:" + ex.getMessage());
        } catch (Exception e) {
            log.info("http put 请求异常..... ");
            log.error("http put 请求异常信息: " + e.getMessage());
        }  finally{
            try {
                //关闭连接、释放资源
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseContent;
    }

    /**
     * 发送http delete请求 参数拼接在地址上
     * @param url 参数拼接在地址上
     * @return
     */
    public static String httpDelete(String url){
        return httpDelete(url, null);
    }

    /**
     * 发送http delete请求 参数map格式
     * @param url
     * @param  paramsMap
     * @param headersMap
     * @return
     */
    public static String httpDelete(String url, Map<String,Object> paramsMap, Map<String,String> headersMap){
        log.info("http delete 请求地址: " + url.trim());
        log.info("http delete 请求参数: " + JSON.toJSONString(paramsMap));
        String responseContent = null;
        CloseableHttpClient httpclient = gethttpclient(url);
        //设置参数
        List<NameValuePair> nvps = new ArrayList<>();
        for (Iterator iter = paramsMap.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String value = String.valueOf(paramsMap.get(name));
            nvps.add(new BasicNameValuePair(name.trim(), value.trim()));
        }
        try {
            HttpClients.custom().setConnectionManager(pccm).setDefaultRequestConfig(requestConfig).setRetryHandler(retryHandler)
                    .build();
            URIBuilder builder = new URIBuilder(url.trim());
            builder.setParameters(nvps);
            HttpDelete httpDelete = new HttpDelete(builder.build());
            if (headersMap != null && headersMap.size() > 0) {
                for (Map.Entry<String, String> entry : headersMap.entrySet()) {
                    httpDelete.setHeader(entry.getKey().trim(), entry.getValue().trim());
                }
            }
            CloseableHttpResponse response = httpclient.execute(httpDelete);
            responseContent = getResult(response);
        }  catch (SocketTimeoutException | ConnectTimeoutException ex) {
            log.info("http delete 请求连接超时......");
            log.error("http delete 请求连接超时异常信息:" + ex.getMessage());
        } catch (Exception e) {
            log.info("http delete 请求异常..... ");
            log.error("http delete 请求异常信息: " + e.getMessage());
        }  finally{
            try {
                //关闭连接、释放资源
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseContent;
    }

    /**
     * 发送http delete请求 参数拼接在地址上
     * @param url 参数拼接在地址上
     * @param headersMap
     * @return
     */
    public static String httpDelete(String url, Map<String,String> headersMap){
        log.info("http delete 请求地址: " + url.trim());
        String responseContent = null;
        CloseableHttpClient httpclient = gethttpclient(url);
        HttpDelete httpDelete = new HttpDelete(url.trim());
        //设置header
        if (headersMap != null && headersMap.size() > 0) {
            for (Map.Entry<String, String> entry : headersMap.entrySet()) {
                httpDelete.setHeader(entry.getKey().trim(), entry.getValue().trim());
            }
        }
        try {
            HttpClients.custom().setConnectionManager(pccm).setDefaultRequestConfig(requestConfig).setRetryHandler(retryHandler)
                    .build();
            CloseableHttpResponse response = httpclient.execute(httpDelete);
            responseContent = getResult(response);
        }  catch (SocketTimeoutException | ConnectTimeoutException ex) {
            log.info("http delete 请求连接超时......");
            log.error("http delete 请求连接超时异常信息:" + ex.getMessage());
        } catch (Exception e) {
            log.info("http delete 请求异常..... ");
            log.error("http delete 请求异常信息: " + e.getMessage());
        }  finally{
            try {
                //关闭连接、释放资源
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseContent;
    }

    /**
     * 发送 http post 请求，支持文件上传
     * @param url url
     * @param params 参数
     * @param files 文件
     * @param headers
     * @return
     */
    public static String httpPostFormMultipart(String url, Map<String,Object> params, List<File> files, Map<String,String> headers){
        String responseContent = null;
        String   encode = "utf-8";
        CloseableHttpClient httpclient = gethttpclient(url);
        HttpPost httPost = new HttpPost(url.trim());
        setPostHead(httPost, headers);
        MultipartEntityBuilder mEntityBuilder = MultipartEntityBuilder.create();
        mEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        mEntityBuilder.setCharset(Charset.forName(encode));
        // 普通参数
        ContentType contentType = ContentType.create("text/plain", Charset.forName(encode));
        if (params != null && params.size() > 0) {
            Set<String> keySet = params.keySet();
            for (String key : keySet) {
                mEntityBuilder.addTextBody(key.trim(), String.valueOf(params.get(key)).trim(), contentType);
            }
        }
        //二进制参数
        if (files != null && files.size() > 0) {
            for (File file : files) {
                mEntityBuilder.addBinaryBody("file", file);
            }
        }
        httPost.setEntity(mEntityBuilder.build());
        try {
            HttpClients.custom().setConnectionManager(pccm).setDefaultRequestConfig(requestConfig).setRetryHandler(retryHandler)
                    .build();
            CloseableHttpResponse response = httpclient.execute(httPost);
            responseContent = getResult(response);
        }  catch (SocketTimeoutException | ConnectTimeoutException ex) {
            log.info("http post 请求连接超时......");
            log.error("http post 请求连接超时异常信息:" + ex.getMessage());
        } catch (Exception e) {
            log.info("http post 请求异常..... ");
            log.error("http post 请求异常信息: " + e.getMessage());
        }  finally{
            try {
                //关闭连接、释放资源
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseContent;
    }

    /**
     * 发送 post 请求 返回任何状态下的数据
     * @param url
     * @param paramsMap
     * @param headersMap
     * @return
     */
    public static String httpPostIgnoreResponseStatus(String url, Map<String, Object> paramsMap, Map<String, String> headersMap) {
        String responseContent = null;
        CloseableHttpClient httpclient = gethttpclient(url);
        try {
            log.info("http post 请求地址: " + url.trim());
            log.info("http post 请求参数: " + JSON.toJSONString(paramsMap));
            HttpClients.custom().setConnectionManager(pccm).setDefaultRequestConfig(requestConfig).setRetryHandler(retryHandler)
                    .build();
            HttpPost httpPost = new HttpPost(url.trim());
            setPostHead(httpPost, headersMap);
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



    /**
     * 设置http的HEAD
     *
     * @param httpGet
     * @param headersMap
     */
    private static void setGetHead(HttpGet httpGet, Map<String, String> headersMap) {
        if (headersMap != null && headersMap.size() > 0) {
            Set<String> keySet = headersMap.keySet();
            for (String key : keySet) {
                httpGet.addHeader(key.trim(), headersMap.get(key).trim());
            }
        }
    }

    /**
     * 设置http的HEAD
     *
     * @param httpPost
     * @param headersMap
     */
    private static void setPostHead(HttpPost httpPost, Map<String, String> headersMap) {
        if (headersMap != null && headersMap.size() > 0) {
            Set<String> keySet = headersMap.keySet();
            for (String key : keySet) {
                httpPost.addHeader(key.trim(), headersMap.get(key).trim());
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
     * 处理 返回结果数据
     * @param response
     * @return
     */
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
