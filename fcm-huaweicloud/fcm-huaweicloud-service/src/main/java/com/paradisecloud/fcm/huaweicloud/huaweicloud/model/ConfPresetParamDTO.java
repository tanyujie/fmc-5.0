package com.paradisecloud.fcm.huaweicloud.huaweicloud.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nj
 * @date 2024/3/18 15:23
 */
@Data
@NoArgsConstructor
public class ConfPresetParamDTO {

    private List<PresetMultiPicReqDto> presetMultiPics=new ArrayList<>();
}
