package com.paradisecloud.com.fcm.smc.modle.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2022/9/27 9:46
 */
@NoArgsConstructor
@Data
public class SmcTemplateListRep {


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
        private String subject;
        private String organizationName;
        private Integer duration;
        private String type;
    }
}
