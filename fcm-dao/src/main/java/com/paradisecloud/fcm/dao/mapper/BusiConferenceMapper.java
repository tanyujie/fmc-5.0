package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.paradisecloud.fcm.dao.model.BusiConference;

/**
 * 活跃会议室信息，用于存放活跃的会议室Mapper接口
 * 
 * @author lilinhai
 * @date 2021-02-02
 */
public interface BusiConferenceMapper 
{
    /**
     * 查询活跃会议室信息，用于存放活跃的会议室
     * 
     * @param id 活跃会议室信息，用于存放活跃的会议室ID
     * @return 活跃会议室信息，用于存放活跃的会议室
     */
    public BusiConference selectBusiConferenceById(Long id);
    
    /**
     * <pre>根据coSpace查找当前正在进行的会议</pre>
     * @author lilinhai
     * @since 2021-02-02 19:32 
     * @param id
     * @return BusiConference
     */
    public BusiConference selectBusiConferenceByCoSpaceId(String id);
    
    /**
     * <pre>根据会议号和会议模板ID查找</pre>
     * @author lilinhai
     * @since 2021-02-02 19:34 
     * @param id
     * @return BusiConference
     */
    public BusiConference selectBusiConferenceByTemplateConferenceId(@Param("deptId") Long deptId, @Param("id") Long id);

    /**
     * 查询活跃会议室信息，用于存放活跃的会议室列表
     * 
     * @param busiConference 活跃会议室信息，用于存放活跃的会议室
     * @return 活跃会议室信息，用于存放活跃的会议室集合
     */
    public List<BusiConference> selectBusiConferenceList(BusiConference busiConference);
    
    
    /**
     * 查询活跃会议室信息，包含data字段，用于存放活跃的会议室列表
     * 
     * @param busiConference 活跃会议室信息，用于存放活跃的会议室
     * @return 活跃会议室信息，用于存放活跃的会议室集合
     */
    public List<BusiConference> selectBusiConferenceDataList(BusiConference busiConference);

    /**
     * 新增活跃会议室信息，用于存放活跃的会议室
     * 
     * @param busiConference 活跃会议室信息，用于存放活跃的会议室
     * @return 结果
     */
    public int insertBusiConference(BusiConference busiConference);

    /**
     * 修改活跃会议室信息，用于存放活跃的会议室
     * 
     * @param busiConference 活跃会议室信息，用于存放活跃的会议室
     * @return 结果
     */
    public int updateBusiConference(BusiConference busiConference);

    /**
     * 删除活跃会议室信息，用于存放活跃的会议室
     * 
     * @param id 活跃会议室信息，用于存放活跃的会议室ID
     * @return 结果
     */
    public int deleteBusiConferenceById(Long id);
    
    /**
     * 根据coSpace删除会议室
     * @author lilinhai
     * @since 2021-02-05 15:54 
     * @param coSpaceId
     * @return int
     */
    public int deleteBusiConferenceByCoSpaceId(String coSpaceId);
    
    /**
     * 根据级联号批量删除会议室
     * @author lilinhai
     * @since 2021-02-05 16:01 
     * @param coSpaceId
     * @return int
     */
    public int deleteBusiConferenceByCascadeId(String coSpaceId);

    /**
     * 批量删除活跃会议室信息，用于存放活跃的会议室
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiConferenceByIds(Long[] ids);
}
