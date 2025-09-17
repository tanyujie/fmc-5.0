package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;

/**
 * 租户绑定服务器资源Mapper接口
 * 
 * @author zyz
 * @date 2021-09-02
 */
public interface BusiFreeSwitchDeptMapper 
{
    /**
     * 查询租户绑定服务器资源
     * 
     * @param id 租户绑定服务器资源ID
     * @return 租户绑定服务器资源
     */
    public BusiFreeSwitchDept selectBusiFreeSwitchDeptById(Long id);

    /**
     * 查询租户绑定服务器资源列表
     * 
     * @param busiFreeSwitchDept 租户绑定服务器资源
     * @return 租户绑定服务器资源集合
     */
    public List<BusiFreeSwitchDept> selectBusiFreeSwitchDeptList(BusiFreeSwitchDept busiFreeSwitchDept);

    /**
     * 新增租户绑定服务器资源
     * 
     * @param busiFreeSwitchDept 租户绑定服务器资源
     * @return 结果
     */
    public int insertBusiFreeSwitchDept(BusiFreeSwitchDept busiFreeSwitchDept);

    /**
     * 修改租户绑定服务器资源
     * 
     * @param busiFreeSwitchDept 租户绑定服务器资源
     * @return 结果
     */
    public int updateBusiFreeSwitchDept(BusiFreeSwitchDept busiFreeSwitchDept);

    /**
     * 删除租户绑定服务器资源
     * 
     * @param id 租户绑定服务器资源ID
     * @return 结果
     */
    public int deleteBusiFreeSwitchDeptById(Long id);

    /**
     * 批量删除租户绑定服务器资源
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiFreeSwitchDeptByIds(Long[] ids);
}
