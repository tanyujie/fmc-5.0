package com.paradisecloud.fcm.service.impls;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.paradisecloud.fcm.dao.enums.CallLegEndReasonEnum;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryAllParticipantMapper;
import com.paradisecloud.fcm.dao.mapper.CdrCallLegEndAlarmMapper;
import com.paradisecloud.fcm.dao.mapper.CdrCallLegEndMediaInfoMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryAllParticipant;
import com.paradisecloud.fcm.dao.model.CdrCallLegEnd;
import com.paradisecloud.fcm.dao.model.CdrCallLegEndAlarm;
import com.paradisecloud.fcm.dao.model.CdrCallLegEndMediaInfo;
import com.paradisecloud.fcm.service.interfaces.IBusiHistoryAllParticipantService;

import javax.annotation.Resource;

/**
 * 历史会议的参会者Service业务层处理
 *
 * @author lilinhai
 * @date 2021-01-20
 */
@Service
public class BusiHistoryAllParticipantServiceImpl implements IBusiHistoryAllParticipantService {

    @Resource
    private BusiHistoryAllParticipantMapper busiHistoryAllParticipantMapper;
    @Resource
    private CdrCallLegEndAlarmMapper cdrCallLegEndAlarmMapper;
    @Resource
    private CdrCallLegEndMediaInfoMapper cdrCallLegEndMediaInfoMapper;

    /**
     * 查询历史会议的参会者
     *
     * @param id 历史会议的参会者ID
     * @return 历史会议的参会者
     */
    @Override
    public BusiHistoryAllParticipant selectBusiHistoryAllParticipantById(Long id) {
        return busiHistoryAllParticipantMapper.selectBusiHistoryAllParticipantById(id);
    }

    /**
     * 查询历史会议的参会者列表
     *
     * @param busiHistoryAllParticipant 历史会议的参会者
     * @return 历史会议的参会者
     */
    @Override
    public List<BusiHistoryAllParticipant> selectBusiHistoryAllParticipantList(BusiHistoryAllParticipant busiHistoryAllParticipant) {
        return busiHistoryAllParticipantMapper.selectBusiHistoryAllParticipantList(busiHistoryAllParticipant);
    }

    /**
     * 新增历史会议的参会者
     *
     * @param busiHistoryAllParticipant 历史会议的参会者
     * @return 结果
     */
    @Override
    public int insertBusiHistoryAllParticipant(BusiHistoryAllParticipant busiHistoryAllParticipant) {
        busiHistoryAllParticipant.setCreateTime(new Date());
        return busiHistoryAllParticipantMapper.insertBusiHistoryAllParticipant(busiHistoryAllParticipant);
    }

    /**
     * 修改历史会议的参会者
     *
     * @param busiHistoryAllParticipant 历史会议的参会者
     * @return 结果
     */
    @Override
    public int updateBusiHistoryAllParticipant(BusiHistoryAllParticipant busiHistoryAllParticipant) {
        busiHistoryAllParticipant.setUpdateTime(new Date());
        return busiHistoryAllParticipantMapper.updateBusiHistoryAllParticipant(busiHistoryAllParticipant);
    }

    /**
     * 批量删除历史会议的参会者
     *
     * @param ids 需要删除的历史会议的参会者ID
     * @return 结果
     */
    @Override
    public int deleteBusiHistoryAllParticipantByIds(Long[] ids) {
        return busiHistoryAllParticipantMapper.deleteBusiHistoryAllParticipantByIds(ids);
    }

    /**
     * 删除历史会议的参会者信息
     *
     * @param id 历史会议的参会者ID
     * @return 结果
     */
    @Override
    public int deleteBusiHistoryAllParticipantById(Long id) {
        return busiHistoryAllParticipantMapper.deleteBusiHistoryAllParticipantById(id);
    }

    /**
     * 通过历史会议ID查询该会议的与会者信息
     *
     * @param hisConferenceId
     * @return
     */
    @Override
    public List<Map<String, Object>> reportByHisConferenceId(String hisConferenceId, Boolean isJoin) {
        Assert.isTrue(StringUtils.hasText(hisConferenceId), "请先择对应的会议查询");
        List<BusiHistoryAllParticipant> list = busiHistoryAllParticipantMapper.selectHistoryParticipantDetailList(isJoin, Long.parseLong(hisConferenceId));
        List<Map<String, Object>> jsonArray = new ArrayList();
        if (!CollectionUtils.isEmpty(list)) {
            for (BusiHistoryAllParticipant historyParticipant : list) {
                CdrCallLegEnd cdrCallLegEnd = historyParticipant.getCdrCallLegEnd();
                List<CdrCallLegEndAlarm> alarmList = cdrCallLegEndAlarmMapper.selectCdrCallLegEndAlarmList(new CdrCallLegEndAlarm(historyParticipant.getCallLegId()));
                cdrCallLegEnd.setCdrCallLegEndAlarmList(alarmList);

                CdrCallLegEndMediaInfo callLegEndMediaInfo = new CdrCallLegEndMediaInfo();
                callLegEndMediaInfo.setCdrId(historyParticipant.getCallLegId());
                List<CdrCallLegEndMediaInfo> callLegEndMediaInfoList = cdrCallLegEndMediaInfoMapper.selectCdrCallLegEndMediaInfoList(callLegEndMediaInfo);
//                cdrCallLegEnd.setCallLegEndMediaInfoList(callLegEndMediaInfoList);
                Map<String, Object> jsonObject = new HashMap<>(4);

                if (historyParticipant.getOutgoingTime() != null && cdrCallLegEnd.getReason() == null) {
                    cdrCallLegEnd.setReason(CallLegEndReasonEnum.CALL_ENDED);
                }


                jsonObject.put("historyParticipant", historyParticipant);
                if (!ObjectUtils.isEmpty(callLegEndMediaInfoList)) {
                    jsonObject.put("media", buildDownAndUpLinkParam(callLegEndMediaInfoList));
                } else {
                    jsonObject.put("media", new ArrayList<>());
                }
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    /**
     * 将同一callLeg的媒体信息封装构建成一条Map信息
     *
     * @param callLegEndMediaInfoList
     * @return
     */
    private List<Map<String, Map<String, Object>>> buildDownAndUpLinkParam(List<CdrCallLegEndMediaInfo> callLegEndMediaInfoList) {
        Map<String, Map<String, Object>> mapResult = new HashMap<>(6);
        ArrayList<Map<String, Map<String, Object>>> list = new ArrayList();
        for (CdrCallLegEndMediaInfo cdrCallLegEndMediaInfo : callLegEndMediaInfoList) {
            Map<String, Object> map = new HashMap<>(10);

            map.put("codec", cdrCallLegEndMediaInfo.getCodec());
            if (cdrCallLegEndMediaInfo.getMaxSizeWidth() != null) {
                map.put("resolutionRatio", cdrCallLegEndMediaInfo.getMaxSizeWidth() + "x" + cdrCallLegEndMediaInfo.getMaxSizeHeight());
            }
            map.put("packetGapDensity", cdrCallLegEndMediaInfo.getPacketGapDensity());
            map.put("packetGapDuration", cdrCallLegEndMediaInfo.getPacketGapDuration());
            map.put("packetLossBurstsDensity", cdrCallLegEndMediaInfo.getPacketLossBurstsDensity());
            map.put("packetLossBurstsDuration", cdrCallLegEndMediaInfo.getPacketLossBurstsDuration());

            mapResult.put(cdrCallLegEndMediaInfo.getType(), map);
        }
        list.add(mapResult);
        return list;
    }
}
