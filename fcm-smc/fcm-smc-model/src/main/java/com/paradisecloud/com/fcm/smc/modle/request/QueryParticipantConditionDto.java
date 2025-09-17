package com.paradisecloud.com.fcm.smc.modle.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2023/2/28 14:52
 */
@Data
@NoArgsConstructor
public class QueryParticipantConditionDto {
    /**
     * * 与会者名称(1~64字符)
     */
    private  String name;
    /**
     * 是否可做视频源会场
     * (true/false)
     */
    private Boolean videoSource;
    /**
     * 是否可作为视频源会场或
     * 语音会场(true/false)
     */
    private  Boolean videoAndVoice;
    /**
     * 发言状态
     */
    private Boolean handUp;
    /**
     * 不可见
     */
    private  Boolean virtual;
    /**
     * 在线状态
     */
    private Boolean online;
    /**
     * 是否静音
     */
    private  Boolean quiet;
    /**
     * 是否闭音
     */
    private Boolean mute;
    private List<String> participantIds;
    /**
     * 是否仅显示当前组织的会
     * 议信息
     */
    private int showCurrentOrg;

}
