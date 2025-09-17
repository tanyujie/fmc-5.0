package com.paradisecloud.fcm.web.utils;

public interface SshConfigConstant {

    //服务严格的密钥检查
    String STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";

    //no
    String NO = "no";

    //执行exec
    String EXEC = "exec";

    //服务器默认端口号
    Integer DEFAULT_SERVER_PORT = 2233;

    //服务器的用户名
    String SERVER_DEFAULT_USER_NAME = "root";

    //OPS服务器的用户名
    String SERVER_DEFAULT_USER_NAME_FOR_OPS = "opsadm";

    //服务器的密码
    String SERVER_DEFAULT_PASSWORD = "P@rad1se";

    //服务器重启命令
    String SERVER_REBOOT = "reboot";

}
