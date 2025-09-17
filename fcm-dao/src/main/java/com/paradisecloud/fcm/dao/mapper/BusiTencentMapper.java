package com.paradisecloud.fcm.dao.mapper;


import com.paradisecloud.fcm.dao.model.BusiTencent;

import java.util.List;

/**
 * smc2信息Mapper接口
 * 
 * @author lilinhai
 * @date 2023-07-05
 */
public interface BusiTencentMapper 
{
    /**
     * 查询smc2信息
     * 
     * @param id smc2信息ID
     * @return smc2信息
     */
    public BusiTencent selectBusiTencentById(Long id);

    /**
     * 查询smc2信息列表
     * 
     * @param busiTencent smc2信息
     * @return smc2信息集合
     */
    public List<BusiTencent> selectBusiTencentList(BusiTencent busiTencent);

    /**
     * 新增smc2信息
     * 
     * @param busiTencent smc2信息
     * @return 结果
     */
    public int insertBusiTencent(BusiTencent busiTencent);

    /**
     * 修改smc2信息
     * 
     * @param busiTencent smc2信息
     * @return 结果
     */
    public int updateBusiTencent(BusiTencent busiTencent);

    /**
     * 删除smc2信息
     * 
     * @param id smc2信息ID
     * @return 结果
     */
    public int deleteBusiTencentById(Long id);

    /**
     * 批量删除smc2信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiTencentByIds(Long[] ids);
}
