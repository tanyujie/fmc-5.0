package com.paradisecloud.fcm.cdr.service.interfaces;

import com.paradisecloud.fcm.dao.model.CdrCallLegStart;

import java.util.List;

/**
 * callLegStart 记录Service接口
 *
 * @author lilinhai
 * @date 2021-05-13
 */
public interface ICdrCallLegStartService
{
    /**
     * 查询callLegStart 记录
     *
     * @param cdrId callLegStart 记录ID
     * @return callLegStart 记录
     */
    public CdrCallLegStart selectCdrCallLegStartById(Integer cdrId);
    
    /**
     * 查询callLegStart 记录列表
     *
     * @param cdrCallLegStart callLegStart 记录
     * @return callLegStart 记录集合
     */
    public List<CdrCallLegStart> selectCdrCallLegStartList(CdrCallLegStart cdrCallLegStart);
    
    /**
     * 新增callLegStart 记录
     *
     * @param cdrCallLegStart callLegStart 记录
     * @return 结果
     */
    public int insertCdrCallLegStart(CdrCallLegStart cdrCallLegStart);
    
    /**
     * 修改callLegStart 记录
     *
     * @param cdrCallLegStart callLegStart 记录
     * @return 结果
     */
    public int updateCdrCallLegStart(CdrCallLegStart cdrCallLegStart);
    
    /**
     * 根据CallLegId更新
     * 
     * @param cdrCallLegStart
     * @return
     */
    int updateByCallLegId(CdrCallLegStart cdrCallLegStart);
    
    /**
     * 批量删除callLegStart 记录
     *
     * @param cdrIds 需要删除的callLegStart 记录ID
     * @return 结果
     */
    public int deleteCdrCallLegStartByIds(Integer[] cdrIds);
    
    /**
     * 删除callLegStart 记录信息
     *
     * @param cdrId callLegStart 记录ID
     * @return 结果
     */
    public int deleteCdrCallLegStartById(Integer cdrId);
}
