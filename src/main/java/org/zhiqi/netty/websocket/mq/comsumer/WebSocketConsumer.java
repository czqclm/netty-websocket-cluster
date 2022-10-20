package org.zhiqi.netty.websocket.mq.comsumer;

import com.alibaba.fastjson2.JSONObject;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.zhiqi.netty.websocket.pojo.dto.RabbitFanoutMsgDTO;
import org.zhiqi.netty.websocket.utils.EasyWebSocketUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class WebSocketConsumer {

    @RabbitListener(queues = "#{currentNodeQueueName}")
    public void processMessage(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("\n收到消息: {}", body);

        RabbitFanoutMsgDTO rabbitFanoutMsg = JSONObject.parseObject(body, RabbitFanoutMsgDTO.class);
        if (rabbitFanoutMsg.getAllUser()) {
            EasyWebSocketUtils.broadcastCurrentNodeAllUser(rabbitFanoutMsg.getContent());
        } else {
            EasyWebSocketUtils.sendToUser(rabbitFanoutMsg.getTargetUserList(), rabbitFanoutMsg.getContent());
        }
        channel.basicAck(tag, false);
    }

}
