package com.paradisecloud.fcm.cdr.service.interfaces;

import com.paradisecloud.fcm.dao.model.CdrCallLegUpdate;

import java.util.List;

/**
 * callLegUpdate记录Service接口
 *
 * @author lilinhai
 * @date 2021-05-13
 */
public interface ICdrCallLegUpdateService
{
    /**
     * 查询callLegUpdate记录
     *
     * @param cdrId callLegUpdate记录ID
     * @return callLegUpdate记录
     */
    public CdrCallLegUpdate selectCdrCallLegUpdateById(Integer cdrId);
    
    /**
     * 查询callLegUpdate记录列表
     *
     * @param cdrCallLegUpdate callLegUpdate记录
     * @return callLegUpdate记录集合
     */
    public List<CdrCallLegUpdate> selectCdrCallLegUpdateList(CdrCallLegUpdate cdrCallLegUpdate);
    
    /**
     * 新增callLegUpdate记录
     *
     * @param cdrCallLegUpdate callLegUpdate记录
     * @return 结果
     */
    public int insertCdrCallLegUpdate(CdrCallLegUpdate cdrCallLegUpdate);
    
    /**
     * 修改callLegUpdate记录
     *
     * @param cdrCallLegUpdate callLegUpdate记录
     * @return 结果
     */
    public int updateCdrCallLegUpdate(CdrCallLegUpdate cdrCallLegUpdate);
    
    /**
     * 批量删除callLegUpdate记录
     *
     * @param cdrIds 需要删除的callLegUpdate记录ID
     * @return 结果
     */
    public int deleteCdrCallLegUpdateByIds(Integer[] cdrIds);
    
    /**
     * 删除callLegUpdate记录信息
     *
     * @param cdrId callLegUpdate记录ID
     * @return 结果
     */
    public int deleteCdrCallLegUpdateById(Integer cdrId);
}
