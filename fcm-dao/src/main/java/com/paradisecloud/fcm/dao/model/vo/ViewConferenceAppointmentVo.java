package com.paradisecloud.fcm.dao.model.vo;

import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.ViewConferenceAppointment;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class ViewConferenceAppointmentVo extends ViewConferenceAppointment {

    /** 审批未通过原因 */
    @Schema(description = "审批未通过原因")
    @Excel(name = "审批未通过原因")
    private String approvalFailReason;
    private String mcuTypeAlias;
    private boolean downCascade;
    private boolean upCascade;
    private List<ModelBean> downCascades;

    public String getApprovalFailReason() {
        return approvalFailReason;
    }

    public void setApprovalFailReason(String approvalFailReason) {
        this.approvalFailReason = approvalFailReason;
    }

    public String getMcuTypeAlias() {
        return mcuTypeAlias;
    }

    public void setMcuTypeAlias(String mcuTypeAlias) {
        this.mcuTypeAlias = mcuTypeAlias;
    }

    public boolean isDownCascade() {
        return downCascade;
    }

    public void setDownCascade(boolean downCascade) {
        downCascade = downCascade;
    }

    public boolean isUpCascade() {
        return upCascade;
    }

    public void setUpCascade(boolean upCascade) {
        upCascade = upCascade;
    }

    public List<ModelBean> getDownCascades() {
        return downCascades;
    }

    public void setDownCascades(List<ModelBean> downCascades) {
        this.downCascades = downCascades;
    }
}
