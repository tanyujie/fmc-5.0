package com.paradisecloud.smc3.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;


/**
 * @author nj
 * @date 2022/9/23 10:01
 */
@Data
@NoArgsConstructor
public class ExtendTimeReq {

    private String conferenceId;

    @Min(value=15,message ="单次延长时间不能小于15分钟,当前最大可延长的会议时长为1320分钟。")
    @Max(value=1320,message ="单次延长时间不能小于15分钟,当前最大可延长的会议时长为1320分钟。")
    private Integer extendTime;
}
