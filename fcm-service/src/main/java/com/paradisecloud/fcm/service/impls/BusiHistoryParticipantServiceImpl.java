package com.paradisecloud.fcm.service.impls;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.BusiHistoryParticipantTerminalVo;
import com.paradisecloud.fcm.dao.model.vo.OperationLogSearchVo;
import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;
import com.paradisecloud.fcm.service.interfaces.IBusiOperationLogService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.paradisecloud.fcm.dao.enums.CallLegEndReasonEnum;
import com.paradisecloud.fcm.service.interfaces.IBusiHistoryParticipantService;

import javax.annotation.Resource;

/**
 * 历史会议的参会者Service业务层处理
 *
 * @author lilinhai
 * @date 2021-01-20
 */
@Service
public class BusiHistoryParticipantServiceImpl implements IBusiHistoryParticipantService {
    @Autowired
    private BusiHistoryParticipantMapper busiHistoryParticipantMapper;
    @Autowired
    private CdrCallLegEndAlarmMapper cdrCallLegEndAlarmMapper;
    @Autowired
    private CdrCallLegEndMediaInfoMapper cdrCallLegEndMediaInfoMapper;
    @Resource
    private CdrCallLegStartMapper cdrCallLegStartMapper;
    @Resource
    private CdrCallLegEndMapper cdrCallLegEndMapper;
    @Resource
    private BusiHistoryConferenceMapper busiHistoryConferenceMapper;
    @Resource
    private BusiHistoryParticipantTerminalMapper busiHistoryParticipantTerminalMapper;
    @Resource
    private IBusiOperationLogService busiOperationLogService;

    /**
     * 查询历史会议的参会者
     *
     * @param id 历史会议的参会者ID
     * @return 历史会议的参会者
     */
    @Override
    public BusiHistoryParticipant selectBusiHistoryParticipantById(Long id) {
        return busiHistoryParticipantMapper.selectBusiHistoryParticipantById(id);
    }

    /**
     * 查询历史会议的参会者列表
     *
     * @param busiHistoryParticipant 历史会议的参会者
     * @return 历史会议的参会者
     */
    @Override
    public List<BusiHistoryParticipant> selectBusiHistoryParticipantList(BusiHistoryParticipant busiHistoryParticipant) {
        return busiHistoryParticipantMapper.selectBusiHistoryParticipantList(busiHistoryParticipant);
    }

    /**
     * 新增历史会议的参会者
     *
     * @param busiHistoryParticipant 历史会议的参会者
     * @return 结果
     */
    @Override
    public int insertBusiHistoryParticipant(BusiHistoryParticipant busiHistoryParticipant) {
        busiHistoryParticipant.setCreateTime(new Date());
        String remoteParty = busiHistoryParticipant.getRemoteParty();
        if (remoteParty.contains(":")) {
            remoteParty = remoteParty.substring(0, remoteParty.indexOf(":"));
        }
        BusiTerminal busiTerminal = TerminalCache.getInstance().getByRemoteParty(remoteParty);
        if (busiTerminal == null) {
            if (remoteParty.contains("@")) {
                try {
                    String[] remotePartyArr = remoteParty.split("@");
                    String credential = remotePartyArr[0];
                    String ip = remotePartyArr[1];
                    if (StringUtils.hasText(ip)) {
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
        if (busiTerminal != null) { {
            busiHistoryParticipant.setTerminalId(busiTerminal.getId());
        }}
        return busiHistoryParticipantMapper.insertBusiHistoryParticipant(busiHistoryParticipant);
    }

    /**
     * 修改历史会议的参会者
     *
     * @param busiHistoryParticipant 历史会议的参会者
     * @return 结果
     */
    @Override
    public int updateBusiHistoryParticipant(BusiHistoryParticipant busiHistoryParticipant) {
        busiHistoryParticipant.setUpdateTime(new Date());
        return busiHistoryParticipantMapper.updateBusiHistoryParticipant(busiHistoryParticipant);
    }

    /**
     * 批量删除历史会议的参会者
     *
     * @param ids 需要删除的历史会议的参会者ID
     * @return 结果
     */
    @Override
    public int deleteBusiHistoryParticipantByIds(Long[] ids) {
        return busiHistoryParticipantMapper.deleteBusiHistoryParticipantByIds(ids);
    }

    /**
     * 删除历史会议的参会者信息
     *
     * @param id 历史会议的参会者ID
     * @return 结果
     */
    @Override
    public int deleteBusiHistoryParticipantById(Long id) {
        return busiHistoryParticipantMapper.deleteBusiHistoryParticipantById(id);
    }

    /**
     * 通过历史会议ID查询该会议的与会者信息
     *
     * @param hisConferenceId
     * @return
     */
    @Override
    public PaginationData<Map<String,Object>> reportByHisConferenceId(String hisConferenceId, Boolean isJoin, Integer pageNum, Integer pageSize) {
        Assert.isTrue(StringUtils.hasText(hisConferenceId), "请先择对应的会议查询");
        PaginationData<Map<String, Object>> paginationData = new PaginationData<>();
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        if (pageSize > 50) {
            pageSize = 50;
        }
        long historyConferenceId = Long.valueOf(hisConferenceId);
        BusiHistoryConference busiHistoryConference = busiHistoryConferenceMapper.selectBusiHistoryConferenceById(historyConferenceId);
        PageHelper.startPage(pageNum, pageSize);
        List<BusiHistoryParticipantTerminal> busiHistoryParticipantTerminalList = busiHistoryParticipantTerminalMapper.selectHistoryParticipantTerminalDetailList(isJoin, historyConferenceId);
        if (!CollectionUtils.isEmpty(busiHistoryParticipantTerminalList)) {
            PageInfo<?> pageInfo = new PageInfo<>(busiHistoryParticipantTerminalList);
            for (BusiHistoryParticipantTerminal busiHistoryParticipantTerminal : busiHistoryParticipantTerminalList) {
                CdrCallLegEnd cdrCallLegEnd = null;
                CdrCallLegStart cdrCallLegStart = null;
                List<CdrCallLegEndAlarm> alarmList;
                Long terminalId = busiHistoryParticipantTerminal.getTerminalId();
                if (terminalId != null && terminalId > 0) {
                    List<CdrCallLegEnd> cdrCallLegEndList = cdrCallLegEndMapper.selectCallLegEndForConferenceTerminalById(historyConferenceId, terminalId);
                    if (cdrCallLegEndList.size() > 0) {
                        cdrCallLegEnd = cdrCallLegEndList.get(0);
                        if (cdrCallLegEnd == null) {
                            cdrCallLegEnd = new CdrCallLegEnd();
                        }
                    } else {
                        cdrCallLegEnd = new CdrCallLegEnd();
                    }

                    if (busiHistoryConference != null && busiHistoryConference.getConferenceEndTime() != null) {
                        if (cdrCallLegEnd.getReason() == null) {
                            cdrCallLegEnd.setReason(CallLegEndReasonEnum.CALL_ENDED);
                        }
                    } else {
                        List<BusiHistoryParticipant> busiHistoryParticipantListLast = busiHistoryParticipantMapper.selectBusiHistoryParticipantLastForConferenceTerminal(historyConferenceId, terminalId, null);
                        if (busiHistoryParticipantListLast.size() > 0) {
                            BusiHistoryParticipant busiHistoryParticipantLast = busiHistoryParticipantListLast.get(0);
                            if (busiHistoryParticipantLast.getOutgoingTime() == null) {
                                cdrCallLegEnd.setReason(null);
                            }
                        }
                    }
                    alarmList = cdrCallLegEndAlarmMapper.selectAlarmListForConferenceTerminalById(historyConferenceId, terminalId);
                    List<CdrCallLegStart> cdrCallLegStartList = cdrCallLegStartMapper.selectCallLegStartListForConferenceTerminalById(historyConferenceId, terminalId);
                    if (cdrCallLegStartList.size() > 0) {
                        cdrCallLegStart = cdrCallLegStartList.get(0);
                        if (cdrCallLegStart == null) {
                            cdrCallLegStart = new CdrCallLegStart();
                        }
                    } else {
                        cdrCallLegStart = new CdrCallLegStart();
                    }
                } else {
                    List<CdrCallLegEnd> cdrCallLegEndList = cdrCallLegEndMapper.selectCallLegEndForConferenceTerminalByRemoteParty(historyConferenceId, busiHistoryParticipantTerminal.getRemoteParty());
                    if (cdrCallLegEndList.size() > 0) {
                        cdrCallLegEnd = cdrCallLegEndList.get(0);
                        if (cdrCallLegEnd == null) {
                            cdrCallLegEnd = new CdrCallLegEnd();
                        }
                    } else {
                        cdrCallLegEnd = new CdrCallLegEnd();
                    }

                    if (busiHistoryConference != null && busiHistoryConference.getConferenceEndTime() != null) {
                        if (cdrCallLegEnd.getReason() == null) {
                            cdrCallLegEnd.setReason(CallLegEndReasonEnum.CALL_ENDED);
                        }
                    } else {
                        List<BusiHistoryParticipant> busiHistoryParticipantListLast = busiHistoryParticipantMapper.selectBusiHistoryParticipantLastForConferenceTerminal(historyConferenceId, null, busiHistoryParticipantTerminal.getRemoteParty());
                        if (busiHistoryParticipantListLast.size() > 0) {
                            BusiHistoryParticipant busiHistoryParticipantLast = busiHistoryParticipantListLast.get(0);
                            if (busiHistoryParticipantLast.getOutgoingTime() == null) {
                                cdrCallLegEnd.setReason(null);
                            }
                        }
                    }
                    alarmList = cdrCallLegEndAlarmMapper.selectAlarmListForConferenceTerminalByRemoteParty(historyConferenceId, busiHistoryParticipantTerminal.getRemoteParty());
                    List<CdrCallLegStart> cdrCallLegStartList = cdrCallLegStartMapper.selectCallLegStartListForConferenceTerminalByRemoteParty(historyConferenceId, busiHistoryParticipantTerminal.getRemoteParty());
                    if (cdrCallLegStartList.size() > 0) {
                        cdrCallLegStart = cdrCallLegStartList.get(0);
                        if (cdrCallLegStart == null) {
                            cdrCallLegStart = new CdrCallLegStart();
                        }
                    } else {
                        cdrCallLegStart = new CdrCallLegStart();
                    }
                }
                cdrCallLegEnd.setCdrCallLegEndAlarmList(alarmList);
                Map<String, Object> jsonObject = new HashMap<>(4);

                busiHistoryParticipantTerminal.setCdrCallLegEnd(cdrCallLegEnd);
                busiHistoryParticipantTerminal.setCdrCallLegStart(cdrCallLegStart);

                BusiHistoryParticipantTerminalVo busiHistoryParticipantTerminalVo = new BusiHistoryParticipantTerminalVo(busiHistoryParticipantTerminal);
                jsonObject.put("historyParticipant", busiHistoryParticipantTerminalVo);
                jsonObject.put("media", new ArrayList<>());
                paginationData.addRecord(jsonObject);
            }
            paginationData.setTotal(pageInfo.getTotal());
            paginationData.setSize(pageInfo.getSize());
            paginationData.setPage(pageInfo.getPageNum());
            return paginationData;
        }
        PageHelper.startPage(pageNum, pageSize);
        List<BusiHistoryParticipant> list = busiHistoryParticipantMapper.selectHistoryParticipantDetailList(isJoin, Long.parseLong(hisConferenceId));
        if (!CollectionUtils.isEmpty(list)) {
            PageInfo<?> pageInfo = new PageInfo<>(busiHistoryParticipantTerminalList);
            for (BusiHistoryParticipant historyParticipant : list) {
                CdrCallLegEnd cdrCallLegEnd = historyParticipant.getCdrCallLegEnd();
                List<CdrCallLegEndAlarm> alarmList = cdrCallLegEndAlarmMapper.selectCdrCallLegEndAlarmList(new CdrCallLegEndAlarm(historyParticipant.getCallLegId()));
                cdrCallLegEnd.setCdrCallLegEndAlarmList(alarmList);

                CdrCallLegEndMediaInfo callLegEndMediaInfo = new CdrCallLegEndMediaInfo();
                callLegEndMediaInfo.setCdrId(historyParticipant.getCallLegId());
//                List<CdrCallLegEndMediaInfo> callLegEndMediaInfoList = cdrCallLegEndMediaInfoMapper.selectCdrCallLegEndMediaInfoList(callLegEndMediaInfo);
//                cdrCallLegEnd.setCallLegEndMediaInfoList(callLegEndMediaInfoList);
                Map<String, Object> jsonObject = new HashMap<>(4);

                if (busiHistoryConference != null && busiHistoryConference.getConferenceEndTime() != null) {
                    if (cdrCallLegEnd.getReason() == null) {
                        cdrCallLegEnd.setReason(CallLegEndReasonEnum.CALL_ENDED);
                    }
                } else {
                    if (historyParticipant.getOutgoingTime() != null && cdrCallLegEnd.getReason() == null) {
                        cdrCallLegEnd.setReason(CallLegEndReasonEnum.CALL_ENDED);
                    }
                }

                BusiHistoryParticipantTerminalVo busiHistoryParticipantTerminalVo = new BusiHistoryParticipantTerminalVo(historyParticipant);
                jsonObject.put("historyParticipant", busiHistoryParticipantTerminalVo);
//                if (!ObjectUtils.isEmpty(callLegEndMediaInfoList)) {
//                    jsonObject.put("media", buildDownAndUpLinkParam(callLegEndMediaInfoList));
//                } else {
                    jsonObject.put("media", new ArrayList<>());
//                }
                paginationData.addRecord(jsonObject);
            }
            paginationData.setTotal(pageInfo.getTotal());
            paginationData.setSize(pageInfo.getSize());
            paginationData.setPage(pageInfo.getPageNum());
        }
        return paginationData;
    }

    /**
     * 查询会议历史记录页面
     *
     * @param hisConferenceId
     * @param participantTerminalId
     * @param reportSearchVo
     * @return
     */
    @Override
    public PaginationData<Map<String, Object>> reportTerminalByHisConferenceIdPage(long hisConferenceId, long participantTerminalId, ReportSearchVo reportSearchVo) {
        BusiHistoryConference busiHistoryConference = busiHistoryConferenceMapper.selectBusiHistoryConferenceById(hisConferenceId);
        BusiHistoryParticipantTerminal busiHistoryParticipantTerminal = busiHistoryParticipantTerminalMapper.selectBusiHistoryParticipantTerminalById(participantTerminalId);
        PaginationData<Map<String, Object>> paginationData = new PaginationData<>();
        if (busiHistoryParticipantTerminal != null) {
            if (reportSearchVo.getPageNum() == null) {
                reportSearchVo.setPageNum(1);
            }
            if (reportSearchVo.getPageSize() == null) {
                reportSearchVo.setPageSize(10);
            }
            PageHelper.startPage(reportSearchVo.getPageNum(), reportSearchVo.getPageSize());

            List<BusiHistoryParticipant> busiHistoryParticipantList;
            long terminalId = busiHistoryParticipantTerminal.getTerminalId();
            String remoteParty = busiHistoryParticipantTerminal.getRemoteParty();
            if (terminalId > 0) {
                busiHistoryParticipantList = busiHistoryParticipantMapper.selectHistoryParticipantDetailListByTerminal(hisConferenceId, terminalId, null);
            } else {
                busiHistoryParticipantList = busiHistoryParticipantMapper.selectHistoryParticipantDetailListByTerminal(hisConferenceId, null, remoteParty);
            }

            for (BusiHistoryParticipant busiHistoryParticipant : busiHistoryParticipantList) {
                CdrCallLegEnd cdrCallLegEnd = busiHistoryParticipant.getCdrCallLegEnd();
                List<CdrCallLegEndAlarm> alarmList = cdrCallLegEndAlarmMapper.selectCdrCallLegEndAlarmList(new CdrCallLegEndAlarm(busiHistoryParticipant.getCallLegId()));
                cdrCallLegEnd.setCdrCallLegEndAlarmList(alarmList);
                if (busiHistoryConference != null && busiHistoryConference.getConferenceEndTime() != null) {
                    if (cdrCallLegEnd.getReason() == null) {
                        cdrCallLegEnd.setReason(CallLegEndReasonEnum.CALL_ENDED);
                    }
                }
                Map<String, Object> participantMap = new HashMap<>();
                participantMap.put("historyParticipant", busiHistoryParticipant);
                participantMap.put("media", new ArrayList<>());
                paginationData.addRecord(participantMap);
            }

            PageInfo<?> pageInfo = new PageInfo<>(busiHistoryParticipantList);
            paginationData.setTotal(pageInfo.getTotal());
            paginationData.setSize(pageInfo.getSize());
            paginationData.setPage(pageInfo.getPageNum());
        }

        return paginationData;
    }

    @Override
    public HSSFWorkbook downHistoryExcel(Long id) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sheetTerminalName = null;
        BusiHistoryConference busiHistoryConference = busiHistoryConferenceMapper.selectBusiHistoryConferenceById(id);
        Integer endReasonsTypeTemp = busiHistoryConference.getEndReasonsType();
        if (endReasonsTypeTemp == null) {
            return null;
        }
        if (busiHistoryConference != null) {
            String conferenceNumber = busiHistoryConference.getNumber();
            String coSpace = busiHistoryConference.getCoSpace();
            if (com.paradisecloud.common.utils.StringUtils.isNotEmpty(coSpace) && coSpace.length() > conferenceNumber.length() && coSpace.length() < 22) {
                if (coSpace.contains("-")) {
                    busiHistoryConference.setNumber(coSpace.substring(0, coSpace.indexOf("-")));
                } else {
                    busiHistoryConference.setNumber(coSpace);
                }
            }
            List<Map<String, Object>> list = new ArrayList<>();
            PaginationData<Map<String, Object>> jsonArray = reportByHisConferenceId(String.valueOf(id), true, 1, 1000);
            list.addAll(jsonArray.getRecords());
            PaginationData<Map<String, Object>> jsonArrayFalse = reportByHisConferenceId(String.valueOf(id), false, 1, 1000);
            list.addAll(jsonArrayFalse.getRecords());

            List<BusiHistoryParticipantTerminalVo> busiHistoryParticipantTerminalVoJoinedList = new ArrayList<>();
            BusiHistoryParticipantTerminal busiHistoryParticipantTerminalJoined = new BusiHistoryParticipantTerminal();
            busiHistoryParticipantTerminalJoined.setHistoryConferenceId(id);
            List<BusiHistoryParticipantTerminal> busiHistoryParticipantTerminalList = busiHistoryParticipantTerminalMapper.selectHistoryParticipantTerminalDetailList(true, id);
            for (BusiHistoryParticipantTerminal busiHistoryParticipantTerminal : busiHistoryParticipantTerminalList) {
                CdrCallLegEnd cdrCallLegEnd = null;
                CdrCallLegStart cdrCallLegStart = null;
                List<CdrCallLegEndAlarm> alarmList;
                Long terminalId = busiHistoryParticipantTerminal.getTerminalId();
                if (terminalId != null && terminalId > 0) {
                    List<CdrCallLegEnd> cdrCallLegEndList = cdrCallLegEndMapper.selectCallLegEndForConferenceTerminalById(id, terminalId);
                    if (cdrCallLegEndList.size() > 0) {
                        cdrCallLegEnd = cdrCallLegEndList.get(0);
                        if (cdrCallLegEnd == null) {
                            cdrCallLegEnd = new CdrCallLegEnd();
                        }
                    } else {
                        cdrCallLegEnd = new CdrCallLegEnd();
                    }

                    if (busiHistoryConference != null && busiHistoryConference.getConferenceEndTime() != null) {
                        if (cdrCallLegEnd.getReason() == null) {
                            cdrCallLegEnd.setReason(CallLegEndReasonEnum.CALL_ENDED);
                        }
                    } else {
                        List<BusiHistoryParticipant> busiHistoryParticipantListLast = busiHistoryParticipantMapper.selectBusiHistoryParticipantLastForConferenceTerminal(id, terminalId, null);
                        if (busiHistoryParticipantListLast.size() > 0) {
                            BusiHistoryParticipant busiHistoryParticipantLast = busiHistoryParticipantListLast.get(0);
                            if (busiHistoryParticipantLast.getOutgoingTime() == null) {
                                cdrCallLegEnd.setReason(null);
                            }
                        }
                    }
                    alarmList = cdrCallLegEndAlarmMapper.selectAlarmListForConferenceTerminalById(id, terminalId);
                    List<CdrCallLegStart> cdrCallLegStartList = cdrCallLegStartMapper.selectCallLegStartListForConferenceTerminalById(id, terminalId);
                    if (cdrCallLegStartList.size() > 0) {
                        cdrCallLegStart = cdrCallLegStartList.get(0);
                        if (cdrCallLegStart == null) {
                            cdrCallLegStart = new CdrCallLegStart();
                        }
                    } else {
                        cdrCallLegStart = new CdrCallLegStart();
                    }
                } else {
                    List<CdrCallLegEnd> cdrCallLegEndList = cdrCallLegEndMapper.selectCallLegEndForConferenceTerminalByRemoteParty(id, busiHistoryParticipantTerminal.getRemoteParty());
                    if (cdrCallLegEndList.size() > 0) {
                        cdrCallLegEnd = cdrCallLegEndList.get(0);
                        if (cdrCallLegEnd == null) {
                            cdrCallLegEnd = new CdrCallLegEnd();
                        }
                    } else {
                        cdrCallLegEnd = new CdrCallLegEnd();
                    }

                    if (busiHistoryConference != null && busiHistoryConference.getConferenceEndTime() != null) {
                        if (cdrCallLegEnd.getReason() == null) {
                            cdrCallLegEnd.setReason(CallLegEndReasonEnum.CALL_ENDED);
                        }
                    } else {
                        List<BusiHistoryParticipant> busiHistoryParticipantListLast = busiHistoryParticipantMapper.selectBusiHistoryParticipantLastForConferenceTerminal(id, null, busiHistoryParticipantTerminal.getRemoteParty());
                        if (busiHistoryParticipantListLast.size() > 0) {
                            BusiHistoryParticipant busiHistoryParticipantLast = busiHistoryParticipantListLast.get(0);
                            if (busiHistoryParticipantLast.getOutgoingTime() == null) {
                                cdrCallLegEnd.setReason(null);
                            }
                        }
                    }
                    alarmList = cdrCallLegEndAlarmMapper.selectAlarmListForConferenceTerminalByRemoteParty(id, busiHistoryParticipantTerminal.getRemoteParty());
                    List<CdrCallLegStart> cdrCallLegStartList = cdrCallLegStartMapper.selectCallLegStartListForConferenceTerminalByRemoteParty(id, busiHistoryParticipantTerminal.getRemoteParty());
                    if (cdrCallLegStartList.size() > 0) {
                        cdrCallLegStart = cdrCallLegStartList.get(0);
                        if (cdrCallLegStart == null) {
                            cdrCallLegStart = new CdrCallLegStart();
                        }
                    } else {
                        cdrCallLegStart = new CdrCallLegStart();
                    }
                }
                cdrCallLegEnd.setCdrCallLegEndAlarmList(alarmList);
                busiHistoryParticipantTerminal.setCdrCallLegEnd(cdrCallLegEnd);
                busiHistoryParticipantTerminal.setCdrCallLegStart(cdrCallLegStart);

                BusiHistoryParticipantTerminalVo busiHistoryParticipantTerminalVo = new BusiHistoryParticipantTerminalVo(busiHistoryParticipantTerminal);
                busiHistoryParticipantTerminalVoJoinedList.add(busiHistoryParticipantTerminalVo);
            }

            busiHistoryConference.setDeviceNum(jsonArray.getSize());


            sheetTerminalName = busiHistoryConference.getNumber() + "_" + "参会终端";
            HSSFSheet sheetTerminal = workbook.createSheet(sheetTerminalName);

            String sheetTerminalNameNotJoin = busiHistoryConference.getNumber() + "_" + "未参会终端";
            HSSFSheet sheetTerminalNotJoin = workbook.createSheet(sheetTerminalNameNotJoin);

            HSSFRow rowTerminal = sheetTerminal.createRow(0);
            int rowNumTerminal = 1;
            String[] headersTerminal = {"设备名", "类型", "方向", "URl", "呼叫发起时间", "入会时间", "参会时长", "参会次数", "是否加密", "离线原因"};
            for (int i = 0; i < headersTerminal.length; i++) {
                HSSFCell cell = rowTerminal.createCell(i);
                HSSFRichTextString text = new HSSFRichTextString(headersTerminal[i]);
                cell.setCellValue(text);
                cell.setCellType(CellType.STRING);
            }
            for (BusiHistoryParticipantTerminalVo busiHistoryParticipant : busiHistoryParticipantTerminalVoJoinedList) {
                HSSFRow row1 = sheetTerminal.createRow(rowNumTerminal);
                row1.createCell(0).setCellValue(busiHistoryParticipant.getName());
                row1.createCell(1).setCellValue(busiHistoryParticipant.getMediaInfo().get("type").toString());
                row1.createCell(2).setCellValue(busiHistoryParticipant.getMediaInfo().get("direction").toString().equals("outgoing") ? "内呼" : "外呼");
                row1.createCell(3).setCellValue(busiHistoryParticipant.getRemoteParty());
                Date createTime = busiHistoryParticipant.getCreateTime();
                String format = simpleDateFormat.format(createTime);
                row1.createCell(4).setCellValue(format);
                row1.createCell(5).setCellValue(simpleDateFormat.format(busiHistoryParticipant.getJoinTime()));
                Integer durationSeconds = busiHistoryParticipant.getDurationSeconds();
                Integer hours = (int) Math.floor(durationSeconds / 3600);
                Integer minute = (int) Math.floor(durationSeconds % 3600 / 60);
                Integer s = (int) Math.floor(durationSeconds % 3600 % 60);
                String duration = String.format("%02d:%02d:%02d", hours, minute, s);
                row1.createCell(6).setCellValue(duration);
                row1.createCell(7).setCellValue(busiHistoryParticipant.getJoinedTimes());
                row1.createCell(8).setCellValue(busiHistoryParticipant.getMediaInfo().get("isEncrypted").toString());
                row1.createCell(9).setCellValue(busiHistoryParticipant.getCdrCallLegEnd().getReason().getDisplayName());
                rowNumTerminal++;
            }
        }

        //新增数据行，并且设置单元格数据
        int rowNum = 1;

        String[] headers = {"会议名", "会议号", "会议类型", "开始时间", "结束时间", "会议时长", "结束原因", "参会终端数"};
        //headers表示excel表中第一行的表头

        Date conferenceStartTime = busiHistoryConference.getConferenceStartTime();
        SimpleDateFormat simple = new SimpleDateFormat("yyyyMMddHHmmss");
        String startTime = simple.format(conferenceStartTime);
        HSSFSheet sheet = workbook.createSheet(busiHistoryConference.getName() + "_" + busiHistoryConference.getNumber() + "_" + startTime);
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
            cell.setCellType(CellType.STRING);
        }

        {
            HSSFRow row1 = sheet.createRow(rowNum);
            row1.createCell(0).setCellValue(busiHistoryConference.getName());
            row1.createCell(1).setCellValue(busiHistoryConference.getNumber());
            Integer type = busiHistoryConference.getType();
            String typeStr = type == 0 ? "模板会议" : type == 1 ? "预约会议" : "即时会议";
            row1.createCell(2).setCellValue(typeStr);
            row1.createCell(3).setCellValue(simpleDateFormat.format(busiHistoryConference.getConferenceStartTime()));
            row1.createCell(4).setCellValue(simpleDateFormat.format(busiHistoryConference.getConferenceEndTime()));
            Integer duration = busiHistoryConference.getDuration();
            Integer hours = (int) Math.floor(duration / 3600);
            Integer minute = (int) Math.floor(duration % 3600 / 60);
            Integer s = (int) Math.floor(duration % 3600 % 60);
            String format = String.format("%02d:%02d:%02d", hours, minute, s);
            row1.createCell(5).setCellValue(format);
            Integer endReasonsType = busiHistoryConference.getEndReasonsType();
            String endReasonsTypeStr = "空闲过长，自动结束";
            switch (endReasonsType) {
                case 1:
                    endReasonsTypeStr = "管理员挂断";
                    break;
                case 2:
                    endReasonsTypeStr = "到时自动结束";
                    break;
                case 3:
                    endReasonsTypeStr = "异常结束";
                default:
                    break;
            }
            row1.createCell(6).setCellValue(endReasonsTypeStr);
            row1.createCell(7).setCellValue(busiHistoryConference.getDeviceNum());
            rowNum++;
        }


        OperationLogSearchVo busiOperationLog = new OperationLogSearchVo();
        busiOperationLog.setHistoryConferenceId(id);
        busiOperationLog.setSort("asc");
        List<BusiOperationLog> busiOperationLogs = new ArrayList<>();
        busiOperationLogs.addAll(busiOperationLogService.selectBusiOperationLogList(busiOperationLog));
        if (busiOperationLogs != null && busiOperationLogs.size() > 0) {
            HSSFSheet sheetTemp = workbook.createSheet(busiHistoryConference.getNumber() + "_" + "操作日志记录表");

            //新增数据行，并且设置单元格数据

            int rowNumTemp = 1;

            String[] headersTemp = { "操作人员", "主机", "操作状态", "操作消息", "操作日期"};
            //headers表示excel表中第一行的表头

            HSSFRow rowTemp = sheetTemp.createRow(0);
            //在excel表中添加表头

            for(int i = 0; i< headersTemp.length; i++){
                HSSFCell cell = rowTemp.createCell(i);
                HSSFRichTextString text = new HSSFRichTextString(headersTemp[i]);
                cell.setCellValue(text);
                cell.setCellType(CellType.STRING);
            }

            //在表中存放查询到的数据放入对应的列
            for (BusiOperationLog excel : busiOperationLogs) {
                HSSFRow row1 = sheetTemp.createRow(rowNumTemp);
                row1.createCell(0).setCellValue(excel.getOperatorName());
                row1.createCell(1).setCellValue(excel.getIp());
                row1.createCell(2).setCellValue(excel.getActionResult() == 1 ? "成功" : "失败");
                row1.createCell(3).setCellValue(excel.getActionDetails());
                Date time = excel.getTime();
                String dateToString = DateUtil.convertDateToString(time, "yyyy-MM-dd hh:mm:ss");
                row1.createCell(4).setCellValue(dateToString);
                rowNumTemp++;
            }
        }

        return workbook;
    }

    /**
     * 将同一callLeg的媒体信息封装构建成一条Map信息
     * @param callLegEndMediaInfoList
     * @return
     */
    private List<Map<String,Map<String,Object>>> buildDownAndUpLinkParam(List<CdrCallLegEndMediaInfo> callLegEndMediaInfoList){
        Map<String,Map<String,Object>> mapResult = new HashMap<>(6);
        ArrayList<Map<String,Map<String,Object>>> list = new ArrayList();
        for (CdrCallLegEndMediaInfo cdrCallLegEndMediaInfo : callLegEndMediaInfoList) {
            Map<String,Object> map = new HashMap<>(10);

            map.put("codec",cdrCallLegEndMediaInfo.getCodec());
            if(cdrCallLegEndMediaInfo.getMaxSizeWidth()!=null){
                map.put("resolutionRatio",cdrCallLegEndMediaInfo.getMaxSizeWidth()+"x"+cdrCallLegEndMediaInfo.getMaxSizeHeight());
            }
            map.put("packetGapDensity",cdrCallLegEndMediaInfo.getPacketGapDensity());
            map.put("packetGapDuration",cdrCallLegEndMediaInfo.getPacketGapDuration());
            map.put("packetLossBurstsDensity",cdrCallLegEndMediaInfo.getPacketLossBurstsDensity());
            map.put("packetLossBurstsDuration",cdrCallLegEndMediaInfo.getPacketLossBurstsDuration());

            mapResult.put(cdrCallLegEndMediaInfo.getType(),map);
        }
        list.add(mapResult);
        return list;
    }
}
