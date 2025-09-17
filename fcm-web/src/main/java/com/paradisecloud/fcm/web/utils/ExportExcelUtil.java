/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2020-, All right reserved.
 * Description : <pre>(用一句话描述该文件做什么)</pre>
 * FileName    :
 * Package     :
 * @author
 * @since 2020/12/23 18:16
 * @version  V1.0
 */
package com.paradisecloud.fcm.web.utils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.excel.EasyExcel;
import com.paradisecloud.common.model.ConferenceRecordExcel;
import com.sinhy.utils.DateUtils;

/**
 * 导出excel->web
 * @author ws
 * @version 1.0
 * @createTime 2020/12/23 18:16
 */
public class ExportExcelUtil {

    /**
     * 导出excel到web
     * 文件下载并且失败的时候返回json（默认失败了会返回一个有部分数据的Excel）
     * @param response
     */
    public static void export(HttpServletResponse response, List<? extends Object> list,String type) throws IOException {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            Date date = new Date();
            String name = DateUtils.formatTo("yyyy年MM月dd日HH时mm分", date);
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode(type + name, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), ConferenceRecordExcel.class).sheet(type).doWrite(list);
    }
}
