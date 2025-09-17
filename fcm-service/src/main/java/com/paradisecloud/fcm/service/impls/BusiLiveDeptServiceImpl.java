package com.paradisecloud.fcm.service.impls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Date;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.FcmType;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.dao.mapper.BusiLiveClusterMapper;
import com.paradisecloud.fcm.dao.mapper.BusiLiveDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiLiveMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.service.interfaces.IBusiLiveDeptService;
import com.paradisecloud.fcm.terminal.fs.cache.*;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridgeCluster;
import com.paradisecloud.fcm.terminal.fs.model.LiveBridge;
import com.paradisecloud.fcm.terminal.fs.model.LiveBridgeCluster;
import com.sinhy.exception.SystemException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）Service业务层处理
 * 
 * @author lilinhai
 * @date 2022-10-26
 */
@Service
public class BusiLiveDeptServiceImpl implements IBusiLiveDeptService
{
    @Resource
    private BusiLiveDeptMapper busiLiveDeptMapper;
    @Resource
    private BusiLiveMapper busiLiveMapper;
    @Resource
    private BusiLiveClusterMapper busiLiveClusterMapper;

    /**
     * 查询直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * 
     * @param id 直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）ID
     * @return 直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     */
    @Override
    public BusiLiveDept selectBusiLiveDeptById(Long id)
    {
        return busiLiveDeptMapper.selectBusiLiveDeptById(id);
    }

    /**
     * 查询直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）列表
     * 
     * @param busiLiveDept 直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * @return 直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     */
    @Override
    public List<ModelBean> selectBusiLiveDeptList(BusiLiveDept busiLiveDept) {
        List<BusiLiveDept> liveDeptList = busiLiveDeptMapper.selectBusiLiveDeptList(busiLiveDept);
        List<ModelBean> modelBeans = new ArrayList<>();
        for (BusiLiveDept liveDept : liveDeptList) {
            ModelBean modelBean = new ModelBean();
            modelBean.put("deptId", liveDept.getDeptId());
            modelBean.put("liveType", liveDept.getLiveType());
            modelBean.put("liveTypeName", liveDept.getLiveType() == 1 ? "单节点" : "集群");
            modelBean.put("id", liveDept.getId());
            modelBean.put("liveId", liveDept.getLiveId());
//            【155】SERVER[172.16.100.155]
//            BusiLive busiLive = LiveBridgeCache.getInstance().get(liveDept.getLiveId()).getBusiLive();
            StringBuilder sb = new StringBuilder();
            if (100 == liveDept.getLiveType()) {
                BusiLiveCluster busiLiveCluster = LiveClusterCache.getInstance().get(liveDept.getLiveId());
                LiveBridgeCluster byliveClusterId = LiveBridgeCache.getInstance().getByliveClusterId(busiLiveCluster.getId());
                List<LiveBridge> liveBridges = byliveClusterId.getLiveBridges();
                if (liveBridges != null && liveBridges.size() > 0) {
                    sb.append("【").append(busiLiveCluster.getName()).append("】");
                    sb.append("SERVER[");
                    List<BusiLiveCluster> copiedAllValues = LiveClusterCache.getInstance().getCopiedAllValues();
                    if (copiedAllValues.size() > 0) {
                        for (LiveBridge liveBridge : byliveClusterId.getLiveBridges()) {
                            BusiLive busiLive1 = liveBridge.getBusiLive();
                            sb.append(busiLive1.getIp());
                            sb.append(", ");
                            modelBean.put("serverStatus", busiLive1.getStatus());
                        }
                    }
                    sb.append("]");

                }
            } else {
                BusiLive busiLive = LiveBridgeCache.getInstance().getLiveBridgeMap().get(liveDept.getLiveId()).getBusiLive();
                if (null != busiLive) {
                    sb.append("【").append(busiLive.getName()).append("】");
                    sb.append("SERVER[" + busiLive.getIp() + "]");
                    modelBean.put("serverStatus", busiLive.getStatus());
                }
            }
            modelBean.put("liveInfo", sb);
            modelBeans.add(modelBean);
        }

        return modelBeans;
    }

    /**
     * 新增直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * 
     * @param busiLiveDept 直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * @return 结果
     */
    @Override
    public int insertBusiLiveDept(BusiLiveDept busiLiveDept)
    {
        if (busiLiveDept.getLiveType() == 1) {
            BusiLive busiLive = busiLiveMapper.selectBusiLiveById(busiLiveDept.getLiveId());
            if (busiLive == null) {
                throw new SystemException(1000013, "绑定直播服务器出错，找不到指定的直播服务器。");
            }
        }
        if (busiLiveDept.getLiveType() == 100) {
            BusiLiveCluster busiLiveCluster = busiLiveClusterMapper.selectBusiLiveClusterById(busiLiveDept.getLiveId());
            if (busiLiveCluster == null) {
                throw new SystemException(1000013, "绑定直播服务器集群出错，找不到指定的直播服务器集群。");
            }
        }

        busiLiveDept.setCreateTime(new Date());
        int i = busiLiveDeptMapper.insertBusiLiveDept(busiLiveDept);
        BusiLiveDept busiLiveDept1 = busiLiveDeptMapper.selectBusiLiveDeptById(busiLiveDept.getId());
        if (i > 0) {
            LiveDeptCache.getInstance().put(busiLiveDept1.getDeptId(), busiLiveDept1);
        }
        return i;
    }

    /**
     * 修改直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * 
     * @param busiLiveDept 直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * @return 结果
     */
    @Override
    public int updateBusiLiveDept(BusiLiveDept busiLiveDept)
    {
        busiLiveDept.setUpdateTime(new Date());
        int i = busiLiveDeptMapper.updateBusiLiveDept(busiLiveDept);
        BusiLiveDept busiLiveDept1 = busiLiveDeptMapper.selectBusiLiveDeptById(busiLiveDept.getId());
        if (i > 0 && busiLiveDept1 != null) {
            LiveDeptCache.getInstance().put(busiLiveDept1.getDeptId(), busiLiveDept1);
        }
        return i;
    }

    /**
     * 批量删除直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * 
     * @param ids 需要删除的直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    @Override
    public int deleteBusiLiveDeptByIds(Long[] ids)
    {
        int i = 0;
        for (Long id : ids) {
            BusiLiveDept busiLiveDept = busiLiveDeptMapper.selectBusiLiveDeptById(id);
            if (busiLiveDept != null) {
                i = busiLiveDeptMapper.deleteBusiLiveDeptById(id);
                if (i > 0) {
                    LiveDeptCache.getInstance().remove(busiLiveDept.getDeptId());
                }
            }
        }
        return i;
    }

    /**
     * 删除直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）信息
     * 
     * @param id 直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    @Override
    public int deleteBusiLiveDeptById(Long id)
    {
        return busiLiveDeptMapper.deleteBusiLiveDeptById(id);
    }
}
