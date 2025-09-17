package com.paradisecloud.fcm.telp.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/10/12 17:06
 */
@Data
@NoArgsConstructor
public class FaultResponse {
    private int faultCode;
    private String faultString;
}
