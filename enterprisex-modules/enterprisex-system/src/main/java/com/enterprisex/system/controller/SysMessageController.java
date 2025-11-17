package com.enterprisex.system.controller;

import com.enterprisex.common.core.domain.R;
import com.enterprisex.common.core.domain.TableDataInfo;
import com.enterprisex.system.domain.SysMessage;
import com.enterprisex.system.service.ISysMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统消息控制器
 */
@Tag(name = "系统消息管理", description = "系统消息通知接口")
@Slf4j
@RestController
@RequestMapping("/system/message")
public class SysMessageController {

    @Autowired
    private ISysMessageService messageService;

    /**
     * 获取当前用户消息列表
     */
    @Operation(summary = "获取用户消息列表")
    @GetMapping("/list")
    public TableDataInfo<SysMessage> list(SysMessage message) {
        // TODO: 从安全上下文获取当前用户ID，这里暂时使用固定值
        Long userId = 1L;
        List<SysMessage> list = messageService.selectUserMessageList(userId, message);
        return TableDataInfo.ok(list, list.size());
    }

    /**
     * 获取未读消息数量
     */
    @Operation(summary = "获取未读消息数量")
    @GetMapping("/unreadCount")
    public R<Long> getUnreadCount() {
        // TODO: 从安全上下文获取当前用户ID
        Long userId = 1L;
        Long count = messageService.countUnreadMessages(userId);
        return R.ok(count);
    }

    /**
     * 标记消息为已读
     */
    @Operation(summary = "标记消息为已读")
    @PutMapping("/read/{messageId}")
    public R<Void> markAsRead(@PathVariable Long messageId) {
        // TODO: 从安全上下文获取当前用户ID
        Long userId = 1L;
        return R.toAjax(messageService.markAsRead(messageId, userId));
    }

    /**
     * 批量标记消息为已读
     */
    @Operation(summary = "批量标记消息为已读")
    @PutMapping("/read/batch")
    public R<Void> batchMarkAsRead(@RequestBody List<Long> messageIds) {
        // TODO: 从安全上下文获取当前用户ID
        Long userId = 1L;
        return R.toAjax(messageService.batchMarkAsRead(messageIds, userId));
    }

    /**
     * 全部标记为已读
     */
    @Operation(summary = "全部标记为已读")
    @PutMapping("/read/all")
    public R<Void> markAllAsRead() {
        // TODO: 从安全上下文获取当前用户ID
        Long userId = 1L;
        return R.toAjax(messageService.markAllAsRead(userId));
    }

    /**
     * 删除消息
     */
    @Operation(summary = "删除消息")
    @DeleteMapping("/{messageIds}")
    public R<Void> remove(@PathVariable Long[] messageIds) {
        for (Long messageId : messageIds) {
            messageService.removeById(messageId);
        }
        return R.ok();
    }

    /**
     * 发送消息（管理员功能）
     */
    @Operation(summary = "发送消息")
    @PostMapping("/send")
    public R<Void> sendMessage(@RequestBody SysMessage message) {
        // 设置发送人信息
        // TODO: 从安全上下文获取当前用户信息
        message.setSenderId(1L);
        message.setSenderName("系统管理员");
        return R.toAjax(messageService.sendMessage(message));
    }

    /**
     * 发送广播消息（管理员功能）
     */
    @Operation(summary = "发送广播消息")
    @PostMapping("/broadcast")
    public R<Void> broadcastMessage(@RequestBody SysMessage message) {
        // 设置发送人信息
        // TODO: 从安全上下文获取当前用户信息
        message.setSenderId(1L);
        message.setSenderName("系统管理员");
        return R.toAjax(messageService.broadcastMessage(message));
    }
}
