/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做�?�?)</pre>
 * FileName    : Application.java
 * Package     : com.paradisecloud.application
 * @author lilinhai 
 * @since 2020-12-03 09:59
 * @version  V1.0
 */ 
package com.paradisecloud.fcm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.paradisecloud.ParadisecloudApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**  
 * <pre>springboot项目启动�?</pre>
 * @author lilinhai
 * @since 2020-12-03 09:59
 * @version V1.0  
 */
@SpringBootApplication
@EnableScheduling
@MapperScan("com.paradisecloud.smc.dao.model.mapper")
public class Application extends ParadisecloudApplication
{
    public static void main(String[] args)
    {
        start(Application.class, args);
    }
}
