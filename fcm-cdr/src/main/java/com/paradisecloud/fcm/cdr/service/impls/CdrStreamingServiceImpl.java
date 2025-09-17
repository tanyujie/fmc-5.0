package com.paradisecloud.fcm.cdr.service.impls;

import com.paradisecloud.fcm.cdr.service.interfaces.ICdrStreamingService;
import com.paradisecloud.fcm.dao.mapper.CdrStreamingMapper;
import com.paradisecloud.fcm.dao.model.CdrStreaming;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * streamingStart和streamingEnd记录Service业务层处理
 *
 * @author lilinhai
 * @date 2021-05-13
 */
@Service
public class CdrStreamingServiceImpl implements ICdrStreamingService
{
    @Autowired
    private CdrStreamingMapper cdrStreamingMapper;
    
    /**
     * 查询streamingStart和streamingEnd记录
     *
     * @param cdrId streamingStart和streamingEnd记录ID
     * @return streamingStart和streamingEnd记录
     */
    @Override
    public CdrStreaming selectCdrStreamingById(Integer cdrId)
    {
        return cdrStreamingMapper.selectCdrStreamingById(cdrId);
    }
    
    /**
     * 查询streamingStart和streamingEnd记录列表
     *
     * @param cdrStreaming streamingStart和streamingEnd记录
     * @return streamingStart和streamingEnd记录
     */
    @Override
    public List<CdrStreaming> selectCdrStreamingList(CdrStreaming cdrStreaming)
    {
        return cdrStreamingMapper.selectCdrStreamingList(cdrStreaming);
    }
    
    /**
     * 新增streamingStart和streamingEnd记录
     *
     * @param cdrStreaming streamingStart和streamingEnd记录
     * @return 结果
     */
    @Override
    public int insertCdrStreaming(CdrStreaming cdrStreaming)
    {
        return cdrStreamingMapper.insertCdrStreaming(cdrStreaming);
    }
    
    /**
     * 修改streamingStart和streamingEnd记录
     *
     * @param cdrStreaming streamingStart和streamingEnd记录
     * @return 结果
     */
    @Override
    public int updateCdrStreaming(CdrStreaming cdrStreaming)
    {
        return cdrStreamingMapper.updateCdrStreaming(cdrStreaming);
    }
    
    /**
     * 批量删除streamingStart和streamingEnd记录
     *
     * @param cdrIds 需要删除的streamingStart和streamingEnd记录ID
     * @return 结果
     */
    @Override
    public int deleteCdrStreamingByIds(Integer[] cdrIds)
    {
        return cdrStreamingMapper.deleteCdrStreamingByIds(cdrIds);
    }
    
    /**
     * 删除streamingStart和streamingEnd记录信息
     *
     * @param cdrId streamingStart和streamingEnd记录ID
     * @return 结果
     */
    @Override
    public int deleteCdrStreamingById(Integer cdrId)
    {
        return cdrStreamingMapper.deleteCdrStreamingById(cdrId);
    }
}
