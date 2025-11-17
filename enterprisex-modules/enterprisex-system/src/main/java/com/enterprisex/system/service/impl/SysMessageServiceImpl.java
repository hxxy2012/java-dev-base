package com.enterprisex.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enterprisex.system.domain.SysMessage;
import com.enterprisex.system.mapper.SysMessageMapper;
import com.enterprisex.system.service.ISysMessageService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 系统消息Service实现
 */
@Service
public class SysMessageServiceImpl extends ServiceImpl<SysMessageMapper, SysMessage> implements ISysMessageService {

    @Override
    public List<SysMessage> selectUserMessageList(Long userId, SysMessage message) {
        LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<>();

        // 查询发给该用户的消息或广播消息
        wrapper.and(w -> w.eq(SysMessage::getUserId, userId).or().isNull(SysMessage::getUserId));

        // 消息类型
        if (message.getMessageType() != null) {
            wrapper.eq(SysMessage::getMessageType, message.getMessageType());
        }

        // 是否已读
        if (message.getIsRead() != null) {
            wrapper.eq(SysMessage::getIsRead, message.getIsRead());
        }

        // 消息级别
        if (message.getLevel() != null) {
            wrapper.eq(SysMessage::getLevel, message.getLevel());
        }

        // 按创建时间倒序
        wrapper.orderByDesc(SysMessage::getCreateTime);

        return list(wrapper);
    }

    @Override
    public Long countUnreadMessages(Long userId) {
        LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(SysMessage::getUserId, userId).or().isNull(SysMessage::getUserId));
        wrapper.eq(SysMessage::getIsRead, 0);
        return count(wrapper);
    }

    @Override
    public boolean markAsRead(Long messageId, Long userId) {
        LambdaUpdateWrapper<SysMessage> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysMessage::getMessageId, messageId);
        wrapper.and(w -> w.eq(SysMessage::getUserId, userId).or().isNull(SysMessage::getUserId));
        wrapper.set(SysMessage::getIsRead, 1);
        wrapper.set(SysMessage::getReadTime, new Date());
        return update(wrapper);
    }

    @Override
    public boolean batchMarkAsRead(List<Long> messageIds, Long userId) {
        LambdaUpdateWrapper<SysMessage> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysMessage::getMessageId, messageIds);
        wrapper.and(w -> w.eq(SysMessage::getUserId, userId).or().isNull(SysMessage::getUserId));
        wrapper.set(SysMessage::getIsRead, 1);
        wrapper.set(SysMessage::getReadTime, new Date());
        return update(wrapper);
    }

    @Override
    public boolean markAllAsRead(Long userId) {
        LambdaUpdateWrapper<SysMessage> wrapper = new LambdaUpdateWrapper<>();
        wrapper.and(w -> w.eq(SysMessage::getUserId, userId).or().isNull(SysMessage::getUserId));
        wrapper.eq(SysMessage::getIsRead, 0);
        wrapper.set(SysMessage::getIsRead, 1);
        wrapper.set(SysMessage::getReadTime, new Date());
        return update(wrapper);
    }

    @Override
    public boolean sendMessage(SysMessage message) {
        message.setIsRead(0);
        message.setCreateTime(new Date());
        return save(message);
    }

    @Override
    public boolean broadcastMessage(SysMessage message) {
        message.setUserId(null); // null表示全体用户
        message.setIsRead(0);
        message.setCreateTime(new Date());
        return save(message);
    }
}
