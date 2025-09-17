package com.paradisecloud.fcm.service.interfaces;

import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.fcm.dao.model.BusiHistoryParticipant;
import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;
import java.util.Map;

/**
 * 历史会议的参会者Service接口
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
public interface IBusiHistoryParticipantService 
{
    /**
     * 查询历史会议的参会者
     * 
     * @param id 历史会议的参会者ID
     * @return 历史会议的参会者
     */
    public BusiHistoryParticipant selectBusiHistoryParticipantById(Long id);

    /**
     * 查询历史会议的参会者列表
     * 
     * @param busiHistoryParticipant 历史会议的参会者
     * @return 历史会议的参会者集合
     */
    public List<BusiHistoryParticipant> selectBusiHistoryParticipantList(BusiHistoryParticipant busiHistoryParticipant);

    /**
     * 新增历史会议的参会者
     * 
     * @param busiHistoryParticipant 历史会议的参会者
     * @return 结果
     */
    public int insertBusiHistoryParticipant(BusiHistoryParticipant busiHistoryParticipant);

    /**
     * 修改历史会议的参会者
     * 
     * @param busiHistoryParticipant 历史会议的参会者
     * @return 结果
     */
    public int updateBusiHistoryParticipant(BusiHistoryParticipant busiHistoryParticipant);

    /**
     * 批量删除历史会议的参会者
     * 
     * @param ids 需要删除的历史会议的参会者ID
     * @return 结果
     */
    public int deleteBusiHistoryParticipantByIds(Long[] ids);

    /**
     * 删除历史会议的参会者信息
     * 
     * @param id 历史会议的参会者ID
     * @return 结果
     */
    public int deleteBusiHistoryParticipantById(Long id);

    /**
     * 通过历史会议ID查询该会议的与会者信息
     * @param hisConferenceId
     * @return
     */
    PaginationData<Map<String,Object>> reportByHisConferenceId(String hisConferenceId, Boolean isJoin, Integer pageNum, Integer pageSize);

    /**
     * 查询会议历史记录页面
     * @param reportSearchVo
     * @return
     */
    PaginationData<Map<String, Object>> reportTerminalByHisConferenceIdPage(long hisConferenceId, long participantTerminalId, ReportSearchVo reportSearchVo);

    /**
     * 导出历史会议记录
     * @param ids
     * @return
     */
    HSSFWorkbook downHistoryExcel(Long ids);
}
