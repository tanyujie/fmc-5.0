/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DialInSecurityProfile.java
 * Package     : com.paradisecloud.fcm.fme.model.cms
 * @author sinhy 
 * @since 2021-07-26 17:07
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.model.cms;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**  
 * <pre>请加上该类的描述</pre>
 * @author sinhy
 * @since 2021-07-26 17:07
 * @version V1.0  
 */
@Getter
@Setter
@ToString
public class DialInSecurityProfile
{
    
    /**
     * 唯一id
     */
    private String id;
    
    /**
     * 名称
     */
    private String name;
    
    /**
     * 最小密码长度
     */
    private Integer minPasscodeLength; 
    
    /**
     * 策略
     */
    private Boolean allowOutOfPolicy;
    
}
