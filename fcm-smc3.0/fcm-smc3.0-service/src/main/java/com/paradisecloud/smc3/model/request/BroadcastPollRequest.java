package com.paradisecloud.smc3.model.request;

import com.paradisecloud.smc3.model.PollOperateTypeDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2022/8/26 11:26
 */
@NoArgsConstructor
@Data
public class BroadcastPollRequest {

    private String conferenceId;

    /**
     * 轮询操作
     */
    private PollOperateTypeDto pollStatus;

    /**
     * 间隔时间
     */
    private Integer interval;
    /**
     *  轮询会场列表
     */
    private List<String> participantIds;
}
