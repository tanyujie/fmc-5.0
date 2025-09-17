package com.paradisecloud.fcm.mqtt.interfaces;

import com.paradisecloud.fcm.dao.model.BusiInfoDisplay;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.dao.model.vo.BusiInfoDisplayVO;

import java.util.List;

/**
 * 信息展示Service接口
 * 
 * @author lilinhai
 * @date 2024-05-13
 */
public interface IBusiInfoDisplayService 
{
    /**
     * 查询信息展示
     * 
     * @param id 信息展示ID
     * @return 信息展示
     */
    public BusiInfoDisplay selectBusiInfoDisplayById(Long id);

    /**
     * 查询信息展示列表
     * 
     * @param busiInfoDisplay 信息展示
     * @return 信息展示集合
     */
    public List<BusiInfoDisplay> selectBusiInfoDisplayList(BusiInfoDisplayVO busiInfoDisplay);

    /**
     * 新增信息展示
     * 
     * @param busiInfoDisplay 信息展示
     * @return 结果
     */
    public int insertBusiInfoDisplay(BusiInfoDisplay busiInfoDisplay);

    /**
     * 修改信息展示
     * 
     * @param busiInfoDisplay 信息展示
     * @return 结果
     */
    public int updateBusiInfoDisplay(BusiInfoDisplay busiInfoDisplay);

    /**
     * 批量删除信息展示
     * 
     * @param ids 需要删除的信息展示ID
     * @return 结果
     */
    public int deleteBusiInfoDisplayByIds(Long[] ids);

    /**
     * 删除信息展示信息
     * 
     * @param id 信息展示ID
     * @return 结果
     */
    public int deleteBusiInfoDisplayById(Long id);

    /**
     * 推送信息展示
     * @param id
     * @return
     */
    int push(Long id);

    List<DeptRecordCount> getDeptRecordCounts();

    /**
     * 修改状态
     * @param busiInfoDisplay
     * @return
     */
    int status(BusiInfoDisplay busiInfoDisplay);
}
