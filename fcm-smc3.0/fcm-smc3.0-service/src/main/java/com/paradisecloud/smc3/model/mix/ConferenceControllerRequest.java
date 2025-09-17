package com.paradisecloud.smc3.model.mix;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/8/25 16:30
 */
@Data
@NoArgsConstructor
public class ConferenceControllerRequest {


    private String conferenceId;
    private String participantId;
    /**
     * 打开视频源：false/关闭
     * true
     */
    private Boolean isVideoMute;

    /**
     * 关闭扬声器：true/打开：
     * false
     */
    private Boolean isQuiet;

    /**
     * 静音：true/取消：false
     */
    private Boolean isMute;


    private int operate;

    private int controlType;

    private int number;

    private String  name;

    private int volume;

}
