package com.paradisecloud.smc3.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2023/7/26 10:35
 */
@NoArgsConstructor
@Data
public class QueryAREASResponse {

    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("seqInParent")
    private Double seqInParent;
    @JsonProperty("remarks")
    private String remarks;
    @JsonProperty("parent")
    private Parent parent;

    @NoArgsConstructor
    @Data
    public static class Parent {
        @JsonProperty("id")
        private String id;
        @JsonProperty("name")
        private String name;
        @JsonProperty("seqInParent")
        private Double seqInParent;
        @JsonProperty("remarks")
        private String remarks;
    }
}
