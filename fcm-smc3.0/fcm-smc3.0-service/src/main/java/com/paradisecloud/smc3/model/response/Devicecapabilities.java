package com.paradisecloud.smc3.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2023/10/19 11:48
 */
@NoArgsConstructor
@Data
public class Devicecapabilities {


    private String id;
    private String name;
    private List<?> h263Capabilities;
    private List<Integer> h264BpCapabilities;
    private List<Integer> h264HpCapabilities;
    private List<?> h265Capabilities;
    private Boolean svcCapability;
    private List<String> rate;
    private List<Integer> ipProtocolType;
    private List<?> businessList;
    private List<?> maintenanceList;
    private List<String> serviceList;
    private Boolean tpCapability;
}
