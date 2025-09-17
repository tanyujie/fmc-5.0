/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeBridgeAddpterProcessor.java
 * Package     : com.paradisecloud.fcm.fme.cache.bridgeprocessor
 * @author lilinhai 
 * @since 2021-03-22 11:50
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.cache.bridgeprocessor;

import java.util.Set;

/**  
 * <pre>FME桥适配器处理器</pre>
 * @author lilinhai
 * @since 2021-03-22 11:50
 * @version V1.0  
 */
public abstract class FmeBridgeAddpterProcessor implements FmeBridgeProcessor
{

    private boolean isBreak;

    /**
     * <p>Get Method   :   isBreak boolean</p>
     * @return isBreak
     */
    public boolean isBreak()
    {
        return isBreak;
    }

    /**
     * <p>Set Method   :   isBreak boolean</p>
     * @param isBreak
     */
    public void setBreak(boolean isBreak)
    {
        this.isBreak = isBreak;
    }

    @Override
    public Set<String> excludeFmeIps()
    {
        return null;
    }
}
