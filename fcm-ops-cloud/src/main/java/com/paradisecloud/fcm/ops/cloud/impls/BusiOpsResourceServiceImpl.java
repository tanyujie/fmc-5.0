package com.paradisecloud.fcm.ops.cloud.impls;

import java.util.Date;
import java.util.List;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.PurchaseType;
import com.paradisecloud.fcm.dao.mapper.BusiOpsResourceMapper;
import com.paradisecloud.fcm.dao.model.BusiOps;
import com.paradisecloud.fcm.dao.model.BusiOpsResource;
import com.paradisecloud.fcm.dao.model.vo.BusiOpsResourceVo;
import com.paradisecloud.fcm.ops.cloud.cache.OpsCache;
import com.paradisecloud.fcm.ops.cloud.interfaces.IBusiOpsResourceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * ops资源Service业务层处理
 * 
 * @author lilinhai
 * @date 2024-07-31
 */
@Service
public class BusiOpsResourceServiceImpl implements IBusiOpsResourceService
{
    @Resource
    private BusiOpsResourceMapper busiOpsResourceMapper;

    /**
     * 查询ops资源
     * 
     * @param id ops资源ID
     * @return ops资源
     */
    @Override
    public BusiOpsResource selectBusiOpsResourceById(Long id)
    {
        return busiOpsResourceMapper.selectBusiOpsResourceById(id);
    }

    /**
     * 查询ops资源列表
     * 
     * @param busiOpsResource ops资源
     * @return ops资源
     */
    @Override
    public List<BusiOpsResource> selectBusiOpsResourceList(BusiOpsResourceVo busiOpsResource)
    {
        if (busiOpsResource.getOpsId() != null) {
            BusiOps busiOps = OpsCache.getInstance().get(busiOpsResource.getOpsId());
            if (busiOps != null) {
                busiOpsResource.setSn(busiOps.getSn());
            }
        }
        return busiOpsResourceMapper.selectBusiOpsResourceList(busiOpsResource);
    }

    /**
     * 新增ops资源
     * 
     * @param busiOpsResource ops资源
     * @return 结果
     */
    @Override
    public int insertBusiOpsResource(BusiOpsResourceVo busiOpsResource)
    {
        busiOpsResource.setCreateTime(new Date());
        if (busiOpsResource.getOpsId() != null) {
            BusiOps busiOps = OpsCache.getInstance().get(busiOpsResource.getOpsId());
            if (busiOps != null) {
                busiOpsResource.setSn(busiOps.getSn());
            } else {
                throw new CustomException("OPS不存在！");
            }
            BusiOpsResource busiOpsResourceCon = new BusiOpsResource();
            busiOpsResourceCon.setSn(busiOps.getSn());
            busiOpsResourceCon.setPurchaseType(busiOpsResource.getPurchaseType());
            busiOpsResourceCon.setMcuType(busiOpsResource.getMcuType());
            if (!PurchaseType.MEETING.getCode().equals(busiOpsResource.getPurchaseType())) {
                busiOpsResource.setMcuType(null);
            }
            List<BusiOpsResource> resourcesExist = busiOpsResourceMapper.selectBusiOpsResourceList(busiOpsResourceCon);
            if (resourcesExist.size() > 0) {
                throw new CustomException("该资源已分配，不能重复分配！");
            }
            return busiOpsResourceMapper.insertBusiOpsResource(busiOpsResource);
        }
        return 0;
    }

    /**
     * 修改ops资源
     * 
     * @param busiOpsResource ops资源
     * @return 结果
     */
    @Override
    public int updateBusiOpsResource(BusiOpsResourceVo busiOpsResource)
    {
        busiOpsResource.setUpdateTime(new Date());
        busiOpsResource.setSn(null);
        if (!PurchaseType.MEETING.getCode().equals(busiOpsResource.getPurchaseType())) {
            busiOpsResource.setMcuType(null);
        }
        return busiOpsResourceMapper.updateBusiOpsResource(busiOpsResource);
    }

    /**
     * 批量删除ops资源
     * 
     * @param ids 需要删除的ops资源ID
     * @return 结果
     */
    @Override
    public int deleteBusiOpsResourceByIds(Long[] ids)
    {
        return busiOpsResourceMapper.deleteBusiOpsResourceByIds(ids);
    }

    /**
     * 删除ops资源信息
     * 
     * @param id ops资源ID
     * @return 结果
     */
    @Override
    public int deleteBusiOpsResourceById(Long id)
    {
        return busiOpsResourceMapper.deleteBusiOpsResourceById(id);
    }
}
