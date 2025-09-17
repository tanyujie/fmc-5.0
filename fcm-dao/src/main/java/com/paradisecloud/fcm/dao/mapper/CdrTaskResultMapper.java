package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.CdrTaskResult;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Description cdr定时任务结果Mapper接口
 * @Author johnson liu
 * @Date 2021/6/8 0:13
 **/
public interface CdrTaskResultMapper {
    /**
     * 查询cdr定时任务结果
     *
     * @param id cdr定时任务结果ID
     * @return cdr定时任务结果
     */
    CdrTaskResult selectCdrTaskResultById(Long id);

    /**
     * 查询cdr定时任务结果列表
     *
     * @param cdrTaskResult cdr定时任务结果
     * @return cdr定时任务结果集合
     */
    List<CdrTaskResult> selectCdrTaskResultList(CdrTaskResult cdrTaskResult);

    /**
     * 新增cdr定时任务结果
     *
     * @param cdrTaskResult cdr定时任务结果
     * @return 结果
     */
    int insertCdrTaskResult(CdrTaskResult cdrTaskResult);

    /**
     * 修改cdr定时任务结果
     *
     * @param cdrTaskResult cdr定时任务结果
     * @return 结果
     */
    int updateCdrTaskResult(CdrTaskResult cdrTaskResult);

    /**
     * 更新当天统计结果
     * @param cdrTaskResult
     * @return
     */
    int updateCurrentDateTaskResult(CdrTaskResult cdrTaskResult);

    /**
     * 删除cdr定时任务结果
     *
     * @param id cdr定时任务结果ID
     * @return 结果
     */
    int deleteCdrTaskResultById(Long id);

    /**
     * 批量删除cdr定时任务结果
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteCdrTaskResultByIds(Long[] ids);

    /**
     * 查询当天该部门下的所有类型的记录
     *
     * @param deptId
     * @param startDate
     * @param endDate
     * @param reportTypes
     * @return
     */
    List<CdrTaskResult> selectByDateAndDeptAndReportType(@Param("deptId") Long deptId, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("reportTypes") Integer[] reportTypes);

    /**
     * 删除日期前的记录
     *
     * @param beforeDate 删除该日期前的数据
     * @return 结果
     */
    int deleteHistory(@Param("beforeDate") Date beforeDate);
}
