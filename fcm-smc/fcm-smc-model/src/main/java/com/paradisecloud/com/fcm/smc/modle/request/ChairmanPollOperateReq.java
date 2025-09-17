package com.paradisecloud.com.fcm.smc.modle.request;

import com.paradisecloud.com.fcm.smc.modle.PollOperateTypeDto;
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
