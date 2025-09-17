package com.paradisecloud.com.fcm.smc.modle;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2022/8/19 10:58
 */
@NoArgsConstructor
@Data
public class ParticipantStatus {

    /**
     *  会场Id(36字符)
     */
    private String id;
    /**
     * 呼叫：true/挂断：false
     */
    private Boolean isOnline;
    /**
     * 静音：true/取消：false
     */
    private Boolean isMute;
    /**
     * 关闭扬声器：true/打开：
     * false
     */
    private Boolean isQuiet;
    /**
     * 打开视频源：false/关闭
     * true
     */
    private Boolean isVideoMute;
    /**
     * 音量(0~100)
     */
    private Integer volume;
    /**
     * 视频源锁定
     */
    private String videoSwitchAttribute;
    /**
     * 设置会场视频源
     */
    private MultiPicInfoDTO multiPicInfo;

    @NoArgsConstructor
    @Data
    public static class MultiPicInfoDTO {
        private Integer picNum;
        private Integer mode;
        private List<SubPicListDTO> subPicList;

        @NoArgsConstructor
        @Data
        public static class SubPicListDTO {
            private String participantId;
            private Integer streamNumber;
        }
    }
}
