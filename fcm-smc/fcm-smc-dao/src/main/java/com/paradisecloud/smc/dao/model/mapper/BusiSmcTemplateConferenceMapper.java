package com.paradisecloud.smc.dao.model.mapper;

import java.util.List;

import com.paradisecloud.smc.dao.model.BusiSmcTemplateConference;

/**
 * smc模板会议关联Mapper接口
 * 
 * @author nj
 * @date 2022-09-20
 */
public interface BusiSmcTemplateConferenceMapper 
{
    /**
     * 查询smc模板会议关联
     * 
     * @param id smc模板会议关联ID
     * @return smc模板会议关联
     */
    public BusiSmcTemplateConference selectBusiSmcTemplateConferenceById(Integer id);

    /**
     * 查询smc模板会议关联列表
     * 
     * @param busiSmcTemplateConference smc模板会议关联
     * @return smc模板会议关联集合
     */
    public List<BusiSmcTemplateConference> selectBusiSmcTemplateConferenceList(BusiSmcTemplateConference busiSmcTemplateConference);

    /**
     * 新增smc模板会议关联
     * 
     * @param busiSmcTemplateConference smc模板会议关联
     * @return 结果
     */
    public int insertBusiSmcTemplateConference(BusiSmcTemplateConference busiSmcTemplateConference);

    /**
     * 修改smc模板会议关联
     * 
     * @param busiSmcTemplateConference smc模板会议关联
     * @return 结果
     */
    public int updateBusiSmcTemplateConference(BusiSmcTemplateConference busiSmcTemplateConference);

    /**
     * 删除smc模板会议关联
     * 
     * @param id smc模板会议关联ID
     * @return 结果
     */
    public int deleteBusiSmcTemplateConferenceById(Integer id);

    /**
     * 批量删除smc模板会议关联
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiSmcTemplateConferenceByIds(Integer[] ids);

    int deleteBusiSmcTemplateConferenceByConferenceId(String conferenceId);

    List<BusiSmcTemplateConference> selectTemplateConferenceList(Long deptId);
}
