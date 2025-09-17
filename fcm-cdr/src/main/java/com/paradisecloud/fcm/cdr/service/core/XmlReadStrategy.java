package com.paradisecloud.fcm.cdr.service.core;

import com.paradisecloud.fcm.cdr.service.model.RecordElement;

/**
 * Xml读取策略
 * 
 * @author johnson liu
 * @date 2021/5/13 23:06
 */
public interface XmlReadStrategy<T>
{
    /**
     * 读取Xml并转为JavaBean
     * 
     * @param recordElement
     * @return
     */
    T readToBean(String session, RecordElement recordElement);
    
    /**
     * 执行数据库插入操作
     * 
     * @param t
     */
    void executeAdd(T t);
    
    /**
     * 执行数据库插入操作
     * 
     * @param recordElement
     * @return
     */
    void executeAdd(String session, RecordElement recordElement, String fmeIp);
}
