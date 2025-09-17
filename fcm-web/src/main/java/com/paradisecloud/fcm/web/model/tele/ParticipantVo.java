package com.paradisecloud.fcm.web.model.tele;

import com.paradisecloud.fcm.telep.model.busi.participants.TeleParticipant;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/10/16 10:40
 */
@Data
@NoArgsConstructor
public class ParticipantVo {

    private String uri;
    private TeleParticipant teleParticipant;
    private String conferenceId;

}
