package org.zhiqi.netty.websocket.mq.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

/**
 * rabbitMq
 *
 * @author zhiqi
 * @version 1.0
 */
@Slf4j
@Configuration
public class MyRabbitConfig {

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);

        // 设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            log.info("===> ConfirmCallback:     发送情况：" + ack);
            if (!ack) {
                log.info("===> ConfirmCallback:     相关数据：" + correlationData);
                log.info("===> ConfirmCallback:     原因：" + cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.info("===> ReturnCallback:     消息：" + message);
            log.info("===> ReturnCallback:     回应码：" + replyCode);
            log.info("===> ReturnCallback:     回应信息：" + replyText);
            log.info("===> ReturnCallback:     交换机：" + exchange);
            log.info("===> ReturnCallback:     路由键：" + routingKey);
        });
        return rabbitTemplate;
    }
}
