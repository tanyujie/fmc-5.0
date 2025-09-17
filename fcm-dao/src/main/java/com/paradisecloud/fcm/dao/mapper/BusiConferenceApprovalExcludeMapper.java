package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiConferenceApprovalExclude;

import java.util.List;

/**
 * 会议审批排除Mapper接口
 * 
 * @author lilinhai
 * @date 2025-03-22
 */
public interface BusiConferenceApprovalExcludeMapper 
{
    /**
     * 查询会议审批排除
     * 
     * @param id 会议审批排除ID
     * @return 会议审批排除
     */
    public BusiConferenceApprovalExclude selectBusiConferenceApprovalExcludeById(Long id);

    /**
     * 查询会议审批排除列表
     * 
     * @param busiConferenceApprovalExclude 会议审批排除
     * @return 会议审批排除集合
     */
    public List<BusiConferenceApprovalExclude> selectBusiConferenceApprovalExcludeList(BusiConferenceApprovalExclude busiConferenceApprovalExclude);

    /**
     * 新增会议审批排除
     * 
     * @param busiConferenceApprovalExclude 会议审批排除
     * @return 结果
     */
    public int insertBusiConferenceApprovalExclude(BusiConferenceApprovalExclude busiConferenceApprovalExclude);

    /**
     * 修改会议审批排除
     * 
     * @param busiConferenceApprovalExclude 会议审批排除
     * @return 结果
     */
    public int updateBusiConferenceApprovalExclude(BusiConferenceApprovalExclude busiConferenceApprovalExclude);

    /**
     * 删除会议审批排除
     * 
     * @param id 会议审批排除ID
     * @return 结果
     */
    public int deleteBusiConferenceApprovalExcludeById(Long id);

    /**
     * 批量删除会议审批排除
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiConferenceApprovalExcludeByIds(Long[] ids);
}
