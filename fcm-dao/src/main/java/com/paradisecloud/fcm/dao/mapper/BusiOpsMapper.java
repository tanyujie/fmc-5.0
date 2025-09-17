package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiOps;

import java.util.List;

/**
 * OPSMapper接口
 * 
 * @author lilinhai
 * @date 2024-07-26
 */
public interface BusiOpsMapper 
{
    /**
     * 查询OPS
     * 
     * @param id OPSID
     * @return OPS
     */
    public BusiOps selectBusiOpsById(Long id);

    /**
     * 查询OPS
     *
     * @param sn
     * @return OPS
     */
    public BusiOps selectBusiOpsBySn(String sn);

    /**
     * 查询OPS列表
     * 
     * @param busiOps OPS
     * @return OPS集合
     */
    public List<BusiOps> selectBusiOpsList(BusiOps busiOps);

    /**
     * 新增OPS
     * 
     * @param busiOps OPS
     * @return 结果
     */
    public int insertBusiOps(BusiOps busiOps);

    /**
     * 修改OPS
     * 
     * @param busiOps OPS
     * @return 结果
     */
    public int updateBusiOps(BusiOps busiOps);

    /**
     * 删除OPS
     * 
     * @param id OPSID
     * @return 结果
     */
    public int deleteBusiOpsById(Long id);

    /**
     * 批量删除OPS
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiOpsByIds(Long[] ids);
}
