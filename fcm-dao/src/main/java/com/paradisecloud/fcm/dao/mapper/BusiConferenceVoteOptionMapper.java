package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiConferenceVoteOption;

import java.util.List;

/**
 * 会议投票选项Mapper接口
 *
 * @author lilinhai
 * @date 2025-09-18
 */
public interface BusiConferenceVoteOptionMapper
{
    /**
     * 查询会议投票选项
     *
     * @param optionId 会议投票选项ID
     * @return 会议投票选项
     */
    public BusiConferenceVoteOption selectBusiConferenceVoteOptionById(Long optionId);

    /**
     * 查询会议投票选项列表
     *
     * @param busiConferenceVoteOption 会议投票选项
     * @return 会议投票选项集合
     */
    public List<BusiConferenceVoteOption> selectBusiConferenceVoteOptionList(BusiConferenceVoteOption busiConferenceVoteOption);

    /**
     * 新增会议投票选项
     *
     * @param busiConferenceVoteOption 会议投票选项
     * @return 结果
     */
    public int insertBusiConferenceVoteOption(BusiConferenceVoteOption busiConferenceVoteOption);

    /**
     * 修改会议投票选项
     *
     * @param busiConferenceVoteOption 会议投票选项
     * @return 结果
     */
    public int updateBusiConferenceVoteOption(BusiConferenceVoteOption busiConferenceVoteOption);

    /**
     * 删除会议投票选项
     *
     * @param optionId 会议投票选项ID
     * @return 结果
     */
    public int deleteBusiConferenceVoteOptionById(Long optionId);

    /**
     * 批量删除会议投票选项
     *
     * @param optionIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiConferenceVoteOptionByIds(Long[] optionIds);
}
