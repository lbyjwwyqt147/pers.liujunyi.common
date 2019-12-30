package pers.liujunyi.cloud.common.encrypt.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/***
 *	encrypt 属性信息
 * @author ljy
 */
@Data
@ConfigurationProperties(prefix = "spring.encrypt")
@RefreshScope
public class EncryptProperties {
	/**
	 * RSA 私钥
	 */
	private String privateKey;
	/**
	 * RSA 公钥
	 */
	private String publicKey;

	/**
	 * AES 密匙
	 */
	private String secretKey;

	private String charset = "UTF-8";
	
	/**
	 * 开启调试模式，调试模式下不进行加解密操作，用于像Swagger这种在线API测试场景
	 */
	private boolean debug = false;
	
	/**
	 * 签名过期时间（分钟）
	 */
	private Long signExpireTime = 10L;

}
