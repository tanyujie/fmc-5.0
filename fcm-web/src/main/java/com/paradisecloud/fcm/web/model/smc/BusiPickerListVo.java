package com.paradisecloud.fcm.web.model.smc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusiPickerListVo {

    private List<Integer> apiDepts;
    private List<Integer> accessDepts;

}
