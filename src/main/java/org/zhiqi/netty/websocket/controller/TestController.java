package org.zhiqi.netty.websocket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zhiqi.netty.websocket.utils.EasyWebSocketUtils;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/sendAll")
    public void sendAll() {
        EasyWebSocketUtils.broadcast("消息群发！");
    }
}
