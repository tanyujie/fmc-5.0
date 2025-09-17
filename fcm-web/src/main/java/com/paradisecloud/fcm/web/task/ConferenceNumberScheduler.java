package com.paradisecloud.fcm.web.task;

import com.paradisecloud.fcm.dao.mapper.ViewTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiConferenceNumber;
import com.paradisecloud.fcm.dao.model.ViewTemplateConference;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberService;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConferenceNumberScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(ConferenceNumberScheduler.class);

    /**
     * 每天2点19分处理会议号
     */
    @Scheduled(cron = "0 19 2 * * ?")
    public void dealConferenceNumber() {
        IBusiConferenceNumberService busiConferenceNumberService = BeanFactory.getBean(IBusiConferenceNumberService.class);
        ViewTemplateConferenceMapper viewTemplateConferenceMapper = BeanFactory.getBean(ViewTemplateConferenceMapper.class);
        BusiConferenceNumber busiConferenceNumberCon = new BusiConferenceNumber();
        busiConferenceNumberCon.setCreateType(1);
        List<BusiConferenceNumber> busiConferenceNumberList = busiConferenceNumberService.selectBusiConferenceNumberList(busiConferenceNumberCon);
        for (BusiConferenceNumber busiConferenceNumber : busiConferenceNumberList) {
            if (busiConferenceNumber.getStatus() != null && busiConferenceNumber.getStatus() == 1) {
                busiConferenceNumberService.deleteBusiConferenceNumberById(busiConferenceNumber.getId());
            } else {
                ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
                viewTemplateConferenceCon.setConferenceNumber(busiConferenceNumber.getId());
                List<ViewTemplateConference> viewTemplateConferenceList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCon);
                if (viewTemplateConferenceList.size() == 0 ) {
                    busiConferenceNumberService.deleteBusiConferenceNumberById(busiConferenceNumber.getId());
                }
            }
        }
    }
}
