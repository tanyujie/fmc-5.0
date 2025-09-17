package com.paradisecloud.fcm.web.controller.mcu.all;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.EncryptIdVo;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.McuTypeVo;
import com.paradisecloud.fcm.ding.cache.*;
import com.paradisecloud.fcm.ding.service2.interfaces.IMcuDingCacheService;
import com.paradisecloud.fcm.fme.cache.DeptFmeMappingCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.paradisecloud.fcm.fme.websocket.interfaces.IFmeCacheService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.*;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IMcuHwcloudCacheService;
import com.paradisecloud.fcm.mcu.kdc.cache.DeptMcuKdcMappingCache;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcBridgeCache;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcConferenceContextCache;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcBridge;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IMcuKdcCacheService;
import com.paradisecloud.fcm.mcu.plc.cache.DeptMcuPlcMappingCache;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcBridgeCache;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcConferenceContextCache;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcBridge;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IMcuPlcCacheService;
import com.paradisecloud.fcm.mcu.zj.cache.DeptMcuZjMappingCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjConferenceContextCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IMcuZjCacheService;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.smc2.cache.*;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IMcuSmc2CacheService;
import com.paradisecloud.fcm.tencent.cache.*;
import com.paradisecloud.fcm.tencent.service2.interfaces.IMcuTencentCacheService;
import com.paradisecloud.fcm.web.service.interfaces.IBusiAllMcuService;
import com.paradisecloud.fcm.zte.cache.DeptMcuZteMappingCache;
import com.paradisecloud.fcm.zte.cache.McuZteBridgeCache;
import com.paradisecloud.fcm.zte.cache.McuZteConferenceContextCache;
import com.paradisecloud.fcm.zte.cache.model.McuZteBridge;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.service.interfaces.IMcuZteCacheService;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.cache.DeptSmc3MappingCache;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.smc3.service.interfaces.IMcuSmc3CacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * MCU终端信息Controller
 *
 * @author lilinhai
 * @date 2021-01-20
 */
@RestController
@RequestMapping("/busi/mcu/all/mcu")
@Tag(name = "MCU基础配置控制层")
public class BusiAllMcuController extends BaseController {

    @Resource
    private IFmeCacheService fmeCacheService;
    @Resource
    private IMcuZjCacheService mcuZjCacheService;
    @Resource
    private IMcuPlcCacheService mcuPlcCacheService;
    @Resource
    private IMcuKdcCacheService mcuKdcCacheService;
    @Resource
    private IMcuSmc3CacheService mcuSmc3CacheService;
    @Resource
    private IMcuSmc2CacheService mcuSmc2CacheService;
    @Resource
    private IMcuTencentCacheService tencentCacheService;
    @Resource
    private IMcuDingCacheService dingCacheService;
    @Resource
    private IMcuHwcloudCacheService hwcloudCacheService;
    @Resource
    private IBusiAllMcuService busiAllMcuService;
    @Resource
    private IMcuZteCacheService mcuZteCacheService;

    /**
     * @return
     */
    @Operation(summary = "获取MCU类型列表")
    @GetMapping("/mcuTypeList")
    public RestResponse mcuTypeList() {
        return success(McuType.getMcuTypeList());
    }

    /**
     * 获取部门可使用MCU类型列表
     */
    @GetMapping(value = "/mcuTypeListDept")
    @Operation(summary = "获取部门可使用MCU列表")
    public RestResponse mcuTypeList(Long deptId) {
        Assert.isTrue(deptId != null, "部门ID不能为空！");
        List<McuTypeVo> mcuTypeList = busiAllMcuService.getMcuTypeList(deptId);
        return RestResponse.success(mcuTypeList);
    }

    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键查找单个记录</pre>
     *
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "获取MCU桥列表（不分页）")
    @GetMapping("/list")
    public RestResponse list() {
        List<ModelBean> busiMcus = new ArrayList<>();
        //fme
        {
            List<FmeBridge> fbs = FmeBridgeCache.getInstance().getFmeBridges();
            for (FmeBridge fmeBridge : fbs) {
                ModelBean mb = new ModelBean(fmeBridge.getBusiFme());
                McuType mcuType = McuType.FME;
                String mcuIdStr = EncryptIdUtil.generateEncryptId(fmeBridge.getBusiFme().getId(), mcuType.getCode());
                mb.put("mcuId", mcuIdStr);
                mb.put("mcuType", mcuType.getCode());
                mb.put("mcuTypeAlias", mcuType.getAlias());
                mb.remove("password");
                mb.remove("adminPassword");
                mb.put("bindDeptCount", DeptFmeMappingCache.getInstance().getBindDeptCount(FmeType.SINGLE_NODE, fmeBridge.getBusiFme().getId()));

                // websocket断开次数
                mb.put("webSocketBreakCount", fmeBridge.getWebSocketBreakCount());

                // 连接创建成功的数量，理论只能存在一个
                mb.put("websocketConnections", fmeBridge.getWsAuthTokens());

                // 参会者数
                mb.put("participantCount", fmeBridge.getDataCache().getParticipantCount());

                Collection<BaseConferenceContext> cc = AllConferenceContextCache.getInstance().values();
                if (cc.isEmpty()) {
                    // 活跃会议数
                    mb.put("callCount", 0);
                } else {
                    int count = 0;

                    List<Call> calls = fmeBridge.getDataCache().getCalls();
                    List<String> coSpaces = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(calls)) {
                        for (Call call : calls) {
                            coSpaces.add(call.getCoSpace());
                        }
                    }
                    for (BaseConferenceContext baseConferenceContext : cc) {

                        if (baseConferenceContext instanceof ConferenceContext) {
                            ConferenceContext conferenceContext = (ConferenceContext) baseConferenceContext;
                            String coSpaceId = conferenceContext.getCoSpaceId();
                            if (coSpaces.contains(coSpaceId)) {
                                count++;
                            }
                        }
                    }

                    mb.put("callCount", count);
                }


                // 会议号数
                mb.put("coSpaceCount", fmeBridge.getDataCache().getCoSpaceCount());

                // 自上次断开后的重试次数
                mb.put("websocketConnectionTryTimesSinceLastDisconnected", fmeBridge.getWebsocketConnectionTryTimesSinceLastDisconnected());

                // 首次建立连接时间
                mb.put("firstConnectedTime", fmeBridge.getFirstConnectedTime());

                // 最后一次建立连接时间
                mb.put("lastConnectedTime", fmeBridge.getLastConnectedTime());

                // 最后一次连接断开时间
                mb.put("lastDisConnectedTime", fmeBridge.getLastDisConnectedTime());
                mb.put("fmeStatus", fmeBridge.getDataCache().getSystemStatus());

                // 连接失败原因
                mb.put("connectionFailedReason", fmeBridge.getConnectionFailedReason());
                busiMcus.add(mb);
            }
        }
        //zj
        {
            Map<Long, Long> callCountMap = new HashMap<>();
            Map<Long, Long> coSpaceCountMap = new HashMap<>();
            Map<Long, Long> participantCountMap = new HashMap<>();
            for (McuZjConferenceContext conferenceContext : McuZjConferenceContextCache.getInstance().values()) {
                Long mcuId = conferenceContext.getMcuZjBridge().getBusiMcuZj().getId();
                Long callCount = callCountMap.get(mcuId);
                if (callCount == null) {
                    callCount = 0L;
                }
                callCount++;
                callCountMap.put(mcuId, callCount);
                Long coSpaceCount = coSpaceCountMap.get(mcuId);
                if (coSpaceCount == null) {
                    coSpaceCount = 0L;
                }
                coSpaceCount++;
                coSpaceCountMap.put(mcuId, coSpaceCount);
                Long participantCount = participantCountMap.get(mcuId);
                if (participantCount == null) {
                    participantCount = 0L;
                }
                participantCount += conferenceContext.getMasterAttendee() != null ? 1 : 0;
                participantCount += conferenceContext.getAttendees().size();
                participantCount += conferenceContext.getMasterAttendees().size();
                for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                    participantCount += conferenceContext.getCascadeAttendeesMap().get(deptId).size();
                }
                participantCountMap.put(mcuId, participantCount);
            }
            List<McuZjBridge> mcuZjBridgeList = McuZjBridgeCache.getInstance().getMcuZjBridges();
            for (McuZjBridge mcuZjBridge : mcuZjBridgeList) {
                ModelBean mb = new ModelBean(mcuZjBridge.getBusiMcuZj());
                McuType mcuType = McuType.MCU_ZJ;
                String mcuIdStr = EncryptIdUtil.generateEncryptId(mcuZjBridge.getBusiMcuZj().getId(), mcuType.getCode());
                mb.put("mcuId", mcuIdStr);
                mb.put("mcuType", mcuType.getCode());
                mb.put("mcuTypeAlias", mcuType.getAlias());
                mb.remove("password");
                mb.remove("adminPassword");
                mb.remove("devToken");
                mb.remove("capacity");
                mb.put("bindDeptCount", DeptMcuZjMappingCache.getInstance().getBindDeptCount(McuZjType.SINGLE_NODE, mcuZjBridge.getBusiMcuZj().getId()));

                Long mcuId = mcuZjBridge.getBusiMcuZj().getId();
                // 参会者数
                Long participantCount = participantCountMap.get(mcuId);
                if (participantCount == null) {
                    participantCount = 0L;
                }
                mb.put("participantCount", participantCount);

                // 活跃会议数
                Long callCount = callCountMap.get(mcuId);
                if (callCount == null) {
                    callCount = 0L;
                }
                mb.put("callCount", callCount);

                // 会议号数
                Long coSpaceCount = coSpaceCountMap.get(mcuId);
                if (coSpaceCount == null) {
                    coSpaceCount = 0L;
                }
                mb.put("coSpaceCount", coSpaceCount);

//            mb.put("mcuStatus", mcuZjBridge.isAvailable() ? 1 : 0);

                mb.put("systemResourceCount", mcuZjBridge.getSystemResourceCount());
                mb.put("usedResourceCount", mcuZjBridge.getUsedResourceCount());
                int sourceTemplateCount = 0;
                if (mcuZjBridge.getSourceTemplateList() != null) {
                    sourceTemplateCount = mcuZjBridge.getSourceTemplateList().size();
                }
                mb.put("sourceTemplateCount", sourceTemplateCount);

                // 连接失败原因
                mb.put("connectionFailedReason", mcuZjBridge.getConnectionFailedReason());
                busiMcus.add(mb);
            }
        }
        //plc
        {
            Map<Long, Long> callCountMap = new HashMap<>();
            Map<Long, Long> coSpaceCountMap = new HashMap<>();
            Map<Long, Long> participantCountMap = new HashMap<>();
            for (McuPlcConferenceContext conferenceContext : McuPlcConferenceContextCache.getInstance().values()) {
                Long mcuId = conferenceContext.getMcuPlcBridge().getBusiMcuPlc().getId();
                Long callCount = callCountMap.get(mcuId);
                if (callCount == null) {
                    callCount = 0L;
                }
                callCount++;
                callCountMap.put(mcuId, callCount);
                Long coSpaceCount = coSpaceCountMap.get(mcuId);
                if (coSpaceCount == null) {
                    coSpaceCount = 0L;
                }
                coSpaceCount++;
                coSpaceCountMap.put(mcuId, coSpaceCount);
                Long participantCount = participantCountMap.get(mcuId);
                if (participantCount == null) {
                    participantCount = 0L;
                }
                participantCount += conferenceContext.getMasterAttendee() != null ? 1 : 0;
                participantCount += conferenceContext.getAttendees().size();
                participantCount += conferenceContext.getMasterAttendees().size();
                for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                    participantCount += conferenceContext.getCascadeAttendeesMap().get(deptId).size();
                }
                participantCountMap.put(mcuId, participantCount);
            }
            List<McuPlcBridge> mcuPlcBridgeList = McuPlcBridgeCache.getInstance().getMcuPlcBridges();
            for (McuPlcBridge mcuPlcBridge : mcuPlcBridgeList) {
                ModelBean mb = new ModelBean(mcuPlcBridge.getBusiMcuPlc());
                McuType mcuType = McuType.MCU_PLC;
                String mcuIdStr = EncryptIdUtil.generateEncryptId(mcuPlcBridge.getBusiMcuPlc().getId(), mcuType.getCode());
                mb.put("mcuId", mcuIdStr);
                mb.put("mcuType", mcuType.getCode());
                mb.put("mcuTypeAlias", mcuType.getAlias());
                mb.remove("password");
                mb.remove("adminPassword");
                mb.remove("capacity");
                mb.put("bindDeptCount", DeptMcuPlcMappingCache.getInstance().getBindDeptCount(McuPlcType.SINGLE_NODE, mcuPlcBridge.getBusiMcuPlc().getId()));

                Long mcuId = mcuPlcBridge.getBusiMcuPlc().getId();
                // 参会者数
                Long participantCount = participantCountMap.get(mcuId);
                if (participantCount == null) {
                    participantCount = 0L;
                }
                mb.put("participantCount", participantCount);

                // 活跃会议数
                Long callCount = callCountMap.get(mcuId);
                if (callCount == null) {
                    callCount = 0L;
                }
                mb.put("callCount", callCount);

                // 会议号数
                Long coSpaceCount = coSpaceCountMap.get(mcuId);
                if (coSpaceCount == null) {
                    coSpaceCount = 0L;
                }
                mb.put("coSpaceCount", coSpaceCount);

                mb.put("systemResourceCount", mcuPlcBridge.getSystemResourceCount());
                mb.put("usedResourceCount", mcuPlcBridge.getUsedResourceCount());

                // 连接失败原因
                mb.put("connectionFailedReason", mcuPlcBridge.getConnectionFailedReason());
                busiMcus.add(mb);
            }
        }
        //kdc
        {
            Map<Long, Long> callCountMap = new HashMap<>();
            Map<Long, Long> coSpaceCountMap = new HashMap<>();
            Map<Long, Long> participantCountMap = new HashMap<>();
            for (McuKdcConferenceContext conferenceContext : McuKdcConferenceContextCache.getInstance().values()) {
                Long mcuId = conferenceContext.getMcuKdcBridge().getBusiMcuKdc().getId();
                Long callCount = callCountMap.get(mcuId);
                if (callCount == null) {
                    callCount = 0L;
                }
                callCount++;
                callCountMap.put(mcuId, callCount);
                Long coSpaceCount = coSpaceCountMap.get(mcuId);
                if (coSpaceCount == null) {
                    coSpaceCount = 0L;
                }
                coSpaceCount++;
                coSpaceCountMap.put(mcuId, coSpaceCount);
                Long participantCount = participantCountMap.get(mcuId);
                if (participantCount == null) {
                    participantCount = 0L;
                }
                participantCount += conferenceContext.getMasterAttendee() != null ? 1 : 0;
                participantCount += conferenceContext.getAttendees().size();
                participantCount += conferenceContext.getMasterAttendees().size();
                for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                    participantCount += conferenceContext.getCascadeAttendeesMap().get(deptId).size();
                }
                participantCountMap.put(mcuId, participantCount);
            }
            List<McuKdcBridge> mcuKdcBridgeList = McuKdcBridgeCache.getInstance().getMcuKdcBridges();
            for (McuKdcBridge mcuKdcBridge : mcuKdcBridgeList) {
                ModelBean mb = new ModelBean(mcuKdcBridge.getBusiMcuKdc());
                McuType mcuType = McuType.MCU_KDC;
                String mcuIdStr = EncryptIdUtil.generateEncryptId(mcuKdcBridge.getBusiMcuKdc().getId(), mcuType.getCode());
                mb.put("mcuId", mcuIdStr);
                mb.put("mcuType", mcuType.getCode());
                mb.put("mcuTypeAlias", mcuType.getAlias());
                mb.remove("password");
                mb.remove("adminPassword");
                mb.remove("capacity");
                mb.put("bindDeptCount", DeptMcuKdcMappingCache.getInstance().getBindDeptCount(McuKdcType.SINGLE_NODE, mcuKdcBridge.getBusiMcuKdc().getId()));

                Long mcuId = mcuKdcBridge.getBusiMcuKdc().getId();
                // 参会者数
                Long participantCount = participantCountMap.get(mcuId);
                if (participantCount == null) {
                    participantCount = 0L;
                }
                mb.put("participantCount", participantCount);

                // 活跃会议数
                Long callCount = callCountMap.get(mcuId);
                if (callCount == null) {
                    callCount = 0L;
                }
                mb.put("callCount", callCount);

                // 会议号数
                Long coSpaceCount = coSpaceCountMap.get(mcuId);
                if (coSpaceCount == null) {
                    coSpaceCount = 0L;
                }
                mb.put("coSpaceCount", coSpaceCount);

                mb.put("systemResourceCount", mcuKdcBridge.getSystemResourceCount());
                mb.put("usedResourceCount", mcuKdcBridge.getUsedResourceCount());

                // 连接失败原因
                mb.put("connectionFailedReason", mcuKdcBridge.getConnectionFailedReason());
                busiMcus.add(mb);
            }
        }
        // smc3
        {
            Map<Long, Long> callCountMap = new HashMap<>();
            Map<Long, Long> coSpaceCountMap = new HashMap<>();
            Map<Long, Long> participantCountMap = new HashMap<>();
            for (Smc3ConferenceContext conferenceContext : Smc3ConferenceContextCache.getInstance().values()) {
                Long mcuId = conferenceContext.getSmc3Bridge().getBusiSMC().getId();
                Long callCount = callCountMap.get(mcuId);
                if (callCount == null) {
                    callCount = 0L;
                }
                callCount++;
                callCountMap.put(mcuId, callCount);
                Long coSpaceCount = coSpaceCountMap.get(mcuId);
                if (coSpaceCount == null) {
                    coSpaceCount = 0L;
                }
                coSpaceCount++;
                coSpaceCountMap.put(mcuId, coSpaceCount);
                Long participantCount = participantCountMap.get(mcuId);
                if (participantCount == null) {
                    participantCount = 0L;
                }
                participantCount += conferenceContext.getMasterAttendee() != null ? 1 : 0;
                participantCount += conferenceContext.getAttendees().size();
                participantCount += conferenceContext.getMasterAttendees().size();
                for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                    participantCount += conferenceContext.getCascadeAttendeesMap().get(deptId).size();
                }
                participantCountMap.put(mcuId, participantCount);
            }

            List<Smc3Bridge> fbs = new ArrayList<>(Smc3BridgeCache.getInstance().getIdToTeleBridgeMap().values());
            for (Smc3Bridge fmeBridge : fbs) {
                ModelBean mb = new ModelBean(fmeBridge.getBusiSMC());
                Long mcuId = fmeBridge.getBusiSMC().getId();
                McuType mcuType = McuType.SMC3;
                String mcuIdStr = EncryptIdUtil.generateEncryptId(fmeBridge.getBusiSMC().getId(), mcuType.getCode());
                mb.put("mcuId", mcuIdStr);
                mb.put("mcuType", mcuType.getCode());
                mb.put("mcuTypeAlias", mcuType.getAlias());
                mb.remove("username");
                mb.remove("password");
                mb.remove("adminPassword");
                mb.remove("meetingUsername");
                mb.remove("meetingPassword");
                mb.put("bindDeptCount", DeptSmc3MappingCache.getInstance().getBindDeptCount(FmeType.SINGLE_NODE, fmeBridge.getBusiSMC().getId()));

                // websocket断开次数
                mb.put("webSocketBreakCount", fmeBridge.getWebSocketBreakCount());

                // 连接创建成功的数量，理论只能存在一个
                mb.put("websocketConnections", fmeBridge.getWsAuthTokens());


                // 自上次断开后的重试次数
                mb.put("websocketConnectionTryTimesSinceLastDisconnected", fmeBridge.getWebsocketConnectionTryTimesSinceLastDisconnected());

                // 首次建立连接时间
                mb.put("firstConnectedTime", fmeBridge.getFirstConnectedTime());

                // 最后一次建立连接时间
                mb.put("lastConnectedTime", fmeBridge.getLastConnectedTime());

                // 最后一次连接断开时间
                mb.put("lastDisConnectedTime", fmeBridge.getLastDisConnectedTime());
                mb.put("smc3Status", fmeBridge.isAvailable());

                // 连接失败原因
                mb.put("connectionFailedReason", fmeBridge.getConnectionFailedReason());


                // 参会者数
                Long participantCount = participantCountMap.get(mcuId);
                if (participantCount == null) {
                    participantCount = 0L;
                }
                mb.put("participantCount", participantCount);

                // 活跃会议数
                Long callCount = callCountMap.get(mcuId);
                if (callCount == null) {
                    callCount = 0L;
                }
                mb.put("callCount", callCount);

                // 会议号数
                Long coSpaceCount = coSpaceCountMap.get(mcuId);
                if (coSpaceCount == null) {
                    coSpaceCount = 0L;
                }
                mb.put("coSpaceCount", coSpaceCount);
                busiMcus.add(mb);
            }
        }
        // smc2
        {
            Map<Long, Long> callCountMap = new HashMap<>();
            Map<Long, Long> coSpaceCountMap = new HashMap<>();
            Map<Long, Long> participantCountMap = new HashMap<>();
            for (Smc2ConferenceContext conferenceContext : Smc2ConferenceContextCache.getInstance().values()) {
                Long mcuId = conferenceContext.getSmc2Bridge().getBusiSmc2().getId();
                Long callCount = callCountMap.get(mcuId);
                if (callCount == null) {
                    callCount = 0L;
                }
                callCount++;
                callCountMap.put(mcuId, callCount);
                Long coSpaceCount = coSpaceCountMap.get(mcuId);
                if (coSpaceCount == null) {
                    coSpaceCount = 0L;
                }
                coSpaceCount++;
                coSpaceCountMap.put(mcuId, coSpaceCount);
                Long participantCount = participantCountMap.get(mcuId);
                if (participantCount == null) {
                    participantCount = 0L;
                }
                participantCount += conferenceContext.getMasterAttendee() != null ? 1 : 0;
                participantCount += conferenceContext.getAttendees().size();
                participantCount += conferenceContext.getMasterAttendees().size();
                for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                    participantCount += conferenceContext.getCascadeAttendeesMap().get(deptId).size();
                }
                participantCountMap.put(mcuId, participantCount);
            }

            List<Smc2Bridge> fbs = new ArrayList<>(Smc2BridgeCache.getInstance().getSmc2BridgeMap().values());
            for (Smc2Bridge fmeBridge : fbs) {
                ModelBean mb = new ModelBean(fmeBridge.getBusiSmc2());
                Long mcuId = fmeBridge.getBusiSmc2().getId();
                McuType mcuType = McuType.SMC2;
                String mcuIdStr = EncryptIdUtil.generateEncryptId(fmeBridge.getBusiSmc2().getId(), mcuType.getCode());
                mb.put("mcuId", mcuIdStr);
                mb.put("mcuType", mcuType.getCode());
                mb.put("mcuTypeAlias", mcuType.getAlias());
                mb.remove("username");
                mb.remove("password");
                mb.remove("adminPassword");
                mb.remove("meetingUsername");
                mb.remove("meetingPassword");
                mb.put("bindDeptCount", DeptSmc2MappingCache.getInstance().getBindDeptCount(FmeType.SINGLE_NODE, fmeBridge.getBusiSmc2().getId()));


                mb.put("smc2Status", fmeBridge.isAvailable());
                // 连接失败原因
                mb.put("connectionFailedReason", fmeBridge.getConnectionFailedReason());


                // 参会者数
                Long participantCount = participantCountMap.get(mcuId);
                if (participantCount == null) {
                    participantCount = 0L;
                }
                mb.put("participantCount", participantCount);

                // 活跃会议数
                Long callCount = callCountMap.get(mcuId);
                if (callCount == null) {
                    callCount = 0L;
                }
                mb.put("callCount", callCount);

                // 会议号数
                Long coSpaceCount = coSpaceCountMap.get(mcuId);
                if (coSpaceCount == null) {
                    coSpaceCount = 0L;
                }
                mb.put("coSpaceCount", coSpaceCount);
                busiMcus.add(mb);
            }
        }

        //tencent
        {
            Map<Long, Long> callCountMap = new HashMap<>();
            Map<Long, Long> coSpaceCountMap = new HashMap<>();
            Map<Long, Long> participantCountMap = new HashMap<>();
            for (TencentConferenceContext conferenceContext : TencentConferenceContextCache.getInstance().values()) {
                Long mcuId = conferenceContext.getTencentBridge().getBusiTencent().getId();
                Long callCount = callCountMap.get(mcuId);
                if (callCount == null) {
                    callCount = 0L;
                }
                callCount++;
                callCountMap.put(mcuId, callCount);
                Long coSpaceCount = coSpaceCountMap.get(mcuId);
                if (coSpaceCount == null) {
                    coSpaceCount = 0L;
                }
                coSpaceCount++;
                coSpaceCountMap.put(mcuId, coSpaceCount);
                Long participantCount = participantCountMap.get(mcuId);
                if (participantCount == null) {
                    participantCount = 0L;
                }
                participantCount += conferenceContext.getMasterAttendee() != null ? 1 : 0;
                participantCount += conferenceContext.getAttendees().size();
                participantCount += conferenceContext.getMasterAttendees().size();
                for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                    participantCount += conferenceContext.getCascadeAttendeesMap().get(deptId).size();
                }
                participantCountMap.put(mcuId, participantCount);
            }
            List<TencentBridge> fbs = new ArrayList<>(TencentBridgeCache.getInstance().getTencentBridgeMap().values());
            for (TencentBridge fmeBridge : fbs) {
                ModelBean mb = new ModelBean(fmeBridge.getBusiTencent());
                Long mcuId = fmeBridge.getBusiTencent().getId();
                McuType mcuType = McuType.MCU_TENCENT;
                String mcuIdStr = EncryptIdUtil.generateEncryptId(fmeBridge.getBusiTencent().getId(), mcuType.getCode());
                mb.put("mcuId", mcuIdStr);
                mb.put("mcuType", mcuType.getCode());
                mb.put("mcuTypeAlias", mcuType.getAlias());
                mb.remove("secretKey");
                mb.put("bindDeptCount", DeptTencentMappingCache.getInstance().getBindDeptCount(FmeType.SINGLE_NODE, fmeBridge.getBusiTencent().getId()));


                mb.put("tencentStatus", fmeBridge.isAvailable());
                // 连接失败原因
                mb.put("connectionFailedReason", fmeBridge.getConnectionFailedReason());


                // 参会者数
                Long participantCount = participantCountMap.get(mcuId);
                if (participantCount == null) {
                    participantCount = 0L;
                }
                mb.put("participantCount", participantCount);

                // 活跃会议数
                Long callCount = callCountMap.get(mcuId);
                if (callCount == null) {
                    callCount = 0L;
                }
                mb.put("callCount", callCount);

                // 会议号数
                Long coSpaceCount = coSpaceCountMap.get(mcuId);
                if (coSpaceCount == null) {
                    coSpaceCount = 0L;
                }
                mb.put("coSpaceCount", coSpaceCount);
                busiMcus.add(mb);
            }
        }
        //ding
        {
            Map<Long, Long> callCountMap = new HashMap<>();
            Map<Long, Long> coSpaceCountMap = new HashMap<>();
            Map<Long, Long> participantCountMap = new HashMap<>();
            for (DingConferenceContext conferenceContext : DingConferenceContextCache.getInstance().values()) {
                Long mcuId = conferenceContext.getDingBridge().getBusiDing().getId();
                Long callCount = callCountMap.get(mcuId);
                if (callCount == null) {
                    callCount = 0L;
                }
                callCount++;
                callCountMap.put(mcuId, callCount);
                Long coSpaceCount = coSpaceCountMap.get(mcuId);
                if (coSpaceCount == null) {
                    coSpaceCount = 0L;
                }
                coSpaceCount++;
                coSpaceCountMap.put(mcuId, coSpaceCount);
                Long participantCount = participantCountMap.get(mcuId);
                if (participantCount == null) {
                    participantCount = 0L;
                }
                participantCount += conferenceContext.getMasterAttendee() != null ? 1 : 0;
                participantCount += conferenceContext.getAttendees().size();
                participantCount += conferenceContext.getMasterAttendees().size();
                for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                    participantCount += conferenceContext.getCascadeAttendeesMap().get(deptId).size();
                }
                participantCountMap.put(mcuId, participantCount);
            }
            List<DingBridge> fbs = new ArrayList<>(DingBridgeCache.getInstance().getDingBridgeMap().values());
            for (DingBridge fmeBridge : fbs) {
                ModelBean mb = new ModelBean(fmeBridge.getBusiDing());
                Long mcuId = fmeBridge.getBusiDing().getId();
                McuType mcuType = McuType.MCU_DING;
                String mcuIdStr = EncryptIdUtil.generateEncryptId(fmeBridge.getBusiDing().getId(), mcuType.getCode());
                mb.put("mcuId", mcuIdStr);
                mb.put("mcuType", mcuType.getCode());
                mb.put("mcuTypeAlias", mcuType.getAlias());
                mb.remove("secretKey");
                mb.put("bindDeptCount", DeptDingMappingCache.getInstance().getBindDeptCount(FmeType.SINGLE_NODE, fmeBridge.getBusiDing().getId()));


                // 连接失败原因
                mb.put("connectionFailedReason", fmeBridge.getConnectionFailedReason());


                // 参会者数
                Long participantCount = participantCountMap.get(mcuId);
                if (participantCount == null) {
                    participantCount = 0L;
                }
                mb.put("participantCount", participantCount);

                // 活跃会议数
                Long callCount = callCountMap.get(mcuId);
                if (callCount == null) {
                    callCount = 0L;
                }
                mb.put("callCount", callCount);

                // 会议号数
                Long coSpaceCount = coSpaceCountMap.get(mcuId);
                if (coSpaceCount == null) {
                    coSpaceCount = 0L;
                }
                mb.put("coSpaceCount", coSpaceCount);
                busiMcus.add(mb);
            }
        }
        //hwcloud
        {
            Map<Long, Long> callCountMap = new HashMap<>();
            Map<Long, Long> coSpaceCountMap = new HashMap<>();
            Map<Long, Long> participantCountMap = new HashMap<>();
            for (HwcloudConferenceContext conferenceContext : HwcloudConferenceContextCache.getInstance().values()) {
                Long mcuId = conferenceContext.getHwcloudBridge().getBusiHwcloud().getId();
                Long callCount = callCountMap.get(mcuId);
                if (callCount == null) {
                    callCount = 0L;
                }
                callCount++;
                callCountMap.put(mcuId, callCount);
                Long coSpaceCount = coSpaceCountMap.get(mcuId);
                if (coSpaceCount == null) {
                    coSpaceCount = 0L;
                }
                coSpaceCount++;
                coSpaceCountMap.put(mcuId, coSpaceCount);
                Long participantCount = participantCountMap.get(mcuId);
                if (participantCount == null) {
                    participantCount = 0L;
                }
                participantCount += conferenceContext.getMasterAttendee() != null ? 1 : 0;
                participantCount += conferenceContext.getAttendees().size();
                participantCount += conferenceContext.getMasterAttendees().size();
                for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                    participantCount += conferenceContext.getCascadeAttendeesMap().get(deptId).size();
                }
                participantCountMap.put(mcuId, participantCount);
            }
            List<HwcloudBridge> fbs = new ArrayList<>(HwcloudBridgeCache.getInstance().getHwcloudBridgeMap().values());
            for (HwcloudBridge fmeBridge : fbs) {
                ModelBean mb = new ModelBean(fmeBridge.getBusiHwcloud());
                Long mcuId = fmeBridge.getBusiHwcloud().getId();
                McuType mcuType = McuType.MCU_HWCLOUD;
                String mcuIdStr = EncryptIdUtil.generateEncryptId(fmeBridge.getBusiHwcloud().getId(), mcuType.getCode());
                mb.put("mcuId", mcuIdStr);
                mb.put("mcuType", mcuType.getCode());
                mb.put("mcuTypeAlias", mcuType.getAlias());
                mb.remove("appKey");
                mb.put("bindDeptCount", DeptHwcloudMappingCache.getInstance().getBindDeptCount(FmeType.SINGLE_NODE, fmeBridge.getBusiHwcloud().getId()));


                // 连接失败原因
                mb.put("connectionFailedReason", fmeBridge.getConnectionFailedReason());
                mb.put("cropResource", fmeBridge.getShowCorpResourceResponse());
                // 参会者数
                Long participantCount = participantCountMap.get(mcuId);
                if (participantCount == null) {
                    participantCount = 0L;
                }
                mb.put("participantCount", participantCount);

                // 活跃会议数
                Long callCount = callCountMap.get(mcuId);
                if (callCount == null) {
                    callCount = 0L;
                }
                mb.put("callCount", callCount);

                // 会议号数
                Long coSpaceCount = coSpaceCountMap.get(mcuId);
                if (coSpaceCount == null) {
                    coSpaceCount = 0L;
                }
                mb.put("coSpaceCount", coSpaceCount);
                busiMcus.add(mb);
            }
        }
        //zte
        {
            Map<Long, Long> callCountMap = new HashMap<>();
            Map<Long, Long> coSpaceCountMap = new HashMap<>();
            Map<Long, Long> participantCountMap = new HashMap<>();
            for (McuZteConferenceContext conferenceContext : McuZteConferenceContextCache.getInstance().values()) {
                Long mcuId = conferenceContext.getMcuZteBridge().getBusiMcuZte().getId();
                Long callCount = callCountMap.get(mcuId);
                if (callCount == null) {
                    callCount = 0L;
                }
                callCount++;
                callCountMap.put(mcuId, callCount);
                Long coSpaceCount = coSpaceCountMap.get(mcuId);
                if (coSpaceCount == null) {
                    coSpaceCount = 0L;
                }
                coSpaceCount++;
                coSpaceCountMap.put(mcuId, coSpaceCount);
                Long participantCount = participantCountMap.get(mcuId);
                if (participantCount == null) {
                    participantCount = 0L;
                }
                participantCount += conferenceContext.getMasterAttendee() != null ? 1 : 0;
                participantCount += conferenceContext.getAttendees().size();
                participantCount += conferenceContext.getMasterAttendees().size();
                for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                    participantCount += conferenceContext.getCascadeAttendeesMap().get(deptId).size();
                }
                participantCountMap.put(mcuId, participantCount);
            }
            List<McuZteBridge> mcuZteBridgeList = McuZteBridgeCache.getInstance().getMcuZteBridges();
            for (McuZteBridge mcuZteBridge : mcuZteBridgeList) {
                ModelBean mb = new ModelBean(mcuZteBridge.getBusiMcuZte());
                McuType mcuType = McuType.MCU_ZTE;
                String mcuIdStr = EncryptIdUtil.generateEncryptId(mcuZteBridge.getBusiMcuZte().getId(), mcuType.getCode());
                mb.put("mcuId", mcuIdStr);
                mb.put("mcuType", mcuType.getCode());
                mb.put("mcuTypeAlias", mcuType.getAlias());
                mb.remove("password");
                mb.remove("adminPassword");
                mb.remove("capacity");
                mb.put("bindDeptCount", DeptMcuZteMappingCache.getInstance().getBindDeptCount(McuZteType.SINGLE_NODE, mcuZteBridge.getBusiMcuZte().getId()));

                Long mcuId = mcuZteBridge.getBusiMcuZte().getId();
                // 参会者数
                Long participantCount = participantCountMap.get(mcuId);
                if (participantCount == null) {
                    participantCount = 0L;
                }
                mb.put("participantCount", participantCount);

                // 活跃会议数
                Long callCount = callCountMap.get(mcuId);
                if (callCount == null) {
                    callCount = 0L;
                }
                mb.put("callCount", callCount);

                // 会议号数
                Long coSpaceCount = coSpaceCountMap.get(mcuId);
                if (coSpaceCount == null) {
                    coSpaceCount = 0L;
                }
                mb.put("coSpaceCount", coSpaceCount);

//                mb.put("systemResourceCount", mcuZteBridge.getSystemResourceCount());
//                mb.put("usedResourceCount", mcuZteBridge.getUsedResourceCount());

                // 连接失败原因
                mb.put("connectionFailedReason", mcuZteBridge.getConnectionFailedReason());
                busiMcus.add(mb);
            }
        }
        return success(busiMcus);
    }

    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">创建会议桥[bridgeHost]</pre>
     *
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "单个会议桥记录新增：记录的属性和属性值放到请求body中封装为json格式", description = "新增MCU")
    @PostMapping("")
    public RestResponse saveMcu(@RequestBody JSONObject jsonObject) {
        String mcuTypeStr = jsonObject.getString("mcuType");
        McuType mcuType = McuType.convert(mcuTypeStr);
        switch (mcuType) {
            case FME: {
                BusiFme busiFme = jsonObject.toJavaObject(BusiFme.class);
                fmeCacheService.addFme(busiFme);
                break;
            }
            case MCU_ZJ: {
                BusiMcuZj busiMcuZj = jsonObject.toJavaObject(BusiMcuZj.class);
                mcuZjCacheService.addMcuZj(busiMcuZj);
                break;
            }
            case MCU_PLC: {
                BusiMcuPlc busiMcuPlc = jsonObject.toJavaObject(BusiMcuPlc.class);
                mcuPlcCacheService.addMcuPlc(busiMcuPlc);
                break;
            }
            case MCU_KDC: {
                BusiMcuKdc busiMcuKdc = jsonObject.toJavaObject(BusiMcuKdc.class);
                mcuKdcCacheService.addMcuKdc(busiMcuKdc);
                break;
            }
            case SMC3: {
                BusiMcuSmc3 busiMcuSmc3 = jsonObject.toJavaObject(BusiMcuSmc3.class);
                mcuSmc3CacheService.addMcuSmc3(busiMcuSmc3);
                break;
            }
            case SMC2: {
                BusiMcuSmc2 busiMcuSmc2 = jsonObject.toJavaObject(BusiMcuSmc2.class);
                mcuSmc2CacheService.addMcuSmc2(busiMcuSmc2);
                break;
            }
            case MCU_TENCENT: {
                BusiMcuTencent busiMcuTencent = jsonObject.toJavaObject(BusiMcuTencent.class);
                tencentCacheService.addMcuTencent(busiMcuTencent);
                break;
            }

            case MCU_DING: {
                BusiMcuDing busiMcuding = jsonObject.toJavaObject(BusiMcuDing.class);
                dingCacheService.addMcuDing(busiMcuding);
                break;
            }
            case MCU_HWCLOUD: {
                BusiMcuHwcloud busiMcuhwcloud = jsonObject.toJavaObject(BusiMcuHwcloud.class);
                hwcloudCacheService.addMcuHwcloud(busiMcuhwcloud);
                break;
            }
            case MCU_ZTE: {
                BusiMcuZte busiMcuZte = jsonObject.toJavaObject(BusiMcuZte.class);
                mcuZteCacheService.addMcuZte(busiMcuZte);
                break;
            }

        }
        return success();
    }

    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键ID删除一个实体</pre>
     *
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "根据主键ID删除单个记录：id放到rest地址上占位符处", description = "删除MCU")
    @DeleteMapping("/{mcuId}")
    public RestResponse deleteMcu(@PathVariable String mcuId) {
        EncryptIdVo encryptIdVo = EncryptIdUtil.parasEncryptId(mcuId);
        Long id = encryptIdVo.getId();
        McuType mcuType = encryptIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                fmeCacheService.deleteFme(id);
                break;
            }
            case MCU_ZJ: {
                mcuZjCacheService.deleteMcuZj(id);
                break;
            }
            case MCU_PLC: {
                mcuPlcCacheService.deleteMcuPlc(id);
                break;
            }
            case MCU_KDC: {
                mcuKdcCacheService.deleteMcuKdc(id);
                break;
            }
            case SMC3: {
                mcuSmc3CacheService.deleteMcuSmc3(id);
                break;
            }
            case SMC2: {
                mcuSmc2CacheService.deleteMcuSmc2(id);
                break;
            }
            case MCU_TENCENT: {
                tencentCacheService.deleteMcuTencent(id);
                break;
            }
            case MCU_DING: {
                dingCacheService.deleteMcuDing(id);
                break;
            }
            case MCU_HWCLOUD: {
                hwcloudCacheService.deleteMcuHwcloud(id);
                break;
            }
            case MCU_ZTE: {
                mcuZteCacheService.deleteMcuZte(id);
                break;
            }
        }
        return success("Delete Entity successfully, id: " + id);
    }

    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键修改实体属性</pre>
     *
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "根据主键修改单个会议桥记录：id放到rest地址上占位符处，修改的字段和字段值放到请求body中封装为json格式", description = "修改MCU")
    @PutMapping("/{mcuId}")
    public RestResponse updateMcu(@RequestBody JSONObject jsonObject, @PathVariable String mcuId) {
        EncryptIdVo encryptIdVo = EncryptIdUtil.parasEncryptId(mcuId);
        Long id = encryptIdVo.getId();
        McuType mcuType = encryptIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                BusiFme busiFme = jsonObject.toJavaObject(BusiFme.class);
                busiFme.setId(id);
                fmeCacheService.updateFme(busiFme);
                break;
            }
            case MCU_ZJ: {
                BusiMcuZj busiMcuZj = jsonObject.toJavaObject(BusiMcuZj.class);
                busiMcuZj.setId(id);
                mcuZjCacheService.updateMcuZj(busiMcuZj);
                break;
            }
            case MCU_PLC: {
                BusiMcuPlc busiMcuPlc = jsonObject.toJavaObject(BusiMcuPlc.class);
                busiMcuPlc.setId(id);
                mcuPlcCacheService.updateMcuPlc(busiMcuPlc);
                break;
            }
            case MCU_KDC: {
                BusiMcuKdc busiMcuKdc = jsonObject.toJavaObject(BusiMcuKdc.class);
                busiMcuKdc.setId(id);
                mcuKdcCacheService.updateMcuKdc(busiMcuKdc);
                break;
            }
            case SMC3: {
                BusiMcuSmc3 busiMcuSmc3 = jsonObject.toJavaObject(BusiMcuSmc3.class);
                busiMcuSmc3.setId(id);
                mcuSmc3CacheService.updateMcuSmc3(busiMcuSmc3);
                break;
            }
            case SMC2: {
                BusiMcuSmc2 busiMcuSmc2 = jsonObject.toJavaObject(BusiMcuSmc2.class);
                busiMcuSmc2.setId(id);
                mcuSmc2CacheService.updateMcuSmc2(busiMcuSmc2);
                break;
            }
            case MCU_TENCENT: {
                BusiMcuTencent busiMcuTencent = jsonObject.toJavaObject(BusiMcuTencent.class);
                busiMcuTencent.setId(id);
                tencentCacheService.updateMcuTencent(busiMcuTencent);
                break;
            }

            case MCU_DING: {
                BusiMcuDing busiMcuDing = jsonObject.toJavaObject(BusiMcuDing.class);
                busiMcuDing.setId(id);
                dingCacheService.updateMcuDing(busiMcuDing);
                break;
            }

            case MCU_HWCLOUD: {
                BusiMcuHwcloud busiMcuhwcloud = jsonObject.toJavaObject(BusiMcuHwcloud.class);
                busiMcuhwcloud.setId(id);
                hwcloudCacheService.updateMcuHwcloud(busiMcuhwcloud);
                break;
            }

            case MCU_ZTE: {
                BusiMcuZte busiMcuZte = jsonObject.toJavaObject(BusiMcuZte.class);
                busiMcuZte.setId(id);
                mcuZteCacheService.updateMcuZte(busiMcuZte);
                break;
            }
        }
        return success();
    }

    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键查找单个记录</pre>
     *
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "根据主键查询单个会议桥记录：id放到rest地址上占位符处")
    @GetMapping("/{mcuId}")
    public RestResponse getMcu(@PathVariable String mcuId) {
        EncryptIdVo encryptIdVo = EncryptIdUtil.parasEncryptId(mcuId);
        Long id = encryptIdVo.getId();
        McuType mcuType = encryptIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                FmeBridge fmeBridge = FmeBridgeCache.getInstance().get(id);
                if (fmeBridge != null) {
                    ModelBean mb = new ModelBean(fmeBridge.getBusiFme());
                    mb.remove("password");
                    mb.remove("adminPassword");
                    mb.remove("devToken");
                    return success(mb);
                }
            }
            case MCU_ZJ: {
                McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().get(id);
                if (mcuZjBridge != null) {
                    ModelBean mb = new ModelBean(mcuZjBridge.getBusiMcuZj());
                    mb.remove("password");
                    mb.remove("adminPassword");
                    mb.remove("devToken");
                    return success(mb);
                }
            }
            case MCU_PLC: {
                McuPlcBridge mcuPlcBridge = McuPlcBridgeCache.getInstance().get(id);
                if (mcuPlcBridge != null) {
                    ModelBean mb = new ModelBean(mcuPlcBridge.getBusiMcuPlc());
                    mb.remove("password");
                    mb.remove("adminPassword");
                    mb.remove("devToken");
                    return success(mb);
                }
            }
            case MCU_KDC: {
                McuKdcBridge mcuKdcBridge = McuKdcBridgeCache.getInstance().get(id);
                if (mcuKdcBridge != null) {
                    ModelBean mb = new ModelBean(mcuKdcBridge.getBusiMcuKdc());
                    mb.remove("password");
                    mb.remove("adminPassword");
                    mb.remove("devToken");
                    return success(mb);
                }
            }
            case SMC3: {
                Smc3Bridge smc3Bridge = Smc3BridgeCache.getInstance().get(id);
                if (smc3Bridge != null) {
                    ModelBean mb = new ModelBean(smc3Bridge.getBusiSMC());
                    mb.remove("password");
                    mb.remove("adminPassword");
                    mb.remove("devToken");
                    return success(mb);
                }
            }
            case SMC2: {
                Smc2Bridge smc2Bridge = Smc2BridgeCache.getInstance().get(id);
                if (smc2Bridge != null) {
                    ModelBean mb = new ModelBean(smc2Bridge.getBusiSmc2());
                    mb.remove("password");
                    mb.remove("adminPassword");
                    mb.remove("devToken");
                    return success(mb);
                }
            }

            case MCU_TENCENT: {
                TencentBridge tencentBridge = TencentBridgeCache.getInstance().get(id);
                if (tencentBridge != null) {
                    ModelBean mb = new ModelBean(tencentBridge.getBusiTencent());
                    return success(mb);
                }
            }

            case MCU_DING: {
                DingBridge dingBridge = DingBridgeCache.getInstance().get(id);
                if (dingBridge != null) {
                    ModelBean mb = new ModelBean(dingBridge.getBusiDing());
                    return success(mb);
                }
            }

            case MCU_HWCLOUD: {
                HwcloudBridge hwcloudBridge = HwcloudBridgeCache.getInstance().get(id);
                if (hwcloudBridge != null) {
                    ModelBean mb = new ModelBean(hwcloudBridge.getBusiHwcloud());
                    return success(mb);
                }
            }

            case MCU_ZTE: {
                McuZteBridge mcuZteBridge = McuZteBridgeCache.getInstance().get(id);
                if (mcuZteBridge != null) {
                    ModelBean mb = new ModelBean(mcuZteBridge.getBusiMcuZte());
                    mb.remove("password");
                    mb.remove("adminPassword");
                    mb.remove("devToken");
                    return success(mb);
                }
            }
        }
        return success(null);
    }
}
