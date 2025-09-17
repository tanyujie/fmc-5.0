package com.paradisecloud.com.fcm.smc.modle;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2022/8/23 10:35
 */
@NoArgsConstructor
@Data
public class DefaultServiceZoneIdRep {


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
        private Boolean unpaged;
        private Boolean paged;

        @NoArgsConstructor
        @Data
        public static class SortDTO {
            private Boolean sorted;
            private Boolean unsorted;
            private Boolean empty;
        }
    }

    @NoArgsConstructor
    @Data
    public static class SortDTO {
        private Boolean sorted;
        private Boolean unsorted;
        private Boolean empty;
    }

    @NoArgsConstructor
    @Data
    public static class ContentDTO {
        private String id;
        private String name;
        private String servZoneType;
        private String serviceZoneStatus;
        private Integer avcParticipantNum;
        private String scId;
        private String scName;
        private String scIpAddress;
        private String dmzScId;
        private String dmzScName;
        private String dmzScIpAddress;
    }
}
