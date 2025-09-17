package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiOperationLog;
import com.paradisecloud.fcm.dao.model.vo.OperationLogSearchVo;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

/**
 * 操作日志记录Mapper接口
 *
 * @author lilinhai
 * @date 2023-11-27
 */
public interface BusiOperationLogMapper
{
    /**
     * 查询第一条
     *
     * @return 操作日志记录
     */
    public BusiOperationLog selectBusiOperationLogFirst();

    /**
     * 查询第一条
     *
     * @return 操作日志记录
     */
    public Long selectBusiOperationLogCount(BusiOperationLog busiOperationLog);

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
    public List<BusiOperationLog> selectBusiOperationLogList(BusiOperationLog busiOperationLog);

    /**
     * 查询操作日志记录列表
     *
     * @param operationLogSearchVo 操作日志记录
     * @return 操作日志记录集合
     */
    public List<BusiOperationLog> selectBusiOperationLogListHistory(OperationLogSearchVo operationLogSearchVo);

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
     * 删除操作日志记录
     *
     * @param id 操作日志记录ID
     * @return 结果
     */
    public int deleteBusiOperationLogById(Long id);

    /**
     * 批量删除操作日志记录
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiOperationLogByIds(Long[] ids);

    /**
     * 拷贝表
     * @param tableName
     */
    void copyTable(@Param("tableName") String tableName);

    /**
     * 清空表
     * @param tableName
     */
    void clearTable(@Param("tableName") String tableName);
}