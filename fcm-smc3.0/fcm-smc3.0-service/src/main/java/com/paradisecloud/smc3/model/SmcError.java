package com.paradisecloud.smc3.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/9/21 11:49
 */
@NoArgsConstructor
@Data
public class SmcError {

    private String timestamp;
    private Integer status;
    private String error;
    private String exception;
    private String message;
    private String path;
}
