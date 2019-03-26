package pers.liujunyi.cloud.common.encrypt;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/***
 * 签名信息
 * @author ljy
 */
@Data
@Component
public class SignInfo {

    @Value("${data.coreAppId}")
    private String appId;

    @Value("${data.coreAppKey}")
    private String appKey;

    @Value("${data.coreCredential}")
    private String credential;

    @Value("${spring.encrypt.signExpireTime}")
    private Integer signExpireTime;

    @Value("${spring.encrypt.secretKey}")
    private  String secretKey;

}
