package com.paradisecloud.fcm.ops.ssh;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * @author nj
 */
public class SSHConnectionPool {
    private static final Logger logger = LoggerFactory.getLogger(SSHConnectionPool.class);
    private final List<Session> sessionPool = new LinkedList<>();
    private final int maxPoolSize;
    private final String host;
    private final String user;
    private final String password;

    public SSHConnectionPool(int initialPoolSize, int maxPoolSize, String host, String user, String password) throws JSchException {
        this.maxPoolSize = maxPoolSize;
        this.host = host;
        this.user = user;
        this.password = password;

        for (int i = 0; i < initialPoolSize; i++) {
            sessionPool.add(createSession());
        }
    }

    /**
     * 获取会话并等待可用会话，带有超时机制。
     */
    public synchronized Session getSession(long maxWaitMillis) throws JSchException, InterruptedException {
        long startTime = System.currentTimeMillis();
        while (sessionPool.isEmpty()) {
            long elapsed = System.currentTimeMillis() - startTime;
            if (elapsed >= maxWaitMillis) {
                logger.warn("Timed out waiting for available SSH session");
                throw new RuntimeException("No available SSH sessions in the pool after waiting " + maxWaitMillis + "ms");
            }

            if (sessionPool.size() < maxPoolSize) {
                logger.info("Creating new session, pool size: {}/{}", sessionPool.size() + 1, maxPoolSize);
                sessionPool.add(createSession());
            } else {
                logger.info("All sessions are in use, waiting for a session to be released...");
                wait(maxWaitMillis - elapsed);  // Wait for a session to be released
            }
        }
        return sessionPool.remove(0);
    }

    public synchronized void releaseSession(Session session) {
        sessionPool.add(session);
        notify();  // Notify any waiting threads that a session is now available
    }

    /**
     * 创建新的 SSH 会话。
     */
    private Session createSession() throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, 22);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        return session;
    }

    public void close() {
        for (Session session : sessionPool) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }
}
