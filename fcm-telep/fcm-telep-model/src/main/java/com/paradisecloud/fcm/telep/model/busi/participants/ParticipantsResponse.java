package com.paradisecloud.fcm.telep.model.busi.participants;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2022/10/13 9:58
 */
@Data
@NoArgsConstructor
public class ParticipantsResponse {
    private List<TeleParticipant> participants;
    private String enumerateID;

}
