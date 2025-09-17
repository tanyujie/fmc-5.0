package com.paradisecloud.smc3.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/8/12 17:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddVmrResponse {

    private String id;
    private String vmrNumber;
    private String vmrType;

}
