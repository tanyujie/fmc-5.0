package com.paradisecloud.fcm.smc.cache.modle;

import com.paradisecloud.com.fcm.smc.modle.SmcConferenceContextBase;
import com.paradisecloud.smc.dao.model.BusiSmcDept;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2022/8/15 16:53
 */
public class SmcBridgeCache {

    private static final SmcBridgeCache INSTANCE = new SmcBridgeCache() {
    };


    private Map<String, SmcBridge> ipToSmcBridgeMap = new ConcurrentHashMap<>();

    private Map<Long, SmcBridge> smcBridgeMap = new ConcurrentHashMap<>();
    private Map<Long, String> deptIdOrgMap = new ConcurrentHashMap<>();
    private Map<String, SmcBridge> conferenceBridge = new ConcurrentHashMap<>();

    private Map<String, SmcBridge> appointmentConferenceBridge = new ConcurrentHashMap<>();

    private Map<Long, SmcBridge> deptIdSmcBridgeMap = new ConcurrentHashMap<>();

    private List<SmcBridge> initSmcBridgeList = new ArrayList();
    private Map<String, SmcConferenceContextBase> conferenceMap = new ConcurrentHashMap<>();

    public Map<String, SmcBridge> getIpToSmcBridgeMap() {
        return ipToSmcBridgeMap;
    }

    public Map<Long, SmcBridge> getDeptIdSmcBridgeMap() {
        return deptIdSmcBridgeMap;
    }

    public List<SmcBridge> getInitSmcBridgeList() {
        return initSmcBridgeList;
    }

    public Map<Long, String> getDeptIdOrgMap() {
        return deptIdOrgMap;
    }

    public void setDeptIdOrgMap(Map<Long, String> deptIdOrgMap) {
        this.deptIdOrgMap = deptIdOrgMap;
    }

    public void setIpToSmcBridgeMap(Map<String, SmcBridge> ipToSmcBridgeMap) {
        this.ipToSmcBridgeMap = ipToSmcBridgeMap;
    }

    public void setSmcBridgeMap(Map<Long, SmcBridge> smcBridgeMap) {
        this.smcBridgeMap = smcBridgeMap;
    }

    public Map<String, SmcBridge> getConferenceBridge() {
        return conferenceBridge;
    }

    public void setConferenceBridge(Map<String, SmcBridge> conferenceBridge) {
        this.conferenceBridge = conferenceBridge;
    }

    public void setDeptIdSmcBridgeMap(Map<Long, SmcBridge> deptIdSmcBridgeMap) {
        this.deptIdSmcBridgeMap = deptIdSmcBridgeMap;
    }

    public void setInitSmcBridgeList(List<SmcBridge> initSmcBridgeList) {
        this.initSmcBridgeList = initSmcBridgeList;
    }

    public synchronized void update(SmcBridge smcBridge) {
        // 添加ID映射
        smcBridgeMap.put(smcBridge.getBusiSMC().getId(), smcBridge);
        // 添加IP映射
        ipToSmcBridgeMap.put(smcBridge.getBusiSMC().getIp(), smcBridge);
        initSmcBridgeList.add(smcBridge);
    }

    public synchronized void updateDeptIdOrgMap(Long deptId, String orgId) {
        deptIdOrgMap.put(deptId, orgId);
    }

    public synchronized void removeDeptIdOrgMap(Long deptId, String orgId) {
        deptIdOrgMap.remove(deptId, orgId);
    }

    public synchronized void updateConferenceBridge(String id, SmcBridge smcBridge) {
        conferenceBridge.put(id, smcBridge);
    }

    public synchronized void removeConferenceBridge(String id, SmcBridge smcBridge) {
        conferenceBridge.remove(id, smcBridge);
    }

    public synchronized void init(SmcBridge smcBridge) {

        // 添加ID映射
        smcBridgeMap.put(smcBridge.getBusiSMC().getId(), smcBridge);
        // 添加IP映射
        ipToSmcBridgeMap.put(smcBridge.getBusiSMC().getIp(), smcBridge);

        initSmcBridgeList.add(smcBridge);
    }

    public synchronized void update(BusiSmcDept busiSmcDept) {

        deptIdSmcBridgeMap.put(busiSmcDept.getDeptId(), smcBridgeMap.get(busiSmcDept.getSmcId()));

    }

    public static SmcBridgeCache getInstance() {
        return INSTANCE;
    }


    public SmcBridge getSmcBridgeByDeptId(Long deptId) {
        if (deptId == null) {
            if (!CollectionUtils.isEmpty(initSmcBridgeList)) {
                SmcBridge smcBridge = initSmcBridgeList.get(0);
                return smcBridge;
            }
            if (!CollectionUtils.isEmpty(deptIdSmcBridgeMap)) {
                for (SmcBridge value : deptIdSmcBridgeMap.values()) {
                    return value;
                }
            }
            if (!CollectionUtils.isEmpty(ipToSmcBridgeMap)) {
                for (SmcBridge value : ipToSmcBridgeMap.values()) {
                    return value;
                }
            }
        }

        if (CollectionUtils.isEmpty(deptIdSmcBridgeMap) && !CollectionUtils.isEmpty(initSmcBridgeList)) {
            SmcBridge smcBridge = initSmcBridgeList.get(0);
            return smcBridge;
        }
        return getBindSmcBridge(deptId);
    }


    public SmcBridge getBindSmcBridge(Long deptId)
    {

        SmcBridge smcBridge =null;
        try {
            smcBridge= deptIdSmcBridgeMap.get(deptId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (smcBridge == null)
        {
            SysDept sysDept = SysDeptCache.getInstance().get(deptId);
            if (sysDept.getParentId() != null && sysDept.getParentId().longValue() > 0)
            {
                return getBindSmcBridge(sysDept.getParentId());
            }
            else
            {
                return null;
            }
        }
        else
        {
            return smcBridge;
        }
    }


    public Map<Long, SmcBridge> getSmcBridgeMap() {
        return smcBridgeMap;
    }

    public synchronized void removeDeptMap(Long deptId) {
        deptIdSmcBridgeMap.remove(deptId);
    }

    public synchronized void removeSmc(SmcBridge smcBridge) {
        // 添加ID映射
        if(smcBridge==null){
            return;
        }
        smcBridgeMap.remove(smcBridge.getBusiSMC().getId(), smcBridge);

        // 添加IP映射
        ipToSmcBridgeMap.remove(smcBridge.getBusiSMC().getIp(), smcBridge);

        if (initSmcBridgeList.contains(smcBridge)) {
            initSmcBridgeList.remove(smcBridge);
        }

    }

    public synchronized void removeSmcById(Long id) {
        SmcBridge smcBridge = smcBridgeMap.get(id);
        if(smcBridge!=null){
            removeSmc(smcBridge);
        }

    }

    public synchronized void putAppointmentConferenceBridge(String conferenceId, SmcBridge bridge) {
        appointmentConferenceBridge.put(conferenceId, bridge);
    }

    public synchronized void removeAppointConferenceBridge(String conferenceId, SmcBridge bridge) {
        appointmentConferenceBridge.remove(conferenceId, bridge);
    }

    public Map<String, SmcBridge> getAppointmentConferenceBridge() {
        return appointmentConferenceBridge;
    }

    public void setAppointmentConferenceBridge(Map<String, SmcBridge> appointmentConferenceBridge) {
        this.appointmentConferenceBridge = appointmentConferenceBridge;
    }

    public Map<String, SmcConferenceContextBase> getConferenceMap() {
        return conferenceMap;
    }

    public void setConferenceMap(Map<String, SmcConferenceContextBase> conferenceMap) {
        this.conferenceMap = conferenceMap;
    }
}
