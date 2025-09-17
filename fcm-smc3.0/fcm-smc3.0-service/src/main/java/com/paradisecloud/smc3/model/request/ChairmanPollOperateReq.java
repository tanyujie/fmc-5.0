package com.paradisecloud.smc3.model.request;

import com.paradisecloud.fcm.common.enumer.PollOperateTypeDto;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/9/29 14:27
 */
@Data
@NoArgsConstructor
public class ChairmanPollOperateReq {

    private String conferenceId;
    private PollOperateTypeDto pollStatus;
}
