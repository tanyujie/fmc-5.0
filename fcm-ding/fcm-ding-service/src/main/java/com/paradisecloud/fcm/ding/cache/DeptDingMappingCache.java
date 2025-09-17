package com.paradisecloud.fcm.ding.cache;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.dao.model.BusiMcuDingDept;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;
import org.springframework.util.Assert;

/**
 * @author nj
 * @date 2023/5/17 16:35
 */
public class DeptDingMappingCache extends JavaCache<Long, BusiMcuDingDept> {

    private static final long serialVersionUID = 1L;
    private static final DeptDingMappingCache INSTANCE = new DeptDingMappingCache();

    /**
     * <pre>构造方法</pre>
     *
     * @author lilinhai
     * @since 2021-01-22 18:07
     */
    private DeptDingMappingCache() {
    }

    public static DeptDingMappingCache getInstance() {
        return INSTANCE;
    }

    public BusiMcuDingDept getBindSmc(Long key) {
        BusiMcuDingDept busiSmc2Dept = super.get(key);
        if (busiSmc2Dept == null) {
            SysDept sysDept = SysDeptCache.getInstance().get(key);
            if (sysDept.getParentId() != null && sysDept.getParentId().longValue() > 0) {
                return getBindSmc(sysDept.getParentId());
            } else {
                return null;
            }
        } else {
            return busiSmc2Dept;
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
        BusiMcuDingDept last = getBindSmc(deptIds[0]);
        for (int i = 1; i < deptIds.length; i++) {
            BusiMcuDingDept busiSmc2Dept = getBindSmc(deptIds[i]);
            if (!(FmeType.convert(busiSmc2Dept.getMcuType()) == FmeType.convert(last.getMcuType()) && busiSmc2Dept.getMcuId().longValue() == last.getMcuId().longValue())) {
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
            BusiMcuDingDept busiSmc2Dept = getBindSmc(deptIds[i]);
            if (FmeType.convert(busiSmc2Dept.getMcuType()) == FmeType.CLUSTER) {
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
        for (BusiMcuDingDept busiSmc2Dept : values()) {
            if (FmeType.convert(busiSmc2Dept.getMcuType()) == fmeType && busiSmc2Dept.getMcuId().longValue() == fmeId) {
                c++;
            }
        }
        return c;
    }
}
