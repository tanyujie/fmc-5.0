package com.paradisecloud.fcm.wvp.gb28181.reponse;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class WvpSyncStatusResponse extends WvpCommonResponse{


    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        private Integer total;
        private Integer current;
        private Object errorMsg;
        private Boolean syncIng;
    }
}
