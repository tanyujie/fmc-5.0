package com.paradisecloud.smc3.busi.cache;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3Dept;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;
import org.springframework.util.Assert;

/**
 * @author nj
 * @date 2023/5/17 16:35
 */
public class DeptSmc3MappingCache extends JavaCache<Long, BusiMcuSmc3Dept> {

    private static final long serialVersionUID = 1L;
    private static final DeptSmc3MappingCache INSTANCE = new DeptSmc3MappingCache();

    /**
     * <pre>构造方法</pre>
     *
     * @author lilinhai
     * @since 2021-01-22 18:07
     */
    private DeptSmc3MappingCache() {
    }

    public static DeptSmc3MappingCache getInstance() {
        return INSTANCE;
    }

    public BusiMcuSmc3Dept getBindSmc(Long key) {
        BusiMcuSmc3Dept busiTeleDept = super.get(key);
        if (busiTeleDept == null) {
            SysDept sysDept = SysDeptCache.getInstance().get(key);
            if (sysDept.getParentId() != null && sysDept.getParentId().longValue() > 0) {
                return getBindSmc(sysDept.getParentId());
            } else {
                return null;
            }
        } else {
            return busiTeleDept;
        }
    }

    /**
     * 判断多个部门使用绑定同一个FME
     *
     * @param deptIds
     * @return boolean
     * @author lilinhai
     * @since 2021-03-24 09:37
     */
    public boolean isBindSameFme(long... deptIds) {
        Assert.state(deptIds.length > 1, "必须要传入两个及以上的部门ID才能调用本方法！");
        BusiMcuSmc3Dept last = getBindSmc(deptIds[0]);
        for (int i = 1; i < deptIds.length; i++) {
            BusiMcuSmc3Dept busiTeleDept = getBindSmc(deptIds[i]);
            if (!(FmeType.convert(busiTeleDept.getMcuType()) == FmeType.convert(last.getMcuType()) && busiTeleDept.getMcuId().longValue() == last.getMcuId().longValue())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否存在绑定FME集群的部门
     *
     * @param deptIds
     * @return boolean
     * @author lilinhai
     * @since 2021-03-24 09:47
     */
    public boolean isExsitFmeClusterBind(long... deptIds) {
        for (int i = 0; i < deptIds.length; i++) {
            BusiMcuSmc3Dept busiTeleDept = getBindSmc(deptIds[i]);
            if (FmeType.convert(busiTeleDept.getMcuType()) == FmeType.CLUSTER) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回该FME绑定的租户数
     *
     * @param fmeId
     * @return int
     * @author lilinhai
     * @since 2021-03-23 14:22
     */
    public int getBindDeptCount(FmeType fmeType, long fmeId) {
        int c = 0;
        for (BusiMcuSmc3Dept busiTeleDept : values()) {
            if (FmeType.convert(busiTeleDept.getMcuType()) == fmeType && busiTeleDept.getMcuId().longValue() == fmeId) {
                c++;
            }
        }
        return c;
    }

    public BusiMcuSmc3Dept getBindSmc3(long key) {
        BusiMcuSmc3Dept busiMcuSmc3Dept = super.get(key);
        if (busiMcuSmc3Dept == null)
        {
            SysDept sysDept = SysDeptCache.getInstance().get(key);
            if (sysDept.getParentId() != null && sysDept.getParentId().longValue() > 0)
            {
                return getBindSmc3(sysDept.getParentId());
            }
            else
            {
                return null;
            }
        }
        else
        {
            return busiMcuSmc3Dept;
        }
    }
}
