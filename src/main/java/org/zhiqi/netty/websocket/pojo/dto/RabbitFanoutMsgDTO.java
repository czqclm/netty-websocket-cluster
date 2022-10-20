package org.zhiqi.netty.websocket.pojo.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RabbitFanoutMsgDTO {

    private String content;

    private Boolean allUser;

    private List<String> targetUserList;
}
