package com.paradisecloud.fcm.service.interfaces;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiConferenceNumberSection;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;

import java.util.List;

/**
 * 会议号段Service接口
 * 
 * @author lilinhai
 * @date 2021-05-19
 */
public interface IBusiConferenceNumberSectionService 
{
    /**
     * 查询会议号段
     * 
     * @param id 会议号段ID
     * @return 会议号段
     */
    BusiConferenceNumberSection selectBusiConferenceNumberSectionById(Long id);

    /**
     * 查询会议号段列表
     * 
     * @param busiConferenceNumberSection 会议号段
     * @return 会议号段集合
     */
    List<ModelBean> selectBusiConferenceNumberSectionList(BusiConferenceNumberSection busiConferenceNumberSection);

    /**
     * 新增会议号段
     * 
     * @param busiConferenceNumberSection 会议号段
     * @return 结果
     */
    int insertBusiConferenceNumberSection(BusiConferenceNumberSection busiConferenceNumberSection);

    /**
     * 修改会议号段
     * 
     * @param busiConferenceNumberSection 会议号段
     * @return 结果
     */
    int updateBusiConferenceNumberSection(BusiConferenceNumberSection busiConferenceNumberSection);

    /**
     * 批量删除会议号段
     * 
     * @param ids 需要删除的会议号段ID
     * @return 结果
     */
    int deleteBusiConferenceNumberSectionByIds(Long[] ids);

    /**
     * 删除会议号段信息
     * 
     * @param id 会议号段ID
     * @return 结果
     */
    int deleteBusiConferenceNumberSectionById(Long id);
    
    long autoGenerate(Long deptId, String mcuType);

    long autoGenerate(Long deptId, String mcuType, boolean random);
    
    String getConferenceNumberSections(Long deptId, String mcuType, Integer sectionType);

    /**
     * 会议号规则校验
     * @author lilinhai
     * @since 2021-05-19 17:54
     * @param conferenceNumber
     * @param deptId void
     */
    void validNumber(Long conferenceNumber, Long deptId, String mcuType);
    
    /**
     * 会议号规则校验
     * @author lilinhai
     * @since 2021-05-19 17:54 
     * @param conferenceNumber
     * @param deptId void
     */
    void validNumber(Long conferenceNumber, Long deptId, String mcuType, Integer sectionType);
    
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();
}
