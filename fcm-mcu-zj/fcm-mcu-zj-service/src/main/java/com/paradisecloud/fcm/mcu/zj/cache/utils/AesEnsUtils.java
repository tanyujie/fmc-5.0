/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AesEnsUtils.java
 * Package     : com.paradisecloud.fcm.fme.attendee.utils
 * @author lilinhai 
 * @since 2021-03-04 10:35
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.zj.cache.utils;

import com.sinhy.encryptor.AESEncryptor;

/**  
 * <pre>AES可逆加密工具</pre>
 * @author lilinhai
 * @since 2021-03-04 10:35
 * @version V1.0  
 */
public class AesEnsUtils
{
    /**
     * AES可逆加密器
     */
    private static final AESEncryptor AES_ENCRYPTOR = new AESEncryptor("P+D\n\\4d\nhF!JHX^l6\\5\\B.QQ(=Xl?zNs", "2!(*Gg987f^689uH");

    /**
     * <p>Get Method   :   AES_ENCRYPTOR AESEncryptor</p>
     * @return aesEncryptor
     */
    public static AESEncryptor getAesEncryptor()
    {
        return AES_ENCRYPTOR;
    }
}
