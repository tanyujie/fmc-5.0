package com.paradisecloud.fcm.common.message.terminal;

import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author nj
 * @date 2023/6/15 10:44
 */
public class Smc2TerminalOnlineStatusMessageQueue extends LinkedBlockingQueue<TerminalOnlineStatusMessage>
    {

        /**
         * <pre>用一句话描述这个变量的含义</pre>
         * @since 2021-02-04 16:34
         */
        private static final long serialVersionUID = 1L;

        private static final Smc2TerminalOnlineStatusMessageQueue INSTANCE = new Smc2TerminalOnlineStatusMessageQueue();



        /**
         * <pre>构造方法</pre>
         * @author lilinhai
         * @since 2021-02-04 17:11
         * @param capacity
         */
    private Smc2TerminalOnlineStatusMessageQueue()
        {
            super(1000);
        }

        public void put(TerminalOnlineStatusMessage e)
        {
            try
            {
                super.put(e);
            }
            catch (Throwable e2)
            {
                LoggerFactory.getLogger(getClass()).error("smc2.0 put(TerminalEventMessage e)  error", e2);
            }
        }

        public static Smc2TerminalOnlineStatusMessageQueue getInstance()
        {
            return INSTANCE;
        }
}
