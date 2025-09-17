package com.paradisecloud.fcm.web.controller.mcu.all;

import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.TransCodecStatusCodeEnum;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.utils.FileUtil;
import com.paradisecloud.fcm.common.utils.PathUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.dao.model.BusiMeetingFile;
import com.paradisecloud.fcm.dao.model.BusiTransServer;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.interfaces.IBusiMeetingFileService;
import com.paradisecloud.fcm.service.interfaces.IBusiTransServerService;
import com.paradisecloud.fcm.service.task.JoinMeetingTask;
import com.paradisecloud.fcm.service.task.MeetAddFileTask;
import com.paradisecloud.fcm.web.model.JoinMeetingFile;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.spring.BeanFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.constraints.Size;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 终端升级Controller
 *
 * @author nj
 * @date 2024-03-29
 */
@Validated
@RestController
@RequestMapping("/busi/meetingFile")
@Tag(name = "入会文件管理")
public class BusiMeetingFileController extends BaseController {

    /**
     * 入会文件保存位置
     */
    public static final String meetingFilePath = "/home/upload/transcodec/";
    @Resource
    private IBusiMeetingFileService busiMeetingFileService;
    @Resource
    private IBusiTransServerService busiTransServerService;

    private static String getUploadPath() {
        String os = System.getProperty("os.name");
        if (os.contains("indows")) {
            return PathUtil.getRootPath() + "/transcodec/";
        } else {
            return meetingFilePath;
        }
    }

    public static String saveFile(final MultipartFile file, final String folderPath, final String fileName) throws Exception {
        String filePath = "";
        if (file.isEmpty()) {
            throw new CustomException("上传文件为空！");
        }

        try {
            // 获取保存路径
            File files = new File(folderPath, fileName);
            File parentFile = files.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            file.transferTo(files);
            File fileNew = new File(folderPath, fileName);
            fileNew.setReadable(true, false);
            fileNew.setExecutable(true, false);
            fileNew.setWritable(true, false);
            filePath = fileNew.getAbsolutePath();
        } catch (IOException e) {
            throw e;
        }
        return filePath;
    }

    /**
     * 查询终端升级列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询入会文件列表")
    public RestResponse list(BusiMeetingFile busiMeetingFile) {
        if (busiMeetingFile == null) {
            busiMeetingFile = new BusiMeetingFile();
        }
        busiMeetingFile.setFileStatus(1);
        startPage();
        List<BusiMeetingFile> list = busiMeetingFileService.selectBusiMeetingFileList(busiMeetingFile);
        PaginationData<Object> pd = new PaginationData();
        pd.setTotal((new PageInfo(list)).getTotal());
        for (BusiMeetingFile meetingFile : list) {
            ModelBean modelBean = new ModelBean(meetingFile);
            pd.addRecord(modelBean);
        }
        return RestResponse.success(0L, "查询成功", pd);
    }

    /**
     * 获取文件详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取文件详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id) {
        return RestResponse.success(busiMeetingFileService.selectBusiMeetingFileById(id));
    }

    /**
     * 上传文件
     *
     * @param uploadFile
     * @param participantName
     * @param remark
     * @return
     */
    @Log(title = "上传文件", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "上传文件", description = "上传文件")
    public RestResponse add(@RequestParam(value = "uploadFile", required = true) MultipartFile uploadFile,
                            @RequestParam(value = "participantName", required = true) @Size(max = 50) String participantName,
                            @RequestParam(value = "remark", required = false) @Size(max = 100) String remark,
                            @RequestParam(value = "deptId", required = false) Long deptId) {

        List<BusiTransServer> busiTransServers = busiTransServerService.selectBusiTransServerList(new BusiTransServer());
        if (CollectionUtils.isEmpty(busiTransServers)) {
            throw new CustomException("请先配置转码服务器");
        }
        String filePath = null;
        if (uploadFile != null && !uploadFile.isEmpty()) {
            // 保存
            try {
                long size = uploadFile.getSize();
                String contentType = uploadFile.getContentType();
                String originalFilename = uploadFile.getOriginalFilename();
                String fileSuffix = originalFilename.substring(originalFilename.indexOf("."));
                // 给文件重命名
                String fileName = DateUtil.convertDateToString(new Date(), "yyyyMMddHHmmss") + fileSuffix;
                String savePath = getUploadPath();
                filePath = saveFile(uploadFile, savePath, fileName);
                if (StringUtils.isNotEmpty(filePath)) {
                    BusiMeetingFile busiMeetingFile = new BusiMeetingFile();
                    busiMeetingFile.setFileName(fileName);
                    busiMeetingFile.setParticipantName(participantName);
                    String fileSizeWithUnit = FileUtil.getFileSizeWithUnit(size);
                    busiMeetingFile.setFileSize(fileSizeWithUnit);
                    busiMeetingFile.setFileType(contentType);
                    if (deptId != null) {
                        busiMeetingFile.setDeptId(deptId.intValue());
                    }
                    busiMeetingFile.setUrl(filePath);
                    busiMeetingFile.setCreateTime(new Date());
                    busiMeetingFile.setRemark(remark);

                    try {
                        SysUser user = SecurityUtils.getLoginUser().getUser();
                        if (user != null) {
                            busiMeetingFile.setCreateUserId(user.getUserId());
                        }
                    } catch (Exception e) {
                    }
                    int row = busiMeetingFileService.insertBusiMeetingFile(busiMeetingFile);
                    if (row > 0) {
                        MeetAddFileTask meetAddFileTask = new MeetAddFileTask(busiMeetingFile.getId() + "", 10, busiMeetingFile, busiTransServers.get(0));
                        BeanFactory.getBean(TaskService.class).addTask(meetAddFileTask);
                    } else {
                        throw new CustomException("上传失败");
                    }
                    return toAjax(row);
                }
            } catch (Exception e) {
                try {
                    File file = new File(filePath);
                    deleteFile(file);
                } catch (Exception exception) {
                }
                if (e instanceof CustomException) {
                    return RestResponse.fail(e.getMessage());
                } else {
                    if (StringUtils.isNotEmpty(e.getMessage())) {
                        return RestResponse.fail(e.getMessage());
                    } else {
                        return RestResponse.fail("入会文件上传错误");
                    }
                }
            }
        }
        return RestResponse.fail("入会文件上传错误");
    }

    /**
     * 删除文件
     */
    @Log(title = "删除文件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    @Operation(summary = "删除文件", description = "删除文件")
    public RestResponse remove(@PathVariable Long id) {
        return toAjax(busiMeetingFileService.deleteBusiMeetingFileById(id));
    }

    @Log(title = "修改入会文件", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改入会文件", description = "修改入会文件")
    public RestResponse edit(@RequestBody BusiMeetingFile meetingFile) {

        BusiMeetingFile busiMeetingFile = busiMeetingFileService.selectBusiMeetingFileById(meetingFile.getId());
        if (busiMeetingFile != null) {
            busiMeetingFile.setParticipantName(meetingFile.getParticipantName());
            busiMeetingFile.setRemark(meetingFile.getRemark());
        } else {
            throw new CustomException("文件不存在");
        }

        try {
            if (busiMeetingFileService.updateBusiMeetingFile(busiMeetingFile) > 0) {
                return RestResponse.success();
            }
        } catch (Exception e) {
            return RestResponse.fail("修改入会文件错误");
        }

        return RestResponse.success();
    }

    @Log(title = "入会")
    @PostMapping("/displayInConference")
    @Operation(summary = "入会", description = "入会")
    public RestResponse displayInConference(@RequestBody JoinMeetingFile joinMeetingFile) {
        Long[] ids = joinMeetingFile.getIds();
        if (ids == null || ids.length == 0) {
            return RestResponse.fail("请选择入会文件");
        }
        for (Long id : ids) {
            BusiMeetingFile busiMeetingFile = busiMeetingFileService.selectBusiMeetingFileById(id);
            if (busiMeetingFile != null) {
                Integer codecStatus = busiMeetingFile.getCodecStatus();
                if (!Objects.equals(codecStatus, TransCodecStatusCodeEnum.TRANS_SUCCESS.getCode())) {
                    return RestResponse.fail("文件还未转码，无法入会");
                }
            }
        }
        String conferenceId = joinMeetingFile.getConferenceId();

        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (baseConferenceContext == null) {
            return RestResponse.fail("会议不存在");
        }
        if (!baseConferenceContext.isStart()) {
            return RestResponse.fail("会议未开始");
        }
        String mcu_ip = "";
        String conferenceNumber = "";
        List<BusiTransServer> busiTransServers = busiTransServerService.selectBusiTransServerList(new BusiTransServer());
        if (CollectionUtils.isEmpty(busiTransServers)) {
            throw new CustomException("请先配置虚拟终端服务器");
        }
        String serverip = busiTransServers.get(0).getIp();


        switch (mcuType) {
            case FME -> {
                ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
                FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceContext(conferenceContext);
                if (fmeBridge == null) {
                    return RestResponse.fail("会议不存在");
                }
                mcu_ip = fmeBridge.getBusiFme().getIp();
                conferenceNumber = conferenceContext.getConferenceNumber();
                JoinMeetingTask joinMeetingTask = new JoinMeetingTask("会议ID" + baseConferenceContext.getId(), 10, ids, mcu_ip, conferenceNumber, serverip, baseConferenceContext);
                BeanFactory.getBean(TaskService.class).addTask(joinMeetingTask);
                break;
            }
        }

        return RestResponse.success("入会成功");
    }

    @GetMapping("/callback")
    @Operation(summary = "回调", description = "回调")
    public RestResponse callback(@RequestParam Long id) {
        logger.info("=========MeetingFile callback====================================:ID" + id);
        BusiMeetingFile busiMeetingFile = busiMeetingFileService.selectBusiMeetingFileById(id);
        if (busiMeetingFile != null) {
            busiMeetingFile.setCodecStatus(1);
            busiMeetingFileService.updateBusiMeetingFile(busiMeetingFile);
        }
        return RestResponse.success();
    }

    private void deleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        File[] files = file.listFiles();
        for (File f : files) {
            deleteFile(f);
        }
        file.delete();
    }


}
