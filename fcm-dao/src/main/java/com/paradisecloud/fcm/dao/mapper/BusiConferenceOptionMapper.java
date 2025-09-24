package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiConferenceOption;

import java.util.List;

/**
 * 会议问卷选项Mapper接口
 *
 * @author lilinhai
 * @date 2025-09-24
 */
public interface BusiConferenceOptionMapper
{
    /**
     * 查询会议问卷选项
     *
     * @param optionId 会议问卷选项ID
     * @return 会议问卷选项
     */
    public BusiConferenceOption selectBusiConferenceOptionById(Long optionId);

    /**
     * 查询会议问卷选项列表
     *
     * @param busiConferenceOption 会议问卷选项
     * @return 会议问卷选项集合
     */
    public List<BusiConferenceOption> selectBusiConferenceOptionList(BusiConferenceOption busiConferenceOption);

    /**
     * 新增会议问卷选项
     *
     * @param busiConferenceOption 会议问卷选项
     * @return 结果
     */
    public int insertBusiConferenceOption(BusiConferenceOption busiConferenceOption);

    /**
     * 修改会议问卷选项
     *
     * @param busiConferenceOption 会议问卷选项
     * @return 结果
     */
    public int updateBusiConferenceOption(BusiConferenceOption busiConferenceOption);

    /**
     * 删除会议问卷选项
     *
     * @param optionId 会议问卷选项ID
     * @return 结果
     */
    public int deleteBusiConferenceOptionById(Long optionId);

    /**
     * 批量删除会议问卷选项
     *
     * @param optionIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiConferenceOptionByIds(Long[] optionIds);
    public List<BusiConferenceOption> selectByQuestionId(Long optionId);


}
