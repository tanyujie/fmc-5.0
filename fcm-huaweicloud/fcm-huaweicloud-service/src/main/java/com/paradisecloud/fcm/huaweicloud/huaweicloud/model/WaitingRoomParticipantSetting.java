package com.paradisecloud.fcm.huaweicloud.huaweicloud.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2024/3/8 11:04
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
    }
}
