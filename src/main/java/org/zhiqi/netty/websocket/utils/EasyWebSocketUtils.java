package org.zhiqi.netty.websocket.utils;


import org.springframework.stereotype.Component;
import org.zhiqi.netty.websocket.constants.WebSocketConstant;
import org.zhiqi.netty.websocket.mq.provider.WebSocketProvider;
import org.zhiqi.netty.websocket.pojo.dto.RabbitFanoutMsgDTO;
import org.zhiqi.netty.websocket.server.WebSocketServer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 调用 websocket 工具
 *
 * @author zhiqi
 * @version 1.0
 */
@Component
public class EasyWebSocketUtils {

    @Resource
    private EasyRedisUtils easyRedisUtils;

    @Resource
    private String currentNodeQueueName;

    @Resource
    private WebSocketProvider webSocketProvider;


    private static EasyWebSocketUtils easyWebSocketUtils;

    @PostConstruct
    public void init() {
        easyWebSocketUtils = this;
        easyWebSocketUtils.easyRedisUtils = this.easyRedisUtils;
        easyWebSocketUtils.currentNodeQueueName = this.currentNodeQueueName;
        easyWebSocketUtils.webSocketProvider = this.webSocketProvider;
    }

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
    public static void pushUser(String user, WebSocketServer server) {
        WEB_SOCKET_MAP.put(user, server);
        easyWebSocketUtils.easyRedisUtils.oSet(WebSocketConstant.REDIS_KEY_WEB_SOCKET_USER + EasyRedisUtils.DELIMITER + user, easyWebSocketUtils.currentNodeQueueName, 20L, TimeUnit.SECONDS);
    }

    public static void popUser(String user) {
        WEB_SOCKET_MAP.remove(user);
        easyWebSocketUtils.easyRedisUtils.delete(WebSocketConstant.REDIS_KEY_WEB_SOCKET_USER + EasyRedisUtils.DELIMITER + user);
    }

    public static void broadcast(String msg) {
        easyWebSocketUtils.webSocketProvider.fanoutMsgToAllNode(RabbitFanoutMsgDTO.builder().allUser(true).content(msg).build());
    }

    public static void sendToUser(List<String> userList, String msg) {
        for (String userId : userList) {
            Optional.ofNullable(WEB_SOCKET_MAP.get(userId)).ifPresent(session -> session.getSession().sendText(msg));
        }
    }

    public static void broadcastCurrentNodeAllUser(String msg) {
        WEB_SOCKET_MAP.forEach((k, v) -> {
            v.getSession().sendText(msg);
        });
    }
}
