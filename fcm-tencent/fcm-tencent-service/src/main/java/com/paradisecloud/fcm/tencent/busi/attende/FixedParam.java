/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FixedParam.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.attendee
 * @author sinhy 
 * @since 2021-09-02 17:32
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.tencent.busi.attende;

import java.util.Map;

/**  
 * <pre>固定参数</pre>
 * @author sinhy
 * @since 2021-09-02 17:32
 * @version V1.0  
 */
public class FixedParam
{

    private boolean isFixed;
    private String name;
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-02 17:55 
     * @param name 
     * @param fixedParamMap 
     */
    public FixedParam(String name, Map<String, FixedParam> fixedParamMap)
    {
        this.name = name;
        fixedParamMap.put(name, this);
    }
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-02 18:54  
     */
    public FixedParam()
    {
        
    }

    /**
     * <p>Get Method   :   isFixed boolean</p>
     * @return isFixed
     */
    public boolean isFixed()
    {
        return isFixed;
    }
    /**
     * <p>Set Method   :   isFixed boolean</p>
     * @param isFixed
     */
    public void setFixed(boolean isFixed)
    {
        this.isFixed = isFixed;
    }
    /**
     * <p>Get Method   :   name String</p>
     * @return name
     */
    public String getName()
    {
        return name;
    }
    /**
     * <p>Set Method   :   name String</p>
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }
    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author sinhy
     * @since 2021-09-02 17:55 
     * @return
     * @see Object#toString()
     */
    @Override
    public String toString()
    {
        return "FixedParam [isFixed=" + isFixed + ", name=" + name + "]";
    }
}
