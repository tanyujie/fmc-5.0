package com.paradisecloud.fcm.tencent.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nj
 * @date 2022/9/20 11:22
 */
@NoArgsConstructor
@Data
public class ConferenceState {


    private String conferenceId;
    private Boolean mute=Boolean.FALSE;
    private Boolean quiet=Boolean.FALSE;
    private String chairmanId;
    private String broadcastId;
    private String spokesmanId;
    private String presenterId;
    private String lockPresenterId;
    private Integer recordStatus;
    private Boolean enableVoiceActive=Boolean.FALSE;
    private Boolean lock=Boolean.FALSE;
    private Boolean directing=Boolean.FALSE;
    private Boolean local=Boolean.FALSE;
    private String multiPicPollStatus;
    private String broadcastPollStatus;
    private String chairmanPollStatus;
    private Integer handUpNum;
    private String localRecordId;
    private Integer pushStreamStatus;
    private Boolean globalRecordStatus=Boolean.FALSE;
    private Boolean generatedAlarm=Boolean.FALSE;
    private ChooseMultiPicInfo.MultiPicInfoDTO multiPicInfo;
    private Boolean cascade;
    private String chooseId;
    private ParticipantRspDto chairman;
    private Long templateId;
    private String type;
    private List<String> currentSpeakers=new ArrayList<>();
}
