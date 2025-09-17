package com.paradisecloud.smc3.model.request;

import com.paradisecloud.com.fcm.smc.modle.SmcConferenceTemplate;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 */
@NoArgsConstructor
@Data
public class CasecadeTemplateRequest {

    private String id;
    private String subject;
    private String chairmanPassword;
    private String guestPassword;
    private Integer duration;
    private String organizationId;
    private SmcConferenceTemplate.ConferencePolicySettingDTO conferencePolicySetting;
    private List<TemplateNode> templateNodes;
    private SmcConferenceTemplate.ConfPresetParamDTO confPresetParam;
    private Boolean showSecurityLevel;
}
