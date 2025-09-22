package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiConferenceVoteRecord;

import java.util.List;

/**
 * 会议投票记录Mapper接口
 *
 * @author lilinhai
 * @date 2025-09-19
 */
public interface BusiConferenceVoteRecordMapper
{
    /**
     * 查询会议投票记录
     *
     * @param recordId 会议投票记录ID
     * @return 会议投票记录
     */
    public BusiConferenceVoteRecord selectBusiConferenceVoteRecordById(Long recordId);

    /**
     * 查询会议投票记录列表
     *
     * @param busiConferenceVoteRecord 会议投票记录
     * @return 会议投票记录集合
     */
    public List<BusiConferenceVoteRecord> selectBusiConferenceVoteRecordList(BusiConferenceVoteRecord busiConferenceVoteRecord);

    /**
     * 新增会议投票记录
     *
     * @param busiConferenceVoteRecord 会议投票记录
     * @return 结果
     */
    public int insertBusiConferenceVoteRecord(BusiConferenceVoteRecord busiConferenceVoteRecord);

    /**
     * 修改会议投票记录
     *
     * @param busiConferenceVoteRecord 会议投票记录
     * @return 结果
     */
    public int updateBusiConferenceVoteRecord(BusiConferenceVoteRecord busiConferenceVoteRecord);

    /**
     * 删除会议投票记录
     *
     * @param recordId 会议投票记录ID
     * @return 结果
     */
    public int deleteBusiConferenceVoteRecordById(Long recordId);

    /**
     * 批量删除会议投票记录
     *
     * @param recordIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiConferenceVoteRecordByIds(Long[] recordIds);
}
