package com.enterprisex.system.controller;

import com.enterprisex.common.core.annotation.BusinessType;
import com.enterprisex.common.core.annotation.Log;
import com.enterprisex.common.core.domain.R;
import com.enterprisex.common.core.domain.TableDataInfo;
import com.enterprisex.system.domain.SysUserOnline;
import com.enterprisex.system.service.ISysUserOnlineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 在线用户监控控制器
 *
 * @author EnterpriseX
 */
@Tag(name = "在线用户监控")
@RestController
@RequestMapping("/monitor/online")
public class SysUserOnlineController {

    @Autowired
    private ISysUserOnlineService userOnlineService;

    /**
     * 获取在线用户列表
     */
    @Operation(summary = "查询在线用户列表")
    @GetMapping("/list")
    public TableDataInfo<SysUserOnline> list(String username, String ipaddr) {
        List<SysUserOnline> list = userOnlineService.selectOnlineList();

        // 按条件过滤
        if (username != null && !username.isEmpty()) {
            list = list.stream()
                    .filter(user -> user.getUsername().contains(username))
                    .collect(Collectors.toList());
        }

        if (ipaddr != null && !ipaddr.isEmpty()) {
            list = list.stream()
                    .filter(user -> user.getIpaddr() != null && user.getIpaddr().contains(ipaddr))
                    .collect(Collectors.toList());
        }

        return TableDataInfo.ok(list, list.size());
    }

    /**
     * 强退用户
     */
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @Operation(summary = "强退用户")
    @DeleteMapping("/{tokenId}")
    public R<Void> forceLogout(@PathVariable String tokenId) {
        boolean success = userOnlineService.forceLogout(tokenId);
        return success ? R.ok("强退成功") : R.fail("强退失败");
    }

    /**
     * 批量强退用户
     */
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @Operation(summary = "批量强退用户")
    @DeleteMapping("/batch/{tokenIds}")
    public R<Void> batchForceLogout(@PathVariable String[] tokenIds) {
        int count = userOnlineService.batchForceLogout(tokenIds);
        return R.ok(String.format("成功强退%d个用户", count));
    }
}
