package pers.liujunyi.cloud.common.vo.tenement;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 租户信息VO
 * @author ljy
 */
@Data
public class TenementVo implements Serializable {
    private static final long serialVersionUID = -3509675789087425445L;

    private Long id;
    /** 手机号码 */
    private String tenementCode;

    /** 租户名称 */
    private String tenementName;

    /**  client_id 第三方应用ID */
    private String appId;

    /**  app_key */
    private String appKey;

    /** client_secret 密钥 */
    private String appSecret;

    /** 域名 */
    private String domainName;

    /** 运营程序版本号（不同租户可能使用不同的版本程序） */
    private String specialVersion;

    /** 到期时间 */
    private Date expireTime;

    /** 0: 启动 1：禁用  */
    private Byte status;
}
