package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiCallLegProfile;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;

/**
 * 入会方案配置，控制参会者进入会议的方案Mapper接口
 * 
 * @author lilinhai
 * @date 2021-01-26
 */
public interface BusiCallLegProfileMapper 
{
    /**
     * 查询入会方案配置，控制参会者进入会议的方案
     * 
     * @param id 入会方案配置，控制参会者进入会议的方案ID
     * @return 入会方案配置，控制参会者进入会议的方案
     */
    public BusiCallLegProfile selectBusiCallLegProfileById(Long id);

    /**
     * 查询入会方案配置，控制参会者进入会议的方案列表
     * 
     * @param busiCallLegProfile 入会方案配置，控制参会者进入会议的方案
     * @return 入会方案配置，控制参会者进入会议的方案集合
     */
    public List<BusiCallLegProfile> selectBusiCallLegProfileList(BusiCallLegProfile busiCallLegProfile);

    /**
     * 新增入会方案配置，控制参会者进入会议的方案
     * 
     * @param busiCallLegProfile 入会方案配置，控制参会者进入会议的方案
     * @return 结果
     */
    public int insertBusiCallLegProfile(BusiCallLegProfile busiCallLegProfile);

    /**
     * 修改入会方案配置，控制参会者进入会议的方案
     * 
     * @param busiCallLegProfile 入会方案配置，控制参会者进入会议的方案
     * @return 结果
     */
    public int updateBusiCallLegProfile(BusiCallLegProfile busiCallLegProfile);

    /**
     * 删除入会方案配置，控制参会者进入会议的方案
     * 
     * @param id 入会方案配置，控制参会者进入会议的方案ID
     * @return 结果
     */
    public int deleteBusiCallLegProfileById(Long id);

    /**
     * 批量删除入会方案配置，控制参会者进入会议的方案
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiCallLegProfileByIds(Long[] ids);
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54 
     * @param businessFieldType
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();
}
