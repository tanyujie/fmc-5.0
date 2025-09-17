package com.paradisecloud.fcm.service.packet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2024/7/10 15:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacketEndPoint {
    private Integer id;
    private String name;
    private String ip;
    private String url1;
    private String url2;
}
