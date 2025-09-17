package com.paradisecloud.smc3.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2022/8/23 11:23
 */
@NoArgsConstructor
@Data
public class ScEndpointInfoListRequest {


    private List<ScEndpointInfoListDTO> scEndpointInfoList;

    @NoArgsConstructor
    @Data
    public static class ScEndpointInfoListDTO {
        private String uri;
        private String serviceZoneId;
        private String nwZoneType="TRUSTED_ZONE";
    }
}
