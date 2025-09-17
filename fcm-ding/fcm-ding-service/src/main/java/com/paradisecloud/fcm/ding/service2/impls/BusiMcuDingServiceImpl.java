package com.paradisecloud.fcm.ding.service2.impls;

import com.paradisecloud.fcm.ding.service2.interfaces.IBusiMcuDingService;
import com.paradisecloud.fcm.dao.mapper.BusiMcuDingMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuDing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Ding.0MCU终端信息Service业务层处理
 *
 * @author lilinhai
 * @date 2022-09-19
 */
@Service
public class BusiMcuDingServiceImpl implements IBusiMcuDingService
{
    @Autowired
    private BusiMcuDingMapper busiMcuDingMapper;

    /**
     * 查询Ding.0MCU终端信息
     *
     * @param id Ding.0MCU终端信息ID
     * @return Ding.0MCU终端信息
     */
    @Override
    public BusiMcuDing selectBusiMcuDingById(Long id)
    {
        return busiMcuDingMapper.selectBusiMcuDingById(id);
    }

    /**
     * 查询Ding.0MCU终端信息列表
     *
     * @param busiMcuDing Ding.0MCU终端信息
     * @return Ding.0MCU终端信息
     */
    @Override
    public List<BusiMcuDing> selectBusiMcuDingList(BusiMcuDing busiMcuDing)
    {
        return busiMcuDingMapper.selectBusiMcuDingList(busiMcuDing);
    }

    /**
     * 新增Ding.0MCU终端信息
     *
     * @param busiMcuDing Ding.0MCU终端信息
     * @return 结果
     */
    @Override
    public int insertBusiMcuDing(BusiMcuDing busiMcuDing)
    {
        busiMcuDing.setCreateTime(new Date());
        return busiMcuDingMapper.insertBusiMcuDing(busiMcuDing);
    }

    /**
     * 修改Ding.0MCU终端信息
     *
     * @param busiMcuDing Ding.0MCU终端信息
     * @return 结果
     */
    @Override
    public int updateBusiMcuDing(BusiMcuDing busiMcuDing)
    {
        busiMcuDing.setUpdateTime(new Date());
        return busiMcuDingMapper.updateBusiMcuDing(busiMcuDing);
    }

    /**
     * 批量删除Ding.0MCU终端信息
     *
     * @param ids 需要删除的Ding.0MCU终端信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuDingByIds(Long[] ids)
    {
        return busiMcuDingMapper.deleteBusiMcuDingByIds(ids);
    }

    /**
     * 删除Ding.0MCU终端信息信息
     *
     * @param id Ding.0MCU终端信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuDingById(Long id)
    {
        return busiMcuDingMapper.deleteBusiMcuDingById(id);
    }
}
