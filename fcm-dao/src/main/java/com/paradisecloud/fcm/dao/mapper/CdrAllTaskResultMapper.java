package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.CdrAllTaskResult;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Description cdr全定时任务结果Mapper接口
 * @Author johnson liu
 * @Date 2021/6/8 0:13
 **/
public interface CdrAllTaskResultMapper {
    /**
     * 查询cdr定时任务结果
     *
     * @param id cdr定时任务结果ID
     * @return cdr定时任务结果
     */
    CdrAllTaskResult selectCdrAllTaskResultById(Long id);

    /**
     * 查询cdr定时任务结果列表
     *
     * @param cdrAllTaskResult cdr定时任务结果
     * @return cdr定时任务结果集合
     */
    List<CdrAllTaskResult> selectCdrAllTaskResultList(CdrAllTaskResult cdrAllTaskResult);

    /**
     * 新增cdr定时任务结果
     *
     * @param cdrAllTaskResult cdr定时任务结果
     * @return 结果
     */
    int insertCdrAllTaskResult(CdrAllTaskResult cdrAllTaskResult);

    /**
     * 修改cdr定时任务结果
     *
     * @param cdrAllTaskResult cdr定时任务结果
     * @return 结果
     */
    int updateCdrAllTaskResult(CdrAllTaskResult cdrAllTaskResult);

    /**
     * 更新当天统计结果
     * @param cdrAllTaskResult
     * @return
     */
    int updateCurrentDateTaskResult(CdrAllTaskResult cdrAllTaskResult);

    /**
     * 删除cdr定时任务结果
     *
     * @param id cdr定时任务结果ID
     * @return 结果
     */
    int deleteCdrAllTaskResultById(Long id);

    /**
     * 批量删除cdr定时任务结果
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteCdrAllTaskResultByIds(Long[] ids);

    /**
     * 查询当天该部门下的所有类型的记录
     *
     * @param deptId
     * @param startDate
     * @param endDate
     * @param reportTypes
     * @return
     */
    List<CdrAllTaskResult> selectByDateAndReportType( @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("reportTypes") Integer[] reportTypes);
}
