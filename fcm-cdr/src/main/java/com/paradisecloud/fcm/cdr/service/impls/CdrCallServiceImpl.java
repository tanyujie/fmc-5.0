package com.paradisecloud.fcm.cdr.service.impls;

import com.paradisecloud.fcm.cdr.service.interfaces.ICdrCallService;
import com.paradisecloud.fcm.dao.mapper.CdrCallMapper;
import com.paradisecloud.fcm.dao.model.CdrCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * callstart 和callend记录Service业务层处理
 *
 * @author lilinhai
 * @date 2021-05-13
 */
@Service
public class CdrCallServiceImpl implements ICdrCallService
{
    @Autowired
    private CdrCallMapper cdrCallMapper;
    
    /**
     * 查询callstart 和callend记录
     *
     * @param cdrId callstart 和callend记录ID
     * @return callstart 和callend记录
     */
    @Override
    public CdrCall selectCdrCallById(Integer cdrId)
    {
        return cdrCallMapper.selectCdrCallById(cdrId);
    }
    
    /**
     * 查询callstart 和callend记录列表
     *
     * @param cdrCall callstart 和callend记录
     * @return callstart 和callend记录
     */
    @Override
    public List<CdrCall> selectCdrCallList(CdrCall cdrCall)
    {
        return cdrCallMapper.selectCdrCallList(cdrCall);
    }
    
    /**
     * 新增callstart 和callend记录
     *
     * @param cdrCall callstart 和callend记录
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertCdrCall(CdrCall cdrCall)
    {
        // todo 会议结束生成话单记录
        return cdrCallMapper.insertCdrCall(cdrCall);
    }
    
    /**
     * 修改callstart 和callend记录
     *
     * @param cdrCall callstart 和callend记录
     * @return 结果
     */
    @Override
    public int updateCdrCall(CdrCall cdrCall)
    {
        return cdrCallMapper.updateCdrCall(cdrCall);
    }
    
    /**
     * 批量删除callstart 和callend记录
     *
     * @param cdrIds 需要删除的callstart 和callend记录ID
     * @return 结果
     */
    @Override
    public int deleteCdrCallByIds(Integer[] cdrIds)
    {
        return cdrCallMapper.deleteCdrCallByIds(cdrIds);
    }
    
    /**
     * 删除callstart 和callend记录信息
     *
     * @param cdrId callstart 和callend记录ID
     * @return 结果
     */
    @Override
    public int deleteCdrCallById(Integer cdrId)
    {
        return cdrCallMapper.deleteCdrCallById(cdrId);
    }
    
    @Override
    public CdrCall selectCdrCallByRecordIndex(String id, Integer recordIndex, Integer correlatorIndex)
    {
        return cdrCallMapper.selectCdrCallByRecordIndex(id, recordIndex, correlatorIndex);
    }
    
    /**
     * 根据callId查询call记录
     *
     * @param callId
     * @return
     */
    @Override
    public List<CdrCall> selectCdrCallByCallId(String callId)
    {
        CdrCall cdrCall = new CdrCall();
        cdrCall.setCdrId(callId);
        return cdrCallMapper.selectCdrCallList(cdrCall);
    }
}
