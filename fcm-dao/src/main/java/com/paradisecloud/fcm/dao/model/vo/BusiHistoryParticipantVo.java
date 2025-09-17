package com.paradisecloud.fcm.dao.model.vo;

import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.fcm.dao.model.CdrCallLegEnd;
import com.paradisecloud.fcm.dao.model.CdrCallLegStart;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
public class BusiHistoryParticipantVo {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 关联的会议ID */
    @Schema(description = "关联的会议ID")
    private Long historyConferenceId;

    /** 参会者顺序（权重倒叙排列） */
    @Schema(description = "参会者顺序（权重倒叙排列）")
    private Integer weight;

    /** 会场ID：callLegId */
    @Schema(description = "会场ID：callLegId")
    private String callLegId;

    /** 会议ID：callId */
    @Schema(description = "会议ID：callId")
    private String callId;

    /** 部门Id */
    @Schema(description = "部门Id")
    private Integer deptId;

    /** callLeg的远程参与方地址 */
    @Schema(description = "callLeg的远程参与方地址")
    @Excel(name = "远程参与方地址")
    private String remoteParty;

    /** 终端名称 */
    @Schema(description = "终端名称")
    @Excel(name = "终端名称")
    private String name;

    /** 入会时间 */
    @Schema(description = "入会时间")
    @Excel(name = "入会时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date joinTime;

    /** 离会时间 */
    @Schema(description = "离会时间")
    @Excel(name = "离会时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date outgoingTime;

    /**
     * call leg处于活动的时长(s)
     */
    @Schema(description = "call leg处于活动的时长(s)")
    @Excel(name = "入会时长(s)")
    private Integer durationSeconds;

    private CdrCallLegStart cdrCallLegStart;

    private CdrCallLegEnd cdrCallLegEnd;

    @Schema(description = "coSpaceId")
    private String coSpace;

    @Schema(description = "joined")
    private Boolean joined;

    /** 媒体属性 */
    @Schema(description = "媒体属性")
    @Excel(name = "媒体属性")
    private Map<String, Object> mediaInfo;

    /** 关联的终端ID */
    @Schema(description = "关联的终端ID")
    private Long terminalId;

    private String images;

    private String terminalTypeName;

}
