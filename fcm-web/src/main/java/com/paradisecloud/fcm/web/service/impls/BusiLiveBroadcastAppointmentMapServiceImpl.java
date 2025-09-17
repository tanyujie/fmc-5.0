package com.paradisecloud.fcm.web.service.impls;

import java.util.List;
import java.util.Date;

import com.paradisecloud.fcm.dao.mapper.BusiLiveBroadcastAppointmentMapMapper;
import com.paradisecloud.fcm.dao.model.BusiLiveBroadcastAppointmentMap;
import com.paradisecloud.fcm.web.service.interfaces.IBusiLiveBroadcastAppointmentMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 直播会议对应Service业务层处理
 *
 * @author lilinhai
 * @date 2024-05-07
 */
@Service
public class BusiLiveBroadcastAppointmentMapServiceImpl implements IBusiLiveBroadcastAppointmentMapService
{
    @Autowired
    private BusiLiveBroadcastAppointmentMapMapper busiLiveBroadcastAppointmentMapMapper;

    /**
     * 查询直播会议对应
     *
     * @param id 直播会议对应ID
     * @return 直播会议对应
     */
    @Override
    public BusiLiveBroadcastAppointmentMap selectBusiLiveBroadcastAppointmentMapById(Long id)
    {
        return busiLiveBroadcastAppointmentMapMapper.selectBusiLiveBroadcastAppointmentMapById(id);
    }

    /**
     * 查询直播会议对应列表
     *
     * @param busiLiveBroadcastAppointmentMap 直播会议对应
     * @return 直播会议对应
     */
    @Override
    public List<BusiLiveBroadcastAppointmentMap> selectBusiLiveBroadcastAppointmentMapList(BusiLiveBroadcastAppointmentMap busiLiveBroadcastAppointmentMap)
    {
        return busiLiveBroadcastAppointmentMapMapper.selectBusiLiveBroadcastAppointmentMapList(busiLiveBroadcastAppointmentMap);
    }

    /**
     * 新增直播会议对应
     *
     * @param busiLiveBroadcastAppointmentMap 直播会议对应
     * @return 结果
     */
    @Override
    public int insertBusiLiveBroadcastAppointmentMap(BusiLiveBroadcastAppointmentMap busiLiveBroadcastAppointmentMap)
    {
        busiLiveBroadcastAppointmentMap.setCreateTime(new Date());
        return busiLiveBroadcastAppointmentMapMapper.insertBusiLiveBroadcastAppointmentMap(busiLiveBroadcastAppointmentMap);
    }

    /**
     * 修改直播会议对应
     *
     * @param busiLiveBroadcastAppointmentMap 直播会议对应
     * @return 结果
     */
    @Override
    public int updateBusiLiveBroadcastAppointmentMap(BusiLiveBroadcastAppointmentMap busiLiveBroadcastAppointmentMap)
    {
        busiLiveBroadcastAppointmentMap.setUpdateTime(new Date());
        return busiLiveBroadcastAppointmentMapMapper.updateBusiLiveBroadcastAppointmentMap(busiLiveBroadcastAppointmentMap);
    }

    /**
     * 批量删除直播会议对应
     *
     * @param ids 需要删除的直播会议对应ID
     * @return 结果
     */
    @Override
    public int deleteBusiLiveBroadcastAppointmentMapByIds(Long[] ids)
    {
        return busiLiveBroadcastAppointmentMapMapper.deleteBusiLiveBroadcastAppointmentMapByIds(ids);
    }

    /**
     * 删除直播会议对应信息
     *
     * @param id 直播会议对应ID
     * @return 结果
     */
    @Override
    public int deleteBusiLiveBroadcastAppointmentMapById(Long id)
    {
        return busiLiveBroadcastAppointmentMapMapper.deleteBusiLiveBroadcastAppointmentMapById(id);
    }
}
