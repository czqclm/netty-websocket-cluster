package org.zhiqi.netty.websocket.utils;


import org.zhiqi.netty.websocket.server.WebSocketServer;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 调用 websocket 服务
 *
 * @author zhiqi
 * @version 1.0
 */
public class EasyWebSocketUtils {

    /**
     * 内存存储 websocket 对象
     */
    public static final ConcurrentHashMap<String, WebSocketServer> WEB_SOCKET_MAP = new ConcurrentHashMap<>();


    /**
     * 添加 websocket 缓存
     *
     * @param user   用户
     * @param server 服务
     */
    public static void pushServer(String user, WebSocketServer server) {
        WEB_SOCKET_MAP.put(user, server);
    }

    public static void broadcast(String msg) {
        WEB_SOCKET_MAP.forEach((k, v) -> {
            v.getSession().sendText(msg);
        });
    }
}
