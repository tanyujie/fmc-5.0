package com.paradisecloud.smc3.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiConferenceNumber;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;

import java.util.List;

/**
 * 会议号码记录Service接口
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
public interface IBusiConferenceNumberSmc3Service
{
    
    /**
     * 自动创建号码
     * @author lilinhai
     * @since 2021-05-31 19:48 
     * @param deptId void
     */
    BusiConferenceNumber autoCreateConferenceNumber(Long deptId);
    
    /**
     * 创建指定号码
     * @author lilinhai
     * @since 2021-06-01 12:01 
     * @param deptId
     * @param cn
     * @return BusiConferenceNumber
     */
    BusiConferenceNumber autoCreateConferenceNumber(Long deptId, long cn);
    
    /**
     * 查询会议号码记录
     * 
     * @param id 会议号码记录ID
     * @return 会议号码记录
     */
    BusiConferenceNumber selectBusiConferenceNumberById(Long id);

    /**
     * 查询会议号码记录列表
     * 
     * @param busiConferenceNumber 会议号码记录
     * @return 会议号码记录集合
     */
    List<BusiConferenceNumber> selectBusiConferenceNumberList(BusiConferenceNumber busiConferenceNumber);

    /**
     * 新增会议号码记录
     * 
     * @param busiConferenceNumber 会议号码记录
     * @return 结果
     */
    int insertBusiConferenceNumber(BusiConferenceNumber busiConferenceNumber);

    /**
     * 修改会议号码记录
     * 
     * @param busiConferenceNumber 会议号码记录
     * @return 结果
     */
    int updateBusiConferenceNumber(BusiConferenceNumber busiConferenceNumber);

    /**
     * 删除会议号码记录信息
     * 
     * @param id 会议号码记录ID
     * @return 结果
     */
    int deleteBusiConferenceNumberById(Long id);
    
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54 
     * @param businessFieldType
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();
}
