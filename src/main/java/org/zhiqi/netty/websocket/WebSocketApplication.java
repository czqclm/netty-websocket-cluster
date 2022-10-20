package org.zhiqi.netty.websocket;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * websocket 服务
 *
 * @author zhiqi
 * @version 1.0
 */
@SpringBootApplication
@EnableScheduling
public class WebSocketApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WebSocketApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(WebSocketApplication.class, args);
    }
}
