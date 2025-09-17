package com.paradisecloud.com.fcm.smc.modle;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/8/25 15:46
 */
@Data
@NoArgsConstructor
public class ParticipantStatusDto {

    private String id;
    private Boolean isOnline;
    private Boolean isMute;
    private Boolean isQuiet;
    private Boolean isVideoMute;
    /**
     * 1 锁定
     * 0 解锁
     */
    private int videoSwitchAttribute;
}
