package pers.liujunyi.cloud.common.dto;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import pers.liujunyi.cloud.common.util.SystemUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author ljy
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class IdParamDto implements Serializable {

    private static final long serialVersionUID = -602053824373827558L;

    private Long id;
    private Long pid;
    private Long otherId;
    /** 一组 id  必须是 1,2,3 格式  */
    private String ids;
    /** 一组 id  必须是 1,2,3 格式  */
    private String otherIds;
    private List<Long> idList;
    private List<Long> otherIdList;
    private String code;
    /** 一组 code  必须是 1,2,3 格式  */
    private String codes;
    private List<String> codeList;
    /**  修改状态时前端传的json数组   格式必须是  [{id=1,dataVersion=}] */
    private String putParams;
    private Byte status;
    private Long dataVersion;
    public void setIds(String ids) {
        if (StringUtils.isNotBlank(ids)) {
            try {
                this.setIdList(JSONArray.parseArray(ids, Long.class));
            } catch (Exception e) {
                this.setIdList(SystemUtils.idToLong(ids));
            }
        }
        this.ids = ids;
    }

    public void setCodes(String codes) {
        if (StringUtils.isNotBlank(codes)) {
            try {
                this.setCodeList(JSONArray.parseArray(codes, String.class));
            } catch (Exception e) {
                this.setCodeList(SystemUtils.stringToList(codes));
            }
        }
        this.codes = codes;
    }

    public void setOtherIds(String otherIds) {
        if (StringUtils.isNotBlank(otherIds)) {
            try {
                this.setOtherIdList(JSONArray.parseArray(otherIds, Long.class));
            } catch (Exception e) {
                this.setOtherIdList(SystemUtils.idToLong(otherIds));
            }
        }
        this.otherIds = otherIds;
    }
}
