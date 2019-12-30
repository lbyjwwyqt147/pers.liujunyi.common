package pers.liujunyi.cloud.common.controller;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;

/***
 * BaseController
 */
@RequestMapping("/api/{version}")
@RefreshScope
public class BaseController {

}
