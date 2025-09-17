package com.paradisecloud.smc3.service.impls;

import com.alibaba.fastjson.JSONArray;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.service.interfaces.Smc3DeviceroutesService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author nj
 * @date 2022/8/23 10:51
 */
@Service
public class Smc3DeviceroutesServiceImpl implements Smc3DeviceroutesService {

    @Override
    public String getDeviceroutes(String zoneId,Long deptId) {
        Smc3Bridge bridge = Smc3BridgeCache.getInstance().getBridgesByDept(deptId);
        String deviceroutes = bridge.getSmcDeviceroutesInvoker().getDeviceroutes(zoneId, bridge.getSmcportalTokenInvoker().getSystemHeaders());
        List<String> strings = JSONArray.parseArray(deviceroutes, String.class);
        return  strings.get(0);
    }

    @Override
    public  List<String>  getDeviceroutes(String zoneId, int number,Long deptId) {
        Smc3Bridge bridge = Smc3BridgeCache.getInstance().getBridgesByDept(deptId);
        String deviceroutes = bridge.getSmcDeviceroutesInvoker().getDeviceroutes(zoneId,number, bridge.getSmcportalTokenInvoker().getSystemHeaders());
        return  JSONArray.parseArray(deviceroutes, String.class);
    }
}
