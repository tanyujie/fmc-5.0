package com.paradisecloud.fcm.telep.dao.model;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author nj
 * @date 2022/10/21 10:59
 */
@Schema(description = "【请填写功能名称】")
public class BusiTeleParticipant extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Integer id;

    /** 会议名称 */
    @Schema(description = "会议名称")
    @Excel(name = "会议名称")
    private String conferenceName;

    /** 与会者民 */
    @Schema(description = "与会者民")
    @Excel(name = "与会者民")
    private String participantName;

    /** 选中 */
    @Schema(description = "选中")
    @Excel(name = "选中")
    private Integer choose;

    /** 点名 */
    @Schema(description = "点名")
    @Excel(name = "点名")
    private Integer calltheroll;

    /** 会议数字ID */
    @Schema(description = "会议数字ID")
    @Excel(name = "会议数字ID")
    private String conferenceNumber;

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }
    public void setConferenceName(String conferenceName)
    {
        this.conferenceName = conferenceName;
    }

    public String getConferenceName()
    {
        return conferenceName;
    }
    public void setParticipantName(String participantName)
    {
        this.participantName = participantName;
    }

    public String getParticipantName()
    {
        return participantName;
    }
    public void setChoose(Integer choose)
    {
        this.choose = choose;
    }

    public Integer getChoose()
    {
        return choose;
    }
    public void setCalltheroll(Integer calltheroll)
    {
        this.calltheroll = calltheroll;
    }

    public Integer getCalltheroll()
    {
        return calltheroll;
    }
    public void setConferenceNumber(String conferenceNumber)
    {
        this.conferenceNumber = conferenceNumber;
    }

    public String getConferenceNumber()
    {
        return conferenceNumber;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("conferenceName", getConferenceName())
                .append("participantName", getParticipantName())
                .append("choose", getChoose())
                .append("calltheroll", getCalltheroll())
                .append("createTime", getCreateTime())
                .append("conferenceNumber", getConferenceNumber())
                .toString();
    }
}