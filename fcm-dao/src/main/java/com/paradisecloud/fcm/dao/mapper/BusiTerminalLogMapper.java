package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiTerminalLog;

/**
 * 终端日志信息Mapper接口
 * 
 * @author lilinhai
 * @date 2021-10-13
 */
public interface BusiTerminalLogMapper 
{
    /**
     * 查询终端日志信息
     * 
     * @param id 终端日志信息ID
     * @return 终端日志信息
     */
    public BusiTerminalLog selectBusiTerminalLogById(Long id);

    /**
     * 查询终端日志信息列表
     * 
     * @param busiTerminalLog 终端日志信息
     * @return 终端日志信息集合
     */
    public List<BusiTerminalLog> selectBusiTerminalLogList(BusiTerminalLog busiTerminalLog);

    /**
     * 新增终端日志信息
     * 
     * @param busiTerminalLog 终端日志信息
     * @return 结果
     */
    public int insertBusiTerminalLog(BusiTerminalLog busiTerminalLog);

    /**
     * 修改终端日志信息
     * 
     * @param busiTerminalLog 终端日志信息
     * @return 结果
     */
    public int updateBusiTerminalLog(BusiTerminalLog busiTerminalLog);

    /**
     * 删除终端日志信息
     * 
     * @param id 终端日志信息ID
     * @return 结果
     */
    public int deleteBusiTerminalLogById(Long id);

    /**
     * 批量删除终端日志信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiTerminalLogByIds(Long[] ids);
}
