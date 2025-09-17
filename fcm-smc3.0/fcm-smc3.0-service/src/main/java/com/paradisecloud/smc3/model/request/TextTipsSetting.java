package com.paradisecloud.smc3.model.request;

import com.paradisecloud.smc3.model.TxtOperationTypeEnumDto;
import com.paradisecloud.smc3.model.TxtTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/9/24 15:29
 */
@NoArgsConstructor
@Data
public class TextTipsSetting {
    private String conferenceId;

    /**
     * 发送内容
     */
    private String content;
    /**
     * 操作类型
     */
    private String opType= TxtOperationTypeEnumDto.SET.name();

    /**
     * 类型 BANNER
     */
    private String type= TxtTypeEnum.BANNER.name();
    /**
     * 发送位置
     * 1顶部
     * 2中部
     * 3底部
     */
    private int disPosition=1;
    /**
     * 效果
     * 1从下向上
     * 2从右向左
     * 3靠左 LEFT
     * 4居中 MIDDLE
     * 5靠右 RIGHT
     * 6终端自定义Type
     */
    private int displayType;

    private int  captionShort=0;
}
