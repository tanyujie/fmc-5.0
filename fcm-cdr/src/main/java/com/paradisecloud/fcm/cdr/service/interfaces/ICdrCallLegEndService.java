package com.paradisecloud.fcm.cdr.service.interfaces;

import com.paradisecloud.fcm.dao.model.CdrCallLegEnd;

import java.util.List;

/**
 * callLegEnd记录Service接口
 *
 * @author lilinhai
 * @date 2021-05-13
 */
public interface ICdrCallLegEndService
{
    /**
     * 查询callLegEnd记录
     *
     * @param cdrId callLegEnd记录ID
     * @return callLegEnd记录
     */
    public CdrCallLegEnd selectCdrCallLegEndById(Integer cdrId);
    
    /**
     * 查询callLegEnd记录列表
     *
     * @param cdrCallLegEnd callLegEnd记录
     * @return callLegEnd记录集合
     */
    public List<CdrCallLegEnd> selectCdrCallLegEndList(CdrCallLegEnd cdrCallLegEnd);
    
    /**
     * 新增callLegEnd记录
     *
     * @param cdrCallLegEnd callLegEnd记录
     * @return 结果
     */
    public int insertCdrCallLegEnd(CdrCallLegEnd cdrCallLegEnd);
    
    /**
     * 修改callLegEnd记录
     *
     * @param cdrCallLegEnd callLegEnd记录
     * @return 结果
     */
    public int updateCdrCallLegEnd(CdrCallLegEnd cdrCallLegEnd);
    
    /**
     * 批量删除callLegEnd记录
     *
     * @param cdrIds 需要删除的callLegEnd记录ID
     * @return 结果
     */
    public int deleteCdrCallLegEndByIds(Integer[] cdrIds);
    
    /**
     * 删除callLegEnd记录信息
     *
     * @param cdrId callLegEnd记录ID
     * @return 结果
     */
    public int deleteCdrCallLegEndById(Integer cdrId);
    
}
