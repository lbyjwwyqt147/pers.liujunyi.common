package pers.liujunyi.cloud.common.task;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import org.frameworkset.util.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pers.liujunyi.cloud.common.dto.blogs.OperateLogRecordsDto;
import pers.liujunyi.cloud.common.restful.ResultInfo;

/***
 * 文件名称: LogAsyncTask
 * 文件描述: 日志异步入库任务
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2020/4/2 15:35
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Component
@Log4j2
public class LogAsyncTask {

    @Autowired
    private RestTemplate restTemplate;

    @Async
   public void pushLog(String url, OperateLogRecordsDto logRecords) {
        // 设置restemplate编码为utf-8
        restTemplate.getMessageConverters().set(1,new StringHttpMessageConverter(StandardCharsets.UTF_8));
        MediaType type = MediaType.parseMediaType("application/json;charset=UTF-8");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HttpEntity<String> entity = new HttpEntity<>(JSON.toJSONString(logRecords));
        this.restTemplate.postForObject(url + "ignore/logs/records/s", entity, ResultInfo.class );
    }


}
