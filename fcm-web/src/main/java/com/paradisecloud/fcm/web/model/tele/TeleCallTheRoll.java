package com.paradisecloud.fcm.web.model.tele;

import com.paradisecloud.fcm.telep.model.busi.participants.TeleParticipant;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author nj
 * @date 2022/10/17 16:22
 */
@Data
@NoArgsConstructor
public class TeleCallTheRoll {

    @NotBlank
    private String conferenceId;
    @NotBlank
    private String participantId;
    @NotBlank
    private String conferenceNumber;
    @NotBlank
    private String uri;
    @NotNull
    private TeleParticipant teleParticipant;

}
