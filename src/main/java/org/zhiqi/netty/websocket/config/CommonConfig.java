package org.zhiqi.netty.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.zhiqi.netty.websocket.constants.WebSocketConstant;
import org.zhiqi.netty.websocket.utils.EasyRedisUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class CommonConfig {

    @Resource
    private EasyRedisUtils easyRedisUtils;

    @Resource
    private String currentNodeQueueName;

    /**
     * 生成一个唯一的节点 id
     *
     * @return String
     */
    @Bean
    public String currentNodeQueueName() {
        return "websocket_node_" + UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }


    /**
     * spring 启动的时候注入节点信息到 redis
     */
    @PostConstruct
    public void init() {
        easyRedisUtils.oSet(WebSocketConstant.REDIS_KEY_WEB_SOCKET_NODE + EasyRedisUtils.DELIMITER + currentNodeQueueName, currentNodeQueueName, 15L, TimeUnit.SECONDS);
    }
}
