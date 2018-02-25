package com.lx.barragereptile.config.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class WebSocketUtils {
    @Autowired
    SimpMessagingTemplate messagingTemplate;

    public void rt(String msg) {
        messagingTemplate.convertAndSend("/topic/notice", msg);
    }
}
