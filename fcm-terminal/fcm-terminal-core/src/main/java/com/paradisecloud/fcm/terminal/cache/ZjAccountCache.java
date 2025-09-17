package com.paradisecloud.fcm.terminal.cache;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.dao.model.BusiZjNumberSection;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZjAccountCache extends JavaCache<Integer, BusiZjNumberSection> {

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     *
     * @since 2021-01-28 13:36
     */
    private static final long serialVersionUID = 1L;
    private static final ZjAccountCache INSTANCE = new ZjAccountCache();
    private Map<Long, BusiZjNumberSection> zjAccountMap = new ConcurrentHashMap<>();
    private Map<Long, List<BusiZjNumberSection>> zjAccountByDeptMap = new ConcurrentHashMap<>();

    /**
     * <pre>构造方法</pre>
     *
     * @author
     * @since 2021-01-22 18:07
     */
    private ZjAccountCache() {
    }

    public static ZjAccountCache getInstance() {
        return INSTANCE;
    }

    public BusiZjNumberSection get(Integer key) {
        BusiZjNumberSection busiZjNumberSection = new BusiZjNumberSection();
        if (zjAccountMap.containsKey(key)) {
            busiZjNumberSection = zjAccountMap.get(key);
        }
        return busiZjNumberSection;
    }

    /**
     * 增加Zj账号到缓存
     *
     * @param busiZjNumberSection
     */
    public void add(BusiZjNumberSection busiZjNumberSection) {
        zjAccountMap.put(busiZjNumberSection.getId(), busiZjNumberSection);
        List<BusiZjNumberSection> busiZjNumberSectionList = new ArrayList<>();
        if (zjAccountByDeptMap.containsKey(busiZjNumberSection.getDeptId())) {
            busiZjNumberSectionList = zjAccountByDeptMap.get(busiZjNumberSection.getDeptId());
            busiZjNumberSectionList.add(busiZjNumberSection);
        } else {
            busiZjNumberSectionList.add(busiZjNumberSection);
        }
        zjAccountByDeptMap.put(busiZjNumberSection.getDeptId(), busiZjNumberSectionList);
    }

    /**
     * 更新Zj账号缓存
     *
     * @param busiZjNumberSection
     * @author LiuXiLong
     * @since
     */
    public void update(BusiZjNumberSection busiZjNumberSection) {
        if (zjAccountMap.containsKey(busiZjNumberSection.getId())) {
            zjAccountMap.remove(busiZjNumberSection.getId());
            zjAccountMap.put(busiZjNumberSection.getId(), busiZjNumberSection);
        }

        List<BusiZjNumberSection> busiZjNumberSectionList = new ArrayList<>();
        if (zjAccountByDeptMap.containsKey(busiZjNumberSection.getDeptId())) {
            busiZjNumberSectionList = zjAccountByDeptMap.get(busiZjNumberSection.getDeptId());
            if (busiZjNumberSectionList != null && busiZjNumberSectionList.size() > 0) {
                for (BusiZjNumberSection zjNumberSection : busiZjNumberSectionList) {
                    if (zjNumberSection.getId() == busiZjNumberSection.getId()) {
                        busiZjNumberSectionList.remove(zjNumberSection);
                        busiZjNumberSectionList.add(busiZjNumberSection);
                        break;
                    }
                }
            }
            zjAccountByDeptMap.put(busiZjNumberSection.getDeptId(), busiZjNumberSectionList);
        }
    }

    /**
     * 移除Zj终端账号缓存
     *
     * @param id
     * @return
     * @author LiuXiLong
     * @since
     */
    public void remove(Long id) {
        BusiZjNumberSection busiZjNumberSection = zjAccountMap.get(id);
        if (zjAccountByDeptMap.containsKey(busiZjNumberSection.getDeptId())) {
            List<BusiZjNumberSection> busiZjNumberSectionList = zjAccountByDeptMap.get(busiZjNumberSection.getDeptId());
            if (busiZjNumberSectionList != null && busiZjNumberSectionList.size() > 0) {
                for (BusiZjNumberSection zjNumberSection : busiZjNumberSectionList) {
                    if (zjNumberSection.getId() == id) {
                        busiZjNumberSectionList.remove(zjNumberSection);
                        break;
                    }
                }
            }

            zjAccountByDeptMap.put(busiZjNumberSection.getDeptId(), busiZjNumberSectionList);
        }
        zjAccountMap.remove(id);
    }

    //返回值类型转化String
    public String toString(List<BusiZjNumberSection> list) {
        if (!ObjectUtils.isEmpty(list)) {
            StringBuilder sb = new StringBuilder();
            for (BusiZjNumberSection busiZjNumberSection : list) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append('[').append(busiZjNumberSection.getStartValue()).append(", ").append(busiZjNumberSection.getEndValue()).append(']');
            }

            return sb.toString();
        }
        return null;

    }

    /**
     * 通过部门Id获取
     * @param deptId
     * @return
     */
    public List<BusiZjNumberSection> getByDeptId(Long deptId) {
        List<BusiZjNumberSection> busiZjNumberSectionList = new ArrayList<>();
        if (zjAccountByDeptMap.containsKey(deptId)) {
            busiZjNumberSectionList = zjAccountByDeptMap.get(deptId);
        }
        return busiZjNumberSectionList;
    }

    /**
     * 判断zj账号是否合法
     * @param deptId
     * @param credential
     * @return
     */
    public boolean isZjAccount(Long deptId, String credential) {
        boolean isZjAccount = false;
        List<BusiZjNumberSection> busiZjNumberSectionList = getZjAccountByDeptId(deptId);
        if (busiZjNumberSectionList != null && busiZjNumberSectionList.size() > 0) {
            for (BusiZjNumberSection busiZjNumberSection : busiZjNumberSectionList) {
                Long bigDecimal = Long.valueOf(credential);
                if (busiZjNumberSection.getStartValue() <= bigDecimal && bigDecimal <= busiZjNumberSection.getEndValue()){
                    isZjAccount = true;
                }
            }
        }
        return isZjAccount;
    }

    /**
     * 部门向上查找
     * @param deptId
     * @return
     */
    public List<BusiZjNumberSection> getZjAccountByDeptId(Long deptId) {
        if (deptId > 0) {
            List<BusiZjNumberSection> zjNumberSectionList = getByDeptId(deptId);
            if (zjNumberSectionList != null && zjNumberSectionList.size() > 0) {
                return zjNumberSectionList;
            } else {
                SysDept sysDept = SysDeptCache.getInstance().get(deptId);
                if (sysDept != null && sysDept.getParentId() != null) {
                    Long parentId = sysDept.getParentId();
                    if (parentId != null && parentId > 0) {
                        return getZjAccountByDeptId(parentId);
                    }
                }
                return null;
            }
        } else {
            return null;
        }
    }
}
