package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiConferenceNumber;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;

/**
 * 会议号码记录Mapper接口
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
public interface BusiConferenceNumberMapper 
{
    /**
     * 查询会议号码记录
     * 
     * @param id 会议号码记录ID
     * @return 会议号码记录
     */
    public BusiConferenceNumber selectBusiConferenceNumberById(Long id);

    /**
     * 查询会议号码记录列表
     * 
     * @param busiConferenceNumber 会议号码记录
     * @return 会议号码记录集合
     */
    public List<BusiConferenceNumber> selectBusiConferenceNumberList(BusiConferenceNumber busiConferenceNumber);

    /**
     * 新增会议号码记录
     * 
     * @param busiConferenceNumber 会议号码记录
     * @return 结果
     */
    public int insertBusiConferenceNumber(BusiConferenceNumber busiConferenceNumber);

    /**
     * 修改会议号码记录
     * 
     * @param busiConferenceNumber 会议号码记录
     * @return 结果
     */
    public int updateBusiConferenceNumber(BusiConferenceNumber busiConferenceNumber);

    /**
     * 删除会议号码记录
     * 
     * @param id 会议号码记录ID
     * @return 结果
     */
    public int deleteBusiConferenceNumberById(Long id);

    /**
     * 批量删除会议号码记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiConferenceNumberByIds(Long[] ids);
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54 
     * @param businessFieldType
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();
}
