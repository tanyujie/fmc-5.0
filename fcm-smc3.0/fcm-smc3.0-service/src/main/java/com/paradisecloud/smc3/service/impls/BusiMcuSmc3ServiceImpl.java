package com.paradisecloud.smc3.service.impls;

import java.util.List;
import java.util.Date;

import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc3Mapper;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3;
import com.paradisecloud.smc3.service.interfaces.IBusiMcuSmc3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * SMC3.0MCU终端信息Service业务层处理
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
@Service
public class BusiMcuSmc3ServiceImpl implements IBusiMcuSmc3Service
{
    @Autowired
    private BusiMcuSmc3Mapper busiMcuSmc3Mapper;

    /**
     * 查询SMC3.0MCU终端信息
     * 
     * @param id SMC3.0MCU终端信息ID
     * @return SMC3.0MCU终端信息
     */
    @Override
    public BusiMcuSmc3 selectBusiMcuSmc3ById(Long id)
    {
        return busiMcuSmc3Mapper.selectBusiMcuSmc3ById(id);
    }

    /**
     * 查询SMC3.0MCU终端信息列表
     * 
     * @param busiMcuSmc3 SMC3.0MCU终端信息
     * @return SMC3.0MCU终端信息
     */
    @Override
    public List<BusiMcuSmc3> selectBusiMcuSmc3List(BusiMcuSmc3 busiMcuSmc3)
    {
        return busiMcuSmc3Mapper.selectBusiMcuSmc3List(busiMcuSmc3);
    }

    /**
     * 新增SMC3.0MCU终端信息
     * 
     * @param busiMcuSmc3 SMC3.0MCU终端信息
     * @return 结果
     */
    @Override
    public int insertBusiMcuSmc3(BusiMcuSmc3 busiMcuSmc3)
    {
        busiMcuSmc3.setCreateTime(new Date());
        return busiMcuSmc3Mapper.insertBusiMcuSmc3(busiMcuSmc3);
    }

    /**
     * 修改SMC3.0MCU终端信息
     * 
     * @param busiMcuSmc3 SMC3.0MCU终端信息
     * @return 结果
     */
    @Override
    public int updateBusiMcuSmc3(BusiMcuSmc3 busiMcuSmc3)
    {
        busiMcuSmc3.setUpdateTime(new Date());
        return busiMcuSmc3Mapper.updateBusiMcuSmc3(busiMcuSmc3);
    }

    /**
     * 批量删除SMC3.0MCU终端信息
     * 
     * @param ids 需要删除的SMC3.0MCU终端信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuSmc3ByIds(Long[] ids)
    {
        return busiMcuSmc3Mapper.deleteBusiMcuSmc3ByIds(ids);
    }

    /**
     * 删除SMC3.0MCU终端信息信息
     * 
     * @param id SMC3.0MCU终端信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuSmc3ById(Long id)
    {
        return busiMcuSmc3Mapper.deleteBusiMcuSmc3ById(id);
    }
}
