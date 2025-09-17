/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CdrParamBuilder.java
 * Package     : com.paradisecloud.fcm.fme.model.parambuilder
 * @author sinhy 
 * @since 2021-12-21 14:05
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.model.parambuilder;

public class CdrParamBuilder extends ParamBuilder<CdrParamBuilder>
{
    /**
     * uri
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param bandwidth
     * @return ParticipantParamBuilder
     */
    public CdrParamBuilder uri(String uri)
    {
        return param("uri", uri);
    }
}
