package com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2024/2/28 13:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscribeData {
    private String conferenceID;
    private String confToken;
    private List<String> subscribeType;
}
