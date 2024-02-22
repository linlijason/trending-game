package com.bistro.web.core.websocket.client;

import com.bistro.web.core.config.CoinConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class WebSocketUtils {

    /**
     * 记录重试次数，当连接成功时，重置为0
     */
    private final AtomicInteger retryNum = new AtomicInteger(0);

    private static final Logger logger = LoggerFactory.getLogger(WebSocketUtils.class);


    /**
     * 系统持有的websocket连接，为空时代表连接为建立，不为空时表示连接已建立
     */
    private WebSocket webSocket = null;

    /**
     * websocket 连接地址
     */
    @Value("${urls.websocket.binance}")
    private String webSocketUrl;

    /**
     * 连接状态
     * true : 连接成功
     * false: 连接失败
     */
    private volatile Boolean webState = false;
    private volatile Boolean destroy = false;

    static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .writeTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();

    @Autowired
    private CoinConfig coinConfig;


    /**
     * 开启websocket连接
     */
    public void startConnection() {

        if (!destroy && !getState() && !StringUtils.isEmpty(getWebSocketUrl())) {

            okHttpClient.newWebSocket(new Request.Builder().url(getWebSocketUrl()).build(),
                    new CustomWebSocketListener(this));

        }


    }

    /**
     * 改变websocket状态
     *
     * @param state
     */
    public void changeState(boolean state) {
        synchronized (this) {
            webState = state;
        }
    }

    /**
     * 获取websocket状态
     *
     * @return
     */
    public boolean getState() {
        synchronized (this) {
            return webState;
        }
    }

    public WebSocket getWebSocket() {
        return webSocket;
    }

    public void setWebSocket(WebSocket webSocket) {

        closeWs();//先停止一遍
        this.webSocket = webSocket;
    }

    public String getWebSocketUrl() {
        return webSocketUrl + Strings.join(coinConfig.getStream().values(), '/');
    }

    public void setWebSocketUrl(String webSocketUrl) {
        this.webSocketUrl = webSocketUrl;
    }


    /**
     * 监听重试事件，并启动重试
     *
     * @param closeEvent
     */
    @EventListener({WebSocketRetryEvent.class})
    public void eventClose(WebSocketRetryEvent closeEvent) {

        if (this.retryNum.incrementAndGet() >= 20) {
            logger.info("websocket 重试次数超过 20 次，停止重试");
            return;
        }
        logger.info("开始重试 websocket 连接重试次数-{}", this.retryNum.get());

        this.startConnection();

    }

    /**
     * 重置失败次数
     */
    public void resetRetryNum() {
        this.retryNum.set(0);
    }

    @PreDestroy
    public void destroy() {
        destroy = true;
        closeWs();//断开连接
    }

    private void closeWs() {
        try {
            if (this.webSocket != null) {
                this.webSocket.close(999, "close this");
            }
        } catch (Exception e) {

        }
    }
}
