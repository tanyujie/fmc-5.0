package com.paradisecloud.fcm.smc2.setvice2.impls;

import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc2Mapper;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiMcuSmc2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * SMC2.0MCU终端信息Service业务层处理
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
@Service
public class BusiMcuSmc2ServiceImpl implements IBusiMcuSmc2Service
{
    @Autowired
    private BusiMcuSmc2Mapper busiMcuSmc2Mapper;

    /**
     * 查询SMC2.0MCU终端信息
     * 
     * @param id SMC2.0MCU终端信息ID
     * @return SMC2.0MCU终端信息
     */
    @Override
    public BusiMcuSmc2 selectBusiMcuSmc2ById(Long id)
    {
        return busiMcuSmc2Mapper.selectBusiMcuSmc2ById(id);
    }

    /**
     * 查询SMC2.0MCU终端信息列表
     * 
     * @param busiMcuSmc2 SMC2.0MCU终端信息
     * @return SMC2.0MCU终端信息
     */
    @Override
    public List<BusiMcuSmc2> selectBusiMcuSmc2List(BusiMcuSmc2 busiMcuSmc2)
    {
        return busiMcuSmc2Mapper.selectBusiMcuSmc2List(busiMcuSmc2);
    }

    /**
     * 新增SMC2.0MCU终端信息
     * 
     * @param busiMcuSmc2 SMC2.0MCU终端信息
     * @return 结果
     */
    @Override
    public int insertBusiMcuSmc2(BusiMcuSmc2 busiMcuSmc2)
    {
        busiMcuSmc2.setCreateTime(new Date());
        return busiMcuSmc2Mapper.insertBusiMcuSmc2(busiMcuSmc2);
    }

    /**
     * 修改SMC2.0MCU终端信息
     * 
     * @param busiMcuSmc2 SMC2.0MCU终端信息
     * @return 结果
     */
    @Override
    public int updateBusiMcuSmc2(BusiMcuSmc2 busiMcuSmc2)
    {
        busiMcuSmc2.setUpdateTime(new Date());
        return busiMcuSmc2Mapper.updateBusiMcuSmc2(busiMcuSmc2);
    }

    /**
     * 批量删除SMC2.0MCU终端信息
     * 
     * @param ids 需要删除的SMC2.0MCU终端信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuSmc2ByIds(Long[] ids)
    {
        return busiMcuSmc2Mapper.deleteBusiMcuSmc2ByIds(ids);
    }

    /**
     * 删除SMC2.0MCU终端信息信息
     * 
     * @param id SMC2.0MCU终端信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuSmc2ById(Long id)
    {
        return busiMcuSmc2Mapper.deleteBusiMcuSmc2ById(id);
    }
}
