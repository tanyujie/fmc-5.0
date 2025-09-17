package com.paradisecloud.fcm.terminal.fs.core;

import com.paradisecloud.fcm.terminal.fs.db.FreeSwitchDatabaseManager;
import com.paradisecloud.fcm.terminal.fs.server.FreeSwitchServerManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 初始化free switch相关
 * 2022/04/25
 * 初始化顺序排在 TerminalModuleInitializer 后面
 */
@Order(200)
@Component
public class InitFreeSwitch implements ApplicationRunner {

    /** 使用其它用户信息数据库 */
    @Value("${freeswitch.datasource.driverClassName}")
    private String db_driverClass;
    @Value("${freeswitch.datasource.url}")
    private String db_url;
    @Value("${freeswitch.datasource.username}")
    private String db_username;
    @Value("${freeswitch.datasource.password}")
    private String db_password;
    @Value("${freeswitch.transaction.enable}")
    private boolean transactionEnable;
    @Value("${fs.port}")
    private int fs_port;
    @Value("${fs.password}")
    private String fs_password;
    /** 使用固定用户信息数据库 */
    @Value("${freeswitch.userinfo.enable}")
    private boolean userinfo_db_enable = true;
    @Value("${freeswitch.userinfo.useFcmDb}")
    private boolean userinfo_db_useFcmDb = false;
    @Value("${freeswitch.userinfo.datasource.driverClassName}")
    private String userinfo_db_driverClass;
    @Value("${freeswitch.userinfo.datasource.ip}")
    private String userinfo_db_ip;
    @Value("${freeswitch.userinfo.datasource.url}")
    private String userinfo_db_url;
    @Value("${freeswitch.userinfo.datasource.username}")
    private String userinfo_db_username;
    @Value("${freeswitch.userinfo.datasource.password}")
    private String userinfo_db_password;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        FreeSwitchDatabaseManager.init(db_driverClass, db_url, db_username, db_password, transactionEnable,
                userinfo_db_enable, userinfo_db_useFcmDb, userinfo_db_driverClass, userinfo_db_ip, userinfo_db_url, userinfo_db_username, userinfo_db_password);
        FreeSwitchServerManager.init(fs_port, fs_password);
    }
}
