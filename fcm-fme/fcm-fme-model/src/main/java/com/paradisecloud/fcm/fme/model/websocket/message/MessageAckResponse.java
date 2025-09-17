package com.paradisecloud.fcm.fme.model.websocket.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * webSocket 握手响应
 *
 * @author zt1994 2019/8/30 14:22
 */
@Getter
@Setter
@ToString
public class MessageAckResponse
{
    
    /**
     * 类型
     */
    private String type;
    
    /**
     * webSocket 握手响应
     */
    private MessageAck messageAck;
}
