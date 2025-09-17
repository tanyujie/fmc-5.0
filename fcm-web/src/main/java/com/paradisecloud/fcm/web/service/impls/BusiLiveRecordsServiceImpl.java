package com.paradisecloud.fcm.web.service.impls;

import java.util.List;
import java.util.Date;

import com.paradisecloud.fcm.dao.mapper.BusiLiveRecordsMapper;
import com.paradisecloud.fcm.dao.model.BusiLiveRecords;
import com.paradisecloud.fcm.web.service.interfaces.IBusiLiveRecordsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 直播录制文件记录Service业务层处理
 * 
 * @author lilinhai
 * @date 2024-05-21
 */
@Service
public class BusiLiveRecordsServiceImpl implements IBusiLiveRecordsService
{
    @Resource
    private BusiLiveRecordsMapper busiLiveRecordsMapper;

    /**
     * 查询直播录制文件记录
     * 
     * @param id 直播录制文件记录ID
     * @return 直播录制文件记录
     */
    @Override
    public BusiLiveRecords selectBusiLiveRecordsById(Long id)
    {
        return busiLiveRecordsMapper.selectBusiLiveRecordsById(id);
    }

    /**
     * 查询直播录制文件记录列表
     * 
     * @param busiLiveRecords 直播录制文件记录
     * @return 直播录制文件记录
     */
    @Override
    public List<BusiLiveRecords> selectBusiLiveRecordsList(BusiLiveRecords busiLiveRecords)
    {
        return busiLiveRecordsMapper.selectBusiLiveRecordsList(busiLiveRecords);
    }

    /**
     * 新增直播录制文件记录
     * 
     * @param busiLiveRecords 直播录制文件记录
     * @return 结果
     */
    @Override
    public int insertBusiLiveRecords(BusiLiveRecords busiLiveRecords)
    {
        busiLiveRecords.setCreateTime(new Date());
        return busiLiveRecordsMapper.insertBusiLiveRecords(busiLiveRecords);
    }

    /**
     * 修改直播录制文件记录
     * 
     * @param busiLiveRecords 直播录制文件记录
     * @return 结果
     */
    @Override
    public int updateBusiLiveRecords(BusiLiveRecords busiLiveRecords)
    {
        busiLiveRecords.setUpdateTime(new Date());
        return busiLiveRecordsMapper.updateBusiLiveRecords(busiLiveRecords);
    }

    /**
     * 批量删除直播录制文件记录
     * 
     * @param ids 需要删除的直播录制文件记录ID
     * @return 结果
     */
    @Override
    public int deleteBusiLiveRecordsByIds(Long[] ids)
    {
        return busiLiveRecordsMapper.deleteBusiLiveRecordsByIds(ids);
    }

    /**
     * 删除直播录制文件记录信息
     * 
     * @param id 直播录制文件记录ID
     * @return 结果
     */
    @Override
    public int deleteBusiLiveRecordsById(Long id)
    {
        return busiLiveRecordsMapper.deleteBusiLiveRecordsById(id);
    }
}
