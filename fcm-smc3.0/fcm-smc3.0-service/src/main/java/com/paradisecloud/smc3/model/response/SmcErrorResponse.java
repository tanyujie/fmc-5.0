package com.paradisecloud.smc3.model.response;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SmcErrorResponse {
    private String errorType;
    private String errorNo;
    private String errorDesc;
    private String param;
}
