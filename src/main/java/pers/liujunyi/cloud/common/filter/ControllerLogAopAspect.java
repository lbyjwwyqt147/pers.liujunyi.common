package pers.liujunyi.cloud.common.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pers.liujunyi.cloud.common.dto.blogs.OperateLogRecordsDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;

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
public class ControllerLogAopAspect {


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
     * @param pjp
     * @return
     * @throws Throwable
     */
    @SuppressWarnings({ "rawtypes", "unused" })
    @Around("controllerAspect()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        //日志对象
        OperateLogRecordsDto log = new OperateLogRecordsDto();
        //获取登录用户账户
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        //方法通知前获取时间,为什么要记录这个时间呢？当然是用来计算模块执行时间的
        //获取系统时间
        String time = new SimpleDateFormat(FucdnStrConstant.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND.getConstant()).format(new Date());
        log.setStartTime(time);

        //获取系统ip,这里用的是我自己的工具类,可自行网上查询获取ip方法
        //String ip = GetLocalIp.localIp();
        //log.setIP(ip);

        // 拦截的实体类，就是当前正在执行的controller
        Object target = pjp.getTarget();
        // 拦截的方法名称。当前正在执行的方法
        String methodName = pjp.getSignature().getName();
        // 拦截的方法参数
        Object[] args = pjp.getArgs();
        //String params = Arrays.toString(pjp.getArgs());
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
                JSONObject json =new JSONObject(map);
                operateParamArray.add(json);
            }
        }
        //设置请求参数
        log.setOperateParams(operateParamArray.toJSONString());
        // 拦截的放参数类型
        Signature sig = pjp.getSignature();
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
            LOGGER.error("ControllerLogAopAspect around error",e1);
        } catch (SecurityException e1) {
            LOGGER.error("ControllerLogAopAspect around error",e1);
        }
        if (null != method) {
            // 判断是否包含自定义的注解，说明一下这里的SystemLog就是我自己自定义的注解
            if (method.isAnnotationPresent(SystemControllerLog.class)) {

                //此处需要对用户进行区分：1为admin user 2为customer user
                // get session
                HttpSession httpSession = httpRequest.getSession(true);
                // 从session获取登录用户
                AdminUserVO adminUserVO = (AdminUserVO) httpSession
                        .getAttribute(FucdnStrConstant.SESSION_KEY_ADMIN.getConstant());
                long adminUserId = adminUserVO.getAdminUserId();
                log.setUserId(String.valueOf(adminUserId));

                SystemControllerLog systemlog = method.getAnnotation(SystemControllerLog.class);

                log.setModule(systemlog.module());
                log.setMethod(systemlog.methods());
                //请求查询操作前数据的spring bean
                String serviceClass = systemlog.serviceClass();
                //请求查询数据的方法
                String queryMethod = systemlog.queryMethod();
                //判断是否需要进行操作前的对象参数查询
                if(StringUtils.isNotBlank(systemlog.parameterKey())
                        &&StringUtils.isNotBlank(systemlog.parameterType())
                        &&StringUtils.isNotBlank(systemlog.queryMethod())
                        &&StringUtils.isNotBlank(systemlog.serviceClass())){
                    boolean isArrayResult = systemlog.paramIsArray();
                    //参数类型
                    String paramType = systemlog.parameterType();
                    String key = systemlog.parameterKey();

                    if(isArrayResult){//批量操作
                        //JSONArray jsonarray = (JSONArray) object.get(key);
                        //从请求的参数中解析出查询key对应的value值
                        String value = "";
                        JSONArray beforeParamArray = new JSONArray();
                        for (int i = 0; i < operateParamArray.size(); i++) {
                            JSONObject params =  operateParamArray.getJSONObject(i);
                            JSONArray paramArray = (JSONArray) params.get(key);
                            if (paramArray != null) {
                                for (int j = 0; j < paramArray.size(); j++) {
                                    String paramId =  paramArray.getString(j);
                                    //在此处判断spring bean查询的方法参数类型
                                    Object data = getOperateBeforeData(paramType, serviceClass, queryMethod, paramId);
                                    JSONObject json = (JSONObject) JSON.toJSON(data);
                                    beforeParamArray.add(json);
                                }
                            }
                        }
                        log.setBeforeParams(beforeParamArray.toJSONString());

                    }else{//单量操作

                        //从请求的参数中解析出查询key对应的value值
                        String value = "";
                        for (int i = 0; i < operateParamArray.size(); i++) {
                            JSONObject params =  operateParamArray.getJSONObject(i);
                            value = params.getString(key);
                            if(StringUtils.isNotBlank(value)){
                                break;
                            }
                        }
                        //在此处获取操作前的spring bean的查询方法
                        Object data = getOperateBeforeData(paramType, serviceClass, queryMethod, value);
                        JSONObject beforeParam = (JSONObject) JSON.toJSON(data);
                        log.setBeforeParams(beforeParam.toJSONString());
                    }
                }

                try {
                    //执行页面请求模块方法，并返回
                    object = pjp.proceed();
                    //获取系统时间
                    String endTime = new SimpleDateFormat(FucdnStrConstant.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND.getConstant()).format(new Date());
                    log.setEndTime(endTime);
                    //将object 转化为controller封装返回的实体类：RequestResult
                    RequestResult requestResult = (RequestResult) object;
                    if(requestResult.isResult()){
                        //操作流程成功
                        if(StringUtils.isNotBlank(requestResult.getErrMsg())){
                            log.setResultMsg(requestResult.getErrMsg());
                        }else if(requestResult.getData() instanceof String){
                            log.setResultMsg((String) requestResult.getData());
                        }else{
                            log.setResultMsg("执行成功");
                        }
                    }else{
                        log.setResultMsg("失败");
                    }
                    //保存进数据库
                    logservice.saveLog(log);
                } catch (Throwable e) {
                    String endTime = new SimpleDateFormat(FucdnStrConstant.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND.getConstant()).format(new Date());
                    log.setEndTime(endTime);

                    log.setResultMsg(e.getMessage());
                    logservice.saveLog(log);
                }
            } else {
                //没有包含注解
                object = pjp.proceed();
            }
        } else {
            //不需要拦截直接执行
            object = pjp.proceed();
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
    public Object getOperateBeforeData(String paramType,String serviceClass,String queryMethod,String value){
        Object obj = new Object();
        //在此处解析请求的参数类型，根据id查询数据，id类型有四种：int，Integer,long,Long
        if(paramType.equals("int")){
            int id = Integer.parseInt(value);
            Method  mh = ReflectionUtils.findMethod(SpringContextUtil.getBean(serviceClass).getClass(), queryMethod,Long.class );
            //用spring bean获取操作前的参数,此处需要注意：传入的id类型与bean里面的参数类型需要保持一致
            obj = ReflectionUtils.invokeMethod(mh,  SpringContextUtil.getBean(serviceClass),id);

        }else if(paramType.equals("Integer")){
            Integer id = Integer.valueOf(value);
            Method  mh = ReflectionUtils.findMethod(SpringContextUtil.getBean(serviceClass).getClass(), queryMethod,Long.class );
            //用spring bean获取操作前的参数,此处需要注意：传入的id类型与bean里面的参数类型需要保持一致
            obj = ReflectionUtils.invokeMethod(mh,  SpringContextUtil.getBean(serviceClass),id);

        }else if(paramType.equals("long")){
            long id = Long.parseLong(value);
            Method  mh = ReflectionUtils.findMethod(SpringContextUtil.getBean(serviceClass).getClass(), queryMethod,Long.class );
            //用spring bean获取操作前的参数,此处需要注意：传入的id类型与bean里面的参数类型需要保持一致
            obj = ReflectionUtils.invokeMethod(mh,  SpringContextUtil.getBean(serviceClass),id);

        }else if(paramType.equals("Long")){
            Long id = Long.valueOf(value);
            Method  mh = ReflectionUtils.findMethod(SpringContextUtil.getBean(serviceClass).getClass(), queryMethod,Long.class );
            //用spring bean获取操作前的参数,此处需要注意：传入的id类型与bean里面的参数类型需要保持一致
            obj = ReflectionUtils.invokeMethod(mh,  SpringContextUtil.getBean(serviceClass),id);
        }
        return obj;
    }

}
