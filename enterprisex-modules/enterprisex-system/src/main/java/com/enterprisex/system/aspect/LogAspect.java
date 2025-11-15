package com.enterprisex.system.aspect;

import com.enterprisex.common.core.annotation.Log;
import com.enterprisex.system.domain.SysOperLog;
import com.enterprisex.system.service.ISysOperLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 操作日志记录切面
 *
 * @author EnterpriseX
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    @Autowired
    private ISysOperLogService operLogService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 配置织入点 - 所有带@Log注解的方法
     */
    @Pointcut("@annotation(com.enterprisex.common.core.annotation.Log)")
    public void logPointCut() {
    }

    /**
     * 处理完请求后执行 - 正常返回
     *
     * @param joinPoint 切点
     * @param controllerLog 日志注解
     * @param jsonResult 返回结果
     */
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object jsonResult) {
        handleLog(joinPoint, controllerLog, null, jsonResult);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param controllerLog 日志注解
     * @param e 异常
     */
    @AfterThrowing(pointcut = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Log controllerLog, Exception e) {
        handleLog(joinPoint, controllerLog, e, null);
    }

    /**
     * 处理日志
     *
     * @param joinPoint 切点
     * @param controllerLog 日志注解
     * @param e 异常
     * @param jsonResult 返回结果
     */
    protected void handleLog(final JoinPoint joinPoint, Log controllerLog, final Exception e, Object jsonResult) {
        try {
            // 获取当前的request
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }
            HttpServletRequest request = attributes.getRequest();

            // 创建操作日志对象
            SysOperLog operLog = new SysOperLog();
            operLog.setStatus(0); // 默认正常

            // 设置操作时间
            operLog.setOperTime(LocalDateTime.now());

            // 设置请求的IP地址
            String ipAddr = getRemoteAddr(request);
            operLog.setOperIp(ipAddr);

            // 设置请求的URL
            operLog.setOperUrl(request.getRequestURI());

            // 从JWT token或session中获取当前用户名（简化处理，实际应该从SecurityContext或JWT中获取）
            String username = request.getHeader("username");
            if (username == null || username.isEmpty()) {
                username = "system";
            }
            operLog.setOperName(username);

            if (e != null) {
                operLog.setStatus(1);
                operLog.setErrorMsg(e.getMessage() != null && e.getMessage().length() > 2000
                    ? e.getMessage().substring(0, 2000) : e.getMessage());
            }

            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");

            // 设置请求方式
            operLog.setRequestMethod(request.getMethod());

            // 处理注解上的参数
            getControllerMethodDescription(joinPoint, controllerLog, operLog, jsonResult);

            // 保存数据库
            operLogService.save(operLog);
        } catch (Exception exp) {
            // 记录本地异常日志
            log.error("操作日志记录异常:", exp);
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param log 日志注解
     * @param operLog 操作日志
     * @param jsonResult 返回结果
     * @throws Exception
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, Log log, SysOperLog operLog, Object jsonResult) throws Exception {
        // 设置业务类型
        operLog.setBusinessType(log.businessType().ordinal());

        // 设置模块标题
        operLog.setTitle(log.title());

        // 设置操作人类别
        operLog.setOperatorType(log.operatorType().ordinal());

        // 是否需要保存request参数
        if (log.isSaveRequestData()) {
            setRequestValue(joinPoint, operLog);
        }

        // 是否需要保存response参数
        if (log.isSaveResponseData() && jsonResult != null) {
            String jsonResultStr = toJsonString(jsonResult);
            operLog.setJsonResult(jsonResultStr != null && jsonResultStr.length() > 2000
                ? jsonResultStr.substring(0, 2000) : jsonResultStr);
        }
    }

    /**
     * 获取请求的参数，放到log中
     *
     * @param joinPoint 切点
     * @param operLog 操作日志
     * @throws Exception 异常
     */
    private void setRequestValue(JoinPoint joinPoint, SysOperLog operLog) throws Exception {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();

        // 获取请求参数
        Map<String, String[]> params = request.getParameterMap();
        if (params != null && !params.isEmpty()) {
            String paramsStr = toJsonString(params);
            operLog.setOperParam(paramsStr != null && paramsStr.length() > 2000
                ? paramsStr.substring(0, 2000) : paramsStr);
        } else {
            // 如果没有request参数，尝试获取方法参数
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                String argsStr = toJsonString(args);
                operLog.setOperParam(argsStr != null && argsStr.length() > 2000
                    ? argsStr.substring(0, 2000) : argsStr);
            }
        }
    }

    /**
     * 对象转JSON字符串
     */
    private String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("对象转JSON字符串失败:", e);
            return object.toString();
        }
    }

    /**
     * 获取真实IP地址
     */
    private String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.substring(0, ip.indexOf(","));
        }
        return ip;
    }
}
