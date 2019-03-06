package pers.liujunyi.common.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***
 * 业务字典
 *
 * @author ljy
 */
@Component
public class DictUtil {

    @Value("${data.dictUrl}")
    private String dictUrl;
    @Value("${data.coreSystemCode}")
    private String systemCode;
    @Value("${data.coreAppId}")
    private String appId;
    @Value("${data.coreAppKey}")
    private String appKey;
    @Value("${data.coreCredential}")
    private String credential;


    /**
     * 获取业务字典值
     * @param pidDictCode
     * @param dictCode
     * @return
     */
    public String getDictName(String pidDictCode, String dictCode) {
        Map<String, String> head = new ConcurrentHashMap<>();
        head.put("systemCode", systemCode.trim());
        head.put("credential", credential.trim());
        head.put("appId", appId.trim());
        head.put("appKey", appKey.trim());
        Map<String, Object> paramMap = new ConcurrentHashMap<>();
        paramMap.put("systemCode", systemCode.trim());
        paramMap.put("pidDictCode", pidDictCode.trim());
        paramMap.put("dictCode", dictCode.trim());
        String result = HttpClientUtils.httpGet(dictUrl.trim(), paramMap, head);
        JSONObject jsonObject = JSONObject.parseObject(result);
        return StringUtils.isNotBlank(jsonObject.getString("data")) ? jsonObject.getString("data") : "";
    }
}
