package pers.liujunyi.cloud.common.task;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pers.liujunyi.cloud.common.dto.blogs.OperateLogRecordsDto;

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
public class LogAsyncTask {


    @Async
   public void pushLog(OperateLogRecordsDto logRecords) {

   }


}
