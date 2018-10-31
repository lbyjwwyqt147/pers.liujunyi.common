package pers.liujunyi.common.dto;

import lombok.Data;

import java.io.Serializable;

/***
 * BaseDto
 */
@Data
public class BaseDto implements Serializable {

    private static final long serialVersionUID = -5375298588993640910L;

    /** ID */
    private Long id;
}
