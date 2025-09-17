package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.CdrStreaming;
import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * streamingStart和streamingEnd记录Mapper接口
 *
 * @author lilinhai
 * @date 2021-05-13
 */
public interface CdrStreamingMapper {
    /**
     * 查询streamingStart和streamingEnd记录
     *
     * @param id streamingStart和streamingEnd记录ID
     * @return streamingStart和streamingEnd记录
     */
    public CdrStreaming selectCdrStreamingById(Integer id);

    /**
     * 查询streamingStart和streamingEnd记录列表
     *
     * @param cdrStreaming streamingStart和streamingEnd记录
     * @return streamingStart和streamingEnd记录集合
     */
    public List<CdrStreaming> selectCdrStreamingList(CdrStreaming cdrStreaming);

    /**
     * 新增streamingStart和streamingEnd记录
     *
     * @param cdrStreaming streamingStart和streamingEnd记录
     * @return 结果
     */
    public int insertCdrStreaming(CdrStreaming cdrStreaming);

    /**
     * 修改streamingStart和streamingEnd记录
     *
     * @param cdrStreaming streamingStart和streamingEnd记录
     * @return 结果
     */
    public int updateCdrStreaming(CdrStreaming cdrStreaming);

    /**
     * 删除streamingStart和streamingEnd记录
     *
     * @param id streamingStart和streamingEnd记录ID
     * @return 结果
     */
    public int deleteCdrStreamingById(Integer id);

    /**
     * 批量删除streamingStart和streamingEnd记录
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteCdrStreamingByIds(Integer[] ids);
    /**
     * 根据通用searchVo搜索
     * @param reportSearchVo
     * @return
     */
    List<CdrStreaming> selectBySearchVo(ReportSearchVo reportSearchVo);

    /**
     * 删除日期前的记录
     *
     * @param beforeDate 删除该日期前的数据
     * @return 结果
     */
    int deleteHistory(@Param("beforeDate") Date beforeDate);
}
