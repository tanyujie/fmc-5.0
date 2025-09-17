package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiClient;

import java.util.List;

/**
 * OPSMapper接口
 * 
 * @author lilinhai
 * @date 2024-07-26
 */
public interface BusiClientMapper 
{
    /**
     * 查询OPS
     * 
     * @param id OPSID
     * @return OPS
     */
    public BusiClient selectBusiClientById(Long id);

    /**
     * 查询OPS
     *
     * @param sn
     * @return OPS
     */
    public BusiClient selectBusiClientBySn(String sn);

    /**
     * 查询OPS列表
     * 
     * @param busiClient OPS
     * @return OPS集合
     */
    public List<BusiClient> selectBusiClientList(BusiClient busiClient);

    /**
     * 新增OPS
     * 
     * @param busiClient OPS
     * @return 结果
     */
    public int insertBusiClient(BusiClient busiClient);

    /**
     * 修改OPS
     * 
     * @param busiClient OPS
     * @return 结果
     */
    public int updateBusiClient(BusiClient busiClient);

    /**
     * 删除OPS
     * 
     * @param id OPSID
     * @return 结果
     */
    public int deleteBusiClientById(Long id);

    /**
     * 批量删除OPS
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiClientByIds(Long[] ids);
}
