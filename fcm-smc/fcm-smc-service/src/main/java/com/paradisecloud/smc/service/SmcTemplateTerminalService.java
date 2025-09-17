package com.paradisecloud.smc.service;

import com.paradisecloud.smc.dao.model.SmcTemplateTerminal;

import java.util.List;

/**
 * @author nj
 * @date 2022/9/19 10:36
 */
public interface SmcTemplateTerminalService {
    void add(SmcTemplateTerminal smcTemplateTerminal);

    List<SmcTemplateTerminal> list(String smcTemplateId);

    void deleteBytemplateId(String id);

    void update(SmcTemplateTerminal smcTemplateTerminal);
}
