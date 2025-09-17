package com.paradisecloud.smc3.model;

import com.paradisecloud.smc3.model.request.TextTipsSetting;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/9/26 18:07
 */
@NoArgsConstructor
public class ConfTextTip {
    private TextTipsSetting banner;
    private TextTipsSetting midCaption;
    private TextTipsSetting bottomCaption;
    private TextTipsSetting caption;


    public TextTipsSetting getBanner() {
        return banner;
    }

    public void setBanner(TextTipsSetting banner) {
        this.banner = banner;
    }

    public TextTipsSetting getMidCaption() {
        return midCaption;
    }

    public void setMidCaption(TextTipsSetting midCaption) {
        this.midCaption = midCaption;
    }

    public TextTipsSetting getBottomCaption() {
        return bottomCaption;
    }

    public void setBottomCaption(TextTipsSetting bottomCaption) {
        this.bottomCaption = bottomCaption;
    }

    public TextTipsSetting getCaption() {
        return getMidCaption()==null?getBottomCaption():getMidCaption();
    }

    public void setCaption(TextTipsSetting caption) {
        this.caption = caption;
    }
}
