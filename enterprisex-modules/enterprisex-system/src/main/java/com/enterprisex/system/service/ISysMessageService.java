package com.enterprisex.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enterprisex.system.domain.SysMessage;

import java.util.List;

/**
 * 系统消息Service接口
 */
public interface ISysMessageService extends IService<SysMessage> {

    /**
     * 查询用户消息列表
     */
    List<SysMessage> selectUserMessageList(Long userId, SysMessage message);

    /**
     * 获取用户未读消息数量
     */
    Long countUnreadMessages(Long userId);

    /**
     * 标记消息为已读
     */
    boolean markAsRead(Long messageId, Long userId);

    /**
     * 批量标记消息为已读
     */
    boolean batchMarkAsRead(List<Long> messageIds, Long userId);

    /**
     * 全部标记为已读
     */
    boolean markAllAsRead(Long userId);

    /**
     * 发送系统消息
     */
    boolean sendMessage(SysMessage message);

    /**
     * 发送广播消息（全体用户）
     */
    boolean broadcastMessage(SysMessage message);
}
