package pers.liujunyi.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/***
 * BaseDto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseDto implements Serializable {

    private static final long serialVersionUID = -5375298588993640910L;

    /** ID */
    private Long id;
}
