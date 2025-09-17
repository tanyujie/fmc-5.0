package com.paradisecloud.fcm.ops.task;


import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.PathUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.ops.utils.*;
import com.paradisecloud.fcm.web.utils.SshConfigConstant;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.nio.file.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author admin
 */
public class OpsChangeConfigTask extends Task {

    private static final String filePath = PathUtil.getRootPath() + "/external_config.properties";
    private static final Logger log = LoggerFactory.getLogger(OpsChangeConfigTask.class);
    private final BusiOpsInfo busiOpsInfo;
    private final String oldIp;
    private String oldFmeIp;
    private final SshRemoteServerOperateForOPSChangeIp sshRemoteServerOperate;

    public OpsChangeConfigTask(String id, long delayInMilliseconds, BusiOpsInfo busiOpsInfo, String old_fmeIp, String old_ip, SshRemoteServerOperateForOPSChangeIp sshRemoteServerOperate) {
        super(id, delayInMilliseconds);
        this.busiOpsInfo = busiOpsInfo;
        this.oldFmeIp = old_fmeIp;
        this.oldIp = old_ip;
        this.sshRemoteServerOperate = sshRemoteServerOperate;
    }


    @Override
    public void run() {
        synchronized (this) {
            log.info("OPS服务器信息修改开始。ID:" + getId());

            // PropertiesUtil.updateProperty(filePath, "region","ops");
//            PropertiesUtil.updateProperty(filePath, "autoLoginUser", "admin");
//            PropertiesUtil.updateProperty(filePath, "autoLoginPassword", "123456");
            PropertiesUtil.updateProperty(filePath, "fmcRootUrl", "https://" + busiOpsInfo.getIpAddress() + ":8899");


            //修改 MCU FMQ FCM 数据库 没有新增
            try {
                updateDb(busiOpsInfo);
            } catch (Exception e) {
                throw new CustomException("ops配置信息失败");
            }

            try {
                //连接ssh执行脚本
                ipchange(busiOpsInfo, oldIp, sshRemoteServerOperate);
                log.info("OPS服务器信息修改完成..开始重启:");
            } catch (Exception e) {
                log.info("OPS服务器信息修改失败..:" + e.getMessage());
            } finally {
                log.info("OPS服务器信息修改关闭Session");
                sshRemoteServerOperate.closeSession();
            }
        }

    }

    private void ipchange(BusiOpsInfo busiOpsInfo, String localIP, SshRemoteServerOperateForOPSChangeIp sshRemoteServerOperate) {
        int subnet = IpUtil.subnetMaskToCIDR(busiOpsInfo.getSubnetMask());

        //修改FME地址
        fmechange(busiOpsInfo, subnet);

        try {

            String mountedDirPath = "/etc/netplan";
            String filePath2 = getnetFilePath(mountedDirPath);
            String newAddress = busiOpsInfo.getIpAddress() + "/" + subnet;// 新的 IP 地址
            String newVia = busiOpsInfo.getGatewayName(); // 新的 via 地址

            String new_fme_ip = busiOpsInfo.getFmeIp();

            if (!Objects.equals(oldIp, busiOpsInfo.getIpAddress())) {

                String old_fme_ip = oldFmeIp;
                log.info("old_fme_ip:" + old_fme_ip);
                String new_ip = busiOpsInfo.getIpAddress();
                log.info("new_ip:" + new_ip);

                log.info("new_fme_ip:" + new_fme_ip);
                String cmd_host = "chroot /host";
                String md = sshRemoteServerOperate.execCommand(cmd_host);
                log.info("执行脚本命令cmd_host结果：" + md);

                //执行脚本
                String scriptPath = "/home/all_confs/scripts/change_ip.sh";
                String publicIp = busiOpsInfo.getPublicIp();
                String scriptArgs = oldIp + " " + old_fme_ip + " " + new_ip + " " + new_fme_ip;
                if (Strings.isNotBlank(publicIp)) {
                    scriptArgs = oldIp + " " + old_fme_ip + " " + new_ip + " " + new_fme_ip + " " + publicIp;
                }
                String scriptCommand = scriptPath + " " + scriptArgs;
                log.info("执行脚本命令：" + scriptCommand);
                String s = sshRemoteServerOperate.execCommand(scriptCommand);
                log.info("执行脚本结果：" + s);

            }


            //  changNetplanConfig(filePath2, newAddress, newVia);
            //自己创建文件
            try {
                List<String> networkInterfaces = NetPlanConfigGen.getNetworkInterfacesByLshw();
                //读取macaddress的值
                String macAddress = NetPlanConfigGen.getMacAddress(filePath2, "br0");
                NetPlanConfigGen.generateNetplanConfig(networkInterfaces, newAddress, newVia, filePath2, macAddress);
            } catch (IOException e) {
                log.error("创建文件NetPlan失败" + e.getMessage());
            }
            TaskService taskService = BeanFactory.getBean(TaskService.class);
            OpsRestartTask opsRestartTask = new OpsRestartTask("修改ops配置信息重启", 20000, localIP);
            taskService.addTask(opsRestartTask);
        } catch (Exception e) {
            log.error("修改本地IP：" + e.getMessage());
        }
    }

    private void fmechange(BusiOpsInfo busiOpsInfo, int subnet) {
        if (Strings.isBlank(oldFmeIp)) {
            oldFmeIp = IpUtil.incrementLastOctet(oldIp);
        }

        SshRemoteServerOperateForFMEOPS sshRemoteServerOperate_fme = SshRemoteServerOperateForFMEOPS.getInstance();
        try {
            sshRemoteServerOperate_fme.sshRemoteCallLogin(oldFmeIp, "ttadmin", "tTcl0uds@cn", 22);
            boolean logined_fme = sshRemoteServerOperate_fme.isLogined();
            if (logined_fme) {
                sshRemoteServerOperate_fme.execCommand("recorder disable");
                Threads.sleep(500);
                sshRemoteServerOperate_fme.execCommand("recorder sip listen a 6000 6001");
                Threads.sleep(500);
                sshRemoteServerOperate_fme.execCommand("recorder sip certs dbs.key dbs.crt root.crt");
                Threads.sleep(500);
                sshRemoteServerOperate_fme.execCommand("recorder nfs " + busiOpsInfo.getIpAddress() + ":/mnt/nfs");
                Threads.sleep(500);
                sshRemoteServerOperate_fme.execCommand("recorder resolution 720p");
                Threads.sleep(500);
                sshRemoteServerOperate_fme.execCommand("recorder enable");
                Threads.sleep(500);
                sshRemoteServerOperate_fme.execCommand("streamer disable");
                Threads.sleep(500);
                sshRemoteServerOperate_fme.execCommand("streamer sip listen a 7000 7000");
                Threads.sleep(500);
                sshRemoteServerOperate_fme.execCommand("streamer sip certs dbs.key dbs.crt root.crt");
                Threads.sleep(500);
                sshRemoteServerOperate_fme.execCommand("streamer sip resolution 720p");
                Threads.sleep(500);
                sshRemoteServerOperate_fme.execCommand("streamer enable");
                Threads.sleep(500);
                String cmd_fme = "ipv4 a add " + busiOpsInfo.getFmeIp() + "/" + subnet + " " + busiOpsInfo.getGatewayName();
                sshRemoteServerOperate_fme.execCommand(cmd_fme);
                log.info("修改FME:" + busiOpsInfo.getFmeIp() + "网关：" + busiOpsInfo.getGatewayName());

                log.info("修改FME:====================================================================");
                log.info("修改FME:====================================================================");
                log.info("关闭FME:====================================================================");
                //sshRemoteServerOperate_fme.execCommand("shutdown Y");
            } else {
                log.error("修改FMEIP登录失败：");
            }
        } catch (Exception e) {
            log.error("修改FMEIP登录失败：");
        } finally {
            sshRemoteServerOperate_fme.closeSession();
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

    private synchronized void updateDb(BusiOpsInfo busiOpsInfo) {

        BusiFme busiFme = new BusiFme();
        BusiFmeDeptMapper busiFmeDeptMapper = BeanFactory.getBean(BusiFmeDeptMapper.class);
        BusiFmeMapper busiFmeMapper = BeanFactory.getBean(BusiFmeMapper.class);
        List<BusiFme> bs = busiFmeMapper.selectBusiFmeList(busiFme);

        try {
            if (CollectionUtils.isEmpty(bs)) {
                busiFme.setIp(busiOpsInfo.getFmeIp());
                busiFme.setUsername("ttadmin");
                busiFme.setPassword("tTcl0uds@cn");
                busiFme.setAdminUsername("ttadmin");
                busiFme.setAdminPassword("tTcl0uds@cn");
                busiFme.setPort(9443);
                busiFme.setCapacity(80);
                busiFme.setCreateTime(new Date());
                busiFme.setName("FME");
                busiFmeMapper.insertBusiFme(busiFme);
                log.info("新增加busiFme：{}", busiFme);

                BusiFmeDept busiFmeDept1 = busiFmeDeptMapper.selectBusiFmeDeptByDeptId(1L);
                if (busiFmeDept1 == null) {
                    BusiFmeDept busiFmeDept = new BusiFmeDept();
                    busiFmeDept.setFmeId(busiFme.getId());
                    busiFmeDept.setDeptId(1L);
                    busiFmeDept.setFmeType(1);
                    busiFmeDeptMapper.insertBusiFmeDept(busiFmeDept);
                    log.info("busiFmeDept：{}", busiFmeDept);
                } else {
                    busiFmeDept1.setFmeId(busiFme.getId());
                    busiFmeDeptMapper.updateBusiFmeDept(busiFmeDept1);
                    log.info("updateBusiFmeDept：{}", busiFmeDept1);
                }


            } else {
                for (BusiFme b : bs) {
                    busiFmeMapper.deleteBusiFmeById(b.getId());
                }
                List<BusiFmeDept> busiFmeDepts = busiFmeDeptMapper.selectBusiFmeDeptList(new BusiFmeDept());
                if (CollectionUtils.isNotEmpty(busiFmeDepts)) {
                    for (BusiFmeDept busiFmeDept : busiFmeDepts) {
                        busiFmeDeptMapper.deleteBusiFmeDeptById(busiFmeDept.getId());
                    }
                }
                busiFme.setIp(busiOpsInfo.getFmeIp());
                busiFme.setUsername("ttadmin");
                busiFme.setPassword("tTcl0uds@cn");
                busiFme.setAdminUsername("ttadmin");
                busiFme.setAdminPassword("tTcl0uds@cn");
                busiFme.setPort(9443);
                busiFme.setCapacity(80);
                busiFme.setCreateTime(new Date());
                busiFme.setName("FME");
                busiFmeMapper.insertBusiFme(busiFme);
                log.info("新增加busiFme：{}", busiFme);

                BusiFmeDept busiFmeDept = new BusiFmeDept();
                busiFmeDept.setFmeId(busiFme.getId());
                busiFmeDept.setDeptId(1L);
                busiFmeDept.setFmeType(1);
                busiFmeDeptMapper.insertBusiFmeDept(busiFmeDept);
                log.info("busiFmeDept：insertBusiFmeDept{}", busiFmeDept);

            }
        } catch (Exception e) {
            log.info("busiFme udeate db error", e.getMessage());
        }


        //修改FMQ
        BusiMqtt busiMqtt = new BusiMqtt();
        BusiMqttDeptMapper busiMqttDeptMapper = BeanFactory.getBean(BusiMqttDeptMapper.class);
        BusiMqttMapper busiMqttMapper = BeanFactory.getBean(BusiMqttMapper.class);
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
            busiMqtt.setServerUserName("root");
            busiMqtt.setServerPassword(SshConfigConstant.SERVER_DEFAULT_PASSWORD);
            busiMqtt.setNodeName("emqx@" + busiOpsInfo.getIpAddress());
            busiMqtt.setUseSsl(0);
            busiMqtt.setCreateTime(new Date());
            busiMqttMapper.insertBusiMqtt(busiMqtt);
            log.info("insertBusiMqtt：{}", busiMqtt);

            BusiMqttDept busiMqttDept1 = new BusiMqttDept();
            busiMqttDept1.setDeptId(1L);
            List<BusiMqttDept> busiMqttDepts = busiMqttDeptMapper.selectBusiMqttDeptList(busiMqttDept1);
            if (CollectionUtils.isEmpty(busiMqttDepts)) {
                busiMqttDept1.setMqttId(busiMqtt.getId());
                busiMqttDept1.setDeptId(1L);
                busiMqttDept1.setMqttType(1);
                busiMqttDeptMapper.insertBusiMqttDept(busiMqttDept1);
                log.info("insertBusiMqttDept：{}", busiMqttDept1);
            } else {
                busiMqttDept1.setMqttId(busiMqtt.getId());
                busiMqttDeptMapper.updateBusiMqttDept(busiMqttDept1);
                log.info("updateBusiMqttDept：{}", busiMqttDept1);
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
                mqtt.setServerUserName("root");
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
                log.info("updateBusiMqtt：{}", mqtt);

                BusiMqttDept busiMqttDept1 = new BusiMqttDept();
                busiMqttDept1.setDeptId(1L);
                List<BusiMqttDept> busiMqttDepts = busiMqttDeptMapper.selectBusiMqttDeptList(busiMqttDept1);
                if (CollectionUtils.isEmpty(busiMqttDepts)) {
                    busiMqttDept1.setMqttId(mqtt.getId());
                    busiMqttDept1.setDeptId(1L);
                    busiMqttDept1.setMqttType(1);
                    busiMqttDeptMapper.insertBusiMqttDept(busiMqttDept1);
                    log.info("insertBusiMqttDept：{}", busiMqttDept1);
                } else {
                    busiMqttDept1.setMqttId(mqtt.getId());
                    busiMqttDeptMapper.updateBusiMqttDept(busiMqttDept1);
                    log.info("updateBusiMqttDept：{}", busiMqttDept1);
                }

                break;
            }
            PropertiesUtil.updateProperty(filePath, "fmqIpList", fmqIpList);
        }

        //FCM
        BusiFreeSwitch busiFreeSwitch = new BusiFreeSwitch();
        BusiFreeSwitchDeptMapper busiFreeSwitchDeptMapper = BeanFactory.getBean(BusiFreeSwitchDeptMapper.class);
        BusiFreeSwitchMapper busiFreeSwitchMapper = BeanFactory.getBean(BusiFreeSwitchMapper.class);
        List<BusiFreeSwitch> busiFreeSwitches = busiFreeSwitchMapper.selectBusiFreeSwitchList(busiFreeSwitch);

        if (CollectionUtils.isEmpty(busiFreeSwitches)) {

            busiFreeSwitch.setIp(busiOpsInfo.getIpAddress());
            busiFreeSwitch.setUserName("root");
            busiFreeSwitch.setPassword(SshConfigConstant.SERVER_DEFAULT_PASSWORD);
            busiFreeSwitch.setName("FCM");
            busiFreeSwitch.setPort(SshConfigConstant.DEFAULT_SERVER_PORT);
            busiFreeSwitch.setOutBound(0);
            busiFreeSwitch.setCreateTime(new Date());
            busiFreeSwitchMapper.insertBusiFreeSwitch(busiFreeSwitch);
            log.info("insertBusiFreeSwitch：{}", busiFreeSwitch);
            BusiFreeSwitchDept busiFreeSwitchDept = new BusiFreeSwitchDept();
            busiFreeSwitchDept.setDeptId(1L);
            List<BusiFreeSwitchDept> busiFreeSwitchDepts = busiFreeSwitchDeptMapper.selectBusiFreeSwitchDeptList(busiFreeSwitchDept);
            if (CollectionUtils.isEmpty(busiFreeSwitchDepts)) {
                busiFreeSwitchDept.setServerId(busiFreeSwitch.getId());
                busiFreeSwitchDept.setFcmType(1);
                int insert = busiFreeSwitchDeptMapper.insertBusiFreeSwitchDept(busiFreeSwitchDept);

                log.info("insertBusiFreeSwitchDept：{}", busiFreeSwitchDept);

            } else {
                BusiFreeSwitchDept busiFreeSwitchDept1 = busiFreeSwitchDepts.get(0);
                busiFreeSwitchDept1.setServerId(busiFreeSwitch.getId());
                busiFreeSwitchDeptMapper.updateBusiFreeSwitchDept(busiFreeSwitchDept1);
                log.info("updateBusiFreeSwitchDept：{}", busiFreeSwitchDept1);
            }


        } else {
            for (BusiFreeSwitch freeSwitch : busiFreeSwitches) {
                freeSwitch.setIp(busiOpsInfo.getIpAddress());
                freeSwitch.setUserName("root");
                freeSwitch.setPassword(SshConfigConstant.SERVER_DEFAULT_PASSWORD);
                freeSwitch.setName("FCM");
                freeSwitch.setPort(SshConfigConstant.DEFAULT_SERVER_PORT);
                freeSwitch.setOutBound(0);
                freeSwitch.setUpdateTime(new Date());
                busiFreeSwitchMapper.updateBusiFreeSwitch(freeSwitch);
                log.info("updateBusiFreeSwitch：{}", freeSwitch);
                BusiFreeSwitchDept busiFreeSwitchDept = new BusiFreeSwitchDept();
                busiFreeSwitchDept.setDeptId(1L);
                List<BusiFreeSwitchDept> busiFreeSwitchDepts = busiFreeSwitchDeptMapper.selectBusiFreeSwitchDeptList(busiFreeSwitchDept);
                if (CollectionUtils.isEmpty(busiFreeSwitchDepts)) {
                    busiFreeSwitchDept.setServerId(freeSwitch.getId());
                    busiFreeSwitchDept.setFcmType(1);
                    int insert = busiFreeSwitchDeptMapper.insertBusiFreeSwitchDept(busiFreeSwitchDept);
                    log.info("insertBusiFreeSwitchDept：{}", busiFreeSwitchDept);
                } else {
                    BusiFreeSwitchDept busiFreeSwitchDept1 = busiFreeSwitchDepts.get(0);
                    busiFreeSwitchDept1.setServerId(freeSwitch.getId());
                    busiFreeSwitchDeptMapper.updateBusiFreeSwitchDept(busiFreeSwitchDept1);
                    log.info("updateBusiFreeSwitchDept：{}", busiFreeSwitchDept1);
                }
                break;
            }
        }

        //直播
        try {
            BusiLiveMapper busiLiveMapper = BeanFactory.getBean(BusiLiveMapper.class);
            BusiLiveDeptMapper busiLiveDeptMapper = BeanFactory.getBean(BusiLiveDeptMapper.class);
            BusiLive busiLive = new BusiLive();
            List<BusiLive> busiLives = busiLiveMapper.selectBusiLiveList(busiLive);
            if (CollectionUtils.isEmpty(busiLives)) {
                busiLive.setName("直播");
                busiLive.setIp(busiOpsInfo.getIpAddress());
                busiLive.setStatus(1);
                busiLive.setUriPath("live");
                busiLive.setProtocolType("rtmp");
                busiLive.setCreateTime(new Date());
                int i = busiLiveMapper.insertBusiLive(busiLive);
                log.info("insertBusiLive：{}", busiLive);
                if (i > 0) {
                    BusiLiveDept busiLiveDept = new BusiLiveDept();
                    busiLiveDept.setDeptId(100L);
                    List<BusiLiveDept> busiLiveDepts = busiLiveDeptMapper.selectBusiLiveDeptList(busiLiveDept);
                    if (CollectionUtils.isEmpty(busiLiveDepts)) {
                        busiLiveDept.setCreateTime(new Date());
                        busiLiveDept.setLiveType(1);
                        busiLiveDept.setLiveId(busiLive.getId());
                        busiLiveDeptMapper.insertBusiLiveDept(busiLiveDept);
                        log.info("insertBusiLiveDept：{}", busiLiveDept);
                    } else {
                        busiLiveDept.setLiveId(busiLive.getId());
                        busiLiveDept.setUpdateTime(new Date());
                        busiLiveDeptMapper.updateBusiLiveDept(busiLiveDept);
                        log.info("updateBusiLiveDept：{}", busiLiveDept);
                    }
                }
            } else {
                for (BusiLive live : busiLives) {
                    busiLiveMapper.deleteBusiLiveById(live.getId());
                }
                busiLive.setName("直播");
                busiLive.setIp(busiOpsInfo.getIpAddress());
                busiLive.setStatus(1);
                busiLive.setUriPath("live");
                busiLive.setProtocolType("rtmp");
                busiLive.setCreateTime(new Date());
                int i = busiLiveMapper.insertBusiLive(busiLive);
                log.info("insertBusiLive：{}", busiLive);
                if (i > 0) {
                    BusiLiveDept busiLiveDept = new BusiLiveDept();
                    List<BusiLiveDept> busiLiveDepts = busiLiveDeptMapper.selectBusiLiveDeptList(busiLiveDept);
                    if (CollectionUtils.isEmpty(busiLiveDepts)) {
                        busiLiveDept.setCreateTime(new Date());
                        busiLiveDept.setLiveType(1);
                        busiLiveDept.setLiveId(busiLive.getId());
                        busiLiveDept.setDeptId(100L);
                        busiLiveDeptMapper.insertBusiLiveDept(busiLiveDept);
                        log.info("insertBusiLiveDept：{}", busiLiveDept);
                    } else {
                        for (BusiLiveDept liveDept : busiLiveDepts) {
                            busiLiveDeptMapper.deleteBusiLiveDeptById(liveDept.getId());
                        }
                        busiLiveDept.setCreateTime(new Date());
                        busiLiveDept.setLiveType(1);
                        busiLiveDept.setLiveId(busiLive.getId());
                        busiLiveDept.setDeptId(100L);
                        busiLiveDeptMapper.insertBusiLiveDept(busiLiveDept);
                        log.info("insertBusiLiveDept：{}", busiLiveDept);
                    }
                }
            }
        } catch (Exception e) {
            log.info("直播设置失败" + e.getMessage());
        }


        try {
            BusiLiveSettingMapper liveSettingMapper = BeanFactory.getBean(BusiLiveSettingMapper.class);
            BusiLiveSetting busiLiveSetting = new BusiLiveSetting();

            List<BusiLiveSetting> busiLiveSettings = liveSettingMapper.selectBusiLiveSettingList(busiLiveSetting);
            if (CollectionUtils.isEmpty(busiLiveSettings)) {
                busiLiveSetting.setName("直播地址");
                busiLiveSetting.setUrl("rtmp://" + busiOpsInfo.getIpAddress() + "/live/123");
                busiLiveSetting.setStatus(1);
                busiLiveSetting.setDeptId(100L);
                int i = liveSettingMapper.insertBusiLiveSetting(busiLiveSetting);
                log.info("liveSettingMapper insertBusiLiveSetting：{},结果{}", busiLiveSetting.getId(), i);
            } else {
                for (BusiLiveSetting liveSetting : busiLiveSettings) {
                    liveSettingMapper.deleteBusiLiveSettingById(liveSetting.getId());
                }
                busiLiveSetting.setName("直播地址");
                busiLiveSetting.setUrl("rtmp://" + busiOpsInfo.getIpAddress() + "/live/123");
                busiLiveSetting.setStatus(1);
                busiLiveSetting.setDeptId(100L);
                int i = liveSettingMapper.insertBusiLiveSetting(busiLiveSetting);
                log.info("liveSettingMapper insertBusiLiveSetting：{},结果{}", busiLiveSetting.getId(), i);
            }
        } catch (Exception e) {
            log.info("直播liveSettingMapper设置失败" + e.getMessage());
        }


        //录制
        try {
            BusiRecordSettingMapper busiRecordSettingMapper = BeanFactory.getBean(BusiRecordSettingMapper.class);
            BusiRecordSetting busiRecordSetting = new BusiRecordSetting();
            List<BusiRecordSetting> busiRecordSettings = busiRecordSettingMapper.selectBusiRecordSettingList(busiRecordSetting);
            if (CollectionUtils.isEmpty(busiRecordSettings)) {
                inserRecordingsetting(busiOpsInfo, busiRecordSettingMapper, busiRecordSetting);
            } else {
                for (BusiRecordSetting recordSetting : busiRecordSettings) {
                    int i = busiRecordSettingMapper.deleteBusiRecordSettingById(recordSetting.getId());
                    log.info("deleteBusiRecordSettingById：{},结果{}", recordSetting.getId(), i);
                }
                busiRecordSetting.setDeptId(100L);
                inserRecordingsetting(busiOpsInfo, busiRecordSettingMapper, busiRecordSetting);
            }
        } catch (Exception e) {
            log.info("录制设置失败" + e.getMessage());
        }


        //号码段
        try {
            BusiConferenceNumberSectionMapper busiConferenceNumberSectionMapper = BeanFactory.getBean(BusiConferenceNumberSectionMapper.class);
            BusiConferenceNumberSection busiConferenceNumberSection = new BusiConferenceNumberSection();
            busiConferenceNumberSection.setMcuType("fme");
            busiConferenceNumberSection.setDeptId(100L);
            List<BusiConferenceNumberSection> busiConferenceNumberSections = busiConferenceNumberSectionMapper.selectBusiConferenceNumberSectionList(busiConferenceNumberSection);

            if (CollectionUtils.isEmpty(busiConferenceNumberSections)) {
                busiConferenceNumberSection.setCreateTime(new Date());
                busiConferenceNumberSection.setStartValue(80000L);
                busiConferenceNumberSection.setEndValue(80999L);
                int i = busiConferenceNumberSectionMapper.insertBusiConferenceNumberSection(busiConferenceNumberSection);
                log.info("insertBusiConferenceNumberSection：{},结果{}", busiConferenceNumberSection, i);
            } else {
                busiConferenceNumberSection.setUpdateTime(new Date());
                busiConferenceNumberSection.setStartValue(80000L);
                busiConferenceNumberSection.setEndValue(80999L);
                int i = busiConferenceNumberSectionMapper.updateBusiConferenceNumberSection(busiConferenceNumberSection);
                log.info("updateBusiConferenceNumberSection：{},结果{}", busiConferenceNumberSection, i);
            }
        } catch (Exception e) {
            log.info("号码段设置失败" + e.getMessage());
        }
        //文件入会

        try {
            BusiTransServerMapper busiTransServerMapper = BeanFactory.getBean(BusiTransServerMapper.class);
            BusiTransServer busiTransServer = new BusiTransServer();

            List<BusiTransServer> busiTransServers = busiTransServerMapper.selectBusiTransServerList(busiTransServer);

            if (CollectionUtils.isEmpty(busiTransServers)) {
                busiTransServer.setCreateTime(new Date());
                busiTransServer.setUserName("root");
                busiTransServer.setPassword(SshConfigConstant.SERVER_DEFAULT_PASSWORD);
                busiTransServer.setIp(busiOpsInfo.getIpAddress());
                busiTransServer.setPort(SshConfigConstant.DEFAULT_SERVER_PORT);
                int i = busiTransServerMapper.insertBusiTransServer(busiTransServer);
                log.info("insertBusiTransServer：{},结果{}", busiTransServer, i);
            } else {
                for (BusiTransServer transServer : busiTransServers) {
                    busiTransServerMapper.deleteBusiTransServerById(transServer.getId());
                }
                busiTransServer.setCreateTime(new Date());
                busiTransServer.setUserName(SshConfigConstant.SERVER_DEFAULT_USER_NAME_FOR_OPS);
                busiTransServer.setPassword(SshConfigConstant.SERVER_DEFAULT_PASSWORD);
                busiTransServer.setIp(busiOpsInfo.getIpAddress());
                busiTransServer.setPort(SshConfigConstant.DEFAULT_SERVER_PORT);
                int i = busiTransServerMapper.insertBusiTransServer(busiTransServer);
                log.info(" insertBusiTransServer：{},结果{}", busiTransServer, i);
            }
        } catch (Exception e) {
            log.info("文件入会设置错误" + e.getMessage());
        }

        //FCM 号码段

        try {
            BusiFcmNumberSectionMapper busiFcmNumberSectionMapper = BeanFactory.getBean(BusiFcmNumberSectionMapper.class);
            BusiFcmNumberSection busiFcmNumberSection = new BusiFcmNumberSection();
            List<BusiFcmNumberSection> busiFcmNumberSections = busiFcmNumberSectionMapper.selectBusiFcmNumberSectionList(busiFcmNumberSection);
            if (CollectionUtils.isEmpty(busiFcmNumberSections)) {
                busiFcmNumberSection.setCreateTime(new Date());
                busiFcmNumberSection.setStartValue(51000L);
                busiFcmNumberSection.setEndValue(52000L);
                busiFcmNumberSection.setDeptId(100L);
                int i = busiFcmNumberSectionMapper.insertBusiFcmNumberSection(busiFcmNumberSection);
                log.info(" insertBusiFcmNumberSection：{},结果{}", busiFcmNumberSection, i);
            }
        } catch (Exception e) {
            log.info("FCM号码段设置错误" + e.getMessage());
        }


    }


    private void inserRecordingsetting(BusiOpsInfo busiOpsInfo, BusiRecordSettingMapper busiRecordSettingMapper, BusiRecordSetting busiRecordSetting) {
        busiRecordSetting.setCreateTime(new Date());
        busiRecordSetting.setUrl("https://" + busiOpsInfo.getIpAddress() + ":8899/spaces");
        busiRecordSetting.setPath("/mnt/nfs");
        busiRecordSetting.setFolder("spaces");
        busiRecordSetting.setStatus(1);
        busiRecordSetting.setRetentionType(3);
        busiRecordSetting.setDeptId(100L);
        int i = busiRecordSettingMapper.insertBusiRecordSetting(busiRecordSetting);
        log.info("insertBusiRecordSetting：{},结果{}", busiRecordSetting, i);
    }

    public void updateNetplanAddress(String newIpAddress, String netplanConfigFile) {
        String command = String.format(
                "sudo sed -i '0,/addresses: \\[.*\\]/s//addresses: [%s]/' %s",
                newIpAddress.replace("/", "\\/"), netplanConfigFile
        );

    }


}
