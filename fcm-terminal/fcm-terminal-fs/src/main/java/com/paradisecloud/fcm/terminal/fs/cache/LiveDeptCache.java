package com.paradisecloud.fcm.terminal.fs.cache;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.common.enumer.FcmType;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;
import com.paradisecloud.fcm.dao.model.BusiLiveDept;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class LiveDeptCache extends JavaCache<Long, BusiLiveDept> {

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-01-28 13:36
     */
    private static final long serialVersionUID = 1L;
    private static final LiveDeptCache INSTANCE = new LiveDeptCache();

    /**
     * <pre>构造方法</pre>
     * @author zyz
     * @since 2021-01-22 18:07
     */
    private LiveDeptCache()
    {
    }

    public static LiveDeptCache getInstance()
    {
        return INSTANCE;
    }

    @Override
    public BusiLiveDept get(Object key)
    {
        BusiLiveDept busiFreeSwitchDept = super.get(key);
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
        BusiLiveDept last = get(deptIds[0]);
        for (int i = 1; i < deptIds.length; i++)
        {
            BusiLiveDept busiLiveDept = get(deptIds[i]);
            if (!(busiLiveDept.getLiveId().equals(last.getLiveId())))
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
    public boolean isExistLiveClusterBind(long... deptIds)
    {
        for (int i = 0; i < deptIds.length; i++)
        {
            BusiLiveDept busiLiveDept = get(deptIds[i]);
            if (FcmType.convert(busiLiveDept.getLiveType()) == FcmType.CLUSTER)
            {
                return true;
            }
        }
        return false;
    }

    public int getBindDeptCount(FcmType liveType, long liveId)
    {
        int c = 0;
        for (BusiLiveDept busiLiveDept : values())
        {
            if (FcmType.convert(busiLiveDept.getLiveType()) == liveType && liveId == busiLiveDept.getLiveId())
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
    public List<BusiLiveDept> getDeptListByClusterId(long clusterId) {
        List<BusiLiveDept> deptList = new ArrayList<>();
        for (BusiLiveDept busiLiveDept : values()) {
            if (FcmType.CLUSTER.getValue() == busiLiveDept.getLiveType()) {
                if (busiLiveDept.getLiveId() == clusterId) {
                    deptList.add(busiLiveDept);
                }
            }
        }
        return deptList;
    }

    /**
     * 通过fcmDd获取绑定该直播的部门
     *
     * @param fcmId
     * @return
     */
    public List<BusiLiveDept> getDeptListByLiveId(long fcmId) {
        List<BusiLiveDept> deptList = new ArrayList<>();
        for (BusiLiveDept busiLiveDept : values()) {
            if (FcmType.SINGLE_NODE.getValue() == busiLiveDept.getLiveType()) {
                if (busiLiveDept.getLiveId() == fcmId) {
                    deptList.add(busiLiveDept);
                }
            }
        }
        return deptList;
    }
}
