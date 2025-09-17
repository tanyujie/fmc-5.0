package com.paradisecloud.fcm.tencent.service2.impls;

import com.paradisecloud.fcm.dao.mapper.BusiMcuTencentMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuTencent;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiMcuTencentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Tencent.0MCU终端信息Service业务层处理
 *
 * @author lilinhai
 * @date 2022-09-19
 */
@Service
public class BusiMcuTencentServiceImpl implements IBusiMcuTencentService
{
    @Autowired
    private BusiMcuTencentMapper busiMcuTencentMapper;

    /**
     * 查询Tencent.0MCU终端信息
     *
     * @param id Tencent.0MCU终端信息ID
     * @return Tencent.0MCU终端信息
     */
    @Override
    public BusiMcuTencent selectBusiMcuTencentById(Long id)
    {
        return busiMcuTencentMapper.selectBusiMcuTencentById(id);
    }

    /**
     * 查询Tencent.0MCU终端信息列表
     *
     * @param busiMcuTencent Tencent.0MCU终端信息
     * @return Tencent.0MCU终端信息
     */
    @Override
    public List<BusiMcuTencent> selectBusiMcuTencentList(BusiMcuTencent busiMcuTencent)
    {
        return busiMcuTencentMapper.selectBusiMcuTencentList(busiMcuTencent);
    }

    /**
     * 新增Tencent.0MCU终端信息
     *
     * @param busiMcuTencent Tencent.0MCU终端信息
     * @return 结果
     */
    @Override
    public int insertBusiMcuTencent(BusiMcuTencent busiMcuTencent)
    {
        busiMcuTencent.setCreateTime(new Date());
        return busiMcuTencentMapper.insertBusiMcuTencent(busiMcuTencent);
    }

    /**
     * 修改Tencent.0MCU终端信息
     *
     * @param busiMcuTencent Tencent.0MCU终端信息
     * @return 结果
     */
    @Override
    public int updateBusiMcuTencent(BusiMcuTencent busiMcuTencent)
    {
        busiMcuTencent.setUpdateTime(new Date());
        return busiMcuTencentMapper.updateBusiMcuTencent(busiMcuTencent);
    }

    /**
     * 批量删除Tencent.0MCU终端信息
     *
     * @param ids 需要删除的Tencent.0MCU终端信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuTencentByIds(Long[] ids)
    {
        return busiMcuTencentMapper.deleteBusiMcuTencentByIds(ids);
    }

    /**
     * 删除Tencent.0MCU终端信息信息
     *
     * @param id Tencent.0MCU终端信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuTencentById(Long id)
    {
        return busiMcuTencentMapper.deleteBusiMcuTencentById(id);
    }
}
