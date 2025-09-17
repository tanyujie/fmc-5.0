package com.paradisecloud.fcm.license;

import com.sinhy.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;


/**
 * @author nj
 * @date 2022/7/27 11:26
 */
public class SystemLicenseMonitor extends Thread implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void run()
    {
        ThreadUtils.sleep(60 * 1000);
        while (true)
        {
            try
            {
                LicenseVerify licenseVerify = new LicenseVerify();

                //校验证书是否有效
                boolean verifyResult = licenseVerify.verify();

                if(verifyResult){
                }else{
                    logger.warn("系统试用期限已到，请购买正式版本！");
                    System.exit(0);
                }

                ThreadUtils.sleep(60 * 1000);
            }
            catch (Throwable e)
            {
                logger.error("系统到期监听器运行出错", e);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        ThreadUtils.sleep(60 * 1000);
        this.start();
    }


}
