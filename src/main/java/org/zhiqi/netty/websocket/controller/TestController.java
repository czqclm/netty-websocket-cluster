package org.zhiqi.netty.websocket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zhiqi.netty.websocket.utils.EasyWebSocketUtils;

/**
 * @author zhiqi
 * @version 1.0
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/sendAll")
    public void sendAll() {
        EasyWebSocketUtils.broadcast("消息群发！");
    }
}
