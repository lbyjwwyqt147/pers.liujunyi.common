package pers.liujunyi.cloud.common.util;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 获取注解信息工具类
 * @author ljy
 */
public class AnnotationUtil {

    /**
     * 获取类上的注解@EntityScan(basePackages = {}) 的 basePackages 值
     * @param clsList
     * @return MongoDb 集合名称
     */
    public static Set<String> getEntityScanBasePackages(List<Class<?>> clsList) {
        Set<String> basePackages = new HashSet<>();
        if (clsList != null && clsList.size() > 0) {
            for (Class<?> curClass : clsList) {
                EntityScan annotation = curClass.getAnnotation(EntityScan.class);
                String[] packages = annotation.basePackages();
                if (packages != null && packages.length > 0) {
                    for (String pack : packages) {
                        basePackages.add(pack);
                    }
                }
            }
        }
        return basePackages;
    }

    /**
     * 获取类上的注解@Document(collection = "集合名称") 的collection 值
     * @param clsList
     * @return MongoDb 集合名称
     */
    public static Set<String> getMongoDocumentCollection(List<Class<?>> clsList) {
        Set<String> collectionList = new HashSet<>();
        if (clsList != null && clsList.size() > 0) {
            for (Class<?> curClass : clsList) {
                //获取类上的注解@Document(collection = "集合名称") 的集合名称
                Document annotation = curClass.getAnnotation(Document.class);
                collectionList.add(annotation.collection());
            }
        }
        return collectionList;
    }

}
