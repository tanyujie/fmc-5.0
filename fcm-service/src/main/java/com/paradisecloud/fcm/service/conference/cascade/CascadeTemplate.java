package com.paradisecloud.fcm.service.conference.cascade;

import com.paradisecloud.fcm.common.enumer.McuType;

/**
 * @author nj
 * @date 2023/8/2 11:05
 */
public class CascadeTemplate {

    private McuType mcuType;

    private Long templateId;

    private CascadeTemplate parentTemplate;

    public McuType getMcuType() {
        return mcuType;
    }

    public void setMcuType(McuType mcuType) {
        this.mcuType = mcuType;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public CascadeTemplate getParentTemplate() {
        return parentTemplate;
    }

    public void setParentTemplate(CascadeTemplate parentTemplate) {
        this.parentTemplate = parentTemplate;
    }
}
