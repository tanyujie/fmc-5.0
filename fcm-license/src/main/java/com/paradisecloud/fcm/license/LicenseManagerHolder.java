package com.paradisecloud.fcm.license;

import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.license.LicenseParam;

/**
 * @author nj
 * @date 2022/7/26 10:08
 */
public class LicenseManagerHolder {

    private static volatile LicenseManager LICENSE_MANAGER;

    public static LicenseManager getInstance(LicenseParam param){
        if(LICENSE_MANAGER == null){
            synchronized (LicenseManagerHolder.class){
                if(LICENSE_MANAGER == null){
                    LICENSE_MANAGER = new CustomLicenseManager(param);
                }
            }
        }
        return LICENSE_MANAGER;
    }
}
