package com.paradisecloud.smc3.invoker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/8/12 17:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmcAuthResponse {

    private String uuid;
    private String userType;
    private long expire;

}
