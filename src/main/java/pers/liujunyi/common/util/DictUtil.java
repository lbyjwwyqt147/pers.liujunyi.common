package pers.liujunyi.common.util;

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

    /**
     * 获取业务字典值
     * @param systemCode
     * @param credential
     * @param pidDictCode
     * @param dictCode
     * @return
     */
    public String getDictName(String systemCode, String credential, String pidDictCode, String dictCode) {
        Map<String, Object> paramMap = new ConcurrentHashMap<>();
        paramMap.put("systemCode", systemCode);
        paramMap.put("credential", credential);
        paramMap.put("pidDictCode", pidDictCode);
        paramMap.put("dictCode", dictCode);
        String name = HttpClientUtils.httpGet(dictUrl, paramMap);
        return StringUtils.isNotBlank(name) ? name : "";
    }
}
