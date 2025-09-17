/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ConfigurationCluster.java
 * Package     : com.paradisecloud.fcm.fme.model.cms.system
 * @author sinhy 
 * @since 2021-09-01 20:20
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.model.cms.system;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**  
 * <pre>配置集群信息</pre>
 * @author sinhy
 * @since 2021-09-01 20:20
 * @version V1.0  
 */
@Getter
@Setter
@ToString
public class ConfigurationCluster
{
    
    private String uniqueName;
    
    private Long peerLinkBitRate;
    
    private Long participantLimit;
    
    private Long loadLimit;
    
    private Long newConferenceLoadLimitBasisPoints;
    
    private Long existingConferenceLoadLimitBasisPoints;
    
    private Long maxPeerVideoStreams;
}
