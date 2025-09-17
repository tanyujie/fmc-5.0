package com.paradisecloud.com.fcm.smc.modle.mix;

import com.paradisecloud.com.fcm.smc.modle.ParticipantReqDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2022/8/25 14:57
 */
@Data
@NoArgsConstructor
public class CreateParticipantsReq {

   private String conferenceId;

   private List<ParticipantReqDto> participants;

   private List<Long> terminalIds;
}
