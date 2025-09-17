package com.paradisecloud.fcm.ops.cloud.interfaces;

import com.paradisecloud.fcm.dao.model.BusiOpsResource;
import com.paradisecloud.fcm.dao.model.vo.BusiOpsResourceVo;

import java.util.List;

/**
 * ops资源Service接口
 * 
 * @author lilinhai
 * @date 2024-07-31
 */
public interface IBusiOpsResourceService 
{
    /**
     * 查询ops资源
     * 
     * @param id ops资源ID
     * @return ops资源
     */
    public BusiOpsResource selectBusiOpsResourceById(Long id);

    /**
     * 查询ops资源列表
     * 
     * @param busiOpsResource ops资源
     * @return ops资源集合
     */
    public List<BusiOpsResource> selectBusiOpsResourceList(BusiOpsResourceVo busiOpsResource);

    /**
     * 新增ops资源
     * 
     * @param busiOpsResource ops资源
     * @return 结果
     */
    public int insertBusiOpsResource(BusiOpsResourceVo busiOpsResource);

    /**
     * 修改ops资源
     * 
     * @param busiOpsResource ops资源
     * @return 结果
     */
    public int updateBusiOpsResource(BusiOpsResourceVo busiOpsResource);

    /**
     * 批量删除ops资源
     * 
     * @param ids 需要删除的ops资源ID
     * @return 结果
     */
    public int deleteBusiOpsResourceByIds(Long[] ids);

    /**
     * 删除ops资源信息
     * 
     * @param id ops资源ID
     * @return 结果
     */
    public int deleteBusiOpsResourceById(Long id);
}
