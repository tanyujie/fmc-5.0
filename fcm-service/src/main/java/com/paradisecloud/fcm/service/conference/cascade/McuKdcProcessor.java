package com.paradisecloud.fcm.service.conference.cascade;

import com.paradisecloud.fcm.common.enumer.McuType;

import java.util.Map;

public class McuKdcProcessor implements Processor {

    private AbstractConference conference;
    private CascadeTemplate template;
    private Boolean isMainMcu;


    public McuKdcProcessor(AbstractConference conference, CascadeTemplate template) {
        this.conference = conference;
        this.template = template;
    }

    @Override
    public void execute(ProcessContext processContext) {
        conference.startConference(McuType.MCU_KDC, template.getTemplateId());
        String uri = conference.getUri();
        Map<Long, String> uriMap = processContext.getUriMap();
        uriMap.put(template.getTemplateId(),uri);
        CascadeTemplate parentTemplate = template.getParentTemplate();
        if(parentTemplate==null){
            this.isMainMcu=true;
            processContext.setMainUri(conference.getUri());
            processContext.setMainConferenceId(conference.getConferenceId());
        }else {
            String s = uriMap.get(parentTemplate.getTemplateId());
            conference.processCascade("MCU_KDC级联",s,"");
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
