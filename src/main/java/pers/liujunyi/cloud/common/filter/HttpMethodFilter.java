package pers.liujunyi.cloud.common.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.HiddenHttpMethodFilter;

/**
 * 解决 put 请求  delete 请求 无法获取参数问题
 */
@Order(value = -999999999)
@Component
public class HttpMethodFilter extends HiddenHttpMethodFilter {

}
