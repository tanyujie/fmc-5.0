package com.paradisecloud.smc3.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 * 会议预置参数
 * @author nj
 * @date 2022/9/28 9:54
 */
@Data
@NoArgsConstructor
public class ConferencePresetReqDto {

    private List<PresetMultiPicReqDto> presetMultiPics;
}
