package com.paradisecloud.fcm.fme.model.param.conefrence;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author LYP
 * @version 1.0
 * @date 2020/10/24 0024 下午 4:00
 * @description:
 */
@Getter
@Setter
@ToString
public class GetCallLegVoListParam
{
    
    /**
     * 传入的对象参数
     */
    private List<Integer> ids;
}
