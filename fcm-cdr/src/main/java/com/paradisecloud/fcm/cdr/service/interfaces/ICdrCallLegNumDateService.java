package com.paradisecloud.fcm.cdr.service.interfaces;

import com.paradisecloud.fcm.dao.model.CdrCallLegNumDate;

import java.util.List;

/**
 * 每天参会的数量Service接口
 * 
 * @author johnson liu
 * @date 2021/6/16 17:35
 */
public interface ICdrCallLegNumDateService
{
    /**
     * 查询每天参会的数量
     *
     * @param id 每天参会的数量ID
     * @return 每天参会的数量
     */
    CdrCallLegNumDate selectCdrCallLegNumDateById(Long id);
    
    /**
     * 查询每天参会的数量列表
     *
     * @param cdrCallLegNumDate 每天参会的数量
     * @return 每天参会的数量集合
     */
    List<CdrCallLegNumDate> selectCdrCallLegNumDateList(CdrCallLegNumDate cdrCallLegNumDate);
    
    /**
     * 新增每天参会的数量
     *
     * @param cdrCallLegNumDate 每天参会的数量
     * @return 结果
     */
    int insertCdrCallLegNumDate(CdrCallLegNumDate cdrCallLegNumDate);
    
    /**
     * 修改每天参会的数量
     *
     * @param cdrCallLegNumDate 每天参会的数量
     * @return 结果
     */
    int updateCdrCallLegNumDate(CdrCallLegNumDate cdrCallLegNumDate);
    
    /**
     * 批量删除每天参会的数量
     *
     * @param ids 需要删除的每天参会的数量ID
     * @return 结果
     */
    int deleteCdrCallLegNumDateByIds(Long[] ids);
    
    /**
     * 删除每天参会的数量信息
     *
     * @param id 每天参会的数量ID
     * @return 结果
     */
    int deleteCdrCallLegNumDateById(Long id);
    
    /**
     * 通过监听事件每天新增或更新参会者的数量
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
    CdrCallLegNumDate selectByFmeIpAndDate(Long deptId, String fmeIp, String date);
    
    /**
     * 添加默认的数据
     * 
     * @param fmeIp
     * @return
     */
    int insertInitData(String fmeIp);
}
