package com.paradisecloud.com.fcm.smc.modle.request;

import com.paradisecloud.com.fcm.smc.modle.MasterPollTemplate;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author nj
 * @date 2022/9/29 15:47
 */
@Data
@NoArgsConstructor
public class MasterPollTemplateReq implements Serializable {

    private Integer interval;
    private Integer templateIndex;
    private String pollStatus;
    private Map<String, List<MasterPollTemplate.SubPic>> pollTemplates;

    @NoArgsConstructor
    @Data
    public static class SubPic {
        private String participantId;
        private Integer streamNumber;
    }
}
