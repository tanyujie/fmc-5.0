package com.paradisecloud.fcm.web.controller.mqtt;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.management.MalformedObjectNameException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.fcm.fme.conference.interfaces.ITemplateConferenceStartService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.mqtt.interfaces.ITerminalActionService;
import com.paradisecloud.fcm.mqtt.model.*;
import com.paradisecloud.fcm.terminal.fs.db.FreeSwitchTransaction;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.fcm.dao.model.BusiMqtt;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.BusiTerminalAction;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiTerminalActionService;
import com.paradisecloud.system.model.ExcelUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 终端动作Controller
 * 
 * @author zyz
 * @date 2021-07-31
 */
@RestController
@RequestMapping("/busi/terminalAction")
@Tag(name = "终端动作控制器")
public class BusiTerminalActionController extends BaseController
{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BusiTerminalActionController.class);
	
    @Autowired
    private IBusiTerminalActionService busiTerminalActionService;

    @Autowired
    private ITerminalActionService terminalActionService;

    @Autowired
    private ITemplateConferenceStartService templateConferenceStartService;


    @Value("${application.home}")
   	private String projectPath;


    /**
     * 查询终端列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询终端列表")
    public RestResponse list(BusiTerminalAction busiTerminalAction)
    {
        startPage();
        List<BusiTerminalAction> list = busiTerminalActionService.selectBusiTerminalActionList(busiTerminalAction);
        return getDataTable(list);
    }

    /**
     * 导出终端动作列表
     */
    @Log(title = "导出终端动作信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @Operation(summary = "导出终端动作列表")
    public RestResponse export(BusiTerminalAction busiTerminalAction)
    {
        List<BusiTerminalAction> list = busiTerminalActionService.selectBusiTerminalActionList(busiTerminalAction);
        ExcelUtil<BusiTerminalAction> util = new ExcelUtil<BusiTerminalAction>(BusiTerminalAction.class);
        return util.exportExcel(list, "action");
    }

    /**
     * 获取终端动作详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取终端动作详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiTerminalActionService.selectBusiTerminalActionById(id));
    }

    /**
     * 新增终端动作信息
     */
    @Log(title = "新增终端动作信息", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增终端动作信息")
    public RestResponse add(@RequestBody BusiTerminalAction busiTerminalAction)
    {
        return toAjax(busiTerminalActionService.insertBusiTerminalAction(busiTerminalAction));
    }

    /**
     * 修改终端动作信息
     */
    @Log(title = "修改终端动作信息", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改终端动作信息")
    public RestResponse edit(@RequestBody BusiTerminalAction busiTerminalAction)
    {
        return toAjax(busiTerminalActionService.updateBusiTerminalAction(busiTerminalAction));
    }

    /**
     * 删除终端动作信息
     */
    @Log(title = "删除终端动作信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除终端动作信息")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiTerminalActionService.deleteBusiTerminalActionByIds(ids));
    }
    
    @PostMapping("/isAgree")
    @Operation(summary = "同意和不同意直播入会或者会议发言")
    public RestResponse isAgreeTerminalAction(@RequestBody OperateStatus operateStatus)
    {
        return toAjax(busiTerminalActionService.isAgreeTerminalAction(operateStatus.getId(), operateStatus.getIsAgree()));
    }
    
    /**
     *  重启mqtt服务器
     */
    @PostMapping(value = "/restartServer")
    @Operation(summary = "重启mqtt服务器")
    public Boolean EmqxServerRestart(@RequestBody BusiMqtt busiMqtt)
    {
        return busiTerminalActionService.mqttServerRestart(busiMqtt.getId());
    }
    
    /**
     * 处理终端的重启、关机、恢复出厂设置
     * @param id
     * @param action
     */
    @PostMapping("/action")
	public void terminalActionDeal(@RequestBody TerminalAction terminalAction) 
	{
    	busiTerminalActionService.terminalActionDealResult(terminalAction);
	}
    
    /**
     * 终端升级
     * @param ids
     */
    @PostMapping("/remoteUpgrade/{ids}")
	public RestResponse terminalUpgrade(@PathVariable Long[] ids)
	{
    	return RestResponse.success(0, busiTerminalActionService.terminalRemoteUpgrade(ids), null);
	}

    /**
     * 终端一键升级
     * @return id = apptype
     */
    @PostMapping("/remoteUpgradeAll/{id}")
    public RestResponse terminalUpgradeAll(@PathVariable Long id)
    {
        return RestResponse.success(0, busiTerminalActionService.terminalRemoteUpgradeAll(id), null);
    }
    
    /**
     * 终端日志收集
     * @param id
     * @param action
     * @throws MalformedObjectNameException 
     */
    
    @PostMapping("/collectLog")
	public void terminalLogsCollect(@RequestBody TerminalAction terminalAction) throws MalformedObjectNameException 
	{
    	busiTerminalActionService.terminalCollectLogs(terminalAction.getId());
	}
    
    
    /**
     * 入会参数属性为mqtt,主持人在会控控制镜头下发命令
     * @param id
     * @param action
     */
    
    @PostMapping("/cameraControl")
	public void hostCameraControl(@RequestParam("clientId") String clientId, 
								  @RequestParam("conferenceNum") String conferenceNum, 
								  @RequestParam("controlParam") String controlParam, 
								  @RequestParam("controlCmd") String controlCmd) 
	{
    	busiTerminalActionService.hostControlCamera(clientId, conferenceNum, controlParam, controlCmd);
	}
    
    /**
     * 未注册账号与终端绑定
     * @param busiTerminal
     */
    @PostMapping("/bindNoRegisterAccount")
   	public void bindAccount(@RequestBody BusiTerminal busiTerminal) 
   	{
       	busiTerminalActionService.bindNoRegisterAccount(busiTerminal);
   	}
    
    /**
     * 给入会方式是mqtt类型的终端下发会议号和密码
     * @param busiTerminal
     */
    @GetMapping("/sendIntoConferenceInfo")
   	public void bindAccount(@RequestParam("templateId") Long templateId, @RequestParam("sn") String sn) 
   	{
       	busiTerminalActionService.inviteTerminalIntoConference(templateId,sn);
   	}
    
    /**
     * 设置横幅
     * @param BannerParams
     */
    @PostMapping("/setBanner")
   	public void hostSetBanner(@RequestBody BannerParams bannerParams) 
   	{
       	busiTerminalActionService.hostSetConferenceBanner(bannerParams);
   	}
    
    /**
     * 设置滚动横幅
     * @param BannerParams
     */
    @PostMapping("/setScrollBanner")
   	public void hostSetScrollBanner(@RequestBody BannerParams bannerParams) 
   	{
       	busiTerminalActionService.hostSetConferenceScrollBanner(bannerParams);
   	}
    
    /**
     * 日志文件的上传（多个文件）
     * @param id
     * @param action
     * @throws IOException 
     * @throws IllegalStateException 
     */
    @PostMapping("/logsUploadMulti")
	public String terminalLogsFileUpload(@RequestParam(value = "uploadFiles",required = false) List<MultipartFile> uploadFiles, HttpServletRequest request, @RequestParam("mac") String mac) throws IllegalStateException, IOException
	{
    	LOGGER.info("=========================> 上传文件开始！");
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String format = sdf.format(new Date());
    	if (!uploadFiles.isEmpty() && uploadFiles.size() > 0) {
			for (MultipartFile multipartFile : uploadFiles) {
				String fileName = multipartFile.getOriginalFilename();
				String filePath = projectPath + "/tmp/terminalLogs/" + mac + "/" + format + "/";
				LOGGER.info("=========================> 上传文件的路径！" + filePath);
				
				if(!new File(filePath).exists()) {
					new File(filePath).mkdirs();
				}
				multipartFile.transferTo(new File(filePath + fileName));
				
				//保存日志文件信息
				busiTerminalActionService.saveTerminalLogInfo(mac, filePath, fileName);
			}
			return "success";
		}
		return "failed";
	}
    
    
    /**
     * 日志文件的上传（单个文件）
     * @param id
     * @param action
     * @throws IOException 
     * @throws IllegalStateException 
     */
    @PostMapping("/LogsUpload")
    public String terminalLogsFileUpload(MultipartFile uploadFile, HttpServletRequest request, @RequestParam("sn") String sn) throws IllegalStateException, IOException 
    {
    	String UPLOAD_PATH_PREFIX = "static/uploadFile/";
    	
        if(uploadFile.isEmpty()){
        	
            //返回选择文件提示
            return "请选择上传文件";
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        //构建文件上传所要保存的"文件夹路径"--这里是相对路径，保存到项目根路径的文件夹下
        String realPath = new String("src/main/resources/" + UPLOAD_PATH_PREFIX);
        String format = sdf.format(new Date());
        
        //存放上传文件的文件夹
        File file = new File(realPath + format);
        if(!file.isDirectory()){
        	
            //递归生成文件夹
            file.mkdirs();
        }
        
        //获取原始的名字  original:最初的，起始的  方法是得到原来的文件名在客户机的文件系统名称
        String oldName = uploadFile.getOriginalFilename();
        String newName = UUID.randomUUID().toString() + oldName.substring(oldName.lastIndexOf("."),oldName.length());
        try {
        	
            //构建真实的文件路径
            File newFile = new File(file.getAbsolutePath() + File.separator + newName);
            
            //转存文件到指定路径，如果文件名重复的话，将会覆盖掉之前的文件,这里是把文件上传到 “绝对路径”
            uploadFile.transferTo(newFile);
            String filePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/uploadFile/" + format + newName;
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "上传失败!";
    }
    
    
    //文件下载相关代码
    @RequestMapping("/downFiles")
    public String downloadFiles(HttpServletRequest request, HttpServletResponse response) {
    	String fileName = request.getParameter("fileName"); // 下载文件名
    	String realFileName = fileName.split("/")[2];
        if (fileName != null) {
            //设置文件路径
        	String ctxPath = projectPath + "/tmp/terminalLogs/" ;
            File file = new File(ctxPath , fileName);
            if (file.exists()) {
                response.setContentType("application/octet-stream");
                response.setHeader("content-type", "application/octet-stream");
                response.setHeader("Content-Disposition", "attachment;fileName=" + realFileName);// 设置文件名
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return null;
    }
    
    @RequestMapping("/downFile")  
    public void downloadFile(HttpServletRequest request, HttpServletResponse response) {  
        String fileName = request.getParameter("fileName"); // 下载文件名
        String ctxPath = projectPath + "/tmp/terminalLogs/" ;
		String fullPath = ctxPath + fileName;
        File downloadFile = new File(fullPath);  
  
        ServletContext context = request.getServletContext();  
  
        String mimeType = context.getMimeType(fullPath);  
        if (mimeType == null) {  
            mimeType = "application/octet-stream";  
        }  
  
        // set content attributes for the response  
        response.setContentType(mimeType);  
        response.setContentLength((int) downloadFile.length());  
  
        // set headers for the response  
        String headerKey = "Content-Disposition";  
        String headerValue = String.format("attachment; filename=\"%s\"",  
                downloadFile.getName());  
        response.setHeader(headerKey, headerValue);  
  
        try {  
            InputStream myStream = new FileInputStream(fullPath);  
            IOUtils.copy(myStream, response.getOutputStream());  
            response.flushBuffer();  
        } catch (IOException e) {  
            LOGGER.error("下载日志文件失败!" , e);
        }  
    } 
    
    
    @RequestMapping("/downTerminalTemplateExcel")  
    public void downloadTemplateExcel(HttpServletRequest request, HttpServletResponse response) {  
//        String fileName = "terminalInfo.xlsx"; // 下载文件名
        String fileName = request.getParameter("fileName");
        String ctxPath = projectPath + "/template/" ;
		String fullPath = ctxPath + fileName;
        File downloadFile = new File(fullPath);  
  
        ServletContext context = request.getServletContext();  
  
        String mimeType = context.getMimeType(fullPath);  
        if (mimeType == null) {  
            mimeType = "application/octet-stream";  
        }  
  
        response.setContentType(mimeType);  
        response.setContentLength((int) downloadFile.length());  
  
        String headerKey = "Content-Disposition";  
        String headerValue = String.format("attachment; filename=\"%s\"",  
                downloadFile.getName());  
        response.setHeader(headerKey, headerValue);  
  
        try {  
            InputStream myStream = new FileInputStream(fullPath);  
            IOUtils.copy(myStream, response.getOutputStream());  
            response.flushBuffer();  
        } catch (IOException e) {  
            LOGGER.error("下载终端模板文件失败!" , e);
        }  
    }
    
    /**
     * 终端配置文件的上传（多个文件）
     * @param id
     * @param action
     * @throws IOException 
     * @throws IllegalStateException 
     */
    @PostMapping("/excelFilesUpload")
    @FreeSwitchTransaction
	public RestResponse excelFilesUpload(@RequestParam(value = "uploadFiles",required = false) List<MultipartFile> uploadFiles, HttpServletRequest request, @RequestParam("deptId") Long deptId) throws IllegalStateException, IOException 
	{
    	if (!uploadFiles.isEmpty() && uploadFiles.size() > 0) {
//    		for (MultipartFile multipartFile : uploadFiles) {
    			
    			//保存终端的excel文件
    			return toAjax(busiTerminalActionService.saveTerminalExcelFile(uploadFiles.get(0) , deptId));
//    		}
    	}
    	
		return null;
	}
    
    /**
     * 终端列表导出
     * @param response
     * @param ids
     */
    @RequestMapping(value = "/downTerminalExcel/{ids}", method = RequestMethod.GET)
    public void downloadAllClassmate(HttpServletResponse response,@PathVariable Long[] ids){

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("信息表");

        List<ExcelTerminalOut> list = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            list.add(busiTerminalActionService.selectExcel(ids[i]).get(0));
        }

        String fileName = "terminal"  + ".xls";
        //新增数据行，并且设置单元格数据

        int rowNum = 1;

        String[] headers = { "名称*", "类型*", "账号", "密码", "入会类型*", "终端mac地址", "IP地址", "号码", "摄像头IP", "版本", "备注"};
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
        for (ExcelTerminalOut excel : list) {
            HSSFRow row1 = sheet.createRow(rowNum);
            row1.createCell(0).setCellValue(excel.getName());
            row1.createCell(1).setCellValue(excel.getType());
            row1.createCell(2).setCellValue(excel.getCredential());
            row1.createCell(3).setCellValue(excel.getPassword());
            row1.createCell(4).setCellValue(excel.getAttendType());
            row1.createCell(5).setCellValue(excel.getSn());
            row1.createCell(6).setCellValue(excel.getIp());
            row1.createCell(7).setCellValue(excel.getNumber());
            row1.createCell(8).setCellValue(excel.getCameraIp());
            String version = excel.getAppVersionCode() + "(" + excel.getAppVersionName() + ")";
            version = StringUtils.isEmpty(excel.getAppVersionCode()) ? null : version;
            row1.createCell(9).setCellValue(version);
            row1.createCell(10).setCellValue(excel.getRemarks());
            rowNum++;
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);

        try {
            response.flushBuffer();
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            LOGGER.debug("导出终端失败!");
            e.printStackTrace();
        }
    }

    /**
     * 导出终端列表到excel表格
     * @param ids
     * @return
     */
    @Log(title = "会议号段", businessType = BusinessType.EXPORT)
    @GetMapping("/excelOut/{ids}")
    @Operation(summary = "导出会议号段列表")
    public RestResponse export(@PathVariable  Long[] ids)
    {
        List<ExcelTerminalOut> list = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            list.add(busiTerminalActionService.selectExcel(ids[i]).get(0));
        }
        ExcelUtil<ExcelTerminalOut> util = new ExcelUtil<ExcelTerminalOut>(ExcelTerminalOut.class);

        return util.exportExcel(list,"terminal");
    }


    /**
     * 获取直播终端列表
     */
    @Log(title = "终端信息")
    @GetMapping("/liveTerminalList")
    @Operation(summary = "通过会议好获取直播终端消息")
    public RestResponse getLiveTerminal(Long id , @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,@RequestParam(value = "pageNum", defaultValue = "1") int pageNum) {

        ConferenceContext conferenceContext = templateConferenceStartService.buildTemplateConferenceContext(id);
        List<TerminalLive> terminalLiveList = terminalActionService.liveTerminalList(conferenceContext);

        PaginationData<TerminalLive> pd = new PaginationData();
        if (terminalLiveList != null && terminalLiveList.size() > 0) {
            pd.setTotal((new PageInfo(terminalLiveList)).getTotal());
            pd.setSize(pageSize);
            pd.setPage(pageNum);

            int count = 0;
            count = terminalLiveList.size();
            int fromIndex = (pageNum - 1) * pageSize;
            int toIndex = pageNum * pageSize;
            if (toIndex > count) {
                toIndex = count;
            }
            if (!(toIndex > terminalLiveList.size() || fromIndex >= terminalLiveList.size())) {
                pd.setRecords(terminalLiveList.subList(fromIndex, toIndex));
            }else {
                pd.setRecords(null);
            }

        } else {
            return RestResponse.fail(0L, "查询失败", pd);
        }
        return RestResponse.success(0L, "查询成功", pd);
    }

    /**
     * 邀请或移除终端直播进入直播或者会议
     * 0===无状态
     * 1===直播
     * 2===会议中
     * @return
     */
    @Log(title = "邀请或移除直播终端")
    @PostMapping("/isJoinLiveTerminal")
    @Operation(summary = "通过会议好获取直播终端消息")
    public RestResponse isInviteLiveTerminal(@RequestBody JSONObject params){
        return RestResponse.success(busiTerminalActionService.isInviteLiveTerminal(params.get("mac").toString(), params.get("conferenceId").toString(), params.get("status").toString(), params.get("conferenceNumber").toString(), params.get("conferenceName").toString()));
    }

    /**
     * 专业终端是否打开辅流
     * @param params
     * @return
     */
    @Log(title = "专业终端是否打开辅流")
    @PostMapping("/isOpenSecondaryStream")
    @Operation(summary = "通过会议好获取直播终端消息")
    public RestResponse isOpenSecondaryStream(@RequestBody JSONObject params) {
        String id = params.get("id").toString();
        boolean isOpen = (boolean) params.get("isOpen");
        boolean isOpenSecondaryStream = terminalActionService.isOpenSecondaryStream(params.get("conferenceNumber").toString(), Long.parseLong(id), isOpen);
//        if (isOpenSecondaryStream){
//            return RestResponse.success(isOpenSecondaryStream);
//        }
        return RestResponse.success(isOpenSecondaryStream);
    }

    /**
     * 查询会议中直播终端数
     * @param conferenceId
     * @return
     */
    @Log(title = "查询会议中直播终端数")
    @GetMapping("/getLiveTerminalCount/{conferenceId}")
    @Operation(summary = "通过会议Id查询会议中直播终端数")
    public RestResponse getLiveTerminalCount(@PathVariable String conferenceId) {
        Integer count = terminalActionService.getLiveTerminalCount(conferenceId);
        Map<String, Integer> map = new HashMap<>();
        map.put("liveTerminalCount", count);
        return RestResponse.success(map);
    }
}
