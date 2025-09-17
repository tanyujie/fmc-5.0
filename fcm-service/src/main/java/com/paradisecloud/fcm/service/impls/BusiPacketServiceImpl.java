package com.paradisecloud.fcm.service.impls;

import java.util.Collection;
import java.util.List;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.dao.mapper.BusiPacketMapper;
import com.paradisecloud.fcm.dao.model.BusiPacket;
import com.paradisecloud.fcm.service.interfaces.IBusiPacketService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 抓包服务器Service业务层处理
 * 
 * @author lilinhai
 * @date 2024-09-04
 */
@Service
public class BusiPacketServiceImpl implements IBusiPacketService
{
    @Autowired
    private BusiPacketMapper busiPacketMapper;

    /**
     * 查询抓包服务器
     * 
     * @param id 抓包服务器ID
     * @return 抓包服务器
     */
    @Override
    public BusiPacket selectBusiPacketById(Integer id)
    {
        return busiPacketMapper.selectBusiPacketById(id);
    }

    /**
     * 查询抓包服务器列表
     * 
     * @param busiPacket 抓包服务器
     * @return 抓包服务器
     */
    @Override
    public List<BusiPacket> selectBusiPacketList(BusiPacket busiPacket)
    {
        return busiPacketMapper.selectBusiPacketList(busiPacket);
    }

    /**
     * 新增抓包服务器
     * 
     * @param busiPacket 抓包服务器
     * @return 结果
     */
    @Override
    public int insertBusiPacket(BusiPacket busiPacket)

    {
        List<BusiPacket> busiPackets = busiPacketMapper.selectBusiPacketList(new BusiPacket());
        if(CollectionUtils.isNotEmpty(busiPackets)){
            throw new CustomException("抓包服务器已存在");
        }
        return busiPacketMapper.insertBusiPacket(busiPacket);
    }

    /**
     * 修改抓包服务器
     * 
     * @param busiPacket 抓包服务器
     * @return 结果
     */
    @Override
    public int updateBusiPacket(BusiPacket busiPacket)
    {
        return busiPacketMapper.updateBusiPacket(busiPacket);
    }

    /**
     * 批量删除抓包服务器
     * 
     * @param ids 需要删除的抓包服务器ID
     * @return 结果
     */
    @Override
    public int deleteBusiPacketByIds(Integer[] ids)
    {
        return busiPacketMapper.deleteBusiPacketByIds(ids);
    }

    /**
     * 删除抓包服务器信息
     * 
     * @param id 抓包服务器ID
     * @return 结果
     */
    @Override
    public int deleteBusiPacketById(Integer id)
    {
        return busiPacketMapper.deleteBusiPacketById(id);
    }
}
