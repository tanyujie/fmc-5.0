package com.paradisecloud.fcm.web.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2024/3/29 10:56
 */
@Data
@NoArgsConstructor
public class MeetingFileConference {

    private String type;
    private String file_path;
    private String meeting_room;
    private String fme_ip;
    private String mcu_ip;
    private String mcu_url;
    private String display_name;
}
