package com.paradisecloud.fcm.cdr.service.interfaces;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author johnson liu
 * @date 2021/5/12 14:05
 */
public interface ICallDetailRecordHandler
{
    /**
     * 保存CDR记录
     * 
     * @param request
     * @param response
     * @return
     */
    int saveCallDetailRecords(HttpServletRequest request, HttpServletResponse response);
    
}
