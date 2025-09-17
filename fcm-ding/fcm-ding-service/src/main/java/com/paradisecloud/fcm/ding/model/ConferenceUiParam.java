package com.paradisecloud.fcm.ding.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/9/20 11:24
 */
@NoArgsConstructor
@Data
public class ConferenceUiParam {


    private String id;
    private Integer totalParticipantNum;
    private Integer onlineParticipantNum;
    private Integer totalNum;
    private Integer onlineNum;
    private Integer handUpNum;
    private String subject;
    private String scheduleStartTime;
    private Integer duration;
    private Integer startedTime;
    private String localTime;
    private String accessCode;
    private Boolean voice=Boolean.FALSE;
    private Boolean record=Boolean.FALSE;
    private Boolean containSvc=Boolean.FALSE;
    private Boolean audioRecord=Boolean.FALSE;
    private Boolean supportSubtitle=Boolean.FALSE;



}
