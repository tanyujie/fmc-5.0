package com.paradisecloud.fcm.huaweicloud.huaweicloud.cloud;


import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudTemplateConference;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudBridge;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudBridgeCache;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiMcuHwcloudTemplateConferenceService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.templateConference.StartTemplateConference;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
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
public class HuaWeiCloudMeetingService {

    @Resource
    private IBusiMcuHwcloudTemplateConferenceService busiMcuHwcloudTemplateConferenceService;



    public BaseConferenceContext createMeeting(String name, int duration) {

        Map<Long, HwcloudBridge> tencentBridgeMap = HwcloudBridgeCache.getInstance().getHwcloudBridgeMap();

        Collection<HwcloudBridge> values = tencentBridgeMap.values();
        for (HwcloudBridge value : values) {

            BusiMcuHwcloudTemplateConference busiTemplateConference = new BusiMcuHwcloudTemplateConference();
            busiTemplateConference.setName(name);
            busiTemplateConference.setBusinessFieldType(100);
            busiTemplateConference.setChairmanPassword("");
            busiTemplateConference.setDeptId(1L);
            if(duration!=0){
                busiTemplateConference.setDurationTime(duration);
            }
            int c = 0;
            try {
                c = busiMcuHwcloudTemplateConferenceService.insertBusiTemplateConference(busiTemplateConference, null, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (c > 0) {
                HwcloudConferenceContext tencentConferenceContext = null;
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
