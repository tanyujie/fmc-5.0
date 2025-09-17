package com.paradisecloud.fcm.smc2.setvice2.interfaces;


import com.paradisecloud.fcm.dao.model.BusiMcuSmc2;

import java.util.List;

/**
 * SMC2.0MCU终端信息Service接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface IBusiMcuSmc2Service 
{
    /**
     * 查询SMC2.0MCU终端信息
     * 
     * @param id SMC2.0MCU终端信息ID
     * @return SMC2.0MCU终端信息
     */
    public BusiMcuSmc2 selectBusiMcuSmc2ById(Long id);

    /**
     * 查询SMC2.0MCU终端信息列表
     * 
     * @param busiMcuSmc2 SMC2.0MCU终端信息
     * @return SMC2.0MCU终端信息集合
     */
    public List<BusiMcuSmc2> selectBusiMcuSmc2List(BusiMcuSmc2 busiMcuSmc2);

    /**
     * 新增SMC2.0MCU终端信息
     * 
     * @param busiMcuSmc2 SMC2.0MCU终端信息
     * @return 结果
     */
    public int insertBusiMcuSmc2(BusiMcuSmc2 busiMcuSmc2);

    /**
     * 修改SMC2.0MCU终端信息
     * 
     * @param busiMcuSmc2 SMC2.0MCU终端信息
     * @return 结果
     */
    public int updateBusiMcuSmc2(BusiMcuSmc2 busiMcuSmc2);

    /**
     * 批量删除SMC2.0MCU终端信息
     * 
     * @param ids 需要删除的SMC2.0MCU终端信息ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2ByIds(Long[] ids);

    /**
     * 删除SMC2.0MCU终端信息信息
     * 
     * @param id SMC2.0MCU终端信息ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2ById(Long id);
}
