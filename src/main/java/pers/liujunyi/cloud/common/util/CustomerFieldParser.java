package pers.liujunyi.cloud.common.util;

import lombok.extern.log4j.Log4j2;
import pers.liujunyi.cloud.common.annotation.CustomerField;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/***
 * 文件名称: CustomerFieldParser
 * 文件描述: 
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2020/3/30 20:30
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Log4j2
public class CustomerFieldParser {

    /**
     * 通过属性取得属性的描述注解
     *
     * @param field
     * @return
     */
    public static String getDesc(Field field) {
        String result = null;
        try {
            field.setAccessible(true);
            Annotation[] annotation = field.getAnnotations();
            for (Annotation tag : annotation) {
                if (tag instanceof CustomerField) {
                    if (((CustomerField) tag).isLog()) {
                        result = ((CustomerField) tag).desc();
                        break;
                    }
                }
            }
            field.setAccessible(false);
        } catch (SecurityException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 通过对象和属性名称取得属性的描述注解
     *
     * @param obj
     * @param propertyName
     * @return
     */
    public static String getDesc(Object obj, String propertyName) {
        String result = null;
        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.getName().equals(propertyName)) {
                    String desc = getDesc(field);
                    if (desc != null && !desc.isEmpty()) {
                        result = desc;
                        break;
                    }
                }
                field.setAccessible(false);
            }
        } catch (SecurityException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }



    /**
     * 取得obj所有属性的描述注解，返回值为key为obj的属性名称,value为此属性的描述注解
     *
     * @param obj
     * @return Map
     */
    public static Map<String, String> getAllDesc(Object obj) {
        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            return getResult(fields);
        } catch (SecurityException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 取得obj所有属性的描述注解，返回值为key为obj的属性名称,value为此属性的描述注解
     *
     * @param clzName
     * @return Map
     */
    public static Map<String, String> getAllDesc(String clzName) {
        try {
            Field[] fields = Class.forName(clzName).getDeclaredFields();
            return getResult(fields);
        } catch (SecurityException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将field[]里的字段名称做为key和字段描述做value放在map中
     *
     * @param fields
     * @param
     * @return Map
     */
    private static Map<String, String> getResult(Field[] fields) {
        Map<String, String> result = new HashMap<String, String>();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getName().equals("id")) {
                continue;
            }
            String desc = getDesc(field);
            if (desc != null && !desc.isEmpty()) {
                result.put(field.getName(), getDesc(field));
            }
            field.setAccessible(false);
        }
        return result;
    }
	
    
}
