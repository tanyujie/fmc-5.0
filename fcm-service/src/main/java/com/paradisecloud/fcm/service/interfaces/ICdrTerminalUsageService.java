package com.paradisecloud.fcm.service.interfaces;

import com.paradisecloud.fcm.dao.model.CdrTerminalUsage;

import java.util.List;

/**
 * cdr使用情况Service接口
 * 
 * @author lilinhai
 * @date 2022-06-08
 */
public interface ICdrTerminalUsageService 
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
     * 新增cdr使用情况
     * 
     * @param cdrTerminalUsage cdr使用情况
     * @return 结果
     */
    public int insertCdrTerminalUsage(CdrTerminalUsage cdrTerminalUsage);

    /**
     * 修改cdr使用情况
     * 
     * @param cdrTerminalUsage cdr使用情况
     * @return 结果
     */
    int updateCdrTerminalUsage(CdrTerminalUsage cdrTerminalUsage);

    /**
     * 批量删除cdr使用情况
     * 
     * @param ids 需要删除的cdr使用情况ID
     * @return 结果
     */
    int deleteCdrTerminalUsageByIds(Long[] ids);

    /**
     * 删除cdr使用情况信息
     * 
     * @param id cdr使用情况ID
     * @return 结果
     */
    int deleteCdrTerminalUsageById(Long id);

    /**
     * 更新今天的
     *
     * @return
     */
    int updateCdrTerminalUsageToday();
}
