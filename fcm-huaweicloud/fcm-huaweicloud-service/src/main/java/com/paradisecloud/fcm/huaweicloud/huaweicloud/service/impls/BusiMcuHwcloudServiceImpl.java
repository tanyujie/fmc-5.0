package com.paradisecloud.fcm.huaweicloud.huaweicloud.service.impls;

import com.paradisecloud.fcm.dao.mapper.BusiMcuHwcloudMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiMcuHwcloudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Hwcloud.0MCU终端信息Service业务层处理
 *
 * @author lilinhai
 * @date 2022-09-19
 */
@Service
public class BusiMcuHwcloudServiceImpl implements IBusiMcuHwcloudService
{
    @Autowired
    private BusiMcuHwcloudMapper busiMcuHwcloudMapper;

    /**
     * 查询Hwcloud.0MCU终端信息
     *
     * @param id Hwcloud.0MCU终端信息ID
     * @return Hwcloud.0MCU终端信息
     */
    @Override
    public BusiMcuHwcloud selectBusiMcuHwcloudById(Long id)
    {
        return busiMcuHwcloudMapper.selectBusiMcuHwcloudById(id);
    }

    /**
     * 查询Hwcloud.0MCU终端信息列表
     *
     * @param busiMcuHwcloud Hwcloud.0MCU终端信息
     * @return Hwcloud.0MCU终端信息
     */
    @Override
    public List<BusiMcuHwcloud> selectBusiMcuHwcloudList(BusiMcuHwcloud busiMcuHwcloud)
    {
        return busiMcuHwcloudMapper.selectBusiMcuHwcloudList(busiMcuHwcloud);
    }

    /**
     * 新增Hwcloud.0MCU终端信息
     *
     * @param busiMcuHwcloud Hwcloud.0MCU终端信息
     * @return 结果
     */
    @Override
    public int insertBusiMcuHwcloud(BusiMcuHwcloud busiMcuHwcloud)
    {
        busiMcuHwcloud.setCreateTime(new Date());
        return busiMcuHwcloudMapper.insertBusiMcuHwcloud(busiMcuHwcloud);
    }

    /**
     * 修改Hwcloud.0MCU终端信息
     *
     * @param busiMcuHwcloud Hwcloud.0MCU终端信息
     * @return 结果
     */
    @Override
    public int updateBusiMcuHwcloud(BusiMcuHwcloud busiMcuHwcloud)
    {
        busiMcuHwcloud.setUpdateTime(new Date());
        return busiMcuHwcloudMapper.updateBusiMcuHwcloud(busiMcuHwcloud);
    }

    /**
     * 批量删除Hwcloud.0MCU终端信息
     *
     * @param ids 需要删除的Hwcloud.0MCU终端信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuHwcloudByIds(Long[] ids)
    {
        return busiMcuHwcloudMapper.deleteBusiMcuHwcloudByIds(ids);
    }

    /**
     * 删除Hwcloud.0MCU终端信息信息
     *
     * @param id Hwcloud.0MCU终端信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuHwcloudById(Long id)
    {
        return busiMcuHwcloudMapper.deleteBusiMcuHwcloudById(id);
    }
}
