package com.paradisecloud.smc3.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2023/9/21 14:57
 */
@NoArgsConstructor
@Data
public class ConferenceCapabilitySettingCreate {


    private Boolean enableHdRealTime=Boolean.valueOf(false);
    private Boolean autoRecord=Boolean.valueOf(false);
    private String dataConfProtocol;
    private Integer checkInDuration=10;
    private Boolean audioRecord=Boolean.valueOf(false);
    private Boolean amcRecord=Boolean.FALSE;
    private Boolean enableDataConf=Boolean.valueOf(false);
    private String type;
    private String videoProtocol;
    private String audioProtocol;
    private Boolean enableCheckIn=Boolean.valueOf(false);
    private Integer rate;
    private Integer reserveResource=0;
    private Boolean enableRecord=Boolean.valueOf(false);
    private Boolean enableLiveBroadcast=Boolean.valueOf(false);
    private String videoResolution;
    private String mediaEncrypt;
    private String srcLang="CHINESE";
    private String svcVideoProtocol="H265";
    private String svcVideoResolution="MPI_1080P";
    private Integer svcRate=3840;

    private Boolean supportMinutes=Boolean.FALSE;
    private Boolean supportSubtitle=Boolean.valueOf(false);
    private Boolean enableFec=Boolean.TRUE;
}
