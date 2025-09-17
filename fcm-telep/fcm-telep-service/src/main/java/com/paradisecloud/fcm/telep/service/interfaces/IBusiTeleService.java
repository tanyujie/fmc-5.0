package com.paradisecloud.fcm.telep.service.interfaces;

import com.paradisecloud.fcm.telep.dao.model.BusiTele;

import java.util.List;

/**
 * tele终端信息Service接口
 * 
 * @author lilinhai
 * @date 2022-10-11
 */
public interface IBusiTeleService 
{
    /**
     * 查询tele终端信息
     * 
     * @param id tele终端信息ID
     * @return tele终端信息
     */
    public BusiTele selectBusiTeleById(Long id);

    /**
     * 查询tele终端信息列表
     * 
     * @param busiTele tele终端信息
     * @return tele终端信息集合
     */
    public List<BusiTele> selectBusiTeleList(BusiTele busiTele);

    /**
     * 新增tele终端信息
     * 
     * @param busiTele tele终端信息
     * @return 结果
     */
    public int insertBusiTele(BusiTele busiTele);

    /**
     * 修改tele终端信息
     * 
     * @param busiTele tele终端信息
     * @return 结果
     */
    public int updateBusiTele(BusiTele busiTele);

    /**
     * 批量删除tele终端信息
     * 
     * @param ids 需要删除的tele终端信息ID
     * @return 结果
     */
    public int deleteBusiTeleByIds(Long[] ids);

    /**
     * 删除tele终端信息信息
     * 
     * @param id tele终端信息ID
     * @return 结果
     */
    public int deleteBusiTeleById(Long id);
}
