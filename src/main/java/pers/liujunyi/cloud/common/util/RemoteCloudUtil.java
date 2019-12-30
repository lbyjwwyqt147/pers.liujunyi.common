package pers.liujunyi.cloud.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pers.liujunyi.cloud.common.vo.tenement.TenementVo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 连接Cloud 工具类
 * @author ljy
 */
@Log4j2
@Component
@RefreshScope
public class RemoteCloudUtil {
    private ThreadPoolExecutor threadPoolExecutor = ThreadPoolExecutorFactory.getThreadPoolExecutor();

    @Value("${data.cloud.url}")
    private String cloudUrl;
    private static final Long SLEEP = 1000L;
    private static final Integer RETRY = 5;

    /**
     * 获取行政区划名称
     * @param id
     * @return
     */
    public String getAreaName(Long id) {
        String areaName = "";
        if (id != null) {
            log.info(" * 开始请求获取行政区划名称 ..................... ");
            Map<String, Object> paramMap = new ConcurrentHashMap<>();
            paramMap.put("id", id);
            String result = HttpClientUtils.httpGet(cloudUrl.trim() + "/v1/ignore/area/name", paramMap);
            JSONObject jsonObject = JSONObject.parseObject(result);
            String data = jsonObject.getString("data");
            areaName = StringUtils.isNotBlank(data) ? data.trim() : "";
        }
        return areaName;
    }




    /**
     * 根据id 获取行政区划名称  返回map
     * @param ids
     * @return  返回 map   key = 编号   value = 名称
     */
    public Map<Long, String> getAreaNameToMap(List<Long> ids) {
        Map<Long, String> areaNameMap = null;
        if (!CollectionUtils.isEmpty(ids)) {
            log.info(" * 开始请求获取行政区划名称 ..................... ");
            Map<String, Object> paramMap = new ConcurrentHashMap<>();
            paramMap.put("ids", StringUtils.join(ids,","));
            String result = HttpClientUtils.httpGet(cloudUrl.trim() + "/v1/ignore/area/map/name", paramMap);
            JSONObject jsonObject = JSONObject.parseObject(result);
            String data = jsonObject.getString("data");
            if (StringUtils.isNotBlank(data)) {
                areaNameMap = JSONObject.parseObject(data, Map.class);
            }
        }
        return areaNameMap;
    }


    /**
     * 单条删除服务器上文件数据
     * @param fileId 文件id
     * @return
     */
    public void deleteFileById(Long fileId) {
        log.info(" 请求单条删除服务器上文件数据 ..................... ");
        threadPoolExecutor.execute(() -> {
            for (int i = 0; i < RETRY; i++) {
                String result = HttpClientUtils.httpDelete(cloudUrl.trim() + "v1/ignore/file/d/" + fileId);
                JSONObject jsonObject = JSONObject.parseObject(result);
                boolean success = jsonObject.getBoolean("success");
                if (success) {
                    break;
                }
                try {
                    Thread.sleep(SLEEP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 批量删除服务器上文件数据
     * @param fileId 文件id 多个文件用,隔开
     * @return
     */
    public void deleteFileByIds(String fileId) {
        log.info(" 请求批量删除服务器上文件数据 ..................... ");
        threadPoolExecutor.execute(() -> {
            for (int i = 0; i < RETRY; i++) {
                String result = HttpClientUtils.httpDelete(cloudUrl.trim() + "/v1/ignore/file/d/b/" + fileId);
                JSONObject jsonObject = JSONObject.parseObject(result);
                boolean success = jsonObject.getBoolean("success");
                if (success) {
                    break;
                }
                try {
                    Thread.sleep(SLEEP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 根据租户ID 获取租户信息
     * @param tenementId
     * @return
     */
    public TenementVo getTenement(Long tenementId) {
        TenementVo tenement = null;
        if (tenementId != null) {
            log.info(" * 开始请求根据租户ID 获取租户信息 ..................... ");
            Map<String, Object> paramMap = new ConcurrentHashMap<>();
            paramMap.put("id", tenementId);
            String result = HttpClientUtils.httpGet(cloudUrl.trim() + "/v1/ignore/tenement/details", paramMap);
            JSONObject jsonObject = JSONObject.parseObject(result);
            String data = jsonObject.getString("data");
            if (StringUtils.isNotBlank(data)) {
                tenement = JSON.parseObject(data, TenementVo.class);
            }
        }
        return tenement;
    }

}
