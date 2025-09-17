package com.paradisecloud.fcm.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiOperationLog;
import com.paradisecloud.fcm.dao.model.vo.OperationLogSearchVo;

import java.util.List;

/**
 * 操作日志记录Service接口
 *
 * @author lilinhai
 * @date 2023-11-27
 */
public interface IBusiOperationLogService
{
    /**
     * 查询操作日志记录
     *
     * @param id 操作日志记录ID
     * @return 操作日志记录
     */
    public BusiOperationLog selectBusiOperationLogById(Long id);

    /**
     * 查询操作日志记录列表
     *
     * @param busiOperationLog 操作日志记录
     * @return 操作日志记录集合
     */
    public List<BusiOperationLog> selectBusiOperationLogList(OperationLogSearchVo busiOperationLog);

    /**
     * 新增操作日志记录
     *
     * @param busiOperationLog 操作日志记录
     * @return 结果
     */
    public int insertBusiOperationLog(BusiOperationLog busiOperationLog);

    /**
     * 修改操作日志记录
     *
     * @param busiOperationLog 操作日志记录
     * @return 结果
     */
    public int updateBusiOperationLog(BusiOperationLog busiOperationLog);

    /**
     * 批量删除操作日志记录
     *
     * @param ids 需要删除的操作日志记录ID
     * @return 结果
     */
    public int deleteBusiOperationLogByIds(Long[] ids);

    /**
     * 删除操作日志记录信息
     *
     * @param id 操作日志记录ID
     * @return 结果
     */
    public int deleteBusiOperationLogById(Long id);
}
