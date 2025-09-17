package com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.client;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2023/3/2 14:22
 */
@Data
@NoArgsConstructor
public class StompMessage {
    private String conferenceId;
    private String destination;
    private String contentType;
    private String messageId;
    private int contentLength;
    private String subscription;
    private String obj;
}
