package com.paradisecloud.fcm.smc2.cache;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2Dept;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;
import org.springframework.util.Assert;

/**
 * @author nj
 * @date 2023/5/17 16:35
 */
public class DeptSmc2MappingCache extends JavaCache<Long, BusiMcuSmc2Dept> {

    private static final long serialVersionUID = 1L;
    private static final DeptSmc2MappingCache INSTANCE = new DeptSmc2MappingCache();

    /**
     * <pre>构造方法</pre>
     *
     * @author lilinhai
     * @since 2021-01-22 18:07
     */
    private DeptSmc2MappingCache() {
    }

    public static DeptSmc2MappingCache getInstance() {
        return INSTANCE;
    }

    public BusiMcuSmc2Dept getBindSmc(Long key) {
        BusiMcuSmc2Dept busiSmc2Dept = super.get(key);
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
        BusiMcuSmc2Dept last = getBindSmc(deptIds[0]);
        for (int i = 1; i < deptIds.length; i++) {
            BusiMcuSmc2Dept busiSmc2Dept = getBindSmc(deptIds[i]);
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
            BusiMcuSmc2Dept busiSmc2Dept = getBindSmc(deptIds[i]);
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
        for (BusiMcuSmc2Dept busiSmc2Dept : values()) {
            if (FmeType.convert(busiSmc2Dept.getMcuType()) == fmeType && busiSmc2Dept.getMcuId().longValue() == fmeId) {
                c++;
            }
        }
        return c;
    }
}
