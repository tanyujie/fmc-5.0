package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.CdrCallLegNumDate;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 每天参会的数量Mapper接口
 *
 * @author johnson liu
 * @date 2021/6/16 17:31
 */
public interface CdrCallLegNumDateMapper {
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
     * 更新当天的统计结果
     *
     * @param cdrCallLegNumDate
     * @return
     */
    int updateByCurrentDate(CdrCallLegNumDate cdrCallLegNumDate);

    /**
     * 删除每天参会的数量
     *
     * @param id 每天参会的数量ID
     * @return 结果
     */
    int deleteCdrCallLegNumDateById(Long id);

    /**
     * 批量删除每天参会的数量
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteCdrCallLegNumDateByIds(Long[] ids);

    /**
     * 根据部门和日期查询
     *
     * @param deptId
     * @param fmeIp
     * @param date
     * @return
     */
    CdrCallLegNumDate selectByDeptIdAndFmeIpAndDate(@Param("deptId") Long deptId, @Param("fmeIp") String fmeIp, @Param("recordDate") String date);

    /**
     * 查询指定时间范围内，各fme的参会者数量查询
     *
     * @param fmeIp
     * @param startTime
     * @param endTime
     * @return
     */
    List<CdrCallLegNumDate> selectByFmeIpAndDate(@Param("deptId") Integer deptId, @Param("fmeIp") String fmeIp, @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 删除日期前的记录
     *
     * @param beforeDate 删除该日期前的数据
     * @return 结果
     */
    int deleteHistory(@Param("beforeDate") Date beforeDate);
}
