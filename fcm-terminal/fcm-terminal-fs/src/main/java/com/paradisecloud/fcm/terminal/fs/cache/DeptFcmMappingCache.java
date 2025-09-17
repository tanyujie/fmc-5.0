package com.paradisecloud.fcm.terminal.fs.cache;

import com.paradisecloud.fcm.common.enumer.FcmType;
import org.springframework.util.Assert;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;

import java.util.ArrayList;
import java.util.List;

/**  
 * <pre>FCM组-部门映射缓存</pre>
 * key为部门ID
 * @author zyz
 * @since 2021-11-09 17:50
 * @version V1.0  
 */
public class DeptFcmMappingCache extends JavaCache<Long, BusiFreeSwitchDept>
{
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-01-28 13:36 
     */
    private static final long serialVersionUID = 1L;
    private static final DeptFcmMappingCache INSTANCE = new DeptFcmMappingCache();

    /**
     * <pre>构造方法</pre>
     * @author zyz 
     * @since 2021-01-22 18:07  
     */
    private DeptFcmMappingCache()
    {
    }
    
    public static DeptFcmMappingCache getInstance()
    {
        return INSTANCE;
    }
    
    @Override
    public BusiFreeSwitchDept get(Object key)
    {
    	BusiFreeSwitchDept busiFreeSwitchDept = super.get(key);
        if (busiFreeSwitchDept == null)
        {
            SysDept sysDept = SysDeptCache.getInstance().get(key);
            if (sysDept.getParentId() != null && sysDept.getParentId().longValue() > 0)
            {
                return get(sysDept.getParentId());
            }
            else
            {
                return null;
            }
        }
        else
        {
            return busiFreeSwitchDept;
        }
    }

    public boolean isBindSameFcm(long... deptIds)
    {
        Assert.state(deptIds.length > 1, "必须要传入两个及以上的部门ID才能调用本方法！");
        BusiFreeSwitchDept last = get(deptIds[0]);
        for (int i = 1; i < deptIds.length; i++)
        {
        	BusiFreeSwitchDept busiFreeSwitchDept = get(deptIds[i]);
            if (!(busiFreeSwitchDept.getServerId().equals(last.getServerId())))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否存在绑定FCM集群的部门
     * @author lilinhai
     * @since 2021-03-24 09:47
     * @param deptIds
     * @return boolean
     */
    public boolean isExistFcmClusterBind(long... deptIds)
    {
        for (int i = 0; i < deptIds.length; i++)
        {
            BusiFreeSwitchDept busiFreeSwitchDept = get(deptIds[i]);
            if (FcmType.convert(busiFreeSwitchDept.getFcmType()) == FcmType.CLUSTER)
            {
                return true;
            }
        }
        return false;
    }
    
    public int getBindDeptCount(FcmType fcmType, long fsServerId)
    {
        int c = 0;
        for (BusiFreeSwitchDept busiFreeSwitchDept : values())
        {
            if (FcmType.convert(busiFreeSwitchDept.getFcmType()) == fcmType && fsServerId == busiFreeSwitchDept.getServerId())
            {
                c++;
            }
        }
        return c;
    }

    /**
     * 通过集群id获取绑定该集群的部门
     *
     * @param clusterId
     * @return
     */
    public List<BusiFreeSwitchDept> getDeptListByClusterId(long clusterId) {
        List<BusiFreeSwitchDept> deptList = new ArrayList<>();
        for (BusiFreeSwitchDept busiFreeSwitchDept : values()) {
            if (FcmType.CLUSTER.getValue() == busiFreeSwitchDept.getFcmType()) {
                if (busiFreeSwitchDept.getServerId() == clusterId) {
                    deptList.add(busiFreeSwitchDept);
                }
            }
        }
        return deptList;
    }

    /**
     * 通过fcmDd获取绑定该fcm的部门
     *
     * @param fcmId
     * @return
     */
    public List<BusiFreeSwitchDept> getDeptListByFcmId(long fcmId) {
        List<BusiFreeSwitchDept> deptList = new ArrayList<>();
        for (BusiFreeSwitchDept busiFreeSwitchDept : values()) {
            if (FcmType.SINGLE_NODE.getValue() == busiFreeSwitchDept.getFcmType()) {
                if (busiFreeSwitchDept.getServerId() == fcmId) {
                    deptList.add(busiFreeSwitchDept);
                }
            }
        }
        return deptList;
    }
}
