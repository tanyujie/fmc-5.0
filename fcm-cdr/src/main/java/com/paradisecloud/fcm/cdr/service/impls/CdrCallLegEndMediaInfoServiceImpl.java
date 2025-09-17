package com.paradisecloud.fcm.cdr.service.impls;

import com.paradisecloud.fcm.cdr.service.interfaces.ICdrCallLegEndMediaInfoService;
import com.paradisecloud.fcm.dao.mapper.CdrCallLegEndMediaInfoMapper;
import com.paradisecloud.fcm.dao.model.CdrCallLegEndMediaInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 视频流、音频流传输信息Service业务层处理
 *
 * @author lilinhai
 * @date 2021-05-14
 */
@Service
public class CdrCallLegEndMediaInfoServiceImpl implements ICdrCallLegEndMediaInfoService
{
    @Autowired
    private CdrCallLegEndMediaInfoMapper cdrCallLegEndMediaInfoMapper;
    
    /**
     * 查询视频流、音频流传输信息
     *
     * @param cdrId 视频流、音频流传输信息ID
     * @return 视频流、音频流传输信息
     */
    @Override
    public CdrCallLegEndMediaInfo selectCdrCallLegEndMediaInfoById(Integer cdrId)
    {
        return cdrCallLegEndMediaInfoMapper.selectCdrCallLegEndMediaInfoById(cdrId);
    }
    
    /**
     * 查询视频流、音频流传输信息列表
     *
     * @param cdrCallLegEndMediaInfo 视频流、音频流传输信息
     * @return 视频流、音频流传输信息
     */
    @Override
    public List<CdrCallLegEndMediaInfo> selectCdrCallLegEndMediaInfoList(CdrCallLegEndMediaInfo cdrCallLegEndMediaInfo)
    {
        return cdrCallLegEndMediaInfoMapper.selectCdrCallLegEndMediaInfoList(cdrCallLegEndMediaInfo);
    }
    
    /**
     * 新增视频流、音频流传输信息
     *
     * @param cdrCallLegEndMediaInfo 视频流、音频流传输信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertCdrCallLegEndMediaInfo(CdrCallLegEndMediaInfo cdrCallLegEndMediaInfo)
    {
        return cdrCallLegEndMediaInfoMapper.insertCdrCallLegEndMediaInfo(cdrCallLegEndMediaInfo);
    }
    
    /**
     * 修改视频流、音频流传输信息
     *
     * @param cdrCallLegEndMediaInfo 视频流、音频流传输信息
     * @return 结果
     */
    @Override
    public int updateCdrCallLegEndMediaInfo(CdrCallLegEndMediaInfo cdrCallLegEndMediaInfo)
    {
        return cdrCallLegEndMediaInfoMapper.updateCdrCallLegEndMediaInfo(cdrCallLegEndMediaInfo);
    }
    
    /**
     * 批量删除视频流、音频流传输信息
     *
     * @param cdrIds 需要删除的视频流、音频流传输信息ID
     * @return 结果
     */
    @Override
    public int deleteCdrCallLegEndMediaInfoByIds(Integer[] cdrIds)
    {
        return cdrCallLegEndMediaInfoMapper.deleteCdrCallLegEndMediaInfoByIds(cdrIds);
    }
    
    /**
     * 删除视频流、音频流传输信息信息
     *
     * @param cdrId 视频流、音频流传输信息ID
     * @return 结果
     */
    @Override
    public int deleteCdrCallLegEndMediaInfoById(Integer cdrId)
    {
        return cdrCallLegEndMediaInfoMapper.deleteCdrCallLegEndMediaInfoById(cdrId);
    }
}
