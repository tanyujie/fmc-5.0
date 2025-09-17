package com.paradisecloud.smc.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author nj
 * @date 2022/10/25 9:49
 */
@Schema(description = "【请填写功能名称】")
public class BusiSmcConferenceState extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Integer id;

    /** smc会议id */
    @Schema(description = "smc会议id")
    @Excel(name = "smc会议id")
    private String conferenceId;

    /** 选看者id */
    @Schema(description = "选看者id")
    @Excel(name = "选看者id")
    private String chooseid;

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }
    public void setConferenceId(String conferenceId)
    {
        this.conferenceId = conferenceId;
    }

    public String getConferenceId()
    {
        return conferenceId;
    }
    public void setChooseid(String chooseid)
    {
        this.chooseid = chooseid;
    }

    public String getChooseid()
    {
        return chooseid;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("conferenceId", getConferenceId())
                .append("chooseid", getChooseid())
                .append("createTime", getCreateTime())
                .toString();
    }
}