package pers.liujunyi.common.vo.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/***
 * 文件名称: FileDataVo.java
 * 文件描述: 文件 VO.
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年01月17日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDataVo implements Serializable {

    private static final long serialVersionUID = 641910025324617490L;

    private Long id;

    /** 文件初始名称(上传文件的原始名称) */
    private String fileInitialName;

    /** 文件名称 */
    private String fileName;

    /** 文件访问地址 */
    private String fileCallAddress;

    /** 文件大小（kb） */
    private Double fileSize;

    /**
     * 文件分类 0：图片 1：文档  2：视频  5：其他
     */
    private Byte fileCategory;

}
