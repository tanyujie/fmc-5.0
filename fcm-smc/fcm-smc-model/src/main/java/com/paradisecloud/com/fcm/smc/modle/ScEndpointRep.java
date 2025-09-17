package com.paradisecloud.com.fcm.smc.modle;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/8/23 11:24
 */
@NoArgsConstructor
@Data
public class ScEndpointRep {

    private String uri;
    private Boolean successQuery;
    private Boolean gkState;
    private Boolean sipState;
    private Boolean connect;
}
