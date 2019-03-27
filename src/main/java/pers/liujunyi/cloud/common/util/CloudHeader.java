package pers.liujunyi.cloud.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pers.liujunyi.cloud.common.encrypt.AesEncryptUtils;

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
    @Value("${data.cloudAppId}")
    private String cloudAppId;
    @Value("${data.cloudAppKey}")
    private String cloudAppKey;
    @Value("${spring.encrypt.secretKey}")
    private  String secretKey;

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
        headerMap.put("sign", this.buildSign());
        return headerMap;
    }

    /**
     * 构建数字签名信息
     * @return
     */
    private String buildSign(){
        Map<String, String> signMap = new ConcurrentHashMap<>();
        signMap.put("appId", cloudAppId.trim());
        signMap.put("appKey", cloudAppKey.trim());
        signMap.put("secret", secretKey.trim());
        signMap.put("signTime",String.valueOf(System.currentTimeMillis()));
        return AesEncryptUtils.aesEncrypt(signMap, secretKey.trim());
    }
}
