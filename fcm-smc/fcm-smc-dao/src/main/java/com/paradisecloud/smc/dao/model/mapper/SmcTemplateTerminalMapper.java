package com.paradisecloud.smc.dao.model.mapper;

import com.paradisecloud.smc.dao.model.BusiSmc;
import com.paradisecloud.smc.dao.model.SmcTemplateTerminal;

import java.util.List;

/**
 * @author nj
 * @date 2022/9/19 10:29
 */
public interface SmcTemplateTerminalMapper {


    SmcTemplateTerminal selectSmcTemplateTerminalById(Long id);

    List<SmcTemplateTerminal> selectBusiSmcList(String  smcTemplateId);


    int insertSmcTemplateTerminal(SmcTemplateTerminal smcTemplateTerminal);

    int updateSmcTemplateTerminal(SmcTemplateTerminal smcTemplateTerminal);

    int deleteSmcTemplateTerminalById(Long id);


    void deleteSmcTemplateTerminalByTemplateId(String smcTemplateId);
}
