package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.CdrCallLegUpdate;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * callLegUpdate记录Mapper接口
 *
 * @author lilinhai
 * @date 2021-05-13
 */
public interface CdrCallLegUpdateMapper {
    /**
     * 查询callLegUpdate记录
     *
     * @param id callLegUpdate记录ID
     * @return callLegUpdate记录
     */
    public CdrCallLegUpdate selectCdrCallLegUpdateById(Integer id);

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
     * 删除callLegUpdate记录
     *
     * @param id callLegUpdate记录ID
     * @return 结果
     */
    public int deleteCdrCallLegUpdateById(Integer id);

    /**
     * 批量删除callLegUpdate记录
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteCdrCallLegUpdateByIds(Integer[] ids);

    /**
     * 删除日期前的记录
     *
     * @param beforeDate 删除该日期前的数据
     * @return 结果
     */
    int deleteHistory(@Param("beforeDate") Date beforeDate);
}
