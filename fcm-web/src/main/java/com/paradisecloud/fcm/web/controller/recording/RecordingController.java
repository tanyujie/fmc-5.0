package com.paradisecloud.fcm.web.controller.recording;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.utils.PathUtil;
import com.paradisecloud.fcm.dao.mapper.BusiRecordsMapper;
import com.paradisecloud.fcm.dao.model.BusiMtr;
import com.paradisecloud.fcm.dao.model.BusiRecords;
import com.paradisecloud.fcm.dao.model.vo.RecordsSearchVo;
import com.sinhy.exception.SystemException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.dao.model.BusiConferenceNumber;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiRecordsService;
import com.paradisecloud.fcm.fme.service.interfaces.IRecordingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author johnson liu
 * @Date 2021/4/27 10:33
 */
@RestController
@RequestMapping("/busi/recording")
@Tag(name = "会议录制")
public class RecordingController extends BaseController {
    @Autowired
    private IRecordingService iRecordingService;
    @Autowired
    private IBusiRecordsService iBusiRecordsService;
    @Resource
    private BusiRecordsMapper busiRecordsMapper;

    /**
     * 获取录制文件列表
     *
     * @param conferenceNumber 会议号
     * @return
     */
    @GetMapping("/getFolder")
    @Operation(summary = "获取录制文件列表")
    public RestResponse getFolder(HttpServletRequest request, @RequestParam("conferenceNumber") String conferenceNumber, @RequestParam("deptId") Long deptId, String coSpaceId) {
        List<Map<String, Object>> folder = iRecordingService.getFolder(conferenceNumber, deptId, coSpaceId);
        String host = request.getHeader("Host");
        if (StringUtils.isNotEmpty(host)) {
            if (host.indexOf(":8898") > 0) {
                for (Map<String, Object> map : folder) {
                    try {
                        String url = (String) map.get("url");
                        url = url.replace(":8899", ":8898").replace("https://", "http://");
                        map.put("url", url);
                    } catch (Exception e) {
                    }
                }
            } else if (host.indexOf(":443") > 0) {
                for (Map<String, Object> map : folder) {
                    try {
                        String url = (String) map.get("url");
                        url = url.replace(":8899", "");
                        map.put("url", url);
                    } catch (Exception e) {
                    }
                }
            }
        }
        return RestResponse.success(folder);
    }


    /**
     * 开启或关闭会议录制功能
     *
     * @param conferenceNumber 会议号
     * @return
     */
    @PostMapping("/changeRecordingStatus/{conferenceNumber}")
    @Operation(summary = "开启或关闭会议录制功能", description = "会议录制")
    public RestResponse changeRecordingStatus(@PathVariable String conferenceNumber, @RequestBody JSONObject jsonObject) {
        try {
            boolean flag = jsonObject.getBoolean("recording");
            iBusiRecordsService.updateBusiRecords(flag, conferenceNumber);
            //iBusiConferenceService.updateCallRecordStatus(conferenceNumber, flag);
        } catch (Exception e) {
            return RestResponse.fail(e.getMessage());
        }
        return RestResponse.success();
    }

    /**
     * 根据部门ID和会议名称
     * 获取存在录制文件的会议列表
     * List<File> collect = files.stream().sorted(Comparator.comparing(File::lastModified).reversed()).limit(1).collect(Collectors.toList());
     *
     * @param recordsSearchVo 部门ID
     * @return
     */
    @GetMapping("/getRecordingConferences")
    @Operation(summary = "获取存在录制文件的会议列表")
    public RestResponse getRecordingConferences(RecordsSearchVo recordsSearchVo) {
        return RestResponse.success(iRecordingService.getBusiConferenceNumberVoList(recordsSearchVo));
    }

    /**
     * 删除录制文件
     *
     * @return
     */
    @PostMapping("/deleteRecordingFile/{coSpaceId}")
    @Operation(summary = "删除录制文件", description = "删除录制文件")
    public RestResponse deleteRecordingFile(String id, String fileName, @PathVariable String coSpaceId, @RequestParam(required = false) Boolean force) {
        logger.info("deleteRecordingFile入参文件名:{},会议号：{}", fileName, coSpaceId);
        if (force == null) {
            force = false;
        }
        iRecordingService.deleteRecordingFile(id, fileName, coSpaceId, force);
        return RestResponse.success();
    }

    /**
     * 获取存在回收站的录制文件列表
     * @return
     */
    @GetMapping("/getReclaimRecordingConferences")
    @Operation(summary = "获取存在回收站的录制文件列表")
    public RestResponse getReclaimRecordingConferences(RecordsSearchVo recordsSearchVo) {
        return RestResponse.success(iRecordingService.getReclaimRecordingConferences(recordsSearchVo));
    }

    /**
     * 恢复回收站的录制文件列表
     * @param id
     * @return
     */
    @PostMapping("/recoverRecordingConferences")
    @Operation(summary = "恢复回收站的录制文件列表", description = "恢复回收站的录制文件")
    public RestResponse recoverRecordingConferences(String id) {
        iRecordingService.recoverRecordingConferences(id);
        return RestResponse.success();
    }

    /**
     * 删除回收站录制文件
     * @param id
     * @return
     */
    @PostMapping("/deleteRecoverRecordingFile")
    @Operation(summary = "删除回收站录制文件", description = "删除回收站录制文件")
    public RestResponse deleteRecoverRecordingFile(String id) {
        logger.info("deleteRecoverRecordingFile入参文件名:{},会议号：{}", id);
        iRecordingService.deleteRecoverRecordingFile(id);
        return RestResponse.success();
    }

    /**
     * 下载录制文件
     */
    @RequestMapping("/download/{coSpaceId}/{fileName}")
    @Operation(summary = "下载录制文件", description = "下载录制文件")
    public void downLoad(HttpServletRequest request, HttpServletResponse response, @PathVariable("coSpaceId") String coSpaceId, @PathVariable("fileName") String fileName) {
        String realName = fileName + ".mp4";
        BusiRecords busiRecordsCon = new BusiRecords();
        busiRecordsCon.setCoSpaceId(coSpaceId);
        busiRecordsCon.setRealName(realName);
        List<BusiRecords> busiRecordList = busiRecordsMapper.selectBusiRecordsList(busiRecordsCon);
        if (busiRecordList == null || busiRecordList.size() == 0) {
            throw new SystemException("文件不存在！");
        }
        String fileUrl = getFilePath(coSpaceId, realName);
        File file = new File(fileUrl);
        if (!file.exists()) {
            throw new SystemException("文件不存在！");
        }

        ServletContext context = request.getServletContext();
        String mimeType = context.getMimeType(fileUrl);
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        response.setContentType(mimeType);
        response.setContentLength((int) file.length());

        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                file.getName());
        response.setHeader(headerKey, headerValue);

        try {
            InputStream myStream = new FileInputStream(fileUrl);
            IOUtils.copy(myStream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
        }
    }

    private String getFilePath(String coSpaceId, String fileName) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            if ("record".equals(coSpaceId)) {
                return PathUtil.getRootPath() + "/record/" + fileName;
            } else {
                return PathUtil.getRootPath() + "/spaces/" + coSpaceId + "/" + fileName;
            }
        } else {
            if ("record".equals(coSpaceId)) {
                return "/mnt/nfs/record/" + fileName;
            } else {
                return "/mnt/nfs/spaces/" + coSpaceId + "/" + fileName;
            }
        }
    }

}
