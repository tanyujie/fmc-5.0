package com.paradisecloud.fcm.ops.service;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.sql.SQLException;

@Component
public class DatabaseBackupService {

    private Logger logger=LoggerFactory.getLogger(DatabaseBackupService.class);

    @Autowired
    private SqlSession sqlSession;

    public void backupDatabase(String backupFilePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(backupFilePath))) {
            // 执行备份SQL语句
            String sql = "mysqldump -u " + "root" + " -p" + "P@rad1se" + " " + "fcmdb" + " > " + backupFilePath;
            sqlSession.getConnection().createStatement().execute(sql);

            logger.info("数据库备份成功！");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void restoreDatabase(String backupFilePath) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(backupFilePath));
            StringBuilder sql = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sql.append(line).append("\n");
            }
            // 执行恢复SQL语句
            sqlSession.getConnection().createStatement().execute(sql.toString());

            logger.info("数据库恢复成功！");

    }
}
