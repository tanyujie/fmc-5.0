package com.paradisecloud.fcm.service.conference.cascade;

import com.paradisecloud.fcm.common.enumer.McuType;

import java.util.Map;

/**
 * @author nj
 * @date 2023/8/2 15:35
 */
public class SmcProcessor implements Processor{

    private AbstractConference conference;
    private CascadeTemplate template;
    private Boolean isMainMcu;


    public SmcProcessor(AbstractConference conference, CascadeTemplate template) {
        this.conference = conference;
        this.template = template;
    }

    @Override
    public void execute(ProcessContext processContext) {
        conference.startConference(McuType.SMC3, template.getTemplateId());
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
            conference.processCascade("SMC级联",s,null);
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
