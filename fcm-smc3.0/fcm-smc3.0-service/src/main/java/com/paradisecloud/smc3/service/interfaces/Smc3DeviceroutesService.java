package com.paradisecloud.smc3.service.interfaces;


import java.util.List;

/**
 * @author nj
 * @date 2022/8/23 10:50
 */
public interface Smc3DeviceroutesService {

    String getDeviceroutes(String zoneId,Long deptId);
    List<String> getDeviceroutes(String zoneId, int number,Long deptId);
}
