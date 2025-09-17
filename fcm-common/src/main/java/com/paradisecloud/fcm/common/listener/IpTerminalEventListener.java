package com.paradisecloud.fcm.common.listener;

public interface IpTerminalEventListener {
    void pushAll(long terminalId);
    void pushServerInfo(long terminalId);
    void pushGetSipAccount(long terminalId);
    void pushAddrBook(long terminalId);
    void pushRecordList(long terminalId);
    void pushInfoDisplay(long terminalId);
}
