package com.paradisecloud.fcm.service.interfaces;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiLiveDept;

import java.util.List;

/**
 * 直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）Service接口
 * 
 * @author lilinhai
 * @date 2022-10-26
 */
public interface IBusiLiveDeptService 
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
    public List<ModelBean> selectBusiLiveDeptList(BusiLiveDept busiLiveDept);

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
     * 批量删除直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * 
     * @param ids 需要删除的直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    public int deleteBusiLiveDeptByIds(Long[] ids);

    /**
     * 删除直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）信息
     * 
     * @param id 直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    public int deleteBusiLiveDeptById(Long id);
}
