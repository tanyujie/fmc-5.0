package com.paradisecloud.fcm.terminal.fs.db.manager;

import com.paradisecloud.fcm.terminal.fs.db.DatabasePool;
import com.paradisecloud.fcm.terminal.fs.db.DatabaseUtil;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class RegistrationsManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationsManager.class);

    private static final String TABLE_NAME = "registrations";
    private static final String COLUMN_reg_user = "reg_user";
    private static final String COLUMN_realm = "realm";
    private static final String COLUMN_token = "token";
    private static final String COLUMN_url = "url";
    private static final String COLUMN_expires = "expires";
    private static final String COLUMN_network_ip = "network_ip";
    private static final String COLUMN_network_port = "network_port";
    private static final String COLUMN_network_proto = "network_proto";
    private static final String COLUMN_hostname = "hostname";
    private static final String COLUMN_metadata = "metadata";

    private static final String SELECT_COLUMN_ALL = COLUMN_reg_user + "," + COLUMN_realm + "," + COLUMN_token + "," + COLUMN_url + "," + COLUMN_expires + "," + COLUMN_network_ip + "," + COLUMN_network_port + "," + COLUMN_network_proto + "," + COLUMN_hostname + "," + COLUMN_metadata;
    private static final String SELECT_COLUMN_FOR_ONLINE = COLUMN_reg_user;

    private final String ip;

    public RegistrationsManager(String ip) {
        this.ip = ip;
    }

    /**
     * 查询所有在线终端
     *
     * @return
     */
    public Set<String> queryAllForOnline() {
        Set<String> set = new HashSet<>();
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("SELECT ");
        sqlSb.append(SELECT_COLUMN_FOR_ONLINE);
        sqlSb.append(" FROM ");
        sqlSb.append(TABLE_NAME);
        String sql = sqlSb.toString();
        LOGGER.info(sql);
        DatabasePool databasePool = BeanFactory.getBean(DatabasePool.class);
        Connection connection = databasePool.getConnection(ip);
        try {
            ResultSet resultSet = DatabaseUtil.executeQuery(connection, sql);
            while (resultSet.next()) {
                String regUser = resultSet.getString(COLUMN_reg_user);
                set.add(regUser);
            }
        } catch (Exception e) {
            try {
                throw e;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } finally {
            if (!databasePool.isTransaction()) {
                databasePool.releaseConnection(ip, connection);
            }
        }
        LOGGER.info("Select执行结果:" + set.size());

        return set;
    }
}
