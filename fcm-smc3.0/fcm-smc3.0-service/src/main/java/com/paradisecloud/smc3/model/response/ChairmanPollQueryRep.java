package com.paradisecloud.smc3.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2022/9/29 14:02
 */
@NoArgsConstructor
@Data
public class ChairmanPollQueryRep {


    private Integer interval;
    private PollTemplatesDTO pollTemplates;
    private Integer templateIndex;
    private String pollStatus;

    @NoArgsConstructor
    @Data
    public static class PollTemplatesDTO {
        private List<_$0DTO> $0;

        @NoArgsConstructor
        @Data
        public static class _$0DTO {
            private String participantId;
            private Integer streamNumber;
        }
    }
}
