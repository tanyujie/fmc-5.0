package com.paradisecloud.fcm.web.controller.smc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridgeCache;
import com.paradisecloud.smc.dao.model.BusiSmcDept;
import com.paradisecloud.smc.service.IBusiSmcDeptService;
import com.paradisecloud.system.model.ExcelUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import javax.annotation.Resource;

/**
 * 【请填写功能名称】Controller
 * 
 * @author liuxilong
 * @date 2022-08-25
 */
@RestController
@RequestMapping("/smc/dept")
@Tag(name = "【请填写功能名称】")
public class BusiSmcDeptController extends BaseController
{
    @Resource
    private IBusiSmcDeptService busiSmcDeptService;

    /**
     * 查询【请填写功能名称】列表
     */
    @GetMapping("/list")
    public RestResponse list(BusiSmcDept busiSmcDept)
    {
        //startPage();
        List<ModelBean> mbs = new ArrayList<>();
        List<BusiSmcDept> list = busiSmcDeptService.selectBusiSmcDeptList(busiSmcDept);

        for (BusiSmcDept smcDept : list) {

            ModelBean mb = new ModelBean(smcDept);
            SmcBridge s = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(smcDept.getDeptId());
            Integer status = (Integer)s.getParams().get("onlineStatus");
            boolean f = status != null && status.intValue() == TerminalOnlineStatus.ONLINE.getValue();
            if (f)
            {
                mb.put("smcInfo", "【" + s.getBusiSMC().getName() + "】" + s.getBusiSMC().getIp());
            }
            else
            {
                mb.put("smcInfo", "不可用【" +  s.getBusiSMC().getName() + "】" + s.getBusiSMC().getIp());
            }
            mb.put("available", f);
            mbs.add(mb);
        }

      //  return getDataTable(list);
        return RestResponse.success(mbs);
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
    @GetMapping(value = "/{id}")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiSmcDeptService.selectBusiSmcDeptById(id));
    }

    /**
     * 新增【请填写功能名称】
     */
    @PostMapping
    public RestResponse add(@RequestBody BusiSmcDept busiSmcDept)
    {
        return toAjax(busiSmcDeptService.insertBusiSmcDept(busiSmcDept));
    }

    /**
     * 修改【请填写功能名称】
     */
    @PutMapping
    public RestResponse edit(@RequestBody BusiSmcDept busiSmcDept)
    {
        return toAjax(busiSmcDeptService.updateBusiSmcDept(busiSmcDept));
    }

    /**
     * 删除【请填写功能名称】
     */
	@DeleteMapping("/{id}")
    public RestResponse remove(@PathVariable Long id)
    {
        return toAjax(busiSmcDeptService.deleteBusiSmcDeptById(id));
    }
}
