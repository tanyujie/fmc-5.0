package com.paradisecloud.fcm.huaweicloud.huaweicloud.model.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2024/2/29 15:45
 */
@NoArgsConstructor
@Data
public class InviteResultNotifyMessage {


    private List<DataDTO> data;
    private String confID;
    private String msgID;
    private Integer msgMode;
    private Long version;
    private Long createTime;
    private String action;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        private String callNumber;
        private String resultCode;
    }
}
