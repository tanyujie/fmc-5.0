package com.paradisecloud.fcm.dao.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2023/10/20 16:42
 */
@NoArgsConstructor
@Data
public class TerminalParam {
    private String id;
    private String terminalType;
    private String ipProtocolType;
    private String rate;
    private List<String> serviceList;
    private String securityLevel;
    private String loginSmcName;
    private String loginSmcPassword;
    private String loginScName;
    private String loginScPassword;
    private String middleUri;
    private String leftUri;
    private String rightUri;
    private String nwZoneType;
    private String scRegisterAddress;
}
