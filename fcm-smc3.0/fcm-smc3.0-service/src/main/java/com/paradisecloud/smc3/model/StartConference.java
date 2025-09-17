package com.paradisecloud.smc3.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2022/8/16 14:29
 */
@NoArgsConstructor
@Data
public class StartConference {

    private String id;

    private String subject="立即会议";

    private String scheduleStartTime;

    private String conferenceTimeType=ConferenceTimeType.INSTANT_CONFERENCE.name();

    private Integer duration=120;

    private PeriodConferenceTimeDTO periodConferenceTime;

    @NoArgsConstructor
    @Data
    public static class PeriodConferenceTimeDTO {
        private String startDate;

        private String endDate;

        private List<?> dayLists;

        private Integer weekIndexInMonthMode=0;

        private Integer dayIndexInMonthMode=0;
    }
}
