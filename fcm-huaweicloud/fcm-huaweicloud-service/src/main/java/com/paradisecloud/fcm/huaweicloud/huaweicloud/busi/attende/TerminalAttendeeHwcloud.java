/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : TerminalAttendee.java
 * Package     : com.paradisecloud.fcm.fme.model.busi
 * @author lilinhai 
 * @since 2021-02-02 14:54
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende;

import com.paradisecloud.fcm.common.enumer.AttendType;

import java.util.HashMap;
import java.util.Map;

/**  
 * <pre>真实的参会者</pre>
 * @author lilinhai
 * @since 2021-02-02 14:54
 * @version V1.0  
 */
public class TerminalAttendeeHwcloud extends AttendeeHwcloud
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
     * 默认要外呼
     */
    private int attendType = AttendType.OUT_BOUND.getValue();
    
    /**
     * 终端类型名
     */
    private String terminalTypeName;
    
    /**
     * 终端sn
     */
    private String sn;
    
    /**
     * 业务属性
     */
    private Map<String, Object> businessProperties = new HashMap<>();





    
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
     * <p>Get Method   :   attendType int</p>
     * @return attendType
     */
    public int getAttendType()
    {
        return attendType;
    }

    /**
     * <p>Set Method   :   attendType int</p>
     * @param attendType
     */
    public void setAttendType(int attendType)
    {
        this.attendType = attendType;
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

    /**
     * <p>Get Method   :   sn String</p>
     * @return sn
     */
    @Override
    public String getSn()
    {
        return sn;
    }

    /**
     * <p>Set Method   :   sn String</p>
     * @param sn
     */
    @Override
    public void setSn(String sn)
    {
        this.sn = sn;
    }

    public int getTerminalType() {
        return terminalType;
    }

    public String getTerminalTypeName() {
        return terminalTypeName;
    }
}
