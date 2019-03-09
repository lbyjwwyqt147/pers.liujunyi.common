package pers.liujunyi.common.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***
 * cloud 访问需要的 Header
 * @author ljy
 */
@Component
public class CloudHeader {

    @Value("${data.coreSystemCode}")
    private String systemCode;
    @Value("${data.coreAppId}")
    private String appId;
    @Value("${data.coreAppKey}")
    private String appKey;
    @Value("${data.coreCredential}")
    private String credential;

    /**
     * 设置Header
     * @return
     */
    public Map<String, String> getHeader() {
        Map<String, String> headerMap = new ConcurrentHashMap<>();
        headerMap.put("systemCode", systemCode.trim());
        headerMap.put("credential", credential.trim());
        headerMap.put("appId", appId.trim());
        headerMap.put("appKey", appKey.trim());
        return headerMap;
    }
}
