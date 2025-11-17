package com.enterprisex.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 请求日志过滤器
 * 记录所有经过网关的请求信息
 *
 * @author EnterpriseX
 */
@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        long startTime = System.currentTimeMillis();
        String requestPath = request.getPath().value();
        String requestMethod = request.getMethod().toString();
        String requestIp = getClientIp(request);

        log.info("===== 网关请求开始 =====");
        log.info("请求路径: {} {}", requestMethod, requestPath);
        log.info("请求IP: {}", requestIp);
        log.info("请求参数: {}", request.getQueryParams());

        return chain.filter(exchange).then(
                Mono.fromRunnable(() -> {
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    int statusCode = exchange.getResponse().getStatusCode() != null
                            ? exchange.getResponse().getStatusCode().value()
                            : 0;

                    log.info("响应状态: {}", statusCode);
                    log.info("请求耗时: {}ms", duration);
                    log.info("===== 网关请求结束 =====\n");
                })
        );
    }

    @Override
    public int getOrder() {
        // 设置较高优先级，确保第一个执行
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(ServerHttpRequest request) {
        String ip = request.getHeaders().getFirst("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddress() != null
                    ? request.getRemoteAddress().getAddress().getHostAddress()
                    : "";
        }
        return ip;
    }
}
