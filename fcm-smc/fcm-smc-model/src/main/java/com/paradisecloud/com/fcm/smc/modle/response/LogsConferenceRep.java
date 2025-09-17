package com.paradisecloud.com.fcm.smc.modle.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2023/3/6 9:18
 */
@NoArgsConstructor
@Data
public class LogsConferenceRep {


    private List<ContentDTO> content;
    private PageableDTO pageable;
    private Integer totalPages;
    private Integer totalElements;
    private Boolean last;
    private Boolean first;
    private SortDTO sort;
    private Integer numberOfElements;
    private Integer size;
    private Integer number;
    private Boolean empty;

    @NoArgsConstructor
    @Data
    public static class PageableDTO {
        private SortDTO sort;
        private Integer pageNumber;
        private Integer pageSize;
        private Integer offset;
        private Boolean paged;
        private Boolean unpaged;

        @NoArgsConstructor
        @Data
        public static class SortDTO {
            private Boolean unsorted;
            private Boolean sorted;
            private Boolean empty;
        }
    }

    @NoArgsConstructor
    @Data
    public static class SortDTO {
        private Boolean unsorted;
        private Boolean sorted;
        private Boolean empty;
    }

    @NoArgsConstructor
    @Data
    public static class ContentDTO {
        private String id;
        private String logType;
        private String logId;
        private String logLevel;
        private String confId;
        private String operatorId;
        private String operatorName;
        private String time;
        private OthersDTO others;

        @NoArgsConstructor
        @Data
        public static class OthersDTO {
            private Integer result;
            private String conferenceSubject;
            private String participantName;
        }
    }
}
