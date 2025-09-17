/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : TerminalAttendee.java
 * Package     : com.paradisecloud.fcm.fme.model.busi
 * @author lilinhai 
 * @since 2021-02-02 14:54
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.kdc.model.busi.attendee;

import java.util.HashMap;
import java.util.Map;

/**  
 * <pre>真实的参会者</pre>
 * @author lilinhai
 * @since 2021-02-02 14:54
 * @version V1.0  
 */
public class TerminalAttendeeForMcuKdc extends AttendeeForMcuKdc
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-02 14:55 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 终端类型
     */
    private int terminalType;
    
    /**
     * 终端类型名
     */
    private String terminalTypeName;
    
    /**
     * 业务属性
     */
    private Map<String, Object> businessProperties = new HashMap<>();

    /**
     * <p>Get Method   :   terminalType Integer</p>
     * @return terminalType
     */
    public int getTerminalType()
    {
        return terminalType;
    }
    
    /**
     * <p>Get Method   :   businessProperties Map<String,Object></p>
     * @return businessProperties
     */
    public Map<String, Object> getBusinessProperties()
    {
        return businessProperties;
    }

    /**
     * <p>Set Method   :   businessProperties Map<String,Object></p>
     * @param businessProperties
     */
    public void putBusinessProperties(Map<String, Object> businessProperties)
    {
        this.businessProperties.putAll(businessProperties);
    }

    /**
     * <p>Set Method   :   terminalType Integer</p>
     * @param terminalType
     */
    public void setTerminalType(int terminalType)
    {
        if (this.terminalType != terminalType)
        {
            this.terminalType = terminalType;
            updateMap.put("terminalType", terminalType);
        }
    }


    /**
     * <p>Get Method   :   terminalTypeName String</p>
     * @return terminalTypeName
     */
    public String getTerminalTypeName()
    {
        return terminalTypeName;
    }

    /**
     * <p>Set Method   :   terminalTypeName String</p>
     * @param terminalTypeName
     */
    public void setTerminalTypeName(String terminalTypeName)
    {
        if (terminalTypeName != null && !terminalTypeName.equals(this.terminalTypeName))
        {
            this.terminalTypeName = terminalTypeName;
            updateMap.put("terminalTypeName", terminalTypeName);
        }
    }
    
}
