package com.paradisecloud.smc3.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nj
 * @date 2022/8/15 10:09
 */
@NoArgsConstructor
@Data
public class SmcConferenceTemplateQuery {

    private List<Content> content;

    @NoArgsConstructor
    @Data
    public static class Content {

        private String id;
        private String subject;
    }
}
