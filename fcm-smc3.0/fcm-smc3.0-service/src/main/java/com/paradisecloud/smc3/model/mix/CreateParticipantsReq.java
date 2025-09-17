package com.paradisecloud.smc3.model.mix;

import com.paradisecloud.smc3.model.ParticipantReqDto;
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
