package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.CdrRecording;

import java.util.List;

/**
 * recordingStart和recordingEnd记录Mapper接口
 *
 * @author lilinhai
 * @date 2021-05-13
 */
public interface CdrRecordingMapper {
    /**
     * 查询recordingStart和recordingEnd记录
     *
     * @param id recordingStart和recordingEnd记录ID
     * @return recordingStart和recordingEnd记录
     */
    public CdrRecording selectCdrRecordingById(Integer id);

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
     * 删除recordingStart和recordingEnd记录
     *
     * @param id recordingStart和recordingEnd记录ID
     * @return 结果
     */
    public int deleteCdrRecordingById(Integer id);

    /**
     * 批量删除recordingStart和recordingEnd记录
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteCdrRecordingByIds(Integer[] ids);

    /**
     * 根据path删除录制文件记录
     * @param path
     */
    void deleteByPath(String path);

    public List<CdrRecording> selectCdrRecordingListDesc(CdrRecording cdrRecording);
}
