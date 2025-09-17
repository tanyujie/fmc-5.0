package com.paradisecloud.fcm.web.model.smc;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2023/4/20 10:38
 */
@Data
@NoArgsConstructor
public class CospaceRequest {
    private String conferenceId;
    private String subject;
    private String monitorNumber;
    private String accessCode;
}
