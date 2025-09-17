package com.paradisecloud.fcm.dao.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "成员签到关联表")
@Data
public class BusiConferenceUserSignIn extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    private Integer id;

    /**
     * 签到 ID
     */
    @Schema(description = "签到 ID")
    private Integer signInId;

    /**
     * 成员 ID
     */
    @Schema(description = "成员 ID")
    private Integer userId;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String userNickname;

    /**
     * 成员签到时间
     */
    @Schema(description = "成员签到时间")
    private Long signInTime;

    /**
     * 成员签到状态1：未签到，2：已签到
     */
    @Schema(description = "成员签到状态1：未签到，2：已签到")
    private Integer signStatus;

}
