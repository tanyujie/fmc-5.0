package com.paradisecloud.fcm.ops.configModifier;

import com.paradisecloud.fcm.ops.utils.ConfigFileModifier;
import com.paradisecloud.fcm.ops.utils.SshRemoteServerOperateForOPS;
import com.paradisecloud.fcm.service.util.SshRemoteServerOperateForMeetingFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nj
 * @date 2024/5/27 9:29
 */
public class FreeSwitchConfigModifier extends AbstractConfigModifier {

    private SshRemoteServerOperateForOPS sshRemoteServerOperate;
    public static final String REOMET_FILEPATH="/usr/local/freeswitch/conf/vars.xml";
    public static final String REOMET_FILEPATH2="/usr/local/freeswitch/conf/autoload_configs/distributor.conf.xml";

    private static final String LOCAL_CONFIG_FILE = "vars.xml";

    private static final String LOCAL_CONFIG_FILE2 = "distributor.conf.xml";


    @Override
    protected void connect() throws Exception {
        //连接ssh执行脚本
        sshRemoteServerOperate = SshRemoteServerOperateForOPS.getInstance();
        try {
            sshRemoteServerOperate.sshRemoteCallLogin("localhost","root","P@rad1se",2233);
            boolean logined = sshRemoteServerOperate.isLogined();
            if (logined) {
                logger.info("ssh远程连接成功");
            }
        } catch (Exception e) {
            logger.error("修改本地IP："+ e.getMessage());
        }

    }

    @Override
    protected void updateConfigFile(String filePath, String ip) throws Exception {

        Map<String, String> var_changes=new HashMap<>();
        var_changes.put("user_local_ip",ip);
        var_changes.put("user_public_ip",ip);
        var_changes.put("user_external_ip",ip);
        sshRemoteServerOperate.fileDownload(REOMET_FILEPATH, LOCAL_CONFIG_FILE);
        ConfigFileModifier.modifyXmlConfigFile(LOCAL_CONFIG_FILE, var_changes);
        sshRemoteServerOperate.uploadFile(LOCAL_CONFIG_FILE,REOMET_FILEPATH);


        var_changes.put("user_local_ip",ip);
        var_changes.put("user_public_ip",ip);
        var_changes.put("user_external_ip",ip);
        sshRemoteServerOperate.fileDownload(REOMET_FILEPATH2, LOCAL_CONFIG_FILE2);
        ConfigFileModifier.modifyAttribute(LOCAL_CONFIG_FILE2, "list","node",ip);
        sshRemoteServerOperate.uploadFile(LOCAL_CONFIG_FILE2,REOMET_FILEPATH2);
    }

    @Override
    protected void disconnect() throws Exception {
        if (sshRemoteServerOperate != null) {
            sshRemoteServerOperate.closeSession();
        }
    }
}
