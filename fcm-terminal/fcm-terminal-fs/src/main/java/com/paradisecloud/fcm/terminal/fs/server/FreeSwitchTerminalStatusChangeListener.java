package com.paradisecloud.fcm.terminal.fs.server;

import com.paradisecloud.fcm.dao.model.BusiTerminal;

public interface FreeSwitchTerminalStatusChangeListener {

    void onServerChange(BusiTerminal busiTerminal);
}
