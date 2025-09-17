package com.paradisecloud.fcm.ding.model.vo;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2023/8/10 17:42
 */

@NoArgsConstructor
@Data
public class WaitingRoomParticipantSetting {
    /**
     * 操作类型：
     * 1：主持人将等候室成员移入会议
     * 2：主持人将会中成员移入等候室
     * 3：主持人将等候室成员移出等候室
     */
    private Integer operateType;
    private List<UsersDTO> users;
    private Boolean allowRejoin;

    @NoArgsConstructor
    @Data
    public static class UsersDTO {
        private String msOpenId;
        private Integer instanceid;
    }
}
