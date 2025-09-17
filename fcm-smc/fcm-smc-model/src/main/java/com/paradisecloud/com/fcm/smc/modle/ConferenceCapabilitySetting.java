package com.paradisecloud.com.fcm.smc.modle;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/8/16 15:24
 */
@NoArgsConstructor
@Data
public class ConferenceCapabilitySetting {

    private Boolean enableHdRealTime;
    private Boolean autoRecord;
    private Integer dataConfProtocol;
    private Integer checkInDuration;
    private Boolean audioRecord;
    private Boolean amcRecord;
    private Boolean enableDataConf;
    private String type;
    private Integer videoProtocol;
    private Integer audioProtocol;
    private Boolean enableCheckIn;
    private Integer rate;
    private Integer reserveResource;
    private Boolean enableRecord;
    private Boolean enableLiveBroadcast;
    private Integer videoResolution;
    private Integer mediaEncrypt;
}
