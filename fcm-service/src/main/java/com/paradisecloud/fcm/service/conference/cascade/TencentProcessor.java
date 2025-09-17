package com.paradisecloud.fcm.service.conference.cascade;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.McuType;

import java.util.Map;

/**
 * @author nj
 * @date 2023/8/2 10:14
 */
public class TencentProcessor implements Processor {

    private AbstractConference conference;
    private CascadeTemplate template;
    private Boolean isMainMcu;

    public TencentProcessor(AbstractConference conference, CascadeTemplate template) {
        this.conference = conference;
        this.template = template;
    }

    @Override
    public void execute(ProcessContext processContext) {
        if (conference == null) {
            throw new CustomException("未找到TENCENT级联实现类");
        }
        conference.startConference(McuType.MCU_TENCENT, template.getTemplateId());
        String uri = conference.getUri();
        Map<Long, String> uriMap = processContext.getUriMap();
        uriMap.put(template.getTemplateId(),uri);
        CascadeTemplate parentTemplate = template.getParentTemplate();
        if (parentTemplate == null) {
            this.isMainMcu = true;
            processContext.setMainUri(uri);
            processContext.setMainConferenceId(conference.getConferenceId());
        } else {
            this.isMainMcu = false;
            AbstractConference mainConference = processContext.getMainConference();
            mainConference.processCascade("腾讯级联会议", uri,null);
        }

    }

    @Override
    public Boolean needExecute(ProcessContext processContext) {

        return true;
    }

    @Override
    public void end(ProcessContext processContext) {
        conference.end();
    }
}
