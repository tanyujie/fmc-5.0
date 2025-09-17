package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.CdrCallNumDate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 每天开始会议的数量Mapper接口
 *
 * @author lilinhai
 * @date 2021-06-16
 */
public interface CdrCallNumDateMapper {
    /**
     * 查询每天开始会议的数量
     *
     * @param id 每天开始会议的数量ID
     * @return 每天开始会议的数量
     */
    public CdrCallNumDate selectCdrCallNumDateById(Long id);

    /**
     * 查询每天开始会议的数量列表
     *
     * @param cdrCallNumDate 每天开始会议的数量
     * @return 每天开始会议的数量集合
     */
    public List<CdrCallNumDate> selectCdrCallNumDateList(CdrCallNumDate cdrCallNumDate);

    /**
     * 新增每天开始会议的数量
     *
     * @param cdrCallNumDate 每天开始会议的数量
     * @return 结果
     */
    public int insertCdrCallNumDate(CdrCallNumDate cdrCallNumDate);

    /**
     * 修改每天开始会议的数量
     *
     * @param cdrCallNumDate 每天开始会议的数量
     * @return 结果
     */
    public int updateCdrCallNumDate(CdrCallNumDate cdrCallNumDate);

    /**
     * 删除每天开始会议的数量
     *
     * @param id 每天开始会议的数量ID
     * @return 结果
     */
    public int deleteCdrCallNumDateById(Long id);

    /**
     * 批量删除每天开始会议的数量
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteCdrCallNumDateByIds(Long[] ids);

    /**
     * 根据部门ID和日期查询
     *
     * @param deptId
     * @param date
     * @param fmeIp
     * @return
     */
    CdrCallNumDate selectByDeptIdAndFmeIpAndDate(@Param("deptId") Long deptId, @Param("fmeIp") String fmeIp, @Param("recordDate") String date);

    /**
     * 查询指定时间范围内，各fme的发起会议数量查询
     *
     * @param fmeIp
     * @param startTime
     * @param endTime
     * @return
     */
    List<CdrCallNumDate> selectByFmeIpAndDate(@Param("deptId") Integer deptId, @Param("fmeIp") String fmeIp, @Param("startTime") String startTime, @Param("endTime") String endTime);
}
