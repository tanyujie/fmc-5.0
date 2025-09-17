package com.paradisecloud.smc3.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiMcuSmc3;

import java.util.List;

/**
 * SMC3.0MCU终端信息Service接口
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
public interface IBusiMcuSmc3Service 
{
    /**
     * 查询SMC3.0MCU终端信息
     * 
     * @param id SMC3.0MCU终端信息ID
     * @return SMC3.0MCU终端信息
     */
    public BusiMcuSmc3 selectBusiMcuSmc3ById(Long id);

    /**
     * 查询SMC3.0MCU终端信息列表
     * 
     * @param busiMcuSmc3 SMC3.0MCU终端信息
     * @return SMC3.0MCU终端信息集合
     */
    public List<BusiMcuSmc3> selectBusiMcuSmc3List(BusiMcuSmc3 busiMcuSmc3);

    /**
     * 新增SMC3.0MCU终端信息
     * 
     * @param busiMcuSmc3 SMC3.0MCU终端信息
     * @return 结果
     */
    public int insertBusiMcuSmc3(BusiMcuSmc3 busiMcuSmc3);

    /**
     * 修改SMC3.0MCU终端信息
     * 
     * @param busiMcuSmc3 SMC3.0MCU终端信息
     * @return 结果
     */
    public int updateBusiMcuSmc3(BusiMcuSmc3 busiMcuSmc3);

    /**
     * 批量删除SMC3.0MCU终端信息
     * 
     * @param ids 需要删除的SMC3.0MCU终端信息ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3ByIds(Long[] ids);

    /**
     * 删除SMC3.0MCU终端信息信息
     * 
     * @param id SMC3.0MCU终端信息ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3ById(Long id);
}
