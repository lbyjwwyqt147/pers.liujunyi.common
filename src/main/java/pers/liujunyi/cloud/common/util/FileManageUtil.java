package pers.liujunyi.cloud.common.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import pers.liujunyi.common.restful.ResultInfo;
import pers.liujunyi.common.restful.ResultUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/***
 * 文件管理 工具类
 *
 * @author ljy
 */
@Log4j2
@Component
public class FileManageUtil {
    private ThreadPoolExecutor threadPoolExecutor = ThreadPoolExecutorFactory.getThreadPoolExecutor();

    @Value("${data.cloudUrl}")
    private String cloudUrl;
    @Autowired
    private CloudHeader cloudHeader;
    private static final Long SLEEP = 1000L;
    private static final Integer RETRY = 5;


    /**
     * 单条删除服务器上文件数据
     * @param fileId 文件id
     * @return
     */
    public void singleDeleteById(Long fileId) {
        log.info(" 请求单条删除服务器上文件数据 ..................... ");
        Map<String, String> header = this.cloudHeader.getHeader();
        threadPoolExecutor.execute(() -> {
            for (int i = 0; i < RETRY; i++) {
                String result = HttpClientUtils.httpDelete(cloudUrl.trim() + "/file/delete/" + fileId, header);
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
    public void batchDeleteById(String fileId) {
        log.info(" 请求批量删除服务器上文件数据 ..................... ");
        Map<String, String> header = this.cloudHeader.getHeader();
        threadPoolExecutor.execute(() -> {
            for (int i = 0; i < RETRY; i++) {
                String result = HttpClientUtils.httpDelete(cloudUrl.trim() + "/file/batchDelete/" + fileId, header);
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

}
