package com.paradisecloud.fcm.fme.conference.impls;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONArray;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiTemplateConferenceService;
import com.paradisecloud.fcm.fme.conference.task.StatisticalConferenceTask;
import com.paradisecloud.fcm.service.minutes.MinutesFileHandler;
import com.paradisecloud.system.dao.mapper.SysUserMapper;
import com.paradisecloud.system.dao.model.SysUser;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiHistoryConferenceService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.system.dao.mapper.SysDeptMapper;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;

import javax.annotation.Resource;

/**
 * 历史会议，每次挂断会保存该历史记录Service业务层处理
 *
 * @author lilinhai
 * @date 2021-01-20
 */
@Transactional
@Service
public class BusiHistoryConferenceServiceImpl implements IBusiHistoryConferenceService {
    
    private Logger logger = LoggerFactory.getLogger(BusiHistoryConferenceServiceImpl.class);
    
    @Resource
    private BusiHistoryConferenceMapper busiHistoryConferenceMapper;
    @Resource
    private SysDeptMapper sysDeptMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private BusiHistoryParticipantMapper busiHistoryParticipantMapper;
    @Resource
    private BusiHistoryParticipantTerminalMapper busiHistoryParticipantTerminalMapper;
    @Resource
    private TaskService taskService;
    @Resource
    private BusiRecordsMapper busiRecordsMapper;

    /**
     * 查询历史会议，每次挂断会保存该历史记录
     *
     * @param id 历史会议，每次挂断会保存该历史记录ID
     * @return 历史会议，每次挂断会保存该历史记录
     */
    @Override
    public BusiHistoryConference selectBusiHistoryConferenceById(Long id) {
        return busiHistoryConferenceMapper.selectBusiHistoryConferenceById(id);
    }

    /**
     * 查询历史会议，每次挂断会保存该历史记录列表
     *
     * @param busiHistoryConference 历史会议，每次挂断会保存该历史记录
     * @return 历史会议，每次挂断会保存该历史记录
     */
    @Override
    public List<BusiHistoryConference> selectBusiHistoryConferenceList(BusiHistoryConference busiHistoryConference) {
        return busiHistoryConferenceMapper.selectBusiHistoryConferenceList(busiHistoryConference);
    }

    /**
     * 新增历史会议，每次挂断会保存该历史记录
     *
     * @param busiHistoryConference 历史会议，每次挂断会保存该历史记录
     * @return 结果
     */
    @Override
    public int insertBusiHistoryConference(BusiHistoryConference busiHistoryConference) {
        busiHistoryConference.setCreateTime(new Date());
        return busiHistoryConferenceMapper.insertBusiHistoryConference(busiHistoryConference);
    }
    
    public BusiHistoryConference saveHistory(CoSpace cosapce, Call call, ConferenceContext conferenceContext)
    {
        try
        {
            BusiHistoryConference busiHistoryConference = null;
            if (call != null)
            {
                busiHistoryConference = busiHistoryConferenceMapper.selectBusiHistoryConferenceByCallId(call.getId());
            }
            
            if (busiHistoryConference == null)
            {
                // 删除其它场景模式会议
                {
                    BusiHistoryConference busiHistoryConferenceDelete = new BusiHistoryConference();
                    busiHistoryConferenceDelete.setTemplateId(conferenceContext.getTemplateConferenceId());
                    busiHistoryConferenceMapper.deleteModeHistoryConference(busiHistoryConferenceDelete);
                }
                busiHistoryConference = new BusiHistoryConference();
                busiHistoryConference.setDeptId(conferenceContext.getDeptId());
                busiHistoryConference.setBandwidth(conferenceContext.getBandwidth());
                busiHistoryConference.setCallLegProfileId(cosapce.getCallLegProfile());
                busiHistoryConference.setName(conferenceContext.getName());
                busiHistoryConference.setNumber(conferenceContext.getConferenceNumber());
                busiHistoryConference.setDeviceNum(0);
                busiHistoryConference.setCoSpace(cosapce.getId());
                busiHistoryConference.setCreateTime(new Date());
                busiHistoryConference.setConferenceStartTime(conferenceContext.getStartTime());
                busiHistoryConference.setCreateUserId(conferenceContext.getCreateUserId());
                busiHistoryConference.setMcuType(McuType.FME.getCode());
                busiHistoryConference.setTemplateId(conferenceContext.getTemplateConferenceId());
                busiHistoryConference.setCreateUserId(conferenceContext.getCreateUserId());
                if (conferenceContext.getCreateUserId() != null) {
                    SysUser sysUser = sysUserMapper.selectUserById(conferenceContext.getCreateUserId());
                    if (sysUser != null) {
                        busiHistoryConference.setCreateUserName(sysUser.getNickName());
                    }
                }

                int type = 0;
                if (conferenceContext.getAppointmentType() != null) {
                    if (conferenceContext.getAppointmentType() == 2) {
                        type = 2;
                    } else {
                        type = 1;
                    }
                }
                busiHistoryConference.setType(type);
                busiHistoryConference.setDuration(0);
                busiHistoryConferenceMapper.insertBusiHistoryConference(busiHistoryConference);

                // 会议统计
                StatisticalConferenceTask statisticalConferenceTask = new StatisticalConferenceTask(busiHistoryConference.getDeptId().toString(), 10000, busiHistoryConference.getDeptId());
                taskService.addTask(statisticalConferenceTask);
                return busiHistoryConference;
            }
            else
            {
                return saveHistory(busiHistoryConference, conferenceContext);
            }
        }
        catch (Throwable e)
        {
            logger.error("saveHistory error", e);
            return null;
        }
    }
    
    public BusiHistoryConference saveHistory(CoSpace cosapce, CdrCall cdrCall, Long deptId)
    {
        try
        {
            BusiHistoryConference busiHistoryConference = busiHistoryConferenceMapper.selectBusiHistoryConferenceByCallId(cdrCall.getCdrId());
            if (busiHistoryConference == null)
            {
                busiHistoryConference = new BusiHistoryConference();
                busiHistoryConference.setDeptId(deptId);
                busiHistoryConference.setCallLegProfileId(cosapce.getCallLegProfile());
                busiHistoryConference.setName(cdrCall.getName());
                busiHistoryConference.setNumber(cosapce.getUri());
                busiHistoryConference.setDeviceNum(0);
                busiHistoryConference.setCoSpace(cosapce.getId());
                busiHistoryConference.setCreateTime(new Date());
                busiHistoryConference.setConferenceStartTime(cdrCall.getTime());
                
                busiHistoryConference.setType(0);
                busiHistoryConference.setDuration(0);
                busiHistoryConferenceMapper.insertBusiHistoryConference(busiHistoryConference);

                // 会议统计
                StatisticalConferenceTask statisticalConferenceTask = new StatisticalConferenceTask(busiHistoryConference.getDeptId().toString(), 10000, busiHistoryConference.getDeptId());
                taskService.addTask(statisticalConferenceTask);
                return busiHistoryConference;
            }
            else
            {
                return busiHistoryConference;
            }
        }
        catch (Throwable e)
        {
            logger.error("saveHistory error", e);
            return null;
        }
    }
    
    public BusiHistoryConference saveHistory(BusiHistoryConference busiHistoryConference, ConferenceContext conferenceContext)
    {
        try
        {
            BusiHistoryConference record = busiHistoryConference;
            record.setConferenceStartTime(conferenceContext.getStartTime());
            if (conferenceContext.getEndTime() != null)
            {
                record.setConferenceEndTime(conferenceContext.getEndTime());
                record.setDuration((int)((conferenceContext.getEndTime().getTime() - conferenceContext.getStartTime().getTime()) / 1000));
            }
            int type = 0;
            if (conferenceContext.getAppointmentType() != null) {
                if (conferenceContext.getAppointmentType() == 2) {
                    type = 2;
                } else {
                    type = 1;
                }
            }
            busiHistoryConference.setType(type);
            busiHistoryConference.setMcuType(McuType.FME.getCode());
            busiHistoryConference.setTemplateId(conferenceContext.getTemplateConferenceId());
            busiHistoryConferenceMapper.updateBusiHistoryConference(record);

            // 会议统计
            StatisticalConferenceTask statisticalConferenceTask = new StatisticalConferenceTask(busiHistoryConference.getDeptId().toString(), 10000, busiHistoryConference.getDeptId());
            taskService.addTask(statisticalConferenceTask);
            return record;
        }
        catch (Throwable e)
        {
            logger.error("saveHistory error", e);
            return null;
        }
    }
    
    /**
     * 修改历史会议，每次挂断会保存该历史记录
     *
     * @param busiHistoryConference 历史会议，每次挂断会保存该历史记录
     * @return 结果
     */
    @Override
    public int updateBusiHistoryConference(BusiHistoryConference busiHistoryConference) {
        busiHistoryConference.setUpdateTime(new Date());

        // 绑定终端归属部门
        LoginUser loginUser = SecurityUtils.getLoginUser();
        busiHistoryConference.setDeptId(loginUser.getUser().getDeptId());
        return busiHistoryConferenceMapper.updateBusiHistoryConference(busiHistoryConference);
    }

    /**
     * 批量删除历史会议，每次挂断会保存该历史记录
     *
     * @param ids 需要删除的历史会议，每次挂断会保存该历史记录ID
     * @return 结果
     */
    @Override
    public int deleteBusiHistoryConferenceByIds(Long[] ids) {
        return busiHistoryConferenceMapper.deleteBusiHistoryConferenceByIds(ids);
    }

    /**
     * 删除历史会议，每次挂断会保存该历史记录信息
     *
     * @param id 历史会议，每次挂断会保存该历史记录ID
     * @return 结果
     */
    @Override
    public int deleteBusiHistoryConferenceById(Long id) {
        return busiHistoryConferenceMapper.deleteBusiHistoryConferenceById(id);
    }

    /**
     * 会议时长统计（每个模板使用次数，时间） 双柱子（x模板名，y次数和累计时长）
     *
     * @param deptId 部门Id
     * @return
     */
    @Override
    public List<BusiHistoryConference> reportByDept(Long deptId) {
        deptId = (deptId == null) ? SecurityUtils.getLoginUser().getUser().getDeptId() : deptId;
        //String startTime = DateUtil.fillDateString(busiHistoryConference.getConferenceStartTime().toString(),false);
        List<BusiHistoryConference> busiHistoryConferences = busiHistoryConferenceMapper.reportByDept(deptId, null, null, null);
        return busiHistoryConferences;
    }
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54
     * @return List<DeptRecordCount>
     */
    public List<DeptRecordCount> getDeptRecordCounts()
    {
        return busiHistoryConferenceMapper.getDeptRecordCounts();
    }

    /**
     * 查询会议历史记录页面
     *
     * @param reportSearchVo
     * @return
     */
    @Override
    public PaginationData<Map<String, Object>> selectHistoryPage(ReportSearchVo reportSearchVo) {
        PaginationData<Map<String, Object>> pd = new PaginationData<>();
        //补全日期格式的 时分秒 部分
        if (StringUtils.isNotNull(reportSearchVo.getPageNum()) && StringUtils.isNotNull(reportSearchVo.getPageSize())) {
            PageHelper.startPage(reportSearchVo.getPageNum(), reportSearchVo.getPageSize());
        }
        List<BusiHistoryConference> historyConferenceList = null;
        if (StringUtils.isNotEmpty(reportSearchVo.getDeviceName())) {
            BusiHistoryParticipant busiHistoryParticipant = new BusiHistoryParticipant();
            busiHistoryParticipant.setName(reportSearchVo.getDeviceName());
            List<BusiHistoryParticipant> historyParticipantList = busiHistoryParticipantMapper.selectBusiHistoryParticipantList(busiHistoryParticipant);
            if (CollectionUtils.isEmpty(historyParticipantList)) {
                return pd;
            }
            List<Long> historyIdList = historyParticipantList.stream().map(p -> p.getHistoryConferenceId()).collect(Collectors.toList());
            historyConferenceList = busiHistoryConferenceMapper.selectBySearchVoAndHistoryId(reportSearchVo, historyIdList);
        } else {
            historyConferenceList = busiHistoryConferenceMapper.selectBySearchVo(reportSearchVo);
        }
        PageInfo<?> pageInfo = new PageInfo<>(historyConferenceList);
        int size = 0;
        List<Map<String, Object>> mapList;
        if (!CollectionUtils.isEmpty(historyConferenceList)) {
            size = historyConferenceList.size();
            SysDept sysDept = sysDeptMapper.selectDeptById(reportSearchVo.getDeptId());
            mapList = new ArrayList<>(size);
            for (BusiHistoryConference busiHistoryConference : historyConferenceList) {
                busiHistoryConference.setSysDept(sysDept);
                McuType mcuType = McuType.convert(busiHistoryConference.getMcuType());
                CdrRecording cdrRecording = new CdrRecording();
                cdrRecording.setCallId(busiHistoryConference.getCallId());
                cdrRecording.setRecordType(1);
//                List<CdrRecording> cdrRecordingList = cdrRecordingMapper.selectCdrRecordingList(cdrRecording);
//                boolean recorded = (CollectionUtils.isEmpty(cdrRecordingList)) ? false : true;

                int num = busiHistoryConference.getDeviceNum();
                if (num == 0) {
                    BusiHistoryParticipantTerminal busiHistoryParticipantTerminalCon = new BusiHistoryParticipantTerminal();
                    busiHistoryParticipantTerminalCon.setHistoryConferenceId(busiHistoryConference.getId());
                    List<BusiHistoryParticipantTerminal> historyParticipantTerminalList = busiHistoryParticipantTerminalMapper.selectBusiHistoryParticipantTerminalList(busiHistoryParticipantTerminalCon);
                    num = historyParticipantTerminalList.size();
                    if (num == 0) {
                        BusiHistoryParticipant participant = new BusiHistoryParticipant();
                        participant.setHistoryConferenceId(busiHistoryConference.getId());
                        List<BusiHistoryParticipant> historyParticipantList = busiHistoryParticipantMapper.selectBusiHistoryParticipantList(participant);
                        num = (CollectionUtils.isEmpty(historyParticipantList)) ? 0 : historyParticipantList.size();
                    }
                }



                // MCU-ZJ,MCU-PLC
                String conferenceNumber = busiHistoryConference.getNumber();
                String coSpace = busiHistoryConference.getCoSpace();
                if (StringUtils.isNotEmpty(coSpace) && coSpace.length() > conferenceNumber.length() && (coSpace.endsWith("-zj") || coSpace.endsWith("-plc") || coSpace.endsWith("-kdc")|| coSpace.endsWith("-SMC3")|| coSpace.endsWith("-SMC2")|| coSpace.endsWith("-TENCENT"))) {
                    if (coSpace.contains("-")) {
                        busiHistoryConference.setNumber(coSpace.substring(0, coSpace.indexOf("-")));
                    } else {
                        busiHistoryConference.setNumber(coSpace);
                    }
                }

                Map<String, Object> map = new HashMap<>(6);
                if (busiHistoryConference.getConferenceEndTime() == null) {
                    busiHistoryConference.setEndReasonsType(null);
                }
                map.put("historyConference", busiHistoryConference);
                if (reportSearchVo.isModeConference()) {
                    if (busiHistoryConference.getTemplateId() != null) {
                        if (McuType.FME == mcuType) {
                            IBusiTemplateConferenceService busiTemplateConferenceService = BeanFactory.getBean(IBusiTemplateConferenceService.class);
                            BusiTemplateConferenceMapper busiTemplateConferenceMapper = BeanFactory.getBean(BusiTemplateConferenceMapper.class);
                            ViewTemplateConferenceMapper viewTemplateConferenceMapper = BeanFactory.getBean(ViewTemplateConferenceMapper.class);
                            BusiTemplateConference busiTemplateConference = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(busiHistoryConference.getTemplateId());
                            if (busiHistoryConference != null) {
                                ModelBean modelBean = busiTemplateConferenceService.getTemplateConferenceDetails(busiTemplateConference);
                                if (modelBean != null) {
                                    modelBean.put("conferenceId", EncryptIdUtil.generateConferenceId(busiHistoryConference.getTemplateId(), mcuType.getCode()));
                                    modelBean.put("mcuType", mcuType.getCode());
                                    modelBean.put("mcuTypeAlias", mcuType.getAlias());
                                    if (busiTemplateConference.getUpCascadeId() != null) {
                                        modelBean.put("isDownCascade", true);
                                    } else {
                                        modelBean.put("isDownCascade", false);
                                    }
                                    List<ModelBean> downCascades = new ArrayList<>();
                                    {
                                        ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
                                        viewTemplateConferenceCon.setUpCascadeId(busiHistoryConference.getTemplateId());
                                        viewTemplateConferenceCon.setUpCascadeMcuType(mcuType.getCode());
                                        List<ViewTemplateConference> downCascadeList = viewTemplateConferenceMapper.selectViewTemplateConferenceList(viewTemplateConferenceCon);
                                        for (ViewTemplateConference viewTemplateConference : downCascadeList) {
                                            ModelBean modelBeanDownCascade = new ModelBean();
                                            McuType mcuTypeDownCascade = McuType.convert(viewTemplateConference.getMcuType());
                                            if (mcuTypeDownCascade != null) {
                                                modelBeanDownCascade.put("conferenceId", viewTemplateConference.getConferenceId());
                                                modelBeanDownCascade.put("mcuType", mcuTypeDownCascade.getCode());
                                                modelBeanDownCascade.put("mcuTypeAlias", mcuTypeDownCascade.getAlias());
                                                downCascades.add(modelBeanDownCascade);
                                            }
                                        }
                                    }
//                                    {
//                                        Map<String, Object> businessProperties = busiTemplateConference.getBusinessProperties();
//                                        Object cloudMcuTypeArrObj = businessProperties.get("cloudMcuType");
//                                        if (cloudMcuTypeArrObj != null) {
//                                            if(cloudMcuTypeArrObj instanceof JSONArray){
//                                                JSONArray jsonArray = (JSONArray) cloudMcuTypeArrObj;
//                                                for (Object cloudMcuTypeObj : jsonArray) {
//                                                    try {
//                                                        String cloudMcuType = (String) cloudMcuTypeObj;
//                                                        ModelBean modelBeanDownCascade = new ModelBean();
//                                                        McuType mcuTypeDownCascade = McuType.convert(cloudMcuType);
//                                                        if (mcuTypeDownCascade != null) {
//                                                            modelBeanDownCascade.put("conferenceId", "");
//                                                            modelBeanDownCascade.put("mcuType", mcuTypeDownCascade.getCode());
//                                                            modelBeanDownCascade.put("mcuTypeAlias", mcuTypeDownCascade.getAlias());
//                                                            downCascades.add(modelBeanDownCascade);
//                                                        }
//                                                    } catch (Exception e) {
//                                                    }
//                                                }
//                                            }
//                                            if(cloudMcuTypeArrObj instanceof String){
//                                                String cloudMcuType = (String) cloudMcuTypeArrObj;
//                                                ModelBean modelBeanDownCascade = new ModelBean();
//                                                McuType mcuTypeDownCascade = McuType.convert(cloudMcuType);
//                                                if (mcuTypeDownCascade != null) {
//                                                    modelBeanDownCascade.put("conferenceId", "");
//                                                    modelBeanDownCascade.put("mcuType", mcuTypeDownCascade.getCode());
//                                                    modelBeanDownCascade.put("mcuTypeAlias", mcuTypeDownCascade.getAlias());
//                                                    downCascades.add(modelBeanDownCascade);
//                                                }
//                                            }
//                                        }
//                                    }
                                    if (downCascades.size() > 0) {
                                        modelBean.put("isUpCascade", true);
                                    } else {
                                        modelBean.put("isUpCascade", false);
                                    }
                                    modelBean.put("downCascades", downCascades);
                                    map.put("template", modelBean);
                                }
                            }
                        }
                    }
                }
                //是否开启录制
//                map.put("recorded", recorded);
                map.put("participantNum", num);
                String minutesFilePath = MinutesFileHandler.generateFilePath(busiHistoryConference.getCoSpace(), busiHistoryConference.getNumber(), busiHistoryConference.getId());
                File downloadFile = new File(minutesFilePath);
                if (downloadFile.exists()) {
                    map.put("hasMinutes", true);
                } else {
                    map.put("hasMinutes", false);
                }

                List<BusiRecords> busiRecordsList = busiRecordsMapper.selectBusiRecordsByCoSpaceId(busiHistoryConference.getDeptId(), busiHistoryConference.getCoSpace(), Boolean.FALSE);
                map.put("recordNum", busiRecordsList.size());
                

                pd.addRecord(map);
                mapList.add(map);
            }
            pd.setTotal(pageInfo.getTotal());
            pd.setSize(pageInfo.getSize());
            pd.setPage(pageInfo.getPageNum());
            return pd;
        }
        return pd;
    }


    @Override
    public PaginationData<Map<String, Object>> selectHistoryPageDoc(ReportSearchVo reportSearchVo) {
        reportSearchVo.setMinutesDoc(1);
        PaginationData<Map<String, Object>> pd = new PaginationData<>();
        //补全日期格式的 时分秒 部分
        if (StringUtils.isNotNull(reportSearchVo.getPageNum()) && StringUtils.isNotNull(reportSearchVo.getPageSize())) {
            PageHelper.startPage(reportSearchVo.getPageNum(), reportSearchVo.getPageSize());
        }
        List<BusiHistoryConference> historyConferenceList = null;
        if (StringUtils.isNotEmpty(reportSearchVo.getDeviceName())) {
            BusiHistoryParticipant busiHistoryParticipant = new BusiHistoryParticipant();
            busiHistoryParticipant.setName(reportSearchVo.getDeviceName());
            List<BusiHistoryParticipant> historyParticipantList = busiHistoryParticipantMapper.selectBusiHistoryParticipantList(busiHistoryParticipant);
            if (CollectionUtils.isEmpty(historyParticipantList)) {
                return pd;
            }
            List<Long> historyIdList = historyParticipantList.stream().map(p -> p.getHistoryConferenceId()).collect(Collectors.toList());
            historyConferenceList = busiHistoryConferenceMapper.selectBySearchVoAndHistoryId(reportSearchVo, historyIdList);
        } else {
            historyConferenceList = busiHistoryConferenceMapper.selectBySearchVo(reportSearchVo);
        }
        PageInfo<?> pageInfo = new PageInfo<>(historyConferenceList);
        int size = 0;
        List<Map<String, Object>> mapList;
        if (!CollectionUtils.isEmpty(historyConferenceList)) {
            size = historyConferenceList.size();
            SysDept sysDept = sysDeptMapper.selectDeptById(reportSearchVo.getDeptId());
            mapList = new ArrayList<>(size);
            for (BusiHistoryConference busiHistoryConference : historyConferenceList) {
                busiHistoryConference.setSysDept(sysDept);
                McuType mcuType = McuType.convert(busiHistoryConference.getMcuType());
                CdrRecording cdrRecording = new CdrRecording();
                cdrRecording.setCallId(busiHistoryConference.getCallId());
                cdrRecording.setRecordType(1);
//                List<CdrRecording> cdrRecordingList = cdrRecordingMapper.selectCdrRecordingList(cdrRecording);
//                boolean recorded = (CollectionUtils.isEmpty(cdrRecordingList)) ? false : true;

                int num = busiHistoryConference.getDeviceNum();
                if (num == 0) {
                    BusiHistoryParticipantTerminal busiHistoryParticipantTerminalCon = new BusiHistoryParticipantTerminal();
                    busiHistoryParticipantTerminalCon.setHistoryConferenceId(busiHistoryConference.getId());
                    List<BusiHistoryParticipantTerminal> historyParticipantTerminalList = busiHistoryParticipantTerminalMapper.selectBusiHistoryParticipantTerminalList(busiHistoryParticipantTerminalCon);
                    num = historyParticipantTerminalList.size();
                    if (num == 0) {
                        BusiHistoryParticipant participant = new BusiHistoryParticipant();
                        participant.setHistoryConferenceId(busiHistoryConference.getId());
                        List<BusiHistoryParticipant> historyParticipantList = busiHistoryParticipantMapper.selectBusiHistoryParticipantList(participant);
                        num = (CollectionUtils.isEmpty(historyParticipantList)) ? 0 : historyParticipantList.size();
                    }
                }



                // MCU-ZJ,MCU-PLC
                String conferenceNumber = busiHistoryConference.getNumber();
                String coSpace = busiHistoryConference.getCoSpace();
                if (StringUtils.isNotEmpty(coSpace) && coSpace.length() > conferenceNumber.length() && (coSpace.endsWith("-zj") || coSpace.endsWith("-plc") || coSpace.endsWith("-kdc")|| coSpace.endsWith("-SMC3")|| coSpace.endsWith("-SMC2")|| coSpace.endsWith("-TENCENT"))) {
                    if (coSpace.contains("-")) {
                        busiHistoryConference.setNumber(coSpace.substring(0, coSpace.indexOf("-")));
                    } else {
                        busiHistoryConference.setNumber(coSpace);
                    }
                }

                Map<String, Object> map = new HashMap<>(6);
                if (busiHistoryConference.getConferenceEndTime() == null) {
                    busiHistoryConference.setEndReasonsType(null);
                }
                map.put("historyConference", busiHistoryConference);
                if (reportSearchVo.isModeConference()) {
                    if (busiHistoryConference.getTemplateId() != null) {
                        if (McuType.FME == mcuType) {
                            IBusiTemplateConferenceService busiTemplateConferenceService = BeanFactory.getBean(IBusiTemplateConferenceService.class);
                            BusiTemplateConferenceMapper busiTemplateConferenceMapper = BeanFactory.getBean(BusiTemplateConferenceMapper.class);
                            ViewTemplateConferenceMapper viewTemplateConferenceMapper = BeanFactory.getBean(ViewTemplateConferenceMapper.class);
                            BusiTemplateConference busiTemplateConference = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(busiHistoryConference.getTemplateId());
                            if (busiHistoryConference != null) {
                                ModelBean modelBean = busiTemplateConferenceService.getTemplateConferenceDetails(busiTemplateConference);
                                if (modelBean != null) {
                                    modelBean.put("conferenceId", EncryptIdUtil.generateConferenceId(busiHistoryConference.getTemplateId(), mcuType.getCode()));
                                    modelBean.put("mcuType", mcuType.getCode());
                                    modelBean.put("mcuTypeAlias", mcuType.getAlias());
                                    if (busiTemplateConference.getUpCascadeId() != null) {
                                        modelBean.put("isDownCascade", true);
                                    } else {
                                        modelBean.put("isDownCascade", false);
                                    }
                                    List<ModelBean> downCascades = new ArrayList<>();
                                    {
                                        ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
                                        viewTemplateConferenceCon.setUpCascadeId(busiHistoryConference.getTemplateId());
                                        viewTemplateConferenceCon.setUpCascadeMcuType(mcuType.getCode());
                                        List<ViewTemplateConference> downCascadeList = viewTemplateConferenceMapper.selectViewTemplateConferenceList(viewTemplateConferenceCon);
                                        for (ViewTemplateConference viewTemplateConference : downCascadeList) {
                                            ModelBean modelBeanDownCascade = new ModelBean();
                                            McuType mcuTypeDownCascade = McuType.convert(viewTemplateConference.getMcuType());
                                            if (mcuTypeDownCascade != null) {
                                                modelBeanDownCascade.put("conferenceId", viewTemplateConference.getConferenceId());
                                                modelBeanDownCascade.put("mcuType", mcuTypeDownCascade.getCode());
                                                modelBeanDownCascade.put("mcuTypeAlias", mcuTypeDownCascade.getAlias());
                                                downCascades.add(modelBeanDownCascade);
                                            }
                                        }
                                    }
//                                    {
//                                        Map<String, Object> businessProperties = busiTemplateConference.getBusinessProperties();
//                                        Object cloudMcuTypeArrObj = businessProperties.get("cloudMcuType");
//                                        if (cloudMcuTypeArrObj != null) {
//                                            if(cloudMcuTypeArrObj instanceof JSONArray){
//                                                JSONArray jsonArray = (JSONArray) cloudMcuTypeArrObj;
//                                                for (Object cloudMcuTypeObj : jsonArray) {
//                                                    try {
//                                                        String cloudMcuType = (String) cloudMcuTypeObj;
//                                                        ModelBean modelBeanDownCascade = new ModelBean();
//                                                        McuType mcuTypeDownCascade = McuType.convert(cloudMcuType);
//                                                        if (mcuTypeDownCascade != null) {
//                                                            modelBeanDownCascade.put("conferenceId", "");
//                                                            modelBeanDownCascade.put("mcuType", mcuTypeDownCascade.getCode());
//                                                            modelBeanDownCascade.put("mcuTypeAlias", mcuTypeDownCascade.getAlias());
//                                                            downCascades.add(modelBeanDownCascade);
//                                                        }
//                                                    } catch (Exception e) {
//                                                    }
//                                                }
//                                            }
//                                            if(cloudMcuTypeArrObj instanceof String){
//                                                String cloudMcuType = (String) cloudMcuTypeArrObj;
//                                                ModelBean modelBeanDownCascade = new ModelBean();
//                                                McuType mcuTypeDownCascade = McuType.convert(cloudMcuType);
//                                                if (mcuTypeDownCascade != null) {
//                                                    modelBeanDownCascade.put("conferenceId", "");
//                                                    modelBeanDownCascade.put("mcuType", mcuTypeDownCascade.getCode());
//                                                    modelBeanDownCascade.put("mcuTypeAlias", mcuTypeDownCascade.getAlias());
//                                                    downCascades.add(modelBeanDownCascade);
//                                                }
//                                            }
//                                        }
//                                    }
                                    if (downCascades.size() > 0) {
                                        modelBean.put("isUpCascade", true);
                                    } else {
                                        modelBean.put("isUpCascade", false);
                                    }
                                    modelBean.put("downCascades", downCascades);
                                    map.put("template", modelBean);
                                }
                            }
                        }
                    }
                }
                //是否开启录制
//                map.put("recorded", recorded);
                map.put("participantNum", num);

                List<BusiRecords> busiRecordsList = busiRecordsMapper.selectBusiRecordsByCoSpaceId(busiHistoryConference.getDeptId(), busiHistoryConference.getCoSpace(), Boolean.FALSE);
                map.put("recordNum", busiRecordsList.size());


                pd.addRecord(map);
                mapList.add(map);
            }
            pd.setTotal(pageInfo.getTotal());
            pd.setSize(pageInfo.getSize());
            pd.setPage(pageInfo.getPageNum());
            return pd;
        }
        return pd;
    }

    /**
     * 统计每天发起会议的数量
     *
     * @param searchVo
     * @return
     */
    @Override
    public List<Map<String, Map<String, Object>>> reportNumOfDay(ReportSearchVo searchVo) {
        Long deptId = (searchVo.getDeptId() == null) ? SecurityUtils.getLoginUser().getUser().getDeptId() : searchVo.getDeptId();
        searchVo.setDeptId(deptId);
        List<BusiHistoryConference> list = busiHistoryConferenceMapper.selectBySearchVo(searchVo);

        Map<String, List<BusiHistoryConference>> allList = list.stream().collect(Collectors.groupingBy(item -> new SimpleDateFormat("yyyy-MM-dd").format(item.getConferenceStartTime())));

        List<Map<String, Map<String, Object>>> mapList = new ArrayList<>();

        allList.forEach((day, dayList) -> {
            Map<String, Map<String, Object>> dayMap = new HashMap<>();
            Map<String, Object> typeMap = new HashMap<>();
            typeMap.put("0", 0);
            typeMap.put("1", 0);
            typeMap.put("2", 0);
            dayList.stream().collect(Collectors.groupingBy(BusiHistoryConference::getType)).forEach((type, typeList) -> {
                String string = String.valueOf(type);
                typeMap.put(string, typeList.size());
            });
            dayMap.put(day, typeMap);
            mapList.add(dayMap);
        });
        return mapList;
    }

    /**
     * 每天发起会议的时长总和
     *
     * @param searchVo
     * @return
     */
    @Override
    public List<Map<String, Map<String, Object>>> reportDurationOfDay(ReportSearchVo searchVo) {
        Long deptId = (searchVo.getDeptId() == null) ? SecurityUtils.getLoginUser().getUser().getDeptId() : searchVo.getDeptId();
        searchVo.setDeptId(deptId);
        List<BusiHistoryConference> list = busiHistoryConferenceMapper.selectBySearchVo(searchVo);

        Map<String, List<BusiHistoryConference>> allList = list.stream().collect(Collectors.groupingBy(item -> new SimpleDateFormat("yyyy-MM-dd").format(item.getConferenceStartTime())));

        List<Map<String, Map<String, Object>>> mapList = new ArrayList<>();

        allList.forEach((day, dayList) -> {
            Map<String, Map<String, Object>> dayMap = new HashMap<>();
            Map<String, Object> typeMap = new HashMap<>();
            typeMap.put("0", 0);
            typeMap.put("1", 0);
            typeMap.put("2", 0);
            dayList.stream().collect(Collectors.groupingBy(BusiHistoryConference::getType)).forEach((type, typeList) -> {
                String string = String.valueOf(type);
                typeMap.put(string, typeList.stream().mapToInt(BusiHistoryConference::getDuration).sum());
            });
            dayMap.put(day, typeMap);
            mapList.add(dayMap);
        });
        return mapList;
    }

    /**
     * 不同会议时长类型数量统计
     *
     * @param searchVo
     * @return
     */
    @Override
    public List<Map<String, Object>> reportDurationType(ReportSearchVo searchVo) {
        Long deptId = (searchVo.getDeptId() == null) ? SecurityUtils.getLoginUser().getUser().getDeptId() : searchVo.getDeptId();
        searchVo.setDeptId(deptId);
        List<BusiHistoryConference> list = busiHistoryConferenceMapper.selectBySearchVo(searchVo);
        //15分钟
        int halfAnHour = 15 * 60;
        int num = 0;
        int lessFifteenNum = 0;
        int lessThirtyNum = 0;
        int lessHourNum = 0;
        int moreOneHourNum = 0;
        int moreTwoHourNum = 0;
        for (BusiHistoryConference busiHistoryConference : list) {
            num = busiHistoryConference.getDuration() / halfAnHour;
            if (num <= 1) {
                //小于15分钟统计
                lessFifteenNum++;
            }
            if (num > 1 && num <= 2) {
                lessThirtyNum++;
            }
            if (num > 2 && num <= 4) {
                lessHourNum++;
            }
            if (num > 4 && num <= 8) {
                moreOneHourNum++;
            }
            if (num > 8) {
                moreTwoHourNum++;
            }
        }
        List<Map<String, Object>> listMap = new ArrayList<>();
        String[] names = {"<=15", "15-30", "30-60", "60-120", ">120"};
        for (int i = 0; i < names.length; i++) {
            Map<String, Object> map = new HashMap<>(4);
            map.put("name", names[i]);
            if (i == 0) {
                map.put("value", lessFifteenNum);
            }
            if (i == 1) {
                map.put("value", lessThirtyNum);
            }
            if (i == 2) {
                map.put("value", lessHourNum);
            }
            if (i == 3) {
                map.put("value", moreOneHourNum);
            }
            if (i == 4) {
                map.put("value", moreTwoHourNum);
            }
            listMap.add(map);
        }
        return listMap;
    }

    @Override
    public int updateBusiHistoryConferenceEndReasonsType(BusiHistoryConference busiHistoryConference, int endReasonsType) {
        busiHistoryConference.setEndReasonsType(endReasonsType);
        int i = busiHistoryConferenceMapper.updateBusiHistoryConference(busiHistoryConference);
        return i;
    }
}
