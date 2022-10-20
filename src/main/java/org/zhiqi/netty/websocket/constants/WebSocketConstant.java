package org.zhiqi.netty.websocket.constants;

/**
 * @author zhiqi
 * @version 1.0
 */
public class WebSocketConstant {

    /**
     * 存储当前 websocket 所有节点
     */
    public static final String REDIS_KEY_WEB_SOCKET_NODE = "websocket:node";

    /**
     * 春初当前 websocket 用户与节点的关系
     */
    public static final String REDIS_KEY_WEB_SOCKET_USER = "websocket:user";


    /**
     * 默认的 webSocketExchange
     */
    public final static String DEFAULT_EXCHANGE = "web_socket_exchange";
}
