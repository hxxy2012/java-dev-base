package com.enterprisex.system.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

/**
 * Sentinel配置类
 */
@Slf4j
@Configuration
public class SentinelConfig implements BlockExceptionHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
        log.warn("Sentinel blocked request: {}, exception: {}", request.getRequestURI(), e.getClass().getSimpleName());

        Map<String, Object> result = new HashMap<>();
        result.put("code", 429);

        if (e instanceof FlowException) {
            result.put("msg", "请求过于频繁，请稍后再试");
        } else if (e instanceof DegradeException) {
            result.put("msg", "服务降级，请稍后再试");
        } else if (e instanceof ParamFlowException) {
            result.put("msg", "热点参数限流");
        } else if (e instanceof AuthorityException) {
            result.put("msg", "授权规则不通过");
        } else {
            result.put("msg", "系统限流");
        }

        response.setStatus(429);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(result));
    }
}
