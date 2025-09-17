package com.paradisecloud.fcm.smc2.service.impl;

import com.alibaba.fastjson.util.TypeUtils;
import com.paradisecloud.com.fcm.smc.modle.SmcConferenceTemplate;
import com.paradisecloud.com.fcm.smc.modle.TemplateTerminal;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.model.BusiConferenceNumber;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;
import com.paradisecloud.fcm.dao.model.BusiFsbcServerDept;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberService;
import com.paradisecloud.fcm.smc2.cache.Smc2BridgeCache;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContextCache;
import com.paradisecloud.fcm.dao.mapper.BusiSmc2DeptTemplateMapper;
import com.paradisecloud.fcm.dao.mapper.BusiSmc2TemplateTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiSmc2DeptTemplate;
import com.paradisecloud.fcm.dao.model.BusiSmc2TemplateTerminal;
import com.paradisecloud.fcm.smc2.model.BusiSmc2TemplateConferenceRequest;
import com.paradisecloud.fcm.smc2.cache.Smc2Bridge;
import com.paradisecloud.fcm.smc2.service.IBusiSmc2DeptTemplateService;
import com.paradisecloud.fcm.smc2.task.Smc2DelayTaskService;
import com.paradisecloud.fcm.smc2.utils.AesEnsUtils;
import com.paradisecloud.fcm.smc2.utils.RandomUtil;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.DeptFsbcMappingCache;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.paradisecloud.fcm.terminal.service.interfaces.IBusiTerminalService;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import com.suntek.smc.esdk.ServiceFactoryEx;
import com.suntek.smc.esdk.pojo.local.AddConfTemplateEx;
import com.suntek.smc.esdk.pojo.local.ConfTempplateParam;
import com.suntek.smc.esdk.pojo.local.DelConfTemplateEx;
import com.suntek.smc.esdk.pojo.local.TPSDKResponseEx;
import com.suntek.smc.esdk.pojo.smc.*;
import com.suntek.smc.esdk.service.client.TemplateServiceEx;
import io.jsonwebtoken.lang.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * 部门smc模板关联Service业务层处理
 *
 * @author lilinhai
 * @date 2023-04-19
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BusiSmc2DeptTemplateServiceImpl implements IBusiSmc2DeptTemplateService {
    public static final int INT = 1347444749;
    public static final int INT1 = 1347424258;
    private Logger log= LoggerFactory.getLogger(getClass());
    @Resource
    private BusiSmc2DeptTemplateMapper busiSmc2DeptTemplateMapper;

    @Resource
    private IBusiTerminalService iBusiTerminalService;
    @Resource
    private BusiSmc2TemplateTerminalMapper busiSmc2TemplateTerminalMapper;
    @Resource
    private IBusiConferenceNumberService busiConferenceNumberService;
    @Resource
    private Smc2DelayTaskService smc2DelayTaskService;
    /**
     * 查询部门smc模板关联
     *
     * @param id 部门smc模板关联ID
     * @return 部门smc模板关联
     */
    @Override
    public BusiSmc2DeptTemplate selectBusiSmc2DeptTemplateById(Integer id) {
        return busiSmc2DeptTemplateMapper.selectBusiSmc2DeptTemplateById(id);
    }

    /**
     * 查询部门smc模板关联列表
     *
     * @param busiSmc2DeptTemplate 部门smc模板关联
     * @return 部门smc模板关联
     */
    @Override
    public List<BusiSmc2DeptTemplate> selectBusiSmc2DeptTemplateList(BusiSmc2DeptTemplate busiSmc2DeptTemplate) {
        return busiSmc2DeptTemplateMapper.selectBusiSmc2DeptTemplateList(busiSmc2DeptTemplate);
    }

    /**
     * 新增部门smc模板关联
     *
     * @param busiSmc2DeptTemplate 部门smc模板关联
     * @return 结果
     */
    @Override
    public SmcConferenceTemplate insertBusiSmc2DeptTemplate(BusiSmc2DeptTemplate busiSmc2DeptTemplate, List<TemplateTerminal> templateTerminalList) {
        Assert.notNull(busiSmc2DeptTemplate.getTemplateName(), "会议名称不能为空！");
        Assert.notNull(busiSmc2DeptTemplate.getType(), "会议模板创建类型不能为空！");
       // Assert.notNull(busiSmc2DeptTemplate.getAccessCode(), "会议号码不能为空！");]
        if(busiSmc2DeptTemplate.getAccessCode()==null){
            busiSmc2DeptTemplate.setAccessCode(RandomUtil.getRandom());
        }
        // 绑定终端归属部门
        Long deptId=null;
        if (busiSmc2DeptTemplate.getDeptId() != null) {
            deptId=busiSmc2DeptTemplate.getDeptId().longValue();
        }
        if (deptId == null) {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            deptId = loginUser.getUser().getDeptId();
            if(deptId==null){
                deptId=1L;
            }
            busiSmc2DeptTemplate.setDeptId(deptId.intValue());
        }
        if (busiSmc2DeptTemplate.getDeptId() == null) {
            throw new SystemException(1004543, "添加会议模板，部门ID不能为空！");
        }
        if (busiSmc2DeptTemplate.getCreatUser() == null) {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null) {
                busiSmc2DeptTemplate.setCreatUser(loginUser.getUser().getUserName());
            }
        }
        busiSmc2DeptTemplate.setCreateTime(new Date());
        int c = busiSmc2DeptTemplateMapper.insertBusiSmc2DeptTemplate(busiSmc2DeptTemplate);
        List<ScheduleSiteParam2> templateParticipants = new ArrayList<>();
        Integer masterTerminalId = busiSmc2DeptTemplate.getMasterTerminalId();

        if (c > 0) {
            terminalParticipantCreate(busiSmc2DeptTemplate, templateTerminalList, templateParticipants, masterTerminalId);

            AddConfTemplateEx addConfTemplateEx = getAddConfTemplateEx(busiSmc2DeptTemplate, templateParticipants);

            Smc2Bridge smc2Bridge = Smc2BridgeCache.getInstance().getAvailableBridgesByDept(deptId.intValue());
            if(smc2Bridge==null){
                throw new SystemException(1004543, "添加会议模板失败,未找到MCU");
            }
            TemplateServiceEx templateServiceEx = smc2Bridge.getTemplateServiceEx();
            TPSDKResponseEx<Long> longTPSDKResponseEx =
                    templateServiceEx.addConfTemplateEx(addConfTemplateEx);
            int resultCode = longTPSDKResponseEx.getResultCode();
            if (resultCode == 0) {
                SmcConferenceTemplate smcConferenceTemplate = new SmcConferenceTemplate();
                smcConferenceTemplate.setId(longTPSDKResponseEx.getResult().intValue() + "");
                smcConferenceTemplate.setSubject(busiSmc2DeptTemplate.getTemplateName());
                smcConferenceTemplate.setDuration(busiSmc2DeptTemplate.getDuration());
                smcConferenceTemplate.setChairmanPassword(busiSmc2DeptTemplate.getChairmanPassword());
                if (busiSmc2DeptTemplate.getMasterTerminalId() != null) {
                    smcConferenceTemplate.setMasterTerminalId(busiSmc2DeptTemplate.getMasterTerminalId());
                }
                smcConferenceTemplate.setTemplateTerminalList(templateTerminalList);
                busiSmc2DeptTemplate.setSmc2TemplateId(longTPSDKResponseEx.getResult().intValue());
                busiSmc2DeptTemplateMapper.updateBusiSmc2DeptTemplate(busiSmc2DeptTemplate);
                return smcConferenceTemplate;
            } else {
                if(resultCode== INT){
                    throw new SystemException(1004543, "添加会议模板失败，MCU错误:会议号重复" );
                }
                throw new SystemException(1004543, "添加会议模板失败，MCU错误" + resultCode);
            }

        }
        return null;
    }


    @Override
    public SmcConferenceTemplate updateBusiSmc2DeptTemplate(BusiSmc2DeptTemplate busiSmc2DeptTemplate, List<TemplateTerminal> templateTerminalList) {
        Assert.notNull(busiSmc2DeptTemplate.getTemplateName(), "会议名称不能为空！");
        Assert.notNull(busiSmc2DeptTemplate.getType(), "会议模板创建类型不能为空！");
        // Assert.notNull(busiSmc2DeptTemplate.getAccessCode(), "会议号码不能为空！");]
        if(busiSmc2DeptTemplate.getAccessCode()==null){
            busiSmc2DeptTemplate.setAccessCode(RandomUtil.getRandom());
        }
        // 绑定终端归属部门
        Long deptId=null;
        if (busiSmc2DeptTemplate.getDeptId() != null) {
            deptId=busiSmc2DeptTemplate.getDeptId().longValue();
        }
        if (deptId == null) {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            deptId = loginUser.getUser().getDeptId();
            if(deptId==null){
                deptId=1L;
            }
            busiSmc2DeptTemplate.setDeptId(deptId.intValue());
        }
        if (busiSmc2DeptTemplate.getDeptId() == null) {
            throw new SystemException(1004543, "添加会议模板，部门ID不能为空！");
        }
        if (busiSmc2DeptTemplate.getCreatUser() == null) {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null) {
                busiSmc2DeptTemplate.setCreatUser(loginUser.getUser().getUserName());
            }
        }
        busiSmc2DeptTemplate.setUpdateTime(new Date());
        int c = busiSmc2DeptTemplateMapper.updateBusiSmc2DeptTemplate(busiSmc2DeptTemplate);
        List<ScheduleSiteParam2> templateParticipants = new ArrayList<>();
        Integer masterTerminalId = busiSmc2DeptTemplate.getMasterTerminalId();

        if (c > 0) {
            terminalParticipantCreate(busiSmc2DeptTemplate, templateTerminalList, templateParticipants, masterTerminalId);
            com.suntek.smc.esdk.pojo.local.EditConfTemplateEx editConfTemplateEx = getEditConfTemplateEx(busiSmc2DeptTemplate, templateParticipants);
            Smc2Bridge smc2Bridge = Smc2BridgeCache.getInstance().getAvailableBridgesByDept(deptId.intValue());
            if(smc2Bridge==null){
                throw new SystemException(1004543, "添加会议模板失败,未找到MCU");
            }
            TemplateServiceEx templateServiceEx = smc2Bridge.getTemplateServiceEx();
            TPSDKResponseEx<Integer> integerTPSDKResponseEx = templateServiceEx.editConfTemplateEx(editConfTemplateEx);
            int resultCode = integerTPSDKResponseEx.getResultCode();
            if (resultCode == 0) {
                SmcConferenceTemplate smcConferenceTemplate = new SmcConferenceTemplate();
                smcConferenceTemplate.setId(busiSmc2DeptTemplate.getId() + "");
                smcConferenceTemplate.setSubject(busiSmc2DeptTemplate.getTemplateName());
                smcConferenceTemplate.setDuration(busiSmc2DeptTemplate.getDuration());
                smcConferenceTemplate.setChairmanPassword(busiSmc2DeptTemplate.getChairmanPassword());
                if (busiSmc2DeptTemplate.getMasterTerminalId() != null) {
                    smcConferenceTemplate.setMasterTerminalId(busiSmc2DeptTemplate.getMasterTerminalId());
                }
                smcConferenceTemplate.setTemplateTerminalList(templateTerminalList);
                busiSmc2DeptTemplateMapper.updateBusiSmc2DeptTemplate(busiSmc2DeptTemplate);
                return smcConferenceTemplate;
            } else {
                throw new SystemException(1004543, "修改模板失败，MCU错误" + resultCode);
            }

        }
        return null;
    }

    private AddConfTemplateEx getAddConfTemplateEx(BusiSmc2DeptTemplate busiSmc2DeptTemplate, List<ScheduleSiteParam2> templateParticipants) {
        // 新建一个AddConfTemplateEx对象
        AddConfTemplateEx addConfTemplateEx = new AddConfTemplateEx();
        // 初始化内容
        addConfTemplateEx.setOrgId("1");
        ConfTempplateParam confTempplateParam = new ConfTempplateParam();
        confTempplateParam.setConfTemplateId(Long.valueOf(busiSmc2DeptTemplate.getId().longValue()));
        confTempplateParam.setAccessCode(busiSmc2DeptTemplate.getAccessCode());
        confTempplateParam.setName(busiSmc2DeptTemplate.getTemplateName());
        TimeSpanEx timeSpanEx = new TimeSpanEx();
        timeSpanEx.setTimeSpan("PTOM");
        confTempplateParam.setDuration(timeSpanEx);
        confTempplateParam.setCPResource(4);
        Integer rate = busiSmc2DeptTemplate.getRate();
        confTempplateParam.setRate(rate + " kbit/s");
        confTempplateParam.setConfTemptype(ConfType.ADHOC_CONF);
        confTempplateParam.setMediaEncryptType(MediaEncryptType.AUTO_ENCRYPT);
        confTempplateParam.setIsLiveBroadcast(busiSmc2DeptTemplate.getSupportLive() == null ? false : (busiSmc2DeptTemplate.getSupportLive() == 1 ? true : false));
        confTempplateParam.setIsRecording(busiSmc2DeptTemplate.getSupportRecord() == null ? false : (busiSmc2DeptTemplate.getSupportRecord() == 1 ? true : false));
        confTempplateParam.setPresentation(PresentationType.PRESENTATION);
        confTempplateParam.setMaxSitesCount(busiSmc2DeptTemplate.getMaxParticipantNum());
        confTempplateParam.setState(ConfTemplateRegState.REG_GK_SUCCESS);
        confTempplateParam.setMainMcuId(0L);
        confTempplateParam.setIsDataConference(false);
        confTempplateParam.setSites(templateParticipants);
        confTempplateParam.setConfMediaType(ConfStreamType.VIP);
        confTempplateParam.setCpMode(ContinuousPresenceMode.CP_4_1);
        confTempplateParam.setIsSpecCTConf(false);
        confTempplateParam.setPassword(busiSmc2DeptTemplate.getPassword());
        confTempplateParam.setChairmanPassword(busiSmc2DeptTemplate.getChairmanPassword());
        addConfTemplateEx.setConfTemplate(confTempplateParam);
        return addConfTemplateEx;
    }

    private  com.suntek.smc.esdk.pojo.local.EditConfTemplateEx getEditConfTemplateEx(BusiSmc2DeptTemplate busiSmc2DeptTemplate, List<ScheduleSiteParam2> templateParticipants) {
        // 新建一个AddConfTemplateEx对象
        com.suntek.smc.esdk.pojo.local.EditConfTemplateEx editConfTemplateEx = new com.suntek.smc.esdk.pojo.local.EditConfTemplateEx();
        // 初始化内容
        com.suntek.smc.esdk.pojo.local.ConfTempplateParam confTempplateParam = new com.suntek.smc.esdk.pojo.local.ConfTempplateParam();
        confTempplateParam.setConfTemplateId(Long.valueOf(busiSmc2DeptTemplate.getSmc2TemplateId().longValue()));
        confTempplateParam.setAccessCode(busiSmc2DeptTemplate.getAccessCode());
        confTempplateParam.setName(busiSmc2DeptTemplate.getTemplateName());
        TimeSpanEx timeSpanEx = new TimeSpanEx();
        timeSpanEx.setTimeSpan("PTOM");
        confTempplateParam.setDuration(timeSpanEx);
        confTempplateParam.setCPResource(4);
        Integer rate = busiSmc2DeptTemplate.getRate();
        confTempplateParam.setRate(rate + " kbit/s");
        confTempplateParam.setConfTemptype(ConfType.ADHOC_CONF);
        confTempplateParam.setMediaEncryptType(MediaEncryptType.AUTO_ENCRYPT);
        confTempplateParam.setIsLiveBroadcast(busiSmc2DeptTemplate.getSupportLive() == null ? false : (busiSmc2DeptTemplate.getSupportLive() == 1 ? true : false));
        confTempplateParam.setIsRecording(busiSmc2DeptTemplate.getSupportRecord() == null ? false : (busiSmc2DeptTemplate.getSupportRecord() == 1 ? true : false));
        confTempplateParam.setPresentation(PresentationType.PRESENTATION);
        confTempplateParam.setMaxSitesCount(busiSmc2DeptTemplate.getMaxParticipantNum());
        confTempplateParam.setState(ConfTemplateRegState.REG_GK_SUCCESS);
        confTempplateParam.setMainMcuId(0L);
        confTempplateParam.setIsDataConference(false);
        confTempplateParam.setSites(templateParticipants);
        confTempplateParam.setConfMediaType(ConfStreamType.VIP);
        confTempplateParam.setCpMode(ContinuousPresenceMode.CP_4_1);
        confTempplateParam.setIsSpecCTConf(false);
        confTempplateParam.setPassword(busiSmc2DeptTemplate.getPassword());
        confTempplateParam.setChairmanPassword(busiSmc2DeptTemplate.getChairmanPassword());
        editConfTemplateEx.setConfTemplate(confTempplateParam);
        return editConfTemplateEx;
    }


    private void terminalParticipantCreate(BusiSmc2DeptTemplate busiSmc2DeptTemplate, List<TemplateTerminal> templateTerminalList, List<ScheduleSiteParam2> templateParticipants, Integer masterTerminalId) {
        List<BusiSmc2TemplateTerminal> smcTemplateTerminalList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(templateTerminalList)) {

            for (TemplateTerminal templateTerminal : templateTerminalList) {

                BusiTerminal busiTerminal = iBusiTerminalService.selectBusiTerminalById(templateTerminal.getId());
                if (!Objects.isNull(busiTerminal)) {

                    ScheduleSiteParam2 scheduleSiteParam2 = new ScheduleSiteParam2();
                    String number = busiTerminal.getNumber();
                    scheduleSiteParam2.setName(busiTerminal.getName());
                    scheduleSiteParam2.setUri(number);
                    scheduleSiteParam2.setType(SiteType.SITE_TYPE_SIP);
                    scheduleSiteParam2.setFrom(SiteFrom.INTERNAL);
                    scheduleSiteParam2.setDialingMode(DialMode.DIAL_OUT);
                    scheduleSiteParam2.setRate("1920 kbit/s");
                    VideoParam videoParam = new VideoParam();
                    videoParam.setProtocol(VideoProtocol.AUTO);
                    videoParam.setFormat(VideoFormat.AUTO);
                    scheduleSiteParam2.setVideo(videoParam);
                    scheduleSiteParam2.setIsLockVideoSource(false);
                    scheduleSiteParam2.setParticipantType(ParticipantType.SITE);
                    scheduleSiteParam2.setIsChairMan(false);
                    if (Objects.equals(busiTerminal.getId(), masterTerminalId)) {
                        scheduleSiteParam2.setIsChairMan(true);
                    } else {
                        scheduleSiteParam2.setIsChairMan(false);
                    }

                    if (StringUtils.isNotBlank(busiTerminal.getIp()) && Strings.hasText(busiTerminal.getNumber())) {
                        scheduleSiteParam2.setUri(busiTerminal.getNumber() + "@" + busiTerminal.getIp());
                    }
                    if (TerminalType.isFSBC(busiTerminal.getType())) {
                        if (StringUtils.isNotBlank(busiTerminal.getIp())) {
                            scheduleSiteParam2.setUri(busiTerminal.getCredential() + "@" + busiTerminal.getIp());
                        } else {
                            BusiFsbcServerDept busiFsbcServerDept = DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId());
                            FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(busiFsbcServerDept.getFsbcServerId());
                            String callIp = fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                            scheduleSiteParam2.setUri(busiTerminal.getCredential() + "@" + callIp);
                        }
                    }
                    if (TerminalType.isFCMSIP(busiTerminal.getType())) {
                        BusiFreeSwitchDept busiFreeSwitchDept = DeptFcmMappingCache.getInstance().get(busiTerminal.getDeptId());
                        FcmBridge fcmBridge = FcmBridgeCache.getInstance().getById(busiFreeSwitchDept.getServerId());
                        String callIp = fcmBridge.getBusiFreeSwitch().getIp();;
                        scheduleSiteParam2.setUri(busiTerminal.getCredential() + "@" + callIp);
                    }
                   if (TerminalType.isSMCSIP(busiTerminal.getType()))
                    {
                        scheduleSiteParam2.setUri(busiTerminal.getNumber()+"@"+busiTerminal.getIp());
                    }
                    if (TerminalType.isSMCNUMBER(busiTerminal.getType()))
                    {
                        scheduleSiteParam2.setUri(busiTerminal.getNumber());
                    }
                    if (TerminalType.isCisco(busiTerminal.getType()))
                    {
                        scheduleSiteParam2.setUri(busiTerminal.getName()+ "@"+busiTerminal.getIp());
                    }
                    String s = UUID.randomUUID().toString();
                    String s1 = s.replaceAll("\\-", "");
                    if (scheduleSiteParam2.getUri()==null&&busiTerminal.getIp()!=null) {
                        scheduleSiteParam2.setUri(s1 + "@" + busiTerminal.getIp());
                    }
                    templateParticipants.add(scheduleSiteParam2);
                    //保存关系
                    BusiSmc2TemplateTerminal smcTemplateTerminal = new BusiSmc2TemplateTerminal();
                    smcTemplateTerminal.setTerminalId(templateTerminal.getId().intValue());
                    smcTemplateTerminal.setTerminalDeptId(busiTerminal.getDeptId().intValue());
                    smcTemplateTerminal.setSmcnumber(busiSmc2DeptTemplate.getAccessCode());
                    smcTemplateTerminal.setSmc2TemplateId(busiSmc2DeptTemplate.getId());
                    smcTemplateTerminal.setParticipantId(s1);
                    smcTemplateTerminal.setAttendType(templateTerminal.getAttendType());
                    smcTemplateTerminal.setWeight(templateTerminal.getWeight());
                    smcTemplateTerminalList.add(smcTemplateTerminal);
                    busiSmc2TemplateTerminalMapper.insertBusiSmc2TemplateTerminal(smcTemplateTerminal);
                }

            }
        }
    }

    /**
     * 修改部门smc模板关联
     *
     * @param templateConferenceRequest 部门smc模板关联
     * @return 结果
     */
    @Override
    public int updateBusiSmc2DeptTemplate(BusiSmc2TemplateConferenceRequest templateConferenceRequest) {

        BusiSmc2DeptTemplate busiSmc2DeptTemplate = selectBusiSmc2DeptTemplateById(templateConferenceRequest.getId());
        if (busiSmc2DeptTemplate == null) {
            throw new CustomException("模板不存在");
        }
        if(org.apache.logging.log4j.util.Strings.isNotBlank(busiSmc2DeptTemplate.getConfid())){
            Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().encryptToHex(busiSmc2DeptTemplate.getConfid()));
            if(conferenceContext!=null&&conferenceContext.isStart()){
                throw new CustomException("会议进行中不能修改");
            }
        }
        Integer smc2TemplateId = busiSmc2DeptTemplate.getSmc2TemplateId();
        com.paradisecloud.common.utils.bean.BeanUtils.copyProperties(templateConferenceRequest, busiSmc2DeptTemplate);
        busiSmc2DeptTemplate.setTemplateName(templateConferenceRequest.getSubject());

        String accessCode = busiSmc2DeptTemplate.getAccessCode();
        if (org.apache.logging.log4j.util.Strings.isBlank(accessCode)) {
            BusiConferenceNumber busiConferenceNumber = busiConferenceNumberService.selectBusiConferenceNumberById(TypeUtils.castToLong(accessCode));
            if (busiConferenceNumber == null) {
                // 根据输入的号码初始化号码，再自动创建模板
                busiConferenceNumber = busiConferenceNumberService.autoCreateConferenceNumber(Long.valueOf(busiSmc2DeptTemplate.getDeptId().longValue()), McuType.SMC2.getCode());
                accessCode = busiConferenceNumber.getId().toString();
                busiSmc2DeptTemplate.setAccessCode(accessCode);
            }
        }
        busiSmc2DeptTemplate.setUpdateTime(new Date());
        busiSmc2TemplateTerminalMapper.deleteBusiSmc2TemplateTerminalByTemplateId(templateConferenceRequest.getId());
        SmcConferenceTemplate smcConferenceTemplate = updateBusiSmc2DeptTemplate(busiSmc2DeptTemplate, templateConferenceRequest.getTemplateTerminalList());
        if(smcConferenceTemplate==null){
            throw new CustomException("编辑失败");
        }
       // delayTaskService.addTask(new DeleteTemplateTask(busiSmc2DeptTemplate.getId()+"smc2",1000,smc2TemplateId.longValue()));

        return 0;

    }

    /**
     * 批量删除部门smc模板关联
     *
     * @param ids 需要删除的部门smc模板关联ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmc2DeptTemplateByIds(Integer[] ids) {
        return busiSmc2DeptTemplateMapper.deleteBusiSmc2DeptTemplateByIds(ids);
    }

    /**
     * 删除部门smc模板关联信息
     *
     * @param id 部门smc模板关联ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteBusiSmc2DeptTemplateById(Integer id) {
        BusiSmc2DeptTemplate busiSmc2DeptTemplate = busiSmc2DeptTemplateMapper.selectBusiSmc2DeptTemplateById(id);
        if (busiSmc2DeptTemplate == null) {
            throw new CustomException("模板不存在");
        }
        if(org.apache.logging.log4j.util.Strings.isNotBlank(busiSmc2DeptTemplate.getConfid())){
            Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().encryptToHex(busiSmc2DeptTemplate.getConfid()));
            if(conferenceContext!=null&&conferenceContext.isStart()){
                throw new CustomException("会议进行中不能删除");
            }
        }
        DelConfTemplateEx delConfTemplateEx = new DelConfTemplateEx();
        delConfTemplateEx.setConfTemplateId(busiSmc2DeptTemplate.getSmc2TemplateId().longValue());
        TemplateServiceEx service = ServiceFactoryEx.getService(TemplateServiceEx.class);
        TPSDKResponseEx<Integer> integerTPSDKResponseEx =
                service.delConfTemplateEx(delConfTemplateEx);
        log.info("删除模板："+"本地模板ID:"+id+"删除结果："+integerTPSDKResponseEx.getResultCode());
        if (integerTPSDKResponseEx.getResultCode() == 0||integerTPSDKResponseEx.getResultCode()== INT1) {
            busiSmc2TemplateTerminalMapper.deleteBusiSmc2TemplateTerminalByTemplateId(id);
            return busiSmc2DeptTemplateMapper.deleteBusiSmc2DeptTemplateById(id);
        } else {
            throw new CustomException("删除失败");
        }
    }

    @Override
    public List<BusiSmc2DeptTemplate> queryTemplateListByDeptId(Long deptId) {
        return busiSmc2DeptTemplateMapper.selectBusiSmc2DeptTemplateListNotInAppointMent(deptId==null?null:deptId.intValue());
    }
}
