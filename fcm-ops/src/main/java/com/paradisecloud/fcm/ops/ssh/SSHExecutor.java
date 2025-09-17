package com.paradisecloud.fcm.ops.ssh;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author nj
 */
public class SSHExecutor {
    private static final Logger logger = LoggerFactory.getLogger(SSHExecutor.class);
    private final SSHConnectionPool connectionPool;
    private final ExecutorService executor;

    public SSHExecutor(SSHConnectionPool pool, int threads) {
        this.connectionPool = pool;
        this.executor = Executors.newFixedThreadPool(threads);
    }

    /**
     * 提交一个任务，执行多个命令
     */
    public void executeCommandsInThread(List<String> commands, int retries) {
        executor.submit(() -> {
            executeWithRetry(commands, retries);
        });
    }

    /**
     * 带有重试机制的执行命令方法
     */
    private void executeWithRetry(List<String> commands, int retries) {
        while (retries > 0) {
            Session session = null;
            try {
                session = connectionPool.getSession(10*1000);
                logger.info("Executing commands: {}", commands);
                executeCommands(session, commands);
                connectionPool.releaseSession(session);
                break;  // 成功执行后退出重试循环
            } catch (Exception e) {
                retries--;
                if (session != null) {
                    connectionPool.releaseSession(session);
                }
                logger.error("Failed to execute commands. Attempts left: {}", retries, e);
                if (retries == 0) {
                    logger.error("Commands execution failed after all retries: {}", commands, e);
                }
            }
        }
    }

    /**
     * 顺序执行多个命令
     */
    private void executeCommands(Session session, List<String> commands) throws JSchException, Exception {
        for (String command : commands) {
            logger.info("Executing command: {}", command);
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setErrStream(System.err);

            InputStream in = channel.getInputStream();
            channel.connect();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.info("Command output: {}", line);
                }
            }

            channel.disconnect();
        }
    }

    public void executeSingleCommandInThread(String command, int retries) {
        executor.submit(() -> {
            executeSingleCommandWithRetry(command, retries);
        });
    }
    private void executeSingleCommandWithRetry(String command, int retries) {
        while (retries > 0) {
            Session session = null;
            try {
                session = connectionPool.getSession(10*1000);
                logger.info("Executing command: {}", command);
                executeCommand(session, command);
                connectionPool.releaseSession(session);
                break;  // 成功执行后退出重试循环
            } catch (Exception e) {
                retries--;
                if (session != null) {
                    connectionPool.releaseSession(session);
                }
                logger.error("Failed to execute command: {}. Attempts left: {}", command, retries, e);
                if (retries == 0) {
                    logger.error("Command execution failed after all retries: {}", command, e);
                }
            }
        }
    }

    private void executeCommand(Session session, String command) throws JSchException, Exception {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        channel.setErrStream(System.err);

        InputStream in = channel.getInputStream();
        channel.connect();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info("Command output: {}", line);
            }
        }

        channel.disconnect();
    }


    public void shutdown() {
        executor.shutdown();
        logger.info("Executor service shut down.");
    }
}

