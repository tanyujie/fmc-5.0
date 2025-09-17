package com.paradisecloud.fcm.cdr.service.impls;

import com.paradisecloud.fcm.cdr.service.interfaces.ICdrRecordingService;
import com.paradisecloud.fcm.dao.mapper.CdrRecordingMapper;
import com.paradisecloud.fcm.dao.model.CdrRecording;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * recordingStart和recordingEnd记录Service业务层处理
 *
 * @author lilinhai
 * @date 2021-05-13
 */
@Service
public class CdrRecordingServiceImpl implements ICdrRecordingService
{
    @Autowired
    private CdrRecordingMapper cdrRecordingMapper;
    
    /**
     * 查询recordingStart和recordingEnd记录
     *
     * @param cdrId recordingStart和recordingEnd记录ID
     * @return recordingStart和recordingEnd记录
     */
    @Override
    public CdrRecording selectCdrRecordingById(Integer cdrId)
    {
        return cdrRecordingMapper.selectCdrRecordingById(cdrId);
    }
    
    /**
     * 查询recordingStart和recordingEnd记录列表
     *
     * @param cdrRecording recordingStart和recordingEnd记录
     * @return recordingStart和recordingEnd记录
     */
    @Override
    public List<CdrRecording> selectCdrRecordingList(CdrRecording cdrRecording)
    {
        return cdrRecordingMapper.selectCdrRecordingList(cdrRecording);
    }
    
    /**
     * 新增recordingStart和recordingEnd记录
     *
     * @param cdrRecording recordingStart和recordingEnd记录
     * @return 结果
     */
    @Override
    public int insertCdrRecording(CdrRecording cdrRecording)
    {
        return cdrRecordingMapper.insertCdrRecording(cdrRecording);
    }
    
    /**
     * 修改recordingStart和recordingEnd记录
     *
     * @param cdrRecording recordingStart和recordingEnd记录
     * @return 结果
     */
    @Override
    public int updateCdrRecording(CdrRecording cdrRecording)
    {
        return cdrRecordingMapper.updateCdrRecording(cdrRecording);
    }
    
    /**
     * 批量删除recordingStart和recordingEnd记录
     *
     * @param cdrIds 需要删除的recordingStart和recordingEnd记录ID
     * @return 结果
     */
    @Override
    public int deleteCdrRecordingByIds(Integer[] cdrIds)
    {
        return cdrRecordingMapper.deleteCdrRecordingByIds(cdrIds);
    }
    
    /**
     * 删除recordingStart和recordingEnd记录信息
     *
     * @param cdrId recordingStart和recordingEnd记录ID
     * @return 结果
     */
    @Override
    public int deleteCdrRecordingById(Integer cdrId)
    {
        return cdrRecordingMapper.deleteCdrRecordingById(cdrId);
    }
}
