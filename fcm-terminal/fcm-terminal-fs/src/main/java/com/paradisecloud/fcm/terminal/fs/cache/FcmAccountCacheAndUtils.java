package com.paradisecloud.fcm.terminal.fs.cache;

import com.paradisecloud.fcm.dao.model.BusiFcmNumberSection;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;

import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**  
 * <pre>Fcm桥缓存</pre>
 * @author LiuXiLong
 * @since
 * @version V1.0  
 */
public abstract class FcmAccountCacheAndUtils
{

    private static final FcmAccountCacheAndUtils INSTANCE = new FcmAccountCacheAndUtils()
    {

    };

    
  //返回值类型转化String
    public String toString(List<BusiFcmNumberSection> list){
        if (!ObjectUtils.isEmpty(list))
        {
            StringBuilder sb = new StringBuilder();
            for (BusiFcmNumberSection busiConferenceNumberSection : list)
            {
                if (sb.length() > 0)
                {
                    sb.append(", ");
                }
                sb.append('[').append(busiConferenceNumberSection.getStartValue()).append(", ").append(busiConferenceNumberSection.getEndValue()).append(']');
            }

            return sb.toString();
        }
        return null;

    }

    //部门Id向上查找
    public Long deptId(Long deptId){

        SysDept sysDept = SysDeptCache.getInstance().get(deptId);
        if (sysDept.getParentId() != null && sysDept.getParentId().longValue() > 0)
        {
            deptId = sysDept.getParentId();
            return deptId;
        }
        else
        {
            return (long) 0;
        }
    }

    public List<BusiFcmNumberSection> selectBindingBusiFcmNumberSectionByDeptId(Long deptId) {
        List<BusiFcmNumberSection> busiFcmNumberSectionList = getByDeptId(deptId);
        if (busiFcmNumberSectionList == null || busiFcmNumberSectionList.size() <= 0) {
            SysDept sysDept = SysDeptCache.getInstance().get(deptId);
            if (sysDept.getParentId() != null && sysDept.getParentId().longValue() > 0)
            {
                selectBindingBusiFcmNumberSectionByDeptId(sysDept.getParentId());
            }
        } else {
            return busiFcmNumberSectionList;
        }
        return null;
    }
    
    
    /**
     * 所有Fcm账号缓存，key：部门Id
     */
    private Map<Long, List<BusiFcmNumberSection>> fcmAccountMap = new ConcurrentHashMap<>();

    /**
     * 根据部门Id在缓存中获取号段详细
     * @param busiFcmNumberSection
     * @return
     */
    public List<BusiFcmNumberSection> getByDeptId(Long deptId) {
        return fcmAccountMap.get(deptId);
    }


    /**
     * 增加Fcm账号到缓存
     * @param busiFcmNumberSection
     */
    public void add(BusiFcmNumberSection busiFcmNumberSection){
        boolean containsKey = fcmAccountMap.containsKey(busiFcmNumberSection.getDeptId());
        if (containsKey) {
            List<BusiFcmNumberSection> busiFcmNumberSectionListByDeptId = fcmAccountMap.get(busiFcmNumberSection.getDeptId());
            busiFcmNumberSectionListByDeptId.add(busiFcmNumberSection);
        } else {
            List<BusiFcmNumberSection> busiFcmNumberSectionList = new ArrayList<>();
            busiFcmNumberSectionList.add(busiFcmNumberSection);
            fcmAccountMap.put(busiFcmNumberSection.getDeptId(), busiFcmNumberSectionList);
        }
    }

    /**
     * 更新Fcm账号缓存
     * @author LiuXiLong
     * @since
     * @param busiFcmNumberSection void
     */
    public void update(BusiFcmNumberSection busiFcmNumberSection)
    {
        Long deptId = busiFcmNumberSection.getDeptId();
        boolean containsKey = fcmAccountMap.containsKey(deptId);
        if (containsKey) {
            List<BusiFcmNumberSection> busiFcmNumberSectionList = fcmAccountMap.get(deptId);
            if (busiFcmNumberSectionList != null && busiFcmNumberSectionList.size() > 0) {
                for (BusiFcmNumberSection fcmNumberSection : busiFcmNumberSectionList) {
                    if (fcmNumberSection.getId() == busiFcmNumberSection.getId()) {
                        busiFcmNumberSectionList.remove(fcmNumberSection);
                        break;
                    }
                }
                busiFcmNumberSectionList.add(busiFcmNumberSection);
            }
        } else {
            List<BusiFcmNumberSection> busiFcmNumberSectionList = new ArrayList<>();
            busiFcmNumberSectionList.add(busiFcmNumberSection);
            fcmAccountMap.put(busiFcmNumberSection.getDeptId(), busiFcmNumberSectionList);
        }
    }
    
    /**
     * 移除终端账号缓存123
     * @author LiuXiLong
     * @since
     * @param id
     * @return
     */
    public void remove(BusiFcmNumberSection busiFcmNumberSection)
    {
        Long deptId = busiFcmNumberSection.getDeptId();
        boolean containsKey = fcmAccountMap.containsKey(deptId);
        if (containsKey) {
            List<BusiFcmNumberSection> busiFcmNumberSectionList = fcmAccountMap.get(deptId);
            if (busiFcmNumberSectionList != null && busiFcmNumberSectionList.size() > 0) {
                Iterator<BusiFcmNumberSection> iterator = busiFcmNumberSectionList.iterator();
                while (iterator.hasNext()) {
                    BusiFcmNumberSection fcmNumberSection = iterator.next();
                    if (fcmNumberSection.getId() == busiFcmNumberSection.getId()) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    public static FcmAccountCacheAndUtils getInstance()
    {
        return INSTANCE;
    }
}
