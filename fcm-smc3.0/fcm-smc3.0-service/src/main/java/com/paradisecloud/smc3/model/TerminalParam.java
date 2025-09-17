package com.paradisecloud.smc3.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2022/8/22 11:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TerminalParam {

    /**
     * vhd
     */
    private String terminalType="CUSTOMIZE_TERMINAL";
    private String ipProtocolType=SMcipProtocolType.SIP.name();
    /**
     * 绑定终端号码(前端传入)
     */
    private String  middleUri;
    private String  leftUri;
    private String  rightUri;
    /**
     * 终端登录SC帐号
     */
    private String  loginScName;
    /**
     * 终端登录SC密码
     */
    private String  loginScPassword;
    /**
     * 终端登录SMC帐号
     */
    private String  loginSmcName;
    /**
     * 终端登录SMC密码
     */
    private String  loginSmcPassword;
    /**
     * SC地址
     */
    private String  scRegisterAddress;
    /**
     * 终端速率。取值范围:64
     * Kbit/s,128 Kbit/s,192
     * Kbit/s,256 Kbit/s,320
     * Kbit/s,384 Kbit/s,512
     * Kbit/s,768 Kbit/s,1024
     * Kbit/s,1152 Kbit/s,1427
     * Kbit/s,1536 Kbit/s,1920
     * Kbit/s,2048 Kbit/s,3
     * Mbit/s,4 Mbit/s,5 Mbit/s,
     * 6 Mbit/s,7 Mbit/s,8
     * Mbit/s
     */
    private String  rate="1920 Kbit/s";


    private String nwZoneType="TRUSTED_ZONE";

    /**
     * serviceList
     *    "MULTIMEDIA_CONF",
     *    "DEVICE_MANAGE"
     */
    private List<String> serviceList;

    private String securityLevel="PUBLIC";

}
