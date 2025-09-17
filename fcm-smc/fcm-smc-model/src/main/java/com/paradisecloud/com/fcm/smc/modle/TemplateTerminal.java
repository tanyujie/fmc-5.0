package com.paradisecloud.com.fcm.smc.modle;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2023/2/28 14:00
 */
@NoArgsConstructor
@Data
public class TemplateTerminal {
    private int weight;
    private Long id;
    private int attendType=1;
}
