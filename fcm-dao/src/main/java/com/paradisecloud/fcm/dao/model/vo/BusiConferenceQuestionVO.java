package com.paradisecloud.fcm.dao.model.vo;

import com.paradisecloud.fcm.dao.model.BusiConferenceQuestion;
import lombok.Data;

import java.util.List;
@Data
public class BusiConferenceQuestionVO extends BusiConferenceQuestion {
    private List<BusiConferenceOptionVO> optionList;
    /**
     * 选中的选项ID集合
     * - 对于单选题（类型1）：集合中只包含一个元素
     * - 对于多选题（类型2）：集合中可以包含多个元素
     */
    private List<Long> optionIds;

    // 如果需要区分问题类型，可以添加问题类型字段
    /**
     * 问题类型
     * 1: 单选题
     * 2: 多选题
     */
    private Integer questionType;
}
