package pers.liujunyi.common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
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
public final class HttpClientUtils {

    private static final  Integer STATUS = 200;

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
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            List<NameValuePair> pairs = new LinkedList<>();
            for(Map.Entry<String,Object> entry : paramMap.entrySet()) {
                Object object = entry.getValue();
                if (object != null) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), String.valueOf(object).trim()));
                }
            }
            URIBuilder builder = new URIBuilder(url);
            builder.setParameters(pairs);
            HttpGet httpGet = new HttpGet(builder.build());
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
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(url);
            setPostHead(httpPost, headMap);
            setPostParams(httpPost, paramsMap);
            CloseableHttpResponse response = httpclient.execute(httpPost);
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
    }

    /**
     * post请求，参数为json字符串
     *
     * @param url  请求url
     * @param jsonParam 请求参数  json字符串
     * @return
     */
    public static String postJson(String url, String jsonParam) {
        String responseContent = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(url);
            if (StringUtils.isNotBlank(jsonParam)) {
                //解决中文乱码问题
                StringEntity entity = new StringEntity(jsonParam, "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
            }
            CloseableHttpResponse response = httpclient.execute(httpPost);
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
                httpGet.addHeader(key, headMap.get(key));
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
                httpPost.addHeader(key, headMap.get(key));
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
                    nvps.add(new BasicNameValuePair(key, String.valueOf(object).trim()));
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
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(url);
            setPostHead(httpPost, headMap);
            setPostParams(httpPost, paramsMap);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();
                responseContent = entityToString(entity);
                EntityUtils.consume(entity);
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
            if (response != null && response.getStatusLine().getStatusCode() == STATUS) {
                try {
                    HttpEntity entity = response.getEntity();
                    result = entityToString(entity);
                    EntityUtils.consume(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        if (entity == null) {
            return null;
        }
        InputStream is = entity.getContent();
        StringBuffer strBuf = new StringBuffer();
        byte[] buffer = new byte[4096];
        int r = 0;
        while ((r = is.read(buffer)) > 0) {
            strBuf.append(new String(buffer, 0, r, "UTF-8"));
        }
        return strBuf.toString();
    }
}
