package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 历史会议，每次挂断会保存该历史记录Mapper接口
 *
 * @author lilinhai
 * @date 2021-01-20
 */
public interface BusiHistoryConferenceMapper {
    /**
     * 查询历史会议，每次挂断会保存该历史记录
     *
     * @param id 历史会议，每次挂断会保存该历史记录ID
     * @return 历史会议，每次挂断会保存该历史记录
     */
    public BusiHistoryConference selectBusiHistoryConferenceById(Long id);
    
    /**
     * 根据callId查找历史会议
     * @author sinhy
     * @since 2021-12-15 10:14 
     * @param callId
     * @return BusiHistoryConference
     */
    BusiHistoryConference selectBusiHistoryConferenceByCallId(String callId);
    
    BusiHistoryConference selectBusiHistoryConferenceByCoSpaceId(String coSpaceId);
    
    
    /**
     * 查询历史会议，每次挂断会保存该历史记录列表
     *
     * @param busiHistoryConference 历史会议，每次挂断会保存该历史记录
     * @return 历史会议，每次挂断会保存该历史记录集合
     */
    public List<BusiHistoryConference> selectBusiHistoryConferenceList(BusiHistoryConference busiHistoryConference);

    /**
     * 查询未结束的历史会议
     *
     * @param mcuType MCU类型
     * @return
     */
    List<BusiHistoryConference> selectNotEndHistoryConferenceList(@Param("mcuType") String mcuType);

    /**
     * 新增历史会议，每次挂断会保存该历史记录
     *
     * @param busiHistoryConference 历史会议，每次挂断会保存该历史记录
     * @return 结果
     */
    public int insertBusiHistoryConference(BusiHistoryConference busiHistoryConference);

    /**
     * 修改历史会议，每次挂断会保存该历史记录
     *
     * @param busiHistoryConference 历史会议，每次挂断会保存该历史记录
     * @return 结果
     */
    public int updateBusiHistoryConference(BusiHistoryConference busiHistoryConference);

    /**
     * 删除历史会议，每次挂断会保存该历史记录
     *
     * @param id 历史会议，每次挂断会保存该历史记录ID
     * @return 结果
     */
    public int deleteBusiHistoryConferenceById(Long id);

    /**
     * 批量删除历史会议，每次挂断会保存该历史记录
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiHistoryConferenceByIds(Long[] ids);

    /**
     * @param conferenceNumber
     * @param updateTime
     * @return
     */
    int updateHistoryByConferenceNumber(@Param("conferenceNumber") Integer conferenceNumber, @Param("updateTime") Date updateTime);

    int updateHistoryConferenceByCoSpace(@Param("recordType") Integer recordType, @Param("busiHistoryConference") BusiHistoryConference busiHistoryConference);

    /**
     * 按会议模板统计模板使用次数和累计会议时长
     *
     * @param deptId
     * @param type   会议类型
     * @return
     */
    List<BusiHistoryConference> reportByDept(@Param("deptId") Long deptId, @Param("type") Integer type, @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 未入会的终端统计
     * @param deptId
     * @param type 会议类型
     * @param startTime
     * @param endTime
     * @return
     */
    List<Map<String,Object>> reportNoJoinConference(@Param("deptId") Long deptId, @Param("type") Integer type, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();
    
    /**
     * 根据searchVo查询会议历史记录
     * @param searchVo
     * @return
     */
    List<BusiHistoryConference> selectBySearchVo(ReportSearchVo searchVo);

    /**
     * 根据searchVo及历史会议id查询历史会议
     * @param searchVo
     * @param historyIdList
     * @return
     */
    List<BusiHistoryConference> selectBySearchVoAndHistoryId(@Param("searchVo")ReportSearchVo searchVo,@Param("historyIdList")List<Long> historyIdList);

    /**
     * 查询对象日未结束会议的部门
     *
     * @param endTime eg.2022-05-31 23:59:59
     * @return 返回eg.2022-05-31 当日未结束的会议
     */
    List<Long> selectNotEndCalcDayHistoryConferenceDeptList(@Param("endTime") Date endTime);

    /**
     * 查询部门对象当日（处理的前一日）未结束的会议
     *
     * @param deptId
     * @param endTime eg.2022-05-31 23:59:59
     * @return 返回eg.2022-05-31 当日未结束的会议
     */
    List<BusiHistoryConference> selectNotEndCalcDayHistoryConferenceList(@Param("deptId") Long deptId, @Param("endTime") Date endTime);

    /**
     * 查询部门对象日已结束的会议
     *
     * @param deptId
     * @param startTime eg.2022-05-31 00:00:00
     * @param endTime eg.2022-05-31 23:59:59
     * @return 返回eg.2022-05-31 当日未结束的会议
     */
    List<BusiHistoryConference> selectEndedCalcDayHistoryConferenceList(@Param("deptId") Long deptId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 删除日期前的记录
     *
     * @param beforeDate 删除该日期前的数据
     * @return 结果
     */
    int deleteHistory(@Param("beforeDate") Date beforeDate);

    /**
     * 更新场景历史
     *
     * @param busiHistoryConference 历史会议，每次挂断会保存该历史记录
     * @return 结果
     */
    int updateModeHistoryConference(BusiHistoryConference busiHistoryConference);

    /**
     * 删除场景历史
     *
     * @param busiHistoryConference 历史会议，每次挂断会保存该历史记录
     * @return 结果
     */
    int deleteModeHistoryConference(BusiHistoryConference busiHistoryConference);

    /**
     * 删除所有场景历史
     *
     * @return 结果
     */
    int deleteAllModeHistoryConference();

    /**
     * 查询场景会议历史
     *
     * @return 结果
     */
    List<BusiHistoryConference> selectBusiHistoryConferenceModeList();

    /**
     * 删除所有场景历史
     *
     * @return 结果
     */
    int deleteAllMinutesHistoryConference();
}
