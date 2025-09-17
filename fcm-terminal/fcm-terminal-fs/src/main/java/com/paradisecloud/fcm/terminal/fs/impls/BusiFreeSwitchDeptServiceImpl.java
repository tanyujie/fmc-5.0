package com.paradisecloud.fcm.terminal.fs.impls;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.paradisecloud.fcm.common.enumer.FcmType;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchCluster;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.cache.FreeSwitchClusterCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridgeCluster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.mapper.BusiFreeSwitchDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiFreeSwitchMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitch;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.constant.FcmConfigConstant;
import com.paradisecloud.fcm.terminal.fs.interfaces.IBusiFreeSwitchDeptService;
import com.sinhy.exception.SystemException;


/**
 * 租户绑定服务器资源Service业务层处理
 * 
 * @author zyz
 * @date 2021-09-02
 */
@Service
public class BusiFreeSwitchDeptServiceImpl implements IBusiFreeSwitchDeptService 
{
    @Autowired
    private BusiFreeSwitchDeptMapper busiFreeSwitchDeptMapper;
    
    @Autowired
    private BusiFreeSwitchMapper busiFreeSwitchMapper;
    
    @Autowired
    private BusiTerminalMapper busiTerminalMapper;

    /**
     * 查询租户绑定服务器资源
     * 
     * @param id 租户绑定服务器资源ID
     * @return 租户绑定服务器资源
     */
    @Override
    public BusiFreeSwitchDept selectBusiFreeSwitchDeptById(Long id)
    {
        return busiFreeSwitchDeptMapper.selectBusiFreeSwitchDeptById(id);
    }

    /**
     * 查询租户绑定服务器资源列表
     * 
     * @param busiFreeSwitchDept 租户绑定服务器资源
     * @return 租户绑定服务器资源
     */
    @Override
    public List<BusiFreeSwitchDept> selectBusiFreeSwitchDeptList(BusiFreeSwitchDept busiFreeSwitchDept)
    {
        return busiFreeSwitchDeptMapper.selectBusiFreeSwitchDeptList(busiFreeSwitchDept);
    }

    /**
     * 新增租户绑定服务器资源
     * 
     * @param busiFreeSwitchDept 租户绑定服务器资源
     * @return 结果
     */
    @Override
    public int insertBusiFreeSwitchDept(BusiFreeSwitchDept busiFreeSwitchDept)
    {
        busiFreeSwitchDept.setCreateTime(new Date());
        if (busiFreeSwitchDept.getFcmType() == null) {
            busiFreeSwitchDept.setFcmType(FcmType.SINGLE_NODE.getValue());
        }
        if (FcmType.CLUSTER.getValue() == busiFreeSwitchDept.getFcmType()) {
            BusiFreeSwitchCluster busiFreeSwitchCluster = FreeSwitchClusterCache.getInstance().get(busiFreeSwitchDept.getServerId());
            if (busiFreeSwitchCluster == null) {
                throw new SystemException(1000013, "绑定FCM集群出错，找不到指定的FCM集群。");
            }
        } else if (FcmType.SINGLE_NODE.getValue() == busiFreeSwitchDept.getFcmType()) {
            FcmBridge fcmBridge = FcmBridgeCache.getInstance().get(busiFreeSwitchDept.getServerId());
            if (fcmBridge == null) {
                throw new SystemException(1000013, "绑定FCM出错，找不到指定的FCM。");
            }
        } else {
            throw new SystemException(1000013, "绑定FCM出错，FCM类型不正确。");
        }
        int insert = busiFreeSwitchDeptMapper.insertBusiFreeSwitchDept(busiFreeSwitchDept);
        if(insert > 0) {
        	DeptFcmMappingCache.getInstance().put(busiFreeSwitchDept.getDeptId(), busiFreeSwitchDept);
        }
        return insert;
    }

    /**
     * 修改租户绑定服务器资源
     * 
     * @param busiFreeSwitchDept 租户绑定服务器资源
     * @return 结果
     */
    @Override
    public int updateBusiFreeSwitchDept(BusiFreeSwitchDept busiFreeSwitchDept)
    {
        busiFreeSwitchDept.setUpdateTime(new Date());
        if (busiFreeSwitchDept.getFcmType() == null) {
            busiFreeSwitchDept.setFcmType(FcmType.SINGLE_NODE.getValue());
        }
        if (FcmType.CLUSTER.getValue() == busiFreeSwitchDept.getFcmType()) {
            BusiFreeSwitchCluster busiFreeSwitchCluster = FreeSwitchClusterCache.getInstance().get(busiFreeSwitchDept.getServerId());
            if (busiFreeSwitchCluster == null) {
                throw new SystemException(1000013, "绑定FCM集群出错，找不到指定的FCM集群。");
            }
        } else if (FcmType.SINGLE_NODE.getValue() == busiFreeSwitchDept.getFcmType()) {
            FcmBridge fcmBridge = FcmBridgeCache.getInstance().get(busiFreeSwitchDept.getServerId());
            if (fcmBridge == null) {
                throw new SystemException(1000013, "绑定FCM出错，找不到指定的FCM。");
            }
        } else {
            throw new SystemException(1000013, "绑定FCM出错，FCM类型不正确。");
        }
        int update = busiFreeSwitchDeptMapper.updateBusiFreeSwitchDept(busiFreeSwitchDept);
        if(update > 0) {
        	DeptFcmMappingCache.getInstance().put(busiFreeSwitchDept.getDeptId(), busiFreeSwitchDeptMapper.selectBusiFreeSwitchDeptById(busiFreeSwitchDept.getId()));
        }
        return update;
    }

    /**
     * 批量删除租户绑定服务器资源
     * 
     * @param ids 需要删除的租户绑定服务器资源ID
     * @return 结果
     */
    @Override
    public int deleteBusiFreeSwitchDeptByIds(Long[] ids)
    {
    	int delete = FcmConfigConstant.ZERO;
    	if(ids.length > 0) {
    		for (int i = 0; i < ids.length; i++) {
    			BusiFreeSwitchDept busiFreeSwitchDept = busiFreeSwitchDeptMapper.selectBusiFreeSwitchDeptById(ids[i]);
    			if(null != busiFreeSwitchDept) {
        			BusiTerminal busiTerminal = new BusiTerminal();
        			busiTerminal.setType(TerminalType.FCM_SIP.getId());
        			busiTerminal.setDeptId(busiFreeSwitchDept.getDeptId());
        			
        			List<BusiTerminal> terminalList = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
        			if(null != terminalList && terminalList.size() > 0) {
        				throw new SystemException(1000013, "FCM节点的删除，请先删除该节点下的终端！");
        			}
        			
        			delete = busiFreeSwitchDeptMapper.deleteBusiFreeSwitchDeptById(ids[i]);
        			if(delete > 0) {
        				DeptFcmMappingCache.getInstance().remove(busiFreeSwitchDept.getDeptId());
        			}
    			}
			}
    	}
    	
        return delete;
    }

    /**
     * 删除租户绑定服务器资源信息
     * 
     * @param id 租户绑定服务器资源ID
     * @return 结果
     */
    @Override
    public int deleteBusiFreeSwitchDeptById(Long id)
    {
        return busiFreeSwitchDeptMapper.deleteBusiFreeSwitchDeptById(id);
    }

	@Override
	public List<ModelBean> selectBusiFreeSwitchDepts(BusiFreeSwitchDept busiFreeSwitchDept) {
		List<ModelBean> modelBeans = new ArrayList<ModelBean>();
		BusiFreeSwitchDept freeSwitchDept = new BusiFreeSwitchDept();
		List<BusiFreeSwitchDept> freeSwitchDeptList = busiFreeSwitchDeptMapper.selectBusiFreeSwitchDeptList(freeSwitchDept);
		if(null != freeSwitchDeptList && freeSwitchDeptList.size() > 0) {
			for (BusiFreeSwitchDept biFreeSwitchDept : freeSwitchDeptList) {
				ModelBean mlb = new ModelBean(biFreeSwitchDept);
				StringBuilder sb = new StringBuilder();
				mlb.remove("createTime");
				mlb.remove("updateTime");

                //返回服务器的是否正在运行
                if (FcmType.CLUSTER == FcmType.convert(biFreeSwitchDept.getFcmType())) {
                    BusiFreeSwitchCluster busiFreeSwitchCluster = FreeSwitchClusterCache.getInstance().get(biFreeSwitchDept.getServerId());
                    if (busiFreeSwitchCluster != null) {
                        sb.append("【").append(busiFreeSwitchCluster.getName()).append("】");
                        FcmBridgeCluster fcmBridgeCluster = FcmBridgeCache.getInstance().getByFcmClusterId(biFreeSwitchDept.getServerId());
                        sb.append("SERVER[");
                        List<FcmBridge> fcmBridges = fcmBridgeCluster.getAvailableFcmBridges();
                        int serverStatus = TerminalOnlineStatus.OFFLINE.getValue();
                        if (fcmBridges.size() > 0) {
                            serverStatus = TerminalOnlineStatus.ONLINE.getValue();
                            for (int i = 0; i < fcmBridges.size(); i++) {
                                FcmBridge fcmBridge = fcmBridges.get(i);
                                if (i > 0) {
                                    sb.append(", ");
                                }
                                sb.append(fcmBridge.getBusiFreeSwitch().getIp());
                            }
                        }
                        sb.append("]");
                        mlb.put("serverStatus", serverStatus);
                        mlb.put("fcmTypeName", FcmType.CLUSTER.getName());
                    }
                } else {
                    FcmBridge fcmBridge = FcmBridgeCache.getInstance().get(biFreeSwitchDept.getServerId());
                    if (fcmBridge != null) {
                        BusiFreeSwitch busiFreeSwitch = fcmBridge.getBusiFreeSwitch();
                        if (null != busiFreeSwitch) {
                            sb.append("【").append(busiFreeSwitch.getName()).append("】");
                            sb.append("SERVER[" + busiFreeSwitch.getIp() + "]");
                        }
                        int serverStatus = fcmBridge.isAvailable() ? TerminalOnlineStatus.ONLINE.getValue() : TerminalOnlineStatus.OFFLINE.getValue();
                        mlb.put("serverStatus", serverStatus);
                        mlb.put("fcmTypeName", FcmType.SINGLE_NODE.getName());
                    }
                }
                mlb.put("serverInfo", sb.toString());
                modelBeans.add(mlb);
            }
		}
		return modelBeans;
	}

	/**
	 * @param mlb
	 * @param busiFreeSwitch
	 * @return
	 */
	private ModelBean serverStatus(ModelBean mlb, BusiFreeSwitch busiFreeSwitch) {
		Boolean isSuccess = this.pingIp(busiFreeSwitch.getIp());
		if(isSuccess) 
		{
			mlb.put("serverStatus", TerminalOnlineStatus.ONLINE.getValue());
		}else {
			mlb.put("serverStatus", TerminalOnlineStatus.OFFLINE.getValue());
		}
		return mlb;
	}
	
	public Boolean pingIp(String ip) 
	{
      if (null == ip || 0 == ip.length()) {
           return false;
      }
      
      try 
      {
          InetAddress.getByName(ip);
          return true;
     } 
     catch (IOException e) 
     {
          return false;
     }
	}
}
