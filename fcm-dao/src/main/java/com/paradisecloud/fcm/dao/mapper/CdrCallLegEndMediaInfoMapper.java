package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.CdrCallLegEndMediaInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 视频流、音频流传输信息Mapper接口
 *
 * @author lilinhai
 * @date 2021-05-14
 */
public interface CdrCallLegEndMediaInfoMapper {
    /**
     * 查询视频流、音频流传输信息
     *
     * @param id 视频流、音频流传输信息ID
     * @return 视频流、音频流传输信息
     */
    public CdrCallLegEndMediaInfo selectCdrCallLegEndMediaInfoById(Integer id);
    
    CdrCallLegEndMediaInfo selectCdrCallLegEndMediaInfo(String callLegId, String type);

    /**
     * 查询视频流、音频流传输信息列表
     *
     * @param cdrCallLegEndMediaInfo 视频流、音频流传输信息
     * @return 视频流、音频流传输信息集合
     */
    public List<CdrCallLegEndMediaInfo> selectCdrCallLegEndMediaInfoList(CdrCallLegEndMediaInfo cdrCallLegEndMediaInfo);

    /**
     * 新增视频流、音频流传输信息
     *
     * @param cdrCallLegEndMediaInfo 视频流、音频流传输信息
     * @return 结果
     */
    public int insertCdrCallLegEndMediaInfo(CdrCallLegEndMediaInfo cdrCallLegEndMediaInfo);

    /**
     * 修改视频流、音频流传输信息
     *
     * @param cdrCallLegEndMediaInfo 视频流、音频流传输信息
     * @return 结果
     */
    public int updateCdrCallLegEndMediaInfo(CdrCallLegEndMediaInfo cdrCallLegEndMediaInfo);

    /**
     * 删除视频流、音频流传输信息
     *
     * @param id 视频流、音频流传输信息ID
     * @return 结果
     */
    public int deleteCdrCallLegEndMediaInfoById(Integer id);

    /**
     * 批量删除视频流、音频流传输信息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteCdrCallLegEndMediaInfoByIds(Integer[] ids);

    /**
     * 查询所有的终端媒体流信息
     *
     * @param deptId       部门
     * @param joinTime     终端入会时间
     * @param outgoingTime 终端离会时间
     * @return
     */
    List<CdrCallLegEndMediaInfo> selectMediaInfo(@Param("deptId") Integer deptId, @Param("joinTime") Date joinTime, @Param("outgoingTime") Date outgoingTime);

    /**
     * 删除日期前的记录
     *
     * @param beforeDate 删除该日期前的数据
     * @return 结果
     */
    int deleteHistory(@Param("beforeDate") Date beforeDate);
}
