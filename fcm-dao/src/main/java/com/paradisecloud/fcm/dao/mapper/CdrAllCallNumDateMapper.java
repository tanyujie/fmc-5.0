package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.CdrAllCallNumDate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 全每天开始会议的数量Mapper接口
 *
 * @author lilinhai
 * @date 2021-06-16
 */
public interface CdrAllCallNumDateMapper {
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
     * 删除每天开始会议的数量
     *
     * @param id 每天开始会议的数量ID
     * @return 结果
     */
    int deleteCdrAllCallNumDateById(Long id);

    /**
     * 批量删除每天开始会议的数量
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteCdrAllCallNumDateByIds(Long[] ids);

    /**
     * 根据fme和日期查询
     *
     * @param date
     * @param fmeIp
     * @return
     */
    CdrAllCallNumDate selectByFmeIpAndDate(@Param("fmeIp") String fmeIp, @Param("recordDate") String date);

    /**
     * 查询指定时间范围内，各fme的发起会议数量查询
     *
     * @param fmeIp
     * @param startTime
     * @param endTime
     * @return
     */
    List<CdrAllCallNumDate> selectByFmeIpAndDateRange(@Param("fmeIp") String fmeIp, @Param("startTime") String startTime, @Param("endTime") String endTime);
}
