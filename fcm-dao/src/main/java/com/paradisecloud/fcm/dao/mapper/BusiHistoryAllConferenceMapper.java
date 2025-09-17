package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiHistoryAllConference;
import com.paradisecloud.fcm.dao.model.RecordCount;
import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 历史全会议，每次挂断会保存该历史记录Mapper接口
 *
 * @author lilinhai
 * @date 2021-01-20
 */
public interface BusiHistoryAllConferenceMapper {
    /**
     * 查询历史会议，每次挂断会保存该历史记录
     *
     * @param id 历史会议，每次挂断会保存该历史记录ID
     * @return 历史会议，每次挂断会保存该历史记录
     */
    BusiHistoryAllConference selectBusiHistoryAllConferenceById(Long id);
    
    /**
     * 根据callId查找历史会议
     * @author sinhy
     * @since 2021-12-15 10:14 
     * @param callId
     * @return BusiHistoryAllConference
     */
    BusiHistoryAllConference selectBusiHistoryAllConferenceByCallId(String callId);

    /**
     *
     * @param coSpaceId
     * @return
     */
    BusiHistoryAllConference selectBusiHistoryAllConferenceByCoSpaceId(String coSpaceId);
    
    
    /**
     * 查询历史会议，每次挂断会保存该历史记录列表
     *
     * @param busiHistoryAllConference 历史会议，每次挂断会保存该历史记录
     * @return 历史会议，每次挂断会保存该历史记录集合
     */
    List<BusiHistoryAllConference> selectBusiHistoryAllConferenceList(BusiHistoryAllConference busiHistoryAllConference);

    /**
     *
     * @return
     */
    List<BusiHistoryAllConference> selectNotEndHistoryConferenceList();

    /**
     * 根据coSpace获取正在会议中的历史会议
     *
     * @param coSpaceId
     * @return
     */
    BusiHistoryAllConference selectNotEndHistoryAllConferenceByCoSpaceId(String coSpaceId);

    /**
     * 新增历史会议，每次挂断会保存该历史记录
     *
     * @param busiHistoryAllConference 历史会议，每次挂断会保存该历史记录
     * @return 结果
     */
    int insertBusiHistoryAllConference(BusiHistoryAllConference busiHistoryAllConference);

    /**
     * 修改历史会议，每次挂断会保存该历史记录
     *
     * @param busiHistoryAllConference 历史会议，每次挂断会保存该历史记录
     * @return 结果
     */
    int updateBusiHistoryAllConference(BusiHistoryAllConference busiHistoryAllConference);

    /**
     * 删除历史会议，每次挂断会保存该历史记录
     *
     * @param id 历史会议，每次挂断会保存该历史记录ID
     * @return 结果
     */
    int deleteBusiHistoryAllConferenceById(Long id);

    /**
     * 批量删除历史会议，每次挂断会保存该历史记录
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteBusiHistoryAllConferenceByIds(Long[] ids);

    /**
     * @param conferenceNumber
     * @param updateTime
     * @return
     */
    int updateHistoryByConferenceNumber(@Param("conferenceNumber") Integer conferenceNumber, @Param("updateTime") Date updateTime);

    /**
     *
     * @param recordType
     * @param busiHistoryAllConference
     * @return
     */
    int updateHistoryConferenceByCoSpace(@Param("recordType") Integer recordType, @Param("busiHistoryAllConference") BusiHistoryAllConference busiHistoryAllConference);

    /**
     * 按会议模板统计模板使用次数和累计会议时长
     *
     * @param type   会议类型
     * @return
     */
    List<BusiHistoryAllConference> report(@Param("type") Integer type, @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 未入会的终端统计
     * @param type 会议类型
     * @param startTime
     * @param endTime
     * @return
     */
    List<Map<String,Object>> reportNoJoinConference(@Param("type") Integer type, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54
     * @return List<RecordCount>
     */
    List<RecordCount> getRecordCounts();
    
    /**
     * 根据searchVo查询会议历史记录
     * @param searchVo
     * @return
     */
    List<BusiHistoryAllConference> selectBySearchVo(ReportSearchVo searchVo);

    /**
     * 根据searchVo及历史会议id查询历史会议
     * @param searchVo
     * @param historyIdList
     * @return
     */
    List<BusiHistoryAllConference> selectBySearchVoAndHistoryId(@Param("searchVo")ReportSearchVo searchVo,@Param("historyIdList")List<Long> historyIdList);
}
