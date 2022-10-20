package org.zhiqi.netty.websocket.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.zhiqi.netty.websocket.constants.WebSocketConstant;
import org.zhiqi.netty.websocket.utils.EasyRedisUtils;
import org.zhiqi.netty.websocket.utils.EasyWebSocketUtils;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ScheduledTask {

    @Resource
    private EasyRedisUtils easyRedisUtils;

    @Resource
    private String currentNodeQueueName;

    @Scheduled(fixedRate = 10000, initialDelay = 5000)
    public void keepAlive() {
        String key = WebSocketConstant.REDIS_KEY_WEB_SOCKET_NODE + EasyRedisUtils.DELIMITER + currentNodeQueueName;
        log.trace("[{}] node keepAlive...", key);
        easyRedisUtils.oSet(key, currentNodeQueueName, 15L, TimeUnit.SECONDS);

        EasyWebSocketUtils.WEB_SOCKET_MAP.forEach((k, v)-> {
            easyRedisUtils.oSet(WebSocketConstant.REDIS_KEY_WEB_SOCKET_USER + EasyRedisUtils.DELIMITER + k, currentNodeQueueName, 15L, TimeUnit.SECONDS);
        });
    }
}
