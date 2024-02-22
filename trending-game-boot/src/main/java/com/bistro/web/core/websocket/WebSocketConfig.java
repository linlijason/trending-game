//package com.bistro.web.core.websocket;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//import org.springframework.web.socket.server.standard.ServerEndpointExporter;
//
//
//@Configuration // 配置类
//@EnableWebSocket // 声明支持websocket
//public class WebSocketConfig implements WebSocketConfigurer {
//
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        // 注册websocket实现类，指定参数访问地址;allowed-origins="*" 允许跨域
//        // addHandler是增加处理接口和设定URL
//        // addInterceptors是增加拦截器处理（可以不用）
//        registry.addHandler(myHandler(), "/ws").addInterceptors(myHandshake()).setAllowedOrigins("*");
//        registry.addHandler(myHandler(), "/sockjs/ws").addInterceptors(myHandshake()).withSockJS();
//
//    }
//
//    @Bean
//    public MyHandler myHandler() {
//        return new MyHandler();
//    }
//
//    @Bean
//    public MyHandshakeInterceptor myHandshake() {
//        return new MyHandshakeInterceptor();
//    }
//
//}
