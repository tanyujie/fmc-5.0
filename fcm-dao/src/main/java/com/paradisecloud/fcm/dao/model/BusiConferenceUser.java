package com.paradisecloud.fcm.dao.model;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "会议模板的用户")
@Data
public class BusiConferenceUser extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 参会类型：1被叫，2手动主叫，3自动主叫，10直播 */
    @Schema(description = "参会类型：1被叫，2手动主叫，3自动主叫，10直播")
    private Integer attendType;

    /** 模板中的与会者的UUID */
    @Schema(description = "模板中的与会者的UUID")
    private String uuid;

    /** 会议模板ID */
    @Schema(description = "会议模板ID")
    private Long templateConferenceId;

    /** 终端ID */
    @Schema(description = "终端ID")
    private Long terminalId;

    /** 参会者顺序（权重倒叙排列） */
    @Schema(description = "参会者顺序（权重倒叙排列）")
    private Integer weight;

    /** 业务属性 */
    @Schema(description = "业务属性")
    private JsonNode businessProperties;

    /** 部门ID */
    @Schema(description = "部门ID")
    private Long deptId;


}
