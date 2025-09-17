package com.paradisecloud.smc3.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2023/3/22 15:58
 */
@Data
@NoArgsConstructor
public class PeriodConferenceTime {
    private int dayIndexInMonthMode;
    private int durationPerPeriodUnit;
    private String periodUnitType;
    private String startDate;
    private String endDate;
    private int weekIndexInMonthMode;
    private List<?> dayLists;
}
