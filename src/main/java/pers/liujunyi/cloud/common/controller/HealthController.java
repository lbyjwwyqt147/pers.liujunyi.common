package pers.liujunyi.cloud.common.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/***
 * 文件名称: HealthController
 * 文件描述: 健康检查
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2020/4/13 17:15
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@RestController
public class HealthController {

    @GetMapping("/heath")
    @ResponseBody
    public String heath() {
        return "ok";
    }

}
