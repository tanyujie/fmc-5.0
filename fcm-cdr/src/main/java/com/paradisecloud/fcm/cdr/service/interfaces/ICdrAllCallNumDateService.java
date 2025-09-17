package com.paradisecloud.fcm.cdr.service.interfaces;

import com.paradisecloud.fcm.dao.model.CdrAllCallNumDate;

import java.util.List;

/**
 * @author johnson liu
 * @date 2021/6/16 17:40
 */
public interface ICdrAllCallNumDateService
{
    /**
     * 查询每天开始会议的数量
     *
     * @param id 每天开始会议的数量ID
     * @return 每天开始会议的数量
     */
    CdrAllCallNumDate selectCdrAllCallNumDateById(Long id);
    
    /**
     * 查询每天开始会议的数量列表
     *
     * @param cdrAllCallNumDate 每天开始会议的数量
     * @return 每天开始会议的数量集合
     */
    List<CdrAllCallNumDate> selectCdrAllCallNumDateList(CdrAllCallNumDate cdrAllCallNumDate);
    
    /**
     * 新增每天开始会议的数量
     *
     * @param cdrAllCallNumDate 每天开始会议的数量
     * @return 结果
     */
    int insertCdrAllCallNumDate(CdrAllCallNumDate cdrAllCallNumDate);
    
    /**
     * 修改每天开始会议的数量
     *
     * @param cdrAllCallNumDate 每天开始会议的数量
     * @return 结果
     */
    int updateCdrAllCallNumDate(CdrAllCallNumDate cdrAllCallNumDate);
    
    /**
     * 批量删除每天开始会议的数量
     *
     * @param ids 需要删除的每天开始会议的数量ID
     * @return 结果
     */
    int deleteCdrAllCallNumDateByIds(Long[] ids);
    
    /**
     * 删除每天开始会议的数量信息
     *
     * @param id 每天开始会议的数量ID
     * @return 结果
     */
    int deleteCdrAllCallNumDateById(Long id);
    
    /**
     * 通过监听事件每天新增或更新创会议的数量
     *
     * @param fmeIp
     */
    void updateByEvent(String fmeIp);
    
    /**
     * 根据fmeIp和记录日期查询
     *
     * @param fmeIp
     * @param date
     * @return
     */
    CdrAllCallNumDate selectByFmeIpAndDate(String fmeIp, String date);
    
    /**
     * 添加默认的数据
     * 
     * @param fmeIp
     * @return
     */
    int insertInitData(String fmeIp);
}
