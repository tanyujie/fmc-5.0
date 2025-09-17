package com.paradisecloud.smc3.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2023/10/20 11:44
 */
@Data
@NoArgsConstructor
public class TemplateNode {
    private String templateId;
    private String subject;
    private String conferenceType="AVC";
    private String parentTemplateId;
}
