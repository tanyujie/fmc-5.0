package com.paradisecloud.fcm.terminal.fs.db.manager;

import com.paradisecloud.fcm.terminal.fs.db.DatabasePool;
import com.paradisecloud.fcm.terminal.fs.db.DatabaseUtil;
import com.paradisecloud.fcm.terminal.fs.db.FreeSwitchDatabaseManager;
import com.paradisecloud.fcm.terminal.fs.db.dao.Userinfo;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserinfoManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserinfoManager.class);

    private static final String TABLE_NAME = "userinfo";
    private static final String COLUMN_id = "id";
    private static final String COLUMN_username = "username";
    private static final String COLUMN_password = "password";

    private static final String SELECT_COLUMN_ALL = COLUMN_id + "," + COLUMN_username + "," + COLUMN_password;

    private final String ip;

    public UserinfoManager(String ip) {
        ip = FreeSwitchDatabaseManager.getInstance().getUserinfoDbIp(ip);
        this.ip = ip;
    }

    /**
     * 插入用户信息
     *
     * @param userinfo
     * @return
     */
    public int insert(Userinfo userinfo) {
        int result = 0;
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("INSERT INTO ");
        sqlSb.append(TABLE_NAME);
        sqlSb.append("(");
        sqlSb.append(COLUMN_username);
        sqlSb.append(", ");
        sqlSb.append(COLUMN_password);
        sqlSb.append(")");
        sqlSb.append(" VALUES(");
        if (userinfo.getUsername() != null) {
            sqlSb.append("'");
            sqlSb.append(userinfo.getUsername());
            sqlSb.append("'");

        } else {
            sqlSb.append("NULL");
        }
        sqlSb.append(", ");
        if (userinfo.getPassword() != null) {
            sqlSb.append("'");
            sqlSb.append(userinfo.getPassword());
            sqlSb.append("'");
        } else {
            sqlSb.append("NULL");
        }
        sqlSb.append(")");
        String sql = sqlSb.toString();
        LOGGER.info(sql);
        DatabasePool databasePool = BeanFactory.getBean(DatabasePool.class);
        Connection connection = databasePool.getConnection(ip);
        try {
            result = DatabaseUtil.executeInsert(connection, sql);
        } catch (Exception e) {
            throw e;
        } finally {
            if (!databasePool.isTransaction()) {
                databasePool.releaseConnection(ip, connection);
            }
        }
        LOGGER.info("Insert执行结果:" + result);

        return result;
    }

    /**
     * 更新用户信息
     *
     * @param userinfo
     * @return
     */
    public int update(Userinfo userinfo) {
        int result = 0;
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("UPDATE ");
        sqlSb.append(TABLE_NAME);
        sqlSb.append(" SET ");
        if (userinfo.getUsername() != null) {
            sqlSb.append(COLUMN_username);
            sqlSb.append(" = ");
            sqlSb.append("'");
            sqlSb.append(userinfo.getUsername());
            sqlSb.append("'");
        }
        sqlSb.append(", ");
        if (userinfo.getPassword() != null) {
            sqlSb.append(COLUMN_password);
            sqlSb.append(" = ");
            sqlSb.append("'");
            sqlSb.append(userinfo.getPassword());
            sqlSb.append("'");
        }
        sqlSb.append(" WHERE ");
        sqlSb.append(COLUMN_id);
        sqlSb.append(" = ");
        sqlSb.append(userinfo.getId());
        String sql = sqlSb.toString();
        LOGGER.info(sql);
        DatabasePool databasePool = BeanFactory.getBean(DatabasePool.class);
        Connection connection = databasePool.getConnection(ip);
        try {
            result = DatabaseUtil.executeUpdate(connection, sql);
        } catch (Exception e) {
            throw e;
        } finally {
            if (!databasePool.isTransaction()) {
                databasePool.releaseConnection(ip, connection);
            }
        }
        LOGGER.info("Update执行结果:" + result);

        return result;
    }

    /**
     * 根据id删除用户信息
     *
     * @param id
     * @return
     */
    public int deleteById(long id) {
        int result = 0;
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("DELETE FROM ");
        sqlSb.append(TABLE_NAME);
        sqlSb.append(" WHERE ");
        sqlSb.append(COLUMN_id);
        sqlSb.append(" = ");
        sqlSb.append(id);
        String sql = sqlSb.toString();
        LOGGER.info(sql);
        DatabasePool databasePool = BeanFactory.getBean(DatabasePool.class);
        Connection connection = databasePool.getConnection(ip);
        try {
            result = DatabaseUtil.executeDelete(connection, sql);
        } catch (Exception e) {
            throw e;
        } finally {
            if (!databasePool.isTransaction()) {
                databasePool.releaseConnection(ip, connection);
            }
        }
        LOGGER.info("Delete执行结果:" + result);

        return result;
    }

    /**
     * 根据多个id删除多条用户信息
     *
     * @param ids
     * @return
     */
    public int deleteByIds(long[] ids) {
        int result = 0;
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("DELETE FROM ");
        sqlSb.append(TABLE_NAME);
        sqlSb.append(" WHERE ");
        sqlSb.append(COLUMN_id);
        sqlSb.append(" IN(");
        for (int i = 0; i < ids.length; i++) {
            if (i > 0) {
                sqlSb.append(",");
            }
            sqlSb.append(ids[i]);
        }
        sqlSb.append(")");
        String sql = sqlSb.toString();
        LOGGER.info(sql);
        DatabasePool databasePool = BeanFactory.getBean(DatabasePool.class);
        Connection connection = databasePool.getConnection(ip);
        try {
            result = DatabaseUtil.executeDelete(connection, sql);
        } catch (Exception e) {
            throw e;
        } finally {
            if (!databasePool.isTransaction()) {
                databasePool.releaseConnection(ip, connection);
            }
        }
        LOGGER.info("Delete执行结果:" + result);

        return result;
    }

    /**
     * 根据用户名删除用户信息
     *
     * @param username
     * @return
     */
    public int deleteByUsername(String username) {
        int result = 0;
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("DELETE FROM ");
        sqlSb.append(TABLE_NAME);
        sqlSb.append(" WHERE ");
        sqlSb.append(COLUMN_username);
        sqlSb.append(" = ");
        sqlSb.append("'");
        sqlSb.append(username);
        sqlSb.append("'");
        String sql = sqlSb.toString();
        LOGGER.info(sql);
        DatabasePool databasePool = BeanFactory.getBean(DatabasePool.class);
        Connection connection = databasePool.getConnection(ip);
        try {
        result = DatabaseUtil.executeDelete(connection, sql);
    } catch (Exception e) {
        throw e;
    } finally {
        if (!databasePool.isTransaction()) {
            databasePool.releaseConnection(ip, connection);
        }
    }
        LOGGER.info("Delete执行结果:" + result);

        return result;
    }

    /**
     * 根据多个用户名删除多条用户信息
     *
     * @param usernames
     * @return
     */
    public int deleteByUsernames(String[] usernames) {
        int result = 0;
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("DELETE FROM ");
        sqlSb.append(TABLE_NAME);
        sqlSb.append(" WHERE ");
        sqlSb.append(COLUMN_username);
        sqlSb.append(" IN(");
        for (int i = 0; i < usernames.length; i++) {
            if (i > 0) {
                sqlSb.append(",");
            }
            sqlSb.append("'");
            sqlSb.append(usernames[i]);
            sqlSb.append("'");
        }
        sqlSb.append(")");
        String sql = sqlSb.toString();
        LOGGER.info(sql);
        DatabasePool databasePool = BeanFactory.getBean(DatabasePool.class);
        Connection connection = databasePool.getConnection(ip);
        try {
            result = DatabaseUtil.executeDelete(connection, sql);
        } catch (Exception e) {
            throw e;
        } finally {
            if (!databasePool.isTransaction()) {
                databasePool.releaseConnection(ip, connection);
            }
        }
        LOGGER.info("Delete执行结果:" + result);

        return result;
    }

    /**
     * 根据id查询用户信息
     *
     * @param id
     * @return
     */
    public Userinfo queryById(long id) {
        Userinfo userinfo = null;
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("SELECT ");
        sqlSb.append(SELECT_COLUMN_ALL);
        sqlSb.append(" FROM ");
        sqlSb.append(TABLE_NAME);
        sqlSb.append(" WHERE ");
        sqlSb.append(COLUMN_id);
        sqlSb.append(" = ");
        sqlSb.append(id);
        String sql = sqlSb.toString();
        LOGGER.info(sql);
        DatabasePool databasePool = BeanFactory.getBean(DatabasePool.class);
        Connection connection = databasePool.getConnection(ip);
        try {
            ResultSet resultSet = DatabaseUtil.executeQuery(connection, sql);
            while (resultSet.next()) {
                userinfo = new Userinfo();
                long c_id = resultSet.getLong(COLUMN_id);
                userinfo.setId(c_id);
                String c_username = resultSet.getString(COLUMN_username);
                userinfo.setUsername(c_username);
                String c_password = resultSet.getString(COLUMN_password);
                userinfo.setPassword(c_password);
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
        if (userinfo != null) {
            LOGGER.info(userinfo.toString());
        }

        return userinfo;
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username
     * @return
     */
    public Userinfo queryByUsername(String username) {
        Userinfo userinfo = null;
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("SELECT ");
        sqlSb.append(SELECT_COLUMN_ALL);
        sqlSb.append(" FROM ");
        sqlSb.append(TABLE_NAME);
        sqlSb.append(" WHERE ");
        sqlSb.append(COLUMN_username);
        sqlSb.append(" = ");
        sqlSb.append("'");
        sqlSb.append(username);
        sqlSb.append("'");
        String sql = sqlSb.toString();
        LOGGER.info(sql);
        DatabasePool databasePool = BeanFactory.getBean(DatabasePool.class);
        Connection connection = databasePool.getConnection(ip);
        try {
            ResultSet resultSet = DatabaseUtil.executeQuery(connection, sql);
            while (resultSet.next()) {
                userinfo = new Userinfo();
                long c_id = resultSet.getLong(COLUMN_id);
                userinfo.setId(c_id);
                String c_username = resultSet.getString(COLUMN_username);
                userinfo.setUsername(c_username);
                String c_password = resultSet.getString(COLUMN_password);
                userinfo.setPassword(c_password);
                break;
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
        if (userinfo != null) {
            LOGGER.info(userinfo.toString());
        }

        return userinfo;
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param usernames
     * @return
     */
    public List<Userinfo> queryByUsernames(String[] usernames) {
        List list = new ArrayList();
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("SELECT ");
        sqlSb.append(SELECT_COLUMN_ALL);
        sqlSb.append(" FROM ");
        sqlSb.append(TABLE_NAME);
        sqlSb.append(" WHERE ");
        sqlSb.append(" IN(");
        for (int i = 0; i < usernames.length; i++) {
            if (i > 0) {
                sqlSb.append(",");
            }
            sqlSb.append("'");
            sqlSb.append(usernames[i]);
            sqlSb.append("'");
        }
        sqlSb.append(")");
        String sql = sqlSb.toString();
        LOGGER.info(sql);
        DatabasePool databasePool = BeanFactory.getBean(DatabasePool.class);
        Connection connection = databasePool.getConnection(ip);
        try {
            ResultSet resultSet = DatabaseUtil.executeQuery(connection, sql);
            while (resultSet.next()) {
                Userinfo userinfo = new Userinfo();
                long c_id = resultSet.getLong(COLUMN_id);
                userinfo.setId(c_id);
                String c_username = resultSet.getString(COLUMN_username);
                userinfo.setUsername(c_username);
                String c_password = resultSet.getString(COLUMN_password);
                userinfo.setPassword(c_password);
                list.add(userinfo);
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
        LOGGER.info("Select执行结果:" + list.size());

        return list;
    }

    /**
     * 查询所有用户信息
     *
     * @return
     */
    public List<Userinfo> queryAll() {
        List list = new ArrayList();
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("SELECT ");
        sqlSb.append(SELECT_COLUMN_ALL);
        sqlSb.append(" FROM ");
        sqlSb.append(TABLE_NAME);
        String sql = sqlSb.toString();
        LOGGER.info(sql);
        DatabasePool databasePool = BeanFactory.getBean(DatabasePool.class);
        Connection connection = databasePool.getConnection(ip);
        try {
            ResultSet resultSet = DatabaseUtil.executeQuery(connection, sql);
            while (resultSet.next()) {
                Userinfo userinfo = new Userinfo();
                long c_id = resultSet.getLong(COLUMN_id);
                userinfo.setId(c_id);
                String c_username = resultSet.getString(COLUMN_username);
                userinfo.setUsername(c_username);
                String c_password = resultSet.getString(COLUMN_password);
                userinfo.setPassword(c_password);
                list.add(userinfo);
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
        LOGGER.info("Select执行结果:" + list.size());

        return list;
    }

}
