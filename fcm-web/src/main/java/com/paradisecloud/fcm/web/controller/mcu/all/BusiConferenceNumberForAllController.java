package com.paradisecloud.fcm.web.controller.mcu.all;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.ConferenceNumberCreateType;
import com.paradisecloud.fcm.common.enumer.ConferenceNumberSectionType;
import com.paradisecloud.fcm.common.enumer.ConferenceNumberStatus;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.dao.model.BusiConferenceNumber;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberService;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.invoker.SmcAuthResponse;
import com.paradisecloud.smc3.model.response.AddVmrResponse;
import com.paradisecloud.smc3.model.response.GetVmrResponse;
import com.paradisecloud.smc3.model.response.UserInfoRep;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 会议号码记录Controller
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
@RestController
@RequestMapping("/busi/mcu/all/conferenceNumber")
@Tag(name = "会议号码记录")
public class BusiConferenceNumberForAllController extends BaseController
{
    @Resource
    private IBusiConferenceNumberService busiConferenceNumberService;

    /**
     * 获取部门条目计数
     */
    @GetMapping(value = "/getDeptRecordCounts")
    @Operation(summary = "获取部门条目计数")
    public RestResponse getDeptRecordCounts()
    {
        return RestResponse.success(busiConferenceNumberService.getDeptRecordCounts());
    }

    /**
     * 查询会议号码记录列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询会议号码记录列表")
    public RestResponse list(BusiConferenceNumber busiConferenceNumber)
    {
        startPage();
        List<BusiConferenceNumber> list = busiConferenceNumberService.selectBusiConferenceNumberList(busiConferenceNumber);
        PaginationData<Object> pd = new PaginationData();
        pd.setTotal((new PageInfo(list)).getTotal());
        for (BusiConferenceNumber busiConferenceNumberTemp : list) {
            ModelBean modelBean = new ModelBean(busiConferenceNumberTemp);
            if (StringUtils.isNotEmpty(busiConferenceNumberTemp.getMcuType())) {
                McuType mcuType = McuType.convert(busiConferenceNumberTemp.getMcuType());
                if (mcuType != null) {
                    modelBean.put("mcuTypeAlias", mcuType.getAlias());
                }
            }
            pd.addRecord(modelBean);
        }
        return RestResponse.success(0L, "查询成功", pd);
    }

    /**
     * 获取会议号码记录详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取会议号码记录详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiConferenceNumberService.selectBusiConferenceNumberById(id));
    }

    /**
     * 新增会议号码记录
     */
    @Log(title = "会议号码记录", businessType = BusinessType.INSERT)
    @PostMapping("")
    @Operation(summary = "新增会议号码记录", description = "新增固定会议号")
    public RestResponse add(@RequestBody BusiConferenceNumber busiConferenceNumber)
    {
        // 设置会议号创建类型
        busiConferenceNumber.setCreateType(ConferenceNumberCreateType.MANUAL.getValue());
        busiConferenceNumber.getParams().put("sectionType", ConferenceNumberSectionType.FIXED.getValue());
        int i = busiConferenceNumberService.insertBusiConferenceNumber(busiConferenceNumber);
        if (i > 0) {
            if (false) {// 开始会议时候创建虚拟会议室
                if (McuType.SMC3.getCode().equals(busiConferenceNumber.getMcuType())) {
                    String conferenceNumber = busiConferenceNumber.getId().toString();
                    Smc3Bridge smc3Bridge = Smc3BridgeCache.getInstance().getBridgesByDept(busiConferenceNumber.getDeptId());
                    try {
                        String vmrNumber = smc3Bridge.getTenantId() + conferenceNumber;
                        String organizationId = null;
                        try {
                            organizationId = (String) busiConferenceNumber.getParams().get("organizationId");
                        } catch (Exception e) {
                        }
                        if (StringUtils.isEmpty(organizationId)) {
                            String userInfo = smc3Bridge.getSmcUserInvoker().getUserInfo(smc3Bridge.getSmcportalTokenInvoker().getUserName(), smc3Bridge.getSmcportalTokenInvoker().getSystemHeaders());
                            UserInfoRep userInfoRep = JSON.parseObject(userInfo, UserInfoRep.class);
                            organizationId = userInfoRep.getAccount().getOrganization().getId();
                        }
                        HashMap<String, Object> params = new HashMap<>();
                        params.put("name", conferenceNumber);
                        params.put("vmrNumber", conferenceNumber);
                        params.put("vmrType", "PROJECT");
                        params.put("organizationId", organizationId);
                        String paramsStr = JSON.toJSONString(params);
                        String responseStr = smc3Bridge.getSmcportalTokenInvoker().createVmr(paramsStr, smc3Bridge.getSmcportalTokenInvoker().getSystemHeaders());
                        AddVmrResponse addVmrResponse = JSON.parseObject(responseStr, AddVmrResponse.class);
                        if (!vmrNumber.equals(addVmrResponse.getVmrNumber())) {
                            throw new CustomException("添加SMC虚拟会议室号码失败，请使用其它号码或者联系管理员！");
                        }
                        String response = smc3Bridge.getSmcportalTokenInvoker().changeVmrPwd(addVmrResponse.getId(), "", "", smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                    } catch (Exception e) {
                        logger.error("创建固定会议号码失败!");
                        busiConferenceNumberService.deleteBusiConferenceNumberById(busiConferenceNumber.getId());
                        i = 0;
                        if (e instanceof CustomException) {
                            return RestResponse.fail(e.getMessage());
                        }
                    }
                }
            }
        }
        return toAjax(i);
    }

    /**
     * 修改会议号码记录
     */
    @Log(title = "会议号码记录", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    @Operation(summary = "修改会议号码记录", description = "修改固定会议号")
    public RestResponse edit(@RequestBody BusiConferenceNumber busiConferenceNumber, @PathVariable Long id)
    {
        busiConferenceNumber.setId(id);
        busiConferenceNumber.getParams().put("sectionType", ConferenceNumberSectionType.FIXED.getValue());
        return toAjax(busiConferenceNumberService.updateBusiConferenceNumber(busiConferenceNumber));
    }

    /**
     * 删除会议号码记录
     */
    @Log(title = "会议号码记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    @Operation(summary = "删除会议号码记录", description = "删除固定会议号")
    public RestResponse remove(@PathVariable Long id)
    {
        BusiConferenceNumber busiConferenceNumber = busiConferenceNumberService.selectBusiConferenceNumberById(id);
        if (busiConferenceNumber.getStatus() != ConferenceNumberStatus.IDLE.getValue()) {
            return RestResponse.fail("该号码正在被使用中，无法删除！");
        }
        if (McuType.SMC3.getCode().equals(busiConferenceNumber.getMcuType())) {
            String conferenceNumber = busiConferenceNumber.getId().toString();
            Smc3Bridge smc3Bridge = Smc3BridgeCache.getInstance().getBridgesByDept(busiConferenceNumber.getDeptId());
            try {
                String responseStr = smc3Bridge.getSmcportalTokenInvoker().getVmr(conferenceNumber, smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                GetVmrResponse getVmrResponse = JSON.parseObject(responseStr, GetVmrResponse.class);
                if (StringUtils.isNotEmpty(getVmrResponse.getId())) {
                    responseStr = smc3Bridge.getSmcportalTokenInvoker().deleteVmr(getVmrResponse.getId(), smc3Bridge.getSmcportalTokenInvoker().getSystemHeaders());
                    if (responseStr != null) {
                        throw new CustomException("删除SMC虚拟会议室号码失败，请重试或者联系管理员！");
                    }
                }
            } catch (Exception e) {
                logger.error("删除固定会议号码失败!");
                if (e instanceof CustomException) {
                    return RestResponse.fail(e.getMessage());
                }
                return RestResponse.fail("删除固定会议号码失败!");
            }
        }
        int i = busiConferenceNumberService.deleteBusiConferenceNumberById(id);
        return toAjax(i);
    }
}
