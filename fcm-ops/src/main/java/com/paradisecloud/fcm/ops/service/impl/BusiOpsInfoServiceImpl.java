package com.paradisecloud.fcm.ops.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.ConferenceTemplateCreateType;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.utils.PathUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.DeptFmeMappingCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiTemplateConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.ITemplateConferenceStartService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.response.callprofile.CallProfileInfoResponse;
import com.paradisecloud.fcm.fme.service.interfaces.IBusiFmeDeptService;
import com.paradisecloud.fcm.mqtt.cache.MqttDeptMappingCache;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiMqttDeptService;
import com.paradisecloud.fcm.ops.service.DatabaseBackupService;
import com.paradisecloud.fcm.ops.service.IBusiOpsInfoService;
import com.paradisecloud.fcm.ops.task.OpsChangeConfigTask;
import com.paradisecloud.fcm.ops.task.OpsRestart_SudoTask;
import com.paradisecloud.fcm.ops.task.OpsShutdownTask;
import com.paradisecloud.fcm.ops.utils.*;
import com.paradisecloud.fcm.terminal.fs.interfaces.IBusiFreeSwitchDeptService;
import com.paradisecloud.fcm.web.utils.SshConfigConstant;
import com.paradisecloud.system.dao.mapper.SysUserMapper;
import com.paradisecloud.system.dao.model.SysUser;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.file.*;
import java.util.*;


/**
 * ops配置信息Service业务层处理
 *
 * @author lilinhai
 * @date 2024-05-27
 */
@Slf4j
@Service
@Transactional
public class BusiOpsInfoServiceImpl implements IBusiOpsInfoService {
    private static final String filePath = PathUtil.getRootPath() + "/external_config.properties";
    @Resource
    private DatabaseBackupService databaseBackupService;
    @Resource
    private BusiOpsInfoMapper busiOpsInfoMapper;
    @Resource
    private BusiFmeMapper busiFmeMapper;

    @Resource
    private BusiMqttMapper busiMqttMapper;

    @Resource
    private BusiFreeSwitchMapper busiFreeSwitchMapper;

    @Resource
    private IBusiFmeDeptService iBusiFmeDeptService;

    @Resource
    private BusiFmeDeptMapper busiFmeDeptMapper;

    @Resource
    private IBusiMqttDeptService busiMqttDeptService;

    @Resource
    private BusiMqttDeptMapper busiMqttDeptMapper;

    @Resource
    private BusiFreeSwitchDeptMapper busiFreeSwitchDeptMapper;

    @Resource
    private IBusiFreeSwitchDeptService busiFreeSwitchDeptService;


    @Resource
    private TaskService taskService;


    @Resource
    private IBusiTemplateConferenceService busiTemplateConferenceService;

    @Resource
    private ITemplateConferenceStartService templateConferenceStartService;

    @Resource
    private SysUserMapper sysUserMapper;

    /**
     * 查询ops配置信息
     *
     * @param id ops配置信息ID
     * @return ops配置信息
     */
    @Override
    public BusiOpsInfo selectBusiOpsInfoById(Integer id) {
        return busiOpsInfoMapper.selectBusiOpsInfoById(id);
    }

    /**
     * 查询ops配置信息列表
     *
     * @param busiOpsInfo ops配置信息
     * @return ops配置信息
     */
    @Override
    public List<BusiOpsInfo> selectBusiOpsInfoList(BusiOpsInfo busiOpsInfo) {
        return busiOpsInfoMapper.selectBusiOpsInfoList(busiOpsInfo);
    }

    /**
     * 新增ops配置信息
     *
     * @param busiOpsInfo ops配置信息
     * @return 结果
     */
    @Override
    public int insertBusiOpsInfo(BusiOpsInfo busiOpsInfo) {
        List<BusiOpsInfo> busiOpsInfos = busiOpsInfoMapper.selectBusiOpsInfoList(new BusiOpsInfo());
        if (CollectionUtils.isNotEmpty(busiOpsInfos)) {
            throw new CustomException("已经初始化");
        }
        int i = busiOpsInfoMapper.insertBusiOpsInfo(busiOpsInfo);

        if (i > 0) {
            SshRemoteServerOperateForOPSChangeIp sshRemoteServerOperate = SshRemoteServerOperateForOPSChangeIp.getInstance();
            try {
                sshRemoteServerOperate.sshRemoteCallLogin(busiOpsInfos.get(0).getIpAddress(), SshConfigConstant.SERVER_DEFAULT_USER_NAME_FOR_OPS, SshConfigConstant.SERVER_DEFAULT_PASSWORD, SshConfigConstant.DEFAULT_SERVER_PORT);
                boolean logined = sshRemoteServerOperate.isLogined();
                if(!logined){
                    throw new CustomException("ops配置信息失败,请重试");
                }
            } catch (Exception e) {
                throw new CustomException("ops配置信息失败,请重试");
            }
            OpsChangeConfigTask opsChangeConfigTask = new OpsChangeConfigTask("ops初始化init", 1000, busiOpsInfo, "", "",sshRemoteServerOperate);
            taskService.addTask(opsChangeConfigTask);
        } else {
            throw new CustomException("初始化失败");
        }

        return i;

    }

    /**
     * 修改ops配置信息
     *
     * @param busiOpsInfo ops配置信息
     * @return 结果
     */
    @Override
    public int updateBusiOpsInfo(BusiOpsInfo busiOpsInfo) {


        List<BusiOpsInfo> busiOpsInfos = busiOpsInfoMapper.selectBusiOpsInfoList(new BusiOpsInfo());
        String old_fmeIp = "";
        if (CollectionUtils.isEmpty(busiOpsInfos)) {
            throw new CustomException("ops配置信息不存在");
        }
        old_fmeIp = busiOpsInfos.get(0).getFmeIp();
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByBridgeAddress(old_fmeIp);
        if (fmeBridge != null) {
            Map<String, ConferenceContext> ccMap = new HashMap<>();
            fmeBridge.getDataCache().eachCoSpace((coSpace) -> {

                ConferenceContext conferenceContextExist = null;
                Collection<ConferenceContext> conferenceContextList = ConferenceContextCache.getInstance().getConferenceContextListByConferenceNum(coSpace.getUri());
                if (conferenceContextList != null && conferenceContextList.size() > 0) {
                    for (ConferenceContext conferenceContextTemp : conferenceContextList) {
                        if (coSpace.getId().equals(conferenceContextTemp.getCoSpaceId())) {
                            conferenceContextExist = conferenceContextTemp;
                            break;
                        }
                    }
                }

                ConferenceContext cc = conferenceContextExist;
                if (cc != null) {
                    ccMap.put(cc.getId(), cc);
                }
            });

            if (!ccMap.isEmpty()) {
                throw new SystemException(1002435, "当前FME有会议正在进行中，无法删除！");
            }

        }

        int i = busiOpsInfoMapper.updateBusiOpsInfo(busiOpsInfo);
        if (i > 0) {
            SshRemoteServerOperateForOPSChangeIp sshRemoteServerOperate = SshRemoteServerOperateForOPSChangeIp.getInstance();
            try {
                sshRemoteServerOperate.sshRemoteCallLogin(busiOpsInfos.get(0).getIpAddress(), SshConfigConstant.SERVER_DEFAULT_USER_NAME_FOR_OPS, SshConfigConstant.SERVER_DEFAULT_PASSWORD, SshConfigConstant.DEFAULT_SERVER_PORT);
                boolean logined = sshRemoteServerOperate.isLogined();
                if(!logined){
                    throw new CustomException("修改ops配置信息失败,请重试");
                }
            } catch (Exception e) {
                throw new CustomException("修改ops配置信息失败,请重试");
            }

            OpsChangeConfigTask opsChangeConfigTask = new OpsChangeConfigTask("busiopsChangeTask", 1000, busiOpsInfo, old_fmeIp, busiOpsInfos.get(0).getIpAddress(),sshRemoteServerOperate);
            taskService.addTask(opsChangeConfigTask);
        } else {
            throw new CustomException("修改ops配置信息失败");
        }
        return i;

    }

    /**
     * 批量删除ops配置信息
     *
     * @param ids 需要删除的ops配置信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiOpsInfoByIds(Integer[] ids) {
        return busiOpsInfoMapper.deleteBusiOpsInfoByIds(ids);
    }

    /**
     * 删除ops配置信息信息
     *
     * @param id ops配置信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiOpsInfoById(Integer id) {
        return busiOpsInfoMapper.deleteBusiOpsInfoById(id);
    }


    @Override
    public int restart() {


        BusiOpsInfo busiOpsInfoQuery = new BusiOpsInfo();
        List<BusiOpsInfo> busiOpsInfos = busiOpsInfoMapper.selectBusiOpsInfoList(busiOpsInfoQuery);

        if (CollectionUtils.isEmpty(busiOpsInfos)) {
            throw new CustomException("初始化ops配置信息失败，未查询到数据");
        }
        BusiOpsInfo busiOpsInfo = busiOpsInfos.get(0);

        OpsRestart_SudoTask opsRestartTask = new OpsRestart_SudoTask("重启OPS", 5000, busiOpsInfo.getIpAddress());
        taskService.addTask(opsRestartTask);

        return 1;
    }


    @Override
    public int shutdown() {


        BusiOpsInfo busiOpsInfoQuery = new BusiOpsInfo();
        List<BusiOpsInfo> busiOpsInfos = busiOpsInfoMapper.selectBusiOpsInfoList(busiOpsInfoQuery);

        if (CollectionUtils.isEmpty(busiOpsInfos)) {
            throw new CustomException("初始化ops配置信息失败，未查询到数据");
        }
        BusiOpsInfo busiOpsInfo = busiOpsInfos.get(0);
        OpsShutdownTask shutdownTask = new OpsShutdownTask("重启OPS", 5000, busiOpsInfo.getIpAddress(), busiOpsInfo.getFmeIp());
        taskService.addTask(shutdownTask);
        return 1;
    }

    @Override
    public String thermal_zone() {
        SshRemoteServerOperateForOPS sshRemoteServerOperate = SshRemoteServerOperateForOPS.getInstance();
        try {
            BusiOpsInfo busiOpsInfoQuery = new BusiOpsInfo();
            List<BusiOpsInfo> busiOpsInfos = busiOpsInfoMapper.selectBusiOpsInfoList(busiOpsInfoQuery);

            if (CollectionUtils.isEmpty(busiOpsInfos)) {
                throw new CustomException("初始化ops配置信息失败，未查询到数据");
            }
            BusiOpsInfo busiOpsInfo = busiOpsInfos.get(0);
            sshRemoteServerOperate.sshRemoteCallLogin(busiOpsInfo.getIpAddress(),
                    SshConfigConstant.SERVER_DEFAULT_USER_NAME_FOR_OPS , 
                    SshConfigConstant.SERVER_DEFAULT_PASSWORD,
                    SshConfigConstant.DEFAULT_SERVER_PORT);
            boolean logined = sshRemoteServerOperate.isLogined();
            if (logined) {
                int avg = 0;

                try {
                    String result = sshRemoteServerOperate.execCommand("cat /sys/class/thermal/thermal_zone2/temp");
                    if (result != null) {
                        String s = result.replaceAll("\n", "");
                        int number = Integer.parseInt(s);
                        int number1 = (number / 1000);
                        avg = avg + number1;
                    }
                } catch (Exception e) {
                   if(avg==0){
                       String result = sshRemoteServerOperate.execCommand("cat /sys/class/thermal/thermal_zone1/temp");
                       if (result != null) {
                           String s = result.replaceAll("\n", "");
                           int number = Integer.parseInt(s);
                           int number1 = (number / 1000);
                           avg = avg + number1;
                       }
                   }
                }

                return avg + "";
            }
        } catch (Exception e) {

        } finally {
            sshRemoteServerOperate.closeSession();
        }
        return "未知";
    }


    @Override
    public Object getNetworkInfo() {
        HashMap<Object, Object> map = new HashMap<>();
        Boolean upFlag = false;
        try {
            // 获取宿主机上所有的网络接口
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            // 遍历每个网络接口
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (networkInterface != null) {
                    if (networkInterface.isUp()) {
                        upFlag = true;
                        break;
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }
        map.put("upSate", upFlag);
        if (upFlag) {
            List<BusiOpsInfo> busiOpsInfos = busiOpsInfoMapper.selectBusiOpsInfoList(new BusiOpsInfo());
            BusiOpsInfo busiOpsInfoOld = busiOpsInfos.get(0);
            String gateway = busiOpsInfoOld.getGatewayName();
            boolean reachable = IpUtil.isReachable(gateway);
            map.put("reachable", reachable);
        } else {
            map.put("reachable", false);
        }

        return map;
    }


    @Override
    public Object getNetworkInfoAddress(String ipAddress) {
        HashMap<Object, Object> map = new HashMap<>();
        Boolean upFlag = false;
        try {
            // 获取宿主机上所有的网络接口
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            // 遍历每个网络接口
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (networkInterface != null) {
                    if (networkInterface.isUp()) {
                        upFlag = true;
                        break;
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }
        map.put("upSate", upFlag);
        if (upFlag) {
            boolean reachable = IpUtil.isReachable(ipAddress);
            map.put("reachable", reachable);
        } else {
            map.put("reachable", false);
        }

        return map;
    }

    @Override
    @Transactional
    public int initBusiOpsInfo(BusiOpsInfo busiOpsInfo) {


        BusiOpsInfo busiOpsInfoQuery = new BusiOpsInfo();
        List<BusiOpsInfo> busiOpsInfos = busiOpsInfoMapper.selectBusiOpsInfoList(busiOpsInfoQuery);

        if (CollectionUtils.isEmpty(busiOpsInfos)) {
            throw new CustomException("初始化ops配置信息失败，未查询到数据");
        }
        //修改 MCU FMQ FCM 数据库 没有新增
        try {
            updateDb(busiOpsInfo);
        } catch (Exception e) {
            throw new CustomException("ops配置信息失败");
        }

        SshRemoteServerOperateForOPS sshRemoteServerOperate = SshRemoteServerOperateForOPS.getInstance();
        String localIp = IpUtil.getLocalIp();
        //连接ssh执行脚本
        ipchange(busiOpsInfo, localIp, sshRemoteServerOperate);


        return 1;
    }

    private void ipchange(BusiOpsInfo busiOpsInfo, String localIP, SshRemoteServerOperateForOPS sshRemoteServerOperate) {
        try {
            sshRemoteServerOperate.sshRemoteCallLogin(localIP, 
                    SshConfigConstant.SERVER_DEFAULT_USER_NAME_FOR_OPS, 
                    SshConfigConstant.SERVER_DEFAULT_PASSWORD,
                    SshConfigConstant.DEFAULT_SERVER_PORT);
            boolean logined = sshRemoteServerOperate.isLogined();
            if (logined) {
                log.info("修改本地IP");
                String netplanConfigFile = "/etc/netplan/00-installer-config.yaml";
                //获取本机IP
                String command_GET_IP = "grep -oP 'addresses:\\s*\\[\\K[^\\]]+' /etc/netplan/00-installer-config.yaml";
                String old_ip = sshRemoteServerOperate.execCommand(command_GET_IP);
                old_ip = old_ip.replace("\n", "");
                old_ip = old_ip.split("/")[0];
                log.info("本地IP:" + old_ip);
                String old_fme_ip = IpUtil.incrementLastOctet(old_ip);
                log.info("old_fme_ip:" + old_fme_ip);
                String new_ip = busiOpsInfo.getIpAddress();
                log.info("new_ip:" + new_ip);
                String new_fme_ip = IpUtil.incrementLastOctet(new_ip);
                log.info("new_fme_ip:" + new_fme_ip);

                //子网掩码计算
                int subnet = IpUtil.subnetMaskToCIDR(busiOpsInfo.getSubnetMask());


                //执行脚本
                String scriptPath = "/home/all_confs/scripts/change_ip.sh";
                String scriptArgs = old_ip + " " + old_fme_ip + " " + new_ip + " " + new_fme_ip;
                String scriptCommand = scriptPath + " " + scriptArgs;
                log.info("执行脚本命令：" + scriptCommand);
                String s = sshRemoteServerOperate.execCommand(scriptCommand);
                log.info("执行脚本结果：" + s);
                //修改本机IP
                String command = String.format(
                        "sudo sed -i 's|addresses: .*|addresses: [%s]|' %s && " +
                                "sudo sed -i 's|via: .*|via: %s|' %s ",
                        busiOpsInfo.getIpAddress() + "/" + subnet, netplanConfigFile,
                        busiOpsInfo.getGatewayName(), netplanConfigFile
                );
                String result = sshRemoteServerOperate.execCommand(command);
                log.info("修改netplanConfig结果：" + result);

                //修改FME地址
                SshRemoteServerOperateForFMEOPS sshRemoteServerOperate_fme = SshRemoteServerOperateForFMEOPS.getInstance();
                try {
                    sshRemoteServerOperate_fme.sshRemoteCallLogin(old_fme_ip, "ttadmin", "tTcl0uds@cn", 22);
                    boolean logined_fme = sshRemoteServerOperate.isLogined();
                    if (logined_fme) {
                        sshRemoteServerOperate_fme.execCommand("recorder disable");
                        sshRemoteServerOperate_fme.execCommand("recorder sip listen a 6000 6001");
                        sshRemoteServerOperate_fme.execCommand("recorder nfs " + busiOpsInfo.getIpAddress() + ":/mnt/nfs");
                        sshRemoteServerOperate_fme.execCommand("recorder resolution 720p");
                        sshRemoteServerOperate_fme.execCommand("recorder enable");

                        sshRemoteServerOperate_fme.execCommand("streamer disable");
                        sshRemoteServerOperate_fme.execCommand("streamer sip listen a 7000 7000");
                        sshRemoteServerOperate_fme.execCommand("streamer sip certs dbs.key dbs.crt root.crt");
                        sshRemoteServerOperate_fme.execCommand("streamer sip resolution 720p");
                        sshRemoteServerOperate_fme.execCommand("streamer enable");

                        String cmd_fme = "ipv4 a add " + new_fme_ip + "/" + subnet + " " + busiOpsInfo.getGatewayName();
                        sshRemoteServerOperate_fme.execCommand(cmd_fme);
                        sshRemoteServerOperate_fme.execCommand("reboot");
                        sshRemoteServerOperate_fme.closeSession();

                    } else {
                        log.error("修改FMEIP登录失败：");
                    }
                } catch (Exception e) {
                    log.error("修改FMEIP登录失败：");
                } finally {
                    sshRemoteServerOperate_fme.closeSession();
                }
                log.info("重启ops服务器中......");

            } else {
                log.error("localhost登录失败");
            }
        } catch (Exception e) {
            log.error("修改本地IP：" + e.getMessage());
        } finally {
            sshRemoteServerOperate.closeSession();
        }
    }

    private void updateDb(BusiOpsInfo busiOpsInfo) {
        BusiFme busiFme = new BusiFme();
        List<BusiFme> bs = busiFmeMapper.selectBusiFmeList(busiFme);
        if (CollectionUtils.isEmpty(bs)) {
            busiFme.setIp(IpUtil.incrementLastOctet(busiOpsInfo.getIpAddress()));
            busiFme.setUsername("ttadmin");
            busiFme.setPassword("tTcl0uds@cn");
            busiFme.setAdminPassword("ttadmin");
            busiFme.setAdminPassword("tTcl0uds@cn");
            busiFme.setPort(9443);
            busiFme.setCapacity(80);
            busiFme.setCreateTime(new Date());
            busiFmeMapper.insertBusiFme(busiFme);


            BusiFmeDept busiFmeDept1 = busiFmeDeptMapper.selectBusiFmeDeptByDeptId(1L);
            if (busiFmeDept1 == null) {
                BusiFmeDept busiFmeDept = new BusiFmeDept();
                busiFmeDept.setFmeId(busiFme.getId());
                busiFmeDept.setDeptId(1L);
                busiFmeDept.setFmeType(1);
                iBusiFmeDeptService.insertBusiFmeDept(busiFmeDept);
            } else {
                busiFmeDept1.setFmeId(busiFme.getId());
                iBusiFmeDeptService.updateBusiFmeDept(busiFmeDept1);
            }


        } else {
            for (BusiFme b : bs) {
                b.setIp(IpUtil.incrementLastOctet(busiOpsInfo.getIpAddress()));
                b.setUsername("ttadmin");
                b.setPassword("tTcl0uds@cn");
                b.setAdminPassword("ttadmin");
                b.setAdminPassword("tTcl0uds@cn");
                b.setPort(9443);
                b.setCapacity(80);
                b.setUpdateTime(new Date());
                busiFmeMapper.updateBusiFme(b);


                boolean b1 = DeptFmeMappingCache.getInstance().containsKey(1L);
                if (b1) {
                    BusiFmeDept bindFme = DeptFmeMappingCache.getInstance().getBindFme(1L);
                    bindFme.setFmeId(b.getId());
                    iBusiFmeDeptService.updateBusiFmeDept(bindFme);
                } else {
                    BusiFmeDept busiFmeDept = new BusiFmeDept();
                    busiFmeDept.setFmeId(b.getId());
                    busiFmeDept.setDeptId(1L);
                    busiFmeDept.setFmeType(1);
                    iBusiFmeDeptService.insertBusiFmeDept(busiFmeDept);
                }

                break;
            }
        }
        //修改FMQ
        BusiMqtt busiMqtt = new BusiMqtt();
        List<BusiMqtt> busiMqtts = busiMqttMapper.selectBusiMqttList(busiMqtt);
        if (CollectionUtils.isEmpty(busiMqtts)) {
            busiMqtt.setUserName("admin");
            busiMqtt.setPassword(SshConfigConstant.SERVER_DEFAULT_PASSWORD);
            busiMqtt.setMqttName("FMQ");
            busiMqtt.setIp(busiOpsInfo.getIpAddress());
            busiMqtt.setTcpPort(1883);
            busiMqtt.setDashboardPort(18083);
            busiMqtt.setManagementPort(8081);
            busiMqtt.setServerPort(SshConfigConstant.DEFAULT_SERVER_PORT);
            busiMqtt.setMqttStartupPath("/home/emqx/bin/");
            busiMqtt.setServerUserName( SshConfigConstant.SERVER_DEFAULT_USER_NAME_FOR_OPS);
            busiMqtt.setServerPassword(SshConfigConstant.SERVER_DEFAULT_PASSWORD);
            busiMqtt.setNodeName("emqx@" + busiOpsInfo.getIpAddress());
            busiMqtt.setUseSsl(0);
            busiMqtt.setCreateTime(new Date());
            busiMqttMapper.insertBusiMqtt(busiMqtt);

            BusiMqttDept busiMqttDept1 = new BusiMqttDept();
            busiMqttDept1.setDeptId(1L);
            List<BusiMqttDept> busiMqttDepts = busiMqttDeptMapper.selectBusiMqttDeptList(busiMqttDept1);
            if (CollectionUtils.isEmpty(busiMqttDepts)) {
                busiMqttDept1.setMqttId(busiMqtt.getId());
                busiMqttDept1.setDeptId(1L);
                busiMqttDept1.setMqttType(1);
                busiMqttDeptService.insertBusiMqttDept(busiMqttDept1);
            } else {
                busiMqttDept1.setMqttId(busiMqtt.getId());
                busiMqttDeptService.updateBusiMqttDept(busiMqttDept1);
            }

            //配置文件修改IP
            PropertiesUtil.updateProperty(filePath, "fmqIpList", busiOpsInfo.getIpAddress());
        } else {
            String fmqIpList = "";
            for (BusiMqtt mqtt : busiMqtts) {
                mqtt.setUserName("admin");
                mqtt.setPassword(SshConfigConstant.SERVER_DEFAULT_PASSWORD);
                mqtt.setMqttName("FMQ");
                mqtt.setIp(busiOpsInfo.getIpAddress());
                mqtt.setTcpPort(1883);
                mqtt.setDashboardPort(18083);
                mqtt.setManagementPort(8081);
                mqtt.setServerPort(SshConfigConstant.DEFAULT_SERVER_PORT);
                mqtt.setMqttStartupPath("/home/emqx/bin/");
                mqtt.setServerUserName( SshConfigConstant.SERVER_DEFAULT_USER_NAME_FOR_OPS);
                mqtt.setServerPassword(SshConfigConstant.SERVER_DEFAULT_PASSWORD);
                mqtt.setNodeName("emqx@" + busiOpsInfo.getIpAddress());
                mqtt.setUseSsl(0);
                mqtt.setUpdateTime(new Date());
                if (Strings.isBlank(fmqIpList)) {
                    fmqIpList = busiOpsInfo.getIpAddress();
                } else {
                    fmqIpList = fmqIpList + "," + busiOpsInfo.getIpAddress();
                }
                busiMqttMapper.updateBusiMqtt(mqtt);


                BusiMqttDept busiMqttDept1 = new BusiMqttDept();
                busiMqttDept1.setDeptId(1L);
                List<BusiMqttDept> busiMqttDepts = busiMqttDeptMapper.selectBusiMqttDeptList(busiMqttDept1);
                if (CollectionUtils.isEmpty(busiMqttDepts)) {
                    busiMqttDept1.setMqttId(mqtt.getId());
                    busiMqttDept1.setDeptId(1L);
                    busiMqttDept1.setMqttType(1);
                    busiMqttDeptMapper.insertBusiMqttDept(busiMqttDept1);

                    if (!MqttDeptMappingCache.getInstance().containsKey(busiMqttDept1.getDeptId())) {
                        MqttDeptMappingCache.getInstance().put(busiMqttDept1.getDeptId(), busiMqttDept1);
                    }

                } else {
                    busiMqttDept1.setMqttId(mqtt.getId());
                    busiMqttDeptService.updateBusiMqttDept(busiMqttDept1);
                }

                break;
            }
            PropertiesUtil.updateProperty(filePath, "fmqIpList", fmqIpList);
        }

        //FCM
        BusiFreeSwitch busiFreeSwitch = new BusiFreeSwitch();

        List<BusiFreeSwitch> busiFreeSwitches = busiFreeSwitchMapper.selectBusiFreeSwitchList(busiFreeSwitch);

        if (CollectionUtils.isEmpty(busiFreeSwitches)) {

            busiFreeSwitch.setIp(busiOpsInfo.getIpAddress());
            busiFreeSwitch.setUserName( SshConfigConstant.SERVER_DEFAULT_USER_NAME_FOR_OPS);
            busiFreeSwitch.setPassword(SshConfigConstant.SERVER_DEFAULT_PASSWORD);
            busiFreeSwitch.setName("FCM");
            busiFreeSwitch.setPort(SshConfigConstant.DEFAULT_SERVER_PORT);
            busiFreeSwitch.setOutBound(0);
            busiFreeSwitch.setCreateTime(new Date());
            busiFreeSwitchMapper.insertBusiFreeSwitch(busiFreeSwitch);

            BusiFreeSwitchDept busiFreeSwitchDept = new BusiFreeSwitchDept();
            busiFreeSwitchDept.setDeptId(1L);
            List<BusiFreeSwitchDept> busiFreeSwitchDepts = busiFreeSwitchDeptMapper.selectBusiFreeSwitchDeptList(busiFreeSwitchDept);
            if (CollectionUtils.isEmpty(busiFreeSwitchDepts)) {
                busiFreeSwitchDept.setServerId(busiFreeSwitch.getId());
                busiFreeSwitchDept.setFcmType(1);
                int insert = busiFreeSwitchDeptMapper.insertBusiFreeSwitchDept(busiFreeSwitchDept);

            } else {
                BusiFreeSwitchDept busiFreeSwitchDept1 = busiFreeSwitchDepts.get(0);
                busiFreeSwitchDept1.setServerId(busiFreeSwitch.getId());
                busiFreeSwitchDeptService.updateBusiFreeSwitchDept(busiFreeSwitchDept1);

            }


        } else {
            for (BusiFreeSwitch freeSwitch : busiFreeSwitches) {
                freeSwitch.setIp(busiOpsInfo.getIpAddress());
                freeSwitch.setUserName( SshConfigConstant.SERVER_DEFAULT_USER_NAME_FOR_OPS);
                freeSwitch.setPassword(SshConfigConstant.SERVER_DEFAULT_PASSWORD);
                freeSwitch.setName("FCM");
                freeSwitch.setPort(SshConfigConstant.DEFAULT_SERVER_PORT);
                freeSwitch.setOutBound(0);
                freeSwitch.setUpdateTime(new Date());
                busiFreeSwitchMapper.updateBusiFreeSwitch(freeSwitch);

                BusiFreeSwitchDept busiFreeSwitchDept = new BusiFreeSwitchDept();
                busiFreeSwitchDept.setDeptId(1L);
                List<BusiFreeSwitchDept> busiFreeSwitchDepts = busiFreeSwitchDeptMapper.selectBusiFreeSwitchDeptList(busiFreeSwitchDept);
                if (CollectionUtils.isEmpty(busiFreeSwitchDepts)) {
                    busiFreeSwitchDept.setServerId(freeSwitch.getId());
                    busiFreeSwitchDept.setFcmType(1);
                    int insert = busiFreeSwitchDeptMapper.insertBusiFreeSwitchDept(busiFreeSwitchDept);

                } else {
                    BusiFreeSwitchDept busiFreeSwitchDept1 = busiFreeSwitchDepts.get(0);
                    busiFreeSwitchDept1.setServerId(freeSwitch.getId());
                    busiFreeSwitchDeptService.updateBusiFreeSwitchDept(busiFreeSwitchDept1);

                }

                break;
            }
        }
    }


    @Override
    public Object localRecorder() {
        FmeBridge fmeBridge_ = null;
        List<FmeBridge> fmeBridges = FmeBridgeCache.getInstance().getFmeBridges();
        for (FmeBridge fmeBridge : fmeBridges) {
            boolean available = fmeBridge.isAvailable();
            if (available) {
                fmeBridge_ = fmeBridge;
                break;
            }
        }
        return notMuteTemp(fmeBridge_);

    }

    @Override
    public void endlocalRecorder(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        Long id = conferenceIdVo.getId();
        try {
            try {
                ModelBean modelBean = busiTemplateConferenceService.selectBusiTemplateConferenceById(id);
                ModelBean templateConferenceModelBean = (ModelBean) modelBean.get("templateConference");
                String callLegProfileId = (String) templateConferenceModelBean.get("callLegProfileId");
                String callProfileId = (String) templateConferenceModelBean.get("callProfileId");


                FmeBridge fmeBridge_ = null;
                List<FmeBridge> fmeBridges = FmeBridgeCache.getInstance().getFmeBridges();
                for (FmeBridge fmeBridge : fmeBridges) {
                    boolean available = fmeBridge.isAvailable();
                    if (available) {
                        fmeBridge_ = fmeBridge;
                        break;
                    }
                }
                BusiCallLegProfileMapper busiCallLegProfileMapper = BeanFactory.getBean(BusiCallLegProfileMapper.class);
                BusiCallLegProfile busiCallLegProfile = new BusiCallLegProfile();
                busiCallLegProfile.setCallLegProfileUuid(callLegProfileId);
                busiCallLegProfile.setDeptId(100L);
                List<BusiCallLegProfile> busiCallLegProfiles = busiCallLegProfileMapper.selectBusiCallLegProfileList(busiCallLegProfile);
                if (CollectionUtils.isNotEmpty(busiCallLegProfiles)) {
                    BusiCallLegProfile busiCallLegProfile1 = busiCallLegProfiles.get(0);
                    busiCallLegProfileMapper.deleteBusiCallLegProfileById(busiCallLegProfile1.getId());
                }
                fmeBridge_.getCallProfileInvoker().deleteCallProfile(callProfileId);
                fmeBridge_.getCallLegProfileInvoker().deleteCallLegProfile(callLegProfileId);
            } catch (Exception e) {
            }

            busiTemplateConferenceService.deleteBusiTemplateConferenceById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ConferenceContext notMuteTemp(FmeBridge fmeBridge) {

        String calllegprofileId = getCalllegprofileId(fmeBridge);


        BusiProfileCall busiProfileCall = new BusiProfileCall();
        busiProfileCall.setName("本地录播");
        busiProfileCall.setDeptId(100L);
        Map<String, Object> params = new HashMap<>();
        params.put("recordingMode", "automatic");
        params.put("streamingMode", "automatic");
        params.put("sipRecorderUri", "recording@recorder.com");
        params.put("sipStreamerUri", "streaming@streamer.com");
        params.put("participantLimit", 2);

        params.put("chatAllowed", "");
        params.put("gatewayAudioCallOptimization", "");
        params.put("lockMode", "");
        params.put("locked", "");
        params.put("messageBannerText", "");
        params.put("muteBehavior", "");
        params.put("passcodeMode", "");
        params.put("passcodeTimeout", "");
        params.put("raiseHandEnabled", "");

        busiProfileCall.setParams(params);


        Long deptId = 100L;
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        buildParams(busiProfileCall, nameValuePairs);

        String profileId = fmeBridge.getCallProfileInvoker().createCallProfile(nameValuePairs);
        if (ObjectUtils.isEmpty(profileId)) {
            throw new SystemException(1001098, "创建Call profile失败");
        }

        CallProfileInfoResponse profileInfoResponse = fmeBridge.getCallProfileInvoker().getCallProfile(profileId);
        if (profileInfoResponse != null && profileInfoResponse.getCallProfile() != null) {
            // 更新缓存
            FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(deptId, new FmeBridgeAddpterProcessor() {
                @Override
                public void process(FmeBridge fmeBridge) {
                    fmeBridge.getDataCache().update(profileInfoResponse.getCallProfile());
                }
            });
        }

        busiProfileCall.getParams().put("id", profileId);
        busiProfileCall.getParams().put("deptId", deptId);

        //创建模板：
        BusiTemplateConference templateConference = new BusiTemplateConference();
        templateConference.setCallProfileId(profileId);
        templateConference.setName("本地录播");
        templateConference.setCallLegProfileId(calllegprofileId);
        templateConference.setDeptId(100L);
        templateConference.setIsAutoCall(2);
        templateConference.setBandwidth(2);
        templateConference.setBusinessFieldType(100);
        templateConference.setDurationEnabled(0);
        templateConference.setIsAutoCreateConferenceNumber(2);
        templateConference.setIsAutoCreateStreamUrl(1);
        templateConference.setViewType(1);
        templateConference.setCreateUserId(1L);
        templateConference.setCreateUserName("superAdmin");
        templateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
        templateConference.setIsAutoCreateConferenceNumber(1);
        templateConference.setStreamingEnabled(1);
        templateConference.setRecordingEnabled(1);
        templateConference.setMinutesEnabled(2);

        int c = busiTemplateConferenceService.insertBusiTemplateConferenceOps(templateConference, null, new ArrayList<>(), new ArrayList<>());
        if (c > 0) {
            // 分屏
            try {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("defaultViewLayout", "1+1_B");
                jsonObj.put("defaultViewIsBroadcast", 1);
                jsonObj.put("defaultViewIsDisplaySelf", 1);
                jsonObj.put("defaultViewIsFill", 1);
                jsonObj.put("pollingInterval", 10);
                jsonObj.put("defaultViewCellScreens", new ArrayList<>());
                jsonObj.put("defaultViewDepts", new ArrayList<>());
                jsonObj.put("defaultViewPaticipants", new ArrayList<>());
                busiTemplateConferenceService.updateDefaultViewConfigInfo(jsonObj, templateConference.getId());
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            Long id = templateConference.getId();

            String s = templateConferenceStartService.startTemplateConference(id);


            ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(s);

            return conferenceContext;

        }

        return null;
    }

    private String getCalllegprofileId(FmeBridge fmeBridge) {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("name", "(录播)入会方案"));
        nameValuePairs.add(new BasicNameValuePair("rxAudioMute", "false"));
        nameValuePairs.add(new BasicNameValuePair("allowAllPresentationContributionAllowed", "true"));
        nameValuePairs.add(new BasicNameValuePair("qualityMain", "max1080p30"));
        nameValuePairs.add(new BasicNameValuePair("qualityPresentation", "max720p5"));
        nameValuePairs.add(new BasicNameValuePair("participantCounter", "never"));
        nameValuePairs.add(new BasicNameValuePair("participantLabels", "true"));
        nameValuePairs.add(new BasicNameValuePair("muteSelfAllowed", "true"));
        nameValuePairs.add(new BasicNameValuePair("defaultLayout", ""));
        String calllegprofileId = fmeBridge.getCallLegProfileInvoker().createCallLegProfile(nameValuePairs);
        // 更新内存
        fmeBridge.getDataCache().update(fmeBridge.getCallLegProfileInvoker().getCallLegProfile(calllegprofileId).getCallLegProfile());
        return calllegprofileId;
    }


    private void buildParams(BusiProfileCall busiProfileCall, List<NameValuePair> nameValuePairs) {
        Assert.isTrue(busiProfileCall.getParams().containsKey("participantLimit"), "participantLimit不能为空");
//        Assert.isTrue(busiProfileCall.getParams().containsKey("messageBoardEnabled"), "messageBoardEnabled不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("locked"), "locked不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("lockMode"), "lockMode不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("recordingMode"), "recordingMode不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("streamingMode"), "streamingMode不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("passcodeMode"), "passcodeMode不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("passcodeTimeout"), "passcodeTimeout不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("gatewayAudioCallOptimization"), "gatewayAudioCallOptimization不能为空");
//        Assert.isTrue(busiProfileCall.getParams().containsKey("lyncConferenceMode"), "lyncConferenceMode不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("messageBannerText"), "messageBannerText不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("muteBehavior"), "muteBehavior不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("sipRecorderUri"), "sipRecorderUri不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("sipStreamerUri"), "sipStreamerUri不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("chatAllowed"), "chatAllowed不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("raiseHandEnabled"), "raiseHandEnabled不能为空");

        nameValuePairs.add(new BasicNameValuePair("participantLimit", busiProfileCall.getParams().get("participantLimit").toString()));
//        nameValuePairs.add(new BasicNameValuePair("messageBoardEnabled", busiProfileCall.getParams().get("messageBoardEnabled").toString()));
        nameValuePairs.add(new BasicNameValuePair("locked", busiProfileCall.getParams().get("locked").toString()));
        nameValuePairs.add(new BasicNameValuePair("lockMode", busiProfileCall.getParams().get("lockMode").toString()));
        nameValuePairs.add(new BasicNameValuePair("recordingMode", busiProfileCall.getParams().get("recordingMode").toString()));
        nameValuePairs.add(new BasicNameValuePair("streamingMode", busiProfileCall.getParams().get("streamingMode").toString()));
        nameValuePairs.add(new BasicNameValuePair("passcodeMode", busiProfileCall.getParams().get("passcodeMode").toString()));
        nameValuePairs.add(new BasicNameValuePair("passcodeTimeout", busiProfileCall.getParams().get("passcodeTimeout").toString()));
        nameValuePairs.add(new BasicNameValuePair("gatewayAudioCallOptimization", busiProfileCall.getParams().get("gatewayAudioCallOptimization").toString()));
//        nameValuePairs.add(new BasicNameValuePair("lyncConferenceMode", busiProfileCall.getParams().get("lyncConferenceMode").toString()));
        nameValuePairs.add(new BasicNameValuePair("messageBannerText", busiProfileCall.getParams().get("messageBannerText").toString()));
        nameValuePairs.add(new BasicNameValuePair("muteBehavior", busiProfileCall.getParams().get("muteBehavior").toString()));
        nameValuePairs.add(new BasicNameValuePair("sipRecorderUri", busiProfileCall.getParams().get("sipRecorderUri").toString()));
        nameValuePairs.add(new BasicNameValuePair("sipStreamerUri", busiProfileCall.getParams().get("sipStreamerUri").toString()));
        nameValuePairs.add(new BasicNameValuePair("chatAllowed", busiProfileCall.getParams().get("chatAllowed").toString()));
        nameValuePairs.add(new BasicNameValuePair("raiseHandEnabled", busiProfileCall.getParams().get("raiseHandEnabled").toString()));
    }

    @Override
    public String getPhone() {
        SysUser admin = sysUserMapper.selectUserByUserName("admin");
        return admin.getPhonenumber();
    }

    @Override
    public Object setPhone(String phoneNumber) {
        if (Strings.isBlank(phoneNumber)) {
            return 0;
        }
        if (isValidChineseMobileNumber(phoneNumber)) {
            SysUser admin = sysUserMapper.selectUserByUserName("admin");
            admin.setPhonenumber(phoneNumber);
            return sysUserMapper.updateUser(admin);
        } else {
            throw new CustomException("手机号格式不正确");
        }

    }

    public static boolean isValidChineseMobileNumber(String number) {
        String regex = "^1[3-9]\\d{9}$";
        return number.matches(regex);
    }

    @Override
    public int restore() throws Exception{


        String initPath = "/home/all_confs/backup/mysql/fcmdb_init.sql";

        databaseBackupService.restoreDatabase(initPath);


        return 1;
    }



    @Override
    public Object pingIp(String ip) {

        String result = "";
        String charset="utf-8";
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("ping", ip, "-c", "10");
            if (isWindows()) {
                processBuilder.command("cmd", "/c", "ping " + ip);
                charset = "gbk";
            }
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), charset));
            String line;
            while ((line = reader.readLine()) != null) {
                result += line + "\n";
            }
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (ping(ip)) {
            result += "\n";
            result += "tcp:5060 " + checkTcp(ip, 5060) + "\n";
            result += "tcp:5061 " + checkTcp(ip, 5061) + "\n";
            result += "udp:5060 " + checkUdp(ip, 5060) + "\n";
            result += "udp:5061 " + checkUdp(ip, 5061) + "\n";
        }

        return result;
    }

    private boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            return true;
        } else {
            return false;
        }
    }

    public static void executeCommand(String command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
               log.info(command+" completed successfully.");
            } else {
                log.info(command+" failed with error code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void changNetplanConfig(String filePath2, String newAddress, String newVia) {
        try {
            // 读取 YAML 文件
            List<String> lines = Files.readAllLines(Paths.get(filePath2));
            StringBuilder content = new StringBuilder();
            for (String line : lines) {
                content.append(line).append("\n");
            }

            // 创建 YAML 解析器
            Yaml yaml = new Yaml(new Constructor(Map.class));
            Map<String, Object> yamlData = yaml.load(content.toString());

            // 打印原始内容（调试用）
            log.info("Original netplan configuration:");
            log.info(yaml.dump(yamlData));

            // 获取网络配置
            Map<String, Object> network = (Map<String, Object>) yamlData.get("network");
            Map<String, Object> bridges = (Map<String, Object>) ((Map<String, Object>) network.get("bridges")).get("br0");

            // 修改 addresses
            if (bridges != null && bridges.containsKey("addresses")) {
                List<String> addresses = (List<String>) bridges.get("addresses");
                if (!addresses.isEmpty()) {
                    String currentAddress = addresses.get(0);
                    log.info("Current address: " + currentAddress);
                    addresses.set(0, newAddress);
                    log.info("New address: " + newAddress);
                } else {
                    log.info("No addresses found or addresses list is empty.");
                }
            } else {
                log.info("No addresses key found in the configuration.");
            }

            // 修改 via
            if (bridges != null && bridges.containsKey("routes")) {
                List<Map<String, Object>> routes = (List<Map<String, Object>>) bridges.get("routes");
                for (Map<String, Object> route : routes) {
                    if (route.containsKey("via")) {
                        String currentVia = (String) route.get("via");
                        log.info("Current via: " + currentVia);
                        route.put("via", newVia);
                        log.info("New via: " + newVia);
                    }
                }
            } else {
                log.info("No routes key found in the configuration.");
            }

            // 设置 YAML 输出选项
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Representer representer = new Representer();
            Yaml yamlWriter = new Yaml(representer, options);

            // 写回修改后的 YAML 文件
            String updatedYaml = yamlWriter.dump(yamlData);
            Files.write(Paths.get(filePath2), updatedYaml.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);

            log.info("File has been modified successfully.");

        } catch (IOException e) {
            log.info("An error occurred while accessing the file.");
            e.printStackTrace();
        } catch (ClassCastException e) {
            log.info("An error occurred while casting YAML data.");
            e.printStackTrace();
        }
    }

    private String getnetFilePath(String mountedDirPath) throws IOException {
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(mountedDirPath));

        for (Path path : directoryStream) {
            // 检查是否为常规文件
            if (Files.isRegularFile(path)) {
                Path fileName = path.getFileName();
                mountedDirPath = mountedDirPath + "/" + fileName.toString();
                break;
            }
        }
        log.info("mountedDirPath：" + mountedDirPath);

        return mountedDirPath;
    }

    public static String executeRemoteScript(String host, String user, String password, String scriptPath)
            throws  Exception {
        JSch jsch = new JSch();
        Session session = null;
        Channel channel = null;
        StringBuilder output = new StringBuilder();

        try {
            // 创建会话
            session = jsch.getSession(user, host, SshConfigConstant.DEFAULT_SERVER_PORT);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // 打开执行命令的通道
            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand("bash " + scriptPath);

            // 获取命令执行的输出流
            InputStream in = channel.getInputStream();

            // 启动通道，执行远程脚本
            channel.connect();

            // 等待脚本执行完成
            while (!channel.isClosed()) {
                Thread.sleep(1000);
            }

            // 读取脚本执行的输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

        } finally {
            // 关闭通道和会话
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }

        return output.toString();
    }

    private boolean ping(String ip) {
        if (null == ip || 0 == ip.length()) {
            return false;
        }

        try {
            InetAddress.getByName(ip);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean checkTcp(String ip, int port) {
        if (null == ip || 0 == ip.length() || port < 1024 || port > 65535) {
            return false;
        }

        Socket s = new Socket();
        try {
            SocketAddress add = new InetSocketAddress(ip, port);
            s.connect(add, 500);// 超时3秒
            return true;
        } catch (IOException e) {
        } finally {
            try {
                s.close();
            } catch (Exception e) {

            }
        }
        return false;
    }

    private boolean checkUdp(String ip, int port) {
        if (null == ip || 0 == ip.length() || port < 1024 || port > 65535) {
            return false;
        }

        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.connect(InetAddress.getByName(ip), port);
            return !(socket.getLocalPort() == socket.getPort());
        } catch (Exception e) {
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception e) {
                }
            }
        }
        return false;
    }

}
