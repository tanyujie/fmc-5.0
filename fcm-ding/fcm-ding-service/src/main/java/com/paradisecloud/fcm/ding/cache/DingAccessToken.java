package com.paradisecloud.fcm.ding.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2024/2/20 15:25
 */
@NoArgsConstructor
@Data
public class DingAccessToken {

    private String accessToken;
    private Long expireIn;
}
