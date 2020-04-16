package pers.liujunyi.cloud.common.task;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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
        this.restTemplate.postForObject(url + "ignore/logs/records/s", JSON.toJSONString(logRecords), ResultInfo.class );
    }


}
