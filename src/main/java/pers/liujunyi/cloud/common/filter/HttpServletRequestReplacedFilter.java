package pers.liujunyi.cloud.common.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pers.liujunyi.cloud.common.encrypt.AesEncryptUtils;
import pers.liujunyi.cloud.common.util.HttpClientUtils;
import pers.liujunyi.cloud.common.util.JsonUtils;
import pers.liujunyi.cloud.common.util.SystemUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/***
 * 文件名称: Base
 * 文件描述: 
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2020/4/15 16:44
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Component
@Log4j2
public class HttpServletRequestReplacedFilter extends OncePerRequestFilter {

    @Value("${spring.encrypt.secretKey}")
    private  String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        String servletPath = httpServletRequest.getRequestURI();
        BodyReaderHttpServletRequestWrapper requestWrapper = null;
        if(httpServletRequest instanceof HttpServletRequest) {
            requestWrapper = new BodyReaderHttpServletRequestWrapper(httpServletRequest);
        }
        if (!servletPath.equals("/heath") && !servletPath.equals("/") && !servletPath.equals("/oauth/token")) {
            // 获取url携带的参数信息
            Map<String, Object> params = HttpClientUtils.getAllRequestParam(httpServletRequest);
            // 获取 body 参数信息
            String bodyParams = new String(requestWrapper.getBody(), "ISO-8859-1");
            if (StringUtils.isNotBlank(bodyParams)) {
                if (params == null ) {
                    params = new HashMap<>();
                }
                if (JsonUtils.isjson(bodyParams)) {
                    params.putAll(JSONObject.parseObject(bodyParams, Map.class));
                } else if (SystemUtils.isBase64(bodyParams)) {
                    String decryptBody = AesEncryptUtils.aesDecrypt(bodyParams, secretKey.trim());
                    params.putAll(JsonUtils.getMapper().readValue(decryptBody, Map.class));
                } else {
                    params.putAll(HttpClientUtils.paramToMap(bodyParams));
                }
            }
            //获取Header所有参数
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", httpServletRequest.getHeader("Authorization"));
            headers.put("sign", httpServletRequest.getHeader("sign"));
            headers.put("tenement", httpServletRequest.getHeader("tenement"));
            headers.put("userId", httpServletRequest.getHeader("userId"));
            headers.put("contentType", httpServletRequest.getHeader("content-type"));
            headers.put("host", httpServletRequest.getHeader("host"));
            log.info("当前访问的URL地址：【" + servletPath + "】 Method：" + httpServletRequest.getMethod() + " Params：" + JSON.toJSONString(params) + " Headers：" + JSON.toJSONString(headers));
        }
        if(null == requestWrapper) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } else {
            filterChain.doFilter(requestWrapper, httpServletResponse);
        }
    }

    @Override
    public void destroy() {
        //Do nothing
    }

}
