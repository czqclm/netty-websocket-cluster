package org.zhiqi.netty.websocket.mq.provider;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.zhiqi.netty.websocket.constants.WebSocketConstant;
import org.zhiqi.netty.websocket.pojo.dto.RabbitFanoutMsgDTO;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 生产者
 *
 * @author zhiqi
 * @version 1.0
 */
@Component
@Slf4j
public class WebSocketProvider {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 推送消息到 mq
     *
     * @param msg 消息内容
     */
    public void fanoutMsgToAllNode(RabbitFanoutMsgDTO msg) {
        Optional.ofNullable(msg).ifPresent(data -> rabbitTemplate.send(WebSocketConstant.DEFAULT_EXCHANGE, "", new Message(JSON.toJSONString(data).getBytes(), new MessageProperties())));
    }

}
