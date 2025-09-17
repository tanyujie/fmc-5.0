/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2020-, All right reserved.
 * Description : <pre>(用一句话描述该文件做什么)</pre>
 * FileName :
 * Package :
 * 
 * @author
 * 
 * @since 2020/12/24 11:18
 * 
 * @version V1.0
 */
package com.paradiscloud.fcm.business.model.enumer;

import java.util.HashMap;
import java.util.Map;

import com.paradiscloud.fcm.business.impls.CommonFieldServiceImpl;
import com.paradiscloud.fcm.business.impls.EducationFieldServiceImpl;
import com.paradiscloud.fcm.business.interfaces.IBusinessFieldService;
import com.sinhy.exception.SystemException;

/**
 * <pre>业务领域类型</pre>
 * 
 * @author lilinhai
 * @since 2020-12-29 15:35
 * @version V1.0
 */
public enum BusinessFieldType
{
    
    /**
     * 公共版本
     */
    COMMON(100, "公共版本", new CommonFieldServiceImpl()),
    
    /**
     * 教育领域
     */
    EDUCATION(200, "教育领域", new EducationFieldServiceImpl());
    
    /**
     * 值
     */
    private int value;
    
    /**
     * 名
     */
    private String name;
    
    /**
     * 业务领域服务
     */
    private IBusinessFieldService businessFieldService;
    
    private static final Map<Integer, BusinessFieldType> MAP = new HashMap<>();
    static
    {
        for (BusinessFieldType recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }
    
    BusinessFieldType(int value, String name, IBusinessFieldService businessFieldService)
    {
        this.value = value;
        this.name = name;
        this.businessFieldService = businessFieldService;
    }
    
    /**
     * <p>
     * Get Method : value int
     * </p>
     * 
     * @return value
     */
    public int getValue()
    {
        return value;
    }
    
    /**
     * <p>
     * Get Method : name String
     * </p>
     * 
     * @return name
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * <p>Get Method   :   businessFieldService IBusinessFieldService</p>
     * @return businessFieldService
     */
    public IBusinessFieldService getBusinessFieldService()
    {
        return businessFieldService;
    }

    public static BusinessFieldType convert(Integer value)
    {
        BusinessFieldType t = MAP.get(value);
        if (t == null)
        {
            throw new SystemException("非法的" + BusinessFieldType.class.getSimpleName() + "枚举值：" + value);
        }
        return t;
    }
}
