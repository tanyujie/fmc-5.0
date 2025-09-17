package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMtr;

import java.util.List;

/**
 * MTR检测记录Mapper接口
 * 
 * @author lilinhai
 * @date 2023-12-27
 */
public interface BusiMtrMapper 
{
    /**
     * 查询MTR检测记录
     * 
     * @param id MTR检测记录ID
     * @return MTR检测记录
     */
    public BusiMtr selectBusiMtrById(Long id);

    /**
     * 查询MTR检测记录列表
     * 
     * @param busiMtr MTR检测记录
     * @return MTR检测记录集合
     */
    public List<BusiMtr> selectBusiMtrList(BusiMtr busiMtr);

    /**
     * 查询MTR检测记录列表
     *
     * @param busiMtr MTR检测记录
     * @return MTR检测记录集合
     */
    public List<BusiMtr> selectBusiMtrListWithDeleted(BusiMtr busiMtr);

    /**
     * 查询MTR检测记录列表
     *
     * @return MTR检测记录集合
     */
    public List<BusiMtr> selectBusiMtrListForLongNotEnd();

    /**
     * 查询MTR检测记录列表
     *
     * @return MTR检测记录集合
     */
    public List<BusiMtr> selectBusiMtrListForShortNotEnd();

    /**
     * 查询MTR检测记录列表
     *
     * @return MTR检测记录集合
     */
    public List<BusiMtr> selectBusiMtrListForDeletedNotEnd();

    /**
     * 新增MTR检测记录
     * 
     * @param busiMtr MTR检测记录
     * @return 结果
     */
    public int insertBusiMtr(BusiMtr busiMtr);

    /**
     * 修改MTR检测记录
     * 
     * @param busiMtr MTR检测记录
     * @return 结果
     */
    public int updateBusiMtr(BusiMtr busiMtr);

    /**
     * 逻辑删除MTR检测记录
     *
     * @param id MTR检测记录ID
     * @return 结果
     */
    public int updateBusiMtrForDeleteById(Long id);

    /**
     * 批量逻辑删除MTR检测记录
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int updateBusiMtrForDeleteByIds(Long[] ids);

    /**
     * 删除MTR检测记录
     * 
     * @param id MTR检测记录ID
     * @return 结果
     */
    public int deleteBusiMtrById(Long id);

    /**
     * 批量删除MTR检测记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMtrByIds(Long[] ids);
}
