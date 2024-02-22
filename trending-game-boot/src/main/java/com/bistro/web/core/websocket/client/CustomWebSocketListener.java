package com.bistro.web.core.websocket.client;


import com.alibaba.fastjson.JSONObject;
import com.bistro.common.utils.spring.SpringUtils;
import com.bistro.framework.manager.AsyncManager;
import com.bistro.module.binance.domain.TickerBaseInfo;
import com.bistro.module.binance.service.IBinanceTickerService;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomWebSocketListener extends WebSocketListener {


    static private final Logger logger = LoggerFactory.getLogger(CustomWebSocketListener.class);

    private WebSocketUtils webSocketConnect;
    private IBinanceTickerService binanceTickerService;

    CustomWebSocketListener(WebSocketUtils webSocketConnect) {
        this.webSocketConnect = webSocketConnect;
        binanceTickerService = SpringUtils.getBean(IBinanceTickerService.class);
    }

    /**
     * 连接建立，设置状态，重置连接次数
     */
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        logger.info("连接建立");
        this.webSocketConnect.resetRetryNum();
        this.webSocketConnect.setWebSocket(webSocket);
        this.webSocketConnect.changeState(true);
    }

    /**
     * 收到消息
     */
    @Override
    public void onMessage(WebSocket webSocket, String text) {

//        logger.info("Receive From Websocket: {}", text);

        AsyncManager.me().execute(() -> {
            try {
                JSONObject data = JSONObject.parseObject(text).getJSONObject("data");
                long time = data.getLong("E");
                TickerBaseInfo ticker = new TickerBaseInfo();
                ticker.setPrice(data.getString("c"));
                ticker.setTime(time / 1000 * 1000);//保留到毫秒
                binanceTickerService.addTicker(data.getString("s"), ticker, ticker.getTime());

            } catch (Exception e) {
                logger.error("onMessage", e);
            }
        });

    }

    /**
     * 收到 字节码消息
     */
    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {

    }

    /**
     * 连接中断
     */
    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {

        logger.error("WebSocket 连接发生中断-code={} {}", code, reason);
        this.webSocketConnect.setWebSocket(null);
        this.webSocketConnect.changeState(false);

        SpringUtils.publishEvent(new WebSocketRetryEvent(this));

    }

    /**
     * 连接中断
     */
    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {

        //从源码看，不一定会走这个分支
        logger.error("WebSocket 连接中断-{}", reason);
        this.webSocketConnect.setWebSocket(null);
        this.webSocketConnect.changeState(false);
    }

    /**
     * 连接出错，在这里启动重试
     */
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        logger.error("WebSocket 连接发生错误", t);
        this.webSocketConnect.changeState(false);
        this.webSocketConnect.setWebSocket(null);
        SpringUtils.publishEvent(new WebSocketRetryEvent(this));
    }
}
