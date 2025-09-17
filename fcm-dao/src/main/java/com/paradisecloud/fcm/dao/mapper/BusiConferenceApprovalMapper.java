package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiConferenceApproval;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.dao.model.vo.ConferenceApprovalSearchVo;

import java.util.List;

/**
 * 会议审批Mapper接口
 * 
 * @author lilinhai
 * @date 2023-11-27
 */
public interface BusiConferenceApprovalMapper 
{
    /**
     * 查询会议审批
     * 
     * @param id 会议审批ID
     * @return 会议审批
     */
    BusiConferenceApproval selectBusiConferenceApprovalById(Long id);

    /**
     * 查询会议审批列表
     * 
     * @param busiConferenceApproval 会议审批
     * @return 会议审批集合
     */
    List<BusiConferenceApproval> selectBusiConferenceApprovalList(BusiConferenceApproval busiConferenceApproval);

    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();

    /**
     * 查询会议审批列表
     *
     * @param conferenceApprovalSearchVo 会议审批
     * @return 会议审批集合
     */
    List<BusiConferenceApproval> searchBusiConferenceApprovalList(ConferenceApprovalSearchVo conferenceApprovalSearchVo);

    /**
     * 新增会议审批
     * 
     * @param busiConferenceApproval 会议审批
     * @return 结果
     */
    int insertBusiConferenceApproval(BusiConferenceApproval busiConferenceApproval);

    /**
     * 修改会议审批
     * 
     * @param busiConferenceApproval 会议审批
     * @return 结果
     */
    int updateBusiConferenceApproval(BusiConferenceApproval busiConferenceApproval);

    /**
     * 删除会议审批
     * 
     * @param id 会议审批ID
     * @return 结果
     */
    int deleteBusiConferenceApprovalById(Long id);

    /**
     * 批量删除会议审批
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteBusiConferenceApprovalByIds(Long[] ids);
}
