package com.paradisecloud.smc3.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2023/7/25 18:03
 */
@NoArgsConstructor
@Data
public class AreaIDResponse {


    private List<DataDTO> data;
    private String result;
    private Integer code;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        private String areaId;
        private String areaName;
        private String areaRelation;
        private String areaIdRelation;
    }
}
