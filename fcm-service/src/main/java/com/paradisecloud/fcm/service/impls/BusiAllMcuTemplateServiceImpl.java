package com.paradisecloud.fcm.service.impls;

import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.UpCascadeType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.interfaces.IBusiAllMcuTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BusiAllMcuTemplateServiceImpl implements IBusiAllMcuTemplateService {

    protected final Logger logger = LoggerFactory.getLogger(BusiAllMcuTemplateServiceImpl.class);

    @Resource
    private ViewTemplateConferenceMapper viewTemplateConferenceMapper;
    @Resource
    private BusiTemplateConferenceMapper busiTemplateConferenceMapper;
    @Resource
    private BusiMcuZjTemplateConferenceMapper busiMcuZjTemplateConferenceMapper;
    @Resource
    private BusiMcuPlcTemplateConferenceMapper busiMcuPlcTemplateConferenceMapper;
    @Resource
    private BusiMcuKdcTemplateConferenceMapper busiMcuKdcTemplateConferenceMapper;
    @Resource
    private BusiMcuSmc3TemplateConferenceMapper busiMcuSmc3TemplateConferenceMapper;
    @Resource
    private BusiMcuSmc2TemplateConferenceMapper busiMcuSmc2TemplateConferenceMapper;

    @Override
    public void deleteAllMcuTemplate(Long templateId, String mcuType) {

        ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
        viewTemplateConferenceCascadeCon.setUpCascadeId(templateId);
        viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType);
        List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
        for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
            try {
                if (viewTemplateConference.getUpCascadeType() == UpCascadeType.AUTO_CREATE.getCode()) {
                    deleteAllMcuTemplate(viewTemplateConference.getId(), mcuType);
                } else {
                    recoverUpdateCascadeTemplateConference(viewTemplateConference);
                }
            } catch (Exception e1) {
                logger.error(e1.getMessage());
            }
        }

    }

    private void recoverUpdateCascadeTemplateConference(ViewTemplateConference viewTemplateConference) {
        Long templateId = viewTemplateConference.getId();
        String mcuTypeStr = viewTemplateConference.getMcuType();
        McuType mcuType = McuType.convert(mcuTypeStr);
        switch (mcuType) {
            case FME: {
                BusiTemplateConference busiTemplateConferenceUpdate = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiTemplateConferenceMapper.updateBusiTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_ZJ: {
                BusiMcuZjTemplateConference busiTemplateConferenceUpdate = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_PLC: {
                BusiMcuPlcTemplateConference busiTemplateConferenceUpdate = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuPlcTemplateConferenceMapper.updateBusiMcuPlcTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_KDC: {
                BusiMcuKdcTemplateConference busiTemplateConferenceUpdate = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuKdcTemplateConferenceMapper.updateBusiMcuKdcTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case SMC3: {
                BusiMcuSmc3TemplateConference busiTemplateConferenceUpdate = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case SMC2: {
                BusiMcuSmc2TemplateConference busiTemplateConferenceUpdate = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuSmc2TemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
        }
        BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(viewTemplateConference.getConferenceId()));
        if (conferenceContext != null) {
            conferenceContext.setUpCascadeConferenceId(null);
            conferenceContext.setUpCascadeIndex(null);
            conferenceContext.setUpCascadeRemoteParty(null);
        }
    }

}
