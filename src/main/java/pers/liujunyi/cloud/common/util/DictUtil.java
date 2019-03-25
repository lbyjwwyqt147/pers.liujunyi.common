package pers.liujunyi.cloud.common.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***
 * 业务字典 工具类
 *
 * @author ljy
 */
@Log4j2
@Component
public class DictUtil {

    @Value("${data.dictUrl}")
    private String dictUrl;
    @Autowired
    private CloudHeader cloudHeader;
    @Value("${data.cloudUrl}")
    private String cloudUrl;

      /**
     * 获取业务字典值
     * @param parentDictCode  父级字典代码
     * @param dictCode        需要转换的字典代码
     * @return
     */
    public String getDictName(String parentDictCode, String dictCode) {
        log.info(" * 开始请求获取业务字典值 ..................... ");
        Map<String, String> header = this.cloudHeader.getHeader();
        Map<String, Object> paramMap = new ConcurrentHashMap<>();
        paramMap.put("systemCode", header.get("systemCode"));
        paramMap.put("pidDictCode", parentDictCode.trim());
        paramMap.put("dictCode", dictCode.trim());
        String result = HttpClientUtils.httpGet(dictUrl.trim(), paramMap, header);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String data = jsonObject.getString("data");
        return StringUtils.isNotBlank(data) ? data.trim() : "";
    }

    /**
     * 获取业务字典值 返回 map  key = dictCode  value = 字典名称
     * @param parentDictCode 父级字典代码
     * @return
     */
    public String getDictNameToMap(String parentDictCode) {
        log.info(" * 开始请求获取业务字典值 ..................... ");
        Map<String, String> header = this.cloudHeader.getHeader();
        Map<String, Object> paramMap = new ConcurrentHashMap<>();
        paramMap.put("systemCode", header.get("systemCode"));
        paramMap.put("pidDictCode", parentDictCode.trim());
        String result = HttpClientUtils.httpGet(dictUrl.trim(), paramMap, header);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String data = jsonObject.getString("data");
        return StringUtils.isNotBlank(data) ? data.trim() : "";
    }

    /**
     * 获取业务字典值 返回 map  key = dictCode  value = 字典名称
     * @param parentDictCodes 父级字典代码
     * @return
     */
    public String getDictNameToMapList(List<String> parentDictCodes) {
        log.info(" * 开始请求获取业务字典值 ..................... ");
        Map<String, String> header = this.cloudHeader.getHeader();
        Map<String, Object> paramMap = new ConcurrentHashMap<>();
        paramMap.put("systemCode", header.get("systemCode"));
       // paramMap.put("pidDictCode", parentDictCode.trim());
        String result = HttpClientUtils.httpGet(dictUrl.trim(), paramMap, header);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String data = jsonObject.getString("data");
        return StringUtils.isNotBlank(data) ? data.trim() : "";
    }



    /**
     * 获取行政区划名称
     * @param id
     * @return
     */
    public String getAreaName(Long id) {
        log.info(" * 开始请求获取行政区划名称 ..................... ");
        Map<String, String> header = this.cloudHeader.getHeader();
        Map<String, Object> paramMap = new ConcurrentHashMap<>();
        paramMap.put("id", id);
        String result = HttpClientUtils.httpGet(cloudUrl + "/area/name", paramMap, header);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String data = jsonObject.getString("data");
        return StringUtils.isNotBlank(data) ? data.trim() : "";
    }


    /**
     * 获取行政区划 全名称
     * @param id
     * @return
     */
    public String getFullAreaName(Long id) {
        log.info(" * 开始请求获取行政区划名称 ..................... ");
        Map<String, String> header = this.cloudHeader.getHeader();
        Map<String, Object> paramMap = new ConcurrentHashMap<>();
        paramMap.put("id", id);
        String result = HttpClientUtils.httpGet(cloudUrl + "/area/name", paramMap, header);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String data = jsonObject.getString("data");
        return StringUtils.isNotBlank(data) ? data.trim() : "";
    }


    /**
     * 获取行政区划名称
     * @param id
     * @return
     */
    public String getAreaNameToMap(Long id) {
        log.info(" * 开始请求获取行政区划名称 ..................... ");
        Map<String, String> header = this.cloudHeader.getHeader();
        Map<String, Object> paramMap = new ConcurrentHashMap<>();
        paramMap.put("id", id);
        String result = HttpClientUtils.httpGet(cloudUrl + "/area/name", paramMap, header);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String data = jsonObject.getString("data");
        return StringUtils.isNotBlank(data) ? data.trim() : "";
    }
}
