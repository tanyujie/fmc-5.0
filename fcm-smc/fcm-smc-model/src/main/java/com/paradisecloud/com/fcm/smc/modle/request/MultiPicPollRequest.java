package com.paradisecloud.com.fcm.smc.modle.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 多画面设置
 * @author nj
 * @date 2022/8/26 11:09
 */
@NoArgsConstructor
@Data
public class MultiPicPollRequest {
    private String conferenceId;

    private Boolean broadcast;

    private Integer interval;

    /**
     * 画面数
     */
    private Integer picNum;
    /**
     * 多画面模式
     */
    private Integer mode;
    /**
     * 轮询画面列表
     */
    private List<SubPicPollInfoListDTO> subPicPollInfoList;
    /**
     *  轮询操作
     */
    private String pollStatus;

    @NoArgsConstructor
    @Data
    public static class SubPicPollInfoListDTO {
        /**
         * 轮询间隔
         */
        private Integer interval;
        private List<ParticipantIdsDTO> participantIds;

        @NoArgsConstructor
        @Data
        public static class ParticipantIdsDTO {
            private String participantId;
            private Integer streamNumber=0;
            private Integer weight;
        }
    }
}
