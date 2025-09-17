package com.paradisecloud.fcm.cdr.service.interfaces;

import com.paradisecloud.fcm.dao.model.CdrCall;

import java.util.List;

/**
 * callstart 和callend记录Service接口
 *
 * @author lilinhai
 * @date 2021-05-13
 */
public interface ICdrCallService
{
    /**
     * 查询callstart 和callend记录
     *
     * @param cdrId callstart 和callend记录ID
     * @return callstart 和callend记录
     */
    public CdrCall selectCdrCallById(Integer cdrId);
    
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
     * 批量删除callstart 和callend记录
     *
     * @param cdrIds 需要删除的callstart 和callend记录ID
     * @return 结果
     */
    public int deleteCdrCallByIds(Integer[] cdrIds);
    
    /**
     * 删除callstart 和callend记录信息
     *
     * @param cdrId callstart 和callend记录ID
     * @return 结果
     */
    public int deleteCdrCallById(Integer cdrId);
    
    CdrCall selectCdrCallByRecordIndex(String id, Integer recordIndex, Integer correlatorIndex);
    
    /**
     * 根据callId查询call记录
     * 
     * @param callId
     * @return
     */
    List<CdrCall> selectCdrCallByCallId(String callId);
}
