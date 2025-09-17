package com.paradisecloud.fcm.ops.configModifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author nj
 * @date 2024/5/27 17:34
 */
public abstract class AbstractConfigModifier {

    public static Logger logger= LoggerFactory.getLogger(AbstractConfigModifier.class);

    public final void modifyConfig(String filePath, String ip) throws Exception {
       // connect();
        updateConfigFile(filePath, ip);
       // disconnect();
    }

    protected abstract void connect() throws Exception;
    protected abstract void updateConfigFile(String filePath,String ip)throws Exception ;
    protected abstract void disconnect() throws Exception;
}
