package com.paradisecloud.fcm.smc2.service;

import com.paradisecloud.fcm.dao.model.BusiSmc2;

import java.util.List;

/**
 * smc信息Service接口
 * 
 * @author lilinhai
 * @date 2023-05-17
 */
public interface IBusiSmc2Service 
{
    /**
     * 查询smc信息
     * 
     * @param id smc信息ID
     * @return smc信息
     */
    public BusiSmc2 selectBusiSmc2ById(Long id);

    /**
     * 查询smc信息列表
     * 
     * @param busiSmc2 smc信息
     * @return smc信息集合
     */
    public List<BusiSmc2> selectBusiSmc2List(BusiSmc2 busiSmc2);

    /**
     * 新增smc信息
     * 
     * @param busiSmc2 smc信息
     * @return 结果
     */
    public int insertBusiSmc2(BusiSmc2 busiSmc2);

    /**
     * 修改smc信息
     * 
     * @param busiSmc2 smc信息
     * @return 结果
     */
    public int updateBusiSmc2(BusiSmc2 busiSmc2);

    /**
     * 批量删除smc信息
     * 
     * @param ids 需要删除的smc信息ID
     * @return 结果
     */
    public int deleteBusiSmc2ByIds(Long[] ids);

    /**
     * 删除smc信息信息
     * 
     * @param id smc信息ID
     * @return 结果
     */
    public int deleteBusiSmc2ById(Long id);
}
