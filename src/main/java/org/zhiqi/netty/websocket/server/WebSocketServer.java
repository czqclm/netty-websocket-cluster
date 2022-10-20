package org.zhiqi.netty.websocket.server;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yeauty.annotation.*;
import org.yeauty.pojo.Session;
import org.zhiqi.netty.websocket.utils.EasyWebSocketUtils;


/**
 * websocket server
 *
 * @author zhiqi
 * @version 1.0
 */
@Slf4j
@ServerEndpoint(path = "${websocket.path}", port = "${websocket.port}")
@Data
public class WebSocketServer {

    private Session session;
    private String userId;


    @BeforeHandshake
    public void handshake(Session session, @PathVariable String userId){
        this.session = session;
        this.userId = userId;
        EasyWebSocketUtils.pushUser(userId, this);
    }


    @OnOpen
    public void onOpen(Session session, @PathVariable String userId){
        log.info("\n========== websocket open ==========\nuserId: {}\n",userId);
        session.sendText("Try send message to server!");
    }

    @OnClose
    public void onClose(Session session) {
        log.info("\n========== websocket closed ==========\nuserId: {}\n",userId);
        EasyWebSocketUtils.popUser(userId);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.info("\n========== websocket error ==========\nuserId: {}\n",userId);
        EasyWebSocketUtils.popUser(userId);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("\n========== websocket receive ==========\nuserId: {}\nmsg: {}", userId, message);
    }
}
