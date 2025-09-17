package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.CdrAllCallLegNumDate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 全每天参会的数量Mapper接口
 *
 * @author johnson liu
 * @date 2021/6/16 17:31
 */
public interface CdrAllCallLegNumDateMapper {
    /**
     * 查询每天参会的数量
     *
     * @param id 每天参会的数量ID
     * @return 每天参会的数量
     */
    CdrAllCallLegNumDate selectCdrAllCallLegNumDateById(Long id);

    /**
     * 查询每天参会的数量列表
     *
     * @param cdrAllCallLegNumDate 每天参会的数量
     * @return 每天参会的数量集合
     */
    List<CdrAllCallLegNumDate> selectCdrAllCallLegNumDateList(CdrAllCallLegNumDate cdrAllCallLegNumDate);

    /**
     * 新增每天参会的数量
     *
     * @param cdrAllCallLegNumDate 每天参会的数量
     * @return 结果
     */
    int insertCdrAllCallLegNumDate(CdrAllCallLegNumDate cdrAllCallLegNumDate);

    /**
     * 修改每天参会的数量
     *
     * @param cdrAllCallLegNumDate 每天参会的数量
     * @return 结果
     */
    int updateCdrAllCallLegNumDate(CdrAllCallLegNumDate cdrAllCallLegNumDate);

    /**
     * 更新当天的统计结果
     *
     * @param cdrAllCallLegNumDate
     * @return
     */
    int updateByCurrentDate(CdrAllCallLegNumDate cdrAllCallLegNumDate);

    /**
     * 删除每天参会的数量
     *
     * @param id 每天参会的数量ID
     * @return 结果
     */
    int deleteCdrAllCallLegNumDateById(Long id);

    /**
     * 批量删除每天参会的数量
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteCdrAllCallLegNumDateByIds(Long[] ids);

    /**
     * 根据部门和日期查询
     *
     * @param fmeIp
     * @param date
     * @return
     */
    CdrAllCallLegNumDate selectByFmeIpAndDate(@Param("fmeIp") String fmeIp, @Param("recordDate") String date);

    /**
     * 查询指定时间范围内，各fme的参会者数量查询
     *
     * @param fmeIp
     * @param startTime
     * @param endTime
     * @return
     */
    List<CdrAllCallLegNumDate> selectByFmeIpAndDate(@Param("fmeIp") String fmeIp, @Param("startTime") String startTime, @Param("endTime") String endTime);
}
