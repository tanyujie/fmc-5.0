package com.paradisecloud.fcm.cdr.service.interfaces;

import com.paradisecloud.fcm.dao.model.CdrCallNumDate;

import java.util.List;

/**
 * @author johnson liu
 * @date 2021/6/16 17:40
 */
public interface ICdrCallNumDateService
{
    /**
     * 查询每天开始会议的数量
     *
     * @param id 每天开始会议的数量ID
     * @return 每天开始会议的数量
     */
    CdrCallNumDate selectCdrCallNumDateById(Long id);
    
    /**
     * 查询每天开始会议的数量列表
     *
     * @param cdrCallNumDate 每天开始会议的数量
     * @return 每天开始会议的数量集合
     */
    List<CdrCallNumDate> selectCdrCallNumDateList(CdrCallNumDate cdrCallNumDate);
    
    /**
     * 新增每天开始会议的数量
     *
     * @param cdrCallNumDate 每天开始会议的数量
     * @return 结果
     */
    int insertCdrCallNumDate(CdrCallNumDate cdrCallNumDate);
    
    /**
     * 修改每天开始会议的数量
     *
     * @param cdrCallNumDate 每天开始会议的数量
     * @return 结果
     */
    int updateCdrCallNumDate(CdrCallNumDate cdrCallNumDate);
    
    /**
     * 批量删除每天开始会议的数量
     *
     * @param ids 需要删除的每天开始会议的数量ID
     * @return 结果
     */
    int deleteCdrCallNumDateByIds(Long[] ids);
    
    /**
     * 删除每天开始会议的数量信息
     *
     * @param id 每天开始会议的数量ID
     * @return 结果
     */
    int deleteCdrCallNumDateById(Long id);
    
    /**
     * 通过监听事件每天新增或更新创会议的数量
     *
     * @param deptId
     * @param fmeIp
     */
    void updateByEvent(Long deptId, String fmeIp);
    
    /**
     * 根据fmeIp和记录日期查询
     * 
     * @param deptId
     * @param fmeIp
     * @param date
     * @return
     */
    CdrCallNumDate selectByFmeIpAndDate(Long deptId, String fmeIp, String date);
    
    /**
     * 添加默认的数据
     * 
     * @param fmeIp
     * @return
     */
    int insertInitData(String fmeIp);
}
