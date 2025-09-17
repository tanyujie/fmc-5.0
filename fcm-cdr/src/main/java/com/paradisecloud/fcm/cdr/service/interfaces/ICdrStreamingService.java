package com.paradisecloud.fcm.cdr.service.interfaces;

import com.paradisecloud.fcm.dao.model.CdrStreaming;

import java.util.List;

/**
 * streamingStart和streamingEnd记录Service接口
 *
 * @author lilinhai
 * @date 2021-05-13
 */
public interface ICdrStreamingService
{
    /**
     * 查询streamingStart和streamingEnd记录
     *
     * @param cdrId streamingStart和streamingEnd记录ID
     * @return streamingStart和streamingEnd记录
     */
    public CdrStreaming selectCdrStreamingById(Integer cdrId);
    
    /**
     * 查询streamingStart和streamingEnd记录列表
     *
     * @param cdrStreaming streamingStart和streamingEnd记录
     * @return streamingStart和streamingEnd记录集合
     */
    public List<CdrStreaming> selectCdrStreamingList(CdrStreaming cdrStreaming);
    
    /**
     * 新增streamingStart和streamingEnd记录
     *
     * @param cdrStreaming streamingStart和streamingEnd记录
     * @return 结果
     */
    public int insertCdrStreaming(CdrStreaming cdrStreaming);
    
    /**
     * 修改streamingStart和streamingEnd记录
     *
     * @param cdrStreaming streamingStart和streamingEnd记录
     * @return 结果
     */
    public int updateCdrStreaming(CdrStreaming cdrStreaming);
    
    /**
     * 批量删除streamingStart和streamingEnd记录
     *
     * @param cdrIds 需要删除的streamingStart和streamingEnd记录ID
     * @return 结果
     */
    public int deleteCdrStreamingByIds(Integer[] cdrIds);
    
    /**
     * 删除streamingStart和streamingEnd记录信息
     *
     * @param cdrId streamingStart和streamingEnd记录ID
     * @return 结果
     */
    public int deleteCdrStreamingById(Integer cdrId);
}
