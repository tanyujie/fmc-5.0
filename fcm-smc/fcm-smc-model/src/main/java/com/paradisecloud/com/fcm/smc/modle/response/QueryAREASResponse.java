package com.paradisecloud.com.fcm.smc.modle.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2023/7/26 10:35
 */
@NoArgsConstructor
@Data
public class QueryAREASResponse {


    private String id;
    private String name;
    private Double seqInParent;
    private String remarks;
    private QueryAREASResponse parent;
}
