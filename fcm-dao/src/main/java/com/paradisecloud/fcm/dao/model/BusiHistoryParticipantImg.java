package com.paradisecloud.fcm.dao.model;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 【请填写功能名称】对象 busi_history_participant_img
 * 
 * @author lilinhai
 * @date 2024-09-23
 */
@Schema(description = "【请填写功能名称】")
public class BusiHistoryParticipantImg extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Integer id;

    /** 参会者url */
    @Schema(description = "参会者url")
    @Excel(name = "参会者url")
    private String remoteParty;

    /** coSpaceId */
    @Schema(description = "coSpaceId")
    @Excel(name = "coSpaceId")
    private String coSpace;

    /** callID */
    @Schema(description = "callID")
    @Excel(name = "callID")
    private String callId;

    /** 参会者名称 */
    @Schema(description = "参会者名称")
    @Excel(name = "参会者名称")
    private String name;

    /** 图片 */
    @Schema(description = "图片")
    @Excel(name = "图片")
    private String imageBase64;

    /** 参会者ID */
    @Schema(description = "参会者ID")
    @Excel(name = "参会者ID")
    private String callLegId;

    /** 图片时间 */
    @Schema(description = "图片时间")
    private Date imgTime;

    /** 入会时间 */
    @Schema(description = "入会时间")
    private Date joinTime;
    @Schema(description = "是否主席0 不是, 1是")
    private int chairman;

    private Long historyId;

    public void setId(Integer id) 
    {
        this.id = id;
    }

    public Integer getId() 
    {
        return id;
    }
    public void setRemoteParty(String remoteParty) 
    {
        this.remoteParty = remoteParty;
    }

    public String getRemoteParty() 
    {
        return remoteParty;
    }
    public void setCoSpace(String coSpace) 
    {
        this.coSpace = coSpace;
    }

    public String getCoSpace() 
    {
        return coSpace;
    }
    public void setCallId(String callId) 
    {
        this.callId = callId;
    }

    public String getCallId() 
    {
        return callId;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setImageBase64(String imageBase64) 
    {
        this.imageBase64 = imageBase64;
    }

    public String getImageBase64() 
    {
        return imageBase64;
    }
    public void setCallLegId(String callLegId) 
    {
        this.callLegId = callLegId;
    }

    public String getCallLegId() 
    {
        return callLegId;
    }
    public void setImgTime(Date imgTime) 
    {
        this.imgTime = imgTime;
    }

    public Date getImgTime() 
    {
        return imgTime;
    }

    public Date getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Date joinTime) {
        this.joinTime = joinTime;
    }

    public int getChairman() {
        return chairman;
    }

    public void setChairman(int chairman) {
        this.chairman = chairman;
    }

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("remoteParty", getRemoteParty())
            .append("coSpace", getCoSpace())
            .append("callId", getCallId())
            .append("name", getName())
            .append("imageBase64", getImageBase64())
            .append("callLegId", getCallLegId())
            .append("createTime", getCreateTime())
            .append("imgTime", getImgTime())
            .append("joinTime", getJoinTime())
            .append("chairman", getChairman())
            .append("historyId",getHistoryId())
            .toString();
    }
}
