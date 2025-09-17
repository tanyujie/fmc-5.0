package com.paradisecloud.fcm.web.controller.log;

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.model.BusiOperationLog;
import com.paradisecloud.fcm.dao.model.vo.OperationLogSearchVo;
import com.paradisecloud.fcm.service.interfaces.IBusiOperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 操作日志记录Controller
 *
 * @author lilinhai
 * @date 2023-11-27
 */
@RestController
@RequestMapping("/busi/operationLog")
@Tag(name = "操作日志记录")
public class BusiOperationLogController extends BaseController
{
    @Autowired
    private IBusiOperationLogService busiOperationLogService;

    /**
     * 查询操作日志记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:log:list')")
    @GetMapping("/list")
    @Operation(summary = "查询操作日志记录列表")
    public RestResponse list(OperationLogSearchVo busiOperationLog)
    {
//        startPage();
        List<BusiOperationLog> list = busiOperationLogService.selectBusiOperationLogList(busiOperationLog);
        return getDataTable(list);
    }

    /**
     * 导出操作日志记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:log:export')")
    @Log(title = "操作日志记录", businessType = BusinessType.EXPORT)
    @GetMapping("/export/{ids}")
    @Operation(summary = "导出操作日志记录列表")
    public void export(HttpServletResponse response, @PathVariable Long[] ids)
    {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("操作日志记录表");

        List<BusiOperationLog> list = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            list.add(busiOperationLogService.selectBusiOperationLogById(ids[i]));
        }

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
        for (BusiOperationLog excel : list) {
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

    /**
     * 获取操作日志记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:log:query')")
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取操作日志记录详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiOperationLogService.selectBusiOperationLogById(id));
    }

    /**
     * 新增操作日志记录
     */
//    @PreAuthorize("@ss.hasPermi('system:log:add')")
//    @Log(title = "操作日志记录", businessType = BusinessType.INSERT)
//    @PostMapping
//    @Operation(summary = "新增操作日志记录")
//    public RestResponse add(@RequestBody BusiOperationLog busiOperationLog)
//    {
//        return toAjax(busiOperationLogService.insertBusiOperationLog(busiOperationLog));
//    }

    /**
     * 修改操作日志记录
     */
//    @PreAuthorize("@ss.hasPermi('system:log:edit')")
//    @Log(title = "操作日志记录", businessType = BusinessType.UPDATE)
//    @PutMapping
//    @Operation(summary = "修改操作日志记录")
//    public RestResponse edit(@RequestBody BusiOperationLog busiOperationLog)
//    {
//        return toAjax(busiOperationLogService.updateBusiOperationLog(busiOperationLog));
//    }

    /**
     * 删除操作日志记录
     */
//    @PreAuthorize("@ss.hasPermi('system:log:remove')")
//    @Log(title = "操作日志记录", businessType = BusinessType.DELETE)
//    @DeleteMapping("/{ids}")
//    @Operation(summary = "删除操作日志记录")
//    public RestResponse remove(@PathVariable Long[] ids)
//    {
//        return toAjax(busiOperationLogService.deleteBusiOperationLogByIds(ids));
//    }
}
