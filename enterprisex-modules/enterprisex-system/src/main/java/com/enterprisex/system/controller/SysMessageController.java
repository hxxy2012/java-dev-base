package com.enterprisex.system.controller;

import com.enterprisex.common.core.domain.R;
import com.enterprisex.common.core.domain.TableDataInfo;
import com.enterprisex.common.core.utils.SecurityUtils;
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
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return TableDataInfo.ok(List.of(), 0);
        }
        List<SysMessage> list = messageService.selectUserMessageList(userId, message);
        return TableDataInfo.ok(list, list.size());
    }

    /**
     * 获取未读消息数量
     */
    @Operation(summary = "获取未读消息数量")
    @GetMapping("/unreadCount")
    public R<Long> getUnreadCount() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return R.ok(0L);
        }
        Long count = messageService.countUnreadMessages(userId);
        return R.ok(count);
    }

    /**
     * 标记消息为已读
     */
    @Operation(summary = "标记消息为已读")
    @PutMapping("/read/{messageId}")
    public R<Void> markAsRead(@PathVariable Long messageId) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return R.fail("用户未登录");
        }
        return R.toAjax(messageService.markAsRead(messageId, userId));
    }

    /**
     * 批量标记消息为已读
     */
    @Operation(summary = "批量标记消息为已读")
    @PutMapping("/read/batch")
    public R<Void> batchMarkAsRead(@RequestBody List<Long> messageIds) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return R.fail("用户未登录");
        }
        return R.toAjax(messageService.batchMarkAsRead(messageIds, userId));
    }

    /**
     * 全部标记为已读
     */
    @Operation(summary = "全部标记为已读")
    @PutMapping("/read/all")
    public R<Void> markAllAsRead() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return R.fail("用户未登录");
        }
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
        Long senderId = SecurityUtils.getUserId();
        String senderName = SecurityUtils.getUsername();
        if (senderId == null) {
            return R.fail("用户未登录");
        }
        message.setSenderId(senderId);
        message.setSenderName(senderName != null ? senderName : "未知用户");
        return R.toAjax(messageService.sendMessage(message));
    }

    /**
     * 发送广播消息（管理员功能）
     */
    @Operation(summary = "发送广播消息")
    @PostMapping("/broadcast")
    public R<Void> broadcastMessage(@RequestBody SysMessage message) {
        // 设置发送人信息
        Long senderId = SecurityUtils.getUserId();
        String senderName = SecurityUtils.getUsername();
        if (senderId == null) {
            return R.fail("用户未登录");
        }
        message.setSenderId(senderId);
        message.setSenderName(senderName != null ? senderName : "未知用户");
        return R.toAjax(messageService.broadcastMessage(message));
    }
}
