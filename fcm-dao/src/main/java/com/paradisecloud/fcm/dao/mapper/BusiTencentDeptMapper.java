package com.paradisecloud.fcm.dao.mapper;


import com.paradisecloud.fcm.dao.model.BusiTencentDept;

import java.util.List;

/**
 * smc2部门绑定Mapper接口
 * 
 * @author lilinhai
 * @date 2023-07-05
 */
public interface BusiTencentDeptMapper 
{
    /**
     * 查询smc2部门绑定
     * 
     * @param id smc2部门绑定ID
     * @return smc2部门绑定
     */
    public BusiTencentDept selectBusiTencentDeptById(Long id);

    /**
     * 查询smc2部门绑定列表
     * 
     * @param busiTencentDept smc2部门绑定
     * @return smc2部门绑定集合
     */
    public List<BusiTencentDept> selectBusiTencentDeptList(BusiTencentDept busiTencentDept);

    /**
     * 新增smc2部门绑定
     * 
     * @param busiTencentDept smc2部门绑定
     * @return 结果
     */
    public int insertBusiTencentDept(BusiTencentDept busiTencentDept);

    /**
     * 修改smc2部门绑定
     * 
     * @param busiTencentDept smc2部门绑定
     * @return 结果
     */
    public int updateBusiTencentDept(BusiTencentDept busiTencentDept);

    /**
     * 删除smc2部门绑定
     * 
     * @param id smc2部门绑定ID
     * @return 结果
     */
    public int deleteBusiTencentDeptById(Long id);

    /**
     * 批量删除smc2部门绑定
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiTencentDeptByIds(Long[] ids);
}
