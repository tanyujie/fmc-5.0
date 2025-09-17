package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiLiveDept;

import java.util.List;

/**
 * 直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）Mapper接口
 * 
 * @author lilinhai
 * @date 2022-10-26
 */
public interface BusiLiveDeptMapper 
{
    /**
     * 查询直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * 
     * @param id 直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）ID
     * @return 直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     */
    public BusiLiveDept selectBusiLiveDeptById(Long id);

    /**
     * 查询直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）列表
     * 
     * @param busiLiveDept 直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * @return 直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）集合
     */
    public List<BusiLiveDept> selectBusiLiveDeptList(BusiLiveDept busiLiveDept);

    /**
     * 新增直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * 
     * @param busiLiveDept 直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * @return 结果
     */
    public int insertBusiLiveDept(BusiLiveDept busiLiveDept);

    /**
     * 修改直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * 
     * @param busiLiveDept 直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * @return 结果
     */
    public int updateBusiLiveDept(BusiLiveDept busiLiveDept);

    /**
     * 删除直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * 
     * @param id 直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    public int deleteBusiLiveDeptById(Long id);

    /**
     * 批量删除直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiLiveDeptByIds(Long[] ids);
}
