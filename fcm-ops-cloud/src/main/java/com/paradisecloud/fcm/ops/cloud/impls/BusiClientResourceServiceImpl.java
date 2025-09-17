package com.paradisecloud.fcm.ops.cloud.impls;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.PurchaseType;
import com.paradisecloud.fcm.dao.mapper.BusiClientResourceMapper;
import com.paradisecloud.fcm.dao.model.BusiClient;
import com.paradisecloud.fcm.dao.model.BusiClientResource;
import com.paradisecloud.fcm.dao.model.vo.BusiClientResourceVo;
import com.paradisecloud.fcm.ops.cloud.cache.ClientCache;
import com.paradisecloud.fcm.ops.cloud.interfaces.IBusiClientResourceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 客户端资源Service业务层处理
 * 
 * @author lilinhai
 * @date 2024-07-31
 */
@Service
public class BusiClientResourceServiceImpl implements IBusiClientResourceService
{
    @Resource
    private BusiClientResourceMapper busiClientResourceMapper;

    /**
     * 查询ops资源
     * 
     * @param id ops资源ID
     * @return ops资源
     */
    @Override
    public BusiClientResource selectBusiClientResourceById(Long id)
    {
        return busiClientResourceMapper.selectBusiClientResourceById(id);
    }

    /**
     * 查询ops资源列表
     * 
     * @param busiClientResource ops资源
     * @return ops资源
     */
    @Override
    public List<BusiClientResource> selectBusiClientResourceList(BusiClientResourceVo busiClientResource)
    {
        if (busiClientResource.getClientId() != null) {
            BusiClient busiClient = ClientCache.getInstance().get(busiClientResource.getClientId());
            if (busiClient != null) {
                busiClientResource.setSn(busiClient.getSn());
            }
        }
        return busiClientResourceMapper.selectBusiClientResourceList(busiClientResource);
    }

    /**
     * 新增ops资源
     * 
     * @param busiClientResource ops资源
     * @return 结果
     */
    @Override
    public int insertBusiClientResource(BusiClientResourceVo busiClientResource)
    {
        busiClientResource.setCreateTime(new Date());
        if (busiClientResource.getClientId() != null) {
            BusiClient busiClient = ClientCache.getInstance().get(busiClientResource.getClientId());
            if (busiClient != null) {
                busiClientResource.setSn(busiClient.getSn());
            } else {
                throw new CustomException("客户端不存在！");
            }
            BusiClientResource busiClientResourceCon = new BusiClientResource();
            busiClientResourceCon.setSn(busiClient.getSn());
            busiClientResourceCon.setPurchaseType(busiClientResource.getPurchaseType());
            busiClientResourceCon.setMcuType(busiClientResource.getMcuType());
            if (!PurchaseType.MEETING.getCode().equals(busiClientResource.getPurchaseType())) {
                busiClientResource.setMcuType(null);
            }
            List<BusiClientResource> resourcesExist = busiClientResourceMapper.selectBusiClientResourceList(busiClientResourceCon);
            if (resourcesExist.size() > 0) {
                throw new CustomException("该资源已分配，不能重复分配！");
            }
            return busiClientResourceMapper.insertBusiClientResource(busiClientResource);
        }
        return 0;
    }

    /**
     * 修改ops资源
     * 
     * @param busiClientResource ops资源
     * @return 结果
     */
    @Override
    public int updateBusiClientResource(BusiClientResourceVo busiClientResource)
    {
        busiClientResource.setUpdateTime(new Date());
        busiClientResource.setSn(null);
        if (!PurchaseType.MEETING.getCode().equals(busiClientResource.getPurchaseType())) {
            busiClientResource.setMcuType(null);
        }
        return busiClientResourceMapper.updateBusiClientResource(busiClientResource);
    }

    /**
     * 批量删除ops资源
     * 
     * @param ids 需要删除的ops资源ID
     * @return 结果
     */
    @Override
    public int deleteBusiClientResourceByIds(Long[] ids)
    {
        return busiClientResourceMapper.deleteBusiClientResourceByIds(ids);
    }

    /**
     * 删除ops资源信息
     * 
     * @param id ops资源ID
     * @return 结果
     */
    @Override
    public int deleteBusiClientResourceById(Long id)
    {
        return busiClientResourceMapper.deleteBusiClientResourceById(id);
    }
}
