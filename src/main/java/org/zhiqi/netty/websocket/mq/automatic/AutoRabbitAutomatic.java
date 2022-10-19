package org.zhiqi.netty.websocket.mq.automatic;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * rabbitMq 自动化
 * 创建 exchange 和 queue
 */
@Component
public class AutoRabbitAutomatic {

    /**
     * 默认的 webSocketExchange
     */
    private final static String DEFAULT_EXCHANGE = "web_socket_exchange";

    @Resource
    private RabbitAdmin rabbitAdmin;

    @Resource
    private String currentNodeQueueName;

    @PostConstruct
    private void createCurrentNodeQueueToRabbit() {
        Queue currentNodeQueue = new Queue(currentNodeQueueName, true, false, true);
        rabbitAdmin.declareQueue(currentNodeQueue);
        FanoutExchange fanoutExchange = new FanoutExchange(DEFAULT_EXCHANGE, true, false);
        rabbitAdmin.declareExchange(fanoutExchange);
        rabbitAdmin.declareBinding(BindingBuilder.bind(currentNodeQueue).to(fanoutExchange));
    }
}
