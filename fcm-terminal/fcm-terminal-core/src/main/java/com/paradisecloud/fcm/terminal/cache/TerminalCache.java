/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : TerminalCache.java
 * Package     : com.paradisecloud.fcm.terminal
 * @author lilinhai 
 * @since 2021-01-22 18:06
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.terminal.cache;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.common.enumer.FcmType;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.listener.IpTerminalEventListener;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.BusiTerminalMeetingJoinSettings;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridgeCluster;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**  
 * <pre>终端缓存</pre>
 * @author lilinhai
 * @since 2021-01-22 18:06
 * @version V1.0  
 */
public class TerminalCache extends JavaCache<Long, BusiTerminal>
{
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-01-28 13:36 
     */
    private static final long serialVersionUID = 1L;
    private static final TerminalCache INSTANCE = new TerminalCache();
    
    private Map<Long, Map<String, BusiTerminal>> fsbcTerminalsMap = new ConcurrentHashMap<>();
    private Map<Long, Map<String, BusiTerminal>> fcmTerminalsMap = new ConcurrentHashMap<>();
//    private Map<Long, Map<String, BusiTerminal>> zjTerminalsMap = new ConcurrentHashMap<>();
//    private Map<Long, Map<String, BusiTerminal>> smcTerminalsMap = new ConcurrentHashMap<>();
    private Map<Long, BusiTerminal> originalTerminalMap = new ConcurrentHashMap<>();
    private Map<Long, BusiTerminalMeetingJoinSettings> terminalMeetingJoinSettingsMap = new ConcurrentHashMap<>();
    private Map<Long, Map<String, BusiTerminal>> deptRemotePartyTerminalMap = new ConcurrentHashMap<>();
    private Map<Long, Map<Long, BusiTerminal>> deptTerminalMap = new ConcurrentHashMap<>();
    private Map<String, BusiTerminal> remotePartyTerminalMap = new ConcurrentHashMap<>();
    private Map<String, Map<Long, BusiTerminal>> appTypeTerminalMap = new ConcurrentHashMap<>();
    private Map<Long, BusiTerminal> onlyIpTerminalsMap = new ConcurrentHashMap<>();
    private boolean loadFinished = false;
    private boolean needUpdateMqStatus = false;
    private Map<String, BusiTerminal> snTerminalMap = new ConcurrentHashMap<>();
    private Set<String> fcmAccountSet = new HashSet<>();
    private Map<String, Long> fcmTerminalOnlineMap = new HashMap<>();
//    private Set<String> smcAccountSet = new HashSet<>();

    private IpTerminalEventListener ipTerminalEventListener = null;

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-01-22 18:07  
     */
    private TerminalCache()
    {
        
    }
    
    public synchronized BusiTerminal put(Long key, BusiTerminal busiTerminal)
    {
        if (TerminalType.isFSBC(busiTerminal.getType()))
        {
            Map<String, BusiTerminal> fsbcTerminalMap = fsbcTerminalsMap.get(busiTerminal.getFsbcServerId());
            if (fsbcTerminalMap == null)
            {
                fsbcTerminalMap = new ConcurrentHashMap<>();
                fsbcTerminalsMap.put(busiTerminal.getFsbcServerId(), fsbcTerminalMap);
            }
            fsbcTerminalMap.put(busiTerminal.getCredential(), busiTerminal);
        }
        else if (TerminalType.isFCMSIP(busiTerminal.getType()))
        {
            BusiFreeSwitchDept busiFreeSwitchDept = DeptFcmMappingCache.getInstance().get(busiTerminal.getDeptId());
            Map<String, BusiTerminal> fcmTerminalMap = fcmTerminalsMap.get(busiFreeSwitchDept.getDeptId());
            if (fcmTerminalMap == null)
            {
                fcmTerminalMap = new ConcurrentHashMap<>();
                fcmTerminalsMap.put(busiFreeSwitchDept.getDeptId(), fcmTerminalMap);
            }
            fcmTerminalMap.put(busiTerminal.getCredential(), busiTerminal);
            fcmAccountSet.add(busiTerminal.getCredential());
        }
//        else if (TerminalType.isZJ(busiTerminal.getType())) {
//            Map<String, BusiTerminal> zjTerminalMap = zjTerminalsMap.get(busiTerminal.getZjServerId());
//            if (zjTerminalMap == null) {
//                zjTerminalMap = new ConcurrentHashMap<>();
//                zjTerminalsMap.put(busiTerminal.getZjServerId(), zjTerminalMap);
//            }
//            zjTerminalMap.put(busiTerminal.getCredential(), busiTerminal);
//        }
//        else if (TerminalType.isSMCSIP(busiTerminal.getType())) {
//            Map<String, BusiTerminal> smcTerminalMap = smcTerminalsMap.get(busiTerminal.getDeptId());
//            if (smcTerminalMap == null)
//            {
//                smcTerminalMap = new ConcurrentHashMap<>();
//                smcTerminalsMap.put(busiTerminal.getDeptId(), smcTerminalMap);
//            }
//            if(busiTerminal.getNumber()!=null){
//                smcTerminalMap.put(busiTerminal.getNumber(), busiTerminal);
//                smcAccountSet.add(busiTerminal.getNumber());
//            }
//
//        }
        else
        {
            originalTerminalMap.put(key, busiTerminal);
        }

        if (TerminalType.isOnlyIP(busiTerminal.getType())) {
            onlyIpTerminalsMap.put(busiTerminal.getId(), busiTerminal);
        }
        
        String rp = getRemoteParty(busiTerminal);
        
        Map<String, BusiTerminal> remotePartyTerminalMap = deptRemotePartyTerminalMap.get(busiTerminal.getDeptId());
        if (remotePartyTerminalMap == null)
        {
            remotePartyTerminalMap = new ConcurrentHashMap<>();
            deptRemotePartyTerminalMap.put(busiTerminal.getDeptId(), remotePartyTerminalMap);
        }
        if(Strings.isNotBlank(rp)){
            remotePartyTerminalMap.put(rp, busiTerminal);
            this.remotePartyTerminalMap.put(rp, busiTerminal);
        }
        Map<Long, BusiTerminal> terminalMapInDept = deptTerminalMap.get(busiTerminal.getDeptId());
        if (terminalMapInDept == null) {
            terminalMapInDept = new ConcurrentHashMap<>();
            deptTerminalMap.put(busiTerminal.getDeptId(), terminalMapInDept);
        }
        terminalMapInDept.put(busiTerminal.getId(), busiTerminal);
        if (StringUtils.isNotEmpty(busiTerminal.getAppType())) {
            String appType = busiTerminal.getAppType();
            Map<Long, BusiTerminal> terminalMap = appTypeTerminalMap.get(appType);
            if (terminalMap == null) {
                terminalMap = new HashMap<>();
                appTypeTerminalMap.put(appType, terminalMap);
            }
            terminalMap.put(busiTerminal.getId(), busiTerminal);
        }
        if (loadFinished) {
            setNeedUpdateMqStatus(true);
        }
        if (StringUtils.isNotEmpty(busiTerminal.getSn())) {
            snTerminalMap.put(busiTerminal.getSn().toLowerCase(), busiTerminal);
        }
        return super.put(key, busiTerminal);
    }
    
    public BusiTerminalMeetingJoinSettings getById(Long id)
    {
        return terminalMeetingJoinSettingsMap.get(id);
    }
    
    public BusiTerminalMeetingJoinSettings update(BusiTerminalMeetingJoinSettings busiTerminalMeetingJoinSettings)
    {
        return terminalMeetingJoinSettingsMap.put(busiTerminalMeetingJoinSettings.getId(), busiTerminalMeetingJoinSettings);
    }
    
    public BusiTerminalMeetingJoinSettings removeTerminalMeetingJoinSettings(Long id)
    {
        return terminalMeetingJoinSettingsMap.remove(id);
    }
    
    public Map<Long, BusiTerminal> getByDept(Long deptId)
    {
        return deptTerminalMap.get(deptId);
    }

    public Map<String, BusiTerminal> getRemotePartyTerminalByDept(Long deptId)
    {
        return deptRemotePartyTerminalMap.get(deptId);
    }

    public String getRemoteParty(BusiTerminal busiTerminal)
    {
        String rp = null;
        if (TerminalType.isFSBC(busiTerminal.getType()))
        {
            String ip = FsbcBridgeCache.getInstance().getById(busiTerminal.getFsbcServerId()).getBusiFsbcRegistrationServer().getCallIp();
            rp = busiTerminal.getCredential() + "@" + ip;
        }
        else if (TerminalType.isFCMSIP(busiTerminal.getType()))
        {
            FcmBridge fcmBridge = null;
            BusiFreeSwitchDept fsd = DeptFcmMappingCache.getInstance().get(busiTerminal.getDeptId());
            if (FcmType.CLUSTER == FcmType.convert(fsd.getFcmType())) {
                if (busiTerminal.getFsServerId() != null) {
                    fcmBridge = FcmBridgeCache.getInstance().get(busiTerminal.getFsServerId());
                }
                if (fcmBridge == null) {
                    FcmBridgeCluster fcmBridgeCluster = FcmBridgeCache.getInstance().getByFcmClusterId(fsd.getServerId());
                    if (fcmBridgeCluster != null) {
                        List<FcmBridge> fcmBridges = fcmBridgeCluster.getFcmBridges();
                        // 由于使用固定用户信息数据库，任意一个FCM即可
                        fcmBridge = fcmBridges.get(0);
                    }
                }
            } else {
                fcmBridge = FcmBridgeCache.getInstance().getById(fsd.getServerId());
            }
            rp = busiTerminal.getCredential() + "@" + fcmBridge.getBusiFreeSwitch().getIp();
        }
        else if (TerminalType.isZJ(busiTerminal.getType())) {
            rp = busiTerminal.getCredential() + "@" + busiTerminal.getIp();
        }
        else if (TerminalType.isSMCSIP(busiTerminal.getType())) {
            Map<String, Object> businessProperties = busiTerminal.getBusinessProperties();
            if(businessProperties!=null){
                String scRegisterAddress =(String)businessProperties.get("scRegisterAddress");
                if(Strings.isNotBlank(scRegisterAddress)){
                    rp = busiTerminal.getNumber() + "@" +scRegisterAddress ;
                }
            }

        }
        else if (TerminalType.isRtsp(busiTerminal.getType())) {
        }
        else
        {
            if (ObjectUtils.isEmpty(busiTerminal.getNumber()))
            {
                rp = busiTerminal.getIp();
            }
            else
            {
                rp = busiTerminal.getNumber() + "@" + busiTerminal.getIp();
            }
        }
        return rp;
    }
    
    public BusiTerminal getByRemoteParty(long deptId, String rp)
    {
        Map<String, BusiTerminal> remotePartyTerminalMap = deptRemotePartyTerminalMap.get(deptId);
        return remotePartyTerminalMap == null ? null : remotePartyTerminalMap.get(rp);
    }

    public BusiTerminal getByRemoteParty(String remoteParty)
    {
        return remotePartyTerminalMap.get(remoteParty);
    }

    public void updateRemotePartyTerminalMapForFcmChange(String oldRemoteParty, BusiTerminal busiTerminal) {
        String newRemoteParty = getRemoteParty(busiTerminal);
        if (!newRemoteParty.equals(oldRemoteParty)) {
            remotePartyTerminalMap.remove(oldRemoteParty);
            remotePartyTerminalMap.put(newRemoteParty, busiTerminal);
            Map<String, BusiTerminal> remotePartyTerminalMap = deptRemotePartyTerminalMap.get(busiTerminal.getDeptId());
            if (remotePartyTerminalMap != null) {
                remotePartyTerminalMap.remove(oldRemoteParty);
                remotePartyTerminalMap.put(newRemoteParty, busiTerminal);
            }
        }
    }
    
    public List<BusiTerminal> getCopiedOriginalTerminals()
    {
        return Collections.unmodifiableList(new ArrayList<>(originalTerminalMap.values()));
    }

    @Override
    public synchronized BusiTerminal remove(Object key)
    {
        BusiTerminal busiTerminal = super.remove(key);
        if (busiTerminal != null)
        {
            deleteFcmOrFsbcTerminal(busiTerminal);
            Map<String, BusiTerminal> remotePartyTerminalMap = deptRemotePartyTerminalMap.get(busiTerminal.getDeptId());
            String remoteParty = getRemoteParty(busiTerminal);
            if(remoteParty!=null){
                if (remotePartyTerminalMap != null)
                {
                    remotePartyTerminalMap.remove(remoteParty);
                }
                this.remotePartyTerminalMap.remove(getRemoteParty(busiTerminal));
            }
            if (StringUtils.isNotEmpty(busiTerminal.getAppType())) {
                Map<Long, BusiTerminal> terminalMap = appTypeTerminalMap.get(busiTerminal.getAppType());
                if (terminalMap != null && terminalMap.containsKey(busiTerminal.getAppType())) {
                    terminalMap.remove(busiTerminal.getId());
                }
            }
            if (StringUtils.isNotEmpty(busiTerminal.getSn())) {
                snTerminalMap.remove(busiTerminal.getSn().toLowerCase());
            }
        }
        originalTerminalMap.remove(key);
        onlyIpTerminalsMap.remove(key);
        return busiTerminal;
    }
    
    public void deleteFcmOrFsbcTerminal(BusiTerminal busiTerminal)
    {
        if (TerminalType.isFSBC(busiTerminal.getType()))
        {
            Map<String, BusiTerminal> fsbcTerminalMap = fsbcTerminalsMap.get(busiTerminal.getFsbcServerId());
            if (fsbcTerminalMap != null)
            {
                fsbcTerminalMap.remove(busiTerminal.getCredential());
            }
        }
        else if (TerminalType.isFCMSIP(busiTerminal.getType()))
        {
            Map<String, BusiTerminal> fcmTerminalMap = fcmTerminalsMap.get(busiTerminal.getDeptId());
            if (fcmTerminalMap != null)
            {
                fcmTerminalMap.remove(busiTerminal.getCredential());
            }
            fcmAccountSet.remove(busiTerminal.getCredential());
        }
//        else if (TerminalType.isZJ(busiTerminal.getType())) {
//            Map<String, BusiTerminal> zjTerminalMap = zjTerminalsMap.get(busiTerminal.getZjServerId());
//            if (zjTerminalMap != null)
//            {
//                zjTerminalMap.remove(busiTerminal.getCredential());
//            }
//        }
//        else if (TerminalType.isSMCSIP(busiTerminal.getType()))
//        {
//            Map<String, BusiTerminal> smcTerminalMap = smcTerminalsMap.get(busiTerminal.getDeptId());
//            if (smcTerminalMap != null)
//            {
//                smcTerminalMap.remove(busiTerminal.getNumber());
//            }
//            smcAccountSet.remove(busiTerminal.getNumber());
//        }
    }
    
    public Map<Long, Map<String, BusiTerminal>> getFsbcTerminalsMap()
    {
        return new HashMap<>(fsbcTerminalsMap);
    }
    
    /**
     * <p>Get Method   :   fcmTerminalsMap Map<Long,Map<String,BusiTerminal>></p>
     * @return fcmTerminalsMap
     */
    public Map<Long, Map<String, BusiTerminal>> getFcmTerminalsMap()
    {
        return new HashMap<>(fcmTerminalsMap);
    }

    public static TerminalCache getInstance()
    {
        return INSTANCE;
    }

    public boolean isLoadFinished() {
        return loadFinished;
    }

    public void setLoadFinished() {
        this.loadFinished = true;
    }

    public boolean isNeedUpdateMqStatus() {
        if (loadFinished) {
            return needUpdateMqStatus;
        }
        return false;
    }

    public void setNeedUpdateMqStatus(boolean needUpdateMqStatus) {
        this.needUpdateMqStatus = needUpdateMqStatus;
    }

    public Map<Long, BusiTerminal> getAppTypeTerminalMap(String appType) {
        return appTypeTerminalMap.get(appType);
    }

    public void updateAppTypeTerminalMap(String oldAppType, BusiTerminal busiTerminal) {
        if (StringUtils.isNotEmpty(busiTerminal.getAppType()) && busiTerminal.getAppType().equals(oldAppType)) {
            return;
        }
        if (StringUtils.isNotEmpty(oldAppType)) {
            Map<Long, BusiTerminal> terminalMapOld = appTypeTerminalMap.get(oldAppType);
            if (terminalMapOld != null && terminalMapOld.containsKey(oldAppType)) {
                terminalMapOld.remove(oldAppType);
            }
        }
        if (StringUtils.isNotEmpty(busiTerminal.getAppType())) {
            Map<Long, BusiTerminal> terminalMapNew = appTypeTerminalMap.get(busiTerminal.getAppType());
            if (terminalMapNew == null) {
                terminalMapNew = new HashMap<>();
            }
            terminalMapNew.put(busiTerminal.getId(), busiTerminal);
        }
    }

    public BusiTerminal getBySn(String sn)
    {
        if (StringUtils.isNotEmpty(sn)) {
            return snTerminalMap.get(sn.toLowerCase());
        }
        return null;
    }

    public boolean hasFcmAccount(String account) {
        return fcmAccountSet.contains(account);
    }

    public Map<Long, BusiTerminal> getOnlyIpTerminalsMap() {
        return onlyIpTerminalsMap;
    }

    public IpTerminalEventListener getIpTerminalEventListener() {
        return ipTerminalEventListener;
    }

    public void setIpTerminalEventListener(IpTerminalEventListener ipTerminalEventListener) {
        this.ipTerminalEventListener = ipTerminalEventListener;
    }

    public void setFcmTerminalOnline(String credential) {
        fcmTerminalOnlineMap.put(credential, System.currentTimeMillis());
    }

    public Long getFcmTerminalOnlineTime(String credential) {
        return fcmTerminalOnlineMap.get(credential);
    }
}
