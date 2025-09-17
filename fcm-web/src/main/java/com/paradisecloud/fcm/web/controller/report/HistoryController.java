package com.paradisecloud.fcm.web.controller.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.alibaba.fastjson.JSONArray;
import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.common.utils.uuid.IdUtils;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiOperationLog;
import com.paradisecloud.fcm.dao.model.vo.OperationLogSearchVo;
import com.paradisecloud.fcm.service.interfaces.IBusiOperationLogService;
import com.paradisecloud.fcm.service.minutes.MinutesFileHandler;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.BusiHistoryParticipant;
import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiHistoryConferenceService;
import com.paradisecloud.fcm.service.interfaces.IBusiHistoryParticipantService;
import com.paradisecloud.system.model.ExcelUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 历史会议、参会者记录控制器
 *
 * @author johnson liu
 * @date 2021/6/15 9:56
 */
@RestController
@RequestMapping("/busi/history")
@Tag(name = "历史会议、参会者记录统计")
public class HistoryController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(HistoryController.class);
    @Autowired
    private IBusiHistoryConferenceService iBusiHistoryConferenceService;
    @Autowired
    private IBusiHistoryParticipantService iBusiHistoryParticipantService;
    @Autowired
    private IBusiOperationLogService busiOperationLogService;
    @Resource
    private RedisCache redisCache;
    @Resource
    private BusiHistoryConferenceMapper busiHistoryConferenceMapper;

    /**
     * 按组织、时间范围、会议号搜索
     */
    @GetMapping("/conference/reportByDept")
    @Operation(summary = "历史会议列表")
    public RestResponse reportByDept(ReportSearchVo reportSearchVo) {
        log.info("{}/reportByDept方法入参:{}", this.getClass(), reportSearchVo);
        PaginationData<Map<String,Object>> mapList = iBusiHistoryConferenceService.selectHistoryPage(reportSearchVo);
        return RestResponse.success(0L, "查询成功", mapList);
    }

    /**
     * 导出历史会议，每次挂断会保存该历史记录列表
     */
    @Log(title = "历史会议，每次挂断会保存该历史记录", businessType = BusinessType.EXPORT)
    @GetMapping("/conference/export")
    @Operation(summary = "导出历史会议，每次挂断会保存该历史记录列表")
    public RestResponse exportConference(ReportSearchVo reportSearchVo) {
        log.info("{}/exportConference方法入参:{}", this.getClass(), reportSearchVo);
        PaginationData<Map<String,Object>> mapList = iBusiHistoryConferenceService.selectHistoryPage(reportSearchVo);
        List<BusiHistoryConference> historyConferenceList = new ArrayList<>(mapList.getPage());
        for (Map<String,Object> map : mapList.getRecords()) {
            BusiHistoryConference historyConference =(BusiHistoryConference) map.get("historyConference");
            historyConferenceList.add(historyConference);
        }
        ExcelUtil<BusiHistoryConference> util = new ExcelUtil<BusiHistoryConference>(BusiHistoryConference.class);
        //util.exportExcel(historyConferenceList, "conferenceRecord",ServletUtils.getRequest(), ServletUtils.getResponse());
        return null;
    }
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54
     * @return List<DeptRecordCount>
     */
    @GetMapping("/conference/getDeptRecordCounts")
    @Operation(summary = "获取部门历史统计记录")
    public RestResponse getDeptRecordCounts()
    {
        return RestResponse.success(iBusiHistoryConferenceService.getDeptRecordCounts());
    }

    /**
     * 按会议模板统计模板使用次数和累计会议时长
     *
     * @param hisConferenceId 历史会议id
     * @param isJoin          是否入会
     */
    @GetMapping("/participant/{hisConferenceId}")
    @Operation(summary = "历史与会者列表信息,包含进出时间")
    public RestResponse reportByHisConferenceId(@PathVariable String hisConferenceId, @RequestParam(required = false) Boolean isJoin, @RequestParam(required = false) Integer pageNum, @RequestParam(required = false) Integer pageSize) {
        log.info("{}/reportByHisConferenceId方法入参:{}", this.getClass(), hisConferenceId);
        PaginationData<Map<String,Object>> list = iBusiHistoryParticipantService.reportByHisConferenceId(hisConferenceId, isJoin, pageNum, pageSize);
        return RestResponse.success(list);
    }

    /**
     * 导出历史会议，每次挂断会保存该历史记录列表
     */
    @Log(title = "历史会议，每次挂断会保存该历史记录", businessType = BusinessType.EXPORT)
    @GetMapping("/participant/{hisConferenceId}/export")
    @Operation(summary = "导出历史会议，每次挂断会保存该历史记录列表")
    public RestResponse exportParticipant(@PathVariable String hisConferenceId, @RequestParam(required = false) Boolean isJoin) {
        log.info("{}/exportParticipant方法入参:{}", this.getClass(), hisConferenceId);
        PaginationData<Map<String,Object>> jsonArray = iBusiHistoryParticipantService.reportByHisConferenceId(hisConferenceId, isJoin, 1, 1000);
        List<BusiHistoryParticipant> historyParticipantList = new ArrayList<>();
        for (Object o : jsonArray.getRecords()) {
            JSONObject jsonObject= (JSONObject) o;
            BusiHistoryParticipant historyParticipant = (BusiHistoryParticipant)jsonObject.get("historyParticipant");
            historyParticipantList.add(historyParticipant);
        }
        ExcelUtil<BusiHistoryParticipant> util = new ExcelUtil<BusiHistoryParticipant>(BusiHistoryParticipant.class);
        //util.exportExcel(historyParticipantList, "participantRecord",ServletUtils.getRequest(), ServletUtils.getResponse());
        return null;
    }

    /**
     * 查询历史会议终端参与信息列表
     *
     * @param hisConferenceId 历史会议id
     * @param participantTerminalId 参会者终端id
     */
    @GetMapping("/participant/terminal/{hisConferenceId}/{participantTerminalId}")
    @Operation(summary = "历史与会者列表信息,包含进出时间")
    public RestResponse reportTerminalByHisConferenceId(@PathVariable Long hisConferenceId, @PathVariable Long participantTerminalId, ReportSearchVo reportSearchVo) {
        log.info("{}/reportTerminalByHisConferenceId方法入参:{}", this.getClass(), hisConferenceId);
        PaginationData<Map<String,Object>> list = iBusiHistoryParticipantService.reportTerminalByHisConferenceIdPage(hisConferenceId, participantTerminalId, reportSearchVo);
        return RestResponse.success(list);
    }

    @RequestMapping(value = "/downHistory/{ids}", method = RequestMethod.GET)
    @Operation(description = "导出历史会议列表")
    public RestResponse downloadAllClassmate(@PathVariable Long[] ids) {
        String uuid = IdUtils.simpleUUID();
        String key = "download_history:" + uuid;
        this.redisCache.setCacheObject(key, ids, 1, TimeUnit.MINUTES);
        return RestResponse.success(uuid);
    }

    @RequestMapping(value = "/downHistoryExcel/{token}", method = RequestMethod.GET)
    public void downloadAllClassmate(HttpServletResponse response, @PathVariable String token) {
        String fileName = "downHistoryExcel" + ".zip";
        JSONArray ids = null;
        String key = "download_history:" + token;
        try {
            ids = this.redisCache.getCacheObject(key);
            if (ids == null) {
                return;
            }
        } catch (Exception e) {
            return;
        }

        try {
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
            for (Object idObj : ids) {
                Long id = (Long) idObj;
                HSSFWorkbook hssfWorkbook = iBusiHistoryParticipantService.downHistoryExcel(id);
                if (hssfWorkbook != null) {
                    BusiHistoryConference busiHistoryConference = iBusiHistoryConferenceService.selectBusiHistoryConferenceById(id);
                    Date conferenceStartTime = busiHistoryConference.getConferenceStartTime();
                    SimpleDateFormat simple = new SimpleDateFormat("yyyyMMddHHmmss");
                    String startTime = simple.format(conferenceStartTime);
                    zipOut.putNextEntry(new ZipEntry(busiHistoryConference.getName() + "_" + busiHistoryConference.getNumber() + "_" + startTime + ".xls"));
                    hssfWorkbook.write(zipOut);
                    zipOut.closeEntry();
                }
            }
            zipOut.close();
            logger.info("Zip file created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出操作日志记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:log:export')")
    @Log(title = "操作日志记录", businessType = BusinessType.EXPORT)
    @GetMapping("/export/{hisConferenceId}")
    @Operation(summary = "导出操作日志记录列表")
    public void export(HttpServletResponse response, @PathVariable Long hisConferenceId)
    {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("操作日志记录表");

        OperationLogSearchVo operationLogSearchVo = new OperationLogSearchVo();
        operationLogSearchVo.setHistoryConferenceId(hisConferenceId);
        operationLogSearchVo.setSort("asc");
        List<BusiOperationLog> operationLogList = busiOperationLogService.selectBusiOperationLogList(operationLogSearchVo);

        String fileName = "operationLog"  + ".xls";
        //新增数据行，并且设置单元格数据

        int rowNum = 1;

        String[] headers = { "操作人员", "主机", "操作状态", "操作消息", "操作日期"};
        //headers表示excel表中第一行的表头

        HSSFRow row = sheet.createRow(0);
        //在excel表中添加表头

        for(int i=0;i<headers.length;i++){
            HSSFCell cell = row.createCell(i);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
            cell.setCellType(CellType.STRING);
        }

        //在表中存放查询到的数据放入对应的列
        for (BusiOperationLog excel : operationLogList) {
            HSSFRow row1 = sheet.createRow(rowNum);
            row1.createCell(0).setCellValue(excel.getOperatorName());
            row1.createCell(1).setCellValue(excel.getIp());
            row1.createCell(2).setCellValue(excel.getActionResult() == 1 ? "成功" : "失败");
            row1.createCell(3).setCellValue(excel.getActionDetails());
            Date time = excel.getTime();
            String dateToString = DateUtil.convertDateToString(time, "yyyy-MM-dd hh:mm:ss");
            row1.createCell(4).setCellValue(dateToString);
            rowNum++;
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);

        try {
            response.flushBuffer();
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/downMinutesFile/{hisConferenceId}")
    @Operation(description = "下载会议纪要文件")
    public void downloadMinutesFile(HttpServletRequest request, HttpServletResponse response, @PathVariable Long hisConferenceId) {
        BusiHistoryConference busiHistoryConference = iBusiHistoryConferenceService.selectBusiHistoryConferenceById(hisConferenceId);
        Assert.notNull(busiHistoryConference, "该历史会议不存在！");
        String minutesFilePath = MinutesFileHandler.generateFilePath(busiHistoryConference.getCoSpace(), busiHistoryConference.getNumber(), busiHistoryConference.getId());
        File downloadFile = new File(minutesFilePath);
        if (!downloadFile.exists()) {
            Assert.isTrue(false, "该历史会议纪要不存在！");
        }

        // set content attributes for the response
        response.setContentType("application/octet-stream");
        response.setContentLength((int) downloadFile.length());
        response.setHeader("Content-disposition", "attachment;filename=" + downloadFile.getName());

        try {
            InputStream myStream = new FileInputStream(minutesFilePath);
            IOUtils.copy(myStream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            logger.error("下载会议纪要文件失败!" , e);
            Assert.isTrue(false, "下载文件出错！");
        }
    }


    @RequestMapping("/downMeetingMinutesDocFile/{hisConferenceId}")
    @Operation(description = "下载会议纪要文件PDF")
    public void downMeetingMinutesDocFile(HttpServletRequest request, HttpServletResponse response, @PathVariable Long hisConferenceId) {
        BusiHistoryConference busiHistoryConference = iBusiHistoryConferenceService.selectBusiHistoryConferenceById(hisConferenceId);
        Assert.notNull(busiHistoryConference, "该历史会议不存在！");
        String minutesFilePath = MinutesFileHandler.generateFilePdfPath(busiHistoryConference.getCoSpace(), busiHistoryConference.getNumber(), busiHistoryConference.getId());
        File downloadFile = new File(minutesFilePath);
        if (!downloadFile.exists()) {
            Assert.isTrue(false, "该历史会议纪要PDF不存在！");
        }

        // set content attributes for the response
        response.setContentType("application/octet-stream");
        response.setContentLength((int) downloadFile.length());
        response.setHeader("Content-disposition", "attachment;filename=" + downloadFile.getName());

        try {
            InputStream myStream = new FileInputStream(minutesFilePath);
            IOUtils.copy(myStream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            logger.error("下载会议纪要文件失败!" , e);
            Assert.isTrue(false, "下载文件出错！");
        }
    }

    @DeleteMapping("/deleteMeetingMinutesPdfFile/{hisConferenceIds}")
    @Operation(description = "删除会议纪要文件PDF",summary = "删除会议纪要文件PDF")
    public RestResponse deleteMeetingMinutesPdfFile(HttpServletRequest request, HttpServletResponse response, @PathVariable("hisConferenceIds") Long[] hisConferenceIds) {
        for (Long hisConferenceId : hisConferenceIds) {
            BusiHistoryConference busiHistoryConference = iBusiHistoryConferenceService.selectBusiHistoryConferenceById(hisConferenceId);
            Assert.notNull(busiHistoryConference, "该历史会议不存在！");
            busiHistoryConference.setMinutesDoc(0);
            iBusiHistoryConferenceService.updateBusiHistoryConference(busiHistoryConference);
            String minutesFilePath = MinutesFileHandler.generateFilePdfPath(busiHistoryConference.getCoSpace(), busiHistoryConference.getNumber(), busiHistoryConference.getId());
            File downloadFile = new File(minutesFilePath);
            if (downloadFile.exists()) {
                downloadFile.delete();
            }
        }
        return RestResponse.success();
    }


    /**
     * 按组织、时间范围、会议号搜索 历史会议纪要文档列表
     */
    @GetMapping("/conferenceMinutesDoc/reportByDept")
    @Operation(summary = "历史会议纪要文档列表")
    public RestResponse reportDocxByDept(ReportSearchVo reportSearchVo) {
        log.info("{}/reportByDept方法入参:{}", this.getClass(), reportSearchVo);
        PaginationData<Map<String,Object>> mapList = iBusiHistoryConferenceService.selectHistoryPageDoc(reportSearchVo);
        return RestResponse.success(0L, "查询成功", mapList);
    }


    @PostMapping("/preview/{hisConferenceId}")
    public List<Object> previewDocx(@PathVariable Long hisConferenceId) throws IOException {
        BusiHistoryConference busiHistoryConference = iBusiHistoryConferenceService.selectBusiHistoryConferenceById(hisConferenceId);
        Assert.notNull(busiHistoryConference, "该历史会议不存在！");
        String minutesFilePath = MinutesFileHandler.generateFilePdfPath(busiHistoryConference.getCoSpace(), busiHistoryConference.getNumber(), busiHistoryConference.getId());
        File downloadFile = new File(minutesFilePath);
        if (!downloadFile.exists()) {
            Assert.isTrue(false, "该历史会议纪要PDF不存在！");
        }
        List<Object> contentList = new ArrayList<>();
        try (FileInputStream  fis = new FileInputStream(minutesFilePath);
             XWPFDocument document = new XWPFDocument(fis)) {

            for (XWPFParagraph paragraph : document.getParagraphs()) {
                // 添加文本
                contentList.add(paragraph.getText());
                // 添加段落中的图片
                for (XWPFPictureData pictureData : document.getAllPictures()) {
                    if (paragraph.getRuns().stream().anyMatch(run -> run.getEmbeddedPictures().contains(pictureData))) {
                        byte[] bytes = pictureData.getData();
                        String base64Image = "data:" + pictureData.suggestFileExtension() + ";base64," + Base64.getEncoder().encodeToString(bytes);
                        contentList.add(base64Image);
                    }
                }
            }
        }

        return contentList;
    }

    @DeleteMapping("/deleteAllMeetingMinutesPdfFile")
    @Operation(description = "删除所有会议纪要文件PDF",summary = "删除所有会议纪要文件PDF")
    public RestResponse deleteMeetingMinutesPdfFile() {
        BusiHistoryConference busiHistoryConferenceCon = new BusiHistoryConference();
        busiHistoryConferenceCon.setMinutesDoc(1);
        final List<BusiHistoryConference> busiHistoryConferenceList = iBusiHistoryConferenceService.selectBusiHistoryConferenceList(busiHistoryConferenceCon);
        busiHistoryConferenceMapper.deleteAllMinutesHistoryConference();
        if (busiHistoryConferenceList.size() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (BusiHistoryConference busiHistoryConference : busiHistoryConferenceList) {
                        String minutesFilePath = MinutesFileHandler.generateFilePdfPath(busiHistoryConference.getCoSpace(), busiHistoryConference.getNumber(), busiHistoryConference.getId());
                        File downloadFile = new File(minutesFilePath);
                        if (downloadFile.exists()) {
                            downloadFile.delete();
                        }
                    }
                }
            }).start();
        }
        return RestResponse.success();
    }

}
