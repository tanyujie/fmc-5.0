package com.paradisecloud.fcm.cdr.service.interfaces;

import com.paradisecloud.fcm.dao.model.CdrRecording;

import java.util.List;

/**
 * recordingStart和recordingEnd记录Service接口
 *
 * @author lilinhai
 * @date 2021-05-13
 */
public interface ICdrRecordingService
{
    /**
     * 查询recordingStart和recordingEnd记录
     *
     * @param cdrId recordingStart和recordingEnd记录ID
     * @return recordingStart和recordingEnd记录
     */
    public CdrRecording selectCdrRecordingById(Integer cdrId);
    
    /**
     * 查询recordingStart和recordingEnd记录列表
     *
     * @param cdrRecording recordingStart和recordingEnd记录
     * @return recordingStart和recordingEnd记录集合
     */
    public List<CdrRecording> selectCdrRecordingList(CdrRecording cdrRecording);
    
    /**
     * 新增recordingStart和recordingEnd记录
     *
     * @param cdrRecording recordingStart和recordingEnd记录
     * @return 结果
     */
    public int insertCdrRecording(CdrRecording cdrRecording);
    
    /**
     * 修改recordingStart和recordingEnd记录
     *
     * @param cdrRecording recordingStart和recordingEnd记录
     * @return 结果
     */
    public int updateCdrRecording(CdrRecording cdrRecording);
    
    /**
     * 批量删除recordingStart和recordingEnd记录
     *
     * @param cdrIds 需要删除的recordingStart和recordingEnd记录ID
     * @return 结果
     */
    public int deleteCdrRecordingByIds(Integer[] cdrIds);
    
    /**
     * 删除recordingStart和recordingEnd记录信息
     *
     * @param cdrId recordingStart和recordingEnd记录ID
     * @return 结果
     */
    public int deleteCdrRecordingById(Integer cdrId);
}
