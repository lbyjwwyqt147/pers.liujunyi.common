package pers.liujunyi.cloud.common.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
        //日志对象
        OperateLogRecordsDto logRecord = new OperateLogRecordsDto();
        String logId = UUID.randomUUID().toString().replaceAll("-", "");
        logRecord.setLogId(logId);
        //获取登录用户账户
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //获取系统时间
        logRecord.setResponseStartTime(new Date());
        logRecord.setIpAddress(HttpClientUtils.getIpAddress(httpRequest));
        // 拦截当前正在执行的controller
        Object target = joinPoint.getTarget();
        // 拦截的方法名称。当前正在执行的方法
        String methodName = joinPoint.getSignature().getName();
        // 拦截的方法参数
        Object[] args = joinPoint.getArgs();
        JSONArray operateParamArray = new JSONArray();
        for (int i = 0; i < args.length; i++) {
            Object paramsObj = args[i];
            //通过该方法可查询对应的object属于什么类型：String type = paramsObj.getClass().getName();
            if(paramsObj instanceof String || paramsObj instanceof JSONObject){
                String str = (String) paramsObj;
                //将其转为jsonobject
                JSONObject dataJson = JSONObject.parseObject(str);
                if(dataJson == null || dataJson.isEmpty() || "null".equals(dataJson)){
                    break;
                }else{
                    operateParamArray.add(dataJson);
                }
            }else if(paramsObj instanceof Map){
                //get请求，以map类型传参
                //1.将object的map类型转为jsonobject类型
                Map<String, Object> map = (Map<String, Object>) paramsObj;
                JSONObject json = new JSONObject(map);
                operateParamArray.add(json);
            }
        }
        //设置请求参数
        logRecord.setParameters(operateParamArray.toJSONString());
        // 拦截的放参数类型
        Signature sig = joinPoint.getSignature();
        MethodSignature msig = null;
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        msig = (MethodSignature) sig;

        Class[] parameterTypes = msig.getMethod().getParameterTypes();
        Object object = null;
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
                // get session
                HttpSession httpSession = httpRequest.getSession(true);
                //获取登录用户
                UserDetails user = this.userUtils.getCurrentUserDetail();
                long adminUserId = user.getUserId();
                logRecord.setOperateUserId(adminUserId);
                logRecord.setOperateUserName(user.getUserName());
                logRecord.setOperateUserAccount(user.getUserAccounts());
                logRecord.setOperateUserNumber(user.getUserNumber());
                logRecord.setOperateUserType(user.getUserCategory());
                logRecord.setApplicationName(applicationName);
                // 获取方法上自定义日志注解数据
                ControllerMethodLog systemLog = method.getAnnotation(ControllerMethodLog.class);
                logRecord.setOperateModule(systemLog.operModule());
                logRecord.setOperateMethod(method.getName());
                logRecord.setOperateType(systemLog.operType());
                logRecord.setLogType(systemLog.logType());
                //请求查询操作前数据的spring bean
                String serviceClass = systemLog.serviceClass();
                //请求查询数据的方法
                String queryMethod = systemLog.findDataMethod();
                String tableName = null;
                if (systemLog.operType() == OperateLogType.UPDATE) {
                    //判断是否需要进行操作前的对象参数查询
                    if(StringUtils.isNotBlank(systemLog.parameterKey())
                            &&StringUtils.isNotBlank(systemLog.parameterType())
                            &&StringUtils.isNotBlank(systemLog.findDataMethod())
                            &&StringUtils.isNotBlank(systemLog.serviceClass())){
                        // 参数是否是一组数据
                        boolean isArrayResult = systemLog.paramIsArray();
                        //参数类型
                        String paramType = systemLog.parameterType();
                        String key = systemLog.parameterKey();
                        String paramValue = null;
                        //批量操作
                        if(isArrayResult){
                            key = "ids";
                            paramType = "List<Long>";
                            String value = "";
                            JSONArray beforeParamArray = new JSONArray();
                            for (int i = 0; i < operateParamArray.size(); i++) {
                                JSONObject params =  operateParamArray.getJSONObject(i);
                                JSONArray paramArray = (JSONArray) params.get(key);
                                if (paramArray != null) {
                                    for (int j = 0; j < paramArray.size(); j++) {
                                        paramValue =  paramArray.getString(j);
                                        //在此处判断spring bean查询;的方法参数类型
                                        Object objData = getOperateBeforeData(paramType, serviceClass, queryMethod, paramValue);
                                        // 获取查询数据对象上的注解信息
                                        Table tableAnnotation = objData.getClass().getAnnotation(Table.class);
                                        if (StringUtils.isBlank(tableName)) {
                                            tableName = tableAnnotation.catalog();
                                        }
                                        //JSONObject json = (JSONObject) JSON.toJSON(data);
                                        //beforeParamArray.add(json);
                                    }
                                }
                            }
                        } else {
                            // 单量操作
                            for (int i = 0; i < operateParamArray.size(); i++) {
                                JSONObject params =  operateParamArray.getJSONObject(i);
                                paramValue = params.getString(key);
                                if(StringUtils.isNotBlank(paramValue)){
                                    break;
                                }
                            }
                            //查询根据参数历史数据
                            Object objData = getOperateBeforeData(paramType, serviceClass, queryMethod, paramValue);
                            // 获取查询数据对象上的注解信息
                            Table tableAnnotation = objData.getClass().getAnnotation(Table.class);
                            if (StringUtils.isBlank(tableName)) {
                                tableName = tableAnnotation.catalog();
                            }
                        }
                    }
                }
                logRecord.setTableName(tableName);
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
                } catch (Throwable e) {
                    logRecord.setResultMessage(e.getMessage());
                    logRecord.setLogType((byte)1);
                    logRecord.setOperateStatus((byte) 1);
                }
                //保存进数据库
                logAsyncTask.pushLog(logRecord);
            } else {
                //没有包含注解
                object = joinPoint.proceed();
            }
        } else {
            //不需要拦截直接执行
            object = joinPoint.proceed();
        }
        return object;
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
