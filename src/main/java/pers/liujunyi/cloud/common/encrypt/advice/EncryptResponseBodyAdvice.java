package pers.liujunyi.cloud.common.encrypt.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import pers.liujunyi.cloud.common.encrypt.AesEncryptUtils;
import pers.liujunyi.cloud.common.encrypt.annotation.Encrypt;
import pers.liujunyi.cloud.common.encrypt.autoconfigure.EncryptProperties;

/**
 * 请求响应处理类<br>
 * 
 * 对加了@Encrypt的方法的数据进行加密操作
 * 
 * @author yinjihuan
 * 
 * @about http://cxytiandi.com/about
 *
 */
@Log4j2
@ControllerAdvice
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Object> {

	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	private EncryptProperties encryptProperties;
	
	private static ThreadLocal<Boolean> encryptLocal = new ThreadLocal<Boolean>();
	
	public static void setEncryptStatus(boolean status) {
		encryptLocal.set(status);
	}

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
								  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
		// 可以通过调用EncryptResponseBodyAdvice.setEncryptStatus(false);来动态设置不加密操作
		Boolean status = encryptLocal.get();
		if (status != null && status == false) {
			encryptLocal.remove();
			return body;
		}
		
		long startTime = System.currentTimeMillis();
		boolean encrypt = false;
		if (returnType.getMethod().isAnnotationPresent(Encrypt.class) && !encryptProperties.isDebug()) {
			encrypt = true;
		}
		if (encrypt) {
			try {
				String content = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
				if (!StringUtils.hasText(encryptProperties.getSecretKey())) {
					throw new NullPointerException("请配置spring.encrypt.secretKey");
				}
				String result =  AesEncryptUtils.aesEncrypt(content, encryptProperties.getSecretKey().trim());
				long endTime = System.currentTimeMillis();
				log.debug("AES Encrypt Time:" + (endTime - startTime));
				return result;
			} catch (Exception e) {
				log.error("AES 加密数据异常:", e);
			}
		}
		
		return body;
	}

}
