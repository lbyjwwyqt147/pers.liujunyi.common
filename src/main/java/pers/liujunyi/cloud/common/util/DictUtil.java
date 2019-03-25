package pers.liujunyi.cloud.common.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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
        String dictName = "";
        log.info(" * 开始请求获取业务字典值 ..................... ");
        if (StringUtils.isNotBlank(parentDictCode) && StringUtils.isNotBlank(dictCode)) {
            Map<String, String> header = this.cloudHeader.getHeader();
            Map<String, Object> paramMap = new ConcurrentHashMap<>();
            paramMap.put("systemCode", header.get("systemCode"));
            paramMap.put("parentCode", parentDictCode.trim());
            paramMap.put("dictCode", dictCode.trim());
            String result = HttpClientUtils.httpGet(cloudUrl.trim() + "/v1/dict/dictName", paramMap, header);
            JSONObject jsonObject = JSONObject.parseObject(result);
            dictName = jsonObject.getString("data");
        } else {
            log.info(" * 获取业务字典值 parentDictCode 参数 或者 dictCode 参数 为 空 ");
        }
        return dictName;
    }

    /**
     * 根据fullParentCode获取字典值 返回map
     * @param fullParentCode  父级 dict code
     * @return  返回 map   key = 字典代码   value = 字典名称
     */
    public Map<String, String> getDictNameToMap(String fullParentCode) {
        Map<String, String> dictNameMap = null;
        log.info(" * 开始请求获取业务字典值 ..................... ");
        if (StringUtils.isNotBlank(fullParentCode)) {
            Map<String, String> header = this.cloudHeader.getHeader();
            Map<String, Object> paramMap = new ConcurrentHashMap<>();
            paramMap.put("systemCode", header.get("systemCode"));
            paramMap.put("fullParentCode", fullParentCode.trim());
            String result = HttpClientUtils.httpGet(cloudUrl.trim() + "/v1/dict/map/dictName", paramMap, header);
            JSONObject jsonObject = JSONObject.parseObject(result);
            String data = jsonObject.getString("data");
            if (StringUtils.isNotBlank(data)) {
                dictNameMap = JSONObject.parseObject(data, Map.class);
            }
        } else {
            log.info(" * 获取业务字典值 fullParentCode 参数 为 空 ");
        }
        return dictNameMap;
    }

    /**
     * 根据fullParentCodes 获取字典值 返回map
     * @param parentDictCodes  父级 dict code
     * @return  返回 map   key = 字典代码   value = map
     */
    public Map<String, Map<String, String>> getDictNameToMapList(List<String> parentDictCodes) {
        Map<String, Map<String, String>> dictNameMap = null;
        log.info(" * 开始请求获取业务字典值 ..................... ");
        if (!CollectionUtils.isEmpty(parentDictCodes)) {
            Map<String, String> header = this.cloudHeader.getHeader();
            Map<String, Object> paramMap = new ConcurrentHashMap<>();
            paramMap.put("systemCode", header.get("systemCode"));
            paramMap.put("fullParentCodes", StringUtils.join(parentDictCodes,","));
            String result = HttpClientUtils.httpGet(cloudUrl.trim() + "/v1/dict/map/list/dictName", paramMap, header);
            JSONObject jsonObject = JSONObject.parseObject(result);
            String data = jsonObject.getString("data");
            if (StringUtils.isNotBlank(data)) {
                dictNameMap = JSONObject.parseObject(data, Map.class);
            }
        } else {
            log.info(" * 获取业务字典值 parentDictCodes 参数 为 空 ");
        }

        return dictNameMap;
    }



    /**
     * 获取行政区划名称
     * @param id
     * @return
     */
    public String getAreaName(Long id) {
        String areaName = "";
        if (id != null) {
            log.info(" * 开始请求获取行政区划名称 ..................... ");
            Map<String, String> header = this.cloudHeader.getHeader();
            Map<String, Object> paramMap = new ConcurrentHashMap<>();
            paramMap.put("id", id);
            String result = HttpClientUtils.httpGet(cloudUrl + "/area/name", paramMap, header);
            JSONObject jsonObject = JSONObject.parseObject(result);
            String data = jsonObject.getString("data");
            areaName = StringUtils.isNotBlank(data) ? data.trim() : "";
        }
        return areaName;
    }




    /**
     * 根据id 获取名称  返回map
     * @param ids
     * @return  返回 map   key = 编号   value = 名称
     */
    public Map<Long, String> getAreaNameToMap(List<Long> ids) {
        Map<Long, String> areaNameMap = null;
        if (!CollectionUtils.isEmpty(ids)) {
            log.info(" * 开始请求获取行政区划名称 ..................... ");
            Map<String, String> header = this.cloudHeader.getHeader();
            Map<String, Object> paramMap = new ConcurrentHashMap<>();
            paramMap.put("ids", StringUtils.join(ids,","));
            String result = HttpClientUtils.httpGet(cloudUrl + "/area/map/name", paramMap, header);
            JSONObject jsonObject = JSONObject.parseObject(result);
            String data = jsonObject.getString("data");
            if (StringUtils.isNotBlank(data)) {
                areaNameMap = JSONObject.parseObject(data, Map.class);
            }
        }
        return areaNameMap;
    }
}
