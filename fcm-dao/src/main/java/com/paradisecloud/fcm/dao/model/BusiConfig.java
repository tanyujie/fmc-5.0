package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 【请填写功能名称】对象 busi_config
 * 
 * @author lilinhai
 * @date 2022-09-10
 */
@Schema(description = "【请填写功能名称】")
public class BusiConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 参数主键 */
    @Schema(description = "参数主键")
    private Integer configId;

    /** 参数名称 */
    @Schema(description = "参数名称")
    @Excel(name = "参数名称")
    private String configName;

    /** 参数键名 */
    @Schema(description = "参数键名")
    @Excel(name = "参数键名")
    private String configKey;

    /** 参数键值 */
    @Schema(description = "参数键值")
    @Excel(name = "参数键值")
    private String configValue;

    public void setConfigId(Integer configId) 
    {
        this.configId = configId;
    }

    public Integer getConfigId() 
    {
        return configId;
    }
    public void setConfigName(String configName) 
    {
        this.configName = configName;
    }

    public String getConfigName() 
    {
        return configName;
    }
    public void setConfigKey(String configKey) 
    {
        this.configKey = configKey;
    }

    public String getConfigKey() 
    {
        return configKey;
    }
    public void setConfigValue(String configValue)
    {
        this.configValue = configValue;
    }

    public String getConfigValue()
    {
        return configValue;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("configId", getConfigId())
            .append("configName", getConfigName())
            .append("configKey", getConfigKey())
            .append("configValue", getConfigValue())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
