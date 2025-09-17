package com.paradisecloud.fcm.tencent.service2.impls;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.dao.enums.CallLegEndReasonEnum;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.service.interfaces.IBusiHistoryParticipantTerminalService;

import com.paradisecloud.fcm.tencent.busi.attende.AttendeeTencent;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContext;
import com.paradisecloud.fcm.tencent.model.SmcParitipantsStateRep;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiMcuTencentHistoryConferenceService;
import com.paradisecloud.fcm.tencent.task.EndConferenceTaskTencent;
import com.paradisecloud.fcm.tencent.task.StatisticalConferenceTaskTencent;
import com.paradisecloud.fcm.tencent.task.TencentDelayTaskService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.paradisecloud.system.dao.mapper.SysDeptMapper;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 历史会议，每次挂断会保存该历史记录Service业务层处理
 *
 * @author lilinhai
 * @date 2021-01-20
 */
@Transactional
@Service
public class BusiMcuTencentHistoryConferenceServiceImpl implements IBusiMcuTencentHistoryConferenceService {

    private Logger logger = LoggerFactory.getLogger(BusiMcuTencentHistoryConferenceServiceImpl.class);

    @Resource
    private BusiHistoryConferenceMapper busiHistoryConferenceMapper;
    @Resource
    private SysDeptMapper sysDeptMapper;
    @Resource
    private BusiHistoryParticipantMapper busiHistoryParticipantMapper;
    @Resource
    private BusiHistoryParticipantTerminalMapper busiHistoryParticipantTerminalMapper;
    @Resource
    private com.paradisecloud.fcm.tencent.task.TencentDelayTaskService TencentDelayTaskService;
    @Resource
    private BusiHistoryCallMapper busiHistoryCallMapper;
    @Resource
    private CdrCallLegStartMapper cdrCallLegStartMapper;
    @Resource
    private CdrCallLegEndMapper cdrCallLegEndMapper;
    @Resource
    private IBusiHistoryParticipantTerminalService busiHistoryParticipantTerminalService;
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

    @Override
    public BusiHistoryConference saveHistory(CoSpace cosapce, Call call, TencentConferenceContext conferenceContext)
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
                busiHistoryConference = new BusiHistoryConference();
                busiHistoryConference.setDeptId(conferenceContext.getDeptId());
                busiHistoryConference.setBandwidth(conferenceContext.getBandwidth());
                busiHistoryConference.setCallLegProfileId(conferenceContext.getTemplateConferenceId().toString());
                busiHistoryConference.setName(conferenceContext.getName());
                busiHistoryConference.setNumber(conferenceContext.getConferenceNumber());
                busiHistoryConference.setDeviceNum(0);
                busiHistoryConference.setCoSpace(cosapce.getId());
                busiHistoryConference.setCreateTime(new Date());
                busiHistoryConference.setConferenceStartTime(conferenceContext.getStartTime());
                busiHistoryConference.setCreateUserId(conferenceContext.getCreateUserId());

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

    @Override
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

    @Override
    public BusiHistoryConference saveHistory(BusiHistoryConference busiHistoryConference, TencentConferenceContext conferenceContext)
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
            busiHistoryConferenceMapper.updateBusiHistoryConference(record);

            if (busiHistoryConference.getConferenceEndTime() != null) {
                // 结束会议
                EndConferenceTaskTencent endConferenceTask = new EndConferenceTaskTencent(busiHistoryConference.getId().toString(), 1000, busiHistoryConference.getId());
                TencentDelayTaskService.addTask(endConferenceTask);
            }

            // 会议统计
            StatisticalConferenceTaskTencent statisticalConferenceTask = new StatisticalConferenceTaskTencent(busiHistoryConference.getDeptId().toString(), 10000, busiHistoryConference.getDeptId());
            TencentDelayTaskService.addTask(statisticalConferenceTask);

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
    @Override
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
                CdrRecording cdrRecording = new CdrRecording();
                cdrRecording.setCallId(busiHistoryConference.getCallId());
                cdrRecording.setRecordType(1);


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

                String conferenceNumber = busiHistoryConference.getNumber();
                String coSpace = busiHistoryConference.getCoSpace();
                if (StringUtils.isNotEmpty(coSpace) && coSpace.length() > conferenceNumber.length() && (coSpace.endsWith("-zj") || coSpace.endsWith("-plc"))) {
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

                map.put("participantNum", num);

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
        String[] names = {"<=15", "15-20", "20-60", "60-120", ">120"};
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
            if (i == 2) {
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

    @Override
    public BusiHistoryConference saveHistory(TencentConferenceContext conferenceContext) {
        BusiHistoryConference busiHistoryConference = new BusiHistoryConference();
        busiHistoryConference.setDeptId(conferenceContext.getDeptId());
        busiHistoryConference.setBandwidth(conferenceContext.getBandwidth());
        busiHistoryConference.setCallLegProfileId(conferenceContext.getTemplateConferenceId().toString());
        busiHistoryConference.setName(conferenceContext.getName());
        busiHistoryConference.setNumber(conferenceContext.getConferenceNumber());
        busiHistoryConference.setDeviceNum(0);
        busiHistoryConference.setCoSpace(conferenceContext.getCoSpaceId());
        busiHistoryConference.setCreateTime(new Date());
        busiHistoryConference.setConferenceStartTime(conferenceContext.getStartTime());
        busiHistoryConference.setCreateUserId(conferenceContext.getCreateUserId());

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

        StatisticalConferenceTaskTencent statisticalConferenceTask=new StatisticalConferenceTaskTencent(busiHistoryConference.getDeptId().toString(),1000,busiHistoryConference.getDeptId());
        TencentDelayTaskService.addTask(statisticalConferenceTask);
        return busiHistoryConference;
    }


    /**
     * 更新参会者
     *
     * @param conferenceContext
     * @param attendee
     */
    @Override
    public void updateBusiHistoryParticipant(TencentConferenceContext conferenceContext, AttendeeTencent attendee, boolean updateMediaInfo) {
        if (attendee.isMeetingJoined()) {
            BusiHistoryConference busiHistoryConference = conferenceContext.getHistoryConference();
            if (busiHistoryConference != null) {
                BusiHistoryParticipant busiHistoryParticipant = busiHistoryParticipantMapper.selectBusiHistoryParticipantByCallLegId(attendee.getParticipantUuid());
                if (busiHistoryParticipant != null) {
                    if (busiHistoryParticipant.getJoined() == null || !busiHistoryParticipant.getJoined()) {
                        busiHistoryParticipant.setJoined(true);
                        busiHistoryParticipant.setJoinTime(new Date());
                        busiHistoryParticipant.setUpdateTime(new Date());
                        busiHistoryParticipantMapper.updateBusiHistoryParticipant(busiHistoryParticipant);
                    }else if(Strings.isBlank(busiHistoryParticipant.getName())){
                        busiHistoryParticipant.setUpdateTime(new Date());
                        busiHistoryParticipant.setName(attendee.getName());
                        busiHistoryParticipantMapper.updateBusiHistoryParticipant(busiHistoryParticipant);
                    } else {
                        if (updateMediaInfo) {
                         //todo
                        }
                    }
                } else {
                    BusiHistoryCall busiHistoryCallCon = new BusiHistoryCall();
                    busiHistoryCallCon.setHistoryConferenceId(busiHistoryConference.getId());
                    List<BusiHistoryCall> busiHistoryCallList = busiHistoryCallMapper.selectBusiHistoryCallList(busiHistoryCallCon);
                    BusiHistoryCall busiHistoryCall = null;
                    if (busiHistoryCallList.size() > 0) {
                        busiHistoryCall = busiHistoryCallList.get(0);
                    }
                    busiHistoryParticipant = new BusiHistoryParticipant();
                    busiHistoryParticipant.setCreateTime(new Date());
                    busiHistoryParticipant.setDeptId(busiHistoryConference.getDeptId().intValue());
                    if (busiHistoryCall != null) {
                        busiHistoryParticipant.setCallId(busiHistoryCall.getCallId());
                    }
                    busiHistoryParticipant.setCoSpace(conferenceContext.getCoSpaceId());
                    busiHistoryParticipant.setCallLegId(attendee.getParticipantUuid());
                    busiHistoryParticipant.setName(attendee.getName());
                    busiHistoryParticipant.setHistoryConferenceId(busiHistoryConference.getId());
                    busiHistoryParticipant.setJoinTime(new Date());
                    busiHistoryParticipant.setCreateTime(new Date());
                    busiHistoryParticipant.setJoined(true);
                    if (updateMediaInfo) {
                        //todo
                    }
                    String remoteParty = attendee.getRemoteParty();
                    if (remoteParty.contains(":")) {
                        remoteParty = remoteParty.substring(0, remoteParty.indexOf(":"));
                    }
                    busiHistoryParticipant.setRemoteParty(remoteParty);
                    BusiTerminal busiTerminal = TerminalCache.getInstance().getByRemoteParty(remoteParty);
                    if (busiTerminal == null) {
                        if (remoteParty.contains("@")) {
                            try {
                                String[] remotePartyArr = remoteParty.split("@");
                                String credential = remotePartyArr[0];
                                String ip = remotePartyArr[1];
                                if (org.springframework.util.StringUtils.hasText(ip)) {
                                    FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getByDomainName(ip);
                                    if (fsbcBridge != null) {
                                        String remotePartyNew = credential + "@" + fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                                        busiTerminal = TerminalCache.getInstance().getByRemoteParty(remotePartyNew);
                                    }
                                    if (busiTerminal == null) {
                                        FcmBridge fcmBridge = FcmBridgeCache.getInstance().getByDomainName(ip);
                                        if (fcmBridge != null) {
                                            String remotePartyNew = credential + "@" + fcmBridge.getBusiFreeSwitch().getIp();
                                            busiTerminal = TerminalCache.getInstance().getByRemoteParty(remotePartyNew);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                    if (busiTerminal != null) {
                        {
                            busiHistoryParticipant.setTerminalId(busiTerminal.getId());
                        }
                    }
                    busiHistoryParticipantMapper.insertBusiHistoryParticipant(busiHistoryParticipant);
                    try {
                        CdrCallLegStart cdrCallLegStart = new CdrCallLegStart();
                        Date date = new Date();
                        cdrCallLegStart.setCdrId(busiHistoryParticipant.getCallLegId());
                        cdrCallLegStart.setRecordIndex(0);
                        cdrCallLegStart.setCorrelatorIndex(0);
                        cdrCallLegStart.setTime(date);
                        cdrCallLegStart.setDisplayName(busiHistoryParticipant.getName());
                        cdrCallLegStart.setRemoteParty(busiHistoryParticipant.getRemoteParty());
                        cdrCallLegStart.setRemoteAddress(busiHistoryParticipant.getRemoteParty());
                        cdrCallLegStart.setCall(busiHistoryParticipant.getCallId());
                        cdrCallLegStart.setSession(busiHistoryParticipant.getCallId());
                        cdrCallLegStart.setCreateTime(date);
                        cdrCallLegStart.setRecording(false);
                        cdrCallLegStart.setStreaming(false);
                        cdrCallLegStart.setDirection("outgoing");
                        String type="sip";
                        SmcParitipantsStateRep.ContentDTO smcParticipant = attendee.getSmcParticipant();
                        if(smcParticipant!=null){
                            Integer type1 = attendee.getSmcParticipant().getGeneralParam().getType();
                            if(type1==1){
                                type="h222";
                            }else if(type1==2) {
                                type="sip&h222";
                            }
                        }
                        cdrCallLegStart.setType(type);
                        cdrCallLegStartMapper.insertCdrCallLegStart(cdrCallLegStart);
                    } catch (Exception e) {
                    }
                    logger.info("Participant joined and saved: " + attendee.toString());
                }
                // 更新参会者终端信息
                updateBusiHistoryParticipantTerminal(busiHistoryParticipant);
            }
        } else {
            BusiHistoryParticipant busiHistoryParticipant = busiHistoryParticipantMapper.selectBusiHistoryParticipantByCallLegId(attendee.getParticipantUuid());
            if (busiHistoryParticipant != null) {
                if (busiHistoryParticipant.getOutgoingTime() == null || busiHistoryParticipant.getDurationSeconds() == null || busiHistoryParticipant.getDurationSeconds().intValue() == 0) {
                    busiHistoryParticipant.setOutgoingTime(new Date());
                    busiHistoryParticipant.setUpdateTime(new Date());
                    busiHistoryParticipant.setDurationSeconds((int) ((busiHistoryParticipant.getOutgoingTime().getTime() - busiHistoryParticipant.getJoinTime().getTime()) / 1000));
                    busiHistoryParticipantMapper.updateBusiHistoryParticipant(busiHistoryParticipant);
                    logger.info("Participant left and saved: " + attendee.toString());

                    // 更新参会者终端信息
                    updateBusiHistoryParticipantTerminal(busiHistoryParticipant);
                }
                try {
                    CdrCallLegEnd cdrCallLegEnd = new CdrCallLegEnd();
                    Date date = new Date();
                    cdrCallLegEnd.setRecordIndex(0);
                    cdrCallLegEnd.setCorrelatorIndex(0);
                    cdrCallLegEnd.setTime(date);
                    cdrCallLegEnd.setSession(busiHistoryParticipant.getCallId());
                    cdrCallLegEnd.setCreateTime(date);
                    cdrCallLegEnd.setCdrId(busiHistoryParticipant.getCallLegId());
                    cdrCallLegEnd.setDurationSeconds(busiHistoryParticipant.getDurationSeconds());
                    if (cdrCallLegEnd.getReason() == null) {
                        if (attendee.getSmcParticipant() != null) {
                            Integer callFailReason = attendee.getSmcParticipant().getState().getCallFailReason();
                            CallLegEndReasonEnum callLegEndReasonEnum =CallLegEndReasonEnum.getEnumObjectByCode(callFailReason) ;
                            cdrCallLegEnd.setReason(callLegEndReasonEnum);
                        } else {
                            cdrCallLegEnd.setReason(CallLegEndReasonEnum.CALL_ENDED);
                        }
                    }
                    cdrCallLegEndMapper.insertCdrCallLegEnd(cdrCallLegEnd);
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 更新参会者
     *
     * @param busiHistoryParticipant
     * @param busiHistoryConference
     */
    @Override
    public void updateBusiHistoryParticipant(BusiHistoryParticipant busiHistoryParticipant, BusiHistoryConference busiHistoryConference) {
        if (busiHistoryParticipant != null && busiHistoryConference != null && busiHistoryConference.getConferenceEndTime() != null) {
            if (busiHistoryParticipant.getOutgoingTime() == null || busiHistoryParticipant.getDurationSeconds() == null || busiHistoryParticipant.getDurationSeconds().intValue() == 0) {
                busiHistoryParticipant.setOutgoingTime(busiHistoryConference.getConferenceEndTime());
                busiHistoryParticipant.setUpdateTime(new Date());
                busiHistoryParticipant.setDurationSeconds((int) ((busiHistoryParticipant.getOutgoingTime().getTime() - busiHistoryParticipant.getJoinTime().getTime()) / 1000));
                busiHistoryParticipantMapper.updateBusiHistoryParticipant(busiHistoryParticipant);
                logger.info("Participant left and saved: " + busiHistoryParticipant.toString());

                // 更新参会者终端信息
                updateBusiHistoryParticipantTerminal(busiHistoryParticipant);
            }
            try {
                CdrCallLegEnd cdrCallLegEnd = new CdrCallLegEnd();
                Date date = new Date();
                cdrCallLegEnd.setRecordIndex(0);
                cdrCallLegEnd.setCorrelatorIndex(0);
                cdrCallLegEnd.setTime(date);
                cdrCallLegEnd.setSession(busiHistoryParticipant.getCallId());
                cdrCallLegEnd.setCreateTime(date);
                cdrCallLegEnd.setCdrId(busiHistoryParticipant.getCallLegId());
                cdrCallLegEnd.setDurationSeconds(busiHistoryParticipant.getDurationSeconds());
                cdrCallLegEnd.setReason(CallLegEndReasonEnum.CALL_ENDED);
                cdrCallLegEndMapper.insertCdrCallLegEnd(cdrCallLegEnd);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 更新参会者终端信息
     *
     * @param busiHistoryParticipant
     */
    private void updateBusiHistoryParticipantTerminal(BusiHistoryParticipant busiHistoryParticipant) {
        if (busiHistoryParticipant != null) {
            if (busiHistoryParticipant.getTerminalId() != null) {
                BusiTerminal busiTerminal = TerminalCache.getInstance().get(busiHistoryParticipant.getTerminalId());
                if (busiTerminal != null) {
                    busiHistoryParticipant.setName(busiTerminal.getName());
                }
            }
            try {
               busiHistoryParticipantTerminalService.updateBusiHistoryParticipantTerminalByBusiHistoryParticipant(busiHistoryParticipant);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public List<BusiHistoryConference> selectBusiHistoryConferenceListNotEnd() {
        return busiHistoryConferenceMapper.selectNotEndHistoryConferenceList(McuType.MCU_TENCENT.getCode());
    }
}
