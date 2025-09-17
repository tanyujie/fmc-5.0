package com.paradisecloud.fcm.service.conference.cascade;

import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.service.McuStrategyFactory;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.service.conference.utils.AllConferenceContextUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author nj
 * @date 2023/8/3 9:43
 */
public class ConferenceCascadeHandler {

    public static void start(List<CascadeTemplate> templates) {
        if (CollectionUtils.isEmpty(templates)) {
            return;
        }
        templates.stream().sorted((h1, h2) -> {
            if (h1.getParentTemplate() == null) {
                return h2.getParentTemplate() == null ? 0 : 1;
            } else if (h2.getParentTemplate() == null) {
                return -1;
            }
            return h1.getTemplateId().compareTo(h2.getTemplateId());
        });

        ProcessContext processContext = new ProcessContext();
        ProcessCascadeEngineImpl processCascadeEngine = new ProcessCascadeEngineImpl();
        for (CascadeTemplate template : templates) {
            AbstractConference abstractConference = McuStrategyFactory.getMcuStrategy(template.getMcuType());
            switch (template.getMcuType()) {
                case MCU_TENCENT:
                    TencentProcessor tencentProcessor = new TencentProcessor(abstractConference, template);
                    processCascadeEngine.addProcessor(tencentProcessor);
                    break;
                case SMC3:
                    SmcProcessor smcProcessor = new SmcProcessor(abstractConference, template);
                    processCascadeEngine.addProcessor(smcProcessor);
                    break;
                case SMC2:
                    Smc2Processor smc2Processor = new Smc2Processor(abstractConference, template);
                    processCascadeEngine.addProcessor(smc2Processor);
                    break;
                case FME:
                    FmeProcessor fmeProcessor = new FmeProcessor(abstractConference, template);
                    processCascadeEngine.addProcessor(fmeProcessor);
                    break;
                case MCU_ZJ:
                    McuZjProcessor zjProcessor = new McuZjProcessor(abstractConference, template);
                    processCascadeEngine.addProcessor(zjProcessor);
                    break;
                case MCU_PLC:
                    McuPlcProcessor mcuPlcProcessor = new McuPlcProcessor(abstractConference, template);
                    processCascadeEngine.addProcessor(mcuPlcProcessor);
                    break;
                case MCU_KDC:
                    McuKdcProcessor mcuKdcProcessor = new McuKdcProcessor(abstractConference, template);
                    processCascadeEngine.addProcessor(mcuKdcProcessor);
                    break;
            }

        }
        processCascadeEngine.start(processContext);
        ProcessContextCache.getInstance().add(processContext);
        processContext.setProcessCascadeEngine(processCascadeEngine);
    }

    public static void end(String conferenceId) {
        ProcessContext processContext = ProcessContextCache.getInstance().get(conferenceId);
        ProcessCascadeEngine processCascadeEngine = processContext.getProcessCascadeEngine();
        processCascadeEngine.end(processContext);
    }

    public static BaseConferenceContext startConference(String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        AbstractConference abstractConference = McuStrategyFactory.getMcuStrategy(mcuType);
        if (abstractConference != null) {
            return abstractConference.startConference(id);
        }
        return null;
    }

    public static BaseConferenceContext endConference(String conferenceId, int endType) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        AbstractConference abstractConference = McuStrategyFactory.getMcuStrategy(mcuType);
        if (abstractConference != null) {
            return abstractConference.endConference(conferenceId, endType);
        }
        return null;
    }

    /**
     * 获取会议详情
     * @param conferenceId
     * @return 会议上下文
     */
    public static BaseConferenceContext getCurrentConferenceInfo(String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        AbstractConference abstractConference = McuStrategyFactory.getMcuStrategy(mcuType);
        if (abstractConference != null) {
            return abstractConference.buildTemplateConferenceContext(id);
        }
        return null;
    }

    /**
     * 重呼
     *
     * @param conferenceId
     * @param attendeeId
     */
    public static void recall(String conferenceId, String attendeeId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        AbstractConference abstractConference = McuStrategyFactory.getMcuStrategy(mcuType);
        if (abstractConference != null) {
            abstractConference.recall(conferenceId, attendeeId);
        }
    }


    public static void invite(String conferenceId,String conferenceName, String tencentRemoteParty,String password) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        AbstractConference abstractConference = McuStrategyFactory.getMcuStrategy(mcuType);
        if (abstractConference != null) {
            abstractConference.invite(conferenceId,conferenceName, tencentRemoteParty,password);
        }
    }
    /**
     * 选看
     *
     * @param conferenceId
     * @param attendeeId
     */
    public static void chooseSee(String conferenceId, String attendeeId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        AbstractConference abstractConference = McuStrategyFactory.getMcuStrategy(mcuType);
        if (abstractConference != null) {
            abstractConference.chooseSee(conferenceId, attendeeId);
        }
    }

    public static void callTheRoll(String conferenceId, String attendeeId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        AbstractConference abstractConference = McuStrategyFactory.getMcuStrategy(mcuType);
        if (abstractConference != null) {
            abstractConference.callTheRoll(conferenceId, attendeeId);
        }
    }

    /**
     * 选看
     *
     * @param conferenceId
     * @param attendeeId
     * @param upCascadeOperate 是否为上级会议操作
     * @param upCascadeBroadcast 是否为上级会议广播
     * @param upCascadePolling 是否为上级会议轮询
     */
    public static void chooseSee(String conferenceId, String attendeeId, boolean upCascadeOperate, boolean upCascadeBroadcast, boolean upCascadePolling) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        AbstractConference abstractConference = McuStrategyFactory.getMcuStrategy(mcuType);
        if (abstractConference != null) {
            abstractConference.chooseSee(conferenceId, attendeeId, upCascadeOperate, upCascadeBroadcast, upCascadePolling, false);
        }
    }

    /**
     * 选看
     *
     * @param conferenceId
     * @param attendeeId
     * @param upCascadeRollCall 是否为上级点名
     */
    public static void chooseSee(String conferenceId, String attendeeId, boolean upCascadeRollCall) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        AbstractConference abstractConference = McuStrategyFactory.getMcuStrategy(mcuType);
        if (abstractConference != null) {
            abstractConference.chooseSee(conferenceId, attendeeId, true, false, false, upCascadeRollCall);
        }
    }

    /**
     * 默认选看
     *
     * @param conferenceId
     */
    public static void defaultChooseSee(String conferenceId) {
       defaultChooseSee(conferenceId, false);
    }

    /**
     * 默认选看
     *
     * @param conferenceId
     * @param upCascadeRollCall 是否为上级点名
     */
    public static void defaultChooseSee(String conferenceId, boolean upCascadeRollCall) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            BaseAttendee attendee = AllConferenceContextUtils.getDefaultChooseToSee(conferenceContext);
            if (attendee != null) {
                ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
                Long id = conferenceIdVo.getId();
                McuType mcuType = conferenceIdVo.getMcuType();
                AbstractConference abstractConference = McuStrategyFactory.getMcuStrategy(mcuType);
                if (abstractConference != null) {
                    abstractConference.chooseSee(conferenceId, attendee.getId(), true, false, false, upCascadeRollCall);
                }
            }
        }
    }


}
