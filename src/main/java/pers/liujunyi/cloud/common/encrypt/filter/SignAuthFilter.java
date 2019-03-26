package pers.liujunyi.cloud.common.encrypt.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import pers.liujunyi.cloud.common.encrypt.AesEncryptUtils;
import pers.liujunyi.cloud.common.exception.ErrorCodeEnum;
import pers.liujunyi.cloud.common.restful.ResultUtil;
import pers.liujunyi.cloud.common.util.JsonUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;


/**
 * 请求签名验证过滤器<br>
 * 
 * 请求头中获取sign进行校验，判断合法性和是否过期<br>
 * 
 * sign=加密({参数：值, 参数2：值2, signTime:签名时间戳})
 * @author ljy
 *
 *
 */
@Slf4j
public class SignAuthFilter extends OncePerRequestFilter {

	/**  AES 密匙 */
	private String secretKey;
	/** 签名过期 分钟数 */
	private Integer signExpireMinute;
	/** sign 过期时间 */
	private Integer signExpireTime = 60000;

	public SignAuthFilter(String secretKey, Integer signExpireMinute) {
		this.secretKey = secretKey.trim();
		this.signExpireMinute = signExpireMinute;
	}


	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
		httpServletResponse.setCharacterEncoding("UTF-8");
		String sign = httpServletRequest.getHeader("sign");
		if (!StringUtils.hasText(sign)) {
			log.info("非法请求:缺少签名信息");
			ResultUtil.writeJavaScript(httpServletResponse, ErrorCodeEnum.PARAMS, "非法请求:缺少签名信息");
			return;
		}
		try {
			String decryptBody = AesEncryptUtils.aesDecrypt(sign, this.secretKey);
			Map<String, Object> signInfo = JsonUtils.getMapper().readValue(decryptBody, Map.class);
			Long signTime = (Long) signInfo.get("signTime");
			String secret = (String) signInfo.get("secret");
			if (!secret.equals(this.secretKey)) {
				log.info("非法请求: 密钥 secret 错误");
				ResultUtil.writeJavaScript(httpServletResponse, ErrorCodeEnum.PARAMS, "非法请求:非法凭证");
				return;
			}
			// 签名时间和服务器时间相差10分钟以上则认为是过期请求，此时间可以配置
			if ((System.currentTimeMillis() - signTime) > this.signExpireMinute * this.signExpireTime) {
				log.info("非法请求:请求已过期");
				ResultUtil.writeJavaScript(httpServletResponse, ErrorCodeEnum.PARAMS, "非法请求:请求已过期");
				return;
			}

			// POST请求只处理时间
			// GET请求处理参数和时间
			if(httpServletRequest.getMethod().equals(HttpMethod.GET.name())) {
				Set<String> paramsSet = signInfo.keySet();
				for (String key : paramsSet) {
					if (!"signTime".equals(key)) {
						String signValue = signInfo.get(key).toString();
						String reqValue = httpServletRequest.getParameter(key).toString();
						if (!signValue.equals(reqValue)) {
							log.info("非法请求:参数被篡改");
							ResultUtil.writeJavaScript(httpServletResponse, ErrorCodeEnum.PARAMS, "非法请求:参数被篡改");
							return;
						}
					}
				}
			}
		} catch (Exception e) {
			log.info("非法请求:" + e.getMessage());
			ResultUtil.writeJavaScript(httpServletResponse, ErrorCodeEnum.PARAMS, "非法请求:" + e.getMessage());
			return;
		}
		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}

	@Override
	public void destroy() {
		
	}
	
}
