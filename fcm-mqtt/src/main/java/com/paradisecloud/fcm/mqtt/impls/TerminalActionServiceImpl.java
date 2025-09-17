package com.paradisecloud.fcm.mqtt.impls;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.bean.BeanUtils;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.mqtt.cache.*;
import com.paradisecloud.fcm.ops.cloud.cache.ClientCache;
import com.paradisecloud.fcm.ops.cloud.cache.OpsCache;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomDoorplateCache;
import com.paradisecloud.fcm.service.conference.attendee.BaseFixedParamValue;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.RecordsSearchVo;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.fme.conference.interfaces.*;
import com.paradisecloud.fcm.fme.conference.task.SendConferenceInfoToTerminalTask;
import com.paradisecloud.fcm.fme.conference.task.UpdateRecordsTask;
import com.paradisecloud.fcm.fme.model.busi.attendee.*;
import com.paradisecloud.fcm.mcu.kdc.attendee.utils.McuKdcConferenceContextUtils;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.AttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.*;
import com.paradisecloud.fcm.mcu.kdc.task.McuKdcDelayTaskService;
import com.paradisecloud.fcm.mcu.kdc.task.McuKdcSendConferenceInfoToTerminalTask;
import com.paradisecloud.fcm.mcu.plc.attendee.utils.McuPlcConferenceContextUtils;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.AttendeeForMcuPlc;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.*;
import com.paradisecloud.fcm.mcu.plc.task.McuPlcDelayTaskService;
import com.paradisecloud.fcm.mcu.plc.task.McuPlcSendConferenceInfoToTerminalTask;
import com.paradisecloud.fcm.mcu.zj.attendee.utils.McuZjConferenceContextUtils;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.*;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.*;
import com.paradisecloud.fcm.mcu.zj.task.DelayTaskService;
import com.paradisecloud.fcm.mcu.zj.task.McuZjSendConferenceInfoToTerminalTask;
import com.paradisecloud.fcm.mqtt.client.EmqClient;
import com.paradisecloud.fcm.mqtt.constant.*;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiRegisterTerminalService;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiTerminalUpgradeService;
import com.paradisecloud.fcm.mqtt.model.*;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomLotCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveBridgeCache;
import com.paradisecloud.fcm.terminal.fs.util.SpringContextUtil;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.service.ISysUserService;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.model.GenericValue;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiCallLegProfileService;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.mqtt.common.PublisMessage;
import com.paradisecloud.fcm.mqtt.common.ResponseTerminal;
import com.paradisecloud.fcm.mqtt.common.TerminalSipAccount;
import com.paradisecloud.fcm.mqtt.enums.MqttType;
import com.paradisecloud.fcm.mqtt.enums.QosEnum;
import com.paradisecloud.fcm.mqtt.enums.TerminalActionEnum;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiTerminalSysInfoService;
import com.paradisecloud.fcm.mqtt.interfaces.ITerminalActionService;
import com.paradisecloud.fcm.sdk.video.service.interfaces.IVideoConferenceSDKService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.constant.FcmConfigConstant;
import com.paradisecloud.fcm.terminal.service.interfaces.IBusiTerminalService;
import com.paradisecloud.system.dao.mapper.SysDeptMapper;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;
import com.paradisecloud.system.model.TreeSelect;
import com.paradisecloud.system.service.ISysDeptService;
import com.sinhy.exception.SystemException;
import com.sinhy.http.HttpObjectCreator;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessorAdapter;

import javax.annotation.Resource;

/**
 * @author zyz
 */
@Transactional
@Service
public class TerminalActionServiceImpl implements ITerminalActionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalActionServiceImpl.class);

    @Resource
    private BusiTerminalMapper busiTerminalMapper;

    @Resource
    private ISysDeptService deptService;

    @Resource
    private BusiTerminalActionMapper busiTerminalActionMapper;

    @Resource
    private IBusiCallLegProfileService busiCallLegProfileService;

    @Resource
    private IBusiConferenceAppointmentService busiConferenceAppointmentService;

    @Resource
    private IBusiTerminalSysInfoService busiTerminalSysInfoService;

    @Resource
    private BusiTemplateConferenceMapper busiTemplateConferenceMapper;

    @Resource
    private IBusiConferenceService busiConferenceService;

    @Resource
    private IAttendeeService attendeeService;

    @Resource
    private IBusiRecordsService busiRecordsService;

    @Resource
    private SysDeptMapper deptMapper;

    @Resource
    private ISysDeptService sysDeptService;

    @Resource
    private IDefaultAttendeeOperationPackageService defaultAttendeeOperationPackageService;

    @Resource
    private IVideoConferenceSDKService videoConferenceSDKService;

    @Resource
    private IBusiTerminalService busiTerminalService;

    @Resource
    private BusiSipAccountMapper busiSipAccountMapper;

    @Resource
    private BusiRegisterTerminalMapper busiRegisterTerminalMapper;

    @Resource
    private BusiTemplateParticipantMapper busiTemplateParticipantMapper;

    @Resource
    private BusiMqttMapper busiMqttMapper;

    @Resource
    private BusiConferenceAppointmentMapper busiConferenceAppointmentMapper;

    @Resource
    private IBusiTemplateConferenceService busiTemplateConferenceService;

    @Resource
    private IBusiRegisterTerminalService busiRegisterTerminalService;

    @Resource
    private BusiRecordSettingMapper busiRecordSettingMapper;

    @Resource
    private BusiHistoryConferenceMapper busiHistoryConferenceMapper;

    @Resource
    private BusiRecordsMapper busiRecordsMapper;

    @Resource
    private IMqttService iMqttService;

    @Resource
    private BusiMcuZjTemplateConferenceMapper busiMcuZjTemplateConferenceMapper;

    @Resource
    private TaskService taskService;

    @Resource
    private ISimpleConferenceControlForMcuZjService simpleConferenceControlForMcuZjService;

    @Resource
    private IBusiTerminalUpgradeService busiTerminalUpgradeService;

    @Resource
    private IBusiMcuZjConferenceService busiMcuZjConferenceService;

    @Resource
    private BusiUserTerminalMapper busiUserTerminalMapper;

    @Resource
    private ISysUserService sysUserService;

    @Resource
    private BusiLiveSettingMapper busiLiveSettingMapper;

    @Resource
    private IBusiMcuZjConferenceAppointmentService busiMcuZjConferenceAppointmentService;

    @Resource
    private IAttendeeForMcuZjService attendeeForMcuZjService;

    @Resource
    private IBusiRecordsForMcuZjService busiRecordsForMcuZjService;

    @Resource
    private BusiMcuZjConferenceAppointmentMapper busiMcuZjConferenceAppointmentMapper;

    @Resource
    private IBusiMcuZjTemplateConferenceService busiMcuZjTemplateConferenceService;

    @Resource
    private ITemplateConferenceStartService templateConferenceStartService;

    @Resource
    private IDefaultAttendeeOperationPackageForMcuZjService defaultAttendeeOperationPackageForMcuZjService;

    @Resource
    private DelayTaskService delayTaskService;

    @Resource
    private BusiMcuZjTemplateParticipantMapper busiMcuZjTemplateParticipantMapper;

    @Resource
    private BusiMcuPlcTemplateConferenceMapper busiMcuPlcTemplateConferenceMapper;

    @Resource
    private ISimpleConferenceControlForMcuPlcService simpleConferenceControlForMcuPlcService;

    @Resource
    private IBusiMcuPlcConferenceService busiMcuPlcConferenceService;

    @Resource
    private IBusiMcuPlcConferenceAppointmentService busiMcuPlcConferenceAppointmentService;

    @Resource
    private IAttendeeForMcuPlcService attendeeForMcuPlcService;

    @Resource
    private IBusiRecordsForMcuPlcService busiRecordsForMcuPlcService;

    @Resource
    private BusiMcuPlcConferenceAppointmentMapper busiMcuPlcConferenceAppointmentMapper;

    @Resource
    private IBusiMcuPlcTemplateConferenceService busiMcuPlcTemplateConferenceService;

    @Resource
    private IDefaultAttendeeOperationPackageForMcuPlcService defaultAttendeeOperationPackageForMcuPlcService;

    @Resource
    private McuPlcDelayTaskService mcuPlcDelayTaskService;

    @Resource
    private BusiMcuPlcTemplateParticipantMapper busiMcuPlcTemplateParticipantMapper;

    @Resource
    private BusiMcuKdcTemplateConferenceMapper busiMcuKdcTemplateConferenceMapper;

    @Resource
    private ISimpleConferenceControlForMcuKdcService simpleConferenceControlForMcuKdcService;

    @Resource
    private IBusiMcuKdcConferenceService busiMcuKdcConferenceService;

    @Resource
    private BusiMcuKdcConferenceAppointmentMapper busiMcuKdcConferenceAppointmentMapper;

    @Resource
    private IBusiMcuKdcConferenceAppointmentService busiMcuKdcConferenceAppointmentService;

    @Resource
    private IAttendeeForMcuKdcService attendeeForMcuKdcService;

    @Resource
    private IBusiRecordsForMcuKdcService busiRecordsForMcuKdcService;

    @Resource
    private IBusiMcuKdcTemplateConferenceService busiMcuKdcTemplateConferenceService;

    @Resource
    private IDefaultAttendeeOperationPackageForMcuKdcService defaultAttendeeOperationPackageForMcuKdcService;

    @Resource
    private McuKdcDelayTaskService mcuKdcDelayTaskService;

    @Resource
    private BusiMcuKdcTemplateParticipantMapper busiMcuKdcTemplateParticipantMapper;

    @Resource
    private BusiSmartRoomDoorplateMapper busiSmartRoomDoorplateMapper;

    @Resource
    private BusiSmartRoomLotMapper busiSmartRoomLotMapper;

    @Resource
    private BusiInfoDisplayMapper busiInfoDisplayMapper;

    @Resource
    private BusiOpsMapper busiOpsMapper;

    @Resource
    private BusiClientMapper busiClientMapper;


    private HttpRequester httpRequester = HttpObjectCreator.getInstance().createHttpRequester(MqttConfigConstant.MQTT_BACK_NAME, MqttConfigConstant.MQTT_BACK_PASSWORD, false);

    @Override
    public void notifyFcmDealData(String clientId, Integer action, String userName, String mqttIp, String terminalIp) {
        if (StringUtils.isNotEmpty(clientId) && StringUtils.isNotEmpty(userName)) {

            //0、上线   1、下线
            if (action == 0) {
                //处理上线终端的是新增、还是更新
                this.dealOnlineTerminalCondition(mqttIp, terminalIp, clientId);
            } else {
                //处理下线终端的情况
                this.dealOfflineTerminalCondition(mqttIp, terminalIp, clientId);
            }
        } else {
            //处理下线终端的情况  terminalName,
            this.dealOfflineTerminalCondition(mqttIp, clientId, terminalIp);
        }

        LOGGER.info("fcm根据终端id" + clientId + "上下线动作" + action, "进行逻辑处理!");
    }


    /**
     * 处理下线终端的情况
     *
     * @param mqttIp
     * @param terminalIp
     * @param clientId
     */
    private void dealOfflineTerminalCondition(String mqttIp, String terminalIp, String clientId) {
        BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(clientId);
        if (busiTerminalSn != null) {
            busiTerminalSn.setUpdateTime(new Date());
            busiTerminalSn.setMqttOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
            busiTerminalService.updateBusiTerminal(busiTerminalSn);
        }
    }


    /**
     * 处理上线终端的是新增、还是更新
     *
     * @param mqttIp
     * @param terminalIp
     * @param clientId
     */
    private void dealOnlineTerminalCondition(String mqttIp, String terminalIp, String clientId) {
        BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(clientId);
        if (busiTerminalSn != null) {
            Long deptId = busiTerminalSn.getDeptId();

            //租户deptId，取出集群下的节点，或者单节点的信息，处理更新对象或发布修改ip主题
            this.tenantDeptIdDealRelatedData(deptId, mqttIp, busiTerminalSn);
        }
    }

    /**
     * 租户deptId，取出集群下的节点，或者单节点的信息，处理更新对象或发布修改ip主题
     *
     * @param deptId
     * @param mqttIp
     * @param busiTerminal
     */
    private void tenantDeptIdDealRelatedData(Long deptId, String mqttIp, BusiTerminal busiTerminal) {

        Boolean isExist = false;
        BusiMqttDept busiMqttDept = MqttDeptMappingCache.getInstance().getBindDeptMqtt(deptId);
        if (null != busiMqttDept) {

            if (MqttType.CLUSTER.getValue() == busiMqttDept.getMqttType()) {
                //集群
                MqttBridgeCollection deptAboutMqttBridge = MqttBridgeCache.getInstance().getDeptAboutMqttBridge(deptId);
                if (null != deptAboutMqttBridge && null != deptAboutMqttBridge.getMqttBridges() && deptAboutMqttBridge.getMqttBridges().size() > 0) {
                    List<MqttBridge> mqttBridges = deptAboutMqttBridge.getMqttBridges();
                    if (null != mqttBridges && mqttBridges.size() > 0) {
                        for (MqttBridge mqttBridge : mqttBridges) {
                            if (mqttIp.equals(mqttBridge.getBusiMqtt().getIp())) {
                                isExist = true;
                                break;
                            }

                        }

                        if (isExist) {
                            //处理终端ip和租户绑定的mqtt服务ip，一样或者包含
                            this.terminalIPAndDeptBindIpSame(busiTerminal, mqttIp);
                        } else {
                            //首先发布主题，让终端先修改ip,终端订阅成功后，然后删除mqtt服务上的终端和会话
                            this.publishUpdateTerminalIp(mqttBridges.get(0).getBusiMqtt().getIp(), mqttIp, busiTerminal.getSn());
                            LOGGER.info("租户绑定的集群的ip，不包含此ip" + mqttIp);
                        }
                    }
                }
            } else {
                MqttBridge mqttBridge = MqttBridgeCache.getInstance().getMqttBridgeById(busiMqttDept.getMqttId());
                if (null != mqttBridge && null != mqttBridge.getBusiMqtt()) {
                    //单节点
                    if (mqttIp.equals(mqttBridge.getBusiMqtt().getIp())) {
                        //处理终端ip和租户绑定的mqtt服务ip，一样或者包含
                        this.terminalIPAndDeptBindIpSame(busiTerminal, mqttIp);
                    } else {
                        //首先发布主题，让终端先修改ip,终端订阅成功后，然后删除mqtt服务上的终端和会话
                        this.publishUpdateTerminalIp(mqttBridge.getBusiMqtt().getIp(), mqttIp, busiTerminal.getSn());
                        LOGGER.info("租户绑定的ip " + mqttBridge.getBusiMqtt().getIp() + "," + "实际连接ip" + mqttIp + "," + "两个ip不一致！");
                    }
                }
            }
        } else {
            //租户没有绑定mqtt服务的ip，就要递归找上级的绑定的信息，除非到顶层租户还是为空，那么ip就为空
            this.byDeptIdGetMqttServerInfo(deptId, mqttIp, busiTerminal);
        }
    }


    /**
     * 租户没有绑定mqtt服务的ip，就要递归找上级的绑定的信息，除非到顶层租户还是为空，那么ip就为空
     *
     * @param deptId
     * @param mqttIp
     * @param busiTerminal
     */
    private void byDeptIdGetMqttServerInfo(Long deptId, String mqttIp, BusiTerminal busiTerminal) {
        SysDept sysDept = SysDeptCache.getInstance().get(deptId);
        if (null != sysDept) {
            Long parentDeptId = sysDept.getParentId();
            if (parentDeptId != 0) {
                tenantDeptIdDealRelatedData(parentDeptId, mqttIp, busiTerminal);
            } else {
                BusiMqttDept mqttDept = MqttDeptMappingCache.getInstance().getBindMqttNode(deptId);
                if (null != mqttDept) {
                    tenantDeptIdDealRelatedData(parentDeptId, mqttIp, busiTerminal);
                } else {
                    //更新mqtt类型终端
                    this.updateMqttTypeTerminal(busiTerminal);
                }
            }
        }
    }


    /**
     * 更新mqtt类型终端
     *
     * @param busiTerminal
     */
    private void updateMqttTypeTerminal(BusiTerminal busiTerminal) {
        if (null != busiTerminal && null != busiTerminal.getId()) {
            busiTerminal.setUpdateTime(new Date());
            busiTerminal.setMqttOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
            busiTerminalService.updateBusiTerminal(busiTerminal);
            TerminalCache.getInstance().put(busiTerminal.getId(), busiTerminal);
        }
    }

    /**
     * //处理终端ip和租户绑定的mqtt服务ip，一样或者包含
     *
     * @param busiTerminal
     * @param mqttIp
     */
    private void terminalIPAndDeptBindIpSame(BusiTerminal busiTerminal, String mqttIp) {
        if (null != busiTerminal && null != busiTerminal.getId()) {
            //实际ip和租户绑定ip相同，更新
            this.updateBusiTerminalData(busiTerminal);
        }
    }

    /**
     * 首先发布主题，让终端先修改ip,终端订阅成功后，然后删除mqtt服务上的终端和会话
     *
     * @param mqttIp
     * @param terminalIp
     * @param clientId
     */
    private void publishUpdateTerminalIp(String mqttIp, String terminalIp, String clientId) {
        MqttBridge mqttBridge = MqttBridgeCache.getInstance().getMqttBridgeByIp(mqttIp);
        if (null != mqttBridge && null != mqttBridge.getBusiMqtt()) {
            String topic = MqttConfigConstant.TOPIC_PREFIX + clientId;
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObj = new JSONObject();
            jsonObject.put(MqttConfigConstant.CLIENTID, clientId);
            jsonObject.put(MqttConfigConstant.ACTION, TerminalTopic.UPDATE_MQTT_CONFIG);

            jsonObj.put(MqttConfigConstant.IP, mqttIp);
            jsonObj.put(MqttConfigConstant.USERNAME, mqttBridge.getBusiMqtt().getUserName());
            jsonObj.put(MqttConfigConstant.PASSWORD, mqttBridge.getBusiMqtt().getPassword());
            jsonObject.put(MqttConfigConstant.JSON_DATA_STR, jsonObj);
            PublisMessage.getInstance().publishTopicMsg(topic, clientId, jsonObject.toString(), false);
        }
    }

    /**
     * 实际ip和租户绑定ip相同，更新
     *
     * @param busiTerminal
     */
    private void updateBusiTerminalData(BusiTerminal busiTerminal) {
        busiTerminal.setMqttOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
        busiTerminal.setUpdateTime(new Date());
        int terminal = busiTerminalService.updateBusiTerminal(busiTerminal);
        if (terminal > 0) {
            TerminalCache.getInstance().put(busiTerminal.getId(), busiTerminal);
        }
    }

//	@Override
//	public Boolean deleteBrokerTerminal(String clientid, String brokerUrl, String userName)
//	{
//		//根据ip从BusiMqtt获取信息
//		Integer mPort = this.byIpGetBusiMqttInfo(brokerUrl);
//		if(null != mPort)
//		{
//			String delUrl = MqttConfigConstant.HTTP + brokerUrl
//							+ MqttConfigConstant.COLON + mPort
//							+ MqttConfigConstant.API_AND_VERSION + "/clients/" + clientid;
//
//			httpRequester.delete(delUrl, new HttpResponseProcessorAdapter() {
//
//				@Override
//				public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
//					String success = getBodyContent(httpResponse);
//                    LOGGER.info(success + "=======> 删除会话成功:{}",httpResponse.getStatusLine().getStatusCode());
//				}
//			});
//
//			//删除mqtt服务节点，会控更新状态
//			this.notifyFcmDealData(clientid, TerminalActionEnum.DEL_BROKER_NODE.value(), userName, brokerUrl);
//			return true;
//		}
//		return false;
//	}


    @Override
    public void bindBrokerNodeAndPubTopic(String clientid, String mqttIp, String userName, String password, String terminalIp) {
        //终端连接mqtt服务器
        Boolean isConnectFlg = this.terminalConnectMqttServer(clientid, mqttIp, userName, password);
        if (isConnectFlg) {
            MqttPublishData publishData = new MqttPublishData();
            publishData.setClientId(clientid);
            publishData.setAction(String.valueOf(TerminalActionEnum.BIND_BROKER_NODE.value()));
            publishData.setIp(mqttIp);
            publishData.setFromTerminal(MqttConfigConstant.FCM_SYSTEM);
            publishData.setToTerminal(clientid);

            String bindTopic = MqttConfigConstant.TOPIC_PREFIX + clientid;
            PublisMessage.getInstance().publishTopicMsg(bindTopic, clientid, publishData.toString(), false);
            this.notifyFcmDealData(clientid, TerminalActionEnum.BIND_BROKER_NODE.value(), userName, mqttIp, terminalIp);//绑定mqtt服务节点，会控更新状态
        }

    }

    public Boolean terminalConnectMqttServer(String clientid, String brokerUrl, String userName, String password) {
        Boolean isConnectFlg = ResponseTerminal.getInstance().connectMqttServer(userName, password, clientid, brokerUrl);
        return isConnectFlg;
    }


//	@Override
//	public void terminalJoinMeetingWay(String clientid, Integer type)
//	{
//		//检测客户端是否连接
//		Boolean isConnectFlg = this.terminalIsConnect();
//		if(!isConnectFlg)
//		{
//			isConnectFlg = this.terminalConnectMqttServer("FCMTERMINAL_FCMSYSTEM", "192.168.1.188:1883", "admin", "P@rad1se");
//		}
//
//		if(isConnectFlg)
//		{
//			if(StringUtils.isNotEmpty(clientid))
//			{
//				String[] clientSplit = clientid.split(MqttConfigConstant.AND_SYMBOL);
//				int length = clientSplit.length;
//				if(length > 1)
//				{
//					for (int i = 0; i < length; i++)
//					{
//						String topic = null;
//						MqttPublishData publishData = new MqttPublishData();
//						publishData.setClientId(clientSplit[i]);
//						publishData.setAction(String.valueOf(type));
//						publishData.setQos(String.valueOf(QosEnum.QOS2.value()));
//						publishData.setFromTerminal(MqttConfigConstant.FCM_SYSTEM);
//						publishData.setToTerminal(clientSplit[i]);
//
//						//邀请入会 (3)
//						if(TerminalActionEnum.JOIN_MEETING.value() == type)
//						{
//							topic = MqttConfigConstant.TOPIC_PREFIX + TerminalActionEnum.JOIN_MEETING.value() + MqttConfigConstant.SLASH + clientSplit[i];
//						}
//
//						//预约会议 (5)
//						if(TerminalActionEnum.SCHEDULE_MEETING.value() == type)
//						{
//							topic = MqttConfigConstant.TOPIC_PREFIX + TerminalActionEnum.SCHEDULE_MEETING.value() + MqttConfigConstant.SLASH + clientSplit[i];
//						}
//
////						this.publishTopicMsg(topic, clientSplit[i], gson.toJson(publishData), false);
//					}
//				}
//			}
//
//		}
//	}

//	@Override
//	public Boolean terminalIsConnect(String clientId) {
//		Boolean isConn = false;
//		List<String> onlineStatus = new ArrayList<String>();
//		BusiTerminal busiTerminal = new BusiTerminal();
//		busiTerminal.setSn(clientId);
//		List<BusiTerminal> busiTerminalList = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
//		if(null != busiTerminalList && busiTerminalList.size() > 0) {
//			Long deptId = busiTerminalList.get(0).getDeptId();
//			BusiMqttDept mqttDept = MqttDeptMappingCache.getInstance().get(deptId);
//			MqttBridge mqttBridge = MqttBridgeCache.getInstance().get(mqttDept.getMqttId());
//			if(null != mqttBridge) {
//				BusiMqtt busiMqtt = mqttBridge.getBusiMqtt();
//				Integer tcpPort = busiMqtt.getManagementPort();
//				String httpUrl = MqttConfigConstant.HTTP + busiMqtt.getIp() + MqttConfigConstant.COLON + tcpPort + MqttConfigConstant.API_AND_VERSION;
//				String connUrl = httpUrl + "/clients/" + clientId;
//
//				httpRequester.get(connUrl, new HttpResponseProcessorAdapter() {
//
//					@Override
//					public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
//						try {
//
//		                    String nodeData = getBodyContent(httpResponse);
//		                    if(StringUtils.isNotEmpty(nodeData))
//		    				{
//
//		    					JSONObject jsonObject = (JSONObject) JSONObject.parse(nodeData);
//		                    	String data = jsonObject.getString(MqttConfigConstant.JSON_DATA_STR);
//		    					JSONArray array = (JSONArray) JSONArray.parse(data);
//		    					if(null != array && array.size() > 0) {
//	    							JSONObject jsonObj = (JSONObject)array.get(0);
//	    							Boolean connect = jsonObj.getBoolean("connected");
//	    							if(connect) {
//	    								onlineStatus.add("1");
//	    							}else {
//	    								onlineStatus.add("2");
//									}
//		    					}
//
//		    				}
//						} catch (Exception e) {
//							throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "连接MQTT服务失败!");
//						}
//					}
//				});
//		}
//
//		if(onlineStatus.size() > 0 && "1".equals(onlineStatus.get(0))) {
//			isConn = true;
//		}
//	}
//
//	return isConn;
//}


    public String currentServerIP() {
        Enumeration<NetworkInterface> nis;
        String ip = null;
        try {
            nis = NetworkInterface.getNetworkInterfaces();
            for (; nis.hasMoreElements(); ) {
                NetworkInterface ni = nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                for (; ias.hasMoreElements(); ) {
                    InetAddress ia = ias.nextElement();
                    if (ia instanceof Inet4Address && !ia.getHostAddress().equals("127.0.0.1")) {
                        ip = ia.getHostAddress();
                        LOGGER.info(" ==============================================> " + ip);
                    }
                }
            }
        } catch (SocketException e) {
            throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "获取应用服务器IP失败!");
        }
        return ip;
    }


    @Override
    public void fcmPublishAgreeLiveAction(String clientid, String isAgree, Integer action) {
        //0、不同意，1、同意
        String liveTopic = null;
        MqttPublishData publishData = new MqttPublishData();
        if (action == TerminalActionEnum.JOIN_LIVE.value()) {
            liveTopic = MqttConfigConstant.TOPIC_PREFIX + TerminalActionEnum.JOIN_LIVE.value() + "/" + clientid;
        }

        if (action == TerminalActionEnum.CONFERENCE_SPEECH.value()) {
            liveTopic = MqttConfigConstant.TOPIC_PREFIX + TerminalActionEnum.CONFERENCE_SPEECH.value() + "/" + clientid;
        }

        publishData.setIsAgree(isAgree);
        publishData.setClientId(clientid);
        publishData.setToTerminal(clientid);
        publishData.setAction(String.valueOf(action));
        publishData.setQos(String.valueOf(QosEnum.QOS2.value()));
        publishData.setFromTerminal(MqttConfigConstant.FCM_SYSTEM);
//		emqClient.publish(liveTopic, gson.toJson(publishData), QosEnum.QOS2, false);
    }


    /**
     * 处理终端申请入会和会议发言
     */
    @Override
    public void terminalLiveLaunchAction(String clientId, String conferenceNum, Integer action) {
        LOGGER.info("fcm根据clientid,conferenceNum和action,查询出直播申请入会和会议发言信息!");
        if (StringUtils.isNotEmpty(clientId)) {
            String[] splitSn = clientId.split(MqttConfigConstant.XOR);
            if (splitSn.length > 1) {
                BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(splitSn[0]);
                if (busiTerminalSn != null) {
                    BusiTerminalAction busiTerminalAction = new BusiTerminalAction();
                    busiTerminalAction.setTerminalSn(splitSn[0]);
                    busiTerminalAction.setConferenceNum(Long.valueOf(conferenceNum));

                    List<BusiTerminalAction> busiTerminalActionList = busiTerminalActionMapper.selectBusiTerminalActionList(busiTerminalAction);
                    if (busiTerminalActionList.size() < 1) {
                        busiTerminalAction.setCreateTime(new Date());
                        busiTerminalAction.setIp(busiTerminalSn.getIp());
                        busiTerminalAction.setTerminalId(busiTerminalSn.getId());
                        busiTerminalAction.setDeptId(busiTerminalSn.getDeptId());
                        busiTerminalAction.setTerminalName(busiTerminalSn.getName());
                        if (action == TerminalActionEnum.JOIN_LIVE.value()) {
                            busiTerminalAction.setActionType(TerminalActionEnum.JOIN_LIVE.value());
                        } else {
                            busiTerminalAction.setActionType(TerminalActionEnum.CONFERENCE_SPEECH.value());
                        }

                        busiTerminalActionMapper.insertBusiTerminalAction(busiTerminalAction);
                    }
                }
            }
        }

        StringBuilder messageTip = new StringBuilder();
        BusiTerminalAction busiTerminalAction = new BusiTerminalAction();
        List<BusiTerminalAction> busiTerminalActionList = busiTerminalActionMapper.selectBusiTerminalActionList(busiTerminalAction);
//		if(null != busiTerminalActionList && busiTerminalActionList.size() > 0)
//		{
//			messageTip.append("终端有"+ busiTerminalActionList.size() + "条消息，需要处理!" );
//			ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(conferenceNum);
//			if(null != conferenceContext)
//			{
//				WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(ConferenceContextCache.getInstance().get(conferenceNum), WebsocketMessageType.MESSAGE_TIP, messageTip);
//			}
//		}
    }

    @Override
    public int addDefaultUserInfo(String userName, String password, String brokerUrl) {
        int successFlg = MqttConfigConstant.ZERO;
        List<Long> arrayList = new ArrayList<Long>();
        Map<String, String> HeadersMap = new HashMap<>();
        HeadersMap.put("Accept", "application/json, text/plain, */*");
        HeadersMap.put("Accept-Encoding", "gzip, deflate");
        HeadersMap.put("Accept-Language", "zh-CN,zh;q=0.9");
        HeadersMap.put("Connection", "keep-alive");
        HeadersMap.put("Content-Type", "application/json;charset=UTF-8");
        HeadersMap.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.5024.400 QQBrowser/10.0.932.400");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MqttConfigConstant.USERNAME, userName);
        jsonObject.put(MqttConfigConstant.PASSWORD, password);
        StringEntity entity = new StringEntity(jsonObject.toString(), Consts.UTF_8);

        //根据ip从BusiMqtt获取信息
        Integer mPort = ResponseTerminal.getInstance().byIpGetBusiMqttInfo(brokerUrl);
        if (null != mPort) {
            arrayList.clear();
            String terminalUrl = MqttConfigConstant.HTTP + brokerUrl
                    + MqttConfigConstant.COLON + mPort
                    + MqttConfigConstant.API_AND_VERSION + "/auth_username";
            httpRequester.post(terminalUrl, HeadersMap, entity, new HttpResponseProcessorAdapter() {

                @Override
                public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                    String success = getBodyContent(httpResponse);
                    arrayList.add((long) httpResponse.getStatusLine().getStatusCode());
                    LOGGER.info(success, "默认用户添加成功！");
                }

                @Override
                public void fail(HttpResponse httpResponse) {
                    LOGGER.info("添加用户失败！" + httpResponse.getStatusLine().getStatusCode());
                }
            });
        }

        if (!arrayList.isEmpty()) {
            successFlg = MqttConfigConstant.SUCCESS;
        }

        //0、添加成功   4、失败
        return successFlg;
    }

    @Override
    public void deleteMqttBrokerTerminal(JSONObject jsonS, String clientId) {
        if (null != jsonS) {
            String mqttIp = (String) jsonS.getString("mqttIp");
            String terminalIp = (String) jsonS.getString("terminalIp");
            String userName = (String) jsonS.getString(MqttConfigConstant.USERNAME);

            //根据ip从BusiMqtt获取信息
            Integer mPort = ResponseTerminal.getInstance().byIpGetBusiMqttInfo(mqttIp);
            if (null != mPort) {
                String delUrl = MqttConfigConstant.HTTP + mqttIp
                        + MqttConfigConstant.COLON + mPort
                        + MqttConfigConstant.API_AND_VERSION + "/clients/" + clientId;

                httpRequester.delete(delUrl, new HttpResponseProcessorAdapter() {

                    @Override
                    public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                        String success = getBodyContent(httpResponse);
                        LOGGER.info(success + "=======> 删除会话成功:{}", httpResponse.getStatusLine().getStatusCode());
                    }
                });

                //删除mqtt服务节点，会控更新状态
                this.notifyFcmDealData(clientId, TerminalActionEnum.OFF_LINE.value(), userName, mqttIp, terminalIp);
            }
        }
    }

    @Override
    public void terminalUpdateSipAccount(JSONObject obj, String clientId) {
        if (null != obj) {
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
            BusiSipAccount busiSipAccount = new BusiSipAccount();
            busiSipAccount.setSn(clientId);

            List<BusiSipAccount> sipAccountList = busiSipAccountMapper.selectBusiSipAccountList(busiSipAccount);
            if (null != sipAccountList && sipAccountList.size() > 0) {
                BusiSipAccount sipAccount = sipAccountList.get(0);
                sipAccount.setUpdateTime(new Date());
                sipAccount.setSipServer((String) obj.get(SipAccountProperty.SIP_SERVER));
                sipAccount.setSipPort((Integer) obj.get(SipAccountProperty.SIP_PORT));
                sipAccount.setSipUserName(Long.valueOf(obj.get(SipAccountProperty.SIP_USER_NAME).toString()));
                sipAccount.setSipPassword((String) obj.get(SipAccountProperty.SIP_PASSWORD));
                sipAccount.setTurnPort((Integer) obj.get(SipAccountProperty.TURN_PORT));
                sipAccount.setTurnServer((String) obj.get(SipAccountProperty.TURN_SERVER));
                sipAccount.setStunServer((String) obj.get(SipAccountProperty.STUN_SERVER));
                sipAccount.setStunPort((Integer) obj.get(SipAccountProperty.STUN_PORT));
                sipAccount.setTurnUserName((String) obj.get(SipAccountProperty.TURN_USER_NAME));
                sipAccount.setTurnPassword((String) obj.get(SipAccountProperty.TURN_PASSWORD));
                busiSipAccount.setProxyServer((String) obj.get(SipAccountProperty.SIP_SERVER) + MqttConfigConstant.COLON + (Integer) obj.get(SipAccountProperty.SIP_PORT));

                int up = busiSipAccountMapper.updateBusiSipAccount(sipAccount);
                if (up > 0) {
                    JSONObject jObj = new JSONObject();
                    String action = TerminalTopic.UPDATE_SIP_ACCOUNT;
                    jObj.put(MqttConfigConstant.ID, sipAccount.getId());
                    this.responseTerminal(terminalTopic, action, jObj, clientId, "");
                }
            }

        }
    }

    @Override
    public void terminalCreateConference(JSONObject jsonS, String clientId, String messageId) {
        String mcuType = ExternalConfigCache.getInstance().getMcuType();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONArray jArray = new JSONArray();
        JSONArray tArray = new JSONArray();

        Long masterTerminalId = null;
        Long deptId = null;
        Integer businessFieldType = null;
        String hostUserId = jsonS.getString(InstantMeetingParam.HOST_USER_ID);
        String length = jsonS.getString(InstantMeetingParam.LENGTH);
        String ifInvite = jsonS.getString(InstantMeetingParam.IF_INVITE);
        String mode = jsonS.getString(InstantMeetingParam.MODE);
        String layout = jsonS.getString(InstantMeetingParam.LAYOUT);
        String password = jsonS.getString(InstantMeetingParam.PASSWORD);
        String ifRecord = jsonS.getString(InstantMeetingParam.IF_RECORD);
        String ifLive = jsonS.getString(InstantMeetingParam.IF_LIVE);
        String ifMute = jsonS.getString(InstantMeetingParam.IF_MUTE);
        String theme = jsonS.getString(InstantMeetingParam.THEME);
        String participantList = jsonS.getString(InstantMeetingParam.PARTICIPANT_LIST);
        String startTime = jsonS.getString(InstantMeetingParam.START_TIME);
        Date startTimeNew = new Date();
        Integer type = null;
        try {
            type = jsonS.getInteger(InstantMeetingParam.TYPE);
        } catch (Exception e) {
        }
        if (type == null) {
            type = 2;
        }
        if (type != 1 && type != 3) {
            type = 2;
        } else {
            try {
                startTimeNew = Timestamp.valueOf(startTime);
            } catch (Exception e) {
            }
        }
        long endTime = startTimeNew.getTime() + Long.valueOf(length) * 1000;

        String action = TerminalTopic.CREATE_CONFERENCE;
        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;

        boolean canCreateConference = false;
        try {
            BusiTerminal busiTerminal = TerminalCache.getInstance().getBySn(clientId);
            if (busiTerminal != null) {
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(busiTerminal.getId());
                if (busiUserTerminal != null) {
                    Long userId = busiUserTerminal.getUserId();
                    if (userId != null) {
                        SysUser sysUser = sysUserService.selectUserById(userId);
                        jsonObject.put("createUserId", sysUser.getUserId());
                        jsonObject.put("createUserName", sysUser.getUserName());
                        jsonObject.put("presenter", sysUser.getUserId());
                        canCreateConference = true;
                    }
                }
            }
        } catch (Exception e) {
        }
        if (!canCreateConference) {
            ResponseTerminal.getInstance().responseTerminal(ResponseInfo.CODE_401, "没有权限创建会议，请联系管理员！", terminalTopic, action, null, clientId, "");
            return;
        }
        BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(clientId);
        if (busiTerminalSn != null) {
            deptId = busiTerminalSn.getDeptId();
            businessFieldType = busiTerminalSn.getBusinessFieldType();
            if (StringUtils.isNotEmpty(hostUserId)) {
                masterTerminalId = busiTerminalSn.getId();
                jsonObject.put(InstantMeetingConversionJson.MASTER_TERMINAL_ID, masterTerminalId);
            }
        }
        String startDate = DateUtil.convertDateToString(startTimeNew, "yyyy-MM-dd HH:mm:ss");
        String endDate = simpleDateFormat.format(new Date(endTime));
        if (type == 3) {
            endDate = "9999-01-01 00:00:00";
            type = 2;
        }
        String[] dateSp = startDate.split(" ");
        String[] endSp = endDate.split(" ");


        jsonObject.put(InstantMeetingConversionJson.DEPT_ID, deptId);
        jsonObject.put(InstantMeetingConversionJson.REPEAT_VALUE, 1);
        if (dateSp.length > 1) {
            jsonArray.add(dateSp[1]);
            jsonObject.put(InstantMeetingConversionJson.DATE, dateSp[0]);
        }

        if (endSp.length > 1) {
            jsonArray.add(endSp[1]);
        }

        jsonObject.put(InstantMeetingConversionJson.TIME, jsonArray);
        jsonObject.put(InstantMeetingConversionJson.IS_AUTO_CREATE_TEMPLATE, 1);
        jsonObject.put(InstantMeetingConversionJson.CONFERENCE_NAME, theme);
        jsonObject.put(InstantMeetingConversionJson.CONFERENCE_PASSWORD, password);
        jsonObject.put(InstantMeetingConversionJson.DEFAULT_VIEW_LAYOUT, layout);
        jsonObject.put(InstantMeetingConversionJson.STATUS, 1);
        if (ifInvite.equals(InstantMeetingParam.TRUE_STR)) {
            jsonObject.put(InstantMeetingConversionJson.IS_AUTO_CALL, 1);
        } else {
            jsonObject.put(InstantMeetingConversionJson.IS_AUTO_CALL, 2);
        }

        if (ifLive.equals(InstantMeetingParam.TRUE_STR)) {
            jsonObject.put(InstantMeetingConversionJson.STREAMING_ENABLED, 1);
        } else {
            jsonObject.put(InstantMeetingConversionJson.STREAMING_ENABLED, 2);
        }
        jsonObject.put(InstantMeetingConversionJson.DEFAULT_VIEW_IS_BROADCAST, 2);
        jsonObject.put(InstantMeetingConversionJson.DEFAULT_VIEW_IS_FILL, 1);
        jsonObject.put(InstantMeetingConversionJson.DEFAULT_VIEW_IS_DISPLAY_SELF, 2);
        jsonObject.put(InstantMeetingConversionJson.BUSINESS_FIELD_TYPE, businessFieldType);
        jsonObject.put("businessProperties", new JSONObject());
        if (ifRecord.equals(InstantMeetingParam.TRUE_STR)) {
            jsonObject.put(InstantMeetingConversionJson.RECORDING_ENABLED, 1);
        } else {
            jsonObject.put(InstantMeetingConversionJson.RECORDING_ENABLED, 2);
        }

//			jsonObject.put("streamUrl", 2);
        JSONArray parseArray = JSONArray.parseArray(participantList);
        if (null != parseArray && parseArray.size() > 0) {
            for (int i = 0; i < parseArray.size(); i++) {
                JSONObject object = (JSONObject) parseArray.get(i);
                Long id = object.getLong(MqttConfigConstant.TERMINAL_ID);
                JSONArray array = new JSONArray();
                String clientId1 = object.getString(MqttConfigConstant.CLIENTID);
                BusiTerminal terminal = new BusiTerminal();
                terminal.setSn(clientId1);
                terminal.setBusinessFieldType(businessFieldType);
//					List<ModelBean> terminalList = busiTerminalService.selectBusiTerminalList(terminal);
                List<BusiTerminal> terminalList = new ArrayList<>();
                if (StringUtils.isNotEmpty(clientId1)) {
                    BusiTerminal terminalBySn = TerminalCache.getInstance().getBySn(clientId1);
                    terminalList.add(terminalBySn);
                }
                if (id != null) {
                    BusiTerminal busiTerminal1 = TerminalCache.getInstance().get(id);
                    terminalList.add(busiTerminal1);
                }
                if (null != terminalList && terminalList.size() > 0) {
                    Long deptIds = (Long) terminalList.get(0).getDeptId();
                    SysDept sysDept = deptService.selectDeptById(deptIds);

                    BusiTerminal terminal2 = terminalList.get(0);
                    Integer attendType = terminal2.getAttendType();
                    array.add(attendType);
                    JSONObject jObj = (JSONObject) JSON.toJSON(terminal2);
                    jObj.put("attendTypeArr", array);
                    jObj.put(InstantMeetingConversionJson.WEIGHT, i + 1);//终端权限，需要查询
                    jObj.put("terminalId", terminal2.getId());
                    if (null != sysDept) {
                        jObj.put("deptName", sysDept.getDeptName());
                    }
                    if (busiTerminalSn.getDeptId() < terminal2.getDeptId()) {
                        JSONObject job = new JSONObject();
                        job.put(InstantMeetingConversionJson.WEIGHT, i + 1);
                        job.put("deptId", terminal2.getDeptId());
                        tArray.add(job);
                    }
                    jArray.add(jObj);
                }
            }

            jsonObject.put(InstantMeetingConversionJson.TEMPLATE_PARTICIPANTS, jArray);
        }
        //终端创建会议生成入会方案
        if (ifMute.equals(InstantMeetingParam.FALSE_STR)) {

            jsonObject.put(InstantMeetingConversionJson.TEMPLATE_DEPTS, tArray);
        }
        Map<String, Object> resultMap = new HashMap<>();

        if (McuType.MCU_ZJ.getCode().equals(mcuType)) {
            BusiMcuZjConferenceAppointment busiMcuZjConferenceAppointment = new BusiMcuZjConferenceAppointment();
            busiMcuZjConferenceAppointment.setDeptId(busiTerminalSn.getDeptId());
            busiMcuZjConferenceAppointment.setStartTime(startDate);

            busiMcuZjConferenceAppointment.setRepeatRate(1);
            busiMcuZjConferenceAppointment.setIsAutoCreateTemplate(1);
            busiMcuZjConferenceAppointment.setEndTime(endDate);
            busiMcuZjConferenceAppointment.setStatus(1);
            busiMcuZjConferenceAppointment.setParams(jsonObject);
            busiMcuZjConferenceAppointment.setType(type);
            LOGGER.info(JSON.toJSONString(busiMcuZjConferenceAppointment));
            resultMap = busiMcuZjConferenceAppointmentService.insertBusiMcuZjConferenceAppointment(busiMcuZjConferenceAppointment);
            LOGGER.info(busiMcuZjConferenceAppointment.getTemplateId().toString());
        } else if (McuType.MCU_PLC.getCode().equals(mcuType)) {
            BusiMcuPlcConferenceAppointment busiMcuPlcConferenceAppointment = new BusiMcuPlcConferenceAppointment();
            busiMcuPlcConferenceAppointment.setDeptId(busiTerminalSn.getDeptId());
            busiMcuPlcConferenceAppointment.setStartTime(startDate);

            busiMcuPlcConferenceAppointment.setRepeatRate(1);
            busiMcuPlcConferenceAppointment.setIsAutoCreateTemplate(1);
            busiMcuPlcConferenceAppointment.setEndTime(endDate);
            busiMcuPlcConferenceAppointment.setStatus(1);
            busiMcuPlcConferenceAppointment.setParams(jsonObject);
            busiMcuPlcConferenceAppointment.setType(type);
            LOGGER.info(JSON.toJSONString(busiMcuPlcConferenceAppointment));
            resultMap = busiMcuPlcConferenceAppointmentService.insertBusiMcuPlcConferenceAppointment(busiMcuPlcConferenceAppointment);
            LOGGER.info(busiMcuPlcConferenceAppointment.getTemplateId().toString());
        } else if (McuType.MCU_KDC.getCode().equals(mcuType)) {
            BusiMcuKdcConferenceAppointment busiMcuKdcConferenceAppointment = new BusiMcuKdcConferenceAppointment();
            busiMcuKdcConferenceAppointment.setDeptId(busiTerminalSn.getDeptId());
            busiMcuKdcConferenceAppointment.setStartTime(startDate);

            busiMcuKdcConferenceAppointment.setRepeatRate(1);
            busiMcuKdcConferenceAppointment.setIsAutoCreateTemplate(1);
            busiMcuKdcConferenceAppointment.setEndTime(endDate);
            busiMcuKdcConferenceAppointment.setStatus(1);
            busiMcuKdcConferenceAppointment.setParams(jsonObject);
            busiMcuKdcConferenceAppointment.setType(type);
            LOGGER.info(JSON.toJSONString(busiMcuKdcConferenceAppointment));
            resultMap = busiMcuKdcConferenceAppointmentService.insertBusiMcuKdcConferenceAppointment(busiMcuKdcConferenceAppointment);
            LOGGER.info(busiMcuKdcConferenceAppointment.getTemplateId().toString());
        } else {
            String callLegProfileId = terminalCreatecallLegProfile(theme, false, layout, busiTerminalSn.getDeptId());
            jsonObject.put(InstantMeetingConversionJson.CALLLEGPROFILE_ID, callLegProfileId);
            BusiConferenceAppointment busiConferenceAppointment = new BusiConferenceAppointment();
            busiConferenceAppointment.setDeptId(busiTerminalSn.getDeptId());
            busiConferenceAppointment.setStartTime(startDate);

            busiConferenceAppointment.setRepeatRate(1);
            busiConferenceAppointment.setIsAutoCreateTemplate(1);
            busiConferenceAppointment.setEndTime(endDate);
            busiConferenceAppointment.setStatus(1);

            busiConferenceAppointment.setParams(jsonObject);
            busiConferenceAppointment.setType(type);
            LOGGER.info(JSON.toJSONString(busiConferenceAppointment));
            resultMap = busiConferenceAppointmentService.insertBusiConferenceAppointment(busiConferenceAppointment);
            LOGGER.info(busiConferenceAppointment.getTemplateId().toString());
        }
        Integer success = 0;
        try {
            success = (Integer) resultMap.get("rows");
        } catch (Exception e) {
        }
        JSONObject jObj = new JSONObject();
        jObj.put(MqttConfigConstant.CLIENTID, clientId);
        if (success > 0) {
            Long conferenceNumber = null;
            Long templateId = null;
            String tenantId = "";
            try {
                conferenceNumber = (Long) resultMap.get("conferenceNumber");
            } catch (Exception e) {

            }
            try {
                templateId = (Long) resultMap.get("templateId");
            } catch (Exception e) {

            }
            try {
                tenantId = (String) resultMap.get("tenantId");
            } catch (Exception e) {

            }
            if (conferenceNumber != null) {
                jObj.put(MqttConfigConstant.CLIENTID, clientId);
                jObj.put(InstantMeetingParam.CONFERENCENUM, tenantId + conferenceNumber);
                jObj.put(InstantMeetingParam.PASSWORD, password);
                ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jObj, clientId, messageId);
                if (type == 2) {
                    if (templateId != null) {
                        if (McuType.MCU_ZJ.getCode().equals(mcuType)) {
                            busiMcuZjConferenceService.startConference(templateId);
                        } else if (McuType.MCU_PLC.getCode().equals(mcuType)) {
                            busiMcuPlcConferenceService.startConference(templateId);
                        } else if (McuType.MCU_KDC.getCode().equals(mcuType)) {
                            busiMcuKdcConferenceService.startConference(templateId);
                        }
                    }
                }
                return;
            }
        }
        ResponseTerminal.getInstance().responseTerminalFailed(terminalTopic, action, jObj, clientId, messageId);
    }


    private void getConferenceNum(JSONArray parseArray, Long templateId, String clientId, String messageId) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
//				try {
//					Thread.sleep(4000);
                BusiTemplateConference templateConference = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(templateId);
                if (null != templateConference) {
                    Long conferenceNumber = templateConference.getConferenceNumber();
                    String password = templateConference.getConferencePassword();
//						String encryptConferenceNum = AesEnsUtils.getAesEncryptor().encryptToHex(String.valueOf(conferenceNumber));
//						ConferenceContext conferenceContext = ConferenceContextCache.getInstance().getByEncryptedConferenceNumber(encryptConferenceNum);
//						System.out.println("====================="  + conferenceContext + "======================");
//						if(null != conferenceContext) {
//							for (int i = 0; i < mqttUserIds.size(); i++) {
//								JSONObject jObj = new JSONObject();
//								JSONObject objs = (JSONObject)parseArray.get(i);
//								TerminalAttendee terminalAttendee = conferenceContext.getTerminalAttendeeMap().get(mqttUserIds.get(i));
//								String clientId = objs.getString(InstantMeetingConversionJson.USER_ID);
//								String action = TerminalTopic.CONFERENCE_DETAILS;
//								String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
//								jObj.put(MqttConfigConstant.CLIENTID, clientId);
//								jObj.put(MqttConfigConstant.PASSWORD, templateConference.getConferencePassword());
//								jObj.put(InstantMeetingParam.CONFERENCENUM,conferenceNumber);
//								jObj.put(InstantMeetingParam.PASSWORD,password);
//								responseTerminal(terminalTopic, action, jObj, clientId, messageId);
//							}


                    if (null != parseArray && parseArray.size() > 0) {
                        for (int i = 0; i < parseArray.size(); i++) {
                            JSONObject jObj = new JSONObject();
                            JSONObject objs = (JSONObject) parseArray.get(i);
                            String clientIdT = objs.getString(InstantMeetingConversionJson.USER_ID);
                            String action = TerminalTopic.CONFERENCE_DETAILS;
                            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
                            jObj.put(MqttConfigConstant.CLIENTID, clientIdT);
                            jObj.put(MqttConfigConstant.PASSWORD, templateConference.getConferencePassword());
                            jObj.put(InstantMeetingParam.CONFERENCENUM, conferenceNumber);
                            jObj.put(InstantMeetingParam.PASSWORD, password);
                            responseTerminal(terminalTopic, action, jObj, clientId, messageId);
//                            if (clientIdT.equals(clientId)) {
//
//                            } else {
//                                responseTerminal(terminalTopic, action, jObj, clientId, "");
//                            }
                        }
                    }

//						}
                }
//				} catch (InterruptedException e) {
//					throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "获取即时会议的会议号失败!");
//				}
            }
        }, "获取会议号线程");

        thread.start();
    }


    /**
     * @param theme
     * @param b
     * @param layout
     * @param deptId 终端创建会议生成入会方案
     * @return
     */
    private String terminalCreatecallLegProfile(String theme, boolean b, String layout, Long deptId) {
        String profileId = null;
        FutureTask<String> futureTask = new FutureTask<>(new Callable<String>() {

            @Override
            public String call() throws Exception {
                String callLegProfileId = terminalCallLegProfile(theme, false, layout, deptId);
                return callLegProfileId;
            }
        });

        new Thread(futureTask, "生成入会方案线程 =======================> callLegProfileId1111").start();
        try {
            profileId = futureTask.get();
        } catch (Exception e) {
            throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "即时会议生成入会方案异常!");
        }
        return profileId;
    }


    private String terminalCallLegProfile(String theme, boolean rxAudioMute, String layout, Long deptId) {
        BusiCallLegProfile busiCallLegProfile = new BusiCallLegProfile();
        Map<String, Object> map = new HashMap<String, Object>();
        busiCallLegProfile.setDeptId(deptId);
        Boolean isProfile = false;
        List<ModelBean> callLegProfiles = busiCallLegProfileService.getAllCallLegProfiles(busiCallLegProfile.getDeptId());
        if (null != callLegProfiles && callLegProfiles.size() > 0) {
            for (ModelBean modelBean : callLegProfiles) {
                String name = (String) modelBean.get("name");
                Boolean txAudioM = (Boolean) modelBean.get("rxAudioMute");
                if (name.contains("即时会议入会方案") && !txAudioM) {
                    isProfile = true;
                    return (String) modelBean.get("id");
                } else {
                    isProfile = false;
                }
            }
        }

        if (!isProfile) {
            map.put(JoinMeetingDefaultParams.NAME, theme + "即时会议入会方案");
            map.put(JoinMeetingDefaultParams.QUALITY_MAIN, "max1080p30");
            map.put(JoinMeetingDefaultParams.QUALITY_PRESENTATION, "max720p5");
            map.put(JoinMeetingDefaultParams.DEPT_ID, deptId);
            map.put(JoinMeetingDefaultParams.TX_AUDIO_MUTE, "");
            map.put(JoinMeetingDefaultParams.DEFAULT_LAYOUT, "automatic");
            map.put(JoinMeetingDefaultParams.PARTICIPANT_COUNTER, "never");
            map.put(JoinMeetingDefaultParams.PARTICIPANT_LABELS, true);
            map.put(JoinMeetingDefaultParams.ALLOW_ALL_PRESENTATION_CONTRIBUTION_ALLOWED, true);
            map.put(JoinMeetingDefaultParams.PRESENTATION_VIEWING_ALLOWED, "");
            map.put(JoinMeetingDefaultParams.SIP_PRESENTATION_CHANNEL_ENABLED, "");
            map.put(JoinMeetingDefaultParams.PRESENTATION_DISPLAY_MODE, "");
            map.put(JoinMeetingDefaultParams.BFCP_MODE, "serverOnly");
            map.put(JoinMeetingDefaultParams.CONTROL_REMOTE_CAMERA_ALLOWED, true);
            map.put(JoinMeetingDefaultParams.AUDIO_GAIN_MODE, "");
            map.put(JoinMeetingDefaultParams.SET_IMPORTANCE_ALLOWED, true);
            map.put(JoinMeetingDefaultParams.SIP_MEDIA_ENCRYPTION, "");
            map.put(JoinMeetingDefaultParams.RX_AUDIO_MUTE, rxAudioMute);
            map.put(JoinMeetingDefaultParams.RX_VIDEO_MUTE, "");
            map.put(JoinMeetingDefaultParams.TX_VIDEO_MUTE, "");
            map.put(JoinMeetingDefaultParams.NEEDS_ACTIVATION, "");
            map.put(JoinMeetingDefaultParams.DEACTIVATION_MODE, "");
            map.put(JoinMeetingDefaultParams.DEACTIVATION_MODE_TIME, "");
            map.put(JoinMeetingDefaultParams.TELEPRESENCE_CALLS_ALLOWED, "");
            map.put(JoinMeetingDefaultParams.CALL_LOCK_ALLOWED, "");
            map.put(JoinMeetingDefaultParams.CHANGE_JOIN_AUDIO_MUTE_OVERRIDE_ALLOWED, "");
            map.put(JoinMeetingDefaultParams.END_CALL_ALLOWED, "");
            map.put(JoinMeetingDefaultParams.DISCONNECT_OTHERS_ALLOWED, "");
            map.put(JoinMeetingDefaultParams.ADD_PARTICIPANT_ALLOWED, "");
            map.put(JoinMeetingDefaultParams.MUTE_OTHERS_ALLOWED, "");
            map.put(JoinMeetingDefaultParams.VIDEO_MUTE_OTHERS_ALLOWED, "");
            map.put(JoinMeetingDefaultParams.MUTE_SELF_ALLOWED, "");
            map.put(JoinMeetingDefaultParams.VIDEO_MUTE_SELF_ALLOWED, "");
            map.put(JoinMeetingDefaultParams.CHANGE_LAYOUT_ALLOWED, "");
            map.put(JoinMeetingDefaultParams.ALLOW_ALL_MUTE_SELF_ALLOWED, "");
            map.put(JoinMeetingDefaultParams.MAX_CALL_DURATION_TIME, "");
            map.put(JoinMeetingDefaultParams.MEETING_TITLE_POSITION, "");

            busiCallLegProfile.setParams(map);
            if (busiCallLegProfileService.insertBusiCallLegProfile(busiCallLegProfile) == 1) {
                return (String) busiCallLegProfile.getParams().get("id");
            }
        }
        return null;
    }

    @Override
    public void modifyTerminalInfo(JSONObject jsonS, String clientId, String messageId) {
        BusiTerminal terminal = this.getTerminalObject(clientId);
        if (null != terminal) {
            String terminalName = jsonS.getString(MqttConfigConstant.NAME);
            if (StringUtils.isNotEmpty(terminalName)) {
                terminal.setName(terminalName);
                terminal.setUpdateTime(new Date());
                int u = busiTerminalService.updateBusiTerminal(terminal);
                if (u > 0) {
                    JSONObject jObj = new JSONObject();
                    String action = TerminalTopic.MODIFY_TERMINAL_INFO;
                    String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
                    jObj.put(MqttConfigConstant.ID, terminal.getId());
                    this.responseTerminal(terminalTopic, action, jObj, clientId, messageId);
                }
            }
        }
    }


    @Override
    public void terminalPresentationOpenOrClose(JSONObject jsonS, String clientId, String messageId, String action) {
        List<BaseFixedParamValue> params = new ArrayList<>();
        //会控处理，终端打开双流
        if (null != jsonS) {
            String conferenceNum = jsonS.getString(InstantMeetingParam.CONFERENCENUM);
            String conferenceId = AesEnsUtils.getAesEncryptor().encryptToHex(conferenceNum);
            JSONArray objArr = jsonS.getJSONArray("presentationStatus");
            String sn = jsonS.getString(MqttConfigConstant.CLIENTID);
            if (null != objArr) {
                for (int i = 0; i < objArr.size(); i++) {
                    BaseFixedParamValue fixedParamValue = new BaseFixedParamValue();
                    JSONObject obj = (JSONObject) objArr.get(i);
                    String name = obj.getString("name");
                    Boolean fixed = obj.getBoolean("fixed");
                    fixedParamValue.setName(name);
                    fixedParamValue.setFixed(fixed);
                    params.add(fixedParamValue);
                }

                //根据sn序列号，获取终端的具体信息
                BusiTerminal terminal = this.getTerminalObject(sn);
                if (null != terminal) {
                    Long terminalId = terminal.getId();

                    BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
                    BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
                    if (null != conferenceContext) {
                        BaseAttendee attendee = conferenceContext.getAttendeeByTerminalId(terminalId);
                        if (attendee != null) {
                            String attendeeId = attendee.getId();

                            //移除与会者
                            if (conferenceContext instanceof ConferenceContext) {
                                attendeeService.presentationSetting(conferenceId, attendeeId, params);
                            } else if (conferenceContext instanceof McuZjConferenceContext) {
                                attendeeForMcuZjService.presentationSetting(conferenceContext.getId(), attendeeId, params);
                            } else if (conferenceContext instanceof McuPlcConferenceContext) {
                                attendeeForMcuPlcService.presentationSetting(conferenceContext.getId(), attendeeId, params);
                            } else if (conferenceContext instanceof McuKdcConferenceContext) {
                                attendeeForMcuKdcService.presentationSetting(conferenceContext.getId(), attendeeId, params);
                            }

                            JSONObject jObj = new JSONObject();
                            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
                            jObj.put(MqttConfigConstant.CONFERENCENUM, conferenceNum);
                            if (action.equals(TerminalTopic.OPEN_PRESENTATION)) {
                                jObj.put("presentation", true);
                            } else {
                                jObj.put("presentation", false);
                            }

                            this.responseTerminal(terminalTopic, action, jObj, clientId, messageId);
                        }
                    }
                }
            }
        }
    }

    private BusiTerminal getTerminalObject(String clientId) {
        BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(clientId);
        return busiTerminalSn;
    }


    @Override
    public void terminalSysInfo(JSONObject jsonS, String clientId, String messageId) {
        if (null != jsonS) {
            BusiTerminalSysInfo busiTerminalSysInfo = new BusiTerminalSysInfo();
            busiTerminalSysInfo.setSn(clientId);
            if (StringUtils.isNotEmpty(clientId)) {
                List<BusiTerminalSysInfo> terminalSysInfoList = busiTerminalSysInfoService.selectBusiTerminalSysInfoList(busiTerminalSysInfo);
                if (null != terminalSysInfoList && terminalSysInfoList.size() > 0) {
                    BusiTerminalSysInfo terminalSysInfo = terminalSysInfoList.get(0);
                    terminalSysInfo.setUpdateTime(new Date());
                    terminalSysInfo.setMacAddr((String) jsonS.getString(TerminalSysInfoParams.MAC_ADDR));
                    terminalSysInfo.setArmVersion((String) jsonS.getString(TerminalSysInfoParams.ARM_VERSION));
                    terminalSysInfo.setFpgaVersion((String) jsonS.getString(TerminalSysInfoParams.FPGA_VERSION));
                    terminalSysInfo.setBootVersion((String) jsonS.getString(TerminalSysInfoParams.BOOT_VERSION));
                    terminalSysInfo.setTerminalType((String) jsonS.getString(TerminalSysInfoParams.TERMINAL_TYPE));
                    terminalSysInfo.setSystemVersion((String) jsonS.getString(TerminalSysInfoParams.SYSTEM_VERSION));
                    busiTerminalSysInfoService.updateBusiTerminalSysInfo(terminalSysInfo);
                } else {
                    busiTerminalSysInfo.setCreateTime(new Date());
                    busiTerminalSysInfo.setMacAddr((String) jsonS.getString(TerminalSysInfoParams.MAC_ADDR));
                    busiTerminalSysInfo.setArmVersion((String) jsonS.getString(TerminalSysInfoParams.ARM_VERSION));
                    busiTerminalSysInfo.setFpgaVersion((String) jsonS.getString(TerminalSysInfoParams.FPGA_VERSION));
                    busiTerminalSysInfo.setBootVersion((String) jsonS.getString(TerminalSysInfoParams.BOOT_VERSION));
                    busiTerminalSysInfo.setTerminalType((String) jsonS.getString(TerminalSysInfoParams.TERMINAL_TYPE));
                    busiTerminalSysInfo.setSystemVersion((String) jsonS.getString(TerminalSysInfoParams.SYSTEM_VERSION));
                    busiTerminalSysInfoService.insertBusiTerminalSysInfo(busiTerminalSysInfo);
                }

                BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(clientId);
                if (busiTerminalSn != null) {
                    String terminalName = jsonS.getString(TerminalSysInfoParams.TERMINAL_NAME);
                    if (StringUtils.isNotEmpty(terminalName)) {
                        busiTerminalSn.setName(terminalName);
                    }
                    busiTerminalSn.setUpdateTime(new Date());
                    int t = busiTerminalService.updateBusiTerminal(busiTerminalSn);
                    if (t > 0) {
                        JSONObject jObj = new JSONObject();
                        String action = TerminalTopic.TERMINAL_SYS_INFO;
                        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
                        jObj.put(MqttConfigConstant.ID, busiTerminalSn.getId());
                        this.responseTerminal(terminalTopic, action, jObj, clientId, messageId);
                    }
                }
            }
        }
    }


    @Override
    public void hostEndConference(JSONObject jsonS, String clientId, String messageId) {
        String conferenceNum = jsonS.getString(InstantMeetingParam.CONFERENCENUM);
        if (!canControlConference(clientId, conferenceNum)) {
            String action = TerminalTopic.CREATE_CONFERENCE;
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
            ResponseTerminal.getInstance().responseTerminal(ResponseInfo.CODE_401, "没有操作权限！", terminalTopic, action, null, clientId, "");
            return;
        }

        BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
        BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
        if (null != conferenceContext) {
            String conferenceId = conferenceContext.getId();
            if (conferenceContext instanceof ConferenceContext) {
                busiConferenceService.endConference(conferenceId, ConferenceEndType.COMMON.getValue(), EndReasonsType.ADMINISTRATOR_HANGS_UP);
            } else if (conferenceContext instanceof McuZjConferenceContext) {
                busiMcuZjConferenceService.endConference(conferenceContext.getId());
            } else if (conferenceContext instanceof McuPlcConferenceContext) {
                busiMcuPlcConferenceService.endConference(conferenceContext.getId());
            } else if (conferenceContext instanceof McuKdcConferenceContext) {
                busiMcuKdcConferenceService.endConference(conferenceContext.getId());
            }
        }

    }

    @Override
    public void hostKickParticipant(JSONObject jsonS, String clientId, String messageId) {
        //根据会议号和终端id，得到与会者id
        String sn = jsonS.getString(MqttConfigConstant.CLIENTID);
        String attendeeId = jsonS.getString(MqttConfigConstant.ATTENDEE_ID);
        String conferenceNum = jsonS.getString(InstantMeetingParam.CONFERENCENUM);
        if (!canControlConference(clientId, conferenceNum)) {
            String action = TerminalTopic.CREATE_CONFERENCE;
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
            ResponseTerminal.getInstance().responseTerminal(ResponseInfo.CODE_401, "没有操作权限！", terminalTopic, action, null, clientId, "");
            return;
        }
        BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
        BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
        if (null != conferenceContext) {
            if (StringUtils.isNotEmpty(attendeeId)) {
            } else if (StringUtils.isNotEmpty(sn)) {
                BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(sn);
                BaseAttendee attendee = conferenceContext.getAttendeeByTerminalId(busiTerminalSn.getId());
                if (attendee != null) {
                    attendeeId = attendee.getId();
                }
            }
            if (StringUtils.isNotEmpty(attendeeId)) {
                //移除与会者
                if (conferenceContext instanceof ConferenceContext) {
                    attendeeService.remove(conferenceContext.getId(), attendeeId);
                } else if (conferenceContext instanceof McuZjConferenceContext) {
                    attendeeForMcuZjService.remove(conferenceContext.getId(), attendeeId);
                } else if (conferenceContext instanceof McuPlcConferenceContext) {
                    attendeeForMcuPlcService.remove(conferenceContext.getId(), attendeeId);
                } else if (conferenceContext instanceof McuKdcConferenceContext) {
                    attendeeForMcuKdcService.remove(conferenceContext.getId(), attendeeId);
                }

                //点名终端，给所有参会者发信息
                String action = TerminalTopic.KICK_PARTICIPANT;
//                    this.sendInfoToAllParticipant(conferenceContext.getId(), action, clientId, messageId, kickSn);
            }
        }
    }

    /**
     * 给所有参会者发信息
     *
     * @param conferenceId
     * @param action
     * @param clientId
     * @param kickSn
     */
    private void sendInfoToAllParticipant(String conferenceId, String action, String clientId, String messageId, String kickSn) {
        sendInfoToAllParticipant(conferenceId, action, clientId, messageId, kickSn, false);
    }

    /**
     * 给所有参会者发信息
     *
     * @param conferenceId
     * @param action
     * @param clientId
     * @param kickSn
     */
    private void sendInfoToAllParticipant(String conferenceId, String action, String clientId, String messageId, String kickSn, boolean isNew) {
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().getByConferenceId(conferenceId);
        Map<Long, TerminalAttendee> attendeeMap = conferenceContext.getTerminalAttendeeMap();

        LOGGER.info("================================>$$$" + conferenceContext.toString());
        LOGGER.info("================================>### attendeeMap" + attendeeMap.toString());
        if (isNew) {
            //会议订阅（新）
            JSONObject jObj = new JSONObject();
            String topic = MqttConfigConstant.TOPIC_PREFIX_CONFERENCE + conferenceId;
            jObj.put(MqttConfigConstant.CONFERENCE_ID, conferenceId);
            jObj.put(MqttConfigConstant.CONFERENCENUM, conferenceContext.getConferenceNumber());
            jObj.put("hostClientId", clientId);

            if (action.equals(TerminalTopic.CHANGE_HOST)) {
                jObj.put("hostClientId", clientId);
            }

            if (action.equals(TerminalTopic.KICK_PARTICIPANT) && StringUtils.isNotEmpty(kickSn)) {
                jObj.put("kickClinetId", kickSn);
            }
            ResponseTerminal.getInstance().responseTerminalSuccess(topic, action, jObj, "", "");
        }
        if (!attendeeMap.isEmpty()) {
            attendeeMap.forEach((k, v) -> {
                Long terminalId = (Long) k;
                BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
                if (null != busiTerminal) {
                    String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + busiTerminal.getSn();
                    JSONObject jObj = new JSONObject();
                    jObj.put(MqttConfigConstant.ID, busiTerminal.getId());
                    jObj.put(MqttConfigConstant.NAME, busiTerminal.getName());
                    jObj.put(MqttConfigConstant.CLIENTID, busiTerminal.getSn());
                    jObj.put("hostClientId", clientId);

                    if (action.equals(TerminalTopic.CHANGE_HOST)) {
                        jObj.put("hostClientId", clientId);
                    }

                    if (action.equals(TerminalTopic.KICK_PARTICIPANT) && StringUtils.isNotEmpty(kickSn)) {
                        jObj.put("kickClinetId", kickSn);
                    }

                    //给消息发布者发送messageId
                    this.messagePublisherTomessageId(clientId, busiTerminal.getSn(), messageId, terminalTopic, action, jObj);
                }
            });
        }
    }

    /**
     * 封装信息，给终端发送对应的响应信息
     *
     * @param terminalTopic
     * @param action
     * @param jObj
     * @param clientId
     * @param messageId
     */
    public void responseTerminal(String terminalTopic, String action, JSONObject jObj, String clientId, String messageId) {
        JSONObject jObject = new JSONObject();
        jObject.put(MqttConfigConstant.CODE, ResponseInfo.CODE_200);
        jObject.put(MqttConfigConstant.MSG, ResponseInfo.SUCCESS);
        jObject.put(MqttConfigConstant.ACTION, action);
        jObject.put(MqttConfigConstant.MESSAGE_ID, messageId);
        jObject.put(MqttConfigConstant.JSON_DATA_STR, jObj);
        PublisMessage.getInstance().publishTopicMsg(terminalTopic, clientId, jObject.toString(), false);
    }

    public void responseTerminalFail(String terminalTopic, String action, JSONObject jObj, String clientId, String messageId, Integer code) {
        JSONObject jObject = new JSONObject();
        jObject.put(MqttConfigConstant.CODE, code);
        jObject.put(MqttConfigConstant.MSG, ResponseInfo.SUCCESS);
        jObject.put(MqttConfigConstant.ACTION, action);
        jObject.put(MqttConfigConstant.MESSAGE_ID, messageId);
        jObject.put(MqttConfigConstant.JSON_DATA_STR, jObj);
        PublisMessage.getInstance().publishTopicMsg(terminalTopic, clientId, jObject.toString(), false);
    }

    public void responseTerminalByQOS(String terminalTopic, String action, JSONObject jObj, String messageId, QosEnum qos) {
        JSONObject jObject = new JSONObject();
        jObject.put(MqttConfigConstant.CODE, ResponseInfo.CODE_200);
        jObject.put(MqttConfigConstant.MSG, ResponseInfo.SUCCESS);
        jObject.put(MqttConfigConstant.ACTION, action);
        jObject.put(MqttConfigConstant.MESSAGE_ID, messageId);
        jObject.put(MqttConfigConstant.JSON_DATA_STR, jObj);
        if (StringUtils.isNotEmpty(terminalTopic)) {
            String[] topicArr = terminalTopic.split(MqttConfigConstant.SLASH);
            if (topicArr.length >= 1) {
                try {
                    EmqClient emqClient = (EmqClient) SpringContextUtil.getBean("emqClient");
                    if (null != emqClient) {
                        emqClient.publish(terminalTopic, jObject.toString(), qos, false);
                    }
                } catch (Exception e) {
                    LOGGER.error("发布主题失败!", e);
                }
            }
        }
    }

    @Override
    public void hostRollCall(JSONObject jsonS, String clientId, String messageId) {
        String sn = jsonS.getString(MqttConfigConstant.CLIENTID);
        String conferenceNum = jsonS.getString(InstantMeetingParam.CONFERENCENUM);
        String attendeeId = jsonS.getString(MqttConfigConstant.ATTENDEE_ID);
        String quickRollCallCommand = jsonS.getString("quickRollCallCommand");
        String action = TerminalTopic.ROLL_CALL;
        BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
        BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
        if (conferenceContext != null) {
            if (StringUtils.isNotEmpty(quickRollCallCommand)) {
                String VenueNum = quickRollCallCommand.substring(2, quickRollCallCommand.length());
                if (StringUtils.isNotEmpty(VenueNum)) {
                    LOGGER.info("=======================================> 快捷点会场点名=== 会议号" + conferenceNum);
                    LOGGER.info("=======================================> 快捷点会场点名=== 会议编号" + Integer.valueOf(VenueNum));
                    RestResponse restResponse = null;
                    if (conferenceContext instanceof ConferenceContext) {
                        restResponse = videoConferenceSDKService.callTheRoll((ConferenceContext) conferenceContext, Integer.valueOf(VenueNum));
                    } else if (conferenceContext instanceof McuZjConferenceContext) {
                        McuZjConferenceContext mcuZjConferenceContext = (McuZjConferenceContext) conferenceContext;
                        AtomicInteger ai = new AtomicInteger();
                        GenericValue<AttendeeForMcuZj> val = new GenericValue<>();
                        McuZjConferenceContextUtils.eachNonMcuAttendeeInConference(mcuZjConferenceContext, (a) -> {
                            if (a.isMeetingJoined() && ai.incrementAndGet() == Integer.valueOf(VenueNum)) {
                                val.setValue(a);
                            }
                        });
                        simpleConferenceControlForMcuZjService.rollCall((McuZjConferenceContext) conferenceContext, val.getValue().getId());
                    } else if (conferenceContext instanceof McuPlcConferenceContext) {
                        McuPlcConferenceContext mcuPlcConferenceContext = (McuPlcConferenceContext) conferenceContext;
                        AtomicInteger ai = new AtomicInteger();
                        GenericValue<AttendeeForMcuPlc> val = new GenericValue<>();
                        McuPlcConferenceContextUtils.eachNonMcuAttendeeInConference(mcuPlcConferenceContext, (a) -> {
                            if (a.isMeetingJoined() && ai.incrementAndGet() == Integer.valueOf(VenueNum)) {
                                val.setValue(a);
                            }
                        });
                        simpleConferenceControlForMcuPlcService.rollCall((McuPlcConferenceContext) conferenceContext, val.getValue().getId());
                    } else if (conferenceContext instanceof McuKdcConferenceContext) {
                        McuKdcConferenceContext mcuKdcConferenceContext = (McuKdcConferenceContext) conferenceContext;
                        AtomicInteger ai = new AtomicInteger();
                        GenericValue<AttendeeForMcuKdc> val = new GenericValue<>();
                        McuKdcConferenceContextUtils.eachNonMcuAttendeeInConference(mcuKdcConferenceContext, (a) -> {
                            if (a.isMeetingJoined() && ai.incrementAndGet() == Integer.valueOf(VenueNum)) {
                                val.setValue(a);
                            }
                        });
                        simpleConferenceControlForMcuKdcService.rollCall((McuKdcConferenceContext) conferenceContext, val.getValue().getId());
                    }
                    if (restResponse.isSuccess()) {
                        LOGGER.info("=======================================> 快捷键会场点名成功" + restResponse.isSuccess());

                        //点名终端，给所有参会者发信息
//                        this.sendInfoToAllParticipant(conferenceContext.getId(), action, clientId, messageId, "");
                    }
                }
            } else {
                if (StringUtils.isNotEmpty(attendeeId)) {
                    BaseAttendee attendee = conferenceContext.getAttendeeById(attendeeId);
                    if (attendee != null && attendee.isMeetingJoined()) {
                    } else {
                        attendeeId = null;
                    }
                } else if (StringUtils.isNotEmpty(sn)) {
                    BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(sn);
                    if (busiTerminalSn != null) {
                        Long terminalId = busiTerminalSn.getId();
                        if (conferenceContext != null) {
                            BaseAttendee attendee = conferenceContext.getAttendeeByTerminalId(terminalId);
                            if (attendee != null && attendee.isMeetingJoined()) {
                                attendeeId = attendee.getId();
                            }
                        }
                    }
                }
                if (StringUtils.isNotEmpty(attendeeId)) {
                    //主持人点名
                    if (conferenceContext instanceof ConferenceContext) {
                        attendeeService.callTheRoll(conferenceContext.getId(), attendeeId);
                    } else if (conferenceContext instanceof McuZjConferenceContext) {
                        attendeeForMcuZjService.callTheRoll(conferenceContext.getId(), attendeeId);
                    } else if (conferenceContext instanceof McuPlcConferenceContext) {
                        attendeeForMcuPlcService.callTheRoll(conferenceContext.getId(), attendeeId);
                    } else if (conferenceContext instanceof McuKdcConferenceContext) {
                        attendeeForMcuKdcService.callTheRoll(conferenceContext.getId(), attendeeId);
                    }
                    //点名终端，给所有参会者发信息
//                  this.sendInfoToAllParticipant(conferenceContext.getId(), action, clientId, messageId, "");
                }
            }
        }
    }

    @Override
    public void hostOpenOrCloseMixing(JSONObject jsonS, String clientId, String action, String messageId) {
        //根据会议号和终端id，得到与会者id
        String sn = jsonS.getString(MqttConfigConstant.CLIENTID);
        String attendeeId = jsonS.getString(MqttConfigConstant.ATTENDEE_ID);
        String conferenceNum = jsonS.getString(InstantMeetingParam.CONFERENCENUM);
        if (StringUtils.isNotEmpty(sn) || StringUtils.isNotEmpty(attendeeId)) {
            BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
            BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
            if (conferenceContext != null) {
                if (StringUtils.isNotEmpty(attendeeId)) {
                } else if (StringUtils.isNotEmpty(sn)) {
                    BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(sn);
                    BaseAttendee attendee = conferenceContext.getAttendeeByTerminalId(busiTerminalSn.getId());
                    if (attendee != null) {
                        attendeeId = attendee.getId();
                    }
                }
                if (StringUtils.isNotEmpty(attendeeId)) {
                    if (conferenceContext instanceof ConferenceContext) {
                        if (action.equals(TerminalTopic.OPEN_MIXING)) {

                            //主持人对终端开麦
                            attendeeService.openMixing(conferenceContext.getId(), attendeeId);
                        } else {

                            //主持人对终端关麦
                            attendeeService.closeMixing(conferenceContext.getId(), attendeeId);
                        }
                    } else if (conferenceContext instanceof McuZjConferenceContext) {
                        if (action.equals(TerminalTopic.OPEN_MIXING)) {

                            //主持人对终端开麦
                            attendeeForMcuZjService.openMixing(conferenceContext.getId(), attendeeId);
                        } else {

                            //主持人对终端关麦
                            attendeeForMcuZjService.closeMixing(conferenceContext.getId(), attendeeId);
                        }
                    } else if (conferenceContext instanceof McuPlcConferenceContext) {
                        if (action.equals(TerminalTopic.OPEN_MIXING)) {

                            //主持人对终端开麦
                            attendeeForMcuPlcService.openMixing(conferenceContext.getId(), attendeeId);
                        } else {

                            //主持人对终端关麦
                            attendeeForMcuPlcService.closeMixing(conferenceContext.getId(), attendeeId);
                        }
                    } else if (conferenceContext instanceof McuKdcConferenceContext) {
                        if (action.equals(TerminalTopic.OPEN_MIXING)) {

                            //主持人对终端开麦
                            attendeeForMcuKdcService.openMixing(conferenceContext.getId(), attendeeId);
                        } else {

                            //主持人对终端关麦
                            attendeeForMcuKdcService.closeMixing(conferenceContext.getId(), attendeeId);
                        }
                    }
                    //开音，给所有参会者发信息
//                    this.sendInfoToAllParticipant(conferenceContext.getId(), action, clientId, messageId, "");
                }
            }
        }
    }

    @Override
    public void hostExtendMinutes(JSONObject jsonS, String clientId, String messageId) {
        if (null != jsonS) {
            String conferenceNum = jsonS.getString(InstantMeetingParam.CONFERENCENUM);
            Integer minutes = jsonS.getInteger(InstantMeetingParam.MINUTES);
            BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
            BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
            if (null != conferenceContext) {
                String conferenceId = conferenceContext.getId();
                String action = TerminalTopic.EXTEND_MINUTES;
                String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
                try {
                    if (conferenceContext instanceof ConferenceContext) {
                        busiConferenceService.extendMinutes(conferenceId, minutes);
                    } else if (conferenceContext instanceof McuZjConferenceContext) {
                        busiMcuZjConferenceService.extendMinutes(conferenceId, minutes);
                    } else if (conferenceContext instanceof McuPlcConferenceContext) {
                        busiMcuPlcConferenceService.extendMinutes(conferenceId, minutes);
                    } else if (conferenceContext instanceof McuKdcConferenceContext) {
                        busiMcuKdcConferenceService.extendMinutes(conferenceId, minutes);
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(InstantMeetingParam.CONFERENCENUM, conferenceNum);
                    jsonObject.put(InstantMeetingParam.MINUTES, minutes);
                    this.responseTerminal(terminalTopic, action, jsonObject, clientId, messageId);
                } catch (Exception e) {
                    this.responseTerminalFail(terminalTopic, action, null, clientId, messageId, ResponseInfo.CODE_500);
                }
            }
        }
    }

    @Override
    public void hostOpenOrCloseConferenceMixing(JSONObject jsonS, String clientId, String messageId) {
        Boolean isOpen = jsonS.getBoolean(InstantMeetingParam.IS_OPEN);
        String conferenceNum = jsonS.getString(InstantMeetingParam.CONFERENCENUM);
        BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
        BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
        if (null != conferenceContext) {
            String conferenceId = conferenceContext.getId();
            if (conferenceContext instanceof ConferenceContext) {
                if (isOpen) {
                    attendeeService.openMixing(conferenceId);
                } else {
                    attendeeService.closeMixing(conferenceId);
                }
            } else if (conferenceContext instanceof McuZjConferenceContext) {
                if (isOpen) {
                    attendeeForMcuZjService.openMixing(conferenceId);
                } else {
                    attendeeForMcuZjService.closeMixing(conferenceId);
                }
            } else if (conferenceContext instanceof McuPlcConferenceContext) {
                if (isOpen) {
                    attendeeForMcuPlcService.openMixing(conferenceId);
                } else {
                    attendeeForMcuPlcService.closeMixing(conferenceId);
                }
            } else if (conferenceContext instanceof McuKdcConferenceContext) {
                if (isOpen) {
                    attendeeForMcuKdcService.openMixing(conferenceId);
                } else {
                    attendeeForMcuKdcService.closeMixing(conferenceId);
                }
            }
        }
    }

    @Override
    public void hostReinviteTerminal(JSONObject jsonS, String clientId, String action, String messageId) {
        if (null != jsonS) {
            List<Long> terminalIdLists = new ArrayList<Long>();
            BusiTerminal busiTerminal = TerminalCache.getInstance().getBySn(clientId);
            JSONObject jObj = new JSONObject();
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + busiTerminal.getSn();
            jObj.put(MqttConfigConstant.ID, busiTerminal.getId());
            jObj.put(MqttConfigConstant.NAME, busiTerminal.getName());
            jObj.put(MqttConfigConstant.CLIENTID, busiTerminal.getSn());

            //根据会议号和终端id，得到与会者id
            JSONArray jsonArray = jsonS.getJSONArray(InstantMeetingParam.PARTICIPANTS);
            String conferenceNum = jsonS.getString(InstantMeetingParam.CONFERENCENUM);
            boolean canControlConference = canControlConference(clientId, conferenceNum);
            if (!canControlConference) {
                ResponseTerminal.getInstance().responseTerminal(ResponseInfo.CODE_401, "没有操作权限！", terminalTopic, action, null, clientId, "");
                return;
            }
            BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
            BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
            if (conferenceContext != null) {
                if (null != jsonArray && jsonArray.size() > 0) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        String sn = jsonArray.get(i).toString();
                        BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(sn);
                        if (busiTerminalSn != null) {
                            terminalIdLists.add(busiTerminal.getId());
                        }
                    }
                }
                //主持人重新邀请终端
                if (conferenceContext instanceof ConferenceContext) {
                    attendeeService.invite(conferenceContext.getId(), terminalIdLists);
                } else if (conferenceContext instanceof McuZjConferenceContext) {
                    attendeeForMcuZjService.invite(conferenceContext.getId(), terminalIdLists);
                } else if (conferenceContext instanceof McuPlcConferenceContext) {
                    attendeeForMcuPlcService.invite(conferenceContext.getId(), terminalIdLists);
                } else if (conferenceContext instanceof McuKdcConferenceContext) {
                    attendeeForMcuKdcService.invite(conferenceContext.getId(), terminalIdLists);
                }
            }

            //给消息发布者发送messageId
            this.messagePublisherTomessageId(clientId, busiTerminal.getSn(), messageId, terminalTopic, action, jObj);
        }
    }

    @Override
    public void hostChange(JSONObject jsonS, String clientId, String messageId) {
        //根据会议号和终端id，得到与会者id
        String sn = jsonS.getString(MqttConfigConstant.CLIENTID);
        String attendeeId = jsonS.getString(MqttConfigConstant.ATTENDEE_ID);
        String conferenceNum = jsonS.getString(InstantMeetingParam.CONFERENCENUM);
        if (StringUtils.isNotEmpty(sn) || StringUtils.isNotEmpty(attendeeId)) {
            BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
            BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
            if (conferenceContext != null) {
                if (StringUtils.isNotEmpty(attendeeId)) {
                } else if (StringUtils.isNotEmpty(sn)) {
                    BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(sn);
                    BaseAttendee attendee = conferenceContext.getAttendeeByTerminalId(busiTerminalSn.getId());
                    if (attendee != null) {
                        attendeeId = attendee.getId();
                    }
                }
                if (StringUtils.isNotEmpty(attendeeId)) {
                    //主持人转换
                    if (conferenceContext instanceof ConferenceContext) {
                        attendeeService.changeMaster(conferenceContext.getId(), attendeeId);
                    } else if (conferenceContext instanceof McuZjConferenceContext) {
                        attendeeForMcuZjService.changeMaster(conferenceContext.getId(), attendeeId);
                    } else if (conferenceContext instanceof McuPlcConferenceContext) {
                        attendeeForMcuPlcService.changeMaster(conferenceContext.getId(), attendeeId);
                    } else if (conferenceContext instanceof McuKdcConferenceContext) {
                        attendeeForMcuKdcService.changeMaster(conferenceContext.getId(), attendeeId);
                    }
                }
            }
        }
    }

    @Override
    public void recordConference(JSONObject jsonS, String clientId, String messageId) {
        String conferenceNum = jsonS.getString(InstantMeetingParam.CONFERENCENUM);
        Boolean recording = jsonS.getBoolean(InstantMeetingParam.RECORDING_STATUS);

        if (!canControlConference(clientId, conferenceNum)) {
            String action = TerminalTopic.CREATE_CONFERENCE;
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
            ResponseTerminal.getInstance().responseTerminal(ResponseInfo.CODE_401, "没有操作权限！", terminalTopic, action, null, clientId, "");
            return;
        }
        //开启和关闭录制
        String action = TerminalTopic.RECORD_CONFERENCE;
        BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
        BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
        if (null != conferenceContext) {
            int updateBusiRecords = 0;
            if (conferenceContext instanceof ConferenceContext) {
                updateBusiRecords = busiRecordsService.updateBusiRecords(recording, conferenceNum);
            } else if (conferenceContext instanceof McuZjConferenceContext) {
                updateBusiRecords = busiRecordsForMcuZjService.updateBusiRecords(recording, conferenceContext.getContextKey());
            } else if (conferenceContext instanceof McuPlcConferenceContext) {
                updateBusiRecords = busiRecordsForMcuPlcService.updateBusiRecords(recording, conferenceContext.getContextKey());
            } else if (conferenceContext instanceof McuKdcConferenceContext) {
                updateBusiRecords = busiRecordsForMcuKdcService.updateBusiRecords(recording, conferenceContext.getContextKey());
            }
            if (updateBusiRecords > 0) {
                String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
                JSONObject jObj = new JSONObject();
                jObj.put(InstantMeetingParam.CONFERENCENUM, conferenceNum);
                jObj.put(InstantMeetingParam.RECORDING_STATUS, recording);

                //给消息发布者发送messageId
                this.responseTerminal(terminalTopic, action, jObj, clientId, messageId);
            }
        }
    }

    @Override
    public void lockConference(JSONObject jsonS, String clientId, String messageId) {
        String conferenceNum = jsonS.getString(InstantMeetingParam.CONFERENCENUM);
        Boolean lockStatus = jsonS.getBoolean(InstantMeetingParam.LOCK_STATUS);
        if (!canControlConference(clientId, conferenceNum)) {
            String action = TerminalTopic.CREATE_CONFERENCE;
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
            ResponseTerminal.getInstance().responseTerminal(ResponseInfo.CODE_401, "没有操作权限！", terminalTopic, action, null, clientId, "");
            return;
        }
        BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
        BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
        //锁定会议室
        if (conferenceContext != null) {
            if (conferenceContext instanceof ConferenceContext) {
                busiConferenceService.lock(conferenceContext.getId(), lockStatus);
            } else if (conferenceContext instanceof McuZjConferenceContext) {
                busiMcuZjConferenceService.lock(conferenceContext.getId(), lockStatus);
            } else if (conferenceContext instanceof McuPlcConferenceContext) {
                busiMcuPlcConferenceService.lock(conferenceContext.getId(), lockStatus);
            } else if (conferenceContext instanceof McuKdcConferenceContext) {
                busiMcuKdcConferenceService.lock(conferenceContext.getId(), lockStatus);
            }
            String action = TerminalTopic.LOCK_CONFERENCE;
            if (null != conferenceContext) {
                String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
                JSONObject jObj = new JSONObject();
                jObj.put(InstantMeetingParam.CONFERENCENUM, conferenceNum);
                jObj.put(InstantMeetingParam.RECORDING_STATUS, lockStatus);

                //给消息发布者发送messageId
                this.responseTerminal(terminalTopic, action, jObj, clientId, messageId);
            }
        }
    }


    private void messagePublisherTomessageId(String clientId, String sn, String messageId, String terminalTopic, String action, JSONObject jObj) {
        if (clientId.equals(sn)) {
            this.responseTerminal(terminalTopic, action, jObj, clientId, messageId);
        } else {
            this.responseTerminal(terminalTopic, action, jObj, clientId, "");
        }
    }


    @Override
    public void addParticipants(JSONObject jsonS, String clientId, String messageId) {
        List<Long> terminalIds = new ArrayList<Long>();
        String conferenceNum = jsonS.getString(InstantMeetingParam.CONFERENCENUM);
        JSONArray jsonArray = jsonS.getJSONArray(InstantMeetingParam.PARTICIPANTS);
        if (!canControlConference(clientId, conferenceNum)) {
            String action = TerminalTopic.CREATE_CONFERENCE;
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
            ResponseTerminal.getInstance().responseTerminal(ResponseInfo.CODE_401, "没有操作权限！", terminalTopic, action, null, clientId, "");
            return;
        }
        if (null != jsonArray && jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                String sn = jsonArray.get(i).toString();
                BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(sn);
                if (busiTerminalSn != null) {
                    terminalIds.add(busiTerminalSn.getId());
                }
            }

            BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
            BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
            if (conferenceContext != null) {
                //添加参会人
                if (conferenceContext instanceof ConferenceContext) {
                    attendeeService.invite(conferenceContext.getId(), terminalIds);
                } else if (conferenceContext instanceof McuZjConferenceContext) {
                    attendeeForMcuZjService.invite(conferenceContext.getId(), terminalIds);
                } else if (conferenceContext instanceof McuPlcConferenceContext) {
                    attendeeForMcuPlcService.invite(conferenceContext.getId(), terminalIds);
                } else if (conferenceContext instanceof McuKdcConferenceContext) {
                    attendeeForMcuKdcService.invite(conferenceContext.getId(), terminalIds);
                }

                String action = TerminalTopic.ADD_PARTICIPANTS;
//                this.sendInfoToAllParticipant(conferenceContext.getId(), action, clientId, messageId, "");
            }
        }
    }

    @Override
    public void setConferenceCaption(JSONObject jsonS, String clientId, String messageId) {
        //未测试
        String conferenceNum = jsonS.getString(InstantMeetingParam.CONFERENCENUM);
        BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
        BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
        if (null != conferenceContext) {
            JSONObject object = new JSONObject();
            object.put(InstantMeetingParam.MESSAGE_TEXT, (String) jsonS.getString(InstantMeetingParam.MESSAGE_TEXT));
            object.put(InstantMeetingParam.MESSAGE_POSITION, (String) jsonS.getString(InstantMeetingParam.MESSAGE_POSITION));
            object.put(InstantMeetingParam.MESSAGE_DURATION, (Long) jsonS.getLong(InstantMeetingParam.MESSAGE_DURATION));
            if (conferenceContext instanceof ConferenceContext) {
                attendeeService.sendMessage(conferenceContext.getId(), object);
            } else if (conferenceContext instanceof McuZjConferenceContext) {
                attendeeForMcuZjService.sendMessage(conferenceContext.getId(), object);
            } else if (conferenceContext instanceof McuPlcConferenceContext) {
                attendeeForMcuPlcService.sendMessage(conferenceContext.getId(), object);
            } else if (conferenceContext instanceof McuKdcConferenceContext) {
                attendeeForMcuKdcService.sendMessage(conferenceContext.getId(), object);
            }
        }

        JSONObject jObj = new JSONObject();
        String action = TerminalTopic.SET_CONFERENCE_CAPTION;
        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
        jObj.put(InstantMeetingParam.CONFERENCENUM, conferenceNum);
        jObj.put(InstantMeetingConversionJson.TERMINAL_ID, clientId);
        this.responseTerminal(terminalTopic, action, jObj, clientId, messageId);
    }

    @Override
    public void hostCancleRollCall(JSONObject jsonS, String clientId, String messageId) {
        if (null != jsonS) {
            String conferenceNum = jsonS.getString(InstantMeetingParam.CONFERENCENUM);
            if (!canControlConference(clientId, conferenceNum)) {
                String action = TerminalTopic.CREATE_CONFERENCE;
                String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
                ResponseTerminal.getInstance().responseTerminal(ResponseInfo.CODE_401, "没有操作权限！", terminalTopic, action, null, clientId, "");
                return;
            }
            BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
            BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
            if (conferenceContext != null) {
                //取消点名
                if (conferenceContext instanceof ConferenceContext) {
                    attendeeService.cancelCallTheRoll(conferenceContext.getId());
                } else if (conferenceContext instanceof McuZjConferenceContext) {
                    attendeeForMcuZjService.cancelCallTheRoll(conferenceContext.getId());
                } else if (conferenceContext instanceof McuPlcConferenceContext) {
                    attendeeForMcuPlcService.cancelCallTheRoll(conferenceContext.getId());
                } else if (conferenceContext instanceof McuKdcConferenceContext) {
                    attendeeForMcuKdcService.cancelCallTheRoll(conferenceContext.getId());
                }

                //点名终端，给所有参会者发信息
                String action = TerminalTopic.CANCEL_ROLL_CALL;
//                this.sendInfoToAllParticipant(conferenceContext.getId(), action, clientId, messageId, "");
            }
        }
    }

    @Override
    public JSONObject byTerminalIdGetDeptTree(JSONObject jsonS, String clientId, String messageId) {
        JSONObject deptTreeToTerminal = null;
        if (null != jsonS) {
            String sn = jsonS.getString(MqttConfigConstant.CLIENTID);
            Long deptId = jsonS.getLong(InstantMeetingConversionJson.DEPT_ID);
            if (null == deptId && StringUtils.isNotEmpty(sn)) {
                BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(sn);
                if (busiTerminalSn != null) {
                    SysDept sysDept = deptService.selectDeptById(busiTerminalSn.getDeptId());
                    if (null != sysDept) {
                        SysDept sysDept2 = new SysDept();
                        sysDept2.setDeptId(sysDept.getDeptId());
                        sysDept2.setAncestors(sysDept.getAncestors());
                        sysDept2.setDelFlag("0");
                        sysDept2.setStatus("0");

                        //过滤需要的部门
                        List<SysDept> depts = filterDepts(sysDept2, sysDept.getParentId());

                        //部门树下发终端
                        deptTreeToTerminal = this.deptTreeToTerminal(sn, depts, clientId, messageId, deptId);
                    }
                }
            } else {
                SysDept dept = new SysDept();
                dept.setParentId(deptId);
                dept.setDelFlag("0");
                dept.setStatus("0");

                //过滤需要的部门
                List<SysDept> depts = filterDepts(dept, deptId);
                deptTreeToTerminal = this.deptTreeToTerminal(sn, depts, clientId, messageId, deptId);
            }
        }

        return deptTreeToTerminal;
    }

    /**
     * //过滤需要的部门
     *
     * @param sysDept2
     * @param parentId
     * @return
     */
    private List<SysDept> filterDepts(SysDept sysDept2, Long parentId) {
        List<SysDept> depts = new ArrayList<SysDept>();
        List<SysDept> deptList = deptMapper.selectDeptList(sysDept2);
        if (null != deptList && deptList.size() > 0) {
            for (SysDept dept : deptList) {
                if (dept.getParentId().equals(parentId)) {
                    depts.add(dept);
                }
            }
        }
        return depts;
    }

    private JSONObject deptTreeToTerminal(String sn, List<SysDept> deptsOri, String clientId, String messageId, Long deptId) {
        List<SysDept> depts = new ArrayList<>();
        for (SysDept sysDept : deptsOri) {
            if (sysDept.getDeptId() <= 1 || sysDept.getDeptId() >= 100) {
                depts.add(sysDept);
            }
        }
        JSONObject jObj = new JSONObject();
        String action = TerminalTopic.ADDRESS_BOOK;
        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + sn;
        if (null != depts && depts.size() > 0) {
            List<TreeSelect> treeSelect = deptService.buildDeptTreeSelect(depts);
            if (null != deptId) {
                jObj.put("children", JSON.toJSON(treeSelect));
            } else {
                jObj = (JSONObject) JSON.toJSON(treeSelect.get(0));
            }
        } else {
            jObj.put("children", new JSONArray());
        }

        if (!"fuzzyQuery".equals(messageId)) {
            this.responseTerminal(terminalTopic, action, jObj, clientId, messageId);
        }

        return jObj;
    }

    @Override
    public void deptDownTerminal(JSONObject jsonS, String clientId, String messageId) {
        Long deptId = (Long) jsonS.getLong(InstantMeetingConversionJson.DEPT_ID);
        Integer page = (Integer) jsonS.getInteger(ResponseInfo.PAGE);
        Integer size = (Integer) jsonS.getInteger(ResponseInfo.SIZE);
        String action = TerminalTopic.DEPT_DOWN_TERMINAL;
        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
        Assert.isTrue(!ObjectUtils.isEmpty(clientId), "SN不能为空！");
        Assert.isTrue(page != null, "page不能为空！");
        Assert.isTrue(size != null, "size不能为空！");
        Assert.isTrue(deptId != null, "部门id不能为空！");

        if (page == null || page <= 0) {
            page = 0;
        }
        if (size == null || size <= 0) {
            size = 100;
        }

        PaginationData<BusiTerminal> pd = new PaginationData<>();
        List<BusiTerminal> terminals = new ArrayList<BusiTerminal>();
        Map<Long, BusiTerminal> deptMap = TerminalCache.getInstance().getByDept(deptId);
        if (null != deptMap && !deptMap.isEmpty()) {
            deptMap.forEach((key, value) -> {
                if (StringUtils.isNotEmpty(value.getSn())) {
                    if (TerminalType.isZJ(value.getType())) {
                        BusiTerminal busiTerminal = new BusiTerminal();
                        BeanUtils.copyBeanProp(busiTerminal, value);
                        List<McuZjBridge> mcuZjBridgeList = McuZjBridgeCache.getInstance().getMcuZjBridgesByDept(value.getDeptId());
                        if (mcuZjBridgeList != null && mcuZjBridgeList.size() > 0) {
                            McuZjBridge mcuZjBridge = mcuZjBridgeList.get(0);
                            busiTerminal.setCredential(busiTerminal.getCredential() + "@" + mcuZjBridge.getBusiMcuZj().getMcuDomain());
                        }
                        terminals.add(busiTerminal);
                    } else {
                        terminals.add(value);
                    }
                }
            });

            int fromIndex = page * size;
            int toIndex = fromIndex + size;
            if (toIndex >= terminals.size()) {
                toIndex = terminals.size();
            }

            if (fromIndex >= toIndex) {
                pd.setRecords(new ArrayList<BusiTerminal>());
            } else {
                pd.setRecords(terminals.subList(fromIndex, toIndex));
            }

            pd.setTotal(terminals.size());
            pd.setPage(page);
            pd.setSize(size);

            JSONObject jObj = (JSONObject) JSON.toJSON(pd);
            this.responseTerminal(terminalTopic, action, jObj, clientId, messageId);
        } else {
            JSONObject jObjs = new JSONObject();
            this.responseTerminal(terminalTopic, action, jObjs, clientId, messageId);
        }
    }


    private List<ModelBean> getBusiTerminalList(BusiTerminal busiTerminal) {
        Long deptId = busiTerminal.getDeptId();

        List<Long> deptIds = null;
        if (deptId != null) {
            deptIds = new ArrayList<>();
        }

        SysDept dept = new SysDept();
        dept.setDeptId(deptId);
        List<SysDept> ds = sysDeptService.selectDeptList(dept);
        for (SysDept sysDept : ds) {
            if (deptIds != null) {
                deptIds.add(sysDept.getDeptId());
            }
        }

        List<ModelBean> ms = new ArrayList<>();

        busiTerminal.getParams().put("deptIds", deptIds);

        List<BusiTerminal> ts = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
        for (BusiTerminal busiTerminal0 : ts) {
            ModelBean m = new ModelBean(busiTerminal0);
            m.put("deptName", SysDeptCache.getInstance().get(busiTerminal0.getDeptId()).getDeptName());
            ms.add(m);
        }

        return ms;
    }

    @Override
    public void conferenceViewLayout(JSONObject jsonS, String clientId, String messageId) {
        String conferenceNum = (String) jsonS.get(MqttConfigConstant.CONFERENCENUM);
        if (StringUtils.isNotEmpty(conferenceNum)) {
            BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
            BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
            if (conferenceContext != null) {
                if (conferenceContext instanceof ConferenceContext) {
                    conferenceViewLayoutForFme(jsonS, clientId, messageId, (ConferenceContext) conferenceContext);
                } else if (conferenceContext instanceof McuZjConferenceContext) {
                    conferenceViewLayoutForZj(jsonS, clientId, messageId, (McuZjConferenceContext) conferenceContext);
                } else if (conferenceContext instanceof McuPlcConferenceContext) {
                    conferenceViewLayoutForPlc(jsonS, clientId, messageId, (McuPlcConferenceContext) conferenceContext);
                } else if (conferenceContext instanceof McuKdcConferenceContext) {
                    conferenceViewLayoutForKdc(jsonS, clientId, messageId, (McuKdcConferenceContext) conferenceContext);
                }
            }
        }
    }

    /**
     * Fme 会议布局
     *
     * @param jsonS
     * @param clientId
     * @param messageId
     */
    private void conferenceViewLayoutForFme(JSONObject jsonS, String clientId, String messageId, ConferenceContext conferenceContext) {
        if (null != jsonS) {
            JSONObject jObj = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            JSONArray array = new JSONArray();
            String conferenceNum = (String) jsonS.getString(InstantMeetingParam.CONFERENCENUM);
            String conferenceLayoutCommand = (String) jsonS.getString("conferenceLayoutCommand");
            if (StringUtils.isNotEmpty(conferenceLayoutCommand)) {

                //显示布局,快捷键方式
                this.quickModeLayout(conferenceContext, conferenceLayoutCommand, messageId, clientId);
            } else {
                String defaultViewLayout = (String) jsonS.getString(ConferenceLayout.DEFAULT_VIEW_LAYOUT);
                Integer defaultViewIsBroadcast = (Integer) jsonS.getInteger(ConferenceLayout.DEFAULT_VIEW_IS_BROADCAST);
                Integer defaultViewIsDisplaySelf = (Integer) jsonS.getInteger(ConferenceLayout.DEFAULT_VIEW_IS_DISPLAY_SELF);
                Integer defaultViewIsFill = (Integer) jsonS.getInteger(ConferenceLayout.DEFAULT_VIEW_IS_FILL);
                Integer pollingInterval = (Integer) jsonS.getInteger(ConferenceLayout.POLLING_INTERVAL);
                JSONArray viewDeptsArr = jsonS.getJSONArray(ConferenceLayout.DEFAULT_VIEW_DEPTS);
                JSONArray viewPaticipants = jsonS.getJSONArray(ConferenceLayout.DEFAULT_VIEW_PATICIPANTS);
                if (null != conferenceContext) {
                    if (null != viewPaticipants && viewPaticipants.size() > 0) {
                        for (int i = 0; i < viewPaticipants.size(); i++) {
                            JSONObject obj = new JSONObject();
                            JSONObject object = (JSONObject) viewPaticipants.get(i);
                            String name = object.getString(ConferenceLayout.NAME);
                            Integer weight = object.getInteger(ConferenceLayout.WEIGHT);
                            String cellSequenceNumber = object.getString(ConferenceLayout.CELL_SEQUENCE_NUMBER);
                            String sn = object.getString(MqttConfigConstant.CLIENTID);
                            BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(sn);
                            if (busiTerminalSn != null) {
                                Attendee terminalAttendee = conferenceContext.getAttendeeByTerminalId(busiTerminalSn.getId());
                                if (null != terminalAttendee) {
                                    String participantUuid = terminalAttendee.getId();
                                    obj.put(ConferenceLayout.PARTICIPANT_UUID, participantUuid);
                                }

                                obj.put(ConferenceLayout.NAME, name);
                                obj.put(ConferenceLayout.WEIGHT, weight);
                                obj.put(ConferenceLayout.CELL_SEQUENCE_NUMBER, cellSequenceNumber);
                                jsonArray.add(obj);
                            }
                        }
                    }

                    if (defaultViewLayout.equals(ConferenceLayout.ALL_EQUAL_NINTHS)) {
//						JSONArray defaultViewCellScreens = jsonS.getJSONArray(ConferenceLayout.DEFAULT_VIEW_CELL_SCREENS);
//						jObj.put(ConferenceLayout.DEFAULT_VIEW_CELL_SCREENS, defaultViewCellScreens);

                        for (int x = 0; x < 9; x++) {
                            JSONObject objn = new JSONObject();
                            objn.put(ConferenceLayout.CELL_SEQUENCE_NUMBER, x + 1);
                            objn.put(ConferenceLayout.OPERATION, 101);
                            objn.put(ConferenceLayout.ISFIXED, 2);
                            array.add(objn);
                        }
                    }

                    jObj.put(ConferenceLayout.DEFAULT_VIEW_CELL_SCREENS, array);
                    jObj.put(ConferenceLayout.DEFAULT_VIEW_LAYOUT, defaultViewLayout);
                    jObj.put(ConferenceLayout.DEFAULT_VIEW_IS_BROADCAST, defaultViewIsBroadcast);
                    jObj.put(ConferenceLayout.DEFAULT_VIEW_IS_DISPLAY_SELF, defaultViewIsDisplaySelf);
                    jObj.put(ConferenceLayout.DEFAULT_VIEW_IS_FILL, defaultViewIsFill);
                    jObj.put(ConferenceLayout.POLLING_INTERVAL, pollingInterval);
                    jObj.put(ConferenceLayout.DEFAULT_VIEW_DEPTS, viewDeptsArr);
                    jObj.put(ConferenceLayout.DEFAULT_VIEW_PATICIPANTS, jsonArray);

                    defaultAttendeeOperationPackageService.updateDefaultViewConfigInfo(conferenceContext.getId(), jObj);

                    JSONObject jObjs = new JSONObject();
                    String action = TerminalTopic.CONFERENCE_LAYOUT;
                    String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
                    jObjs.put(InstantMeetingParam.CONFERENCENUM, conferenceNum);
                    this.responseTerminal(terminalTopic, action, jObjs, clientId, messageId);
                }
            }
        }
    }

    /**
     * Zj 会议布局
     *
     * @param jsonS
     * @param clientId
     * @param messageId
     */
    private void conferenceViewLayoutForZj(JSONObject jsonS, String clientId, String messageId, McuZjConferenceContext conferenceContext) {
        if (null != jsonS) {
            String conferenceNum = (String) jsonS.getString(InstantMeetingParam.CONFERENCENUM);
            String conferenceLayoutCommand = (String) jsonS.getString("conferenceLayoutCommand");
            if (StringUtils.isNotEmpty(conferenceLayoutCommand)) {
                String substr = conferenceLayoutCommand.substring(0, 2);
                try {
                    int layoutE = Integer.parseInt(conferenceLayoutCommand.substring(2));
                    LOGGER.info("======================================>&&&&###" + conferenceNum);
                    LOGGER.info("======================================>&&&&$$$" + layoutE);

                    RestResponse restResponse = null;

                    if ("*3".equals(substr)) {
                        restResponse = simpleConferenceControlForMcuZjService.updateDefaultViewConfigInfo(conferenceContext, layoutE, true);
                    } else if ("*2".equals(substr)) {
                        restResponse = simpleConferenceControlForMcuZjService.updateDefaultViewConfigInfo(conferenceContext, layoutE, false);
                    } else if ("*4".equals(substr)) {
                        restResponse = simpleConferenceControlForMcuZjService.polling(conferenceContext, layoutE, true);
                    } else if ("*5".equals(substr)) {
                        restResponse = simpleConferenceControlForMcuZjService.polling(conferenceContext, layoutE, false);
                    }

                    JSONObject jObjs = new JSONObject();
                    String action = TerminalTopic.CONFERENCE_LAYOUT;
                    String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
                    jObjs.put(InstantMeetingParam.CONFERENCENUM, conferenceNum);
                    jObjs.put("conferenceLayout", layoutE);
                    if (restResponse.isSuccess()) {
                        this.responseTerminal(terminalTopic, action, jObjs, clientId, messageId);
                    } else {
                        jObjs.put(InstantMeetingParam.CONFERENCENUM, conferenceNum);
                        jObjs.put("conferenceLayout", QuickKeyLayout.NO_LAYOUT);
                        this.responseTerminal(terminalTopic, action, jObjs, clientId, messageId);
                    }

                } catch (Exception e) {
                    JSONObject jObjs = new JSONObject();
                    String action = TerminalTopic.CONFERENCE_LAYOUT;
                    String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
                    jObjs.put(InstantMeetingParam.CONFERENCENUM, conferenceNum);
                    jObjs.put("conferenceLayout", QuickKeyLayout.NO_LAYOUT);
                    this.responseTerminal(terminalTopic, action, jObjs, clientId, messageId);
                }
            } else {
                RestResponse restResponse = simpleConferenceControlForMcuZjService.recoveryLastDefaultView(conferenceContext);
                if (restResponse.isSuccess()) {
                    JSONObject jObjs = new JSONObject();
                    String action = TerminalTopic.CONFERENCE_LAYOUT;
                    String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
                    jObjs.put(InstantMeetingParam.CONFERENCENUM, conferenceNum);
                    this.responseTerminal(terminalTopic, action, jObjs, clientId, messageId);
                }
            }
        }
    }

    /**
     * plc 会议布局
     *
     * @param jsonS
     * @param clientId
     * @param messageId
     */
    private void conferenceViewLayoutForPlc(JSONObject jsonS, String clientId, String messageId, McuPlcConferenceContext conferenceContext) {
        if (null != jsonS) {
            String conferenceNum = (String) jsonS.getString(InstantMeetingParam.CONFERENCENUM);
            String conferenceLayoutCommand = (String) jsonS.getString("conferenceLayoutCommand");
            if (StringUtils.isNotEmpty(conferenceLayoutCommand)) {
                String substr = conferenceLayoutCommand.substring(0, 2);
                try {
                    int layoutE = Integer.parseInt(conferenceLayoutCommand.substring(2));
                    LOGGER.info("======================================>&&&&###" + conferenceNum);
                    LOGGER.info("======================================>&&&&$$$" + layoutE);

                    RestResponse restResponse = null;

                    if ("*3".equals(substr)) {
                        restResponse = simpleConferenceControlForMcuPlcService.updateDefaultViewConfigInfo(conferenceContext, layoutE, true);
                    } else if ("*2".equals(substr)) {
                        restResponse = simpleConferenceControlForMcuPlcService.updateDefaultViewConfigInfo(conferenceContext, layoutE, false);
                    } else if ("*4".equals(substr)) {
                        restResponse = simpleConferenceControlForMcuPlcService.polling(conferenceContext, layoutE, true);
                    } else if ("*5".equals(substr)) {
                        restResponse = simpleConferenceControlForMcuPlcService.polling(conferenceContext, layoutE, false);
                    }

                    JSONObject jObjs = new JSONObject();
                    String action = TerminalTopic.CONFERENCE_LAYOUT;
                    String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
                    jObjs.put(InstantMeetingParam.CONFERENCENUM, conferenceNum);
                    jObjs.put("conferenceLayout", layoutE);
                    if (restResponse.isSuccess()) {
                        this.responseTerminal(terminalTopic, action, jObjs, clientId, messageId);
                    } else {
                        jObjs.put(InstantMeetingParam.CONFERENCENUM, conferenceNum);
                        jObjs.put("conferenceLayout", QuickKeyLayout.NO_LAYOUT);
                        this.responseTerminal(terminalTopic, action, jObjs, clientId, messageId);
                    }

                } catch (Exception e) {
                    JSONObject jObjs = new JSONObject();
                    String action = TerminalTopic.CONFERENCE_LAYOUT;
                    String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
                    jObjs.put(InstantMeetingParam.CONFERENCENUM, conferenceNum);
                    jObjs.put("conferenceLayout", QuickKeyLayout.NO_LAYOUT);
                    this.responseTerminal(terminalTopic, action, jObjs, clientId, messageId);
                }
            } else {
                RestResponse restResponse = simpleConferenceControlForMcuPlcService.recoveryLastDefaultView(conferenceContext);
                if (restResponse.isSuccess()) {
                    JSONObject jObjs = new JSONObject();
                    String action = TerminalTopic.CONFERENCE_LAYOUT;
                    String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
                    jObjs.put(InstantMeetingParam.CONFERENCENUM, conferenceNum);
                    this.responseTerminal(terminalTopic, action, jObjs, clientId, messageId);
                }
            }
        }
    }

    /**
     * kdc 会议布局
     *
     * @param jsonS
     * @param clientId
     * @param messageId
     */
    private void conferenceViewLayoutForKdc(JSONObject jsonS, String clientId, String messageId, McuKdcConferenceContext conferenceContext) {
        if (null != jsonS) {
            String conferenceNum = (String) jsonS.getString(InstantMeetingParam.CONFERENCENUM);
            String conferenceLayoutCommand = (String) jsonS.getString("conferenceLayoutCommand");
            String mcuKdcConferenceNum = conferenceNum.substring(3);
            if (StringUtils.isNotEmpty(conferenceLayoutCommand)) {
                String substr = conferenceLayoutCommand.substring(0, 2);
                try {
                    int layoutE = Integer.parseInt(conferenceLayoutCommand.substring(2));
                    LOGGER.info("======================================>&&&&###" + conferenceNum);
                    LOGGER.info("======================================>&&&&$$$" + layoutE);

                    RestResponse restResponse = null;

                    if ("*3".equals(substr)) {
                        restResponse = simpleConferenceControlForMcuKdcService.updateDefaultViewConfigInfo(conferenceContext, layoutE, true);
                    } else if ("*2".equals(substr)) {
                        restResponse = simpleConferenceControlForMcuKdcService.updateDefaultViewConfigInfo(conferenceContext, layoutE, false);
                    } else if ("*4".equals(substr)) {
                        restResponse = simpleConferenceControlForMcuKdcService.polling(conferenceContext, layoutE, true);
                    } else if ("*5".equals(substr)) {
                        restResponse = simpleConferenceControlForMcuKdcService.polling(conferenceContext, layoutE, false);
                    }

                    JSONObject jObjs = new JSONObject();
                    String action = TerminalTopic.CONFERENCE_LAYOUT;
                    String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
                    jObjs.put(InstantMeetingParam.CONFERENCENUM, conferenceNum);
                    jObjs.put("conferenceLayout", layoutE);
                    if (restResponse.isSuccess()) {
                        this.responseTerminal(terminalTopic, action, jObjs, clientId, messageId);
                    } else {
                        jObjs.put(InstantMeetingParam.CONFERENCENUM, conferenceNum);
                        jObjs.put("conferenceLayout", QuickKeyLayout.NO_LAYOUT);
                        this.responseTerminal(terminalTopic, action, jObjs, clientId, messageId);
                    }

                } catch (Exception e) {
                    JSONObject jObjs = new JSONObject();
                    String action = TerminalTopic.CONFERENCE_LAYOUT;
                    String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
                    jObjs.put(InstantMeetingParam.CONFERENCENUM, conferenceNum);
                    jObjs.put("conferenceLayout", QuickKeyLayout.NO_LAYOUT);
                    this.responseTerminal(terminalTopic, action, jObjs, clientId, messageId);
                }
            } else {
                RestResponse restResponse = simpleConferenceControlForMcuKdcService.recoveryLastDefaultView(conferenceContext);
                if (restResponse.isSuccess()) {
                    JSONObject jObjs = new JSONObject();
                    String action = TerminalTopic.CONFERENCE_LAYOUT;
                    String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
                    jObjs.put(InstantMeetingParam.CONFERENCENUM, conferenceNum);
                    this.responseTerminal(terminalTopic, action, jObjs, clientId, messageId);
                }
            }
        }
    }

    private void quickModeLayout(ConferenceContext conferenceContext, String conferenceLayoutCommand, String messageId, String clientId) {
        String substr = conferenceLayoutCommand.substring(0, 2);
        String[] starSplit = conferenceLayoutCommand.split("\\" + substr);

        //显示布局广播
        this.showLayoutBroadCast(conferenceContext, substr, messageId, clientId, starSplit);
    }


    private void showLayoutBroadCast(ConferenceContext conferenceContext, String layoutType, String messageId, String clientId, String[] starSplit) {
        String layoutE = null;
        RestResponse response = null;
        String layoutM = starSplit[1];
        if (starSplit.length > 1) {
            if (!layoutM.contains("*")) {

                //根据快捷数字，对应英文布局
                layoutE = quickKeyNumberToLayout(starSplit[1]);
                if (!layoutE.equals(QuickKeyLayout.NO_LAYOUT)) {
                    //是广播还是选看
                    response = this.isBroadCastOrXk(layoutType, conferenceContext, layoutE, PanePlacementSelfPaneMode.OFF.getValue());
                }
            } else {
                String[] layoutNum = layoutM.split("\\*");
                if (layoutNum.length > 0) {
                    //根据快捷数字，对应英文布局
                    layoutE = quickKeyNumberToLayout(layoutNum[0]);
                    if (!layoutE.equals(QuickKeyLayout.NO_LAYOUT)) {
                        //是广播还是选看
                        response = this.isBroadCastOrXk(layoutType, conferenceContext, layoutE, PanePlacementSelfPaneMode.SELF.getValue());
                    }
                }
            }

            JSONObject jObjs = new JSONObject();
            String action = TerminalTopic.CONFERENCE_LAYOUT;
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
            jObjs.put(InstantMeetingParam.CONFERENCENUM, conferenceContext.getConferenceNumber());
            jObjs.put("conferenceLayout", layoutE);
            if (!layoutE.equals(QuickKeyLayout.NO_LAYOUT)) {
                if (response.isSuccess()) {
                    this.responseTerminal(terminalTopic, action, jObjs, clientId, messageId);
                }
            } else {
                jObjs.put("conferenceLayout", layoutE);
                this.responseTerminal(terminalTopic, action, jObjs, clientId, messageId);
            }
        }

    }


    private RestResponse isBroadCastOrXk(String layoutType, ConferenceContext conferenceContext, String layoutE, int mode) {
        RestResponse restResponse = null;
        LOGGER.info("======================================>&&&&###" + conferenceContext.getConferenceNumber());
        LOGGER.info("======================================>&&&&$$$" + layoutE);
        LOGGER.info("======================================>&&&&@@@" + mode);
        if ("*3".equals(layoutType)) {
            restResponse = videoConferenceSDKService.displayLayoutGB(conferenceContext, layoutE, mode);
        } else if ("*2".equals(layoutType)) {
            restResponse = videoConferenceSDKService.displayLayoutXK(conferenceContext, layoutE, mode);
        } else if ("*4".equals(layoutType)) {
            restResponse = videoConferenceSDKService.pollingGB(conferenceContext, layoutE, mode);
        } else if ("*5".equals(layoutType)) {
            restResponse = videoConferenceSDKService.pollingXK(conferenceContext, layoutE, mode);
        }
        return restResponse;
    }

    private String quickKeyNumberToLayout(String layout) {
        String layoutEng = null;
        switch (layout) {
            case "1":
                layoutEng = QuickKeyLayout.SPEAKER_ONLY;
                break;
            case "2":
                layoutEng = QuickKeyLayout.TELE_PRESENCE;
                break;
            case "3":
                layoutEng = QuickKeyLayout.ATUOMATIC;
                break;
            case "4":
                layoutEng = QuickKeyLayout.ALL_EQUAL_QUARTERS;
                break;
            case "5":
                layoutEng = QuickKeyLayout.ALL_EQUAL;
                break;
            case "6":
                layoutEng = QuickKeyLayout.ONE_PLUS_FIVE;
                break;
            case "7":
                layoutEng = QuickKeyLayout.ONE_PLUS_N;
                break;
            case "8":
                layoutEng = QuickKeyLayout.ONE_PLUS_SEVEN;
                break;
            case "9":
                layoutEng = QuickKeyLayout.ALL_EQUAL_NINTHS;
                break;
            case "10":
                layoutEng = QuickKeyLayout.ONE_PLUS_NINE;
                break;
            case "11":
                layoutEng = QuickKeyLayout.STACKED;
                break;
            case "16":
                layoutEng = QuickKeyLayout.ALL_EQUAL_SIXTEENTHS;
                break;
            case "25":
                layoutEng = QuickKeyLayout.ALL_EQUAL_TWENTY_FIFTHS;
                break;
            default:
                layoutEng = QuickKeyLayout.NO_LAYOUT;
                break;
        }
        return layoutEng;
    }


    @Override
    public void leaveConference(JSONObject jsonS, String clientId, String messageId) {
        String conferenceNum = jsonS.getString(InstantMeetingParam.CONFERENCENUM);
        String sn = jsonS.getString(MqttConfigConstant.CLIENTID);
        String attendeeId = jsonS.getString(MqttConfigConstant.ATTENDEE_ID);
        BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
        BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
        if (conferenceContext != null) {
            if (StringUtils.isNotEmpty(attendeeId)) {
            } else if (StringUtils.isNotEmpty(sn)) {
                BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(sn);
                BaseAttendee attendee = conferenceContext.getAttendeeByTerminalId(busiTerminalSn.getId());
                if (attendee != null) {
                    attendeeId = attendee.getId();
                }
            }
            if (StringUtils.isNotEmpty(attendeeId)) {
                if (conferenceContext instanceof ConferenceContext) {
                    attendeeService.hangUp(conferenceContext.getId(), attendeeId);
                } else if (conferenceContext instanceof McuZjConferenceContext) {
                    attendeeForMcuZjService.hangUp(conferenceContext.getId(), attendeeId);
                } else if (conferenceContext instanceof McuPlcConferenceContext) {
                    attendeeForMcuPlcService.hangUp(conferenceContext.getId(), attendeeId);
                } else if (conferenceContext instanceof McuKdcConferenceContext) {
                    attendeeForMcuKdcService.hangUp(conferenceContext.getId(), attendeeId);
                }
            }
        }
    }


    @Override
    public void chooseSee(JSONObject jsonS, String clientId, String messageId) {
        String conferenceNum = jsonS.getString(InstantMeetingParam.CONFERENCENUM);
        Long terminalId = jsonS.getLong(InstantMeetingConversionJson.TERMINAL_ID);
        String attendeeId = jsonS.getString(MqttConfigConstant.ATTENDEE_ID);
        if (!canControlConference(clientId, conferenceNum)) {
            String action = TerminalTopic.CHOOSE_SEE;
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
            ResponseTerminal.getInstance().responseTerminal(ResponseInfo.CODE_401, "没有操作权限！", terminalTopic, action, null, clientId, "");
            return;
        }
        BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
        BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
        if (conferenceContext != null) {
            if (StringUtils.isNotEmpty(attendeeId)) {
            } else if (terminalId != null) {
                BaseAttendee attendee = conferenceContext.getAttendeeByTerminalId(terminalId);
                if (attendee != null) {
                    attendeeId = attendee.getId();
                }
            }
            if (StringUtils.isNotEmpty(attendeeId)) {
                if (conferenceContext instanceof ConferenceContext) {
                    attendeeService.chooseSee(conferenceContext.getId(), attendeeId);
                } else if (conferenceContext instanceof McuZjConferenceContext) {
                    attendeeForMcuZjService.chooseSee(conferenceContext.getId(), attendeeId);
                } else if (conferenceContext instanceof McuPlcConferenceContext) {
                    attendeeForMcuPlcService.chooseSee(conferenceContext.getId(), attendeeId);
                } else if (conferenceContext instanceof McuKdcConferenceContext) {
                    attendeeForMcuKdcService.chooseSee(conferenceContext.getId(), attendeeId);
                }

                JSONObject jObjs = new JSONObject();
                String action = TerminalTopic.CHOOSE_SEE;
                String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
                jObjs.put(InstantMeetingParam.CONFERENCENUM, conferenceNum);
                jObjs.put(InstantMeetingConversionJson.TERMINAL_ID, terminalId);
                this.responseTerminal(terminalTopic, action, jObjs, clientId, messageId);
            }
        }
    }

    @Override
    public void conferenceList(JSONObject jsonS, String clientId, String messageId, List<BusiTemplateConference> busiTemplateConferencesList) {
        Integer page = (Integer) jsonS.getInteger(ResponseInfo.PAGE);
        Integer size = (Integer) jsonS.getInteger(ResponseInfo.SIZE);
        String sn = (String) jsonS.getString(MqttConfigConstant.CLIENTID);
        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size <= 0) {
            size = 20;
        }

        String videoConference = null;
        List<BusiConferenceAppointment> cas = new ArrayList<>();
        PaginationData<BusiConferenceAppointment> pd = new PaginationData<>();

        // 预约会议
        Collection<BusiConferenceAppointment> valuesAppointment = AppointmentCache.getInstance().getAll().values();
        if (valuesAppointment != null && valuesAppointment.size() > 0) {
            for (BusiConferenceAppointment busiConferenceAppointmentCached : valuesAppointment) {
                if (busiConferenceAppointmentCached == null) {
                    continue;
                }
                boolean isMyConference = false;

                try {
                    BusiTerminal busiTerminal = TerminalCache.getInstance().getBySn(clientId);
                    if (busiTerminal != null) {
                        BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(busiTerminal.getId());
                        if (busiUserTerminal != null) {
                            if (busiConferenceAppointmentCached instanceof BusiConferenceAppointment) {
                                BusiTemplateConference busiTemplateConference = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(busiConferenceAppointmentCached.getTemplateId());
                                if (busiTemplateConference != null) {
                                    if (busiTemplateConference.getCreateUserId().longValue() == busiUserTerminal.getUserId().longValue()) {
                                        isMyConference = true;
                                    }
                                }
                            } else if (busiConferenceAppointmentCached instanceof BusiMcuZjConferenceAppointment) {
                                BusiMcuZjTemplateConference busiMcuZjTemplateConference = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(busiConferenceAppointmentCached.getTemplateId());
                                if (busiMcuZjTemplateConference != null) {
                                    if (busiMcuZjTemplateConference.getCreateUserId().longValue() == busiUserTerminal.getUserId().longValue()) {
                                        isMyConference = true;
                                    }
                                }
                            } else if (busiConferenceAppointmentCached instanceof BusiMcuPlcConferenceAppointment) {
                                BusiMcuPlcTemplateConference busiMcuPlcTemplateConference = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(busiConferenceAppointmentCached.getTemplateId());
                                if (busiMcuPlcTemplateConference != null) {
                                    if (busiMcuPlcTemplateConference.getCreateUserId().longValue() == busiUserTerminal.getUserId().longValue()) {
                                        isMyConference = true;
                                    }
                                }
                            } else if (busiConferenceAppointmentCached instanceof BusiMcuKdcConferenceAppointment) {
                                BusiMcuKdcTemplateConference busiMcuKdcTemplateConference = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(busiConferenceAppointmentCached.getTemplateId());
                                if (busiMcuKdcTemplateConference != null) {
                                    if (busiMcuKdcTemplateConference.getCreateUserId().longValue() == busiUserTerminal.getUserId().longValue()) {
                                        isMyConference = true;
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                }
                if (!isMyConference) {
                    JSONArray templateParticipants = (JSONArray) JSON.toJSON(busiConferenceAppointmentCached.getParams().get("templateParticipants"));
                    if (templateParticipants != null && templateParticipants.size() > 0) {
                        for (int i = 0; i < templateParticipants.size(); i++) {
                            if (sn.equals(templateParticipants.getJSONObject(i).get("sn"))) {
                                isMyConference = true;
                                break;
                            }
                        }
                    }
                }
                if (isMyConference) {
                    BusiTemplateConference busiTemplateConference = null;

                    McuType mcuType = McuType.FME;
                    if (busiTemplateConferencesList == null) {
                        busiTemplateConference = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(busiConferenceAppointmentCached.getTemplateId());
                        if (busiConferenceAppointmentCached instanceof BusiConferenceAppointment) {
                            BusiTemplateConference busiTemplateConferenceTemp = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(busiConferenceAppointmentCached.getTemplateId());
                            if (busiTemplateConferenceTemp != null) {
                                busiTemplateConference = new BusiTemplateConference();
                                BeanUtils.copyBeanProp(busiTemplateConference, busiTemplateConferenceTemp);
                                mcuType = McuType.FME;
                            }
                        } else if (busiConferenceAppointmentCached instanceof BusiMcuZjConferenceAppointment) {
                            BusiMcuZjTemplateConference busiMcuZjTemplateConference = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(busiConferenceAppointmentCached.getTemplateId());
                            if (busiMcuZjTemplateConference != null) {
                                busiTemplateConference = new BusiTemplateConference();
                                BeanUtils.copyBeanProp(busiTemplateConference, busiMcuZjTemplateConference);
                                mcuType = McuType.MCU_ZJ;
                            }
                        } else if (busiConferenceAppointmentCached instanceof BusiMcuPlcConferenceAppointment) {
                            BusiMcuPlcTemplateConference busiMcuPlcTemplateConference = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(busiConferenceAppointmentCached.getTemplateId());
                            if (busiMcuPlcTemplateConference != null) {
                                busiTemplateConference = new BusiTemplateConference();
                                BeanUtils.copyBeanProp(busiTemplateConference, busiMcuPlcTemplateConference);
                                mcuType = McuType.MCU_PLC;
                            }
                        } else if (busiConferenceAppointmentCached instanceof BusiMcuKdcConferenceAppointment) {
                            BusiMcuKdcTemplateConference busiMcuKdcTemplateConference = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(busiConferenceAppointmentCached.getTemplateId());
                            if (busiMcuKdcTemplateConference != null) {
                                busiTemplateConference = new BusiTemplateConference();
                                BeanUtils.copyBeanProp(busiTemplateConference, busiMcuKdcTemplateConference);
                                mcuType = McuType.MCU_KDC;
                            }
                        }
                        if (busiTemplateConference == null) {
                            continue;
                        }
                    } else if (busiTemplateConferencesList != null && busiTemplateConferencesList.size() > 0) {
                        for (BusiTemplateConference busiTemplateConferenceTemp : busiTemplateConferencesList) {
                            if (busiTemplateConferenceTemp.getId().equals(busiConferenceAppointmentCached.getTemplateId())) {
                                busiTemplateConference = busiTemplateConferenceTemp;
                                break;
                            }
                        }
                    }
                    if (busiTemplateConference == null) {
                        continue;
                    }
                    BusiConferenceAppointment busiConferenceAppointment = new BusiConferenceAppointment();
                    ModelBean modelBeanTemplateDetails = new ModelBean();
                    ModelBean modelBeanParams = new ModelBean();
                    busiConferenceAppointment.setStartTime(busiConferenceAppointmentCached.getStartTime());
                    busiConferenceAppointment.setEndTime(busiConferenceAppointmentCached.getEndTime());
                    boolean isStart = false;
                    if (busiTemplateConference != null) {
                        modelBeanParams.put("conferencePassword", busiTemplateConference.getConferencePassword());
                        modelBeanParams.put("conferenceName", busiTemplateConference.getName());
                        modelBeanParams.put("conferenceNumber", busiTemplateConference.getConferenceNumber());
                        if (AllConferenceContextCache.getInstance().containsKey(EncryptIdUtil.generateContextKey(busiTemplateConference.getId(), mcuType.getCode()))) {
                            isStart = true;
                        }
                    }
                    modelBeanTemplateDetails.put("isStart", isStart);
                    busiConferenceAppointment.setStatus(isStart ? 1 : 0);

                    ArrayList<ModelBean> templateParticipantList = new ArrayList<>();
                    /* 会议列表中去除参会者列表：数据超长导致mqtt断开
                    if (templateParticipants != null && templateParticipants.size() > 0) {
                        for (int i = 0; i < templateParticipants.size(); i++) {
                            JSONObject jsonObject = (JSONObject) templateParticipants.get(i);
                            String credential = String.valueOf(jsonObject.get("credential"));
                            String name = (String) jsonObject.get("name");
                            Integer onlineStatus = jsonObject.getInteger("onlineStatus");
                            ModelBean modelBeanParticipant = new ModelBean();
                            modelBeanParticipant.put("credential", credential);
                            modelBeanParticipant.put("onlineStatus", onlineStatus);
                            modelBeanParticipant.put("name", name);
                            //createTime 字段字段没有使用，终端需要createTime来进行时间戳的转时间===可不传
                            modelBeanParticipant.put("createTime", busiTemplateConference.getCreateTime().getTime());
                            templateParticipantList.add(modelBeanParticipant);
                        }
                    }
                     */
                    modelBeanTemplateDetails.put("templateParticipants", templateParticipantList);
                    modelBeanParams.put("templateDetails", modelBeanTemplateDetails);

                    busiConferenceAppointment.setParams(modelBeanParams);
                    cas.add(busiConferenceAppointment);
                }
            }
        }

        // 模板会议
        Collection<BaseConferenceContext> valuesTemplate = AllConferenceContextCache.getInstance().values();
        if (valuesTemplate != null) {
            for (BaseConferenceContext conferenceContext : valuesTemplate) {
                boolean isTemplateConference = true;
                if (conferenceContext.isAppointment()) {
                    if (conferenceContext.getConferenceAppointment() != null) {
                        isTemplateConference = false;
                    }
                }
                if (conferenceContext.getTerminalAttendeeMap() != null && isTemplateConference) {
                    boolean isMyConference = false;
                    boolean isMyLiveConference = false;
                    Map<Long, TerminalAttendee> terminalAttendeeMap = conferenceContext.getTerminalAttendeeMap();
                    for (TerminalAttendee terminalAttendee : terminalAttendeeMap.values()) {
                        if (clientId.equals(terminalAttendee.getSn())) {
                            isMyConference = true;
                            break;
                        }
                    }
                    BaseAttendee liveTerminal = conferenceContext.getLiveTerminal(sn);
                    if (liveTerminal != null) {
                        isMyLiveConference = true;
                    }
                    if (isMyConference || isMyLiveConference) {
                        BusiConferenceAppointment busiConferenceAppointment = new BusiConferenceAppointment();
                        ModelBean modelBeanTemplateDetails = new ModelBean();
                        ModelBean modelBeanParams = new ModelBean();

                        String startTimeStr = DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", conferenceContext.getStartTime());
                        Date endTime = DateUtils.getDiffDate(conferenceContext.getStartTime(), conferenceContext.getDurationTime(), TimeUnit.MINUTES);
                        String endTimeStr = DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", endTime);
                        busiConferenceAppointment.setStartTime(startTimeStr);
                        busiConferenceAppointment.setEndTime(endTimeStr);

                        modelBeanParams.put("conferencePassword", conferenceContext.getConferencePassword());
                        modelBeanParams.put("conferenceName", conferenceContext.getName());
                        modelBeanParams.put("conferenceNumber", conferenceContext.getConferenceNumber());
                        List<String> stringList = new ArrayList<>();
                        if (conferenceContext.getIsAutoCreateStreamUrl() == 1) {
                            stringList = conferenceContext.getStreamUrlList();
                        } else {
                            if (StringUtils.isNotEmpty(conferenceContext.getStreamingUrl())) {
                                stringList.add(conferenceContext.getStreamingUrl());
                            }
                        }
                        modelBeanParams.put("liveUrlList", stringList);
                        if (isMyLiveConference) {
                            int nextInt = 0;
                            String streamingUrl = conferenceContext.getStreamingUrl();
                            if (stringList.size() > 0) {
                                nextInt = new Random().nextInt(stringList.size());
                                streamingUrl = stringList.get(nextInt);
                            }
                            modelBeanParams.put("liveUrl", streamingUrl);
                        }
                        modelBeanTemplateDetails.put("isStart", true);
                        busiConferenceAppointment.setStatus(1);

                        ArrayList<ModelBean> templateParticipantList = new ArrayList<>();
                        /* 会议列表中去除参会者列表：数据超长导致mqtt断开
                        for (TerminalAttendee terminalAttendee : terminalAttendeeMap.values()) {
                            ModelBean modelBeanParticipant = new ModelBean();
                            String remoteParty = terminalAttendee.getRemoteParty();
                            String credential = StringUtils.substringBefore(remoteParty, "@");
                            modelBeanParticipant.put("credential", credential);
                            modelBeanParticipant.put("joinConference", terminalAttendee.getAttendType());
                            modelBeanParticipant.put("name", terminalAttendee.getName());
                            modelBeanParticipant.put("onlineStatus", terminalAttendee.getOnlineStatus());
                            modelBeanParticipant.put("createTime", conferenceContext.getStartTime());
                            templateParticipantList.add(modelBeanParticipant);
                        }
                         */
                        modelBeanTemplateDetails.put("templateParticipants", templateParticipantList);
                        modelBeanParams.put("templateDetails", modelBeanTemplateDetails);

                        busiConferenceAppointment.setParams(modelBeanParams);
                        cas.add(busiConferenceAppointment);
                    }
                }
            }
        }

        int fromIndex = page * size;
        int toIndex = fromIndex + size;
        if (toIndex >= cas.size()) {
            toIndex = cas.size();
        }

        pd.setRecords(cas.subList(fromIndex, toIndex));
        pd.setPage(page);
        pd.setSize(size);
        videoConference = JSON.toJSONString(pd);

        if (StringUtils.isNotEmpty(videoConference)) {
            JSONObject object = JSONObject.parseObject(videoConference);

            String action = TerminalTopic.CONFERENCE_LIST_NEW;
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
            this.responseTerminal(terminalTopic, action, object, clientId, messageId);
        }
    }

    @Override
    public void conferencePresentationControl(JSONObject jsonS, String clientId, String messageId) {
        if (null != jsonS) {
            String conferenceNum = jsonS.getString(InstantMeetingParam.CONFERENCENUM);
            Boolean enabledPresentation = jsonS.getBoolean(InstantMeetingParam.ENABLED_PRESENTATION);

            BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
            BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
            if (null != conferenceContext) {
                if (conferenceContext instanceof ConferenceContext) {
                    busiConferenceService.allowAllPresentationContribution(conferenceContext.getId(), enabledPresentation);
                } else if (conferenceContext instanceof McuZjConferenceContext) {
                    busiMcuZjConferenceService.allowAllPresentationContribution(conferenceContext.getId(), enabledPresentation);
                } else if (conferenceContext instanceof McuPlcConferenceContext) {
                    busiMcuPlcConferenceService.allowAllPresentationContribution(conferenceContext.getId(), enabledPresentation);
                } else if (conferenceContext instanceof McuKdcConferenceContext) {
                    busiMcuKdcConferenceService.allowAllPresentationContribution(conferenceContext.getId(), enabledPresentation);
                }
                Map<Long, TerminalAttendee> attendeeMap = conferenceContext.getTerminalAttendeeMap();
                String action = TerminalTopic.ALLOW_ALL_PRESENTATION;
                if (!attendeeMap.isEmpty()) {
                    attendeeMap.forEach((k, v) -> {
                        Long terminalIds = (Long) k;
                        BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalIds);
                        if (null != busiTerminal) {
                            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + busiTerminal.getSn();
                            JSONObject jObj = new JSONObject();
                            jObj.put(InstantMeetingParam.CONFERENCENUM, conferenceNum);
                            jObj.put(InstantMeetingParam.ENABLED_PRESENTATION, enabledPresentation);

                            //给消息发布者发送messageId
                            this.messagePublisherTomessageId(clientId, busiTerminal.getSn(), messageId, terminalTopic, action, jObj);

                        }
                    });
                }
            }
        }
    }

    @Override
    public void conferenceCameraControl(JSONObject jsonS, String clientId) {
        if (null != jsonS) {
            String conferenceNum = jsonS.getString(InstantMeetingParam.CONFERENCENUM);
            String tilt = jsonS.getString(InstantMeetingParam.TILT);
            String focus = jsonS.getString(InstantMeetingParam.FOCUS);
            String zoom = jsonS.getString(InstantMeetingParam.ZOOM);
            Long terminalId = jsonS.getLong(InstantMeetingConversionJson.TERMINAL_ID);
            BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
            BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
            if (null != conferenceContext) {
                JSONObject jsonObject = new JSONObject();
                BaseAttendee attendee = conferenceContext.getAttendeeByTerminalId(terminalId);
                if (attendee != null) {
                    String attendeeId = attendee.getId();
                    if (StringUtils.isNotEmpty(tilt)) {
                        jsonObject.put(InstantMeetingParam.TILT, tilt);
                    } else if (StringUtils.isNotEmpty(focus)) {
                        jsonObject.put(InstantMeetingParam.FOCUS, focus);
                    } else {
                        jsonObject.put(InstantMeetingParam.ZOOM, zoom);
                    }

                    if (conferenceContext instanceof ConferenceContext) {
                        attendeeService.cameraControl(conferenceContext.getId(), attendeeId, jsonObject);
                    } else if (conferenceContext instanceof McuZjConferenceContext) {
                        attendeeForMcuZjService.cameraControl(conferenceContext.getId(), attendeeId, jsonObject);
                    } else if (conferenceContext instanceof McuPlcConferenceContext) {
                        attendeeForMcuPlcService.cameraControl(conferenceContext.getId(), attendeeId, jsonObject);
                    } else if (conferenceContext instanceof McuKdcConferenceContext) {
                        attendeeForMcuKdcService.cameraControl(conferenceContext.getId(), attendeeId, jsonObject);
                    }
                }
            }
        }
    }

    @Override
    public void conferenceDetails(JSONObject jsonS, String clientId, String messageId) {
        String sn = jsonS.getString(MqttConfigConstant.CLIENTID);
        String conferenceNum = jsonS.getString(InstantMeetingParam.CONFERENCENUM);

        String action = TerminalTopic.CONFERENCE_DETAILS;
        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
        ModelBean modelBean = null;

        BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
        BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
        if (conferenceContext != null) {
            if (conferenceContext instanceof ConferenceContext) {
                BusiTemplateConference tc = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(conferenceContext.getTemplateConferenceId());
                modelBean = busiTemplateConferenceService.getTemplateConferenceDetails(tc);
            } else if (conferenceContext instanceof McuZjConferenceContext) {
                BusiMcuZjTemplateConference tc = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(conferenceContext.getTemplateConferenceId());
                modelBean = busiMcuZjTemplateConferenceService.getTemplateConferenceDetails(tc);
            } else if (conferenceContext instanceof McuPlcConferenceContext) {
                BusiMcuPlcTemplateConference tc = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(conferenceContext.getTemplateConferenceId());
                modelBean = busiMcuPlcTemplateConferenceService.getTemplateConferenceDetails(tc);
            } else if (conferenceContext instanceof McuKdcConferenceContext) {
                BusiMcuKdcTemplateConference tc = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(conferenceContext.getTemplateConferenceId());
                modelBean = busiMcuKdcTemplateConferenceService.getTemplateConferenceDetails(tc);
            }
        } else {
            BusiTerminal bySn = TerminalCache.getInstance().getBySn(sn);
            List<Long> terminalIds = new ArrayList<>();
            terminalIds.add(bySn.getId());

            boolean isExist = false;
            if (!isExist) {
                BusiTemplateConference con = new BusiTemplateConference();
                con.getParams().put("conditionTerminalIds", terminalIds);
                List<BusiTemplateConference> tcs = busiTemplateConferenceMapper.selectAllBusiTemplateConferenceList(con);
                if (null != tcs && tcs.size() > 0) {
                    for (BusiTemplateConference tc : tcs) {
                        if (conferenceNum.equals(tc.getConferenceNumber().toString())) {
                            conferenceContext = templateConferenceStartService.buildTemplateConferenceContext(tc.getId());
                            modelBean = busiTemplateConferenceService.getTemplateConferenceDetails(tc);
                            isExist = true;
                            break;
                        }
                    }
                }
            }
            if (!isExist) {
                BusiMcuZjTemplateConference con = new BusiMcuZjTemplateConference();
                con.getParams().put("conditionTerminalIds", terminalIds);
                List<BusiMcuZjTemplateConference> tcs = busiMcuZjTemplateConferenceMapper.selectAllBusiMcuZjTemplateConferenceList(con);
                if (null != tcs && tcs.size() > 0) {
                    for (BusiMcuZjTemplateConference tc : tcs) {
                        if (conferenceNum.equals(tc.getConferenceNumber().toString())) {
                            conferenceContext = busiMcuZjConferenceService.buildTemplateConferenceContext(tc.getId());
                            modelBean = busiMcuZjTemplateConferenceService.getTemplateConferenceDetails(tc);
                            isExist = true;
                            break;
                        }
                    }
                }
            }
            if (!isExist) {
                BusiMcuPlcTemplateConference con = new BusiMcuPlcTemplateConference();
                con.getParams().put("conditionTerminalIds", terminalIds);
                List<BusiMcuPlcTemplateConference> tcs = busiMcuPlcTemplateConferenceMapper.selectAllBusiMcuPlcTemplateConferenceList(con);
                if (null != tcs && tcs.size() > 0) {
                    for (BusiMcuPlcTemplateConference tc : tcs) {
                        if (conferenceNum.equals(tc.getConferenceNumber().toString())) {
                            conferenceContext = busiMcuPlcConferenceService.buildTemplateConferenceContext(tc.getId());
                            modelBean = busiMcuPlcTemplateConferenceService.getTemplateConferenceDetails(tc);
                            isExist = true;
                            break;
                        }
                    }
                }
            }
            if (!isExist) {
                BusiMcuKdcTemplateConference con = new BusiMcuKdcTemplateConference();
                con.getParams().put("conditionTerminalIds", terminalIds);
                List<BusiMcuKdcTemplateConference> tcs = busiMcuKdcTemplateConferenceMapper.selectAllBusiMcuKdcTemplateConferenceList(con);
                if (null != tcs && tcs.size() > 0) {
                    for (BusiMcuKdcTemplateConference tc : tcs) {
                        if (conferenceNum.equals(tc.getConferenceNumber().toString())) {
                            conferenceContext = busiMcuKdcConferenceService.buildTemplateConferenceContext(tc.getId());
                            modelBean = busiMcuKdcTemplateConferenceService.getTemplateConferenceDetails(tc);
                            isExist = true;
                            break;
                        }
                    }
                }
            }
        }

        if (modelBean != null) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("supportRollCall", conferenceContext.isSupportRollCall());
            jsonObj.put("supportSplitScreen", conferenceContext.isSupportSplitScreen());
            jsonObj.put("supportPolling", conferenceContext.isSupportPolling());
            jsonObj.put("supportChooseSee", conferenceContext.isSupportChooseSee());
            jsonObj.put("supportTalk", conferenceContext.isSupportTalk());
            jsonObj.put("supportBroadcast", conferenceContext.isSupportBroadcast());
            jsonObj.put("singleView", conferenceContext.isSingleView());
            List<ModelBean> speakerSplitScreenList = conferenceContext.getSpeakerSplitScreenList();
            for (int i = 0; i < speakerSplitScreenList.size(); i++) {
                ModelBean modelBeanSplitScreen = speakerSplitScreenList.get(i);
                if (i == 0) {
                    modelBeanSplitScreen.put("isDefault", true);
                } else {
                    modelBeanSplitScreen.put("isDefault", false);
                }
            }
            jsonObj.put("layoutList", speakerSplitScreenList);

            if (modelBean != null) {
                JSONObject jsonObjDetails = new JSONObject();
                ModelBean modelBeanTemplateDetails = new ModelBean();
                modelBeanTemplateDetails.putAll(modelBean);
                jsonObjDetails.put("templateDetails", modelBeanTemplateDetails);
                jsonObj.put("params", jsonObjDetails);
                Object masterTerminalIdObj = modelBeanTemplateDetails.get("templateConference");
                if (null != masterTerminalIdObj) {
                    JSONObject json = (JSONObject) JSONObject.toJSON(masterTerminalIdObj);
                    masterTerminalIdObj = json.get("masterTerminalId");
                    json.put("presenter", conferenceContext.getPresenter());
                    Long masterTerminalId = null;
                    try {
                        masterTerminalId = Long.parseLong(masterTerminalIdObj.toString());
                    } catch (Exception e) {
                    }

                    if (null != masterTerminalId) {
                        BusiTerminal busiTerminal = TerminalCache.getInstance().get(masterTerminalId);
                        if (null != busiTerminal) {
                            jsonObj.put(MqttConfigConstant.CLIENTID, busiTerminal.getSn());
                        }
                    } else {
                        jsonObj.put(MqttConfigConstant.CLIENTID, "");
                    }
                    jsonObj.put("recordingEnabled", json.getString("recordingEnabled"));
                }

            }

            this.responseTerminal(terminalTopic, action, jsonObj, clientId, messageId);
        }
    }

    @Override
    public void getFuzzyQueryTerminal(JSONObject jsonS, String clientId, String messageId) {
        if (null != jsonS) {
            JSONObject object = new JSONObject();
            String searchKey = jsonS.getString("credential");//变为searchKey
            if (jsonS.containsKey("searchKey")) {
                searchKey = jsonS.getString("searchKey");
            }
            Integer businessFieldType = jsonS.getInteger("businessFieldType");
            String action = TerminalTopic.FUZZY_QUERY_TERMINAL;
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;

            BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(clientId);
            if (busiTerminalSn != null) {
                Long deptId = busiTerminalSn.getDeptId();
                JSONArray jsonArray = new JSONArray();

                //获取子部门的deptId
                List<Long> deptIds = this.getChildDeptId(deptId);
                if (null != deptIds && deptIds.size() > 0) {
                    PageHelper.startPage(1, 100);
                    BusiTerminal busiTerminalCon = new BusiTerminal();
                    Map<String, Object> params = new HashMap<>();
                    params.put("deptIds", deptIds);
                    params.put("searchKey", searchKey);
                    busiTerminalCon.setParams(params);
                    busiTerminalCon.setBusinessFieldType(businessFieldType);
                    List<BusiTerminal> terminalList = busiTerminalMapper.selectBusiTerminalList(busiTerminalCon);
                    LOGGER.info("================> terminalList " + terminalList.toString());
                    if (null != terminalList && terminalList.size() > 0) {
                        for (int i = 0; i < terminalList.size(); i++) {
                            BusiTerminal busiTerminalTemp = terminalList.get(i);
                            JSONObject jObjs = new JSONObject();

                            jObjs.put("createUserId", busiTerminalTemp.getCreateUserId());
                            jObjs.put("fsServerId", busiTerminalTemp.getFsServerId());
                            jObjs.put("businessFieldType", busiTerminalTemp.getBusinessFieldType());
                            jObjs.put("ip", busiTerminalTemp.getIp());
                            jObjs.put("onlineStatus", busiTerminalTemp.getOnlineStatus());
                            jObjs.put("deptId", busiTerminalTemp.getDeptId());
                            jObjs.put("mqttOnlineStatus", busiTerminalTemp.getMqttOnlineStatus());
                            jObjs.put("createUserName", busiTerminalTemp.getCreateUserName());
                            jObjs.put("number", busiTerminalTemp.getNumber());
                            jObjs.put("password", busiTerminalTemp.getPassword());
                            jObjs.put("credential", busiTerminalTemp.getCredential());
                            jObjs.put("name", busiTerminalTemp.getName());
                            jObjs.put("id", busiTerminalTemp.getId());
                            jObjs.put("sn", busiTerminalTemp.getSn());

                            jsonArray.add(jObjs);
                        }

                        object.put("records", jsonArray);
                    }
                }
            }
            this.responseTerminal(terminalTopic, action, object, clientId, messageId);
        }
    }

    private List<Long> getChildDeptId(Long deptId) {
        List<Long> deptIds = new ArrayList<Long>();
        deptIds.add(deptId);
        SysDept sysDept = new SysDept();
        List<SysDept> deptList = deptService.selectDeptList(sysDept);
        if (null != deptList && deptList.size() > 0) {
            SysDept dept = new SysDept();
            dept.setDeptId(deptId);
            List<SysDept> childList = this.getChildList(deptList, dept);
            if (null != childList && childList.size() > 0) {
                for (SysDept sysDept2 : childList) {
                    deptIds.add(sysDept2.getDeptId());
                }
            }

        }
        return deptIds;
    }


    private List<SysDept> getChildList(List<SysDept> deptList, SysDept dept) {
        List<SysDept> tlist = new ArrayList<SysDept>();
        Iterator<SysDept> it = deptList.iterator();
        while (it.hasNext()) {
            SysDept n = (SysDept) it.next();
            if (null != n.getParentId() && n.getAncestors().contains(dept.getDeptId().toString())) {
                tlist.add(n);
            }
        }

        return tlist;
    }


    @Override
    public void conferenceDiscuss(JSONObject jsonS, String clientId, String messageId) {
        String conferenceNum = (String) jsonS.get(MqttConfigConstant.CONFERENCENUM);
        if (StringUtils.isNotEmpty(conferenceNum)) {
            BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
            BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
            if (conferenceContext != null) {
                if (conferenceContext instanceof ConferenceContext) {
                    conferenceDiscussForFme(jsonS, clientId, messageId, (ConferenceContext) conferenceContext);
                } else if (conferenceContext instanceof McuZjConferenceContext) {
                    conferenceDiscussForZj(jsonS, clientId, messageId, (McuZjConferenceContext) conferenceContext);
                } else if (conferenceContext instanceof McuPlcConferenceContext) {
                    conferenceDiscussForPlc(jsonS, clientId, messageId, (McuPlcConferenceContext) conferenceContext);
                } else if (conferenceContext instanceof McuKdcConferenceContext) {
                    conferenceDiscussForKdc(jsonS, clientId, messageId, (McuKdcConferenceContext) conferenceContext);
                }
            }
        }

    }

    private void conferenceDiscussForFme(JSONObject jsonS, String clientId, String messageId, ConferenceContext conferenceContext) {
        if (null != jsonS) {
            String action = TerminalTopic.DISCUSS;
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
            String discussCommand = jsonS.getString("discussCommand");
            String conferenceNum = jsonS.getString(MqttConfigConstant.CONFERENCENUM);
            if (StringUtils.isNotEmpty(discussCommand) && "*6".equals(discussCommand)) {
                LOGGER.info("=======================================> 会场进行讨论" + conferenceNum);
                RestResponse restResponse = videoConferenceSDKService.discuss(conferenceContext);
                if (restResponse.isSuccess()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(MqttConfigConstant.CONFERENCENUM, conferenceNum);
                    this.responseTerminal(terminalTopic, action, jsonObject, clientId, messageId);
                }
            } else {
                LOGGER.info("=======================================> 会场进行讨论" + conferenceNum);
                RestResponse restResponse = videoConferenceSDKService.discuss(conferenceContext);
                if (restResponse.isSuccess()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(MqttConfigConstant.CONFERENCENUM, conferenceNum);
                    this.responseTerminal(terminalTopic, action, jsonObject, clientId, messageId);
                }
            }
        }
    }

    private void conferenceDiscussForZj(JSONObject jsonS, String clientId, String messageId, McuZjConferenceContext conferenceContext) {
        if (null != jsonS) {
            String action = TerminalTopic.DISCUSS;
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
            String discussCommand = jsonS.getString("discussCommand");
            String conferenceNum = jsonS.getString(MqttConfigConstant.CONFERENCENUM);
            if (StringUtils.isNotEmpty(discussCommand) && "*6".equals(discussCommand)) {
                LOGGER.info("=======================================> 会场进行讨论" + conferenceNum);
                RestResponse restResponse = simpleConferenceControlForMcuZjService.discuss(conferenceContext);
                if (restResponse.isSuccess()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(MqttConfigConstant.CONFERENCENUM, conferenceNum);
                    this.responseTerminal(terminalTopic, action, jsonObject, clientId, messageId);
                }
            } else {
                LOGGER.info("=======================================> 会场进行讨论" + conferenceNum);
                RestResponse restResponse = simpleConferenceControlForMcuZjService.discuss(conferenceContext);
                if (restResponse.isSuccess()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(MqttConfigConstant.CONFERENCENUM, conferenceNum);
                    this.responseTerminal(terminalTopic, action, jsonObject, clientId, messageId);
                }
            }
        }
    }

    private void conferenceDiscussForPlc(JSONObject jsonS, String clientId, String messageId, McuPlcConferenceContext conferenceContext) {
        if (null != jsonS) {
            String action = TerminalTopic.DISCUSS;
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
            String discussCommand = jsonS.getString("discussCommand");
            String conferenceNum = jsonS.getString(MqttConfigConstant.CONFERENCENUM);
            if (StringUtils.isNotEmpty(discussCommand) && "*6".equals(discussCommand)) {
                LOGGER.info("=======================================> 会场进行讨论" + conferenceNum);
                RestResponse restResponse = simpleConferenceControlForMcuPlcService.discuss(conferenceContext);
                if (restResponse.isSuccess()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(MqttConfigConstant.CONFERENCENUM, conferenceNum);
                    this.responseTerminal(terminalTopic, action, jsonObject, clientId, messageId);
                }
            } else {
                LOGGER.info("=======================================> 会场进行讨论" + conferenceNum);
                RestResponse restResponse = simpleConferenceControlForMcuPlcService.discuss(conferenceContext);
                if (restResponse.isSuccess()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(MqttConfigConstant.CONFERENCENUM, conferenceNum);
                    this.responseTerminal(terminalTopic, action, jsonObject, clientId, messageId);
                }
            }
        }
    }

    private void conferenceDiscussForKdc(JSONObject jsonS, String clientId, String messageId, McuKdcConferenceContext conferenceContext) {
        if (null != jsonS) {
            String action = TerminalTopic.DISCUSS;
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
            String discussCommand = jsonS.getString("discussCommand");
            String conferenceNum = jsonS.getString(MqttConfigConstant.CONFERENCENUM);
            if (StringUtils.isNotEmpty(discussCommand) && "*6".equals(discussCommand)) {
                LOGGER.info("=======================================> 会场进行讨论" + conferenceNum);
                RestResponse restResponse = simpleConferenceControlForMcuKdcService.discuss(conferenceContext);
                if (restResponse.isSuccess()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(MqttConfigConstant.CONFERENCENUM, conferenceNum);
                    this.responseTerminal(terminalTopic, action, jsonObject, clientId, messageId);
                }
            } else {
                LOGGER.info("=======================================> 会场进行讨论" + conferenceNum);
                RestResponse restResponse = simpleConferenceControlForMcuKdcService.discuss(conferenceContext);
                if (restResponse.isSuccess()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(MqttConfigConstant.CONFERENCENUM, conferenceNum);
                    this.responseTerminal(terminalTopic, action, jsonObject, clientId, messageId);
                }
            }
        }
    }

    @Override
    public void backDefaultLayout(JSONObject jsonS, String clientId, String messageId) {
        String conferenceNum = (String) jsonS.get(MqttConfigConstant.CONFERENCENUM);
        if (StringUtils.isNotEmpty(conferenceNum)) {
            BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
            BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
            if (conferenceContext != null) {
                if (conferenceContext instanceof ConferenceContext) {
                    backDefaultLayoutForFme(jsonS, clientId, messageId, (ConferenceContext) conferenceContext);
                } else if (conferenceContext instanceof McuZjConferenceContext) {
                    backDefaultLayoutForZj(jsonS, clientId, messageId, (McuZjConferenceContext) conferenceContext);
                } else if (conferenceContext instanceof McuPlcConferenceContext) {
                    backDefaultLayoutForPlc(jsonS, clientId, messageId, (McuPlcConferenceContext) conferenceContext);
                } else if (conferenceContext instanceof McuKdcConferenceContext) {
                    backDefaultLayoutForKdc(jsonS, clientId, messageId, (McuKdcConferenceContext) conferenceContext);
                }
            }
        }
    }

    private void backDefaultLayoutForFme(JSONObject jsonS, String clientId, String messageId, ConferenceContext conferenceContext) {
        if (null != jsonS) {
            String action = TerminalTopic.BACK_DEFAULT_LAYOUT;
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
            String layoutCommand = jsonS.getString("layoutCommand");
            String conferenceNum = jsonS.getString(MqttConfigConstant.CONFERENCENUM);
            if (StringUtils.isNotEmpty(layoutCommand) && "*9".equals(layoutCommand)) {
                LOGGER.info("=======================================> 返回上次默认布局" + conferenceNum);
                RestResponse restResponse = videoConferenceSDKService.backToDisplayLayout(conferenceContext);
                if (restResponse.isSuccess()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(MqttConfigConstant.CONFERENCENUM, conferenceNum);
                    this.responseTerminal(terminalTopic, action, jsonObject, clientId, messageId);
                }
            }
        }
    }

    private void backDefaultLayoutForZj(JSONObject jsonS, String clientId, String messageId, McuZjConferenceContext conferenceContext) {
        if (null != jsonS) {
            String action = TerminalTopic.BACK_DEFAULT_LAYOUT;
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
            String layoutCommand = jsonS.getString("layoutCommand");
            String conferenceNum = jsonS.getString(MqttConfigConstant.CONFERENCENUM);
            if (StringUtils.isNotEmpty(layoutCommand) && "*9".equals(layoutCommand)) {
                LOGGER.info("=======================================> 返回上次默认布局" + conferenceNum);
                RestResponse restResponse = simpleConferenceControlForMcuZjService.recoveryLastDefaultView(conferenceContext);
                if (restResponse.isSuccess()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(MqttConfigConstant.CONFERENCENUM, conferenceNum);
                    this.responseTerminal(terminalTopic, action, jsonObject, clientId, messageId);
                }
            }
        }
    }

    private void backDefaultLayoutForPlc(JSONObject jsonS, String clientId, String messageId, McuPlcConferenceContext conferenceContext) {
        if (null != jsonS) {
            String action = TerminalTopic.BACK_DEFAULT_LAYOUT;
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
            String layoutCommand = jsonS.getString("layoutCommand");
            String conferenceNum = jsonS.getString(MqttConfigConstant.CONFERENCENUM);
            if (StringUtils.isNotEmpty(layoutCommand) && "*9".equals(layoutCommand)) {
                LOGGER.info("=======================================> 返回上次默认布局" + conferenceNum);
                RestResponse restResponse = simpleConferenceControlForMcuPlcService.recoveryLastDefaultView(conferenceContext);
                if (restResponse.isSuccess()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(MqttConfigConstant.CONFERENCENUM, conferenceNum);
                    this.responseTerminal(terminalTopic, action, jsonObject, clientId, messageId);
                }
            }
        }
    }

    private void backDefaultLayoutForKdc(JSONObject jsonS, String clientId, String messageId, McuKdcConferenceContext conferenceContext) {
        if (null != jsonS) {
            String action = TerminalTopic.BACK_DEFAULT_LAYOUT;
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
            String layoutCommand = jsonS.getString("layoutCommand");
            String conferenceNum = jsonS.getString(MqttConfigConstant.CONFERENCENUM);
            if (StringUtils.isNotEmpty(layoutCommand) && "*9".equals(layoutCommand)) {
                LOGGER.info("=======================================> 返回上次默认布局" + conferenceNum);
                RestResponse restResponse = simpleConferenceControlForMcuKdcService.recoveryLastDefaultView(conferenceContext);
                if (restResponse.isSuccess()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(MqttConfigConstant.CONFERENCENUM, conferenceNum);
                    this.responseTerminal(terminalTopic, action, jsonObject, clientId, messageId);
                }
            }
        }
    }

    @Override
    public void interactiveRaiseHand(JSONObject jsonS, String clientId, String messageId) {
        try {
            String conferenceNum = (String) jsonS.get(MqttConfigConstant.CONFERENCENUM);
            if (StringUtils.isNotEmpty(conferenceNum)) {
                BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
                BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
                if (conferenceContext != null) {
                    if (conferenceContext instanceof ConferenceContext) {
                        interactiveRaiseHandFme(jsonS, clientId, messageId, (ConferenceContext) conferenceContext);
                    } else if (conferenceContext instanceof McuZjConferenceContext) {
                        interactiveRaiseHandForZj(jsonS, clientId, messageId, (McuZjConferenceContext) conferenceContext);
                    } else if (conferenceContext instanceof McuPlcConferenceContext) {
                        interactiveRaiseHandForPlc(jsonS, clientId, messageId, (McuPlcConferenceContext) conferenceContext);
                    } else if (conferenceContext instanceof McuKdcConferenceContext) {
                        interactiveRaiseHandForKdc(jsonS, clientId, messageId, (McuKdcConferenceContext) conferenceContext);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("interactiveRaiseHand ++ == ", e);
        }
    }

    /**
     * FME 互动举手
     *
     * @param jsonS
     * @param clientId
     * @param messageId
     */
    public void interactiveRaiseHandFme(JSONObject jsonS, String clientId, String messageId, ConferenceContext conferenceContext) {
        if (null != jsonS) {
            String action = TerminalTopic.INTERACTIVE_RAISE_HAND;
            String conferenceNum = (String) jsonS.get(MqttConfigConstant.CONFERENCENUM);
            String sn = (String) jsonS.get(MqttConfigConstant.CLIENTID);
            Boolean isCancle = jsonS.getBooleanValue("isCancle");
            LOGGER.info("====================================>interactiveRaiseHandFme");
            BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(sn);
            if (busiTerminalSn != null) {
                if (null != conferenceContext) {
                    String attendeeId = null;
                    RestResponse response = null;
                    JSONObject jsonObject = new JSONObject();
                    String remoteParty = TerminalCache.getInstance().getRemoteParty(busiTerminalSn);
                    Map<String, Attendee> attendeeMapByUri = conferenceContext.getUuidAttendeeMapByUri(remoteParty);
                    for (String uri : attendeeMapByUri.keySet()) {
                        Attendee attendee = attendeeMapByUri.get(uri);
                        if (remoteParty.equals(attendee.getRemoteParty())) {
                            attendeeId = attendee.getId();
                            break;
                        }
                    }

                    LOGGER.info("====================================>attendeeId:" + attendeeId);
                    if (StringUtils.isNotEmpty(attendeeId)) {
                        if (isCancle) {
                            jsonObject.put("flag", "normal");
                            response = videoConferenceSDKService.raiseHand(conferenceContext, attendeeId, RaiseHandStatus.YES);
                        } else {
                            jsonObject.put("flag", "timeOut");
                            response = videoConferenceSDKService.raiseHand(conferenceContext, attendeeId, RaiseHandStatus.NO);
                        }

                        if (response.isSuccess()) {
                            LOGGER.info("====================================>terminalr:" + busiTerminalSn.toString());
                            if (busiTerminalSn.getSn() != null) {
                                String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + busiTerminalSn.getSn();
                                jsonObject.put(MqttConfigConstant.CLIENTID, sn);
                                jsonObject.put(MqttConfigConstant.CONFERENCENUM, conferenceNum);
                                this.responseTerminal(terminalTopic, action, jsonObject, clientId, messageId);
                            }
                        }
                    }

                    sendConferenceInfoToPushTargetTerminal(conferenceContext);
                }
            }
        }
    }

    /**
     * Zj互动举手
     *
     * @param jsonS
     * @param clientId
     * @param messageId
     */
    private void interactiveRaiseHandForZj(JSONObject jsonS, String clientId, String messageId, McuZjConferenceContext conferenceContext) {
        if (jsonS != null) {
            String action = TerminalTopic.INTERACTIVE_RAISE_HAND;
            String conferenceNum = (String) jsonS.get(MqttConfigConstant.CONFERENCENUM);
            String sn = (String) jsonS.get(MqttConfigConstant.CLIENTID);
            Boolean isCancle = (Boolean) jsonS.getBooleanValue("isCancle");
            LOGGER.info("====================================>&&&&0000" + jsonS.toString());
            LOGGER.info("====================================>&&&&01010" + isCancle);
            BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(sn);
            if (busiTerminalSn != null) {
                if (null != conferenceContext) {
                    String attendeeId = null;
                    RestResponse response = null;
                    JSONObject jsonObject = new JSONObject();
                    String remoteParty = TerminalCache.getInstance().getRemoteParty(busiTerminalSn);
                    Map<String, AttendeeForMcuZj> attendeeMapByUri = conferenceContext.getAttendeeMapByUri(remoteParty);
                    if (attendeeMapByUri != null) {
                        for (String uri : attendeeMapByUri.keySet()) {
                            AttendeeForMcuZj attendee = attendeeMapByUri.get(uri);
                            if (remoteParty.equals(attendee.getRemoteParty())) {
                                attendeeId = attendee.getId();
                            }
                        }
                    }
                    if (StringUtils.isNotEmpty(attendeeId)) {
                        if (isCancle) {
                            LOGGER.info("====================================>&&&&6666" + attendeeId);
                            jsonObject.put("flag", "normal");
                            response = simpleConferenceControlForMcuZjService.raiseHand(conferenceContext, attendeeId, RaiseHandStatus.YES);
                        } else {
                            jsonObject.put("flag", "timeOut");
                            response = simpleConferenceControlForMcuZjService.raiseHand(conferenceContext, attendeeId, RaiseHandStatus.NO);
                        }

                        if (response.isSuccess()) {
                            LOGGER.info("====================================>&&&& terminalr^^^" + busiTerminalSn.toString());
                            if (busiTerminalSn.getSn() != null) {
                                String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + busiTerminalSn.getSn();
                                jsonObject.put(MqttConfigConstant.CLIENTID, sn);
                                jsonObject.put(MqttConfigConstant.CONFERENCENUM, conferenceNum);
                                this.responseTerminal(terminalTopic, action, jsonObject, clientId, messageId);
                            }
                        }
                    }
                    sendConferenceInfoToPushTargetTerminal(conferenceContext);
                }
            }
        }
    }

    /**
     * plc互动举手
     *
     * @param jsonS
     * @param clientId
     * @param messageId
     */
    private void interactiveRaiseHandForPlc(JSONObject jsonS, String clientId, String messageId, McuPlcConferenceContext conferenceContext) {
        if (jsonS != null) {
            String action = TerminalTopic.INTERACTIVE_RAISE_HAND;
            String conferenceNum = (String) jsonS.get(MqttConfigConstant.CONFERENCENUM);
            String sn = (String) jsonS.get(MqttConfigConstant.CLIENTID);
            Boolean isCancle = (Boolean) jsonS.getBooleanValue("isCancle");
            LOGGER.info("====================================>&&&&0000" + jsonS.toString());
            LOGGER.info("====================================>&&&&01010" + isCancle);
            BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(sn);
            if (busiTerminalSn != null) {
                if (null != conferenceContext) {
                    String attendeeId = null;
                    RestResponse response = null;
                    JSONObject jsonObject = new JSONObject();
                    String remoteParty = TerminalCache.getInstance().getRemoteParty(busiTerminalSn);
                    Map<String, AttendeeForMcuPlc> attendeeMapByUri = conferenceContext.getAttendeeMapByUri(remoteParty);
                    if (attendeeMapByUri != null) {
                        for (String uri : attendeeMapByUri.keySet()) {
                            AttendeeForMcuPlc attendee = attendeeMapByUri.get(uri);
                            if (remoteParty.equals(attendee.getRemoteParty())) {
                                attendeeId = attendee.getId();
                            }
                        }
                    }
                    if (StringUtils.isNotEmpty(attendeeId)) {
                        if (isCancle) {
                            LOGGER.info("====================================>&&&&6666" + attendeeId);
                            jsonObject.put("flag", "normal");
                            response = simpleConferenceControlForMcuPlcService.raiseHand(conferenceContext, attendeeId, RaiseHandStatus.YES);
                        } else {
                            jsonObject.put("flag", "timeOut");
                            response = simpleConferenceControlForMcuPlcService.raiseHand(conferenceContext, attendeeId, RaiseHandStatus.NO);
                        }

                        if (response.isSuccess()) {
                            LOGGER.info("====================================>&&&& terminalr^^^" + busiTerminalSn.toString());
                            if (busiTerminalSn.getSn() != null) {
                                String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + busiTerminalSn.getSn();
                                jsonObject.put(MqttConfigConstant.CLIENTID, sn);
                                jsonObject.put(MqttConfigConstant.CONFERENCENUM, conferenceNum);
                                this.responseTerminal(terminalTopic, action, jsonObject, clientId, messageId);
                            }
                        }
                    }
                    sendConferenceInfoToPushTargetTerminal(conferenceContext);
                }
            }
        }
    }

    /**
     * kdc互动举手
     *
     * @param jsonS
     * @param clientId
     * @param messageId
     */
    private void interactiveRaiseHandForKdc(JSONObject jsonS, String clientId, String messageId, McuKdcConferenceContext conferenceContext) {
        if (jsonS != null) {
            String action = TerminalTopic.INTERACTIVE_RAISE_HAND;
            String conferenceNum = (String) jsonS.get(MqttConfigConstant.CONFERENCENUM);
            String sn = (String) jsonS.get(MqttConfigConstant.CLIENTID);
            Boolean isCancle = (Boolean) jsonS.getBooleanValue("isCancle");
            LOGGER.info("====================================>&&&&0000" + jsonS.toString());
            LOGGER.info("====================================>&&&&01010" + isCancle);
            BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(sn);
            if (busiTerminalSn != null) {
                if (null != conferenceContext) {
                    String attendeeId = null;
                    RestResponse response = null;
                    JSONObject jsonObject = new JSONObject();
                    String remoteParty = TerminalCache.getInstance().getRemoteParty(busiTerminalSn);
                    Map<String, AttendeeForMcuKdc> attendeeMapByUri = conferenceContext.getAttendeeMapByUri(remoteParty);
                    if (attendeeMapByUri != null) {
                        for (String uri : attendeeMapByUri.keySet()) {
                            AttendeeForMcuKdc attendee = attendeeMapByUri.get(uri);
                            if (remoteParty.equals(attendee.getRemoteParty())) {
                                attendeeId = attendee.getId();
                            }
                        }
                    }
                    if (StringUtils.isNotEmpty(attendeeId)) {
                        if (isCancle) {
                            LOGGER.info("====================================>&&&&6666" + attendeeId);
                            jsonObject.put("flag", "normal");
                            response = simpleConferenceControlForMcuKdcService.raiseHand(conferenceContext, attendeeId, RaiseHandStatus.YES);
                        } else {
                            jsonObject.put("flag", "timeOut");
                            response = simpleConferenceControlForMcuKdcService.raiseHand(conferenceContext, attendeeId, RaiseHandStatus.NO);
                        }

                        if (response.isSuccess()) {
                            LOGGER.info("====================================>&&&& terminalr^^^" + busiTerminalSn.toString());
                            if (busiTerminalSn.getSn() != null) {
                                String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + busiTerminalSn.getSn();
                                jsonObject.put(MqttConfigConstant.CLIENTID, sn);
                                jsonObject.put(MqttConfigConstant.CONFERENCENUM, conferenceNum);
                                this.responseTerminal(terminalTopic, action, jsonObject, clientId, messageId);
                            }
                        }
                    }
                    sendConferenceInfoToPushTargetTerminal(conferenceContext);
                }
            }
        }
    }

    @Override
    public void terminalDialogue(JSONObject jsonS, String clientId, String messageId) {
        if (null != jsonS) {
            String action = TerminalTopic.DIALOGUE;
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
            String dialogueCommand = jsonS.getString("dialogueCommand");
            String conferenceNum = jsonS.getString(MqttConfigConstant.CONFERENCENUM);
            BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
            BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
            if (conferenceContext != null) {
                if (StringUtils.isNotEmpty(dialogueCommand) && dialogueCommand.contains("*7")) {
                    LOGGER.info("=======================================> 会场对话" + conferenceNum);

                    String[] talkSp = dialogueCommand.split("\\*7");
                    if (talkSp.length > 0) {
                        RestResponse restResponse = null;
                        if (conferenceContext instanceof ConferenceContext) {
                            restResponse = videoConferenceSDKService.talk((ConferenceContext) conferenceContext, Integer.valueOf(talkSp[1]));
                        } else if (conferenceContext instanceof McuZjConferenceContext) {
                            McuZjConferenceContext mcuZjConferenceContext = (McuZjConferenceContext) conferenceContext;
                            AtomicInteger ai = new AtomicInteger();
                            GenericValue<AttendeeForMcuZj> val = new GenericValue<>();
                            McuZjConferenceContextUtils.eachNonMcuAttendeeInConference(mcuZjConferenceContext, (a) -> {
                                if (a.isMeetingJoined() && ai.incrementAndGet() == Integer.valueOf(talkSp[1])) {
                                    val.setValue(a);
                                }
                            });
                            restResponse = simpleConferenceControlForMcuZjService.talk(mcuZjConferenceContext, val.getValue().getId());
                        } else if (conferenceContext instanceof McuPlcConferenceContext) {
                            McuPlcConferenceContext mcuPlcConferenceContext = (McuPlcConferenceContext) conferenceContext;
                            AtomicInteger ai = new AtomicInteger();
                            GenericValue<AttendeeForMcuPlc> val = new GenericValue<>();
                            McuPlcConferenceContextUtils.eachNonMcuAttendeeInConference(mcuPlcConferenceContext, (a) -> {
                                if (a.isMeetingJoined() && ai.incrementAndGet() == Integer.valueOf(talkSp[1])) {
                                    val.setValue(a);
                                }
                            });
                            restResponse = simpleConferenceControlForMcuPlcService.talk(mcuPlcConferenceContext, val.getValue().getId());
                        } else if (conferenceContext instanceof McuKdcConferenceContext) {
                            McuKdcConferenceContext mcuKdcConferenceContext = (McuKdcConferenceContext) conferenceContext;
                            AtomicInteger ai = new AtomicInteger();
                            GenericValue<AttendeeForMcuKdc> val = new GenericValue<>();
                            McuKdcConferenceContextUtils.eachNonMcuAttendeeInConference(mcuKdcConferenceContext, (a) -> {
                                if (a.isMeetingJoined() && ai.incrementAndGet() == Integer.valueOf(talkSp[1])) {
                                    val.setValue(a);
                                }
                            });
                            restResponse = simpleConferenceControlForMcuKdcService.talk(mcuKdcConferenceContext, val.getValue().getId());
                        }
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(MqttConfigConstant.CONFERENCENUM, conferenceNum);
                        if (restResponse != null && restResponse.isSuccess()) {
                            jsonObject.put("isDialogue", true);
                        } else {
                            jsonObject.put("isDialogue", false);
                        }

                        this.responseTerminal(terminalTopic, action, jsonObject, clientId, messageId);
                    }
                }
            }
        }
    }

    @Override
    public void askSipAccountCondition(JSONObject jsonS, String clientId, String messageId) {
        if (null != jsonS) {
            JSONObject jsonObject = new JSONObject();
            String action = TerminalTopic.ASK_SIP_ACCOUNT_CONDITION;
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
            String sipUserName = jsonS.getString("sipUserName");
            String terminalType = jsonS.getString("terminalType");

            if (terminalType.equals("topBox")) {
                BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(clientId);
                if (busiTerminalSn != null) {
                    boolean isInTemPlate = false;
                    if (!isInTemPlate) {
                        BusiTemplateParticipant busiTemplateParticipant = new BusiTemplateParticipant();
                        busiTemplateParticipant.setTerminalId(busiTerminalSn.getId());
                        List<BusiTemplateParticipant> busiTemplateParticipantList = busiTemplateParticipantMapper.selectBusiTemplateParticipantList(busiTemplateParticipant);
                        if (null != busiTemplateParticipantList && busiTemplateParticipantList.size() > 0) {
                            isInTemPlate = true;
                        }
                    }
                    if (!isInTemPlate) {
                        BusiMcuZjTemplateParticipant busiTemplateParticipant = new BusiMcuZjTemplateParticipant();
                        busiTemplateParticipant.setTerminalId(busiTerminalSn.getId());
                        List<BusiMcuZjTemplateParticipant> busiTemplateParticipantList = busiMcuZjTemplateParticipantMapper.selectBusiMcuZjTemplateParticipantList(busiTemplateParticipant);
                        if (null != busiTemplateParticipantList && busiTemplateParticipantList.size() > 0) {
                            isInTemPlate = true;
                        }
                    }
                    if (!isInTemPlate) {
                        BusiMcuPlcTemplateParticipant busiTemplateParticipant = new BusiMcuPlcTemplateParticipant();
                        busiTemplateParticipant.setTerminalId(busiTerminalSn.getId());
                        List<BusiMcuPlcTemplateParticipant> busiTemplateParticipantList = busiMcuPlcTemplateParticipantMapper.selectBusiMcuPlcTemplateParticipantList(busiTemplateParticipant);
                        if (null != busiTemplateParticipantList && busiTemplateParticipantList.size() > 0) {
                            isInTemPlate = true;
                        }
                    }
                    if (!isInTemPlate) {
                        BusiMcuKdcTemplateParticipant busiTemplateParticipant = new BusiMcuKdcTemplateParticipant();
                        busiTemplateParticipant.setTerminalId(busiTerminalSn.getId());
                        List<BusiMcuKdcTemplateParticipant> busiTemplateParticipantList = busiMcuKdcTemplateParticipantMapper.selectBusiMcuKdcTemplateParticipantList(busiTemplateParticipant);
                        if (null != busiTemplateParticipantList && busiTemplateParticipantList.size() > 0) {
                            isInTemPlate = true;
                        }
                    }
                    if (isInTemPlate) {
                        jsonObject.put("inTemPlate", true);
                    } else {
                        BusiTerminal buTerminal = new BusiTerminal();
                        buTerminal.setCredential(sipUserName);
                        List<BusiTerminal> terminalLists = busiTerminalMapper.selectBusiTerminalList(buTerminal);
                        if (null != terminalLists && terminalLists.size() > 0) {
                            BusiTerminal terminal = terminalLists.get(0);
                            if (null != terminal.getSn()) {
                                if (terminal.getSn().equals(clientId)) {

                                    //一致
                                    jsonObject.put("inconsistentSipAccount", true);

                                } else {

                                    //不一致
                                    jsonObject.put("inconsistentSipAccount", false);
                                }
                            } else {

                                //不一致
                                jsonObject.put("inconsistentSipAccount", false);
                            }


                            BusiTerminal previousTerminal = TerminalCache.getInstance().getBySn(clientId);
                            if (previousTerminal != null) {
                                jsonObject.put("previousSipUserName", previousTerminal.getCredential()); //以前的sipAccount
                            }

                            jsonObject.put("currentSipUserName", sipUserName); //现在的sipAccount
                            jsonObject.put(MqttConfigConstant.CLIENTID, clientId);
                        }
                    }
                }

                this.responseTerminal(terminalTopic, action, jsonObject, clientId, messageId);
            }
        }

    }

    @Override
    public void unbindTerminalSipAccount(JSONObject jsonS, String clientId, String messageId) {
        if (null != jsonS) {
//			String action = TerminalTopic.UNBIND_SIP_ACCOUNT;
//			String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
            String sipUserName = jsonS.getString("sipUserName");
            String terminalType = jsonS.getString("terminalType");
            Boolean isAgree = jsonS.getBoolean("isAgree");
            if (isAgree) {

                //删除注册表里面的终端
                this.updateRegisterTerminal(clientId, messageId, terminalType, sipUserName);
            }
        }
    }

    private void updateRegisterTerminal(String clientId, String messageId, String terminalType, String sipUserName) {
        List<BusiTerminal> list = new ArrayList<BusiTerminal>();
        BusiTerminal busiTerminal2 = null;
        Long terminalId = null;

        //在绑定当前账号
        BusiTerminal terminal2 = new BusiTerminal();
        terminal2.setCredential(sipUserName);
        List<BusiTerminal> terminalList = busiTerminalMapper.selectBusiTerminalList(terminal2);
        if (null != terminalList && terminalList.size() > 0) {
            busiTerminal2 = terminalList.get(0);
            busiTerminal2.setUpdateTime(new Date());
//			busiTerminalMapper.deleteBusiTerminalById(busiTerminal2.getId());
//			busiTerminal2.setId(null);
            list.add(busiTerminal2);
        }

        BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(clientId);
        if (busiTerminalSn != null) {
            busiTerminal2.setSn(busiTerminalSn.getSn());
            busiTerminal2.setIp(busiTerminalSn.getIp());
            busiTerminal2.setIntranetIp(busiTerminalSn.getIntranetIp());
            busiTerminal2.setMqttOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());

            busiTerminalSn.setSn("");
            busiTerminalSn.setIp("");
            busiTerminalSn.setIntranetIp("");
            busiTerminalSn.setMqttOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
            busiTerminalSn.setUpdateTime(new Date());
            list.add(busiTerminalSn);

            try {
                Long[] arrLongs = new Long[1];
                arrLongs[0] = busiTerminalSn.getId();
                busiTerminalService.deleteBusiTerminalByIds(arrLongs);
                busiTerminalSn.setId(null);
            } catch (Exception e) {
                LOGGER.error("============不能解绑这个sip账号,因为在模板会议中", e);
            }
        }


        for (int i = 0; i < list.size(); i++) {
            if (null != list.get(i).getId()) {
                int j = busiTerminalService.updateBusiTerminal(list.get(i));
                if (j > 0) {
                    terminalId = list.get(i).getId();
                }
            } else {
                busiTerminalService.insertBusiTerminal(list.get(i));
            }
        }

        BusiRegisterTerminal registerTerminal = new BusiRegisterTerminal();
        registerTerminal.setSn(clientId);
        List<BusiRegisterTerminal> registerTerminalList = busiRegisterTerminalMapper.selectBusiRegisterTerminalList(registerTerminal);
        if (null != registerTerminalList && registerTerminalList.size() > 0) {
            registerTerminalList.get(0).setUpdateTime(new Date());
            registerTerminalList.get(0).setCredential(sipUserName);
            registerTerminalList.get(0).setTerminalId(terminalId);
            busiRegisterTerminalMapper.updateBusiRegisterTerminal(registerTerminalList.get(0));
        }

        //更新终端信息,下发sip账号
        this.updateTerminalInfo(messageId, terminalType, busiTerminal2);
    }

    private void updateTerminalInfo(String messageId, String terminalType, BusiTerminal busiTerminal2) {
        if (terminalType.equals("topBox")) {
            TerminalSipAccount.getInstance().setTopBoxGetSipAccount(messageId, busiTerminal2);
        } else {
            TerminalSipAccount.getInstance().vhdTerminalGetSipAcc(messageId, busiTerminal2);
        }
    }


    @Override
    public void receiveDelAccountInfo(JSONObject jsonS, String clientId, String messageId) {
        if (null != jsonS) {
            String terminalType = jsonS.getString("terminalType");
            Boolean delSuccess = jsonS.getBoolean("delSuccess");
            BusiRegisterTerminal busiRegisterTerminal = new BusiRegisterTerminal();
            busiRegisterTerminal.setMac(clientId);
            List<BusiRegisterTerminal> busiRegisterTerminalList = busiRegisterTerminalMapper.selectBusiRegisterTerminalList(busiRegisterTerminal);
            LOGGER.info("=======================> 确认待注册表的终端是否被删除" + busiRegisterTerminalList.toString());
            if (null != busiRegisterTerminalList && busiRegisterTerminalList.size() > 0) {
                busiRegisterTerminalMapper.deleteBusiRegisterTerminalById(busiRegisterTerminalList.get(0).getId());
            }

            if (delSuccess) {

                //获取部门上绑定的mqtt信息
                this.getAllMqtt(clientId);
            }
        }

    }


    private void getAllMqtt(String sn) {
        BusiMqtt busiMqtt = new BusiMqtt();
        List<BusiMqtt> mqttLists = busiMqttMapper.selectBusiMqttList(busiMqtt);
        if (null != mqttLists && mqttLists.size() > 0) {
            for (BusiMqtt busiMqtt2 : mqttLists) {

                //剔除mqtt上的终端和会话
                getTerminalConnection(busiMqtt2, sn);
            }
        }
    }

    private void getTerminalConnection(BusiMqtt busiMqtt, String sn) {
        String httpUrl = FcmConfigConstant.HTTP + busiMqtt.getIp() + FcmConfigConstant.COLON + busiMqtt.getManagementPort() + FcmConfigConstant.API_AND_VERSION;
        String connUrl = httpUrl + "/clients/" + sn;

        httpRequester.get(connUrl, new HttpResponseProcessorAdapter() {

            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                try {

                    String nodeData = getBodyContent(httpResponse);
                    if (StringUtils.isNotEmpty(nodeData)) {
                        JSONObject jsonObject = (JSONObject) JSONObject.parse(nodeData);
                        String data = jsonObject.getString(FcmConfigConstant.JSON_DATA_STR);
                        JSONArray array = (JSONArray) JSONArray.parse(data);
                        if (null != array && array.size() > 0) {
                            JSONObject jsonObj = (JSONObject) array.get(0);
                            Boolean connect = jsonObj.getBoolean("connected");
                            if (connect) {
                                LOGGER.info("=======================> 删除终端在Mqtt后台的连接！");
                                lastKickOutTerminal(connUrl, sn);
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new SystemException(FcmConfigConstant.EXCEPTION_ONE_th_th_F, "查询终端连接MQTT异常!");
                }
            }
        });
    }


    private void lastKickOutTerminal(String delUrl, String sn) {
        httpRequester.delete(delUrl, new HttpResponseProcessorAdapter() {

            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                LOGGER.info("=======================> 删除终端在Mqtt后台的连接,已成功！");

            }
        });

    }

    @Override
    public void byUriInviteTerminal(JSONObject jsonS, String clientId, String messageId) {
        if (null != jsonS) {
            String action = TerminalTopic.URI_INVITE;
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
            String conferenceNum = jsonS.getString(MqttConfigConstant.CONFERENCENUM);
            BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
            BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
            if (conferenceContext != null) {
                if (conferenceContext instanceof ConferenceContext) {
                    attendeeService.invite(conferenceContext.getId(), jsonS);
                } else if (conferenceContext instanceof McuZjConferenceContext) {
                    attendeeForMcuZjService.invite(conferenceContext.getId(), jsonS);
                } else if (conferenceContext instanceof McuPlcConferenceContext) {
                    attendeeForMcuPlcService.invite(conferenceContext.getId(), jsonS);
                } else if (conferenceContext instanceof McuKdcConferenceContext) {
                    attendeeForMcuKdcService.invite(conferenceContext.getId(), jsonS);
                }
                List<BaseAttendee> attendees = conferenceContext.getAttendees();
                if (null != attendees && attendees.size() > 0) {
                    this.responseTerminal(terminalTopic, action, (JSONObject) JSON.toJSON(attendees.get(0)), clientId, messageId);
                }
            }
        }
    }

    @Override
    public void fuzzyQueryConference(JSONObject jsonS, String clientId, String messageId) {
        if (null != jsonS) {
            String sn = jsonS.getString(MqttConfigConstant.CLIENTID);
            Integer page = (Integer) jsonS.getInteger(ResponseInfo.PAGE);
            Integer size = (Integer) jsonS.getInteger(ResponseInfo.SIZE);
            String name = jsonS.getString(MqttConfigConstant.NAME);

            String jsonConference = this.queryConferenceInfo(sn, page, size, name);
            if (StringUtils.isNotEmpty(jsonConference)) {
                JSONObject object = JSONObject.parseObject(jsonConference);

                String action = TerminalTopic.FUZZY_QUERY_CONFERENCE;
                String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
                this.responseTerminal(terminalTopic, action, object, clientId, messageId);
            }
        }
    }

    private String queryConferenceInfo(String sn, Integer page, Integer size, String name) {
        Assert.isTrue(!ObjectUtils.isEmpty(sn), "SN不能为空！");
        Assert.isTrue(!ObjectUtils.isEmpty(name), "会议字符不能为空！");
        Assert.isTrue(page != null, "page不能为空！");
        Assert.isTrue(size != null, "size不能为空！");
        PaginationData<BusiConferenceAppointment> pd = new PaginationData<>();
        pd.setPage(page);
        pd.setSize(size);

        BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(sn);
        if (busiTerminalSn == null) {
            return JSON.toJSONString(pd);
        }

        List<Long> terminalIds = new ArrayList<>();
        terminalIds.add(busiTerminalSn.getId());

        List<BusiConferenceAppointment> cas = new ArrayList<>();
        {
            BusiTemplateConference con1 = new BusiTemplateConference();
            con1.getParams().put("conditionTerminalIds", terminalIds);
            con1.setName(name);
            List<BusiTemplateConference> tcs = busiTemplateConferenceMapper.selectAllBusiTemplateConferenceList(con1);
            for (BusiTemplateConference busiTemplateConference : tcs) {
                BusiConferenceAppointment con2 = new BusiConferenceAppointment();
                con2.setTemplateId(busiTemplateConference.getId());
                List<BusiConferenceAppointment> is = busiConferenceAppointmentMapper.selectBusiConferenceAppointmentList(con2);
                if (!ObjectUtils.isEmpty(is)) {
                    ModelBean mb = busiTemplateConferenceService.getTemplateConferenceDetails(busiTemplateConference);
                    BusiConferenceAppointment ca = is.get(0);
                    ca.getParams().put("templateDetails", mb);
                    cas.add(ca);
                }
            }
        }
        {
            BusiMcuZjTemplateConference con1 = new BusiMcuZjTemplateConference();
            con1.getParams().put("conditionTerminalIds", terminalIds);
            con1.setName(name);
            List<BusiMcuZjTemplateConference> tcs = busiMcuZjTemplateConferenceMapper.selectAllBusiMcuZjTemplateConferenceList(con1);
            for (BusiMcuZjTemplateConference busiTemplateConference : tcs) {
                BusiMcuZjConferenceAppointment con2 = new BusiMcuZjConferenceAppointment();
                con2.setTemplateId(busiTemplateConference.getId());
                List<BusiMcuZjConferenceAppointment> is = busiMcuZjConferenceAppointmentMapper.selectBusiMcuZjConferenceAppointmentList(con2);
                if (!ObjectUtils.isEmpty(is)) {
                    ModelBean mb = busiMcuZjTemplateConferenceService.getTemplateConferenceDetails(busiTemplateConference);
                    BusiConferenceAppointment ca = is.get(0);
                    ca.getParams().put("templateDetails", mb);
                    cas.add(ca);
                }
            }
        }
        {
            BusiMcuPlcTemplateConference con1 = new BusiMcuPlcTemplateConference();
            con1.getParams().put("conditionTerminalIds", terminalIds);
            con1.setName(name);
            List<BusiMcuPlcTemplateConference> tcs = busiMcuPlcTemplateConferenceMapper.selectAllBusiMcuPlcTemplateConferenceList(con1);
            for (BusiMcuPlcTemplateConference busiTemplateConference : tcs) {
                BusiMcuPlcConferenceAppointment con2 = new BusiMcuPlcConferenceAppointment();
                con2.setTemplateId(busiTemplateConference.getId());
                List<BusiMcuPlcConferenceAppointment> is = busiMcuPlcConferenceAppointmentMapper.selectBusiMcuPlcConferenceAppointmentList(con2);
                if (!ObjectUtils.isEmpty(is)) {
                    ModelBean mb = busiMcuPlcTemplateConferenceService.getTemplateConferenceDetails(busiTemplateConference);
                    BusiConferenceAppointment ca = is.get(0);
                    ca.getParams().put("templateDetails", mb);
                    cas.add(ca);
                }
            }
        }
        {
            BusiMcuKdcTemplateConference con1 = new BusiMcuKdcTemplateConference();
            con1.getParams().put("conditionTerminalIds", terminalIds);
            con1.setName(name);
            List<BusiMcuKdcTemplateConference> tcs = busiMcuKdcTemplateConferenceMapper.selectAllBusiMcuKdcTemplateConferenceList(con1);
            for (BusiMcuKdcTemplateConference busiTemplateConference : tcs) {
                BusiMcuKdcConferenceAppointment con2 = new BusiMcuKdcConferenceAppointment();
                con2.setTemplateId(busiTemplateConference.getId());
                List<BusiMcuKdcConferenceAppointment> is = busiMcuKdcConferenceAppointmentMapper.selectBusiMcuKdcConferenceAppointmentList(con2);
                if (!ObjectUtils.isEmpty(is)) {
                    ModelBean mb = busiMcuKdcTemplateConferenceService.getTemplateConferenceDetails(busiTemplateConference);
                    BusiConferenceAppointment ca = is.get(0);
                    ca.getParams().put("templateDetails", mb);
                    cas.add(ca);
                }
            }
        }

        if (ObjectUtils.isEmpty(cas)) {
            return JSON.toJSONString(pd);
        }

        Collections.sort(cas, new Comparator<BusiConferenceAppointment>() {
            @Override
            public int compare(BusiConferenceAppointment o1, BusiConferenceAppointment o2) {
                return o2.getCreateTime().compareTo(o1.getCreateTime());
            }
        });

        int fromIndex = page * size;
        int toIndex = fromIndex + size;
        if (toIndex >= cas.size()) {
            toIndex = cas.size();
        }

        if (fromIndex >= toIndex) {
            return JSON.toJSONString(pd);
        }

        pd.setTotal(cas.size());
        pd.setRecords(cas.subList(fromIndex, toIndex));
        return JSON.toJSONString(pd);

    }

    @Override
    public void hostAcceptRaiseHand(JSONObject jsonS, String clientId, String messageId) {
        if (null != jsonS) {
            JSONObject jsonObject = new JSONObject();
            String conferenceNum = (String) jsonS.get(MqttConfigConstant.CONFERENCENUM);
            String sn = (String) jsonS.get(MqttConfigConstant.CLIENTID);
            BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
            BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);

            //获取所有参会人的信息
            List<BaseAttendee> attendees = this.getAllAttendeeInfoList(conferenceContext);

            //筛选举手人信息
            String attendeeId = this.getRaiseHandInfo(attendees, sn);

            if (conferenceContext instanceof ConferenceContext) {
                attendeeService.acceptRaiseHand(conferenceContext.getId(), attendeeId);
            } else if (conferenceContext instanceof McuZjConferenceContext) {
                attendeeForMcuZjService.acceptRaiseHand(conferenceContext.getId(), attendeeId);
            } else if (conferenceContext instanceof McuPlcConferenceContext) {
                attendeeForMcuPlcService.acceptRaiseHand(conferenceContext.getId(), attendeeId);
            } else if (conferenceContext instanceof McuKdcConferenceContext) {
                attendeeForMcuKdcService.acceptRaiseHand(conferenceContext.getId(), attendeeId);
            }

            for (int i = 0; i < attendees.size(); i++) {
                long deptId = attendees.get(i).getDeptId();
                String rp = attendees.get(i).getRemoteParty();
                String action = TerminalTopic.INTERACTIVE_RAISE_HAND;
                LOGGER.info("====================================>&&&& deptId" + deptId);
                LOGGER.info("====================================>&&&& rp" + rp);
                BusiTerminal terminal2 = TerminalCache.getInstance().getByRemoteParty(deptId, rp);
                LOGGER.info("====================================>&&&& terminal2^^^" + terminal2.toString());
                if (null != terminal2) {
                    String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + terminal2.getSn();
                    jsonObject.put(MqttConfigConstant.CONFERENCENUM, conferenceNum);
                    jsonObject.put(MqttConfigConstant.CLIENTID, sn);
                    jsonObject.put("isAgree", true);
                    this.responseTerminal(terminalTopic, action, jsonObject, clientId, messageId);
                }
            }
        }
    }

    private String getRaiseHandInfo(List<BaseAttendee> attendees, String sn) {
        String attendeeId = null;
        BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(sn);
        if (busiTerminalSn != null) {
            for (BaseAttendee attendee : attendees) {
                String remoteParty = attendee.getRemoteParty();
                if (remoteParty.contains("@")) {
                    if (remoteParty.equals(busiTerminalSn.getCredential() + "@" + busiTerminalSn.getIp())) {
                        attendeeId = attendee.getId();
                        break;
                    }
                } else {
                    if (remoteParty.equals(busiTerminalSn.getIntranetIp())) {
                        attendeeId = attendee.getId();
                        break;
                    }
                }

            }
        }

        return attendeeId;
    }

    private List<BaseAttendee> getAllAttendeeInfoList(BaseConferenceContext conferenceContext) {
        List<BaseAttendee> attendeeList = new ArrayList<>();
        if (null != conferenceContext) {
            List<BaseAttendee> attendees = conferenceContext.getAttendees();
            BaseAttendee masterAttendee = conferenceContext.getMasterAttendee();
            if (null != attendees && attendees.size() > 0) {
                for (BaseAttendee attendee : attendees) {
                    attendeeList.add(attendee);
                }
            }

            if (null != masterAttendee) {
                attendeeList.add(masterAttendee);
            }
        }
        return attendeeList;
    }


    @Override
    public void hostRejectRaiseHand(JSONObject jsonS, String clientId, String messageId) {
        if (null != jsonS) {
            String conferenceNum = (String) jsonS.get(MqttConfigConstant.CONFERENCENUM);
            String sn = (String) jsonS.get(MqttConfigConstant.CLIENTID);
            BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
            BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);

            //获取所有参会人的信息
            List<BaseAttendee> attendees = this.getAllAttendeeInfoList(conferenceContext);

            //筛选举手人信息
            String attendeeId = this.getRaiseHandInfo(attendees, sn);

            if (conferenceContext instanceof ConferenceContext) {
                attendeeService.rejectRaiseHand(conferenceContext.getId(), attendeeId);
            } else if (conferenceContext instanceof McuZjConferenceContext) {
                attendeeForMcuZjService.rejectRaiseHand(conferenceContext.getId(), attendeeId);
            } else if (conferenceContext instanceof McuPlcConferenceContext) {
                attendeeForMcuPlcService.rejectRaiseHand(conferenceContext.getId(), attendeeId);
            } else if (conferenceContext instanceof McuKdcConferenceContext) {
                attendeeForMcuKdcService.rejectRaiseHand(conferenceContext.getId(), attendeeId);
            }

            JSONObject jsonObject = new JSONObject();
            String action = TerminalTopic.INTERACTIVE_RAISE_HAND;
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + sn;
            jsonObject.put(MqttConfigConstant.CONFERENCENUM, conferenceNum);
            jsonObject.put(MqttConfigConstant.CLIENTID, sn);
            jsonObject.put("isAgree", false);
            this.responseTerminal(terminalTopic, action, jsonObject, clientId, messageId);
        }
    }

    @Override
    public void terminalStatus(JSONObject jsonObject) {
        try {
            String clientid = jsonObject.getString("clientid");
            if (StringUtils.isNotEmpty(clientid)) {
                int onlineStatus;
                if (jsonObject.get("clean_start") != null) {
                    onlineStatus = TerminalOnlineStatus.ONLINE.getValue();
                } else {
                    onlineStatus = TerminalOnlineStatus.OFFLINE.getValue();
                }

                BusiTerminal busiTerminal = TerminalCache.getInstance().getBySn(clientid);
                if (busiTerminal != null) {
                    if (onlineStatus != busiTerminal.getMqttOnlineStatus()) {
                        busiTerminal.setMqttOnlineStatus(onlineStatus);
                        busiTerminalMapper.updateBusiTerminal(busiTerminal);
                    }
                }
                BusiSmartRoomDoorplate busiSmartRoomDoorplate = SmartRoomDoorplateCache.getInstance().getBySn(clientid);
                if (busiSmartRoomDoorplate != null) {
                    if (onlineStatus != busiSmartRoomDoorplate.getMqttOnlineStatus()) {
                        busiSmartRoomDoorplate.setMqttOnlineStatus(onlineStatus);
                        int i = busiSmartRoomDoorplateMapper.updateBusiSmartRoomDoorplate(busiSmartRoomDoorplate);
                        if (i > 0) {
                            SmartRoomDoorplateCache.getInstance().add(busiSmartRoomDoorplate);
                        }
                    }
                }
                BusiSmartRoomLot busiSmartRoomLot = SmartRoomLotCache.getInstance().getByClientId(clientid);
                if (busiSmartRoomLot != null) {
                    if (onlineStatus != busiSmartRoomLot.getMqttOnlineStatus()) {
                        busiSmartRoomLot.setMqttOnlineStatus(onlineStatus);
                        int i = busiSmartRoomLotMapper.updateBusiSmartRoomLot(busiSmartRoomLot);
                        if (i > 0) {
                            SmartRoomLotCache.getInstance().add(busiSmartRoomLot);
                        }
                    }
                }
                BusiOps busiOps = OpsCache.getInstance().getBySn(clientid);
                if (busiOps != null) {
                    if (onlineStatus != busiOps.getMqttOnlineStatus()) {
                        busiOps.setMqttOnlineStatus(onlineStatus);
                        if (onlineStatus == TerminalOnlineStatus.ONLINE.getValue()) {
                            busiOps.setLastOnlineTime(new Date());
                        }
                        busiOpsMapper.updateBusiOps(busiOps);
                    }
                }
                BusiClient busiClient = ClientCache.getInstance().getBySn(clientid);
                if (busiClient != null) {
                    if (onlineStatus != busiClient.getMqttOnlineStatus()) {
                        busiClient.setMqttOnlineStatus(onlineStatus);
                        if (onlineStatus == TerminalOnlineStatus.ONLINE.getValue()) {
                            busiClient.setLastOnlineTime(new Date());
                        }
                        busiClientMapper.updateBusiClient(busiClient);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 直播终端列表
     * 直播终端回调信息处理
     * 0===无状态
     * 1===直播中
     * 2===会议中
     */
    @Override
    public void liveTerminal(JSONObject jsonS, String clientIdL, String messageId) {
        try {
            String conferenceNum = (String) jsonS.get(MqttConfigConstant.CONFERENCENUM);
            if (StringUtils.isNotEmpty(conferenceNum)) {
                BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientIdL);
                BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
                if (conferenceContext != null) {
                    liveTerminal(jsonS, clientIdL, messageId, conferenceContext);
                }
            }
        } catch (Exception e) {
            LOGGER.error("liveTerminal ++ == ", e);
        }
    }

    public void liveTerminal(JSONObject jsonS, String clientIdL, String messageId, BaseConferenceContext conferenceContext) {
        try {

            String conferenceNum = String.valueOf(jsonS.get(MqttConfigConstant.CONFERENCENUM));
            String isLive = String.valueOf(jsonS.get(MqttConfigConstant.ISLIVE));
            String clientId = String.valueOf(jsonS.get(MqttConfigConstant.CLIENTID));
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            TerminalLive terminalLive = new TerminalLive();
            terminalLive.setMac(clientId);

            Assert.isTrue(conferenceContext != null, "会议或者直播不存在");
            List<TerminalLive> terminalLiveList = TerminalLiveCache.getInstance().getById(conferenceContext.getContextKey());

            if (jsonS != null && conferenceContext != null) {
                switch (isLive) {
                    case "1":
                        for (int i = 0; i < terminalLiveList.size(); i++) {
                            if (terminalLiveList.get(i).getMac().equals(clientId)) {
                                terminalLiveList.get(i).setJoinTime(formatter.format(date));
                                terminalLiveList.get(i).setOutTime(null);
                                terminalLiveList.get(i).setStatus(1);
                                TerminalLiveCache.getInstance().update(conferenceContext.getContextKey(), terminalLiveList);

                                LOGGER.info("===========" + clientId + "已进入" + conferenceNum + "直播");
                                break;
                            }
                        }
                        break;
                    case "0":
                        if (terminalLiveList != null && terminalLiveList.size() > 0) {
                            for (int i = 0; i < terminalLiveList.size(); i++) {
                                if (terminalLiveList.get(i).getMac().equals(clientId)) {
                                    if (terminalLiveList.get(i).getJoinTime() != null && terminalLiveList.get(i).getJoinTime().length() > 0) {
                                        terminalLiveList.get(i).setOutTime(formatter.format(date));
                                    }
                                    terminalLiveList.get(i).setStatus(0);
                                    TerminalLiveCache.getInstance().update(conferenceContext.getContextKey(), terminalLiveList);

                                    LOGGER.info("===========" + clientId + "已退出" + conferenceNum + "直播");
                                    break;
                                }
                            }
                        }
                        break;
                    case "2":
                        if (terminalLiveList != null && terminalLiveList.size() > 0) {
                            for (int i = 0; i < terminalLiveList.size(); i++) {
                                if (terminalLiveList.get(i).getMac().equals(clientId)) {
                                    if (terminalLiveList.get(i).getJoinTime() != null && terminalLiveList.get(i).getJoinTime().length() > 0) {
                                        terminalLiveList.get(i).setOutTime(formatter.format(date));
                                    }
                                    terminalLiveList.get(i).setStatus(2);
                                    TerminalLiveCache.getInstance().update(conferenceContext.getContextKey(), terminalLiveList);
                                    LOGGER.info("===========" + clientId + "进入" + conferenceNum + "会议");
                                    break;
                                }
                            }
                        }
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            LOGGER.info("=liveTerminal方法异常处理===", e);
        }
    }

    /**
     * Api 获取直播终端列表
     *
     * @param baseConferenceContext
     * @return
     */
    @Override
    public List<TerminalLive> liveTerminalList(BaseConferenceContext baseConferenceContext) {
        List<TerminalLive> terminalLiveList = new ArrayList<>();
        if (baseConferenceContext != null) {
            List<?> liveTerminals = null;
            if (baseConferenceContext instanceof ConferenceContext) {
                ConferenceContext conferenceContext = (ConferenceContext) baseConferenceContext;
                liveTerminals = conferenceContext.getLiveTerminals();
            } else if (baseConferenceContext instanceof McuZjConferenceContext) {
                McuZjConferenceContext mcuZjConferenceContext = (McuZjConferenceContext) baseConferenceContext;
                liveTerminals = mcuZjConferenceContext.getLiveTerminals();
            } else if (baseConferenceContext instanceof McuPlcConferenceContext) {
                McuPlcConferenceContext mcuPlcConferenceContext = (McuPlcConferenceContext) baseConferenceContext;
                liveTerminals = mcuPlcConferenceContext.getLiveTerminals();
            } else if (baseConferenceContext instanceof McuKdcConferenceContext) {
                McuKdcConferenceContext mcuKdcConferenceContext = (McuKdcConferenceContext) baseConferenceContext;
                liveTerminals = mcuKdcConferenceContext.getLiveTerminals();
            }
            List<TerminalLive> terminalLiveList1 = TerminalLiveCache.getInstance().getById(baseConferenceContext.getContextKey());

            for (int i = 0; i < liveTerminals.size(); i++) {
                BaseAttendee baseAttendee = (BaseAttendee) liveTerminals.get(i);
                Long terminalId = baseAttendee.getTerminalId();
                BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
                if (baseAttendee.getSn() != null) {
                    TerminalLive terminalLive = new TerminalLive();
                    terminalLive.setCredential(busiTerminal.getCredential());
                    terminalLive.setMac(busiTerminal.getSn());
                    terminalLive.setName(busiTerminal.getName());
                    terminalLive.setOnlineStatus(busiTerminal.getOnlineStatus());
                    terminalLive.setMqttOnlineStatus(busiTerminal.getMqttOnlineStatus());
                    terminalLive.setIntranetIp(busiTerminal.getIntranetIp());
                    terminalLive.setType(busiTerminal.getType());
                    terminalLive.setId(busiTerminal.getId());

                    if (terminalLiveList1 != null) {
                        for (TerminalLive terminalLiveTemp : terminalLiveList1) {
                            if (terminalLiveTemp.getId().longValue() == busiTerminal.getId().longValue()) {
                                if (busiTerminal.getOnlineStatus() == 2 && busiTerminal.getMqttOnlineStatus() == 2) {
                                    terminalLiveTemp.setStatus(0);
                                }
                                terminalLive.setStatus(terminalLiveTemp.getStatus());

                                terminalLive.setJoinTime(terminalLiveTemp.getJoinTime());
                                terminalLive.setOutTime(terminalLiveTemp.getOutTime());
                            }
                        }
                    }
                    terminalLiveList.add(terminalLive);
                    TerminalLiveCache.getInstance().update(baseConferenceContext.getContextKey(), terminalLiveList);
                }
            }
        } else {
            terminalLiveList = null;
        }

        return terminalLiveList;
    }

    //推送直播
    @Override
    public void pushLive(JSONObject jsonS, String clientId, String messageId) {
        if (jsonS != null) {
            this.liveTerminal(jsonS, clientId, messageId);
        }
    }

    @Override
    public void recordingList(JSONObject jsonS, String clientId, String messageId) {

        Integer page = null;
        Integer size = null;
        Integer startIndex = null;
        Integer endIndex = null;
        if (jsonS.containsKey(ResponseInfo.PAGE) && jsonS.containsKey(ResponseInfo.PAGE)) {
            page = jsonS.getInteger(ResponseInfo.PAGE);
            size = jsonS.getInteger(ResponseInfo.SIZE);
            if (page > 0 && size > 0) {
                startIndex = (page - 1) * size;
                endIndex = startIndex + size;
            }
        }
        if (startIndex == null || endIndex == null) {
            page = 1;
            size = 1000;
            startIndex = 0;
            endIndex = startIndex + size;
        }
        // 录制类型：0：不限 1：我创建的会议的录制 2：我参与的会议的录制
        Integer recordsType = jsonS.getInteger("recordsType");
        if (recordsType == null) {
            recordsType = 0;
        }

        String action = TerminalTopic.RECORDING_LIST;
        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;

        StringBuffer stringBuffer = new StringBuffer();

        List<Map> list = new ArrayList<>();
        //JSONObject stringObjectMap = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        BusiRecordSetting busiRecordSetting = new BusiRecordSetting();

        BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(clientId);
        if (busiTerminalSn != null) {

            busiRecordSetting.setDeptId(busiTerminalSn.getDeptId());
            List<BusiRecordSetting> busiRecordSettings = busiRecordSettingMapper.selectBusiRecordSettingList(busiRecordSetting);
            LOGGER.info("busiRecordSettings:" + busiRecordSettings);
            if (!(busiRecordSettings == null || busiRecordSettings.isEmpty())) {

                try {
                    Long deptId = (busiTerminalSn.getDeptId() == null) ? SecurityUtils.getLoginUser().getUser().getDeptId() : busiTerminalSn.getDeptId();
                    RecordsSearchVo recordsSearchVo = new RecordsSearchVo();
                    if (recordsSearchVo.getPageNum() == null || recordsSearchVo.getPageNum() <= 0) {
                        recordsSearchVo.setPageNum(1);
                    }
                    if (recordsSearchVo.getPageSize() == null || recordsSearchVo.getPageSize() > 100) {
                        recordsSearchVo.setPageSize(100);
                    }
                    List<BusiRecordsSearchResult> busiRecordsSearchResultList;
                    if (recordsType == 2) {
                        busiRecordsSearchResultList = busiRecordsMapper.selectBusiRecordsListForGroupForMyJoinedConference(deptId, recordsSearchVo.getSearchKey(), busiTerminalSn.getId());
                    } else if (recordsType == 1) {
                        BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(busiTerminalSn.getId());
                        if (busiUserTerminal != null) {
                            busiRecordsSearchResultList = busiRecordsMapper.selectBusiRecordsListForGroupForMyConference(deptId, recordsSearchVo.getSearchKey(), busiUserTerminal.getUserId());
                        } else {
                            busiRecordsSearchResultList = new ArrayList<>();
                        }
                    } else {
                        busiRecordsSearchResultList = busiRecordsMapper.selectBusiRecordsListForGroup(deptId, recordsSearchVo.getSearchKey());
                    }
                    if (busiRecordsSearchResultList != null && busiRecordsSearchResultList.size() > 0) {
                        for (BusiRecordsSearchResult busiRecordsSearchResult : busiRecordsSearchResultList) {

                            Map<String, Object> stringObjectMap = new HashMap<>();
                            String coSpaceId = busiRecordsSearchResult.getCoSpaceId();
                            stringObjectMap.put("coSpaceId", coSpaceId);
                            if (StringUtils.isNotEmpty(coSpaceId) && (coSpaceId.endsWith("-zj") || coSpaceId.endsWith("-plc") || coSpaceId.endsWith("-kdc"))) {
                                if (coSpaceId.contains("-")) {
                                    stringObjectMap.put("conferenceNumber", coSpaceId.substring(0, coSpaceId.indexOf("-")));
                                }
                            } else {
                                stringObjectMap.put("conferenceNumber", busiRecordsSearchResult.getConferenceNumber().toString());
                            }
                            stringObjectMap.put("deptId", busiRecordsSearchResult.getDeptId());
                            stringObjectMap.put("fileSize", 0);
                            stringObjectMap.put("recordFileNum", busiRecordsSearchResult.getRecordFileNum());
                            stringObjectMap.put("recordingTimeOfLate", busiRecordsSearchResult.getRecordingTimeOfLate());
                            stringObjectMap.put("conferenceName", busiRecordsSearchResult.getName());
                            list.add(stringObjectMap);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("录制列表异常===", e);
                }
                jsonObject.put("total", list.size());
                jsonObject.put("page", page);
                jsonObject.put("size", size);

                List<Map> arrayList = new ArrayList<>();
                if (startIndex < list.size() && endIndex >= list.size()) {
                    endIndex = list.size();
                    arrayList = list.subList(startIndex, endIndex);
                } else if (startIndex < list.size() && endIndex < list.size()) {
                    arrayList = list.subList(startIndex, endIndex);
                }
                jsonObject.put("data", arrayList);
                this.responseTerminal(terminalTopic, action, jsonObject, clientId, messageId);
            }
        }

    }

    @Override
    public void recordingInfo(JSONObject jsonS, String clientId, String messageId) {

        try {
            String action = TerminalTopic.RECORDING_INFO;
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;

            String coSpaceId = jsonS.getString(MqttConfigConstant.COSPACEID);
            // 录制类型：0：不限 1：我创建的会议的录制 2：我参与的会议的录制
            Integer recordsType = jsonS.getInteger("recordsType");
            if (recordsType == null) {
                recordsType = 0;
            }

            JSONObject jsonArray = new JSONObject();
            List<Map<String, Object>> folders = new ArrayList<>();
            BusiTerminal busiTerminalSn = TerminalCache.getInstance().getBySn(clientId);
            if (busiTerminalSn != null) {
                Long deptId = busiTerminalSn.getDeptId();
                deptId = (deptId == null) ? SecurityUtils.getLoginUser().getUser().getDeptId() : deptId;
                BusiRecordSetting recordSetting = new BusiRecordSetting();
                recordSetting.setStatus(YesOrNo.YES.getValue());
                recordSetting.setDeptId(deptId);
                List<BusiRecordSetting> busiRecordSettings = busiRecordSettingMapper.selectBusiRecordSettingList(recordSetting);
                if (!(busiRecordSettings == null || busiRecordSettings.isEmpty())) {

                    Set<String> conferenceNumberSet = new HashSet<>();

                    List<BusiRecords> busiRecordsList;
                    if (recordsType == 2) {
                        busiRecordsList = busiRecordsMapper.selectBusiRecordsByCoSpaceIdForMyJoinedConference(deptId, coSpaceId, Boolean.FALSE, busiTerminalSn.getId());
                    } else if (recordsType == 1) {
                        BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(busiTerminalSn.getId());
                        if (busiUserTerminal != null) {
                            busiRecordsList = busiRecordsMapper.selectBusiRecordsByCoSpaceIdForMyConference(deptId, coSpaceId, Boolean.FALSE, busiUserTerminal.getUserId());
                        } else {
                            busiRecordsList = new ArrayList<>();
                        }
                    } else {
                        busiRecordsList = busiRecordsMapper.selectBusiRecordsByCoSpaceId(deptId, coSpaceId, Boolean.FALSE);
                    }
                    LOGGER.debug("\\\\\\\\\\\\" + busiRecordsList);
                    for (int i = 0; i < busiRecordsList.size(); i++) {
                        BusiRecords busiRecords = busiRecordsList.get(i);
                        if (StringUtils.isNotEmpty(busiRecords.getRealName())) {
                            Map<String, Object> map = new HashMap<>();
                            String coSpaceIdTemp = busiRecords.getCoSpaceId();
                            String comUrl = (busiRecordSettings != null && busiRecordSettings.size() > 0) ? busiRecordSettings.get(0).getUrl() : "";
                            if (comUrl.lastIndexOf("/") < comUrl.length() - 1) {
                                comUrl += "/" + coSpaceIdTemp;
                            } else {
                                comUrl += coSpaceIdTemp;
                            }
                            if (StringUtils.isNotEmpty(coSpaceId) && (coSpaceId.endsWith("-zj") || coSpaceId.endsWith("-plc") || coSpaceId.endsWith("-kdc"))) {
                                if (coSpaceId.contains("-")) {
                                    String conferenceNumber = coSpaceId.substring(0, coSpaceId.indexOf("-"));
                                    conferenceNumberSet.add(conferenceNumber);
                                }
                            } else {
                                String conferenceNumber = busiRecords.getConferenceNumber().toString();
                                conferenceNumberSet.add(conferenceNumber);
                            }
                            String url = comUrl + "/" + busiRecords.getRealName();
                            map.put("fileName", busiRecords.getFileName());
                            map.put("realName", busiRecords.getRealName());
                            map.put("recordingTime", busiRecords.getCreateTime());
                            map.put("fileSize", busiRecords.getFileSize());
                            map.put("url", url);
                            map.put("id", busiRecords.getId());
                            map.put("coSpaceId", coSpaceIdTemp);
                            map.put("deptId", deptId);
                            folders.add(map);
                        }
                    }
                    for (String conferenceNumber : conferenceNumberSet) {
                        UpdateRecordsTask updateRecordsTask = new UpdateRecordsTask(coSpaceId, 5000, deptId, coSpaceId, conferenceNumber, null);
                        taskService.addTask(updateRecordsTask);
                    }
                }
                jsonArray.put("data", folders);
                this.responseTerminal(terminalTopic, action, jsonArray, clientId, messageId);
            } else {
                jsonArray.put("data", folders);
                this.responseTerminal(terminalTopic, action, jsonArray, clientId, messageId);
            }
        } catch (Exception e) {
            LOGGER.error("录制文件异常===", e);
        }

    }

    /**
     * 专业终端是否打开辅流
     *
     * @param conferenceId
     * @param id
     * @param isOpen
     * @return
     */
    @Override
    public boolean isOpenSecondaryStream(String conferenceId, long id, boolean isOpen) {
        String action = TerminalTopic.OPEN_SECONDARYSTREAM;
        boolean isOpenSecondaryStream = false;

        BusiTerminal busiTerminal = TerminalCache.getInstance().get(id);
        if (isOpen) {
            Assert.isTrue(busiTerminal.getMqttOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue(), "该终端未连接FMQ服务，无法从会控开启共享辅流!");
        } else {
            Assert.isTrue(busiTerminal.getMqttOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue(), "该终端未连接FMQ服务，无法从会控关闭共享辅流!");
        }
        if (busiTerminal != null) {
            AppType appType = AppType.convert(busiTerminal.getAppType());
            Assert.notNull(appType, "该类型终端不支持从会控开启或关闭共享辅流！请从终端开启或关闭辅流。");
            Assert.isTrue(appType.isSupportSecondaryStream(), "该类型终端不支持从会控开启或关闭共享辅流！请从终端开启或关闭辅流。");

            BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
            if (conferenceContext != null) {
                BaseAttendee terminalAttendee = (BaseAttendee) conferenceContext.getTerminalAttendeeMap().get(id);
                if (isOpen) {
                    if (StringUtils.isNotEmpty(conferenceContext.getPresentAttendeeId()) && !terminalAttendee.getId().equals(conferenceContext.getPresentAttendeeId())) {
                        throw new CustomException("已有其它终端开启共享辅流，请先关闭后重试。");
                    }
                } else {
                    if (!terminalAttendee.getId().equals(conferenceContext.getPresentAttendeeId())) {
                        throw new CustomException("该终端未开启共享辅流。");
                    }
                }
                if (busiTerminal.getSn() != null && busiTerminal.getMqttOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
                    String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + busiTerminal.getSn();

                    JSONObject jsonArray = new JSONObject();
                    jsonArray.put("isOpenSecondaryStream", isOpen);
                    this.responseTerminal(terminalTopic, action, jsonArray, busiTerminal.getSn(), null);
                    isOpenSecondaryStream = true;
                }
            }
        }
        return isOpenSecondaryStream;
    }

    @Override
    public void getServerInfo(JSONObject jsonS, String clientId, String messageId) {

        String action = TerminalTopic.SERVER_INFO;
        JSONObject jsonArray = new JSONObject();
        jsonArray.put(MqttConfigConstant.TIME, System.currentTimeMillis());
        if (TerminalCache.getInstance().getBySn(clientId) != null) {
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
            this.responseTerminalByQOS(terminalTopic, action, jsonArray, messageId, QosEnum.QOS1);
        }
        if (SmartRoomDoorplateCache.getInstance().getBySn(clientId) != null) {
            String topicDoorplate = MqttConfigConstant.TOPIC_PREFIX_DOORPLATE + clientId;
            this.responseTerminalByQOS(topicDoorplate, action, jsonArray, messageId, QosEnum.QOS1);
        }
        if (OpsCache.getInstance().getBySn(clientId) != null) {
            String topicOps = MqttConfigConstant.TOPIC_PREFIX_OPS + clientId;
            this.responseTerminalByQOS(topicOps, action, jsonArray, messageId, QosEnum.QOS1);
        }
        if (ClientCache.getInstance().getBySn(clientId) != null) {
            String topicClient = MqttConfigConstant.TOPIC_PREFIX_CLIENT + clientId;
            this.responseTerminalByQOS(topicClient, action, jsonArray, messageId, QosEnum.QOS1);
        }
    }

    @Override
    public void sendServerInfo() {

        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX_FMC + TerminalTopic.SERVER_INFO;
        String action = TerminalTopic.SERVER_INFO;
        JSONObject jsonArray = new JSONObject();
        jsonArray.put(MqttConfigConstant.TIME, System.currentTimeMillis());
        this.responseTerminalByQOS(terminalTopic, action, jsonArray, null, QosEnum.QOS0);
    }

    @Override
    public void checkAppVersion(JSONObject jsonS, String clientId, String messageId) {
        String action = TerminalTopic.CHECK_APP_VERSION;
        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;

        String versionCode = (String) jsonS.get(MqttConfigConstant.versionCode);
        String terminalType = (String) jsonS.get(MqttConfigConstant.TERMINAL_TYPE);
        Map<String, Object> map = busiTerminalUpgradeService.selectBusiAppVersion(terminalType, versionCode);
        JSONObject jsonArray = new JSONObject();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            jsonArray.put(entry.getKey(), entry.getValue());
        }
        this.responseTerminal(terminalTopic, action, jsonArray, clientId, messageId);
    }

    @Override
    public void sendConferenceInfo(JSONObject jsonS, String clientId, String messageId) {
        try {
            String conferenceNum = (String) jsonS.get("conferenceNum");
            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
            String action = TerminalTopic.CONFERENCE_INFO;
            BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
            BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
            if (conferenceContext != null) {
                ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                String jsonStr = objectMapper.writeValueAsString(conferenceContext);
                JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                jsonObject.remove("supervisorPassword");
                if (jsonObject.get("conferenceAppointment") == null) {
                    if (!conferenceContext.isAppointment() && conferenceContext.getConferenceAppointment() == null) {
                        BusiConferenceAppointment busiConferenceAppointment = new BusiConferenceAppointment();
                        String startTimeStr = DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", conferenceContext.getStartTime());
                        Date endTime = DateUtils.getDiffDate(conferenceContext.getStartTime(), conferenceContext.getDurationTime(), TimeUnit.MINUTES);
                        String endTimeStr = DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", endTime);
                        busiConferenceAppointment.setStartTime(startTimeStr);
                        busiConferenceAppointment.setEndTime(endTimeStr);
                        busiConferenceAppointment.setDeptId(conferenceContext.getDeptId());
                        busiConferenceAppointment.setTemplateId(conferenceContext.getTemplateConferenceId());
                        jsonObject.put("conferenceAppointment", busiConferenceAppointment);
                    }
                }
                this.responseTerminal(terminalTopic, action, jsonObject, clientId, messageId);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public boolean canControlConference(String clientId, String conferenceNum) {
        try {
            BusiTerminal busiTerminal = TerminalCache.getInstance().getBySn(clientId);
            if (busiTerminal != null) {
                BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, busiTerminal);
                if (conferenceContext != null) {
                    Long userId = conferenceContext.getUserIdByTerminalId(busiTerminal.getId());
                    if (userId == null) {
                        BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(busiTerminal.getId());
                        if (busiUserTerminal != null) {
                            userId = busiUserTerminal.getUserId();
                        }
                    }
                    if (userId != null) {
                        if (conferenceContext.getCreateUserId() == userId || (conferenceContext.getPresenter() != null && conferenceContext.getPresenter() == userId)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
        }

        return false;
    }

    @Override
    public boolean canCreateConference(String clientId) {
        try {
            BusiTerminal busiTerminal = TerminalCache.getInstance().getBySn(clientId);
            if (busiTerminal != null) {
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(busiTerminal.getId());
                if (busiUserTerminal != null) {
                    Long userId = busiUserTerminal.getUserId();
                    if (userId != null) {
                        //判断角色
                        return true;
                    }
                }
            }
        } catch (Exception e) {
        }

        return false;
    }

    @Override
    public void sendLiveListInfo(JSONObject jsonS, String clientId, String messageId) {
        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
        String action = TerminalTopic.LIVE_LIST;
        try {
            JSONArray jsonArray = new JSONArray();
            JSONObject job = new JSONObject();
            Map<String, JSONObject> map = new HashMap<>();
            for (BaseConferenceContext conferenceContext : AllConferenceContextCache.getInstance().values()) {
                if (conferenceContext.isStreaming()) {
                    if (StringUtils.isNotEmpty(conferenceContext.getStreamingUrl())) {
                        if (!conferenceContext.isEnd()) {
                            JSONObject object = new JSONObject();
                            object.put(MqttConfigConstant.CONFERENCENUM, conferenceContext.getTenantId() + conferenceContext.getConferenceNumber());
                            object.put(MqttConfigConstant.CONFERENCE_NAME, conferenceContext.getName());
                            map.put(conferenceContext.getStreamingUrl(), object);
                        }
                    }
                }
            }

            BusiTerminal busiTerminal = TerminalCache.getInstance().getBySn(clientId);
            List<BusiLiveSetting> liveSettingList = getBusiLiveSettingByDeptId(busiTerminal.getDeptId());
            if (liveSettingList != null && liveSettingList.size() > 0) {
                for (BusiLiveSetting liveSetting : liveSettingList) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", liveSetting.getName());
                    jsonObject.put("url", liveSetting.getUrl());
                    if (map.containsKey(liveSetting.getUrl())) {
                        JSONObject object = map.get(liveSetting.getUrl());
                        jsonObject.putAll(object);
                    }
                    jsonArray.add(jsonObject);
                }
            }

            job.put(TerminalTopic.LIVE_LIST, jsonArray);
            this.responseTerminal(terminalTopic, action, job, clientId, messageId);
        } catch (Exception e) {
            this.responseTerminalFail(terminalTopic, action, null, clientId, messageId, ResponseInfo.CODE_500);
        }
    }

    @Override
    public List<BusiLiveSetting> getBusiLiveSettingByDeptId(Long deptId) {
        BusiLiveSetting busiLiveSetting = new BusiLiveSetting();
        busiLiveSetting.setStatus(1);
        busiLiveSetting.setDeptId(deptId);
        List<BusiLiveSetting> busiLiveSettingList = busiLiveSettingMapper.selectBusiLiveSettingList(busiLiveSetting);
        if (busiLiveSettingList != null && busiLiveSettingList.size() > 0) {
            return busiLiveSettingList;
        } else {
            SysDept sysDept = SysDeptCache.getInstance().get(deptId);
            if (sysDept.getParentId() != null && sysDept.getParentId().longValue() > 0) {
                return getBusiLiveSettingByDeptId(sysDept.getParentId());
            } else {
                return null;
            }
        }
    }

    @Override
    public void changePresenter(JSONObject jsonS, String clientId, String messageId) {
        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
        String action = TerminalTopic.CHANGE_PRESENTER;
        boolean isSuccess = false;
        try {
            if (jsonS != null) {
                String sn = jsonS.getString("clientId");
                String conferenceNumber = jsonS.getString("conferenceNumber");
                Boolean enabled = jsonS.getBooleanValue("enable");
                Long userId = jsonS.getLong("userId");
                boolean canControlConference = canControlConference(sn, conferenceNumber);
                if (canControlConference) {
                    BusiTerminal busiTerminal = TerminalCache.getInstance().getBySn(clientId);
                    if (busiTerminal != null) {
                        BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNumber, busiTerminal);
                        if (conferenceContext != null) {
                            if (conferenceContext instanceof ConferenceContext) {
                                if (enabled) {
                                    SysUser sysUser = sysUserService.selectUserById(userId);
                                    if (sysUser != null) {
                                        if (conferenceContext instanceof ConferenceContext) {
                                            busiConferenceService.updateConferencePresenter(conferenceContext.getId(), enabled, userId);
                                        } else {
                                            conferenceContext.setPresenter(userId);
                                        }
                                        isSuccess = true;
                                    }
                                } else {
                                    if (conferenceContext instanceof ConferenceContext) {
                                        busiConferenceService.updateConferencePresenter(conferenceContext.getId(), enabled, userId);
                                    } else {
                                        conferenceContext.setPresenter(0L);
                                    }
                                    isSuccess = true;
                                }
                            } else {
                                if (enabled) {
                                    SysUser sysUser = sysUserService.selectUserById(userId);
                                    if (sysUser != null) {
                                        conferenceContext.setPresenter(userId);
                                        isSuccess = true;
                                    }
                                } else {
                                    conferenceContext.setPresenter(0L);
                                    isSuccess = true;
                                }
                            }

                            if (isSuccess) {
                                if (!(conferenceContext instanceof ConferenceContext)) {
                                    sendConferenceInfoToPushTargetTerminal(conferenceContext, userId);
                                }
                            }
                        }
                    }
                } else {
                    ResponseTerminal.getInstance().responseTerminal(ResponseInfo.CODE_401, "没有操作权限！", terminalTopic, action, null, clientId, "");
                }
            }
        } catch (Exception e) {
            ResponseTerminal.getInstance().responseTerminalByQOS(ResponseInfo.CODE_500, e.getMessage(), terminalTopic, action, null, messageId, QosEnum.QOS2);
        }
        if (!isSuccess) {
            ResponseTerminal.getInstance().responseTerminalByQOS(ResponseInfo.CODE_500, "操作失败！", terminalTopic, action, null, messageId, QosEnum.QOS2);
        }
    }

    @Override
    public void conferenceAttendeeList(JSONObject jsonS, String clientId, String messageId) {
        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
        String action = TerminalTopic.CONFERENCE_ATTENDEE_LIST;
        PaginationData<BaseAttendee> pd = new PaginationData<>();
        String conferenceAttendee = null;
        try {
            if (jsonS != null) {
                String conferenceNumber = jsonS.getString("conferenceNumber");
                Integer page = jsonS.getInteger(ResponseInfo.PAGE);
                Integer size = jsonS.getInteger(ResponseInfo.SIZE);

                if (page == null || page < 0) {
                    page = 0;
                }
                if (size == null || size <= 0) {
                    size = 100;
                }
                List<BaseAttendee> attendeeList = new ArrayList<>();
                BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
                BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNumber, opTerminal);
                if (conferenceContext != null) {

                    if (conferenceContext.getMasterAttendee() != null) {
                        attendeeList.add(conferenceContext.getMasterAttendee());
                    }

                    ArrayList<BaseAttendee> arrayList = new ArrayList<>(conferenceContext.getAttendees());
                    attendeeList.addAll(arrayList);
                    attendeeList.addAll(conferenceContext.getMasterAttendees());

                    if (conferenceContext instanceof ConferenceContext) {
                        ConferenceContext conferenceContextT = (ConferenceContext) conferenceContext;
                        for (Long deptId : conferenceContextT.getCascadeAttendeesMap().keySet()) {
                            List<Attendee> attendees = conferenceContextT.getCascadeAttendeesMap().get(deptId);
                            if (attendees != null) {
                                attendeeList.addAll(attendees);
                            }
                        }
                    } else if (conferenceContext instanceof McuZjConferenceContext) {
                        McuZjConferenceContext conferenceContextT = (McuZjConferenceContext) conferenceContext;
                        for (Long deptId : conferenceContextT.getCascadeAttendeesMap().keySet()) {
                            List<AttendeeForMcuZj> attendees = conferenceContextT.getCascadeAttendeesMap().get(deptId);
                            if (attendees != null) {
                                attendeeList.addAll(attendees);
                            }
                        }
                    } else if (conferenceContext instanceof McuPlcConferenceContext) {
                        McuPlcConferenceContext conferenceContextT = (McuPlcConferenceContext) conferenceContext;
                        for (Long deptId : conferenceContextT.getCascadeAttendeesMap().keySet()) {
                            List<AttendeeForMcuPlc> attendees = conferenceContextT.getCascadeAttendeesMap().get(deptId);
                            if (attendees != null) {
                                attendeeList.addAll(attendees);
                            }
                        }
                    } else if (conferenceContext instanceof McuKdcConferenceContext) {
                        McuKdcConferenceContext conferenceContextT = (McuKdcConferenceContext) conferenceContext;
                        for (Long deptId : conferenceContextT.getCascadeAttendeesMap().keySet()) {
                            List<AttendeeForMcuKdc> attendees = conferenceContextT.getCascadeAttendeesMap().get(deptId);
                            if (attendees != null) {
                                attendeeList.addAll(attendees);
                            }
                        }
                    }
                }

                int fromIndex = page * size;
                int toIndex = fromIndex + size;
                if (toIndex >= attendeeList.size()) {
                    toIndex = attendeeList.size();
                }

                if (fromIndex > toIndex) {
                    fromIndex = 0;
                    toIndex = attendeeList.size() > 100 ? 100 : attendeeList.size();
                    page = fromIndex;
                    size = toIndex;
                }
                pd.setRecords(attendeeList.subList(fromIndex, toIndex));
                pd.setTotal(attendeeList.size());
                pd.setPage(page);
                pd.setSize(size);
                ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                try {
                    conferenceAttendee = objectMapper.writeValueAsString(pd);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                if (StringUtils.isNotEmpty(conferenceAttendee)) {
                    JSONObject jsonObject = JSONObject.parseObject(conferenceAttendee);
                    this.responseTerminal(terminalTopic, action, jsonObject, clientId, messageId);
                }
            }
        } catch (Exception e) {
            ResponseTerminal.getInstance().responseTerminalByQOS(ResponseInfo.CODE_500, e.getMessage(), terminalTopic, action, jsonS, messageId, QosEnum.QOS2);
        }
    }

    /**
     *
     * @param jsonS
     * @param clientId
     * @param messageId
     */
    @Override
    public void updateDefaultViewConfigInfo(JSONObject jsonS, String clientId, String messageId) {
        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
        String action = TerminalTopic.UPDATE_DEFAULTVIEWCONFIGINFO;
        JSONObject job = new JSONObject();
        job.put("updateDefaultViewConfigInfo", "fail");
        try {
            if (jsonS != null) {
                String conferenceNumber = jsonS.getString("conferenceNumber");
                String defaultViewLayout = jsonS.getString("defaultViewLayout");
                String defaultViewIsBroadcast = jsonS.getString("defaultViewIsBroadcast");
                String defaultViewIsDisplaySelf = jsonS.getString("defaultViewIsDisplaySelf");
                String defaultViewIsFill = jsonS.getString("defaultViewIsFill");
                String pollingInterval = jsonS.getString("pollingInterval");
                boolean canControlConference = canControlConference(clientId, conferenceNumber);
                if (!canControlConference) {
                    ResponseTerminal.getInstance().responseTerminal(ResponseInfo.CODE_401, "没有操作权限！", terminalTopic, action, null, clientId, "");
                } else {
                    BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
                    BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNumber, opTerminal);
                    if (conferenceContext != null) {

                        if (conferenceContext instanceof ConferenceContext) {
                            List<DefaultViewCellScreens> obj = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("defaultViewLayout", defaultViewLayout);
                            jsonObject.put("defaultViewIsBroadcast", defaultViewIsBroadcast);
                            jsonObject.put("defaultViewIsDisplaySelf", defaultViewIsDisplaySelf);
                            jsonObject.put("defaultViewIsFill", defaultViewIsFill);
                            jsonObject.put("pollingInterval", pollingInterval);
                            switch (defaultViewLayout) {
                                case OneSplitScreen.LAYOUT:
                                    obj.add(new DefaultViewCellScreens(1));
                                    jsonObject.put("defaultViewCellScreens", obj);
                                    break;
                                case FourSplitScreen.LAYOUT:
                                    for (int i = 0; i < 4; i++) {
                                        obj.add(new DefaultViewCellScreens(i + 1));
                                    }
                                    jsonObject.put("defaultViewCellScreens", obj);
                                    break;
                                case NineSplitScreen.LAYOUT:
                                    for (int i = 0; i < 8; i++) {
                                        obj.add(new DefaultViewCellScreens(i + 1));
                                    }
                                    jsonObject.put("defaultViewCellScreens", obj);
                                    break;
                                case AutomaticSplitScreen.LAYOUT:
                                    jsonObject.put("defaultViewLayout", "automatic");
                                    break;
                                case OnePlusFiveSplitScreen.LAYOUT:
                                    for (int i = 0; i < 6; i++) {
                                        obj.add(new DefaultViewCellScreens(i + 1));
                                    }
                                    jsonObject.put("defaultViewCellScreens", obj);
                                    break;
                                case OnePlusSevenSplitScreen.LAYOUT:
                                    for (int i = 0; i < 8; i++) {
                                        obj.add(new DefaultViewCellScreens(i + 1));
                                    }
                                    jsonObject.put("defaultViewCellScreens", obj);
                                    break;
                                default:
                                    break;
                            }
                            defaultAttendeeOperationPackageService.updateDefaultViewConfigInfo(conferenceContext.getId(), jsonObject);
                            job.put("updateDefaultViewConfigInfo", "success");
                        } else if (conferenceContext instanceof McuZjConferenceContext) {
                            McuZjConferenceContext mcuZjConferenceContext = (McuZjConferenceContext) conferenceContext;
                            List<DefaultViewCellScreens> obj = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("defaultViewLayout", defaultViewLayout);
                            jsonObject.put("defaultViewIsBroadcast", defaultViewIsBroadcast);
                            jsonObject.put("defaultViewIsDisplaySelf", defaultViewIsDisplaySelf);
                            jsonObject.put("defaultViewIsFill", defaultViewIsFill);
                            jsonObject.put("pollingInterval", pollingInterval);
                            switch (defaultViewLayout) {
                                case OneSplitScreen.LAYOUT:
                                    obj.add(new DefaultViewCellScreens(1));
                                    jsonObject.put("defaultViewCellScreens", obj);
                                    break;
                                case FourSplitScreen.LAYOUT:
                                    for (int i = 0; i < 4; i++) {
                                        obj.add(new DefaultViewCellScreens(i + 1));
                                    }
                                    jsonObject.put("defaultViewCellScreens", obj);
                                    break;
                                case OnePlusFiveSplitScreen.LAYOUT:
                                    for (int i = 0; i < 6; i++) {
                                        obj.add(new DefaultViewCellScreens(i + 1));
                                    }
                                    jsonObject.put("defaultViewCellScreens", obj);
                                    break;
                                case OnePlusSevenSplitScreen.LAYOUT:
                                    for (int i = 0; i < 8; i++) {
                                        obj.add(new DefaultViewCellScreens(i + 1));
                                    }
                                    jsonObject.put("defaultViewCellScreens", obj);
                                    break;
                                case NineSplitScreen.LAYOUT:
                                    for (int i = 0; i < 9; i++) {
                                        obj.add(new DefaultViewCellScreens(i + 1));
                                    }
                                    jsonObject.put("defaultViewCellScreens", obj);
                                    break;
                                case OnePlusNineSplitScreen.LAYOUT:
                                    for (int i = 0; i < 10; i++) {
                                        obj.add(new DefaultViewCellScreens(i + 1));
                                    }
                                    jsonObject.put("defaultViewCellScreens", obj);
                                    break;
                                case SixteenSplitScreen.LAYOUT:
                                    for (int i = 0; i < 16; i++) {
                                        obj.add(new DefaultViewCellScreens(i + 1));
                                    }
                                    jsonObject.put("defaultViewCellScreens", obj);
                                    break;
                                case TwentyFiveSplitScreen.LAYOUT:
                                    for (int i = 0; i < 25; i++) {
                                        obj.add(new DefaultViewCellScreens(i + 1));
                                    }
                                    jsonObject.put("defaultViewCellScreens", obj);
                                    break;
                                case AutomaticSplitScreen.LAYOUT:
                                    jsonObject.put("defaultViewLayout", "automatic");
                                    break;
                                default:
                                    break;
                            }
                            defaultAttendeeOperationPackageForMcuZjService.updateDefaultViewConfigInfo(mcuZjConferenceContext.getId(), jsonObject);
                            // 终端会控可广播但不广播时，有主会场时看主会场，无主会场时自动
                            if (!mcuZjConferenceContext.isSingleView() && mcuZjConferenceContext.isSupportBroadcast()) {
                                if (mcuZjConferenceContext.getDefaultViewOperation().getDefaultViewIsBroadcast() == BroadcastStatus.NO.getValue()) {
                                    mcuZjConferenceContext.setAttendeeOperationForGuest(mcuZjConferenceContext.getDefaultViewOperation());
                                }
                            }
                            job.put("updateDefaultViewConfigInfo", "success");
                        } else if (conferenceContext instanceof McuPlcConferenceContext) {
                            List<DefaultViewCellScreens> obj = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("defaultViewLayout", defaultViewLayout);
                            jsonObject.put("defaultViewIsBroadcast", defaultViewIsBroadcast);
                            jsonObject.put("defaultViewIsDisplaySelf", defaultViewIsDisplaySelf);
                            jsonObject.put("defaultViewIsFill", defaultViewIsFill);
                            jsonObject.put("pollingInterval", pollingInterval);
                            switch (defaultViewLayout) {
                                case OneSplitScreen.LAYOUT:
                                    obj.add(new DefaultViewCellScreens(1));
                                    jsonObject.put("defaultViewCellScreens", obj);
                                    break;
                                case FourSplitScreen.LAYOUT:
                                    for (int i = 0; i < 4; i++) {
                                        obj.add(new DefaultViewCellScreens(i + 1));
                                    }
                                    jsonObject.put("defaultViewCellScreens", obj);
                                    break;
                                case OnePlusFiveSplitScreen.LAYOUT:
                                    for (int i = 0; i < 6; i++) {
                                        obj.add(new DefaultViewCellScreens(i + 1));
                                    }
                                    jsonObject.put("defaultViewCellScreens", obj);
                                    break;
                                case OnePlusSevenSplitScreen.LAYOUT:
                                    for (int i = 0; i < 8; i++) {
                                        obj.add(new DefaultViewCellScreens(i + 1));
                                    }
                                    jsonObject.put("defaultViewCellScreens", obj);
                                    break;
                                case NineSplitScreen.LAYOUT:
                                    for (int i = 0; i < 9; i++) {
                                        obj.add(new DefaultViewCellScreens(i + 1));
                                    }
                                    jsonObject.put("defaultViewCellScreens", obj);
                                    break;
                                case SixteenSplitScreen.LAYOUT:
                                    for (int i = 0; i < 16; i++) {
                                        obj.add(new DefaultViewCellScreens(i + 1));
                                    }
                                    jsonObject.put("defaultViewCellScreens", obj);
                                    break;
                                case AutomaticSplitScreen.LAYOUT:
                                    jsonObject.put("defaultViewLayout", "automatic");
                                    break;
                                default:
                                    break;
                            }
                            defaultAttendeeOperationPackageForMcuPlcService.updateDefaultViewConfigInfo(conferenceContext.getId(), jsonObject);
                            job.put("updateDefaultViewConfigInfo", "success");
                        } else if (conferenceContext instanceof McuKdcConferenceContext) {
                            List<DefaultViewCellScreens> obj = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("defaultViewLayout", defaultViewLayout);
                            jsonObject.put("defaultViewIsBroadcast", defaultViewIsBroadcast);
                            jsonObject.put("defaultViewIsDisplaySelf", defaultViewIsDisplaySelf);
                            jsonObject.put("defaultViewIsFill", defaultViewIsFill);
                            jsonObject.put("pollingInterval", pollingInterval);
                            switch (defaultViewLayout) {
                                case OneSplitScreen.LAYOUT:
                                    obj.add(new DefaultViewCellScreens(1));
                                    jsonObject.put("defaultViewCellScreens", obj);
                                    break;
                                case FourSplitScreen.LAYOUT:
                                    for (int i = 0; i < 4; i++) {
                                        obj.add(new DefaultViewCellScreens(i + 1));
                                    }
                                    jsonObject.put("defaultViewCellScreens", obj);
                                    break;
                                case OnePlusFiveSplitScreen.LAYOUT:
                                    for (int i = 0; i < 6; i++) {
                                        obj.add(new DefaultViewCellScreens(i + 1));
                                    }
                                    jsonObject.put("defaultViewCellScreens", obj);
                                    break;
                                case OnePlusSevenSplitScreen.LAYOUT:
                                    for (int i = 0; i < 8; i++) {
                                        obj.add(new DefaultViewCellScreens(i + 1));
                                    }
                                    jsonObject.put("defaultViewCellScreens", obj);
                                    break;
                                case NineSplitScreen.LAYOUT:
                                    for (int i = 0; i < 9; i++) {
                                        obj.add(new DefaultViewCellScreens(i + 1));
                                    }
                                    jsonObject.put("defaultViewCellScreens", obj);
                                    break;
                                case SixteenSplitScreen.LAYOUT:
                                    for (int i = 0; i < 16; i++) {
                                        obj.add(new DefaultViewCellScreens(i + 1));
                                    }
                                    jsonObject.put("defaultViewCellScreens", obj);
                                    break;
                                case AutomaticSplitScreen.LAYOUT:
                                    jsonObject.put("defaultViewLayout", "automatic");
                                    break;
                                default:
                                    break;
                            }
                            defaultAttendeeOperationPackageForMcuKdcService.updateDefaultViewConfigInfo(conferenceContext.getId(), jsonObject);
                            job.put("updateDefaultViewConfigInfo", "success");
                        }
                    }
                }
            }
            this.responseTerminal(terminalTopic, action, job, clientId, messageId);
        } catch (Exception e) {
            String message = null;
            if (e instanceof SystemException) {
                message = e.getMessage();
            } else {
                message = "操作失败";
            }
            ResponseTerminal.getInstance().responseTerminalByQOS(ResponseInfo.CODE_500, message, terminalTopic, action, job, messageId, QosEnum.QOS2);
        }
    }

    @Override
    public Integer getLiveTerminalCount(String conferenceId) {
        Integer liveConferenceTerminalCount = LiveBridgeCache.getInstance().getLiveConferenceTerminalCount(conferenceId);
        return liveConferenceTerminalCount;
    }

    @Override
    public void reCall(JSONObject jsonS, String clientId, String messageId) {
        String conferenceNum = jsonS.getString(MqttConfigConstant.CONFERENCENUM);
        JSONArray attendIds = jsonS.getJSONArray("attendIds");
        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
        String action = TerminalTopic.RE_CALL;
        boolean canControlConference = canControlConference(clientId, conferenceNum);
        if (!canControlConference) {
            ResponseTerminal.getInstance().responseTerminal(ResponseInfo.CODE_401, "没有操作权限！", terminalTopic, action, null, clientId, "");
            return;
        }

        JSONObject jObj = new JSONObject();
        String isSuccess = "fail";
        BusiTerminal opTerminal = TerminalCache.getInstance().getBySn(clientId);
        BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().getContext(conferenceNum, opTerminal);
        if (conferenceContext != null) {
            if (conferenceContext instanceof ConferenceContext) {
                if (attendIds != null && attendIds.size() > 0) {
                    for (Object attendId : attendIds) {
                        attendeeService.recall(conferenceContext.getId(), String.valueOf(attendId));
                    }
                } else {
                    busiConferenceService.reCall(conferenceContext.getId());
                }
                isSuccess = "success";
            } else if (conferenceContext instanceof McuZjConferenceContext) {
                if (attendIds != null && attendIds.size() > 0) {
                    for (Object attendId : attendIds) {
                        attendeeForMcuZjService.recall(conferenceContext.getId(), String.valueOf(attendId));
                    }
                } else {
                    busiMcuZjConferenceService.reCall(conferenceContext.getId());
                }
                isSuccess = "success";
            } else if (conferenceContext instanceof McuPlcConferenceContext) {
                if (attendIds != null && attendIds.size() > 0) {
                    for (Object attendId : attendIds) {
                        attendeeForMcuPlcService.recall(conferenceContext.getId(), String.valueOf(attendId));
                    }
                } else {
                    busiMcuPlcConferenceService.reCall(conferenceContext.getId());
                }
                isSuccess = "success";
            } else if (conferenceContext instanceof McuKdcConferenceContext) {
                if (attendIds != null && attendIds.size() > 0) {
                    for (Object attendId : attendIds) {
                        attendeeForMcuKdcService.recall(conferenceContext.getId(), String.valueOf(attendId));
                    }
                } else {
                    busiMcuKdcConferenceService.reCall(conferenceContext.getId());
                }
                isSuccess = "success";
            }
        }

        BusiTerminal busiTerminal = TerminalCache.getInstance().getBySn(clientId);
        jObj.put(MqttConfigConstant.ID, busiTerminal.getId());
        jObj.put(MqttConfigConstant.NAME, busiTerminal.getName());
        jObj.put(MqttConfigConstant.CLIENTID, busiTerminal.getSn());
        jObj.put(TerminalTopic.RE_CALL, isSuccess);

        //给消息发布者发送messageId
        this.messagePublisherTomessageId(clientId, busiTerminal.getSn(), messageId, terminalTopic, action, jObj);
    }

    /**
     * 邀请或移除终端直播进入直播或者会议
     * 0===无状态
     * 1===直播
     * 2===会议中
     * @return
     */
    @Override
    public int isInviteLiveTerminal(String mac, String conferenceId, String status) {

        String contextKey = AesEnsUtils.getAesEncryptor().encryptToHex(conferenceId);
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (baseConferenceContext != null) {
            String terminalTopic = "terminal/" + mac;
            String action = "liveTerminal";
            JSONObject jsonObject = new JSONObject();
            List<String> stringList = baseConferenceContext.getStreamUrlList();
            switch (status) {
                case "0":
                    jsonObject.put("status", status);
                    jsonObject.put("conferenceNum", baseConferenceContext.getTenantId() + baseConferenceContext.getConferenceNumber());
                    jsonObject.put("conferenceName", baseConferenceContext.getName());
                    ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, mac, "");
                    LOGGER.info("直播终端==" + mac + "==状态0==未进入直播和会议");
                    break;
                case "1":
                    if (baseConferenceContext.isStreaming()) {
                        int nextInt = 0;
                        String streamingUrl = baseConferenceContext.getStreamingUrl();
                        if (stringList.size() > 0) {
                            nextInt = new Random().nextInt(stringList.size());
                            streamingUrl = stringList.get(nextInt);
                        }
                        jsonObject.put("conferenceNum", baseConferenceContext.getTenantId() + baseConferenceContext.getConferenceNumber());
                        jsonObject.put("liveUrl", baseConferenceContext.getIsAutoCreateStreamUrl() == 1 ? streamingUrl : baseConferenceContext.getStreamingUrl());
                        jsonObject.put("status", status);
                        jsonObject.put("conferenceName", baseConferenceContext.getName());
                        ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, mac, "");
                        LOGGER.info("直播终端==" + mac + "==状态1==进入直播");
                    } else {
                        throw new SystemException("未开启直播，不能邀请终端进入直播！");
                    }
                    break;
                case "2":
                    if (baseConferenceContext.getConferencePassword() != null) {
                        jsonObject.put("conferencePassword", baseConferenceContext.getConferencePassword());
                    }
                    jsonObject.put("conferenceNum", baseConferenceContext.getTenantId() + baseConferenceContext.getConferenceNumber());
                    jsonObject.put("conferenceName", baseConferenceContext.getName());
                    jsonObject.put("status", status);
                    ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, mac, "");
                    LOGGER.info("直播终端==" + mac + "==状态1==进入会议" + baseConferenceContext.getConferenceNumber());
                    break;
                default:
                    break;
            }
        }
        return 1;
    }

    /**
     * 推送信息展示
     * @param jsonS
     * @param clientId
     * @param messageId
     */
    @Override
    public void pushInfoDisplay(JSONObject jsonS, String clientId, String messageId) {
        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
        String action = TerminalTopic.INFO_DISPLAY;
        JSONObject jsonObject = new JSONObject();

        boolean isPush = false;
        BusiTerminal terminal = TerminalCache.getInstance().getBySn(clientId);
        BusiInfoDisplay busiInfoDisplayTemp = new BusiInfoDisplay();
        Long terminalDeptId = terminal.getDeptId();
        SysDept sysDept = SysDeptCache.getInstance().get(terminalDeptId);
        String ancestors = sysDept.getAncestors();

        List<BusiInfoDisplay> busiInfoDisplayList = InfoDisplayCache.getInstance().getByDeptId(terminalDeptId);
        if (busiInfoDisplayList != null && busiInfoDisplayList.size() > 0) {
            for (BusiInfoDisplay busiInfoDisplay : busiInfoDisplayList) {
                if (busiInfoDisplay.getStatus() == 1 && busiInfoDisplay.getType() == 1 && busiInfoDisplay.getPushType() == 1) {
                    busiInfoDisplayTemp = busiInfoDisplay;
                    isPush = true;
                }
            }
        } else {
            if (StringUtils.isNotEmpty(ancestors)) {
                String[] split = ancestors.split(",");
                for (int i = split.length - 1; i >= 0; i--) {
                    String deptId = split[i];
                    List<BusiInfoDisplay> busiInfoDisplayListTemp = InfoDisplayCache.getInstance().getByDeptId(Long.valueOf(deptId));
                    if (busiInfoDisplayListTemp != null && busiInfoDisplayListTemp.size() > 0) {
                        for (BusiInfoDisplay busiInfoDisplay : busiInfoDisplayListTemp) {
                            if (busiInfoDisplay.getStatus() == 1 && busiInfoDisplay.getType() == 1 && busiInfoDisplay.getPushType() == 1) {
                                busiInfoDisplayTemp = busiInfoDisplay;
                                isPush = true;
                            }
                        }
                    }
                }
            }
        }
        if (!isPush) {
            BusiInfoDisplay busiInfoDisplay = new BusiInfoDisplay();
            busiInfoDisplay.setStatus(1);
            busiInfoDisplay.setPushType(1);
            busiInfoDisplay.setType(1);
            List<BusiInfoDisplay> busiInfoDisplays = busiInfoDisplayMapper.selectBusiInfoDisplayList(busiInfoDisplay);
            if (busiInfoDisplays != null && busiInfoDisplays.size() > 0) {
                for (BusiInfoDisplay infoDisplay : busiInfoDisplays) {
                    String pushTerminalIds = infoDisplay.getPushTerminalIds();
                    if (StringUtils.isNotEmpty(pushTerminalIds)) {
                        String[] split = pushTerminalIds.split(",");
                        for (String idStr : split) {
                            Long id = Long.valueOf(idStr);
                            if (id.longValue() == terminal.getId().longValue()) {
                                busiInfoDisplayTemp = busiInfoDisplays.get(0);
                                isPush = true;
                            }
                        }
                    }
                }
            }
        }

        if (isPush) {
            String urlTemp = ExternalConfigCache.getInstance().getFmcRootUrl();
            String fmcRootUrlExternal = ExternalConfigCache.getInstance().getFmcRootUrlExternal();
            if (com.paradisecloud.common.utils.StringUtils.isNotEmpty(fmcRootUrlExternal)) {
                try {
                    String ip = terminal.getConnectIp();
                    String externalIp = fmcRootUrlExternal.replace("http://", "").replace("https://", "");
                    if (externalIp.indexOf(":") > 0) {
                        externalIp.substring(0, externalIp.indexOf(":"));
                    }
                    if (externalIp.indexOf("/") > 0) {
                        externalIp = externalIp.substring(0, externalIp.indexOf("/"));
                    }
                    if (externalIp.equals(ip)) {
                        urlTemp = fmcRootUrlExternal;
                    }
                } catch (Exception e) {
                }
            }
            String introduce = busiInfoDisplayTemp.getUrlData();
            if (com.paradisecloud.common.utils.StringUtils.isNotEmpty(introduce)) {
                introduce = introduce.replace("{url}", urlTemp);
                jsonObject.put("urlData", introduce);
            }

            jsonObject.put("type", busiInfoDisplayTemp.getType());
            jsonObject.put("displayType", busiInfoDisplayTemp.getDisplayType());
            ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, clientId, "");
        } else {

            jsonObject.put("type", 0);
            ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, clientId, "");
        }
    }

    private void sendConferenceInfoToPushTargetTerminal(BaseConferenceContext conferenceContext) {
        sendConferenceInfoToPushTargetTerminal(conferenceContext, null);
    }

    private void sendConferenceInfoToPushTargetTerminal(BaseConferenceContext conferenceContext, Long newPresenter) {
        if (conferenceContext instanceof ConferenceContext) {
            sendConferenceInfoToPushTargetTerminal((ConferenceContext) conferenceContext, newPresenter);
        } else if (conferenceContext instanceof McuZjConferenceContext) {
            sendConferenceInfoToPushTargetTerminal((McuZjConferenceContext) conferenceContext, newPresenter);
        } else if (conferenceContext instanceof McuPlcConferenceContext) {
            sendConferenceInfoToPushTargetTerminal((McuPlcConferenceContext) conferenceContext, newPresenter);
        } else if (conferenceContext instanceof McuKdcConferenceContext) {
            sendConferenceInfoToPushTargetTerminal((McuKdcConferenceContext) conferenceContext, newPresenter);
        }
    }

    private void sendConferenceInfoToPushTargetTerminal(McuZjConferenceContext conferenceContext) {
        sendConferenceInfoToPushTargetTerminal(conferenceContext, null);
    }

    private void sendConferenceInfoToPushTargetTerminal(McuZjConferenceContext conferenceContext, Long newPresenter) {
        McuZjSendConferenceInfoToTerminalTask mcuZjSendConferenceInfoToTerminalTask = new McuZjSendConferenceInfoToTerminalTask(conferenceContext.getId(), 500, conferenceContext, newPresenter);
        delayTaskService.addTask(mcuZjSendConferenceInfoToTerminalTask);
    }

    private void sendConferenceInfoToPushTargetTerminal(ConferenceContext conferenceContext) {
        sendConferenceInfoToPushTargetTerminal(conferenceContext, null);
    }

    private void sendConferenceInfoToPushTargetTerminal(ConferenceContext conferenceContext, Long newPresenter) {
        SendConferenceInfoToTerminalTask sendConferenceInfoToTerminalTask = new SendConferenceInfoToTerminalTask(conferenceContext.getId(), 500, conferenceContext, newPresenter);
        taskService.addTask(sendConferenceInfoToTerminalTask);
    }

    private void sendConferenceInfoToPushTargetTerminal(McuPlcConferenceContext conferenceContext) {
        sendConferenceInfoToPushTargetTerminal(conferenceContext, null);
    }

    private void sendConferenceInfoToPushTargetTerminal(McuPlcConferenceContext conferenceContext, Long newPresenter) {
        McuPlcSendConferenceInfoToTerminalTask mcuPlcSendConferenceInfoToTerminalTask = new McuPlcSendConferenceInfoToTerminalTask(conferenceContext.getId(), 500, conferenceContext, newPresenter);
        mcuPlcDelayTaskService.addTask(mcuPlcSendConferenceInfoToTerminalTask);
    }

    private void sendConferenceInfoToPushTargetTerminal(McuKdcConferenceContext conferenceContext) {
        sendConferenceInfoToPushTargetTerminal(conferenceContext, null);
    }

    private void sendConferenceInfoToPushTargetTerminal(McuKdcConferenceContext conferenceContext, Long newPresenter) {
        McuKdcSendConferenceInfoToTerminalTask mcuKdcSendConferenceInfoToTerminalTask = new McuKdcSendConferenceInfoToTerminalTask(conferenceContext.getId(), 500, conferenceContext, newPresenter);
        mcuKdcDelayTaskService.addTask(mcuKdcSendConferenceInfoToTerminalTask);
    }

}