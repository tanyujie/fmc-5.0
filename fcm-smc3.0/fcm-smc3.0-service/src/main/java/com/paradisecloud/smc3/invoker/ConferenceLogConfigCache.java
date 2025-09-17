package com.paradisecloud.smc3.invoker;

import com.github.pagehelper.util.StringUtil;
import com.paradisecloud.fcm.common.utils.PathUtil;
import com.paradisecloud.fcm.common.utils.PropertiesUtil;
import lombok.Data;

import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2023/3/6 9:58
 */
@Data
public class ConferenceLogConfigCache {

    private static Map<String, String> logIdMap=new ConcurrentHashMap<>();
    private static Map<Object, Object> resultMap=new ConcurrentHashMap<>();
    private static final ConferenceLogConfigCache INSTANCE=new ConferenceLogConfigCache();

    public ConferenceLogConfigCache() {
        init();
    }



    private  void init() {
        String filePath = PathUtil.getRootPath() + "/external_smc_logId.properties";
        Properties properties = PropertiesUtil.readProperties(filePath);
        Set<Map.Entry<Object, Object>> entries = properties.entrySet();
        for (Map.Entry<Object, Object> entry : entries) {
            Object key = entry.getKey();
           String s= (String) key;
           if(s.contains("result.map")){
               int resultId = getResultId(s);
               resultMap.put(resultId,(String)entry.getValue());
           }else {
               logIdMap.put(s,(String)entry.getValue());
           }
        }
    }

    public static ConferenceLogConfigCache getInstance() {
        return INSTANCE;
    }

    public static Map<String, String> getLogIdMap() {
        return logIdMap;
    }
    private  int getResultId(String key){
        String subStr=key.substring(key.indexOf("[")+1,key.indexOf("]"));
        if(StringUtil.isEmpty(subStr)){
            return 0;
        }
        return Integer.parseInt(subStr);
    }
}
