package com.paradisecloud.com.fcm.smc.modle;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2022/9/28 9:56
 */
@NoArgsConstructor
@Data
public class PresetMultiPicReqDto {

    /**
     * 多画面名称
     */
    private String name;
    /**
     * 多画面数
     */
    private Integer picNum;
    /**
     * 多画面模式
     */
    private Integer mode;
    /**
     * 预置为多画面时，是否自
     * 动广播多画面
     */
    private Boolean autoBroadCast;
    /**
     * 该组多画面是否启动声控
     */
    private Boolean autoVoiceActive;
    /**
     * 该组多画面是否自动生效
     */
    private Boolean autoEffect;
    /**
     * 当该列表为1时，代表是预
     * 置的多画面，当列表大于1
     * 时，代表是多画面轮训
     */
    private List<PresetMultiPicRollsDTO> presetMultiPicRolls;

    @NoArgsConstructor
    @Data
    public static class PresetMultiPicRollsDTO {
        private Integer interval;
        private List<SubPicListDTO> subPicList;

        @NoArgsConstructor
        @Data
        public static class SubPicListDTO {
            private String name;
            private String uri;
            private String participantId;
            private Integer streamNumber;
        }
    }
}
