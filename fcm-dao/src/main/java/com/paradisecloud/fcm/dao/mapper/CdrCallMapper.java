package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.CdrCall;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * callstart 和callend记录Mapper接口
 *
 * @author lilinhai
 * @date 2021-05-13
 */
public interface CdrCallMapper {
    /**
     * 查询callstart 和callend记录
     *
     * @param id callstart 和callend记录ID
     * @return callstart 和callend记录
     */
    public CdrCall selectCdrCallById(Integer id);

    /**
     * 查询callstart 和callend记录列表
     *
     * @param cdrCall callstart 和callend记录
     * @return callstart 和callend记录集合
     */
    public List<CdrCall> selectCdrCallList(CdrCall cdrCall);

    /**
     * 新增callstart 和callend记录
     *
     * @param cdrCall callstart 和callend记录
     * @return 结果
     */
    public int insertCdrCall(CdrCall cdrCall);

    /**
     * 修改callstart 和callend记录
     *
     * @param cdrCall callstart 和callend记录
     * @return 结果
     */
    public int updateCdrCall(CdrCall cdrCall);

    /**
     * 删除callstart 和callend记录
     *
     * @param id callstart 和callend记录ID
     * @return 结果
     */
    public int deleteCdrCallById(Integer id);

    /**
     * 批量删除callstart 和callend记录
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteCdrCallByIds(Integer[] ids);

    /**
     * @param recordIndex
     * @param correlatorIndex
     * @return
     */
    CdrCall selectCdrCallByRecordIndex(@Param("cdrId") String id, @Param("recordIndex") Integer recordIndex, @Param("correlatorIndex") Integer correlatorIndex);

    CdrCall selectCdrCallByIdAndRecordType(@Param("cdrId") String id, @Param("recordType") Integer recordType);

    /**
     * 删除日期前的记录
     *
     * @param beforeDate 删除该日期前的数据
     * @return 结果
     */
    int deleteHistory(@Param("beforeDate") Date beforeDate);
}
