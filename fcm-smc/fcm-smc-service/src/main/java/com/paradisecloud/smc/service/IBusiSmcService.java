package com.paradisecloud.smc.service;

import java.util.List;
import com.paradisecloud.smc.dao.model.BusiSmc;

/**
 * smc信息Service接口
 * 
 * @author liuxilong
 * @date 2022-08-25
 */
public interface IBusiSmcService 
{
    /**
     * 查询smc信息
     * 
     * @param id smc信息ID
     * @return smc信息
     */
    public BusiSmc selectBusiSmcById(Long id);

    /**
     * 查询smc信息列表
     * 
     * @param busiSmc smc信息
     * @return smc信息集合
     */
    public List<BusiSmc> selectBusiSmcList(BusiSmc busiSmc);
    /**
     * 查询smc信息列表
     *
     * @param busiSmc smc信息
     * @return smc信息集合
     */
    public List<BusiSmc> selectBusiSmcListNoP(BusiSmc busiSmc);
    /**
     * 新增smc信息
     * 
     * @param busiSmc smc信息
     * @return 结果
     */
    public int insertBusiSmc(BusiSmc busiSmc);

    /**
     * 修改smc信息
     * 
     * @param busiSmc smc信息
     * @return 结果
     */
    public int updateBusiSmc(BusiSmc busiSmc);

    /**
     * 删除smc信息信息
     * 
     * @param id smc信息ID
     * @return 结果
     */
    public int deleteBusiSmcById(Long id);
}
