package com.paradisecloud.fcm.tencent.cloud;


import com.paradisecloud.fcm.dao.model.BusiMcuTencentTemplateConference;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.tencent.cache.TencentBridge;
import com.paradisecloud.fcm.tencent.cache.TencentBridgeCache;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContext;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiMcuTencentTemplateConferenceService;
import com.paradisecloud.fcm.tencent.templateConference.StartTemplateConference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Map;

/**
 * @author nj
 * @date 2024/7/16 9:50
 */

@Service
@Slf4j
public class TencentCloudMeetingService {

    @Resource
    private IBusiMcuTencentTemplateConferenceService busiMcuTencentTemplateConferenceService;



    public BaseConferenceContext createMeeting(String name, int duration) {

        Map<Long, TencentBridge> tencentBridgeMap = TencentBridgeCache.getInstance().getTencentBridgeMap();

        Collection<TencentBridge> values = tencentBridgeMap.values();
        for (TencentBridge value : values) {

            //TODO 云资源查询

            BusiMcuTencentTemplateConference busiTemplateConference = new BusiMcuTencentTemplateConference();
            busiTemplateConference.setName(name);
            busiTemplateConference.setBusinessFieldType(100);
            busiTemplateConference.setChairmanPassword("");
            busiTemplateConference.setDeptId(1L);
            if(duration!=0){
                busiTemplateConference.setDurationTime(duration);
            }
            int c = 0;
            try {
                c = busiMcuTencentTemplateConferenceService.insertBusiTemplateConference(busiTemplateConference, null, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (c > 0) {
                TencentConferenceContext tencentConferenceContext = null;
                try {
                    tencentConferenceContext = new StartTemplateConference().startTemplateConference(busiTemplateConference.getId(), value);
                    if (tencentConferenceContext != null) {
                        return tencentConferenceContext;
                    }
                } catch (Exception e) {
                    log.info("云资源错误"+e.getMessage());
                }

            }
        }

        return null;
    }

}
