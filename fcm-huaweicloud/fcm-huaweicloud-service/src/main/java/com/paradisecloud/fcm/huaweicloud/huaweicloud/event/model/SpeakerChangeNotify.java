package com.paradisecloud.fcm.huaweicloud.huaweicloud.event.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2024/3/8 9:34
 */
@NoArgsConstructor
@Data
public class SpeakerChangeNotify {


    private List<DataDTO> data;
    private String confID;
    private String msgID;
    private Integer msgMode;
    private Long version;
    private Long createTime;
    private String action;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        private String pid;
        private String name;
        private Integer speakingVolume;
    }
}
