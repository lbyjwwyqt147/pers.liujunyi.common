package pers.liujunyi.cloud.common.util;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ljy
 */
@Data
@Builder
public class DayCompare implements Serializable {
    private static final long serialVersionUID = 1771841350129292430L;
    private int year;
    private int month;
    private int day;
}