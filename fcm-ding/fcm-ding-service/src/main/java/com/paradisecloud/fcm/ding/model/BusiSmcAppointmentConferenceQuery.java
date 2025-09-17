package com.paradisecloud.fcm.ding.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2023/3/16 14:13
 */
@Data
@NoArgsConstructor
public class BusiSmcAppointmentConferenceQuery {

    private String searchKey;

    private int pageIndex=1;

    private int pageSize=10;

    private String startTime;

    private String endTime;

    private String deptId;
}
