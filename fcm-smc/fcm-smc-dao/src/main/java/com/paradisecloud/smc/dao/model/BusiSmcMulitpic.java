package com.paradisecloud.smc.dao.model;

import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author nj
 * @date 2022/10/20 14:32
 */
@Schema(description = "【请填写功能名称】")
public class BusiSmcMulitpic extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Integer id;

    /** smc会议id */
    @Schema(description = "smc会议id")
    @Excel(name = "smc会议id")
    private String conferenceId;

    /** 布局 */
    @Schema(description = "布局")
    @Excel(name = "布局")
    private String mulitpic;

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
    public void setMulitpic(String mulitpic)
    {
        this.mulitpic = mulitpic;
    }

    public String getMulitpic()
    {
        return mulitpic;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("conferenceId", getConferenceId())
                .append("mulitpic", getMulitpic())
                .toString();
    }
}