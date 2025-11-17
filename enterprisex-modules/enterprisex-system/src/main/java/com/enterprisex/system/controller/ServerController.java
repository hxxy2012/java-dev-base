package com.enterprisex.system.controller;

import com.enterprisex.common.core.domain.R;
import com.enterprisex.system.domain.Server;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务器监控
 *
 * @author EnterpriseX
 */
@Slf4j
@Tag(name = "服务器监控")
@RestController
@RequestMapping("/monitor/server")
public class ServerController {

    /**
     * 获取服务器信息
     */
    @Operation(summary = "获取服务器信息")
    @GetMapping("/info")
    public R<Server> getInfo() {
        try {
            Server server = new Server();
            server.copyTo();
            return R.ok(server);
        } catch (Exception e) {
            log.error("获取服务器信息失败", e);
            return R.fail("获取服务器信息失败：" + e.getMessage());
        }
    }
}
