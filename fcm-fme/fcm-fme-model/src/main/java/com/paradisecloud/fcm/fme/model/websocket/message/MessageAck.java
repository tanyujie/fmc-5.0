package com.paradisecloud.fcm.fme.model.websocket.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * webSocket 握手响应
 *
 * @author zt1994 2019/8/30 14:24
 */
@Getter
@Setter
@ToString
public class MessageAck
{
    
    /**
     * 消息id
     */
    private Integer messageId;
    
    /**
     * ack 响应状态
     */
    private String status;
}
