package com.paradisecloud.smc3.task;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc3TemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplateConference;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.model.SmcConferenceTemplateQuery;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Smc3DeleteConferenceTemplateTask extends Smc3DelayTask  {

    private static final Logger LOGGER = LoggerFactory.getLogger(Smc3DeleteConferenceTemplateTask.class);

    public Smc3DeleteConferenceTemplateTask(String id, long delayInMilliseconds) {
        super("delete_smc3_template_" + id, delayInMilliseconds);
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        LOGGER.info("==删除SMC3会议模板任务开始");
        BusiMcuSmc3TemplateConferenceMapper busiMcuSmc3TemplateConferenceMapper = BeanFactory.getBean(BusiMcuSmc3TemplateConferenceMapper.class);
        Smc3Bridge smc3Bridge = Smc3BridgeCache.getInstance().getBridgesByDept(1l);
        {
            BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConferenceCon = new BusiMcuSmc3TemplateConference();
            busiMcuSmc3TemplateConferenceCon.setCategory("CASCADE");
            List<BusiMcuSmc3TemplateConference> busiMcuSmc3TemplateConferencesList = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceList(busiMcuSmc3TemplateConferenceCon);
            for (BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference : busiMcuSmc3TemplateConferencesList) {
                if (StringUtils.isNotEmpty(busiMcuSmc3TemplateConference.getCascadeId())) {
                    smc3Bridge.getSmcConferencesTemplateInvoker().deleteCascadeTemplate(busiMcuSmc3TemplateConference.getCascadeId(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }
                if (StringUtils.isNotEmpty(busiMcuSmc3TemplateConference.getSmcTemplateId())) {
                    smc3Bridge.getSmcConferencesTemplateInvoker().deleteConferencesTemplate(busiMcuSmc3TemplateConference.getSmcTemplateId(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }
            }
        }
        {
            BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConferenceCon = new BusiMcuSmc3TemplateConference();
            busiMcuSmc3TemplateConferenceCon.setCategory("NORMAL");
            List<BusiMcuSmc3TemplateConference> busiMcuSmc3TemplateConferencesList = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceList(busiMcuSmc3TemplateConferenceCon);
            for (BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference : busiMcuSmc3TemplateConferencesList) {
                if (StringUtils.isNotEmpty(busiMcuSmc3TemplateConference.getSmcTemplateId())) {
                    smc3Bridge.getSmcConferencesTemplateInvoker().deleteConferencesTemplate(busiMcuSmc3TemplateConference.getSmcTemplateId(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }
            }
        }
        Map<String, Object> paramsMap = new HashMap<>();
        String s = smc3Bridge.getSmcConferencesTemplateInvoker().queryConferencesTemplate(JSON.toJSONString(paramsMap), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        SmcConferenceTemplateQuery smcConferenceTemplateQuery = JSON.parseObject(s, SmcConferenceTemplateQuery.class);
        if (smcConferenceTemplateQuery != null && smcConferenceTemplateQuery.getContent() != null) {
            for (SmcConferenceTemplateQuery.Content content : smcConferenceTemplateQuery.getContent()) {
                BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceBySmcTemplateId(content.getId());
                if (busiMcuSmc3TemplateConference == null) {
                    smc3Bridge.getSmcConferencesTemplateInvoker().deleteConferencesTemplate(content.getId(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }
            }
        }
    }
}
