package com.paradisecloud.smc.service;

import com.paradisecloud.com.fcm.smc.modle.SmcConferenceTemplate;
import com.paradisecloud.com.fcm.smc.modle.StartConference;

/**
 * @author nj
 * @date 2022/8/15 10:21
 */
public interface TemplateService {
    /**
     * 创建会议模板
     * @param smcConferenceTemplate
     */

    SmcConferenceTemplate addTemplateRoom(SmcConferenceTemplate smcConferenceTemplate,Long deptId,long masterTerminalId);

    SmcConferenceTemplate addTemplateRoomSmc(SmcConferenceTemplate smcConferenceTemplate,Long deptId);

    String queryConferenceTemplates(String name, Long deptId);

    String deleteTemplateById(String id);

    String putTemplate(String id,SmcConferenceTemplate smcConferenceTemplate);

    String getTemplateById(String id);

    String startConferenceTemplate(StartConference startConference);
}
