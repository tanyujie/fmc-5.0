package com.paradisecloud.fcm.web.model.mobile.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 显示布局分屏设置
 * @author nj
 * @date 2022/7/5 15:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefaultViewCellScreens {

    private int cellSequenceNumber;
    private int operation;
    private int isFixed;



    public DefaultViewCellScreens(int cellSequenceNumber) {
        this.cellSequenceNumber = cellSequenceNumber;
        this.operation = 101;
        this.isFixed = 2;
    }
}
