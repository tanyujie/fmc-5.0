package com.paradisecloud.com.fcm.smc.modle.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SmcOrganization {
    private String id;
    private String remarks;
    private String name;
    private Double seqInParent;
    private SmcOrganization parent;
}
