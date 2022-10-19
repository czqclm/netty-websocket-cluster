package org.zhiqi.netty.websocket.mq.comsumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class WebSocketConsumer {

    @RabbitListener(queues = "#{currentNodeQueueName}")
    public void processMessage(Message message, Channel channel) throws IOException {
        // 消息的标识
        long tag = message.getMessageProperties().getDeliveryTag();
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("\n收到消息: {}", body);
        // 消费成功
        // false 表示不进行批量表示消费成功
        channel.basicAck(tag, false);
    }

}
