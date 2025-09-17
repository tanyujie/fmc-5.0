package com.paradisecloud.fcm.cdr.service.interfaces;

import com.paradisecloud.fcm.dao.model.CdrCallLegEndMediaInfo;

import java.util.List;

/**
 * 视频流、音频流传输信息Service接口
 *
 * @author lilinhai
 * @date 2021-05-14
 */
public interface ICdrCallLegEndMediaInfoService
{
    /**
     * 查询视频流、音频流传输信息
     *
     * @param cdrId 视频流、音频流传输信息ID
     * @return 视频流、音频流传输信息
     */
    public CdrCallLegEndMediaInfo selectCdrCallLegEndMediaInfoById(Integer cdrId);
    
    /**
     * 查询视频流、音频流传输信息列表
     *
     * @param cdrCallLegEndMediaInfo 视频流、音频流传输信息
     * @return 视频流、音频流传输信息集合
     */
    public List<CdrCallLegEndMediaInfo> selectCdrCallLegEndMediaInfoList(CdrCallLegEndMediaInfo cdrCallLegEndMediaInfo);
    
    /**
     * 新增视频流、音频流传输信息
     *
     * @param cdrCallLegEndMediaInfo 视频流、音频流传输信息
     * @return 结果
     */
    public int insertCdrCallLegEndMediaInfo(CdrCallLegEndMediaInfo cdrCallLegEndMediaInfo);
    
    /**
     * 修改视频流、音频流传输信息
     *
     * @param cdrCallLegEndMediaInfo 视频流、音频流传输信息
     * @return 结果
     */
    public int updateCdrCallLegEndMediaInfo(CdrCallLegEndMediaInfo cdrCallLegEndMediaInfo);
    
    /**
     * 批量删除视频流、音频流传输信息
     *
     * @param cdrIds 需要删除的视频流、音频流传输信息ID
     * @return 结果
     */
    public int deleteCdrCallLegEndMediaInfoByIds(Integer[] cdrIds);
    
    /**
     * 删除视频流、音频流传输信息信息
     *
     * @param cdrId 视频流、音频流传输信息ID
     * @return 结果
     */
    public int deleteCdrCallLegEndMediaInfoById(Integer cdrId);
    
}
