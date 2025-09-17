package com.paradisecloud.com.fcm.smc.modle;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2022/8/17 10:04
 */
@NoArgsConstructor
@Data
public class SmcSubtitleService {
    private Boolean enableSubtitle;
    private List<String> supLanguageList;
    private String subscribePath;
    private String srcLang;
}
