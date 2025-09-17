package com.paradisecloud.com.fcm.smc.modle.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OperatorName","Description","Type"
 * @author nj
 * @date 2023/3/6 16:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogsPageModel {
    private String id;
    private String logId;
    private String logLevel;
    private String time;
    private String operatorName;
    private String description;
    private String type;
}
