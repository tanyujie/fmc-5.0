package com.paradisecloud.fcm.terminal.fs.server;

public interface FreeSwitchTerminalOnlineEventListener {

    void online(long serverId, String username);

    void offline(long serverId, String username);

    void serverOffline(long serverId);

    void serverOnline(long serverId);
}
