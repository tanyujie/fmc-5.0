package com.paradisecloud.fcm.web.model.smc;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2023/4/25 14:43
 */
@Data
@NoArgsConstructor
public class ParticipantFontSetting {
    private int fontMode;
    private int fontSize;
    private boolean overLayClose;
    private String conferenceId;
}
