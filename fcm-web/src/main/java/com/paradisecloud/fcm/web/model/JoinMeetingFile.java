package com.paradisecloud.fcm.web.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2024/4/1 15:52
 */
@Data
@NoArgsConstructor
public class JoinMeetingFile {

    private  String conferenceId;

    private Long[] ids;
}
