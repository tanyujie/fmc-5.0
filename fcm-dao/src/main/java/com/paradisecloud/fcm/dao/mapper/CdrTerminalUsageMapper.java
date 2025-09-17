package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.CdrTerminalUsage;
import com.paradisecloud.fcm.dao.model.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * cdr使用情况Mapper接口
 * 
 * @author lilinhai
 * @date 2022-06-08
 */
public interface CdrTerminalUsageMapper 
{
    /**
     * 查询cdr使用情况
     * 
     * @param id cdr使用情况ID
     * @return cdr使用情况
     */
    CdrTerminalUsage selectCdrTerminalUsageById(Long id);

    /**
     * 查询cdr使用情况列表
     * 
     * @param cdrTerminalUsage cdr使用情况
     * @return cdr使用情况集合
     */
    List<CdrTerminalUsage> selectCdrTerminalUsageList(CdrTerminalUsage cdrTerminalUsage);

    /**
     * 查询cdr使用情况列表
     *
     * @param reportSearchVo
     * @return cdr使用情况集合
     */
    List<CdrTerminalUsage> searchCdrTerminalUsageList(ReportSearchVo reportSearchVo);

    /**
     * 新增cdr使用情况
     * 
     * @param cdrTerminalUsage cdr使用情况
     * @return 结果
     */
    int insertCdrTerminalUsage(CdrTerminalUsage cdrTerminalUsage);

    /**
     * 修改cdr使用情况
     * 
     * @param cdrTerminalUsage cdr使用情况
     * @return 结果
     */
    int updateCdrTerminalUsage(CdrTerminalUsage cdrTerminalUsage);

    /**
     * 删除cdr使用情况
     * 
     * @param id cdr使用情况ID
     * @return 结果
     */
    int deleteCdrTerminalUsageById(Long id);

    /**
     * 批量删除cdr使用情况
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteCdrTerminalUsageByIds(Long[] ids);

    /**
     * 查询终端报告终端
     *
     * @param deptId 部门ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param terminalId 终端ID
     * @param terminalName 终端名
     * @param terminalType 终端类型
     * @return
     */
    List<TerminalReportTerminalVo> selectTerminalListForReport(@Param("deptId") Long deptId, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("terminalId") Long terminalId, @Param("terminalName") String terminalName, @Param("terminalType") Integer terminalType);

    /**
     * 查询终端报告终端概况
     *
     * @param deptId 部门ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param terminalId 终端ID
     * @return
     */
    TerminalReportTerminalOverviewVo selectTerminalOverviewForReport(@Param("deptId") Long deptId, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("terminalId")  Long terminalId);

    /**
     * 查询终端报告终端使用数
     *
     * @param deptId 部门ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return
     */
    List<DeptTerminalUseCountVo> selectTerminalUseCountForReport(@Param("deptId") Long deptId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 查询终端报告终端类型使用数
     *
     * @param deptId 部门ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return
     */
    List<TerminalTypeCountVo> selectTerminalTypeUseCountForReport(@Param("deptId") Long deptId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 查询cdr使用情况列表定时任务
     *
     * @param startId 开始主键ID
     * @param date 日期
     * @return
     */
    List<CdrTerminalUsage> selectCdrTerminalUsageListForJob(@Param("startId") Long startId, @Param("date") Date date);

    /**
     * 删除日期前的记录
     *
     * @param beforeDate 删除该日期前的数据
     * @return 结果
     */
    int deleteHistory(@Param("beforeDate") Date beforeDate);
}
