package com.lx.barragereptile.config.websocket;

import lombok.extern.log4j.Log4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;


@Configuration
@EnableWebSocketMessageBroker
@Log4j
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    /**
     * 客户端与服务器端建立连接的点
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        stompEndpointRegistry.addEndpoint("/websocket").withSockJS();
    }

    /**
     * 配置客户端发送信息的路径的前缀
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //客户端接收路径
        registry.enableSimpleBroker("/topic");
        //客户端发送路径
        registry.setApplicationDestinationPrefixes("/app");
    }


//    @Override
//    public void configureWebSocketTransport(final WebSocketTransportRegistration registration) {
//        registration.addDecoratorFactory(new WebSocketHandlerDecoratorFactory() {
//            @Override
//            public WebSocketHandler decorate(final WebSocketHandler barragehandler) {
//                return new WebSocketHandlerDecorator(barragehandler) {
//                    @Override
//                    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
//                        // 客户端与服务器端建立连接后，此处记录谁上线了
//                        super.afterConnectionEstablished(session);
//                    }
//
//                    @Override
//                    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
//                        // 客户端与服务器端断开连接后，此处记录谁下线了
//                        super.afterConnectionClosed(session, closeStatus);
//                    }
//                };
//            }
//        });
//        super.configureWebSocketTransport(registration);
//    }

}
