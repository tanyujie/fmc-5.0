package com.paradisecloud.fcm.web.controller.smartRoom;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.common.enumer.DeviceType;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomDoorplate;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomDoorplateVo;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomDoorplateService;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.enums.BusinessType;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

/**
 * 会议室门牌Controller
 *
 * @author lilinhai
 * @date 2024-02-19
 */
@RestController
@RequestMapping("/busi/doorplate")
@Tag(name = "会议室门牌")
public class BusiSmartRoomDoorplateController extends BaseController {
    @Resource
    private IBusiSmartRoomDoorplateService busiSmartRoomDoorplateService;

    /**
     * 查询会议室门牌列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询会议室门牌列表")
    public RestResponse list(BusiSmartRoomDoorplateVo busiSmartRoomDoorplateVo)
    {
        startPage();
        List<BusiSmartRoomDoorplate> list = busiSmartRoomDoorplateService.selectBusiSmartRoomDoorplateList(busiSmartRoomDoorplateVo);
        return getDataTable(list);
    }

    /**
     * 获取会议室门牌详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取会议室门牌详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiSmartRoomDoorplateService.selectBusiSmartRoomDoorplateById(id));
    }

    /**
     * 新增会议室门牌
     */
    @PreAuthorize("@ss.hasPermi('busi:doorplate:add')")
    @Log(title = "会议室门牌", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增会议室门牌")
    public RestResponse add(@RequestBody BusiSmartRoomDoorplate busiSmartRoomDoorplate)
    {
        return toAjax(busiSmartRoomDoorplateService.insertBusiSmartRoomDoorplate(busiSmartRoomDoorplate));
    }

    /**
     * 修改会议室门牌
     */
    @PreAuthorize("@ss.hasPermi('busi:doorplate:edit')")
    @Log(title = "会议室门牌", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改会议室门牌")
    public RestResponse edit(@RequestBody BusiSmartRoomDoorplate busiSmartRoomDoorplate)
    {
        return toAjax(busiSmartRoomDoorplateService.updateBusiSmartRoomDoorplate(busiSmartRoomDoorplate));
    }

    /**
     * 删除会议室门牌
     */
    @PreAuthorize("@ss.hasPermi('busi:doorplate:remove')")
    @Log(title = "会议室门牌", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除会议室门牌")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiSmartRoomDoorplateService.deleteBusiSmartRoomDoorplateByIds(ids));
    }

    /**
     * 通过 Excel 导入
     * @param uploadFile
     * @param
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    @PostMapping("/importSmartRoomDoorplateByExcel")
    @Operation(summary = "导入会议室门牌")
    public RestResponse importSmartRoomDoorplateByExcel(@RequestParam(value = "uploadFile") MultipartFile uploadFile) throws IllegalStateException
    {
        return toAjax(busiSmartRoomDoorplateService.importSmartRoomDoorplateByExcel(uploadFile));
    }

    /**
     * 通过 Excel 导出
     * @param
     * @param
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    @GetMapping("/exportSmartRoomDoorplateByExcel/{ids}")
    @Operation(summary = "导出会议室门牌")
    public void exportSmartRoomDoorplateByExcel(HttpServletResponse response, @PathVariable Long[] ids) throws IllegalStateException, IOException {
        List<BusiSmartRoomDoorplate> busiSmartRoomDoorplateList = new ArrayList<>();
        String fileName = "会议室门牌" + ".xls";
        fileName = URLEncoder.encode(fileName, "UTF-8");
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("SmartRoomDoorplate");

        for (Long id : ids) {
            BusiSmartRoomDoorplate busiSmartRoomDoorplate = busiSmartRoomDoorplateService.selectBusiSmartRoomDoorplateById(id);
            if (busiSmartRoomDoorplate != null) {
                busiSmartRoomDoorplateList.add(busiSmartRoomDoorplate);
            }
        }

        //新增数据行，并且设置单元格数据

        int rowNum = 1;

        String[] headers = { "名称*", "序列号*", "IP地址", "APP版本号", "APP版本名", "软件类型", "连接IP", "备注"};
        //headers表示excel表中第一行的表头

        HSSFRow row = sheet.createRow(0);
        //在excel表中添加表头

        for(int i=0;i<headers.length;i++){
            HSSFCell cell = row.createCell(i);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
            cell.setCellType(CellType.STRING);
        }

        if (busiSmartRoomDoorplateList.size() > 0) {
            for (BusiSmartRoomDoorplate busiSmartRoomDoorplate : busiSmartRoomDoorplateList) {
                HSSFRow rowTemp = sheet.createRow(rowNum);
                rowTemp.createCell(0).setCellValue(busiSmartRoomDoorplate.getName());
                rowTemp.createCell(1).setCellValue(busiSmartRoomDoorplate.getSn());
                rowTemp.createCell(2).setCellValue(busiSmartRoomDoorplate.getIp());
                rowTemp.createCell(3).setCellValue(busiSmartRoomDoorplate.getAppVersionCode());
                rowTemp.createCell(4).setCellValue(busiSmartRoomDoorplate.getAppVersionName());
                Integer appType = Integer.valueOf(busiSmartRoomDoorplate.getAppType());
                if (appType != null) {
                    DeviceType convert = DeviceType.convert(appType);
                    rowTemp.createCell(5).setCellValue(convert.getCode() + ":" + convert.getName());
                }
                rowTemp.createCell(6).setCellValue(busiSmartRoomDoorplate.getConnectIp());
                rowTemp.createCell(7).setCellValue(busiSmartRoomDoorplate.getRemark());
                rowNum++;
            }

        }
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);

        try {
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            workbook.close();
        }
    }

    /**
     * Excel模板
     * @param
     * @param
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    @RequestMapping(value = "/excelTemplate", method = RequestMethod.GET)
    @Operation(summary = "下载会议室门牌Excel模板")
    public void excelTemplate(HttpServletResponse response) throws IllegalStateException, IOException {
        String fileName = "Excel.xls";
        fileName = URLEncoder.encode(fileName, "UTF-8");
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("模板");

        int rowNum = 1;

        String[] headers = { "名称*", "序列号*", "IP地址", "软件类型", "备注"};
        HSSFRow row = sheet.createRow(0);

        for(int i=0; i<headers.length; i++){
            HSSFCell cell = row.createCell(i);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
            cell.setCellType(CellType.STRING);
        }

        List<Map<String, Object>> deviceTypeList = DeviceType.getDeviceTypeList();
        String[] strs = new String[deviceTypeList.size()];
        if (deviceTypeList != null && deviceTypeList.size() > 0) {
            for (int i = 0; i < deviceTypeList.size(); i++) {
                Map<String, Object> map = deviceTypeList.get(i);
                String name = String.valueOf(map.get("name"));
                String code = String.valueOf(map.get("code"));
                strs[i] = code + ":" + name;
            }
        }

        HSSFRow rowTemp = sheet.createRow(rowNum);
        rowTemp.createCell(0).setCellValue("设备A");
        rowTemp.createCell(1).setCellValue("524f3s6fd14d6sv");
        rowTemp.createCell(2).setCellValue("192.168.1.1");
        rowTemp.createCell(3).setCellValue("0:电子门牌");
        rowTemp.createCell(4).setCellValue("备注内容");

        // 创建下拉列表的单元格范围
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1, 3, 3);

        // 创建下拉列表的数据约束
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(strs);
        DataValidation validation = validationHelper.createValidation(constraint, addressList);

        // 将数据约束应用到表格中的单元格
        sheet.addValidationData(validation);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);

        try {
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            workbook.close();
        }
    }

    /**
     * 获取未绑定会议室门牌详细信息
     */
    @GetMapping(value = "/notBoundDoorplate")
    @Operation(summary = "获取未绑定会议室门牌详细信息")
    public RestResponse notBound(@RequestParam(value = "roomId",required = false) Long roomId)
    {
        return RestResponse.success(busiSmartRoomDoorplateService.notBound(roomId));
    }

}
