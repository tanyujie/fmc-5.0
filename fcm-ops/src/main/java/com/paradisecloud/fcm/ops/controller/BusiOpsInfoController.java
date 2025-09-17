package com.paradisecloud.fcm.ops.controller;

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.constant.Constants;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.common.utils.uuid.IdUtils;
import com.paradisecloud.fcm.common.cache.CommonConfigCache;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.cache.LicenseCache;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.utils.RSAUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.dao.mapper.BusiConfigMapper;
import com.paradisecloud.fcm.dao.mapper.BusiOpsInfoMapper;
import com.paradisecloud.fcm.dao.mapper.BusiRecordsMapper;
import com.paradisecloud.fcm.dao.model.BusiConfig;
import com.paradisecloud.fcm.dao.model.BusiOpsInfo;
import com.paradisecloud.fcm.dao.model.BusiRecords;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceService;
import com.paradisecloud.fcm.service.ops.OpsDataCache;
import com.paradisecloud.fcm.ops.service.IBusiOpsInfoService;
import com.paradisecloud.fcm.ops.task.OpsRestartAndChangeNetPlanTask;
import com.paradisecloud.fcm.ops.task.OpsRestartTask;
import com.paradisecloud.fcm.ops.utils.IpUtil;
import com.paradisecloud.fcm.ops.utils.NetPlanConfigGen;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.task.EndDownCascadeConferenceTask;
import com.paradisecloud.fcm.web.service.impls.BusiUserLoginService;
import com.paradisecloud.framework.web.domain.Server;
import com.sinhy.spring.BeanFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.websocket.server.PathParam;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * ops配置信息Controller
 *
 * @author lilinhai
 * @date 2024-05-27
 */
@RestController
@RequestMapping("/ops")
@Tag(name = "ops配置信息")
@Slf4j
public class BusiOpsInfoController extends BaseController {
    @Resource
    private IBusiOpsInfoService busiOpsInfoService;

    @Value("${application.name:}")
    private String an;
    @Resource
    private BusiUserLoginService busiUserLoginService;

    @Resource
    private RedisCache redisCache;

    @Resource
    private TaskService taskService;

    @Resource
    private IBusiConferenceService busiConferenceService;


    @Resource
    private BusiRecordsMapper busiRecordsMapper;


    @Resource
    private BusiConfigMapper busiConfigMapper;
    @Resource
    private BusiOpsInfoMapper busiOpsInfoMapper;

    public static boolean areListsEqualIgnoreOrder(List<String> list1, List<String> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }

        // 对列表进行排序
        Collections.sort(list1);
        Collections.sort(list2);


        // 比较排序后的列表
        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).equals(list2.get(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 查询ops配置信息列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询ops配置信息列表")
    public RestResponse list(BusiOpsInfo busiOpsInfo) {
        startPage();
        List<BusiOpsInfo> list = busiOpsInfoService.selectBusiOpsInfoList(busiOpsInfo);
        return getDataTable(list);
    }

    /**
     * 获取ops配置信息详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取ops配置信息详细信息")
    public RestResponse getInfo(@PathVariable("id") Integer id) {
        return RestResponse.success(busiOpsInfoService.selectBusiOpsInfoById(id));
    }

    /**
     * 新增ops配置信息
     */

    @Log(title = "ops配置信息", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增ops配置信息")
    public RestResponse add(@RequestBody BusiOpsInfo busiOpsInfo) {
        if (IpUtil.validateIP(busiOpsInfo.getIpAddress()) && IpUtil.validateIP(busiOpsInfo.getFmeIp()) && IpUtil.validateIP(busiOpsInfo.getGatewayName())) {

            String localIp = IpUtil.getLocalIp();
            RestResponse restResponse = toAjax(busiOpsInfoService.insertBusiOpsInfo(busiOpsInfo));
            OpsRestartTask opsRestartTask = new OpsRestartTask("初始化ops重启", 5000, localIp);
            taskService.addTask(opsRestartTask);
            return restResponse;
        } else {
            return RestResponse.fail("ops配置信息ip地址不合法");
        }
    }

    /**
     * 修改ops配置信息
     */
    @Log(title = "ops配置信息", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改ops配置信息",description = "修改opsIP地址")
    public synchronized RestResponse edit(@RequestBody BusiOpsInfo busiOpsInfo) {
        if (IpUtil.validateIP(busiOpsInfo.getIpAddress()) && IpUtil.validateIP(busiOpsInfo.getFmeIp()) && IpUtil.validateIP(busiOpsInfo.getGatewayName())) {

            RestResponse restResponse = toAjax(busiOpsInfoService.updateBusiOpsInfo(busiOpsInfo));

            return restResponse;
        } else {
            return RestResponse.fail("ops配置信息ip地址不合法");
        }
    }

    /**
     * 查询温度
     */
    @GetMapping("/thermal_zone")
    @Operation(summary = "查询温度")
    public RestResponse thermal_zone1() {
        return RestResponse.success(busiOpsInfoService.thermal_zone());
    }

    /**
     * 重启
     */
    @GetMapping("/restart")
    @Operation(summary = "重启",description = "重启")
    public RestResponse restart() {
        return toAjax(busiOpsInfoService.restart());
    }

    /**
     * 关机
     */
    @GetMapping("/close")
    @Operation(summary = "关机",description = "关机")
    public RestResponse close() {
        return toAjax(busiOpsInfoService.shutdown());
    }

    /**
     * 恢复出厂设置
     */
    @GetMapping("/restore")
    @Operation(summary = "恢复出厂设置",description = "恢复出厂设置")
    public RestResponse restore() {

        try {
            busiOpsInfoService.restore();
        } catch (Exception e) {
            log.info(e.getMessage());
          return RestResponse.fail();
        }
        executeCommand("rm -rf /mnt/nfs/spaces/*");
        OpsRestartAndChangeNetPlanTask opsChangeConfigTask = new OpsRestartAndChangeNetPlanTask("OpsRestartAndChangeNetPlanTask_r_t_e", 1000);
        taskService.addTask(opsChangeConfigTask);

        return RestResponse.success();
    }


    @Operation(summary = "ping终端IP返回消息(OPS)")
    @GetMapping("/pingIp")
    public RestResponse pingIp(@PathParam("ip") String ip){
        return success(busiOpsInfoService.pingIp(ip));
    }


    /**
     * 初始化检测
     */
    @GetMapping("/initCheck")
    @Operation(summary = "初始化检测")
    public RestResponse initCheck() {
        HashMap<String, Boolean> mapInit = new HashMap<>();
        mapInit.put("needInit", false);
        BusiOpsInfo busiOpsInfo = new BusiOpsInfo();

        List<BusiOpsInfo> list = busiOpsInfoService.selectBusiOpsInfoList(busiOpsInfo);
        if (CollectionUtils.isNotEmpty(list)) {
            BusiOpsInfo busiOpsInfo1 = list.get(0);
            String password = busiOpsInfo1.getPassword();
            if (Strings.isBlank(password)) {
                //查询
                List<String> networkInterfaces = null;
                try {
                    networkInterfaces = NetPlanConfigGen.getNetworkInterfacesByLshwNoEnp2s0();
                } catch (IOException e) {
                }
                if (CollectionUtils.isNotEmpty(networkInterfaces)) {

                    if (networkInterfaces.size() == 1 && Objects.equals(networkInterfaces.get(0), "wlp1s0")) {
                        mapInit.put("needInit", false);
                    } else {
                        if(Objects.equals("172.16.99.200",busiOpsInfo1.getIpAddress())){
                            mapInit.put("needInit", false);
                        }else {
                            mapInit.put("needInit", true);
                        }

                    }

                }
            } else {
                //查询
                List<String> networkInterfaces = null;
                try {
                    networkInterfaces = NetPlanConfigGen.getNetworkInterfacesByLshwNoEnp2s0();
                } catch (IOException e) {
                }
                if (CollectionUtils.isNotEmpty(networkInterfaces)) {
                    List<String> old = Arrays.asList(password.split(","));
                    boolean areEqual = areListsEqualIgnoreOrder(networkInterfaces, old);
                    if (!areEqual) {
                        mapInit.put("needInit", true);
                    }
                }
            }
        } else {
            throw new CustomException("初始化检查到数据错误");
        }
        return RestResponse.success(mapInit);
    }

    @GetMapping("/firstInit")
    @Operation(summary = "首次初始化")
    public RestResponse firstInit() {
        List<BusiOpsInfo> list = busiOpsInfoService.selectBusiOpsInfoList(new BusiOpsInfo());
        BusiOpsInfo busiOpsInfo1 = list.get(0);

        //查询
        List<String> networkInterfaces = null;
        try {
            networkInterfaces = NetPlanConfigGen.getNetworkInterfacesByLshwNoEnp2s0();
        } catch (IOException e) {
        }
        if (CollectionUtils.isEmpty(networkInterfaces)) {
            throw new CustomException("首次初始化错误,不能正确识别网口");
        }
        String joinedString = String.join(",", networkInterfaces);
        busiOpsInfo1.setPassword(joinedString);
        RestResponse restResponse = toAjax(busiOpsInfoService.updateBusiOpsInfo(busiOpsInfo1));
        return restResponse;
    }

    @PostMapping({"/loginAuto"})
    public RestResponse loginAuto() {
        String region = ExternalConfigCache.getInstance().getRegion();
        Map<String, Object> data = new HashMap();
        RSAUtil.SignCertInfo signCertInfo = CommonConfigCache.getInstance().getSignCertInfo();
        if (signCertInfo != null) {
            if (signCertInfo.getPublicKey() != null) {
                data.put("publicKey", RSAUtil.encryptBASE64(signCertInfo.getPublicKey().getEncoded()));
            }
        }
        data.put("region", region);
        String title = ExternalConfigCache.getInstance().getTitle();
        if (StringUtils.isEmpty(title)) {
            title = an;
        }
        data.put("title", title);
        if (!Objects.equals(region, "ops")) {
            return RestResponse.fail(0, "", data);
        }
        String userName = ExternalConfigCache.getInstance().getAutoLoginUser();
        String password = ExternalConfigCache.getInstance().getAutoLoginPassword();
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            return RestResponse.fail(0, "", data);
        }
        try {
            String uuid = IdUtils.simpleUUID();
            String verifyKey = "captcha_codes:" + uuid;
            String code = "999";
            this.redisCache.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
            String token = this.busiUserLoginService.login(userName, password, code, uuid);
            data.put("token", token);
            String tokenCloud = OpsDataCache.getInstance().getCloudToken();
            String  cloudUrl = ExternalConfigCache.getInstance().getCloudUrl();
            data.put("cloudToken", tokenCloud);
            data.put("cloudUrl", cloudUrl);
            return RestResponse.success(data);
        } catch (Exception e) {
            return RestResponse.fail(0, "");
        }
    }

    @GetMapping("/server")
    public RestResponse getInfo() throws Exception {
        Server server = new Server();
        server.copyTo();
        return RestResponse.success(server);
    }

    @GetMapping("/network")
    public RestResponse getNetworkInfo() {
        Object a = busiOpsInfoService.getNetworkInfo();
        return RestResponse.success(a);
    }

    @GetMapping("/networkCheck/{ipAddress}")
    public RestResponse getNetworkInfoAddress(@PathVariable String ipAddress) {
        Object a = busiOpsInfoService.getNetworkInfoAddress(ipAddress);
        return RestResponse.success(a);
    }

    @GetMapping("/localRecorder")
    public RestResponse localRecorder() {
        Boolean localRecoder = LicenseCache.getInstance().getLocalRecoder();
        if (localRecoder == null) {
            throw new CustomException("系统未开启本地录屏功能,请联系供应商授权后使用");
        }
        if (!localRecoder) {
            throw new CustomException("系统未开启本地录屏功能,请联系供应商授权后使用");
        }

        boolean useRecorderLimit = LicenseCache.getInstance().getUseRecorderLimit();

        BusiRecords busiRecords_query = new BusiRecords();
        busiRecords_query.setRecordsFileStatus(2);
        busiRecords_query.setDeptId(100L);
        List<BusiRecords> busiRecords = busiRecordsMapper.selectBusiRecordsList(busiRecords_query);
        int fileCount = 0;
        if (CollectionUtils.isNotEmpty(busiRecords)) {
            fileCount = busiRecords.size();
        }
        Integer useableSpace =  LicenseCache.getInstance().getUseableSpace();
        if(useableSpace==null){
            throw new CustomException("录制空间未授权,请联系供应商授权后使用");
        }
        BusiConfig busiConfig1 = new BusiConfig();
        busiConfig1.setConfigKey("Recording_Files_Storage_Space_Max");
        List<BusiConfig> busiConfigs = busiConfigMapper.selectBusiConfigList(busiConfig1);
        if (busiConfigs != null && busiConfigs.size() > 0) {
            BusiConfig busiConfig = busiConfigs.get(0);
            Double recordingFilesStorageSpaceMax = Double.valueOf(busiConfig.getConfigValue());
            if(recordingFilesStorageSpaceMax.intValue()!=useableSpace){
                busiConfig.setConfigValue(String.valueOf(useableSpace));
                busiConfig.setUpdateTime(new Date());
                busiConfigMapper.updateBusiConfig(busiConfig);
            }

        }else {
            busiConfig1.setCreateTime(new Date());
            busiConfig1.setConfigValue(String.valueOf(useableSpace));
            busiConfigMapper.insertBusiConfig(busiConfig1);
        }

        if(useRecorderLimit){
            Integer recorderLimit = LicenseCache.getInstance().getRecorderLimit();
            if(recorderLimit==null){
                throw new CustomException("录制文件数量超过限制,请联系供应商授权后使用");
            }
            if(fileCount>=recorderLimit.intValue()){
                throw new CustomException("录制文件数量超过限制,请联系供应商授权后使用");
            }
        }

        Object a = busiOpsInfoService.localRecorder();
        return RestResponse.success(a);
    }

    @GetMapping("/phone")
    public RestResponse getPhone() {
        String phone = busiOpsInfoService.getPhone();
        return RestResponse.success(phone);
    }

    @PutMapping("/phone/{phoneNumber}")
    public RestResponse setPhone(@PathVariable String phoneNumber) {
        Object a = busiOpsInfoService.setPhone(phoneNumber);
        return RestResponse.success(a);
    }

    @GetMapping("/localRecorder/end/{conferenceId}")
    public RestResponse endlocalRecorder(@PathVariable String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                busiConferenceService.endConference(conferenceId, 1, EndReasonsType.ADMINISTRATOR_HANGS_UP);
                break;
            }
        }
        if (baseConferenceContext != null) {
            EndDownCascadeConferenceTask endDownCascadeConferenceTask = new EndDownCascadeConferenceTask(conferenceId, 0, baseConferenceContext, 1);
            BeanFactory.getBean(TaskService.class).addTask(endDownCascadeConferenceTask);
        }
        long currentTimeMillis = System.currentTimeMillis();
        new Thread(() -> {
            Threads.sleep(3000);
            while (true) {
                if (System.currentTimeMillis() - currentTimeMillis > 20 * 1000) {
                    break;
                }
                if (baseConferenceContext.isEnd() || baseConferenceContext == null) {
                    break;
                }
            }
            busiOpsInfoService.endlocalRecorder(conferenceId);
        }).start();


        return success();
    }




    /**
     * 获取MCU状态
     */
    @GetMapping(value = "/mcuStatus")
    @Operation(summary = "获取部门可使用MCU列表")
    public RestResponse mcuStatus() {
        HashMap<String, Boolean> map = new HashMap<>();
        map.put("mcuStatus", false);
        List<BusiOpsInfo> list = busiOpsInfoService.selectBusiOpsInfoList(new BusiOpsInfo());
        if (CollectionUtils.isEmpty(list)) {
            return RestResponse.success(map);
        }
        BusiOpsInfo busiOpsInfo = list.get(0);
        String fmeIp = busiOpsInfo.getFmeIp();
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByBridgeAddressOnly(fmeIp);
        map.put("mcuStatus", fmeBridge.isAvailable());
        return RestResponse.success(map);
    }

    private static void executeDockerCommand(String containerId, String command) {
        // 使用bash执行docker exec命令
        String fullCommand = String.format("docker exec -it %s /bin/bash -c '%s'", containerId, command);

        ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", fullCommand);
        processBuilder.redirectErrorStream(true); // 合并标准输出和标准错误流

        try {
            // 启动进程
            Process process = processBuilder.start();

            // 读取命令输出
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }

            // 等待命令执行完成
            int exitCode = process.waitFor();
            log.info("Docker命令执行完成，退出码：" + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @PostMapping({"/sn/{sn}"})
    public RestResponse uploadSn(@PathVariable String sn) {
        List<BusiOpsInfo> busiOpsInfos = busiOpsInfoService.selectBusiOpsInfoList(new BusiOpsInfo());
        if(CollectionUtils.isEmpty(busiOpsInfos)){
            return RestResponse.fail();
        }
        BusiOpsInfo busiOpsInfo = busiOpsInfos.get(0);
        busiOpsInfo.setSn(sn);
        busiOpsInfoMapper.updateBusiOpsInfo(busiOpsInfo);
        return RestResponse.success();
    }


    public static void executeCommand(String command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh", "-c", command);
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

}
