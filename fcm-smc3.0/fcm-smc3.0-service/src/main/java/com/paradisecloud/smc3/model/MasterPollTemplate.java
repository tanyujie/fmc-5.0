package com.paradisecloud.smc3.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author nj
 * @date 2022/9/29 9:39
 */
@NoArgsConstructor
@Data
public class MasterPollTemplate implements Serializable {

    private String conferenceId;
    private Integer interval;
    private Integer templateIndex;
    private String pollStatus;
    private Map<String,List<SubPic>> pollTemplates;

    @NoArgsConstructor
    @Data
    public static class SubPic {
        private String participantId;
        private Integer streamNumber;
    }
}
