package pers.liujunyi.cloud.common.encrypt.filter;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import pers.liujunyi.cloud.common.encrypt.AesEncryptUtils;
import pers.liujunyi.cloud.common.encrypt.SignInfo;
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
@Log4j2
public class SignAuthFilter extends OncePerRequestFilter {

	/** sign 过期时间 */
	private Integer signExpireTime = 60000;


	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(super.getServletContext());
		SignInfo signObj = ctx.getBean(SignInfo.class);
	    httpServletResponse.setCharacterEncoding("UTF-8");
		// 如果是OPTIONS则结束请求
		if (HttpMethod.OPTIONS.toString().equals(httpServletRequest.getMethod())) {
			httpServletResponse.setStatus(HttpStatus.NO_CONTENT.value());
			filterChain.doFilter(httpServletRequest, httpServletResponse);
			return;
		}
		String sign = httpServletRequest.getHeader("sign");
		if (!StringUtils.hasText(sign)) {
			log.info(" >> 非法请求: " + httpServletRequest.getRequestURI() + " 缺少签名信息");
			ResultUtil.writeJavaScript(httpServletResponse, ErrorCodeEnum.SIGN_INVALID);
			return;
		}
		try {
			String decryptBody = AesEncryptUtils.aesDecrypt(sign, signObj.getSecretKey().trim());
			Map<String, Object> signInfo = JsonUtils.getMapper().readValue(decryptBody, Map.class);
			Long signTime = (Long) signInfo.get("signTime");
			String secret = (String) signInfo.get("secret");
			String appKey = (String) signInfo.get("appKey");
			String appId = (String) signInfo.get("appId");
			boolean validateParameter = (Boolean) signInfo.get("parameter");
			if (!secret.equals(signObj.getSecretKey().trim()) || !appKey.equals(signObj.getAppKey().trim()) || !appId.equals(signObj.getAppId().trim()) ) {
				log.info(" >> 非法请求: " + httpServletRequest.getRequestURI() + " 签名信息不正确");
				ResultUtil.writeJavaScript(httpServletResponse, ErrorCodeEnum.SIGN_INVALID);
				return;
			}
			// 签名时间和服务器时间相差10分钟以上则认为是过期请求，此时间可以配置
			if ((System.currentTimeMillis() - signTime) > signObj.getSignExpireTime() * this.signExpireTime) {
				log.info(" >> 非法请求:" + httpServletRequest.getRequestURI() + " 请求已过期");
				ResultUtil.writeJavaScript(httpServletResponse, ErrorCodeEnum.SIGN_TIME_OUT);
				return;
			}

			// POST请求只处理时间
			// GET请求处理参数和时间(参数信息需要在签名信息中才行)
			if(validateParameter && httpServletRequest.getMethod().equals(HttpMethod.GET.name())) {
				Set<String> paramsSet = signInfo.keySet();
				for (String key : paramsSet) {
					if (!"signTime".equals(key)) {
						String signValue = signInfo.get(key).toString();
						String reqValue = httpServletRequest.getParameter(key);
						//签名信息中的参数和请求参数进行比较 看是否一至
						if (!signValue.equals(reqValue)) {
							log.info(" >> 非法请求:" + httpServletRequest.getRequestURI() + " 参数被篡改");
							ResultUtil.writeJavaScript(httpServletResponse, ErrorCodeEnum.SIGN_INVALID, "非法请求:参数被篡改");
							return;
						}
					}
				}
			}
			log.info(" >> 签名校验通过....  ");
		} catch (Exception e) {
			log.info(" >> 非法请求:" + httpServletRequest.getRequestURI());
			e.printStackTrace();
			ResultUtil.writeJavaScript(httpServletResponse, ErrorCodeEnum.SIGN_INVALID);
			return;
		}
		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}

	@Override
	public void destroy() {
		
	}
	
}
