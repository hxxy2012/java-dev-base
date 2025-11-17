package com.enterprisex.system.controller;

import com.enterprisex.system.websocket.WebSocketMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * WebSocket消息推送Controller
 */
@Slf4j
@Tag(name = "WebSocket消息推送")
@RestController
@RequestMapping("/system/websocket")
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 广播消息
     */
    @Operation(summary = "广播消息")
    @PostMapping("/broadcast")
    public void broadcast(@RequestBody WebSocketMessage message) {
        message.setSendTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        messagingTemplate.convertAndSend("/topic/public", message);
        log.info("Broadcast message: {}", message);
    }

    /**
     * 发送给指定用户
     */
    @Operation(summary = "发送给指定用户")
    @PostMapping("/sendToUser")
    public void sendToUser(@RequestBody WebSocketMessage message) {
        message.setSendTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        messagingTemplate.convertAndSendToUser(message.getReceiver(), "/queue/message", message);
        log.info("Send message to user {}: {}", message.getReceiver(), message);
    }

    /**
     * 处理来自客户端的消息
     */
    @MessageMapping("/chat")
    @SendTo("/topic/public")
    public WebSocketMessage handleMessage(WebSocketMessage message) {
        message.setSendTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        log.info("Received message: {}", message);
        return message;
    }

    /**
     * 发送系统通知
     */
    @Operation(summary = "发送系统通知")
    @PostMapping("/notify")
    public void sendNotification(@RequestBody WebSocketMessage message) {
        message.setType("system");
        message.setSendTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        messagingTemplate.convertAndSend("/topic/notification", message);
        log.info("Send notification: {}", message);
    }
}
