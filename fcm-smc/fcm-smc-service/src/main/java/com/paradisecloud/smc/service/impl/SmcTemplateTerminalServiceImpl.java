package com.paradisecloud.smc.service.impl;

import com.paradisecloud.smc.dao.model.SmcTemplateTerminal;
import com.paradisecloud.smc.dao.model.mapper.SmcTemplateTerminalMapper;
import com.paradisecloud.smc.service.SmcTemplateTerminalService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author nj
 * @date 2022/9/19 10:36
 */
@Service
public class SmcTemplateTerminalServiceImpl implements SmcTemplateTerminalService {

    @Resource
    private SmcTemplateTerminalMapper smcTemplateTerminalMapper;

    @Override
    public void add(SmcTemplateTerminal smcTemplateTerminal) {
        smcTemplateTerminalMapper.insertSmcTemplateTerminal(smcTemplateTerminal);
    }

    @Override
    public List<SmcTemplateTerminal> list(String smcTemplateId) {
        return   smcTemplateTerminalMapper.selectBusiSmcList(smcTemplateId);
    }

    @Override
    public void deleteBytemplateId(String smcTemplateId) {

        smcTemplateTerminalMapper.deleteSmcTemplateTerminalByTemplateId(smcTemplateId);

    }

    @Override
    public void update(SmcTemplateTerminal smcTemplateTerminal) {
        smcTemplateTerminalMapper.updateSmcTemplateTerminal(smcTemplateTerminal);
    }
}
