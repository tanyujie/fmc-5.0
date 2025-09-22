package com.paradisecloud.fcm.dao.model.vo;

import com.paradisecloud.fcm.dao.model.BusiConferenceUserSignIn;
import lombok.Data;

import java.util.List;

@Data
public class BusiConferenceUserSignInDetailVO {
    /**
     * 已签到人数
     * 说明：统计 userSignInList 中 signStatus = 2（已签到）的记录数量
     */
    private int signedCount;

    /**
     * 总记录数
     * 说明：签到记录表中的总条数（包含已签到、未签到等所有状态）
     */
    private int totalCount;
    /**
     * 签到状态（冗余字段，可选，用于标记当前统计的整体状态，如 1-未开始签到 2-签到中）
     * 若无需整体状态，可删除该字段
     */
    private int signStatus;
    private List<BusiConferenceUserSignIn> userSignInList;
}
