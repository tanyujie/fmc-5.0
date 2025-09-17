package com.paradisecloud.fcm.web.controller.mobile;

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.dao.model.BusiFcmNumberSection;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.vo.TerminalSearchVo;
import com.paradisecloud.fcm.service.interfaces.IBusiFcmNumberSectionService;
import com.paradisecloud.fcm.tencent.model.MeetingRoom;
import com.paradisecloud.fcm.tencent.model.reponse.RoomResponse;
import com.paradisecloud.fcm.tencent.service2.interfaces.IAttendeeTencentService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.db.FreeSwitchTransaction;
import com.paradisecloud.fcm.web.service.interfaces.IBusiTerminalWebService;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.model.SysDeptCache;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author nj
 * @date 2022/6/15 16:02
 */
@RestController
@RequestMapping("/mobile/terminal")
@Tag(name = "通讯录")
public class MobileTerminalController  extends BaseController {

    @Resource
    private IBusiTerminalWebService busiTerminalWebService;
    @Resource
    private IBusiFcmNumberSectionService busiFcmNumberSectionService;
    @Resource
    private IAttendeeTencentService attendeeTencentService;

    /**
     * 获取终端信息详细信息
     */
    @GetMapping(value = "/terminalInfo")
    @Operation(summary = "根据ID获取终端信息详细信息")
    public RestResponse getInfo(@RequestParam("id") Long id)
    {
        return RestResponse.success(busiTerminalWebService.selectBusiTerminalById(id));
    }

    /**
     * 查询终端通讯录列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询终端通讯录列表")
    public RestResponse list(TerminalSearchVo busiTerminal)
    {
        busiTerminal.setDeptId(getDeptId());
        busiTerminal.setBusinessFieldType(100);
        PaginationData<ModelBean> list = busiTerminalWebService.selectBusiTerminalList(busiTerminal);
        return RestResponse.success(list);
    }

    /**
     * 新增终端
     */
    @Log(title = "新增终端", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Operation(summary = "新增终端")
    @FreeSwitchTransaction
    public RestResponse add(@RequestBody BusiTerminal busiTerminal)
    {
        busiTerminal.setDeptId(getDeptId());
        busiTerminal.setBusinessFieldType(100);
        int rows = busiTerminalWebService.insertBusiTerminal(busiTerminal);
        BusiTerminal res = busiTerminalWebService.selectBusiTerminal(busiTerminal);
        return rows > 0 ? RestResponse.success(res) : RestResponse.fail();
    }

    /**
     * 修改终端信息
     */
    @Log(title = "终端信息", businessType = BusinessType.UPDATE)
    @PostMapping("/editTerminalInfo")
    @Operation(summary = "根据ID修改终端信息")
    @FreeSwitchTransaction
    public RestResponse edit(@RequestBody BusiTerminal busiTerminal)
    {
        Assert.notNull(busiTerminal.getId(),"终端id不能为空");
        return toAjax(busiTerminalWebService.updateBusiTerminal(busiTerminal));
    }

    /**
     * 删除终端信息
     */
    @Log(title = "终端信息", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @Operation(summary = "根据ID删除终端信息（支持批量删除，传多个id，逗号隔开）")
    @FreeSwitchTransaction
    public RestResponse remove(@RequestBody Long[] ids)
    {
        int c = 0;
        String msg = "";
        for (Long id : ids)
        {
            try {
                c += busiTerminalWebService.deleteBusiTerminalById(id);
            } catch (Exception e) {
                if (!msg.contains(e.getMessage())) {
                    msg += "【" + e.getMessage() + "】";
                }
            }
        }

        if (ids.length > 1) {
            if (c == ids.length) {
                return RestResponse.success();
            } else {
                if (c > 1) {
                    return RestResponse.success("删除成功" + c + "条，删除失败" + (ids.length - c) + "条:" + msg);
                } else {
                    return RestResponse.fail("删除成功" + c + "条，删除失败" + (ids.length - c) + "条:" + msg);
                }
            }
        } else {
            if (c == 0) {
                if (StringUtils.isNotEmpty(msg)) {
                    return RestResponse.fail(msg);
                } else {
                    return RestResponse.fail("删除失败！");
                }
            } else {
                return toAjax(c);
            }
        }
    }


    /**
     * 查询终端号段列表
     */
    @GetMapping("/FcmCnsection")
    @Operation(summary = "查询会议号段列表")
    public RestResponse getFcmCnsection()
    {
        BusiFcmNumberSection busiFcmNumberSection = new BusiFcmNumberSection();
        busiFcmNumberSection.setDeptId(getDeptId());
        return RestResponse.success(busiFcmNumberSectionService.selectBusiFcmNumberSection(busiFcmNumberSection));
    }

    /**
     * 查询终端通讯录列表
     */
    @GetMapping("/getTerminalList")
    @Operation(summary = "查询终端通讯录列表")
    public RestResponse getTerminalList()
    {
        long deptId = getDeptId();
        SysDept sysDept = SysDeptCache.getInstance().get(deptId);
        ModelBean sysDeptMb = new ModelBean();
        sysDeptMb.put("id", sysDept.getDeptId());
        sysDeptMb.put("label", sysDept.getDeptName());

        Integer page = 0;
        Integer size = 100;
        PaginationData<BusiTerminal> pd = new PaginationData<>();
        List<BusiTerminal> terminals = new ArrayList<BusiTerminal>();
        Map<Long, BusiTerminal> deptMap = TerminalCache.getInstance().getByDept(sysDept.getDeptId());
        if (null != deptMap && !deptMap.isEmpty()) {
            deptMap.forEach((key, value) -> {
                terminals.add(value);
            });

            int fromIndex = page * size;
            int toIndex = fromIndex + size;
            if (toIndex >= terminals.size()) {
                toIndex = terminals.size();
            }

            if (fromIndex >= toIndex) {
                pd.setRecords(new ArrayList<>());
            } else {
                pd.setRecords(terminals.subList(fromIndex, toIndex));
            }

            pd.setTotal(terminals.size());
            pd.setPage(page);
            pd.setSize(size);
            sysDeptMb.put("terminalPage", pd);
        }
        return RestResponse.success(sysDeptMb);
    }

    private Long getDeptId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser principal = (LoginUser) authentication.getPrincipal();
        Long deptId = principal.getUser().getDeptId();
        return deptId;
    }
}
