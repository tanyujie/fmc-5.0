package com.paradisecloud.fcm.service.packet;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2024/7/10 15:51
 */
@Data
@NoArgsConstructor
public class AddPacketEndPointResponse {
    private boolean result;
    private Integer id;
}
