package pers.liujunyi.common.util;

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

/***
 * 文件管理 工具类
 *
 * @author ljy
 */
@Log4j2
@Component
public class FileManageUtil {

    @Value("${data.cloudUrl}")
    private String cloudUrl;
    @Autowired
    private CloudHeader cloudHeader;



    /**
     * 单条删除服务器上文件数据
     * @param fileId 文件id
     * @return
     */
    public Boolean singleDeleteById(Long fileId) {
        log.info(" * 开启请求单条删除服务器上文件数据 ..................... ");
        Map<String, String> header = this.cloudHeader.getHeader();
        String result = HttpClientUtils.httpDelete(cloudUrl.trim() + "/file/delete/" + fileId, header);
        JSONObject jsonObject = JSONObject.parseObject(result);
        return jsonObject.getBoolean("success");
    }

    /**
     * 批量删除服务器上文件数据
     * @param fileId 文件id 多个文件用,隔开
     * @return
     */
    public Boolean batchDeleteById(String fileId) {
        log.info(" * 开启请求批量删除服务器上文件数据 ..................... ");
        Map<String, String> header = this.cloudHeader.getHeader();
        String result = HttpClientUtils.httpDelete(cloudUrl.trim() + "/file/batchDelete/" + fileId, header);
        JSONObject jsonObject = JSONObject.parseObject(result);
        return jsonObject.getBoolean("success");
    }

}
