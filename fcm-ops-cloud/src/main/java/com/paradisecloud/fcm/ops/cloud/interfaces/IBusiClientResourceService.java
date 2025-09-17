package com.paradisecloud.fcm.ops.cloud.interfaces;

import com.paradisecloud.fcm.dao.model.BusiClientResource;
import com.paradisecloud.fcm.dao.model.vo.BusiClientResourceVo;

import java.util.List;

/**
 * ops资源Service接口
 * 
 * @author lilinhai
 * @date 2024-07-31
 */
public interface IBusiClientResourceService 
{
    /**
     * 查询ops资源
     * 
     * @param id ops资源ID
     * @return ops资源
     */
    public BusiClientResource selectBusiClientResourceById(Long id);

    /**
     * 查询ops资源列表
     * 
     * @param busiClientResource ops资源
     * @return ops资源集合
     */
    public List<BusiClientResource> selectBusiClientResourceList(BusiClientResourceVo busiClientResource);

    /**
     * 新增ops资源
     * 
     * @param busiClientResource ops资源
     * @return 结果
     */
    public int insertBusiClientResource(BusiClientResourceVo busiClientResource);

    /**
     * 修改ops资源
     * 
     * @param busiClientResource ops资源
     * @return 结果
     */
    public int updateBusiClientResource(BusiClientResourceVo busiClientResource);

    /**
     * 批量删除ops资源
     * 
     * @param ids 需要删除的ops资源ID
     * @return 结果
     */
    public int deleteBusiClientResourceByIds(Long[] ids);

    /**
     * 删除ops资源信息
     * 
     * @param id ops资源ID
     * @return 结果
     */
    public int deleteBusiClientResourceById(Long id);
}
