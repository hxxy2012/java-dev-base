package com.enterprisex.auth.service;

import com.enterprisex.auth.domain.LoginLog;
import com.enterprisex.auth.util.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

/**
 * 登录日志服务
 *
 * @author EnterpriseX
 */
@Slf4j
@Service
public class LoginLogService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${service.system.url:http://localhost:9201}")
    private String systemServiceUrl;

    /**
     * 记录登录日志（异步）
     *
     * @param username 用户名
     * @param status   登录状态 0失败 1成功
     * @param message  提示消息
     * @param request  HTTP请求
     */
    @Async
    public void recordLoginLog(String username, Integer status, String message, HttpServletRequest request) {
        try {
            // 构建登录日志对象
            LoginLog loginLog = LoginLog.builder()
                    .username(username)
                    .status(status)
                    .msg(message)
                    .loginTime(new Date())
                    .ipaddr(RequestUtil.getRemoteAddr(request))
                    .browser(RequestUtil.getBrowser(request))
                    .os(RequestUtil.getOs(request))
                    .loginLocation("内网IP") // 简化处理，实际项目可以集成IP地址库
                    .build();

            // 调用系统服务API
            String url = systemServiceUrl + "/system/loginlog";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<LoginLog> entity = new HttpEntity<>(loginLog, headers);

            restTemplate.postForObject(url, entity, Object.class);

            log.info("登录日志记录成功: username={}, status={}, ip={}", username, status, loginLog.getIpaddr());
        } catch (Exception e) {
            log.error("记录登录日志失败: username={}, status={}", username, status, e);
        }
    }
}
