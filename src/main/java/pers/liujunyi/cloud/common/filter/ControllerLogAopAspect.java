package pers.liujunyi.cloud.common.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pers.liujunyi.cloud.common.annotation.ControllerMethodLog;
import pers.liujunyi.cloud.common.dto.blogs.ChangeRecordLogDto;
import pers.liujunyi.cloud.common.dto.blogs.OperateLogRecordsDto;
import pers.liujunyi.cloud.common.restful.ResultInfo;
import pers.liujunyi.cloud.common.task.LogAsyncTask;
import pers.liujunyi.cloud.common.util.*;
import pers.liujunyi.cloud.common.vo.user.UserDetails;

import javax.persistence.Table;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/***
 * 文件名称: ControllerLogAopAspect
 * 文件描述: 操作日志切面记录
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2020/4/1 11:34
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Aspect
@Component
@Log4j2
public class ControllerLogAopAspect {

    @Autowired
    private LogAsyncTask logAsyncTask;
    @Autowired
    private UserUtils userUtils;
    @Value("(${spring.application.name})")
    private String applicationName;

    /**
    * 配置接入点
    * @Description: 指定controller的类进行切面　　@Pointcut("execution(* pers.liujunyi.cloud..controller.*(..))")
    * @return: void
    * @Date: 2020/4/1  13:46
    * @Author:
    **/
    @Pointcut("execution(* pers.liujunyi.cloud..controller.*.*(..))")
    private void controllerAspect(){
        System.out.println("point cut start");
    }

    /**
     * 定义一个切入点
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @SuppressWarnings({ "rawtypes", "unused" })
    @Around("controllerAspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object object = null;
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Signature sig = joinPoint.getSignature();
        // 拦截当前正在执行的controller
        Object target = joinPoint.getTarget();
        // 拦截当前正在执行的方法名称
        String methodName = sig.getName();
        MethodSignature msig = null;
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        msig = (MethodSignature) sig;
        Class[] parameterTypes = msig.getMethod().getParameterTypes();
        // 获得被拦截的方法
        Method method = null;
        try {
            method = target.getClass().getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e1) {
            log.error("ControllerLogAopAspect around error",e1);
        } catch (SecurityException e1) {
            log.error("ControllerLogAopAspect around error",e1);
        }
        if (null != method) {
            // 判断是否包含自定义的注解，ControllerMethodLog是自定义的注解
            if (method.isAnnotationPresent(ControllerMethodLog.class)) {
                // 获取方法上自定义日志注解数据
                ControllerMethodLog systemLog = method.getAnnotation(ControllerMethodLog.class);
                if (systemLog.operType() == OperateLogType.UPDATE) {
                    //判断是否需要进行操作前的对象参数查询
                    if (StringUtils.isNotBlank(systemLog.parameterKey())
                            && StringUtils.isNotBlank(systemLog.parameterType())
                            && StringUtils.isNotBlank(systemLog.findDataMethod())) {
                        // 参数是否是一组数据
                        boolean isArrayResult = systemLog.paramIsArray();
                        if (isArrayResult) {
                            // 批量更新

                        } else {
                            // 单个更新
                            object = this.singlePushLog(joinPoint, httpRequest, methodName, systemLog);
                        }
                    }
                }
            }
        }
        return object;
    }


    /**
     * 单条数据操作 日志
     * @param joinPoint
     * @param httpRequest
     * @param methodName
     * @param systemLog
     * @return
     * @throws Throwable
     */
    private Object singlePushLog(ProceedingJoinPoint joinPoint, HttpServletRequest httpRequest, String methodName, ControllerMethodLog systemLog) throws Throwable {
        Object object = null;
        //日志对象
        OperateLogRecordsDto logRecord = this.logRecordsData(joinPoint, httpRequest, methodName, systemLog);
        //请求查询操作前数据的spring bean
        String serviceClass = systemLog.serviceClass().getName();
        //请求查询数据的方法
        String queryMethod = systemLog.findDataMethod();
        String id = null;
        //获取方法所有参数
        Enumeration<String> enu = httpRequest.getParameterNames();
        while(enu.hasMoreElements()){
            String paramKey = enu.nextElement();
            if (paramKey.equals(systemLog.parameterKey())) {
                id =  httpRequest.getParameter(paramKey);
                break;
            }
        }
        // 修改之前的数据
        Object beforeObject = null;
        // 修改之后的数据
        Object afterObject = null;
        boolean update = false;
        if (systemLog.operType() == OperateLogType.UPDATE || StringUtils.isNotBlank(id)) {
            update = true;
            //查询修改之前的数据
            beforeObject = getOperateBeforeData(systemLog.parameterType(), serviceClass, queryMethod, id);
        }
        try {
            //执行页面请求模块方法，并返回
            object = joinPoint.proceed();
            //获取系统时间
            logRecord.setResponseEndTime(new Date());
            //将object 转化为controller封装返回的实体类：RequestResult
            ResultInfo requestResult = (ResultInfo) object;
            logRecord.setResultMessage(JSON.toJSONString(requestResult));
            if(requestResult.getSuccess()){
                logRecord.setOperateStatus((byte) 0);
            }else{
                logRecord.setOperateStatus((byte) 1);
            }
            if (update) {
                //查询修改之前的数据
                afterObject = getOperateBeforeData(systemLog.parameterType(), serviceClass, queryMethod, id);
                // 变更数据日志
                List<ChangeRecordLogDto> changeRecordList = this.buildChangeRecordLogList(logRecord.getLogId(), beforeObject, afterObject);
                logRecord.setChangeDataItem(JSON.toJSONString(changeRecordList));
            }
        } catch (Throwable e) {
            logRecord.setErrorMessage(e.getMessage());
            logRecord.setResultMessage(JSON.toJSONString(object));
            logRecord.setLogType((byte)1);
            logRecord.setOperateStatus((byte) 1);
        }
        logRecord.setExpendTime(logRecord.getResponseEndTime().getTime() - logRecord.getResponseStartTime().getTime());
        //保存进数据库
        logAsyncTask.pushLog(logRecord);
        return object;
    }

    /**
     * 批量数据操作 日志
     * @param joinPoint
     * @param httpRequest
     * @param methodName
     * @param systemLog
     * @return
     * @throws Throwable
     */
    private Object batchPushLog(ProceedingJoinPoint joinPoint, HttpServletRequest httpRequest, String methodName, ControllerMethodLog systemLog) throws Throwable {
        Object object = null;
        List<OperateLogRecordsDto> logRecordList = new CopyOnWriteArrayList<>();
        //日志对象
        OperateLogRecordsDto logRecord =this.logRecordsData(joinPoint, httpRequest, methodName, systemLog);

        String ids = null;
        //获取方法所有参数
        Enumeration<String> enu = httpRequest.getParameterNames();
        String paramName =  systemLog.parameterKey() != null && systemLog.parameterKey().equals("id") ? "ids" : systemLog.parameterKey();
        String parameterType = systemLog.parameterType() != null && systemLog.parameterType().equals("Long") ? "List<Long>" : systemLog.parameterType();
        while(enu.hasMoreElements()){
            String paramKey = enu.nextElement();
            if (paramKey.equals(paramName)) {
                ids =  httpRequest.getParameter(paramKey);
                break;
            }
        }
        //请求查询操作前数据的spring bean
        String serviceClass = systemLog.serviceClass().getName();
        //请求查询数据的方法
        String queryMethod = systemLog.findDataMethod();
        // 修改之前的数据
        Object beforeObject = null;
        // 修改之后的数据
        Object afterObject = null;
        boolean update = false;
        if (systemLog.operType() == OperateLogType.UPDATE || StringUtils.isNotBlank(ids)) {
            update = true;
            //查询修改之前的数据
            beforeObject = getOperateBeforeData(parameterType, serviceClass, queryMethod, ids);
        }
        try {
            //执行页面请求模块方法，并返回
            object = joinPoint.proceed();
            //获取系统时间
            logRecord.setResponseEndTime(new Date());
            //将object 转化为controller封装返回的实体类：RequestResult
            ResultInfo requestResult = (ResultInfo) object;
            logRecord.setResultMessage(JSON.toJSONString(requestResult));
            if(requestResult.getSuccess()){
                logRecord.setOperateStatus((byte) 0);
            }else{
                logRecord.setOperateStatus((byte) 1);
            }
            if (update) {
                //查询修改之前的数据
                afterObject = getOperateBeforeData(parameterType, serviceClass, queryMethod, ids);
                List<Object> beforeObjectList = (List<Object>) beforeObject;
                List<Object> afterObjectList = (List<Object>) afterObject;


                // 变更数据日志
                List<ChangeRecordLogDto> changeRecordList = this.buildChangeRecordLogList(logRecord.getLogId(), beforeObject, afterObject);
                logRecord.setChangeDataItem(JSON.toJSONString(changeRecordList));
            }
        } catch (Throwable e) {
            logRecord.setErrorMessage(e.getMessage());
            logRecord.setResultMessage(JSON.toJSONString(object));
            logRecord.setLogType((byte)1);
            logRecord.setOperateStatus((byte) 1);
        }
        logRecord.setExpendTime(logRecord.getResponseEndTime().getTime() - logRecord.getResponseStartTime().getTime());
        //保存进数据库
        logAsyncTask.pushLog(logRecord);
        return object;
    }

    /**
     * 日志数据
     * @param joinPoint
     * @param httpRequest
     * @param methodName
     * @param systemLog
     * @return
     */
    private OperateLogRecordsDto logRecordsData(ProceedingJoinPoint joinPoint, HttpServletRequest httpRequest, String methodName, ControllerMethodLog systemLog) {
        //日志对象
        OperateLogRecordsDto logRecord = new OperateLogRecordsDto();
        String logId = UUID.randomUUID().toString().replaceAll("-", "");
        logRecord.setLogId(logId);
        //获取系统时间
        logRecord.setResponseStartTime(new Date());
        logRecord.setIpAddress(HttpClientUtils.getIpAddress(httpRequest));
        //获取登录用户
        UserDetails user = this.userUtils.getCurrentUserDetail();
        long adminUserId = user.getUserId();
        logRecord.setOperateUserId(adminUserId);
        logRecord.setOperateUserName(user.getUserName());
        logRecord.setOperateUserAccount(user.getUserAccounts());
        logRecord.setOperateUserNumber(user.getUserNumber());
        logRecord.setOperateUserType(user.getUserCategory());
        logRecord.setApplicationName(applicationName);
        logRecord.setOperateModule(systemLog.operModule());
        logRecord.setOperateMethod(methodName);
        logRecord.setOperateType(systemLog.operType());
        logRecord.setLogType(systemLog.logType());
        // 表名称
        String tableName = null;
        // 获取实体对象上的注解信息
        Table tableAnnotation = systemLog.entityBeanClass().getClass().getAnnotation(Table.class);
        if (StringUtils.isBlank(tableName)) {
            tableName = tableAnnotation.catalog();
        }
        logRecord.setTableName(tableName);
        return logRecord;
    }

    /**
     *
     * 功能描述: <br>
     * 〈功能详细描述〉
     *
     * @param paramType:参数类型
     * @param serviceClass：bean名称
     * @param queryMethod：查询method
     * @param value：查询id的value
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public Object getOperateBeforeData(String paramType,String serviceClass,String queryMethod, String value){
        Object obj = new Object();
        Method  mh = ReflectionUtils.findMethod(ApplicationContextUtils.getBean(serviceClass).getClass(), queryMethod,Long.class );
        //在此处解析请求的参数类型，根据id查询数据，id类型有：int，Integer,long,Long, List<Long>
        if (paramType.equals("int")) {
            int id = Integer.parseInt(value);
            //用spring bean获取操作前的参数,传入的id类型与bean里面的参数类型需要保持一致
            obj = ReflectionUtils.invokeMethod(mh,  ApplicationContextUtils.getBean(serviceClass),id);
        } else if (paramType.equals("Integer")) {
            Integer id = Integer.valueOf(value);
            obj = ReflectionUtils.invokeMethod(mh,  ApplicationContextUtils.getBean(serviceClass),id);
        } else if (paramType.equals("long")) {
            long id = Long.parseLong(value);
            obj = ReflectionUtils.invokeMethod(mh,  ApplicationContextUtils.getBean(serviceClass),id);
        } else if (paramType.equals("Long")) {
            Long id = Long.valueOf(value);
            obj = ReflectionUtils.invokeMethod(mh,  ApplicationContextUtils.getBean(serviceClass),id);
        } else if (paramType.equals("List<Long>")) {
            List<Long> ids = SystemUtils.idToLong(value);
            obj = ReflectionUtils.invokeMethod(mh,  ApplicationContextUtils.getBean(serviceClass), ids);
        }
        return obj;
    }

    /**
     * 构建变更记录
     * @param beforeObject  修改之前数据
     * @param afterObject   修改之后数据
     * @param logId  操作日志记录ID
     * @return
     */
    private List<ChangeRecordLogDto> buildChangeRecordLogList(String logId, Object beforeObject, Object afterObject) {
        List<ChangeRecordLogDto> recordLogList = new CopyOnWriteArrayList<>();
        //获取字段描述
        Map<String, String> fieldMap = CustomerFieldParser.getAllDesc(beforeObject);
        if (fieldMap.size() > 0) {
            // 修改之前数据对象
            Map<String, Object> beforeObjectMap = JSONObject.parseObject(JSON.toJSONString(beforeObject), new TypeReference<Map<String, Object>>(){});
            // 修改之后数据对象
            Map<String, Object> afterObjectMap = JSONObject.parseObject(JSON.toJSONString(afterObject), new TypeReference<Map<String, Object>>(){});
            for (Map.Entry<String, Object> entry : beforeObjectMap.entrySet()) {
                String key = entry.getKey();
                String beforeValue = entry.getValue() != null ? String.valueOf(entry.getValue()) : "";
                // 判断字段是否需要记录日志
                if (fieldMap.containsKey(key)) {
                    String afterValue = afterObjectMap.get(key)!= null ? String.valueOf(afterObjectMap.get(key)) : "";
                    ChangeRecordLogDto recordLog = new ChangeRecordLogDto();
                    recordLog.setLogId(logId);
                    recordLog.setFieldName(ChangeCharUtil.camelToUnderline(key, 1));
                    recordLog.setFieldDescription(fieldMap.get(key));
                    recordLog.setBeforeValue(beforeValue);
                    recordLog.setAfterValue(afterObjectMap.get(key) != null ? String.valueOf(afterObjectMap.get(key)) : "");
                    if (afterValue.equals(beforeValue)) {
                        recordLog.setChangeStatus((byte) 0);
                    } else {
                        recordLog.setChangeStatus((byte) 1);
                    }
                    recordLogList.add(recordLog);
                }
            }
        }
        return recordLogList;
    }
}
