package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiLiveRecords;

import java.util.List;;

/**
 * 直播录制文件记录Mapper接口
 * 
 * @author lilinhai
 * @date 2024-05-21
 */
public interface BusiLiveRecordsMapper 
{
    /**
     * 查询直播录制文件记录
     * 
     * @param id 直播录制文件记录ID
     * @return 直播录制文件记录
     */
    public BusiLiveRecords selectBusiLiveRecordsById(Long id);

    /**
     * 查询直播录制文件记录列表
     * 
     * @param busiLiveRecords 直播录制文件记录
     * @return 直播录制文件记录集合
     */
    public List<BusiLiveRecords> selectBusiLiveRecordsList(BusiLiveRecords busiLiveRecords);

    /**
     * 新增直播录制文件记录
     * 
     * @param busiLiveRecords 直播录制文件记录
     * @return 结果
     */
    public int insertBusiLiveRecords(BusiLiveRecords busiLiveRecords);

    /**
     * 修改直播录制文件记录
     * 
     * @param busiLiveRecords 直播录制文件记录
     * @return 结果
     */
    public int updateBusiLiveRecords(BusiLiveRecords busiLiveRecords);

    /**
     * 删除直播录制文件记录
     * 
     * @param id 直播录制文件记录ID
     * @return 结果
     */
    public int deleteBusiLiveRecordsById(Long id);

    /**
     * 批量删除直播录制文件记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiLiveRecordsByIds(Long[] ids);
}
