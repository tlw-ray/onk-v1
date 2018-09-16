package com.xskr.onk_v1.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketMessageBrokerConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        //TODO 如果改为AJAX调用下面这行可以去掉
//        config.setApplicationDestinationPrefixes("/onk");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //TODO 搞清endpoint的含义，一个与多个的区别
        registry.addEndpoint("/onk/endpoint").setAllowedOrigins("*").withSockJS();
    }

}
