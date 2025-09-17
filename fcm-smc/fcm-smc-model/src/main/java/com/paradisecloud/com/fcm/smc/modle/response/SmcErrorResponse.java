package com.paradisecloud.com.fcm.smc.modle.response;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SmcErrorResponse {
    private String errorType;
    private String errorNo;
    private String errorDesc;
}
